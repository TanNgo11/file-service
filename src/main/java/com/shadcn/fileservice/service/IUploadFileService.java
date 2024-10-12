package com.shadcn.fileservice.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.shadcn.fileservice.dto.response.FileUploadResponse;

public interface IUploadFileService {
    FileUploadResponse uploadFile(MultipartFile multipartFile) throws IOException;
}
