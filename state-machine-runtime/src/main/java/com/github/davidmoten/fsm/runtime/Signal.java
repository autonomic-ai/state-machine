package com.github.davidmoten.fsm.runtime;

public final class Signal<T, Id, R> {

    private final Class<T> cls;
    private final Id id;
    private final Event<R> event;
    // TODO use Optional?
    private final long time;

    private Signal(Class<T> cls, Id id, Event<R> event, long time) {
        this.cls = cls;
        this.id = id;
        this.event = event;
        this.time = time;
    }

    public static <T, Id, R> Signal<T, Id, R> create(Class<T> cls, Id id, Event<R> event) {
        return new Signal<T, Id, R>(cls, id, event, Long.MIN_VALUE);
    }

    public static <T, Id, R> Signal<T, Id, R> create(Class<T> cls, Id id, Event<R> event,
            long time) {
        return new Signal<T, Id, R>(cls, id, event, time);
    }

    public Class<T> cls() {
        return cls;
    }

    public Event<R> event() {
        return event;
    }

    public Id id() {
        return id;
    }

    public long time() {
        return time;
    }

    public boolean isImmediate() {
        return time == Long.MIN_VALUE;
    }

    public Signal<T, Id, ?> now() {
        return create(cls, id, event);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Signal [cls=");
        builder.append(cls);
        builder.append(", id=");
        builder.append(id);
        builder.append(", event=");
        builder.append(event);
        builder.append(", time=");
        builder.append(time);
        builder.append("]");
        return builder.toString();
    }

}
