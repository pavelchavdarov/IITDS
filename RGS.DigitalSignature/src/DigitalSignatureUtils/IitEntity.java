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
    protected IITConnection conn;
    //protected static Gson gson;
    //protected String SessionToken;
    
    protected static Gson gson;
//    protected static final String url = "https://iitcloud-demo.iitrust.ru";
    protected static final String url = "http://127.0.0.1:5000";
    protected String method;
    protected String page;
    protected String url_str;
    
    IitEntity() {
        if (this.gson == null)
            this.gson = new Gson();
    }
    
    IitEntity(String token) {
        if (this.gson == null)
            this.gson = new Gson();
        this.setSessionToken(token);
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

    
    protected String SessionToken;

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
