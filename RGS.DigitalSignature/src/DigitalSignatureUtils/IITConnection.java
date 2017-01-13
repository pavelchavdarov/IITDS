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

    private HttpsURLConnection conn;
    private Proxy proxy;
    private String connMethod;

    public IITConnection(String pUrl, String pMethod, String pContentType) throws Exception {
        this.getConnection(pUrl, pMethod, pContentType);
    }

    
    
    @Override
    public HttpURLConnection getConnection(String pUrl, String pMethod, String pContentType) throws Exception{
        URL url = null;
        int res_code = 0;
        // пока заглушка
//        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.17.46", 8080));
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.95.5.19", 8888));

        url = new URL(pUrl);

        if (res_code == 0){
            conn = (HttpsURLConnection) url.openConnection(proxy);
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

        if (res_code == 0){
            System.err.println("Connection done...");
            return conn;
        }
        else
            System.err.println("Connection error!!!");
        return null;
    }

    @Override
    public int sendData(String pData) throws Exception{

        OutputStreamWriter outstrean = new OutputStreamWriter (conn.getOutputStream (), "UTF-8");
        BufferedWriter wr = new BufferedWriter (outstrean);
        wr.write(pData);
        wr.flush();

        return 0;
    } 
  
    @Override
    public String getData() throws Exception{
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
    
    public Blob getFile(String fileName) throws Exception{
        
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
        resStr = String4CFT.setPar(resStr,"error", "");
        // дополним дло 1000 символов пробелами справа
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
