package io.github.ethanzhang.factsplatform.interfaces;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Value("${app.version:dev}")
    private String appVersion;

    @GetMapping("/live")
    public Map<String, Object> live() {
        return Map.of(
                "status", "UP",
                "version", appVersion,
                "timezone", ZoneId.systemDefault().toString()
        );
    }

    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        // todo 后续补充 DB / Outbox / Kafka 等依赖检查
        boolean ready = true;
        if (ready) {
            return ResponseEntity.ok(Map.of("status", "READY"));
        }
        return ResponseEntity.status(503).body(Map.of("status", "NOT_READY"));
    }
}
