package io.github.ethanzhang.factsplatform.infrastructure.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;

@Data
@TableName("raw_event_identify")
public class RawEventIdentify {
    @TableId
    private long rawEventId;
    private String source;
    private String identifyKey;
    private String zoneId;
    private Instant ingestTime;
    private long randomNumber;
    private long timelineSequenceId;
}
