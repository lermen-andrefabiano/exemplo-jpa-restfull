package br.com.fwk.hibernate;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

/**
 * classe util para executar procedures do banco da MV
 * 
 * @author fabio.arezi
 *
 */
public class ConnectionWork {

	private Session session;

	public ConnectionWork(Session session) {
		this.session = session;
	}

	public ConnectionWork(Session session, String usuarioId, Long empresaId) {
		this.session = session;
		setEmpresa(empresaId).setUsuario(usuarioId);
	}

	public ConnectionWork(Session session, String usuarioId, Long empresaId, String sistema) {
		this.session = session;
		setUsuario(usuarioId);
		setEmpresa(empresaId);
		setSistema(sistema);
	}

	
	public ConnectionWork setSistema(String sistema) {
		if (sistema == null) {
			throw new IllegalArgumentException("sistema == null");
		}
		session.createSQLQuery("BEGIN dbms_application_info.set_module(module_name =>:sistema, action_name =>:sistema); END;").setParameter("sistema", sistema).executeUpdate();
		return this;
	}
	
	public ConnectionWork setUsuario(String usuarioId) {
		if (usuarioId == null) {
			throw new IllegalArgumentException("usuarioId == null");
		}
		session.createSQLQuery("{ call dbamv.PKG_MV_VARIAVEIS.prc_set_usuario( :usuarioId ) }").setParameter("usuarioId", usuarioId.toUpperCase()).executeUpdate();
		return this;
	}

	public ConnectionWork setEmpresa(Long empresaId) {
		if (empresaId == null) {
			throw new IllegalArgumentException("empresaId == null");
		}
		session.createSQLQuery("{ call dbamv.Pkg_Mv2000.Atribui_Empresa( :empresaId ) }").setParameter("empresaId", empresaId).executeUpdate();
		return this;
	}
	
	public void doWork(final Work work) {
		session.doWork(work);
	}
	
}
