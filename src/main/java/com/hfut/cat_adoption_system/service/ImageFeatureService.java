package com.hfut.cat_adoption_system.service;

import com.hfut.cat_adoption_system.common.BusinessException;
import com.hfut.cat_adoption_system.config.RecognitionModelProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ImageFeatureService {
    public static final String LOCAL_VERSION = "local-color-hash-v2";
    private static final long MAX_UPLOAD_BYTES = 5L * 1024 * 1024;
    private static final int HISTOGRAM_BINS = 64;

    private final ResourceLoader resourceLoader;
    private final ProfessionalImageModelClient modelClient;
    private final RecognitionModelProperties modelProperties;

    public ImageFeatureService(ResourceLoader resourceLoader,
                               ProfessionalImageModelClient modelClient,
                               RecognitionModelProperties modelProperties) {
        this.resourceLoader = resourceLoader;
        this.modelClient = modelClient;
        this.modelProperties = modelProperties;
    }

    public ImageFeature extract(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请上传一张猫咪照片");
        }
        if (file.getSize() > MAX_UPLOAD_BYTES) {
            throw new BusinessException("图片不能超过 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BusinessException("只支持图片文件");
        }
        try {
            return extract(file.getBytes(), contentType);
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException("图片读取失败，请换一张清晰照片");
        }
    }

    public ImageFeature extractFromUrl(String photoUrl) {
        try (InputStream inputStream = openPhoto(photoUrl)) {
            return extract(inputStream.readAllBytes(), contentTypeFromUrl(photoUrl));
        } catch (Exception exception) {
            throw new BusinessException("样本照片无法读取：" + photoUrl);
        }
    }

    public ImageFeature parse(String stored) {
        if (stored == null || stored.isBlank()) {
            return null;
        }
        if (stored.startsWith("v1:")) {
            return parseLegacyLocalFeature(stored);
        }
        String[] parts = stored.split(":", 3);
        if (parts.length != 3) {
            return null;
        }
        double[] vector = Arrays.stream(parts[2].split(","))
                .mapToDouble(Double::parseDouble)
                .toArray();
        if (vector.length == 0) {
            return null;
        }
        return new ImageFeature(parts[0], parts[1], normalize(vector));
    }

    public String serialize(ImageFeature feature) {
        String vector = Arrays.stream(feature.vector())
                .mapToObj(value -> String.format(Locale.ROOT, "%.6f", value))
                .collect(Collectors.joining(","));
        return feature.version() + ":" + feature.modelName() + ":" + vector;
    }

    public double similarity(ImageFeature left, ImageFeature right) {
        if (!left.version().equals(right.version()) || left.vector().length != right.vector().length) {
            return 0;
        }
        return cosine(left.vector(), right.vector());
    }

    private ImageFeature extract(byte[] bytes, String contentType) throws Exception {
        var modelEmbedding = modelClient.embed(bytes, contentType);
        if (modelEmbedding.isPresent()) {
            ProfessionalImageModelClient.ModelEmbedding embedding = modelEmbedding.get();
            return new ImageFeature(embedding.version(), embedding.modelName(), normalize(embedding.vector()));
        }
        if (!modelProperties.isFallbackToLocal()) {
            throw new BusinessException("专业识别模型暂不可用，请检查后端模型服务配置");
        }
        return extractLocal(bytes);
    }

    private ImageFeature extractLocal(byte[] bytes) throws Exception {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(bytes));
        if (original == null) {
            throw new BusinessException("无法识别图片格式");
        }
        BufferedImage image = scale(original, 160, 160);
        return new ImageFeature(LOCAL_VERSION, "builtin-color-hash", normalize(localVector(image, differenceHash(original))));
    }

    private InputStream openPhoto(String photoUrl) throws Exception {
        if (photoUrl == null || photoUrl.isBlank()) {
            throw new BusinessException("样本照片地址为空");
        }
        if (photoUrl.startsWith("http://") || photoUrl.startsWith("https://")) {
            HttpURLConnection connection = (HttpURLConnection) URI.create(photoUrl).toURL().openConnection();
            connection.setConnectTimeout(2500);
            connection.setReadTimeout(3500);
            connection.setRequestProperty("User-Agent", "cat-adoption-recognition/1.0");
            return connection.getInputStream();
        }
        String normalized = photoUrl.startsWith("/") ? photoUrl : "/" + photoUrl;
        Resource resource = resourceLoader.getResource("classpath:static" + normalized);
        if (!resource.exists()) {
            throw new BusinessException("样本照片不存在");
        }
        return resource.getInputStream();
    }

    private String contentTypeFromUrl(String photoUrl) {
        if (photoUrl == null) {
            return "image/jpeg";
        }
        String lower = photoUrl.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        return "image/jpeg";
    }

    private BufferedImage scale(BufferedImage source, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(source, 0, 0, width, height, null);
        graphics.dispose();
        return scaled;
    }

    private double[] colorHistogram(BufferedImage image) {
        double[] histogram = new double[HISTOGRAM_BINS];
        int total = image.getWidth() * image.getHeight();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int bin = (r / 64) * 16 + (g / 64) * 4 + (b / 64);
                histogram[bin] += 1.0;
            }
        }
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] /= total;
        }
        return histogram;
    }

    private double[] localVector(BufferedImage image, long hash) {
        double[] histogram = colorHistogram(image);
        double[] vector = Arrays.copyOf(histogram, HISTOGRAM_BINS + 64);
        for (int i = 0; i < 64; i++) {
            vector[HISTOGRAM_BINS + i] = ((hash >>> (63 - i)) & 1L) == 1L ? 0.12 : 0.0;
        }
        return vector;
    }

    private ImageFeature parseLegacyLocalFeature(String stored) {
        try {
            String[] parts = stored.split(":", 3);
            if (parts.length != 3) {
                return null;
            }
            long hash = Long.parseUnsignedLong(parts[1], 16);
            double[] histogram = Arrays.stream(parts[2].split(","))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
            if (histogram.length != HISTOGRAM_BINS) {
                return null;
            }
            double[] vector = Arrays.copyOf(histogram, HISTOGRAM_BINS + 64);
            for (int i = 0; i < 64; i++) {
                vector[HISTOGRAM_BINS + i] = ((hash >>> (63 - i)) & 1L) == 1L ? 0.12 : 0.0;
            }
            return new ImageFeature(LOCAL_VERSION, "builtin-color-hash", normalize(vector));
        } catch (Exception exception) {
            return null;
        }
    }

    private double[] normalize(double[] vector) {
        double norm = 0;
        for (double value : vector) {
            norm += value * value;
        }
        if (norm == 0) {
            return vector;
        }
        double scale = Math.sqrt(norm);
        double[] normalized = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = vector[i] / scale;
        }
        return normalized;
    }

    private long differenceHash(BufferedImage source) {
        BufferedImage image = scale(source, 9, 8);
        long hash = 0L;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int left = gray(image.getRGB(x, y));
                int right = gray(image.getRGB(x + 1, y));
                hash <<= 1;
                if (left > right) {
                    hash |= 1L;
                }
            }
        }
        return hash;
    }

    private int gray(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }

    private double cosine(double[] left, double[] right) {
        double dot = 0;
        double leftNorm = 0;
        double rightNorm = 0;
        for (int i = 0; i < left.length; i++) {
            dot += left[i] * right[i];
            leftNorm += left[i] * left[i];
            rightNorm += right[i] * right[i];
        }
        if (leftNorm == 0 || rightNorm == 0) {
            return 0;
        }
        return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }

    public record ImageFeature(String version, String modelName, double[] vector) {
    }
}
