/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
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
import java.nio.channels.WritableByteChannel;
import java.sql.Blob;
import oracle.jdbc.OracleDriver;
import sun.misc.BASE64Encoder;

import org.apache.commons.httpclient.HttpClient;

//import sun.misc.BASE64Encoder;

/**
 *
 * @author p.chavdarov
 */

public class IITConnection implements IITConnectionInterface{

    private HttpURLConnection conn;
    private Proxy proxy;
    
    private static final String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";

    public IITConnection(String pUrl, String pMethod, String pContentType) throws Exception {
        this.getConnection(pUrl, pMethod, pContentType);
    }

    
    
    @Override
    public HttpURLConnection getConnection(String pUrl, String pMethod, String pContentType) throws Exception{
        URL url = null;
        // пока заглушка
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.17.46", 8080));
 //       proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.5.19", 8888));
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.101.20.32", 3128));
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.101.20.21", 8888));
        System.err.println("Connecting to " + pUrl + " ...");
        url = new URL(pUrl);

        conn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(60000);
        conn.setRequestMethod(pMethod);
        if(!pMethod.equals("GET")){
            if(pContentType.equals("multipart/form-data"))
                pContentType += "; boundary=" + boundary;
            conn.setRequestProperty("Content-Type", pContentType/*"application/json"*/);
            if(pContentType.equals("application/json"))
                conn.setRequestProperty("charset", "utf-8");
        }
        System.err.println("Connection prepared...");
        return conn;
    }

    @Override
    public void sendData(String pData) throws Exception{
        OutputStream os = conn.getOutputStream ();
        OutputStreamWriter outstream = new OutputStreamWriter (os, "UTF-8");
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
//                JsonParser jParser = new JsonParser();
//                JsonObject jObj = (JsonObject)jParser.parse(result);
//                if (jObj != null && jObj.has("detail")){
                    Exception ex = new Exception(result);
                    throw ex;
 //               }
                //throw e;
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
        final String ContentType = "Content-Type: application/pdf";
        final String CRLF = "\r\n"; 
        byte[] buf = new byte[1];
        
        OutputStream outStream = conn.getOutputStream ();
        DataOutputStream wr = new DataOutputStream(outStream);
        // пишем раздел
        wr.write(ContentDisposition.getBytes());
        //outStream.write(ContentDisposition.getBytes());
        wr.write(CRLF.getBytes());
        //outStream.write(CRLF.getBytes());
        wr.write(ContentType.getBytes());
        //outStream.write(ContentType.getBytes());
        wr.write(CRLF.getBytes());
        //outStream.write(CRLF.getBytes());
        wr.write(CRLF.getBytes());
        //outStream.write(CRLF.getBytes());
        
        InputStream inStream = pBlob.getBinaryStream();
        // пишем содержимое файла
        while(inStream.read(buf) != -1){
            wr.write(buf);
        }
        wr.flush();
        wr.close();
    }
    
    
    public void sendFile(String fileName) throws Exception{
        final String ContentDisposition = "Content-Disposition: form-data; name=\"path\"; filename=\"\"";
        final String ContentType = "Content-Type: pdf";
        final String CRLF = "\r\n"; 
        final String LF = "\n"; 
        byte[] buf = new byte[1];
        FileOutputStream fos = new FileOutputStream(fileName + "_bcc");
        
        OutputStream outStream = conn.getOutputStream ();
        DataOutputStream wr = new DataOutputStream(outStream);

        // пишем раздел
//        wr.write(CRLF.getBytes());
        wr.write(boundary.getBytes());
        fos.write(boundary.getBytes());
        wr.write(CRLF.getBytes());
        fos.write(CRLF.getBytes());
        wr.write(ContentDisposition.getBytes());
        fos.write(ContentDisposition.getBytes());
        wr.write(CRLF.getBytes());
        fos.write(CRLF.getBytes());
        wr.write(ContentType.getBytes());
        fos.write(ContentType.getBytes());
        wr.write(CRLF.getBytes());
        fos.write(CRLF.getBytes());
        wr.write(CRLF.getBytes());
        fos.write(CRLF.getBytes());
        wr.write(CRLF.getBytes());
        fos.write(CRLF.getBytes());
        wr.flush();
        fos.flush();
//        wr.write(CRLF.getBytes());
        //outStream.write(CRLF.getBytes());
        
        FileInputStream fis = new FileInputStream(fileName);
        int fileSize = fis.available();
        buf =  new byte[fileSize];
        
        BASE64Encoder encoder = new BASE64Encoder();
        while(fis.read(buf) != -1){
           wr.write(encoder.encode(buf).getBytes("UTF-8"));
           //wr.write(buf);
           fos.write(encoder.encode(buf).getBytes("UTF-8"));
//           fos.write(buf);
        }
//        wr.write("<body>".getBytes());
        fis.close();
//        fos.close();
        wr.flush();
        fos.flush();
        //System.err.print(CRLF);
        //wr.write(CRLF.getBytes());

        wr.write(CRLF.getBytes());
        fos.write(CRLF.getBytes());
        wr.write(CRLF.getBytes());
        fos.write(CRLF.getBytes());
        
        wr.write(boundary.getBytes());
        fos.write(boundary.getBytes());
        wr.write(CRLF.getBytes());
        fos.write(CRLF.getBytes());
        
        wr.flush();
        fos.flush();
        wr.close();
        fos.close();

//        outStream.write(boundary.getBytes());
//        outStream.write(CRLF.getBytes());
//        outStream.write(ContentDisposition.getBytes());
//        outStream.write(CRLF.getBytes());
//        outStream.write(ContentType.getBytes());
//        outStream.write(CRLF.getBytes());
//        outStream.write(CRLF.getBytes());
//
//        FileInputStream fis = new FileInputStream(fileName);
//        WritableByteChannel wbc = Channels.newChannel(outStream);
//        long filePosition = 0;
//        long transferedBytes = fis.getChannel().transferTo(filePosition, Long.MAX_VALUE, wbc);
//        while(transferedBytes == Long.MAX_VALUE){
//            filePosition += Long.MAX_VALUE;
//            transferedBytes = fis.getChannel().transferTo(filePosition, Long.MAX_VALUE, wbc);
//        }
//        
//        outStream.write(CRLF.getBytes());
//        outStream.write(boundary.getBytes());
//        outStream.write(CRLF.getBytes());
        
//        wr.flush();
//        wbc.close();
//        fis.close();
        
////        InputStream inStream = pBlob.getBinaryStream();
////        // пишем содержимое файла
////        while(inStream.read(buf) != -1){
////            outStream.write(buf);
////        }
    }
    
    
public void sendFileToSign(String fileName, String docId) throws Exception{
        final String ContentDisposition = "Content-Disposition: form-data; name=\"path\"; filename=\""+fileName+"\"";
        final String ContentDisposition2 = "Content-Disposition: form-data; name=\"document\"";
//        final String ContentType = "Content-Type: application/pdf";
        final String ContentType = "Content-Type:";
        final String CRLF = "\r\n"; 
        byte[] buf = new byte[1];
        
        
        OutputStream outStream = conn.getOutputStream ();
        DataOutputStream wr = new DataOutputStream(outStream);
        
//////        // пишем раздел
//////        System.err.print(CRLF);
//////        wr.write(CRLF.getBytes());
//////        
//////        System.err.print(boundary);
//////        wr.write(boundary.getBytes());
//////        
//////        System.err.print(CRLF);
//////        wr.write(CRLF.getBytes());
//////        
//////        System.err.print(ContentDisposition);
//////        wr.write(ContentDisposition.getBytes());
//////        //outStream.write(ContentDisposition.getBytes());
//////        
//////        System.err.print(CRLF);
//////        wr.write(CRLF.getBytes());
//////        //outStream.write(CRLF.getBytes());
//////        
//////        System.err.print(ContentType);
//////        wr.write(ContentType.getBytes());
//////        //outStream.write(ContentType.getBytes());
//////        
//////        System.err.print(CRLF);
//////        wr.write(CRLF.getBytes());
//////        //outStream.write(CRLF.getBytes());
//////        
//////        System.err.print(CRLF);
//////        wr.write(CRLF.getBytes());
//////        //outStream.write(CRLF.getBytes());
//////        
//////        FileInputStream fis = new FileInputStream(fileName);
//////        FileOutputStream fos = new FileOutputStream(fileName + "_bcc");
//////        buf =  new byte[fis.available()];
////////        BASE64Encoder encoder = new BASE64Encoder();
//////        while(fis.read(buf) != -1){
//////           wr.write(buf);
//////           fos.write(buf);
//////           System.err.print(buf);
//////        }
////////        wr.write("<body>".getBytes());
//////        fis.close();
//////        fos.close();
//////        
//////        System.err.print(CRLF);
//////        wr.write(CRLF.getBytes());
//////        System.err.print(CRLF);
//////        wr.write(CRLF.getBytes());
//////        
//////        System.err.println(boundary);
//////        wr.write(boundary.getBytes());
//////        System.err.print(CRLF);
//////        wr.write(CRLF.getBytes());
//////        
//////        wr.flush();
//////        wr.close();

        wr.write(boundary.getBytes());
        wr.write(CRLF.getBytes());
        wr.write(ContentDisposition.getBytes());
        wr.write(CRLF.getBytes());
        wr.write(ContentType.getBytes());
        wr.write(CRLF.getBytes());
        wr.write(CRLF.getBytes());

        FileInputStream fis = new FileInputStream(fileName);
        WritableByteChannel wbc = Channels.newChannel(outStream);
        long filePosition = 0;
        long transferedBytes = fis.getChannel().transferTo(filePosition, Long.MAX_VALUE, wbc);
        while(transferedBytes == Long.MAX_VALUE){
            filePosition += Long.MAX_VALUE;
            transferedBytes = fis.getChannel().transferTo(filePosition, Long.MAX_VALUE, wbc);
        }
        wbc.close();
        fis.close();
        
        wr.write(CRLF.getBytes());
        wr.write(boundary.getBytes());
        wr.write(CRLF.getBytes());
        wr.write(ContentDisposition2.getBytes());
        wr.write(CRLF.getBytes());
        wr.write(CRLF.getBytes());
        
        wr.write(docId.getBytes());
        wr.write(CRLF.getBytes());
        wr.write(boundary.getBytes());
        
        wr.flush();
        
        
////        InputStream inStream = pBlob.getBinaryStream();
////        // пишем содержимое файла
////        while(inStream.read(buf) != -1){
////            outStream.write(buf);
////        }
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
    
    public void getFile(String fileName) throws Exception{
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
    }
    
    
}
