/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import com.google.gson.Gson;

/**
 *
 * @author p.chavdarov
 */
public class IitEntity {
    protected static Gson gson;
    protected static final String url = "https://iitcloud-demo.iitrust.ru";

    protected static IITConnectionInterface iitConn;
    protected static String method;
    protected static String uri;
    protected static String url_str;
    protected static String SessionToken;

//    IitEntity() {
//        if (this.gson == null)
//            this.gson = new Gson();
//    }
//    IitEntity(String token) {
//        if (this.gson == null)
//            this.gson = new Gson();
//        this.setSessionToken(token);
//    }
    
    public static void Init(){
        if (IitEntity.gson == null)
            IitEntity.gson = new Gson();
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
        this.SessionToken = SessionToken;
    }
    
}
