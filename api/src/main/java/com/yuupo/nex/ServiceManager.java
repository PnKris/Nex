package com.yuupo.nex;

import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {
    private static final String TAG = "ServiceManager";
    private Map<String, Service> serviceMap = new ConcurrentHashMap<>();

    public synchronized void registerService(String path, Service service) {
        if (serviceMap.containsKey(path)) {
            Log.w(TAG, "register: path " + path + " has existed");
            return;
        }

        if (service == null || service.getComponentType() == ComponentType.UNKNOWN) {
            Log.e(TAG, "register: unknown service");
            return;
        }

        serviceMap.put(path, service);
    }

    public synchronized void unRegisterService(String path) {
        if (serviceMap.containsKey(path)) {
            serviceMap.remove(path);
        }
    }

    public synchronized void unRegisterService(Service service) {
        if (serviceMap.containsValue(service)) {
            serviceMap.remove(service);
        }
    }

    public Service findService(String path) {
        return serviceMap.get(path);
    }


}
