/*
 * DebugXMLList.java
 */
//#ifdef CONSOLE
//# package Console;
//# 
//# import Client.Config;
//# import Client.Msg;
//# import Client.StaticData;
//# import Messages.MessageList;
//# import java.util.Vector;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
//# import Menu.Command;
//#endif
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import javax.microedition.midlet.MIDlet;
//# import locale.SR;
//#ifdef CONSOLE 
//# import ui.MainBar;
//#endif
//# 
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# public final class DebugXMLList 
//#     extends MessageList {
//# 
//#     private StaticData sd=StaticData.getInstance();
//#     
//#     private Command cmdEnableDisable=new Command(SR.MS_ENABLE_DISABLE, Command.SCREEN, 1);
//#     private Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 3);    
//#     private Command copyReport=new Command("Bugreport to clipboard", Command.SCREEN, 2);
//# 
//#     /** Creates a new instance of XMLList */
//#     public DebugXMLList(Display display, Displayable pView) {
//#         super ();
//#         super.smiles=false;
//# 
//#         commandState();
//#         addCommands();
//#         setCommandListener(this);
//# 
//#         moveCursorHome();
//# 
//# 
//#  	MainBar mainbar=new MainBar("Debug console");
//#          setMainBarItem(mainbar);
//# 
//#         attachDisplay(display);
//#         this.parentView=pView;
//#     }
//#     
//#     public void commandState() {
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
//#         
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdBack);
//#endif
//#         addCommand(copyReport); copyReport.setImg(0x44);
//#              if (midlet.BombusQD.cf.useClipBoard) {
//#                 addCommand(midlet.BombusQD.commands.cmdCopy);
//#                 if (!midlet.BombusQD.clipboard.isEmpty()) addCommand(midlet.BombusQD.commands.cmdCopyPlus);
//#             }          
//#         addCommand(cmdEnableDisable); cmdEnableDisable.setImg(0x26);
//#         addCommand(cmdPurge); cmdPurge.setImg(0x41);//DELETE
//#      
//#     }
//#     
//#     private StringBuffer str = new StringBuffer(0);
//#     
//#     protected void beginPaint() {
//#         str.setLength(0);
//#         str.append(" (")
//#         .append(getItemCount())
//#         .append(")");
//#         
//#         if (!midlet.BombusQD.cf.debug)
//#             str.append(" - Disabled");
//#         
//#         getMainBarItem().setElementAt(str.toString(),1);
//#     }
//#     
//# 
//#     public int getItemCount() {
//#         return midlet.BombusQD.debug.stanzas.size();
//#     }
//#     
//#     protected Msg getMessage(int index) {
//#         Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT, "local", null, null);
//#         try {
//#             msg=midlet.BombusQD.debug.msg(index);
//#         } catch (Exception e) {}
//# 	return msg;
//#     }
//# 
//#     public void keyGreen(){
//# 	Msg m=getMessage(cursor);
//#         String stanza = "";
//#         try {
//#             stanza =  m.toString();
//#         } catch (Exception e) {}
//#         new StanzaEdit(display, this, stanza).setParentView(this);
//#     }
//#     
//#     public void commandAction(Command c, Displayable d) {
//#         super.commandAction(c,d);
//#         
//# 	Msg m=getMessage(cursor);
//#         if (c==cmdEnableDisable) {
//#             midlet.BombusQD.cf.debug=!midlet.BombusQD.cf.debug;
//#             redraw();
//#         }
//# 	if (m==null) return;
//# 
//#         if (c==cmdPurge) { 
//#             clearReadedMessageList();
//#         }     
//#         if(c==copyReport){
//# 
//#         }
//#     }
//# 
//#     private void clearReadedMessageList() {
//#         try {
//#             if (cursor+1==midlet.BombusQD.debug.stanzas.size()) {
//#                 midlet.BombusQD.debug.stanzas.removeAllElements();
//#             }
//#             else {
//#                 for (int i=0; i<cursor+1; i++)
//#                     midlet.BombusQD.debug.stanzas.removeElementAt(0);
//#             }
//#             messages.removeAllElements();
//#         } catch (Exception e) { }
//#         moveCursorHome();
//#         redraw(); 
//#     }
//#     
//#     public void keyClear() { 
//#         clearReadedMessageList();
//#     }
//#     
//#     public void userKeyPressed(int keyCode) {
//#         if (keyCode=='0')
//#             clearReadedMessageList();
//#     }
//#     
//#     public void destroyView(){
//# 	super.destroyView();
//#     }
//# }
//#endif
