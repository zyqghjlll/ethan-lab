package io.github.ethanzhang.factsplatform.domain.rawevent;

public interface RawEventRepository {

    void save(RawEventAggRoot event);
}
