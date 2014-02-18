package br.com.fwk.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.MappingException;
import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * classe utilitaria para operações de CRUD de uma entidade específica
 * 
 * @author fabio.arezi
 *
 * @param <E> entidade
 * @param <ID> chave primaria
 */
@Transactional
public abstract class AbstractCrudHibernate<E, ID extends Serializable> extends AbstractHibernate {
	
	private Class<E> entityClass; 

	@SuppressWarnings("unchecked")
	public AbstractCrudHibernate() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[0];
	}

	/**
	 * 
	 * obtem o objeto do banco de acordo com a chave primária
	 * 
	 * se objeto não for encontrado, retornará null
	 *
	 * @author fabio.arezi
	 * 
	 * @param id chave primária
	 * @return objeto do banco
	 */
	@SuppressWarnings("unchecked")
	public E obterPorId(ID id) {
		return (E)getSession().get(entityClass, id);
	}

	/**
	 * salva a entidade (fazendo o merge)
	 * se o id não for encontrado na tabela, será feito o insert, senão update
	 * 
	 * @author fabio.arezi
	 * 
	 * @param entity 
	 * @return retorna o objeto entidade salvo, no contexto de persistencia
	 */
	@SuppressWarnings("unchecked")
	public E salvar(E entity) {
		return (E)getSession().merge(entity);
	}

	/**
	 * exclui a entidade (não é necessario estar no contexto de persistencia)
	 * bastando ter o valor do ID
	 * @param entity
	 */
	public void excluir(E entity) {
		getSession().delete( entity );		
	}
	
	
	/**
	 * lista as entidades referente a tabela
	 * 
	 * para a listagem, será procurado 
	 * uma named query no padrão: <Entidade>.listar  
	 * se for encontrada a named query, a mesma será executada,
	 * senão será executado o hql: from <Entidade> e
	 * 
	 * @author fabio.arezi
	 * 
	 * @return lista de entidades
	 */
	public List<E> listar(){
		
		Query q;
		try {
			q = getSession().getNamedQuery(entityClass.getSimpleName()+".listar");		
		} catch (MappingException e) { // query nao encontrada
			q = getSession().createQuery("from "+entityClass.getSimpleName()+" e");
		}

		@SuppressWarnings("unchecked")
		List<E> lst = q.list();
		
		return lst;
	}
	


	/**
	 * obtem a quantidade de registros na tabela
	 * 
	 * @author fabio.arezi
	 * 
	 * @return nro de registros 
	 */
	public long qtdeRegistros() {
        return (Long)getSession().createQuery("select count(o) from "+entityClass.getSimpleName()+" as o").uniqueResult();
    }
	
//
//	
//	protected List<E> listarPor(String atributo, Object valor){
//		Map<String, Object> valores = new HashMap<String, Object>();
//		valores.put(atributo, valor);
//		return this.listarPor(valores);
//	}
//
//
//	
//	protected List<E> listarPor(Map<String, Object> valores){
//		//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<E> criteria = cb.createQuery(entityClass);
//		Root<E> root = criteria.from(entityClass);
//		
//		Predicate[] predicates = new Predicate[valores.size()];
//		
//		int i = 0;
//		for(String atributo : valores.keySet()) {
//			predicates[i++] = cb.equal(root.get( atributo ), valores.get(atributo));
//		}
//		
//		criteria.select(root).where(cb.and( predicates ));
//		return em.createQuery(criteria).getResultList(); 
//	}
//	
//	
//	protected void desanexar(Collection<?> lst) {
//		for (Object obj : lst) {
//			em.detach(obj);
//		}
//	}
//

	
	
}
