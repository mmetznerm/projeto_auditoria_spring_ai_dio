package com.example.springai.application;

public record AudioFileMetadata(
        String sourceType,
        String fileName,
        String contentType,
        Long fileSize
) {
}
