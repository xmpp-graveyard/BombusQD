/*
 * ImageList.java
 *
 * Created on 31.01.2005, 0:06
 *
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
 */
 
/**
 *
 * @author Eugene Stahov,aqent
 */

package ui;
import javax.microedition.lcdui.*;
import Client.StaticData;
import midlet.BombusQD;
import Client.Config;
import javax.microedition.io.*;
import java.io.*;
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import java.util.*;

public class ImageList {

    protected int height;
    protected int width;

    public ImageList(){ };
    int pos;

    /** Creates a new instance of ImageList */
    String resource;
    
    protected Image resImage;
    
    public ImageList(String resource, int rows, int columns) {
        try {
            resImage = Image.createImage(resource);
            width = resImage.getWidth()/columns;
            height = (rows==0)? width : resImage.getHeight()/rows;
        } catch (Exception e) { 
            System.out.print("Can't load ImgList ");
            System.out.println(resource);
            if(midlet.BombusQD.cf.debug) midlet.BombusQD.debug.add("error Can't load ImgList "+resource,10);
        }
    }
    
    public ImageList(String s)
    {
        try {
            resImage = Image.createImage(s);
            width = height = resImage.getWidth()/10;
        } catch (Exception e) { 
            System.out.print("Can't load smile: ");
            System.out.println(resource);
            if(midlet.BombusQD.cf.debug) midlet.BombusQD.debug.add("error Can't load smile: "+resource,10);
        }
    }     
    
   public static Image resize(Image image, int w, int h) {
        int w0 = image.getWidth(); //Ширина 200
        int h0 = image.getHeight();//Высота 150
        int[] arrayOld = new int[w0*h0];
        int[] arrayNew = new int[w*h];
        image.getRGB(arrayOld, 0, w0, 0, 0, w0, h0);
        int wy=0;
        int wy1=0;
        for (int y = 0; y < h; y++) {
             wy=w*y; 
             wy1=w0*(int)(y*h0/h); //thanks evgs :)
             for (int x = 0; x < w; x++) {
                   arrayNew[x+wy] = arrayOld[x*w0/w+wy1];
             }
        }
        arrayOld=null;
        return Image.createRGBImage(arrayNew, w, h, true); 
   }

   
    public void drawImage(Graphics g, int index, int x, int y){
        int ho=g.getClipHeight();
        int wo=g.getClipWidth();
        int xo=g.getClipX();
        int yo=g.getClipY();
        
        int iy=y-height*(int)(index>>4);
        int ix=x-width*(index&0x0f);
        g.clipRect(x,y, width,height);
        try {
            g.drawImage(resImage,ix,iy,Graphics.TOP|Graphics.LEFT);
        } catch (Exception e) {}
        g.setClip(xo,yo, wo, ho);
    };
    
    public int getHeight() {return height;}
    public int getWidth() {return width;}

}
