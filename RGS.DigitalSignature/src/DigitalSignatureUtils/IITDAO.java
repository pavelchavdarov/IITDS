/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import com.google.gson.Gson;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import oracle.jdbc.OracleDriver;

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
    
//    private static String SessionToken;
    //private IitDocumentPackage[] packageList;
    //private IitDocumentPackage curreantPackage;
    
    private IitAuth auth;
    private IitWorkflow workflow;
    private static ArrayList<String> statList;
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
        auth = new IitAuth();
        this.workflow = new IitWorkflow();
        statList= new ArrayList<String>();
//        SessionToken = "";
        DAO = this;
    }
    
    public static String waitWorkflowState(String wfState) throws Exception {
        int i = 0;
        
        String state = DAO.workflow.getWorkflowState();
        while(!wfState.equals(state) && i++ < 30){ // 30 сек.
            sleep(1000); // "Пять минут, Турецкий" (секунд)
            state = DAO.workflow.getWorkflowState();
        }
        return state;
    }
    
    public static String waitCertificateReady() throws Exception{
        String resSrt = "";
        // 1 -- узнаем текущее состояние потока работ
        String wfState = DAO.workflow.getWorkflowState();
        // 2.1 -- если он в процессе выпуска и устеновки сертификата, то
        //      ждем окончания установки сертификата
        if(wfState.equals("certificate_issue"))
            wfState = waitWorkflowState("wait-order-documents");
        else
        // 2.2 -- если сертификат не в процессе утсановки, значит еще не создана
        // заявка на выпуск сертификата. Ждем создания заявки.    
            wfState = waitWorkflowState("wait-certificate-issue");
        // 3. В итоге, получаем некий статус потока работе. Если это статус 
        // создания запроса на сертификат, то подтверждаем создание запроса
        if(wfState.equals("wait-certificate-issue"))
            // пока пропускаем этап получения запроса на сертификат для сверки реквизитов
            wfState = DAO.workflow.setWorkflowState("certificate_issue");
        
        
        resSrt = String4CFT.setPar(resSrt,"state", wfState);
        
        return resSrt;


    }
    
    /**
     * Сздает Blob и помещает в первую 1000 чисволов строку source.
     * @param source
     * @return Blob
     * @throws Exception 
     */
    private static Blob CreateBlob(String source) throws Exception{
        //resStr= String4CFT.setPar(resStr,"state: ", source);
        //source = String.format("%-1000s", source);

        oracle.jdbc.OracleConnection oraConn =
//                (oracle.jdbc.OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@test03.msk.russb.org:1521:rbotest2","ibs","12ibs");
         (oracle.jdbc.OracleConnection)new OracleDriver().defaultConnection();

        Blob result = oracle.sql.BLOB.createTemporary(oraConn, true, oracle.sql.BLOB.DURATION_SESSION);
        OutputStream outStream = result.setBinaryStream(1);
        byte[] buf = source.getBytes();
        outStream.write(buf);
        //outStream.flush();
        return result;
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
        String userId = RegisterClient(uData, uDocData, uAddrData, "35");
        
        System.out.println("userId: " + userId);
    }

    public static String makeAuth(String login, String password) {
        if (DAO == null) DAO = new IITDAO();
        
        String res = DAO.auth.makeAuth(login, password);
        //DAO.SessionToken = IitAuth.SessionToken;
        return res;
        
    }
    
    public static String getDocPackageList(String token) {//throws Exception {
        if (DAO == null) DAO = new IITDAO();
        
//        if(!DAO.SessionToken.equals(token)){
//            //String ret ="";
//            //ret = String4CFT.setPar(ret, "error", "Токен устарел. Необходимо запустить процедуру аутентификации.");
//            DAO.SessionToken = token;
//        }
        return IitDocumentPackageList.getDocPackagesList();
    }
    /**
     * Создает поток работ по пакету документов packageId
     * @param uData
     * @param uDocData
     * @param uAddrData
     * @param packageId
     * @return 
     */
    public static String RegisterClient(  String uData, String uDocData, 
                                        String uAddrData, String packageId ){
        if (DAO == null) DAO = new IITDAO();

        String resStr="";
        try{
            DAO.workflow.createWorkflow(packageId);

            resStr = DAO.workflow.createClient(uData, uDocData, uAddrData);
        }catch(Exception e){
           e.printStackTrace();
           System.err.println(e.getMessage());
           System.err.println(e);
           resStr = String4CFT.setPar(resStr,"error", e.getMessage());
        }
        return resStr;
//            String state = DAO.workflow.getWorkflowState();
//            while(  !state.equals("wait-confirmation-documents-and-sms")
//                    && !state.equals("rejected")
//                    && !state.equals("wait-order-documents")
//                 ){
//                sleep(5000); // "Пять минут, Турецкий"
//                state = DAO.workflow.getWorkflowState();
//            }
            
            
//        }catch(Exception e){
//           System.err.println(e.getMessage());
//           System.err.println(e);
//            
//            resStr = String4CFT.setPar(resStr,"error", e.getMessage());
//            if (resStr.length()>=1000){
//                resStr = resStr.substring(0, 1000);
//            }else
//                resStr = String.format("%-1000s", resStr);
//
//            try {
//                oracle.jdbc.OracleConnection oraConn =
////                (oracle.jdbc.OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@test03.msk.russb.org:1521:rbotest2","ibs","12ibs");
//                 (oracle.jdbc.OracleConnection)new OracleDriver().defaultConnection();
//                
//                result = oracle.sql.BLOB.createTemporary(oraConn,
//                                                true,
//                                                oracle.sql.BLOB.DURATION_SESSION);
//                OutputStream outStream = result.setBinaryStream(1);
//                //BASE64Decoder decoder = new BASE64Decoder();
//                byte[] buf = resStr.getBytes();
//                
//                outStream.write(buf);
//                outStream.flush();
//
//            } catch (Exception ex) {
//                System.err.println(ex.getMessage());
//            }
//            
//        }
    }
    /**
     * Проверяет, что клиент ответил на смс и загружает соглашение на выпуск ЭП.
     * Если клиент не ответил на смс или смс не был отправлена, то в первых 1000
     * символах будет запись с ключом «state» (если клиент не ответил на смс)
     * или «error» (если произошла какая-то ошибка). Если все ок, то первая 1000
     * будет просто пробелами.
    */
    public static Blob GetSignatureAgreement() throws Exception {
//        statList.clear();
//        statList.add("wait-confirmation-documents-and-sms");
//        statList.add("rejected");
//        statList.add("wait-order-documents");
        
        Blob result = null;
        String resStr = "";
        try{
            // Предполагаем, что статус потока работ уже wait-confirmation-documents-and-sms
            String wfState = DAO.workflow.getWorkflowState();
            if(wfState.equals("wait-confirmation-documents-and-sms")){
//            if (wfState.equals("wait-confirmation-documents-and-sms")){
                // !! сделать отдельную функцю получения списка регистрационных документов
                if (DAO.workflow.regDocs != null)
                    System.err.println("RegDocList: " + DAO.workflow.getRegDocList());
                
                // проверим, ответил ли клиент на смс
                String smsState = DAO.workflow.getCertificateSmsState();
                if(smsState.equals("send"))
                    result = DAO.workflow.dowloadDocument(fileSchema.signatureAgreement);
                else{
                    resStr = String4CFT.setPar(resStr,"state: ", smsState);
                    resStr = String.format("%-1000s", resStr);
                    result = CreateBlob(resStr);
                }
            }else {
                resStr = String4CFT.setPar(resStr,"state: ", wfState);
                resStr = String.format("%-1000s", resStr);
                
                result = CreateBlob(resStr);
/*              try {
                    oracle.jdbc.OracleConnection oraConn =
    //                (oracle.jdbc.OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@test03.msk.russb.org:1521:rbotest2","ibs","12ibs");
                     (oracle.jdbc.OracleConnection)new OracleDriver().defaultConnection();

                    result = oracle.sql.BLOB.createTemporary(oraConn, true, oracle.sql.BLOB.DURATION_SESSION);
                    OutputStream outStream = result.setBinaryStream(1);
                    byte[] buf = resStr.getBytes();
                    outStream.write(buf);
                    outStream.flush();

                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
*/
            }
        }catch(Exception e){
           System.err.println(e.getMessage());
           System.err.println(e);
            
            resStr = String4CFT.setPar(resStr,"error", e.getMessage());
            if (resStr.length()>=1000)
                resStr = resStr.substring(0, 1000);
            else
                resStr = String.format("%-1000s", resStr);
            
            result = CreateBlob(resStr);
//            try {
//                oracle.jdbc.OracleConnection oraConn =
////                (oracle.jdbc.OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@test03.msk.russb.org:1521:rbotest2","ibs","12ibs");
//                 (oracle.jdbc.OracleConnection)new OracleDriver().defaultConnection();
//                
//                result = oracle.sql.BLOB.createTemporary(oraConn,
//                                                true,
//                                                oracle.sql.BLOB.DURATION_SESSION);
//                OutputStream outStream = result.setBinaryStream(1);
//                //BASE64Decoder decoder = new BASE64Decoder();
//                byte[] buf = resStr.getBytes();
//                
//                outStream.write(buf);
//                outStream.flush();

//            } catch (Exception ex) {
//                System.err.println(ex.getMessage());
//            }
            
        }
        return result;
    }
    
    public static String SendRegDocs(Blob passport, Blob agreement){
       
       return DAO.workflow.uploadDocument(passport, agreement);
    }
}
