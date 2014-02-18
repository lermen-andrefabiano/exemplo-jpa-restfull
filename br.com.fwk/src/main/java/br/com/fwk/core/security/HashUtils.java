package br.com.fwk.core.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * utilitarios para geração de hash
 * 
 * @author fabio.arezi
 * @since 14/11/2011
 *
 */
public class HashUtils {

	/**
	 * gera um hash de 8 bytes (32 caracteres em hexadecimal) de uma string usando o algoritmo MD5
	 * 
	 * @author fabio.arezi
	 * @since 14/11/2011
	 * @param str string a ser gerada
	 * @return hash gerado em hexadecimal
	 */
	public static String md5(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(str.getBytes());
			BigInteger lHashInt = new BigInteger(1, digest.digest());
			return String.format("%1$032X", lHashInt);
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * gera um hash de uma string usando o algoritmo SHA-1
	 * 
	 * @author fabio.arezi
	 * @since 14/11/2011
	 * @param str string a ser gerada
	 * @return hash gerado em hexadecimal
	 */
	public static String sha1(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(str.getBytes());
			BigInteger lHashInt = new BigInteger(1, digest.digest());
			return String.format("%1$032X", lHashInt);
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
}
