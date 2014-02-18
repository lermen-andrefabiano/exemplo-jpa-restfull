package br.com.admin;

import java.util.List;

interface ModuloRepository {

	/**
	 * Lista todos os modulos do banco
	 * 
	 * @author andre.lermen
	 * @since 09/05/2012
	 * 
	 * @param apenasAtivos
	 *            - true apenas os ativos / false todos os modulos
	 * @return List<Modulo>
	 */
	public List<Modulo> listar(Boolean apenasAtivos);

	public Modulo obterPorId(Long id);

}
