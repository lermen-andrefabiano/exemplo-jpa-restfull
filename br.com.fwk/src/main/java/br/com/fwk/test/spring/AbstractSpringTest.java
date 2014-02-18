package br.com.fwk.test.spring;


import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import br.com.fwk.test.all.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-tests.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public abstract class AbstractSpringTest extends AbstractTest {

	
	protected void semResultado() {
		semResultado(null, null);
	}

	protected void semResultado(String metodo) {
		semResultado(metodo, null);
	}

	protected void semResultado(String metodo, String pos) {
		if (metodo == null) {
			metodo = "";
		}
		if (pos == null) {
			pos = "";
		}
		log.warn("Sem resultado para testes em "+this.getClass().getSimpleName()+"#"+metodo+". "+pos);		
	}

}
