/*
 * ConfigForm.java
 *
 * Created on 20.05.2008, 22:47
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
 *
 */

package Client;
import java.util.Vector;
//#ifdef PEP
//# import xmpp.extensions.PepListener;
//#endif
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.VirtualList;
import ui.controls.form.CheckBox;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import ui.controls.form.TrackItem;
import util.StringLoader;
import com.alsutton.jabber.datablocks.Presence;
import xmpp.EntityCaps;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
import java.util.Enumeration;
import ui.ImageList;
import VCard.VCard;
import ui.MainBar;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
//#endif
import ui.controls.form.LinkString;
import ui.controls.form.ImageItem; 


public class ConfigForm
    extends  DefForm
{
    
    private Display display;
    GMenuConfig gm = GMenuConfig.getInstance();
    private TrackItem diff_level;
 
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
    private CheckBox altern_chat_colors;
    
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
//#      private CheckBox notifyWhenMessageType;
//#endif
//#ifdef POPUPS
    private CheckBox popUps;
//#endif
    private CheckBox showBalloons;     
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
    private CheckBox fullscreen;
    private CheckBox memMonitor;
    private CheckBox enableVersionOs;
    private CheckBox queryExit;
//#ifdef USER_KEYS
//#    private CheckBox userKeys;
//#endif
    private CheckBox lightState;
    private CheckBox popupFromMinimized;

    private NumberInput fieldGmt; 
    private NumberInput scrollWidth; 
    private CheckBox drawScrollBgnd;    
    
    private DropChoiceBox textWrap;

    
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

    private Vector langs[];

    StaticData sd=StaticData.getInstance();
    
    Config cf;
    
    private CheckBox useClassicChat; 
    private CheckBox use_phone_theme;

    private NumberInput classic_chat_height;     
    private NumberInput line_count;
    
    public Command cmdCancel = new Command(SR.MS_CANCEL, Command.SCREEN, 2);
  
    /** Creates a new instance of ConfigForm */
    public ConfigForm(Display display, Displayable pView) {
        super(display, pView, SR.MS_OPTIONS);
        this.display=display;

        cf=Config.getInstance();

        itemsList.addElement(new SimpleString("* - "+SR.MS_RESTART_APP, true));
        
        itemsList.addElement(new SimpleString(SR.MS_DIFFICULTY_LEVEL, true));
        diff_level=new TrackItem(cf.difficulty_level, 2);
        itemsList.addElement(diff_level);
               
        itemsList.addElement(new SpacerItem(8));
        itemsList.addElement(new SimpleString(SR.MS_ROSTER_ELEMENTS, true));
        showOfflineContacts = new CheckBox(SR.MS_OFFLINE_CONTACTS, cf.showOfflineContacts); itemsList.addElement(showOfflineContacts);
        selfContact = new CheckBox(SR.MS_SELF_CONTACT, cf.selfContact); if(cf.difficulty_level>=1) itemsList.addElement(selfContact);
        showTransports = new CheckBox(SR.MS_TRANSPORTS, cf.showTransports); itemsList.addElement(showTransports);
        ignore = new CheckBox(SR.MS_IGNORE_LIST, cf.ignore); if(cf.difficulty_level>=1) itemsList.addElement(ignore);
        collapsedGroups = new CheckBox(SR.MS_COLLAPSED_GROUPS, cf.collapsedGroups); itemsList.addElement(collapsedGroups);
        autoFocus = new CheckBox(SR.MS_AUTOFOCUS, cf.autoFocus); itemsList.addElement(autoFocus);
        showResources = new CheckBox(SR.MS_SHOW_RESOURCES, cf.showResources); if(cf.difficulty_level>=1) itemsList.addElement(showResources);
        //useBoldFont = new CheckBox(SR.MS_BOLD_FONT, cf.useBoldFont); itemsList.addElement(useBoldFont);
//#ifdef CLIENTS_ICONS
        showClientIcon = new CheckBox(SR.MS_SHOW_CLIENTS_ICONS, cf.showClientIcon);
//#ifdef PLUGINS
//#         if (sd.ClientsIcons)
//#endif
            itemsList.addElement(showClientIcon);
//#endif
        dont_loadMC  = new CheckBox(SR.DONT_LOAD_MUCCONTACTS, cf.dont_loadMC);
        itemsList.addElement(dont_loadMC);
        
        gradient_cursor  = new CheckBox(SR.MS_GRADIENT_CURSOR,cf.gradient_cursor); 
        itemsList.addElement(gradient_cursor);

        altern_chat_colors  = new CheckBox(SR.MS_ALTERNATING_CHAT_COLORS,cf.altern_chat_colors); 
        if(cf.difficulty_level>=1) itemsList.addElement(altern_chat_colors);                
        
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
        
//#ifdef PEP
//#ifdef PLUGINS
//#         if (sd.PEP) {
//#endif
//#             itemsList.addElement(new SpacerItem(10));
//#             itemsList.addElement(new SimpleString(SR.MS_PEP, true));
//# 
//#             sndrcvmood = new CheckBox(SR.MS_USERMOOD, cf.sndrcvmood);
//#             itemsList.addElement(sndrcvmood);
//#ifdef PEP
//#             rcvtune = new CheckBox(SR.MS_USERTUNE, cf.rcvtune); 
//#             itemsList.addElement(rcvtune);
//# 
//#             rcvactivity = new CheckBox(SR.MS_USERACTIVITY, cf.rcvactivity);
//#             itemsList.addElement(rcvactivity);
//#endif
//#ifdef PLUGINS
//#         }
//#endif
//#endif

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_MESSAGES, true));

        eventComposing = new CheckBox(SR.MS_COMPOSING_EVENTS, cf.eventComposing); itemsList.addElement(eventComposing);
        capsState = new CheckBox(SR.MS_CAPS_STATE, cf.capsState); itemsList.addElement(capsState);
        storeConfPresence = new CheckBox(SR.MS_STORE_PRESENCE, cf.storeConfPresence); itemsList.addElement(storeConfPresence);
        timePresence = new CheckBox(SR.MS_SHOW_PRS_TIME, cf.timePresence); itemsList.addElement(timePresence);
        
        autoScroll = new CheckBox(SR.MS_AUTOSCROLL, cf.autoScroll); itemsList.addElement(autoScroll);
        useTabs = new CheckBox(SR.MS_EMULATE_TABS, cf.useTabs); if(cf.difficulty_level>=1) itemsList.addElement(useTabs);
//#ifdef RUNNING_MESSAGE
//#         notifyWhenMessageType = new CheckBox(SR.MS_RUNNING_MESSAGE, cf.notifyWhenMessageType); if(cf.difficulty_level>=1) itemsList.addElement(notifyWhenMessageType);
//#endif
//#ifdef POPUPS
        popUps = new CheckBox(SR.MS_POPUPS, cf.popUps); itemsList.addElement(popUps);
//#endif
        showBalloons = new CheckBox(SR.MS_SHOW_BALLONS, cf.showBalloons); itemsList.addElement(showBalloons);     
        eventDelivery = new CheckBox(SR.MS_DELIVERY, cf.eventDelivery); itemsList.addElement(eventDelivery);
//#ifdef CLIPBOARD
//#         useClipBoard = new CheckBox(SR.MS_CLIPBOARD, cf.useClipBoard); itemsList.addElement(useClipBoard);
//#endif
//#ifdef DETRANSLIT
//#        autoDetranslit = new CheckBox(SR.MS_AUTODETRANSLIT, cf.autoDeTranslit); itemsList.addElement(autoDetranslit);
//#endif
       showNickNames = new CheckBox(SR.MS_SHOW_NACKNAMES, cf.showNickNames); if(cf.difficulty_level>=1) itemsList.addElement(showNickNames);
//#ifdef MENU_LISTENER
       executeByNum = new CheckBox(SR.MS_EXECUTE_MENU_BY_NUMKEY, cf.executeByNum); if(cf.difficulty_level>=1) itemsList.addElement(executeByNum);
//#endif
       sendMoodInMsg = new CheckBox(SR.MS_MOOD_IN_MSG, cf.sendMoodInMsg); //itemsList.addElement(sendMoodInMsg);
       savePos = new CheckBox(SR.MS_SAVE_CURSOR, cf.savePos); 
       if(cf.difficulty_level>=1) itemsList.addElement(savePos); 
       boldNicks = new CheckBox(SR.MS_BOLD_AND_COLORS_NICKS, cf.boldNicks); 
       if(cf.difficulty_level>=1) itemsList.addElement(boldNicks);
//#if LOGROTATE
//#         messageCountLimit=new NumberInput(display, SR.MS_MESSAGE_COUNT_LIMIT, Integer.toString(cf.msglistLimit), 3, 1000);
//#         itemsList.addElement(messageCountLimit);
//#endif

        itemsList.addElement(new SpacerItem(10));
        messageLimit=new NumberInput(display, SR.MS_MESSAGE_COLLAPSE_LIMIT, Integer.toString(cf.messageLimit), 200, 1000);
        itemsList.addElement(messageLimit);
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_STARTUP_ACTIONS, true));
        autoLogin = new CheckBox(SR.MS_AUTOLOGIN, cf.autoLogin); 
        itemsList.addElement(autoLogin);
        autoJoinConferences = new CheckBox(SR.MS_AUTO_CONFERENCES, cf.autoJoinConferences); 
        itemsList.addElement(autoJoinConferences);
          
          
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_ADVANCED_OPT, true));
        useLowMemory_msgedit = new CheckBox(SR.MS_NEW_MSG_EDIT, cf.useLowMemory_msgedit); 
        useLowMemory_userotator = new CheckBox(SR.MS_ANIMATION, cf.useLowMemory_userotator); 
        //animateMenuAndRoster = new CheckBox(SR.MS_ANIMATION_MENU_ROSTER, cf.animateMenuAndRoster); 
        useLowMemory_iconmsgcollapsed = new CheckBox(SR.MS_ICON_COLP, cf.useLowMemory_iconmsgcollapsed);
          itemsList.addElement(useLowMemory_msgedit);
          itemsList.addElement(useLowMemory_userotator);
          //itemsList.addElement(animateMenuAndRoster);
          itemsList.addElement(useLowMemory_iconmsgcollapsed);
//#ifdef SMILES
        smiles = new CheckBox(SR.MS_SMILES, cf.smiles); itemsList.addElement(smiles);
//#endif 
        rosterStatus = new CheckBox(SR.MS_SHOW_STATUSES, cf.rosterStatus); itemsList.addElement(rosterStatus); 
         iconsLeft = new CheckBox(SR.MS_CLIENT_ICONS_LEFT, cf.iconsLeft);           itemsList.addElement(iconsLeft);          

        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_RECONNECT, true));
	reconnectCount=new NumberInput(display, SR.MS_RECONNECT_COUNT_RETRY, Integer.toString(cf.reconnectCount), 0, 100); itemsList.addElement(reconnectCount);
        reconnectTime=new NumberInput(display, SR.MS_RECONNECT_WAIT, Integer.toString(cf.reconnectTime), 1, 60 ); itemsList.addElement(reconnectTime);
        nokiaReconnectHack = new CheckBox("Nokia Reconnect Hack%Solves the reconnection problem on Nokia smartphones", cf.nokiaReconnectHack); itemsList.addElement(nokiaReconnectHack);        
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_APPLICATION, true));
        fullscreen = new CheckBox(SR.MS_FULLSCREEN, cf.fullscreen); itemsList.addElement(fullscreen);
        memMonitor = new CheckBox(SR.MS_HEAP_MONITOR, cf.memMonitor); itemsList.addElement(memMonitor);
        enableVersionOs = new CheckBox(SR.MS_SHOW_HARDWARE, cf.enableVersionOs); itemsList.addElement(enableVersionOs);
        queryExit = new CheckBox(SR.MS_CONFIRM_EXIT, cf.queryExit); itemsList.addElement(queryExit);
//#ifdef MENU_LISTENER
        oldSE = new CheckBox(SR.MS_KEYS_FOR_OLD_SE, cf.oldSE);
        if (phoneManufacturer==cf.SONYE) itemsList.addElement(oldSE);
//#endif
//#ifdef USER_KEYS
//#         userKeys = new CheckBox(SR.MS_CUSTOM_KEYS, cf.userKeys); 
//#ifdef PLUGINS
//#         if (sd.UserKeys)
//#endif
//#             itemsList.addElement(userKeys);
//#endif
        lightState = new CheckBox(SR.MS_FLASHLIGHT, cf.lightState);
        if (phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2 || phoneManufacturer==Config.SONYE || phoneManufacturer==Config.NOKIA) itemsList.addElement(lightState);
//#ifdef FILE_TRANSFER
        fileTransfer = new CheckBox(SR.MS_FILE_TRANSFERS, cf.fileTransfer); 
//#ifdef PLUGINS
//#         if (sd.FileTransfer)
//#endif
            itemsList.addElement(fileTransfer);
//#endif

//#ifdef ADHOC
//#         adhoc = new CheckBox(SR.MS_ADHOC, cf.adhoc); 
//#ifdef PLUGINS
//#         if (sd.Adhoc)
//#endif
//#             itemsList.addElement(adhoc);
//#endif
        if (cf.allowMinimize) {
            popupFromMinimized = new CheckBox(SR.MS_ENABLE_POPUP, cf.popupFromMinimized);
            itemsList.addElement(popupFromMinimized);
        }
        
        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.MS_TIME_SETTINGS, true));
	fieldGmt=new NumberInput(display, SR.MS_GMT_OFFSET, Integer.toString(cf.gmtOffset), -12, 12); 
        itemsList.addElement(fieldGmt);
        
        if(cf.difficulty_level>=1) {
          itemsList.addElement(new SpacerItem(10));
          itemsList.addElement(new SimpleString(SR.MS_SCROLL_OPTIONS, true));
	  scrollWidth=new NumberInput(display, SR.MS_SCROLL_WIDTH, Integer.toString(cf.scrollWidth), 4, 20); 
          itemsList.addElement(scrollWidth);

          itemsList.addElement(new SpacerItem(4));
          //itemsList.addElement(new SimpleString("Scroll Options", true));
          drawScrollBgnd = new CheckBox(SR.MS_BGND_SCROLL,cf.drawScrollBgnd); 
          itemsList.addElement(drawScrollBgnd);        
         }
        
        itemsList.addElement(new SpacerItem(10));
        textWrap=new DropChoiceBox(display, SR.MS_TEXTWRAP);
        textWrap.append(SR.MS_TEXTWRAP_CHARACTER);
        textWrap.append(SR.MS_TEXTWRAP_WORD);
	textWrap.setSelectedIndex(cf.textWrap);
	itemsList.addElement(textWrap);
    
        
        itemsList.addElement(new SpacerItem(10));
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

//#ifdef AUTOSTATUS
//#         itemsList.addElement(new SpacerItem(10));
//#         autoAwayType=new DropChoiceBox(display, SR.MS_AWAY_TYPE);
//#         autoAwayType.append(SR.MS_AWAY_OFF);
//#         autoAwayType.append(SR.MS_AWAY_LOCK);
//#         autoAwayType.append(SR.MS_MESSAGE_LOCK);
//#         autoAwayType.append(SR.MS_IDLE);
//#         autoAwayType.setSelectedIndex(cf.autoAwayType);
//#         itemsList.addElement(autoAwayType);
//# 
//#         fieldAwayDelay=new NumberInput(display, "*"+SR.MS_AWAY_PERIOD, Integer.toString(cf.autoAwayDelay), 1, 60);
//#         itemsList.addElement(fieldAwayDelay);
//# 
//#         awayStatus=new CheckBox(SR.MS_AUTOSTATUS_MESSAGE, cf.setAutoStatusMessage);
//#         itemsList.addElement(awayStatus);
//#endif

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
        itemsList.addElement(new SpacerItem(10));
        bgnd_image=new DropChoiceBox(display, "*"+SR.MS_TYPE_BACKGROUND);
        bgnd_image.append(SR.MS_BGND_NONE); //0
        bgnd_image.append(SR.MS_BGND_IMAGE);//1
        bgnd_image.append(SR.MS_BGND_GRADIENT);//2
        bgnd_image.append(SR.MS_MY_BGND_IMAGE);//3
        bgnd_image.setSelectedIndex(cf.bgnd_image);
        itemsList.addElement(bgnd_image);
        
      if(cf.difficulty_level>=1) {      
        itemsList.addElement(new SpacerItem(5));
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

        enableListWrapping(false);
        attachDisplay(display);
        this.parentView=pView;
    }

    
    public void cmdOk() {
        int diff = diff_level.getValue();
        cf.showOfflineContacts=showOfflineContacts.getValue();
        if(cf.difficulty_level>=1) cf.selfContact=selfContact.getValue();
        cf.showTransports=showTransports.getValue();
        if(cf.difficulty_level>=1) cf.ignore=ignore.getValue();
        cf.collapsedGroups=collapsedGroups.getValue();
        cf.autoFocus=autoFocus.getValue();
        if(cf.difficulty_level>=1) cf.showResources=showResources.getValue();
        //cf.useBoldFont=useBoldFont.getValue();
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#         if (sd.ClientsIcons)
//#endif
            cf.showClientIcon=showClientIcon.getValue();
//#endif
        cf.dont_loadMC=dont_loadMC.getValue();

        cf.gradient_cursor=gradient_cursor.getValue();
        if(cf.difficulty_level>=1) cf.altern_chat_colors=altern_chat_colors.getValue();
         
        cf.autoSubscribe=subscr.getSelectedIndex();
        
        cf.notInListDropLevel=nil.getSelectedIndex();

        cf.eventComposing=eventComposing.getValue();
        cf.capsState=capsState.getValue();
        cf.storeConfPresence=storeConfPresence.getValue();
        cf.timePresence=timePresence.getValue();
        cf.autoScroll=autoScroll.getValue();
        if(cf.difficulty_level>=1) cf.useTabs=useTabs.getValue();
        
//#ifdef PEP
//#ifdef PLUGINS
//#         if (sd.PEP) {
//#endif
//#             cf.sndrcvmood=sndrcvmood.getValue();
//#ifdef PEP
//#             cf.rcvtune=rcvtune.getValue();
//#             cf.rcvactivity=rcvactivity.getValue();
//#             
//#            if(StaticData.getInstance().roster.isLoggedIn()) {  
//#             if(!sndrcvmood.getValue()&&!rcvtune.getValue()&&!rcvactivity.getValue()){
//#                 PepListener.getInstance().removeBlockListener();
//#              }else{
//#                 if(PepListener.getInstance()==null){
//#                   PepListener.getInstance().addBlockListener();          
//#                 }
//#              }
//#            }            
//#endif
//#ifdef PLUGINS
//#         }
//#endif
//#endif       
//#ifdef RUNNING_MESSAGE
//#         if(cf.difficulty_level>=1) cf.notifyWhenMessageType=notifyWhenMessageType.getValue();
//#endif
//#ifdef POPUPS
        cf.popUps=popUps.getValue();
//#endif
        cf.showBalloons=showBalloons.getValue();
        VirtualList.showBalloons=cf.showBalloons;
        cf.eventDelivery=eventDelivery.getValue();
//#ifdef CLIPBOARD
//#         cf.useClipBoard=useClipBoard.getValue();
//#endif
//#ifdef DETRANSLIT
//#         cf.autoDeTranslit=autoDetranslit.getValue();
//#endif
        if(cf.difficulty_level>=1) cf.showNickNames=showNickNames.getValue();
//#ifdef MENU_LISTENER
        if(cf.difficulty_level>=1) cf.executeByNum=executeByNum.getValue();
//#endif
        cf.sendMoodInMsg=sendMoodInMsg.getValue();
        if(cf.difficulty_level>=1) cf.savePos=savePos.getValue();
        if(cf.difficulty_level>=1) cf.boldNicks=boldNicks.getValue();

        cf.autoLogin=autoLogin.getValue();
        cf.useLowMemory_msgedit=useLowMemory_msgedit.getValue();
        cf.useLowMemory_userotator=useLowMemory_userotator.getValue();
        //cf.animateMenuAndRoster=animateMenuAndRoster.getValue();
        cf.useLowMemory_iconmsgcollapsed=useLowMemory_iconmsgcollapsed.getValue();

//#ifdef SMILES
        cf.smiles=smiles.getValue();
//#endif        
        cf.rosterStatus=rosterStatus.getValue();        
        cf.iconsLeft=iconsLeft.getValue();
        

        
        cf.autoJoinConferences=autoJoinConferences.getValue();
        
        cf.reconnectCount=Integer.parseInt(reconnectCount.getValue());
        cf.reconnectTime=Integer.parseInt(reconnectTime.getValue());
        cf.nokiaReconnectHack=nokiaReconnectHack.getValue();
//#ifdef FILE_TRANSFER
//#ifdef PLUGINS
//#         if (sd.FileTransfer)
//#endif
            cf.fileTransfer=fileTransfer.getValue();
//#endif

//#ifdef ADHOC
//#ifdef PLUGINS
//#         if (sd.Adhoc)
//#endif
//#             cf.adhoc=adhoc.getValue();
//#endif
        
        VirtualList.showTimeTraffic=cf.showTimeTraffic=drawMenuCommand.getValue();
        VirtualList.fullscreen=cf.fullscreen=fullscreen.getValue();
        VirtualList.memMonitor=cf.memMonitor=memMonitor.getValue();
        cf.enableVersionOs=enableVersionOs.getValue();
        cf.queryExit=queryExit.getValue();
//#ifdef MENU_LISTENER
        if (phoneManufacturer==cf.SONYE) cf.oldSE=oldSE.getValue();
//#endif
//#ifdef USER_KEYS
//#ifdef PLUGINS
//#             if (sd.UserKeys)
//#endif
//#                 cf.userKeys=VirtualList.userKeys=userKeys.getValue();
//#endif
        cf.lightState=lightState.getValue();
        if (cf.allowMinimize)
            cf.popupFromMinimized=popupFromMinimized.getValue();

        cf.gmtOffset=Integer.parseInt(fieldGmt.getValue());
        if(cf.difficulty_level>=1) {
            cf.scrollWidth=Integer.parseInt(scrollWidth.getValue());
            cf.drawScrollBgnd=drawScrollBgnd.getValue(); 
        }

        cf.textWrap=textWrap.getSelectedIndex();

        if (langs[0].size()>1) {
            cf.lang=(String) langs[0].elementAt( langFiles.getSelectedIndex() );
        }
//#ifdef AUTOSTATUS
//#             cf.setAutoStatusMessage=awayStatus.getValue();
//#             cf.autoAwayDelay=Integer.parseInt(fieldAwayDelay.getValue());
//#             cf.autoAwayType=autoAwayType.getSelectedIndex();
//#endif
        cf.messageLimit=Integer.parseInt(messageLimit.getValue());
//#if LOGROTATE
//#             cf.msglistLimit=Integer.parseInt(messageCountLimit.getValue());
//#endif
        if (cf.panelsState!=panels.getSelectedIndex()) {
            cf.panelsState=panels.getSelectedIndex();
            VirtualList.changeOrient(cf.panelsState);
        }       

        sd.roster.setLight(cf.lightState);   

        sd.roster.setFullScreenMode(cf.fullscreen);

        cf.firstRun=false;

        cf.bgnd_image=bgnd_image.getSelectedIndex();
        
//#ifdef BACK_IMAGE
//#         try {
//#             if (/*img==null && */ cf.bgnd_image==1 /*|| cf.bgnd_image==2*/ ){
//#                 Image img=Image.createImage("/images/back.png");
//#                     gm.imgWidth = img.getWidth();
//#                     gm.imgHeight = img.getHeight();  
//#                     gm.img=img;
//#             }else if (cf.bgnd_image==3) {
//#                 Image bgnd=Image.createImage("/images/bgnd.png");
//#                 ImageList il = new ImageList();
//#                 gm.bgnd=bgnd;
//#             }else if(cf.bgnd_image==0){
//#                 gm.img=null;
//#                 gm.bgnd=null;
//#             }
//#         } catch (Exception e) { }
//#endif        

       if(cf.difficulty_level>=1){ 
        cf.useClassicChat=useClassicChat.getValue();
        cf.use_phone_theme=use_phone_theme.getValue();
        cf.classic_chat_height=Integer.parseInt(classic_chat_height.getValue());
        cf.line_count=Integer.parseInt(line_count.getValue());
       }
       cf.difficulty_level = diff;
       cf.updateTime();
       cf.saveToStorage();
       destroyView();
    }

    
    public void commandAction(Command command, Displayable displayable) {
        if(command==cmdOk){
            cmdOk();
        }
        else super.commandAction(command, displayable);
    }
    
    
    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();
//#endif

//#ifndef GRAPHICS_MENU               
    super.commandState(); 
//#endif             
        removeCommand(cmdCancel);
        removeCommand(cmdOk);
//#ifndef GRAPHICS_MENU        
     addCommand(cmdCancel);
//#endif     
    }
    
    
//#ifdef MENU_LISTENER
    //public String touchLeftCommand(){ return SR.MS_MENU; }
    
 
 //#ifdef GRAPHICS_MENU   
//#     /*
//#      public void touchLeftPressed(){
//#         showGraphicsMenu();
//#     }    
//#     public int showGraphicsMenu() {
//#         commandState();
//#         new GMenu(display, parentView, this, null, menuCommands);        
//#         GMenuConfig.getInstance().itemGrMenu=GMenu.CONFIG_FORM;
//#         redraw();
//#         return GMenu.CONFIG_FORM;
//#     }
//#      */
//#else
    public void touchLeftPressed(){
        showMenu();
    }   
    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.MS_OPTIONS, null, menuCommands);
   }  
//#endif     
    

//#endif         
        

    
    public void destroyView(){
        cf.isOptionsSel=false;
        if (display!=null)  
            display.setCurrent(parentView);
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
    }

    
}
