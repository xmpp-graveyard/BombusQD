/*
 * HistoryStorage.java
 */

package History;
import Client.Contact;
import Client.Msg;
import Client.CommandForm;
import java.io.*;
import ui.controls.form.CheckBox;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;

/**
 *
 * @author aqent
 */
public final class HistoryStorage {
    
    private final static Contact c = null;
    private final static String store = "store_";
    private static CommandForm cmd = null;

    /** Creates a new instance of HistoryStorage */
    public HistoryStorage() {
      if(cmd==null) cmd = new CommandForm(midlet.BombusQD.getInstance().display, midlet.BombusQD.sd.roster, 6 , "" , null, null);
    }
    
    public static RecordStore openRecordStore(Contact c, RecordStore recordStore) {
      try {
        if (recordStore != null) {
          recordStore.closeRecordStore();
          recordStore = null;
          System.gc();
        }
        String rName = getRSName(c.bareJid);
        if(rName.length()>30) rName = rName.substring(0,30);
         recordStore = RecordStore.openRecordStore(rName, true);
        rName = null;
        return recordStore;
      } catch (Exception e) { return null; }
    }
    
    public static RecordStore closeStore(RecordStore recordStore) {	
       try {
           recordStore.closeRecordStore();
       } catch (Exception e) {}
       return null;
    }  
    

    public static RecordStore clearRecordStore(RecordStore recordStore) {
        try {
            int size = recordStore.getNumRecords();
            if (size > 0) {
              for (int i = 1; i <= size; ++i) {
                 recordStore.deleteRecord(i);
              }
            } 
            recordStore.closeRecordStore();
            return null;
         } catch (Exception e) { 
             e.printStackTrace(); return null;
         }
    }

    
    
    private static ByteArrayOutputStream baos = null;
    private static DataOutputStream das = null;
    synchronized public static void addText(Contact c, Msg message, RecordStore recordStore)
    {
        byte[] buffer, textData;
        int len;
        try {
              if(null == recordStore) {
                            String rName = getRSName(c.bareJid);
                            if(rName.length()>30) rName = rName.substring(0,30);
                            recordStore = RecordStore.openRecordStore(rName, true);
                            c.recordStore(c.SAVE_RMS_STORE, recordStore);//save
                            rName = null;
              }
              baos = new ByteArrayOutputStream();
              das = new DataOutputStream(baos);
              das.writeUTF(message.getDayTime());
              das.writeUTF(message.body);
                        
            textData = baos.toByteArray();
            len = textData.length;

            buffer = new byte[len+1];
            System.arraycopy(textData, 0, buffer, 1, len);
            recordStore.addRecord(buffer, 0, buffer.length);

         } catch (Exception ex) {
                 ex.printStackTrace();
         } finally {
                  if (recordStore != null) {
                    c.recordStore(c.CLOSE_RMS_STORE, recordStore);
                    recordStore = null;
                  }
                  try{
                     if (dis != null)  { das.close(); das = null; }
                     if (baos != null) { baos.close(); baos = null; }
                  } catch (Exception e) { }
        }
   }
    
    

    private static Timer timer;
    private static void startTimer(RecordStore rs, Contact c, int repeatTime) {
       if ( timer == null) {
           timer = new Timer();
             LoadMessages load = new LoadMessages();
             load.set(rs, c);
           timer.schedule( load, 0, repeatTime );
       }
    }
   
    private static void stopTimer() {
      if ( timer != null ) {
          timer.cancel();
          timer = null;
      }
    }
  
    private static ByteArrayInputStream bais = null;
    private static DataInputStream dis = null;
    private static StringBuffer sb = new StringBuffer(0);
    private final static class LoadMessages extends TimerTask {
      int posRecord;
      RecordStore recordStore;
      CheckBox addCheckBox;
      Contact c;
      long timeS,timeE;
      
      public void set(RecordStore rs, Contact c){
        posRecord = 0;  
        this.recordStore = rs;
        this.c = c;
      }
      
      public void run () {
         if(posRecord == 0) {
           cmd.setParentView(c);
           timeS = System.currentTimeMillis();
         }
         int size = 0;
         int i;
         try {
              byte[] msgData = null;
              size = recordStore.getNumRecords();
              if(size == 0) stopTimer();
              try {
                   for (i=0; i < 5; ++i) {
                    posRecord++;
                    msgData = recordStore.getRecord(posRecord);
                          bais = new ByteArrayInputStream(msgData, 1, msgData.length - 1);
                          dis = new DataInputStream(bais);
                          sb.setLength(0);
                          sb.append(dis.readUTF());
                          sb.append(':');
                          sb.append('%');
                          sb.append(dis.readUTF());
                    addCheckBox = new CheckBox( sb.toString(), true, true, true);
                    cmd.addObject(addCheckBox, posRecord, size);
                   }
                   msgData = null;
              } catch (Exception e) {
                posRecord = size;
              }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(posRecord == size){
                   timeE = System.currentTimeMillis();
                   
                   try{
                     sb.setLength(0);
                     sb.append("RMS: ")
                       .append(recordStore.getName())
                       .append("%Stats:\nSize->")
                       .append(recordStore.getSize())
                       .append(" bytes")
                       .append("\nSizeAvailable->")
                       .append(recordStore.getSizeAvailable())
                       .append(" bytes\nRecords->")
                       .append(recordStore.getNumRecords())
                       .append("\nLoad Time-> ")
                       .append(Long.toString(timeE - timeS))
                       .append(" msec");
                     
                     addCheckBox = new CheckBox( sb.toString() , true, true, true);
                     cmd.addObject(addCheckBox, posRecord, size);
                     cmd.addObject(c.bareJid, 0, 0);
                   } catch (Exception e) { }
                   
                  stopTimer();
                  addCheckBox = null;
                  if (recordStore != null) {
                    c.recordStore(c.CLOSE_RMS_STORE, recordStore);
                    recordStore = null;
                  }
                    try{
                         if (dis != null) { das.close(); das = null; }
                         if (baos != null) { baos.close(); baos = null; }
                    } catch (Exception e) { }
                }
            }
      }
    }
  

    public static void getAllData(Contact c, RecordStore recordStore) {
       startTimer(recordStore, c, 20);
    }

   
    public static int getRecordCount(RecordStore recordStore) {	
       try {
         return recordStore.getNumRecords();
       } catch (Exception e) {  return -1; }
    }

    
    private static String getRSName(String bareJid) {
       return store + bareJid;
    }
}
