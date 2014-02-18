package br.com.fwk.spring.logger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * usado loelo LoggerInjector
 * 
 * @author fabio.arezi
 *
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
@Documented  
public @interface Log {
	
}  