package br.com.fwk.core.assembler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * anotação usada nos atributos do dto para indicar nome do atributo do bean
 * 
 * @author fabio.arezi
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldBean {
	
	/**
	 * nome do atributo do bean
	 * @return
	 */
	String value();
}
