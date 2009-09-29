package org.apache.tomcat.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.tomcat.utils.CustomToStringStyle;

public class Configuration {
	String engineName="default";
	String defaultHost="localhost";
	int port=8080;
	Context context=new Context();
	public String getEngineName() {
		return engineName;
	}
	public String getDefaultHost() {
		return defaultHost;
	}
	public int getPort() {
		return port;
	}
	public Context getContext() {
		return context;
	}
	
	@Override
	public String toString() {
		if (StringUtils.equals(System.getProperty("debug"), "true")){
			return ReflectionToStringBuilder.toString(this,new CustomToStringStyle(1));
		} else {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Configuration:\n");
			buffer.append("==============================================\n");
			buffer.append("Running on Port = ");
			buffer.append(port);
			buffer.append("\n");
			buffer.append("==============================================\n");
			buffer.append("========Folgende Webapplikationen=============\n");
			buffer.append("==============================================\n");
			for (int i=0; i<getContext().getWebapp().length;i++){
				buffer.append("Base directory = ");
				String webHome;
				try {
					webHome = new File(getContext().getWebapp()[i].webApplicationHome).getCanonicalPath();
				} catch (IOException e) {
					webHome=getContext().getWebapp()[i].webApplicationHome;
				}
				buffer.append(webHome);
				buffer.append("\n");
				buffer.append("ContextPath = ");
				buffer.append(getContext().getWebapp()[i].contextPath);
				buffer.append("\n");
			}
			buffer.append("==============================================\n");
			return buffer.toString();
		}
	}
}
