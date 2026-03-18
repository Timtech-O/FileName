package com.example.assessments.service;

import com.example.assessments.model.FileMetadata;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final Path uploadDir = Paths.get("uploads");
    private final Map<String, FileMetadata> metadataStore = new HashMap<>();

    public FileServiceImpl() {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public FileMetadata uploadFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // ✅ Generate ONE ID
        String id = UUID.randomUUID().toString();

        // ✅ Use same ID for stored filename
        String storedFileName = id + "_" + originalFileName;

        Path targetLocation = uploadDir.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        // ✅ Use SAME ID here
        FileMetadata metadata = new FileMetadata(
                id,
                originalFileName,
                storedFileName,
                file.getContentType(),
                file.getSize()
        );

        metadataStore.put(id, metadata);

        return metadata;
    }

    @Override
    public Resource getFile(String id) {
        FileMetadata metadata = getMetadata(id);
        Path filePath = uploadDir.resolve(metadata.getStoredFileName()).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File URL is invalid", e);
        }
    }

    @Override
    public FileMetadata getMetadata(String id) {
        FileMetadata metadata = metadataStore.get(id);
        if (metadata == null) throw new RuntimeException("File not found");
        return metadata;
    }
}