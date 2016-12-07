/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DigitalSignatureUtils;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Павел
 */
public class IitDocumentPackage extends IitEntity{
    
    IitDocumentPackage(){
        super();
    }
    
    DocPackage[] getPackages(String token){
        DocPackage[] docpackes;
        
        this.method = "GET";
        this.page = "/api/agent/document/package";

        java.lang.reflect.Type itemsArrType = new TypeToken<DocPackage[]>() {}.getType();

        String res = "";
        String url_str = String.format("%s/%s?token=%s", url, page, token);
        conn = new IITConnection();
        conn.getConnection(url_str, method, "application/json");
        
        try{
            res = conn.getData();
            docpackes = gson.fromJson(res, itemsArrType);
            
        }catch(IOException ex){
            Logger.getLogger("http_conn").log(Level.SEVERE, null, ex);
            docpackes = null;
        }
        return docpackes;
    }
    
    static java.sql.Array array_wrapper(
        String typeName,
        Object elements
    ) throws java.sql.SQLException {
        java.oracle.jdbc.OracleDriver ora = new oracle.jdbc.OracleDriver();
        java.sql.Connection conn = ora.defaultConnection();
        oracle.jdbc.OracleConnection oraConn =
            (oracle.jdbc.OracleConnection)conn;
        /* Yeah - typeName have to be UPPERCASE, really. */
        java.sql.Array arr = 
            oraConn.createARRAY(typeName.toUpperCase(), elements);
        return arr;
    }
    
    public static java.sql.Array getPackagesWrapped(String token)
    throws java.sql.SQLException {
        IitDocumentPackage docpack = new IitDocumentPackage();
        return array_wrapper("widgets_t", docpack.getPackages(token));
    }
}


class DocPackage {
    private int id;
    private String title;
    private String type;
    
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }
}

