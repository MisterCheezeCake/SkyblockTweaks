package wtf.cheeze.sbt.utils;

public class TimedValue<T> {
    private T value;
    private long time;

    private int expireTime = 1000;

    private TimedValue(T value) {
        this.value = value;
        this.time = System.currentTimeMillis();
    }
    private TimedValue(T value, int expireTime) {
        this.value = value;
        this.time = System.currentTimeMillis();
        this.expireTime = expireTime;
    }
    public T getValue() {
        if (isExpired()) {
            return null;
        }
        return value;
    }
    private boolean isExpired() {
        return System.currentTimeMillis() - time > expireTime;
    }
    public static <T> TimedValue<T> of(T value) {
        return new TimedValue<>(value);
    }
    public static <T> TimedValue<T> of(T value, int expireTime) {
        return new TimedValue<>(value, expireTime);
    }
}
