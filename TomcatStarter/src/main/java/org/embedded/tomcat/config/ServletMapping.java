package org.embedded.tomcat.config;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.embedded.tomcat.utils.CustomToStringStyle;

public class ServletMapping {
	String[] mappings;
	public String[] getMappings() {
		return mappings;
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,new CustomToStringStyle(4));
	}
}
