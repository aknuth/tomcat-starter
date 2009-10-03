package org.embedded.tomcat.starter;

import java.io.File;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Embedded;
import org.apache.log4j.Logger;
import org.embedded.tomcat.config.Configuration;
import org.embedded.tomcat.config.Reader;

/**
 * @author aknuth
 */
public class TomcatStarter extends TomcatRunner {
	private String CATALINA_HOME = new File(".").getAbsolutePath();
	private static Logger logger = Logger.getLogger(TomcatStarter.class);
	private final Configuration configuration;
	private Embedded embedded;
	private static TomcatStarter server;
	
	public TomcatStarter(Configuration configuration) throws TransformerException {
		super(configuration);
		this.configuration=configuration;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopServer();
			}
		});
	}

	private void init() throws Exception {
        embedded = new Embedded();
        embedded.setCatalinaHome(CATALINA_HOME);
		
		Engine engine = createEngine();
		embedded.addEngine(engine);
		
		createHost(engine);
		
		createConnector();
	}

	/**
	 * Create an Engine
	 * @return Engine
	 */
	private Engine createEngine() {
		Engine engine = embedded.createEngine();
		engine.setName(configuration.getEngineName());
		engine.setDefaultHost(configuration.getDefaultHost());
		embedded.addEngine(engine);
		return engine;
	}

	/**
	 * Create a Host and add this to the engine
	 */
	private void createHost(Engine engine) {
		Host host = embedded.createHost(configuration.getDefaultHost(), CATALINA_HOME);
		engine.addChild(host);
		for (int i = 0; i < configuration.getContext().getWebapp().length; i++) {
			Context context = createContext(configuration.getContext().getWebapp()[i].getContextPath(), 
					new File(configuration.getContext().getWebapp()[i].getWebApplicationHome()).getAbsolutePath());
			host.addChild(context);
		}
	}

	/**
	 * Create a Context from the embedder and return it for appending it to the host
	 * return context
	 */
	private Context createContext(String path, String docBase) {
		Context context = embedded.createContext(path, docBase);
		
		// Create servlets
		for (int i = 0; i < configuration.getContext().getServlet().length; i++) {
			Wrapper servlet = context.createWrapper();
			servlet.setServletClass(configuration.getContext().getServlet()[i].getClazz());
			servlet.setName(configuration.getContext().getServlet()[i].getName());

			servlet.setLoadOnStartup(configuration.getContext().getServlet()[i].getLoadOnStartup());
			
			Iterator<String> iterator = configuration.getContext().getServlet()[i].getInitParams().getParams().keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = configuration.getContext().getServlet()[i].getInitParams().getParams().get(key);
				servlet.addInitParameter(key, value);
			}
			context.addChild(servlet);
			
			String[] mappings = configuration.getContext().getServlet()[i].getServletMappings().getMappings();
			for (int j = 0; j < mappings.length; j++) {
				context.addServletMapping(mappings[j], configuration.getContext().getServlet()[i].getName());
			}
		}

		// Add welcome Files
		for (int i = 0; i < configuration.getContext().getWelcomeFiles().length; i++) {
			context.addWelcomeFile(configuration.getContext().getWelcomeFiles()[i]);
		}

		// Add some mime mappings
		Iterator<String> iterator = configuration.getContext().getMimeMappings().keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = configuration.getContext().getMimeMappings().get(key);
			context.addMimeMapping(key, value);
		}

		context.setParentClassLoader(this.getClass().getClassLoader());

		context.setSessionTimeout(configuration.getContext().getSessionTimeout());
		
		return context;
	}

	/**
	 * Create a Connector and add this to the embedder
	 */
	private void createConnector() {
		Connector connector = embedded.createConnector((String) null, configuration.getPort(), false);
		embedded.addConnector(connector);
	}
	
	/**
	 * init the server and start it
	 * @throws Exception
	 */
	public void startServer() throws Exception {
		init();
		embedded.start();
	}

	/**
	 * stop the Server programmatically
	 * @throws Exception
	 */
	public void stopServer() {
        if (embedded != null) {
            try {
                System.out.println("Shutting down MyServer...");
                embedded.stop();
                System.out.println("MyServer shutdown.");
                server.notifyAll();
            } catch (Exception e) {
                //No need to do anything
            }
        }
	}

	/**
	 * Call the Tomcat Embedder Starter class
	 * @param config-file for creating an Tomcat embedded server
	 */
	public static void main(String[] args) throws Exception {
		File configFile = null;
		if (args.length==1){
			configFile = new File(args[0]);
			if (!configFile.exists()){
				throw new RuntimeException("Config-File: "+configFile.getCanonicalPath()+" does not exitst ...");
			}
			logger.info("Reading config-file:"+configFile.getAbsolutePath());
		} else {
			logger.info("No config-file found ");
		}
		Configuration configuration = Reader.readConfiguration(configFile);
		server = new TomcatStarter(configuration);
		logger.info("Starting Tomcat:\n"+server);
		server.startServer();

        Thread thread = new Thread(server);
        if (listenerMode){
        	thread.start();
        } else {
            synchronized (server) {
                server.wait();
            }
        }

	}
	
}
