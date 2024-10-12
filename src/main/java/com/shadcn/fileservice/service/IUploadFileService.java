package com.shadcn.fileservice.service;

import com.shadcn.fileservice.dto.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUploadFileService {
    FileUploadResponse uploadFile(MultipartFile multipartFile) throws IOException;
}
