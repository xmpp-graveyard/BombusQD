/*
 * BombusQD.java
 *
 * Created on 5.01.2005, 21:46
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * 
 * @author Eugene Stahov
 */
package midlet;
//#ifdef AUTOTASK
//# import AutoTasks.AutoTask;
//#endif
import Account.Account;
import Account.AccountSelect;
import Colors.ColorTheme;
//#ifdef STATS
//# import Statistic.Stats;
//#endif
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import locale.*;
import ui.*;
import Client.*;
import Info.Version;
import java.util.Vector;
import util.StringLoader;
import Fonts.*;
import util.Strconv;
import Account.YesNoAlert;
/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */
public class BombusQD extends MIDlet implements Runnable{
    
    private Display display;    // The display for this MIDlet
    private boolean isRunning;
    public boolean isMinimized;
    StaticData sd=StaticData.getInstance();
    ColorTheme ct=ColorTheme.getInstance();
    public SplashScreen s;

    public static Image splash;
    
    private static BombusQD instance; 

    public BombusQD() {
	instance=this; 
        display = Display.getDisplay(this);
        s=SplashScreen.getInstance(display);
        s.setProgress("Loading", 3); // this message will not be localized
    }
    
    /** Entry point  */
    public void startApp() {
        if (isRunning) {
	    hideApp(false);
            return;
        }
        isRunning=true;
        new Thread(this).start();
    }

    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() {
    
    }

    public Image[] imageArr = new Image[2];
    public int wimg_menu;
    public int himg_menu;
    public int wimg_actions;
    public int himg_actions;    
    
    Config cf;    
    public GMenuConfig gm;
    public ImageBuffer ib;
    

    public void run(){
        gm = GMenuConfig.getInstance();  
        ib = ImageBuffer.getInstance();
//#ifdef PLUGINS
//#         getPlugins();
//#endif
        cf = Config.getInstance();
        try {
           imageArr[0] = Image.createImage("/images/menu.png");
           wimg_menu = imageArr[0].getWidth()/8;
           himg_menu = imageArr[0].getHeight()/10;
        } catch (Exception e) {
           s.img=null;
        }       
        cf.path_skin="";
        cf.animateMenuAndRoster=false;

        s.setProgress(3);
        s.getKeys();
        
        s.setProgress(7);
        s.setProgress(Version.getVersionNumber(),10);
        
        SR.loaded();
//#ifdef PEP        
//#         Activity.loaded();
//#endif                
        s.setProgress(12);
//#ifdef STATS
//#         Stats.getInstance().loadFromStorage();
//#endif        
        s.setProgress(15);
        
//#ifdef AUTOTASK
//#         sd.autoTask=new AutoTask(display);
//#         s.setProgress(17);
//#endif

        sd.roster=new Roster(display);
        s.setProgress(18);

        //cf.smiles=true;
        FontClass.getInstance().Init(cf.drwd_fontname); 
        try {
           ib.bgnd_checkers = Image.createImage("/images/games/bgnd_checkers.png");
           ib.checkers_black = Image.createImage("/images/games/black.png");
           ib.checkers_white = Image.createImage("/images/games/white.png");
           System.out.println("checkers loaded!");
        } catch (Exception e) {
            s.img=null;
        }         

          boolean selAccount=( (cf.accountIndex<0) || s.keypressed!=0);
          if (selAccount) 
            s.setProgress("Entering setup",23);   
          //display.setCurrent(sd.roster);
          if (!selAccount && cf.autoLogin)
            Account.loadAccount(cf.autoLogin, cf.accountIndex,-1); // connect whithout account select
          else {
            new AccountSelect(display, sd.roster, true,-1);     
        }
    }

    public void destroyApp(boolean unconditional) { }

    public void hideApp(boolean hide) {
	if (hide){
            cf.isMinimized=true;
            display.setCurrent(null);
        }
	else if (isMinimized)
        {
            cf.isMinimized=false;            
            display.setCurrent(display.getCurrent());
        isMinimized=hide;
        }
    }
    
    public static BombusQD getInstance() {
        return instance;
    }

//#ifdef PLUGINS
//#     private void getPlugins () {
//# 	Vector defs[]=new StringLoader().stringLoader("/modules.txt", 2);
//#         if (defs!=null) {
//#             for (int i=0; i<defs[0].size(); i++) {
//#                 String name      =(String) defs[0].elementAt(i);
//#                 String value     =(String) defs[1].elementAt(i);
//# 
//#                 boolean state=value.equals("true");
//# 
//#                 if (name.equals("Archive")) {
//#                     sd.Archive=state;
//#                 } else if(name.equals("ChangeTransport")) {
//#                     sd.ChangeTransport=state;
//#                 } else if(name.equals("Console")) {
//#                     sd.Console=state;
//#                 } else if(name.equals("FileTransfer")) {
//#                     sd.FileTransfer=state;
//#                 } else if(name.equals("History")) {
//#                     sd.History=state;
//#                 } else if(name.equals("ImageTransfer")) {
//#                     sd.ImageTransfer=state;
//#                 } else if(name.equals("PEP")) {
//#                     sd.PEP=state;
//#                 } else if(name.equals("Privacy")) {
//#                     sd.Privacy=state;
//#                 } else if(name.equals("IE")) {
//#                     sd.IE=state;
//#                 } else if(name.equals("Colors")) {
//#                     sd.Colors=state;
//#                 } else if(name.equals("Adhoc")) {
//#                     sd.Adhoc=state;
//#                 } else if(name.equals("Stats")) {
//#                     sd.Stats=state;
//#                 } else if (name.equals("ClientsIcons")) {
//#                     sd.ClientsIcons=state;
//#                 } else if (name.equals("UserKeys")) {
//#                     sd.UserKeys=state;
//#                 } else if (name.equals("Upgrade")) {
//#                     sd.Upgrade=state;
//#                 }
//#             }
//#         }
//#     }
//#endif
}
