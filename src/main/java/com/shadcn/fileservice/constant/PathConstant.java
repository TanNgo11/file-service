package com.shadcn.fileservice.constant;

public class PathConstant {

    public static final String UPLOAD_FILE = "/upload";
    public static final String DOWNLOAD_FILE = "/download/**";
    public static final String STREAMING_VIDEO = "/streamVideo/**";
    
    public static final String[] PUBLIC_ENDPOINTS = {
            UPLOAD_FILE,
            DOWNLOAD_FILE,
            STREAMING_VIDEO,
            "/swagger-ui/**",
            "/v3/api-docs/**",
    };
}
