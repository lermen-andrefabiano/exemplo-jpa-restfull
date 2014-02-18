package br.com.init.jaxrs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import br.com.admin.Modulo;
import br.com.admin.ModuloService;
import br.com.fwk.core.assembler.Assembler;
import br.com.fwk.spring.logger.Log;
import br.com.init.dto.ModuloDTO;

@Named
@Path("modulos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModulosResource{

	@Log
	private Logger log;
	
	private Assembler asm = new Assembler();

	private ModuloService moduloService;

	@Inject
	public ModulosResource(ModuloService moduloService) {
		this.moduloService = moduloService;

	}

	// - http://localhost:8888/InitJerseyJPA/rest/modulos?ativos=true
	@GET
	public List<ModuloDTO> listarModulos(@QueryParam("ativos") Boolean ativos) {
		
		log.debug("listarModulos");

		// Se nao vier o parametro, exibe todos (ativos e inativos)
		ativos = (ativos == null) ? false : ativos;

		List<Modulo> modulos = (ativos) ? this.moduloService.listarAtivos()	: this.moduloService.listar();
		
		List<ModuloDTO> retorno = new ArrayList<ModuloDTO>();
		
		retorno = asm.toDTO(modulos,  ModuloDTO.class);	

		return retorno;
	}

}