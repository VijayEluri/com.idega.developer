package com.idega.development.presentation;

import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.jmodule.object.interfaceobject.DropdownMenu;
import java.util.Locale;
import java.util.List;
import java.util.Iterator;
import com.idega.util.LocaleUtil;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class Localizer extends ModuleObjectContainer {

  private static String bundlesParameter="iw_availablebundles";
  private static String localesParameter="iw_locales";
  private static String stringsParameter="iw_localestrings";
  private static String areaParameter = "iw_stringsarea";
  private static String subAction = "iw_localizer_sub_action";
  private static String newStringKeyParameter = "iw_new_string_key";

  public Localizer(){
  }

  public void main(ModuleInfo modinfo){
      IWMainApplication iwma = modinfo.getApplication();
      DropdownMenu bundlesDrop = getRegisteredDropdown(iwma,bundlesParameter);
      bundlesDrop.keepStatusOnAction();
      bundlesDrop.setToSubmit();
      DropdownMenu localesDrop = getAvailableLocalesDropdown(iwma,localesParameter);
      localesDrop.keepStatusOnAction();
      localesDrop.setToSubmit();

      DropdownMenu stringsDrop;

      String selectedLocale = modinfo.getParameter(localesParameter);
      String selectedBundle = modinfo.getParameter(bundlesParameter);

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table table = new Table(4,3);
      form.add(table);
      table.add("Bundle",1,1);
      table.add(bundlesDrop,1,1);
      table.add("Locale",2,1);
      table.add(localesDrop,2,1);

      if(selectedBundle ==null){
        //stringsDrop = new DropdownMenu(stringsParameter);
        table.add(new SubmitButton("Get Available Keys",subAction,"choose"),2,1);
      }
      else{


        IWResourceBundle iwrb = iwma.getBundle(selectedBundle).getResourceBundle(LocaleUtil.getLocale(modinfo.getParameter(localesParameter)));
        String stringsKey = modinfo.getParameter(stringsParameter);
        String areaText = modinfo.getParameter(areaParameter);
        String newStringsKey = modinfo.getParameter(this.newStringKeyParameter);
         if(stringsKey==null && newStringsKey!=null){
            stringsKey=newStringsKey;
          }

        if(stringsKey!=null){
          String oldStringValue = iwrb.getLocalizedString(stringsKey);
          if(areaText==null){
            ModuleObject area = getTextArea(areaParameter,oldStringValue);
            table.add(area,3,2);
          }
          else{
            if(areaText.equals("")){
              ModuleObject area;
              if(oldStringValue!=null){
                area = getTextArea(areaParameter,oldStringValue);
              }
              else{
                area = getTextArea(areaParameter,"");
              }
              table.add(area,3,2);
            }
            else{
              ModuleObject area;
              /**
               * Saving possible
               */
              if(this.isSaveable(modinfo)){
                String newKey = modinfo.getParameter(newStringKeyParameter);

                if(newKey !=null){
                  if(newKey.equals("")){
                    iwrb.setString(stringsKey,areaText);
                  }
                  else{
                    iwrb.setString(newKey,areaText);
                  }
                }
                area = getTextArea(areaParameter,areaText);
              }
              /**
               * Not Saving
               */
              else{

                //String areaValue = iwrb.getStringChecked(stringsKey);
                String areaValue = modinfo.getParameter(this.areaParameter);
                if(areaValue==null){
                  area = getTextArea(areaParameter,"");
                }
                else{
                  if(oldStringValue==null){
                    area = getTextArea(areaParameter,"");
                  }
                  else{
                      area = getTextArea(areaParameter,oldStringValue);
                  }

                }

              }

               table.add(area,3,2);
            }

          }
          table.add(new SubmitButton("Save",subAction,"save"),3,2);
          table.add("New String key",1,2);
          TextInput newInput = new TextInput(newStringKeyParameter);
          table.add(newInput,1,2);
        }
        else{
          table.add(getTextArea(areaParameter,""),2,2);
          table.add(new SubmitButton("Save",subAction,"save"),3,2);
          table.add("New String key",1,2);
          TextInput newInput = new TextInput(newStringKeyParameter);
          table.add(newInput,1,2);
        }

        //table.add(new SubmitButton("Select Locale",subAction,"select"),2,1);
        table.add("String",3,1);
        stringsDrop = this.getLocalizeableStringsMenu(iwma,selectedBundle,stringsParameter);
        stringsDrop.keepStatusOnAction();
        stringsDrop.setToSubmit();
        table.add(stringsDrop,3,1);
        //table.add(new SubmitButton("Choose String",subAction,"choose"),3,1);

        table.add(this.getLocalizeableStringsTable(iwma,selectedBundle,iwrb),2,3);

      }




  }

  public static DropdownMenu getAvailableLocalesDropdown(IWMainApplication iwma,String name){
    List locales = iwma.getAvailableLocales();
    DropdownMenu down = new DropdownMenu(name);
    Iterator iter = locales.iterator();
    while (iter.hasNext()) {
      Locale item = (Locale)iter.next();
      down.addMenuElement(item.toString(),item.getDisplayName());
    }
    return down;
  }

   public static Table getLocalizeableStringsTable(IWMainApplication iwma,String bundleIdentifier, IWResourceBundle iwrb){
    IWBundle bundle = iwma.getBundle(bundleIdentifier);
    String[] strings = bundle.getLocalizableStrings();
    Table table = new Table(2,strings.length);
    String localizedString;
    Text name;
    for (int i = 0; i < strings.length; i++) {
      name = new Text(strings[i],true,false,false);
      table.add(name,1,i+1);
      localizedString = iwrb.getLocalizedString(strings[i]);
      if (localizedString==null) localizedString = "";
      table.add(localizedString ,2,i+1);
    }

    table.setWidth(300);
    table.setColor("#9FA9B3");

    return table;
  }

  public static DropdownMenu getRegisteredDropdown(IWMainApplication iwma,String name){
    return BundlePropertySetter.getRegisteredBundlesDropdown(iwma,name);
  }

  public static DropdownMenu getLocalizeableStringsMenu(IWMainApplication iwma,String bundleIdentifier,String name){
    IWBundle bundle = iwma.getBundle(bundleIdentifier);
    String[] strings = bundle.getLocalizableStrings();
    DropdownMenu down = new DropdownMenu(name);
    for (int i = 0; i < strings.length; i++) {
      down.addMenuElement(strings[i]);
    }
    return down;
  }

  private boolean isSaveable(ModuleInfo modinfo){
      String subActioner = modinfo.getParameter(subAction);
      if(subActioner==null){
        return false;
      }
      else{
        if(subActioner.equals("save")){
          return true;
        }
        return false;
      }
  }

  private ModuleObject getTextArea(String name,String startValue){
      TextArea area = new TextArea(name,startValue);
      area.setWidth(30);
      return area;
  }

}