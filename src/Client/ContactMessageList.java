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

public class ContactMessageList extends MessageList {
    
    Contact contact;

    private static Command cmdSubscribe=new Command(SR.MS_SUBSCRIBE, Command.SCREEN, 1);
    private static Command cmdUnsubscribed=new Command(SR.MS_DECLINE, Command.SCREEN, 2);
    private static Command cmdMessage=new Command(SR.MS_NEW_MESSAGE,Command.SCREEN,3);
    private static Command cmdResume=new Command(SR.MS_RESUME,Command.SCREEN,1);
    private static Command cmdReply=new Command(SR.MS_REPLY,Command.SCREEN,4);
    private static Command cmdQuote=new Command(SR.MS_QUOTE,Command.SCREEN,5);
//#ifdef ARCHIVE
    private static Command cmdArch=new Command(SR.MS_ADD_ARCHIVE,Command.SCREEN,6);
//#endif
    private static Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 7);
    private static Command cmdSelect=new Command(SR.MS_SELECT, Command.SCREEN, 8);
    private static Command cmdActions=new Command(SR.MS_CONTACT,Command.SCREEN,9);
    private static Command cmdActive=new Command(SR.MS_ACTIVE_CONTACTS,Command.SCREEN,10);
//#if TEMPLATES
//#     private static Command cmdTemplate=new Command(SR.MS_SAVE_TEMPLATE,Command.SCREEN,11);
//#endif
//#ifdef FILE_IO
    private static Command cmdSaveChat=new Command(SR.MS_SAVE_CHAT, Command.SCREEN, 12);
//#endif
//#ifdef HISTORY
//#ifdef HISTORY_READER
//#     private static Command cmdReadHistory=new Command("Read history", Command.SCREEN, 13);
//#endif
//# //        if (midlet.BombusQD.cf.lastMessages && !contact.isHistoryLoaded()) loadRecentList();
//#endif
//#ifdef CLIPBOARD    
//#     private static Command cmdSendBuffer=new Command(SR.MS_SEND_BUFFER, Command.SCREEN, 14);
//#endif

//#ifdef CLIPBOARD    
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//#endif
    private static Command cmdAddSearchQuery = new Command(SR.MS_ADD_SEARCH_QUERY, Command.SCREEN, 400);    
    private static Command cmdFind_ = new Command(SR.MS_FIND_TEXT+" ["+midlet.BombusQD.cf.find_text_str+"]", Command.SCREEN, 401);
    
    private static Command cmdTranslate=new Command(SR.MS_TRANSLATE, Command.SCREEN,402);  
    private static Command cmdClrPresences=new Command(SR.MS_DELETE_ALL_STATUSES, Command.SCREEN,403); 
//#if BREDOGENERATOR             
//#     private static Command cmdAutoGenON=new Command(SR.MS_BREDO_ON,Command.SCREEN,87);    
//#     private static Command cmdAutoGenOff=new Command(SR.MS_BREDO_OFF,Command.SCREEN,88);    
//#endif       
    private static Command cmdMyService=new Command(SR.MS_SERVICE, Command.SCREEN, 31);

    private boolean composing=true;

    private boolean startSelection;
    private boolean tr = false;

    /** Creates a new instance of MessageList */

    public ContactMessageList(Contact contact, Display display) {
        super(display);
        this.contact=contact;
        midlet.BombusQD.sd.roster.activeContact=contact;
        
        //MainBar mainbar=new MainBar(contact);
        setMainBarItem(new MainBar(contact));

        cursor=0;
        commandState();
        
        contact.setIncoming(0);
        contact.fileQuery=false;
//#ifdef HISTORY
//#ifdef LAST_MESSAGES
//#         if (midlet.BombusQD.cf.lastMessages && !contact.isHistoryLoaded()) loadRecentList();
//#endif
//#endif
        setCommandListener(this);
       if(tr){
           moveCursorTo(contact.getCursor(), true); 
           tr=false;
        }
       else{
        if (contact.msgs.size()>0){
          if(midlet.BombusQD.cf.savePos) {
            moveCursorTo(contact.getCursor(), true); 
          }
          else { 
            moveCursorTo(firstUnread(), true); 
          }
            contact.resetNewMsgCnt();
        }
       }
      if(contact.cList==null && midlet.BombusQD.cf.module_cashe && contact.msgs.size()>3){
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
         if(midlet.BombusQD.cf.find_text_str.length()>0){
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
//#             if (midlet.BombusQD.cf.useClipBoard) {
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
//#         if (midlet.BombusQD.cf.useClipBoard && !clipboard.isEmpty()) {
//#             addInCommand(3,cmdSendBuffer); cmdSendBuffer.setImg(0x84);
//#         }
//#endif
//#ifdef HISTORY
//#         if (midlet.BombusQD.cf.saveHistory)
//#             if (midlet.BombusQD.cf.msgPath!=null)
//#                 if (!midlet.BombusQD.cf.msgPath.equals(""))
//#                     if (contact.msgs.size()>0)
//#                         addInCommand(3,cmdSaveChat);  cmdSaveChat.setImg(0x44);
//#ifdef HISTORY_READER
//#         if (midlet.BombusQD.cf.saveHistory && midlet.BombusQD.cf.lastMessages)
//#             addInCommand(3,cmdReadHistory); cmdReadHistory.setImg(0x05);
//#endif
//#endif
        addCommand(cmdMyService); cmdMyService.setImg(0x27); 
//#ifndef GRAPHICS_MENU        
     addCommdand(cmdBack);
//#endif            
        
//#if BREDOGENERATOR                
//#         if(midlet.BombusQD.cf.bredoGen==true){
//#            addCommand(cmdAutoGenOff);
//#            removeCommand(cmdAutoGenON);
//#         }else{
//#            addCommand(cmdAutoGenON);
//#         }
//#endif     
    }
    
    public void showNotify(){
        midlet.BombusQD.sd.roster.activeContact=contact;
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

        midlet.BombusQD.sd.roster.countNewMsgs();
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
    

    public void commandAction(Command c, Displayable d){
        super.commandAction(c,d);
	//cf.clearedGrMenu=true;	
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
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;

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
//#            midlet.BombusQD.cf.bredoGen=true;
//#            display.setCurrent(midlet.BombusQD.sd.roster);
//#            VirtualList.setWobble(3, null, SR.MS_BREDO_ON);
//#         }
//#         if (c==cmdAutoGenOff) {
//#            midlet.BombusQD.cf.bredoGen=false;
//#            display.setCurrent(midlet.BombusQD.sd.roster);
//#            VirtualList.setWobble(3, null, SR.MS_BREDO_OFF);
//#         }  
//#endif         
        
        if(c==cmdFind_) {
            find_str(midlet.BombusQD.cf.find_text_str);
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
	if (c==cmdActive) {
            new ActiveContacts(display, this, null); 
        }
        
        if (c==cmdSubscribe) midlet.BombusQD.sd.roster.doSubscribe(contact);
		
        if (c==cmdUnsubscribed) midlet.BombusQD.sd.roster.sendPresence(contact.bareJid, "unsubscribed", null, false);

//#ifdef CLIPBOARD
//#         if (c==cmdSendBuffer) {
//#             String from=midlet.BombusQD.sd.account.toString();
//#             String body=clipboard.getClipBoard();
//# 
//#             String id=String.valueOf((int) System.currentTimeMillis());
//#             Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,null,body);
//#             msg.id=id;
//#             msg.itemCollapsed=true;
//#             
//#             try {
//#                 if (body!=null && body.length()>0) {
//#                     midlet.BombusQD.sd.roster.sendMessage(contact, id, body, null, null,false);
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
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;
//#ifdef RUNNING_MESSAGE
//#         switch(midlet.BombusQD.cf.msgEditType){
//#            case 0: midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, contact.msgSuspended); break;
//#            case 1: midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, contact.msgSuspended, true); break;
//#            case 2: midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, contact.msgSuspended, true); break;
//#         }        
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
                String query = midlet.BombusQD.cf.find_text_str;
                for (int i=0; i<(cursor+1); i++)
                {
                  if((getMessage(i).toString().indexOf(query)>-1))  
                  {
                    Msg m = getMessage(i);
                    m.search_word=false;
                    m.highlite=false;
                  }
                }
                display.setCurrent(midlet.BombusQD.sd.roster);
                midlet.BombusQD.cf.find_text=false;                
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
                    midlet.BombusQD.cf.find_text=true;
                    VirtualList.setWobble(1, null, "Results of Search:\nword: "+query+"\ncounts: "+vectorfound.size());
                    setMainBarItem(new MainBar("    Search: "+Integer.toString(1)+"/"+Integer.toString(vectorfound.size()) + " ..6>"));  
                }else{
                    display.setCurrent(midlet.BombusQD.sd.roster);
                    midlet.BombusQD.cf.find_text=false;
                    VirtualList.setWobble(3, null, SR.MS_NOT_FOUND);
                    moveCursorHome();
                }
    }
        
        
    
    
    public void keyPressed(int keyCode) {
     if(midlet.BombusQD.cf.savePos) { 
       if(keyCode==Config.KEY_BACK || keyCode==Config.SOFT_RIGHT || keyCode==KEY_NUM3){
         contact.setCursor(cursor);
       }
     }
     if(midlet.BombusQD.cf.find_text==false){        
        if (keyCode==KEY_POUND) {
//#ifndef WMUC
            if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT) {
                Reply(); return;
            }
    
//#ifdef JUICK.COM
//#             else{
//#               if(contact.getJid().indexOf("juick@juick.com")>-1){
//#                 Reply(); return;                  
//#               }
//#             }
//#endif             
            
//#endif
            keyGreen();
            return;
        }
      }
      super.keyPressed(keyCode);
   }

    public void userKeyPressed(int keyCode) {
     if(midlet.BombusQD.cf.find_text){
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
                if (midlet.BombusQD.cf.useTabs)
                    midlet.BombusQD.sd.roster.searchActiveContact(-1);
                else
                    super.pageLeft();
                contact.setCursor(cursor);
                break;
            case KEY_NUM0:
                int size = midlet.BombusQD.sd.roster.hContacts.size();
                if(midlet.BombusQD.cf.savePos) {
                  contact.setCursor(cursor);
                }
                Contact c;
                synchronized (midlet.BombusQD.sd.roster.hContacts) {
                for(int i=0;i<size;i++){
                        c = (Contact)midlet.BombusQD.sd.roster.hContacts.elementAt(i);
                        if (c.getNewMsgsCount()>0){
                           if(c.cList!=null && midlet.BombusQD.cf.module_cashe && c.msgs.size()>3){
                              display.setCurrent((ContactMessageList)c.cList); 
                           }else{
	                      new ContactMessageList(c,display).setParentView(midlet.BombusQD.sd.roster);     
                           }
                           break;
                        }
                    }
                  }                
                  break;
            case KEY_NUM6:
                if (midlet.BombusQD.cf.useTabs)
                    midlet.BombusQD.sd.roster.searchActiveContact(1);
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
//#     public void touchRightPressed(){ if (midlet.BombusQD.cf.oldSE) showGraphicsMenu(); else destroyView(); }    
//#     public void touchLeftPressed(){ if (midlet.BombusQD.cf.oldSE) keyGreen(); else showGraphicsMenu(); }
//#else
    public void touchRightPressed(){ if (cf.oldSE) showMenu(); else destroyView(); }    
    public void touchLeftPressed(){ if (cf.oldSE) keyGreen(); else showMenu(); }
//#endif      

//#endif
    
    private void Reply() {
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;
        
        try {
            Msg msg=getMessage(cursor);
            
            if (msg==null ||
                msg.messageType == Msg.MESSAGE_TYPE_OUT ||
                msg.messageType == Msg.MESSAGE_TYPE_SUBJ)
                keyGreen();
            else{
//#ifdef RUNNING_MESSAGE
//#                 
//#                String messg = msg.from+": "; 
//#ifdef JUICK.COM                
//#                if(msg.messageType==Msg.MESSAGE_TYPE_JUICK){
//#                     messg=msg.id;
//#                }
//#endif               
//#              switch(midlet.BombusQD.cf.msgEditType){
//#                  case 0: midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, messg); break;
//#                  case 1: midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, messg, true); break;
//#                  case 2: midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, messg, true); break;
//#              }                
//#              //midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, messg);
//#             }
//#             
//#else
            new MessageEdit(display, this, contact, msg.from+": ");
//#endif
        } catch (Exception e) {/*no messages*/}
    }
    
    private void Quote() {
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;
        
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
//#              switch(midlet.BombusQD.cf.msgEditType){
//#                  case 0: midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, msg); break;
//#                  case 1: midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, msg, true); break;
//#                  case 2: midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, msg, true); break;
//#              }             
//#             //midlet.BombusQD.sd.roster.me=new MessageEdit(display, this, contact, msg);
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
        contact.cList=null;
    }

    public void destroyView(){
//#ifdef GRAPHICS_MENU
//#            midlet.BombusQD.sd.roster.activeContact=null;
//#            midlet.BombusQD.sd.roster.reEnumRoster(); //to reset unread messages icon for this conference in roster
//#            if(midlet.BombusQD.cf.animatedSmiles) images.SmilesIcons.stopTimer();           
//#            if (display!=null) display.setCurrent(midlet.BombusQD.sd.roster);
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
