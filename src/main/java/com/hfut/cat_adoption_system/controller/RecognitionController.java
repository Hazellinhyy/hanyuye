package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.RecognitionResult;
import com.hfut.cat_adoption_system.service.CatRecognitionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 猫咪识别控制器
 * 
 * 提供基于图片的猫咪识别功能：
 * - 上传猫咪图片进行AI识别，返回识别结果
 * 
 * 该接口支持用户上传猫咪照片，通过AI模型识别猫咪品种、年龄、性别等信息
 */
@RestController
@RequestMapping("/api/recognition")
public class RecognitionController {

    /** 猫咪识别服务：处理图片识别相关的业务逻辑 */
    private final CatRecognitionService recognitionService;

    /**
     * 构造函数：注入猫咪识别服务
     */
    public RecognitionController(CatRecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    /**
     * 猫咪图片识别接口（公开接口）
     * 用户上传猫咪照片，系统通过AI模型进行识别
     * 
     * @param image 猫咪图片文件（multipart/form-data格式）
     * @return RecognitionResult 识别结果（包含品种、年龄、性别等信息）
     */
    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PublicApi
    public ApiResponse<RecognitionResult> recognize(@RequestPart("image") MultipartFile image) {
        return ApiResponse.ok(recognitionService.recognize(image));
    }
}