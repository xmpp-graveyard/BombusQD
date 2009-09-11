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
//import ui.*;
import Client.Config;
import Client.StaticData;
import Client.Roster;
import Client.Cashe;
import Info.Version;
import ui.GMenu;
import ui.SplashScreen;
import java.util.Vector;
import util.StringLoader;
import Fonts.*;
import util.Strconv;
import Account.YesNoAlert;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */
public class BombusQD extends MIDlet implements Runnable
{
    
    public Display display  = Display.getDisplay(this);
    private boolean isRunning;
    public boolean isMinimized;
    
    public final static StaticData sd = StaticData.getInstance();
    public final static Config cf = Config.getInstance();
    public final static Cashe cashe = Cashe.get();  
    public final static ClipBoard clipboard=ClipBoard.getInstance();
    public final static Commands commands=Commands.get();    
    
    public int width = 0;
    public int height = 0;
    
    ColorTheme ct=ColorTheme.getInstance();
    
    public SplashScreen s;
    private static BombusQD instance;
    

    public BombusQD() {
	instance=this; 
        s=SplashScreen.getInstance(display);
        s.setProgress("Loading", 3);
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
    public void pauseApp() { }

    
    public Image[] imageArr = null;
    public int wimg_menu;
    public int himg_menu;
    public int wimg_actions;
    public int himg_actions;    
    //public GMenuConfig gm;//
    //public ImageBuffer ib;//

    
    public void run(){
        long s1 = System.currentTimeMillis();
        //gm = GMenuConfig.getInstance();//
        //ib = ImageBuffer.getInstance();//
        AccountSelect acc = new AccountSelect(display, null , true,-1);

        s.setProgress(18);
        boolean selAccount=((cf.accountIndex<0) /*|| s.keypressed!=0*/ );
          //if (selAccount) 
          //display.setCurrent(sd.roster);
          if (!selAccount && cf.autoLogin){
            sd.roster=new Roster(display);
            Account.loadAccount(cf.autoLogin, cf.accountIndex,-1);
            display.setCurrent(sd.roster);
          }
          else {
            display.setCurrent(acc);
        }
        
        long s2 = System.currentTimeMillis();
        System.out.println((s2-s1)+" msec");
        
        s.getKeys();
        width=s.width;
        height=s.height;
        
        try {
           imageArr = new Image[2];
           imageArr[0] = Image.createImage("/images/menu.png");
           wimg_menu = imageArr[0].getWidth()/8;
           himg_menu = imageArr[0].getHeight()/10;
        } catch (Exception e) { }    
        
        //cf.path_skin="";
        
        SR.loaded();
        FontClass.getInstance().Init(cf.drwd_fontname);         
        
        
        if(sd.roster==null){
          sd.roster=new Roster(display);
        }
//#ifdef STATS
//#         Stats.getInstance().loadFromStorage();
//#endif         
//#ifdef PEP        
//#         Activity.loaded();
//#endif                
//#ifdef AUTOTASK
//#         sd.autoTask=new AutoTask(display);
//#endif        


        //StartApp:
        //SE Emulator: W700  JP8-240x320    DefaultJ2ME
        //undo -      697    643            1093-1300      msec
        //after -     288    329            225-350        msec
        //(use: 580kb after start)
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
}
