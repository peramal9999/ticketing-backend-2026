package com.peramal.ticketingsys.service;

import com.peramal.ticketingsys.dto.request.CreateCategoryRequest;
import com.peramal.ticketingsys.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryResponse> getAll();
    CategoryResponse getById(UUID id);
    CategoryResponse create(CreateCategoryRequest request);
    CategoryResponse update(UUID id, CreateCategoryRequest request);
    void delete(UUID id);
}
