package io.github.ethanzhang.factsplatform.infrastructure.db.repository;

import common.utils.IdGenerator;
import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventAggRoot;
import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventRepository;
import io.github.ethanzhang.factsplatform.infrastructure.db.entity.RawEventEntity;
import io.github.ethanzhang.factsplatform.infrastructure.db.entity.RawEventIdentify;
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

        RawEventIdentify rawEventIdentify = new RawEventIdentify();
        rawEventIdentify.setRawEventId(rawEventId);
        rawEventIdentify.setSource(event.getSource());
        rawEventIdentify.setIdentifyKey(event.getIdentifyKey());
        rawEventIdentify.setZoneId(event.getZoneId());
        rawEventIdentify.setIngestTime(event.getIngestTime());
        rawEventIdentify.setTimelineSequenceId(1L);

        rawEventMapper.insertIdentify(rawEventIdentify);
        rawEventMapper.insertRawEvent(new RawEventEntity(rawEventId, event.getEventBody()));
        return rawEventId;
    }
}
