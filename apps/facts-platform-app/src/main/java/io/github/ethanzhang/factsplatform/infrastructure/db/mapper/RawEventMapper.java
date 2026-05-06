package io.github.ethanzhang.factsplatform.infrastructure.db.mapper;

import io.github.ethanzhang.factsplatform.infrastructure.db.entity.RawEventEntity;
import io.github.ethanzhang.factsplatform.infrastructure.db.entity.RawEventIdentify;
import org.apache.ibatis.annotations.Param;

public interface RawEventMapper {
    void insertIdentify(@Param("entity") RawEventIdentify entity);

    void insertRawEvent(@Param("entity") RawEventEntity entity);
}
