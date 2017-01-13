/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DigitalSignatureUtils;

/**
 *
 * @author Павел
 */
public class String4CFT {
    static String setPar(String target, String key, String value){
        if (target.isEmpty())
            target = "";
        return target + "^~" + key + "~" + value + "~^";
    }
}
