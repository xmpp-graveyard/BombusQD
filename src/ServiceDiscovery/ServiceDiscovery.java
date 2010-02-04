/*
 * ServiceDiscovery.java 
 *
 * Created on 4.06.2005, 21:12
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
//#ifdef SERVICE_DISCOVERY
package ServiceDiscovery;
//#ifndef WMUC
import Conference.ConferenceForm;
//#endif
import images.RosterIcons;
import images.MenuIcons;
import java.util.*;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import Colors.ColorTheme;
import ui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import Client.*;
import ui.MainBar;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.EOFException;
import ui.controls.AlertBox;
import xmpp.XmppError;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//#endif 
import util.Strconv;
import VCard.*;
import Client.DiscoSearchForm;

/**
 *
 * @author Evg_S,aqent
 */
public class ServiceDiscovery 
        extends VirtualList
        implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener,
//#endif
        JabberBlockListener
{
    private final static String NS_ITEMS="http://jabber.org/protocol/disco#items";
    private final static String NS_INFO="http://jabber.org/protocol/disco#info";
    private final static String NS_REGS="jabber:iq:register";
    private final static String NS_SRCH="jabber:iq:search";
    private final static String NS_GATE="jabber:iq:gateway";
    private final static String NS_MUC="http://jabber.org/protocol/muc";
    private final static String NODE_CMDS="http://jabber.org/protocol/commands";

    //private final static String strCmds="Execute";
    //private final int AD_HOC_INDEX=17;
    
    //private Command cmdAdd=new Command(SR.get(SR.MS_ADD_TO_ROSTER), Command.SCREEN, 11); //FS#464 => this string is commented in SR.get(SR.java'
    private Command cmdOk;
    private Command cmdRfsh;
    private Command cmdFeatures;
    private Command cmdSrv;
    private Command cmdBack;
    private Command cmdCancel;
    private Command cmdShowStatistics;
    private Command cmdShowNextItems;
    private Command cmdShowPrevItems;
    private int window = 0;
    
    private StaticData sd=StaticData.getInstance();
    
    private Vector items;
    private Vector stackItems=new Vector(0);
    
    private Vector features;
    private Vector nextElements = new Vector(0);
    
    private Vector cmds;
    
    private String service;
    private String node;

    private int discoIcon;

    private JabberStream stream;

    
    private String[] ICQ_transports = {
       "icq.sudouser.ru","icq.mydebian.de","icq.catap.ru","icq.jabber.kiev.ua","icq.jabber.rfei.ru","icq.jabe.ru","icq.freeside.ru"
    };
    
    private String[] MRIM_transports = {
       "mrim.jabber.infos.ru","mrim.sudouser.ru","mrim.jabbim.org","mrim.udaff.com"
    };
    
    private String[] VKONTAKTE_transports = {
       "vkontakte.zoo.dontexist.net","vkontakte.glubina.org","vkontakte.vjabbere.ru","vkontakte.finenet.ru","vk.sski.ru"
    };
    
    
    /** Creates a new instance of ServiceDiscovery */
    public ServiceDiscovery(Display display, String service, String node, boolean search) {
        super(display);

        cmdOk=new Command(SR.get(SR.MS_BROWSE), Command.SCREEN, 1);
        cmdRfsh=new Command(SR.get(SR.MS_REFRESH), Command.SCREEN, 2);
        cmdFeatures=new Command(SR.get(SR.MS_FEATURES), Command.SCREEN, 3);
        cmdSrv=new Command(SR.get(SR.MS_SERVER), Command.SCREEN, 10);
        cmdBack=new Command(SR.get(SR.MS_BACK), Command.BACK, 98);
        cmdCancel=new Command(SR.get(SR.MS_CANCEL), Command.EXIT, 99);
        cmdShowStatistics=new Command(SR.get(SR.MS_STATICSTICS), Command.SCREEN, 4);
        cmdShowNextItems=new Command("Next", Command.SCREEN, 5);
        cmdShowPrevItems=new Command("Prev", Command.SCREEN, 6);
    
        setMainBarItem(new MainBar(3, null, null, false));
        getMainBarItem().addRAlign();
        getMainBarItem().addElement(null);
        
        stream=sd.roster.theStream;
        stream.cancelBlockListenerByClass(this.getClass());
        stream.addBlockListener(this);
        //sd.roster.discoveryListener=this;
        
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#else
//#         addCommand(cmdBack);
//#endif
        setCommandListener(this);
        addCommand(cmdShowStatistics); cmdShowStatistics.setImg(0x57);       
        addCommand(cmdRfsh); cmdRfsh.setImg(0x10);
        addCommand(cmdSrv); cmdSrv.setImg(0x55);
        addCommand(cmdFeatures); cmdFeatures.setImg(0x56);
        //addCommand(cmdAdd);
//#ifndef GRAPHICS_MENU        
     addCommand(cmdCancel);
//#endif     

        items=new Vector(0);
        features=new Vector(0);
        
        this.node=node;
        
        if (service!=null && search) {
            this.service=service;
            requestQuery(NS_SRCH, "discosrch");
        } else if (service!=null) {
            this.service=service;
            requestQuery(NS_INFO, "disco");
        } else {
            this.service=null;
            
            Object add;
            
            add = new DiscoCommand(MenuIcons.ICON_VCARD , SR.get(SR.MS_VCARD), true);  items.addElement(add);//0
            add = new DiscoCommand(MenuIcons.ICON_CONFERENCE , SR.get(SR.MS_CONFERENCE) , true);  items.addElement(add);//1
            add = new DiscoCommand(MenuIcons.ICON_ADD_CONTACT , SR.get(SR.MS_ADD_CONTACT) , true);  items.addElement(add);//2
            add = new DiscoCommand(MenuIcons.ICON_USER_SEARCH , SR.get(SR.MS_USERS_SEARCH) , true);  items.addElement(add);//3
            add = new DiscoCommand(MenuIcons.ICON_PRIVACY , SR.get(SR.MS_PRIVACY_LISTS), true);  items.addElement(add);//5
            if (midlet.BombusQD.cf.fileTransfer) {
                add = new DiscoCommand(MenuIcons.ICON_FT , SR.get(SR.MS_FILE_TRANSFERS), true);  items.addElement(add);//6
            }
            
            add = new DiscoCommand(MenuIcons.ICON_MOOD , SR.get(SR.MS_USERMOOD), true);  items.addElement(add);//7
            add = new DiscoCommand(MenuIcons.ICON_USER_ACTIVITY , SR.get(SR.MS_ACTIVITY), true);  items.addElement(add);//8
            if (midlet.BombusQD.sd.account.isGmail()) {
                add = new DiscoCommand(MenuIcons.ICON_GMAIL , SR.get(SR.MS_CHECK_GOOGLE_MAIL), true);  items.addElement(add);//9
            }
            
            
            String myServer=sd.account.getServer();
            add = new DiscoCommand(0x00, "My Disco:"); items.addElement(add);
            boolean found = false;

            try {
                DataInputStream is=NvStorage.ReadFileRecord("disco", 0);
           
                try {
                    while (true) {
                        String recent=is.readUTF();
                        if (myServer.equals(recent)) continue; //only one instance for our service
                        add = new DiscoContact(null, recent, 0, 16); items.addElement(add);
                        found = true;
                    }
                } catch (EOFException e) { 
                    is.close();
                    is = null;
                }
            } catch (Exception e) {}
            
            if(!found) items.removeElement(add);

            int sizeTransports = 0;            
            
            add = new DiscoCommand(0x15,"My Server:"); items.addElement(add);
            add = new DiscoContact(null, myServer, 0, 16);  items.addElement(add);
            
            
            add = new DiscoCommand(0x01,"My ICQ:"); items.addElement(add);
            
            sizeTransports = ICQ_transports.length;
            for(byte i = 0; i < sizeTransports; ++i){
                add = new DiscoContact(null, ICQ_transports[i], 0, 16);
                items.addElement(add);
            }
                  
            add = new DiscoCommand(0x46,"My MRIM:");
            
            items.addElement(add);
            sizeTransports = MRIM_transports.length;
            for(byte i = 0; i < sizeTransports; ++i) {
                add = new DiscoContact(null, MRIM_transports[i], 0, 16);
                items.addElement(add);
            }
               
               
            add = new DiscoCommand(0x40,"My Vkontakte:"); items.addElement(add);
            
            sizeTransports = VKONTAKTE_transports.length;
            for(byte i = 0; i < sizeTransports; ++i) {
                add = new DiscoContact(null, VKONTAKTE_transports[i], 0, 16);
                items.addElement(add);
            }

            //sort(items);
            discoIcon=0; 
            mainbarUpdate(); 
            moveCursorHome();
            redraw();
        }
    }
    
    private String discoId(String id) {
        return id+service.hashCode();
    }
    
    public int getItemCount(){ return items.size();}
    public VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index);}
    
    protected void beginPaint(){ getMainBarItem().setElementAt(sd.roster.getEventIcon(), 4); }
    
    
    private void mainbarUpdate(){
        getMainBarItem().setElementAt(new Integer(discoIcon), 0);
        if(null == service && null == node) {
          getMainBarItem().setElementAt( SR.get(SR.MS_MY_JABBER) , 2);
        } else {
          getMainBarItem().setElementAt((service==null)?SR.get(SR.MS_RECENT):service, 2);
        }
        getMainBarItem().setElementAt(sd.roster.getEventIcon(), 4);
	
	int size=0;
	try { size=items.size(); } catch (Exception e) {}
	String count=null;
        
	removeCommand(cmdOk);
        
	if (size>0) {
//#ifdef MENU_LISTENER
	    menuCommands.insertElementAt(cmdOk, 0); 
            cmdOk.setImg(0x43);
//#else
//#             addCommand(cmdOk);
//#endif
	    count=" ("+size+") ";
            if(nextElements.size() > 50) {
               size = nextElements.size();
               count = " ("+size+") " + "[" + window + "/" + size/50 + "] ";
            }
	}
        getMainBarItem().setElementAt(count,1);
    }
    
    private void requestQuery(String namespace, String id){
        discoIcon=RosterIcons.ICON_PROGRESS_INDEX; 
        mainbarUpdate(); 
        redraw();
        
        JabberDataBlock req=new Iq(service, Iq.TYPE_GET, discoId(id));
        JabberDataBlock qry=req.addChildNs("query", namespace);
        qry.setAttribute("node", node);

        //stream.addBlockListener(this);
        //System.out.println(">> "+req.toString());
        stream.send(req);
    }
    
    private void requestCommand(String namespace, String id){
        discoIcon=RosterIcons.ICON_PROGRESS_INDEX; 
        mainbarUpdate(); 
        redraw();
        
        JabberDataBlock req=new Iq(service, Iq.TYPE_SET, id);
        JabberDataBlock qry=req.addChildNs("command", namespace);
        qry.setAttribute("node", node);
        qry.setAttribute("action", "execute");

        //stream.addBlockListener(this);
        //System.out.println(req.toString());
        stream.send(req);
    }
    
    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return JabberBlockListener.BLOCK_REJECTED;
        String id=data.getAttribute("id");
        if(null == id) return JabberBlockListener.BLOCK_REJECTED;
        
        if (!id.startsWith("disco")) return JabberBlockListener.BLOCK_REJECTED;
        
        String typeAttr;
        typeAttr = data.getTypeAttribute();
        if(null != typeAttr) {
          if (typeAttr.equals("error")) {
            discoIcon=RosterIcons.ICON_ERROR_INDEX;
            mainbarUpdate();
            //redraw();
            
            XmppError xe=XmppError.findInStanza(data);
            
            new AlertBox(data.getAttribute("from"), xe.toString(), display, this, false) {
                public void yes() { };
                public void no() { };
            };

            return JabberBlockListener.BLOCK_PROCESSED;
          }
        }
        JabberDataBlock command1=data.getChildBlock("query");
        JabberDataBlock command2=data.getChildBlock("command");
        if (command1==null) {
            if (command2!=null) {
                command1=command2;
            }
            String node = command1.getAttribute("node");
            if(node != null){
               if (node.startsWith("http://jabber.org/protocol/rc#")) id="discocmd"; //hack
            }
        }
        JabberDataBlock query=data.getChildBlock((id.equals("discocmd"))?"command":"query");
        Vector childs=query.getChildBlocks();


        if (id.equals(discoId("disco2"))) {
            Vector items=new Vector(0);
            nextElements.removeAllElements();
            if (null != childs) {
              JabberDataBlock item;
              int size = childs.size();
              String name = "";
              String jid = "";
              String node = "";
              Object serv = null;
              StringBuffer nextElement = new StringBuffer(0);
              if(size > 50) {
                 addCommand(cmdShowNextItems); cmdShowNextItems.setImg(0x10);
                 addCommand(cmdShowPrevItems); cmdShowPrevItems.setImg(0x10);
              }

              for(int y = 0; y < size; ++y) {
                item = (JabberDataBlock)childs.elementAt(y);
                if (item.getTagName().equals("item")){
                    name = item.getAttribute("name");
                    jid = item.getAttribute("jid");
                    node = item.getAttribute("node");
                    if (node==null) {
                        int resourcePos=jid.indexOf('/');
                        if (resourcePos>-1) jid = jid.substring(0, resourcePos);
                        nextElement.setLength(0);
                        nextElement.append(name==null ? "null":name)
                                      .append("%JID%")
                                      .append(jid==null ? "null":jid);
                        nextElements.addElement(nextElement.toString());
                    } else {
                        serv=new Node(name, node);
                    }
                    if(null != serv && y <= 50) items.addElement(serv);
                }
              }
              if(null != name) name = null;
              if(null != jid) jid = null;
              if(null != node) node = null;
            }
            childs = null;
            childs = new Vector(0);
            sortElements(nextElements);
            setElements();
            //showResults(items);
            
        }  else if (id.equals(discoId("disco"))) {
            Vector cmds=new Vector(0);
            boolean showPartialResults=false;
            boolean loadItems=true;
            boolean client=false;
            if (childs!=null) {
                JabberDataBlock identity=query.getChildBlock("identity");
                if (identity!=null) {
                    String category=identity.getAttribute("category");
                    typeAttr = identity.getTypeAttribute();
                    if(null == typeAttr) typeAttr = "-";
                    if (category.equals("automation") && typeAttr.equals("command-node"))  {
                        //cmds.addElement(new DiscoCommand(RosterIcons.ICON_AD_HOC, strCmds));
                        requestCommand(NODE_CMDS, "discocmd");
                    }
                    if (category.equals("conference")) {
                        cmds.addElement(new DiscoCommand(RosterIcons.ICON_GCJOIN_INDEX, SR.get(SR.MS_JOIN_CONFERENCE)));
                        if (service.indexOf('@')<=0) {
                            loadItems=false;
                            showPartialResults=true;
                            cmds.addElement(new DiscoCommand(RosterIcons.ICON_ROOMLIST, SR.get(SR.MS_LOAD_ROOMLIST)));
                        }
                    }
                 }
               for (Enumeration e=childs.elements(); e.hasMoreElements();) {
                    JabberDataBlock i=(JabberDataBlock)e.nextElement();
                    if (i.getTagName().equals("feature")) {
                        String var=i.getAttribute("var");
                        features.addElement(var);
                        //if (var.equals(NS_MUC)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_GCJOIN_INDEX, strJoin)); }
                        if (var.equals(NS_SRCH)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_SEARCH_INDEX, SR.get(SR.MS_SEARCH))); }
                        if (var.equals(NS_REGS)) { cmds.addElement(new DiscoCommand(RosterIcons.ICON_REGISTER_INDEX, SR.get(SR.MS_REGISTER))); }
                        if (var.equals(NS_GATE)) { showPartialResults=true; }
                        //if (var.equals(NODE_CMDS)) { cmds.addElement(new DiscoCommand(AD_HOC_INDEX,strCmds)); }
                    }
                }
             }
            /*if (data.getAttribute("from").equals(service)) */ { //FIXME!!!
                this.cmds=cmds;
            //System.out.println("cmds: "+items.toString());
                if (loadItems) requestQuery(NS_ITEMS, "disco2");
                if (showPartialResults) showResults(new Vector(0));
            }
        } else if (id.startsWith ("discoreg")) {
            discoIcon=0;
            new DiscoForm(display, data, stream, "discoResult", "query");
            
        } else if (id.startsWith("discocmd")) {
            discoIcon=0;
            new DiscoForm(display, data, stream, "discocmd", "command");
        } else if (id.startsWith("discosrch")) {
            discoIcon=0;
            new DiscoForm(display, data, stream, "discoRSearch", "query");
        } else if (id.startsWith("discoR")) {
            String text=SR.get(SR.MS_DONE);
            if(null == typeAttr) typeAttr = "-";
            if (typeAttr.equals("error")) text=XmppError.findInStanza(data).toString();
            if (text.equals(SR.get(SR.MS_DONE)) && id.endsWith("Search") ) {
                new SearchResult(display, data);
            } else {
                new AlertBox(typeAttr, text, display, null, false) {
                    public void yes() { }
                    public void no() { }
                };
            }
        }
        redraw();
        return JabberBlockListener.BLOCK_PROCESSED;
    }
    
    
    public void eventOk(){
        super.eventOk();
        Object o= getFocusedObject();
        if (o!=null) 
        if (o instanceof IconTextElement) {
            String element = ((IconTextElement)o).getTipString();
            if(null == element) {
                browse( service, ((Node) o).getNode() );
               return;
            }
            browse( element, null );
            element = null;
        }
    }
	

    private void showResults(final Vector items) {
        try {
            sort(items);
        } catch (Exception e) { 
            //e.printStackTrace(); 
        };
            Object obj;
            for (Enumeration e=cmds.elements(); e.hasMoreElements();) {
                obj = e.nextElement();
                if (!items.contains(obj)) items.insertElementAt(obj,0);
            }
            this.items=items;
            moveCursorHome();
            discoIcon=0; 
            mainbarUpdate(); 
    }
    
    public void browse(String service, String node){
            State st=new State();
            st.cursor=cursor;
            st.items=items;
            st.service=this.service;
            st.node=this.node;
            st.features=features;
            stackItems.addElement(st);
            
            items=new Vector(0);
            features=new Vector(0);
//#ifndef GRAPHICS_MENU    
        removeCommand(cmdBack);
        addCommand(cmdBack);
//#endif             
            this.service=service;
            this.node=node;
            requestQuery(NS_INFO,"disco");
    }
    
    private void sortElements(Vector sort) {
        /*
        int tempSize = sort.size();
        int maxParticipantCount = 0;
        int currentCount = 0;
        int pos = -1;
        StringBuffer sb = new StringBuffer(0);
        String element,result;
        for(int i = 0; i < tempSize; ++i) {
            element = (String)sort.elementAt(i);
            pos = element.indexOf("%JID%");
            String name = element.substring(0, pos); //roomname (count)
            if(-1 != name.indexOf("null")) continue;
            int length = name.length();
            
            sb.setLength(0);
            int k = name.lastIndexOf('(') + 1;
            boolean found = true;
            while(k!=length) {
               char c = name.charAt(k);
               switch (c) {
                   case ')':
                       found = false;
                       break;
               }
               if(found) sb.append(c);
               k++; 
            }
            result = sb.toString();
            if(-1 == result.indexOf("n/a")) {
              currentCount = Integer.parseInt(result);
              if(currentCount > maxParticipantCount) {
                 maxParticipantCount = currentCount;
              } 
            }
        }
         */
    }
    
    private void setElements() {
           String element;
           Object serv;
           int pos = 0;
           int start = window*50;
           int end = start + 50;
           if(window > 0) items.removeAllElements();

           midlet.BombusQD.sd.roster.systemGC();
           for(int i = start; i < end; ++i) {
              try {
                element = (String)nextElements.elementAt(i);
                pos = element.indexOf("%JID%");
                String name = element.substring(0,pos);
                String jid = element.substring(pos + 5,element.length());
                
                if(-1 != name.indexOf("null")) name = null;
                if(-1 != jid.indexOf("null")) jid = null;
                
                serv = new DiscoContact( name , jid  , 0 , 4);
                items.addElement(serv);
              } catch (Exception e) {}
           }
           showResults(items);
    }
    
    public void commandAction(Command c, Displayable d){
	if (c==cmdOk) eventOk();
        if (c==cmdBack) { exitDiscovery(false); }            
        if (c==cmdRfsh) { if (service!=null) requestQuery(NS_INFO, "disco"); }
        if (c==cmdSrv) { new ServerBox(display, this, service, this); }
        if (c==cmdFeatures) { new DiscoFeatures(display, service, features); }
        if (c==cmdShowPrevItems) {
           window--;
           if(window < 0) window = nextElements.size()/50;
           setElements();
        }
        if (c==cmdShowNextItems) {
           window++;
           int maxSize = nextElements.size()/50;
           if(window > maxSize) window = 0;
           setElements();
        }
        if (c==cmdCancel) exitDiscovery(true);
        if (c==cmdShowStatistics) {
            Object o=getFocusedObject();
            JabberDataBlock req=new Iq(o.toString(), Iq.TYPE_GET,"getst");
            JabberDataBlock query = req.addChildNs("query","http://jabber.org/protocol/stats");
            StaticData.getInstance().roster.theStream.send(req);
            req=null;
            query=null;
        }         
    }
    
    private void exitDiscovery(boolean cancel){
        if(nextElements.size() > 0) nextElements.removeAllElements();
        if (cancel || stackItems.isEmpty()) {
            stream.cancelBlockListener(this);
            if (display!=null && parentView!=null /*prevents potential app hiding*/ ) {
                display.setCurrent(parentView);
            }
        } else {
            State st=(State)stackItems.lastElement();
            stackItems.removeElement(st);
            
            service=st.service;
            items=st.items;
            features=st.features;
            discoIcon=0;
            
            mainbarUpdate();
            moveCursorTo(st.cursor);
            redraw();
        }
    }
    
    
    public void destroyView()	{
           exitDiscovery(false);             
    }

    
    private static String bareJid = "";
    private String getTransport(){
        try {
            int beginIndex = bareJid.indexOf('@')+1;
            int endIndex = bareJid.indexOf('.',beginIndex);
            return bareJid.substring(beginIndex, endIndex);
        } catch (Exception e) {
            return "-";
        }
    }
    
    private int getTransportStatus(String jid) {
        int resourcePos = jid.indexOf('/');
        if (resourcePos<0) resourcePos = jid.length();
        bareJid = jid.substring(0,resourcePos).toLowerCase(); //Strconv.toLowerCase( s.substring(0,resourcePos) );???
        return RosterIcons.getInstance().getTransportIndex(getTransport());
    }
     
    class DiscoContact extends IconTextElement {
      private String nickname;
      private String discoJid;
      private int offs; //offset
      private int status;
    
      public DiscoContact(String nick, String sJid, int status, int offs) { 
        super(RosterIcons.getInstance());
        this.nickname = (nick==null) ? null : nick.trim();
        this.discoJid = sJid;
        this.offs = offs;
        if(null != sJid) this.status = getTransportStatus(sJid);
        if(-1 == status) this.status = -1;
      }

      public int getOffset() { return offs; }
      public boolean getFontIndex() { return true; } //change font
      public int getImageIndex() { return status; };
      public String toString() { return (nickname==null)?discoJid:nickname; }
      public String getTipString() { return discoJid; }
    }
    
    
    private class DiscoCommand extends IconTextElement {
        String name;
        int index;
        int icon;
        boolean userCommands;
        boolean lock;

        public DiscoCommand(int icon, String name, boolean value) {
            super(MenuIcons.getInstance());
            this.icon=icon;
            this.name=name;
            this.userCommands=value;
        }
        
        public DiscoCommand(int icon, String name) {
            super(RosterIcons.getInstance());
            this.lock = name.startsWith("My ");
            this.icon=icon;
            this.name=name;
        }
        public int getColor(){ return ColorTheme.getInstance().getColor(ColorTheme.DISCO_CMD); }
        public int getImageIndex() { return icon; }
        public String toString(){ return name; }
        public void onSelect(VirtualList view) {
            if(lock) return;
            if(userCommands) {
                switch (icon) {
                    case MenuIcons.ICON_VCARD:
                        Contact cs=midlet.BombusQD.sd.roster.selfContact();
                        if (cs.vcard!=null) {
                          new VCardEdit(display, parentView, cs.vcard);
                          return;
                        }
                        VCard.request(cs.bareJid, cs.getJid());
                        break;
                    case MenuIcons.ICON_CONFERENCE:
                        midlet.BombusQD.sd.roster.cmdConference();
                        break;
                    case MenuIcons.ICON_ADD_CONTACT:
                        midlet.BombusQD.sd.roster.cmdAdd();
                        break;
                    case MenuIcons.ICON_USER_SEARCH:
                        new DiscoSearchForm(display, parentView , null , -1);
                        break;
                    case MenuIcons.ICON_PRIVACY:
                        new PrivacyLists.PrivacySelect(display, parentView);
                        break;
                    case MenuIcons.ICON_FT:
                        new io.file.transfer.TransferManager(display);
                        break;
                    case MenuIcons.ICON_MOOD:
                        midlet.BombusQD.sd.roster.selectPEP.show(parentView, true);
                        break;
                    case MenuIcons.ICON_USER_ACTIVITY:
                        midlet.BombusQD.sd.roster.selectPEP.show(parentView, false);
                        break; 
                    case MenuIcons.ICON_GMAIL:
                        midlet.BombusQD.sd.roster.theStream.send(xmpp.extensions.IqGmail.query());
                        break;
                }
                return;
            }
            switch (icon) {
//#ifndef WMUC
                case RosterIcons.ICON_GCJOIN_INDEX: {
                    int rp=service.indexOf('@');
                    String room=null;
                    String server=service;
                    if (rp>0) {
                        room=service.substring(0,rp);
                        server=service.substring(rp+1);
                    }
                    new ConferenceForm(display, parentView, room, service, null, false);
                    break;
                }
//#endif
                case RosterIcons.ICON_SEARCH_INDEX:
                    requestQuery(NS_SRCH, "discosrch");
                    break;
                case RosterIcons.ICON_REGISTER_INDEX:
                    requestQuery(NS_REGS, "discoreg");
                    break;
                case RosterIcons.ICON_ROOMLIST:
                    requestQuery(NS_ITEMS, "disco2");
                    break;
                case RosterIcons.ICON_AD_HOC:
                    requestCommand(NODE_CMDS, "discocmd");
                default:
            }
        }
    }
    

//#ifdef MENU_LISTENER
/*    
//#ifdef GRAPHICS_MENU        
//#     public void touchRightPressed(){ if (Config.getInstance().oldSE) showGraphicsMenu(); else destroyView(); }    
//#     public void touchLeftPressed(){ if (Config.getInstance().oldSE) destroyView(); else showGraphicsMenu(); }
//#else
    public void touchRightPressed(){ if (cf.oldSE) showMenu(); else destroyView(); }    
    public void touchLeftPressed(){ if (cf.oldSE) keyGreen(); else showMenu(); }
//#endif      
 */

//#endif    
    
    
    private void exitDiscovery(){
        stream.cancelBlockListener(this);
        destroyView();
    }

//#ifdef MENU_LISTENER

    
//#ifdef GRAPHICS_MENU        
//#     public int showGraphicsMenu() {
//#         //commandState();
//#         new GMenu(display, parentView, this, null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.SERVICE_DISCOVERY; 
//#         redraw();
//#         return GMenu.SERVICE_DISCOVERY;
//#     }
//#else
    public void showMenu() {
        new MyMenu(display, parentView, this, SR.get(SR.MS_DISCO), null, menuCommands);
    }   
//#endif

    public String touchLeftCommand(){ return (Config.getInstance().oldSE)?SR.get(SR.MS_BACK):SR.get(SR.MS_MENU); }
    public String touchRightCommand(){ return (Config.getInstance().oldSE)?SR.get(SR.MS_MENU):SR.get(SR.MS_BACK); }    
    
//#endif

}
class State{
    public String service;
    public String node;
    public Vector items;
    public Vector features;
    public int cursor;
}
//#endif
