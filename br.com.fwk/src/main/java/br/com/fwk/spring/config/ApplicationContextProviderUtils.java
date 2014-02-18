package br.com.fwk.spring.config;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * Utilidades relacionadas ao container do spring
 * 
 */
class ApplicationContextProviderUtils {
	
	public static final String DB_PROPERTIES = "dbProperties";    
    public static final String DB_CONNECTION_POOL_PROPERTIES = "dBConnectionPool";
    public static final String GENERAL_PROPERTIES = "generalProperties";
    
    private static Logger logger = LogManager.getLogger(ApplicationContextProvider.class);    

    /**
     * Application context
     */
    private static ApplicationContext applicationContext;
    
    private static String confFolderName;

    private static Properties dbProperties;

    private static Properties dBconnectionPool;

    private static Properties generalProperties;
	
    /**
     * @return the properties
     */
    public static Properties getProperties(String resourceName) {

        if(resourceName.equalsIgnoreCase(DB_PROPERTIES)){
            return dbProperties;
        
        }else if(resourceName.equalsIgnoreCase(DB_CONNECTION_POOL_PROPERTIES)){
            return dBconnectionPool;
            
        }else{
            return generalProperties;
        }
    }
    
    /**
     * Carrega as configurações de properties conforme nome passado por parametro.
     * 1. Busca o arquivo na pasta de configuração específica da aplicação
     * (%MV_HOME%/apps/[configurationFolderName]/conf/documento-certificado.properties)
     * 
     * @author: joao.cordenunzi
     * @since: 24/08/2010
     * 
     * @param propertieName
     * @return
     */    
    public static Properties getPropertiesByName(String resourceName){ 
        Properties propertie = new Properties();

        File confFile = new File(ApplicationContextProvider.getPropertiesPath(resourceName));

        try {
        	propertie.load(new FileInputStream(confFile));
        	
        	logger.info("-->> Arquivo de Propertie: " + resourceName + ", configurado a partir do arquivo "
        			+ confFile + ".");

        	if (propertie.isEmpty()){
        		throw new FileNotFoundException();
        	}

        } catch (Exception e) {
            logger
                    .error("-->> (ERROR) Nenhuma configuração de "+ confFile.getAbsolutePath()+" foi encontrada. Configure o arquivo " + resourceName);
        }

        return propertie;    	
    }

    public static String getConfFolderName() {
        return confFolderName;
    }

    public static void setConfFolderName(String confFolderName) {
        ApplicationContextProviderUtils.confFolderName = confFolderName;
    }

    /**
     * @return container
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * @param applicationContext
     *            container
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextProviderUtils.applicationContext = applicationContext;
    }

    /**
     * @return the dbProperties
     */
    public static Properties getDbProperties() {
        return dbProperties;
    }

    /**
     * @param dbProperties the dbProperties to set
     */
    public static void setDbProperties(Properties dbProperties) {
        ApplicationContextProviderUtils.dbProperties = dbProperties;
    }

    /**
     * @return the dBConnectionPool
     */
    public static Properties getDBconnectionPool() {
        return dBconnectionPool;
    }

    /**
     * @param properties the dBConnectionPool to set
     */
    public static void setDBconnectionPool(Properties properties) {
    	dBconnectionPool = properties;
    }

    /**
     * @return the generalProperties
     */
    public static Properties getGeneralProperties() {
        return generalProperties;
    }

    /**
     * @param generalProperties the generalProperties to set
     */
    public static void setGeneralProperties(Properties generalProperties) {
        ApplicationContextProviderUtils.generalProperties = generalProperties;
    }
}
