/*
 * CommandForm.java
 *
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
import Client.*; 
import javax.microedition.lcdui.*;
import locale.SR;
import com.alsutton.jabber.datablocks.*;
import com.alsutton.jabber.*;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;
import ui.controls.form.DefForm;
import ui.controls.form.CheckBox;
import Account.*;
import java.util.Vector;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
//#endif
import ui.MainBar;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

public class CommandForm extends DefForm
        implements 
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif 
{
    private Display display;
    private Displayable parentView;
    private TextInput reason;
    
    
    private Object obj;
    private Object res;  
    private int type=-1;
    AccountSelect accountSelect;
    Account acc;   
    Roster r = StaticData.getInstance().roster;
    
    public Command cmdOk = new Command(SR.MS_COPY, Command.OK, 1);
    private ClipBoard clipboard=ClipBoard.getInstance();    
    private static final int DESTROY_ROOM=0;
    private static final int DELETE_ACCOUNT=1;    
    private static final int CHANGE_PASS_ACC=2; 
    private static final int _CHANGE_PASS_RMS=3;  
    private static final int _DEL_ACCOUNT_FROM_RMS=4;
    private static final int STATS_ITEM=5;
    
    
    public CommandForm(){};
    
    public CommandForm(Display display, Displayable pView,int type,String title,Object obj,Object res) {
        super(display, pView,title);
        
        this.display=display;
        this.obj=obj;
        this.type=type;
        this.res=res;
        String field_text="";
        switch(type){
            case DESTROY_ROOM:{
                itemsList.addElement(new SimpleString(SR.MS_DESTROY_ROOM+"?", true));                
                field_text = SR.MS_REASON;
                break;
            }
            case DELETE_ACCOUNT:{
                itemsList.addElement(new SimpleString(SR.MS_REMOVE_ACCOUNT+"?", true));                
                break;
            } 
            case CHANGE_PASS_ACC:{
                field_text = "Password";
                itemsList.addElement(new SimpleString(SR.MS_CHANGE_PASSWORD+"?", true));                
                break;
            }    
            case _CHANGE_PASS_RMS:{
                StringBuffer info = new StringBuffer();
                info.append((String)obj);
                info.append("New Password:%"+(String)res);
                itemsList.addElement(new CheckBox(info.toString(), true, true));
                itemsList.addElement(new SimpleString("Confirm New password?", true));
                break;
            }
            case _DEL_ACCOUNT_FROM_RMS:{
                itemsList.addElement(new SimpleString((String)obj, true));
                itemsList.addElement(new SimpleString("Delete this account?", true));                
                break;
            } 
            case STATS_ITEM:{
                if(res!=null){
                    Vector get = (Vector)res;
                    int size = get.size();
                    for(int i=0;i<size;i++){
                      itemsList.addElement((CheckBox)get.elementAt(i));
                    }
                }
                break;                
            }
        }
        
        if(field_text.length()>0){
         reason=new TextInput(display,field_text, "",null,TextField.ANY);  
         itemsList.addElement(reason);    
        }
        commandState();
        setMainBarItem(new MainBar(title));
        setCommandListener(this);
        attachDisplay(display);
        this.parentView=pView;
    }
    

    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
    }    
    
    public void commandAction(Command c, Displayable displayable) {
        if (c==cmdCancel) {
            destroyView();
            return;
        }
        if (c==cmdOk) { cmdOk(); }
    }    
    
//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.MS_COPY; }
    
//#ifdef GRAPHICS_MENU        
//#     public int showGraphicsMenu() {
//#          commandState();
//#          new GMenu(display, parentView, this, null, menuCommands);
//#          GMenuConfig.getInstance().itemGrMenu = -1;        
//#          eventOk();
//#          return -1;
//#     }
//#else
    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.MS_DISCO, null, menuCommands);
    } 
//#endif

//#endif        
    
    
    public void cmdOk() {
          switch(type){
             case DESTROY_ROOM: 
             {            
                  Group g = (Group) obj;
                  Iq first=new Iq(g.getName(),0,"destroyroom"); 
                  JabberDataBlock x=first.addChild("query",null);
                  x.setNameSpace("http://jabber.org/protocol/muc#owner");       
                  JabberDataBlock destroy=x.addChild("destroy",null);
                  destroy.setAttribute("jid", g.getName()); 
                  destroy.addChild("reason",reason.getValue());
                  r.theStream.send(first);
                  break;
             }
            case DELETE_ACCOUNT://FIX
            {
                  Account acc = (Account) obj;                
                  Iq iqdel=new Iq(acc.getServer(), Iq.TYPE_SET,"delacc");
                  JabberDataBlock qB = iqdel.addChildNs("query", "jabber:iq:register" );
                  qB.addChild("remove",null);
                  r.theStream.send(iqdel);
                  //r.transfer((Vector)res,acc);
                  iqdel=null;
                  qB=null;
                  break;                
            }
            case CHANGE_PASS_ACC://FIX
            {
                 Account acc = (Account) obj;
                 String pass = reason.getValue();
                 Iq iqdel=new Iq(acc.getServer(), Iq.TYPE_SET,"changemypass");
                 JabberDataBlock qB = iqdel.addChildNs("query", "jabber:iq:register" );
                  qB.addChild("username",acc.getUserName());
                  qB.addChild("password",pass);            
                  r.theStream.send(iqdel);
                  //r.transfer((AccountSelect)res,acc);
                  iqdel=null;
                  qB=null;
                  break;                
            }  
            case _CHANGE_PASS_RMS://FIX
            {
                  //r.getAccount().setPassword((String)res);
                  //new AccountForm(display, parentView, r.getAccountSelect(), r.getAccount(),-1,false,null);
                  //r.closeTransfer();
                  break;
            }
            case _DEL_ACCOUNT_FROM_RMS:
            {
                  new AccountSelect(display, parentView, false,-1);
                  //r.closeTransfer();
                  break;
            }
            case STATS_ITEM:
              {
                  try {
                      clipboard.add(new Msg(Msg.MESSAGE_TYPE_EVIL,"bechmark",null,(String)obj));
                  } catch (Exception e) {/*no messages*/}
                  break;
              }
               
          }
       destroyView();
    }
        
    public void destroyView(){
        if (display!=null)
            display.setCurrent(parentView);
    }
  } 
