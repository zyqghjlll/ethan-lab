package io.github.ethanzhang.factsplatform.infrastructure.client;

import io.github.ethanzhang.factsplatform.infrastructure.client.classify.ClassificationResult;

import java.io.IOException;

public interface ClassificationClient {
    ClassificationResult getClassificationResult(String text) throws IOException, InterruptedException;
}
