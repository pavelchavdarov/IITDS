/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author p.chavdarov
 */
public class A_Connection implements IITConnectionInterface{
    private static final String BOUNDARY = "----WebKitFormBoundaryP42Bh5cCzeeP8O38";
    
    private final HttpHost target;
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
    
    @Override
    public void initConnection(String uri, String pMethod, String pContentType) throws Exception{
        this.Uri = uri;
        this.method = pMethod;
        this.contentType = pContentType;
        httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
    }
    
    @Override
    public void sendData(String pData) throws Exception{ 
    
    }
    
    @Override
    public String getData() throws Exception{return null;}
    @Override
    public void sendFile(Blob pBlob) throws Exception{}
    @Override
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
    
    @Override
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
            request.setHeader("Content-Type", contentType + "; boundary=" + BOUNDARY);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(BOUNDARY)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody( "path",
                                    new File(fileName),
                                    ContentType.create("application/pdf"),
                                    null);

            HttpEntity reqEntity = builder.build();
            request.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);

        }catch(IOException e ){
            result = String4CFT.setPar(result, "error", e.getMessage());
        }
        finally{
            httpClient.close();
        }
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
            request.setHeader("Content-Type", contentType + "; boundary=" + BOUNDARY);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(BOUNDARY)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody( "path",
                                    file.getBinaryStream(),
                                    ContentType.create("application/pdf"),
                                    "file_to_reg.pdf");

            HttpEntity reqEntity = builder.build();
            request.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);
        }catch(IOException e ){
                result = String4CFT.setPar(result, "error", e.getMessage());

        } catch (SQLException e) {
            result = String4CFT.setPar(result, "error", e.getMessage());
        }
        finally{
            httpClient.close();
        }
        return result;
    }
    
    @Override
    public String sendDocWithProps(Blob file, String docId, Map<String,String> doc_props) throws Exception{
        String result = "";
        try{
            HttpPost request = new HttpPost(this.Uri);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            request.setHeader("Content-Type", contentType + "; boundary=" + BOUNDARY);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(BOUNDARY)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody( "path",
                                    file.getBinaryStream(),
                                    ContentType.create("application/pdf"),
                                    "file_to_sign.pdf")
                    .addTextBody("document", docId);
            
            
            for(String prop_id: doc_props.keySet())
                builder.addTextBody("property_"+prop_id, doc_props.get(prop_id));

            HttpEntity reqEntity = builder.build();
            request.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);
        }catch(IOException e ){
            result = String4CFT.setPar(result, "error", e.getMessage());
        } catch (SQLException e) {
            result = String4CFT.setPar(result, "error", e.getMessage());
        }
        finally{
            httpClient.close();
        }
        return result;
    }
    
    @Override
    public String sendDoc(Blob file, String docId) throws Exception{
        String result = "";
        try{
            HttpPost request = new HttpPost(this.Uri);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            request.setHeader("Content-Type", contentType + "; boundary=" + BOUNDARY);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(BOUNDARY)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody( "path",
                                    file.getBinaryStream(),
                                    ContentType.create("application/pdf"),
                                    "file_to_sign.pdf")
                    .addTextBody("document", docId);
            
            HttpEntity reqEntity = builder.build();
            request.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);
        }catch(IOException e ){
            result = String4CFT.setPar(result, "error", e.getMessage());
        } catch (SQLException e) {
            result = String4CFT.setPar(result, "error", e.getMessage());
        }
        finally{
            httpClient.close();
        }
        return result;
    }
    
    @Override
    public String sendDoc(String fileName, String docId) throws Exception{
        String result = "";
        try{
            HttpPost request = new HttpPost(this.Uri);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            request.setHeader("Content-Type", contentType + "; boundary=" + BOUNDARY);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(BOUNDARY)
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
        }catch(IOException e ){
            result = String4CFT.setPar(result, "error", e.getMessage());
        }
        finally{
            httpClient.close();
        }
        return result;
    }
    
    @Override
    public String sendDocWithProps(String fileName, String docId, Map<String,String> doc_props) throws Exception{
        String result = "";
        try{
            HttpPost request = new HttpPost(this.Uri);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            request.setHeader("Content-Type", contentType + "; boundary=" + BOUNDARY);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setBoundary(BOUNDARY)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody( "path",
                                    new File(fileName),
                                    //new File(fileName),
                                    ContentType.create("application/pdf"),
                                    null)
                    .addTextBody("document", docId);

            for(String prop_id: doc_props.keySet())
                builder.addTextBody("property_"+prop_id, doc_props.get(prop_id));
            
            HttpEntity reqEntity = builder.build();
            request.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(target, request);
            result = getResponse(response);
        }catch(IOException e ){
            result = String4CFT.setPar(result, "error", e.getMessage());
        }
        finally{
            httpClient.close();
        }
        return result;
    }
    
    @Override
    public Blob getFile() throws Exception{return null;}
    @Override
    public void getFileIO(String url) throws Exception{
        try{
            HttpGet request = new HttpGet(url);
            RequestConfig config;
            if(proxy != null)
                config = RequestConfig.custom().setProxy(proxy).build();
            else
                config = RequestConfig.custom().build();
            request.setConfig(config);
            
            CloseableHttpResponse response = httpClient.execute(request);
            String newUrl = response.getHeaders("Location")[0].getValue();
            request = new HttpGet(newUrl.replace("https://", "http://"));
            request.setConfig(config);
            response = httpClient.execute(request);
            FileOutputStream fos = new FileOutputStream("signed_doc_5.pdf");
            FileChannel foc = fos.getChannel();
            
            try{
                HttpEntity resEntity = response.getEntity();
                if (resEntity  != null){
                    InputStream inStream = resEntity.getContent();
                    ReadableByteChannel rbc = Channels.newChannel(inStream);
                    long filePos = 0;
                    long transferedBypes = foc.transferFrom(rbc, filePos, Long.MAX_VALUE);
                    while(transferedBypes == Long.MAX_VALUE){
                        filePos += transferedBypes;
                        transferedBypes = foc.transferFrom(rbc, filePos, Long.MAX_VALUE);
                    }
                    rbc.close();
                    foc.close();
                    fos.close();
                }
                
            }finally{
                response.close();
            }
        }finally{
            httpClient.close();
        }
    }

    public String getDocData() throws Exception{
        String result = "";
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
        return result;
    }
    
    
    @Override
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
            String newUrl = response.getHeaders("Location")[0].getValue();
            request = new HttpGet(newUrl.replace("https://", "http://"));
            request.setConfig(config);
            response = httpClient.execute(request);
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
