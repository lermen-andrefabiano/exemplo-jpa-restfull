package br.com.fwk.core.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * classe usada pra ler arquivos de propriedades localizados no classpath da aplicação 
 * 
 * @author fabio.arezi
 *
 */
public class ResourceProperties extends Properties {

	private static final long serialVersionUID = 1L;

	/**
	 * carrega o arquivo de propriedades
	 * 
	 * @param file arquivo localizado no classpath
	 * @throws IllegalArgumentException caso não encontre o arquivo
	 */
	public ResourceProperties(String file) {
		super();
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);

		if (is == null) {
			throw new IllegalArgumentException("Arquivo '"+file+"' não encontrada no classpath.");
		}
		
		try {
			load(is);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
