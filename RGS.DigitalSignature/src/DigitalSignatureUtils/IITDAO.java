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
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.sql.Blob;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleDriver;
import sun.misc.BASE64Decoder;

/**
 *
 * @author p.chavdarov
 */
public class IITDAO
{
//    static String url = "https://iitcloud-demo.iitrust.ru";
//    static String method = "POST";
//    static String page = "api/auth/";
    private IITConnectionInterface conn;
    private Gson gson;
    
    private static String SessionToken;
    //private IitDocumentPackage[] packageList;
    //private IitDocumentPackage curreantPackage;
    
    private IitAuth auth;
    private IitWorkflow workflow;
    //private String[] docMandatoryFields;
//    private IitConsumer wfConsumer;
    
    private static IITDAO DAO;

//    /**
//     * Get the value of DAO
//     *
//     * @return the value of DAO
//     */
//    public static IITDAO getDAO() {
//        return DAO;
//    }
//
//    /**
//     * Set the value of DAO
//     *
//     * @param DAO new value of DAO
//     */
//    public static void setDAO(IITDAO DAO) {
//        IITDAO.DAO = DAO;
//    }

    
    public IITDAO(){
        this.gson = new Gson();
        this.workflow = new IitWorkflow();
        SessionToken = "";
        DAO = this;
    }

//////////////////////////////////////
//    public String getDocPackagesList() throws Exception {
//        String url = "http://iitcloud-demo.iitrust.ru";
//        String method = "GET";
//        String page = "api/agent/document/package";
//        java.lang.reflect.Type itemsArrType = new TypeToken<IitDocumentPackage[]>() {}.getType();
//
//        String res="";
//        String url_str = String.format("%s/%s?token=%s", url, page,SessionToken);
//        
//        conn = new IITConnection(url_str, method, "application/json");
//        String answer = conn.getData();
//        System.err.println("response: "+ answer);
////        packageList = gson.fromJson(answer, itemsArrType);
//        IitDocumentPackageList packageList = gson.fromJson(answer, IitDocumentPackageList.class);
//        for (IitDocumentPackage p : packageList.docPackageList){
//            res = String4CFT.setPar(res, "id", String.valueOf(p.getId()));
//            res = String4CFT.setPar(res, "title", String.valueOf(p.getTitle()));
//            res = res + "||";
//        }
//        
//        return res;
//    }
    
//////////////////////////////////////    
    public static void main(String[] args) throws Exception{
        
        String uData = "^~last_name~ФАХРЕТДИНОВ~^^~first_name~АЙРАТ~^^~middle_name~РИНАТОВИЧ~^^~birthed~1985-06-22~^^~phone~79177978047~^^~gender~M~^";
        String uDocData = "^~type~internal-passport~^^~series~8005~^^~number~827104~^^~issue_code~022-001~^^~issue~ДЕМСКИМ РОВД ГОР. УФЫ РЕСП. БАШКОРТОСТАН~^^~issued~2005-07-13~^";
        String uAddrData = "^~type~permanent~^^~region~02~^^~city~Уфа~^^~street~Ухтомского~^^~house~22~^^~apartment~90~^";
        
        String token = makeAuth("deprgs-demo", "321qwe654");
        HashMap<String, String> map = String4CFT.getMap(token);
        System.out.println(token);
        String packageList = getDocPackageList(map.get("token"));
        System.out.println("packageList: " + packageList);
        Blob bl = RegisterClient(uData, uDocData, uAddrData, 35);
        
        String testStr = bl.toString();
    }

    public static String makeAuth(String login, String password) {
        if (DAO == null) DAO = new IITDAO();
        
        String res = IitAuth.makeAuth(login, password);
        DAO.SessionToken = IitAuth.SessionToken;
        return res;
        
    }
    
    public static String getDocPackageList(String token) {//throws Exception {
        if (DAO == null) DAO = new IITDAO();
        
        if(!DAO.SessionToken.equals(token)){
            //String ret ="";
            //ret = String4CFT.setPar(ret, "error", "Токен устарел. Необходимо запустить процедуру аутентификации.");
            DAO.SessionToken = token;
        }
        return IitDocumentPackageList.getDocPackagesList();
    }
    
    public static Blob RegisterClient(  String uData, String uDocData, 
                                        String uAddrData, int packageId ){
        if (DAO == null) DAO = new IITDAO();

        //dao.auth = new IitAuth();
        Blob result = null;
        String resStr="";
        try{
            
            //DAO.auth.makeAuthEx("deprgs-demo", "321qwe654");
            // только для getDocPackagesList()
            //DAO.SessionToken = DAO.auth.SessionToken;
            //System.err.println("package list: " + DAO.getDocPackagesList());
        //        DAO.workflow = new IitWorkflow(DAO.auth.SessionToken);
            //dao.workflow = new IitWorkflow();
            DAO.workflow.createWorkflow(packageId);

            DAO.workflow.createClient(uData, uDocData, uAddrData);

            String state = DAO.workflow.getWorkflowState();
            while(  !state.equals("wait-confirmation-documents-and-sms")
                    && !state.equals("rejected")
                    && !state.equals("wait-order-documents")
                 ){
                sleep(5000); // "Пять минут, Турецкий"
                state = DAO.workflow.getWorkflowState();
            }
            
            if (state.equals("wait-confirmation-documents-and-sms")){
                // !! сделать отдельную функцю получения списка регистрационных документов
                System.out.println("RegDocList: " + DAO.workflow.getRegDocList());
                result = DAO.workflow.dowloadDocument(fileSchema.signatureAgreement);
            }else if(state.equals("wait-order-documents")){
               Exception ex = new Exception("Тут будет загрузка депозитного договора для подписи");
               throw ex;
            }
            
        }catch(Exception e){
           System.err.println(e.getMessage());
           System.err.println(e);
            
            resStr = String4CFT.setPar(resStr,"error", e.getMessage());
            if (resStr.length()>=1000){
                resStr = resStr.substring(0, 1000);
            }else
                resStr = String.format("%-1000s", resStr);

            try {
                oracle.jdbc.OracleConnection oraConn =
//                (oracle.jdbc.OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@test03.msk.russb.org:1521:rbotest2","ibs","12ibs");
                 (oracle.jdbc.OracleConnection)new OracleDriver().defaultConnection();
                
                result = oracle.sql.BLOB.createTemporary(oraConn,
                                                true,
                                                oracle.sql.BLOB.DURATION_SESSION);
                OutputStream outStream = result.setBinaryStream(1);
                //BASE64Decoder decoder = new BASE64Decoder();
                byte[] buf = resStr.getBytes();
                
                outStream.write(buf);
                outStream.flush();

            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
            
            // записать resStr в result!!!
        }
        return result;
    }
    
    public static String SendRegDocs(Blob passport, Blob agreement){
       //if (DAO == null) DAO = new IITDAO();
       
       return DAO.workflow.uploadDocument(passport, agreement);
    }
}
