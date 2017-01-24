/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author p.chavdarov
 */
public class IitWorkflowData extends IitEntity{
    protected int id;
    protected String state;
    protected String lost_state;
    protected String consumer;
    protected String certificate;
    protected String type;
    protected String limited_by;
    protected String code;
    protected String message;
    protected String packageId;
    protected String company; 
    protected int agent;
    protected IitConsumer consumerInstance;
    protected IitRegistrationDocument[] regDocs;
    protected DocTypeForSign[] docsToSign;

    IitWorkflowData(){
        //Init();
        //super();
    }
    
    IitWorkflowData(String token){
        //Init();
        SessionToken = token;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLimited_by() {
        return limited_by;
    }

    public void setLimited_by(String limited_by) {
        this.limited_by = limited_by;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getAgent() {
        return agent;
    }

    public void setAgent(int agent) {
        this.agent = agent;
    }

    public IitConsumer getConsumerInstance() {
        return consumerInstance;
    }

    public void setConsumerInstance(IitConsumer consumerInstance) {
        this.consumerInstance = consumerInstance;
    }

    public String getLost_state() {
        return lost_state;
    }

    public void setLost_state(String lost_state) {
        this.lost_state = lost_state;
    }
    
}
/**
 * Класс отражает документ, который необходимо заполнить для регистраци 
 * (генерации сертификата ЭП) клиента.
 */
class IitRegistrationDocument {
    
    private int id;

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(int id) {
        this.id = id;
    }

    private String state;

    /**
     * Get the value of state
     *
     * @return the value of state
     */
    public String getState() {
        return state;
    }

    /**
     * Set the value of state
     *
     * @param state new value of state
     */
    public void setState(String state) {
        this.state = state;
    }

        private String title;

    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param title new value of title
     */
    public void setTitle(String title) {
        this.title = title;
    }

        private String document_type;

    /**
     * Get the value of document_type
     *
     * @return the value of document_type
     */
    public String getDocument_type() {
        return document_type;
    }

    /**
     * Set the value of document_type
     *
     * @param document_type new value of document_type
     */
    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

        private String accept;

    /**
     * Get the value of accept
     *
     * @return the value of accept
     */
    public String getAccept() {
        return accept;
    }

    /**
     * Set the value of accept
     *
     * @param accept new value of accept
     */
    public void setAccept(String accept) {
        this.accept = accept;
    }

        private String path;

    /**
     * Get the value of path
     *
     * @return the value of path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the value of path
     *
     * @param path new value of path
     */
    public void setPath(String path) {
        this.path = path;
    }

}


class IitConsumer {
    private String id;
    private String state;
    private String last_name;
    private String first_name;
    private String middle_name;
    private String gender;
    private String birthed;
    private String phone;
    private String email;
    private String snils;
    private String inn;
    protected List<IitIdentity> identities;
    protected List<IitAddress> addresses;

    public IitConsumer() {
        identities = new ArrayList<IitIdentity>();
        addresses = new ArrayList<IitAddress>();
    }

    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthed() {
        return birthed;
    }

    public void setBirthed(String birthed) {
        this.birthed = birthed;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

//    public IitIdentity[] getIdentities() {
//        return identities;
//    }
//
//    public void setIdentities(IitIdentity[] identities) {
//        this.identities = identities;
//    }
//
//    public IitAddress[] getAddresses() {
//        return addresses;
//    }
//
//    public void setAddresses(IitAddress[] addresses) {
//        this.addresses = addresses;
//    }
}


class IitAddress {
    private String type;
    private String region;
    private String city;
    private String street;
    private String house;
    private String apartment;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }
    
}


class IitIdentity {
    private String type;
    private String state;
    private String series;
    private String number;
    private String issue;
    private String issue_code;
    private String issued;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getIssue_code() {
        return issue_code;
    }

    public void setIssue_code(String issue_code) {
        this.issue_code = issue_code;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }
}


class DocTypeForSign{
    private String id;
    private String title;
    private Boolean required;
    private Boolean unlimited;
    protected ArrayList<DocProperty> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getUnlimited() {
        return unlimited;
    }

    public void setUnlimited(Boolean unlimited) {
        this.unlimited = unlimited;
    }
   
}

class DocProperty{
    private String id;
    private String title;
    private String type;
    private String error_message;
    private String regular_expression_value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getRegular_expression_value() {
        return regular_expression_value;
    }

    public void setRegular_expression_value(String regular_expression_value) {
        this.regular_expression_value = regular_expression_value;
    }
}
class DocToSign{
    private String id;
    private String state;
    private String path;
    private String path_with_signature_stamp;
    private String signature_path;
    private String company_signature_path;
    private String type;
    private String document;
    protected ArrayList<DocProperty> properties;
    private String sub_type;
    private String document_title;
    private String report_url;
    private String sms_state;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath_with_signature_stamp() {
            return path_with_signature_stamp;
        }

        public void setPath_with_signature_stamp(String path_with_signature_stamp) {
            this.path_with_signature_stamp = path_with_signature_stamp;
        }

        public String getSignature_path() {
            return signature_path;
        }

        public void setSignature_path(String signature_path) {
            this.signature_path = signature_path;
        }

        public String getCompany_signature_path() {
            return company_signature_path;
        }

        public void setCompany_signature_path(String company_signature_path) {
            this.company_signature_path = company_signature_path;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }

        public String getSub_type() {
            return sub_type;
        }

        public void setSub_type(String sub_type) {
            this.sub_type = sub_type;
        }

        public String getDocument_title() {
            return document_title;
        }

        public void setDocument_title(String document_title) {
            this.document_title = document_title;
        }

        public String getReport_url() {
            return report_url;
        }

        public void setReport_url(String report_url) {
            this.report_url = report_url;
        }

        public String getSms_state() {
            return sms_state;
        }

        public void setSms_state(String sms_state) {
            this.sms_state = sms_state;
        }
        
}
