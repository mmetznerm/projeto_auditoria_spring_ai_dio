package com.example.springai.application;

import com.example.springai.infrastructure.persistence.entity.AudioProcessingLogEntity;
import com.example.springai.infrastructure.persistence.repository.AudioProcessingLogEntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AudioProcessingLogService {
    private final AudioProcessingLogEntityRepository repository;

    public AudioProcessingLogService(AudioProcessingLogEntityRepository repository) {
        this.repository = repository;
    }

    public AudioProcessingLogEntity start(AudioFileMetadata metadata) {
        return repository.save(AudioProcessingLogEntity.start(metadata));
    }

    public void complete(UUID id, String transcription, String aiResponse) {
        repository.findById(id).ifPresent(log -> {
            log.complete(transcription, aiResponse);
            repository.save(log);
        });
    }

    public void fail(UUID id, String transcription, String errorMessage) {
        repository.findById(id).ifPresent(log -> {
            log.fail(transcription, errorMessage);
            repository.save(log);
        });
    }

    public List<AudioProcessingLogEntity> list() {
        return repository.findAllByOrderByCreatedAtDesc();
    }
}
