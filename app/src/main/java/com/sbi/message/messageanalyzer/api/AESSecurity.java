package com.sbi.message.messageanalyzer.api;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class AESSecurity {

    private static String secretKey = "$$SBIMessageAnalyzer$$";
    private static String salt = "$$internalAudit$$";

    public String getSecretKey(){
        return secretKey;

    }
    public static String encrypt(String strToEncrypt, String secret){
        try{
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
//            }else{
//                return android.util.Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),1);
//            }
            return android.util.Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),1);
        }
        catch (Exception e){
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }


    public static String decrypt(String strToDecrypt, String secret) {
        try{
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
//            }else{
//                return new String(cipher.doFinal(android.util.Base64.decode(strToDecrypt,1)));
//            }
            return new String(cipher.doFinal(android.util.Base64.decode(strToDecrypt,1)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
