package com.wordle.blog.dto;

import org.springframework.core.io.Resource;

public record LoadedFile(Resource resource, String contentType) {
}