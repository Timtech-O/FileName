package com.example.assessments.service;

import com.example.assessments.model.FileMetadata;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileMetadata uploadFile(MultipartFile file);

    Resource getFile(String id);

    FileMetadata getMetadata(String id);
}