/*
 * FontClass.java
 *
 * created June 16 2008
 * author magdelphi
 * magdelphi@rambler.ru
 *
 */ 

package Fonts;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.IOException;
import Client.Config;
//#ifdef CONSOLE        
//# import Console.StanzasList;
//#endif        
public class FontClass {
    
    private static FontClass df;
    
    public static byte buff[] = new byte[768];//данные таблицы символов из файла xxxxx.dat
    public static Image fontImage = null;
    public int[] buf;//данные одного символа
    public int width = 0;
    public int Color = 0;
    public int h_char;//высота символов
    public int width_char;//ширина символов
    
    
    public int italic =0;//флаг стил€ символов italic
    private String name_font="";

    public static FontClass getInstance(){
        if (df==null) 
            df=new FontClass();
        return df;
    }    
    
    
    public FontClass() {
    };
  
    public void Init(String name_font) {
        this.name_font=name_font;
        try {//----- загрузка image символов ---------------
            this.fontImage = Image.createImage("/images/fonts/"+name_font+".png");
        InputStream is = getClass().getResourceAsStream("/images/fonts/"+name_font+".dat");
        int off = 0;
        int readBytes = 0;
        int n_buf;
          while ( (readBytes = is.read(buff, off, buff.length)) > -1) {}//копируем в буфер
          h_char=buff[0];//высота символов
           if (buff[1] ==1) {italic=h_char/4;}//если fontstyle = [italic] увеличиваем ширину символа
       n_buf =h_char*h_char;// кол-во байт 1 знакоместо
       this.buf = new int[n_buf];
       is.close();
       is=null;
       System.gc();
      } catch (Exception e) {
          Config.getInstance().use_drawed_font=false;
          //System.out.println("error fonts loading");
          //Config.getInstance().saveToStorage();//?        
      }     
    }      
    
    public boolean isCheck()
    {
      if(name_font.indexOf("no")>-1) {
        return false;
      }  else{
        return Config.getInstance().use_drawed_font;            
      }
    }  
 
    public int getFontHeight()
    { 
      return h_char;
    } 

    
    int lenght_str = 0;
    public int stringWidth(String s)
    { 
        int lenght_str = 0;
        int ind;
        int w_char;
        int length=s.length();
        for (int i = 0; i < length; i++) {
            lenght_str+=getCharWidth(s.charAt(i));
        }

        return lenght_str;
    }    
  
 //  private final static char[] maplen= { 
 //       'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
 //       'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
 //       '0','1','2','3','4','5','6','7','8','9',',','_',':',' ','(',')','.','+','-','@','/'
 //   };    
    
   public int getCharWidth(char c) {
       int result=-1;
        if (fontImage != null) {
             int ch = c;
                    ch = ch == 0x401 ? 0xa8 : ch == 0x451 ? 0xb8 : ch; //401-®,451-Є
                    ch = ch > 0x400 ? ch - 0x350 : ch;
                    //!- 0x21,€ - 0x44f
                    //0xa7 - параграф 
                    
             int ind = ((int)(ch)-0x20)*3;//смещение данных в таблице xxxxx.dat
             int len=0;//смещение в таблице xxxxx.png
           //  int maplenth = maplen.length;
             
           //for(int i=0;i<maplenth;i++)
           //{   
           //   if(c==maplen[i]) {
           //     result=1;  
           //   }
           //}  
           
           //  if(result>=1){
              int hlen = (buff[ind+1] & 0x00ff)<<8;//старший байт
              len=(buff[ind] & 0x00ff)+hlen; //смещение в таблице xxxxx.png
              width_char= buff[ind+2]+italic;//ширина символа
               if (c==' '){width_char=h_char>>2;}//если пробел
                result=width_char;
           //  }
        }
        return result;
    }   


    //¬озвращает значение цвета из составл€ющих alpha-фльфа, RGB
    private int toBGR(int a, int r, int g, int b){
        return (b|(g<<8)|(r<<16)|(a<<24));
    }
    
    //”станавливает текущий цвет отображени€ букв по составл€ющим alpha-aфльфа, RGB
    public void setColor(int a, int r, int g, int b){
        Color=toBGR(a,r,g,b);
    }
    
    public void setColor(int a,int color){
        Color=toBGR(a,getR(color),getG(color),getB(color));
    }    
 
    
    
    public static int getR(int color) {
        return ((color >> 16) & 0xFF);
    }
    public static int getG(int color) {
        return ((color >> 8) & 0xFF);
    }
    public static int getB(int color) {
        return (color& 0xFF);
    } 
    
  

   public int drawChar(Graphics g, char c, int x, int y) {
        int result=0;
        if (fontImage != null) {
          //String s=String.valueOf(c);
          //  unicode to ansi 
            int ch = c;
                    ch = ch == 0x401 ? 0xa8 : ch == 0x451 ? 0xb8 : ch; //401-®,451-Є
                    ch = ch > 0x400 ? ch - 0x350 : ch;
            int ind = ((int)(ch)-0x20)*3;//смещение данных в таблице xxxxx.dat
            int len=0;//смещение в таблице xxxxx.png
            int hlen = (buff[ind+1] & 0x00ff)<<8;//старший байт
            len=(buff[ind] & 0x00ff)+hlen;  //смещение в таблице xxxxx.png
            width_char= buff[ind+2]+italic;//ширина символа
              fontImage.getRGB(buf, 0, width_char, len-2, 0,width_char, h_char);//считать в буфер
                   for(int i=0;i<buf.length;i++)
                   {
                        int color = (buf[i] &0x00ffffff);//читаем только RGB
                        if (color == 0) color =  Color;//если черный красим в цвет
                        buf[i] = color;
                    }
              g.drawRGB(buf, 0, width_char, x, y, width_char, h_char, true);
              //System.out.println(y); 
              if (c==' '){width_char=h_char>>2;} //если пробел
                result=width_char;
         }
        return result;
    }
   

   public void drawString(Graphics g, String s, int x, int y) {

        int w = 0;
        int i = 0;

        int len = s.length();
        if(s.endsWith("   ")){
              len-=3;
              //System.out.println("DRAWED: "+s);              
        }
        for (i = 0; i < len; i++) {
          w = drawChar(g, s.charAt(i), x, y);                
          x=x+w; 
        }
    }

    public void Destroy(){
        buff = null;
        buf = null;
        fontImage = null;
    }
 }
