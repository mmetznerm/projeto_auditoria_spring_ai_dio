package com.example.springai.infrastructure.persistence.entity;

import com.example.springai.application.AudioFileMetadata;
import com.example.springai.application.AudioProcessingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class AudioProcessingLogEntity {
    @Id
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String sourceType;
    private String fileName;
    private String contentType;
    private Long fileSize;

    @Lob
    private String transcription;

    @Lob
    private String aiResponse;

    @Lob
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    private AudioProcessingStatus status;

    public static AudioProcessingLogEntity start(AudioFileMetadata metadata) {
        var now = LocalDateTime.now();
        var entity = new AudioProcessingLogEntity();
        entity.id = UUID.randomUUID();
        entity.createdAt = now;
        entity.updatedAt = now;
        entity.sourceType = metadata.sourceType();
        entity.fileName = metadata.fileName();
        entity.contentType = metadata.contentType();
        entity.fileSize = metadata.fileSize();
        entity.status = AudioProcessingStatus.PROCESSING;
        return entity;
    }

    public void complete(String transcription, String aiResponse) {
        this.transcription = transcription;
        this.aiResponse = aiResponse;
        this.status = AudioProcessingStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail(String transcription, String errorMessage) {
        this.transcription = transcription;
        this.errorMessage = errorMessage;
        this.status = AudioProcessingStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }
}
