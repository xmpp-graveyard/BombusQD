/*
 * Checkers.java
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
package xmpp.extensions.games;
import Client.Contact;
import javax.microedition.lcdui.*;
import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Vector;
import Client.Roster;
import ui.ImageBuffer;

public class Checkers extends Canvas implements CommandListener, JabberBlockListener {

    private Display display;
    private Displayable parentView;
    
    private Command addMsg = new Command("Add message", Command.SCREEN, 97);
    private Command myHideCommand = new Command("Hide(not work)", Command.SCREEN, 98);
    private Command myExitCommand = new Command("End Game", Command.EXIT, 99);

    private Contact contact;
    boolean wait_screen=true; 
    boolean game=false;
    boolean firstRun=false;
    
    boolean stepFIRST_player=true;
    
    int[] coodrinats;
    
    ImageBuffer ib = ImageBuffer.getInstance();
    int xR,yR,xR_,yR_;
    
    public Checkers(Display display, Contact contact,boolean wait_screen,boolean firstRun) {
        this.display = display;
        this.contact = contact;
        this.wait_screen=wait_screen;
        this.firstRun=firstRun;
        parentView=display.getCurrent();
        addCommand(myHideCommand);
        addCommand(myExitCommand);
        addCommand(addMsg);
        setCommandListener(this);
        display.setCurrent(this);
                
        int width = getWidth();
        int height = getHeight();
        
        if(firstRun){
            //checkersPos
           if(contact.checkersPos==null) {
             contact.checkersPos = new String[][] { 
                {"b",  "1",   "2",   "3",    "4",    "5",   "6",   "7",   "8"    },
                {"1",  "-",   "b",   "-",    "b",    "-",    "b",   "-",   "b"   },
                {"2",  "b",   "-",   "b",    "-",    "b",    "-",   "b",   "-"   },
                {"3",  "-",   "b",   "-",    "b",    "-",    "b",   "-",   "b"   },
                {"4",  "-",   "-",   "-",    "-",    "-",    "-",   "-",   "-"   },
                {"5",  "-",   "-",   "-",    "-",    "-",    "-",   "-",   "-"   },                
                {"6",  "w",   "-",   "w",    "-",    "w",    "-",   "w",   "-"   },
                {"7",  "-",   "w",   "-",    "w",    "-",    "w",   "-",   "w"   },
                {"8",  "w",   "-",   "w",    "-",    "w",    "-",   "w",   "-"   }
             };
             xR=0;
             yR=5*20;
           }
        }else{
           if(contact.checkersPos==null) {
              contact.checkersPos = new String[][] {
                {"w",  "1",    "2",    "3",    "4",     "5",    "6",    "7",    "8"   },
                {"1",  "-",    "w",    "-",    "w",     "-",    "w",    "-",    "w"   },
                {"2",  "w",    "-",    "w",    "-",     "w",    "-",    "w",    "-"   },
                {"3",  "-",    "w",    "-",    "w",     "-",    "w",    "-",    "w"   },
                {"4",  "-",    "-",    "-",    "-",     "-",    "-",    "-",    "-"   },
                {"5",  "-",    "-",    "-",    "-",     "-",    "-",    "-",    "-"   },                
                {"6",  "b",    "-",    "b",    "-",     "b",    "-",    "b",    "-"   },
                {"7",  "-",    "b",    "-",    "b",     "-",    "b",    "-",    "b"   },
                {"8",  "b",    "-",    "b",    "-",     "b",    "-",    "b",    "-"   }              
             };
             xR=0;
             yR=5*20;
           }
           stepFIRST_player=false;
        }
        setFullScreenMode(true);
        StaticData.getInstance().roster.theStream.addBlockListener(this);        
        game=true;
    }

    
    private void closeGame(){
        JabberDataBlock jdb = new Iq(contact.getJid(), 2, "checkers");
        jdb.addChildNs("query", "checkers").setAttribute("state", "game_close");
        StaticData.getInstance().roster.theStream.send(jdb);
        StaticData.getInstance().roster.theStream.cancelBlockListener(this);
        contact.setCheckers(-1);
        contact.checkersPos=null;
    }

    public void commandAction(Command c, Displayable displayable) {
        if(c == myExitCommand) {
            closeGame();
            destroyView();
        }
        if(c == myHideCommand) {
            //contact.setCheckers(2);
            //destroyView();
        }
        if(c==addMsg){
            
        }
    }
    

    public void destroyView(){
        if (display!=null)
            display.setCurrent(StaticData.getInstance().roster);
    }


    public int blockArrived(JabberDataBlock data) { 
        if (data instanceof Iq) {
            System.out.println(data);
            if (data.getTypeAttribute().equals("result") && data.getAttribute("from").equals(contact.getJid())) {
                JabberDataBlock query=data.getChildBlock("query");
                   if (query!=null){
                        //Ответ с другой стороны
                        if (query.isJabberNameSpace("checkers")) {
                          if(query.getAttribute("state").equals("game_ok")){
                            wait_screen=false;
                            System.out.println("wait_screen was closed");
                            repaint();
                            return BLOCK_PROCESSED;
                          }
                          else if(query.getAttribute("state").equals("game_close")){
                            game=false;
                            removeCommand(myHideCommand);
                            repaint();
                            return BLOCK_REJECTED;
                          }
                        }
                    } 
                if (!data.getAttribute("id").equals("step")) //listen 2 player
                    return BLOCK_REJECTED;      
                   
                String p1 = data.getAttribute("p1");
                String p2 = data.getAttribute("p2");
                String del = data.getAttribute("del");

                if(p1.length()>0 && p2.length()>0){
                    stepFIRST_player=true;
                     contact.checkersPos[Integer.parseInt(p1.substring(0,1))][Integer.parseInt(p1.substring(1,2))]="-";
                     contact.checkersPos[Integer.parseInt(p2.substring(0,1))][Integer.parseInt(p2.substring(1,2))]=firstRun?"b":"w";
                     if(!del.equals("-1")){
                      contact.checkersPos[Integer.parseInt(del.substring(0,1))][Integer.parseInt(del.substring(1,2))]="-";
                     }
                    startAnalys=false;
                    repaint();
                   return BLOCK_REJECTED;
                }
                //..
              repaint();
              return BLOCK_PROCESSED;
            }
        }
        return BLOCK_REJECTED;
    }

    
    protected void paint(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        g.fillRect(0, 0, width, height);
        g.setColor(0xFFFBF0);
        
      if(wait_screen){
          g.drawString("waiting for game..",20,20,Graphics.LEFT|Graphics.TOP); 
      }else{
          if(game==false){
              g.drawString("game closed by another player",20,20,Graphics.LEFT|Graphics.TOP); 
              return;
          }
          g.translate(5,5);
          g.drawImage(ib.bgnd_checkers,0,0,Graphics.TOP|Graphics.LEFT);

          //указатель
          if(stepFIRST_player==false){
              g.setColor(0xff0000);
              g.drawString("step another player!",20,20,Graphics.LEFT|Graphics.TOP);
          }else{
              g.setColor(0x000000);
              g.fillRect(xR,yR,20,20);
          }
          if(startAnalys){
             for(byte r = 0; r < analysRects.size(); r++){ 
               int i1 = Integer.parseInt( ((String)analysRects.elementAt(r)).substring(0,1));
               int i2 = Integer.parseInt( ((String)analysRects.elementAt(r)).substring(1,2));          
               g.setColor(0x0000ff);
               g.fillRect((i2-1)*20,(i1-1)*20,20,20);
               if(stepAnalys>-1){
                 g.setColor(0x00ff00);
                 g.drawRect(xR_,yR_,20,20);
               }
             }
          }
          for(byte i = 1; i <= 8; i++) {
            for(byte j = 1; j <= 8; j++){
              if( ((String)contact.checkersPos[i][j]).startsWith("b") ){
                g.drawImage(ib.checkers_black, (j-1)*20 + 2, (i-1)*20 + 2, Graphics.TOP|Graphics.LEFT);
              } 
              else if( ((String)contact.checkersPos[i][j]).startsWith("w") ){
                g.drawImage(ib.checkers_white, (j-1)*20 + 2, (i-1)*20 + 2, Graphics.TOP|Graphics.LEFT);
              }
          }}
      }
    }    

    boolean startAnalys = false;
    Vector objects = new Vector();
    Vector avsteps = new Vector();
    Vector mySteps = new Vector();
    int myStepSize=0;
    int step=0;
    
    int stepAnalys=-1;
    
    private String inverse(String s){
      if(s.startsWith("-1")){
          return "-1";
      }
      String res = "";
      String res_ = "";
      switch(s.charAt(0)){
          case '1': res="8"; break;
          case '2': res="7"; break;
          case '3': res="6"; break;
          case '4': res="5"; break;
          case '5': res="4"; break;
          case '6': res="3"; break;
          case '7': res="2"; break;
          case '8': res="1"; break;
      }        
       switch(s.charAt(1)){
          case '1': res_="8"; break;
          case '2': res_="7"; break;
          case '3': res_="6"; break;
          case '4': res_="5"; break;
          case '5': res_="4"; break;
          case '6': res_="3"; break;
          case '7': res_="2"; break;
          case '8': res_="1"; break;          
      }
      return res+res_;
    }
    
    private void checkStep(int press){
      switch(press){
          case 4:
              if(startAnalys){
                  stepAnalys-=1;
                  if(stepAnalys<0){
                    stepAnalys=analysRects.size()-1;
                  }
                  int i1 = Integer.parseInt( ((String)analysRects.elementAt(stepAnalys)).substring(0,1));//6
                  int i2 = Integer.parseInt( ((String)analysRects.elementAt(stepAnalys)).substring(1,2));//1
                  yR_=(i1-1)*20;
                  xR_=(i2-1)*20;
              }else{
                goStep(false);
              }
              break;
          case 6:
              if(startAnalys){
                  stepAnalys+=1;
                  if(stepAnalys>analysRects.size()-1){
                    stepAnalys=0;
                  }
                  int i1 = Integer.parseInt( ((String)analysRects.elementAt(stepAnalys)).substring(0,1));//6
                  int i2 = Integer.parseInt( ((String)analysRects.elementAt(stepAnalys)).substring(1,2));//1
                  yR_=(i1-1)*20;
                  xR_=(i2-1)*20;                  
              }else{
                goStep(true);
              }
              break;
          case 5:
              if(startAnalys){
                //начальное положение
                int i1 = yR/20+1;
                int i2 = xR/20+1;
                
                //конечный выбор хода
                int i1_ = yR_/20+1; //координаты 
                int i2_ = xR_/20+1; //координаты
                
                contact.checkersPos[i1][i2]="-"; //у себя
                contact.checkersPos[i1_][i2_]=firstRun?"w":"b";
                
                String del = "-1";
                
                for(byte r = 0; r < analysRects.size(); r++){
                  if(analysRects.elementAt(r).toString().indexOf("DEL")>-1){
                    System.out.println("ANALIZ: "+(String)analysRects.elementAt(r));
                    int d_1 = Integer.parseInt( ((String)analysRects.elementAt(r)).substring(5,6));
                    int d_2 = Integer.parseInt( ((String)analysRects.elementAt(r)).substring(6,7));
                    String d1 = Integer.toString(d_1);
                    String d2 = Integer.toString(d_2);
                    del = d1+d2;
                    contact.checkersPos[d_1][d_2]="-";
                  }
                }

                sendCoordCommand("step",
                        inverse(Integer.toString(i1)+Integer.toString(i2)), 
                        inverse(Integer.toString(i1_)+Integer.toString(i2_)), inverse(del));
                
                //проверка
                //firstRun?"b":"w"
                int count=0;
                for(byte i = 1; i <= 8; i++) {//b вниз i
                  for(byte j = 1; j <= 8; j++){//b влево j
                   if( ((String)contact.checkersPos[i][j]).startsWith(firstRun?"w":"b") ) {
                      //проверка противоположных шашек на доске
                      count+=1;
                   }
                  }  
                }
                if(count==0){
                   //станза победы 
                }
                
                stepAnalys=-1;
                startAnalys=false;
                stepFIRST_player=false;//временный запрет хода после отправки      
              }
              else//startAnalys==false
              {
                int i1 = yR/20+1;
                int i2 = xR/20+1;
                doAnalysStep(i1,i2);
                 startAnalys=true;
                 stepAnalys=0;
                 int i1_ = Integer.parseInt( ((String)analysRects.elementAt(0)).substring(0,1));//6
                 int i2_ = Integer.parseInt( ((String)analysRects.elementAt(0)).substring(1,2));//1
                 yR_=(i1_-1)*20;
                 xR_=(i2_-1)*20;
              }
              break;
      }
    }
    
    void sendCoordCommand(String str, String p1, String p2,String del) {
        JabberDataBlock iq=new Iq(contact.getJid(), Iq.TYPE_RESULT, str);
        iq.setAttribute("p1", p1); 
        iq.setAttribute("p2", p2);
        iq.setAttribute("del",del);
        if (!StaticData.getInstance().roster.isLoggedIn()) 
            return;
        StaticData.getInstance().roster.theStream.send(iq);
    }    

    Vector analysRects = new Vector();
    private int doAnalysStep(int i1,int i2){
         analysRects.removeAllElements();
         if(i2 == 1){
         //6-1  ->  5-2,(7-2 если дамка)
                       if(((String)contact.checkersPos[i1][i2]).startsWith("!")){//дамка
                         for(byte p = 1; p <= 2; p++){
                           if( check("-",i1-1,i2+1) && p == 1) { addAnalys(i1-1,i2+1);  } else {  if(check(firstRun?"b":"w",i1-1,i2+1) && p == 1){  addDEL(i1-2,i2+2, i1-1,i2+1); };  } //5-2 верх
                           if( check("-",i1+1,i2+1) && p == 2) { addAnalys(i1+1,i2+1);  } else {  if(check(firstRun?"b":"w",i1+1,i2+1) && p == 2){  addDEL(i1+2,i2+2, i1+1,i2+1); };  } //7-2 низ
                         }
                       }else{
                           if( check("-",i1-1,i2+1)) { addAnalys(i1-1,i2+1); } else {  if((check(firstRun?"b":"w",i1-1,i2+1) && check("-",i1-2,i2+2))){ addDEL(i1-2,i2+2, i1-1,i2+1); };  }
                       }
                  }
         else if(i2 == 8){
         //5-8  ->  4-7,(6-7 если дамка)
                       if(((String)contact.checkersPos[i1][i2]).startsWith("!")){//дамка
                         for(byte r = 1; r <= 2; r++){
                           if( check("-",i1-1,i2-1)  && r == 1){ addAnalys(i1-1,i2-1);  } else {  if(check(firstRun?"b":"w",i1-2,i2-2) && r == 1){  addDEL(i1-2,i2-2, i1-2,i2-2); };  }//4-7
                           if( check("-",i1+1,i2-1)  && r == 2){ addAnalys(i1+1,i2-1);  } else {  if(check(firstRun?"b":"w",i1+2,i2-2) && r == 2){  addDEL(i1+2,i2-2, i1+2,i2-2); };  }//6-7
                         }
                       }else{
                           if( check("-",i1-1,i2-1)) { addAnalys(i1-1,i2-1); } else {  if((check(firstRun?"b":"w",i1-1,i2-1) && check("-",i1-2,i2-2))){ addDEL(i1-2,i2-2, i1-1,i2-1); };  }//5-2
                       }
                  }
        else{
        //6-3 ->  5-2,5-4,(7-2,7-4 если дамка)
                       if(((String)contact.checkersPos[i1][i2]).startsWith("!")){//дамка
                         for(byte k = 1; k <= 4; k++){
                           if( check("-",i1-1,i2-1) && k == 1){  addAnalys(i1-1,i2-1);  } else {  if(check(firstRun?"b":"w",i1-2,i2-2) && k == 1){  addDEL(i1-2,i2-2, i1-2,i2-2); };  }//5-2
                           if( check("-",i1-1,i2+1) && k == 2){  addAnalys(i1-1,i2+1);  } else {  if(check(firstRun?"b":"w",i1-2,i2+2) && k == 2){  addDEL(i1-2,i2+2, i1-2,i2+2); };  }//5-4
                           if( check("-",i1+1,i2-1) && k == 3){  addAnalys(i1+1,i2-1);  } else {  if(check(firstRun?"b":"w",i1+2,i2-2) && k == 3){  addDEL(i1+2,i2-2, i1+2,i2-2); };  }//7-2
                           if( check("-",i1+1,i2+1) && k == 4){  addAnalys(i1+1,i2+1);  } else {  if(check(firstRun?"b":"w",i1+2,i2+2) && k == 4){  addDEL(i1+2,i2+2, i1+2,i2+2); };  }//7-4
                         }
                       }else{
                         for(byte t = 1; t <= 2; t++){
                           if( check("-",i1-1,i2-1)  && t == 1){  addAnalys(i1-1,i2-1); } else{ if((check(firstRun?"b":"w",i1-1,i2-1) && check("-",i1-2,i2-2))) { addDEL(i1-2,i2-2, i1-1,i2-1); }; }//5-2
                           if( check("-",i1-1,i2+1)  && t == 2){  addAnalys(i1-1,i2+1); } else{ if((check(firstRun?"b":"w",i1-1,i2+1) && check("-",i1-2,i2+2))) { addDEL(i1-2,i2+2, i1-1,i2+1); }; }//5-4
                         }
                       }
         }
         System.out.println("result of AnalysStep end analysRects: " + analysRects.size() + " : " + analysRects.toString());
         repaint();
       return analysRects.size();
    }
    
    
    private void addAnalys(int x,int y){
       analysRects.addElement(Integer.toString(x) + Integer.toString(y));
    }
    
    
    private void addDEL(int x,int y,int delx,int dely){
       analysRects.addElement(Integer.toString(x) + Integer.toString(y)+"DEL"+Integer.toString(delx) + Integer.toString(dely));
    }    
    
    
    private boolean check(String symbol,int x,int y){
        try {
           return ((String)contact.checkersPos[x][y]).startsWith(symbol);
        } catch(ArrayIndexOutOfBoundsException e) { return false; }
    }       

    
    private void goStep(boolean isRigth){
       mySteps.removeAllElements();
       objects.removeAllElements();
              for(byte i = 1; i <= 8; i++) {
                for(byte j = 1; j <= 8; j++){
                   if( ((String)contact.checkersPos[i][j]).startsWith(firstRun?"w":"b") ) {
                      objects.addElement(Integer.toString(i) + Integer.toString(j));
                   }
                }  
              }
              String check_ = firstRun?"w":"b";
              
              for(byte i = 0; i < objects.size(); i++){
                  int i1 = Integer.parseInt( ((String)objects.elementAt(i)).substring(0,1));//6
                  int i2 = Integer.parseInt( ((String)objects.elementAt(i)).substring(1,2));//1
                  avsteps.removeAllElements();
                  if(i2 == 1){
                    //6-1  ->  5-2,(7-2 если дамка)
                       if(((String)contact.checkersPos[i1][i2]).startsWith("!")){//дамка
                         for(byte p = 1; p <= 2; p++){
                           if( (check("-",i1-1,i2+1) || check(firstRun?"b":"w",i1-1,i2+1)) && p == 1) { add(i1-1,i2+1);  } //5-2
                           if( (check("-",i1+1,i2+1) || check(firstRun?"b":"w",i1+1,i2+1)) && p == 2) { add(i1+1,i2+1);  } //7-2
                         }
                       }else{
                           if( check("-",i1-1,i2+1) || check(firstRun?"b":"w",i1-1,i2+1) ) { add(i1-1,i2+1); }//5-2
                       }
                  }
                  else if(i2 == 8){
                      //5-8  ->  4-7,(6-7 если дамка)
                       if(((String)contact.checkersPos[i1][i2]).startsWith("!")){//дамка
                         for(byte r = 1; r <= 2; r++){
                           if( (check("-",i1-1,i2-1) || check(firstRun?"b":"w",i1-1,i2-1)) && r == 1){  add(i1-1,i2-1);  }//4-7
                           if( (check("-",i1+1,i2-1) || check(firstRun?"b":"w",i1+1,i2-1)) && r == 2){  add(i1+1,i2-1);  }//6-7
                         }
                       }else{
                           if( (check("-",i1-1,i2-1))|| check(firstRun?"b":"w",i1-1,i2-1)) { add(i1-1,i2-1);   }//5-2
                       }
                  }                  
                  else{
                    //6-3 ->  5-2,5-4,(7-2,7-4 если дамка)
                       if(((String)contact.checkersPos[i1][i2]).startsWith("!")){//дамка
                         for(byte k = 1; k <= 4; k++){
                           if( (check("-",i1-1,i2-1) || check(firstRun?"b":"w",i1-1,i2-1)) && k == 1){  add(i1-1,i2-1);  }//5-2
                           if( (check("-",i1-1,i2+1) || check(firstRun?"b":"w",i1-1,i2+1)) && k == 2){  add(i1-1,i2+1);  }//5-4
                           if( (check("-",i1+1,i2-1) || check(firstRun?"b":"w",i1+1,i2-1)) && k == 3){  add(i1+1,i2-1);  }//7-2
                           if( (check("-",i1+1,i2+1) || check(firstRun?"b":"w",i1+1,i2+1)) && k == 4){  add(i1+1,i2+1);  }//7-4
                         }
                       }else{
                         for(byte t = 1; t <= 2; t++){
                           if( (check("-",i1-1,i2-1) || check(firstRun?"b":"w",i1-1,i2-1)) && t == 1){  add(i1-1,i2-1); }//5-2
                           if( (check("-",i1-1,i2+1) || check(firstRun?"b":"w",i1-1,i2+1)) && t == 2){  add(i1-1,i2+1); }//5-4
                         }
                       }
                  }
                if(avsteps.size()>0){
                 mySteps.addElement(objects.elementAt(i));
                 myStepSize=mySteps.size();
                }
              }
              if(isRigth){
               step+=1;
               if(step>(myStepSize-1)){
                 step=0;
               }
              }else{
                 step-=1;
                 if(step<0){
                   step=myStepSize-1;
                 }                  
              }
              int i1 = Integer.parseInt( ((String)mySteps.elementAt(step)).substring(0,1));//6
              int i2 = Integer.parseInt( ((String)mySteps.elementAt(step)).substring(1,2));//1          
              yR=(i1-1)*20;
              xR=(i2-1)*20;
    }
    
    
    private void add(int x,int y){
       avsteps.addElement(Integer.toString(x) + Integer.toString(y));
    }
     
    public void keyPressed(int keyCode) {  
          int action = getGameAction(keyCode);   
          if(!stepFIRST_player){
              repaint();
              return;
          }
          switch (action) {
              case LEFT:
                  checkStep(4);
                  break;
              case RIGHT:
                  checkStep(6);
                  break;
              case UP:
                  checkStep(2);
                  break;
              case DOWN:
                  checkStep(8);
                  break;
              case FIRE:
                  checkStep(5);
                  break;
          }
          repaint();
          //serviceRepaints();
    }
}
