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
//#ifdef ARCHIVE
import Archive.MessageArchive;
//#endif
import io.TranslateSelect;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
import Colors.ColorTheme;

public class ContactMessageList extends MessageList {
    
    Contact contact;

    private Command cmdFind_ = new Command(SR.MS_FIND_TEXT+" ["+midlet.BombusQD.cf.find_text_str+"]", Command.SCREEN, 401);

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
        //setCommandListener(this);
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
        if (startSelection) addCommand(midlet.BombusQD.commands.cmdSelect); 
        if (contact.msgSuspended!=null) addCommand(midlet.BombusQD.commands.cmdResume); 
        
        if (midlet.BombusQD.commands.cmdSubscribe==null) return;
        
        try {
            Msg msg=(Msg) contact.msgs.elementAt(cursor);
            if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
                addCommand(midlet.BombusQD.commands.cmdSubscribe); 
                addCommand(midlet.BombusQD.commands.cmdUnsubscribed); 
            }
        } catch (Exception e) {}
        
        addCommand(midlet.BombusQD.commands.cmdMessage); 
        
          addInCommand(3,midlet.BombusQD.commands.cmdTranslate); 
          addInCommand(3,midlet.BombusQD.commands.cmdClrPresences); 

        if (contact.msgs.size()>0) {
//#ifndef WMUC
            if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT
                    || contact.getJid().indexOf("juick@juick.com")>-1 ) {
                addCommand(midlet.BombusQD.commands.cmdReply);
            }
//#endif
            addCommand(midlet.BombusQD.commands.cmdQuote); 
            addInCommand(3,midlet.BombusQD.commands.cmdPurge); 
         if(midlet.BombusQD.cf.find_text_str.length()>0){
            removeInCommand(3,midlet.BombusQD.commands.cmdAddSearchQuery); 
            addInCommand(3,cmdFind_); cmdFind_.setImg(0x82);
         }   
            addInCommand(3,midlet.BombusQD.commands.cmdAddSearchQuery); 
            
            
            if (!startSelection) addInCommand(3,midlet.BombusQD.commands.cmdSelect); 
        
        if (contact.msgs.size()>0) {
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#          if (sd.Archive)
//#endif
            addCommand(midlet.BombusQD.commands.cmdArch); 
//#endif
//#if TEMPLATES
//#ifdef PLUGINS         
//#          if (sd.Archive)
//#endif
//#             addCommand(midlet.BombusQD.commands.cmdTemplate);
//#endif
        }
            
//#ifdef CLIPBOARD
//#             if (midlet.BombusQD.cf.useClipBoard) {
//#                 addCommand(midlet.BombusQD.commands.cmdCopy); 
//#                 if (!midlet.BombusQD.clipboard.isEmpty()) addCommand(midlet.BombusQD.commands.cmdCopyPlus); 
//#             }
//#endif
//#ifdef MENU_LISTENER
            if (isHasScheme()) 
//#endif
                addInCommand(3,midlet.BombusQD.commands.cmdxmlSkin); 
//#ifdef MENU_LISTENER
            if (isHasUrl()) 
//#endif
                addCommand(midlet.BombusQD.commands.cmdUrl); 
        }
        
        if (contact.origin!=Contact.ORIGIN_GROUPCHAT)
            addCommand(midlet.BombusQD.commands.cmdActions); 
    
//#ifdef CLIPBOARD
//#         if (midlet.BombusQD.cf.useClipBoard && !midlet.BombusQD.clipboard.isEmpty()) {
//#             addInCommand(3,midlet.BombusQD.commands.cmdSendBuffer); 
//#         }
//#endif
//#ifdef HISTORY
//#         if (midlet.BombusQD.cf.saveHistory)
//#             if (midlet.BombusQD.cf.msgPath!=null)
//#                 if (!midlet.BombusQD.cf.msgPath.equals(""))
//#                     if (contact.msgs.size()>0)
//#                         addInCommand(3,midlet.BombusQD.commands.cmdSaveChat);  
//#ifdef HISTORY_READER
//#         if (midlet.BombusQD.cf.saveHistory && midlet.BombusQD.cf.lastMessages)
//#             addInCommand(3,midlet.BombusQD.commands.cmdReadHistory); 
//#endif
//#endif
        addCommand(midlet.BombusQD.commands.cmdMyService); 
//#ifndef GRAPHICS_MENU        
     addCommdand(midlet.BombusQD.commands.cmdBack);
//#endif            
        
//#if BREDOGENERATOR                
//#         if(midlet.BombusQD.cf.bredoGen==true){
//#            addCommand(midlet.BombusQD.commands.cmdAutoGenOff);
//#            removeCommand(midlet.BombusQD.commands.cmdAutoGenON);
//#         }else{
//#            addCommand(midlet.BombusQD.commands.cmdAutoGenON);
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
//#         if (midlet.BombusQD.commands.cmdResume==null) return;
//#         if (contact.msgSuspended==null)
//#             removeCommand(midlet.BombusQD.commands.cmdResume);
//#         else
//#             addCommand(midlet.BombusQD.commands.cmdResume);
//#         
//#         if (midlet.BombusQD.commands.cmdSubscribe==null) return;
//#         try {
//#             Msg msg=(Msg) contact.msgs.elementAt(cursor); 
//#             if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
//#                 addCommand(midlet.BombusQD.commands.cmdSubscribe);
//#                 addCommand(midlet.BombusQD.commands.cmdUnsubscribed);
//#             }
//#             else {
//#                 removeCommand(midlet.BombusQD.commands.cmdSubscribe);
//#                 removeCommand(midlet.BombusQD.commands.cmdUnsubscribed);
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
//#         messages=new Vector(0);
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
        if (c==midlet.BombusQD.commands.cmdArch) {
            try {
                MessageArchive.store(replaceNickTags(getMessage(cursor)),1);
            } catch (Exception e) {/*no messages*/}
        }
//#endif
//#if TEMPLATES
//#         if (c==midlet.BombusQD.commands.cmdTemplate) {
//#             try {
//#                 MessageArchive.store(replaceNickTags(getMessage(cursor)),2);
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
        if(c==midlet.BombusQD.commands.cmdClrPresences){
            for (Enumeration select=contact.msgs.elements(); select.hasMoreElements();) {
                    Msg msg=(Msg) select.nextElement();
                    if (msg.isPresence()) {
                        contact.msgs.removeElement(msg);
                        new ContactMessageList(contact,display);
                    }
            }            
        }
        
        if(c==midlet.BombusQD.commands.cmdTranslate){
            String body = replaceNickTags(getMessage(cursor)).body.toString();
            if(body.indexOf(">")>-1){
              String nick = body.substring(0,body.indexOf(">"));
              new TranslateSelect(display,this,contact,
                      replaceNickTags(getMessage(cursor)).body.substring(body.indexOf(">")+1,body.length()).trim(),nick,
                      true,cursor);                
            }else{
              new TranslateSelect(display,this,contact,replaceNickTags(getMessage(cursor)).body.toString(),"none",true,cursor);                
            }

            tr=true;
            contact.setCursor(cursor);
        }

        if (c==midlet.BombusQD.commands.cmdPurge) {
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
                messages=new Vector(0);
            } else {
                clearReadedMessageList();
            }
        }
        if (c==midlet.BombusQD.commands.cmdSelect) {
            startSelection=true;
            Msg mess=((Msg) contact.msgs.elementAt(cursor));
            mess.selected = !mess.selected;
            mess.oldHighlite = mess.highlite;
            mess.highlite = mess.selected;
            return;
        }
//#ifdef HISTORY
//#ifdef HISTORY_READER
//#         if (c==midlet.BombusQD.commands.cmdReadHistory) {
//#             new HistoryReader(display,contact);
//#             return;
//#         }
//#endif
//#endif
//#if (FILE_IO && HISTORY)
//#         if (c==midlet.BombusQD.commands.cmdSaveChat) saveMessages();
//#endif
        /** login-critical section */
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;

        if (c==midlet.BombusQD.commands.cmdMessage) { 
            contact.msgSuspended=null;
            keyGreen(); 
        }
        if (c==midlet.BombusQD.commands.cmdResume) {
            keyGreen();
        }
        if (c==midlet.BombusQD.commands.cmdQuote) Quote();
        if (c==midlet.BombusQD.commands.cmdReply) {
            if(contact.getJid().indexOf("juick@juick.com")>-1){
                Reply(); return;                  
            } else checkOffline();
        }
        if(c==midlet.BombusQD.commands.cmdAddSearchQuery) {
             new SearchText(display,d,contact);
             return;
        } 
//#if BREDOGENERATOR                 
//#         if (c==midlet.BombusQD.commands.cmdAutoGenON) {
//#            midlet.BombusQD.cf.bredoGen=true;
//#            display.setCurrent(midlet.BombusQD.sd.roster);
//#            VirtualList.setWobble(3, null, SR.MS_BREDO_ON);
//#         }
//#         if (c==midlet.BombusQD.commands.cmdAutoGenOff) {
//#            midlet.BombusQD.cf.bredoGen=false;
//#            display.setCurrent(midlet.BombusQD.sd.roster);
//#            VirtualList.setWobble(3, null, SR.MS_BREDO_OFF);
//#         }  
//#endif         
        
        if(c==cmdFind_) {
            find_str(midlet.BombusQD.cf.find_text_str);
        }          
        if (c==midlet.BombusQD.commands.cmdActions) {
//#ifndef WMUC
            if (contact instanceof MucContact) {
                MucContact mc=(MucContact) contact;
                new RosterItemActions(display, this, mc, -1);
            } else
//#endif
                new RosterItemActions(display, this, contact, -1);
        }
	if (c==midlet.BombusQD.commands.cmdActive) {
            new ActiveContacts(display, this, null); 
        }
        
        if (c==midlet.BombusQD.commands.cmdSubscribe) midlet.BombusQD.sd.roster.doSubscribe(contact);
		
        if (c==midlet.BombusQD.commands.cmdUnsubscribed) midlet.BombusQD.sd.roster.sendPresence(contact.bareJid, "unsubscribed", null, false);

//#ifdef CLIPBOARD
//#         if (c==midlet.BombusQD.commands.cmdSendBuffer) {
//#             String from=midlet.BombusQD.sd.account.toString();
//#             String body=midlet.BombusQD.clipboard.getClipBoard();
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
//#                 contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,null,SR.MS_CLIPBOARD_SENDERROR));
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
            checkOffline(); 
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

    
    Vector vectorfound = new Vector(0);
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
        
        
    private void checkOffline(){
       Msg msg;
       boolean found=false; 
       try {
           msg=getMessage(cursor);
           found = msg.from.startsWith("Cleared");
           int size=midlet.BombusQD.sd.roster.hContacts.size();
           Contact c;
           if(!found) {
             for(int i=0;i<size;i++){
              c=(Contact)midlet.BombusQD.sd.roster.hContacts.elementAt(i);
              if (c instanceof MucContact){
                if(c.getNick().indexOf(msg.from)>-1) found=(c.status!=5);
               }
             }
           }
       } catch (Exception e) { msg = null; }
       
       if(!found&&msg!=null) new ui.controls.AlertBox(msg.from,
           SR.MS_ALERT_CONTACT_OFFLINE, display, this) {
           public void yes() { Reply(); }
           public void no() { }
       }; else Reply();
       msg=null;
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
                checkOffline();
                return;
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
            Msg msg=replaceNickTags(getMessage(cursor));
            if (msg==null ||
                msg.messageType == Msg.MESSAGE_TYPE_OUT ||
                msg.messageType == Msg.MESSAGE_TYPE_SUBJ)
                keyGreen();
            else{
//#ifdef RUNNING_MESSAGE
//#                String messg = msg.from+": "; 
//#ifdef JUICK.COM                
//#                if(msg.messageType==Msg.MESSAGE_TYPE_JUICK){
//#                     messg=util.StringUtils.replaceNickTags(msg.id);
//#                }
//#endif          
//#              if(msg.from=="Cleared") {
//#                    messg="";
//#                    msg.body="";
//#              }
//#                
//#              if(messg==null) messg = "";
//#                
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
                .append(replaceNickTags(getMessage(cursor)).quoteString())
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
        int size = msgs.size();

        try {
            if (size>0){
                int virtCursor=size;
                boolean delete = false;
                int i=size;
                while (true) {
                    if (i<0) break;
                    if (i<cur) {
                        if (!delete) {
                            if (((Msg)msgs.elementAt(virtCursor)).dateGmt+1000<System.currentTimeMillis()) {
                                msgs.removeElementAt(virtCursor);
                                delete=true;
                            }
                        } else msgs.removeElementAt(virtCursor);
                    }
                    virtCursor--;
                    i--;
                }
                contact.activeMessage=size-1;
            }
        } catch (Exception e) { }

        msgs=null;
        msgs=new Vector(0);
        midlet.BombusQD.sd.roster.systemGC();

        Msg m=new Msg(Msg.MESSAGE_TYPE_PRESENCE, "Cleared", null,"Cleared");
        
        if(cur==size) contact.activeMessage=-1;
        
        m.itemCollapsed=false;
        contact.addMessage(m);
        
        m=null;
        
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
