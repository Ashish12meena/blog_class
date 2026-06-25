package com.wordle.blog.strategy;

import com.wordle.blog.enums.StorageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The heart of the resolver-based design.
 *
 * Spring automatically collects EVERY bean that implements StorageStrategy
 * into the List<StorageStrategy> constructor parameter below — this is a
 * core Spring DI feature, not something we wrote manually. We then turn that
 * list into a Map keyed by StorageType, so that at request time we can do an
 * O(1) lookup: "give me whichever strategy handles S3" or "...handles LOCAL."
 *
 * This is what enables per-request backend selection: unlike the
 * @ConditionalOnProperty design, every strategy implementation is alive in
 * memory simultaneously, and any single request can ask for any of them.
 */
@Slf4j
@Component
public class StorageStrategyResolver {

    private final Map<StorageType, StorageStrategy> strategies;

    public StorageStrategyResolver(List<StorageStrategy> strategyBeans) {
        this.strategies = strategyBeans.stream()
                .collect(Collectors.toMap(StorageStrategy::getStorageType, s -> s));
        log.info("Registered storage strategies: {}", strategies.keySet());
    }

    /**
     * Looks up the strategy for a given StorageType.
     *
     * Throws immediately if no implementation is registered for that type —
     * this is a DELIBERATE fail-fast point. Compare this to
     * @ConditionalOnProperty, where a typo'd property fails at application
     * STARTUP (no bean gets created). Here, since all strategies always
     * exist, a bad StorageType only surfaces when someone actually requests
     * it — i.e. failure happens later, at request time, not boot time. Worth
     * pointing out to students as the real tradeoff between the two designs.
     */
    public StorageStrategy resolve(StorageType storageType) {
        StorageStrategy strategy = strategies.get(storageType);
        if (strategy == null) {
            throw new IllegalArgumentException("No storage strategy registered for type: " + storageType);
        }
        return strategy;
    }
}