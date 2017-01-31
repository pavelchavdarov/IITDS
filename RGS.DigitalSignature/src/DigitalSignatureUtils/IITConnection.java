/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Blob;
import java.util.Map;

import oracle.jdbc.OracleDriver;

/**
 *
 * @author p.chavdarov
 */

public final class IITConnection implements IITConnectionInterface{

    private HttpURLConnection conn;
    private Proxy proxy;
    
    private static final String BOUNDARY = "----WebKitFormBoundary7MA4YWxkTrZu0gW";

    public IITConnection(String pUrl, String pMethod, String pContentType) throws Exception {
        this.initConnection(pUrl, pMethod, pContentType);
    }
    
    public IITConnection(){
        
    }
    
    @Override
    public Blob getFile(String url) throws Exception{return null;}
        
    @Override
    public void initConnection(String pUrl, String pMethod, String pContentType) throws Exception{
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.5.19", 8889));
        URL url = new URL(pUrl);

        conn = (HttpURLConnection) url.openConnection(proxy);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(60000);
        conn.setRequestMethod(pMethod);
        if(!pMethod.equals("GET")){
            if(pContentType.equals("multipart/form-data"))
                pContentType += "; boundary=" + BOUNDARY;
            conn.setRequestProperty("Content-Type", pContentType/*"application/json"*/);
            if(pContentType.equals("application/json"))
                conn.setRequestProperty("charset", "utf-8");
        }
        conn.connect();
    }

    @Override
    public void sendData(String pData) throws Exception{
        OutputStream os = conn.getOutputStream ();
        OutputStreamWriter outstream = new OutputStreamWriter (os, "UTF-8");
        BufferedWriter wr = new BufferedWriter (outstream);
        wr.write(pData);
        wr.flush();
        
        conn.disconnect();
        
    } 
  
    @Override
    public String getData() throws Exception{
        String result="";
        char[] cbuf = new char[1];
        if (conn != null) {
            InputStreamReader instrean;
            try {
                instrean = new InputStreamReader(conn.getInputStream(), "utf-8");
            } catch (IOException e) {
                instrean = new InputStreamReader(conn.getErrorStream(), "utf-8");
                BufferedReader in = new BufferedReader(instrean);
                while (in.read(cbuf) != -1)
                    result += String.valueOf(cbuf);
                Exception ex = new Exception(result);
                throw ex;
            }
            BufferedReader in = new BufferedReader(instrean);
            while (in.read(cbuf) != -1) {
                result += String.valueOf(cbuf);
            }
        }
        conn.disconnect();

        return result;
    }
    
    @Override
    public void sendFile(Blob pBlob) throws Exception{
    }
    
    
    @Override
    public String sendRegDoc(String fileName) throws Exception{
        return null;
    }
    
    @Override
    public void sendFileIO(String fileName) throws Exception{

    }
    
    @Override
    public String sendDoc(String fileName, String docId) throws Exception{
    return null;
}

    @Override
    public String sendDoc(Blob file, String docId) throws Exception{
    return null;
}
    
    @Override
    public Blob getFile() throws Exception{
        
        oracle.jdbc.OracleConnection oraConn =
//(oracle.jdbc.OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@test03.msk.russb.org:1521:rbotest2","ibs","12ibs");                
            (oracle.jdbc.OracleConnection)new OracleDriver().defaultConnection();
        oracle.sql.BLOB retBlob =
                oracle.sql.BLOB.createTemporary(oraConn,
                                                true,
                                                oracle.sql.BLOB.DURATION_SESSION);
        InputStream inStream = conn.getInputStream();
        OutputStream outStream = retBlob.setBinaryStream(1);

        String resStr = "";

        // дополним до 1000 символов пробелами справа
        resStr = String.format("%-1000s", resStr);
        byte[] buf = resStr.getBytes();
        outStream.write(buf);
        
        byte[] b = new byte[1];
        while(inStream.read(b) != -1){
            outStream.write(b);
        }
        outStream.flush();
        conn.disconnect();
        return retBlob;
    }
    
    @Override
    public void getFileIO(String fileName) throws Exception{
        if (conn != null) {
            InputStream inStream = conn.getInputStream();

            ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(fileName);
            
            long filePosition = 0;
            long transferedBytes = fos.getChannel().transferFrom(rbc, filePosition, Long.MAX_VALUE);
            
            while(transferedBytes == Long.MAX_VALUE){
                filePosition += transferedBytes;
                transferedBytes = fos.getChannel().transferFrom(rbc, filePosition, Long.MAX_VALUE);
            }
            rbc.close();
            fos.close();
        }
        conn.disconnect();
    }

    @Override
    public String sendDocWithProps(Blob file, String docId, Map<String, String> props) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String sendDocWithProps(String fileName, String docId, Map<String, String> props) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
