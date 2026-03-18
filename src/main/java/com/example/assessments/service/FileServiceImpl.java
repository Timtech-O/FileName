package com.example.assessments.service;

import com.example.assessments.exception.FileNotFoundException;
import com.example.assessments.exception.InvalidFileException;
import com.example.assessments.model.FileMetadata;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FileService {

    private final Map<String, FileMetadata> storage = new HashMap<>();

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileMetadata uploadFile(MultipartFile file) {
        validateFile(file);

        try {
            String id = UUID.randomUUID().toString();
            String extension = getExtension(file.getOriginalFilename());
            String storedFileName = id + "." + extension;

            Path path = Paths.get(uploadDir, storedFileName);
            Files.createDirectories(path.getParent());

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            FileMetadata metadata = new FileMetadata(
                    id,
                    file.getOriginalFilename(),
                    storedFileName,
                    file.getContentType(),
                    file.getSize()
            );

            storage.put(id, metadata);
            return metadata;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed");
        }
    }

    public Resource getFile(String id) {
        FileMetadata metadata = storage.get(id);

        if (metadata == null) {
            throw new FileNotFoundException("File not found");
        }

        try {
            Path path = Paths.get(uploadDir).resolve(metadata.getStoredFileName());
            return (Resource) new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error retrieving file");
        }
    }

    private void validateFile(MultipartFile file) {
        List<String> allowed = List.of("image/jpeg", "image/png", "application/pdf");

        if (!allowed.contains(file.getContentType())) {
            throw new InvalidFileException("Only JPG, PNG, PDF allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidFileException("File exceeds 5MB");
        }
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
    public FileMetadata getMetadata(String id) {
        FileMetadata metadata = storage.get(id);

        if (metadata == null) {
            throw new FileNotFoundException("File not found");
        }

        return metadata;
    }
}