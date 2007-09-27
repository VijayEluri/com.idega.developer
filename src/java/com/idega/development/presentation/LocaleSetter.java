package com.idega.development.presentation;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LocaleSetter extends Block {

	public static String localesParameter = "iw_localeswitcher_locale";
	private int count = 0;
	//private Locale _coreLocale = null;

	public LocaleSetter() {
		//setResetGoneThroughMainInRestore(true);
	}

	public void main(IWContext iwc) {
		/*add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE()) {
			getParentPage().setBackgroundColor("#FFFFFF");
		}*/

		//this._coreLocale = iwc.getIWMainApplication().getCoreLocale();

		if (iwc.getParameter("save") != null) {
			save(iwc);
		}

		Locale defLocale = iwc.getApplicationSettings().getDefaultLocale();
		ICLocale icDefLocale = ICLocaleBusiness.getICLocale(defLocale);

		Form form = new Form();
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		Table T = new Table();
		T.add(IWDeveloper.getText("Use"), 1, 1);
		T.add(IWDeveloper.getText("Country"), 2, 1);
		T.add(IWDeveloper.getText("Language"), 3, 1);
		T.add(IWDeveloper.getText("Region"), 4, 1);
		T.add(IWDeveloper.getText("Default"), 5, 1);
		

		this.count = 1;
		addToTable(T, ICLocaleBusiness.listOfLocales(true), icDefLocale);
		
		Text localeVariant = new Text("Locale Variant:");
		String setLocaleVariant = iwc.getApplicationSettings().getProperty("com.idega.core.localevariant");
		if(setLocaleVariant==null){
			setLocaleVariant="";
		}
		TextInput localeVariantInput = new TextInput("com.idega.core.localevariant",setLocaleVariant);
		this.count++;
		T.add(localeVariant, 1, this.count);
		T.add(localeVariantInput, 2, this.count);
		
		SubmitButton save = new SubmitButton("save", "Save");
		this.count++;
		T.add(save, 1, this.count);
		this.count++;
		addToTable(T, ICLocaleBusiness.listOfLocales(false), null);
		T.add(new HiddenInput("loc_count", String.valueOf(this.count)));
		T.setCellpadding(2);
		//T.setBorder(1);
		form.add(T);
		add(form);
	}

	private void addToTable(Table T, List listOfLocales, ICLocale defLocale) {
		if (listOfLocales != null) {
			CheckBox chk;
			RadioButton rb;
			ICLocale icLocale;
			Locale javaLocale;
			Iterator I = listOfLocales.iterator();
			while (I.hasNext()) {
				this.count++;
				icLocale = (ICLocale) I.next();
				javaLocale = ICLocaleBusiness.getLocaleFromLocaleString(icLocale.getLocale());
				chk = new CheckBox("loc_chk" + this.count, String.valueOf(icLocale.getLocaleID()));
				chk.setChecked(icLocale.getInUse());
				//had to comment this out so you can actually disable english!
//				if (javaLocale.equals(this._coreLocale)) {
//					chk.setDisabled(true);
//					T.add(new HiddenInput("loc_chk" + this.count, String.valueOf(icLocale.getLocaleID())), 1, this.count);
//				}
				T.add(chk, 1, this.count);
				T.add(IWDeveloper.getText(javaLocale.getDisplayCountry()), 2, this.count);
				T.add(IWDeveloper.getText(javaLocale.getDisplayLanguage()), 3, this.count);
				T.add(IWDeveloper.getText(javaLocale.getDisplayVariant()), 4, this.count);
				if (defLocale != null && icLocale.getInUse()) {
					rb = new RadioButton("default_locale", icLocale.getName());
					T.add(rb, 5, this.count);
					if (defLocale.getLocaleID() == icLocale.getLocaleID()) {
						rb.setSelected();
					}
				}
			}
		}
	}

	private void save(IWContext iwc) {
		String sCount = iwc.getParameter("loc_count");

		if (sCount != null) {
			java.util.Vector V = new java.util.Vector();
			int count = Integer.parseInt(sCount);
			String chk;
			for (int i = 0; i < count; i++) {
				chk = iwc.getParameter("loc_chk" + i);
				if (chk != null) {
					V.add(chk);
				}
			}
			ICLocaleBusiness.makeLocalesInUse(V);

			String sDefLocale = iwc.getParameter("default_locale");
			if (sDefLocale != null) {
				iwc.getApplicationSettings().setDefaultLocale(ICLocaleBusiness.getLocaleFromLocaleString(sDefLocale));
			}
			
			String localeVariant = iwc.getParameter("com.idega.core.localevariant");
			String setLocaleVariant = iwc.getApplicationSettings().getProperty("com.idega.core.localevariant");
			if(setLocaleVariant==null){
				setLocaleVariant="";
			}
			if(localeVariant!=null){
				if(localeVariant.equals("")){
					iwc.getApplicationSettings().removeProperty("com.idega.core.localevariant");
				}
				else{
					iwc.getApplicationSettings().setProperty("com.idega.core.localevariant", localeVariant);
				}
				boolean update = !localeVariant.equals(setLocaleVariant);
				if(update){
					List bundleList = iwc.getIWMainApplication().getRegisteredBundles();
					for (Iterator iter = bundleList.iterator(); iter.hasNext();) {
						IWBundle bundle = (IWBundle) iter.next();
						bundle.reloadBundle();
					}
				}
			}
		}
	}

}
