package com.wordle.blog.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Simple carrier returned by any StorageStrategy.upload() call.
 *
 * Bundling url + storageKey together (rather than returning just a String
 * URL) forces every strategy implementation to produce both values at upload
 * time — preventing a future implementer from "forgetting" to generate a
 * proper key and accidentally making deletion impossible later.
 */
@Getter
@AllArgsConstructor
public class StorageResult {
    private final String url;
    private final String storageKey;
}