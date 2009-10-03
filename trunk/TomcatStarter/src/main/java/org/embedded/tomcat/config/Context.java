package org.embedded.tomcat.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.embedded.tomcat.utils.CustomToStringStyle;

public class Context {
	
	Context() {
		servlet = new Servlet[2];
		servlet[0] = new Servlet();
		servlet[0].name="default";
		servlet[0].clazz="org.apache.catalina.servlets.DefaultServlet";
		servlet[0].loadOnStartup=1;
		Map<String, String> map = new HashMap<String, String>();
		map = new HashMap<String, String>();
		map.put("debug", "0");
		map.put("listings", "false");
		servlet[0].initParams = new InitParameter(map);
		String[] mappingDefaultServlet = {"/"};
		servlet[0].servletMappings = new ServletMapping();
		servlet[0].servletMappings.mappings=mappingDefaultServlet;
		
		servlet[1] = new Servlet();
		servlet[1].name="jsp";
		servlet[1].clazz="org.apache.jasper.servlet.JspServlet";
		servlet[1].loadOnStartup=2;
		map = new HashMap<String, String>();
		map = new HashMap<String, String>();
		map.put("fork", "false");
		map.put("xpoweredBy", "false");
		servlet[1].initParams = new InitParameter(map);
		String[] mappingJspServlet = {"*.jsp","*.jspx"};
		servlet[1].servletMappings = new ServletMapping();
		servlet[1].servletMappings.mappings=mappingJspServlet;
		
		mimeMappings = new HashMap<String, String>();
		mimeMappings.put("html","text/html"); 
		mimeMappings.put("htm","text/html"); 
		mimeMappings.put("gif","image/gif"); 
		mimeMappings.put("jpg","image/jpeg"); 
		mimeMappings.put("png","image/png"); 
		mimeMappings.put("js","text/javascript"); 
		mimeMappings.put("css","text/css"); 
		mimeMappings.put("pdf","application/pdf"); 
		
		webapp = new Webapp[1];
		webapp[0] = new Webapp();
		webapp[0].contextPath="/";
		webapp[0].webApplicationHome="WebContent";
	}
	
	int sessionTimeout=30;
	Servlet[] servlet;
	Webapp[] webapp;
	String[] welcomeFiles = {"index.html","index.htm","index.jsp"};
	Map<String, String> mimeMappings;
	
	public int getSessionTimeout() {
		return sessionTimeout;
	}
	public Servlet[] getServlet() {
		return servlet;
	}
	public Webapp[] getWebapp() {
		return webapp;
	}
	public String[] getWelcomeFiles() {
		return welcomeFiles;
	}
	public Map<String, String> getMimeMappings() {
		return mimeMappings;
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,new CustomToStringStyle(2));
	}
}
