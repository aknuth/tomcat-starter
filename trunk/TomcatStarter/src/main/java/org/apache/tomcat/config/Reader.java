package org.apache.tomcat.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

public class Reader {

	private static Logger logger = Logger.getLogger(Reader.class);
	private static Stack<String> stack = new Stack<String>();
	
	public static Configuration readConfiguration(File configFile) {
		Configuration result=null;
		if (configFile==null){
			return new Configuration();
		}
		try {
			InputStream in = new FileInputStream(configFile);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(in);
			result = evaluate(parser);
			logger.debug("Reading parsing "+configFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (FactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		logger.debug(result);
		return result;
	}

	private static Configuration evaluate(XMLStreamReader parser) throws XMLStreamException {
		Configuration configuration = new Configuration();
		String attributeValue=null;
		while (parser.hasNext()) {
			int event = parser.next();
			switch (event) {
			case XMLStreamConstants.START_DOCUMENT:
				break;
			case XMLStreamConstants.END_DOCUMENT:
				parser.close();
				break;
			case XMLStreamConstants.START_ELEMENT:
				if (parser.getAttributeCount()>0){
					attributeValue=parser.getAttributeValue(0);
				} else {
					attributeValue=null;
				}
				stack.add(parser.getLocalName());
				configuration = mergeConfigNewElements(getXPathSimple(),configuration);
				break;
			case XMLStreamConstants.CHARACTERS:
				configuration = mergeConfigValues(getXPathSimple(),configuration,parser.getText(),attributeValue);
				break;
			case XMLStreamConstants.END_ELEMENT:
				stack.pop();
				break;
			default:
				break;
			}
		}
		return configuration;
	}

	private static String getXPathSimple() {
		StringBuffer buffer =new StringBuffer();
		for (int i = 0; i < stack.size(); i++) {
			buffer.append(stack.get(i));
			buffer.append(i==stack.size()-1?"":".");
		}
		return buffer.toString();
	}
	private static Configuration mergeConfigNewElements(String expression,
			Configuration configuration) {
		if (expression==null) return configuration;
		//System.out.println(lowerCaseExpression);
		if (StringUtils.equals("tomcat-config.context.webapps", expression)){
			configuration.getContext().webapp=(Webapp[])ArrayUtils.remove(configuration.getContext().getWebapp(), 0);
		} else if (StringUtils.equals("tomcat-config.context.webapps.webapp", expression)){
			Webapp webapp = new Webapp();
			configuration.getContext().webapp=(Webapp[])ArrayUtils.add(configuration.getContext().webapp, webapp);
		} else if (StringUtils.equals("tomcat-config.context.servlets", expression)){
			configuration.getContext().servlet=new Servlet[0];
		} else if (StringUtils.equals("tomcat-config.context.servlets.servlets", expression)){
			configuration.getContext().servlet=(Servlet[])ArrayUtils.add(configuration.getContext().servlet, new Servlet());
		}
		return configuration;
	}


	private static Configuration mergeConfigValues(String expression, Configuration configuration, String nodeValue, String attributeValue) {
		if (expression==null||StringUtils.isWhitespace(nodeValue)) return configuration;
		//############################################################################################
		//GENERAL
		//############################################################################################
		if (StringUtils.equals("tomcat-config.connector.port", expression)){
			configuration.port=NumberUtils.toInt(nodeValue);
		} else if (StringUtils.equals("tomcat-config.engine.name", expression)){
			configuration.engineName=nodeValue;
		} else if (StringUtils.equals("tomcat-config.engine.defaultHost", expression)){
			configuration.defaultHost=nodeValue;
		//############################################################################################
		//WEBAPPS
		//############################################################################################
		} else if (StringUtils.equals("tomcat-config.context.webapps.webapp.contextPath", expression)){
			Webapp[] webapp =  configuration.getContext().webapp;
			webapp[webapp.length-1].contextPath=nodeValue;
		} else if (StringUtils.equals("tomcat-config.context.webapps.webapp.webAppDirectory", expression)){
			Webapp[] webapp =  configuration.getContext().webapp;
			webapp[webapp.length-1].webApplicationHome=nodeValue;
		//############################################################################################
		//SESSIONTIMEOUT
		//############################################################################################
		} else if (StringUtils.equals("tomcat-config.context.sessionTimeout", expression)){
			configuration.getContext().sessionTimeout=NumberUtils.toInt(nodeValue);
		//############################################################################################
		//WELCOME Files
		//############################################################################################
		} else if (StringUtils.equals("tomcat-config.context.welcomeFile", expression)){
			configuration.getContext().welcomeFiles=(String[])ArrayUtils.add(configuration.getContext().welcomeFiles, nodeValue);
		//############################################################################################
		//MIME Mapping
		//############################################################################################
		} else if (StringUtils.equals("tomcat-config.context.mimeMapping", expression)){
			configuration.getContext().mimeMappings.put(attributeValue, nodeValue);
		//############################################################################################
		//SERVLETS
		//############################################################################################
		} else if (StringUtils.equals("tomcat-config.context.servlets.servlets.name", expression)){
			Servlet actualServlet = configuration.getContext().servlet[configuration.getContext().servlet.length-1];
			actualServlet.name=nodeValue;
		} else if (StringUtils.equals("tomcat-config.context.servlets.servlets.class", expression)){
			Servlet actualServlet = configuration.getContext().servlet[configuration.getContext().servlet.length-1];
			actualServlet.clazz=nodeValue;
		} else if (StringUtils.equals("tomcat-config.context.servlets.servlets.loadOnStartup", expression)){
			Servlet actualServlet = configuration.getContext().servlet[configuration.getContext().servlet.length-1];
			actualServlet.loadOnStartup=NumberUtils.toInt(nodeValue);
		} else if (StringUtils.equals("tomcat-config.context.servlets.servlets.servletMapping", expression)){
			Servlet actualServlet = configuration.getContext().servlet[configuration.getContext().servlet.length-1];
			actualServlet.servletMappings.mappings=(String[])ArrayUtils.add(actualServlet.servletMappings.mappings, nodeValue);
		} else if (StringUtils.equals("tomcat-config.context.servlets.servlets.initParameter", expression)){
			Servlet actualServlet = configuration.getContext().servlet[configuration.getContext().servlet.length-1];
			actualServlet.initParams.params.put(attributeValue, nodeValue);
		}
		return configuration;
	}
}
