package br.com.admin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


@Named
public class ModuloServiceImpl implements ModuloService {

	private ModuloRepository moduloRep;

	@Inject
	public ModuloServiceImpl(ModuloRepository moduloRep) {
		this.moduloRep = moduloRep;
	}

	@Override
	public List<Modulo> listar() {
		return this.moduloRep.listar(false); // lista todos
	}
	
	@Override
    public List<Modulo> listarAtivos() {
    	return this.moduloRep.listar(true); // lista apenas ativos
    }
  
	@Override
	public Modulo obterPorId(Long id) {		
		return this.moduloRep.obterPorId(id);
	}


}
