package com.shadcn.fileservice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface IStreamVideoService {
    Resource streamVideo(String fileCode, HttpServletRequest request) throws IOException;

}
