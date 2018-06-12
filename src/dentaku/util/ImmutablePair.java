package dentaku.util;

import java.util.Objects;

public final class ImmutablePair<T, U> {
    public final T item1;
    public final U item2;

    public ImmutablePair(T item1, U item2) {
        this.item1 = item1;
        this.item2 =item2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutablePair)) return false;
        ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) o;
        return Objects.equals(item1, that.item1) &&
            Objects.equals(item2, that.item2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item1, item2);
    }
}
