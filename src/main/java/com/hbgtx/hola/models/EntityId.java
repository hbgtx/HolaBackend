package com.hbgtx.hola.models;

public class EntityId {
    private final String id;

    public EntityId(String id) {
        this.id = id.toLowerCase();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
