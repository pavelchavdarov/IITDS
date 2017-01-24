/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.Blob;
import oracle.jdbc.OracleDriver;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author p.chavdarov
 */
public class A_Connection implements IITConnectionInterface{
    private static final String boundary = "----WebKitFormBoundaryP42Bh5cCzeeP8O38";
    
    private HttpHost target;
    private HttpHost proxy;
    private CloseableHttpClient httpClient;
    private String Uri;
    private String method;
    private String contentType;
    
    public A_Connection(String host, int port, String schema){
        target = new HttpHost(host, port, schema);
        
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public void setProxy(String host, int port, String schema) {
        this.proxy = new HttpHost(host, port, schema);
    }
    
    public void initConnection(String uri, String pMethod, String pContentType) throws Exception{
        this.Uri = uri;
        this.method = pMethod;
        this.contentType = pContentType;
        httpClient = HttpClients.createDefault();
    }
    
    @Override
    public void sendData(String pData) throws Exception{ 
    
    }
    
    public String getData() throws Exception{return null;}
    public void sendFile(Blob pBlob) throws Exception{}
    public void sendFileIO(String fileName) throws Exception{}
    
    
    private String getResponse(CloseableHttpResponse response) throws IOException{
        String result = "";
        char[] cbuf = new char[1];
        
        try{
                HttpEntity resEntity = response.getEntity();
                if (resEntity  != null){
                    InputStreamReader inStream = new InputStreamReader(resEntity.getContent());
                    BufferedReader br = new BufferedReader(inStream);
                    while(br.read(cbuf) != -1){
                        result+=String.valueOf(cbuf);
                    }
                }
            }finally{
                response.close();
            }
        return result;
    }
    
    public String sendRegDoc(String fileName) throws Exception{
        String result = "";
        try{
            HttpPut request = new HttpPut(this.Uri);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            request.setHeader("Content-Type", contentType + "; boundary=" + boundary);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(boundary)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody( "path",
                                    new File(fileName),
                                    ContentType.create("application/pdf"),
                                    null);

            HttpEntity reqEntity = builder.build();
            request.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);

        }catch(Exception e ){
            result = String4CFT.setPar(result, "error", e.getMessage());
        }
        finally{
            httpClient.close();
        }
        System.out.println("result: "+result);
        return result;
    }
    
    public String sendRegDoc(Blob file) throws Exception{
        String result = "";
        try{
            HttpPut request = new HttpPut(this.Uri);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            request.setHeader("Content-Type", contentType + "; boundary=" + boundary);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(boundary)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody( "path",
                                    file.getBinaryStream(),
                                    ContentType.create("application/pdf"),
                                    "file_to_reg.pdf");

            HttpEntity reqEntity = builder.build();
            request.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);
        }catch(Exception e ){
                result = String4CFT.setPar(result, "error", e.getMessage());

        }
        finally{
            httpClient.close();
        }
        System.out.println("result: "+result);
        return result;
    }
    
    public String sendDoc(Blob file, String docId) throws Exception{
        String result = "";
        //char[] cbuf = new char[1];
        try{
            HttpPost request = new HttpPost(this.Uri);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            request.setHeader("Content-Type", contentType + "; boundary=" + boundary);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(boundary)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody( "path",
                                    file.getBinaryStream(),
                                    //new File(fileName),
                                    ContentType.create("application/pdf"),
                                    null)
                    .addTextBody("document", docId);

            HttpEntity reqEntity = builder.build();
            request.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);
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
        }catch(Exception e ){
            result = String4CFT.setPar(result, "error", e.getMessage());
        }
        finally{
            httpClient.close();
        }
        System.out.println("result: "+result);
        return result;
    }
    
    public String sendDoc(String fileName, String docId) throws Exception{
        String result = "";
        //char[] cbuf = new char[1];
        try{
            HttpPost request = new HttpPost(this.Uri);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            request.setHeader("Content-Type", contentType + "; boundary=" + boundary);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(boundary)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody( "path",
                                    new File(fileName),
                                    //new File(fileName),
                                    ContentType.create("application/pdf"),
                                    null)
                    .addTextBody("document", docId);

            HttpEntity reqEntity = builder.build();
            request.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);
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
        }catch(Exception e ){
            result = String4CFT.setPar(result, "error", e.getMessage());
        }
        finally{
            httpClient.close();
        }
        System.out.println("result: "+result);
        return result;
    }
    
    public Blob getFile() throws Exception{return null;}
    public void getFileIO(String fileName) throws Exception{}
    
    public String getDocData() throws Exception{
        String result = "";
        //char[] cbuf = new char[1];
        try{
            HttpGet request = new HttpGet(this.Uri);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            
            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);
        }finally{
            httpClient.close();
        }
        System.out.println("result: "+result);
        return result;
    }
    
    
    public Blob getFile(String url) throws Exception{
        oracle.jdbc.OracleConnection oraConn =
//(oracle.jdbc.OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@test03.msk.russb.org:1521:rbotest2","ibs","12ibs");                
            (oracle.jdbc.OracleConnection)new OracleDriver().defaultConnection();
        oracle.sql.BLOB retBlob =
                oracle.sql.BLOB.createTemporary(oraConn,
                                                true,
                                                oracle.sql.BLOB.DURATION_SESSION);
        OutputStream outStream = retBlob.setBinaryStream(1);
        String resStr = "";

        // дополним до 1000 символов пробелами справа
        resStr = String.format("%-1000s", resStr);
        byte[] buf = resStr.getBytes();
        outStream.write(buf);
        
        try{
            HttpGet request = new HttpGet(url);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            
            CloseableHttpResponse response = httpClient.execute(request);
            
            try{
                HttpEntity resEntity = response.getEntity();
                if (resEntity  != null){
                    InputStream inStream = resEntity.getContent();
                    byte[] b = new byte[1];
                    while(inStream.read(b) != -1)
                        outStream.write(b);
                    outStream.flush();
                }
            }finally{
                response.close();
            }
        }finally{
            httpClient.close();
        }
        
        return retBlob;
    }
}
