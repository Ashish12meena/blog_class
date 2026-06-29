package com.wordle.blog.service;

import com.wordle.blog.dto.CategoryResponseDTO;
import com.wordle.blog.dto.CreateCategoryRequestDto;
import com.wordle.blog.dto.UpdateCategoryRequestDto;
import com.wordle.blog.enitity.Category;
import com.wordle.blog.exception.CategoryAlreadyExistException;
import com.wordle.blog.exception.CategoryNotFoundException;
import com.wordle.blog.mapper.CategoryMapper;
import com.wordle.blog.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryResponseDTO createCategory(CreateCategoryRequestDto request) {
        if (categoryRepository.existsByName(request.getName())) {
            log.warn("Attempt to create duplicate category with name '{}'", request.getName());
            throw new CategoryAlreadyExistException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);
        log.info("Created category with id {} and name '{}'", saved.getId(), saved.getName());

        return categoryMapper.toResponseDTO(saved);
    }

    public CategoryResponseDTO getById(Long id) {
        return categoryMapper.toResponseDTO(findEntityById(id));
    }

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, UpdateCategoryRequestDto request) {
        Category category = findEntityById(id);

        if (StringUtils.hasText(request.getName()) && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                log.warn("Attempt to rename category {} to already-used name '{}'", id, request.getName());
                throw new CategoryAlreadyExistException("Category with name '" + request.getName() + "' already exists");
            }
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        Category saved = categoryRepository.save(category);
        log.info("Updated category with id {}", saved.getId());

        return categoryMapper.toResponseDTO(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findEntityById(id);
        categoryRepository.delete(category);
        log.info("Deleted category with id {}", id);
    }

    private Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found for id {}", id);
                    return new CategoryNotFoundException("Category not found at id: " + id);
                });
    }
}