/*
 * Utf8IOStream.java
 *
 * Created on 18.12.2005, 0:52
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
 */

package io;

//#if ZLIB
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;
//#endif
import java.io.IOException;
import java.io.InputStream; 
import java.io.OutputStream;
import javax.microedition.io.*;
import util.Strconv;

/**
 *
 * @author EvgS
 */
public class Utf8IOStream {
    
    private StreamConnection connection;
    private InputStream inpStream;
    private OutputStream outStream;

    private boolean iStreamWaiting;

    private long bytesRecv;
    private long bytesSent;

//#if (ZLIB)
    public void setStreamCompression(){
        inpStream=new ZInputStream(inpStream);
        outStream=new ZOutputStream(outStream, JZlib.Z_DEFAULT_COMPRESSION);
        ((ZOutputStream)outStream).setFlushMode(JZlib.Z_SYNC_FLUSH);
    }
//#endif
    
    /** Creates a new instance of Utf8IOStream */
    public Utf8IOStream(StreamConnection connection) throws IOException {
	this.connection=connection;
        try {
            SocketConnection sc=(SocketConnection)connection;
            sc.setSocketOption(SocketConnection.KEEPALIVE, 1);
            sc.setSocketOption(SocketConnection.LINGER, 300);
        } catch (Exception e) {}

	inpStream = connection.openInputStream();
	outStream = connection.openOutputStream();	

        length=0;
        pbyte=0;
    }
    
 //#if (ZLIB)    
    public long countPocketsSend=0;
//#endif       
    
    public void send( StringBuffer data ) throws IOException {
//#if (ZLIB)            
	countPocketsSend++;
//#endif          
	synchronized (outStream) {
            byte[] bytes = Strconv.stringToByteArray(data.toString());
            int outLen=bytes.length;
	    outStream.write(bytes);
            setSent(bytesSent+outLen);

	    outStream.flush();
            bytes=null;
            bytes=new byte[0];
            data.setLength(0);
            updateTraffic();
	}
//#if (XML_STREAM_DEBUG)        
//#         System.out.println(">> "+data);
//#endif
    }
    
    byte cbuf[]=new byte[512];
    int length;
    int pbyte;
    
    int avail=0;
    int lenbuf=0;

    public int read(byte buf[]) throws IOException {
        avail=inpStream.available();

        if (avail==0) return 0;

        lenbuf=buf.length;
        
        if (avail>lenbuf) avail=lenbuf;
        
        avail=inpStream.read(buf, 0, avail);
//#if (XML_STREAM_DEBUG)
//# 	System.out.println("<< "+new String(buf, 0, avail));
//#endif
        setRecv(bytesRecv+avail);
        updateTraffic();
        return avail;
    }

    private void updateTraffic() {
        midlet.BombusQD.sd.traffic=getBytes();
        midlet.BombusQD.sd.updateTrafficOut();
    }
    
    
    private void setRecv(long bytes) {
        bytesRecv=bytes;
    }
    
    private void setSent(long bytes) {
        bytesSent=bytes;
    }    
    
    public void close() {
	try { outStream.close(); outStream=null; }  catch (Exception e) {}
	try { inpStream.close(); inpStream=null; }  catch (Exception e) {}
    }

//#if ZLIB
    private StringBuffer stats = new StringBuffer(0);

    public String getPocketsStats() {
        return Long.toString(countPocketsSend);
    }    
  
    public String getStreamStatsBar() {
        stats.setLength(0);
        try {
            long sent=bytesSent;
            long recv=bytesRecv;
            if (inpStream instanceof ZInputStream) {
                ZInputStream z = (ZInputStream) inpStream;
                recv+=z.getTotalIn()-z.getTotalOut();
                ZOutputStream zo = (ZOutputStream) outStream;
                sent+=zo.getTotalOut()-zo.getTotalIn();
                String ratio=Long.toString((10*z.getTotalOut())/z.getTotalIn());
                int dotpos=ratio.length()-1;                
                stats.append("(");
                stats.append( (dotpos==0)? "0":ratio.substring(0, dotpos)).append('.').append(ratio.substring(dotpos)).append('x');
                stats.append(")");
                ratio=null;
            }else{
               return "";
            }
        } catch (Exception e) {
            stats=null;
            return null;
        }
        return stats.toString();       
    }

    public String getStreamStats() {
        stats.setLength(0);
        try {
            long sent=bytesSent;
            long recv=bytesRecv;
            if (inpStream instanceof ZInputStream) {
                ZInputStream z = (ZInputStream) inpStream;
                recv+=z.getTotalIn()-z.getTotalOut();
                ZOutputStream zo = (ZOutputStream) outStream;
                sent+=zo.getTotalOut()-zo.getTotalIn();
                stats.append("ZLib:\nin: "); appendZlibStats(stats, z.getTotalIn(), z.getTotalOut(), true);
                stats.append("\nout: "); appendZlibStats(stats, zo.getTotalOut(), zo.getTotalIn(), false);
            }
            stats.append("\nin: ")
                 .append(recv)
                 .append("\nout: ")
                 .append(sent);
        } catch (Exception e) {
            stats=null;
            return "";
        }
        return stats.toString();
    }
    
    private void appendZlibStats(StringBuffer s, long packed, long unpacked, boolean read){
        s.append(packed).append(read?"->":"<-").append(unpacked);
        String ratio=Long.toString((10*unpacked)/packed);
        int dotpos=ratio.length()-1;
        s.append(" (").append( (dotpos==0)? "0":ratio.substring(0, dotpos)).append('.').append(ratio.substring(dotpos)).append('x').append(")");
    }
        
    
    public String getConnectionData() {
        stats.setLength(0);
        try {
            stats.append(((SocketConnection)connection).getLocalAddress())
                 .append(":")
                 .append(((SocketConnection)connection).getLocalPort())
                 .append("->")
                 .append(((SocketConnection)connection).getAddress())
                 .append(":")
                 .append(((SocketConnection)connection).getPort());
        } catch (Exception ex) {
            stats.append("unknown");
        }
        return stats.toString();
    }

    public long getBytes() {
        long startBytes=bytesSent+bytesRecv;
        try {
            if (inpStream instanceof ZInputStream) {
                ZOutputStream zo = (ZOutputStream) outStream;
                ZInputStream z = (ZInputStream) inpStream;
                return (long)zo.getTotalOut()+(long)z.getTotalIn();
            }
            return startBytes;
        } catch (Exception e) { }
        return 0;
    }
//#else
//#      private StringBuffer stats = new StringBuffer(0);
//#      public String getStreamStats() {
//#          stats.setLength(0);
//#          try {
//#              long sent=bytesSent;
//#              long recv=bytesRecv;
//#              stats.append("\nStream: in=").append(recv).append(" out=").append(sent);
//#          } catch (Exception e) {
//#              stats=null;
//#              return "";
//#          }
//#          return stats.toString();
//#      }
//#      
//#      public long getBytes() {
//#          try {
//#              return bytesSent+bytesRecv;
//#          } catch (Exception e) { }
//#          return 0;
//#      }
//#endif
}
