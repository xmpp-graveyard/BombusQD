/*
 * HistoryConfig.java
 */

package History;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author aqent
 */
public class HistoryConfig {
    
    /** Creates a new instance of HistoryConfig */
    public HistoryConfig() {
    }
    
    private boolean historyInRMS = true;
    private boolean msgLogConf = false;
    
    public void loadFromStorage(){
        DataInputStream inputStream = NvStorage.ReadFileRecord("history_storage", 0);
        try {
            historyInRMS=inputStream.readBoolean();
            inputStream.close();
            inputStream=null;
	} catch (Exception e) {
            try {
                if (inputStream!=null) {
                    inputStream.close();
                    inputStream=null;
                }
            } catch (IOException ex) {}
	}
    }
    
    public void saveToStorage(){
	try {
            DataOutputStream outputStream = NvStorage.CreateDataOutputStream();
            outputStream.writeBoolean(historyInRMS);
            NvStorage.writeFileRecord(outputStream, "history_storage", 0, true);
	} catch (IOException e) { }
    }
}
