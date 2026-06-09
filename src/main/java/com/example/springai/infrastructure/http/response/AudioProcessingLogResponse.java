package com.example.springai.infrastructure.http.response;

import com.example.springai.application.AudioProcessingStatus;
import com.example.springai.infrastructure.persistence.entity.AudioProcessingLogEntity;

import java.time.LocalDateTime;

public record AudioProcessingLogResponse(
        String id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String sourceType,
        String fileName,
        String contentType,
        Long fileSize,
        String transcription,
        String aiResponse,
        String errorMessage,
        AudioProcessingStatus status
) {
    public static AudioProcessingLogResponse from(AudioProcessingLogEntity entity) {
        return new AudioProcessingLogResponse(
                entity.getId().toString(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getSourceType(),
                entity.getFileName(),
                entity.getContentType(),
                entity.getFileSize(),
                entity.getTranscription(),
                entity.getAiResponse(),
                entity.getErrorMessage(),
                entity.getStatus()
        );
    }
}
