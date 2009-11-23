/*
 * msg.java
 *
 * Created on 6.01.2005, 19:20
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

package Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import Colors.ColorTheme;
import ui.Time;

/**
 *
 * @author Eugene Stahov 
 */
public class Msg {

    public boolean highlite;
    public boolean history;
    
    public byte messageType;
    
    public boolean MucChat;
    
    public String from;
    public String subject;
    public String body;
    public long dateGmt;
    public boolean delivered;
    public String id;
    public boolean search_word;        
    
    public boolean unread = false;

    public boolean itemCollapsed;
    //public int itemHeight=-1;
    
    public int color = -1;

    public boolean selected;

    public boolean oldHighlite;
    
    public void destroy(){
       if(null != from) from = null;
       if(null != subject) subject = null;
       if(null != body) body = null;
       if(null != id) id = null;
       if(time.length()>0){
           time.setLength(0);
       }
    }
    
    /** Creates a new instance of msg */
    public Msg(byte messageType, String from, String subj, String body) {
        this.messageType=messageType;
        this.from=from;
        this.body=body;
        this.subject=subj;
        this.dateGmt=Time.utcTimeMillis();
        if (messageType>=Constants.MESSAGE_TYPE_IN) unread=true;
        if (messageType==Constants.MESSAGE_TYPE_PRESENCE) itemCollapsed = midlet.BombusQD.cf.showCollapsedPresences;
        else if (messageType==Constants.MESSAGE_TYPE_HEADLINE) itemCollapsed=true;
        else if (body!=null && messageType!=Constants.MESSAGE_TYPE_SUBJ)
            if (body.length()>midlet.BombusQD.cf.messageLimit) itemCollapsed=true;
    }
    
    public void onSelect(){}

    public String getTime(){
        return Time.timeLocalString(dateGmt); 
    }
    public String getDayTime(){
        return Time.dayLocalString(dateGmt)+Time.timeLocalString(dateGmt); 
    }

    public void setDayTime(String date){
        this.dateGmt=Time.dateStringToLong(date);
    }
    
    public int getColor() {
        if (selected || highlite) return ColorTheme.getColor(ColorTheme.MSG_HIGHLIGHT);
        if (color>-1) return color; 

        switch (messageType) {
            case Constants.MESSAGE_TYPE_IN: return ColorTheme.getColor(ColorTheme.MESSAGE_IN);
            case Constants.MESSAGE_TYPE_PRESENCE: return ColorTheme.getColor(ColorTheme.MESSAGE_PRESENCE);
            case Constants.MESSAGE_TYPE_OUT: return ColorTheme.getColor(ColorTheme.MESSAGE_OUT);
            case Constants.MESSAGE_TYPE_SUBJ:return ColorTheme.getColor(ColorTheme.MSG_SUBJ);
            case Constants.MESSAGE_TYPE_HEADLINE: return ColorTheme.getColor(ColorTheme.MESSAGE_IN);
            case Constants.MESSAGE_TYPE_AUTH: return ColorTheme.getColor(ColorTheme.MESSAGE_AUTH);
            case Constants.MESSAGE_TYPE_EVIL: return 0xFF0000;
            case Constants.MESSAGE_TYPE_HISTORY: return ColorTheme.getColor(ColorTheme.MESSAGE_HISTORY);
//#ifdef JUICK.COM    
//#             case Constants.MESSAGE_TYPE_JUICK: return ColorTheme.getColor(ColorTheme.MESSAGE_IN);
//#endif
        }
        return ColorTheme.getColor(ColorTheme.LIST_INK);
    }
    
    private static StringBuffer time = new StringBuffer(0);
    public String toString() {
       if (messageType==Constants.MESSAGE_TYPE_PRESENCE && midlet.BombusQD.cf.timePresence) {
         time.setLength(0);
         time.append(getTime());
         time.append(' ');
         time.append(body);
         return time.toString(); 
       }else{
         return body;
       }
    }

    public boolean isPresence() { return messageType==Constants.MESSAGE_TYPE_PRESENCE; }
    
    public void serialize(DataOutputStream os) throws IOException {
	os.writeUTF(from);
	os.writeUTF(body);
	os.writeLong(dateGmt);
	if (subject!=null) os.writeUTF(subject);
    }
    
    public Msg (DataInputStream is) throws IOException {
	from=is.readUTF();
	body=is.readUTF();
	dateGmt=is.readLong();
        messageType=Constants.MESSAGE_TYPE_IN;
	try { subject=is.readUTF(); } catch (Exception e) { subject=null; }
    }

    public String quoteString(){
        StringBuffer out=new StringBuffer(0);
        if (subject!=null)
            if (subject.length()>0)
                out.append(subject).append("\n");
        out.append(body);
        int i=0;
        int len = out.length();
        while (i<len) {
            if (out.charAt(i)<0x03) out.deleteCharAt(i);
            else i++;
        }
        return out.toString();
    }
}
