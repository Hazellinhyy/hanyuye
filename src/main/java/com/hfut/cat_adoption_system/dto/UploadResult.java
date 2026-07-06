package com.hfut.cat_adoption_system.dto;

/**
 * 文件上传结果DTO
 * 
 * 用于封装文件上传后的结果信息，包含上传文件的访问URL、原始文件名和文件大小，
 * 支持前端获取上传后的文件信息进行展示或后续操作。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record UploadResult(
                /** 文件访问URL */
                String url,

                /** 文件原始名称 */
                String originalFilename,

                /** 文件大小（字节） */
                long size) {
}