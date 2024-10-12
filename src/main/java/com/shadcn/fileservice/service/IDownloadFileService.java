package com.shadcn.fileservice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface IDownloadFileService {
    Resource downloadFile(String fileCode, HttpServletRequest request) throws IOException;
}
