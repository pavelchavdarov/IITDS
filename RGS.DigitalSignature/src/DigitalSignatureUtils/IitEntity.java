/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import com.google.gson.Gson;
import java.util.Date;

/**
 *
 * @author p.chavdarov
 */
public class IitEntity {
    protected static Gson gson;
    protected static final String URL = "http://iitcloud-demo.iitrust.ru";

    IITConnectionInterface iitConn;
    String method;
    String uri;
    String url_str;
    protected static String SessionToken;
    protected static A_Connection AConn;

    private static final boolean DEBUG = false;
    
    void log(String msg){
        if(DEBUG)
            System.out.println("[" + new Date() + "]: " + msg);
    }
    
    public static void Init(){
        if (IitEntity.gson == null)
            IitEntity.gson = new Gson();
        if (IitEntity.AConn == null){
            IitEntity.AConn = new A_Connection("iitcloud-demo.iitrust.ru", 80, "http");
            AConn.setProxy("10.95.5.19", 8889, "http");            
        }
    }
    
    /**
     * Get the value of gson
     *
     * @return the value of gson
     */
    public static Gson getGson() {
        return gson;
    }

    /**
     * Set the value of gson
     *
     * @param gson new value of gson
     */
    public static void setGson(Gson gson) {
        IitEntity.gson = gson;
    }

    /**
     * Get the value of SessionToken
     *
     * @return the value of SessionToken
     */
    public String getSessionToken() {
        return SessionToken;
    }

    /**
     * Set the value of SessionToken
     *
     * @param SessionToken new value of SessionToken
     */
    public void setSessionToken(String SessionToken) {
        IitEntity.SessionToken = SessionToken;
    }
}
