package com.jbh.secure.spring.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware {
	
	private static Environment environment;
	private static ApplicationContext ctxt;
	
	
	private static void setAppCtxt(ApplicationContext applicationContext) {
		BeanUtil.ctxt = applicationContext;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		BeanUtil.setAppCtxt(applicationContext);
	}
	public static ApplicationContext getAppContext() {
		return ctxt;
	}
	
	public static <T> T getBean(Class<T> requiredType)  throws BeansException{
		return getAppContext().getBean(requiredType);
	}
	
	public static Environment getEnvironment() {
		if(null==environment) environment = getAppContext().getBean(Environment.class);
		return environment;
	}
	public static String getProperty(String prop) {
		return getEnvironment().getProperty(prop);
	}
}
