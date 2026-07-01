package com.wordle.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordle.blog.dto.CreateTagRequestDto;
import com.wordle.blog.dto.TagResponseDTO;
import com.wordle.blog.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    
    @PostMapping
    public TagResponseDTO createTag(@RequestBody CreateTagRequestDto createTagRequestDto ){
       return  tagService.createTag(createTagRequestDto);
    }

    @GetMapping("/get/{id}")
    public TagResponseDTO getTag(@PathVariable Long id){
       return  tagService.getById(id);
    }
}
