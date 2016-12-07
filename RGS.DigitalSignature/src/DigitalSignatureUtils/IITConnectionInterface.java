/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DigitalSignatureUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 *
 * @author p.chavdarov
 */
interface IITConnectionInterface{
  public HttpURLConnection getConnection(String pUrl, String pMethod, String pContentType);
  public int sendData(String pData) throws IOException;
  public String getData() throws IOException;
}