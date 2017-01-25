/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import java.io.OutputStream;
//import static java.lang.Thread.sleep;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import oracle.jdbc.OracleDriver;

/**
 *
 * @author p.chavdarov
 */
public class IITDAO
{
    private final IitAuth auth;
    private final IitWorkflow workflow;
    private final IitDocumentPackageList packageList;
    private static IITDAO DAO;

    
    public IITDAO(){
        IitEntity.Init();
        auth = new IitAuth();
        this.workflow = new IitWorkflow();
        packageList = new IitDocumentPackageList();
        DAO = this;
    }
    
    private static final boolean DEBUG = false;
    
    static void log(String msg){
        if(DEBUG)
            System.out.println("[" + new Date() + "]: " + msg);
    }
    
    public static String waitWorkflowState(String wfState) throws Exception {
        int i = 0;
        
        String state = DAO.workflow.getWorkflowState();
        while(!wfState.equals(state) && i++ < 30){ // 30 сек.
//            sleep(1000); // "Пять минут, Турецкий" (секунд)
            state = DAO.workflow.getWorkflowState();
        }
        return state;
    }
    
    public static String waitWorkflowStates(String wfStates) throws Exception {
        int i = 0;
        String[] states = wfStates.split("\\|\\|");
        ArrayList<String> stateArr = new ArrayList<String>(Arrays.asList(states));
        
        String state = DAO.workflow.getWorkflowState();
        while(!stateArr.contains(state) && i++ < 30){ // 30 сек.
//            sleep(1000); // "Пять минут, Турецкий" (секунд)
            state = DAO.workflow.getWorkflowState();
        }
        return state;
    }
    
    public static String waitWorkflowState() throws Exception {
        return DAO.workflow.getWorkflowState();
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
    
    public static String ComfirmCertificateIssue() throws Exception{
        String resStr = "";
        try{
            resStr = DAO.workflow.setWorkflowState("certificate_issue");   
        }catch(Exception e){
            resStr = String4CFT.setPar(resStr, "error", e.getMessage());
        }
     
     return resStr;
    }
    
    public static String ComfirmOrder() throws Exception{
        String resStr = "";
        try{
            resStr = DAO.workflow.setWorkflowState("order-confirm");
        }catch(Exception e){
            resStr = String4CFT.setPar(resStr, "error", e.getMessage());
        }
     return resStr;
    }
    
    /**
     * Сздает Blob и помещает в первую 1000 чисволов строку source.
     * @param source
     * @return Blob
     * @throws Exception 
     */
    private static Blob CreateBlob(String source) throws Exception{
        log("creating blob...");
        log("source: " + source);
        oracle.jdbc.OracleConnection oraConn =
//                (oracle.jdbc.OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@test03.msk.russb.org:1521:rbotest2","ibs","12ibs");
         (oracle.jdbc.OracleConnection)new OracleDriver().defaultConnection();

        Blob result = oracle.sql.BLOB.createTemporary(oraConn, true, oracle.sql.BLOB.DURATION_SESSION);
        OutputStream outStream = result.setBinaryStream(1);
        byte[] buf = source.getBytes();
        outStream.write(buf);
        outStream.flush();
        return result;
    }

    public static void main(String[] args) throws Exception{
        
//////        String uData = "^~last_name~Иванов~^^~first_name~Иван~^^~middle_name~Иванович~^^~birthed~1984-08-22~^^~phone~79177978047~^^~gender~M~^";
//////        String uDocData = "^~type~internal-passport~^^~series~8005~^^~number~812008~^^~issue_code~022-005~^^~issue~Орджоникидзевский РОВД Г. УФЫ~^^~issued~2005-04-14~^";
//////        String uAddrData ="";// "^~type~permanent~^^~region~02~^^~city~Уфа~^^~street~Комсомольская~^^~house~21~^^~apartment~18~^";
//////
//////        String token = makeAuth("deprgs-demo", "321qwe654");
//////        HashMap<String, String> map = String4CFT.getMap(token);
//////        System.out.println(token);
//////        String packageList = getDocPackageList(map.get("token"));
//////        System.out.println("packageList: " + packageList);
//////        String wfId = RegisterClient(uData, uDocData, uAddrData, "35");
//////        System.out.print("workflow id: " + wfId);
//////        String wfState = waitWorkflowState();
//////        while(wfState.equals("validate-consumer-data")){
//////            sleep(10000);
//////            wfState = waitWorkflowState();
//////        }
//////        if (wfState.equals("wait-confirmation-documents-and-sms")){
//////            System.out.println(GetSignatureAgreementFIO());
//////
//////            System.out.println(SendRegDocs("signature-agreement.pdf", "signature-agreement.pdf"));
//////
//////            //log("RegDocList: " + DAO.workflow.getRegDocList());
//////            while(!waitWorkflowState().equals("wait-certificate-issue")){
//////                sleep(10000);
//////            }
//////            ComfirmCertificateIssue();
//////            while(!waitWorkflowState().equals("wait-order-documents")){
//////                sleep(10000);
//////            }
//////        }
//////        System.out.println("workflowId :" + getWorkflowId());
//////        System.out.println("docs to sign: " + getDocList());
//////        System.out.println(SendDocToSign("signature-agreement.pdf", "44"));
//////        
//////        while(!waitWorkflowState().equals("wait-order-confirmation")){
//////            sleep(10000);
//////        }
//////        ComfirmOrder();
//////        while(!waitWorkflowState().equals("order-sign")){
//////            sleep(10000);
//////        }
//////        while(!waitWorkflowState().equals("complete")){
//////            sleep(10000);
//////        }
//////        
//////        System.out.println(GetSignedDocData());
//////        
//////        makeAuth("deprgs-demo", "321qwe654");
//////        getSignedDocByWf("2860");

    }

    public static String makeAuth(String login, String password) {
        DAO = new IITDAO();
        String res = DAO.auth.makeAuth(login, password);
        return res;
        
    }
    
    public static String getRegDocList(){
        return DAO.workflow.getRegDocList();
    }
    
    public static String getDocPackageList(String token) {//throws Exception {
        if (DAO == null) DAO = new IITDAO();
        return DAO.packageList.getDocPackagesList();
    }
    
    public static String getDocList(){
        if (DAO == null) DAO = new IITDAO();
        return DAO.workflow.getDocTypeList();
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
            resStr =DAO.workflow.createWorkflow(packageId);

            DAO.workflow.createClient(uData, uDocData, uAddrData);
        }catch(Exception e){
           //e.printStackTrace();
           log(e.getMessage());
           log(e.toString());
           resStr = String4CFT.setPar(resStr,"error", e.getMessage());
        }
        return resStr;
    }
    
    /**
     * Проверяет, что клиент ответил на смс и загружает соглашение на выпуск ЭП.
     * Если клиент не ответил на смс или смс не был отправлена, то в первых 1000
     * символах будет запись с ключом «state» (если клиент не ответил на смс)
     * или «error» (если произошла какая-то ошибка). Если все ок, то первая 1000
     * будет просто пробелами.
     * @return Blob
     * @throws java.lang.Exception
    */
    public static Blob GetSignatureAgreement() throws Exception {
        
        Blob result;
        String resStr = "";
        try{
            // Предполагаем, что статус потока работ уже wait-confirmation-documents-and-sms
            if (DAO.workflow.regDocs == null)
                log("RegDocList: " + DAO.workflow.getRegDocList());
            result = DAO.workflow.GetAgreement();
        }catch(Exception e){
           log(e.getMessage());
//           e.printStackTrace();
            resStr = String4CFT.setPar(resStr,"error", e.getMessage());
            if (resStr.length()>=1000)
                resStr = resStr.substring(0, 1000);
            else
                resStr = String.format("%-1000s", resStr);
            
            result = CreateBlob(resStr);
        }
        return result;
    }
    
    public static String GetSignatureAgreementFIO() throws Exception {
        String result = null;
        String resStr = "";
        try{
            // Предполагаем, что статус потока работ уже wait-confirmation-documents-and-sms
            String wfState = DAO.workflow.getWorkflowState();
            if(wfState.equals("wait-confirmation-documents-and-sms")){
                // !! сделать отдельную функцю получения списка регистрационных документов
                if (DAO.workflow.regDocs == null)
                    log("RegDocList: " + DAO.workflow.getRegDocList());
                DAO.workflow.GetAgreement("pdf");
            }else {
                resStr = String4CFT.setPar(resStr,"state: ", wfState);
                resStr = String.format("%-1000s", resStr);
                
                result = resStr;
            }
        }catch(Exception e){
           log(e.getMessage());
           log(e.toString());
//           e.printStackTrace();
            resStr = String4CFT.setPar(resStr,"error", e.getMessage());
            if (resStr.length()>=1000)
                resStr = resStr.substring(0, 1000);
            else
                resStr = String.format("%-1000s", resStr);
            result = resStr;
        }
        return result;
    }
    
    public static String SendRegDocs(Blob passport, Blob agreement){
       return DAO.workflow.SendRegDocs(passport, agreement);
    }
    
    public static String SendRegDocs(String passport, String agreement){
       return DAO.workflow.SendRegDocs(passport, agreement);
    }
    
    public static String SendDocToSign(String doc, String docId){
       return DAO.workflow.SendDocToSign(doc, docId);
    }
    
    public static String SendDocToSign(Blob doc, String docId){
       return DAO.workflow.SendDocToSign(doc, docId);
    }
    
    public static String GetSignedDocData(){
        return DAO.workflow.getDocLinks();
    }
    
    public static Blob getSignedDocById(String docTypeId){
       Blob retBlob = null;
       try{
        retBlob =  DAO.workflow.getSignedDocByTypeId(docTypeId);
       }catch(Exception e){
        String ret = e.getMessage();
        try{
           retBlob = CreateBlob(ret);
        }catch(Exception e1){
//            e1.printStackTrace();
        }
       }
       return retBlob;
    }
    
    public static Blob getSignedDocByWf(String WfId){
       Blob retBlob = null;
       try{
        retBlob =  DAO.workflow.getSignedDocByWf(WfId);
       }catch(Exception e){
        String ret = e.getMessage();
        try{
           retBlob = CreateBlob(ret);
        }catch(Exception e1){
//            e1.printStackTrace();
        }
       }
       return retBlob;
    }
    
    public static String getWorkflowId(){
        return String4CFT.setPar("", "workflowId", String.valueOf(DAO.workflow.getId()));
    }
}
