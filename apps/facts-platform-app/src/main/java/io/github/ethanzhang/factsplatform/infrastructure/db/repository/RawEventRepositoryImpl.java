package io.github.ethanzhang.factsplatform.infrastructure.db.repository;

import common.utils.IdGenerator;
import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventAggRoot;
import io.github.ethanzhang.factsplatform.domain.rawevent.RawEventRepository;
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
    public void save(RawEventAggRoot event) {
        RawEventIdentify rawEventIdentify = new RawEventIdentify();
        rawEventIdentify.setRawEventId(idGenerator.generateSequence());
        rawEventIdentify.setSource(event.getSource());
        rawEventIdentify.setIdentifyKey(event.getIdentifyKey());
        rawEventIdentify.setZoneId(event.getZoneId());
        rawEventIdentify.setIngestTime(event.getIngestTime());
        rawEventIdentify.setTimelineSequenceId(1L);

        rawEventMapper.insert(rawEventIdentify);
    }
}
