package com.hfut.cat_adoption_system.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

public final class DataQualityValidator {
    private DataQualityValidator() {
    }

    public static String requireCleanText(String fieldName, String value, int minLength, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(fieldName + "不能为空");
        }
        String cleaned = value.trim();
        if (cleaned.length() < minLength) {
            throw new BusinessException(fieldName + "内容过短");
        }
        if (cleaned.length() > maxLength) {
            throw new BusinessException(fieldName + "内容过长");
        }
        rejectDirty(fieldName, cleaned);
        return cleaned;
    }

    public static String optionalCleanText(String fieldName, String value, int minLength, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return requireCleanText(fieldName, value, minLength, maxLength);
    }

    public static void rejectDirty(String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        String cleaned = value.trim();
        String compact = cleaned.replaceAll("\\s+", "");
        String lower = compact.toLowerCase(Locale.ROOT);
        if (hasQuestionRun(compact)) {
            throw new BusinessException(fieldName + "包含无效占位字符");
        }
        if (lower.contains("codex") || lower.contains("undefined") || lower.contains("null")) {
            throw new BusinessException(fieldName + "包含无效内容");
        }
        if (lower.contains("test") || lower.contains("asdf") || lower.contains("qwer")
                || compact.contains("测试测试") || compact.contains("随便")) {
            throw new BusinessException(fieldName + "包含明显测试内容");
        }
        if (isAllPunctuation(compact)) {
            throw new BusinessException(fieldName + "不能全是标点符号");
        }
        if (hasExcessiveRepeat(compact)) {
            throw new BusinessException(fieldName + "包含过多重复字符");
        }
    }

    public static void requireEnumValue(String fieldName, String value, String... allowedValues) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(fieldName + "不能为空");
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        for (String allowed : allowedValues) {
            if (normalized.equals(allowed)) {
                return;
            }
        }
        throw new BusinessException(fieldName + "不合法");
    }

    public static void requireNonNegative(String fieldName, BigDecimal value) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(fieldName + "不能为负数");
        }
    }

    public static void requireReasonableDate(String fieldName, LocalDate value) {
        if (value != null && (value.isBefore(LocalDate.now().minusYears(10)) || value.isAfter(LocalDate.now().plusDays(1)))) {
            throw new BusinessException(fieldName + "不合理");
        }
    }

    public static boolean looksDirty(String value) {
        try {
            rejectDirty("内容", value);
            return false;
        } catch (BusinessException exception) {
            return true;
        }
    }

    private static boolean hasExcessiveRepeat(String value) {
        if (value.length() < 6) {
            return false;
        }
        int run = 1;
        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i) == value.charAt(i - 1)) {
                run++;
                if (run >= 6) {
                    return true;
                }
            } else {
                run = 1;
            }
        }
        return false;
    }

    private static boolean hasQuestionRun(String value) {
        int run = 0;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '?' || current == '？') {
                run++;
                if (run >= 3) {
                    return true;
                }
            } else {
                run = 0;
            }
        }
        return false;
    }

    private static boolean isAllPunctuation(String value) {
        boolean hasPunctuation = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (Character.isLetterOrDigit(current) || Character.isIdeographic(current)) {
                return false;
            }
            if (!Character.isWhitespace(current)) {
                hasPunctuation = true;
            }
        }
        return hasPunctuation;
    }
}
