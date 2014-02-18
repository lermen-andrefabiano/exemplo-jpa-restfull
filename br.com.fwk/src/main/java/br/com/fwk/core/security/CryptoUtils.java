package br.com.fwk.core.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


/**
 * Utility class that encrypts/decrypts values using a default key.
 * 
 * @author Marcelo Cyreno
 */
/**
 * @author Marcelo.Cyreno
 * 
 */
public class CryptoUtils {

	private static final String AES_CIPHER = "AES";

	private static final byte[] MV_SYSTEM_PROPERTIES_KEY = { (byte) 0xc8,
			(byte) 0x85, (byte) 0x86, (byte) 0x35, (byte) 0xa2, (byte) 0x65,
			(byte) 0x28, (byte) 0x39, (byte) 0xb6, (byte) 0x6b, (byte) 0xd9,
			(byte) 0x9e, (byte) 0xe9, (byte) 0x5d, (byte) 0x5f, (byte) 0xa5 };

	private static final SecretKeySpec skeySpec = new SecretKeySpec(
			MV_SYSTEM_PROPERTIES_KEY, AES_CIPHER);

	private static final String ERROR_INCORRECT_NUMBER_OF_ARGS = "Incorrect number of arguments. This application expects only one argument: the complete path of a property file or a complete path of a directory.";

	/** TODO colocar isso no projeto de testes
	 * Encrypts a file or a group of files according to argument passed.
	 * 
	 * @param args
	 *            The filename of the property file to be encrypted or a
	 *            filename of a directory containing properties files (This
	 *            application will search recursively every directory for
	 *            properties files).
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println(ERROR_INCORRECT_NUMBER_OF_ARGS);
			return;
		}

		try {
			encryptFile(new File(args[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static byte[] encrypt(byte[] data) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance(AES_CIPHER);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(data);

		return encrypted;
	}

	public static byte[] decrypt(String hexEncValue)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {

		if (hexEncValue == null || hexEncValue.length() == 0) {
			return null;
		}

		byte[] encValue = new byte[hexEncValue.length() / 2];
		for (int i = 0; i < encValue.length * 2; i += 2) {
			String s = hexEncValue.substring(i, i + 2);
			encValue[i / 2] = (byte) (Integer.parseInt(s, 16) & 0xff);
		}

		return decrypt(encValue);
	}

	public static byte[] decrypt(byte[] encrypted)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance(AES_CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] original = cipher.doFinal(encrypted);

		return original;
	}

	public static String asHex(byte buf[]) {

		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		for (int i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	public static void encryptFile(File root) throws IOException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {

		if (!root.exists()) {
			throw new IllegalArgumentException(
					"The specified file doesn´t exists!");
		}

		File[] files = root.listFiles();
		for (File file : files) {

			if (file.isDirectory()) {
				encryptFile(file);
				continue;
			} else if (!file.getAbsolutePath().toLowerCase().endsWith(
					".properties")) {
				continue;
			}

			AESProperties properties = new AESProperties();
			InputStream inputStream = new FileInputStream(file);
			properties.load(inputStream);
			properties.setEncrypted(true);

			OutputStream outputStream = new FileOutputStream(file);
			properties.store(outputStream, "");
			outputStream.flush();
			outputStream.close();
		}

	}


}
