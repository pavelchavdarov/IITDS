/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p.chavdarov
 */
public class IitAuth extends IitEntity{

//    IitAuth(){
//        super();
//    }
    
    
    public static void makeAuthEx(String login, String password) throws Exception
    {
        Init();
        
        String method = "POST";
        uri = "api/auth/";
        IitToken token;
        authRequest auth  = new authRequest(login, password);
        //String res = "";
        
        String url_str = String.format("%s/%s", IitAuth.url, uri);
        String json_str = gson.toJson(auth, authRequest.class);

        iitConn = new IITConnection(url_str, method, "application/json");

        iitConn.sendData(json_str);
        token = gson.fromJson(iitConn.getData(), IitToken.class);

        SessionToken = token.getToken();//conn.getData();
        System.err.println("Authentication result: token " + SessionToken);
        //res =  setPar(res,"token", SessionToken);//"Authentication result: token passed";
        //res = setPar(res,"error", "");
    }
    
    public static String makeAuth(String login, String password)
    {
        Init();
        
        String method = "POST";
        uri = "api/auth/";
        IitToken token;
        authRequest auth  = new authRequest(login, password);
        String res = "";
        
        String url_str = String.format("%s/%s", IitAuth.url, uri);
        String json_str = gson.toJson(auth, authRequest.class);

        
        try{
            iitConn = new IITConnection(url_str, method, "application/json");
            
            iitConn.sendData(json_str);
            token = gson.fromJson(iitConn.getData(), IitToken.class);

            SessionToken = token.getToken();//conn.getData();
            System.out.println("Authentication result: token passed");
            res =  String4CFT.setPar(res,"token", SessionToken);//"Authentication result: token passed";
            res = String4CFT.setPar(res,"error", "");
        }catch(Exception ex){
            System.err.println(ex.getMessage());
            SessionToken = null;
            res = String4CFT.setPar(res,"error", ex.getMessage());
        }
        return res;
    }
    
    
}
/**
 * Класс создал для описания структуры json при аутентификации
 * @author Павел
 */
class authRequest {
    
    private String username;
    private String password;

    authRequest(){}
    authRequest(String userName, String password){
        this.setUsername(userName);
        this.setPassword(password);
    }
    
    /**
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }
    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    
    /**
     * Get the value of username
     *
     * @return the value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the value of username
     *
     * @param username new value of username
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
/**
 * Класс создал для описания структуры json при получении токена
 * @author Павел
 */
class IitToken {
    private String token;
    
    public String getToken(){
        return token;
    }
    
    public void setToken(String pToken){
        token = pToken;
    }
}