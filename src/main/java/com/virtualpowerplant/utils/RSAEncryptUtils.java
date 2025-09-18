package com.virtualpowerplant.utils;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
public class RSAEncryptUtils {
    public static String publicEncrypt(String data, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            RSAPublicKey key = (RSAPublicKey)keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE,
                    data.getBytes("UTF-8"), key.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }
}
