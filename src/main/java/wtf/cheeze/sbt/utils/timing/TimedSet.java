package wtf.cheeze.sbt.utils.timing;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TimedSet<T> {

    private final int expireTime;
    private final Set<Entry<T>> backingSet = new HashSet<>();


    public TimedSet(int expireTime) {
        this.expireTime = expireTime;
    }

    private Set<T> getValues() {
        expiryCheck();
        return backingSet.stream().map(Entry::value).collect(Collectors.toSet());
    }

    public boolean contains(T value) {
        return getValues().contains(value);
    }

    public void add(T value) {
        backingSet.add(new Entry<>(value, System.currentTimeMillis()));
    }

    public void remove(T value) {
        backingSet.removeIf(entry -> entry.value().equals(value));
    }

    private void expiryCheck() {
        long t = System.currentTimeMillis();
        backingSet.removeIf(entry ->  t - entry.time() > expireTime);
    }


    private record Entry<T>(T value, long time) {}


}
