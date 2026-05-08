package io.github.ethanzhang.factsplatform.infrastructure.db.mapper;

import io.github.ethanzhang.factsplatform.infrastructure.db.entity.RawEventContent;
import io.github.ethanzhang.factsplatform.infrastructure.db.entity.RawEventMeta;
import org.apache.ibatis.annotations.Param;

public interface RawEventMapper {
    void insertMeta(@Param("entity") RawEventMeta entity);

    void insertContent(@Param("entity") RawEventContent entity);
}
