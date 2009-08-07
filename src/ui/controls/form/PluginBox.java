/*
 * PluginBox.java
 *
 * Created on 29.07.2009, 16:05
 *
 */

package ui.controls.form;

import Colors.ColorTheme;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import images.RosterIcons;
import Fonts.FontCache;
import javax.microedition.lcdui.Font;
/**
 *
 * @author aqent
 */
public abstract class PluginBox extends IconTextElement {
    
    private boolean state=false;
    private String text="";
    private boolean selectable=true;
    private int colorItem;
    private Font font = Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD|Font.STYLE_ITALIC,Font.SIZE_SMALL);
    int fH = font.getHeight();


    public PluginBox(String text, boolean state) {
        super(RosterIcons.getInstance());
        this.text=text;
        this.state=state;
        colorItem=ColorTheme.getColor(ColorTheme.CONTROL_ITEM);
    }
    
    public String toString() { return text; }

    public void drawItem(Graphics g, int ofs, boolean sel) {
       g.setFont(font);
       int offset= 4;
       if (il!=null) {
            if (getImageIndex()!=-1) {
                offset+=ilImageSize;
                il.drawImage(g, getImageIndex(), 2, imageYOfs);
            }
       }       
       g.clipRect(offset, 0, g.getClipWidth(), itemHeight);
       if (text!=null){
         g.drawString(text, offset-ofs, (ilImageSize-fH)/2, Graphics.TOP|Graphics.LEFT);
       }
       //super.drawItem(g, ofs, sel);
    }

    public void onSelect(){
        state=!state;
        doAction(state);
    }
    
    public int getVHeight(){ 
        itemHeight=(ilImageSize>font.getHeight())?ilImageSize:font.getHeight();
        return itemHeight;
    }    
    
    public abstract void doAction(boolean state);  
    public int getImageIndex(){ return state?0x36:0x37; }
    public boolean getValue() { return state; }
    public boolean isSelectable() { return selectable; }
    /*
    public boolean handleEvent(int keyCode) {
         switch (keyCode) {
            //case 12: // enter
            case 5:
                state=!state;
                return true;
         }
        return false;
    }
     */
}