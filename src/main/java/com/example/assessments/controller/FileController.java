package com.example.assessments.controller;

import com.example.assessments.model.FileMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String id = UUID.randomUUID().toString();
        String storedFileName = id + "_" + file.getOriginalFilename();

        FileMetadata metadata = new FileMetadata(
                id,
                file.getOriginalFilename(),
                storedFileName,
                file.getContentType(),
                file.getSize()
        );

        // Here you can save the file to disk or database if needed
        return ResponseEntity.ok(metadata);
    }
}