package com.yuupo.nex;

import android.util.Log;

public class Service {
    private static final String TAG = "Service";
    private String path;
    private ComponentType componentType;
    private Class<?> componentClz;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public Class<?> getComponentClz() {
        return componentClz;
    }

    public void setComponentClz(Class<?> componentClz) {
        this.componentClz = componentClz;
    }

    private Service() {

    }




    public static Service build(String path, Class<?> componentClz) {
        Service service = new Service();
        service.componentClz = componentClz;
        service.path = path;
        service.componentType = findServiceType(componentClz);
        return service;
    }

    public static ComponentType findServiceType(Class<?> clz) {
        Class<?> superclass = clz.getSuperclass();

        ComponentType serviceType = ComponentType.UNKNOWN;
        while (serviceType == ComponentType.UNKNOWN) {
            if (superclass.getName().equals("java.lang.Object")) {
                return ComponentType.UNKNOWN;
            }
            Log.d(TAG, "findServiceType: superclass.getName()=>" + superclass.getName());
            serviceType = ComponentType.parse(superclass.getName());
            superclass = superclass.getSuperclass();
        }
        return serviceType;
    }
}
