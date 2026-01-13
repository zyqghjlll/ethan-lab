package common.core;

import java.io.Serializable;

public interface AggregateRoot<T> extends Serializable {
    T getId();
}
