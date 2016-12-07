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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author p.chavdarov
 */

public class IITConnection implements IITConnectionInterface{

    private HttpURLConnection conn;
    private Proxy proxy;
    private String connMethod;

    @Override
    public HttpURLConnection getConnection(String pUrl, String pMethod, String pContentType){
        URL url = null;
        int res_code = 0;
        // пока заглушка
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.17.46", 8080));
        try{
                url = new URL(pUrl);
        }catch(MalformedURLException mx){
                Logger.getLogger("https_conn").log(Level.SEVERE, null, mx);
                res_code = 1;
        }
        try {
            if (res_code == 0){
                conn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setConnectTimeout(60000);
                conn.setRequestMethod(pMethod);
                conn.setRequestProperty("Content-Type", pContentType/*"application/json"*/);
                conn.setRequestProperty("charset", "utf-8");
            }
            else
                res_code = 1;
        } catch (IOException ex){
                Logger.getLogger("https_conn").log(Level.SEVERE, null, ex);
                res_code = 1;
        }
        if (res_code == 0){
//            System.out.println("Connection done...");
            return conn;
        }
        else
            System.out.println("Connection error!!!");
        return null;
    }

    @Override
    public int sendData(String pData) throws IOException{
        if (conn == null)
            return 1;
        OutputStreamWriter outstrean = new OutputStreamWriter (conn.getOutputStream (), "UTF-8");
        BufferedWriter wr = new BufferedWriter (outstrean);
        wr.write(pData);
        wr.flush();
        

        return 0; //conn.getResponseCode();

//		byte[] byteData = null;
//		try{
//			byteData = pData.getBytes("UTF-8");
//		}catch (UnsupportedEncodingException ee){
//			Logger.getLogger("https_conn").log(Level.SEVERE, null, ee);
//			return -1;
//		}
//		conn.setRequestProperty("Content-Length", Integer.toString(byteData.length));
//		try {
//			OutputStream os = conn.getOutputStream();
//			os.write(byteData);
//			os.close();
//			return conn.getResponseCode();
//		} catch (IOException ex) {
//			Logger.getLogger("https_conn").log(Level.SEVERE, null, ex);
//			return -1;
//		}

    } 
  
    @Override
    public String getData() throws IOException{
        String result="";
        String inputLine;
        
        if (conn != null) {
            InputStreamReader instrean = new InputStreamReader(conn.getInputStream(), "utf-8");
            BufferedReader in = new BufferedReader(instrean);
            while ((inputLine = in.readLine()) != null) {
                result += inputLine;
            }
            
        }
        return result;
    }
    
    public String getFile(String fileName) throws IOException{
            
        if (conn != null) {
            ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        return fileName;
    }
}
