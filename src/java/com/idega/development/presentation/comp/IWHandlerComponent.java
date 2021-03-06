/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.development.presentation.comp;

import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.core.component.data.BundleComponent;
import com.idega.core.component.data.ICObjectBMPBean;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class IWHandlerComponent extends IWBaseComponent implements BundleComponent {
	
	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#getRequiredInterfaces()
	 */
	public Class[] getRequiredInterfaces() {
		Class[] array = new Class[1];
		array[0] = ICPropertyHandler.class;
		return array;
	}

	/* (non-Javadoc)
	 * @see com.idega.development.presentation.comp.BundleComponent#type()
	 */
	public String getType() {
		return ICObjectBMPBean.COMPONENT_TYPE_PROPERTYHANDLER;
	}

}
