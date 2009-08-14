/*
 * ActiveContacts.java
 *
 * Created on 20.01.2005, 21:20
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

import java.util.Enumeration;
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
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif   
import Conference.ConferenceGroup;
/**
 *
 * @author EvgS,aqent
 */
public class ActiveContacts 
    extends VirtualList
    implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
{
    
    Vector activeContacts;
    
    private Command cmdCancel=new Command(SR.MS_BACK, Command.BACK, 99);
    private Command cmdOk=new Command(SR.MS_SELECT, Command.SCREEN, 1);
    
    /** Creates a new instance of ActiveContacts */
    public ActiveContacts(Display display, Displayable pView, Contact current) {
	super();
        activeContacts=null;
	activeContacts=new Vector();
        Contact c=null;
         int size=midlet.BombusQD.sd.roster.getHContacts().size();        
            for(int i=0;i<size;i++){    
                c=(Contact)midlet.BombusQD.sd.roster.getHContacts().elementAt(i);
                if (c.active()) activeContacts.addElement(c);             
            }
	if (getItemCount()==0) return;
	
        MainBar mainbar=new MainBar(2, String.valueOf(getItemCount()), " ", false);
        mainbar.addElement(SR.MS_ACTIVE_CONTACTS);
        setMainBarItem(mainbar);

	commandState();

	try {
            int focus=activeContacts.indexOf(current);
            moveCursorTo(focus);
        } catch (Exception e) {}

	attachDisplay(display);
        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdOk); cmdOk.setImg(0x43);
    }
    
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
//#     public int showGraphicsMenu() {
//#         new GMenu(display, parentView, this, null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.ACTIVE_CONTACTS;        
//#         return GMenu.ACTIVE_CONTACTS;
//#     }
//#else
    public void showMenu() { eventOk();}   
//#endif      

//#endif

    protected int getItemCount() { return activeContacts.size(); }
    protected VirtualElement getItemRef(int index) { 
	return (VirtualElement) activeContacts.elementAt(index);
    }

    public void eventOk() {
	Contact c=(Contact)getFocusedObject();
         if(Config.getInstance().useClassicChat){
           new SimpleItemChat(display,midlet.BombusQD.sd.roster,(Contact)c);            
         }else{
           if(midlet.BombusQD.cf.animatedSmiles) images.SmilesIcons.startTimer();            
           if(c.cList!=null && midlet.BombusQD.cf.module_cashe && c.msgs.size()>3){
              display.setCurrent((ContactMessageList)c.cList); 
           }else{
	      new ContactMessageList(c,display).setParentView(midlet.BombusQD.sd.roster);     
           }
         }                
    }

    public void commandAction(Command c, Displayable d) {
	if (c==cmdCancel) destroyView();
	if (c==cmdOk) eventOk();
    }

    public void keyPressed(int keyCode) {
        kHold=0;
//#ifdef POPUPS
        VirtualList.popup.next();
//#endif
	if (keyCode==KEY_NUM3) {
            destroyView();
        } else if (keyCode==KEY_NUM0) {
            if (getItemCount()<1)
                return;

            Contact c=(Contact)getFocusedObject();

            Enumeration i=activeContacts.elements();
            
            int pass=0;
            while (pass<2) {
                if (!i.hasMoreElements()) i=activeContacts.elements();
                Contact p=(Contact)i.nextElement();
                if (pass==1) 
                    if (p.getNewMsgsCount()>0) { 
                        focusToContact(p);
                        setRotator();
                        break; 
                    }
                if (p==c) pass++;
            }
            return;
        } else super.keyPressed(keyCode);
    }
    
    private void focusToContact(final Contact c) {
        int index=activeContacts.indexOf(c);
        if (index>=0) 
            moveCursorTo(index);
    }
    
    protected void keyGreen(){
        eventOk();
    }
    
    protected void keyClear () {
        Contact c=(Contact)getFocusedObject();
        c.purge();
        activeContacts.removeElementAt(cursor);
        getMainBarItem().setElementAt(String.valueOf(getItemCount()), 0);
    }
    
    public void destroyView(){
        midlet.BombusQD.sd.roster.reEnumRoster();
        display.setCurrent(parentView);
    }
//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.MS_SELECT; }
    public String touchRightCommand(){ return SR.MS_BACK; }
//#endif
}
