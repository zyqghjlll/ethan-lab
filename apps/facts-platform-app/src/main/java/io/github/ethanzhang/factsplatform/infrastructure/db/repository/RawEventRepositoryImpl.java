package io.github.ethanzhang.factsplatform.infrastructure.db.repository;

import common.utils.IdGenerator;
import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventAggRoot;
import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventRepository;
import io.github.ethanzhang.factsplatform.infrastructure.db.entity.RawEventContent;
import io.github.ethanzhang.factsplatform.infrastructure.db.entity.RawEventMeta;
import io.github.ethanzhang.factsplatform.infrastructure.db.mapper.RawEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RawEventRepositoryImpl implements RawEventRepository {

    private final RawEventMapper rawEventMapper;
    private final IdGenerator idGenerator;

    @Override
    public long save(RawEventAggRoot event) {
        long rawEventId = idGenerator.generateSequence();

        RawEventMeta meta = new RawEventMeta();
        meta.setRawEventId(rawEventId);
        meta.setSource(event.getSource());
        meta.setEventType(event.getEventType());
        meta.setIdentifyKey(event.getIdentifyKey());
        meta.setZoneId(event.getZoneId());
        meta.setIngestTime(event.getIngestTime());

        RawEventContent content = new RawEventContent(rawEventId, event.getEventBody());

        rawEventMapper.insertMeta(meta);
        rawEventMapper.insertContent(content);
        return rawEventId;
    }
}
