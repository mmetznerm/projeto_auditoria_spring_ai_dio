package com.example.springai.infrastructure.http;

import com.example.springai.application.ListByCategoryTransactionUseCase;
import com.example.springai.application.PersistTransactionUseCase;
import com.example.springai.domain.Category;
import com.example.springai.infrastructure.http.request.TransactionRequest;
import com.example.springai.infrastructure.http.response.TransactionResponse;
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

    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;

    public TransactionController(
            PersistTransactionUseCase persistTransactionUseCase,
            ListByCategoryTransactionUseCase listByCategoryTransactionUseCase,
            TranscriptionModel transcriptionModel,
            @Value("classpath:/prompts/system-message.st") Resource systemPrompt,
            ChatClient.Builder chatClientBuilder, TextToSpeechModel textToSpeechModel
    ) throws IOException {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listByCategoryTransactionUseCase = listByCategoryTransactionUseCase;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(persistTransactionUseCase, listByCategoryTransactionUseCase)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        var transaction = persistTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    public List<TransactionResponse> getAllTransactionsByCategory(@PathVariable Category category) {
        return listByCategoryTransactionUseCase.execute(category).stream().map(TransactionResponse::from).toList();
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {
        var resources = file.getResource();
        var userMessage = transcriptionModel.transcribe(resources);

        var result = chatClient.prompt().user(userMessage).call().content();

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

    }
}
