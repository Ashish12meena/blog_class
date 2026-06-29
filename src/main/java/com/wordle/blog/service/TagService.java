package com.wordle.blog.service;

import com.wordle.blog.dto.CreateTagRequestDto;
import com.wordle.blog.dto.TagResponseDTO;
import com.wordle.blog.dto.UpdateTagRequestDto;
import com.wordle.blog.enitity.Tag;
import com.wordle.blog.exception.TagAlreadyExistException;
import com.wordle.blog.exception.TagNotFoundException;
import com.wordle.blog.mapper.TagMapper;
import com.wordle.blog.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Transactional
    public TagResponseDTO createTag(CreateTagRequestDto request) {
        if (tagRepository.existsByName(request.getName())) {
            log.warn("Attempt to create duplicate tag with name '{}'", request.getName());
            throw new TagAlreadyExistException("Tag with name '" + request.getName() + "' already exists");
        }

        Tag tag = tagMapper.toEntity(request);
        Tag saved = tagRepository.save(tag);
        log.info("Created tag with id {} and name '{}'", saved.getId(), saved.getName());

        return tagMapper.toResponseDTO(saved);
    }

    public TagResponseDTO getById(Long id) {
        return tagMapper.toResponseDTO(findEntityById(id));
    }

    public List<TagResponseDTO> getAllTags() {
        return tagRepository.findAll()
                .stream()
                .map(tagMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public TagResponseDTO updateTag(Long id, UpdateTagRequestDto request) {
        Tag tag = findEntityById(id);

        if (StringUtils.hasText(request.getName()) && !request.getName().equals(tag.getName())) {
            if (tagRepository.existsByName(request.getName())) {
                log.warn("Attempt to rename tag {} to already-used name '{}'", id, request.getName());
                throw new TagAlreadyExistException("Tag with name '" + request.getName() + "' already exists");
            }
            tag.setName(request.getName());
        }

        if (StringUtils.hasText(request.getColor())) {
            tag.setColor(request.getColor());
        }

        Tag saved = tagRepository.save(tag);
        log.info("Updated tag with id {}", saved.getId());

        return tagMapper.toResponseDTO(saved);
    }

    @Transactional
    public void deleteTag(Long id) {
        Tag tag = findEntityById(id);
        tagRepository.delete(tag);
        log.info("Deleted tag with id {}", id);
    }

    private Tag findEntityById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tag not found for id {}", id);
                    return new TagNotFoundException("Tag not found at id: " + id);
                });
    }
}