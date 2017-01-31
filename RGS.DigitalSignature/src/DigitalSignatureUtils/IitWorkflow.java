/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DigitalSignatureUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.sql.Blob;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


/**
 *
 * @author Павел
 */
public class IitWorkflow extends IitWorkflowData{

    IitWorkflow(String token) {
        super(token);
    }

    IitWorkflow(){
        super();
    }
    
    enum fileSchema{
        signatureAgreement,
        certificateReportOps,
        certificateReportOpsHsm
    }
    
    private void initialise(IitWorkflow wf){
        log("Initialising...");
        if (wf.getAgent() != 0)
            this.agent = wf.agent;
        if (wf.certificate != null)
            this.certificate = wf.certificate;
        if (wf.code != null)    
            this.code = wf.code;
        if (wf.company!=null)
            this.company = wf.company;
        if (wf.consumer != null)
            this.consumer = wf.consumer;
        if (wf.id != 0)
            this.id = wf.id;
        if (wf.limited_by != null)
            this.limited_by = wf.limited_by;
        if (wf.lost_state != null)
            this.lost_state = wf.lost_state;
        if (wf.message != null)
            this.message = wf.message;
        if (wf.packageId != null)
            this.packageId = wf.packageId;
        if (wf.state != null)
            this.state = wf.state;
        if (wf.type != null)
            this.type =wf.type;
     
    };
    
    public String createWorkflow(String docPackageId) throws Exception {
        log("Creating workflow...");
        
        this.method = "POST";
        this.uri = "api/workflow";
        
        String res= "";
        this.url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);
        String json_str = String.format("{\"package\":%s}", docPackageId);
        
        iitConn = new IITConnection(this.url_str, this.method, "application/json");
        iitConn.sendData(json_str);
        this.initialise(gson.fromJson(iitConn.getData(), IitWorkflow.class));
        this.setPackageId(docPackageId);
        //res = String.valueOf(this.getId()); //"Authentication result: token passed";

        res = String4CFT.setPar(res, "workflowId", String.valueOf(this.getId()));
        return res;
    }
    // возвращает id зарегистрированного клиента
    public String createClient(String uData, String uDocData, String uAddrData) throws Exception{
        log("Creating client...");
        this.method = "POST";
        this.uri = String.format("api/workflow/%s/consumer/", this.getId());
        
        url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);

        IitConsumer consumer_data = new IitConsumer();

        String[] docsData = uDocData.split("\\|\\|");
        String[] addrsData = uAddrData.split("\\|\\|");

        HashMap<String, String> map = String4CFT.getMap(uData);
        
        consumer_data.setLast_name(map.get("last_name"));
        consumer_data.setFirst_name(map.get("first_name"));
        consumer_data.setMiddle_name(map.get("middle_name"));
        consumer_data.setGender(map.get("gender"));
        consumer_data.setBirthed(map.get("birthed"));
        consumer_data.setPhone(map.get("phone"));
        consumer_data.setSnils(map.get("snils"));


        for(String doc : docsData) {
            if(doc.isEmpty()) continue;
            
            map = String4CFT.getMap(doc);
            IitIdentity identity = new IitIdentity();
            identity.setType(map.get("type"));
            identity.setSeries(map.get("series"));
            identity.setNumber(map.get("number"));
            identity.setIssue_code(map.get("issue_code"));
            identity.setIssue(map.get("issue"));
            identity.setIssued(map.get("issued"));
            consumer_data.identities.add(identity);
        }

        for(String addr : addrsData) {
            if(addr.isEmpty()) continue;
            
            map = String4CFT.getMap(addr);
            IitAddress address = new IitAddress();
            address.setType(map.get("type"));
            address.setRegion(map.get("region"));
            address.setCity(map.get("city"));
            address.setStreet(map.get("street"));
            address.setHouse(map.get("house"));
            address.setApartment(map.get("apartmen"));
            consumer_data.addresses.add(address);
        }

        String json_str = gson.toJson(consumer_data, IitConsumer.class);
        log("Client json: "+json_str);
        iitConn = new IITConnection(url_str, method, "application/json");
        iitConn.sendData(json_str);
        log("Consumer send...");

        String response = iitConn.getData();
        log("Consumer response: " + response);

        this.consumerInstance = gson.fromJson(response, IitConsumer.class);//consumer_data;
        
        return this.consumerInstance.getId();

    }
 
    // ???
    public String getClientInfo() throws Exception{
        log("Getting client info...");
        
        this.method = "GET";
        this.uri = String.format("api/workflow/%s/consumer/", id);
        
        String responce;// = "";

        this.url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);
        iitConn = new IITConnection(url_str, this.method, "application/json");

        responce = iitConn.getData();
        this.consumerInstance = gson.fromJson(responce, IitConsumer.class);
        return this.consumerInstance.getState();
    }
    
    public String getWorkflowState() throws Exception{
        log("Getting workflow state...");
        
        this.method = "GET";
        this.uri = String.format("api/workflow/%s", this.getId());
        
        String responce;
        this.url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);
        iitConn = new IITConnection(url_str, method, "application/json");
        

        responce = iitConn.getData();
        this.initialise(gson.fromJson(responce, IitWorkflow.class));
        String ret = this.getState();
        log("Workflow status: " + ret);

        return ret;
    }
    // ???
    public String setWorkflowState(String status) throws Exception{
        log("Setting workflow state '"+status+"'");
        
        this.method = "PUT";
        this.uri = String.format("api/workflow/%s", this.id);
        
        this.url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);
        String json_str = String.format("{\"state\":\"%s\"}", status);
        
        iitConn = new IITConnection(url_str, method, "application/json");

        iitConn.sendData(json_str);
        this.initialise(gson.fromJson(iitConn.getData(), IitWorkflow.class));

        return getWorkflowState();
    }
    
    public String getRegDocList(){
        log("Getting registration docs info...");
        String responce;
        String ret = "";
        
        try{
            this.method = "GET";
            this.uri = String.format("api/workflow/%s/certificate/file/", id);
            java.lang.reflect.Type docsArrType = new TypeToken<IitRegistrationDocument[]>() {}.getType();

            this.url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);
            iitConn = new IITConnection(this.url_str, method, "application/json");

            responce = iitConn.getData();
            this.regDocs = gson.fromJson(responce, docsArrType);

            for(IitRegistrationDocument regDoc: this.regDocs){
                ret = String4CFT.setPar(ret, "accept", regDoc.getAccept());
                ret = String4CFT.setPar(ret, "document_type", regDoc.getDocument_type());
                ret = String4CFT.setPar(ret, "path", regDoc.getPath());
                ret = String4CFT.setPar(ret, "state", regDoc.getState());
                ret = String4CFT.setPar(ret, "title", regDoc.getTitle());
                ret = String4CFT.setPar(ret, "id", String.valueOf(regDoc.getId()));
                ret += "||";
            }
        }catch(Exception e){
            ret = String4CFT.setPar(ret, "error", e.getMessage());
        }
        return ret;
    }
    
    public String getDocTypeList() {
        log("Getting docs info...");
        
        this.method = "GET";
        this.uri = String.format("api/workflow/%s/document/file", id);
        java.lang.reflect.Type docsArrType = new TypeToken<DocTypeForSign[]>() {}.getType();
        
        String responce;
        String ret = "";
        this.url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);
        try{
            iitConn = new IITConnection(this.url_str, method, "application/json");

            responce = iitConn.getData();
            this.docsToSign = gson.fromJson(responce, docsArrType);
            log(String.format("DocList: %s", responce));
    
            for(DocTypeForSign doc: docsToSign){
                ret = String4CFT.setPar(ret, "id", doc.getId());
                ret = String4CFT.setPar(ret, "title", doc.getTitle());
                ret = String4CFT.setPar(ret, "required", String.valueOf(doc.getRequired()));
                ret = String4CFT.setPar(ret, "unlimited", String.valueOf(doc.getUnlimited()));
                ret += "||";
            }
        }catch(Exception e){
            ret = String4CFT.setPar(ret, "error", e.getMessage());
        }
        return ret;
    }
 
    public String getCertificateSmsState() throws Exception{
        log("Getting sms state...");
        method = "GET";
        
        String smsId = "";
        // поиск id sms
        for (IitRegistrationDocument regDoc : regDocs) {
            if (regDoc.getDocument_type().equals("sms")) {
                smsId = String.valueOf(regDoc.getId());
                break;
            }
        }
        
        uri = String.format("api/workflow/%s/certificate/sms/%s", this.getId(), smsId);
        url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);
        iitConn = new IITConnection(url_str, method, "application/json");
        IitRegistrationDocument sms = gson.fromJson(iitConn.getData(), IitRegistrationDocument.class);
        log("sms state: " + sms.getState());
        return sms.getState();
    }
    
    public Blob GetAgreement() throws Exception{
        this.method = "GET";
        final String schema_url = "signature-agreement";
        
        this.uri = String.format("api/workflow/%s/report/%s", this.id, schema_url);
        this.url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);
        
        iitConn = new IITConnection(this.url_str, method, "");

        Blob ret = iitConn.getFile();

        return ret;
    }
    
    public void GetAgreement(String fileSuffix) throws Exception{
        this.method = "GET";
        final String schema_url = "signature-agreement";
        
        this.uri = String.format("api/workflow/%s/report/%s", this.id, schema_url);
        this.url_str = String.format("%s/%s?token=%s", IitWorkflow.URL, this.uri, IitWorkflow.SessionToken);
        
        iitConn = new IITConnection(this.url_str, method, "");
        iitConn.getFileIO(schema_url + "." + fileSuffix);
        
    }
    
    /**
     * Загружает сканы pdf паспорта и согласия на выпуск ЭП на сервер IITrust
     * @param passport
     * @param agreement
     * @return String
     * 
     * В случаем успеха строка с полями "passport" и "agreement"
     * (статус документа).В случаем ошибка -- строку с полем "error" и текстом
     * ошибки.
     */
    public String SendRegDocs(Blob passport, Blob agreement) {//throws Exception{
        String ret = "";
        try{
            for (IitRegistrationDocument regDoc : regDocs) {
                if (regDoc.getDocument_type().equals("internal-passport")) {
                    this.uri = String.format("/api/workflow/%s/certificate/file/%s?token=%s", this.id, regDoc.getId(), IitWorkflow.SessionToken);
                    AConn.initConnection(uri, "PUT", "multipart/form-data");
                    ret += AConn.sendRegDoc(passport) + "||";
                }
                if (regDoc.getDocument_type().equals("signature-agreement")) {
                    this.uri = String.format("/api/workflow/%s/certificate/file/%s?token=%s", this.id, regDoc.getId(), IitWorkflow.SessionToken);
                    AConn.initConnection(uri, "PUT", "multipart/form-data");
                    ret += AConn.sendRegDoc(agreement) + "||";
                }
            }
        }catch(Exception e){
            ret = String4CFT.setPar(ret, "error", e.getMessage());
            //e.printStackTrace();
        }
        return ret;
    } 
    
    public String SendRegDocs(String passport, String agreement) {
        String ret = "";
        try{
            for (IitRegistrationDocument regDoc : regDocs) {
                if (regDoc.getDocument_type().equals("internal-passport")) {
                    this.uri = String.format("/api/workflow/%s/certificate/file/%s?token=%s", this.id, regDoc.getId(), IitWorkflow.SessionToken);
                    AConn.initConnection(uri, "PUT", "multipart/form-data");
                    ret += AConn.sendRegDoc(passport) + "||";
                }
                if (regDoc.getDocument_type().equals("signature-agreement")) {
                    this.uri = String.format("/api/workflow/%s/certificate/file/%s?token=%s", this.id, regDoc.getId(), IitWorkflow.SessionToken);
                    AConn.initConnection(uri, "PUT", "multipart/form-data");
                    ret += AConn.sendRegDoc(agreement) + "||";
                }
            }
        }catch(Exception e){
            ret = String4CFT.setPar(ret, "error", e.getMessage());
        }
        return ret;
    }
    
    public String SendDocToSign(String docName, String... doc_parameters){
        this.method = "POST";
        String ret = "";
        this.uri = String.format("/api/workflow/%s/file/?token=%s", this.id, IitWorkflow.SessionToken);
        try{
            HashMap<String, String> props = new HashMap<String, String>();
            
            for(DocTypeForSign d: docsToSign){
                if(d.getId().equals(doc_parameters[0])){
                    int i = 1;
                    for(DocProperty prop: d.properties)
                        props.put(prop.getId(), doc_parameters[i++]);
                }
                    
            }
            AConn.initConnection(uri, "POST", "multipart/form-data");
            ret = AConn.sendDocWithProps(docName, doc_parameters[0], props);
        
        }catch(Exception e){
            ret = String4CFT.setPar(ret, "error", e.getMessage());
        }
        return ret;
    }
    // doc_parameters[0] -- doc_id, doc_parameters[1..] -- document properties
    public String SendDocToSign(Blob doc, String... doc_parameters){
        this.method = "POST";
        String ret = "";
        this.uri = String.format("/api/workflow/%s/file/?token=%s", this.id, IitWorkflow.SessionToken);
        try{
            
            HashMap<String, String> props = new HashMap<String, String>();
            
            for(DocTypeForSign d: docsToSign){
                if(d.getId()==doc_parameters[0]){
                    int i = 1;
                    for(DocProperty prop: d.properties)
                        props.put(prop.getId(), doc_parameters[i++]);
                }
                    
            }
            
            AConn.initConnection(uri, "POST", "multipart/form-data");
            ret = AConn.sendDocWithProps(doc, doc_parameters[0], props);
        
        }catch(Exception e){
            ret = String4CFT.setPar(ret, "error", e.getMessage());
        }
        return ret;
    }
    
    public String getDocLinks(){
        String ret = "";
        String retJson;
        DocToSign signedDoc;
        this.uri = String.format("/api/workflow/%s/file/?token=%s", this.id, IitWorkflow.SessionToken);
        try{
            AConn.initConnection(uri, "GET", "multipart/form-data");
            retJson = AConn.getDocData();
            JsonParser parser = new JsonParser();
            JsonArray jArray = parser.parse(retJson).getAsJsonArray();
            Iterator it = jArray.iterator();
            while(it.hasNext()){
                signedDoc = gson.fromJson((JsonElement)it.next(), DocToSign.class);
                if(signedDoc.getType().equals("document")){
                    ret = String4CFT.setPar(ret, "id", signedDoc.getId());
                    ret = String4CFT.setPar(ret, "state", signedDoc.getState());
                    ret = String4CFT.setPar(ret, "path", signedDoc.getPath().replace("https", "http"));
                    ret = String4CFT.setPar(ret, "path_with_signature_stamp", 
                                        signedDoc.getPath_with_signature_stamp().replace("https", "http"));
                    ret = String4CFT.setPar(ret, "signature_path", signedDoc.getSignature_path().replace("https", "http"));
                    ret = String4CFT.setPar(ret, "document", signedDoc.getDocument());
                    ret = String4CFT.setPar(ret, "document_title", signedDoc.getDocument_title());
                    ret += "||";
                }
            }
        }catch(Exception e){
            ret = String4CFT.setPar(ret, "error", e.getMessage());
        }
        return ret;
        
    }
    
    public Blob getSignedDocByTypeId(String DocTypeId)throws Exception{
        Blob retBlob = null;
        DocToSign signedDoc = null;
        
        this.uri = String.format("/api/workflow/%s/file/?token=%s", this.id, IitWorkflow.SessionToken);

        AConn.initConnection(uri, "GET", "");
        String retJson = AConn.getDocData();
        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(retJson).getAsJsonArray();
        Iterator it = jArray.iterator();
        while(it.hasNext()){
            JsonObject jo = ((JsonElement)it.next()).getAsJsonObject();
            if (jo.get("document").getAsString().equals(DocTypeId)){
                AConn.initConnection("","","");
                String path = jo.get("path_with_signature_stamp").getAsString();
                retBlob = AConn.getFile(path.replace("https://", "http://"));
            }
        }

        return retBlob;
    }
    
    public Blob getSignedDocByWf(String WfId) throws Exception{
        Blob retBlob = null;
        
        this.uri = String.format("/api/workflow/%s/file/?token=%s", WfId, SessionToken);
        AConn.initConnection(uri, "GET", "");
        String retJson = AConn.getDocData();
        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(retJson).getAsJsonArray();
        Iterator it = jArray.iterator();
        
        while(it.hasNext()){
            JsonObject jo = ((JsonElement)it.next()).getAsJsonObject();
            if (jo.get("type").getAsString().equals("document") && jo.get("document").getAsString().equals("44")){
                AConn.initConnection("","","");
                retBlob = AConn.getFile(jo.get("path_with_signature_stamp").getAsString().replace("https://", "http://"));
            }
        }
        return retBlob;
    }
}
