package io.github.ethanzhang.factsplatform.application.ports;

public record EventMessage(
        String eventId,        // 唯一ID，用于幂等性去重
        String eventType,      // 事件类型，ORDER_CREATED等
        String source,         // 来源服务
        String eventBody,      // 业务数据
        long timestamp         // 发生时间
) {}