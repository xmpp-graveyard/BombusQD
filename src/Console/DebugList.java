/*
 * DebugList.java
 *
 */
//#ifdef CONSOLE
//# package Console;
//#  
//# import Client.Msg;
//# import java.util.Vector;
//# 
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# public class DebugList {
//# 
//#     Vector stanzas=new Vector();
//#     private static DebugList instance;
//#     
//#     public static DebugList get(){
//# 	if (instance==null) {
//# 	    instance=new DebugList();
//# 	}
//# 	return instance;
//#     }
//#     
//#     public Msg msg(int index){
//# 	try {
//#             Msg msg=(Msg)stanzas.elementAt(index);
//# 	    return msg;
//# 	} catch (Exception e) {}
//# 	return null;
//#     }
//# 
//#     public void add(String msg, int type) {
//#       if (midlet.BombusQD.cf.debug) {
//# 	  try {
//#             Msg stanza=new Msg(type, "debug", null, msg.toString());
//#             stanza.itemCollapsed=false;
//#             stanzas.addElement(stanza);
//#             stanza=null;
//# 	  } catch (Exception e) {}
//#        }
//#     }
//# 
//#     public int size(){
//# 	return stanzas.size();
//#     }
//# }
//#endif