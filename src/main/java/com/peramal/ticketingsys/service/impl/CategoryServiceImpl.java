package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.dto.request.CreateCategoryRequest;
import com.peramal.ticketingsys.dto.response.CategoryResponse;
import com.peramal.ticketingsys.entity.Category;
import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.repository.CategoryRepository;
import com.peramal.ticketingsys.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public CategoryResponse create(CreateCategoryRequest req) {
        Category category = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();
        CategoryResponse response = toResponse(categoryRepository.save(category));
        log.info("Category created: id={}, name={}", response.getId(), response.getName());
        return response;
    }

    @Override
    public CategoryResponse update(UUID id, CreateCategoryRequest req) {
        Category category = findOrThrow(id);
        if (req.getName() != null) category.setName(req.getName());
        if (req.getDescription() != null) category.setDescription(req.getDescription());
        CategoryResponse response = toResponse(categoryRepository.save(category));
        log.info("Category updated: id={}", id);
        return response;
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        categoryRepository.deleteById(id);
        log.info("Category deleted: id={}", id);
    }

    private Category findOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
