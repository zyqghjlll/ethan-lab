-- 事件元数据
CREATE TABLE raw_event_meta (
                                    raw_event_id        BIGINT PRIMARY KEY,
                                    source              VARCHAR(64),   -- 来源服务
                                    event_type          VARCHAR(64),   -- 事件类型
                                    identify_key        VARCHAR(128),  -- 业务唯一键（幂等用）
                                    zone_id             VARCHAR(32),   -- 时区
                                    ingest_time         TIMESTAMP,     -- 业务发生时间
                                    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 事件内容
CREATE TABLE raw_event_content (
                           raw_event_id        BIGINT PRIMARY KEY,
                           event_body          TEXT,          -- 业务JSON
                           created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);