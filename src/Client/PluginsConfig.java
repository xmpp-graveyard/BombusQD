/*
 * PluginsConfig.java
 *
 * Created on 28.07.2009, 15:47
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

package Client;
import Colors.ColorSelector;
import javax.microedition.midlet.MIDlet;
import ui.controls.form.PluginBox;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import ui.controls.form.TrackItem;
import locale.SR;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Displayable;
import Menu.MenuListener;
import ui.GMenu;
import ui.GMenuConfig;
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
import util.StringLoader;
import java.util.Vector;
import images.SmilesIcons;
import ui.ImageList;
import java.util.*;
/**
 *
 * @author aqent
 */
public class PluginsConfig extends DefForm implements MenuListener
{
    private Display display;
    
    private static CheckBox showOfflineContacts;
    private static CheckBox selfContact;
    private static CheckBox showTransports;
    private static CheckBox ignore;
    private static CheckBox collapsedGroups;
    private static CheckBox autoFocus;
    private static CheckBox showResources;
    private static CheckBox useBoldFont;
    private static CheckBox rosterStatus;
//#ifdef CLIENTS_ICONS
    private static CheckBox showClientIcon;
//#endif
    private static CheckBox dont_loadMC;
    private static CheckBox gradient_cursor;    
    private static DropChoiceBox subscr;
    private static DropChoiceBox nil;
//#ifdef SMILES
    private static CheckBox smiles;
//#endif
    private static CheckBox eventComposing;
    private static CheckBox capsState;
    private static CheckBox storeConfPresence;
    private static CheckBox showCollapsedPresences;
    private static CheckBox timePresence;
    private static CheckBox autoScroll;
    private static CheckBox useTabs;
//#ifdef PEP
//#     private static CheckBox sndrcvmood;
//#     private static CheckBox rcvtune;
//#     private static CheckBox rcvactivity;
//#endif
//#ifdef RUNNING_MESSAGE
//#     private static CheckBox notifyWhenMessageType;
//#     private static CheckBox runningMessage;
//#endif
//#ifdef POPUPS
    private static CheckBox popUps;
//#endif
    private static CheckBox showBaloons;
    private static CheckBox animatedSmiles;
    private static CheckBox eventDelivery;
    private static CheckBox networkAnnotation;
    private static CheckBox executeByNum;
    private static CheckBox sendMoodInMsg;
    private static CheckBox savePos;
    private static CheckBox boldNicks;    
    private static CheckBox selectOutMessages;
    private static DropChoiceBox msgEditType;
//#ifdef DETRANSLIT
//#     private static CheckBox autoDetranslit;
//#endif
//#ifdef CLIPBOARD
//#     private static CheckBox useClipBoard;
//#endif
//#if LOGROTATE
//#     private static NumberInput messageCountLimit;
//#endif
    private static NumberInput messageLimit;
    private static CheckBox autoLogin;
    private static CheckBox useLowMemory_msgedit;
    private static CheckBox useLowMemory_userotator;  
  //private static CheckBox animateMenuAndRoster;
    private static CheckBox useLowMemory_iconmsgcollapsed;
    private static CheckBox iconsLeft;    
    private static CheckBox autoJoinConferences;
    private static NumberInput reconnectCount;
    private static NumberInput reconnectTime;
    private static CheckBox nokiaReconnectHack;
//#ifdef FILE_TRANSFER
    private static CheckBox fileTransfer;
//#endif
//#ifdef ADHOC
//#     private static CheckBox adhoc;
//#endif
    private static CheckBox fullscr;
    private static CheckBox memMon;
    private static CheckBox enableVersionOs;
    private static CheckBox queryExit;

    private static CheckBox popupFromMinimized;
    private static NumberInput fieldGmt; 
    private static NumberInput scrollWidth; 
    private static CheckBox drawScrollBgnd;    
    private static DropChoiceBox textWrap;
    private static DropChoiceBox langFiles;
    private static DropChoiceBox bgnd_image;    
//#ifdef AUTOSTATUS
//#     private static DropChoiceBox autoAwayType;
//#     private static NumberInput fieldAwayDelay; 
//#     private static CheckBox awayStatus;
//#endif
    private static DropChoiceBox panels;
    private static CheckBox drawMenuCommand;
    private static CheckBox showNickNames;
    private static CheckBox oldSE;
    
    private static CheckBox useClassicChat; 
    private static CheckBox use_phone_theme;
    private static NumberInput classic_chat_height;     
    private static NumberInput line_count;    
    
    private static Vector langs[];
    

    private static PluginBox contacts;    
    private static PluginBox messages;    
    private static PluginBox network;
    private static PluginBox graphics;    
    private static PluginBox app;
    private static PluginBox userKeys;    
    private static PluginBox autostatus;
    private static PluginBox classicchat;
    private static PluginBox debug;     
    private static PluginBox theme;  
    private static PluginBox cashe;  
    
    private static PluginBox history;
    private static PluginBox fonts; 
    private static PluginBox ie;  
    private static PluginBox notify; 
    private static PluginBox tasks;
    private static PluginBox avatars;      

    private final Config cf = midlet.BombusQD.cf;
         
    public PluginsConfig(Display display, Displayable pView) {
        super(display, pView, SR.MS_MODULES_CONFIG);
        this.display=display;
             
        
        graphics = new PluginBox(SR.MS_grStr, cf.module_graphics){ public void doAction(boolean st){ cf.module_graphics=st; } };
        itemsList.addElement(graphics);
        theme = new PluginBox(SR.MS_cthemesStr, cf.module_theme){ public void doAction(boolean st){ cf.module_theme=st; } };
        itemsList.addElement(theme);
        fonts = new PluginBox(SR.MS_fontsStr, cf.module_fonts){ public void doAction(boolean st){ cf.module_fonts=st; } };
        itemsList.addElement(fonts);        
        avatars = new PluginBox(SR.MS_avatarStr, cf.module_avatars){ public void doAction(boolean st){ cf.module_avatars=st; } };
        itemsList.addElement(avatars);         
        
        itemsList.addElement(new SpacerItem(3)); 
        
        app = new PluginBox(SR.MS_appStr, cf.module_app){ public void doAction(boolean st){ cf.module_app=st; } };
        itemsList.addElement(app);        
        notify = new PluginBox(SR.MS_notifyStr, cf.module_notify){ public void doAction(boolean st){ cf.module_notify=st; } };
        itemsList.addElement(notify);          
        contacts = new PluginBox(SR.MS_contactStr, cf.module_contacts){ public void doAction(boolean st){ cf.module_contacts=st; } };
        itemsList.addElement(contacts);
        messages = new PluginBox(SR.MS_msgStr, cf.module_messages){ public void doAction(boolean st){ cf.module_messages=st; } };
        itemsList.addElement(messages);
        network = new PluginBox(SR.MS_netStr, cf.module_network){ public void doAction(boolean st){ cf.module_network=st; } };
        itemsList.addElement(network);
        autostatus = new PluginBox(SR.MS_astatusStr, cf.module_autostatus){ public void doAction(boolean st){ cf.module_autostatus=st; } };
        itemsList.addElement(autostatus);        
        userKeys = new PluginBox(SR.MS_hotkeysStr, cf.userKeys){ public void doAction(boolean st){ cf.userKeys=st; } };
        itemsList.addElement(userKeys);     
        history = new PluginBox(SR.MS_historyStr, cf.module_history){ public void doAction(boolean st){ cf.module_history=st; } };
        itemsList.addElement(history);         

        itemsList.addElement(new SpacerItem(3));
        itemsList.addElement(new SimpleString(SR.MS_ADVANCED_OPT, true));
        itemsList.addElement(new SpacerItem(3));
        
        cashe = new PluginBox(SR.MS_casheStr, cf.module_cashe){ public void doAction(boolean st){ cf.module_cashe=st; } };
        itemsList.addElement(cashe); 
        ie = new PluginBox(SR.MS_ieStr, cf.module_ie){ public void doAction(boolean st){ cf.module_ie=st; } };
        itemsList.addElement(ie);  
        tasks = new PluginBox(SR.MS_taskstr, cf.module_tasks){ public void doAction(boolean st){ cf.module_tasks=st; } };
        itemsList.addElement(tasks); 
        classicchat = new PluginBox(SR.MS_clchatStr, cf.module_classicchat){ public void doAction(boolean st){ cf.module_classicchat=st; } };
        itemsList.addElement(classicchat); 

        debug = new PluginBox(SR.MS_DEBUG_MENU, cf.debug){ public void doAction(boolean st){ cf.debug=st; } };
        itemsList.addElement(debug);

        setCommandListener(this);
        attachDisplay(display);
        if(midlet.BombusQD.cashe.get().menu_PlaginsConfig==null && midlet.BombusQD.cf.module_cashe ){
          midlet.BombusQD.cashe.get().menu_PlaginsConfig=this;
        }
        this.parentView=pView;        
    }
    
    public void commandAction(Command command, Displayable displayable) {
        if(command==cmdOk){
            cmdOk();
        } else super.commandAction(command, displayable);
    }
    
    public String touchRightCommand(){ return SR.MS_BACK; }    
    public String touchLeftCommand(){ 
         try {
             String text=getFocusedObject().toString();
             if(text==SR.MS_contactStr){ return cf.module_contacts?SR.MS_config:""; }
             else if(text==SR.MS_msgStr){ return cf.module_messages?SR.MS_config:""; }
             else if(text==SR.MS_netStr){ return cf.module_network?SR.MS_config:""; } 
             else if(text==SR.MS_grStr){ return cf.module_graphics?SR.MS_config:""; }
             else if(text==SR.MS_appStr){ return cf.module_app?SR.MS_config:""; }
             else if(text==SR.MS_hotkeysStr){ return cf.userKeys?SR.MS_config:""; }
             else if(text==SR.MS_astatusStr){ return cf.module_autostatus?SR.MS_config:""; } 
             else if(text==SR.MS_clchatStr){ return cf.module_classicchat?SR.MS_config:""; }
             else if(text==SR.MS_DEBUG_MENU){ return ""; }             
             else if(text==SR.MS_cthemesStr){ return cf.module_theme?SR.MS_config:""; }
             
             else if(text==SR.MS_casheStr){ return cf.module_cashe?"Clear":""; }
             else if(text==SR.MS_historyStr){ return cf.module_history?SR.MS_config:""; }
             else if(text==SR.MS_fontsStr){ return cf.module_fonts?SR.MS_config:""; }
             else if(text==SR.MS_ieStr){ return cf.module_ie?SR.MS_config:""; } 
             else if(text==SR.MS_notifyStr){ return cf.module_notify?SR.MS_config:""; } 
             else if(text==SR.MS_taskstr){ return cf.module_tasks?SR.MS_config:""; }
             else if(text==SR.MS_avatarStr){ return cf.module_avatars?SR.MS_config:""; }
             
        } catch (Exception e) { } 
      return "";
    }
    
    public void cmdOk() {
        try {
          //String type = touchLeftCommand();
          String type=getFocusedObject().toString();
          if(touchLeftCommand()=="") return;
          if(type==SR.MS_hotkeysStr){
           display.setCurrent(new ui.keys.userKeysList(display));
          }
          else if(type==SR.MS_cthemesStr){
           display.setCurrent(new Colors.ColorConfigForm(display, this));
          }         
          else if(type==SR.MS_historyStr){
           display.setCurrent(new History.HistoryConfig(display, this));
          }
          else if(type==SR.MS_fontsStr){
           display.setCurrent(new Fonts.ConfigFonts(display, this));
          }
          else if(type==SR.MS_ieStr){
           display.setCurrent(new IE.IEMenu(display, this));
          }  
          else if(type==SR.MS_notifyStr){
           display.setCurrent(new Alerts.AlertCustomizeForm(display, this));
          }
          else if(type==SR.MS_taskstr){
           display.setCurrent(new AutoTasks.AutoTaskForm(display, this));
          }
          else if(type==SR.MS_avatarStr){
           display.setCurrent(new ConfigAvatar(display,this));
          }  
          else if(type==SR.MS_casheStr){
            if(cf.module_cashe){
                new ConfigModule(display, this, "Clear");
            }   
          }
          else {
           new ConfigModule(display, this, type);
          }          
        } catch (Exception e) { }
        
    }
    
    

    public void destroyView(){
       display.setCurrent(parentView);
       cf.saveToStorage();
    }
    
    
    

 ////Configure Module
 class ConfigModule extends DefForm implements MenuListener
 {
    private String type="";
    public ConfigModule(Display display, Displayable pView,String type) {
        super(display, pView, type);
        this.display=display;
        this.type=type;
         if(type==SR.MS_contactStr){//
          subscr=new DropChoiceBox(display, SR.MS_AUTH_NEW);
          subscr.append(SR.MS_SUBSCR_AUTO);
          subscr.append(SR.MS_SUBSCR_ASK);
          subscr.append(SR.MS_SUBSCR_DROP);
          subscr.append(SR.MS_SUBSCR_REJECT);
          subscr.setSelectedIndex(cf.autoSubscribe);
          itemsList.addElement(subscr);

          itemsList.addElement(new SpacerItem(2));
          nil=new DropChoiceBox(display, SR.MS_NOT_IN_LIST);
          nil.append(SR.MS_NIL_DROP_MP);
          nil.append(SR.MS_NIL_DROP_P);
          nil.append(SR.MS_NIL_ALLOW_ALL);
          nil.setSelectedIndex((cf.notInListDropLevel>NotInListFilter.ALLOW_ALL)? NotInListFilter.ALLOW_ALL: cf.notInListDropLevel);
          itemsList.addElement(nil);
          itemsList.addElement(new SpacerItem(2));
          
           showOfflineContacts = new CheckBox(SR.MS_OFFLINE_CONTACTS, cf.showOfflineContacts);
           itemsList.addElement(showOfflineContacts);
             selfContact = new CheckBox(SR.MS_SELF_CONTACT, cf.selfContact); 
             itemsList.addElement(selfContact);
               showTransports = new CheckBox(SR.MS_TRANSPORTS, cf.showTransports); 
               itemsList.addElement(showTransports);
                 showResources = new CheckBox(SR.MS_SHOW_RESOURCES, cf.showResources);
                 itemsList.addElement(showResources);
                   showClientIcon = new CheckBox(SR.MS_SHOW_CLIENTS_ICONS, cf.showClientIcon);
                   itemsList.addElement(showClientIcon);
                     iconsLeft = new CheckBox(SR.MS_CLIENT_ICONS_LEFT, cf.iconsLeft);
                     itemsList.addElement(iconsLeft);
                       autoFocus = new CheckBox(SR.MS_AUTOFOCUS, cf.autoFocus);
                       itemsList.addElement(autoFocus);
                       
                       rosterStatus = new CheckBox(SR.MS_SHOW_STATUSES, cf.rosterStatus);
                       itemsList.addElement(rosterStatus);
                       ignore = new CheckBox(SR.MS_IGNORE_LIST, cf.ignore);
                       itemsList.addElement(ignore);
         }
         else if(type==SR.MS_msgStr){//or chat
            msgEditType=new DropChoiceBox(display, SR.MS_MSG_EDIT_TYPE);
            msgEditType.append(SR.MS_MES_EDIT_OLD);//0
            msgEditType.append(SR.MS_MES_EDIT_ALT);//1
            msgEditType.append(SR.MS_MES_EDIT_ALT_GUI);//2                         
	    msgEditType.setSelectedIndex(cf.msgEditType);
            itemsList.addElement(msgEditType);
            itemsList.addElement(new SpacerItem(3));  
               runningMessage = new CheckBox(SR.MS_RUNNING_MESSAGE, cf.runningMessage);//ticker obj
               itemsList.addElement(runningMessage);                               
               notifyWhenMessageType = new CheckBox(SR.MS_NOTIFY_MSGEDIT, cf.notifyWhenMessageType); 
               itemsList.addElement(notifyWhenMessageType);                               
                  itemsList.addElement(new SpacerItem(3));
                  textWrap=new DropChoiceBox(display, SR.MS_TEXTWRAP);
                  textWrap.append(SR.MS_TEXTWRAP_CHARACTER);
                  textWrap.append(SR.MS_TEXTWRAP_WORD);
	          textWrap.setSelectedIndex(cf.textWrap);
                  itemsList.addElement(textWrap);
                  itemsList.addElement(new SpacerItem(3));
                      messageLimit=new NumberInput(display, SR.MS_MESSAGE_COLLAPSE_LIMIT, Integer.toString(cf.messageLimit), 200, 1000);
                      itemsList.addElement(messageLimit);
                      itemsList.addElement(new SpacerItem(3));
                  
           storeConfPresence = new CheckBox(SR.MS_STORE_PRESENCE, cf.storeConfPresence); 
           itemsList.addElement(storeConfPresence);
             showCollapsedPresences = new CheckBox(SR.MS_COLLAPSE_PRESENCE, cf.showCollapsedPresences); 
             itemsList.addElement(showCollapsedPresences);
           
           
             autoScroll = new CheckBox(SR.MS_AUTOSCROLL, cf.autoScroll);
             itemsList.addElement(autoScroll);
               timePresence = new CheckBox(SR.MS_SHOW_PRS_TIME, cf.timePresence);
               itemsList.addElement(timePresence);
                   autoDetranslit = new CheckBox(SR.MS_AUTODETRANSLIT, cf.autoDeTranslit); 
                   itemsList.addElement(autoDetranslit);
                     showNickNames = new CheckBox(SR.MS_SHOW_NACKNAMES, cf.showNickNames); 
                     itemsList.addElement(showNickNames);
                       savePos = new CheckBox(SR.MS_SAVE_CURSOR, cf.savePos);
                       itemsList.addElement(savePos); 
                        boldNicks = new CheckBox(SR.MS_BOLD_AND_COLORS_NICKS, cf.boldNicks); 
                         itemsList.addElement(boldNicks);
                          selectOutMessages = new CheckBox(SR.MS_SELECT_OUT_MESSAGES, cf.selectOutMessages); 
                          itemsList.addElement(selectOutMessages);                         

                              useLowMemory_iconmsgcollapsed = new CheckBox(SR.MS_ICON_COLP, cf.useLowMemory_iconmsgcollapsed);
                              itemsList.addElement(useLowMemory_iconmsgcollapsed);
                                smiles = new CheckBox(SR.MS_SMILES, cf.smiles);
                                itemsList.addElement(smiles);
                                  animatedSmiles = new CheckBox(SR.MS_ANI_SMILES, cf.animatedSmiles); 
                                  if(midlet.BombusQD.cf.ANIsmilesDetect) itemsList.addElement(animatedSmiles);                                
                                   capsState = new CheckBox(SR.MS_CAPS_STATE, cf.capsState); 
                                   itemsList.addElement(capsState);

                                        useTabs = new CheckBox(SR.MS_EMULATE_TABS, cf.useTabs); 
                                        itemsList.addElement(useTabs);
                                          useClipBoard = new CheckBox(SR.MS_CLIPBOARD, cf.useClipBoard); 
                                          itemsList.addElement(useClipBoard);
         }
         else if(type==SR.MS_netStr){//
//#ifdef PEP        
//#             itemsList.addElement(new SimpleString(SR.MS_PEP, true));
//#               sndrcvmood = new CheckBox(SR.MS_USERMOOD, cf.sndrcvmood);
//#               itemsList.addElement(sndrcvmood);
//#                 rcvtune = new CheckBox(SR.MS_USERTUNE, cf.rcvtune); 
//#                 itemsList.addElement(rcvtune);
//#                   rcvactivity = new CheckBox(SR.MS_USERACTIVITY, cf.rcvactivity);
//#                   itemsList.addElement(rcvactivity);
//#endif                  
                   itemsList.addElement(new SpacerItem(10));
                   itemsList.addElement(new SimpleString(SR.MS_MESSAGES, true));
                     eventComposing = new CheckBox(SR.MS_COMPOSING_EVENTS, cf.eventComposing); 
                     itemsList.addElement(eventComposing);
                         eventDelivery = new CheckBox(SR.MS_DELIVERY, cf.eventDelivery); 
                         itemsList.addElement(eventDelivery);
                           networkAnnotation = new CheckBox(SR.MS_CONTACT_ANNOTATIONS, cf.networkAnnotation);
                           itemsList.addElement(networkAnnotation);
                           //sendMoodInMsg = new CheckBox(SR.MS_MOOD_IN_MSG, cf.sendMoodInMsg);
                           //itemsList.addElement(sendMoodInMsg);
                          
                         itemsList.addElement(new SpacerItem(10));
                         itemsList.addElement(new SimpleString(SR.MS_RECONNECT, true));//сеть
        
	                 reconnectCount=new NumberInput(display, SR.MS_RECONNECT_COUNT_RETRY, Integer.toString(cf.reconnectCount), 0, 100);
                         itemsList.addElement(reconnectCount);
                         reconnectTime=new NumberInput(display, SR.MS_RECONNECT_WAIT, Integer.toString(cf.reconnectTime), 1, 60 ); 
                         itemsList.addElement(reconnectTime);
                         nokiaReconnectHack = new CheckBox(SR.MS_NOKIA_RECONNECT_HACK, cf.nokiaReconnectHack);
                         itemsList.addElement(nokiaReconnectHack);
            
                         fileTransfer = new CheckBox(SR.MS_FILE_TRANSFERS, cf.fileTransfer); 
                         itemsList.addElement(fileTransfer);   
                         adhoc = new CheckBox(SR.MS_ADHOC, cf.adhoc); 
                         itemsList.addElement(adhoc);

         } 
         else if(type==SR.MS_grStr){
          panels=new DropChoiceBox(display, SR.MS_PANELS);
          panels.append(SR.MS_NO_BAR+" : "+SR.MS_NO_BAR);
          panels.append(SR.MS_MAIN_BAR+" : "+SR.MS_NO_BAR);
          panels.append(SR.MS_MAIN_BAR+" : "+SR.MS_INFO_BAR);
          panels.append(SR.MS_NO_BAR+" : "+SR.MS_INFO_BAR);
          panels.append(SR.MS_INFO_BAR+" : "+SR.MS_NO_BAR);
          panels.append(SR.MS_INFO_BAR+" : "+SR.MS_MAIN_BAR);
          panels.append(SR.MS_NO_BAR+" : "+SR.MS_MAIN_BAR);
    	  panels.setSelectedIndex(cf.panelsState);
          itemsList.addElement(panels);
          
           itemsList.addElement(new SpacerItem(3));
           bgnd_image=new DropChoiceBox(display, "*"+SR.MS_TYPE_BACKGROUND);
           bgnd_image.append(SR.MS_BGND_NONE); //0
           bgnd_image.append(SR.MS_BGND_IMAGE);//1
           bgnd_image.append(SR.MS_BGND_GRADIENT);//2
           bgnd_image.append(SR.MS_MY_BGND_IMAGE);//3
           bgnd_image.setSelectedIndex(cf.bgnd_image);
           itemsList.addElement(bgnd_image);                     
           itemsList.addElement(new SpacerItem(3));                   
	         scrollWidth=new NumberInput(display, SR.MS_SCROLL_WIDTH, Integer.toString(cf.scrollWidth), 4, 20); 
                 itemsList.addElement(scrollWidth);   
                 itemsList.addElement(new SpacerItem(3));
                 
             useLowMemory_userotator = new CheckBox(SR.MS_ANIMATION, cf.useLowMemory_userotator);
             itemsList.addElement(useLowMemory_userotator);
              gradient_cursor  = new CheckBox(SR.MS_GRADIENT_CURSOR,cf.gradient_cursor); 
              itemsList.addElement(gradient_cursor);
               memMon = new CheckBox(SR.MS_HEAP_MONITOR, cf.memMonitor);
               itemsList.addElement(memMon);
                 drawScrollBgnd = new CheckBox(SR.MS_BGND_SCROLL,cf.drawScrollBgnd); 
                 itemsList.addElement(drawScrollBgnd);  
                   drawMenuCommand = new CheckBox(SR.MS_SHOW_TIME_TRAFFIC, cf.showTimeTraffic); 
                   itemsList.addElement(drawMenuCommand);
                       popUps = new CheckBox(SR.MS_POPUPS, cf.popUps); 
                       itemsList.addElement(popUps);
                         showBaloons = new CheckBox(SR.MS_SHOW_BALLONS, cf.showBalloons); 
                         itemsList.addElement(showBaloons);
         }
         else if(type==SR.MS_appStr){
           itemsList.addElement(new SimpleString(SR.MS_STARTUP_ACTIONS, true));
           autoLogin = new CheckBox(SR.MS_AUTOLOGIN, cf.autoLogin); 
           itemsList.addElement(autoLogin);
             autoJoinConferences = new CheckBox(SR.MS_AUTO_CONFERENCES, cf.autoJoinConferences); 
             itemsList.addElement(autoJoinConferences); 
               collapsedGroups = new CheckBox(SR.MS_COLLAPSED_GROUPS, cf.collapsedGroups);
               itemsList.addElement(collapsedGroups);
                 fullscr = new CheckBox(SR.MS_FULLSCREEN, cf.fullscreen); 
                 itemsList.addElement(fullscr);
                   enableVersionOs = new CheckBox(SR.MS_SHOW_HARDWARE, cf.enableVersionOs);
                   itemsList.addElement(enableVersionOs);
                      queryExit = new CheckBox(SR.MS_CONFIRM_EXIT, cf.queryExit); 
                      itemsList.addElement(queryExit);
                          oldSE = new CheckBox(SR.MS_KEYS_FOR_OLD_SE, cf.oldSE);
                          if (phoneManufacturer==cf.SONYE) itemsList.addElement(oldSE); 
                               if (cf.allowMinimize) {
                                   popupFromMinimized = new CheckBox(SR.MS_ENABLE_POPUP, cf.popupFromMinimized);
                                   itemsList.addElement(popupFromMinimized);
                               }
                               executeByNum = new CheckBox(SR.MS_EXECUTE_MENU_BY_NUMKEY, cf.executeByNum); 
                               itemsList.addElement(executeByNum);
                               
                               itemsList.addElement(new SpacerItem(10));
                               itemsList.addElement(new SimpleString(SR.MS_TIME_SETTINGS, true));
	                       fieldGmt=new NumberInput(display, SR.MS_GMT_OFFSET, Integer.toString(cf.gmtOffset), -12, 12); 
                               itemsList.addElement(fieldGmt);  

	                       langs=new StringLoader().stringLoader("/lang/res.txt",3);
                               if (langs[0].size()>1) {
                                   itemsList.addElement(new SpacerItem(10));
                                   langFiles=new DropChoiceBox(display, "*"+SR.MS_LANGUAGE);
                                   String tempLang=cf.lang;
                                   if (tempLang==null) { //not detected
                                       String locale=System.getProperty("microedition.locale");  
                                       if (locale!=null) {
                                           tempLang=locale.substring(0, 2).toLowerCase();
                                       }
                                   }

                                   for (int i=0; i<langs[0].size(); i++) {
                                       String label=(String) langs[2].elementAt(i);
                                       String langCode=(String) langs[0].elementAt(i);
                                       langFiles.append(label);
                                       if (tempLang.equals(langCode))
                                           langFiles.setSelectedIndex(i);
                                   }
                                   itemsList.addElement(langFiles);
                               }
         }
         else if(type==SR.MS_astatusStr){
           autoAwayType=new DropChoiceBox(display, SR.MS_AWAY_TYPE);
           autoAwayType.append(SR.MS_AWAY_OFF);
           autoAwayType.append(SR.MS_AWAY_LOCK);
           autoAwayType.append(SR.MS_MESSAGE_LOCK);
           autoAwayType.append(SR.MS_IDLE);
           autoAwayType.setSelectedIndex(cf.autoAwayType);
           itemsList.addElement(autoAwayType);
             fieldAwayDelay=new NumberInput(display, "*"+SR.MS_AWAY_PERIOD, Integer.toString(cf.autoAwayDelay), 1, 60);
             itemsList.addElement(fieldAwayDelay);
               awayStatus=new CheckBox(SR.MS_AUTOSTATUS_MESSAGE, cf.setAutoStatusMessage);
               itemsList.addElement(awayStatus);            

         } 
         else if(type==SR.MS_clchatStr){
           itemsList.addElement(new SimpleString(SR.MS_CLCHAT_ON, true));
             useClassicChat = new CheckBox(SR.MS_CLCHAT_ON, cf.useClassicChat);
             itemsList.addElement(useClassicChat);
               use_phone_theme = new CheckBox(SR.MS_CLCHAT_BGNG_PHONE, cf.use_phone_theme);
               itemsList.addElement(use_phone_theme);        
                 classic_chat_height=new NumberInput(display,SR.MS_CLCHAT_HEIGHT, Integer.toString(cf.classic_chat_height), 80, 320);
                 itemsList.addElement(classic_chat_height);
                   line_count=new NumberInput(display,SR.MS_CLCHAT_MSGLIMIT, Integer.toString(cf.line_count), 1, 1000);
                   itemsList.addElement(line_count);
                   itemsList.addElement(new SpacerItem(10));            
         } 
         else if(type=="Clear"){//?
            clearCache(true);         
         }
      setCommandListener(this);
      attachDisplay(display);    
      this.parentView=pView;
    }
    
    
    private void clearCache(boolean itemList){
            int size = StaticData.getInstance().roster.hContacts.size(); 
            System.gc();
            midlet.BombusQD.cashe.get().menu_PlaginsConfig=null;
            Contact c = null;
            int count =0;
             for(int i=0;i<size;i++){
                        c =(Contact)StaticData.getInstance().roster.hContacts.elementAt(i);
                        if(c.cList!=null){
                            count+=1;
                            c.cList=null;
                            System.gc();
                        }
             }
            if(itemList) {
                itemsList.addElement(new SimpleString("Cashe cleared.", true));
                itemsList.addElement(new SimpleString("Chats: "+Integer.toString(count), true));    
            }
    }
    
    
    public void cmdOk() {
         if(type==SR.MS_contactStr){
            cf.showOfflineContacts=showOfflineContacts.getValue();
            cf.selfContact=selfContact.getValue();
            cf.showTransports=showTransports.getValue();
            cf.showResources=showResources.getValue();
            cf.showClientIcon=showClientIcon.getValue();
            cf.iconsLeft=iconsLeft.getValue();
            cf.autoFocus=autoFocus.getValue();
            
            cf.autoSubscribe=subscr.getSelectedIndex();
            cf.notInListDropLevel=nil.getSelectedIndex();
            
            cf.rosterStatus=rosterStatus.getValue(); 
            cf.ignore=ignore.getValue();
            
         }
         else if(type==SR.MS_msgStr){
            cf.msgEditType=msgEditType.getSelectedIndex();
            cf.runningMessage=runningMessage.getValue();
            cf.notifyWhenMessageType=notifyWhenMessageType.getValue();
            cf.textWrap=textWrap.getSelectedIndex();                              
            cf.messageLimit=Integer.parseInt(messageLimit.getValue());
      
            cf.storeConfPresence=storeConfPresence.getValue();
            cf.showCollapsedPresences=showCollapsedPresences.getValue();
            cf.autoScroll=autoScroll.getValue();
            cf.timePresence=timePresence.getValue();
            cf.autoDeTranslit=autoDetranslit.getValue();
            cf.showNickNames=showNickNames.getValue();
            cf.savePos=savePos.getValue();
            cf.boldNicks=boldNicks.getValue();
            cf.selectOutMessages=selectOutMessages.getValue();
     
            cf.useLowMemory_iconmsgcollapsed=useLowMemory_iconmsgcollapsed.getValue();
            cf.smiles=smiles.getValue(); 
            if(midlet.BombusQD.cf.ANIsmilesDetect)
            {
             cf.animatedSmiles=animatedSmiles.getValue();
             if(!cf.smiles){
               SmilesIcons.stopTimer();
               clearCache(false);
             }
             if(!cf.animatedSmiles){
               SmilesIcons.stopTimer();
             }   
            }
            cf.capsState=capsState.getValue(); 

            cf.useTabs=useTabs.getValue();
            cf.useClipBoard=useClipBoard.getValue();
         }
         else if(type==SR.MS_netStr){
//#ifdef PEP             
//#             cf.sndrcvmood=sndrcvmood.getValue();
//#             cf.rcvtune=rcvtune.getValue();
//#             cf.rcvactivity=rcvactivity.getValue(); 
//#endif            
            cf.eventComposing=eventComposing.getValue();
            cf.eventDelivery=eventDelivery.getValue();
            cf.networkAnnotation=networkAnnotation.getValue();
            //cf.sendMoodInMsg=sendMoodInMsg.getValue();
            
            cf.reconnectCount=Integer.parseInt(reconnectCount.getValue());
            cf.reconnectTime=Integer.parseInt(reconnectTime.getValue());
            cf.nokiaReconnectHack=nokiaReconnectHack.getValue();  
            cf.fileTransfer=fileTransfer.getValue();
            cf.adhoc=adhoc.getValue(); 
         } 
         else if(type==SR.MS_grStr){
           cf.panelsState=panels.getSelectedIndex();             
           cf.bgnd_image=bgnd_image.getSelectedIndex();
           cf.scrollWidth=Integer.parseInt(scrollWidth.getValue());
           
           cf.useLowMemory_userotator=useLowMemory_userotator.getValue();
           cf.gradient_cursor=gradient_cursor.getValue();
           ui.VirtualList.memMonitor=cf.memMonitor=memMon.getValue();
           cf.drawScrollBgnd=drawScrollBgnd.getValue();      
           ui.VirtualList.changeOrient(cf.panelsState);   
           ui.VirtualList.showTimeTraffic=cf.showTimeTraffic=drawMenuCommand.getValue();

           
           cf.popUps=popUps.getValue();
           cf.showBalloons=showBaloons.getValue();
//#ifdef BACK_IMAGE
//#            try {
//#             if (/*img==null && */ cf.bgnd_image==1 /*|| cf.bgnd_image==2*/ ){
//#                 Image img=Image.createImage("/images/back.png");
//#                     gm.imgWidth = img.getWidth();
//#                     gm.imgHeight = img.getHeight();  
//#                     gm.img=img;
//#             }else if (cf.bgnd_image==3) {
//#                 Image bgnd=Image.createImage("/images/bgnd.png");
//#                 //ImageList il = new ImageList();
//#                 gm.bgnd=bgnd;
//#             }else if(cf.bgnd_image==0){
//#                 gm.img=null;
//#                 gm.bgnd=null;
//#             }
//#            } catch (Exception e) { }
//#endif   
         }
         else if(type==SR.MS_appStr){
            cf.autoLogin=autoLogin.getValue();
            cf.autoJoinConferences=autoJoinConferences.getValue();
            cf.collapsedGroups=collapsedGroups.getValue();
            
            ui.VirtualList.fullscreen=cf.fullscreen=fullscr.getValue();
            cf.enableVersionOs=enableVersionOs.getValue();
            cf.queryExit=queryExit.getValue();  
            if (phoneManufacturer==cf.SONYE) cf.oldSE=oldSE.getValue();
            if (cf.allowMinimize) cf.popupFromMinimized=popupFromMinimized.getValue();
            cf.executeByNum=executeByNum.getValue();
            cf.gmtOffset=Integer.parseInt(fieldGmt.getValue());
            if (langs[0].size()>1) {
              cf.lang=(String) langs[0].elementAt( langFiles.getSelectedIndex() );
            }        
         }
         else if(type==SR.MS_hotkeysStr){
 
         }
         else if(type==SR.MS_astatusStr){
            cf.autoAwayType=autoAwayType.getSelectedIndex();
            cf.autoAwayDelay=Integer.parseInt(fieldAwayDelay.getValue());
            cf.setAutoStatusMessage=awayStatus.getValue();
         } 
         else if(type==SR.MS_clchatStr){
            cf.useClassicChat=useClassicChat.getValue();
            cf.use_phone_theme=use_phone_theme.getValue();
            cf.classic_chat_height=Integer.parseInt(classic_chat_height.getValue());
            cf.line_count=Integer.parseInt(line_count.getValue()); 
         }          
      destroyView();
    }
    public void destroyView(){
        display.setCurrent(parentView);
    }    
 }
    
  
}
