package com.shadcn.fileservice.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;

public interface IStreamVideoService {
    Resource streamVideo(String fileCode, HttpServletRequest request) throws IOException;
}
