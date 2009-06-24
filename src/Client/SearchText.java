/*
 * SearchText.java
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
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import ui.controls.form.TextInput;
import ui.controls.form.DefForm;
import javax.microedition.lcdui.TextField;
import Client.StaticData;
import Client.Config;
import ui.VirtualList;

class SearchText extends DefForm { 
    private Display display;
    private TextInput text;
    private String txt="";
    Contact c;
    
    public SearchText(Display display, Displayable pView,Contact c) {
        super(display, pView,"Text:");
        this.display=display;
        this.c=c;
        text=new TextInput(display,"Enter search word!", "", "searchtxt", TextField.ANY);
        itemsList.addElement(text);        
        attachDisplay(display);
        this.parentView=pView;
    }
    public void cmdOk() {
        String msgtext = text.getValue();
        Config.getInstance().find_text_str=msgtext;
        VirtualList.setWobble(1, null, "Str: " + Config.getInstance().find_text_str + " is set.");
        destroyView();
    }
    public void destroyView() {
	if (display!=null) display.setCurrent(StaticData.getInstance().roster);
    }
}