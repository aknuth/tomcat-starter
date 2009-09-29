package org.apache.tomcat.config;

import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.tomcat.utils.CustomToStringStyle;

public class InitParameter {
	InitParameter(Map<String, String> params) {
		this.params = params;
	}
	Map<String, String> params;
	public Map<String, String> getParams() {
		return params;
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,new CustomToStringStyle(4));
	}
}
