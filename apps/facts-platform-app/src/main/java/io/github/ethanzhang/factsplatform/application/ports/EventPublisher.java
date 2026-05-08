package io.github.ethanzhang.factsplatform.application.ports;

public interface EventPublisher {
    void publish(EventMessage eventMessage);
}
