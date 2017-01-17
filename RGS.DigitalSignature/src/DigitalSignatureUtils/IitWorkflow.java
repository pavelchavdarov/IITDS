/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DigitalSignatureUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    
    
    private void initialise(IitWorkflow wf){
//        System.out.println("Initialising...");
        if (wf.getAgent() != 0)
            this.agent = wf.agent;
        if (wf.certificate != null)
            this.certificate = wf.certificate;
        if (wf.code != null)    
            this.code = wf.code;
        if (wf.company != 0)
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
        if (wf.packageId != 0)
            this.packageId = wf.packageId;
        if (wf.state != null)
            this.state = wf.state;
        if (wf.type != null)
            this.type =wf.type;
     
    };
    
    public String createWorkflow(int docPackageId) throws Exception {
        System.err.println("Creating workflow...");
        
        this.method = "POST";
        this.uri = "api/workflow";
        
        String res= "";
        this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        String json_str = String.format("{\"package\":%s}", docPackageId);
        
        
//        try{
            iitConn = new IITConnection(this.url_str, this.method, "application/json");
            iitConn.sendData(json_str);
            this.initialise(gson.fromJson(iitConn.getData(), IitWorkflow.class));
            this.setPackageId(docPackageId);
            res = String.valueOf(this.getId()); //"Authentication result: token passed";

//            System.out.println(String.format("Connection result: %s",Integer.toString(i)));
//        }catch(Exception ex){
//            Logger.getLogger("http_conn").log(Level.SEVERE, null, ex);
//            res = ex.toString();
//        }
        System.err.println("Wokflow created: " + res);

        return res;
    }
    
    public int createClient(String uData, String uDocData, String uAddrData) throws Exception{
        System.err.println("Creating client...");
        System.err.println("uData: " + uData);
        System.err.println("uDocData: " + uDocData);
        System.err.println("uAddrData: " + uAddrData);
        
//        String url = "https://iitcloud-demo.iitrust.ru";
        this.method = "POST";
        this.uri = String.format("api/workflow/%s/consumer/", this.getId());
        
        int res;// = 0;
        String url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        String response = "";
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
        System.err.println("Client json: "+json_str);
        iitConn = new IITConnection(url_str, method, "application/json");
        iitConn.sendData(json_str);
        System.err.println("Consumer send...");

        response = iitConn.getData();
        System.err.println("Consumer response: " + response);
        res = 0;

        this.consumerInstance = gson.fromJson(response, IitConsumer.class);//consumer_data;
        return res;
    }
 
    // ???
    public String getClientInfo() throws Exception{
        System.out.println("Getting client info...");
        
//        String url = "https://iitcloud-demo.iitrust.ru";
        this.method = "GET";
        this.uri = String.format("api/workflow/%s/consumer/", id);
        
        String responce;// = "";
        String ret = "";
        this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        iitConn = new IITConnection(url_str, this.method, "application/json");

        responce = iitConn.getData();
//            System.out.println(String.format("request result: %s", res));
        this.consumerInstance = gson.fromJson(responce, IitConsumer.class);
//            System.out.println(String.format("Workflow package: %s", this.getPackageId()));
        ret = this.consumerInstance.getState();

        return ret;
    }
    
    public String getWorkflowState() throws Exception{
        System.out.println("Getting workflow state...");
        
        //String url = "https://iitcloud-demo.iitrust.ru";
        this.method = "GET";
        this.uri = String.format("api/workflow/%s", this.getId());
        
        String responce;// = "";
        String ret = "";
        this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        iitConn = new IITConnection(url_str, method, "application/json");
        

        responce = iitConn.getData();
//            System.out.println(String.format("request result: %s", res));
        this.initialise(gson.fromJson(responce, IitWorkflow.class));
//            System.out.println(String.format("Workflow package: %s", this.getPackageId()));
        ret = this.getState();
        System.out.println("Workflow status: " + ret);

        return ret;
    }
    // ???
    public String setWorkflowState(String status) throws Exception{
        System.out.println("Setting workflow state '"+status+"'");
        
//        String url = "https://iitcloud-demo.iitrust.ru";
        this.method = "PUT";
        this.uri = String.format("api/workflow/%s", this.id);
        
        String responce;// = "";
        String ret;// = "";
        this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        String json_str = String.format("{\"state\":%s}", status);
        
        iitConn = new IITConnection(url_str, method, "application/json");
        

        iitConn.sendData(json_str);
        this.initialise(gson.fromJson(iitConn.getData(), IitWorkflow.class));

        return getWorkflowState();
    }
    
    public String getRegDocList() throws Exception{
        System.out.println("Getting registration docs info...");
        
        //String url = "https://iitcloud-demo.iitrust.ru";
        
        this.method = "GET";
        this.uri = String.format("api/workflow/%s/certificate/file/", id);
        java.lang.reflect.Type docsArrType = new TypeToken<IitRegistrationDocument[]>() {}.getType();
        
        
        String responce;
        String ret = "";
        this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        iitConn = new IITConnection(this.url_str, method, "application/json");
        
        responce = iitConn.getData();
//            System.out.println(String.format("request result: %s", res));
        this.regDocs = gson.fromJson(responce, docsArrType);
//            System.out.println(String.format("Workflow package: %s", this.getPackageId()));
        
        for(IitRegistrationDocument regDoc: this.regDocs){
            ret = String4CFT.setPar(ret, "accept", regDoc.getAccept());
            ret = String4CFT.setPar(ret, "document_type", regDoc.getDocument_type());
            ret = String4CFT.setPar(ret, "path", regDoc.getPath());
            ret = String4CFT.setPar(ret, "state", regDoc.getState());
            ret = String4CFT.setPar(ret, "title", regDoc.getTitle());
            ret = String4CFT.setPar(ret, "id", String.valueOf(regDoc.getId()));
            ret += "||";
        }
        //ret = responce;//this.consumerInstance.getState();

        return ret;

        
    }
 
    public Blob dowloadDocument(fileSchema docType) throws Exception{
        this.method = "GET";
        String schema_url = "";
        Blob ret = null;
        switch(docType){
            case signatureAgreement:
                schema_url = "signature-agreement";
                break;
            case certificateReportOps:
                schema_url = "certificate-report-ops";
                break;
            case certificateReportOpsHsm:
                schema_url = "certificate-report-ops-hsm";
                break;
        }
        
        this.uri = String.format("api/workflow/%s/report/%s", this.id, schema_url);
        this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        
        iitConn = new IITConnection(this.url_str, method, "");

        ret = iitConn.getFile();

        return ret;
    }

    public String uploadDocument(Blob passport, Blob agreement) {//throws Exception{
        this.method = "PUT";
        String ret = "";
        try{
            for(int i = 0; i < regDocs.length; i++){
                if(regDocs[i].getDocument_type().equals("internal-passport")){
                    this.uri = String.format("api/workflow/%s/certificate/file/%s", this.id, regDocs[i].getId());
                    this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
                    iitConn = new IITConnection(this.url_str, method, "multipart/form-data");
                    iitConn.sendFile(passport);
                    regDocs[i] = gson.fromJson(iitConn.getData(), IitRegistrationDocument.class);
                    ret = String4CFT.setPar(ret, "passport", "OK");
                }else if(regDocs[i].getDocument_type().equals("signature-agreement")){
                    this.uri = String.format("api/workflow/%s/certificate/file/%s", this.id, regDocs[i].getId());
                    this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
                    iitConn = new IITConnection(this.url_str, method, "multipart/form-data");
                    iitConn.sendFile(agreement);
                    regDocs[i] = gson.fromJson(iitConn.getData(), IitRegistrationDocument.class);
                    ret = String4CFT.setPar(ret, "agreement", "OK");
                }
            }
        }catch(Exception e){
            ret = String4CFT.setPar(ret, "error", e.getMessage());
        }
        return ret;
    } 
    
}
