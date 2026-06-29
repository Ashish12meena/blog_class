package com.wordle.blog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wordle.blog.strategy.StorageStrategy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RandomService {
    private final StorageStrategy storageStrategy;


    public void uploadThisFile(MultipartFile file){
        storageStrategy.upload(file);
    }
}
