/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DigitalSignatureUtils;

import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Павел
 */

public class IitDocumentPackageList extends IitEntity{
    
    public static String getDocPackagesList() {
        String url = "http://iitcloud-demo.iitrust.ru";
        String method = "GET";
        String page = "api/agent/document/package";
        java.lang.reflect.Type itemsArrType = new TypeToken<DigitalSignatureUtils.IitDocumentPackage[]>() {}.getType();

        String res="";
        String url_str = String.format("%s/%s?token=%s", url, page,SessionToken);
        try{
            iitConn = new IITConnection(url_str, method, "application/json");
            String answer = iitConn.getData();
            System.err.println("response: "+ answer);
            IitDocumentPackage[] packageList = gson.fromJson(answer, itemsArrType);
    //        ArrayList<IitDocumentPackage> packageList = (ArrayList<IitDocumentPackage>)gson.fromJson(answer, ArrayList.class);
            for (IitDocumentPackage p : packageList){
                res = String4CFT.setPar(res, "id", String.valueOf(p.getId()));
                res = String4CFT.setPar(res, "title", String.valueOf(p.getTitle()));
                res = res + "||";
            }
        }catch(Exception e){
            res = String4CFT.setPar(res,"error", e.getMessage());
        }
        return res;
    }
}

class IitDocumentPackage {
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
