package io.github.ethanzhang.factsplatform.infrastructure.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("raw_event")
@NoArgsConstructor
public class RawEventEntity {
    @TableId
    private long rawEventId;
    private String eventBody;

    public RawEventEntity(long rawEventId, String eventBody) {
        this.rawEventId = rawEventId;
        this.eventBody = eventBody;
    }
}
