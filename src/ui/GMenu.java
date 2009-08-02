/*
 * GMenu.java 
 *
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
package ui;
import javax.microedition.lcdui.*;
import Client.Config;
import Colors.ColorTheme;
import java.util.Vector;
import Fonts.*;
import locale.SR;
import midlet.BombusQD;
import Menu.MenuListener;
import Menu.Command;
import java.util.*;
import ui.controls.CommandsPointer;

public class GMenu extends Canvas {
       
   public GMenu() {};
   private Image offscreen = null;
   VirtualList view;
   
   public void init(Graphics g, int width, int height,VirtualList view) {
        this.height=height;
        this.width=width;
        this.view=view;
        if (!isDoubleBuffered()){
            offscreen=Image.createImage(width, height);
        }
    }
   public void paint(Graphics g){ 
       paintCustom(g,gm.itemGrMenu);
   }   
    
   Display display;
   Displayable parentView;
   GMenuConfig gm = GMenuConfig.getInstance(); 
   BombusQD bm = BombusQD.getInstance();
   FontClass MFont = FontClass.getInstance();

   
   public final static int MAIN_MENU_ROSTER=1;
   public final static int ACCOUNT_SELECT_MENU=2;
   public final static int ALERT_CUSTOMIZE_FORM=3;
   public final static int ALERT_PROFILE=4;
   public final static int ACTIVE_CONTACTS=5;//
   public final static int CONFIG_FORM=6;
   public final static int CONTACT_MSGS_LIST=7;//
   public final static int SEARCH_FORM=8;
   public final static int SMILE_PEAKER=9;
   public final static int STATUS_SELECT=10;  
   public final static int APPEND_NICK=11;
   public final static int BOOKMARKS=12;  
   public final static int CONFERENCE_FORM=13; 
   public final static int HISTORY_CONFIG=14; 
   public final static int INFO_WINDOW=16;
   public final static int MESSAGE_LIST=17;
   public final static int PRIVACY_MODIFY_LIST=18;  
   public final static int PRIVACY_SELECT=19;
   public final static int SERVICE_DISCOVERY=20; 
   public final static int STATS_WINDOW=21;  
   public final static int VCARD_EDIT=22;  
   public final static int VCARD_VIEW=23; 
   public final static int BROWSER=24; 
   public final static int TRANSFER_MANAGER=25; 
   public final static int DEF_FORM=26;
   public final static int TEXTLISTBOX=27;
   public final static int USERKEYSLIST=28; 
   public final static int RECONNECT=30; 
   public final static int NEWVECTORCHAT=31; 
   
   
   

   private int width;
   private int height;
   protected Font font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
   private int fh = MFont.isCheck()?MFont.getFontHeight():font.getHeight();
   private int size;  
   private Vector menuCommands = new Vector();
   
   
   
   public GMenu(Display display, Displayable parentView, MenuListener menuListener, ImageList il, Vector menuCommands) {
        gm.ml=menuListener;
        this.parentView=parentView;
        this.display=display;
        int size = menuCommands.size();
        gm.commandslist = new String[size];//3
        for (int index=0; index<size; index++) {
            Command c=(Command)menuCommands.elementAt(index);
            gm.commandslist[index]=c.getName();
            gm.menuCommands=menuCommands;     
        }
   }
   
   
   public GMenu(Display display,Displayable parentView,MenuListener menuListener,ImageList il,Vector menuCommands,
           Vector cmdfirstList,Vector cmdsecondList,Vector cmdThirdList){//Количество вкладок
        gm.ml=menuListener;
        this.parentView=parentView;
        this.display=display;
        int size = menuCommands.size();
        gm.commandslist = new String[size];//3
        for (int index=0; index<size; index++) {
            Command c=(Command)menuCommands.elementAt(index);
            gm.commandslist[index]=c.getName();
            gm.menuCommands=menuCommands;
        }
        gm.cmdfirstList=cmdfirstList;
        gm.cmdsecondList=cmdsecondList;
        gm.cmdThirdList=cmdThirdList;
   }
   
   
   private boolean GMenuIn(Vector getList) {
         int size = getList.size();
          gm.commandslistIn = new String[size];
          for (int index=0; index<size; index++) {
            Command c=(Command)getList.elementAt(index);
            gm.commandslistIn[index]=c.getName();
          }
          gm.menuCommandsIn=getList;
        return true;
   }  
   
   
   public void select(boolean inmenu){
       Command cmd;
       gm.itemGrMenu=-1;
        if(inmenu){
          cmd = (Command)gm.menuCommandsIn.elementAt(gm.itemCursorIndexIn); 
          gm.ml.commandAction(cmd, parentView);
          gm.inMenuSelected=false;//сбрасываем флаг
        }else{
          cmd = (Command)gm.menuCommands.elementAt(gm.itemCursorIndex);  
          gm.ml.commandAction(cmd, parentView);
        }
       gm.ml=null;
    }


  void drawImage(Graphics g, int index, int x, int y){
     int ho=g.getClipHeight();
     int wo=g.getClipWidth();
     int xo=g.getClipX();
     int yo=g.getClipY();
     int iy=y-bm.himg_menu*(int)(index>>4);
     int ix=x-bm.wimg_menu*(index&0x0f);
     g.clipRect(x,y, bm.wimg_menu,bm.himg_menu);
     g.drawImage(bm.imageArr[0],ix,iy,Graphics.TOP|Graphics.LEFT);
     g.setClip(xo,yo, wo, ho);
   }; 

   
  public void paintCustom(Graphics g,int itemGrMenu) {
        Graphics graphics=(offscreen==null)? g: offscreen.getGraphics();    
//long s1 = System.currentTimeMillis();
          if(eventMenu==false){
            drawAllItems(g,gm.menuCommands,gm.commandslist,gm.itemCursorIndex); 
          }
          if(eventMenu==true){
           if(gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_NEW_ACCOUNT)>-1
              || gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_REGISTERING)>-1
              || gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_MY_JABBER)>-1
              || gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_TOOLS)>-1                   
              || gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_SERVICE)>-1
              ){
              drawAllItems(g,gm.menuCommandsIn,gm.commandslistIn,gm.itemCursorIndexIn);
           }
         }
/*          
        long s2 = System.currentTimeMillis();
        int ws = g.getFont().stringWidth(Long.toString(s2-s1)+"msec") + 5;
        int fh = g.getFont().getHeight();
        int xpos = width/2-ws/2;
        g.setColor(255,255,0);
        g.fillRect(xpos,1,ws,fh);
        g.setColor(0,0,0);
        g.drawRect(xpos,1,ws-1,fh-1);
        g.drawString(Long.toString(s2-s1)+"msec", xpos+2, 2, g.LEFT|g.TOP);
 */        
        if (graphics != g) g.drawImage(offscreen, 0, 0, Graphics.LEFT | Graphics.TOP);       
  }
   
   boolean eventMenu=false;
   Vector inMenuItems = new Vector();
   
   Gradient fon;
   
   private void eventOk(){
    cursorY=0;       
     if(gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_NEW_ACCOUNT)>-1 ||
        gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_MY_JABBER)>-1){
          GMenuIn(gm.cmdfirstList); eventMenu=true; return;
     } 
     else if(gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_REGISTERING)>-1
        ||gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_TOOLS)>-1
         ){
          GMenuIn(gm.cmdsecondList); eventMenu=true; return;   
     } 
     else if (gm.commandslist[gm.itemCursorIndex].indexOf(SR.MS_SERVICE)>-1){            
          GMenuIn(gm.cmdThirdList); eventMenu=true; return;
     } else{
          gm.itemGrMenu=-1;
     }
   }
   

   
   void drawAllItems(Graphics g,Vector menuCommands,String[] commandslist,int itemCursorIndex){

        fh = bm.himg_menu>fh?bm.himg_menu:fh;

        size = commandslist.length-1;
        int hitem = 0;        
        int maxHeight=commandslist.length;

        int maxwidth=0;
        int len_str=0;
        for (int index=size; index>=0; index--) {
             if(Config.getInstance().executeByNum){
                  len_str  = MFont.isCheck()?MFont.stringWidth(" ["+index+"] "+commandslist[index]):
                  font.stringWidth(" ["+index+"] "+commandslist[index]);
             }else{
                  len_str  = MFont.isCheck()?MFont.stringWidth(commandslist[index]):
                  font.stringWidth(commandslist[index]);
             }
             if(len_str>maxwidth){
               maxwidth=len_str; 
             }
       }
        
       int mHfh = maxHeight*fh;
       gm.maxHeight=mHfh;
       int w = maxwidth + bm.wimg_menu + 10;
       gm.maxWidth=w;
       hitem=mHfh;
        int bgnd_menu=ColorTheme.getARGB(false);
        if (bgnd_menu!=-1){
          int[] pixelArray = new int[width * height];  
          int lengntp = pixelArray.length;
          for(int i = 0; i < lengntp; i++){
            pixelArray[i] = bgnd_menu;
          }
          g.drawRGB(pixelArray, 0, width, 0 , 0 , width, height, true);
          g.drawRect(-1,-1,width+1,height+1);
        }
        
       
       gm.xcoodr=7;//(g.getClipWidth() - w)/2;
       gm.ycoodr= g.getClipHeight() - hitem - 10;
       //(g.getClipHeight()- mHfh)/2;
     /*   
       if(eventMenu){
           g.setFont(font);
           int w3 = font.stringWidth(gm.commandslist[gm.itemCursorIndex]) + 8;
           int x3 = (w - w3)/2 + gm.xcoodr;
           g.setColor(0x000000);
           g.drawRect( x3 , gm.ycoodr - fh , w3 , fh);           
           g.setColor(ColorTheme.getColor(ColorTheme.GRAPHICS_MENU_BGNG_ARGB));
           g.fillRect( x3 + 1, gm.ycoodr - fh + 1, w3 - 1, fh - 1);
           g.setColor(ColorTheme.getColor(ColorTheme.GRAPHICS_MENU_FONT));
           g.drawString( gm.commandslist[gm.itemCursorIndex] , x3 + 4 , gm.ycoodr - fh + 1, g.LEFT|g.TOP);
       }
      */

       g.translate(gm.xcoodr,gm.ycoodr);
       g.setClip(0,0,w+1,mHfh+40);//?

       int alpha_menu=ColorTheme.getARGB(true);
        if (alpha_menu!=-1){
          int[] pixelArray = new int[w * mHfh];  
          int lengntp = pixelArray.length;
          for(int i = 0; i < lengntp; i++){
            pixelArray[i] = alpha_menu;
          }
          g.drawRGB(pixelArray, 0, w, 0 , 0 , w, mHfh, true);
          g.drawRoundRect(0,0,w,mHfh,10,10);
        }else{
          g.setColor(ColorTheme.getColor(ColorTheme.GRAPHICS_MENU_BGNG_ARGB));     
          g.fillRoundRect(1 , 1 , w - 1, mHfh - 1,10,10);
        }
        
          g.setColor(0x000000);
          g.drawRoundRect(0,0 , w, mHfh,10,10);


          if(midlet.BombusQD.cf.gradient_cursor){ //Tishka17
            int yc = 1 + (midlet.BombusQD.cf.animateMenuAndRoster?cursorY:itemCursorIndex*fh);
            fon=new Gradient(1, yc, w, yc+fh, ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_1),
                  ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_2), false);
            fon.paint(g);
            g.setColor(0x000000);
            //g.drawRect(1, 1 + (cf.animateMenuAndRoster?cursorY:itemCursorIndex*fh), w - 1 , fh - 1);
        }else {
            g.setColor(ColorTheme.getColor(ColorTheme.CURSOR_BGND));
            g.fillRoundRect(1, 1 + (midlet.BombusQD.cf.animateMenuAndRoster?cursorY:itemCursorIndex*fh), w - 1 , fh, 8, 8);
        }  
          
    
        if(!MFont.isCheck()) { g.setFont(font);  }
        if(MFont.isCheck()) { MFont.setColor(255,0x000000); }else{ g.setColor(0x000000); }
        
        g.setColor(ColorTheme.getColor(ColorTheme.GRAPHICS_MENU_FONT));
        int x_start = 3 + bm.wimg_menu;//3
        for (int index=0; index<=size; index++) {
           if(gm.itemGrMenu!=GMenu.DEF_FORM){
             Command cmd = (Command)menuCommands.elementAt(index);
             if(bm.imageArr[0]!=null){
               drawImage(g,cmd.getImg(),3, fh*index + 1 );
             }
             cmd=null;
           }            
           if(MFont.isCheck()){
                if(Config.getInstance().executeByNum){
                  MFont.drawString(g," ["+Integer.toString(index)+"] "+ commandslist[index], x_start,fh*index + 1);
                }else{
                  MFont.drawString(g,commandslist[index], x_start, fh*index + 1);                    
                }
            }else{
               if(Config.getInstance().executeByNum){
                  g.drawString(" ["+Integer.toString(index)+"] "+ commandslist[index], x_start,fh*index + 1, g.LEFT|g.TOP);
               }else{
                  g.drawString(commandslist[index], x_start, fh*index + 1, g.LEFT|g.TOP);                   
               }
           }
        }
   }
   
      
    
   private Timer timer;
   int cursorY=0;
   long s1,s2;
   boolean isDown;
   
   
	private static final int ani_msed = 25;
	private void startTimer (boolean isdownpress)
	{
            /*temp closed
		if ( timer == null && cf.animateMenuAndRoster )
		{
                        isDown=isdownpress;
                        cursorY = 
                                isDown ? 
                                ( (eventMenu ? gm.itemCursorIndexIn*fh : gm.itemCursorIndex*fh) - fh) 
                                : 
                                ( (eventMenu ? gm.itemCursorIndexIn*fh : gm.itemCursorIndex*fh)  + fh);
                        s1 = System.currentTimeMillis();
			timer = new Timer();
			timer.schedule( new anTask(), 0, ani_msed );
		}
             */
	}
	private void stopTimer ()
	{
		if ( timer != null )
		{
                        s2 = System.currentTimeMillis();
			timer.cancel();
                        timer = null;
		}
	}

	private final class anTask extends TimerTask
	{
		public void run ()
		{
                        if(isDown){
                            cursorY+=4;
                            if(eventMenu){
                               if(cursorY>=gm.itemCursorIndexIn*fh) { cursorY = gm.itemCursorIndexIn*fh;  stopTimer(); }
                            }else {
                               if(cursorY>=gm.itemCursorIndex*fh) { cursorY = gm.itemCursorIndex*fh;  stopTimer(); }
                            }
                        }else{
                            cursorY-=4;
                            if(eventMenu){
                               if(cursorY<=gm.itemCursorIndexIn*fh) { cursorY = gm.itemCursorIndexIn*fh;  stopTimer(); }
                            }else {
                               if(cursorY<=gm.itemCursorIndex*fh) { cursorY = gm.itemCursorIndex*fh;  stopTimer(); }
                            }
                        }
                    //System.out.println(gm.xcoodr +"," + gm.ycoodr+ ","+gm.maxWidth+","+gm.maxHeight);
                    view.redrawAni(gm.xcoodr,gm.ycoodr,gm.maxWidth,gm.maxHeight-1);  
  		}
	}
     
   

   public void keyPressed(int keyCode) {
     if (eventMenu==true) {
         eventMenu = sendEvent(keyCode);
         return;
     }else{ 
         if (keyCode==Config.SOFT_LEFT || keyCode=='(') {
            eventOk(); 
            cursorY=0;
         }
         else if (keyCode==Config.SOFT_RIGHT || keyCode==')' || keyCode == -11 || keyCode == -8) {//SE: 11-back,-8-стрелка
            gm.itemGrMenu=-1;
            gm.ml=null;
            gm.itemCursorIndex=0;
            cursorY=0;
            //return;
         }
         else 
         {
            if(Config.getInstance().executeByNum){
              switch (keyCode) 
              {
                case KEY_NUM0: gm.itemCursorIndex=0; eventOk(); break;                  
                case KEY_NUM1: gm.itemCursorIndex=1; eventOk(); break;
                case KEY_NUM2: gm.itemCursorIndex=2; eventOk(); break;
                case KEY_NUM3: gm.itemCursorIndex=3; eventOk(); break;  
                case KEY_NUM4: gm.itemCursorIndex=4; eventOk(); break;
                case KEY_NUM5: gm.itemCursorIndex=5; eventOk(); break;  
                case KEY_NUM6: gm.itemCursorIndex=6; eventOk(); break;
                case KEY_NUM7: gm.itemCursorIndex=7; eventOk(); break;  
                case KEY_NUM8: gm.itemCursorIndex=8; eventOk(); break; 
                case KEY_NUM9: gm.itemCursorIndex=9; eventOk(); break; 
                default:
                  try {
                    switch (getGameAction(keyCode)){
                        case UP:    
                             gm.itemCursorIndex--;
                              if(gm.itemCursorIndex<0){
                                gm.itemCursorIndex=size;  
                              }    
                             startTimer(false);
                             break;  
                        case LEFT: break;
                        case RIGHT: break;
                        case DOWN: 
                            gm.itemCursorIndex++;
                             if(gm.itemCursorIndex>size){
                                gm.itemCursorIndex=0;   
                            } 
                            startTimer(true);
                            break;
                        case FIRE: eventOk(); break;
                    }
                  } catch (Exception e) {}                
              }
            } else {
              switch (keyCode) 
              {
                case KEY_NUM2:
                    gm.itemCursorIndex--;
                     if(gm.itemCursorIndex<0){
                        gm.itemCursorIndex=size;  
                     } 
                     startTimer(false);
                     break;
                case KEY_NUM8:
                     gm.itemCursorIndex++;
                     if(gm.itemCursorIndex>size){
                        gm.itemCursorIndex=0;   
                     }  
                     startTimer(true);
                     break;
                case KEY_NUM5: 
                    gm.itemCursorIndexIn=0;
                    eventOk();
                    //startTimer(true);
                    break;
                case KEY_NUM1:                     
                     gm.itemCursorIndex=0;
                    break;
                case KEY_NUM7:                    
                     gm.itemCursorIndex=size;
                    break;
                default:
                  try {
                    switch (getGameAction(keyCode)){
                        case UP:   
                             gm.itemCursorIndex--;
                              if(gm.itemCursorIndex<0){
                                gm.itemCursorIndex=size;  
                              }
                             startTimer(false);
                             break;                            
                        case LEFT: break;
                        case RIGHT: break;
                        case DOWN:   
                            gm.itemCursorIndex++;
                             if(gm.itemCursorIndex>size){
                                gm.itemCursorIndex=0;   
                            } 
                            startTimer(true);
                            break;                          
                        case FIRE: 
                            eventOk();
                            //startTimer(true);
                            break;
                    }
                  } catch (Exception e) {}
                }
            }//Config.getInstance().executeByNum end
         }
     }
    }   
   
   private void closeEvent(){
     gm.inMenuSelected=true; gm.itemGrMenu=-1;
   }
   
   public boolean sendEvent(int keyCode) {
         if (keyCode==Config.SOFT_LEFT || keyCode=='(') {
                     //gm.itemGrMenu=-1;
                     closeEvent();
                     cursorY=0;
                     return false;
         }
        else if (keyCode==Config.SOFT_RIGHT || keyCode==')' || keyCode == -11 || keyCode == -8) {
            gm.itemGrMenu=-1;
            gm.ml=null;
            cursorY=0;
            return false;            
         }
         else 
         {
            if(Config.getInstance().executeByNum){
              switch (keyCode) 
              {
                case KEY_NUM0: gm.itemCursorIndexIn=0; closeEvent(); return false;
                case KEY_NUM1: gm.itemCursorIndexIn=1; closeEvent(); return false;
                case KEY_NUM2: gm.itemCursorIndexIn=2; closeEvent(); return false;
                case KEY_NUM3: gm.itemCursorIndexIn=3; closeEvent(); return false;
                case KEY_NUM4: gm.itemCursorIndexIn=4; closeEvent(); return false;
                case KEY_NUM5: gm.itemCursorIndexIn=5; closeEvent(); return false;
                case KEY_NUM6: gm.itemCursorIndexIn=6; closeEvent(); return false;
                case KEY_NUM7: gm.itemCursorIndexIn=7; closeEvent(); return false;
                case KEY_NUM8: gm.itemCursorIndexIn=8; closeEvent(); return false;
                case KEY_NUM9: gm.itemCursorIndexIn=9; closeEvent(); return false;
                default:
                  try {
                    switch (getGameAction(keyCode)){
                        case UP:   
                             gm.itemCursorIndexIn--;
                              if(gm.itemCursorIndexIn<0){
                                gm.itemCursorIndexIn=size;  
                              }
                             startTimer(false);
                             return true;
                        case LEFT: gm.itemCursorIndexIn=0; cursorY=0; break;
                        //case RIGHT: return true;
                        case DOWN:   
                            gm.itemCursorIndexIn++;
                             if(gm.itemCursorIndexIn>size){
                                gm.itemCursorIndexIn=0;   
                            } 
                            startTimer(true);
                            return true;
                        case FIRE: closeEvent();
                            return false;
                    }
                  } catch (Exception e) {}                 
              }
            } else {             
              switch (keyCode) {
                case KEY_NUM4: gm.itemCursorIndexIn=0; cursorY=gm.itemCursorIndex*fh;
                      break;
              //case KEY_NUM6:
              //     return true;
                case KEY_NUM2:
                     gm.itemCursorIndexIn--;
                     if(gm.itemCursorIndexIn<0){
                        gm.itemCursorIndexIn=size;  
                     } 
                     startTimer(false);
                     return true;
                case KEY_NUM8:
                     gm.itemCursorIndexIn++;
                     if(gm.itemCursorIndexIn>size){
                        gm.itemCursorIndexIn=0;   
                     }     
                     startTimer(true);
                     return true;
                case KEY_NUM5:
                    closeEvent();
                    return false;
                case KEY_NUM1:                     
                     gm.itemCursorIndexIn=0;
                    return true;
                case KEY_NUM7:                    
                     gm.itemCursorIndexIn=size;
                    return true;
                default:
                  try {
                    switch (getGameAction(keyCode)){
                        case UP:   
                             gm.itemCursorIndexIn--;
                              if(gm.itemCursorIndexIn<0){
                                gm.itemCursorIndexIn=size;  
                              }
                             startTimer(false);
                             return true;
                        case LEFT: gm.itemCursorIndexIn=0; cursorY=gm.itemCursorIndex*fh;
                             break;
                        //case RIGHT: return true;
                        case DOWN:   
                            gm.itemCursorIndexIn++;
                             if(gm.itemCursorIndexIn>size){
                                gm.itemCursorIndexIn=0;   
                            }  
                            startTimer(true);
                            return true;                          
                        case FIRE: gm.inMenuSelected=true; gm.itemGrMenu=-1;
                            return false;
                    }
                  } catch (Exception e) {}
              }
            }//Config.getInstance().executeByNum
         }
       return false;
    }      
}
