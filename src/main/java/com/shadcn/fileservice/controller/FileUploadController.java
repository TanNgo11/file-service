package com.shadcn.fileservice.controller;

import com.shadcn.fileservice.dto.response.ApiResponse;
import com.shadcn.fileservice.dto.response.FileUploadResponse;
import com.shadcn.fileservice.service.IUploadFileService;
import com.shadcn.fileservice.utils.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
