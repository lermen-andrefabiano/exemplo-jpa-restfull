package br.com.fwk.jpa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly=true)
public abstract class AbstractCrudJPA<E, ID extends Serializable> {
	
	@PersistenceContext
	protected EntityManager em;
	
	private Class<E> entityClass; 

	@SuppressWarnings("unchecked")
	public AbstractCrudJPA() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[0];		
	}

	public E obterPorId(ID id) {
		return (E) em.find(entityClass, id);
	}


	public <T> T obterPorId(Serializable id, Class<T> clazz) {
		return (T) em.find(clazz, id);
	}

	
	@Transactional
	public E salvar(E entity) {
		boolean novo = true;
		try {
			Field f = entityClass.getDeclaredField("id");
			f.setAccessible(true);
			novo = f.get(entity) == null;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Campo 'id' nao encontrado na entidade. "+entityClass.getSimpleName(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Campo 'id' da entidade "+entityClass.getSimpleName()+" nao pode ser lido! ", e);
		}
		
		if ( novo ) {
			em.persist(entity);
		} else {
			entity = em.merge(entity);
		}
		
		return entity;
	}	
	
//	@Transactional
//	public E salvar(E entity) {
//		return em.merge(entity);
//	}

	
	@Transactional
    public List<E> salvar(Collection<? extends E> entities) {
        List<E> result = new ArrayList<E>();
        if (entities == null) {
            return result;
        }
        for (E entity : entities) {
            result.add(salvar(entity));
        }
        return result;
    }
	
	

	/**
	 * cria uma Query baseada em um arquivo 
	 * que deve existir no mesmo pacote da classe
	 * 
	 * @param file nome do arquivo
	 * @return query 
	 */
	protected <T> TypedQuery<T> loadQuery(String file, Class<T> tipo) {
		String sq = loadFile(file);
		return em.createQuery(sq, tipo); 
	}

	
	/**
	 * carrega o conteudo de um arquivo que deve existir no mesmo pacote da classe
	 * 
	 * @param file nome do arquivo
	 * @return conteudo do arquivo em String
	 */
	protected String loadFile(String arquivo) throws IllegalArgumentException {

		String dirpack = this.getClass().getPackage().getName().replace('.', '/');
		arquivo = dirpack+"/"+arquivo;

		try {
			
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(arquivo);

			if (in == null) {
				throw new IllegalArgumentException("Arquivo '"+arquivo+"' NAO encontrado no classpath");
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			StringBuilder sb = new StringBuilder();
			String s = null;
			while((s = reader.readLine()) != null) { 
				sb.append(s+" ");
			}
	
			return sb.toString();
		} catch (IOException e) {
			throw new IllegalArgumentException("erro ao ler o arquivo '"+arquivo+"'", e);
		}
	}
		
	
	public int qtdeRegistros() {
        return ((Long) em.createQuery("select count(o) from "+entityClass.getSimpleName()+" as o").getSingleResult()).intValue();
    }
	

	
	@Transactional
	public void excluir(E entity) {
		em.remove( em.contains(entity)? entity : em.merge(entity));		
	}
	

	public List<E> listar(){
		TypedQuery<E> q = null;
		
		try {
			q = em.createNamedQuery(entityClass.getSimpleName()+".listar", entityClass);
		} catch (IllegalArgumentException e) { // query nao encontrada
			q = em.createQuery("from "+entityClass.getSimpleName()+" e", entityClass);
		}
		
		return q.getResultList();
	}


	
	protected List<E> listarPor(String atributo, Object valor){
		Map<String, Object> valores = new HashMap<String, Object>();
		valores.put(atributo, valor);
		return this.listarPor(valores);
	}


	
	protected List<E> listarPor(Map<String, Object> valores){
		//
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<E> criteria = cb.createQuery(entityClass);
		Root<E> root = criteria.from(entityClass);
		
		Predicate[] predicates = new Predicate[valores.size()];
		
		int i = 0;
		for(String atributo : valores.keySet()) {
			predicates[i++] = cb.equal(root.get( atributo ), valores.get(atributo));
		}
		
		criteria.select(root).where(cb.and( predicates ));
		return em.createQuery(criteria).getResultList(); 
	}
	
	
	protected void desanexar(Collection<?> lst) {
		for (Object obj : lst) {
			em.detach(obj);
		}
	}

	protected void fetch(Collection<?> lst) {
		if (lst != null) {
			lst.size();
		}
	}
	
	protected void fetch(Object obj) {
		if (obj == null) {
			return;
		}
		try {
			Method m = obj.getClass().getDeclaredMethod("getId");
			m.invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
