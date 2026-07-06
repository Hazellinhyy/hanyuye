package com.hfut.cat_adoption_system.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

/**
 * 数据质量校验工具类
 * 
 * 提供多种数据校验方法，确保输入数据的合法性和质量，防止脏数据进入系统。
 * 所有方法均为静态方法，无需实例化即可使用。
 */
public final class DataQualityValidator {

    /**
     * 私有构造函数，防止类被实例化
     */
    private DataQualityValidator() {
    }

    /**
     * 必填文本校验（严格模式）
     * 校验文本非空、长度在指定范围内，并检查是否包含脏数据
     * 
     * @param fieldName 字段名称（用于错误提示）
     * @param value     待校验的文本值
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 清理后的文本（去除首尾空格）
     * @throws BusinessException 校验失败时抛出异常
     */
    public static String requireCleanText(String fieldName, String value, int minLength, int maxLength) {
        // 非空校验
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(fieldName + "不能为空");
        }
        String cleaned = value.trim();
        // 最小长度校验
        if (cleaned.length() < minLength) {
            throw new BusinessException(fieldName + "内容过短");
        }
        // 最大长度校验
        if (cleaned.length() > maxLength) {
            throw new BusinessException(fieldName + "内容过长");
        }
        // 脏数据检测
        rejectDirty(fieldName, cleaned);
        return cleaned;
    }

    /**
     * 可选文本校验（宽松模式）
     * 若文本为空则返回null，否则执行严格校验
     */
    public static String optionalCleanText(String fieldName, String value, int minLength, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return requireCleanText(fieldName, value, minLength, maxLength);
    }

    /**
     * 脏数据检测
     * 检测文本中是否包含无效内容、测试内容或异常格式
     */
    public static void rejectDirty(String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        String cleaned = value.trim();
        String compact = cleaned.replaceAll("\\s+", ""); // 去除所有空白字符
        String lower = compact.toLowerCase(Locale.ROOT); // 转为小写用于不区分大小写匹配

        // 检测连续问号（占位符）
        if (hasQuestionRun(compact)) {
            throw new BusinessException(fieldName + "包含无效占位字符");
        }
        // 检测编程相关的无效关键词
        if (lower.contains("codex") || lower.contains("undefined") || lower.contains("null")) {
            throw new BusinessException(fieldName + "包含无效内容");
        }
        // 检测测试占位内容
        if (lower.contains("test") || lower.contains("asdf") || lower.contains("qwer")
                || compact.contains("测试测试") || compact.contains("随便")) {
            throw new BusinessException(fieldName + "包含明显测试内容");
        }
        // 检测全标点内容
        if (isAllPunctuation(compact)) {
            throw new BusinessException(fieldName + "不能全是标点符号");
        }
        // 检测过多重复字符
        if (hasExcessiveRepeat(compact)) {
            throw new BusinessException(fieldName + "包含过多重复字符");
        }
    }

    /**
     * 枚举值校验
     * 校验值是否在允许的枚举值列表中（不区分大小写）
     * 
     * @param fieldName     字段名称
     * @param value         待校验值
     * @param allowedValues 允许的值列表
     */
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

    /**
     * 非负校验
     * 校验数值是否为非负数
     */
    public static void requireNonNegative(String fieldName, BigDecimal value) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(fieldName + "不能为负数");
        }
    }

    /**
     * 日期合理性校验
     * 校验日期是否在合理范围内（过去10年到未来1天）
     */
    public static void requireReasonableDate(String fieldName, LocalDate value) {
        if (value != null
                && (value.isBefore(LocalDate.now().minusYears(10)) || value.isAfter(LocalDate.now().plusDays(1)))) {
            throw new BusinessException(fieldName + "不合理");
        }
    }

    /**
     * 判断文本是否看起来像脏数据
     * 
     * @return true表示是脏数据，false表示数据干净
     */
    public static boolean looksDirty(String value) {
        try {
            rejectDirty("内容", value);
            return false;
        } catch (BusinessException exception) {
            return true;
        }
    }

    /**
     * 检测是否有连续6个以上相同字符
     */
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

    /**
     * 检测是否有连续3个以上问号（中英文问号）
     */
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

    /**
     * 检测是否全是标点符号（不含字母、数字、中文）
     */
    private static boolean isAllPunctuation(String value) {
        boolean hasPunctuation = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            // 包含字母、数字或中文则不是纯标点
            if (Character.isLetterOrDigit(current) || Character.isIdeographic(current)) {
                return false;
            }
            // 记录是否有非空白字符
            if (!Character.isWhitespace(current)) {
                hasPunctuation = true;
            }
        }
        return hasPunctuation;
    }
}
