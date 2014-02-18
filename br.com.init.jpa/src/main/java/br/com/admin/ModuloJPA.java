package br.com.admin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.fwk.core.file.ResourceReader;

@Named
public class ModuloJPA implements ModuloRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Modulo> listar(Boolean apenasAtivos) {

	    String q = apenasAtivos ? "Modulo.listarAtivos" : "Modulo.listar";	    
		Query query = em.createNamedQuery(q);
		
		@SuppressWarnings("unchecked")
		List<Modulo> result = query.getResultList();
		return result;
	}
    
    @Override
	public Modulo obterPorId(Long id) {
		return em.find(Modulo.class, id);
	}
    
    //@Override
	public List<Modulo> listarModulosPorUsuario(String usuarioId) {
		String sqlModulos = ResourceReader.text("sql/workspace/listar-modulos-usuariopapel.sql");
		Query qm = em.createNativeQuery(sqlModulos).setParameter("usuarioId", usuarioId);
		
		@SuppressWarnings("unchecked")
		List<Object[]> modulos = qm.getResultList();
		
		List<Modulo> retorno = new ArrayList<Modulo>();

//		for(Object[] o: modulos){
//			retorno.add(
//				new Modulo(
//					(String) o[1],
//					TipoModulo.valueOf( (String)o[3] ),
//					(o[2] != null) ? ((BigDecimal) o[2]).longValue() : null,//PortletId
//					null //Ordem
//				)
//			);
//		}
		return retorno;
	}
    
    
}