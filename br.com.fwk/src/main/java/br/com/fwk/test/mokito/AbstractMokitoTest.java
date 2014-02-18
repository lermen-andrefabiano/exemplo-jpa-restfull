package br.com.fwk.test.mokito;


import org.junit.Before;
import org.mockito.MockitoAnnotations;

import br.com.fwk.test.all.AbstractTest;

public abstract class AbstractMokitoTest extends AbstractTest {

	@Before
	public void setUpToAll() {
		MockitoAnnotations.initMocks(this);
	}

}
