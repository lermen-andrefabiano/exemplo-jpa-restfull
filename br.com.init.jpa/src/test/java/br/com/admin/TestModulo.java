package br.com.admin;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import br.com.fwk.test.spring.AbstractSpringTest;

public class TestModulo extends AbstractSpringTest {

	@Inject
	ModuloRepository moduloRep;	

	
	@Test
	public void listarModulos() {
		List<Modulo> modulos = moduloRep.listar(false);
		List<Modulo> modulosAtivos = moduloRep.listar(true);

		if (modulosAtivos.isEmpty()) {
			log.debug("não existem modulos ativos!");
		} else {
			log.debug("modulos ativos: ");
			mostrarModulos(modulosAtivos);
		}
		modulos.removeAll(modulosAtivos);
		
		log.debug("demais modulos: ");
		mostrarModulos(modulos);
	}

	private void mostrarModulos(List<Modulo> modulos) {
		for (Modulo m : modulos)
			log.debug(m.getId() + " - " + m.getDescricao());
	}
}