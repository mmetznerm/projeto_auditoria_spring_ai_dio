package com.example.springai.infrastructure.http;

import com.example.springai.application.ListByCategoryTransactionUseCase;
import com.example.springai.application.AudioFileMetadata;
import com.example.springai.application.AudioFileMetadataContext;
import com.example.springai.application.AudioProcessingLogService;
import com.example.springai.application.GetTransactionSummaryUseCase;
import com.example.springai.application.PersistTransactionUseCase;
import com.example.springai.domain.Category;
import com.example.springai.infrastructure.http.request.TransactionRequest;
import com.example.springai.infrastructure.http.response.AudioProcessingLogResponse;
import com.example.springai.infrastructure.http.response.TransactionResponse;
import com.example.springai.infrastructure.http.response.TransactionSummaryResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListByCategoryTransactionUseCase listByCategoryTransactionUseCase;
    private final GetTransactionSummaryUseCase getTransactionSummaryUseCase;

    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;
    private final AudioFileMetadataContext audioFileMetadataContext;
    private final AudioProcessingLogService audioProcessingLogService;

    public TransactionController(
            PersistTransactionUseCase persistTransactionUseCase,
            ListByCategoryTransactionUseCase listByCategoryTransactionUseCase,
            GetTransactionSummaryUseCase getTransactionSummaryUseCase,
            TranscriptionModel transcriptionModel,
            @Value("classpath:/prompts/system-message.st") Resource systemPrompt,
            ChatClient.Builder chatClientBuilder,
            TextToSpeechModel textToSpeechModel,
            AudioFileMetadataContext audioFileMetadataContext,
            AudioProcessingLogService audioProcessingLogService
    ) throws IOException {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listByCategoryTransactionUseCase = listByCategoryTransactionUseCase;
        this.getTransactionSummaryUseCase = getTransactionSummaryUseCase;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
        this.audioFileMetadataContext = audioFileMetadataContext;
        this.audioProcessingLogService = audioProcessingLogService;
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(persistTransactionUseCase, listByCategoryTransactionUseCase, getTransactionSummaryUseCase)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        var transaction = persistTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(transaction);
    }

    @GetMapping("/summary")
    public TransactionSummaryResponse getSummary() {
        return TransactionSummaryResponse.from(getTransactionSummaryUseCase.execute());
    }

    @GetMapping("/{category}")
    public List<TransactionResponse> getAllTransactionsByCategory(@PathVariable Category category) {
        return listByCategoryTransactionUseCase.execute(category).stream().map(TransactionResponse::from).toList();
    }

    @GetMapping("/audio-processing-logs")
    public List<AudioProcessingLogResponse> getAudioProcessingLogs() {
        return audioProcessingLogService.list().stream().map(AudioProcessingLogResponse::from).toList();
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {
        var metadata = new AudioFileMetadata(
                "AUDIO_UPLOAD",
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
        );

        audioFileMetadataContext.set(metadata);
        var processingLog = audioProcessingLogService.start(metadata);
        String userMessage = null;

        try {
            var resources = file.getResource();
            userMessage = transcriptionModel.transcribe(resources);

            var result = chatClient.prompt().user(userMessage).call().content();
            audioProcessingLogService.complete(processingLog.getId(), userMessage, result);

            byte[] audio = textToSpeechModel.call(result);
            var resource = new ByteArrayResource(audio);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition
                                    .attachment()
                                    .filename("audio.mp3")
                                    .build()
                                    .toString())
                    .body(resource);
        } catch (RuntimeException exception) {
            audioProcessingLogService.fail(processingLog.getId(), userMessage, exception.getMessage());
            throw exception;
        } finally {
            audioFileMetadataContext.clear();
        }
    }
}
