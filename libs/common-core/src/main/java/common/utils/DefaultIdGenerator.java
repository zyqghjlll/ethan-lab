package common.utils;

import cn.hutool.core.util.IdUtil;

import java.util.UUID;

public class DefaultIdGenerator implements IdGenerator {
    @Override
    public String nextId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public long generateSequence() {
        return IdUtil.getSnowflakeNextId();
    }
}
