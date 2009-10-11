/*
 * Commands.java
 *
 * Created on 3 Сентябрь 2009 г., 23:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package midlet;
import Menu.Command;
import locale.SR;
/**
 *
 * @author aqent
 */
public class Commands {
    
    
    //MessageList
    public static Command cmdTranslate=new Command(SR.MS_TRANSLATE, Command.SCREEN /*Command.SCREEN*/,337);    
    public static Command cmdSubscribe=new Command(SR.MS_SUBSCRIBE, Command.SCREEN, 1);
    public static Command cmdUnsubscribed=new Command(SR.MS_DECLINE, Command.SCREEN, 2);
    public static Command cmdMessage=new Command(SR.MS_NEW_MESSAGE,Command.SCREEN,3);
    public static Command cmdResume=new Command(SR.MS_RESUME,Command.SCREEN,1);
    public static Command cmdReply=new Command(SR.MS_REPLY,Command.SCREEN,4);
    public static Command cmdQuote=new Command(SR.MS_QUOTE,Command.SCREEN,5);
//#ifdef ARCHIVE
    public static Command cmdArch=new Command(SR.MS_ADD_ARCHIVE,Command.SCREEN,6);
//#endif
    public static Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 7);
    public static Command cmdSelect=new Command(SR.MS_SELECT, Command.SCREEN, 8);
    public static Command cmdActions=new Command(SR.MS_CONTACT,Command.SCREEN,9);
    public static Command cmdActive=new Command(SR.MS_ACTIVE_CONTACTS,Command.SCREEN,10);
//#if TEMPLATES
//#     public static Command cmdTemplate=new Command(SR.MS_SAVE_TEMPLATE,Command.SCREEN,11);
//#endif
//#ifdef FILE_IO
    public static Command cmdSaveChat=new Command(SR.MS_SAVE_CHAT, Command.SCREEN, 12);
//#endif
//#ifdef HISTORY
//#ifdef HISTORY_READER
//#     public static Command cmdReadHistory=new Command("Read history", Command.SCREEN, 13);
//#endif
//# //        if (midlet.BombusQD.cf.lastMessages && !contact.isHistoryLoaded()) loadRecentList();
//#endif
//#ifdef CLIPBOARD    
//#     public static Command cmdSendBuffer=new Command(SR.MS_SEND_BUFFER, Command.SCREEN, 14);
//#endif
    public static Command cmdAddSearchQuery = new Command(SR.MS_ADD_SEARCH_QUERY, Command.SCREEN, 400);        
    public static Command cmdClrPresences=new Command(SR.MS_DELETE_ALL_STATUSES, Command.SCREEN,403); 
//#if BREDOGENERATOR             
//#     public static Command cmdAutoGenON=new Command(SR.MS_BREDO_ON,Command.SCREEN,87);    
//#     public static Command cmdAutoGenOff=new Command(SR.MS_BREDO_OFF,Command.SCREEN,88);    
//#endif       
    public static Command cmdMyService=new Command(SR.MS_SERVICE, Command.SCREEN, 31);
    
//#ifdef CLIPBOARD
//#     public static Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 20);
//#     public static Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 30);
//#endif    
    public static Command cmdxmlSkin = new Command(SR.MS_USE_COLOR_SCHEME, Command.SCREEN, 40);
    public static Command cmdUrl = new Command(SR.MS_GOTO_URL, Command.SCREEN, 80);
    public static Command cmdBack = new Command(SR.MS_BACK, Command.BACK, 99);    
  
//#ifdef JUICK.COM   
//#     /*
//#     public static Command cmdJuickLastPopular = new Command("", Command.SCREEN, 101);//#
//#     public static Command cmdJuickLastMsgs = new Command("", Command.SCREEN, 102);//#+
//#     public static Command cmdJuickSubscribe = new Command("", Command.SCREEN, 103);//S #
//#     public static Command cmdJuickUnsubscribe = new Command("", Command.SCREEN, 104);//U #
//#     public static Command cmdJuickSendPM = new Command("", Command.SCREEN, 105);//PM @nick msg
//#     public static Command cmdJuickUsersMsgs = new Command("", Command.SCREEN, 106); //@nick+
//#     */    
//#endif 

    private static void setImages(){
        cmdSelect.setImg(0x60);
        cmdResume.setImg(0x80);
        cmdSubscribe.setImg(0x43);
        cmdUnsubscribed.setImg(0x41);
        cmdMessage.setImg(0x81);
        cmdTranslate.setImg(0x73);
        
        cmdClrPresences.setImg(0x76);
        cmdReply.setImg(0x72);
        cmdQuote.setImg(0x63);
        cmdPurge.setImg(0x33);
        cmdAddSearchQuery.setImg(0x83);
        cmdSelect.setImg(0x60);
        cmdArch.setImg(0x64);
        cmdCopy.setImg(0x13);
        cmdCopyPlus.setImg(0x23);
        cmdxmlSkin.setImg(0x07);
        cmdUrl.setImg(0x15);
        cmdActions.setImg(0x16);
        cmdSendBuffer.setImg(0x84);
        cmdSaveChat.setImg(0x44);
//#if HISTORY_READER
//#         cmdReadHistory.setImg(0x05);
//#endif
        cmdMyService.setImg(0x27);

    }
        
    private Commands() { }
    public static Commands get(){
        if (commands==null) {
            commands=new Commands();
            setImages();
        }
        return commands;
    }    
    private static Commands commands;    

    
}
