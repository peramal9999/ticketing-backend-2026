package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    public static final String TICKETS_DIR = "tickets";

    @Value("${app.upload.dir}")
    private String storageDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(storageDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + storageDir, e);
        }
    }

    @Override
    public String save(String directory, UUID contextId, MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
        );

        // Guard against path traversal in the original filename
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename: " + originalFilename);
        }

        // Preserve the original file extension
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        // Random UUID-based filename — never uses user-supplied name for storage
        String storedFilename = UUID.randomUUID() + extension;

        // Store under <storageDir>/<directory>/<contextId>/<uuid.ext>
        Path targetDir = rootLocation.resolve(directory).resolve(contextId.toString()).normalize();

        // Guard: ensure resolved path is still inside rootLocation (path traversal prevention)
        if (!targetDir.startsWith(rootLocation)) {
            throw new IllegalArgumentException("Resolved path escapes storage root");
        }

        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(), targetDir.resolve(storedFilename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + originalFilename, e);
        }

        return directory + "/" + contextId + "/" + storedFilename;
    }

    @Override
    public Resource load(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new ResourceNotFoundException("File", "path", relativePath);
        }

        Path filePath = rootLocation.resolve(relativePath).normalize();

        // Guard against path traversal
        if (!filePath.startsWith(rootLocation)) {
            throw new IllegalArgumentException("Resolved path escapes storage root");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new ResourceNotFoundException("File", "path", relativePath);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + relativePath, e);
        }
    }

    @Override
    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        Path filePath = rootLocation.resolve(relativePath).normalize();

        // Guard against path traversal
        if (!filePath.startsWith(rootLocation)) {
            log.warn("Attempted path traversal on delete: {}", relativePath);
            return;
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", relativePath, e);
        }
    }
}
