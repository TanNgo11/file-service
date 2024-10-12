package com.shadcn.fileservice.service.impl;

import com.shadcn.fileservice.service.IStreamVideoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.shadcn.fileservice.constant.System.FILE_UPLOAD_PATH;

@Service
public class StreamVideoService implements IStreamVideoService {

    private final Path fileStorageLocation = Paths.get(FILE_UPLOAD_PATH);

    @Override
    public Resource streamVideo(String fileCode, HttpServletRequest request) throws IOException {
        Path videoPath = Files.walk(this.fileStorageLocation)
                .filter(path -> path.toString().contains(fileCode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Video not found"));

        Resource videoResource = new UrlResource(videoPath.toUri());
        long videoLength = videoResource.contentLength();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));

        String rangeHeader = request.getHeader("Range");
        String contentType = request.getServletContext().getMimeType(videoResource.getFile().getAbsolutePath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        headers.setContentType(MediaType.parseMediaType(contentType));

        if (rangeHeader == null) {
            return videoResource;
        } else {
            long rangeStart = 0;
            long rangeEnd;
            if (rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
                rangeStart = Long.parseLong(ranges[0]);
                if (ranges.length > 1) {
                    rangeEnd = Long.parseLong(ranges[1]);
                } else {
                    rangeEnd = videoLength - 1;
                }
            } else {
                throw new IllegalArgumentException("Range header format is not supported.");
            }

            long contentLength = rangeEnd - rangeStart + 1;
            headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + videoLength);
            headers.setContentLength(contentLength);

            long finalRangeStart = rangeStart;
            return new UrlResource(videoResource.getURI()) {
                @Override
                public InputStream getInputStream() throws IOException {
                    try (InputStream resourceStream = super.getInputStream()) {
                        resourceStream.skip(finalRangeStart);
                        return new ByteArrayInputStream(resourceStream.readNBytes((int) contentLength));
                    }
                }
            };
        }
    }
}