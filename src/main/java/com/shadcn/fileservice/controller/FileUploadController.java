package com.shadcn.fileservice.controller;

import com.shadcn.fileservice.dto.response.FileUploadResponse;
import com.shadcn.fileservice.utils.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileUploadController {

    @PostMapping("/uploadFile")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile multipartFile)
            throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        long size = multipartFile.getSize();

        String filecode = FileUploadUtil.saveFile(fileName, multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("/file/downloadFile/" + filecode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private final Path fileStorageLocation = Paths.get("Files-Upload");

    @GetMapping("/downloadFile/{fileCode}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileCode, HttpServletRequest request) {
        try {

            Path filePath = Files.walk(this.fileStorageLocation)
                    .filter(path -> path.toString().contains(fileCode))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("File not found"));

            Resource resource = new UrlResource(filePath.toUri());


            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {

                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }


    @GetMapping("/streamVideo/{fileCode}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String fileCode, HttpServletRequest request) throws IOException {
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
