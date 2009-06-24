/*
 * InfoWindow.java
 *
 * Created on 25.05.2008, 19:29
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package Info;

import Client.Config;
import Client.StaticData;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import midlet.BombusQD;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;
import util.ClipBoard;
import ui.controls.form.LinkString;
import util.StringLoader;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif   
/**
 *
 * @author ad
 */
public class InfoWindow
        extends DefForm {
    
    LinkString siteUrl;
    MultiLine description;
    MultiLine thanks;
    MultiLine name;
    MultiLine memory;
    MultiLine abilities;
    
    LinkString news;
    LinkString versions; 
    
    StaticData sd=StaticData.getInstance();
    
//#ifdef CLIPBOARD
//#ifndef MENU
//#     public Command cmdOk = new Command(SR.MS_COPY, Command.SCREEN, 1);
//#endif
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//#endif
    
    /**
     * Creates a new instance of InfoWindow
     */
    MultiLine item=null;

    public InfoWindow(Display display, Displayable pView) {
        super(display, pView, SR.MS_ABOUT);
        this.display=display;

        name=new MultiLine(Version.getName(), Version.getVersionNumber()+
                "\n"+Config.getOs()+
                "\nMobile Jabber client" , super.superWidth);

        name.selectable=true;
        itemsList.addElement(name);
        

        description=new MultiLine("Copyright (c) 2005-2009", 
                "Eugene Stahov (evgs,Bombus);\nDaniel Apatin (ad,BombusMod);\nAlexej Kotov(aqent,BombusQD)\n" +
                "Distributed under BSD License \n", super.superWidth);
        description.selectable=true;
        itemsList.addElement(description);
        
        thanks = new MultiLine("Thanks to:","Testing: zaetz,demon(Dmitry Krylov),magnit,Sniffy,NNn\n" +
                "Color-Themes,Clients Images: Xa\n" +
                "Actions icons: Rederick Asher\n" +
                "Site managment: BiLLy\n" +
                "Jimm Dev's for back.png ;)\n" +
                "Windows Fonts: magdelphi(mobilab.ru)" +
                "\nMathFP library",super.superWidth);
        thanks.selectable=false;
        itemsList.addElement(thanks);
        
        itemsList.addElement(new LinkString("http://bombusmod-qd.wen.ru")
                { public void doAction() { try { BombusQD.getInstance().platformRequest("http://bombusmod-qd.wen.ru"); } 
                  catch (ConnectionNotFoundException ex) { }}}
        );
        siteUrl=new LinkString("http://bombusmod.net.ru"){ public void doAction() { try { BombusQD.getInstance().platformRequest("http://bombusmod.net.ru"); } catch (ConnectionNotFoundException ex) { }}};
        itemsList.addElement(siteUrl);
        
        itemsList.addElement(new SpacerItem(10));
        
        StringBuffer memInfo=new StringBuffer(SR.MS_FREE);
        System.gc();
        memInfo.append(Runtime.getRuntime().freeMemory()>>10)
               .append("\n")
               .append(SR.MS_TOTAL)
               .append(Runtime.getRuntime().totalMemory()>>10);
        memory=new MultiLine(SR.MS_MEMORY, memInfo.toString(), super.superWidth);
        memory.selectable=true;
        itemsList.addElement(memory);
        memInfo=null;
        
        abilities=new MultiLine("Abilities", getAbilities(), super.superWidth);
        abilities.selectable=true;
        itemsList.addElement(abilities);
        
        
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             clipboard=ClipBoard.getInstance(); 
//#         }
//#endif
        
//#ifndef MENU
        super.removeCommand(super.cmdOk);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             addCommand(cmdOk); cmdOk.setImg(0x43);
//#         }
//#endif
//#endif
        

        versions = new LinkString("About") { public void doAction() { showVersions(); } };
        itemsList.addElement(versions); 
        attachDisplay(display);
        this.parentView=pView;
    }
    
    public void showNews() {
        itemsList.removeElement(news);         
        String getNews = new StringLoader().loadString("/roadmap.txt");
        item=new MultiLine("Version ", Info.Version.getVersionNumber(), super.superWidth);  item.selectable=true; itemsList.addElement(item);
        item=new MultiLine("News", getNews, super.superWidth);  item.selectable=true; itemsList.addElement(item);
    }
    
    public void showVersions(){
        showNews();         
    }
        
    
//#ifdef CLIPBOARD
//#     public void cmdOk(){
//#         clipboard.setClipBoard(name.toString()+"\n"+memory.toString()+"\n"+abilities.toString());
//#         destroyView();
//# 
//#     }
//#endif
    
    
    
    
    public void commandState(){
//#ifdef MENU_LISTENER        
        menuCommands.removeAllElements();
//#endif  
        
//#ifndef GRAPHICS_MENU               
    super.commandState(); 
//#endif
        removeCommand(cmdCancel);
        removeCommand(cmdOk);
    }
    
    
//#ifdef MENU_LISTENER
    /*
    public String touchLeftCommand(){ return SR.MS_MENU; }
    
//#ifdef GRAPHICS_MENU     
//#     public void touchLeftPressed(){
//#         showGraphicsMenu();
//#     }
//#     public int showGraphicsMenu() {
//#         commandState();
//#         new GMenu(display, parentView, this,null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.INFO_WINDOW;  
//#         redraw();
//#         return GMenu.INFO_WINDOW;
//#     }
//#      */
//#else
    public void touchLeftPressed(){
        showMenu();
    }
    
    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.MS_STATS, null, menuCommands);
   } 
//#endif      


//#endif         
        

    public void commandAction(Command command, Displayable displayable) {
	if (command==cmdOk) {
	    cmdOk();
	}
        super.commandAction(command, displayable);
    }
    
    private String getAbilities() {
        Vector abilitiesList=new Vector();
//#ifdef ADHOC
//#ifdef PLUGINS
//#         if (sd.Adhoc)
//#endif
//#             abilitiesList.addElement((String)"ADHOC");
//#endif
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (sd.Archive)
//#endif
            abilitiesList.addElement((String)"ARCHIVE");
//#endif
//#ifdef AUTOSTATUS
//#         abilitiesList.addElement((String)"AUTOSTATUS");
//#endif
//#ifdef AUTOTASK
//#         abilitiesList.addElement((String)"AUTOTASK");
//#endif 
//#ifdef BACK_IMAGE
//#         abilitiesList.addElement((String)"BACK_IMAGE");
//#endif
//#ifdef CAPTCHA
//#         abilitiesList.addElement((String)"CAPTCHA");
//#endif
//#ifdef CHANGE_TRANSPORT
//#ifdef PLUGINS
//#         if (sd.ChangeTransport)
//#endif
//#             abilitiesList.addElement((String)"CHANGE_TRANSPORT");
//#endif
//#ifdef CHECK_VERSION
//#ifdef PLUGINS
//#         if (sd.Upgrade)
//#endif
//#             abilitiesList.addElement((String)"CHECK_VERSION");
//#endif
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#         if (sd.ClientsIcons)
//#endif
            abilitiesList.addElement((String)"CLIENTS_ICONS");
//#endif
//#ifdef CLIPBOARD
//#         abilitiesList.addElement((String)"CLIPBOARD");
//#endif
//#ifdef CONSOLE
//#ifdef PLUGINS
//#         if (sd.Console)
//#endif
//#             abilitiesList.addElement((String)"CONSOLE");
//#endif
//#ifdef COLOR_TUNE
//#ifdef PLUGINS
//#         if (sd.Colors)
//#endif
//#             abilitiesList.addElement((String)"COLOR_TUNE");
//#endif
//#ifdef DETRANSLIT
//#         abilitiesList.addElement((String)"DETRANSLIT");
//#endif
//#ifdef ELF
//#         abilitiesList.addElement((String)"ELF");
//#endif
//#ifdef FILE_IO
        abilitiesList.addElement((String)"FILE_IO");
//#endif
//#ifdef FILE_TRANSFER
//#ifdef PLUGINS
//#         if (sd.FileTransfer)
//#endif
            abilitiesList.addElement((String)"FILE_TRANSFER");
//#endif
//#ifdef GRADIENT
//#         abilitiesList.addElement((String)"GRADIENT");
//#endif
//#ifdef HISTORY
//#ifdef PLUGINS
//#         if (sd.History)
//#endif
//#             abilitiesList.addElement((String)"HISTORY");
//#endif
//#ifdef HISTORY_READER
//#ifdef PLUGINS
//#         if (sd.History)
//#endif
//#             abilitiesList.addElement((String)"HISTORY_READER");
//#endif
//#ifdef IMPORT_EXPORT
//#ifdef PLUGINS
//#         if (sd.IE)
//#endif
//#             abilitiesList.addElement((String)"IMPORT_EXPORT");
//#endif
//#ifdef LAST_MESSAGES
//#ifdef PLUGINS
//#         if (sd.History)
//#endif
//#             abilitiesList.addElement((String)"LAST_MESSAGES");
//#endif
//#ifdef LOGROTATE
//#         abilitiesList.addElement((String)"LOGROTATE");
//#endif
//#ifdef MENU_LISTENER
        abilitiesList.addElement((String)"MENU_LISTENER");
//#endif
//#ifdef NICK_COLORS
        abilitiesList.addElement((String)"NICK_COLORS");
//#endif
//#ifdef NON_SASL_AUTH
//#         abilitiesList.addElement((String)"NON_SASL_AUTH");
//#endif
//#ifdef PEP
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             abilitiesList.addElement((String)"PEP");
//#endif
//#ifdef PEP_ACTfIVITY
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             //abilitiesList.addElement((String)"PEP_ACTIVITY");
//#endif
//#ifdef PEP
//#ifdef PLUGINS
//#         if (sd.PEP)
//#endif
//#             abilitiesList.addElement((String)"PEP_TUNE_ACTIVITY_MOOD");
//#endif
//#ifdef PLUGINS
//#         abilitiesList.addElement((String)"PLUGINS");
//#endif
//#ifdef POPUPS
        abilitiesList.addElement((String)"POPUPS");
//#endif
//#ifdef REQUEST_VOICE
//#         abilitiesList.addElement((String)"REQUEST_VOICE");
//#endif
//#ifdef PRIVACY
//#ifdef PLUGINS
//#         if (sd.Privacy)
//#endif
            abilitiesList.addElement((String)"PRIVACY");
//#endif
//#ifdef SASL_XGOOGLETOKEN
//#         abilitiesList.addElement((String)"SASL_XGOOGLETOKEN");
//#endif
//#ifdef SE_LIGHT
//#         abilitiesList.addElement((String)"SE_LIGHT");
//#endif
//#ifdef SERVICE_DISCOVERY
        abilitiesList.addElement((String)"SERVICE_DISCOVERY");
//#endif
//#ifdef SMILES
        abilitiesList.addElement((String)"SMILES");
//#endif
//#ifdef STATS
//#ifdef PLUGINS
//#         if (sd.Stats)
//#endif
//#             abilitiesList.addElement((String)"STATS");
//#endif
//#ifdef TEMPLATES
//#ifdef PLUGINS
//#         if (sd.Archive)
//#endif
//#         abilitiesList.addElement((String)"TEMPLATES");
//#endif
//#ifdef USER_KEYS
//#         abilitiesList.addElement((String)"USER_KEYS");
//#endif
//#ifdef USE_ROTATOR
        if(Config.getInstance().useLowMemory_userotator==false){    abilitiesList.addElement((String)"USE_ROTATOR");   }
//#endif
//#ifdef WMUC
//#         abilitiesList.addElement((String)"WMUC");
//#endif
//#ifdef WSYSTEMGC
//#         abilitiesList.addElement((String)"WSYSTEMGC");
//#endif
//#ifdef ZLIB
        abilitiesList.addElement((String)"ZLIB");
//#endif
        
        StringBuffer abilities=new StringBuffer();
        
	for (Enumeration ability=abilitiesList.elements(); ability.hasMoreElements(); ) {
            abilities.append((String)ability.nextElement());
            abilities.append(", ");
	}
        String ab=abilities.toString();
        abilities=null;
        abilitiesList=null;
        return ab.substring(0, ab.length()-2);
    }
}
