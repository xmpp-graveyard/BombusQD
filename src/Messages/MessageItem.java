/*
 * MessageItem.java
 *
 * Created on 21.01.2006, 23:17
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
import images.RosterIcons;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import Colors.ColorTheme;
import ui.ComplexString;
import Fonts.FontCache;
import ui.Time;
import ui.VirtualElement;
import ui.VirtualList;

public class MessageItem
    implements VirtualElement//, MessageParser.MessageParserNotify 
{
    
    public Msg msg;
    Vector msgLines;
    private VirtualList view;
    private boolean even;
    private boolean smiles;
    private boolean partialParse=false;
    
    /** Creates a new instance of MessageItem */
    public MessageItem(Msg msg, VirtualList view, boolean showSmiles) {
	this.msg=msg;
	this.view=view;
        this.smiles=showSmiles;
        partialParse = msg.itemCollapsed;
    }

    public int getVHeight() { 
        if (msg==null) return 0;
        if (msg.itemHeight<0) msg.itemHeight=getFont().getHeight();
        if (msg.delivered||msg.search_word) {
            int rh=RosterIcons.getInstance().getHeight();
            if (msg.itemHeight<rh) return rh;
        }
        return msg.itemHeight; 
    }

    
    public Font getFont() {
        return FontCache.getFont(false, FontCache.msg);
    }

    public int getVWidth(){ 
        return -1;
    }     
    
    public int getColorBGnd() {
        return even?ColorTheme.getColor(ColorTheme.LIST_BGND_EVEN):ColorTheme.getColor(ColorTheme.LIST_BGND);    }
    
    public int getColor() { return msg.getColor(); }
    

    public void drawItem(Graphics g, int ofs, boolean selected) {
        int xorg=g.getTranslateX();
        int yorg=g.getTranslateY();
        g.translate(2,0);
        if (msgLines==null) {
            MessageParser.getInstance().parseMsg(this, view.getListWidth());
            return;
        }
        int size=msgLines.size();
        for(int i=0;i<size;i++){
            if (((ComplexString)msgLines.elementAt(i)).isEmpty()) break;
            int h=((ComplexString)msgLines.elementAt(i)).getVHeight();
            int cy=g.getClipY();

            if (cy <= h && cy+g.getClipHeight()>0 ) {
              ofs=0;
              if(midlet.BombusQD.cf.useLowMemory_iconmsgcollapsed==false) { 
                 if(i==0 && !msg.isPresence() && !msg.MucChat){
                          if (msg.delivered) {
                             RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_DELIVERED_INDEX, 0,0);
                             ofs+=23;
                          }else{
                             RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_MESSAGE_INDEX, 0,0);
                             ofs+=23;
                          }
                          if (msg.itemCollapsed) if (size>1) {
                             RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_MSGCOLLAPSED_INDEX, 0,0);
                          }                           
                 }
                 else{
                   if (msg.itemCollapsed) if (size>1) {
                             RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_MSGCOLLAPSED_INDEX, 0,0);
                             g.translate(8,0);
                             //ofs=8;
                   }
                }
              }
              else{
                   if (msg.itemCollapsed) if (size>1) {
                             RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_MSGCOLLAPSED_INDEX, 0,0);
                             g.translate(8,0);
                             //ofs=8;
                   }                  
              }
              ((ComplexString)msgLines.elementAt(i)).drawItem(g, ofs, selected);
            }
            g.translate(0, h);
            if (msg.itemCollapsed) break;            
        }        

        g.translate(xorg-g.getTranslateX(), yorg-g.getTranslateY());

        if (msg.search_word) {
            int right=g.getClipX()+g.getClipWidth();
            RosterIcons.getInstance().drawImage(
                    g, RosterIcons.ICON_PRIVACY_ALLOW, 
                    right-RosterIcons.getInstance().getWidth()-3 - 16, 0);
        }  
    }
    
    public void onSelect() {
        msg.itemCollapsed=!msg.itemCollapsed;
        updateHeight();
        if (partialParse) {
            partialParse=false;
            MessageParser.getInstance().parseMsg(this, view.getListWidth());
        }
    }
    
   
    byte repaintCounter=0;
    
    public void notifyRepaint() {
        updateHeight();
        //System.out.println(repaintCounter);
        if ((--repaintCounter)>=0) return;
        repaintCounter=6;
        //System.out.println("redraw..");
        view.redraw();
    }
   
    
    public void updateHeight() {
        int height=0;
        int size=msgLines.size();
        for(int i=0;i<size;i++){ 
            height+=((ComplexString)msgLines.elementAt(i)).getVHeight();
            if (msg.itemCollapsed) break;            
        }        
        msg.itemHeight=height;
    }

    public Vector getUrlList() { 
        Vector urlList=new Vector();
        addUrls(msg.body, "http://", urlList);
        addUrls(msg.body, "https://", urlList);
        addUrls(msg.body, "tel:", urlList);
        addUrls(msg.body, "ftp://", urlList);
        addUrls(msg.body, "native:", urlList);
        return (urlList.size()==0)? null: urlList;
    }
    
    private void addUrls(String text, String addString, Vector urlList) {
        int pos=0;
        int len=text.length();
        while (pos<len) {
            int head=text.indexOf(addString, pos);
            if (head>=0) {
                pos=head;
                
                while (pos<len) {
                    char c=text.charAt(pos);
                    if (c==' ' || c==0x09 || c==0x0d || c==0x0a || c==0xa0 || c==')' )  
                        break;
                    pos++;
                }
                urlList.addElement(text.substring(head, pos));
                
            } else break;
        }
    }
    
    public void setEven(boolean even) {
        this.even = even;
    }

    public String getTipString() {
        if (Time.utcTimeMillis() - msg.dateGmt> (86400000)) return msg.getDayTime();
        return msg.getTime();
    }
//#ifdef SMILES
    void toggleSmiles() {
        smiles=!smiles;
        MessageParser.getInstance().parseMsg(this, view.getListWidth());  
    }
    
    boolean smilesEnabled() { return smiles; }
//#endif

    public boolean isSelectable() { return true; }

    public boolean handleEvent(int keyCode) { return false; }
}
