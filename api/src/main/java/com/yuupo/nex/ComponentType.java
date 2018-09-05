package com.yuupo.nex;

public enum ComponentType {
    ACTIVITY(0, "android.app.Activity"),
    SERVICE(1, "android.app.Service"),
    CONTENT_PROVIDER(2, "android.app.ContentProvider"),
    FRAGMENT(3, "android.app.Fragment"),
    FRAGMENTV4(4, "android.support.v4.app.Fragment"),
    PROVIDER(5, "com.beiyan.aop.IProvider"),
    UNKNOWN(-1, "Unknown route type");

    int id;
    String className;

    public int getId() {
        return id;
    }

    public ComponentType setId(int id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public ComponentType setClassName(String className) {
        this.className = className;
        return this;
    }

    ComponentType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public static ComponentType parse(String name) {
        for (ComponentType componentType : ComponentType.values()) {
            if (componentType.getClassName().equals(name)) {
                return componentType;
            }
        }

        return UNKNOWN;
    }
}
