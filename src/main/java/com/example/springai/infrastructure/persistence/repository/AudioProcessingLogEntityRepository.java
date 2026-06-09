package com.example.springai.infrastructure.persistence.repository;

import com.example.springai.infrastructure.persistence.entity.AudioProcessingLogEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface AudioProcessingLogEntityRepository extends CrudRepository<AudioProcessingLogEntity, UUID> {
    List<AudioProcessingLogEntity> findAllByOrderByCreatedAtDesc();
}
