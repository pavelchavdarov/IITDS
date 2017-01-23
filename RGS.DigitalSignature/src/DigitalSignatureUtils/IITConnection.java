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

/**
 *
 * @author p.chavdarov
 */

public class IITConnection implements IITConnectionInterface{

    private HttpURLConnection conn;
    private Proxy proxy;
    
    private static final String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";

    public IITConnection(String pUrl, String pMethod, String pContentType) throws Exception {
        this.initConnection(pUrl, pMethod, pContentType);
    }
    
    public IITConnection(){
        
    }
    
    @Override
    public void initConnection(String pUrl, String pMethod, String pContentType) throws Exception{
        URL url = null;
        // пока заглушка
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.17.46", 8080));
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080));        
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.5.19", 8888));
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));        
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.101.20.32", 3128));
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.101.20.21", 8888));
//        System.err.println("Connecting to " + pUrl + " ...");
        url = new URL(pUrl);

        conn = (HttpURLConnection) url.openConnection(proxy);
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
        
        conn.disconnect();
    }
    
    
    public String sendRegDoc(String fileName) throws Exception{

        String result = "";
//        char[] cbuf = new char[1];
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        try{
//            HttpHost target = new HttpHost("iitcloud-demo.iitrust.ru", 443, "https");
//            HttpHost proxy = new HttpHost("10.95.17.46", 8080, "http");
////            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//            RequestConfig config = RequestConfig.custom().build();
//            HttpPut request = new HttpPut("/"+uri);
//            request.setHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryP42Bh5cCzeeP8O38");
//            
//            request.setConfig(config);
//            
//            FileBody pdfBody = new FileBody(new File(fileName));
//
//            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
//                    .setBoundary("----WebKitFormBoundaryP42Bh5cCzeeP8O38")
//                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
//                    .addBinaryBody( "path",
//                                    new File(fileName),
//                                    ContentType.create("application/pdf"),
//                                    //ContentType.create("text/plain"),
//                                    null);
//                    //.setBoundary(boundary);
//            
//            
//            HttpEntity reqEntity = builder.build();
//                    
//            request.setEntity(reqEntity);
//            
//            
//            
////            System.out.println(request.toString());
////            Header[] headers = request.getAllHeaders();
////            for(Header h : headers){
////                System.out.println(h.getName() + " : " + h.getValue());
////            }
//            
//            CloseableHttpResponse response = httpClient.execute(target, request);
////            CloseableHttpResponse response = httpClient.execute(request);
//            try{
//                HttpEntity resEntity = response.getEntity();
//                if (resEntity  != null){
//                    InputStreamReader inStream = new InputStreamReader(resEntity.getContent());
//                    BufferedReader br = new BufferedReader(inStream);
//                    while(br.read(cbuf) != -1){
//                        result+=String.valueOf(cbuf);
//                    }
//                }
//            }finally{
//                response.close();
//            }
//        }finally{
//            httpClient.close();
//        }
//        System.out.println("result: "+result);
        return result;
    }
    
    public void sendFile(String fileName) throws Exception{
        final String ContentDisposition = "Content-Disposition: form-data; name=\"path\"; filename=\"\"";
        final String ContentType = "Content-Type: multipart/pdf";
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
        
        while(fis.read(buf) != -1){
           wr.write(buf);
           fos.write(buf);
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
    
    
public String sendDoc(String fileName, String docId) throws Exception{
    return null;
}

public String sendDoc(Blob file, String docId) throws Exception{
    return null;
}
    
    public Blob getFile() throws Exception{
        
        oracle.jdbc.OracleConnection oraConn =
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
        System.out.println(retBlob);
        conn.disconnect();
        return retBlob;
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
        conn.disconnect();
    }
    
    
}
