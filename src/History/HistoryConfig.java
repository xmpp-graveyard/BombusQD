/*
 * HistoryConfig.java 
 *
 * Created on 20.05.2008, 19:43
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
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
//#ifdef HISTORY
//# package History;
//# 
//# import Client.Config;
//# import io.file.browse.Browser;
//# import io.file.browse.BrowserListener;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
//# import Menu.MenuListener;
//# import Menu.Command;
//# import Menu.MyMenu;
//#endif
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import javax.microedition.lcdui.TextField;
//# import locale.SR;
//# import ui.controls.form.CheckBox;
//# import ui.controls.form.DefForm;
//# import ui.controls.form.TextInput;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif   
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# public class HistoryConfig 
//#         extends DefForm
//#         implements BrowserListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_HISTORY");
//#endif
//#     Command cmdOk=new Command(SR.MS_OK, Command.SCREEN, 2);
//#     Command cmdPath=new Command(SR.MS_SELECT_HISTORY_FOLDER, Command.SCREEN, 1);
//#     Command cmdOkey=new Command(SR.MS_OK, Command.SCREEN, 3);
//#     
//#     private TextInput historyFolder;
//#     
//#     private CheckBox loadHistory;
//#     private CheckBox saveHistory;
//#     private CheckBox savePres;
//#     private CheckBox saveConfHistory;
//#     private CheckBox saveConfPres;
//#     private CheckBox win1251;
//#ifdef DETRANSLIT
//#     private CheckBox translit;
//#endif
//#     
//#     Config cf;
//# 
//#     /** Creates a new instance of HistoryConfig */
//#     public HistoryConfig(Display display, Displayable pView) {
//#         super(display, pView, SR.MS_HISTORY_OPTIONS);
//# 
//#         cf=Config.getInstance();
//#ifdef LAST_MESSAGES
//#         loadHistory = new CheckBox(SR.MS_LAST_MESSAGES, cf.lastMessages); itemsList.addElement(loadHistory);
//#elifdef HISTORY_READER
//#         loadHistory = new CheckBox("Format for reading", cf.lastMessages); itemsList.addElement(loadHistory); 
//#endif
//#         saveHistory = new CheckBox(SR.MS_SAVE_HISTORY, cf.msgLog); itemsList.addElement(saveHistory); 
//#         savePres = new CheckBox(SR.MS_SAVE_PRESENCES, cf.msgLogPresence); itemsList.addElement(savePres); 
//#         saveConfHistory = new CheckBox(SR.MS_SAVE_HISTORY_CONF, cf.msgLogConf); itemsList.addElement(saveConfHistory); 
//#         saveConfPres = new CheckBox(SR.MS_SAVE_PRESENCES_CONF, cf.msgLogConfPresence); itemsList.addElement(saveConfPres); 
//#         win1251 = new CheckBox(SR.MS_1251_CORRECTION, cf.cp1251); itemsList.addElement(win1251);
//#ifdef DETRANSLIT
//#         translit = new CheckBox(SR.MS_1251_TRANSLITERATE_FILENAMES, cf.transliterateFilenames); itemsList.addElement(translit);
//#endif
//# 
//# 	historyFolder = new TextInput(display, SR.MS_HISTORY_FOLDER, cf.msgPath, null, TextField.ANY); itemsList.addElement(historyFolder); 
//# 
//#         addCommand(cmdPath); cmdPath.setImg(0x34);
//#         addCommand(cmdOkey);  cmdOkey.setImg(0x43);
//#         commandState();
//#         
//#         moveCursorTo(0);
//# 
//#         attachDisplay(display);
//#         this.parentView=pView;
//#     }
//# 
//#     public void BrowserFilePathNotify(String pathSelected) {
//#         historyFolder.setValue(pathSelected);
//#     }
//# 
//#     public void commandAction(Command command, Displayable displayable) {
//#         if (command==cmdPath) {
//#             new Browser(null, display, this, this, true);
//#             return;
//#         }
//#         if (command==cmdOkey) {
//#             cmdOk();
//#         }        
//#         super.commandAction(command, displayable);
//#         destroyView();
//#     }
//# 
//#     public void cmdOk() {
//#ifdef LAST_MESSAGES
//#         cf.lastMessages=loadHistory.getValue();
//#elifdef HISTORY_READER
//#         cf.lastMessages=loadHistory.getValue();
//#endif
//#         cf.msgLog=saveHistory.getValue();
//#         cf.msgLogPresence=savePres.getValue();
//#         cf.msgLogConf=saveConfHistory.getValue();
//#         cf.msgLogConfPresence=saveConfPres.getValue();
//#         cf.cp1251=win1251.getValue();
//#ifdef DETRANSLIT
//#         cf.transliterateFilenames=translit.getValue();
//#endif
//#         cf.msgPath=historyFolder.getValue();         
//# 
//#         cf.saveToStorage();
//#     }
//# 
//#     
//#     public void commandState(){
//#ifdef MENU_LISTENER        
//#         menuCommands.removeAllElements();
//#endif        
//#ifdef GRAPHICS_MENU               
//#         //super.commandState();
//#else
//#     super.commandState(); 
//#endif
//#         removeCommand(cmdCancel);
//#         addCommand(cmdPath);
//#         addCommand(cmdOkey);
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdCancel);
//#endif     
//#     }
//#     
//#     
//#ifdef MENU_LISTENER
//#     public String touchLeftCommand(){ return SR.MS_MENU; }
//#     
//#ifdef GRAPHICS_MENU  
//#     public void touchLeftPressed(){
//#         showGraphicsMenu();
//#     }
//#     public int showGraphicsMenu() {
//#         commandState();
//#         new GMenu(display, parentView, this, null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.HISTORY_CONFIG;
//#         redraw();
//#         return GMenu.HISTORY_CONFIG;
//#     }
//#     
//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//#     
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.MS_HISTORY_OPTIONS, null, menuCommands);
//#    }  
//#endif      
//#     
//# 
//#endif         
//# }
//#endif
