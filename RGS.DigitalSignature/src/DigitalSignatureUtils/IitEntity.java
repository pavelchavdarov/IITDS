/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author p.chavdarov
 */
public class IitEntity {
    protected static Gson gson;
    protected static final String url = "http://iitcloud-demo.iitrust.ru";

    protected static IITConnectionInterface iitConn;
    protected String method;
    protected String uri;
    protected String url_str;
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
    
//    public static void checkErrorInResponse(String responce) throws Exception{
//        JsonParser jParser = new JsonParser();
//        JsonObject jObj = (JsonObject)jParser.parse(responce);
//        if (jObj != null && jObj.has("detail")){
//            Exception ex = new Exception(jObj.get("detail").getAsString());
//            throw ex;
//        }
//    }
    
}
