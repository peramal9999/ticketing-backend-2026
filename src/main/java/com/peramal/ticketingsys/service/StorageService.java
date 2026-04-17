package com.peramal.ticketingsys.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {

    /**
     * Save a file to disk under <storageDir>/<directory>/<contextId>/<uuid>.ext
     * e.g. uploads/tickets/34sdfsdfs/aerwer.pdf
     *
     * @param directory  top-level subdirectory name (e.g. "tickets")
     * @param contextId  context identifier subdirectory (e.g. ticketId)
     * @param file       multipart file to store
     * @return           relative path stored in DB (e.g. tickets/<contextId>/<uuid>.ext)
     */
    String save(String directory, UUID contextId, MultipartFile file);

    /**
     * Load a stored file as a Spring Resource using its relative path.
     *
     * @param relativePath  the relative path returned by save() (e.g. tickets/<contextId>/<uuid>.ext)
     * @return              readable Resource
     */
    Resource load(String relativePath);

    /**
     * Delete a stored file from disk using its relative path.
     *
     * @param relativePath  the relative path returned by save() (e.g. tickets/<contextId>/<uuid>.ext)
     */
    void delete(String relativePath);
}
