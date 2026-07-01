package com.wordle.blog.strategy;

import com.wordle.blog.dto.LoadedFile;

public interface LoadLocalStorage {
    LoadedFile loadFile(String filename);
    
}