package io.github.ethanzhang.factsplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.github.ethanzhang.factsplatform.infrastructure.db.mapper")
public class FactsPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(FactsPlatformApplication.class, args);
    }
}
