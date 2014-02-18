package br.com.fwk.core.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
/**
 * utilitarios para leitura de arquivos do classpath da aplicação
 * 
 * @author fabio.arezi
 *
 */
public class ResourceReader {
	
	public static String text(String arquivo) throws IllegalArgumentException {
		return text(arquivo, null, null);
	}

	public static String text(String arquivo, ClassLoader classLoader) throws IllegalArgumentException {
		return text(arquivo, null, classLoader);
	}

	public static String text(String arquivo, String charset) throws IllegalArgumentException {
		return text(arquivo, charset, null);
	}

	public static String text(String arquivo, String charset, ClassLoader classLoader) throws IllegalArgumentException {
		try {
			if (classLoader == null) {
				classLoader = Thread.currentThread().getContextClassLoader();
			}
			
			InputStream in = classLoader.getResourceAsStream(arquivo);
			if (in == null) {
				throw new IllegalArgumentException("Arquivo '"+arquivo+"' NAO encontrado no classpath");
			}

			InputStreamReader isr = charset == null ? new InputStreamReader(in) : new InputStreamReader(in, charset) ;
			
			BufferedReader reader = new BufferedReader(isr);
			
			StringBuilder sb = new StringBuilder();
			String s = null;
			while((s = reader.readLine()) != null) { 
				sb.append(s);
			}
	
			return sb.toString();
		} catch(UnsupportedEncodingException e){
			throw new IllegalArgumentException("Codificação '"+charset+"' inválida");
		} catch (IOException e) {
			throw new IllegalArgumentException("erro ao ler o arquivo '"+arquivo+"'", e);
		}
	}

}
