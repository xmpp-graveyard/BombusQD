/*
 * Config.java
 *
 * Created on 19.03.2005, 18:37
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

package Client;
import Alerts.AlertProfile;
import images.ActionsIcons;
import images.RosterIcons;
//#ifdef SMILES
import images.SmilesIcons;
//#endif
//#ifdef FILE_IO
import io.file.FileIO;
//#endif
import java.io.*;
import java.util.*;
import midlet.BombusQD;
import Fonts.FontCache;
import util.StringLoader;
import ui.Time;
import ui.VirtualList;
import io.NvStorage;
import Menu.MenuListener;

/**
 *
 * @author Eugene Stahov,aqent
 */
public class Config {
    // Singleton
    private static Config instance;
    
    public final int vibraLen=500;

    public static int KEY_BACK = -11;
    public static int SOFT_LEFT = -1000;
    public static int SOFT_RIGHT = -1000;
    
    public final static int SUBSCR_AUTO=0;
    public final static int SUBSCR_ASK=1;
    public final static int SUBSCR_DROP=2;
    public final static int SUBSCR_REJECT=3;
    
    public final static int NOT_DETECTED=0;
    public final static int NONE=-1;
    public final static int SONYE=1;
    public final static int NOKIA=2;
    public final static int SIEMENS=3;
    public final static int SIEMENS2=4;
    public final static int MOTO=5;
    public final static int MOTOEZX=6;
    public final static int WINDOWS=7;
    public final static int INTENT=8;
    public final static int J2ME=9;
    public final static int NOKIA_9XXX=10;
    public final static int SONYE_M600=11;
//#if !ZLIB
//#     public final static int XENIUM99=12;
//#endif
    public final static int SAMSUNG=14;
    public final static int LG=15;
    public final static int JBED=16;
    public final static int WTK=50;
    public final static int OTHER=99;
    public final static int NOKIA_5800=44;

    StaticData sd = StaticData.getInstance();
    
    private static String platformName;
    
    public boolean ghostMotor=false;
    public boolean flagQuerySign=false;       
    
    
    public boolean muc119=true;	// before muc 1.19 use muc#owner instead of muc#admin
    
    public char keyLock='*';
    public char keyVibra='#';
    
//#ifdef AUTOSTATUS
//#     public final static int AWAY_OFF=0;
//#     public final static int AWAY_LOCK=1;
//#     public final static int AWAY_MESSAGE=2;
//#     public final static int AWAY_IDLE=3;
//#     
//#     public int autoAwayType=0;
//#     public int autoAwayDelay=5; //5 minutes
//#     public boolean setAutoStatusMessage=true;
//#endif
    
//#ifdef HISTORY
//#      public String msgPath="";
//#      public String msgAvatarPath="";
//#      
//#      public boolean msgLog=false;
//#      public boolean msgLogPresence=false;
//#      public boolean msgLogConf=false;
//#      public boolean msgLogConfPresence=false;
//#      public boolean lastMessages=false;
//#endif
    public boolean cp1251=true;     
//#ifndef WMUC
    public String defGcRoom=getStringProperty("defroom", "qd@conference.jabber.ru");
    public boolean storeConfPresence=false;   
    public boolean autoJoinConferences=false;
    public int confMessageCount=20;
//#endif
    // non-volatile values
    public int accountIndex=-1;
    public boolean fullscreen=true;
    public int def_profile=0;
//#ifdef SMILES
    public boolean smiles=true;
//#endif
    public boolean showOfflineContacts=false;
    public boolean showTransports=true;
    public boolean selfContact=true;
    public boolean ignore=false;
    public boolean eventComposing=true;//Уведомление о наборе текста
    public boolean autoLogin=true;
    public boolean autoFocus=false;
    public int loginstatus=0;//loginstatus
    public int gmtOffset;
    public boolean popupFromMinimized=true;
    public boolean memMonitor=false;
    public int rosterFont=0;
    public int msgFont=0;
    public int barFont=0;
    public int baloonFont=0;
    public String lang;
    public boolean capsState=false;
    public int textWrap=1;
    public int autoSubscribe=SUBSCR_ASK;
    // runtime values
    public boolean allowMinimize=false;
    public int profile=0;
    public int lastProfile=0;
    public boolean istreamWaiting;
    public int phoneManufacturer=NOT_DETECTED;
    public int panelsState= 2; //default state both panels show, reverse disabled
    public boolean lightState=false;
    public boolean autoScroll=true;
//#ifdef POPUPS
    public boolean popUps=true;
//#endif
    public boolean showResources=true;
    public boolean enableVersionOs=true;//Показ версии клиента
    public boolean collapsedGroups=true;
    public int messageLimit=512;
    public boolean eventDelivery=true;//Уведомление о доставке
//#ifdef DETRANSLIT
//#     public boolean transliterateFilenames=false;
//#     public boolean autoDeTranslit=false;
//#endif
    public boolean rosterStatus=true;//Вторая строка контакта
//#ifdef PEP
//#     public boolean sndrcvmood = true;
//#     public boolean rcvtune = true;
//#     public boolean rcvactivity = true;
//#endif
    public boolean queryExit = false;
    public int notInListDropLevel=NotInListFilter.ALLOW_ALL;//enable all
    public boolean showBalloons = true;
//#ifdef USER_KEYS
//#     public boolean userKeys = false;
//#endif
//#ifdef LOGROTATE
//#     public int msglistLimit=500;
//#endif
    public boolean useTabs=false;
    public boolean notifyBlink=true;
    public boolean notifySound=false;
    public boolean notifyPicture=false;
    public boolean useBoldFont=true;
//#ifdef RUNNING_MESSAGE
//#     public boolean notifyWhenMessageType = false;//Бегущая строка
//#endif
//#ifdef CLIPBOARD
//#     public boolean useClipBoard = true;
//#endif
    public boolean firstRun = true;
    public String verHash="";
    public String resolvedHost="";
    public int resolvedPort=0;
    public boolean IQNotify=false;
//#ifdef CLIENTS_ICONS
    public boolean showClientIcon=true;//Иконки клиентов
//#endif
    public int reconnectCount=30;
    public int reconnectTime=10;
    public boolean executeByNum = false;
    public boolean showNickNames = true;
    public boolean fileTransfer=true;//Передача файлов
    public boolean adhoc=false;//Удаленное управление
    public boolean saveHistory=true;//История
    
    public boolean oldSE=false;    
    
    public boolean showTimeTraffic=false;
    public boolean useLowMemory_msgedit=true;
    public boolean useLowMemory_userotator=false;   
    public boolean useLowMemory_iconmsgcollapsed=false;    
    public boolean drawCPhoto=true;
    public boolean auto_queryPhoto=false; //Автозагрузка визиток    
    
    public boolean sendMoodInMsg=false;
    public String moodText="";     
    public String moodName="";   
    public String actText="";        
    public String actDescr="";     
    public String actCat=""; 
    
    public int track=0;
    public boolean find_text=false;//fix
    public String find_text_str="";
    public int maxAvatarHeight=45;
    public int maxAvatarWidth=45;
    public int bgnd_image=getIntFromManifest("bgnd_type",0);
    public boolean image_in_popUp=true;
    public String add_contact_name="username@server.domen"; 
    public boolean use_drawed_font=false;       
    public String drwd_fontname="no";  
    
    public boolean savePos=false;  
    public boolean boldNicks=true;  
    public int scrollWidth=4;
    public boolean drawScrollBgnd = true;
    public int[] cursorPos = {
                            1,  //RosterToolsMenu 0
                            1,  //RosterItemActions 1
                            1,  //ActivityMenu 2
                            1,  //MoodList 3
                            -1, //...
                            1,
                            1,
                            1,
                            1
                        };
    public boolean isOptionsSel = false;
    public int req_heap=1000;
    public long free_heap=0;
    public boolean isLegal=false;
    public boolean isMinimized = false;
    public boolean iconsLeft = true;   
    public String path_skin = "";   
    public int width_classic=-1;
    public boolean useClassicChat=false;

    //classic chat
    public int classic_chat_height=140;
    public int line_count=300;    
    public boolean use_phone_theme=false;
    public boolean gradient_cursor=true;
    public boolean altern_chat_colors=false; //чередовать цвета чата

//#if BREDOGENERATOR                
//#    public boolean bredoGen=false;
//#endif   
    
    public String langpair="ru==>en";
   
    public int argb_bgnd=getIntFromManifest("argb_bgnd",0);
    public int gmenu_bgnd=getIntFromManifest("gmenu_bgnd",0);
    public int popup_bgnd=0;
    public int cursor_bgnd=0;
   
    public int avatar_cashe_size=0;
    public int difficulty_level=getIntFromManifest("difflevel",0);
   
    public int inStanz=0;
    public int outStanz=0;
   
    public boolean autoSaveVcard=false;
    public boolean showAvatarRect=false;
    public boolean autoload_FSPhoto=false;

    public boolean nokiaReconnectHack=false;
    public boolean timePresence=false;
    public boolean animateMenuAndRoster=false;
    public boolean cursivUse = false;
    public boolean isStatusFirst=false;
    
    public boolean dont_loadMC=false;
   
   
    public static Config getInstance(){
	if (instance==null) {
	    instance=new Config();
	    instance.loadFromStorage();

            FontCache.roster=instance.rosterFont;
            FontCache.msg=instance.msgFont;
            
            FontCache.bar=instance.barFont;
            FontCache.baloon=instance.baloonFont;
	}
	return instance;
    }
    
    /** Creates a new instance of Config */
    private Config() {
        getPhoneManufacturer();
        VirtualList.phoneManufacturer=phoneManufacturer;
        
	int gmtloc=TimeZone.getDefault().getRawOffset()/3600000;
	gmtOffset=gmtloc;
	
	short greenKeyCode=-1000;
        
        switch (phoneManufacturer) {
            case SONYE:
             //prefetch images
                RosterIcons.getInstance();
                ActionsIcons.getInstance();
//#ifdef SMILES
                if (smiles) SmilesIcons.getInstance();
//#endif
                System.gc();
                try { Thread.sleep(50); } catch (InterruptedException e){}
                allowMinimize=true;
                greenKeyCode=-10;
                fullscreen=false;
                break;
            case SONYE_M600:
                KEY_BACK=-11;
                break;
            case WTK:
                greenKeyCode=-10;
                break;
            case NOKIA:
                KEY_BACK=VirtualList.NOKIA_PEN;
                greenKeyCode=-10;
                allowMinimize=true;
                break;
            case SIEMENS:
            case SIEMENS2:
                keyLock='#';
                keyVibra='*';
                KEY_BACK=-4;
                greenKeyCode=VirtualList.SIEMENS_GREEN;
                break;
            case WINDOWS:
                greenKeyCode=-5;
                VirtualList.keyClear=8;
                break;
            case MOTO:
                ghostMotor=true;
                istreamWaiting=true;
                greenKeyCode=-10;
                break;
            case MOTOEZX:
                VirtualList.keyVolDown=VirtualList.MOTOE680_VOL_DOWN;
                KEY_BACK=VirtualList.MOTOE680_REALPLAYER;
		greenKeyCode=-31;
                break;  
//#if !ZLIB
//#             case XENIUM99:
//#                 istreamWaiting=false; //is it critical for phillips xenium?
//#                 break;
//#endif
        }
	VirtualList.greenKeyCode=greenKeyCode;
    }
    
    protected void loadFromStorage(){
        DataInputStream inputStream=NvStorage.ReadFileRecord("config", 0);
	try {
	    accountIndex = inputStream.readInt();
	    showOfflineContacts=inputStream.readBoolean();
	    fullscreen=inputStream.readBoolean();
	    def_profile = inputStream.readInt()%4;
//#ifdef SMILES
	    smiles=inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif
	    showTransports=inputStream.readBoolean();
	    selfContact=inputStream.readBoolean();
	    collapsedGroups=inputStream.readBoolean();
	    ignore=inputStream.readBoolean();
	    eventComposing=inputStream.readBoolean();
	    gmtOffset=inputStream.readInt();
	    inputStream.readInt(); //locOffset
	    autoLogin=inputStream.readBoolean();
	    autoJoinConferences=inputStream.readBoolean();
	    popupFromMinimized=inputStream.readBoolean();
	    notifyBlink=inputStream.readBoolean();
	    memMonitor=inputStream.readBoolean();
            rosterFont=inputStream.readInt();
            msgFont=inputStream.readInt();
            autoFocus=inputStream.readBoolean();
            notInListDropLevel=inputStream.readInt();
            storeConfPresence=inputStream.readBoolean();
            capsState=inputStream.readBoolean();
	    textWrap=inputStream.readInt();
            loginstatus=inputStream.readInt();
//#ifdef HISTORY
//#             msgPath=inputStream.readUTF();
//#             msgAvatarPath=inputStream.readUTF();
//#             msgLog=inputStream.readBoolean();
//#             msgLogPresence=inputStream.readBoolean();
//#             msgLogConfPresence=inputStream.readBoolean();
//#             msgLogConf=inputStream.readBoolean();
//#else
            inputStream.readUTF();
            inputStream.readBoolean();
            inputStream.readBoolean();
            inputStream.readBoolean();
            inputStream.readBoolean();
//#endif
            cp1251=inputStream.readBoolean();
//#ifdef AUTOSTATUS
//#             autoAwayDelay=inputStream.readInt();
//#else
            inputStream.readInt();
//#endif
            defGcRoom=inputStream.readUTF();
            firstRun=inputStream.readBoolean();
            panelsState=inputStream.readInt();
            confMessageCount=inputStream.readInt();

            fileTransfer=inputStream.readBoolean(); //newMenu

            lightState=inputStream.readBoolean();
            notifySound=inputStream.readBoolean();
//#ifdef HISTORY
//#             lastMessages=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef AUTOSTATUS
//#             setAutoStatusMessage=inputStream.readBoolean();
//#             autoAwayType=inputStream.readInt();
//#else
            inputStream.readBoolean();
            inputStream.readInt();
//#endif
            autoScroll=inputStream.readBoolean();
//#ifdef POPUPS
            popUps=inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif
            showResources=inputStream.readBoolean();
            
            enableVersionOs=inputStream.readBoolean();
            messageLimit=inputStream.readInt();
            lang=inputStream.readUTF();
            eventDelivery=inputStream.readBoolean();
//#ifdef DETRANSLIT
//#             transliterateFilenames=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            
            rosterStatus=inputStream.readBoolean();
            
            queryExit=inputStream.readBoolean();
            notifyPicture=inputStream.readBoolean();
            showBalloons=inputStream.readBoolean();
//#ifdef USER_KEYS
//#             userKeys=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef LOGROTATE
//#             msglistLimit=inputStream.readInt();
//#else
            inputStream.readInt();
//#endif
            useTabs=inputStream.readBoolean();
            autoSubscribe=inputStream.readInt();
            useBoldFont=inputStream.readBoolean();
//#ifdef RUNNING_MESSAGE
//#             notifyWhenMessageType = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            IQNotify=inputStream.readBoolean(); //IRC_LIKE
//#ifdef PEP
//#             sndrcvmood = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            inputStream.readUTF(); //scheme

//#ifdef CLIPBOARD
//#             useClipBoard = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef PEP
//#             rcvtune = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            barFont=inputStream.readInt();
            baloonFont=inputStream.readInt();
            
            verHash=inputStream.readUTF();
            resolvedHost=inputStream.readUTF();
            resolvedPort=inputStream.readInt();
            
//#ifdef DETRANSLIT
//#             autoDeTranslit=inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
//#ifdef CLIENTS_ICONS
            showClientIcon=inputStream.readBoolean();
//#else
//#             inputStream.readBoolean();
//#endif
            
            reconnectCount=inputStream.readInt();
            reconnectTime=inputStream.readInt();
            
            executeByNum=inputStream.readBoolean();
            
            showNickNames=inputStream.readBoolean();
            
            adhoc=inputStream.readBoolean();
//#ifdef PEP
//#             rcvactivity = inputStream.readBoolean();
//#else
            inputStream.readBoolean();
//#endif
            oldSE=inputStream.readBoolean();
            
            showTimeTraffic=inputStream.readBoolean();
            useLowMemory_msgedit=inputStream.readBoolean();
            useLowMemory_userotator=inputStream.readBoolean();
            useLowMemory_iconmsgcollapsed=inputStream.readBoolean();
            drawCPhoto=inputStream.readBoolean();
            auto_queryPhoto=inputStream.readBoolean();
            sendMoodInMsg=inputStream.readBoolean(); 
            
            moodText=inputStream.readUTF();
            moodName=inputStream.readUTF();
            
            actText=inputStream.readUTF();
            actDescr=inputStream.readUTF();
            actCat=inputStream.readUTF();
            maxAvatarHeight=inputStream.readInt();
            bgnd_image=inputStream.readInt(); 
            
            use_drawed_font=inputStream.readBoolean();
            drwd_fontname=inputStream.readUTF();
            savePos=inputStream.readBoolean();
            boldNicks=inputStream.readBoolean();

            scrollWidth=inputStream.readInt();
            drawScrollBgnd=inputStream.readBoolean();
            isLegal=inputStream.readBoolean();
            
            iconsLeft=inputStream.readBoolean();
            path_skin=inputStream.readUTF();
            
            
            useClassicChat=inputStream.readBoolean();
            classic_chat_height=inputStream.readInt();
            line_count=inputStream.readInt();

            use_phone_theme=inputStream.readBoolean();
            gradient_cursor=inputStream.readBoolean();
            altern_chat_colors=inputStream.readBoolean();
            
            argb_bgnd=inputStream.readInt();
            gmenu_bgnd=inputStream.readInt();
            popup_bgnd=inputStream.readInt();
            cursor_bgnd=inputStream.readInt();
            
            avatar_cashe_size=inputStream.readInt();
            difficulty_level=inputStream.readInt();
            maxAvatarWidth=inputStream.readInt();
            
            autoSaveVcard=inputStream.readBoolean();
            showAvatarRect=inputStream.readBoolean();  
            autoload_FSPhoto=inputStream.readBoolean(); 
            nokiaReconnectHack=inputStream.readBoolean(); 

            timePresence=inputStream.readBoolean(); 
            animateMenuAndRoster=inputStream.readBoolean(); 
            
            cursivUse=inputStream.readBoolean(); 
            dont_loadMC=inputStream.readBoolean(); 
   
 //#if BREDOGENERATOR                
//#         bredoGen=inputStream.readBoolean();
//#endif             
	    inputStream.close();
            inputStream=null;
	} catch (Exception e) {
            try {
                if (inputStream!=null) {
                    inputStream.close();
                    inputStream=null;
                }
            } catch (IOException ex) { }
	}
	
	lastProfile=profile=def_profile;
        if (lastProfile==AlertProfile.VIBRA) lastProfile=0;
	updateTime();
	VirtualList.fullscreen=fullscreen;
	VirtualList.memMonitor=memMonitor;
        VirtualList.showBalloons=showBalloons;
        VirtualList.panelsState=panelsState;
        VirtualList.showTimeTraffic=showTimeTraffic;

//#ifdef USER_KEYS
//#ifdef PLUGINS
//#         if(!sd.UserKeys) userKeys=false;
//#endif
//#         VirtualList.userKeys=userKeys;
//#endif
        
//#ifdef PLUGINS
//#ifdef FILE_TRANSFER
//#         if(!sd.FileTransfer) fileTransfer=false;
//#endif
//#ifdef PEP
//#         if(!sd.PEP) sndrcvmood=false;
//#endif
//#ifdef PEP
//#         if(!sd.PEP) rcvtune=false;
//#endif
//#ifdef PEP
//#         if (!sd.PEP) rcvactivity=false;
//#endif
//#ifdef ADHOC
//#         if(!sd.Adhoc) adhoc=false;
//#endif
//#ifdef CLIENTS_ICONS
//#         if(!sd.ClientsIcons) showClientIcon=false;
//#endif
//#ifdef HISTORY
//#         if(!sd.History) saveHistory=false;
//#endif
//#endif
    }
    
    public String langFileName(){
        if (lang==null) {
            //auto-detecting
            lang=System.getProperty("microedition.locale");
            System.out.println(lang);
            //We will use only language code from locale
            if (lang==null) lang="en"; else lang=lang.substring(0, 2).toLowerCase();
        }
        
        if (lang.equals("en")) return null;  //english
	Vector files[]=new StringLoader().stringLoader("/lang/res.txt", 3);
        for (int i=0; i<files[0].size(); i++) {
            String langCode=(String) files[0].elementAt(i);
            if (lang.equals(langCode))
        	return (String) files[1].elementAt(i);
        }
        return null;
        //return "/lang/ru.txt"; //unknown language ->en
    }

    
    public void saveToStorage(){
	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	
	try {
	    outputStream.writeInt(accountIndex);
	    outputStream.writeBoolean(showOfflineContacts);
	    outputStream.writeBoolean(fullscreen);
	    outputStream.writeInt(def_profile);
//#ifdef SMILES
	    outputStream.writeBoolean(smiles);
//#else
//#             outputStream.writeBoolean(false);
//#endif
	    outputStream.writeBoolean(showTransports);
	    outputStream.writeBoolean(selfContact);
	    outputStream.writeBoolean(collapsedGroups);
	    outputStream.writeBoolean(ignore);
	    outputStream.writeBoolean(eventComposing);
	    outputStream.writeInt(gmtOffset);
	    outputStream.writeInt(0); //locOffset
	    outputStream.writeBoolean(autoLogin);
	    outputStream.writeBoolean(autoJoinConferences);
            outputStream.writeBoolean(popupFromMinimized);
	    outputStream.writeBoolean(notifyBlink);
	    outputStream.writeBoolean(memMonitor);
            outputStream.writeInt(rosterFont);
            outputStream.writeInt(msgFont);
            outputStream.writeBoolean(autoFocus);
            outputStream.writeInt(notInListDropLevel);
            outputStream.writeBoolean(storeConfPresence); 
            outputStream.writeBoolean(capsState); 
	    outputStream.writeInt(textWrap);
            outputStream.writeInt(loginstatus);
//#ifdef HISTORY
//#             outputStream.writeUTF(msgPath);
//#             outputStream.writeUTF(msgAvatarPath);
//#             outputStream.writeBoolean(msgLog);
//#             outputStream.writeBoolean(msgLogPresence);
//#             outputStream.writeBoolean(msgLogConfPresence);
//#             outputStream.writeBoolean(msgLogConf);
//#else
            outputStream.writeUTF("");
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeBoolean(cp1251);
//#ifdef AUTOSTATUS
//#             outputStream.writeInt(autoAwayDelay);
//#else
            outputStream.writeInt(5);
//#endif
            outputStream.writeUTF(defGcRoom);
            outputStream.writeBoolean(firstRun);
            outputStream.writeInt(panelsState);
            outputStream.writeInt(confMessageCount);

            outputStream.writeBoolean(fileTransfer); //newMenu

            outputStream.writeBoolean(lightState);
            outputStream.writeBoolean(notifySound);
//#ifdef HISTORY
//#             outputStream.writeBoolean(lastMessages);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef AUTOSTATUS
//#             outputStream.writeBoolean(setAutoStatusMessage);
//#             outputStream.writeInt(autoAwayType);
//#else
            outputStream.writeBoolean(false);
            outputStream.writeInt(0);
//#endif
            outputStream.writeBoolean(autoScroll);
//#ifdef POPUPS
            outputStream.writeBoolean(popUps);
//#else
//#             outputStream.writeBoolean(false);
//#endif
            outputStream.writeBoolean(showResources);

            outputStream.writeBoolean(enableVersionOs);
            outputStream.writeInt(messageLimit);
            outputStream.writeUTF(lang);      
            outputStream.writeBoolean(eventDelivery);
//#ifdef DETRANSLIT
//#             outputStream.writeBoolean(transliterateFilenames);
//#else
            outputStream.writeBoolean(false);
//#endif

            outputStream.writeBoolean(rosterStatus);

            outputStream.writeBoolean(queryExit);
            outputStream.writeBoolean(notifyPicture);
            outputStream.writeBoolean(showBalloons);
//#ifdef USER_KEYS
//#             outputStream.writeBoolean(userKeys);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef LOGROTATE
//#             outputStream.writeInt(msglistLimit);
//#else
            outputStream.writeInt(512);
//#endif
            outputStream.writeBoolean(useTabs);
            outputStream.writeInt(autoSubscribe);
            outputStream.writeBoolean(useBoldFont);
//#ifdef RUNNING_MESSAGE
//#             outputStream.writeBoolean(notifyWhenMessageType);
//#else
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeBoolean(IQNotify); //IRC_LIKE
//#ifdef PEP
//#             outputStream.writeBoolean(sndrcvmood);
//#else
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeUTF("");//scheme
//#ifdef CLIPBOARD
//#             outputStream.writeBoolean(useClipBoard);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef PEP
//#             outputStream.writeBoolean(rcvtune);
//#else
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeInt(barFont);
            outputStream.writeInt(baloonFont);
            
            outputStream.writeUTF(verHash);
            outputStream.writeUTF(resolvedHost);
            outputStream.writeInt(resolvedPort);
            
//#ifdef DETRANSLIT
//#             outputStream.writeBoolean(autoDeTranslit);
//#else
            outputStream.writeBoolean(false);
//#endif
//#ifdef CLIENTS_ICONS
            outputStream.writeBoolean(showClientIcon);
//#else
//#             outputStream.writeBoolean(false);
//#endif
            
            outputStream.writeInt(reconnectCount);
            outputStream.writeInt(reconnectTime);
            
            outputStream.writeBoolean(executeByNum);
            
            outputStream.writeBoolean(showNickNames);
            
            outputStream.writeBoolean(adhoc);
            
//#ifdef PEP
//#             outputStream.writeBoolean(rcvactivity);
//#else
            outputStream.writeBoolean(false);
//#endif
            outputStream.writeBoolean(oldSE);
            
            outputStream.writeBoolean(showTimeTraffic);
            outputStream.writeBoolean(useLowMemory_msgedit);
            outputStream.writeBoolean(useLowMemory_userotator);
            outputStream.writeBoolean(useLowMemory_iconmsgcollapsed);
            outputStream.writeBoolean(drawCPhoto);
            outputStream.writeBoolean(auto_queryPhoto);
            outputStream.writeBoolean(sendMoodInMsg);   
  
            
            outputStream.writeUTF(moodText);
            outputStream.writeUTF(moodName);
            outputStream.writeUTF(actText);
            outputStream.writeUTF(actDescr); 
            outputStream.writeUTF(actCat);  
            
            outputStream.writeInt(maxAvatarHeight);       
            outputStream.writeInt(bgnd_image);
            
            outputStream.writeBoolean(use_drawed_font);     
            outputStream.writeUTF(drwd_fontname); 
            outputStream.writeBoolean(savePos);
            outputStream.writeBoolean(boldNicks);

            outputStream.writeInt(scrollWidth);
            outputStream.writeBoolean(drawScrollBgnd);
            outputStream.writeBoolean(isLegal);
            outputStream.writeBoolean(iconsLeft);
            outputStream.writeUTF(path_skin);
            
            outputStream.writeBoolean(useClassicChat);
            outputStream.writeInt(classic_chat_height);

            outputStream.writeInt(line_count);     

            outputStream.writeBoolean(use_phone_theme);
            outputStream.writeBoolean(gradient_cursor);
            outputStream.writeBoolean(altern_chat_colors);
            
            outputStream.writeInt(argb_bgnd);
            outputStream.writeInt(gmenu_bgnd);
            outputStream.writeInt(popup_bgnd);
            outputStream.writeInt(cursor_bgnd);
            
            outputStream.writeInt(avatar_cashe_size);
            outputStream.writeInt(difficulty_level);
            outputStream.writeInt(maxAvatarWidth);
            
            outputStream.writeBoolean(autoSaveVcard);
            outputStream.writeBoolean(showAvatarRect);
            outputStream.writeBoolean(autoload_FSPhoto);

            outputStream.writeBoolean(nokiaReconnectHack);
            outputStream.writeBoolean(timePresence);
            outputStream.writeBoolean(animateMenuAndRoster);
            
            outputStream.writeBoolean(cursivUse);
            outputStream.writeBoolean(dont_loadMC);
//#if BREDOGENERATOR                
//#             outputStream.writeBoolean(bredoGen);
//#endif            
            
            
	} catch (Exception e) { }
	
	NvStorage.writeFileRecord(outputStream, "config", 0, true);
    }

    
    public void updateTime(){
	Time.setOffset(gmtOffset);
    }

    
    private final void getPhoneManufacturer() {
        if (phoneManufacturer==NOT_DETECTED) {
            String platform=getPlatformName();
            phoneManufacturer=NONE;

            if (platform.endsWith("(NSG)")) {
                phoneManufacturer=SIEMENS;
                return;
            } else if (platform.startsWith("SIE")) {
                phoneManufacturer=SIEMENS2;
                return;
            } else if (platform.startsWith("Motorola-EZX")) {
                phoneManufacturer=MOTOEZX;
                return;
            } else if (platform.startsWith("Moto")) {
                phoneManufacturer=MOTO;
                return;
            } else if (platform.startsWith("SonyE")) {
                if (platform.startsWith("SonyEricssonM600")) {
                    phoneManufacturer=SONYE_M600;
                    return;
                }
                phoneManufacturer=SONYE;
                return;
//#if !ZLIB
//#             } else if (platform.indexOf("9@9")>-1) {
//#                 phoneManufacturer=XENIUM99;
//#                 return;
//#endif
            } else if (platform.startsWith("Windows")) {
                phoneManufacturer=WINDOWS;
                return;
            } else if (platform.startsWith("Nokia9500") || 
                platform.startsWith("Nokia9300") || 
                platform.startsWith("Nokia9300i")) {
                phoneManufacturer=NOKIA_9XXX;
                return;
            } else if (platform.startsWith("Nokia")) {
                phoneManufacturer=NOKIA;
                    if (platform.endsWith("5800")) {
                          phoneManufacturer=NOKIA_5800;
                          return;
                    }                
                return;
            } else if (platform.startsWith("Intent")) {
                phoneManufacturer=INTENT;
                return;
            } else if (platform.startsWith("wtk") || platform.endsWith("wtk")) {
                phoneManufacturer=WTK;
                return;
            } else if (platform.startsWith("Samsung")) {
                phoneManufacturer=SAMSUNG;
                return;
            } else if (platform.startsWith("LG")) {
                phoneManufacturer=LG;
                return;
            } else if (platform.startsWith("j2me")) {
                phoneManufacturer=J2ME;
                return;
            } else if (platform.startsWith("Jbed")) {
                phoneManufacturer=JBED;
//#ifdef FILE_IO
                try { FileIO f=FileIO.createConnection(""); } catch (Exception ex) { }
//#endif
                return;
            }else {
                phoneManufacturer=OTHER;
            }
        }
    }
    
    public static String getPlatformName() {
        if (platformName==null) {
            platformName=System.getProperty("microedition.platform");
            
            String sonyJava=System.getProperty("com.sonyericsson.java.platform");
            if (sonyJava!=null) platformName=platformName+"/"+sonyJava;
            
            String device=System.getProperty("device.model");
            String firmware=System.getProperty("device.software.version");
            
            if (platformName==null) platformName="Motorola";
            
             if (platformName.startsWith("j2me")) {
                if (device!=null) if (device.startsWith("wtk-emulator")) {
                     platformName=device;
                }
                if (device!=null && firmware!=null)
                    platformName="Motorola"; // buggy v360
		else {
		    // Motorola EZX phones
		    String hostname=System.getProperty("microedition.hostname");
		    if (hostname!=null) {
		        platformName="Motorola-EZX";
		        if (device!=null) {
		    	    // Motorola EZX ROKR
			    hostname=device;
                        }
                     
                        if (hostname.indexOf("(none)")<0)
                         platformName+="/"+hostname;
                    }
		}
             }
 	    //else 
		if (platformName.startsWith("Moto")) {
                if (device==null) device=System.getProperty("funlights.product");
                if (device!=null) platformName="Motorola-"+device;
            }

            if (platformName.indexOf("SIE") > -1) {
                platformName=System.getProperty("microedition.platform")+" (NSG)";
            } else if (System.getProperty("com.siemens.OSVersion")!=null) {
                platformName="SIE-"+System.getProperty("microedition.platform")+"/"+System.getProperty("com.siemens.OSVersion");
            }
            
            try {
                Class.forName("com.samsung.util.Vibration");
                platformName="Samsung";
            } catch (Throwable ex) { }
            
            try {
                Class.forName("mmpp.media.MediaPlayer");
                platformName="LG";
            } catch (Throwable ex) {
                try {
                    Class.forName("mmpp.phone.Phone");
                    platformName="LG";
                } catch (Throwable ex1) {
                    try {
                        Class.forName("mmpp.lang.MathFP");
                        platformName="LG";
                    } catch (Throwable ex2) {
                        try {
                            Class.forName("mmpp.media.BackLight");
                            platformName="LG";
                        } catch (Throwable ex3) { }
                    }
                }
            }
        }
      if(platformName.indexOf("/")>-1){
          return platformName.substring(0,platformName.indexOf("/"));  
      }
      return platformName;
    }

    public static String getOs() {
        return getPlatformName();
    }
    
    public final String getStringProperty(final String key, final String defvalue) {
	try {
	    String s=BombusQD.getInstance().getAppProperty(key);
	    return (s==null)?defvalue:s;
	} catch (Exception e) {	}
        return defvalue;
    }
    
    public final int getIntFromManifest(final String key, final int defvalue) {
	try {
	    String s=BombusQD.getInstance().getAppProperty(key);
	    return (s==null)?defvalue:Integer.parseInt(s);
	} catch (Exception e) {	}
        return defvalue;
    }
    
    public final int getIntProperty(final String key, final int defvalue) {
	try { return Integer.parseInt(key); } catch (Exception e) { }
	return defvalue;
    }

    
    public final boolean getBooleanProperty(final String key, final boolean defvalue) {
	try {
	    if (key.equals("true")) return true;
	    if (key.equals("yes")) return true;
	    if (key.equals("1")) return true;
            return false;
	} catch (Exception e) { }
        return defvalue;
    }
    
}
