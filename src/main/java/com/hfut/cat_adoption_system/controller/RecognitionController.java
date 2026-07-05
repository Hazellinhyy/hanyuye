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

@RestController
@RequestMapping("/api/recognition")
public class RecognitionController {
    private final CatRecognitionService recognitionService;

    public RecognitionController(CatRecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PublicApi
    public ApiResponse<RecognitionResult> recognize(@RequestPart("image") MultipartFile image) {
        return ApiResponse.ok(recognitionService.recognize(image));
    }
}
