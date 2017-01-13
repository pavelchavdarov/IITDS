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
    
    private String SessionToken;
    private IitDocumentPackage[] packageList;
    //private IitDocumentPackage curreantPackage;
    
    private IitAuth auth;
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
        this.gson = new Gson();
        this.workflow = new IitWorkflow();
    }

//////////////////////////////////////
    public String getDocPackagesList() throws Exception {
        String url = "http://iitcloud-demo.iitrust.ru";
        String method = "GET";
        String page = "api/agent/document/package";
        java.lang.reflect.Type itemsArrType = new TypeToken<IitDocumentPackage[]>() {}.getType();

        String res="";
        String url_str = String.format("%s/%s?token=%s", url, page,SessionToken);
        System.err.println(url_str);

            conn = new IITConnection(url_str, method, "application/json");
            System.err.println("getting packageList...");
            String answer = conn.getData();
            System.err.println("response: "+ answer);
            packageList = gson.fromJson(answer, itemsArrType);
            System.err.println(packageList.length);
            for (IitDocumentPackage p : packageList){
                res = res + "||" + "^~pkg~" + String.valueOf(p.getId()) + "~^" + "||";
            }
        return res;
    }
    
//////////////////////////////////////    
    public static void main(String[] args) throws Exception{
        /*
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
        */
        String uData = "^~last_name~ФАХРЕТДИНОВ~^^~first_name~АЙРАТ~^^~middle_name~РИНАТОВИЧ~^^~birthed~1985-06-22~^^~phone~79191409459~^^~gender~M~^";
        String uDocData = "^~type~internal-passport~^^~series~8005~^^~number~827104~^^~issue_code~022-001~^^~issue~ДЕМСКИМ РОВД ГОР. УФЫ РЕСП. БАШКОРТОСТАН~^^~issued~2005-07-13~^";
        String uAddrData = "^~type~permanent~^^~region~02~^^~city~Уфа~^^~street~Ухтомского~^^~house~22~^^~apartment~90~^";
        
        Blob bl = RegisterClient(uData, uDocData, uAddrData);
        
        String testStr = bl.toString();
    }

    public static Blob RegisterClient(  String uData, String uDocData, 
                                        String uAddrData){
         IITDAO dao = new IITDAO();
        //dao.auth = new IitAuth();
        Blob result = null;
        String resStr="";
        try{
            
            dao.auth.makeAuthEx("deprgs-demo", "321qwe654");
            // только для getDocPackagesList()
            dao.SessionToken = dao.auth.SessionToken;
            System.err.println("package list: " + dao.getDocPackagesList());
        //        DAO.workflow = new IitWorkflow(DAO.auth.SessionToken);
            //dao.workflow = new IitWorkflow();
            dao.workflow.createWorkflow(dao.packageList[0].getId());

            dao.workflow.createClient(uData, uDocData, uAddrData);

            String state = dao.workflow.getWorkflowState();
            while(!state.equals("wait-confirmation-documents-and-sms") && !state.equals("rejected")){
                System.out.println(state);
                sleep(5000); // "Пять минут, Турецкий"
                state = dao.workflow.getWorkflowState();
            }

            System.out.println("RegDocList: " + dao.workflow.getRegDocList());
            result = dao.workflow.dowloadDocument(fileSchema.signatureAgreement);
            
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
}
