package com.urbanup.service;

import com.urbanup.entity.FileUpload;
import com.urbanup.exception.FileUploadException;
import com.urbanup.repository.FileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileUploadService {

    private final FileUploadRepository fileUploadRepository;

    @Autowired
    public FileUploadService(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }

    public FileUpload uploadFile(MultipartFile file) throws FileUploadException {
        try {
            // Logic to save the file and create a FileUpload entity
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(file.getOriginalFilename());
            fileUpload.setFileType(file.getContentType());
            fileUpload.setData(file.getBytes());
            return fileUploadRepository.save(fileUpload);
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file: " + file.getOriginalFilename(), e);
        }
    }

    public FileUpload getFile(Long fileId) throws FileUploadException {
        return fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new FileUploadException("File not found with id: " + fileId));
    }
}