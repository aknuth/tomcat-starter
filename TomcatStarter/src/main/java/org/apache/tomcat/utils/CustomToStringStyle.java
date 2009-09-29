package org.apache.tomcat.utils;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ToStringStyle;

public final class CustomToStringStyle extends ToStringStyle {
    private static final long serialVersionUID = 8L;
    
    /**
     * <p>Constructor.</p>
     *
     * <p>Use the static constant rather than instantiating.</p>
     */
    public CustomToStringStyle(int layer) {
    	super();
        this.setUseClassName(true);
        this.setUseIdentityHashCode(false);
        String spacer = getSpacer(layer);
        this.setContentStart("[");
        this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + spacer);
        this.setFieldSeparatorAtStart(true);
        this.setContentEnd(SystemUtils.LINE_SEPARATOR + getSpacer(layer-1) +"]");
    }

	private String getSpacer(int layer) {
        String spacer = "";
		for (int i = 1; i < layer; i++) {
			spacer=spacer+"  ";
		}
		return spacer;
	}

}
