package cn.nwcdcloud.samples.ocr.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import cn.nwcdcloud.commons.util.ApplicationUtils;

@Configuration
public class ApplicationConfig implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        ApplicationUtils.setAc(ac);
    }
}
