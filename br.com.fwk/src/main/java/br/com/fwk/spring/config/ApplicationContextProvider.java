package br.com.fwk.spring.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.fwk.core.MvConfig;
import br.com.fwk.core.security.AESProperties;


public class ApplicationContextProvider implements ApplicationContextAware {

    private static Logger logger = LogManager.getLogger(ApplicationContextProvider.class);

    private static String MV_HOME;

    private static String sep = File.separator;

    // Nome das pastas
    private static final String CONF_FOLDER = "conf";
    private static final String APPS_FOLDER = "apps";

    // Nomes dos arquivos
    private final String APPS_XML = "apps.xml";

    private final String DB_PROPERTIES = "db.properties";
    
    private final String LOG4J_PROPERTIES = "log4j.properties";

    private final String LOG4J_XML = "log4j.xml";
    
    private final String DB_CONNECTION_POOL_PROPERTIES = "dBConnectionPool.properties";

    private final String GENERAL_PROPERTIES = "general.properties";

    private final String TRANSACTIONAL_PROPERTIES = "transactionConfiguration.properties";
    
    /*
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        ApplicationContextProviderUtils.setApplicationContext(ctx);

        MV_HOME = System.getenv("MV_HOME");

        if (MV_HOME == null || MV_HOME.equals("")) {

            MV_HOME = MvConfig.getMvHomeDefault();

            logger.warn("-->> Variável de ambiente MV_HOME não foi definida. Usando a DEFAULT '"+MV_HOME+"' ");

        } else {
            logger.info("-->> Usando para MV_HOME o endereço " + MV_HOME);
        }

        String applicationRealPath = getApplicationURL(ctx);
        Document doc = getMappingDocument();

        String configurationFolderName = getConfigurationFolderName(doc, applicationRealPath);

        loadProperties(configurationFolderName, ctx);

        ApplicationContextProviderUtils.setConfFolderName(configurationFolderName);
    }

	/**
     * Retorna o nome da pasta de configuração do sistema apartir do caminho
     * 
     * @author: rafael.oliveira
     * @since: 08/06/2010
     * 
     * @param doc
     * @param applicationRealPath
     * @return
     */
    private String getConfigurationFolderName(Document doc, String applicationRealPath) {
        String configurationFolderName = "";
        // Obtém o nome da pasta que contém os arquivos de configuração da
        // aplicação
        Element root = doc.getDocumentElement();
        NodeList configurations = root.getElementsByTagName("configuration");

        for (int i = 0; i < configurations.getLength(); i++) {
            Element configuration = (Element) configurations.item(i);
            NodeList applications = configuration
                    .getElementsByTagName("application");
            for (int j = 0; j < applications.getLength(); j++) {
                Element application = (Element) applications.item(j);
                if (applicationRealPath.equals(application.getAttribute("dir"))) {
                    configurationFolderName = configuration
                            .getAttribute("name");
                    break;
                }
            }
            if (!configurationFolderName.equals(""))
                break;
        }

        if (configurationFolderName == "") {
            logger.error("-->> (ERROR) Nenhuma configuração foi encontrada para esse sistema! Verifique se a propriedade DIR deste sistema no arquivo "
                            + MV_HOME + sep + CONF_FOLDER + sep + APPS_XML
                            + " está configurado para "
                            + applicationRealPath);
        }

        return configurationFolderName;
    }

    /**
     * Busca as propriedades de todos os arquivos de configuração e carrega no contexto.
     * 
     * @author: gilberto.lupatini
     * @since: 07/06/2010
     * 
     * @param configurationFolderName
     * @param ctx
     */
    private void loadProperties(String configurationFolderName,
            ApplicationContext ctx) {

        AESProperties properties = new AESProperties();

        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        AbstractApplicationContext context = (AbstractApplicationContext) ctx;

        // Carrega as propriedades de banco
        properties.putAll(getDBProperties(configurationFolderName));

        // Carrega as propriedades do poll de conexões do BD
        properties.putAll(getDBConnectionPool(configurationFolderName));

        // Carrega as propriedades gerais de todas as aplicações
        // %MV_HOME%/conf/general.properties
        properties.putAll(getGeneralProperties(configurationFolderName));

        // Carrega as propriedades específicas da aplicação
        // %MV_HOME%/apps/[configurationFolderName]/conf/general.properties
        properties.putAll(getGeneralApplicationProperties(configurationFolderName));
        
        // Carrega as propriedades do log4j
        
        File log4JFile =this.getLog4JXMLFile(configurationFolderName);
        if (log4JFile == null){
        	log4JFile = this.getLog4JFile(configurationFolderName);
        }
        properties.setProperty("log4j.location", log4JFile.getAbsolutePath());
        
        // Carrega as propriedades do transaction Configuration
        properties.setProperty("transactionConfiguration.location", this.getTransactionConfigurationFilePath());
        
        // Seta as propriedades carregadas no contexto
        configurer.setProperties(properties);
        configurer.postProcessBeanFactory(context.getBeanFactory());
    }

    /**
     * Retorna o caminho do arquivo externo de configuração de transações do Spring.
     * 
     * @author gilberto.lupatini 
     * @since 08/04/2011
     *
     * @return
     */
    private String getTransactionConfigurationFilePath()
    {
        String fileAbsolutPath = MV_HOME + sep + CONF_FOLDER + sep + this.TRANSACTIONAL_PROPERTIES;
        
        if(new File(fileAbsolutPath).exists() == true){
            logger.info("-->> Configuração do " + this.TRANSACTIONAL_PROPERTIES + " carregada do arquivo " + fileAbsolutPath + ".");

            return fileAbsolutPath;
        }else{
            logger.warn("-->> (WARNING) Nenhuma configuração do "+this.TRANSACTIONAL_PROPERTIES+" foi encontrada. Configure o arquivo " + fileAbsolutPath);

            return "";
        }
    }

    /**
     * Busca e retorna as propriedades gerais de todas as aplicações. Caminho: %MV_HOME%/conf/general.properties.
     * 
     * @author: gilberto.lupatini
     * @since: 07/06/2010
     * 
     * @param configurationFolderName
     * @return
     */
    private AESProperties getGeneralProperties(String configurationFolderName) {

        AESProperties generalProperties = new AESProperties();

        File confFile = new File(MV_HOME + sep + CONF_FOLDER + sep + GENERAL_PROPERTIES);

        try {
            generalProperties.load(new FileInputStream(confFile));

            logger.info("-->> Configurações gerais carregadas do arquivo " + confFile + ".");

        } catch (Exception e) {

            logger.warn("-->> Não encontrado ou inválido o arquivo geral de configurações " + confFile + ".");
        }

        // Seta os novos properties
        if (ApplicationContextProviderUtils.getProperties(ApplicationContextProviderUtils.GENERAL_PROPERTIES) == null) {
            ApplicationContextProviderUtils.setGeneralProperties(generalProperties);

            // Adiciona se ja existirem
        } else {
            ApplicationContextProviderUtils.getProperties(
                    ApplicationContextProviderUtils.GENERAL_PROPERTIES).putAll(generalProperties);
        }

        return generalProperties;
    }

    /**
     * Busca e retorna as propriedades do projeto em específico. Caminho:
     * %MV_HOME%/apps/[configurationFolderName]/conf/general.properties.
     * 
     * @author: gilberto.lupatini
     * @since: 07/06/2010
     * 
     * @param configurationFolderName
     * @return
     */
    private AESProperties getGeneralApplicationProperties(
            String configurationFolderName) {

        AESProperties applicationProperties = new AESProperties();

        File confFile = new File(MV_HOME + sep + APPS_FOLDER + sep
                + configurationFolderName + sep + CONF_FOLDER + sep
                + GENERAL_PROPERTIES);

        try {
            applicationProperties.load(new FileInputStream(confFile));

            logger.info("-->> Configurações gerais de projeto carregadas do arquivo " + confFile + ".");

        } catch (Exception e) {

            logger.warn("-->> Não encontrado ou inválido o arquivo de configurações de projeto " + confFile + ".");
        }

        // Seta os novos properties
        if (ApplicationContextProviderUtils
                .getProperties(ApplicationContextProviderUtils.GENERAL_PROPERTIES) == null) {
            ApplicationContextProviderUtils.setGeneralProperties(applicationProperties);

            // Adiciona se ja existirem
        } else {
            ApplicationContextProviderUtils.getProperties(
                    ApplicationContextProviderUtils.GENERAL_PROPERTIES).putAll(
                    applicationProperties);
        }

        return applicationProperties;
    }

    /**
     * Carrega as configurações de banco baseado no seguinte fluxo: 1. Busca o arquivo db.properties na pasta de
     * configuração específica da aplicação (%MV_HOME%/apps/[configurationFolderName]/conf/db.properties) 2. Caso o
     * arquivo não for encontrado ou as configurações não forem especificadas, buscar na pasta de configurações gerais
     * (%MV_HOME%/conf/db.properties).
     * 
     * @author: rafael.oliveira
     * @since: 08/06/2010
     * 
     * @param configurationFolderName
     * @return
     */
    private AESProperties getDBProperties(String configurationFolderName) {

        AESProperties dbProperties = new AESProperties();

        File confFile = new File(MV_HOME + sep + APPS_FOLDER + sep
                + configurationFolderName + sep + CONF_FOLDER + sep
                + DB_PROPERTIES);

        try {
            dbProperties.load(new FileInputStream(confFile));
            String service = dbProperties.getProperty("jdbc.servicename");
            if (service == null || service == "") {
                throw new FileNotFoundException();
            }

            logger.info("-->> Banco inicializado a partir do arquivo "
                    + confFile + ".");

        } catch (Exception e) {

            confFile = new File(MV_HOME + sep + CONF_FOLDER + sep
                    + DB_PROPERTIES);

            logger.info("-->> Banco inicializado a partir do arquivo geral "
                    + confFile + ".");
            try {
                dbProperties.load(new FileInputStream(confFile));
            } catch (Exception e1) {
                logger
                        .error("-->> (ERROR) Nenhuma configuração do Banco foi encontrada. Configure o arquivo "
                                + DB_PROPERTIES);
            }
        }

        ApplicationContextProviderUtils.setDbProperties(dbProperties);

        return dbProperties;
    }
    
    

    private File getLog4JXMLFile(String configurationFolderName) {        
        String propertyName = LOG4J_XML;
        File confFile = new File(MV_HOME + sep + APPS_FOLDER + sep
                + configurationFolderName + sep + CONF_FOLDER + sep
                + propertyName);

        try {
            if (! confFile.exists() || !confFile.isFile()){
            	logger.debug("Arquivo "+confFile.getAbsolutePath()+"");
            	throw new FileNotFoundException("Arquivo de configuração do log4j não encontrado");
            }
            logger.info("-->> Configuração do "+propertyName+" carregada do arquivo "
                    + confFile + ".");

        } catch (Exception e) {

            confFile = new File(MV_HOME + sep + CONF_FOLDER + sep
                    + propertyName);

            try {
                if (! confFile.exists() || !confFile.isFile()){
                	throw new FileNotFoundException("Arquivo de configuração do log4j não encontrado");
                }                
                logger.info("-->> Configuração do "+propertyName+" carregada do arquivo "
                        + confFile + ".");

            } catch (Exception e1) {
            	confFile = null;
                logger
                        .error("-->> (ERROR) Nenhuma configuração do "+propertyName+" foi encontrada. Configure o arquivo "
                                + propertyName);
            }
        }
        return confFile;        
    }    

    
    /**
     * Carrega as configurações do log4j baseado no seguinte fluxo: 1. Busca o arquivo log4j.properties na pasta de
     * configuração específica da aplicação (%MV_HOME%/apps/[configurationFolderName]/conf/log4j.properties) 2. Caso o
     * arquivo não for encontrado ou as configurações não forem especificadas, buscar na pasta de configurações gerais
     * (%MV_HOME%/conf/log4j.properties).
     * 
     * @author: rafael.oliveira
     * @since: 08/06/2010
     * 
     * @param configurationFolderName
     * @return
     */
    private File getLog4JFile(String configurationFolderName) {
        AESProperties properties = new AESProperties();
        String propertyName = LOG4J_PROPERTIES;
        File confFile = new File(MV_HOME + sep + APPS_FOLDER + sep
                + configurationFolderName + sep + CONF_FOLDER + sep
                + propertyName);

        try {
            properties.load(new FileInputStream(confFile));
            logger.info("-->> Configuração do "+propertyName+" carregada do arquivo "
                    + confFile + ".");

        } catch (Exception e) {

            confFile = new File(MV_HOME + sep + CONF_FOLDER + sep
                    + propertyName);

            try {
                properties.load(new FileInputStream(confFile));
                logger.info("-->> Configuração do "+propertyName+" carregada do arquivo "
                        + confFile + ".");

            } catch (Exception e1) {
                logger
                        .error("-->> (ERROR) Nenhuma configuração do "+propertyName+" foi encontrada. Configure o arquivo "
                                + propertyName);
            }
        }
        return confFile;        
    }    
    
    /**
     * Carrega as configurações do dBConnectionPool usando o fluxo: 1. Busca o arquivo dBConnectionPool.properties na pasta de configuração
     * específica da aplicação. 2. Caso o arquivo não for encontrado buscar na pasta de configurações gerais
     * 
     * @author: gilberto.lupatini
     * @since: 08/06/2010
     * 
     * @param configurationFolderName
     * @return
     */
    private AESProperties getDBConnectionPool(String configurationFolderName) {

        AESProperties dBConnectionPool = new AESProperties();

        File confFile = new File(MV_HOME + sep + APPS_FOLDER + sep
                + configurationFolderName + sep + CONF_FOLDER + sep
                + DB_CONNECTION_POOL_PROPERTIES);

        try {
        	dBConnectionPool.load(new FileInputStream(confFile));
            logger.info("-->> Configuração do poll de conexões de BD carregada do arquivo "
                    + confFile + ".");

        } catch (Exception e) {

            confFile = new File(MV_HOME + sep + CONF_FOLDER + sep
                    + DB_CONNECTION_POOL_PROPERTIES);

            try {
            	dBConnectionPool.load(new FileInputStream(confFile));
                logger.info("-->> Configuração do poll de conexões de BD carregada do arquivo "
                        + confFile + ".");

            } catch (Exception e1) {
            	
            	logger.warn("-->> (WARNING) Nenhuma configuração do pool de conexões de BD (" + DB_CONNECTION_POOL_PROPERTIES + ") foi encontrada. " +
            			"Os valores padrão para o pool de conexões serão usados.  ");
            	try{
            		dBConnectionPool.setProperty("dBConnectionPool.minLimit", "5");
                	dBConnectionPool.setProperty("dBConnectionPool.maxLimit", "60");
                	dBConnectionPool.setProperty("dBConnectionPool.inactivityTimeout", "600");
                	dBConnectionPool.setProperty("dBConnectionPool.timeToLiveTimeout", "600");
                	dBConnectionPool.setProperty("dBConnectionPool.propertyCheckInterval", "180");
                	dBConnectionPool.setProperty("dBConnectionPool.initialLimit", "5");
                	dBConnectionPool.setProperty("dBConnectionPool.maxStatementsLimit", "5000");
                	dBConnectionPool.setProperty("dBConnectionPool.validateConnection", "true");
            	}
            	catch(Exception e2){
            		logger.error("Erro ao carregar valores padrão para o pool de conexões.");
            	}
            	
            }
        }

        ApplicationContextProviderUtils.setDBconnectionPool(dBConnectionPool);

        return dBConnectionPool;
    }
    /**
     * Lê o xml.
     * 
     * @author: rafael.oliveira
     * @since: 08/06/2010
     * 
     * @param fileName
     * @return
     */
    private Document parseFile(String fileName) {

        logger.info("Parsing XML file... " + fileName);

        DocumentBuilder docBuilder;
        Document doc = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();

        } catch (ParserConfigurationException e) {
            logger.error("Wrong parser configuration: " + e.getMessage());
            return null;
        }
        File sourceFile = new File(fileName);
        try {
            doc = docBuilder.parse(sourceFile);
        } catch (SAXException e) {
            logger.error("Wrong XML file structure: " + e.getMessage());
            return null;

        } catch (IOException e) {
            logger.error("Could not read source file: " + e.getMessage());
        }

        logger.info("XML file parsed");
        return doc;
    }

    /**
     * Retorna a URL da aplicação a partir do contexto.
     * 
     * @author: rafael.oliveira
     * @since: 08/06/2010
     * 
     * @param ctx
     * @return
     */
    private String getApplicationURL(ApplicationContext ctx) {
        WebApplicationContext web = (WebApplicationContext) ctx;
        ServletContext servletContext = web.getServletContext();
        String computerName = "";
        try {
            computerName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String port = getTomcatPort(servletContext);
        String appName = servletContext.getContextPath();
        return "http://" + computerName + ":" + port + appName;
    }

    /**
     * Retorna a porta da aplicação a partir do contexto de servlet.
     * 
     * @author: rafael.oliveira
     * @since: 08/06/2010
     * 
     * @param servletContext
     * @return
     */
    private String getTomcatPort(ServletContext servletContext) {
        String appPath = new File(servletContext.getRealPath(sep))
                .getAbsolutePath();
        String webAppsPath = appPath.substring(0, appPath.lastIndexOf(sep));
        String tomcatPath = webAppsPath.substring(0, webAppsPath
                .lastIndexOf(sep));
        Document serverXMLDoc = parseFile(tomcatPath + sep + CONF_FOLDER + sep
                + "server.xml");
        NodeList list = serverXMLDoc.getElementsByTagName("*");
        for (int i = 0; i < list.getLength(); i++) {
            // Get element
            Element element = (Element) list.item(i);
            if (element.getNodeName().equals("Connector"))
                if (element.hasAttribute("protocol"))
                    if (element.getAttribute("protocol").equals("HTTP/1.1"))
                        return element.getAttribute("port");
        }
        return null;
    }

    /**
     * Retorna o caminho do arquivo geral de configuração das aplicações (.xml).
     * 
     * @author: rafael.oliveira
     * @since: 08/06/2010
     * 
     * @return
     */
    private Document getMappingDocument() {
        return parseFile(MV_HOME + sep + CONF_FOLDER + sep + APPS_XML);
    }

    /**
     * Retorna o caminho do arquivo de configuração.
     * 
     * @author: joao.cordenunzi
     * @since: 24/08/2010
     * 
     * @param propertieName
     * @return PropertiePatch
     */    
    public static String getPropertiesPath(String propertieName) {
        return MV_HOME + sep + APPS_FOLDER + sep
                + ApplicationContextProviderUtils.getConfFolderName() + sep + CONF_FOLDER + sep
                + propertieName;    	
    }    

    /**
     * Retorna o arquivo de configuração na pasta conf.
     * 
     * @author: leonardo.pereira
     * @since: 02/02/2011
     * 
     * @param fileName
     * @return File
     */ 
    public static File getFileByName(String fileName){
    	File file = new File(MV_HOME + sep + APPS_FOLDER + sep
    			+ ApplicationContextProviderUtils.getConfFolderName() + sep + CONF_FOLDER + sep
    			+ fileName);
    	
    	if (!file.exists()){
    		file = new File(MV_HOME + sep + CONF_FOLDER + sep
    				+ fileName);
    	}
    	return file;
    }


}
