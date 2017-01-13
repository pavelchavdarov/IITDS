/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DigitalSignatureUtils;

import java.util.HashMap;

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
    
    static HashMap<String, String> getMap(String source){
        HashMap<String, String> result = new HashMap<String, String>();
        result.clear();

        if (null == source) return result;

        String[] parts = source.split("\\^~");

        for (Integer i = 0; i < parts.length; i++) {
            if (parts [i].isEmpty()) continue;

            String[] keyVal = parts [i].split ("~");

            if (keyVal.length == 0) continue;

            result.put(keyVal[0], keyVal[1]);
        }
        return result;
    }
    
}
