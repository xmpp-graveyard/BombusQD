/*
 * DiscoContact.java
 *
 * Created on 7.06.2006, 22:41
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
//#ifdef SERVICE_DISCOVERY 
package ServiceDiscovery;
import images.RosterIcons;
import ui.IconTextElement;
import Client.Jid;

public class DiscoContact extends IconTextElement {
    
    private String nickname;
    private String sJid;
    private int status;
    
    public DiscoContact(String nick, String sJid, int status) { 
        super(RosterIcons.getInstance());
        this.nickname=(nick==null)?null:nick.trim();
        this.sJid=sJid;
        if(null != sJid) {
            Jid jid = new Jid(sJid);
            this.status = RosterIcons.getInstance().getTransportIndex(jid.getTransport());
            jid = null;
        }
    }

    public boolean getFontIndex() { return true; } //change font
    public int getImageIndex() { return status; };
    public String toString() { return (nickname==null)?sJid:nickname; }
    public String getTipString() { return sJid; }
}
//#endif