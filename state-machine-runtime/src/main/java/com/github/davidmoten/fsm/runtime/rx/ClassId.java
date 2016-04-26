package com.github.davidmoten.fsm.runtime.rx;

final class ClassId<Id> {
    final Class<?> cls;
    final Id id;

    ClassId(Class<?> cls, Id id) {
        this.cls = cls;
        this.id = id;
    }

    public Class<?> cls() {
        return cls;
    }

    public Id id() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cls == null) ? 0 : cls.getCanonicalName().hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClassId<?> other = (ClassId<?>) obj;
        if (cls.getCanonicalName() == null) {
            if (other.cls.getCanonicalName() != null)
                return false;
        } else if (!cls.getCanonicalName().equals(other.cls.getCanonicalName()))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
