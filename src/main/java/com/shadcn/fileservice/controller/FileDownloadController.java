package com.shadcn.fileservice.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shadcn.fileservice.service.IDownloadFileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileDownloadController {
    IDownloadFileService downloadFileService;

    @GetMapping("/download/{fileCode}")
    ResponseEntity<Resource> downloadFile(@PathVariable String fileCode, HttpServletRequest request) {
        try {
            Resource resource = downloadFileService.downloadFile(fileCode, request);
            String contentType = (String) request.getAttribute("contentType");
            String contentDisposition = (String) request.getAttribute("contentDisposition");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            contentDisposition + "; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }
}
