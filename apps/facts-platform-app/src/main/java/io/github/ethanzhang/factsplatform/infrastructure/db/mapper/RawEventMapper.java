package io.github.ethanzhang.factsplatform.infrastructure.db.mapper;

import io.github.ethanzhang.factsplatform.infrastructure.db.entity.RawEventIdentify;
import org.apache.ibatis.annotations.Param;

public interface RawEventMapper {
    void insert(@Param("entity") RawEventIdentify entity);
}
