/*
 * SmilesIcons.java
 *
 * Created on 21.05.2008, 22:24
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
 
package images;

import Messages.MessageParser;
import ui.ImageList;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import java.util.*;

/**
 *
 * @author EvgS,aqent
 */
public class SmilesIcons extends ImageList {

    private final static String res = "/images/smiles/smiles.png";
    private final static String restxt = "/images/smiles/smiles.txt";
    private final static String path = "/images/smiles/";

    private static Timer timer;
    private static Vector anismiles;
    private static int indexPos = 0;    
    private static int interval=195;
    
    private static int smilesCount;
    private static int cols; 
    private static int SMILES_IN_ROW=16;    
    
    
    private SmilesIcons()
    {
      super(res, cols, SMILES_IN_ROW);
      if(anismiles==null){
         boolean loadAnimatedSmiles=true;
         anismiles = new Vector(smilesCount);
           for(int i=1; i<=smilesCount; i++){
             try {
                ImageList setSmile = new ImageList(path + Integer.toString(i) + ".png");
                anismiles.addElement(setSmile);
             } catch(Exception e) {
                i=smilesCount;
                loadAnimatedSmiles=false;
                System.out.println("Err: " + i);
             }
           }
         //if(timer==null && loadAnimatedSmiles) startTimer();//?
      }
    }    


    public final static void startTimer(){
        //System.out.println("start ani timer");
        if(timer==null){
            timer = new Timer();
            timer.schedule(new Counter(), 200 , interval);
        }
    }
    
    
    public final static void stopTimer(){
        //System.out.println("stop ani timer");
        if(timer!=null){
          timer.cancel();
          timer=null;
        }
    }    
    

    private final static class Counter extends TimerTask {
        private int pause=0;
        public Counter(){}
	public void run () {
              //System.out.println(indexPos);
              indexPos++;
              if(indexPos == 10 ) indexPos = 0;
              Displayable displayable=midlet.BombusQD.getInstance().display.getCurrent();
              if (displayable instanceof Canvas) ((Canvas)displayable).repaint();
  	}
    }  
    

    public final void drawImage(Graphics g, int index, int x, int y) {
      if(midlet.BombusQD.cf.animatedSmiles){
          ((ImageList)anismiles.elementAt(index)).drawImage(g, indexPos , x, y);
      }else super.drawImage(g,index,x,y);
    }       
    
    
    public static ImageList instance;

    public static ImageList getInstance() {
	if (instance==null){
//#ifdef SMILES
            try {
                smilesCount=MessageParser.getInstance().getSmileTable().size();
                cols=ceil(SMILES_IN_ROW, smilesCount);
            } catch (Exception e) {
                System.out.println("Exception SmilesIcons: "+restxt);
            }
//#endif
            instance=new SmilesIcons();
        }
	return instance;
    }
    
    private static int ceil(int rows, int count){
        int tempCols=count/rows;
        if (count>(tempCols*rows))
            tempCols++;
        return tempCols;
    }
}
