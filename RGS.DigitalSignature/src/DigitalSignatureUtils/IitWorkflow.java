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
    
    public String createWorkflow(int docPackageId){
        System.out.println("Creating workflow...");
        
        this.method = "POST";
        this.uri = "api/workflow";
        
        String res= "";
        this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        String json_str = String.format("{\"package\":%s}", docPackageId);
        
        
        try{
            iitConn = new IITConnection(this.url_str, this.method, "application/json");
            int i = iitConn.sendData(json_str);
            if (i==0){
                this.initialise(gson.fromJson(iitConn.getData(), IitWorkflow.class));
                this.setPackageId(docPackageId);
                
//                System.out.println(String.format("Workflow state: %s", this.getState()));
//                System.out.println(String.format("Workflow package: %s", this.getPackageId()));
//                System.out.println(String.format("Workflow message: %s", this.getMessage()));
                res = Integer.toString(this.getId()); //"Authentication result: token passed";
            }
            else
                res = null;
//            System.out.println(String.format("Connection result: %s",Integer.toString(i)));
        }catch(Exception ex){
            Logger.getLogger("http_conn").log(Level.SEVERE, null, ex);
            res = ex.toString();
        }

        
        return res;
    }
    
    public int createClient(
//        String last_name,
//        String first_name,
//        String middle_name,
//        String gender,
//        String birthed,
//        String phone,
//
//        String dco_type,
//        String doc_series,
//        String doc_num,
//        String issue_code,
//        String issuer,
//        String issued,
//
//        String addr_type,
//        String addr_region,
//        String addr_city,
//        String addr_street,
//        String addr_house,
//        String addr_apartment

            String uData, String uDocData, String uAddrData
    ) throws Exception{
        System.out.println("Creating client...");
        
//        String url = "https://iitcloud-demo.iitrust.ru";
        this.method = "POST";
        this.uri = String.format("api/workflow/%s/consumer/", this.getId());
        
        int res;// = 0;
        String url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        String responce = "";
        IitConsumer consumer_data = new IitConsumer();

        String[] userData = uData.split("~");
        String[] docsData = uDocData.split("\\|\\|");
        String[] addrsData = uAddrData.split("\\|\\|");

        consumer_data.setLast_name(userData[0]);
        consumer_data.setFirst_name(userData[1]);
        consumer_data.setMiddle_name(userData[2]);
        consumer_data.setGender(userData[5]);
        consumer_data.setBirthed(userData[3]);
        consumer_data.setPhone(userData[4]);
//        consumer_data.setSnils(userData[6]);


        for(String doc : docsData) {
            String[] docData = doc.split("~");
            IitIdentity identity = new IitIdentity();
            identity.setType(docData[0]);
            identity.setSeries(docData[1]);
            identity.setNumber(docData[2]);
            identity.setIssue_code(docData[3]);
            identity.setIssue(docData[4]);
            identity.setIssued(docData[5]);
            consumer_data.identities.add(identity);
        }

        for(String addr : addrsData) {
            String[] addrData = addr.split("~");
            IitAddress address = new IitAddress();
            address.setType(addrData[0]);
            address.setRegion(addrData[1]);
            address.setCity(addrData[2]);
            address.setStreet(addrData[3]);
            address.setHouse(addrData[4]);
            address.setApartment(addrData[5]);
            consumer_data.addresses.add(address);
        }

        String json_str = gson.toJson(consumer_data, IitConsumer.class);
        System.out.println("Client json: "+json_str);
        iitConn = new IITConnection(url_str, method, "application/json");
        int i = iitConn.sendData(json_str);
        if (i==0){
            //System.out.println("Consumer send...");
            responce = iitConn.getData();
            res = 0;

        }
        else
            res = 1;
//            System.out.println(String.format("Connection result: %s",Integer.toString(i)));
        
        this.consumerInstance = consumer_data;
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
        System.out.println("Getting workflow status...");
        
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
        

        int i = iitConn.sendData(json_str);
        if (i==0){
            this.initialise(gson.fromJson(iitConn.getData(), IitWorkflow.class));

//                System.out.println(String.format("Workflow state: %s", this.getState()));
//                System.out.println(String.format("Workflow package: %s", this.getPackageId()));
//                System.out.println(String.format("Workflow message: %s", this.getMessage()));
            //ret = Integer.toString(this.getId()); //"Authentication result: token passed";
        }
//            else
            //res = null;
//            System.out.println(String.format("Connection result: %s",Integer.toString(i)));

        
        return getWorkflowState();
    }
    
    public String getRegDocList() throws Exception{
        System.out.println("Getting client info...");
        
        //String url = "https://iitcloud-demo.iitrust.ru";
        
        this.method = "GET";
        this.uri = String.format("api/workflow/%s/certificate/file/", id);
        java.lang.reflect.Type docsArrType = new TypeToken<IitRegisrationDocument[]>() {}.getType();
        
        
        String responce;
        String ret = "";
        this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        iitConn = new IITConnection(this.url_str, method, "application/json");
        
        responce = iitConn.getData();
//            System.out.println(String.format("request result: %s", res));
        this.regDocs = gson.fromJson(responce, docsArrType);
//            System.out.println(String.format("Workflow package: %s", this.getPackageId()));
        ret = responce;//this.consumerInstance.getState();

        return ret;

        
    }
 
    public Blob dowloadDocument(fileSchema docType) throws Exception{
        this.method = "GET";
        String schema_url = "";
        String fileName = "";
        Blob ret = null;
        int docId = 0;
        switch(docType){
            case signatureAgreement:
                schema_url = "signature-agreement";
                for (IitRegisrationDocument doc : this.regDocs){
                    if (schema_url.equals(doc.getDocument_type())){
                        docId = doc.getId();
                        break;
                    }
                }
                fileName = "signature_agreement_"+ String.valueOf(docId)+".pdf";
                break;
            case certificateReportOps:
                schema_url = "certificate-report-ops";
                fileName = "certificate-report-ops.pdf";
                break;
            case certificateReportOpsHsm:
                schema_url = "certificate-report-ops-hsm";
                fileName = "certificate-report-ops-hsm.pdf";
                break;
        }
        
        this.uri = String.format("api/workflow/%s/report/%s", this.id, schema_url);
        this.url_str = String.format("%s/%s?token=%s", this.url, this.uri, this.SessionToken);
        
        iitConn = new IITConnection(this.url_str, method, "multipart/form-data");

        ret = iitConn.getFile(fileName);

        return ret;
        
        
    }

}
