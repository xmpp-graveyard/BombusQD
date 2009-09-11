/*
 * MessageList.java
 *
 * Created on 11.12.2005, 3:02
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

package Messages; 

import Client.Config;
import Client.Msg;
import Client.StaticData;
import Colors.ColorTheme;
import java.util.Vector;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualElement;
import ui.VirtualList;
//import ui.reconnectWindow;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif

public abstract class MessageList extends VirtualList
    implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {

    protected Vector messages;
    
    /** Creates a new instance of MessageList */
    private MessageItem mi=null;  
    
    public MessageList() {
        super();
        messages=null;
	messages=new Vector();
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        
//#ifdef SMILES
        smiles=midlet.BombusQD.cf.smiles;
//#else
//#         smiles=false;
//#endif
        enableListWrapping(false);
        
        //if(midlet.BombusQD.cf.animatedSmiles) images.SmilesIcons.startTimer();

        cursor=0;//activate
        setCommandListener(this);
    }

    public MessageList(Display display) {
        this();
        attachDisplay(display);
    }
    
    
    public abstract int getItemCount();
    
    protected VirtualElement getItemRef(int index) {
      int size = messages.size();
	if (size<getItemCount()) messages.setSize(getItemCount());
	mi=(MessageItem)messages.elementAt(index);
	if (mi==null) {
	    mi=new MessageItem(getMessage(index), this, smiles);
            mi.setEven( (index & 1) == 0);
	    messages.setElementAt(mi, index);
	}
        return mi;
    }
    
    public int getVWidth(){ 
        return -1;
    }       
    
    public abstract Msg getMessage(int index);
    
    public void markRead(int msgIndex) {}
    
    protected boolean smiles;

    public void addCommands() {
//#ifdef CLIPBOARD
//#         if (midlet.BombusQD.cf.useClipBoard) {
//#             addCommand(midlet.BombusQD.commands.cmdCopy);
//#             addCommand(midlet.BombusQD.commands.cmdCopyPlus);
//#         }
//#endif
        addCommand(midlet.BombusQD.commands.cmdxmlSkin);
        addCommand(midlet.BombusQD.commands.cmdUrl);
//#ifndef GRAPHICS_MENU        
     addCommand(cmdBack);
//#endif         
    }
    public void removeCommands () {
//#ifdef CLIPBOARD
//#         if (midlet.BombusQD.cf.useClipBoard) {
//#             removeCommand(midlet.BombusQD.commands.cmdCopy);
//#             removeCommand(midlet.BombusQD.commands.cmdCopyPlus);
//#         }
//#endif
        removeCommand(midlet.BombusQD.commands.cmdxmlSkin);
        removeCommand(midlet.BombusQD.commands.cmdUrl);
        removeCommand(midlet.BombusQD.commands.cmdBack);
    }
    
    

    public void commandAction(Command c, Displayable d) {
        if (c==midlet.BombusQD.commands.cmdBack) {
            midlet.BombusQD.sd.roster.activeContact=null;
            destroyView();
        }
        if (c==midlet.BombusQD.commands.cmdUrl) {
            try {
                Vector urls=((MessageItem) getFocusedObject()).getUrlList();
                new MessageUrl(display, urls); //throws NullPointerException if no urls
            } catch (Exception e) {/* no urls found */}
        }
        if (c==midlet.BombusQD.commands.cmdxmlSkin) {
           try {
               if (((MessageItem)getFocusedObject()).msg.body.indexOf("xmlSkin")>-1) {
                    ColorTheme.loadSkin(((MessageItem)getFocusedObject()).msg.body,2);
               }
            } catch (Exception e){}
        }
        
//#ifdef CLIPBOARD
//#         if (c == midlet.BombusQD.commands.cmdCopy)
//#         {
//#             try {
//#                 midlet.BombusQD.clipboard.add(((MessageItem)getFocusedObject()).msg);
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#         
//#         if (c==midlet.BombusQD.commands.cmdCopyPlus) {
//#             try {
//#                 midlet.BombusQD.clipboard.append(((MessageItem)getFocusedObject()).msg);
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
    }

    protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
//#ifdef MENU_LISTENER
        /*
        if (keyCode==Config.SOFT_RIGHT || keyCode==Config.KEY_BACK) {
            if (!reconnectWindow.getInstance().isActive() && !cf.oldSE) {
                midlet.BombusQD.sd.roster.activeContact=null;
                destroyView();
                return;
            }
        }
         */
//#endif
       if (keyCode==13) { //copy
            VirtualList.setWobble(1, null, "Copy to buffer is OK.");            
            try {
                midlet.BombusQD.clipboard.append(((MessageItem)getFocusedObject()).msg);
            } catch (Exception e) {/*no messages*/}        
       } 
       if (keyCode==14) { //clear
            VirtualList.setWobble(1, null, "Clear buffer is OK.");                        
            try {
                midlet.BombusQD.clipboard.setClipBoard("");
            } catch (Exception e) {/*no messages*/}            
       }        
//#ifdef SMILES
        if (keyCode=='*') {
            try {
                ((MessageItem)getFocusedObject()).toggleSmiles();
            } catch (Exception e){}
            return;
        }
//#endif       
       super.keyPressed(keyCode);
    }
   
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
//#     public int showGraphicsMenu() {
//#          commandState();
//#          String capt="";
//#          try {
//#              capt=getMainBarItem().elementAt(0).toString();
//#          } catch (Exception ex){ }
//#         new GMenu(display, parentView, this,  null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.MESSAGE_LIST;        
//#         return GMenu.MESSAGE_LIST;
//#     }
//#else
    public void showMenu() {
        commandState();
        String capt="";
        try {
            capt=getMainBarItem().elementAt(0).toString();
        } catch (Exception ex){ }
        new MyMenu(display, parentView, this, capt, null, menuCommands);
   }  
//#endif      

//#endif

    public void commandState() { }
}
