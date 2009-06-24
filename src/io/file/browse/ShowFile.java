/*
 * ShowFile.java
 *
 * Created on 9.10.2006, 14:00
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package io.file.browse;

import Client.Config;
import io.file.FileIO;
import java.io.IOException;
import javax.microedition.lcdui.*;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
import locale.SR;
import util.Strconv;
import javax.microedition.io.ConnectionNotFoundException;
import Client.Config;
//import Mood.EventPublish;
import ui.controls.AlertBox;
import ui.VirtualList;
import ui.ImageList;
/**
 *
 * @author User 
 */
public class ShowFile implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    private Command back = new Command(SR.MS_BACK, Command.BACK, 2);
    private Command stop = new Command(SR.MS_STOP, Command.BACK, 3);
    //private Command appliedRES = new Command("Yes", Command.BACK, 4);    

    private int len;

    private byte[] b;

    private Player pl;
    
    private Config cf;
    private long length=0;
    boolean play=false;
    Image img;
    
    public ShowFile(Display display, String fileName, int type,String trackname) {
        this.display=display;
        parentView=display.getCurrent();
        cf=Config.getInstance();
  
        if (type==1) {
         /*VirtualList.setWobble(1, null, "file:///"+fileName);
          try {
            midlet.BombusMod.getInstance().platformRequest("file:///"+fileName);
            EventPublish ev = new EventPublish();
            Config.getInstance().track++;
            ev.publishMusic(trackname,Config.getInstance().track);
           } catch (ConnectionNotFoundException ex) {
          } 
        } else {
          */
          load(fileName);
        }
        if (type==2) view(fileName);
        if (type==3) read(fileName);
    }

    private ChoiceGroup resType;
    private int replyIndex;       // Index of "reply" in choice group        
    
    private void load(String file) {
        try {
            FileIO f=FileIO.createConnection(file);
            b = f.fileRead();
            length = f.fileSize();
            len = b.length;
            f.close();
        } catch (Exception e) {}
    }
        
    private void view(String file) {
        this.img = Image.createImage(b, 0, len);
        Form form = new Form(file);
        /*
        if(loadRes){
            form.addCommand(appliedRES);  
            form.append("SELECT TYPE OF RESOURCE:\n");
            resType=new ChoiceGroup("Type", ChoiceGroup.POPUP);
            replyIndex = resType.append("skin.png", null);
            //resType.append("menu.png", null);
            //resType.append("actions.png", null);
            resType.setSelectedIndex(replyIndex, true);

            form.append(resType);            
        } 
         */       
        form.append(new ImageItem(null, img, ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_NEWLINE_BEFORE, "[image]"));
        form.addCommand(back);
        form.setCommandListener(this);
        display.setCurrent(form);
    }
    
    private void read(String file) {
       TextBox tb = new TextBox(file+"("+len+" bytes)", null, len, TextField.ANY | TextField.UNEDITABLE);

       tb.addCommand(back);
       tb.setCommandListener(this);


        if (len > 0) {
           String s=new String();
            try {
                int maxSize=tb.getMaxSize();

                if (maxSize>len){
                    s=new String(b, 0, len);
                } else {
                    s=new String(b, 0, maxSize);
                }
            } catch (Exception e) {}

               if (cf.cp1251) {
                    tb.setString(Strconv.convCp1251ToUnicode(s));
               } else {
                    tb.setString(s);
               }
        }

       tb.setCommandListener(this);
       display.setCurrent(tb);
    }
    
    private void play(String file,String trackname,boolean play) {
    /*    
        try {
            pl = Manager.createPlayer("file://" + file);
            pl.realize();
            pl.start();
        } catch (IOException ex) {
            //ex.printStackTrace();
        } catch (MediaException ex) {
            //ex.printStackTrace();
        }

        Alert a = new Alert("Play", "Playing" + " " + file, null, null);
        a.addCommand(stop);
        a.addCommand(back);
        a.setCommandListener(this);
        display.setCurrent(a);
     */
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==back) display.setCurrent(parentView);
        /*
        if (c==appliedRES){
            String res = resType.getString(resType.getSelectedIndex());
            if(res.indexOf("skin")>-1){
               midlet.BombusMod.getInstance().imageArr[0]=img;  
            }
            if(res.indexOf("menu")>-1){
               midlet.BombusMod.getInstance().imageArr[6]=img;                  
            }
            if(res.indexOf("actions")>-1){
               midlet.BombusMod.getInstance().imageArr[1]=img;                  
            }            
            display.setCurrent(parentView);
        }
         */
        if (c==stop) {
            try {
                pl.stop();
                pl.close();
            } catch (Exception e) { }
        }
    }
}
