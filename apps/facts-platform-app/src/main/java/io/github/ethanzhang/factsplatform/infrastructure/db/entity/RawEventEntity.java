package io.github.ethanzhang.factsplatform.infrastructure.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("raw_event")
public class RawEventEntity {
    @TableId
    private String rawEventId;
    private String eventBody;

    public RawEventEntity(String rawEventId, String eventBody) {
    }
}
