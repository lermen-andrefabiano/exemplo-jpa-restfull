package br.com.fwk.spring.logger;


import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * intetor do org.slf4j.Logger no bean quando usado a anoação @mv.fwk.spring.logger.Log
 * ex: @Log Logger log;
 * 
 * @author fabio.arezi
 *
 */
public class LoggerInjector implements BeanPostProcessor {  

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {  
		return bean;  
	}  

	public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {  
		ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {  
			public void doWith(Field field) throws IllegalArgumentException,  
			IllegalAccessException {  
				ReflectionUtils.makeAccessible(field); // tornar o campo acessivel se estiver como private  
				if (field.getAnnotation(Log.class) != null) {  
					Logger log = LoggerFactory.getLogger(bean.getClass());
					field.set(bean, log);  
				}  
			}  
		});  
		return bean;  
	}  
}  