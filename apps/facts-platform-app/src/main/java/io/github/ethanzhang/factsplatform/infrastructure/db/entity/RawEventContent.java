package io.github.ethanzhang.factsplatform.infrastructure.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("raw_event_content")
@NoArgsConstructor
public class RawEventContent {
    @TableId
    private long rawEventId;
    private String eventBody;
    private LocalDateTime createdAt;

    public RawEventContent(long rawEventId, String eventBody) {
        this.rawEventId = rawEventId;
        this.eventBody = eventBody;
    }
}
