package br.com.web;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.admin.Modulo;
import br.com.admin.ModuloService;

//@RequestScoped
//@ManagedBean
@Named
public class ModuloBean {
	
	private ModuloService moduloService;
	
	private List<Modulo> modulos;
	
	public ModuloBean(){
	}	
	
	@Inject
	public ModuloBean(ModuloService moduloService){
		this.moduloService = moduloService;		
	}	

	@PostConstruct
	private void init(){
		modulos = this.moduloService.listarAtivos();
	}

	public List<Modulo> getModulos() {
		return modulos;
	}

	public void setModulos(List<Modulo> modulos) {
		this.modulos = modulos;
	}


}
