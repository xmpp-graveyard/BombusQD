/*
 * ActivityText.java
 *
 * Created on 11.12.2005, 20:43
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
//#ifdef PEP 
//# package Menu;
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import ui.controls.form.TextInput;
//# import ui.controls.form.DefForm;
//# import javax.microedition.lcdui.TextField;
//# import com.alsutton.jabber.JabberDataBlock;
//# import com.alsutton.jabber.datablocks.Iq;
//# import Mood.UserActivityResult;
//# import Mood.EventPublish;
//# import Client.StaticData;
//# import Client.Config;
//# import ui.VirtualList;
//# import ui.controls.form.SimpleString;
//# 
//# class ActivityText extends DefForm {
//#     private Display display;
//#     private TextInput text;
//#     private String cat="";
//#     private String descr="";
//#     EventPublish ap = new EventPublish();    
//#     
//#     public ActivityText(Display display, Displayable pView,String category,String descr) {
//#         super(display, pView,"Text:");
//#         
//#         this.display=display;
//#         
//#         this.cat=category;
//#         this.descr=descr;
//#         
//#         if(cat==null && descr==null){
//#           itemsList.addElement(new SimpleString(locale.Activity.no_activity + "?", true)); 
//#           Config.getInstance().actCat=null;
//#           Config.getInstance().actText=null;
//#           Config.getInstance().actDescr=null;
//#         }
//#         
//#         if(cat!=null && descr!=null){
//#           text=new TextInput(display,"Text", "", "text", TextField.ANY);
//#           itemsList.addElement(text);            
//#         }
//#         
//#         if(cat!=null && descr==null){
//#            itemsList.addElement(new SimpleString(locale.SR.MS_PUBLISH+"?", true)); 
//#            Config.getInstance().actDescr=null;
//#         }
//#                 
//#         
//#         attachDisplay(display);
//#         this.parentView=pView;
//# 
//#     }
//#     
//#     public void cmdOk() {
//#         String msgtext="";
//#         if(cat!=null && descr!=null){
//#           msgtext = text.getValue();            
//#            if(msgtext.length()<1){
//#               msgtext = null;
//#            }
//#         }else{
//#            msgtext = null; 
//#         }
//#         publishActivity(cat,descr,msgtext);
//#         destroyView();
//#     }
//#     
//#     public void publishActivity(final String category, final String descr,String text) {
//# /*
//# <iq type='set' 
//#     from='juliet@capulet.lit/ca486eba-0f9a-11dc-8835-000bcd821bfb'
//#     id='publish1'>
//#   <pubsub xmlns='http://jabber.org/protocol/pubsub'>
//#     <publish node='http://jabber.org/protocol/activity'>
//#       <item>
//#         <activity xmlns='http://jabber.org/protocol/activity'>
//#           <relaxing>
//#             <partying/>
//#           </relaxing>
//#           <text>My nurse&apos;s birthday!</text>
//#         </activity>
//#       </item>
//#     </publish>
//#   </pubsub>
//# </iq>
//#  */             
//#         String sid="publish-action";
//#         JabberDataBlock setMood=new Iq(null, Iq.TYPE_SET, sid);
//#         JabberDataBlock action=setMood.addChildNs("pubsub", "http://jabber.org/protocol/pubsub").addChild("publish", null);
//#         action.setAttribute("node", "http://jabber.org/protocol/activity");
//#         JabberDataBlock item=action.addChild("item", null);
//# 
//#             JabberDataBlock act=item.addChildNs("activity", "http://jabber.org/protocol/activity");
//#          if(cat!=null){
//#             Config.getInstance().actCat=category;    
//#             JabberDataBlock one = act.addChild(category, null);//relaxing
//#             
//#             if(descr!=null){
//#                 Config.getInstance().actDescr=descr;
//#                 one.addChild(descr,null);
//#             } //partying
//#             if(text!=null){
//#                 Config.getInstance().actText=text;
//#                 act.addChild("text",text);
//#             }
//#          }
//#          try {
//#             StaticData.getInstance().roster.theStream.addBlockListener(new UserActivityResult(display, sid));             
//#             StaticData.getInstance().roster.theStream.send(setMood);
//#             Config.getInstance().saveToStorage();
//#             setMood=null;
//#             action=null;
//#          } catch (Exception e) {e.printStackTrace(); }   
//# 
//#    }     
//#     
//#     public void destroyView()	{
//# 	if (display!=null) display.setCurrent(parentView);
//#     }
//# }
//#endif