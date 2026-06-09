package com.example.springai.application;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AudioFileMetadataContext {
    private final ThreadLocal<AudioFileMetadata> currentMetadata = new ThreadLocal<>();

    public void set(AudioFileMetadata metadata) {
        currentMetadata.set(metadata);
    }

    public Optional<AudioFileMetadata> get() {
        return Optional.ofNullable(currentMetadata.get());
    }

    public void clear() {
        currentMetadata.remove();
    }
}
