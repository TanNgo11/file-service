package com.shadcn.fileservice.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shadcn.fileservice.dto.response.FileUploadResponse;
import com.shadcn.fileservice.service.IUploadFileService;
import com.shadcn.fileservice.utils.FileUploadUtil;

@Service
public class UploadFileService implements IUploadFileService {

    @Override
    public FileUploadResponse uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        long size = multipartFile.getSize();

        String filecode = FileUploadUtil.saveFile(fileName, multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("/file-svc/download/" + filecode);

        return response;
    }
}
