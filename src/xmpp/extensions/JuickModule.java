//#ifdef JUICK.COM
//# /*
//#  * JuickModule.java
//#  *
//#  * Created on 24 Сентябрь 2009 г., 22:10
//#  *
//#  * To change this template, choose Tools | Template Manager
//#  * and open the template in the editor.
//#  */
//# 
//# package xmpp.extensions;
//# 
//# import Client.Msg;
//# import com.alsutton.jabber.JabberDataBlock;
//# import com.alsutton.jabber.datablocks.Message;
//# import com.alsutton.jabber.datablocks.Iq;
//# import java.util.Vector;
//# import Client.Contact;
//# 
//# /**
//#  *
//#  * @author aqent
//#  */
//# public class JuickModule{
//#     
//#     /** Creates a new instance of JuickListener */
//#     
//#     public static JuickModule listener;
//#     private static Contact juick = null;
//#     private static String botname = "juick@juick.com/Juick";
//#     
//#     public JuickModule() { }
//#     
//#     public static JuickModule jm() {
//#         if (listener==null) { listener=new JuickModule(); }
//#         return listener;
//#     }    
//# 
//#  /*
//#   \nLast messages:
//#   \n@USER1: \n*this is 1 text message
//#   \n#NUM1 (if replies) URL
//#   \n
//#   \n@USER2: \n*this is 2 text message
//#   \n#NUM2 (if replies) URL
//#   \n
//#   */     
//#     
//#     private void storeMessage(Msg msg){
//#       if(juick==null && midlet.BombusQD.sd.roster.isLoggedIn())
//#           juick = midlet.BombusQD.sd.roster.getContact(botname,false);
//#       midlet.BombusQD.sd.roster.messageStore(juick,msg);
//#     }
//#     
//#     
//#     private boolean separateMsgs(Msg message){
//#        String body = message.body.concat("\n");
//#        StringBuffer buf = new StringBuffer(0);
//#        StringBuffer id = new StringBuffer(0);
//#        
//#         int len = body.length();
//#         int i = 0;
//#         int count = 0;
//#         boolean lastPopularMessages = body.startsWith("Last popular messages:");
//#         boolean lastMessages = body.startsWith("Last messages:");
//#  
//#         if( lastMessages || lastPopularMessages ) {
//#           message = new Msg(Msg.MESSAGE_TYPE_JUICK, juick.bareJid , null ,"[Last" + (lastMessages?" ":" popular ") + "messages]" );
//#           storeMessage(message);
//#           boolean parse = false;
//#           for (i = 0; i < len; i++) {
//#               char c = body.charAt(i);
//#               if(c=='\n'){
//#                  c = body.charAt(i+1);
//#                  
//#                 if (c == '@'){  //@NICK: tagslist \n
//#                     count++;
//#                     buf.setLength(0);
//#                     buf.append(count);
//#                     buf.append(").");
//#                     while(c!='\n'){ i++;  c = body.charAt(i); buf.append(c); } //include tags
//#                     parse = true;
//#                 }
//#                 else if (c=='#'){ //get num post and send to msg.id
//#                    buf.append('\n');
//#                    int j = i;
//#                    while(parse) { j++; c = body.charAt(j);
//#                      if(body.charAt(j)==' '){
//#                        buf.append(' ');
//#                        c = body.charAt(j+1);
//#                        if(c=='('){
//#                          j=j-1;
//#                          while(c!=')') { j++;  c = body.charAt(j);  buf.append(c); } //include replies
//#                        }
//#                        message = new Msg(Msg.MESSAGE_TYPE_JUICK, botname , null , buf.toString() );
//#                        message.id = id.append(' ').toString();
//#                        storeMessage(message);
//#                          buf.setLength(0);
//#                          id.setLength(0);
//#                          parse = false;
//#                      } else {
//#                        buf.append(c); id.append(c);
//#                      }
//#                    }
//#                 }
//#               } else buf.append(c);
//#            }
//#          body = null; return true;
//#       }
//#       return false;
//#       //speed 10 messages - 14 msec
//#     }
//#     
//#     
//#     public Msg getMsg(Msg m,JabberDataBlock data) { //Need fixes
//#         if( data instanceof Iq ) {
//#             String id=(String) data.getAttribute("id"); 
//#             String type = (String) data.getTypeAttribute();
//#             
//#             if (id!=null) {
//#                 if ( type.equals( "result" ) ) {
//#                     if(id.startsWith("cmts_")) {
//#                         //send query comments
//#                         String body = id.substring(5,id.length());
//#                         
//#                           JabberDataBlock comments = new Iq("juick@juick.com/Juick", Iq.TYPE_GET,"qd_comments");
//#                           JabberDataBlock comments_query = comments.addChildNs("query","http://juick.com/query#messages");
//#                           comments_query.setAttribute("mid",body);
//#                           comments_query.setAttribute("rid","*");
//#                           midlet.BombusQD.debug.add(comments.toString(),10);
//#                           if(body.length()>0) midlet.BombusQD.sd.roster.theStream.send(comments);
//#                     }
//#                     
//#      /*
//#       *QUERY to:
//#        <iq to="juick@juick.com/Juick" id="id222" type="get">
//#             <query xmlns="http://juick.com/query#messages" mid="xxx" rid="*">
//#        </iq>
//#       *
//#       *from:
//#        <iq type="result" id="id222">
//#             <query xmlns="http://juick.com/query#messages">
//#                <juick xmlns="http://juick.com/message" mid="234235" rid="22" uname="username" replies=""><body>hello world!</body></juick>
//#                <juick xmlns="http://juick.com/message" mid="234235" rid="22" uname="username" replies=""><body>hello world!</body></juick>
//#                ....
//#             </query>
//#        </iq>
//#       *
//#       *
//#       */                    
//#                     JabberDataBlock query = data.findNamespace("query","http://juick.com/query#messages");
//#                     if(query!=null) { //use <nick> for highlite tags
//#                         
//#                       JabberDataBlock child,tag = null;
//#                       Vector childBlocks,childTags = new Vector(0);
//#                       StringBuffer buf = new StringBuffer(0);
//#                       String uname,mid,rid,replies,body = "";
//#                       
//#                       childBlocks = query.getChildBlocks();
//#                       int size=childBlocks.size();
//# 
//#                         for(int i=0;i<size;i++){  
//#                            child = (JabberDataBlock)childBlocks.elementAt(i);
//#                            childTags = child.getChildBlocks();  
//# 
//#                              uname = child.getAttribute("uname");
//#                              replies = child.getAttribute("replies");
//#                              mid = child.getAttribute("mid");
//#                              rid = child.getAttribute("rid");
//#                              
//#                            if (uname!=null) buf.append("<nick>@").append(uname).append("</nick>: ");
//#                            if (replies!=null) buf.append("\nReplies:").append(replies);      
//#  
//#                            int tagSize = childTags.size();
//#                            if(tagSize>0){
//#                            for(int k=0;k<tagSize;k++){
//#                               //specially for "#+" type
//#                               tag = (JabberDataBlock)childTags.elementAt(k);
//#                                if (tag.getTagName().equals("tag")) buf.append("\n <nick>*"+tag.getText()).append("</nick>");
//#                                if (tag.getTagName().equals("body")) body = tag.getText();
//#                              }
//#                              buf.append('\n').append(body);
//#                            }
//#  
//#                            
//#                            if (mid!=null){
//#                              buf.append(" (");
//#                              buf.append(mid);
//#                                 if(rid!=null) buf.append("/").append(rid);
//#                              buf.append(")");
//#                            }
//#                            
//#                            
//#                           m = new Msg(Msg.MESSAGE_TYPE_JUICK, botname , null , buf.toString() );
//#                           buf.setLength(0);
//#                             if (mid!=null){
//#                              buf.append("#");
//#                              buf.append(mid);
//#                                 if(rid!=null) buf.append("/").append(rid);
//#                              buf.append(" ");
//#                            }  
//#                           m.id = buf.toString();
//#                           buf.setLength(0);
//#                           childTags.setSize(0);
//#                           storeMessage(m);
//#                            /*
//#                             *<nick>NICK</nick>: textbody (id/reply)
//#                             */
//#                         }
//#                       childBlocks = null;
//#                       childTags = null;
//#                       
//#                     }else {
//#                        m = new Msg(Msg.MESSAGE_TYPE_JUICK, botname , null , "none messages." );
//#                        storeMessage(m);                        
//#                     }
//#                     
//#                 }
//#                 if ( type.equals( "error" ) ) {
//#                    m = new Msg(Msg.MESSAGE_TYPE_JUICK, botname , null , "error. "+data.toString());
//#                    storeMessage(m);                     
//#                 }
//#             }
//#             
//#         } else if( data instanceof Message ) {
//#                  Message message = (Message) data;
//#                  
//#                  //JabberDataBlock juickUnameNs = data.findNamespace("nick", "http://jabber.org/protocol/nick");
//#                  //if (juickUnameNs!=null) juickUnameNs.getText();
//#                  
//#                  JabberDataBlock juickNs = data.findNamespace("juick","http://juick.com/message");
//#                  m.messageType=Msg.MESSAGE_TYPE_JUICK;
//#                  m.id=null;
//#                  
//#                  if(juickNs!=null){
//#                        Vector childBlocks = new Vector(0);
//#                        StringBuffer sb = new StringBuffer(0);   
//#                        String rid = juickNs.getAttribute("rid");
//#                        String mid = juickNs.getAttribute("mid");
//#                        String bodyAnsw = "";
//#                        boolean photo = (juickNs.getAttribute("photo")==null)?false:true;                    
//#                         JabberDataBlock child = null; 
//#                         
//#                          sb.append('@');
//#                          sb.append(juickNs.getAttribute("uname")).append(" ");
//#                          sb.append('(');
//#                          sb.append("#").append(mid==null?"PM":mid);
//#                            if(rid!=null) sb.append("/").append(rid);
//#                            if(photo) sb.append("+photo");
//#                          sb.append(')');
//#                          
//#                         childBlocks = juickNs.getChildBlocks();
//#                         int size=childBlocks.size();
//#                         for(int i=0;i<size;i++){  
//#                           child = (JabberDataBlock)childBlocks.elementAt(i);
//#                           if (child.getTagName().equals("tag")) sb.append("\n *"+child.getText());//+tagNames
//#                           if (child.getTagName().equals("body")) bodyAnsw = child.getText();
//#                         }
//#                         sb.append("\n").append(bodyAnsw);
//#                         if(message.getUrl()!=null) sb.append("\n").append(message.getOOB());
//#                         m.body=sb.toString();
//# 
//#                         /*
//#                          *  @NICK (#NUMBER_POST)
//#                          *  *enabled tags []
//#                          *  MESSAGE_TEXT
//#                          *  url:OOB_LINK
//#                          */                        
//#                         
//#                         sb.setLength(0);//Clear for next MessageEdit msg.id
//#                          if(mid==null)
//#                              sb.append("PM @")
//#                                .append(juickNs.getAttribute("uname"));
//#                          else 
//#                              sb.append("#")
//#                                .append(mid);
//#                         
//#                         if(rid!=null && mid!=null) sb.append("/").append(rid);
//#                         
//#                         sb.append(" ");
//#                         m.id=sb.toString(); // #id/num || #id
//#                         sb.setLength(0);
//#                         childBlocks.setSize(0);
//#                         childBlocks=null;
//#                         sb=null;
//#                         bodyAnsw=null;
//#                         mid=null;
//#                         rid=null;
//#                  return m;
//#              } else { //Simple message
//#                 juickNs = null;
//#                 return separateMsgs(m)?null:m;
//#              }
//#         }
//#      return null;
//#     }    
//#     
//# }
//#endif