package br.com.fwk.core.assembler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * usado no atributo DTO, indica que o mesmo não será feito o parser pelo assembler 
 * @author fabio.arezi
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldTransient {
}
