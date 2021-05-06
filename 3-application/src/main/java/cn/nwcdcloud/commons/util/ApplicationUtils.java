package cn.nwcdcloud.commons.util;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

public class ApplicationUtils {
    private static ApplicationContext ac = ContextLoader.getCurrentWebApplicationContext();

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        try {
            return (T) ac.getBean(name);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> requiredType) {
        try {
            return (T) ac.getBean(requiredType);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setAc(ApplicationContext ac) {
        ApplicationUtils.ac = ac;
    }
}
