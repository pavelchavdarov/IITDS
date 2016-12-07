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

    IitAuth(){
        super();
    }
    public String makeAuth(String login, String password)
    {
        this.method = "POST";
        this.page = "api/auth";
        IitToken token;
        authRequest auth  = new authRequest();
        
        String res="";
        this.url_str = String.format("%s/%s", this.url, this.page);
        auth.setUsername(login);
        auth.setPassword(password);
        String json_str = gson.toJson(auth, authRequest.class);

        conn = new IITConnection();
        conn.getConnection(this.url_str, this.method, "application/json");
        try{
            int i = conn.sendData(json_str);
            if (i==0){
                token = gson.fromJson(conn.getData(), IitToken.class);
                SessionToken = token.getToken();//conn.getData();
                System.out.println("Authentication result: token passed");
                res = SessionToken;
            }
            else
                res = "null";
        }catch(IOException ex){
            Logger.getLogger("http_conn").log(Level.SEVERE, null, ex);
            StackTraceElement[] trace = ex.getStackTrace();
            for (StackTraceElement st : trace){
            	res += st.toString()+"\n";
            }
            res += ex.getMessage();
        }
        return res;
    }
    public static String Authenticate(String userName, String password){
               
        IitAuth auth = new IitAuth();
        return auth.makeAuth("deprgs-demo", "321qwe654");
    }
}

class authRequest {
    
    private String username;
    private String password;

    authRequest(){
    }
    
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

class IitToken {
    
    private String token;

    /**
     * Get the value of token
     *
     * @return the value of token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set the value of token
     *
     * @param token new value of token
     */
    public void setToken(String token) {
        this.token = token;
    }

}