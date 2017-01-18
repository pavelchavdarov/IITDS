/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Blob;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.sql.rowset.serial.SerialBlob;
import oracle.jdbc.OracleDriver;

/**
 *
 * @author p.chavdarov
 */

public class IITConnection implements IITConnectionInterface{

    private HttpURLConnection conn;
    private Proxy proxy;

    public IITConnection(String pUrl, String pMethod, String pContentType) throws Exception {
        this.getConnection(pUrl, pMethod, pContentType);
    }

    
    
    @Override
    public HttpURLConnection getConnection(String pUrl, String pMethod, String pContentType) throws Exception{
        URL url = null;
        // пока заглушка
        //proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.17.46", 8080));
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.5.19", 8888));
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.101.20.32", 3128));
        System.err.println("Connecting to " + pUrl + " ...");
        url = new URL(pUrl);

        conn = (HttpURLConnection) url.openConnection(proxy);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(60000);
        conn.setRequestMethod(pMethod);
        if(!pMethod.equals("GET")){
            conn.setRequestProperty("Content-Type", pContentType/*"application/json"*/);
            if(pContentType.equals("application/json"))
                conn.setRequestProperty("charset", "utf-8");
        }
        System.err.println("Connection prepared...");
        return conn;
    }

    @Override
    public void sendData(String pData) throws Exception{

        OutputStreamWriter outstream = new OutputStreamWriter (conn.getOutputStream (), "UTF-8");
        BufferedWriter wr = new BufferedWriter (outstream);
        wr.write(pData);
        wr.flush();

    } 
  
    @Override
    public String getData() throws Exception{
        String result="";
        //String inputLine;
        char[] cbuf = new char[1];
        if (conn != null) {
            InputStreamReader instrean = null;
            try {
                instrean = new InputStreamReader(conn.getInputStream(), "utf-8");
            } catch (IOException e) {
                instrean = new InputStreamReader(conn.getErrorStream(), "utf-8");
                BufferedReader in = new BufferedReader(instrean);
                while (in.read(cbuf) != -1) {
                    result += String.valueOf(cbuf);
                }
                JsonParser jParser = new JsonParser();
                JsonObject jObj = (JsonObject)jParser.parse(result);
                if (jObj != null && jObj.has("detail")){
                    Exception ex = new Exception(jObj.get("detail").getAsString());
                    throw ex;
                }
                throw e;
            }
            BufferedReader in = new BufferedReader(instrean);
//            while ((inputLine = in.readLine()) != null) {
//                result += inputLine;
//            }
            while (in.read(cbuf) != -1) {
                result += String.valueOf(cbuf);
            }
            
        }
        
        conn.disconnect();
        return result;
    }
    
    public void sendFile(Blob pBlob) throws Exception{
        final String ContentDisposition = "Content-Disposition: form-data; name=\"path\"; filename=\"\"";
        final String ContentType = "application/pdf";
        final String CRLF = "\r\n"; 
        byte[] buf = new byte[1];
        
        OutputStream outStream = conn.getOutputStream ();
        // пишем раздел
        outStream.write(ContentDisposition.getBytes());
        outStream.write(CRLF.getBytes());
        outStream.write(ContentType.getBytes());
        outStream.write(CRLF.getBytes());
        
        InputStream inStream = pBlob.getBinaryStream();
        // пишем содержимое файла
        while(inStream.read(buf) != -1){
            outStream.write(buf);
        }
    }
    
    public Blob getFile() throws Exception{
        
        oracle.jdbc.OracleConnection oraConn =
                (oracle.jdbc.OracleConnection)new OracleDriver().defaultConnection();
//                (oracle.jdbc.OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@test03.msk.russb.org:1521:rbotest2","ibs","12ibs");

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
        System.out.println(retBlob);
        return retBlob;

        
//        outStream.write(inStream.read(b));

//        ByteBuffer bbuf = ByteBuffer.allocate(1);
//        ArrayList<ByteBuffer> byteArr = new ArrayList<ByteBuffer>();
//        Blob file = null;
//        if (conn != null) {
//            ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
//            while (rbc.read(bbuf) != -1 ){
//                byteArr.add(bbuf);
//            }
//            byte[] arr = new byte[byteArr.size()];
//            for(int i = 0; i < byteArr.size(); i++ ){
//                arr[i] = byteArr.get(i).array()[0];
//            }
//            try {
//                file = new SerialBlob(arr);
//            } catch (SQLException e) {
//                System.err.println(e.getMessage());
//            }
////            FileOutputStream fos = new FileOutputStream(fileName);
//  //          fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//            return file;
       // }
        //return null;
    }
}
