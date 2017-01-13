/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import static DigitalSignatureUtils.IitEntity.SessionToken;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.sql.Blob;
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
    
    private static IitAuth     auth;
    private IitWorkflow workflow;
    //private String[] docMandatoryFields;
//    private IitConsumer wfConsumer;
    
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
    public String getDocPackagesList(){
        String url = "https://iitcloud-demo.iitrust.ru";
        String method = "GET";
        String page = "api/agent/document/package";
        java.lang.reflect.Type itemsArrType = new TypeToken<IitDocumentPackage[]>() {}.getType();

        String res="";
        String url_str = String.format("%s/%s?token=%s", url, page,SessionToken);
        
        try{
            conn = new IITConnection(url_str, method, "application/json");
            packageList = gson.fromJson(conn.getData(), itemsArrType);
            for (IitDocumentPackage p : packageList){
                res = res + "||" + "^~pkg~" + String.valueOf(p.getId()) + "~^" + "||";
            }
        }catch(Exception ex){
            Logger.getLogger("http_conn").log(Level.SEVERE, null, ex);
            res = res + "^~error~" + ex.toString()+"~^";
        }
        return res;
    }
    
//////////////////////////////////////    
    public static void main(String[] args) throws Exception{
        
        IITDAO dao = new IITDAO();
        //dao.auth = new IitAuth();
        String token = dao.auth.makeAuth("deprgs-demo", "321qwe654");
        dao.SessionToken = token;
        dao.getDocPackagesList();
        dao.workflow = new IitWorkflow(token);
        dao.workflow.createWorkflow(dao.packageList[0].getId());
        
        String str1 = "Алибабаев"+"~"+"Василий"+"~"+"Алибабаевич"+"~"+"1948-05-12"+"~"+"79202392016"+"~"+"M"+"~"+"";
        String str2 = "internal-passport"+"~"+"8005"+"~"+"810001"+"~"+"000-001"+"~"+"ЙЦУ РУВД гор. КЕН респ. ФЫВ"+"~"+"2015-04-14"+"#";
        String str3 = "permanent"+"~"+"02"+"~"+"г. КЕН"+"~"+"ЯЧСМИ"+"~"+"21"+"~"+"18"+"#";
                
        dao.workflow.createClient(str1, str2, str3);
        
        String state = dao.workflow.getWorkflowState();
        while(!state.equals("wait-confirmation-documents-and-sms") && !state.equals("rejected")){
            System.out.println(state);
            sleep(5000); // "Пять минут, Турецкий"
            state = dao.workflow.getWorkflowState();
        }
        
        System.out.println("RegDocList: " + dao.workflow.getRegDocList());
        
        System.out.println(dao.workflow.dowloadDocument(fileSchema.signatureAgreement));
        
    }

//    public static void Test() throws Exception{
//        
//        IITDAO dao = new IITDAO();
//        dao.auth = new IitAuth();
//        String token = dao.auth.makeAuth("deprgs-demo", "321qwe654");
//        dao.SessionToken = token;
//        dao.getDocPackagesList();
//        dao.workflow = new IitWorkflow(token);
//        dao.workflow.createWorkflow(dao.curreantPackage.getId());
//
//        
//        String state = dao.workflow.getWorkflowState();
//        while(!state.equals("wait-confirmation-documents-and-sms") && !state.equals("rejected")){
//            System.out.println(state);
//            sleep(5000); // "Пять минут, Турецкий"
//            state = dao.workflow.getWorkflowState();
//        }
//        
//        System.out.println("RegDocList: " + dao.workflow.getRegDocList());
//        
//        System.out.println(dao.workflow.dowloadDocument(fileSchema.signatureAgreement));
//
//    }
    
//    public static void testAuth() throws Exception{
//        
//        IITDAO dao = new IITDAO();
//        dao.auth = new IitAuth();
//        //return 
//        dao.SessionToken = dao.auth.makeAuth("deprgs-demo", "321qwe654");
//    }
    
    
    public static Blob RegisterClient(  String uData, String uDocData, 
                                        String uAddrData){
         //IITDAO dao = new IITDAO();
        //dao.auth = new IitAuth();
        Blob result = null;
        String resStr="";
        try{
            DAO.auth.makeAuthEx("deprgs-demo", "321qwe654");
            // только для getDocPackagesList()
            DAO.SessionToken = DAO.auth.SessionToken;
            DAO.getDocPackagesList();
        //        DAO.workflow = new IitWorkflow(DAO.auth.SessionToken);
            DAO.workflow = new IitWorkflow();
            DAO.workflow.createWorkflow(DAO.packageList[0].getId());

            DAO.workflow.createClient(uData, uDocData, uAddrData);

            String state = DAO.workflow.getWorkflowState();
            while(!state.equals("wait-confirmation-documents-and-sms") && !state.equals("rejected")){
                System.out.println(state);
                sleep(5000); // "Пять минут, Турецкий"
                state = DAO.workflow.getWorkflowState();
            }

            System.out.println("RegDocList: " + DAO.workflow.getRegDocList());
            result = DAO.workflow.dowloadDocument(fileSchema.signatureAgreement);
            
        }catch(Exception e){
            System.err.println(e.getMessage());
            resStr = String4CFT.setPar(resStr,"error", e.getMessage());
            // записать resStr в result!!!
        }
        return result;
    }
}
