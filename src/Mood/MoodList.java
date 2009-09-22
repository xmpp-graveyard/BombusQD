/*
 * MoodList.java
 * 
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
 *
 */

//#ifdef PEP
//# package Mood;
//# 
//# import javax.microedition.lcdui.TextField;
//# import ui.MIDPTextBox;
//# import ui.MainBar;
//# import Client.StaticData;
//# import com.alsutton.jabber.JabberDataBlock;
//# import com.alsutton.jabber.datablocks.Iq;
//# import java.util.Vector;
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
//# import ui.VirtualElement;
//# import ui.VirtualList;
//# import Client.Config;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
//# /**
//#  *
//#  * @author evgs,aqent
//#  */
//# public class MoodList extends VirtualList 
//#         
//#         implements 
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
//#         MenuListener,
//#endif     
//#         MIDPTextBox.TextBoxNotify {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PEP");
//#endif
//#     
//#     /** Creates a new instance of MoodList */
//#     Command cmdBack=new Command(SR.MS_BACK,Command.BACK,99);
//#     Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
//#     EventPublish ap = new EventPublish();
//#     
//#     Vector moods;
//#     public MoodList(Display display) {
//#         super(display);
//#         setMainBarItem(new MainBar(SR.MS_USERMOOD));
//#         addCommand(cmdBack);
//#         addCommand(cmdOk);
//#         setCommandListener(this);
//#         
//#         moods=new Vector(0);
//#         int count=Moods.getInstance().moodValue.size();
//#         
//#         for (int i=0; i<count; i++) {
//#             moods.addElement(new MoodItem(i));
//#         }
//#         commandState();
//#         sort(moods);
//#         moveCursorTo(Config.getInstance().cursorPos[3]);                
//#         attachDisplay(display);
//#     }
//# 
//#     protected int getItemCount() { return moods.size(); }
//# 
//#     protected VirtualElement getItemRef(int index) { return (VirtualElement)moods.elementAt(index); }
//# 
//#     public void commandState(){
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
//#     }
//#     
//#ifdef MENU_LISTENER
//#     public String touchLeftCommand(){ return SR.MS_SELECT; }
//#     
//#ifdef GRAPHICS_MENU        
//#     public int showGraphicsMenu() {
//#          commandState();
//#          new GMenu(display, parentView, this, null, menuCommands);
//#          GMenuConfig.getInstance().itemGrMenu = -1;        
//#          eventOk();
//#          return -1;
//#     }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.MS_DISCO, null, menuCommands);
//#     } 
//#endif
//# 
//#endif        
//#     
//#     public void eventOk() {
//#         if (cursor==0) OkNotify(null); 
//#         else {
//#             Config.getInstance().cursorPos[3]=cursor;            
//#             new MIDPTextBox(display, SR.MS_USERMOOD, Moods.getInstance().myMoodText, this, TextField.ANY);
//#         }
//#     }
//#     public void OkNotify(String moodText) {
//#         String moodName=((MoodItem)getFocusedObject()).getTipString();
//#         publishMood(moodText, moodName);
//#         destroyView();
//#         display.setCurrent(StaticData.getInstance().roster);
//#     }
//# 
//#     public void publishMood(final String moodText, final String moodName) {
//#                 Config.getInstance().moodName=moodName;
//#                 Config.getInstance().moodText=moodText;
//#                 
//#                 //VirtualList.setWobble(1, null, "Success Mood!\n"+locale.SR.MS_USERMOOD+": "+moodName+"("+moodText+")");        
//# 
//#         String sid="publish-mood";
//#         JabberDataBlock setMood=new Iq(null, Iq.TYPE_SET, sid);
//#         JabberDataBlock action=setMood.addChildNs("pubsub", "http://jabber.org/protocol/pubsub") .addChild( (moodText!=null)?"publish":"retract", null);
//#         action.setAttribute("node", "http://jabber.org/protocol/mood");
//#         JabberDataBlock item=action.addChild("item", null);
//#         item.setAttribute("id", Moods.getInstance().myMoodId);
//# 
//#         if (moodText!=null) {
//#             JabberDataBlock mood=item.addChildNs("mood", "http://jabber.org/protocol/mood");
//#          
//#             mood.addChild(moodName, null);
//#             mood.addChild("text",moodText);
//#         } else {
//#             item.addChild("retract", null);
//#             action.setAttribute("notify","1");
//#         }
//#         try {
//#             StaticData.getInstance().roster.theStream.addBlockListener(new MoodPublishResult(display, sid));           
//#             StaticData.getInstance().roster.theStream.send(setMood);
//#             setMood=null;
//#             action=null;
//#             item=null;
//#         } catch (Exception e) {e.printStackTrace(); }
//#        Config.getInstance().saveToStorage();
//#    }     
//#     
//#     
//#ifdef MENU_LISTENER    
//#     public void userKeyPressed(int keyCode){
//#      switch (keyCode) {
//#         case KEY_NUM4:
//#             pageLeft();
//#             break; 
//#         case KEY_NUM6:
//#             pageRight();
//#             break;  
//#      }
//#     }
//#endif      
//# 
//#    public void commandAction(Command command, Displayable displayable) {
//#         if (command==cmdBack) destroyView();
//#         else eventOk();
//#    }    
//# }
//#endif