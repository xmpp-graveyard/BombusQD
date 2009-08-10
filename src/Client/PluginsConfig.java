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
import net.jscience.math.MathFP;
/**
 *
 * @author aqent
 */
public class PluginsConfig extends DefForm implements MenuListener
{
    private Display display;
    
    private CheckBox showOfflineContacts;
    private CheckBox selfContact;
    private CheckBox showTransports;
    private CheckBox ignore;
    private CheckBox collapsedGroups;
    private CheckBox autoFocus;
    private CheckBox showResources;
    private CheckBox useBoldFont;
    private CheckBox rosterStatus;
//#ifdef CLIENTS_ICONS
    private CheckBox showClientIcon;
//#endif
    private CheckBox dont_loadMC;
    private CheckBox gradient_cursor;    
    private DropChoiceBox subscr;
    private DropChoiceBox nil;
//#ifdef SMILES
    private CheckBox smiles;
//#endif
    private CheckBox eventComposing;
    private CheckBox capsState;
    private CheckBox storeConfPresence;
    private CheckBox timePresence;
    private CheckBox autoScroll;
    private CheckBox useTabs;
//#ifdef PEP
//#     private CheckBox sndrcvmood;
//#     private CheckBox rcvtune;
//#     private CheckBox rcvactivity;
//#endif
//#ifdef RUNNING_MESSAGE
//#     private CheckBox notifyWhenMessageType;
//#endif
//#ifdef POPUPS
    private CheckBox popUps;
//#endif
    private CheckBox showBaloons;     
    private CheckBox eventDelivery;
    private CheckBox executeByNum;
    private CheckBox sendMoodInMsg;
    private CheckBox savePos;
    private CheckBox boldNicks;    
//#ifdef DETRANSLIT
//#     private CheckBox autoDetranslit;
//#endif
//#ifdef CLIPBOARD
//#     private CheckBox useClipBoard;
//#endif
//#if LOGROTATE
//#     private NumberInput messageCountLimit;
//#endif
    private NumberInput messageLimit;
    private CheckBox autoLogin;
    private CheckBox useLowMemory_msgedit;
    private CheckBox useLowMemory_userotator;  
  //private CheckBox animateMenuAndRoster;
    private CheckBox useLowMemory_iconmsgcollapsed;
    private CheckBox iconsLeft;    
    private CheckBox autoJoinConferences;
    private NumberInput reconnectCount;
    private NumberInput reconnectTime;
    private CheckBox nokiaReconnectHack;
//#ifdef FILE_TRANSFER
    private CheckBox fileTransfer;
//#endif
//#ifdef ADHOC
//#     private CheckBox adhoc;
//#endif
    private CheckBox fullscr;
    private CheckBox memMon;
    private CheckBox enableVersionOs;
    private CheckBox queryExit;

    private CheckBox lightState;
    private CheckBox popupFromMinimized;
    private NumberInput fieldGmt; 
    private NumberInput scrollWidth; 
    private CheckBox drawScrollBgnd;    
    private DropChoiceBox textWrap;
    private DropChoiceBox sblockFont;    
    private DropChoiceBox langFiles;
    private DropChoiceBox bgnd_image;    
//#ifdef AUTOSTATUS
//#     private DropChoiceBox autoAwayType;
//#     private NumberInput fieldAwayDelay; 
//#     private CheckBox awayStatus;
//#endif
    private DropChoiceBox panels;
    private CheckBox drawMenuCommand;
    private CheckBox showNickNames;
    private CheckBox oldSE;
    
    private CheckBox useClassicChat; 
    private CheckBox use_phone_theme;
    private NumberInput classic_chat_height;     
    private NumberInput line_count;    
    
    private Vector langs[];
    

    private PluginBox contacts;    
    private PluginBox messages;    
    private PluginBox network;
    private PluginBox graphics;    
    private PluginBox app;
    private PluginBox userKeys;    
    private PluginBox autostatus;
    private PluginBox classicchat; 
    private PluginBox theme;  
    private PluginBox cashe;  
    
    private PluginBox history;
    private PluginBox fonts; 
    private PluginBox ie;  
    private PluginBox notify; 
    private PluginBox tasks;
    private PluginBox avatars;      

    private final Config cf = midlet.BombusQD.cf;
         
    public PluginsConfig(Display display, Displayable pView) {
        super(display, pView, SR.MS_MODULES_CONFIG);
        this.display=display;
        
        contacts = new PluginBox(SR.MS_contactStr, cf.module_contacts){ public void doAction(boolean st){ cf.module_contacts=st; } };
        itemsList.addElement(contacts);
        messages = new PluginBox(SR.MS_msgStr, cf.module_messages){ public void doAction(boolean st){ cf.module_messages=st; } };
        itemsList.addElement(messages);
        network = new PluginBox(SR.MS_netStr, cf.module_network){ public void doAction(boolean st){ cf.module_network=st; } };
        itemsList.addElement(network);
        graphics = new PluginBox(SR.MS_grStr, cf.module_graphics){ public void doAction(boolean st){ cf.module_graphics=st; } };
        itemsList.addElement(graphics);
        app = new PluginBox(SR.MS_appStr, cf.module_app){ public void doAction(boolean st){ cf.module_app=st; } };
        itemsList.addElement(app);
        userKeys = new PluginBox(SR.MS_hotkeysStr, cf.userKeys){ public void doAction(boolean st){ cf.userKeys=st; } };
        itemsList.addElement(userKeys);    
        autostatus = new PluginBox(SR.MS_astatusStr, cf.module_autostatus){ public void doAction(boolean st){ cf.module_autostatus=st; } };
        itemsList.addElement(autostatus);
        theme = new PluginBox(SR.MS_cthemesStr, cf.module_theme){ public void doAction(boolean st){ cf.module_theme=st; } };
        itemsList.addElement(theme);    

        history = new PluginBox(SR.MS_historyStr, cf.module_history){ public void doAction(boolean st){ cf.module_history=st; } };
        itemsList.addElement(history);    
        fonts = new PluginBox(SR.MS_fontsStr, cf.module_fonts){ public void doAction(boolean st){ cf.module_fonts=st; } };
        itemsList.addElement(fonts);
        notify = new PluginBox(SR.MS_notifyStr, cf.module_notify){ public void doAction(boolean st){ cf.module_notify=st; } };
        itemsList.addElement(notify);      
        avatars = new PluginBox(SR.MS_avatarStr, cf.module_avatars){ public void doAction(boolean st){ cf.module_avatars=st; } };
        itemsList.addElement(avatars);  
        
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
             else if(text==SR.MS_cthemesStr){ return cf.module_theme?SR.MS_config:""; }
             
             else if(text==SR.MS_casheStr){ return cf.module_cashe?SR.MS_config:""; }
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
        long s1 = System.currentTimeMillis();        
        cf.updateTime();
        cf.saveToStorage();//save and exit
        long s2 = System.currentTimeMillis();
        System.out.println((s2-s1)+" msec");
        display.setCurrent(parentView);        
        //62 - new
        //100msec - old
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
                     
                       itemsList.addElement(new SpacerItem(10));
                       subscr=new DropChoiceBox(display, SR.MS_AUTH_NEW);
                       subscr.append(SR.MS_SUBSCR_AUTO);
                       subscr.append(SR.MS_SUBSCR_ASK);
                       subscr.append(SR.MS_SUBSCR_DROP);
                       subscr.append(SR.MS_SUBSCR_REJECT);
                       subscr.setSelectedIndex(cf.autoSubscribe);
                       itemsList.addElement(subscr);

                       itemsList.addElement(new SpacerItem(10));
                       nil=new DropChoiceBox(display, SR.MS_NOT_IN_LIST);
                       nil.append(SR.MS_NIL_DROP_MP);
                       nil.append(SR.MS_NIL_DROP_P);
                       nil.append(SR.MS_NIL_ALLOW_ALL);
                       nil.setSelectedIndex((cf.notInListDropLevel>NotInListFilter.ALLOW_ALL)? NotInListFilter.ALLOW_ALL: cf.notInListDropLevel);
                       itemsList.addElement(nil);
            
                       rosterStatus = new CheckBox(SR.MS_SHOW_STATUSES, cf.rosterStatus);
                       itemsList.addElement(rosterStatus);
                       ignore = new CheckBox(SR.MS_IGNORE_LIST, cf.ignore);
                       itemsList.addElement(ignore);
         }
         else if(type==SR.MS_msgStr){//or chat
           storeConfPresence = new CheckBox(SR.MS_STORE_PRESENCE, cf.storeConfPresence); 
           itemsList.addElement(storeConfPresence);
             autoScroll = new CheckBox(SR.MS_AUTOSCROLL, cf.autoScroll);
             itemsList.addElement(autoScroll);
               timePresence = new CheckBox(SR.MS_SHOW_PRS_TIME, cf.timePresence);
               itemsList.addElement(timePresence);
                 notifyWhenMessageType = new CheckBox(SR.MS_RUNNING_MESSAGE, cf.notifyWhenMessageType); 
                 itemsList.addElement(notifyWhenMessageType);
                   autoDetranslit = new CheckBox(SR.MS_AUTODETRANSLIT, cf.autoDeTranslit); 
                   itemsList.addElement(autoDetranslit);
                     showNickNames = new CheckBox(SR.MS_SHOW_NACKNAMES, cf.showNickNames); 
                     itemsList.addElement(showNickNames);
                       savePos = new CheckBox(SR.MS_SAVE_CURSOR, cf.savePos);
                       itemsList.addElement(savePos); 
                        boldNicks = new CheckBox(SR.MS_BOLD_AND_COLORS_NICKS, cf.boldNicks); 
                         itemsList.addElement(boldNicks);
                            messageLimit=new NumberInput(display, SR.MS_MESSAGE_COLLAPSE_LIMIT, Integer.toString(cf.messageLimit), 200, 1000);
                            itemsList.addElement(messageLimit);
                              useLowMemory_iconmsgcollapsed = new CheckBox(SR.MS_ICON_COLP, cf.useLowMemory_iconmsgcollapsed);
                              itemsList.addElement(useLowMemory_iconmsgcollapsed);
                                smiles = new CheckBox(SR.MS_SMILES, cf.smiles);
                                itemsList.addElement(smiles);
                                   capsState = new CheckBox(SR.MS_CAPS_STATE, cf.capsState); 
                                   itemsList.addElement(capsState);
                                     itemsList.addElement(new SpacerItem(10));
                                     textWrap=new DropChoiceBox(display, SR.MS_TEXTWRAP);
                                     textWrap.append(SR.MS_TEXTWRAP_CHARACTER);
                                     textWrap.append(SR.MS_TEXTWRAP_WORD);
	                             textWrap.setSelectedIndex(cf.textWrap);
                                     itemsList.addElement(textWrap);
                                     
                                      sblockFont=new DropChoiceBox(display, SR.MS_sblockFont);
                                      sblockFont.append(SR.MS_sblock_bs);//0
                                      sblockFont.append(SR.MS_sblock_bm);
                                      sblockFont.append(SR.MS_sblock_bl);
                                      sblockFont.append(SR.MS_sblock_ibs);
                                      sblockFont.append(SR.MS_sblock_ibm);
                                      sblockFont.append(SR.MS_sblock_ibl);
                                      sblockFont.append(SR.MS_sblock_no);
	                              sblockFont.setSelectedIndex(cf.sblockFont);
                                      itemsList.addElement(sblockFont);                                     

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
                           sendMoodInMsg = new CheckBox(SR.MS_MOOD_IN_MSG, cf.sendMoodInMsg);
                           itemsList.addElement(sendMoodInMsg);
                          
                         itemsList.addElement(new SpacerItem(10));
                         itemsList.addElement(new SimpleString(SR.MS_RECONNECT, true));//сеть
        
	                 reconnectCount=new NumberInput(display, SR.MS_RECONNECT_COUNT_RETRY, Integer.toString(cf.reconnectCount), 0, 100);
                         itemsList.addElement(reconnectCount);
                         reconnectTime=new NumberInput(display, SR.MS_RECONNECT_WAIT, Integer.toString(cf.reconnectTime), 1, 60 ); 
                         itemsList.addElement(reconnectTime);
                         nokiaReconnectHack = new CheckBox("Nokia Reconnect Hack" +
                                 "%Solves the reconnection problem on Nokia smartphones", cf.nokiaReconnectHack);
                         itemsList.addElement(nokiaReconnectHack);
            
                         fileTransfer = new CheckBox(SR.MS_FILE_TRANSFERS, cf.fileTransfer); 
                         itemsList.addElement(fileTransfer);   
                         adhoc = new CheckBox(SR.MS_ADHOC, cf.adhoc); 
                         itemsList.addElement(adhoc);

         } 
         else if(type==SR.MS_grStr){
             useLowMemory_userotator = new CheckBox(SR.MS_ANIMATION, cf.useLowMemory_userotator);
             itemsList.addElement(useLowMemory_userotator);
              gradient_cursor  = new CheckBox(SR.MS_GRADIENT_CURSOR,cf.gradient_cursor); 
              itemsList.addElement(gradient_cursor);
               memMon = new CheckBox(SR.MS_HEAP_MONITOR, cf.memMonitor);
               itemsList.addElement(memMon);
                 itemsList.addElement(new SpacerItem(5));
                 itemsList.addElement(new SimpleString(SR.MS_SCROLL_OPTIONS, true));
	         scrollWidth=new NumberInput(display, SR.MS_SCROLL_WIDTH, Integer.toString(cf.scrollWidth), 4, 20); 
                 itemsList.addElement(scrollWidth);
                 //itemsList.addElement(new SimpleString("Scroll Options", true));
                 drawScrollBgnd = new CheckBox(SR.MS_BGND_SCROLL,cf.drawScrollBgnd); 
                 itemsList.addElement(drawScrollBgnd);  
                   itemsList.addElement(new SpacerItem(5));
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
                   drawMenuCommand = new CheckBox(SR.MS_SHOW_TIME_TRAFFIC, cf.showTimeTraffic); 
                   itemsList.addElement(drawMenuCommand);
                   itemsList.addElement(new SpacerItem(5));
                     bgnd_image=new DropChoiceBox(display, "*"+SR.MS_TYPE_BACKGROUND);
                     bgnd_image.append(SR.MS_BGND_NONE); //0
                     bgnd_image.append(SR.MS_BGND_IMAGE);//1
                     bgnd_image.append(SR.MS_BGND_GRADIENT);//2
                     bgnd_image.append(SR.MS_MY_BGND_IMAGE);//3
                     bgnd_image.setSelectedIndex(cf.bgnd_image);
                     itemsList.addElement(bgnd_image);  
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
                             lightState = new CheckBox(SR.MS_FLASHLIGHT, cf.lightState);
                             if (phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2 
                             || phoneManufacturer==Config.SONYE || phoneManufacturer==Config.NOKIA) itemsList.addElement(lightState);  
                               if (cf.allowMinimize) {
                                   popupFromMinimized = new CheckBox(SR.MS_ENABLE_POPUP, cf.popupFromMinimized);
                                   itemsList.addElement(popupFromMinimized);
                               }
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
                               executeByNum = new CheckBox(SR.MS_EXECUTE_MENU_BY_NUMKEY, cf.executeByNum); 
                               itemsList.addElement(executeByNum);
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
            int size = StaticData.getInstance().roster.hContacts.size(); 
            System.gc();
            midlet.BombusQD.cashe.get().menu_PlaginsConfig=null;            
            long free = Runtime.getRuntime().freeMemory()>>10;
            long total = Runtime.getRuntime().totalMemory()>>10;
            itemsList.addElement(new SimpleString("Undo:"+Long.toString(free)+"/"+Long.toString(total), true));
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

            long free_ = Runtime.getRuntime().freeMemory()>>10;
            long total_ = Runtime.getRuntime().totalMemory()>>10;
            itemsList.addElement(new SimpleString("After:"+Long.toString(free_)+"/"+Long.toString(total_), true));            
            
            if(free_>free){
               long calc = MathFP.div(MathFP.toFP(free_ - free),MathFP.toFP(free_));
               long calc2 = MathFP.mul(calc,MathFP.toFP(100));
               itemsList.addElement(new SimpleString("Free Memory Level: +"+MathFP.toString(calc2,1)+"%", true));  
            }
            else if(free_==free){
               itemsList.addElement(new SimpleString("Free Memory Level: 0%", true));
            }
            else{
               long calc = MathFP.div(MathFP.toFP(free - free_),MathFP.toFP(free));
               long calc2 = MathFP.mul(calc,MathFP.toFP(100));
               itemsList.addElement(new SimpleString("Free Memory Level: -"+MathFP.toString(calc2,1)+"%", true));                 
            }
            itemsList.addElement(new SimpleString("Cashe cleared.", true));
            itemsList.addElement(new SimpleString("Chats: "+Integer.toString(count), true));            
         }
      setCommandListener(this);
      attachDisplay(display);    
      this.parentView=pView;
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
            cf.storeConfPresence=storeConfPresence.getValue();
            cf.autoScroll=autoScroll.getValue();
            cf.timePresence=timePresence.getValue();
            cf.notifyWhenMessageType=notifyWhenMessageType.getValue();
            cf.autoDeTranslit=autoDetranslit.getValue();
            cf.showNickNames=showNickNames.getValue();
            cf.savePos=savePos.getValue();
            cf.boldNicks=boldNicks.getValue();
            cf.messageLimit=Integer.parseInt(messageLimit.getValue());
            cf.useLowMemory_iconmsgcollapsed=useLowMemory_iconmsgcollapsed.getValue();
            cf.smiles=smiles.getValue(); 
            cf.capsState=capsState.getValue(); 
            cf.textWrap=textWrap.getSelectedIndex();
            cf.sblockFont=sblockFont.getSelectedIndex();
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
            cf.sendMoodInMsg=sendMoodInMsg.getValue();
            
            cf.reconnectCount=Integer.parseInt(reconnectCount.getValue());
            cf.reconnectTime=Integer.parseInt(reconnectTime.getValue());
            cf.nokiaReconnectHack=nokiaReconnectHack.getValue();  
            cf.fileTransfer=fileTransfer.getValue();
            cf.adhoc=adhoc.getValue(); 
         } 
         else if(type==SR.MS_grStr){
           cf.useLowMemory_userotator=useLowMemory_userotator.getValue();
           cf.gradient_cursor=gradient_cursor.getValue();
           ui.VirtualList.memMonitor=cf.memMonitor=memMon.getValue();
           cf.scrollWidth=Integer.parseInt(scrollWidth.getValue());
           cf.drawScrollBgnd=drawScrollBgnd.getValue();      
           cf.panelsState=panels.getSelectedIndex();
           ui.VirtualList.changeOrient(cf.panelsState);   
           ui.VirtualList.showTimeTraffic=cf.showTimeTraffic=drawMenuCommand.getValue();
           cf.bgnd_image=bgnd_image.getSelectedIndex();
           
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
            cf.lightState=lightState.getValue();
            if (cf.allowMinimize) cf.popupFromMinimized=popupFromMinimized.getValue();
            cf.gmtOffset=Integer.parseInt(fieldGmt.getValue());
            if (langs[0].size()>1) {
              cf.lang=(String) langs[0].elementAt( langFiles.getSelectedIndex() );
            }        
            cf.executeByNum=executeByNum.getValue();
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
