package com.shadcn.fileservice.utils;

import static com.shadcn.fileservice.constant.System.FILE_UPLOAD_PATH;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
    private static final Path fileStorageLocation = Paths.get(FILE_UPLOAD_PATH);

    public static String saveFile(String fileName, MultipartFile multipartFile) throws IOException {

        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        }
        // naming a file
        String fileCode = RandomStringUtils.randomAlphanumeric(8);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = fileStorageLocation.resolve(fileCode + "-" + fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        }

        return fileCode;
    }
}
