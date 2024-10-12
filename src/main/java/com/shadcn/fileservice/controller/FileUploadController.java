package com.shadcn.fileservice.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.shadcn.fileservice.dto.response.ApiResponse;
import com.shadcn.fileservice.dto.response.FileUploadResponse;
import com.shadcn.fileservice.service.IUploadFileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileUploadController {
    IUploadFileService uploadFileService;

    @PostMapping("/upload")
    ApiResponse<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        FileUploadResponse response = uploadFileService.uploadFile(multipartFile);
        return ApiResponse.success(response);
    }
}
