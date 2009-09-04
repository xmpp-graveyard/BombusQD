/*
 * VirtualList.java 
 *
 * Created on 30.01.2005, 14:46
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
 *
 */

package ui;
import Colors.ColorTheme;

import Fonts.FontCache;
import javax.microedition.lcdui.*;
import Client.*;
import locale.SR;
//#ifdef POPUPS
import ui.controls.PopUp;
//#endif
import ui.controls.Balloon;
import ui.controls.Progress;
import ui.controls.ScrollBar;
import ui.controls.CommandsPointer;
import util.StringUtils;
//#ifdef USER_KEYS
//# import ui.keys.userKeyExec;
//#endif
import java.util.*;
import java.util.Vector;

//#ifdef MENU_LISTENER
import Menu.Command;
import Menu.MenuListener;
//#endif
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import midlet.BombusQD;
import Conference.ConferenceGroup;

public abstract class VirtualList         
    extends Canvas {
    
    public void focusedItem(int index) {}

    abstract protected int getItemCount();

    abstract protected VirtualElement getItemRef(int index);

    protected int getMainBarBGnd() { return ColorTheme.getColor(ColorTheme.BAR_BGND);} 
    protected int getMainBarBGndBottom() { return ColorTheme.getColor(ColorTheme.BAR_BGND_BOTTOM);}
    
    private StaticData sd=StaticData.getInstance();
    public GMenuConfig gm = GMenuConfig.getInstance();

    private int stringHeight;
    
    private int iHeight;
    private int mHeight;
    
//#ifdef GRADIENT
//#     Gradient grIB;
//#     Gradient grMB;
//#     Gradient fon;        
//#endif

    

    public static int panelsState=2;
    private static boolean reverse=false;
    private static boolean paintTop=true;
    private static boolean paintBottom=true;

    public static int phoneManufacturer;
    
    public static void changeOrient(int newOrient) {
        panelsState=newOrient;
        switch (panelsState) {
            case 0: paintTop=false; paintBottom=false; reverse=false; break;
            case 1: paintTop=true;  paintBottom=false; reverse=false; break;
            case 2: paintTop=true;  paintBottom=true;  reverse=false; break;
            case 3: paintTop=false; paintBottom=true;  reverse=false; break;
            case 4: paintTop=true;  paintBottom=false; reverse=true;  break;
            case 5: paintTop=true;  paintBottom=true;  reverse=true;  break;
            case 6: paintTop=false; paintBottom=true;  reverse=true;  break;
        }
    }
    
//#ifdef USER_KEYS
//#     private static final int USER_OTHER_KEY_PRESSED = 1;
//#     private static final int USER_STAR_KEY_PRESSED = 2;
//#     private static final int USER_KEY_EXECUTED = 3;
//# 
//#     private int additionKeyState = USER_OTHER_KEY_PRESSED;
//#endif

//#ifdef POPUPS
    public static PopUp popup;

    public static void setWobble(int type, String contact, String txt){
        popup.addPopup(type, contact, txt);
    }
//#endif
    protected int getMainBarRGB() {return ColorTheme.getColor(ColorTheme.BAR_INK);}
    

    boolean isSel=false;
    
    
    public void eventOk(){
     try {
      ((VirtualElement)getFocusedObject()).onSelect();
       updateLayout();
       fitCursorByTop();
      } catch (Exception e) {} 
    }

    
    
    public void eventLongOk(){
//#ifdef TEST
//#         drawTest = true;
//#endif
    }

    public void userKeyPressed(int keyCode){}
    
    public void userAdditionKeyPressed(int keyCode){}

    public static final short SIEMENS_GREEN=-11;
    
    public static final short NOKIA_PEN=-50;

    public static final short MOTOE680_VOL_UP=-9;
    public static final short MOTOE680_VOL_DOWN=-8;
    public static final short MOTOE680_REALPLAYER=-6;
    public static final short MOTOE680_FMRADIO=-7;
    
    public static final short MOTOROLA_FLIP=-200;
    
    public static final short SE_FLIPOPEN_JP6=-30;
    public static final short SE_FLIPCLOSE_JP6=-31;
    public static final short SE_GREEN=-10;
    
    public static final short SIEMENS_FLIPOPEN=-24;
    public static final short SIEMENS_FLIPCLOSE=-22;
    
    public static final short SIEMENS_VOLUP=-13;
    public static final short SIEMENS_VOLDOWN=-14;
    
    public static final short SIEMENS_CAMERA=-20;
    public static final short SIEMENS_MPLAYER=-21;

    public static short keyClear=-8;
    public static short keyVolDown=0x1000;
    public static short keyBack=-11;
    public static short greenKeyCode=SIEMENS_GREEN;
    
    public static boolean fullscreen=true;
    public static boolean memMonitor;
    public static boolean showBalloons;
    public static boolean showTimeTraffic = true;
    
//#ifdef USER_KEYS
//#     public static boolean userKeys;
//#endif
    public static boolean canBack=true;

    public int width;
    public int height;

    private Image offscreen = null;

    protected int cursor;

    protected boolean stickyWindow=true;
    
    private int itemLayoutY[]=new int[1];
    private int listHeight;
    
//#ifdef BACK_IMAGE
//#     public Image img;
//#     public Image bgnd;
//#endif
    
//#ifdef MENU_LISTENER
    CommandsPointer ar=new CommandsPointer();
//#endif

    protected synchronized void updateLayout(){
        int size=getItemCount();
        if (size==0) {
            listHeight=0;
            return;
        }
        int layout[]=new int[size+1];
        int y=0;
        for (int index=0; index<size; index++){
            y+=getItemRef(index).getVHeight();
            layout[index+1]=y;
        }
        listHeight=y;
        itemLayoutY=layout;
    }
    
    protected int getElementIndexAt(int yPos){
        int end=getItemCount()-1;
        if (end<0) return -1;
        int begin=0;
        while (end-begin>1) {
            int index=(end+begin)/2;
            if (yPos<itemLayoutY[index]) end=index; else begin=index;
        }
        return (yPos<itemLayoutY[end])? begin:end;
    }
    
    public int win_top;
    private int winHeight;
    
    protected int offset;
    
    protected boolean showBalloon;
    
    protected VirtualElement mainbar;
    protected VirtualElement infobar;
    
    private boolean wrapping = true;

    public static int startGPRS=-1;
    public static int offGPRS=0;

    private int itemBorder[];

    private int lastClickX;
    private int lastClickY;
    private int lastClickItem;
    private long lastClickTime;
    
    public void enableListWrapping(boolean wrap) { this.wrapping=wrap; }
    
    public ComplexString getMainBarItem() {return (ComplexString)mainbar;}
    public void setMainBarItem(ComplexString mainbar) { this.mainbar=mainbar; }
    
    public ComplexString getInfoBarItem() {return (ComplexString)infobar;}
    public void setInfoBarItem(ComplexString infobar) { this.infobar=infobar; }    

//#ifdef ELF    
//#     private static boolean sie_accu=true;
//#     private static boolean sie_net=true;
//#endif    
    
    public Object getFocusedObject() { 
        try {
            return getItemRef(cursor);
        } catch (Exception e) { }
        return null;
    }    

    protected Display display;
    protected Displayable parentView;

    protected ScrollBar scrollbar;
    
    /** Creates a new instance of VirtualList */
    
    int scrWidth;
    int scrHeight;
    int imgWidth;
    int imgHeight;  
    

    public void redrawAni(int x,int y,int width,int height){
        Displayable d=display.getCurrent();
        if (d instanceof Canvas) {
            ((Canvas)d).repaint(x,y,width,height);
        }
    }    
    
    public VirtualList() {
        width=getWidth();
        height=getHeight();
//#ifdef POPUPS
        popup = new PopUp();

//#endif
        
//#ifdef GRAPHICS_MENU     
//#         GMenu = new GMenu();
//#endif       
         gm.phoneWidth = width;
         gm.phoneHeight = height;        

        
//#ifdef BACK_IMAGE
//#         try {
//#             if (/*img==null && */ midlet.BombusQD.cf.bgnd_image==1 /*|| midlet.BombusQD.cf.bgnd_image==2*/ ){//img!=null
//#                 Image img=Image.createImage("/images/back.png");
//#                     gm.imgWidth = img.getWidth();
//#                     gm.imgHeight = img.getHeight();  
//#                     gm.img=img;
//#             }else if (midlet.BombusQD.cf.bgnd_image==3) {
//#                 Image bgnd=Image.createImage("/images/bgnd.png");
//#                 ImageList il = new ImageList();
//#                 gm.bgnd=bgnd;
//#             }
//#         } catch (Exception e) { }
//#endif         
        
        
        if (phoneManufacturer==Config.WINDOWS) {
            setTitle("BombusQD");
        }

        changeOrient(midlet.BombusQD.cf.panelsState);

        setFullScreenMode(fullscreen);

        itemBorder=new int[32];

        scrollbar=new ScrollBar();
        scrollbar.setHasPointerEvents(hasPointerEvents());

        MainBar secondBar=new MainBar("", true);
        secondBar.addElement(null); //1
        secondBar.addRAlign();
        secondBar.addElement(null); //3
        setInfoBarItem(secondBar);

        stringHeight=FontCache.getFont(false, FontCache.roster).getHeight();
//#if (USE_ROTATOR)
       if(midlet.BombusQD.cf.useLowMemory_userotator==false){    TimerTaskRotate.startRotate(0, this);  }
//#endif
    }

    /** Creates a new instance of VirtualList */
    public VirtualList(Display display) {
        this();
        attachDisplay(display);
    }

    public void attachDisplay (Display display) {
        this.display=display;
        parentView=display.getCurrent();
        display.setCurrent(this);
        redraw();
    }

    public void redraw(){
        Displayable d=display.getCurrent();
        if (d instanceof Canvas) {
            ((Canvas)d).repaint();
        }
    }
    

    protected void hideNotify() {
	offscreen=null;
    }

    protected void showNotify() {
	if (!isDoubleBuffered()) offscreen=Image.createImage(width, height);
//#if (USE_ROTATOR)
        if(midlet.BombusQD.cf.useLowMemory_userotator==false){    TimerTaskRotate.startRotate(-1, this);    }
//#endif
    }

    
    protected void sizeChanged(int w, int h) {
        width=w;
        height=h;
//#ifdef GRADIENT
//#         iHeight=0;
//#         mHeight=0;
//#endif
        if (!isDoubleBuffered()) offscreen=Image.createImage(width, height);
    }

    protected void beginPaint(){};

    
    public void paint(Graphics graphics) {

        mHeight=0;
        iHeight=0;
        
        Graphics g=(offscreen==null)? graphics: offscreen.getGraphics();
        //System.out.println("paint " + Thread.activeCount());
        //long s1 = System.currentTimeMillis();
        
//#ifdef POPUPS
        popup.init(g, width, height);
//#ifdef GRAPHICS_MENU
//#         if(gm.itemGrMenu>0){
//#           GMenu.init(g, width, height,this);
//#         }
//#         if(gm.ml!=null && gm.itemGrMenu==-1){
//#             GMenu.select(gm.inMenuSelected);
//#         }
//#endif          
        
        
//#endif
        beginPaint();

        //StaticData.getInstance().screenWidth=width;

        int list_bottom=0;        
        itemBorder[0]=0;
        updateLayout();
        
        setAbsOrg(g, 0,0);

        g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
        g.fillRect(0, 0, width, height);
        
//#ifdef BACK_IMAGE
//#         if(midlet.BombusQD.cf.bgnd_image==1){
//#          if (gm.img!=null) {
//# 			for (int xx = 0; xx < width; xx += gm.imgWidth){
//# 				for (int yy = 0; yy < height; yy += gm.imgHeight){
//# 					g.drawImage(gm.img, xx, yy, Graphics.LEFT|Graphics.TOP);   
//#                                         //System.out.println(xx+" "+yy);
//#                                 }
//#                         }
//#           }
//#         }
//#         else if(midlet.BombusQD.cf.bgnd_image==2){
//#           fon=new Gradient(0, 0, width, height, ColorTheme.getColor(ColorTheme.GRADIENT_BGND_LEFT),
//#                   ColorTheme.getColor(ColorTheme.GRADIENT_BGND_RIGHT), true);
//#           fon.paint(g);
//#         }
//#         else if(midlet.BombusQD.cf.bgnd_image==3){
//#           if(gm.bgnd!=null){
//#             g.drawImage(gm.bgnd, 0, 0, Graphics.LEFT|Graphics.TOP);
//#           }
//#         }
//#endif
        
        if (mainbar!=null)
            mHeight=mainbar.getVHeight(); // nokia fix

        if (infobar!=null) {
            setInfo();
            iHeight=infobar.getVHeight(); // nokia fix
        }
        
        if (paintTop) {
            if (reverse) {
                if (infobar!=null) {
                    iHeight=infobar.getVHeight();
                    itemBorder[0]=iHeight;
                    drawInfoPanel(g);
                }
            } else {
                if (mainbar!=null) {
                    itemBorder[0]=mHeight; 
                    drawMainPanel(g);
                }
            }
        }
        if (paintBottom) {
            if (reverse) {
                if (mainbar!=null) 
                    list_bottom=mHeight;
            } else {
                list_bottom=iHeight; 
            }
        }

        winHeight=height-itemBorder[0]-list_bottom;

        int count=getItemCount();
        
        boolean scroll=(listHeight>winHeight);

        if (count==0) {
            cursor=(cursor==-1)?-1:0; 
            win_top=0;
        } else if (cursor>=count) {
            cursor=count-1;
            stickyWindow=true;
        }
        if (count>0 && stickyWindow) fitCursorByTop();
        
        int itemMaxWidth=(scroll) ?(width-scrollbar.getScrollWidth()) : (width);

        int itemIndex=getElementIndexAt(win_top);
        int displayedIndex=0;
        int displayedBottom=itemBorder[0];
   
        int baloon=-1;
        int itemYpos;
        

        try {
            while ((itemYpos=itemLayoutY[itemIndex]-win_top)<winHeight) {
                VirtualElement el=getItemRef(itemIndex);
                
                boolean sel=(itemIndex==cursor);
                int lh=el.getVHeight();
                
                setAbsOrg(g, 0, itemBorder[0]);
                g.setClip(0,0, itemMaxWidth, winHeight); 
                g.translate(0,itemYpos);
                g.setColor(el.getColorBGnd());

                if (sel) {
                    drawCursor(g, itemMaxWidth , lh);
                /*    
                    if(midlet.BombusQD.cf.animateMenuAndRoster){
                      if(itemIndex==0) { 
                           drawCursor(g, itemMaxWidth , lh, true);
                      } else {
                          if(el.getVWidth()==-1){ drawCursor(g, itemMaxWidth , lh, true);  }
                          else{ 
                              drawCursor(g, itemMaxWidth , lh, false);
                          }
                      }
                    }else{
                      drawCursor(g, itemMaxWidth , lh, true);  
                    }
                 */
                   baloon=g.getTranslateY();
                } else {
//#ifdef BACK_IMAGE
//#                     if (gm.img==null && gm.bgnd==null && midlet.BombusQD.cf.bgnd_image!=2) {
//#endif
                      g.fillRect(0,0, itemMaxWidth, lh); //clear field       
                    }
                }
                
                
                g.setColor(el.getColor());
                g.clipRect(0, 0, itemMaxWidth, lh);
                el.drawItem(g, (sel)?offset:0, sel);
                
                itemIndex++;
		displayedBottom=itemBorder[++displayedIndex]=itemBorder[0]+itemYpos+lh;
            }
        } catch (Exception e) { }
/*       
        if(updown) //плавное перемещение курсора
        {
             setAbsOrg(g, 0, 0);
             g.setClip(0,0, itemMaxWidth, winHeight); 
             g.translate(0,16);
             
            int itemHeightNow = getItemRef(getElementIndexAt(cursorPosNew-1)).getVHeight();
            itemYposition = getItemRef(getElementIndexAt(cursorPosNew)).getVHeight();
            
            System.out.println(" "+itemHeightNow+" "+itemYposition);
            
              VirtualElement el=getItemRef(getElementIndexAt(cursorPosNew));
              h2=el.getVHeight();
              g.setClip(0, cursorLowY, itemMaxWidth, el.getVHeight());
              g.setColor(0x00ff00);
              g.fillRect(0, cursorLowY, itemMaxWidth, el.getVHeight() );
              System.out.println("g.fillRect(0,"+cursorLowY + ","+itemMaxWidth + ","+el.getVHeight()+");"); 
              
              //if(cursorLowY>=cursorPageYend)..stop
              //g.translate(0,cursorLowY);
              //drawCursor(g, itemMaxWidth , el.getVHeight() , true);
              //int indexNext = getElementIndexAt(cursorPageYend);
              //.out.println(cursorLowY+" cursorPageYend "+cursorPageYend + " H:"+el.getVHeight() + "   "+indexNext);               
              //itemIndex=indexNext=cursor;
        }     
 */   
        int clrH=height-displayedBottom;

        if ( clrH>0
//#ifdef BACK_IMAGE
//#                 && (gm.img==null && gm.bgnd==null && midlet.BombusQD.cf.bgnd_image!=2)
//#endif
                ) {
            setAbsOrg(g, 0,displayedBottom);
            g.setClip(0, 0, itemMaxWidth, clrH);
            g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
            g.fillRect(0, 0, itemMaxWidth, clrH);
        }

        if (scroll) {
            int correct=(memMonitor)?1:0;
            setAbsOrg(g, 0, itemBorder[0]+correct);
            g.setClip(0, 0, width, winHeight);

	    scrollbar.setPostion(win_top-correct);
	    scrollbar.setSize(listHeight-correct);
	    scrollbar.setWindowSize(winHeight-correct);
	    
	    scrollbar.draw(g);
        } else scrollbar.setSize(0);

        setAbsClip(g, width, height);
        
        drawHeapMonitor(g, itemBorder[0]); //heap monitor
        

        if (paintBottom) {
            if (reverse) {
                if (mainbar!=null) {
                    setAbsOrg(g, 0, height-mHeight);
                    drawMainPanel(g);
//#ifdef MENU_LISTENER
                    if (hasPointerEvents())
                        ar.init(width, height, mHeight);
//#endif
                }
            } else {
                if (infobar!=null) {
                    setAbsOrg(g, 0, height-iHeight);
                    drawInfoPanel(g);
//#ifdef MENU_LISTENER
                    if (hasPointerEvents())
                        ar.init(width, height, iHeight);
//#endif
                }
            }
            setAbsClip(g, width, height);

            if(midlet.BombusQD.sd.roster!=null) {
                    if (midlet.BombusQD.sd.roster.messageCount>0) drawEnvelop(g);
            }
            if (System.currentTimeMillis()-sd.getTrafficIn()<2000) drawTraffic(g, false);
            if (System.currentTimeMillis()-sd.getTrafficOut()<2000) drawTraffic(g, true);
        }
  
        if(gm.itemGrMenu>0){
          //showBalloon=false;
//#ifdef GRAPHICS_MENU              
//#           drawGraphicsMenu(g);
//#endif           
        }else{
            
//#ifdef POPUPS
        if(popUpshow || midlet.BombusQD.cf.popUps){
            drawPopUp(g);
        }
//#endif
        
          if (showBalloon) {
            if (showBalloons) {
                String text=null;
                try {
                    text=((VirtualElement)getFocusedObject()).getTipString();
                } catch (Exception e) { }
                if (text!=null)
                    drawBalloon(g, baloon, text);
            }
          }            
        }

        /*
        if (reconnectWindow.getInstance().isActive()) {
            if (reconnectTimeout>reconnectPos && reconnectPos!=0) {
   
                int strWidth=g.getFont().stringWidth(SR.MS_RECONNECT);
                int progressWidth=(width/3)*2;
                progressWidth=(strWidth>progressWidth)?strWidth:progressWidth;
                int progressX=(width-progressWidth)/2;
                if (pb==null) pb=new Progress(progressX, height/2, progressWidth);
                int popHeight=pb.getHeight();
                g.setColor(ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_BGND));
                g.fillRoundRect(progressX-2, (height/2)-(popHeight*2), progressWidth+4, (popHeight*2)+1, 6, 6);
                g.setColor(ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_INK));
                g.drawRoundRect(progressX-2, (height/2)-(popHeight*2), progressWidth+4, (popHeight*2)+1, 6, 6);
                g.drawString(SR.MS_RECONNECT, width/2, (height/2)-(popHeight*2), Graphics.TOP | Graphics.HCENTER);
                Progress.draw(g, reconnectPos*progressWidth/reconnectTimeout, reconnectString);
            }
        }
         */

        
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

        if (g != graphics) g.drawImage(offscreen, 0, 0, Graphics.LEFT | Graphics.TOP);
    }
    
//#ifdef GRAPHICS_MENU     
//#     public static GMenu GMenu;
//#     
//#     private void drawGraphicsMenu(final Graphics g) {
//# 
//#         GMenu.paintCustom(g,gm.itemGrMenu);
//#     }
//# 
//#endif
    
    
//#ifdef POPUPS]
    protected void drawPopUp(final Graphics g) {
        popup.paintCustom(g);
    }
//#endif   
    
/*    
    private Timer timer;
    int posX = 0;
    
	private static final int anim_time = 50;
	private void startTimer()//,int cursorPosNew,boolean isKeyDwn)
	{
                posX=0;
		if ( timer == null && midlet.BombusQD.cf.animateMenuAndRoster)
		{
			timer = new Timer();
         		timer.schedule( new AnimTask(), 0, anim_time );
		}
	}
	private void stopTimer()
	{
		if ( timer != null )
		{
                        System.out.println("stop!");
			timer.cancel();
                        timer = null;
		}
	}

	private final class AnimTask
		extends TimerTask
	{
		public void run ()
		{
                  posX+=5;
                  System.out.println(posX);
                  if(posX==100){
                    stopTimer();
                  }
  		}
	}  
 */

             
    
    private int getARGB() {
      StringBuffer color=new StringBuffer(10);
      int ccolor = ColorTheme.getColor(ColorTheme.CURSOR_BGND);
      int red, green, blue,alpha;
      long tmp; 
      int alpha_ = Config.getInstance().cursor_bgnd;
      red = ColorTheme.getRed(ccolor);
      green = ColorTheme.getGreen(ccolor);
      blue = ColorTheme.getBlue(ccolor);
      tmp = (alpha_ << 24) | (red << 16) | (green << 8) | blue;
      color=null;
      return (int)tmp;
    }    
    
        
    protected void drawCursor (Graphics g, int width, int height) {
     if(Config.getInstance().cursor_bgnd!=0){
            int alpha_=getARGB();
            int[] pixelArray = new int[width * height];
            int lengntp = pixelArray.length;
            for(int i = 0; i < lengntp; i++){
               pixelArray[i] = alpha_;
            }
            g.drawRGB(pixelArray, 0, width, 0 , 0 , width, height, true);
             //g.drawRect(1,1,width-1,height-1);
      }
     else
     {           
        if(midlet.BombusQD.cf.gradient_cursor){
             fon=new Gradient(0, 0, width, height, ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_1),
                  ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_2), false);
             fon.paint(g);
        }else
        {
         int cursorBGnd=ColorTheme.getColor(ColorTheme.CURSOR_BGND);
         int cursorOutline=ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE);  
           g.setColor(cursorBGnd);
           g.fillRoundRect(0, 0, width, height,8,8);
           g.setColor(cursorOutline);
           g.drawRoundRect(0, 0, width-1, height-1, 8, 8);
         }  
     }
   }

 
/*    
    protected void drawCursor (Graphics g, int width, int height,boolean firstItem) {
        //currentWidth = width;
       if(firstItem){
          drawCursor (g,width,height);
       } 
       else
       {
        g.fillRect(0, 0, cursorX, height);
        if(midlet.BombusQD.cf.gradient_cursor){
          fon=new Gradient(0, 0, cursorX, height, ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_1),
                  ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_2), false);
          fon.paint(g);
        }
        else{
         int cursorBGnd=ColorTheme.getColor(ColorTheme.CURSOR_BGND);
         int cursorOutline=ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE);            
           if (cursorBGnd!=0x010101) {
            g.setColor(ColorTheme.getColor(ColorTheme.CURSOR_BGND));
            g.fillRoundRect(0, 0, cursorX, height, 6, 6);
           }
           if (cursorOutline!=0x010101) {
            g.setColor(cursorOutline);
            g.drawRoundRect(0, 0, cursorX-1, height-1, 6, 6);
           }            
        }
       }
    }
 */

    
   public void pageLeft() {
        if (getItemCount()==0)
            return;
//#ifdef DEBUG
//#         System.out.println("keyLeft");
//#endif
        try {
            stickyWindow=false;
            win_top-=winHeight;
            if (win_top<0) {
                win_top=0;
                //if (!getItemRef(0).isSelectable()) cursor=getNextSelectableRef(-1); else cursor=0;
                cursor=getNextSelectableRef(-1);
            }
            if (!cursorInWindow()) {
                cursor=getElementIndexAt(itemLayoutY[cursor]-winHeight);
                if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) 
                    fitCursorByTop();
            }
            setRotator();
        } catch (Exception e) { }
    }
   
    public void pageRight() {
        if (getItemCount()==0)
            return;
//#ifdef DEBUG
//#         System.out.println("keyRight");
//#endif
        try {
            stickyWindow=false;
            win_top+=winHeight;
            int endTop=listHeight-winHeight;
            if (endTop<win_top) {
                win_top= (listHeight<winHeight)? 0 : endTop;
                int lastItemNum=getItemCount()-1;
                if (getItemRef(lastItemNum).isSelectable()==false)
                    cursor=getPrevSelectableRef(lastItemNum);
                else
                    cursor=lastItemNum;
            } else {
                if (cursorInWindow()==false) {
                    cursor=getElementIndexAt(itemLayoutY[cursor]+winHeight);//yPos
                    if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) 
                        fitCursorByTop();
                }
            }
            setRotator();
        } catch (Exception e) {}
    }    
    
 
    
    
  
    public void keyUp() {
        if (getItemCount()==0)
            return;
//#ifdef DEBUG
//# 	System.out.println("keyUp");
//#endif
        if (cursor==0) {
            if (wrapping) {
                if (getItemRef(getItemCount()-1).isSelectable())
                    moveCursorEnd();
                else
                    moveCursorTo(getItemCount()-2);
            } else {
                itemPageUp();
            }
            setRotator();
            return;
        }

        if (itemPageUp()) return;
        //stickyWindow=true;
        if (getItemRef(cursor-1).isSelectable())
            cursor--;
        else
            cursor=getPrevSelectableRef(cursor);
        fitCursorByBottom();
        setRotator();
    }

    public void keyDwn() { 
        if (getItemCount()==0)
            return;
//#ifdef DEBUG
//#         System.out.println("keyDwn");
//#endif
	if (cursor==(getItemCount()-1)) {
            if (wrapping) {
                moveCursorHome();
            } else {
                itemPageDown();
            }
            setRotator();
            return; 
        }
        if (itemPageDown()) {
            return;
        }
        stickyWindow=true;
        if (getItemRef(cursor+1).isSelectable()) {
            cursor++;
        } else {
            cursor=getNextSelectableRef(cursor);
        }
        setRotator();
    }
    
    
    

    private static int reconnectPos=0;
    private static int reconnectTimeout=0;
    public static boolean reconnectRedraw=false;
    private static String reconnectString="";
    
    private Progress pb;
    public static void drawReconnect(int pos, int timeout, String reconnect) {
        reconnectPos=pos;
        reconnectTimeout=timeout;
        reconnectRedraw=true;
        reconnectString=reconnect;
    }

    private void drawEnvelop(final Graphics g) {
        g.setColor(getMainBarRGB());
        int wpos= (width/2);
        int hpos= height-13;
        
        g.drawRect(wpos-4,	hpos, 	8, 	6);
        g.drawLine(wpos-3,	hpos+1,	wpos,	hpos+4);
        g.drawLine(wpos,	hpos+4,	wpos+3,	hpos+1);
        g.drawLine(wpos-3,	hpos+5,	wpos-2,	hpos+4);
        g.drawLine(wpos+2,	hpos+4,	wpos+3,	hpos+5);
    }
    
    private void drawTraffic(final Graphics g, boolean up) {
        int pos=(up)?(width/2)+3:(width/2)-3;
        int pos2=(up)?height-4:height-2;
        
        //g.setColor((up)?0xff0000:0x00ff00);
        g.setColor(getMainBarRGB());
        g.drawLine(pos, height-5, pos, height-1);
        g.drawLine(pos-1, pos2, pos+1, pos2);       
        g.fillRect(pos-2, height-3, 1, 1);
        g.fillRect(pos+2, height-3, 1, 1);
    }
    
    private void setAbsClip(final Graphics g, int w, int h) {
        setAbsOrg(g, 0, 0);
        g.setClip(0,0, w, h);
    }
    
    
    
    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        setAbsOrg(g,0,balloon);
        Balloon.draw(g, text);
    }
    
    public static void setAbsOrg(Graphics g, int x, int y){
        g.translate(x-g.getTranslateX(), y-g.getTranslateY());
    }    

    private void drawHeapMonitor(final Graphics g, int y) {
        if (memMonitor) {
            int ram=(int)(((long)Runtime.getRuntime().freeMemory()*width)/(long)Runtime.getRuntime().totalMemory());
            g.setColor(ColorTheme.getColor(ColorTheme.HEAP_TOTAL));  g.fillRect(0,y,width,1);
            g.setColor(ColorTheme.getColor(ColorTheme.HEAP_FREE));  g.fillRect(0,y,ram,1);
        }
    }
//#ifndef MENU
    private void drawInfoPanel (final Graphics g) {
        int h=infobar.getVHeight()+1;

        g.setClip(0,0, width, h);
//#ifdef GRADIENT
//#         if (getMainBarBGnd()!=getMainBarBGndBottom()) {
//#             if (iHeight!=h) {
//#                 grIB=new Gradient(0, 0, width, h, getMainBarBGnd(), getMainBarBGndBottom(), false);
//#                 iHeight=h;
//#             }
//#             grIB.paint(g);
//#         } else {
//#             g.setColor(getMainBarBGnd());
//#             g.fillRect(0, 0, width, h);
//#         }
//#else
            g.setColor(getMainBarBGnd());
            g.fillRect(0, 0, width, h);
//#endif

        g.setColor(getMainBarRGB());
        infobar.drawItem(g,(phoneManufacturer==Config.NOKIA && reverse)?17:0,false);
    }
//#endif
    
    private void drawMainPanel (final Graphics g) {    
        int h=mainbar.getVHeight()+1;
        g.setClip(0,0, width, h);
//#ifdef GRADIENT
//#         if (getMainBarBGnd()!=getMainBarBGndBottom()) {
//#             if (mHeight!=h) {
//#                 grMB=new Gradient(0, 0, width, h, getMainBarBGndBottom(), getMainBarBGnd(), false);
//#                 mHeight=h;
//#             }
//#             grMB.paint(g);
//#         } else {
//#             g.setColor(getMainBarBGnd());
//#             g.fillRect(0, 0, width, h);
//#         }
//#else
            g.setColor(getMainBarBGnd());
            g.fillRect(0, 0, width, h);
//#endif
        
        g.setColor(getMainBarRGB());
        mainbar.drawItem(g,(phoneManufacturer==Config.NOKIA && !reverse)?17:0,false);
    }

    public void moveCursorHome(){
        stickyWindow=true;
        if (cursor>0) cursor=getNextSelectableRef(-1);
        setRotator();
    }

    public void moveCursorEnd(){
        stickyWindow=true;
        int count=getItemCount();
        if (cursor>=0) cursor=(count==0)?0:count-1;
        setRotator();
    }

    public void moveCursorTo(int index){
        int count=getItemCount();
        if (index<0) index=0;
        if (index>=count) index=count-1; 
        
        if (getItemRef(index).isSelectable()) cursor=index;
        stickyWindow=true;
        repaint();
    }
    
    public void moveCursorTo(int index, boolean force){ 
        int count=getItemCount();
        if (index<0) index=0;
        if (index>=count) index=count-1;
        cursor=index;
        stickyWindow=true;
        repaint();
    }        
    
    protected void fitCursorByTop(){
        try {
            int top=itemLayoutY[cursor];
            if (top<win_top) win_top=top;   
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                int bottom=itemLayoutY[cursor+1]-winHeight;
                if (bottom>win_top) win_top=bottom;  
            }
            if (top>=win_top+winHeight) win_top=top; 
        } catch (Exception e) { }
    }
    
    protected void fitCursorByBottom(){
        try {
            int bottom=itemLayoutY[cursor+1]-winHeight;
            if (bottom>win_top) win_top=bottom;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                int top=itemLayoutY[cursor];
                if (top<win_top) win_top=top;
            }
            if (itemLayoutY[cursor+1]<=win_top) win_top=bottom;
        } catch (Exception e) {}
    }

    protected int kHold;
    
    protected void keyRepeated(int keyCode){ key(keyCode); }
    protected void keyReleased(int keyCode) { kHold=0; }
    protected void keyPressed(int keyCode) { kHold=0; key(keyCode); }
    
    protected void pointerPressed(int x, int y) {
        System.out.println("pointerPressed("+x+", "+y+")");
//#ifdef POPUPS
        popup.next();
//#endif        
//#ifdef MENU_LISTENER
        int act=ar.pointerPressed(x, y);
        if (act==1) {
             touchLeftPressed();
             stickyWindow=false;
             return;
        } else if (act==2) {
            touchRightPressed();
            stickyWindow=false;
            return;
        }
//#endif
        if (scrollbar.pointerPressed(x, y, this)) {
            stickyWindow=false;
            return;
        }
	int i=0;
	while (i<32) {
	    if (y<itemBorder[i]) break;
	    i++;
	}
	if (i==0 || i==32) return;
	//System.out.println(i);
	if (cursor>=0) {
            moveCursorTo(getElementIndexAt(win_top)+i-1);
            setRotator();
        }
	
	long clickTime=System.currentTimeMillis();
	if (cursor==lastClickItem) {
	    if (lastClickY-y<5 && y-lastClickY<5) {
                if (clickTime-lastClickTime<500){
                    //System.out.println("short");
		    y=0;
		    eventOk();
                }
            }
        }
	lastClickTime=clickTime;
        lastClickX=x;
	lastClickY=y;
	lastClickItem=cursor;

        int il=itemLayoutY[cursor+1]-winHeight;
        if (il>win_top) win_top=il;
        il=itemLayoutY[cursor];
        if (il<win_top) win_top=il;
      repaint();
    }
     
     int yPointerPos;
     
   protected void pointerDragged(int x, int y) {
      if (phoneManufacturer!=Config.NOKIA_5800) {
          if (scrollbar.pointerDragged(x, y, this)) stickyWindow=false;   
      }
      else{
        if (scrollbar.pointerDragged(x, y, this)) {
            stickyWindow=false;
            return;
        } 
        
        int dy = y-yPointerPos;
        
        yPointerPos=y;
        
        win_top-=dy;
        
        if (win_top+winHeight>listHeight) win_top=listHeight-winHeight;
        if (win_top<0) win_top=0;
        
        stickyWindow=false;
	if (cursor>=0) {
            cursor=getElementIndexAt(win_top+y-itemBorder[0]);
            setRotator();
        }
        repaint();           
      }
    }   
    
    protected void pointerReleased(int x, int y) { 
        scrollbar.pointerReleased(x, y, this); 
        
	long clickTime=System.currentTimeMillis();
        if (lastClickY-y<5 && y-lastClickY<5) {
            if (clickTime-lastClickTime>500) {
                y=0;
                eventLongOk();
            }
        }
    }
    
//#ifdef USER_KEYS
//#     private void additionKeyPressed(int keyCode) {
//#         switch (keyCode) {
//#             case KEY_NUM0: userKeyExec.getInstance().commandExecute(display, 0); break;
//#             case KEY_NUM1: userKeyExec.getInstance().commandExecute(display, 1); break;
//#             case KEY_NUM2: userKeyExec.getInstance().commandExecute(display, 2); break;
//#             case KEY_NUM3: userKeyExec.getInstance().commandExecute(display, 3); break;
//#             case KEY_NUM4: userKeyExec.getInstance().commandExecute(display, 4); break;
//#             case KEY_NUM5: userKeyExec.getInstance().commandExecute(display, 5); break;
//#             case KEY_NUM6: userKeyExec.getInstance().commandExecute(display, 6); break;
//#             case KEY_NUM7: userKeyExec.getInstance().commandExecute(display, 7); break;
//#             case KEY_NUM8: userKeyExec.getInstance().commandExecute(display, 8); break;
//#             case KEY_NUM9: userKeyExec.getInstance().commandExecute(display, 9); break;
//#             case KEY_POUND: userKeyExec.getInstance().commandExecute(display, 10); break;
//#         }
//#     }
//#endif
    
    private boolean sendEvent(int keyCode) {
        int key=-1;
        switch (keyCode) {
            case KEY_NUM0: key=0; break;
            case KEY_NUM1: key=1; break;
            case KEY_NUM2: key=2; break;
            case KEY_NUM3: key=3; break;
            case KEY_NUM4: key=4; break;
            case KEY_NUM5: key=5; break;
            case KEY_NUM6: key=6; break;
            case KEY_NUM7: key=7; break;
            case KEY_NUM8: key=8; break;
            case KEY_NUM9: key=9; break;
            case KEY_STAR: key=10; break;
            case KEY_POUND: key=11; break;
            default:
                try {
                    switch (getGameAction(keyCode)){
                        case UP: key=2; break;
                        case LEFT: key=4; break;
                        case RIGHT: key=6; break;
                        case DOWN: key=8; break;
                        case FIRE: key=12; break;
                    }
                } catch (Exception e) {}
                if (keyCode==Config.KEY_BACK) key=13;
        }
         
        if (key>-1) {
//#ifdef POPUPS
            if (popup.size()>0) {
                return popup.handleEvent(key);
            } else  
//#endif
            if (getFocusedObject()!=null)
                return ((VirtualElement)getFocusedObject()).handleEvent(key);
        }
                
        return false;
    }
    
/*  
    public void reconnectYes() {
        reconnectWindow.getInstance().reconnect();
        //reconnectDraw=false;
        redraw();
    }
    
    public void reconnectNo() {
        reconnectWindow.getInstance().stopReconnect();
        //reconnectDraw=false;
        redraw();
    }
 */
 
    
//#ifdef MENU_LISTENER
    public Vector menuCommands=new Vector();
    
    public Vector cmdfirstList=new Vector();
    public Vector cmdsecondList=new Vector();
    public Vector cmdThirdList=new Vector();    

    public void addCommand(Command command) {
        if (menuCommands.indexOf(command)<0)
            menuCommands.addElement(command);
    }
    
    public void addInCommand(int countMenu,Command command) {
        if(countMenu==1){
          if (cmdfirstList.indexOf(command)<0)cmdfirstList.addElement(command);            
        } else if(countMenu==2){
          if (cmdsecondList.indexOf(command)<0) cmdsecondList.addElement(command);   
        } else if(countMenu==3){
          if (cmdThirdList.indexOf(command)<0) cmdThirdList.addElement(command);   
        }
    } 
    
    public void removeInCommand(int countMenu,Command command) {
        if(countMenu==1){
          if (cmdfirstList.indexOf(command)<0)cmdfirstList.removeElement(command);            
        } else if(countMenu==2){
          if (cmdsecondList.indexOf(command)<0) cmdsecondList.removeElement(command);   
        } else if(countMenu==3){
          if (cmdThirdList.indexOf(command)<0) cmdThirdList.removeElement(command);   
        }        
    }
    
    public void removeCommand(Command command) {
        menuCommands.removeElement(command);        
    }
    
    public void touchLeftPressed(){
//#ifdef GRAPHICS_MENU
//#          gm.itemGrMenu = showGraphicsMenu();
//#          repaint();
//#else
        showMenu();
//#endif  
    }
   
    
//#ifdef GRAPHICS_MENU        
//#         public int showGraphicsMenu() { return -10; }
//#else
    public void showMenu() {};
//#endif     
    
 
    public void setCommandListener(MenuListener menuListener) {
    }
    
    public Command getCommand(int index) {
        if (index>menuCommands.size()-1) return null;
        return (Command) menuCommands.elementAt(index);
    }
   

    public void touchRightPressed(){
        destroyView();
    }
//#endif    
    
    
    private boolean popUpshow=false;

    private void key(int keyCode) {
//#ifdef GRAPHICS_MENU    
//#      if(gm.itemGrMenu>0){ //Активное меню
//#          GMenu.keyPressed(keyCode);
//#          repaint();
//#      }
//#      else{ 
//#        if(midlet.BombusQD.cf.isOptionsSel){
//#         isSel=false;   
//#         if (keyCode==KEY_NUM5) {
//#             eventOk();
//#             redraw();
//#             return;
//#          }     
//#        } 
//#if DEBUG
//#    System.out.println(keyCode);
//#endif
//#ifdef POPUPS
//#         if (keyCode==greenKeyCode) {
//#             if (popup.getContact()!=null) {
//#                    if(Config.getInstance().useClassicChat){
//#                       new SimpleItemChat(display,sd.roster,sd.roster.getContact(popup.getContact(), false));            
//#                    }else{
//#                        Contact c = sd.roster.getContact(popup.getContact(), false);
//#                        if(midlet.BombusQD.cf.animatedSmiles) images.SmilesIcons.startTimer();
//#                        if(c.cList!=null && midlet.BombusQD.cf.module_cashe && c.msgs.size()>3 ){
//#                           display.setCurrent( (ContactMessageList)c.cList );
//#                        }else{
//#                           new ContactMessageList(c,display);  
//#                        }
//#                       //new ContactMessageList(sd.roster.getContact(popup.getContact(), false),display);
//#                    }                
//#                 popup.next();
//#                 return;
//#             } else if (phoneManufacturer==Config.MOTO || phoneManufacturer==Config.NOKIA || phoneManufacturer==Config.NOKIA_9XXX) {
//#                 keyGreen();
//#                 return;
//#             }
//#         }
//#endif
//#ifdef MENU_LISTENER
//#         if (keyCode==Config.SOFT_LEFT || keyCode=='(') {
//#            /*
//#             if (reconnectWindow.getInstance().isActive()) {
//#                 reconnectYes();
//#                 return;
//#             }
//#             */
//#             gm.itemCursorIndex=0;
//#             gm.itemCursorIndexIn=0;
//#              touchLeftPressed();
//#             return;
//#         }
//#          if (keyCode==Config.SOFT_RIGHT || keyCode==')') {
//#            /*
//#             if (reconnectWindow.getInstance().isActive()) {
//#                 reconnectNo();
//#                 return;
//#             }
//#             */
//#              touchRightPressed();
//#             return;
//#          }
//#else
//#          if (keyCode==Config.SOFT_RIGHT) {
//#             if (phoneManufacturer!=Config.SONYE || phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2 || phoneManufacturer==Config.MOTO) {
//#                if (canBack==true)
//#                     destroyView();
//#                 return;
//#             }
//#          }
//#endif
//#         if (sendEvent(keyCode)) {
//#             repaint();
//#             return;
//#         }
//#ifdef USER_KEYS
//#         if (userKeys) {
//#             switch (additionKeyState) {
//#                 case USER_OTHER_KEY_PRESSED:
//#                 case USER_KEY_EXECUTED:
//#                     additionKeyState=(keyCode==KEY_STAR)?USER_STAR_KEY_PRESSED:USER_OTHER_KEY_PRESSED;
//#                     break;
//#                 case USER_STAR_KEY_PRESSED:
//#                     additionKeyState=(keyCode!=KEY_STAR)?USER_KEY_EXECUTED:USER_STAR_KEY_PRESSED;
//#                     additionKeyPressed(keyCode);
//#                     break;
//#             }
//#         }
//#endif
//#         
//#     switch (keyCode) {
//#         case 0: 
//#             break;
//#         case KEY_NUM1:
//#             moveCursorHome();  
//#             break;
//#         case KEY_NUM2:
//#             keyUp();    
//#             break; 
//#         case KEY_NUM4:
//#             userKeyPressed(keyCode);
//#             break; 
//#         case KEY_NUM6:
//#             userKeyPressed(keyCode);
//#             break;
//#         case KEY_NUM7:
//# /*            
//#             if(running_animation==true){
//#                 midlet.BombusQD.cf.flagQuerySign=false;
//#                 at.stop();
//#             }else{
//#                 midlet.BombusQD.cf.flagQuerySign=true;
//#                 at.start();
//#             }
//#  */
//#             moveCursorEnd();
//#             break;
//#         case KEY_NUM8:
//#             keyDwn();
//#             break; 
//#         case KEY_STAR:
//#             popUpshow=true;
//#             System.gc();
//#             try { Thread.sleep(50); } catch (InterruptedException ex) { }
//#ifdef POPUPS
//#             StringBuffer mem = new StringBuffer();
//#             mem.append("Time: ")
//#                 .append(Time.getTimeWeekDay())
//#                 .append("\nTraffic: ")
//#                 .append(getTraffic())
//#                 .append("\n");
//#             
//#             mem.append(SR.MS_MEMORY);
//#             mem.append("\n");
//#                   long free = Runtime.getRuntime().freeMemory()>>10;
//#                   long total = Runtime.getRuntime().totalMemory()>>10; 
//#              mem.append("BombusQD use: "+ Long.toString(total-free)+" kb\n");
//#              mem.append("Free/Total: "+Long.toString(free)+"/"+Long.toString(total)+"kb\n" );
//#              mem.append("*Stanzas(in/out): "+Integer.toString(midlet.BombusQD.cf.inStanz)+"/"+Integer.toString(midlet.BombusQD.cf.outStanz));            
//#             setWobble(1, null, mem.toString());
//#endif
//#             break;
//#ifdef POPUPS
//#         case KEY_POUND:
//#             //if (midlet.BombusQD.cf.popUps) {
//#             popUpshow=true;
//#                 try {
//#                     String text=((VirtualElement)getFocusedObject()).getTipString();
//#                     if (text!=null) {
//#                         setWobble(1, null, text);
//#                     }
//#                 } catch (Exception e) { }
//#             //}
//#             break;
//#endif
//# 
//#         default:
//#             try {
//#                 switch (getGameAction(keyCode)){
//#                     case UP:
//#                         keyUp();
//#                         break;
//#                     case DOWN:
//#                         keyDwn();
//#                         break;
//#                     case LEFT:
//#                         pageLeft();
//#                         break;
//#                     case RIGHT:
//#                         pageRight();
//#                         break;
//#                     case FIRE:
//#                         eventOk();
//#                         break;
//#                 default:
//#                     if (keyCode==keyClear) { keyClear(); break; }
//#                     if (keyCode==keyVolDown) { moveCursorEnd(); break; }
//#                     if (keyCode=='5') {  eventOk(); break; }
//#                     if (keyCode==Config.KEY_BACK /*&&  canBack==true*/) { destroyView(); }
//#                     if (keyCode==greenKeyCode) { keyGreen(); }
//# 
//#                     userKeyPressed(keyCode);
//#                 }
//#             } catch (Exception e) {/* IllegalArgumentException @ getGameAction */}
//#         }
//#         repaint();
//#      }
//#else
       if(midlet.BombusQD.cf.isOptionsSel){
        isSel=false;   
        if (keyCode==KEY_NUM5) {
            eventOk();
            redraw();
            return;
         }     
       } 
//#if DEBUG
//#    System.out.println(keyCode);
//#endif
//#ifdef POPUPS
        if (keyCode==greenKeyCode) {
            if (popup.getContact()!=null) {
                   if(Config.getInstance().useClassicChat){
                      new SimpleItemChat(display,sd.roster,sd.roster.getContact(popup.getContact(), false));            
                   }else{
                      new ContactMessageList(sd.roster.getContact(popup.getContact(), false),display);
                   }                
                popup.next();
                return;
            } else if (phoneManufacturer==Config.MOTO || phoneManufacturer==Config.NOKIA || phoneManufacturer==Config.NOKIA_9XXX) {
                keyGreen();
                return;
            }
        }
//#endif
//#ifdef MENU_LISTENER
        if (keyCode==Config.SOFT_LEFT || keyCode=='(') {
            if (reconnectWindow.getInstance().isActive()) {
                reconnectYes();
                return;
            }
             touchLeftPressed();
            return;
        }
         if (keyCode==Config.SOFT_RIGHT || keyCode==')') {
            if (reconnectWindow.getInstance().isActive()) {
                reconnectNo();
                return;
            }
             touchRightPressed();
            return;
         }
//#else
//#          if (keyCode==Config.SOFT_RIGHT) {
//#             if (phoneManufacturer!=Config.SONYE || phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2 || phoneManufacturer==Config.MOTO) {
//#                if (canBack==true)
//#                     destroyView();
//#                 return;
//#             }
//#          }
//#endif
        if (sendEvent(keyCode)) {
            repaint();
            return;
        }
//#ifdef USER_KEYS
//#         if (userKeys) {
//#             switch (additionKeyState) {
//#                 case USER_OTHER_KEY_PRESSED:
//#                 case USER_KEY_EXECUTED:
//#                     additionKeyState=(keyCode==KEY_STAR)?USER_STAR_KEY_PRESSED:USER_OTHER_KEY_PRESSED;
//#                     break;
//#                 case USER_STAR_KEY_PRESSED:
//#                     additionKeyState=(keyCode!=KEY_STAR)?USER_KEY_EXECUTED:USER_STAR_KEY_PRESSED;
//#                     additionKeyPressed(keyCode);
//#                     break;
//#             }
//#         }
//#endif
        
    switch (keyCode) {
        case 0: 
            break;
        case KEY_NUM1:
            moveCursorHome();  
            break;
        case KEY_NUM2:
            keyUp();    
            break; 
        case KEY_NUM4:
            userKeyPressed(keyCode);
            break; 
        case KEY_NUM6:
            userKeyPressed(keyCode);
            break;
        case KEY_NUM7:
/*            
            if(running_animation==true){
                midlet.BombusQD.cf.flagQuerySign=false;
                at.stop();
            }else{
                midlet.BombusQD.cf.flagQuerySign=true;
                at.start();
            }
 */
            moveCursorEnd();
            break;
        case KEY_NUM8:
            keyDwn();    
            break; 
        case KEY_STAR:
            System.gc();
            try { Thread.sleep(50); } catch (InterruptedException ex) { }
            break;
//#ifdef POPUPS
        case KEY_POUND:
            //if (midlet.BombusQD.cf.popUps) {
            popUpshow=true;
                try {
                    String text=((VirtualElement)getFocusedObject()).getTipString();
                    if (text!=null) {
                        setWobble(1, null, text);
                    }
                } catch (Exception e) { }
            //}
            break;
//#endif

        default:
            try {
                switch (getGameAction(keyCode)){
                    case UP:
                        keyUp();
                        break;
                    case DOWN:
                        keyDwn();
                        break;
                    case LEFT:
                        pageLeft();
                        break;
                    case RIGHT:
                        pageRight();
                        break;
                    case FIRE:
                        eventOk();
                        break;
                default:
                    if (keyCode==keyClear) { keyClear(); break; }
                    if (keyCode==keyVolDown) { moveCursorEnd(); break; }
                    if (keyCode=='5') {  eventOk(); break; }
                    if (keyCode==Config.KEY_BACK /*&&  canBack==true*/) { destroyView(); }
                    if (keyCode==greenKeyCode) { keyGreen(); }

                    userKeyPressed(keyCode);
                }
            } catch (Exception e) {/* IllegalArgumentException @ getGameAction */}
        }
        repaint();
//#endif         
    }
  

    
    public int getPrevSelectableRef(int curRef) {
        int prevRef=curRef;
        boolean process=true;
        while (process) {
            prevRef--;
            if (getItemRef(prevRef).isSelectable())
                break;
            if (prevRef==0 && wrapping)
                prevRef=getItemCount();
        }
        
        return prevRef;
    }

    public int getNextSelectableRef(int curRef) {
        int nextRef=curRef;
        boolean process=true;
        while (process) {
            nextRef++;
            if (nextRef==getItemCount() && wrapping)
                nextRef=0;
            if (getItemRef(nextRef).isSelectable())
                break;
        }
        
        return nextRef;
    }

    private boolean itemPageDown() {
        try {
            stickyWindow=false;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                stickyWindow=true;
                return false;
            }

            if (!cursorInWindow()) {
                return false;
            }
            
            int remainder=itemLayoutY[cursor+1]-win_top;
            if (remainder<=winHeight) {
                return false;
            }
            if (remainder<=2*winHeight) {
                win_top=remainder-winHeight+win_top+8;
                return true;
            }
            win_top+=winHeight-stringHeight;//-stringHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }
    
    private boolean itemPageUp() {
        try {
            stickyWindow=false;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                //stickyWindow=true;
                return false;
            }

            if (!cursorInWindow()) { return false; }
            
            int remainder=win_top-itemLayoutY[cursor];
            if (remainder<=0) return false;
            if (remainder<=winHeight) {
                win_top=itemLayoutY[cursor];
                return true;
            }
            win_top-=winHeight-stringHeight;//-stringHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }

 
    
    public boolean cursorInWindow(){
        try {
            int y1=itemLayoutY[cursor]-win_top;
            int y2=itemLayoutY[cursor+1]-win_top;
            if (y1>=winHeight) return false;
            if (y2>=0) return true;
        } catch (Exception e) { }
        return false;
    }
    
    protected void keyClear() {}
    protected void keyGreen() { eventOk(); }
    
    protected  void setRotator(){
//#if (USE_ROTATOR)
     if(midlet.BombusQD.cf.useLowMemory_userotator==false){           
        try {
            if (getItemCount()<1) return;
            focusedItem(cursor);
        } catch (Exception e) { return; }
        
        if (cursor>=0) {
            int itemWidth=getItemRef(cursor).getVWidth();
            if (itemWidth>=width-scrollbar.getScrollWidth()) 
                itemWidth-=width/2; 
            else 
                itemWidth=0;
            TimerTaskRotate.startRotate(itemWidth, this);
        }
     }
 //#endif
    }

    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }

    public void destroyView(){
        sd.roster.activeContact=null;
        if (display!=null && parentView!=null) /*prevents potential app hiding*/ 
            display.setCurrent(parentView);
    }

    public int getListWidth() {
        return width-scrollbar.getScrollWidth()-2;
    }

    public final static void sort(Vector sortVector){
        try {
            synchronized (sortVector) {
                int f, i;
                IconTextElement left, right;
                int size=sortVector.size();
                for (f = 1; f < size; f++) {
                    left=(IconTextElement)sortVector.elementAt(f);
                    right=(IconTextElement)sortVector.elementAt(f-1);
                    if ( left.compare(right) >=0 ) continue;
                    i = f-1;
                    while (i>=0){
                        right=(IconTextElement)sortVector.elementAt(i);
                        if (right.compare(left) <0) break;
                        sortVector.setElementAt(right,i+1);
                        i--;
                    }
                    sortVector.setElementAt(left,i+1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); /* ClassCastException */
        }
    }
    
    public int getCursor() {
        return cursor;
    }
    

    
    public void setInfo() {
        getInfoBarItem().setElementAt((!showTimeTraffic)?touchLeftCommand():Time.timeLocalString(Time.utcTimeMillis()), 1);
        getInfoBarItem().setElementAt((!showTimeTraffic)?touchRightCommand():getTraffic(), 3);
    }

    public String getTraffic() {
        long traffic = StaticData.getInstance().traffic;
        return StringUtils.getSizeString((traffic>0)?traffic:0);
    }   

    public String touchLeftCommand(){ return (midlet.BombusQD.cf.oldSE)?SR.MS_BACK:SR.MS_MENU; }
    public String touchRightCommand(){ return (midlet.BombusQD.cf.oldSE)?SR.MS_MENU:SR.MS_BACK; }
    public void cmdCancel() {  destroyView();  }
}

//#if (USE_ROTATOR)    
class TimerTaskRotate extends Thread{
    private int scrollLen;
    private int scroll; //wait before scroll * sleep
    private int balloon; // show balloon time

    private boolean scrollline;
    
    private VirtualList attachedList;
    
    private static TimerTaskRotate instance;
    
    private TimerTaskRotate() {
        start();
    }
    
    public static void startRotate(int max, VirtualList list){
        //Windows mobile J9 hanging test
        if (Config.getInstance().phoneManufacturer==Config.WINDOWS) {
            list.showBalloon=true;
            list.offset=0;
            return;
        }
        if (instance==null) 
            instance=new TimerTaskRotate();
        
        if (max<0) {
            //instance.destroyTask();
            list.offset=0;
            return;
        }
        
        synchronized (instance) {
            list.offset=0;
            instance.scrollLen=max;
            instance.scrollline=(max>0);
            instance.attachedList=list;
            instance.balloon  = 20;
            instance.scroll   = 10;
        }
    }
    
    public void run() {
        while (true) {
            try {  sleep(100);  } catch (Exception e) { instance=null; break; }

            synchronized (this) {
                if (scroll==0) {
                    if (        instance.scroll()
                            ||  instance.balloon()
                        )
                        try { attachedList.redraw(); } catch (Exception e) { instance=null; break; }
                } else {
                    scroll --;                    
                }
                if (attachedList.reconnectRedraw) {
                    attachedList.reconnectRedraw=false;
                    try { attachedList.redraw(); } catch (Exception e) { instance=null; break; }
                }
            }
        }
    }

    public boolean scroll() {
        synchronized (this) {
            if (scrollline==false || attachedList==null || scrollLen<0)
                return false;
            if (attachedList.offset>=scrollLen) {
                scrollLen=-1; attachedList.offset=0; scrollline = false;
            } else 
                attachedList.offset+=6;

            return true;
        }
    }
    
    public boolean balloon() {
        synchronized (this) {
            if (attachedList==null || balloon<0)
                return false;
            balloon--;
            attachedList.showBalloon=(balloon<20 && balloon>0);
            return true;
        }
    }
}
//#endif























