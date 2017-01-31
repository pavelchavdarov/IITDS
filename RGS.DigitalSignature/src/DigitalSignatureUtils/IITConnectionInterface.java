/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import java.sql.Blob;
import java.util.Map;

/**
 *
 * @author p.chavdarov
 */
interface IITConnectionInterface{
  public void initConnection(String pUrl, String pMethod, String pContentType) throws Exception;
  public void sendData(String pData) throws Exception;
  public String getData() throws Exception;
  public void sendFile(Blob pBlob) throws Exception;
  public void sendFileIO(String fileName) throws Exception;
  public String sendRegDoc(String fileName) throws Exception;
  public Blob getFile() throws Exception;
  public Blob getFile(String url) throws Exception;
  public void getFileIO(String fileName) throws Exception;
  public String sendDoc(String fileName, String docId) throws Exception;
  public String sendDocWithProps(String fileName, String docId, Map<String,String> props) throws Exception;
  public String sendDoc(Blob file, String docId) throws Exception;
  public String sendDocWithProps(Blob file, String docId, Map<String,String> props) throws Exception;
  
}