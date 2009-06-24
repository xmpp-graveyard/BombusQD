/*
 * ColorsList.java
 *
 * Created on 23.05.2008, 13:10
 *
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
 */

package Colors;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
//#endif

//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
/**
 *
 * @author ad,aqent
 */
public class ColorsList
        extends VirtualList implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener
//#endif           
    {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_COLORS");
//#endif
    

    static void setColor(int paramName, int value) {
        ((ColorVisualItem)itemsList.elementAt(paramName)).setColor(value);
    }
    

    static Vector itemsList;

    public Command cmdOk = new Command(SR.MS_EDIT, Command.OK, 1);
    Command cmdBack=new Command(SR.MS_BACK,Command.BACK,99);
    
    /**
     * Creates a new instance of ColorsList
     */
    public ColorsList(Display display) {
        super(display);
        setMainBarItem(new MainBar(SR.MS_COLOR_TUNE));
        addCommand(cmdBack);
        addCommand(cmdOk);
        setCommandListener(this);
        
        itemsList=new Vector();
        int cnt=0;
        int size = ColorTheme.colorsContainer.size();
        for (int i=0;i<=size;i++) {
            ColorItem c=(ColorItem)ColorTheme.colorsContainer.elementAt(i);
//#ifdef COLOR_TUNE
//#             itemsList.addElement(new ColorVisualItem(c.name, NAMES[cnt], c.color));
//#endif
            cnt++;
        }
        commandState();
        attachDisplay(display);
    }

    protected int getItemCount() { return itemsList.size(); }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)itemsList.elementAt(index);
    }
    
    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
    }
    
//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.MS_SELECT; }
    
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
    
   public void commandAction(Command command, Displayable displayable) {
        if (command==cmdBack) destroyView();
        else eventOk();
   }   

    
    public void eventOk() {
//#ifdef COLOR_TUNE
//#          new ColorSelector(display, cursor);
//#endif
    }

//#ifdef COLOR_TUNE
//#     public static final String[] NAMES = {
//#             SR.MS_BALLOON_INK,
//#             SR.MS_BALLOON_BGND,
//#             SR.MS_LIST_BGND,
//#             SR.MS_LIST_BGND_EVEN,
//#             SR.MS_LIST_INK,
//# 
//#             SR.MS_MSG_SUBJ,
//#             SR.MS_MSG_HIGHLIGHT,
//# 
//#             SR.MS_DISCO_CMD,
//# 
//#             SR.MS_BAR_BGND,
//#             SR.MS_BAR_BGND+" 2",
//#             SR.MS_BAR_INK,
//# 
//#             SR.MS_CONTACT_DEFAULT,
//#             SR.MS_CONTACT_CHAT,
//#             SR.MS_CONTACT_AWAY,
//#             SR.MS_CONTACT_XA,
//#             SR.MS_CONTACT_DND,
//#             SR.MS_CONTACT+" J2J",
//# 
//#             SR.MS_GROUP_INK,
//# 
//#             SR.MS_BLK_INK,
//#             SR.MS_BLK_BGND,
//# 
//#             SR.MS_MESSAGE_IN,
//#             SR.MS_MESSAGE_OUT,
//#             SR.MS_MESSAGE_PRESENCE,
//#             SR.MS_MESSAGE_AUTH,
//#             SR.MS_MESSAGE_HISTORY,
//# 
//#             SR.MS_MESSAGE_IN_S,
//#             SR.MS_MESSAGE_OUT_S,
//#             SR.MS_MESSAGE_PRESENCE_S,
//# 
//#             SR.MS_PGS_REMAINED,
//#             SR.MS_PGS_COMPLETE,
//#             SR.MS_PGS_COMPLETE+" 2",
//#             SR.MS_PGS_INK,
//# 
//#             SR.MS_HEAP_TOTAL,
//#             SR.MS_HEAP_FREE,
//# 
//#             SR.MS_CURSOR_BGND,
//#             SR.MS_CURSOR_OUTLINE,
//# 
//#             SR.MS_SCROLL_BRD,
//#             SR.MS_SCROLL_BAR,
//#             SR.MS_SCROLL_BGND,
//# 
//#             SR.MS_POPUP_MESSAGE,
//#             SR.MS_POPUP_MESSAGE_BGND,
//#             SR.MS_POPUP_SYSTEM,
//#             SR.MS_POPUP_SYSTEM_BGND,
//# 
//#             SR.MS_CONTACT_STATUS,
//# 
//#             SR.MS_CONTROL_ITEM,
//#             
//#             SR.MS_GRADIENT_BGND_LEFT,
//#             SR.MS_GRADIENT_BGND_RIGHT,
//#             
//#             SR.MS_GRADIENT_CURSOR_1,
//#             SR.MS_GRADIENT_CURSOR_2,
//#             SR.MS_TRANSPARENCY_ARGB,
//#             SR.MS_GRAPHICS_MENU_BGNG_ARGB,
//#             SR.MS_GRAPHICS_MENU_FONT
//#         };
//#endif
}
