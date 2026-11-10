package io.github.ethanzhang.factsplatform.infrastructure.client.classify;

import io.github.ethanzhang.factsplatform.infrastructure.client.ClassificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Component
public class AIClassifyProvider implements ClassificationClient {
    @Override
    public ClassificationResult getClassificationResult(String text) throws IOException, InterruptedException {
        try (HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8000/classify"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"text\": \"I was charged twice\"}"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                log.info("Classification result is {}", response.body());
            } else {
                log.error("Classification result is {}, {}", response.statusCode(), response.body());
            }
        }
        return null;
    }
}
