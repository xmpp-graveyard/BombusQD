//#ifdef JUICK.COM
//# /*
//#  * JuickListener.java
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
//# /**
//#  *
//#  * @author aqent
//#  */
//# public class JuickModule{
//#     
//#     /** Creates a new instance of JuickListener */
//#     public static JuickModule listener;
//#     public JuickModule() { }
//#     
//#     
//#     public static JuickModule jm() {
//#         if (listener==null) listener=new JuickModule();
//#         return listener;
//#     }    
//#     
//#     
//#     public Msg getMsg(Msg m,JabberDataBlock data) {
//#         if( data instanceof Iq ) {
//#             
//#         } else if( data instanceof Message ) {
//#                  Message message = (Message) data;
//#                  
//#                  //JabberDataBlock juickUnameNs = data.findNamespace("nick", "http://jabber.org/protocol/nick");
//#                  //if (juickUnameNs!=null) juickUnameNs.getText();
//#                  
//#                  JabberDataBlock juickNs = data.findNamespace("juick","http://juick.com/message");
//# 
//#                  if(juickNs!=null){
//# 
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
//#                         
//#                         int size=juickNs.getChildBlocks().size();
//#                         for(int i=0;i<size;i++){  
//#                           child = (JabberDataBlock)juickNs.getChildBlocks().elementAt(i);
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
//#                         m.messageType=Msg.MESSAGE_TYPE_JUICK;
//#                         m.id=sb.toString(); // #id/num || #id
//#                         sb.setLength(0);
//#                         sb=null;
//#                         bodyAnsw=null;
//#                         mid=null;
//#                         rid=null;
//#                  return m;
//#              }
//#              juickNs=null;
//#            return m;
//#         }
//#        return null;
//#     }    
//#     
//# }
//#endif