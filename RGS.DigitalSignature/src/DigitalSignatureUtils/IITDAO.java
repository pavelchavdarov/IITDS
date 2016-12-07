/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p.chavdarov
 */
public class IITDAO
{
//    static String url = "https://iitcloud-demo.iitrust.ru";
//    static String method = "POST";
//    static String page = "api/auth/";
    private IITConnection conn;
    private Gson gson;
    
    private String SessionToken;
    private IitDocumentPackage[] packageList;
    private IitDocumentPackage curreantPackage;
    
    private IitAuth     auth;
    private IitWorkflow workflow;
    //private String[] docMandatoryFields;
    private IitConsumer wfConsumer;
    
    private static IITDAO DAO;

    /**
     * Get the value of DAO
     *
     * @return the value of DAO
     */
    public static IITDAO getDAO() {
        return DAO;
    }

    /**
     * Set the value of DAO
     *
     * @param DAO new value of DAO
     */
    public static void setDAO(IITDAO DAO) {
        IITDAO.DAO = DAO;
    }

    
    public IITDAO(){
        gson = new Gson();
    }

//////////////////////////////////////
//    public String getDocPackagesList(){
//        String url = "https://iitcloud-demo.iitrust.ru";
//        String method = "GET";
//        String page = "api/agent/document/package";
//        java.lang.reflect.Type itemsArrType = new TypeToken<IitDocumentPackage[]>() {}.getType();
//
//        String res = "";
//        String url_str = String.format("%s/%s?token=%s", url, page,SessionToken);
//        conn = new IITConnection();
//        conn.getConnection(url_str, method, "application/json");
//        
//        try{
//            res = conn.getData();
//            packageList = gson.fromJson(res, itemsArrType);
//            for (IitDocumentPackage p : packageList){
//                curreantPackage = p;
//            }
//        }catch(IOException ex){
//            Logger.getLogger("http_conn").log(Level.SEVERE, null, ex);
//            res = ex.toString();
//        }
//        return res;
//    }
    
//////////////////////////////////////    
    public static void main(String[] args) throws InterruptedException{
        IITDAO dao = new IITDAO();
        dao.auth = new IitAuth();
        String token = dao.auth.makeAuth("deprgs-demo", "321qwe654");
/*        dao.SessionToken = token;
        dao.getDocPackagesList();
        dao.workflow = new IitWorkflow(token);
        dao.workflow.createWorkflow(dao.curreantPackage.getId());
        dao.workflow.createClient(
                        "Чавдаров",
                        "Павел",
                        "Георгиевич",
                        "M",
                        "1984-08-22",
                        "79177978047",
                        
                        "internal-passport",
                        "8005",
                        "812008",
                        "022-005",
                        "Орджоникидзевским РУВД гор. Уфы респ. Башкортостан",
                        "2015-04-14",
                
                        "permanent",
                        "02",
                        "г. Уфа",
                        "Комсомольская",
                        "21",
                        "18"
        );
        
        String state = dao.workflow.getWorkflowState();
        while(!state.equals("wait-confirmation-documents-and-sms") && !state.equals("rejected")){
            System.out.println(state);
            sleep(5000); // "Пять минут, Турецкий"
            state = dao.workflow.getWorkflowState();
        }
        
        System.out.println("RegDocList: " + dao.workflow.getRegDocList());
        
        System.out.println(dao.workflow.dowloadDocument(fileSchema.signatureAgreement));
      */  
    }
    
    
}
