package com.shadcn.fileservice.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;

public interface IDownloadFileService {
    Resource downloadFile(String fileCode, HttpServletRequest request) throws IOException;
}
