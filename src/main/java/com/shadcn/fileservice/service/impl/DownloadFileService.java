package com.shadcn.fileservice.service.impl;

import static com.shadcn.fileservice.constant.System.FILE_UPLOAD_PATH;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.shadcn.fileservice.service.IDownloadFileService;

@Service
public class DownloadFileService implements IDownloadFileService {

    private final Path fileStorageLocation = Paths.get(FILE_UPLOAD_PATH);

    @Override
    public Resource downloadFile(String fileCode, HttpServletRequest request) throws IOException {
        Path filePath = Files.walk(this.fileStorageLocation)
                .filter(path -> path.toString().contains(fileCode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found"));

        Resource resource = new UrlResource(filePath.toUri());

        String contentType = null;
        try {
            contentType =
                    request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        String contentDisposition = contentType != null && contentType.startsWith("image") ? "inline" : "attachment";
        request.setAttribute("contentType", contentType);
        request.setAttribute("contentDisposition", contentDisposition);

        return resource;
    }
}
