package com.idega.development.presentation;

import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;
import com.idega.util.idegaTimestamp;
import java.io.File;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href=mailto:"eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */



public class ApplicationStatus extends Block {

  private static final String RESTART_PARAMETER = "iw_app_re";

  public ApplicationStatus() {
  }

  public void main(IWContext iwc)throws Exception{
      add(IWDeveloper.getTitleTable(this.getClass()));

      Form form = new Form();
      form.maintainParameter(IWDeveloper.actionParameter);
      add(form);
      Table table = new Table(3,8);
      table.setAlignment(3,1,"right");
      form.add(table);

      SubmitButton restart = new SubmitButton("Restart",RESTART_PARAMETER,"true");
      table.add(IWDeveloper.getText("Restart application"),1,1);
      table.add(restart,3,1);

      // Adding some fancy stuff :.

      String shutdown = iwc.getApplicationSettings().getProperty("last_shutdown");
      String startup = iwc.getApplicationSettings().getProperty("last_startup");
      String reboot = iwc.getApplicationSettings().getProperty("last_restart");
      idegaTimestamp start = null,shut = null,rest = null;
      if(shutdown!=null && !shutdown.equals(""))
        shut = new idegaTimestamp(shutdown);
      if(startup!=null && !startup.equals(""))
        start = new idegaTimestamp(startup);
      if(reboot!=null && !reboot.equals(""))
        rest = new idegaTimestamp(reboot);

      table.add(IWDeveloper.getText("Last startup"),1,3);
      if(shut!=null)
        table.add(IWDeveloper.getText(shut.toSQLString()),3,3);
      else
        table.add(IWDeveloper.getText("Unknown"),3,3);

      table.add(IWDeveloper.getText("Last shutdown"),1,4);
      if(start!=null)
        table.add(IWDeveloper.getText(start.toSQLString()),3,4);
      else
        table.add(IWDeveloper.getText("Unknown"),3,4);

      table.add(IWDeveloper.getText("Last restart"),1,5);
      if(start!=null)
        table.add(IWDeveloper.getText(start.toSQLString()),3,5);
      else
        table.add(IWDeveloper.getText("Unknown"),3,5);

      table.add(IWDeveloper.getText("Uptime"),1,7);
      idegaTimestamp now = idegaTimestamp.RightNow();
      int minutes = 0,maxmin = 0;
      String MaxMinutes = iwc.getApplicationSettings().getProperty("max_minutes");
      if(MaxMinutes!=null && !MaxMinutes.equals("")){
        maxmin = Integer.parseInt(MaxMinutes);
      }

      if(rest!=null){
        minutes = idegaTimestamp.getMinutesBetween(rest,now);
        table.add(IWDeveloper.getText(minutes+" Minutes"),3,7);
      }
      else if(shut!=null){
        minutes = idegaTimestamp.getMinutesBetween(shut,now);
        table.add(IWDeveloper.getText(minutes+" Minutes"),3,7);
      }
      else
        table.add(IWDeveloper.getText("Unknown"),3,7);

      if(minutes > maxmin){
        maxmin = minutes;
        iwc.getApplicationSettings().setProperty("max_minutes",maxmin);
      }

      table.add(IWDeveloper.getText("Max. uptime"),1,8);
      if(start!=null)
        table.add(IWDeveloper.getText(maxmin+" Minutes"),3,8);

      doBusiness(iwc);
  }

  private void doBusiness(IWContext iwc)throws Exception{
      String check = iwc.getParameter(RESTART_PARAMETER);
      if(check!=null){
        add(IWDeveloper.getText("Done Restarting!"));
        iwc.getApplicationSettings().setProperty("last_restart",com.idega.util.idegaTimestamp.RightNow().toString());
        iwc.getApplication().restartApplication();
      }
  }
}