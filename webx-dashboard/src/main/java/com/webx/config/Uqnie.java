package com.webx.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.json.simple.JSONObject;

public class Uqnie {
    public static void saveUQnieInJsonFile(String filename) {
        try {
            Object obj = new JSONObject();
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream objOut = new ObjectOutputStream(fos);
            objOut.writeObject(obj);
        fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Uqnie readUQnieInJsonFile(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            
            fis.close();
            return new Uqnie(); 
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
