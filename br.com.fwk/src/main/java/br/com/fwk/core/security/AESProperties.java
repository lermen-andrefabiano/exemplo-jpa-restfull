package br.com.fwk.core.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;


/**
 * Class that extends {@link Properties} adding support for encrypted files
 * (values encrypted/decrypted through {@link CryptoUtils}).
 * 
 * @author Marcelo Cyreno
 */
public class AESProperties extends Properties {

	private static final long serialVersionUID = -5658075430930953271L;

	public static final String MV_MARK = "com_mv_encrypted_file";

	private boolean encrypted = false;

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	private void decryptValues() {

		if (getProperty(MV_MARK) == null) {
			encrypted = false;
			return;
		}

		encrypted = true;
		remove(MV_MARK);

		Enumeration<Object> keys = keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = getProperty(key, "");

			if (value.length() > 0) {
				try {
					value = new String(CryptoUtils.decrypt(value));
					setProperty(key, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	private Properties copy() {

		Properties copy = new Properties();
		Enumeration<Object> keys = keys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = getProperty(key);
			copy.setProperty(key, value);
		}

		return copy;
	}

	private void encryptValues(Properties properties) {

		if (!encrypted) {
			return;
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		properties.setProperty(MV_MARK, format.format(System
				.currentTimeMillis()));

		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = properties.getProperty(key, "");

			if (value.length() > 0) {
				byte[] buffer;
				try {
					buffer = CryptoUtils.encrypt(value.getBytes());
					value = CryptoUtils.asHex(buffer);
					properties.setProperty(key, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public synchronized void load(InputStream inStream) throws IOException {
		super.load(inStream);
		decryptValues();
	}

	@Override
	public synchronized void load(Reader reader) throws IOException {
		super.load(reader);
		decryptValues();
	}

	@Override
	public synchronized void loadFromXML(InputStream in) throws IOException,
			InvalidPropertiesFormatException {
		super.loadFromXML(in);
		decryptValues();
	}

	@Override
	public void store(OutputStream out, String comments) throws IOException {
		Properties p = copy();
		encryptValues(p);
		p.store(out, comments);
	}

	@Override
	public void store(Writer writer, String comments) throws IOException {
		Properties p = copy();
		encryptValues(p);
		p.store(writer, comments);
	}

	@Override
	public synchronized void storeToXML(OutputStream os, String comment)
			throws IOException {
		Properties p = copy();
		encryptValues(p);
		p.storeToXML(os, comment);
	}

	@Override
	public synchronized void storeToXML(OutputStream os, String comment,
			String encoding) throws IOException {
		Properties p = copy();
		encryptValues(p);
		p.storeToXML(os, comment, encoding);
	}

}
