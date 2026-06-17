package com.starmol.sourcecodereview.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtils {

    private static ApplicationContext appContext;

    public static <T> T getInstanceByType(Class<T> cls) {
        return appContext.getBean(cls);
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        appContext = context;
    }
}
