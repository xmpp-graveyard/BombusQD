/*
 * StatsWindow.java
 *
 * Created on 03.10.2008, 19:42
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
//#ifdef STATS
//# package Statistic;
//# 
//# import Client.Config;
//# import Client.StaticData;
//# import Info.Version;
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
//# import locale.SR;
//# import ui.controls.form.DefForm;
//# import ui.controls.form.MultiLine;
//# import util.ClipBoard;
//# import util.StringUtils;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# public class StatsWindow
//#         extends DefForm {
//# 
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_STATS");
//#endif
//#     
//#     Stats st=Stats.getInstance();
//#     
//#     public Command cmdClear = new Command(SR.MS_CLEAR, Command.SCREEN, 2);
//#     public Command cmdSave = new Command(SR.MS_SAVE, Command.OK, 3);   
//#     
//#ifdef CLIPBOARD
//#ifndef MENU
//#     public Command cmdCopy = new Command(SR.MS_COPY, Command.OK, 1);
//#endif
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//#endif
//#     
//#     MultiLine item=null;
//#     
//#     /**
//#      * Creates a new instance of StatsWindow
//#      */
//#     public StatsWindow(Display display, Displayable pView) {
//#         super(display, pView, SR.MS_STATS);
//#         this.display=display;
//# 
//#         item=new MultiLine(SR.MS_ALL, StringUtils.getSizeString(st.getAllTraffic()), super.superWidth); item.selectable=true; itemsList.addElement(item);
//# 
//#         item=new MultiLine(SR.MS_PREVIOUS_, StringUtils.getSizeString(st.getLatest()), super.superWidth); item.selectable=true; itemsList.addElement(item);
//#         
//#         item=new MultiLine(SR.MS_CURRENT, StringUtils.getSizeString(st.getCurrentTraffic()), super.superWidth); item.selectable=true; itemsList.addElement(item);
//#         
//#         if (StaticData.getInstance().roster.isLoggedIn()) {
//#             item=new MultiLine(SR.MS_COMPRESSION, StaticData.getInstance().roster.theStream.getStreamStats(), super.superWidth); item.selectable=true; itemsList.addElement(item);
//#         }
//# 
//#         if (StaticData.getInstance().roster.isLoggedIn()) {
//#             item=new MultiLine(SR.MS_CONNECTED, StaticData.getInstance().roster.theStream.getConnectionData(), super.superWidth); item.selectable=true; itemsList.addElement(item);
//#         }
//#         
//#         item=new MultiLine(SR.MS_CONN, Integer.toString(st.getSessionsCount()), super.superWidth); item.selectable=true; itemsList.addElement(item);
//#                 
//#         item=new MultiLine(SR.MS_STARTED, StaticData.getInstance().roster.startTime, super.superWidth); item.selectable=true; itemsList.addElement(item);
//#  
//#         commandState();
//# 
//#         attachDisplay(display);
//#         this.parentView=pView;
//#     }
//# 
//#     
//#ifdef CLIPBOARD
//#     public void cmdCopy(){
//#         StringBuffer copy=new StringBuffer();
//#         for (int i=0;i<itemsList.size();i++) {
//#             copy.append(((MultiLine)itemsList.elementAt(i)).toString()+"\n");
//#         }
//#         clipboard.setClipBoard(copy.toString());
//#         destroyView();
//#     }
//#endif
//# 
//#  
//#     public void commandAction(Command command, Displayable displayable) {
//#ifdef CLIPBOARD
//# 	if (command==cmdCopy) {
//# 	    cmdCopy();
//# 	} else
//#endif
//#         if(command==cmdSave){
//#             Stats.getInstance().saveToStorage(false);
//#         }
//#         if (command==cmdClear) {
//#             st.saveToStorage(true);
//#             cmdCancel();
//#         } else super.commandAction(command, displayable);
//#     }
//#     
//#     
//#     public void commandState(){
//#ifdef MENU_LISTENER        
//#         menuCommands.removeAllElements();
//#endif    
//#         
//#ifdef GRAPHICS_MENU               
//#         //super.commandState();
//#else
//#     super.commandState(); 
//#endif
//#         removeCommand(cmdCancel);
//#         removeCommand(cmdOk);
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             addCommand(cmdCopy); cmdCopy.setImg(0x13);
//#         }
//#endif
//#         addCommand(cmdClear); cmdClear.setImg(0x13);
//#         addCommand(cmdSave); cmdSave.setImg(0x44);//SAVE
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdCancel);
//#endif     
//#     }
//#     
//#     
//#ifdef MENU_LISTENER
//#     public String touchLeftCommand(){ return SR.MS_MENU; }
//#     
//#     
//#ifdef GRAPHICS_MENU       
//#     public void touchLeftPressed(){
//#         showGraphicsMenu();
//#     }
//#     public int showGraphicsMenu() {
//#         commandState();
//#         new GMenu(display, parentView, this, null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.STATS_WINDOW;
//#         redraw();
//#         return GMenu.STATS_WINDOW;
//#     }
//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.MS_STATS, null, menuCommands);
//#    }  
//#endif    
//# 
//#endif         
//#     
//#     
//# }
//#endif         