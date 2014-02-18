package br.com.admin;

import java.util.List;

public interface ModuloService {

	/**
	 * Lista todos os modulos do banco
	 * 
	 * @author andre.lermen
	 * @since 09/05/2012
	 * 
	 * @return List<Modulo>
	 */
	public List<Modulo> listar();

	/**
	 * Lista todos modulos ativos no banco
	 * */
	public List<Modulo> listarAtivos();

	public Modulo obterPorId(Long id);

}
