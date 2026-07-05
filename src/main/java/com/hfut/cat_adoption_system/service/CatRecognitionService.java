package com.hfut.cat_adoption_system.service;

import com.hfut.cat_adoption_system.common.BusinessException;
import com.hfut.cat_adoption_system.dto.RecognitionCandidate;
import com.hfut.cat_adoption_system.dto.RecognitionResult;
import com.hfut.cat_adoption_system.mapper.CatMapper;
import com.hfut.cat_adoption_system.mapper.CatPhotoMapper;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.CatPhoto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CatRecognitionService {
    private static final int MAX_CANDIDATES = 5;

    private final CatPhotoMapper catPhotoMapper;
    private final CatMapper catMapper;
    private final ImageFeatureService imageFeatureService;

    public CatRecognitionService(CatPhotoMapper catPhotoMapper,
                                 CatMapper catMapper,
                                 ImageFeatureService imageFeatureService) {
        this.catPhotoMapper = catPhotoMapper;
        this.catMapper = catMapper;
        this.imageFeatureService = imageFeatureService;
    }

    @Transactional
    public RecognitionResult recognize(MultipartFile image) {
        ImageFeatureService.ImageFeature queryFeature = imageFeatureService.extract(image);
        List<CatPhoto> samples = catPhotoMapper.findAll();
        if (samples.isEmpty()) {
            throw new BusinessException("样本库为空，请先维护猫咪识别照片");
        }

        Map<String, ScoredMatch> bestByCat = new LinkedHashMap<>();
        int usableSamples = 0;
        for (CatPhoto sample : samples) {
            ImageFeatureService.ImageFeature sampleFeature = getSampleFeature(sample, queryFeature.version());
            if (sampleFeature == null) {
                continue;
            }
            usableSamples++;
            double score = weightedScore(imageFeatureService.similarity(queryFeature, sampleFeature), sample.recognitionWeight());
            ScoredMatch current = bestByCat.get(sample.catId());
            if (current == null || score > current.score()) {
                bestByCat.put(sample.catId(), new ScoredMatch(sample, score));
            }
        }

        if (usableSamples == 0) {
            throw new BusinessException("没有可用样本照片，请检查样本库图片地址");
        }

        List<RecognitionCandidate> candidates = bestByCat.values().stream()
                .sorted(Comparator.comparingDouble(ScoredMatch::score).reversed())
                .limit(MAX_CANDIDATES)
                .map(this::toCandidate)
                .toList();
        return new RecognitionResult(usableSamples, queryFeature.version(), candidates);
    }

    private ImageFeatureService.ImageFeature getSampleFeature(CatPhoto sample, String expectedVersion) {
        ImageFeatureService.ImageFeature cached = imageFeatureService.parse(sample.featureVector());
        if (cached != null && cached.version().equals(expectedVersion)) {
            return cached;
        }
        try {
            ImageFeatureService.ImageFeature extracted = imageFeatureService.extractFromUrl(sample.photoUrl());
            catPhotoMapper.updateFeatureVector(sample.photoId(), imageFeatureService.serialize(extracted));
            return extracted;
        } catch (BusinessException exception) {
            return null;
        }
    }

    private double weightedScore(double rawScore, BigDecimal weight) {
        double safeWeight = weight == null ? 1.0 : Math.max(0.2, Math.min(1.2, weight.doubleValue()));
        return Math.max(0, Math.min(1, rawScore * safeWeight));
    }

    private RecognitionCandidate toCandidate(ScoredMatch match) {
        CatPhoto photo = match.photo();
        Cat cat = catMapper.findById(photo.catId());
        String catName = cat == null ? photo.catId() : cat.catName();
        String coverUrl = cat == null ? photo.photoUrl() : cat.coverUrl();
        double confidence = Math.round(match.score() * 1000.0) / 10.0;
        String reason = "与样本 " + photo.photoId() + " 的专业视觉向量、图像相似度和样本权重综合得分最高";
        return new RecognitionCandidate(photo.catId(), catName, coverUrl, photo.photoId(), photo.photoUrl(),
                photo.angleCode(), confidence, reason);
    }

    private record ScoredMatch(CatPhoto photo, double score) {
    }
}
