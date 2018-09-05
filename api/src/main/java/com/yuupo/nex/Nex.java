package com.yuupo.nex;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class Nex {
    private ServiceManager serviceManager;
    private Context context;
    private static String INJECT_CLASS;
    private IInject inject;

    private static class NexStatic {
        private static Nex nex = new Nex();
    }

    private Nex() {
        serviceManager = new ServiceManager();
    }

    public static Nex getInstance() {
        return NexStatic.nex;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        loadComponent();
    }

    private void loadComponent() {
        INJECT_CLASS = context.getApplicationContext().getPackageName() + ".nex.Inject";
        try {
            Class<IInject> injectClz = (Class<IInject>) Class.forName(INJECT_CLASS);
            IInject inject = injectClz.newInstance();
            inject.initComponent();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private Target target;

    public Nex build(String path) {
        target = new Target(path);
        return this;
    }

    public Object call(String path) {
        Service service;
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        service = serviceManager.findService(path);
        if (service == null) {
            return null;
        }
        return executeService(service);
    }

    private Object executeService(Service service) {
        switch (service.getComponentType()) {
            case ACTIVITY:
                handActivityService(service);
                break;
            case SERVICE:
                break;
            case CONTENT_PROVIDER:
                break;
            case FRAGMENT:
            case FRAGMENTV4:
                return handFragment(service);
            case PROVIDER:
                break;
            case UNKNOWN:
            default:
                throw new IllegalArgumentException("service Type Unknown");
        }
        return null;
    }

    private void handActivityService(Service service) {
        Intent intent = new Intent(context, service.getComponentClz());
        context.startActivity(intent);
    }

    private static Object handFragment(Service service) {
        Class<?> fragmentClz = service.getComponentClz();
        try {
            Object instance = fragmentClz.newInstance();

            return instance;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
