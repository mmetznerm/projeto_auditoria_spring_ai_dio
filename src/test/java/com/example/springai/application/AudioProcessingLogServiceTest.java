package com.example.springai.application;

import com.example.springai.infrastructure.persistence.entity.AudioProcessingLogEntity;
import com.example.springai.infrastructure.persistence.repository.AudioProcessingLogEntityRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AudioProcessingLogServiceTest {

    @Test
    void should_startAudioProcessingLogWithMetadata() {
        var repository = mock(AudioProcessingLogEntityRepository.class);
        var service = new AudioProcessingLogService(repository);
        var savedLog = ArgumentCaptor.forClass(AudioProcessingLogEntity.class);
        var metadata = new AudioFileMetadata("AUDIO_UPLOAD", "audio.wav", "audio/wav", 1024L);

        when(repository.save(any(AudioProcessingLogEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var log = service.start(metadata);

        assertThat(log.getStatus()).isEqualTo(AudioProcessingStatus.PROCESSING);
        assertThat(log.getFileName()).isEqualTo("audio.wav");
        assertThat(log.getContentType()).isEqualTo("audio/wav");
        assertThat(log.getFileSize()).isEqualTo(1024L);
        verify(repository).save(savedLog.capture());
        assertThat(savedLog.getValue().getId()).isNotNull();
        assertThat(savedLog.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    void should_completeAudioProcessingLog() {
        var repository = mock(AudioProcessingLogEntityRepository.class);
        var service = new AudioProcessingLogService(repository);
        var log = AudioProcessingLogEntity.start(new AudioFileMetadata("AUDIO_UPLOAD", "audio.wav", "audio/wav", 1024L));
        var savedLog = ArgumentCaptor.forClass(AudioProcessingLogEntity.class);

        when(repository.findById(log.getId())).thenReturn(Optional.of(log));

        service.complete(log.getId(), "gastei 33 reais no mercado", "Transacao registrada");

        verify(repository).save(savedLog.capture());
        assertThat(savedLog.getValue().getStatus()).isEqualTo(AudioProcessingStatus.COMPLETED);
        assertThat(savedLog.getValue().getTranscription()).isEqualTo("gastei 33 reais no mercado");
        assertThat(savedLog.getValue().getAiResponse()).isEqualTo("Transacao registrada");
    }

    @Test
    void should_failAudioProcessingLog() {
        var repository = mock(AudioProcessingLogEntityRepository.class);
        var service = new AudioProcessingLogService(repository);
        var log = AudioProcessingLogEntity.start(new AudioFileMetadata("AUDIO_UPLOAD", "audio.wav", "audio/wav", 1024L));
        var savedLog = ArgumentCaptor.forClass(AudioProcessingLogEntity.class);

        when(repository.findById(log.getId())).thenReturn(Optional.of(log));

        service.fail(log.getId(), "audio incompleto", "OpenAI error");

        verify(repository).save(savedLog.capture());
        assertThat(savedLog.getValue().getStatus()).isEqualTo(AudioProcessingStatus.FAILED);
        assertThat(savedLog.getValue().getTranscription()).isEqualTo("audio incompleto");
        assertThat(savedLog.getValue().getErrorMessage()).isEqualTo("OpenAI error");
    }
}
