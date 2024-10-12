package com.shadcn.fileservice.controller;

import static com.shadcn.fileservice.constant.System.FILE_UPLOAD_PATH;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shadcn.fileservice.service.IStreamVideoService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoStreamingController {
    private final Path fileStorageLocation = Paths.get(FILE_UPLOAD_PATH);
    IStreamVideoService streamVideoService;

    @GetMapping("/streamVideo/{fileCode}")
    ResponseEntity<Resource> streamVideo(@PathVariable String fileCode, HttpServletRequest request) throws IOException {
        Path videoPath = Files.walk(this.fileStorageLocation)
                .filter(path -> path.toString().contains(fileCode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Video not found"));

        Resource videoResource = new UrlResource(videoPath.toUri());
        long videoLength = videoResource.contentLength();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));

        String rangeHeader = request.getHeader("Range");
        if (rangeHeader == null) {
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(videoLength)
                    .body(videoResource);
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
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentLength(contentLength)
                    .body(new UrlResource(videoResource.getURI()) {
                        @Override
                        public InputStream getInputStream() throws IOException {
                            try (InputStream resourceStream = super.getInputStream()) {
                                resourceStream.skip(finalRangeStart);
                                return new ByteArrayInputStream(resourceStream.readNBytes((int) contentLength));
                            }
                        }
                    });
        }
    }
}
