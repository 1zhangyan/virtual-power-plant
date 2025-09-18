package com.virtualpowerplant.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptUtils {
    public static String encrypt(String content, String password) throws Exception {
        try {
            byte[] result = null;
            byte[] passwordBytes = getSecretKey(password);
            SecretKeySpec skeySpec = new SecretKeySpec(passwordBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            result = cipher.doFinal(content.getBytes("UTF-8"));
            return parseByte2HexStr(result);
        } catch (Exception e) {
            //Deal Exception
            throw new Exception("AES encrypt error");
        }
    }

    public static byte[] getSecretKey(String key) throws Exception {
        final byte paddingChar = '0';
        byte[] realKey = new byte[16];
        byte[] byteKey = key.getBytes("UTF-8");
        for (int i = 0; i < realKey.length; i++) {
            if (i < byteKey.length) {
                realKey[i] = byteKey[i];
            } else {
                realKey[i] = paddingChar;
            }
        }
        return realKey;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static String decrypt(String content, String password) {
        try {
            byte[] original = null;
            byte[] decryptFrom = parseHexStr2Byte(content);
            byte[] passwordBytes = getSecretKey(password);
            SecretKeySpec skeySpec = new SecretKeySpec(passwordBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            original = cipher.doFinal(decryptFrom);
            return new String(original);
        } catch (Exception e) {
            //Deal Exception
            throw new RuntimeException("AES decrypt error");
        }
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
