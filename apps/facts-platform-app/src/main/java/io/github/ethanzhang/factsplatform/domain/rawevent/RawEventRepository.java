package io.github.ethanzhang.factsplatform.domain.rawevent;

public interface RawEventRepository {

    long save(RawEventAggRoot event);
}
