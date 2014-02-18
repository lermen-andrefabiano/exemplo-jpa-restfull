package br.com.fwk.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MvConfig {

	private static String SP = File.separator;
	
	
	public static Properties getProperties(String application, String fileProperties) throws IOException {
		
		Properties pp = new Properties();
		
		File file = new File(getMvHomeDir()+SP+"apps"+SP+application+SP+"conf"+SP+fileProperties );
		if (! file.exists() ) {
			throw new FileNotFoundException("Arquivo '"+file.getAbsolutePath()+"' não encontrado!");
		}
		
		pp.load(new FileReader(file));
		
		return pp;
	}
	 
    /**
     * obtem o diretorio do MV_HOME (se nao informado, pega o padrão)
     * 
     * @author fabio.arezi
     * 
     * @return diretorio padrao
     */
    public static String getMvHomeDir() {

        String home = System.getenv("MV_HOME");

        if (home == null || home.equals("")) {
            home = getMvHomeDefault();
        } 
        
        return home;
    }

    /**
     * obtem o diretorio padrao de acordo com o sistema operacional
     * 
     * @author fabio.arezi
     * 
     * @return diretorio padrao
     */
    public static String getMvHomeDefault() {
    	String base = "C:";
    	if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
    		base = "~";
    	}
		return base+SP+"MV";
	}
}
