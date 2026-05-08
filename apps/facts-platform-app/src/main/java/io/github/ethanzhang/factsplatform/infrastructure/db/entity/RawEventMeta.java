package io.github.ethanzhang.factsplatform.infrastructure.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@TableName("raw_event_meta")
public class RawEventMeta {
    @TableId
    private long rawEventId;
    private String source;
    private String eventType;
    private String identifyKey;
    private String zoneId;
    private LocalDateTime ingestTime;
    private LocalDateTime createdAt;
}
