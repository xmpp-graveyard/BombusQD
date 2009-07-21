/*
 * ContactMessageList.java
 *
 * Created on 19.02.2005, 23:54
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
//#ifndef WMUC
import Conference.MucContact;
//#endif
//#ifdef HISTORY
//# import History.HistoryAppend;
//#ifdef LAST_MESSAGES
//# import History.HistoryStorage;
//#endif
//#ifdef HISTORY_READER
//# import History.HistoryReader;
//#endif
//#endif
import Menu.RosterItemActions;
import Messages.MessageList;
import images.RosterIcons;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;
import java.util.*;
import ui.VirtualList;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
import Menu.Command;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//#ifdef ARCHIVE
import Archive.MessageArchive;
//#endif
import io.TranslateSelect;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
import Colors.ColorTheme;
import ui.controls.form.CheckBox;
import net.jscience.math.MathFP;

public class ContactMessageList extends MessageList {
    
    Contact contact;

    Command cmdSubscribe=new Command(SR.MS_SUBSCRIBE, Command.SCREEN, 1);
    Command cmdUnsubscribed=new Command(SR.MS_DECLINE, Command.SCREEN, 2);
    Command cmdMessage=new Command(SR.MS_NEW_MESSAGE,Command.SCREEN,3);
    Command cmdResume=new Command(SR.MS_RESUME,Command.SCREEN,1);
    Command cmdReply=new Command(SR.MS_REPLY,Command.SCREEN,4);
    Command cmdQuote=new Command(SR.MS_QUOTE,Command.SCREEN,5);
//#ifdef ARCHIVE
    Command cmdArch=new Command(SR.MS_ADD_ARCHIVE,Command.SCREEN,6);
//#endif
    Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 7);
    Command cmdSelect=new Command(SR.MS_SELECT, Command.SCREEN, 8);
    Command cmdActions=new Command(SR.MS_CONTACT,Command.SCREEN,9);
    Command cmdActive=new Command(SR.MS_ACTIVE_CONTACTS,Command.SCREEN,10);
//#if TEMPLATES
//#     Command cmdTemplate=new Command(SR.MS_SAVE_TEMPLATE,Command.SCREEN,11);
//#endif
//#ifdef FILE_IO
    Command cmdSaveChat=new Command(SR.MS_SAVE_CHAT, Command.SCREEN, 12);
//#endif
//#ifdef HISTORY
//#ifdef HISTORY_READER
//#     Command cmdReadHistory=new Command("Read history", Command.SCREEN, 13);
//#endif
//# //        if (cf.lastMessages && !contact.isHistoryLoaded()) loadRecentList();
//#endif
//#ifdef CLIPBOARD    
//#     Command cmdSendBuffer=new Command(SR.MS_SEND_BUFFER, Command.SCREEN, 14);
//#endif

//#ifdef CLIPBOARD    
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//#endif
    Command cmdAddSearchQuery = new Command(SR.MS_ADD_SEARCH_QUERY, Command.SCREEN, 400);    
    Command cmdFind_ = new Command(SR.MS_FIND_TEXT+" ["+Config.getInstance().find_text_str+"]", Command.SCREEN, 401);
    
    Command cmdTranslate=new Command(SR.MS_TRANSLATE, Command.SCREEN,402);  
    Command cmdClrPresences=new Command(SR.MS_DELETE_ALL_STATUSES, Command.SCREEN,403); 
//#if BREDOGENERATOR             
//#     Command cmdAutoGenON=new Command(SR.MS_BREDO_ON,Command.SCREEN,87);    
//#     Command cmdAutoGenOff=new Command(SR.MS_BREDO_OFF,Command.SCREEN,88);    
//#endif       
    private Command cmdMyService=new Command(SR.MS_SERVICE, Command.SCREEN, 31);

   
    private Command cmdHardMode=new Command("BenchMark->", Command.SCREEN, 100); 
    
    private Command cmdHardMode1=new Command("Easy Mode", Command.SCREEN, 101);
    private Command cmdHardMode2=new Command("Normal Mode", Command.SCREEN, 102);
    private Command cmdHardMode3=new Command("Hard Mode", Command.SCREEN, 103);
    private Command cmdHardMode4=new Command("Very Hard Mode", Command.SCREEN, 104);
    private Command cmdHardMode5=new Command("Maniac Mode", Command.SCREEN, 105);    
    private Command cmdHardMode6=new Command("Ooo my God..", Command.SCREEN, 106);

    
    StaticData sd=StaticData.getInstance();
    
    private Config cf;
    
    private boolean composing=true;

    private boolean startSelection;
    private boolean tr = false;

    /** Creates a new instance of MessageList */

    public ContactMessageList(Contact contact, Display display) {
        super(display);
        this.contact=contact;
        sd.roster.activeContact=contact;

        cf=Config.getInstance();
        
        MainBar mainbar=new MainBar(contact);
        setMainBarItem(mainbar);

        cursor=0;
        commandState();
        
        contact.setIncoming(0);
        contact.fileQuery=false;
//#ifdef HISTORY
//#ifdef LAST_MESSAGES
//#         if (cf.lastMessages && !contact.isHistoryLoaded()) loadRecentList();
//#endif
//#endif
        setCommandListener(this);
       if(tr){
           moveCursorTo(contact.getCursor(), true); 
           tr=false;
        }
       else{
        if (contact.msgs.size()>0){
          if(Config.getInstance().savePos) {
            moveCursorTo(contact.getCursor(), true); 
          }
          else { 
            moveCursorTo(firstUnread(), true); 
          }
            contact.resetNewMsgCnt();
        }
       }
      if(contact.cList==null){
          contact.cList=this;
      }        
    }
    
    public int firstUnread(){
        int unreadIndex=0;
            int size=contact.msgs.size();
            for(int i=0;i<size;i++){
              if (((Msg)contact.msgs.elementAt(i)).unread)
                 break;
              unreadIndex++;
            }        
        return unreadIndex;
    }
    
    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();
//#endif
        if (startSelection) addCommand(cmdSelect); cmdSelect.setImg(0x60);
        if (contact.msgSuspended!=null) addCommand(cmdResume); cmdResume.setImg(0x80);
        
        if (cmdSubscribe==null) return;
        
        try {
            Msg msg=(Msg) contact.msgs.elementAt(cursor);
            if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
                addCommand(cmdSubscribe); cmdSubscribe.setImg(0x43);
                addCommand(cmdUnsubscribed); cmdUnsubscribed.setImg(0x41);
            }
        } catch (Exception e) {}
        
        addCommand(cmdMessage); cmdMessage.setImg(0x81);
        
          addInCommand(3,cmdTranslate); cmdTranslate.setImg(0x73);
          addInCommand(3,cmdClrPresences); cmdClrPresences.setImg(0x76);

        if (contact.msgs.size()>0) {
//#ifndef WMUC
            if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
                addCommand(cmdReply); cmdReply.setImg(0x72);
            }
//#endif
            addCommand(cmdQuote); cmdQuote.setImg(0x63);
            addInCommand(3,cmdPurge); cmdPurge.setImg(0x33);
         if(Config.getInstance().find_text_str.length()>0){
            removeInCommand(3,cmdAddSearchQuery); 
            addInCommand(3,cmdFind_); cmdFind_.setImg(0x82);
         }   
            addInCommand(3,cmdAddSearchQuery); cmdAddSearchQuery.setImg(0x83);
            
            
            if (!startSelection) addInCommand(3,cmdSelect); cmdSelect.setImg(0x60);
        
        if (contact.msgs.size()>0) {
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#          if (sd.Archive)
//#endif
            addCommand(cmdArch); cmdArch.setImg(0x64);
//#endif
//#if TEMPLATES
//#ifdef PLUGINS         
//#          if (sd.Archive)
//#endif
//#             addCommand(cmdTemplate);
//#endif
        }
            
//#ifdef CLIPBOARD
//#             if (cf.useClipBoard) {
//#                 addCommand(cmdCopy); cmdCopy.setImg(0x13);
//#                 if (!clipboard.isEmpty()) addCommand(cmdCopyPlus); cmdCopyPlus.setImg(0x23);
//#             }
//#endif
//#ifdef MENU_LISTENER
            if (isHasScheme()) 
//#endif
                addInCommand(3,cmdxmlSkin); cmdxmlSkin.setImg(0x07);
//#ifdef MENU_LISTENER
            if (isHasUrl()) 
//#endif
                addCommand(cmdUrl); cmdUrl.setImg(0x15);
        }
        
        if (contact.origin!=Contact.ORIGIN_GROUPCHAT)
            addCommand(cmdActions); cmdActions.setImg(0x16);
    
//#ifdef CLIPBOARD
//#         if (cf.useClipBoard && !clipboard.isEmpty()) {
//#             addInCommand(3,cmdSendBuffer); cmdSendBuffer.setImg(0x84);
//#         }
//#endif
//#ifdef HISTORY
//#         if (cf.saveHistory)
//#             if (cf.msgPath!=null)
//#                 if (!cf.msgPath.equals(""))
//#                     if (contact.msgs.size()>0)
//#                         addInCommand(3,cmdSaveChat);  cmdSaveChat.setImg(0x44);
//#ifdef HISTORY_READER
//#         if (cf.saveHistory && cf.lastMessages)
//#             addInCommand(3,cmdReadHistory); cmdReadHistory.setImg(0x05);
//#endif
//#endif
        addCommand(cmdMyService); cmdMyService.setImg(0x27); 
//#ifndef GRAPHICS_MENU        
     addCommdand(cmdBack);
//#endif            
        
//#if BREDOGENERATOR                
//#         if(Config.getInstance().bredoGen==true){
//#            addCommand(cmdAutoGenOff);
//#            removeCommand(cmdAutoGenON);
//#         }else{
//#            addCommand(cmdAutoGenON);
//#         }
//#endif     
  
         addCommand(cmdHardMode);    cmdHardMode.setImg(0x11);
              addInCommand(2,cmdHardMode1); cmdHardMode1.setImg(0x11);
              addInCommand(2,cmdHardMode2); cmdHardMode2.setImg(0x11);
              addInCommand(2,cmdHardMode3); cmdHardMode3.setImg(0x11);
              addInCommand(2,cmdHardMode4); cmdHardMode4.setImg(0x11);
              addInCommand(2,cmdHardMode5); cmdHardMode5.setImg(0x11);
              addInCommand(2,cmdHardMode6); cmdHardMode6.setImg(0x11);    
       
    }
    
    public void showNotify(){
        sd.roster.activeContact=contact;
//#ifdef LOGROTATE
//#         getRedraw(true);
//#endif
        super.showNotify();
//#ifndef MENU_LISTENER
//#         if (cmdResume==null) return;
//#         if (contact.msgSuspended==null)
//#             removeCommand(cmdResume);
//#         else
//#             addCommand(cmdResume);
//#         
//#         if (cmdSubscribe==null) return;
//#         try {
//#             Msg msg=(Msg) contact.msgs.elementAt(cursor); 
//#             if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
//#                 addCommand(cmdSubscribe);
//#                 addCommand(cmdUnsubscribed);
//#             }
//#             else {
//#                 removeCommand(cmdSubscribe);
//#                 removeCommand(cmdUnsubscribed);
//#             }
//#         } catch (Exception e) {}
//#endif
    }
    
    public void forceScrolling() { //by voffk
        if (contact.moveToLatest) {
            if (cursor==(messages.size()-1)) {
                contact.moveToLatest=false;
                moveCursorEnd();
            }
        }
    }

    protected void beginPaint(){
        markRead(cursor);
        forceScrolling();
    }   
    
    public void markRead(int msgIndex) {
	if (msgIndex>=getItemCount()) return;
        if (msgIndex<contact.lastUnread) return;

        sd.roster.countNewMsgs();
//#ifdef LOGROTATE
//#         getRedraw(contact.redraw);
//#endif
    }
//#ifdef LOGROTATE
//#     private void getRedraw(boolean redraw) {
//#         if (!redraw) return;
//#         
//#         contact.redraw=false;
//#         messages=null;
//#         messages=new Vector();
//#         redraw();
//#     }
//#endif
    public int getItemCount(){ return contact.msgs.size(); }

    public Msg getMessage(int index) {
        if (index> getItemCount()-1) return null;
	if (((Msg)contact.msgs.elementAt(index)).unread) contact.resetNewMsgCnt();
	((Msg)contact.msgs.elementAt(index)).unread=false;
	return ((Msg)contact.msgs.elementAt(index));
    }
    
    public void focusedItem(int index){ 
        markRead(index); 
    }
    
  
    
    public int getMainColor(int index) {
        switch (index) {
            case 0: return ColorTheme.getColor(ColorTheme.CONTACT_CHAT);
            case 1: return ColorTheme.getColor(ColorTheme.CONTACT_AWAY);
            case 2: return ColorTheme.getColor(ColorTheme.CONTACT_XA);
            case 3: return ColorTheme.getColor(ColorTheme.CONTACT_DND);
            case 4: return ColorTheme.getColor(ColorTheme.CONTACT_DEFAULT);
        }
        return ColorTheme.getColor(ColorTheme.CONTACT_DEFAULT);
    }
    
///////////////////////////////////////////////////////////

    void run(String modeName,int mode){
       clearReadedMessageList(); 
             Vector statistics = new Vector();
             StringBuffer sb = new StringBuffer();
                long undo_free = Runtime.getRuntime().freeMemory()>>10;
                long undo_total = Runtime.getRuntime().totalMemory()>>10; 
                long run1 = 0;
                long run2 = 0;
              long s1 = System.currentTimeMillis();   
                runBenchmark(mode,true);
                runBenchmark(mode,false);
              long s2 = System.currentTimeMillis();
              long results = s2-s1;
              
                int objects = mode*2;
                
                long after_free = Runtime.getRuntime().freeMemory()>>10;
                long after_total = Runtime.getRuntime().totalMemory()>>10;              
                String mem_using = "MemoryUsing" +":%"+
                     "Undo: " + Long.toString(undo_free) + "/" + Long.toString(undo_total) + " kb" + "%" +
                     "After: "+ Long.toString(after_free)+ "/" + Long.toString(after_total) +" kb";             

                     statistics.addElement(new CheckBox("Benchmark Type" +":%"+modeName, true, true));
                       sb.append("Application: BombusQD "+Info.Version.getVersionLang());
                       sb.append("\nQD Benchmark v0.5\nType: " +modeName);
                       sb.append("\nRunned on: " + Config.getOs());
                       sb.append("\nMemUsing(Free/Total):" +
                         "\n[Undo: " + Long.toString(undo_free) + "/" + Long.toString(undo_total) + " kb]" +
                         "\n[After: "+ Long.toString(after_free)+ "/" + Long.toString(after_total) +" kb]"); 
                     
                     statistics.addElement(new CheckBox("Created Objects Time: "+Integer.toString(objects)+"%" + 
                     "Summary: " + MathFP.toString( MathFP.div(MathFP.toFP(results),MathFP.toFP(1000)),3) + " sec" , true, true));
                      
                       sb.append("\nCreated "+Integer.toString(objects)+" objects\nSummary Time: " + 
                       MathFP.toString( MathFP.div(MathFP.toFP(results),MathFP.toFP(1000)) ,3)+ " sec");
                
                long secs = MathFP.div(MathFP.toFP(results),MathFP.toFP(1000));
                long cc = MathFP.div(MathFP.toFP(MathFP.toString(secs)),MathFP.toFP(objects));
                
                     statistics.addElement(new CheckBox("Creating Speed for 1 object:%" + 
                     MathFP.toString(cc)  + " sec", true, true));
                        sb.append("\nCrSp01: " + MathFP.toString(cc,6)  + " sec");
                     

                     statistics.addElement(new CheckBox(mem_using, true, true));

             sd.roster.sendMessage(contact, null , sb.toString() , null , null ,true);
             new CommandForm(display,this,5,"Congratulations!",sb.toString(),statistics);
  }
    
    
    void runBenchmark(int count,boolean isMessage){
        //long s1 = System.currentTimeMillis();
            Random rand_isnow = new Random();
            Random rand = new Random();
            Random nick = new Random();
            String[] nickname = { 
                       "nikkey","baks","trand","journey","girls",
                       "jimmo","silvestro","phoneIsBack","masyaN","jailfun","monkey"
            };            
            String[] qd_is_now = { 
                       "chat","away","xa","dnd","default"
            };            
            String[] qd_offline_status = { 
                       "simple message presence text","hello world","yayayayayaya",
                       "this very very very very very very very very long message presence text from HZ!!",
                       "achtung detected!"
            };
            int i=0;
            try{
              for (i=0; i<count; i++) {
                 int k = Math.abs(rand.nextInt()) % 5;
                 int l = Math.abs(rand_isnow.nextInt()) % 5;
                 int n = Math.abs(nick.nextInt()) % 11;
                 if(isMessage){
                   Msg msg=new Msg(Msg.MESSAGE_TYPE_IN,nickname[n],null,qd_offline_status[k]);
                   contact.addMessage(msg);                   
                 }else{
                   Msg msg=new Msg(Msg.MESSAGE_TYPE_PRESENCE,"random",null,nickname[n]+" is now "+qd_is_now[l]+
                      " ("+qd_offline_status[k]+")");
                   msg.color=getMainColor(k);
                   contact.addMessage(msg);
                  }
                 moveCursorEnd();//try { Thread.sleep(100); } catch (Exception e) {}; //25*300=7.5 sec
                }
             } catch(OutOfMemoryError eom){
             }            
         //  long s2 = System.currentTimeMillis();
         //return s2-s1;
    }
///////////////////////////////////////////////////////////    
    
    

    
    public void commandAction(Command c, Displayable d){
        super.commandAction(c,d);
	//cf.clearedGrMenu=true;	
        /** login-insensitive commands */
   
        if (c==cmdHardMode1) { 
           run("Easy Mode",50);
        }
        
        if (c==cmdHardMode2) { 
           run("Normal Mode",100);
        }
        
        if (c==cmdHardMode3) { 
           run("Hard Mode",200);
        }  
        
        if (c==cmdHardMode4) { 
           run("Very Hard Mode",400);
        }         
        
        if (c==cmdHardMode5) { 
           run("Maniac Mode",800);
        }    
        
        if (c==cmdHardMode6) { 
           run("Ooo my God..",1600);
        }   
     
//#ifdef ARCHIVE
        if (c==cmdArch) {
            try {
                MessageArchive.store(getMessage(cursor),1);
            } catch (Exception e) {/*no messages*/}
        }
//#endif
//#if TEMPLATES
//#         if (c==cmdTemplate) {
//#             try {
//#                 MessageArchive.store(getMessage(cursor),2);
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
        if(c==cmdClrPresences){
            for (Enumeration select=contact.msgs.elements(); select.hasMoreElements();) {
                    Msg msg=(Msg) select.nextElement();
                    if (msg.isPresence()) {
                        contact.msgs.removeElement(msg);
                        new ContactMessageList(contact,display);
                    }
            }            
        }
        
        if(c==cmdTranslate){
            String body = getMessage(cursor).body.toString();
            if(body.indexOf(">")>-1){
              String nick = body.substring(0,body.indexOf(">"));
              new TranslateSelect(display,this,contact,
                      getMessage(cursor).body.substring(body.indexOf(">")+1,body.length()).trim(),nick,
                      true,cursor);                
            }else{
              new TranslateSelect(display,this,contact,getMessage(cursor).body.toString(),"none",true,cursor);                
            }

            tr=true;
            contact.setCursor(cursor);
        }

        if (c==cmdPurge) {
            if (messages.isEmpty()) return;
            
            if (startSelection) {
                for (Enumeration select=contact.msgs.elements(); select.hasMoreElements(); ) {
                    Msg mess=(Msg) select.nextElement();
                    if (mess.selected) {
                        contact.msgs.removeElement(mess);
                    }
                }
                startSelection = false;
                messages=null;
                messages=new Vector();
            } else {
                clearReadedMessageList();
            }
        }
        if (c==cmdSelect) {
            startSelection=true;
            Msg mess=((Msg) contact.msgs.elementAt(cursor));
            mess.selected = !mess.selected;
            mess.oldHighlite = mess.highlite;
            mess.highlite = mess.selected;
            return;
        }
//#ifdef HISTORY
//#ifdef HISTORY_READER
//#         if (c==cmdReadHistory) {
//#             new HistoryReader(display,contact);
//#             return;
//#         }
//#endif
//#endif
//#if (FILE_IO && HISTORY)
//#         if (c==cmdSaveChat) saveMessages();
//#endif
        /** login-critical section */
        if (!sd.roster.isLoggedIn()) return;

        if (c==cmdMessage) { 
            contact.msgSuspended=null;
            keyGreen(); 
        }
        if (c==cmdResume) {
            keyGreen();
        }
        if (c==cmdQuote) Quote();
        if (c==cmdReply) Reply();
        if(c==cmdAddSearchQuery) {
             new SearchText(display,d,contact);
             return;
        } 
//#if BREDOGENERATOR                 
//#         if (c==cmdAutoGenON) {
//#            Config.getInstance().bredoGen=true;
//#            display.setCurrent(StaticData.getInstance().roster);
//#            VirtualList.setWobble(3, null, SR.MS_BREDO_ON);
//#         }
//#         if (c==cmdAutoGenOff) {
//#            Config.getInstance().bredoGen=false;
//#            display.setCurrent(StaticData.getInstance().roster);
//#            VirtualList.setWobble(3, null, SR.MS_BREDO_OFF);
//#         }  
//#endif         
        
        if(c==cmdFind_) {
            find_str(Config.getInstance().find_text_str);
        }          
        if (c==cmdActions) {
//#ifndef WMUC
            if (contact instanceof MucContact) {
                MucContact mc=(MucContact) contact;
                new RosterItemActions(display, this, mc, -1);
            } else
//#endif
                new RosterItemActions(display, this, contact, -1);
        }
	if (c==cmdActive) new ActiveContacts(display, this, contact);
        
        if (c==cmdSubscribe) sd.roster.doSubscribe(contact);
		
        if (c==cmdUnsubscribed) sd.roster.sendPresence(contact.bareJid, "unsubscribed", null, false);

//#ifdef CLIPBOARD
//#         if (c==cmdSendBuffer) {
//#             String from=sd.account.toString();
//#             String body=clipboard.getClipBoard();
//# 
//#             String id=String.valueOf((int) System.currentTimeMillis());
//#             Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,null,body);
//#             msg.id=id;
//#             msg.itemCollapsed=true;
//#             
//#             try {
//#                 if (body!=null && body.length()>0) {
//#                     sd.roster.sendMessage(contact, id, body, null, null,false);
//#                     if (contact.origin<Contact.ORIGIN_GROUPCHAT) contact.addMessage(msg);
//#                 }
//#             } catch (Exception e) {
//#                 contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,null,"clipboard NOT sended"));
//#             }
//#             redraw();
//#         }
//#endif
    }

    public void clearReadedMessageList() {
        smartPurge();
        messages.removeAllElements();
        cursor=0;
        moveCursorHome();
        redraw();
    }
    
    public void eventLongOk(){
        super.eventLongOk();
//#ifndef WMUC
        if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
            Reply();
            return;
        }
//#endif
        keyGreen();
    }
    
    public void keyGreen(){
        if (!sd.roster.isLoggedIn()) return;
//#ifdef RUNNING_MESSAGE
//#         sd.roster.me=new MessageEdit(display, this, contact, contact.msgSuspended);
//#else
    new MessageEdit(display, this, contact, contact.msgSuspended);
//#endif
    }
    
    protected void keyClear(){
        if (!messages.isEmpty())
            clearReadedMessageList();
    }
    
    public void keyRepeated(int keyCode) {
        if (keyCode==KEY_NUM0) 
            clearReadedMessageList();
	else 
            super.keyRepeated(keyCode);
    }  

    
    Vector vectorfound = new Vector();
    int found_count=0;
    
    
    
    private void clear_results(){
                moveCursorEnd();
                String query = Config.getInstance().find_text_str;
                for (int i=0; i<(cursor+1); i++)
                {
                  if((getMessage(i).toString().indexOf(query)>-1))  
                  {
                    Msg m = getMessage(i);
                    m.search_word=false;
                    m.highlite=false;
                  }
                }
                display.setCurrent(StaticData.getInstance().roster);
                Config.getInstance().find_text=false;                
                moveCursorHome();
    }
    
    
    private void find_str(String query){
                moveCursorEnd();
                setMainBarItem(new MainBar("       Wait!"));                
                for (int i=0; i<(cursor+1); i++)
                {
                  if((getMessage(i).toString().indexOf(query)>-1))
                  {
           	    vectorfound.addElement(Integer.toString(i));
                    Msg m = getMessage(i);
                    m.search_word=true; //image
                    m.highlite=true;
                  }
                }
                if(vectorfound.size()>0) { 
                    int cursor_index = Integer.parseInt(vectorfound.elementAt(found_count).toString());
                    moveCursorTo(cursor_index, true);
                    Config.getInstance().find_text=true;
                    VirtualList.setWobble(1, null, "Results of Search:\nword: "+query+"\ncounts: "+vectorfound.size());
                    setMainBarItem(new MainBar("    Search: "+Integer.toString(1)+"/"+Integer.toString(vectorfound.size()) + " ..6>"));  
                }else{
                    display.setCurrent(StaticData.getInstance().roster);
                    Config.getInstance().find_text=false;
                    VirtualList.setWobble(3, null, SR.MS_NOT_FOUND);
                    moveCursorHome();
                }
    }
        
        
    
    
    public void keyPressed(int keyCode) {
     if(Config.getInstance().savePos) { 
       if(keyCode==Config.KEY_BACK || keyCode==Config.SOFT_RIGHT || keyCode==KEY_NUM3){
         contact.setCursor(cursor);
       }
     }
     if(Config.getInstance().find_text==false){        
        if (keyCode==KEY_POUND) {
//#ifndef WMUC
            if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
                Reply();
                return;
            }
//#endif
            keyGreen();
            return;
        }
      }
      super.keyPressed(keyCode);
   }

    public void userKeyPressed(int keyCode) {
     if(Config.getInstance().find_text){
          String whatPress = "<[4]..[6]>";  
          switch (keyCode) {
              case KEY_NUM4: {
                     if(found_count>0){
                        found_count--;
                      } else { 
                          VirtualList.setWobble(1, null, SR.MS_END_SEARCH);  
                          clear_results();               
                      }
                      if(found_count==0) { whatPress = "..[6]>"; }
                      int cursor_index = Integer.parseInt(vectorfound.elementAt(found_count).toString());  
                      moveCursorTo(cursor_index, true);
                      setMainBarItem(new MainBar("    "+SR.MS_SEARCH+": "+
                              Integer.toString(found_count+1)+"/"+Integer.toString(vectorfound.size()) + "   " + whatPress));
                      redraw();      
               break;       
              }  
              case KEY_NUM6: {
                      if(found_count<vectorfound.size()-1){
                        found_count++;
                      } else { 
                          VirtualList.setWobble(1, null, SR.MS_END_SEARCH);   
                          clear_results();
                      }
                      int cursor_index = Integer.parseInt(vectorfound.elementAt(found_count).toString());   
                      moveCursorTo(cursor_index, true); 
                      if(found_count==vectorfound.size()-1) { whatPress = "<[4].."; }
                      setMainBarItem(new MainBar("    "+SR.MS_SEARCH+": "+
                              Integer.toString(found_count+1)+"/"+Integer.toString(vectorfound.size())+ "   " + whatPress));
                      redraw();        
              break;         
             }       
          }            
       }
       else
       {
          switch (keyCode) {
            case KEY_NUM4:
                if (cf.useTabs)
                    sd.roster.searchActiveContact(-1);
                else
                    super.pageLeft();
                contact.setCursor(cursor);
                break;
            case KEY_NUM0:
                int size = StaticData.getInstance().roster.hContacts.size();
                if(Config.getInstance().savePos) {
                  contact.setCursor(cursor);
                }
                Contact c;
                synchronized (StaticData.getInstance().roster.hContacts) {
                for(int i=0;i<size;i++){
                        c = (Contact)StaticData.getInstance().roster.hContacts.elementAt(i);
                        if (c.getNewMsgsCount()>0){
                           if(c.cList!=null){
                              display.setCurrent(c.cList); 
                           }else{
	                      new ContactMessageList(c,display).setParentView(sd.roster);     
                           }
                           break;
                        }
                    }
                  }                
                  break;
            case KEY_NUM6:
                if (cf.useTabs)
                    sd.roster.searchActiveContact(1);
                else
                    super.pageRight();
                contact.setCursor(cursor);
                break;
            case KEY_NUM3:
                new ActiveContacts(display, this, contact);
                contact.setCursor(cursor);
                break;        
            case KEY_NUM9:
                Quote();
                break;
          }
       }
    }

//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
//#     public void touchRightPressed(){ if (cf.oldSE) showGraphicsMenu(); else destroyView(); }    
//#     public void touchLeftPressed(){ if (cf.oldSE) keyGreen(); else showGraphicsMenu(); }
//#else
    public void touchRightPressed(){ if (cf.oldSE) showMenu(); else destroyView(); }    
    public void touchLeftPressed(){ if (cf.oldSE) keyGreen(); else showMenu(); }
//#endif      

//#endif
    
    private void Reply() {
        if (!sd.roster.isLoggedIn()) return;
        
        try {
            Msg msg=getMessage(cursor);
            
            if (msg==null ||
                msg.messageType == Msg.MESSAGE_TYPE_OUT ||
                msg.messageType == Msg.MESSAGE_TYPE_SUBJ)
                keyGreen();
            else
//#ifdef RUNNING_MESSAGE
//#                 sd.roster.me=new MessageEdit(display, this, contact, msg.from+": ");
//#else
            new MessageEdit(display, this, contact, msg.from+": ");
//#endif
        } catch (Exception e) {/*no messages*/}
    }
    
    private void Quote() {
        if (!sd.roster.isLoggedIn()) return;
        
        try {
            String msg=new StringBuffer()
                .append("Quote:\n")
                .append((char)0xab) //
                .append("")
                .append(getMessage(cursor).quoteString())
                .append((char)0xbb)
                .append("\n")
                .toString();
//#ifdef RUNNING_MESSAGE
//#             sd.roster.me=new MessageEdit(display, this, contact, msg);
//#else
            new MessageEdit(display, this, contact, msg);
//#endif
            msg=null;
        } catch (Exception e) {/*no messages*/}
    }
    
//#ifdef HISTORY
//#ifdef LAST_MESSAGES
//#     public void loadRecentList() {
//#         contact.setHistoryLoaded(true);
//#         HistoryStorage hs = new HistoryStorage(contact.bareJid);
//#         Vector history=hs.importData();
//# 
//#         for (Enumeration messages2=history.elements(); messages2.hasMoreElements(); )  {
//#             Msg message=(Msg) messages2.nextElement();
//#             if (!isMsgExists(message)) {
//#                 message.history=true;
//#                 contact.msgs.insertElementAt(message, 0);
//#             }
//#             message=null;
//#         }
//#         history=null;
//#     }
//# 
//#     private boolean isMsgExists(Msg msg) {
//#          for (Enumeration messages=contact.msgs.elements(); messages.hasMoreElements(); )  {
//#             Msg message=(Msg) messages.nextElement();
//#             if (message.body.equals(msg.body)) {
//#                 return true;
//#             }
//#             message=null;
//#          }
//#         return false;
//#     }
//#endif
//# 
//#     private void saveMessages() {
//#         StringBuffer histRecord=new StringBuffer("chatlog_");
//#ifndef WMUC
//#         if (contact instanceof MucContact) {
//#             if (contact.origin>=Contact.ORIGIN_GROUPCHAT) {
//#                 histRecord.append(contact.bareJid);
//#             } else {
//#                 String nick=contact.getJid();
//#                 int rp=nick.indexOf('/');
//#                 histRecord.append(nick.substring(rp+1)).append("_").append(nick.substring(0, rp));
//#                 nick=null;
//#             }
//#         } else {
//#endif
//#             histRecord.append(contact.bareJid);
//#ifndef WMUC
//#         }
//#endif
//#         StringBuffer messageList=new StringBuffer();
//#         if (startSelection) {
//#             for (Enumeration select=contact.msgs.elements(); select.hasMoreElements(); ) {
//#                 Msg mess=(Msg) select.nextElement();
//#                 if (mess.selected) {
//#                     messageList.append(mess.quoteString()).append("\n").append("\n");
//#                     mess.selected=false;
//#                     mess.highlite = mess.oldHighlite;
//#                 }
//#             }
//#             startSelection = false;
//#         } else {
//#             for (Enumeration cmessages=contact.msgs.elements(); cmessages.hasMoreElements(); ) {
//#                 Msg message=(Msg) cmessages.nextElement();
//#                 messageList.append(message.quoteString()).append("\n").append("\n");
//#             }
//#         }
//#         HistoryAppend.getInstance().addMessageList(messageList.toString(), histRecord.toString());
//#         messageList=null;
//#         histRecord=null;
//#     }
//#endif
    
    public final void smartPurge() {
        Vector msgs=contact.msgs;
        int cur=cursor+1;
        try {
            if (msgs.size()>0){
                int virtCursor=msgs.size();
                boolean delete = false;
                int i=msgs.size();
                while (true) {
                    if (i<0) break;

                    if (i<cur) {
                        if (!delete) {
                            if (((Msg)msgs.elementAt(virtCursor)).dateGmt+1000<System.currentTimeMillis()) {
                                msgs.removeElementAt(virtCursor);
                                delete=true;
                            }
                        } else {
                            msgs.removeElementAt(virtCursor);
                        }
                    }
                    virtCursor--;
                    i--;
                }
                contact.activeMessage=msgs.size()-1;
            }
        } catch (Exception e) { }
        
        contact.clearVCard();
        
        contact.lastSendedMessage=null;
        contact.lastUnread=0;
        contact.resetNewMsgCnt();
    }

    public void destroyView(){
//#ifdef GRAPHICS_MENU
//#            sd.roster.activeContact=null;
//#            sd.roster.reEnumRoster(); //to reset unread messages icon for this conference in roster
//#            if (display!=null) display.setCurrent(sd.roster);              
//#else
        sd.roster.activeContact=null;
        sd.roster.reEnumRoster(); //to reset unread messages icon for this conference in roster
        if (display!=null) display.setCurrent(sd.roster);
//#endif        
    }
    
//#ifdef MENU_LISTENER
    
    
//#ifdef GRAPHICS_MENU 
//#     
//#     public int showGraphicsMenu() {
//#          GMenuConfig.getInstance().itemGrMenu = GMenu.CONTACT_MSGS_LIST;
//#          commandState();
//#          new GMenu(display, this, this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
//#          redraw();
//#         return GMenu.CONTACT_MSGS_LIST;
//#     }
//#else
    public void showMenu() {
         commandState();
         super.showMenu();
    }   
//#endif      

    

    
    public boolean isHasScheme() {
        if (contact.msgs.size()<1) {
            return false;
        }
        String body=((Msg) contact.msgs.elementAt(cursor)).body;
        
        if (body.indexOf("xmlSkin")>-1) return true;
        return false;
    }

    public boolean isHasUrl() {
        if (contact.msgs.size()<1) {
            return false;
        }
        String body=((Msg) contact.msgs.elementAt(cursor)).body;
        if (body.indexOf("http://")>-1) return true;
        if (body.indexOf("https://")>-1) return true;
        if (body.indexOf("ftp://")>-1) return true;
        if (body.indexOf("tel:")>-1) return true;
        if (body.indexOf("native:")>-1) return true;
        return false;
    }
//#endif
}
