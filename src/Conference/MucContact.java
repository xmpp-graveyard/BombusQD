/*
 * MucContact.java
 *
 * Created on 2.05.2006, 14:05
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

package Conference;

import Client.Contact;
import Client.Roster;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;
import images.RosterIcons;
import Client.Constants;
import locale.SR;
import Client.Msg;
import util.StringUtils;
import xmpp.XmppError;
import ui.VirtualList;

/**
 *
 * @author root(linux detected!),aqent
 */
public class MucContact extends Contact {
    
    public String realJid;
    
    public String affiliation;
    public String role;
    
    public byte roleCode;
    public byte affiliationCode;
    
    public boolean commonPresence=true;
    
    public long lastMessageTime;
    
    /** Creates a new instance of MucContact */
    public MucContact(String nick, String jid) {
        super(nick, jid, Presence.PRESENCE_OFFLINE, "muc");
        offline_type=Presence.PRESENCE_OFFLINE;
    }
    
    public void destroy(){
       super.destroy();
        if(null != realJid) realJid = null;
        if(null != affiliation) affiliation = null;
        if(null != role) role = null;
        if(b.length()>0) b.setLength(0);
        if(tip.length()>0) tip.setLength(0);
    }
    
    private static StringBuffer b = new StringBuffer(0);//FIX
    
    public static String getAffiliationLocale(int aff) {
        switch (aff) {
            case Constants.AFFILIATION_NONE: return SR.MS_AFFILIATION_NONE;
            case Constants.AFFILIATION_MEMBER: return SR.MS_AFFILIATION_MEMBER;
            case Constants.AFFILIATION_ADMIN: return SR.MS_AFFILIATION_ADMIN;
            case Constants.AFFILIATION_OWNER: return SR.MS_AFFILIATION_OWNER;
        }
        return null;
    }
    
    public String processPresence(JabberDataBlock xmuc, Presence presence) {
        String from=jid.getJid();
       
        byte presenceType=presence.getTypeIndex();
        
        if (presenceType==Presence.PRESENCE_ERROR) return StringUtils.processError(presence, presenceType, (ConferenceGroup) group, this);
        
        JabberDataBlock item=xmuc.getChildBlock("item");   

        String tempRole=item.getAttribute("role");
        if (tempRole.equals("visitor")) roleCode=Constants.ROLE_VISITOR;
   else if (tempRole.equals("participant")) roleCode=Constants.ROLE_PARTICIPANT;
   else if (tempRole.equals("moderator")) roleCode=Constants.ROLE_MODERATOR;
        
        String tempAffiliation=item.getAttribute("affiliation");
        if (tempAffiliation.equals("owner")) affiliationCode=Constants.AFFILIATION_OWNER;
   else if (tempAffiliation.equals("admin")) affiliationCode=Constants.AFFILIATION_ADMIN;
   else if (tempAffiliation.equals("member")) affiliationCode=Constants.AFFILIATION_MEMBER;
   else if (tempAffiliation.equals("none")) affiliationCode=Constants.AFFILIATION_NONE;
        
        boolean roleChanged= !tempRole.equals(role);
        boolean affiliationChanged=!tempAffiliation.equals(affiliation);
        role=tempRole;
        affiliation=tempAffiliation;
        
        tempRole=null;
        tempAffiliation=null;

        setSortKey(getNick());
        switch (roleCode) {
            case Constants.ROLE_MODERATOR:
                transport=RosterIcons.ICON_MODERATOR_INDEX;
                key0=Constants.GROUP_MODERATOR;
                break;
            case Constants.ROLE_VISITOR:
                transport=RosterIcons.getInstance().getTransportIndex("vis");
                key0=Constants.GROUP_VISITOR;
                break;
            default:
                transport=affiliation.equals("member")? 0 :RosterIcons.getInstance().getTransportIndex("vis");
                key0=(affiliation.equals("member")?Constants.GROUP_MEMBER:Constants.GROUP_PARTICIPANT);
        }
        
        JabberDataBlock statusBlock=xmuc.getChildBlock("status");
        int statusCode = 0;
        try { 
            statusCode=Integer.parseInt( statusBlock.getAttribute("code") ); 
        } catch (Exception e) { }
        
        b.setLength(0);
        b.append(getNick().trim());
        
        String statusText=presence.getChildBlockText("status");
        String tempRealJid=item.getAttribute("jid");

 //#if ZLIB        
        try{       
             JabberDataBlock destroy=xmuc.getChildBlock("destroy");
             if(destroy!=null){
                 if(destroy.getChildBlockText("reason")!=null)
                  {
                        VirtualList.setWobble(1,null,"Groupchat " + destroy.getAttribute("jid")
                         + " was destroyed!(reason: "+destroy.getChildBlockText("reason")+")"); 

                   }
             }
        }catch (Exception e) { /* not muc#user case*/ }    
//#endif          
        
        if (statusCode==201) {
            //todo: fix this nasty hack, it will not work if multiple status codes are nested in presence)
            b.setLength(0);
            b.append(SR.MS_NEW_ROOM_CREATED);
        } else if (presenceType==Presence.PRESENCE_OFFLINE) {
            key0=3;
            String reason=item.getChildBlockText("reason");
            switch (statusCode) {
                case 303:
                    b.append(SR.MS_IS_NOW_KNOWN_AS);
                    String chNick=item.getAttribute("nick");
                    b.append(chNick.trim());
                    String newJid=from.substring(0, from.indexOf('/')+1)+chNick;
                    jid.setJid(newJid);
                    bareJid=newJid;
                    from=newJid;
                    setNick(chNick);
                    break;
                case 301: //ban
                    presenceType=Presence.PRESENCE_ERROR;
                case 307: //kick
                    b.append((statusCode==301)? SR.MS_WAS_BANNED : SR.MS_WAS_KICKED );
//#ifdef POPUPS
                    if (((ConferenceGroup)group).selfContact == this ) {
                        midlet.BombusQD.sd.roster.setWobble(3, null, ((statusCode==301)? SR.MS_WAS_BANNED : SR.MS_WAS_KICKED)+((!reason.equals(""))?"\n"+reason:""));
                    }
//#endif
                    if (!reason.equals(""))
                        b.append("(").append(reason).append(")");

                    testMeOffline(true);
                    break;
                case 321:
                case 322:
                    b.append((statusCode==321)?SR.MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM:SR.MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY);
                    testMeOffline(true);
                    break;
                default:
                    if (tempRealJid!=null)
                        b.append(" (").append(tempRealJid).append(")");

                    b.append(SR.MS_HAS_LEFT_CHANNEL);
                    
                    if (statusText.length()>0)
                        b.append(" (").append(statusText).append(")");

                    testMeOffline(false);
            } 
        } else {
            if (this.status==Presence.PRESENCE_OFFLINE) {
                if (tempRealJid!=null) {
                    realJid=tempRealJid;  //for moderating purposes
                    b.append(" (").append(tempRealJid).append(')');
                }
                
                b.append(SR.MS_HAS_JOINED_THE_CHANNEL_AS);

                if (affiliationCode!=Constants.AFFILIATION_MEMBER) b.append(getRoleLocale(roleCode));

                 if (!affiliation.equals("none")) {
                    if (roleCode!=Constants.ROLE_PARTICIPANT) b.append(SR.MS_AND);
  
                    b.append(getAffiliationLocale(affiliationCode));
                }
                
                if (statusText.length()>0) b.append(" (").append(statusText).append(")");
            } else {
                b.append(SR.MS_IS_NOW);
                
                if (roleChanged) b.append(getRoleLocale(roleCode));
                
                if(role.equals("visitor")) {
                      if(item.getChildBlockText("reason")!=null){
                      b.append("("+item.getChildBlockText("reason")+")");
                      }
                 }
                
                 if (affiliationChanged) {
                    if (roleChanged) b.append(SR.MS_AND);
                    b.append(getAffiliationLocale(affiliationCode));
                }
                if (!roleChanged && !affiliationChanged) b.append(presence.getText());
            }
        }
        
        from=null;
        item=null;
        
        statusText=null;
        tempRealJid=null;
        
        setStatus(presenceType);
        return b.toString();
    }


    public static String getRoleLocale(int rol) {
        switch (rol) {
            case Constants.ROLE_VISITOR: return SR.MS_ROLE_VISITOR;
            case Constants.ROLE_PARTICIPANT: return SR.MS_ROLE_PARTICIPANT;
            case Constants.ROLE_MODERATOR: return SR.MS_ROLE_MODERATOR;
        }
        return null;
    }

    private static StringBuffer tip = new StringBuffer(0);
    
    public String getTipString() {
        tip.setLength(0);
        int nm=getNewMsgsCount();
        
        if (nm!=0) tip.append(nm);
        
        if (realJid!=null) {
            if (tip.length()!=0)  tip.append(' ');
            tip.append(realJid);
        }
        
        return (tip.length()==0)? null:tip.toString();
    }

    public void testMeOffline(boolean isKick){
         ConferenceGroup gr=(ConferenceGroup)group;
         if ( gr.selfContact == this ) {
            if(isKick) midlet.BombusQD.sd.roster.showRoster();
            midlet.BombusQD.sd.roster.roomOffline(gr, true);
         }
    }

    public void addMessage(Msg m) {
        super.addMessage(m);
        switch (m.messageType) {
            case Constants.MESSAGE_TYPE_IN:
            case Constants.MESSAGE_TYPE_OUT:
            case Constants.MESSAGE_TYPE_HISTORY: break;
            default: return;
        }
        lastMessageTime=m.dateGmt;
    }
}
