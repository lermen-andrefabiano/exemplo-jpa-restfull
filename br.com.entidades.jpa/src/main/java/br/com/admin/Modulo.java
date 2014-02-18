package br.com.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GSTMOB_MODULO", schema = "dbacp")
public class Modulo implements Serializable {

	private static final long serialVersionUID = -3424647307310577725L;

	@Id
	@Column(name = "CD_MODULO_GESTOR")
	private Long id;

	@Column(name = "DS_MODULO_GESTOR")
	private String descricao;

	@Column(name = "CD_PORTLET")
	private Long portletId;

	@Column(name = "NR_ORDEM")
	private Integer nrOrdem;

	
	@Column(name ="SN_ATIVO")
	private String ativo;
	
	// --
	public Modulo() {
	}

	// --

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getPortletId() {
		return portletId;
	}

	public void setPortletId(Long portletId) {
		this.portletId = portletId;
	}

	public Integer getNrOrdem() {
		return nrOrdem;
	}

	public void setNrOrdem(Integer nrOrdem) {
		this.nrOrdem = nrOrdem;
	}
	
    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

}