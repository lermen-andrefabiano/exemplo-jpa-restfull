package br.com.init.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="modulo")
public class ModuloDTO {

	public Long id;

	public String descricao;

}