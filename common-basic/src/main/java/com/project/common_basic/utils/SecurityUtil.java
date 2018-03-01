package com.project.common_basic.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密解密工具类
 *
 * @author yamlee
 */
@SuppressWarnings("HardCodedStringLiteral")
public class SecurityUtil {
    /**
     * 创建唯一标示ID
     */
    public static String createUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static String encodeBase64(String sourceMsg) {
        String strBase64 = new String(Base64.encode(sourceMsg.getBytes(), Base64.DEFAULT));
        return strBase64;
    }

    public static String decodeBase64(String encodedMsg) {
        String decodStr = new String(Base64.decode(encodedMsg.getBytes(), Base64.DEFAULT));
        return decodStr;
    }

    public String encode_md5(String src) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(src.getBytes());
        return Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
    }

    public String encode_sha(String src) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(src.getBytes());
        return Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
    }

    public static String initMacKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
    }

    public static String encode_hmac(String key, String src) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKey secretkey = new SecretKeySpec(Base64.decode(key.getBytes(), Base64.DEFAULT), "HmacMD5");
        Mac mac = Mac.getInstance(secretkey.getAlgorithm());
        mac.init(secretkey);
        return Base64.encodeToString(mac.doFinal(src.getBytes()), Base64.DEFAULT);
    }

    public static String encode_aes(String key, String src) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (key == null || key.length() != 16) {
            return "keyֵ illegal";
        }
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(key.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return Base64.encodeToString(cipher.doFinal(src.getBytes()), Base64.DEFAULT);
    }

    public static String decode_aes(String key, String encrptStr) throws Exception {
        if (key == null || key.length() != 16) {
            return "keyֵ illegal";
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(Base64.decode(encrptStr.getBytes(), Base64.DEFAULT)));
    }

    public static byte[] encryptAes(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptAes(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 编码后进行验证的字符串,支付签名
     */
    public static String getSignValue(String apiKey, Map<String, Object> map) {
        String qtApikey = apiKey;
        ArrayList<Map.Entry<String, Object>> l = new ArrayList<>(map.entrySet());

        Collections.sort(l, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> lhs, Map.Entry<String, Object> rhs) {
                return lhs.getKey().compareToIgnoreCase(rhs.getKey());
            }
        });
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : l) {
            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue().toString());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(qtApikey);
        return toMd5(sb.toString());
    }

    public static String toMd5(String data) {
        MessageDigest md5;
        byte[] m = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes());
            m = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return byteArray2Hex(m);
    }

    static final String HEXES = "0123456789ABCDEF";

    public static String byteArray2Hex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString().toUpperCase();
    }


    /**
     * 获取文件md5
     *
     * @throws Exception
     */
    public static String md5File(File file) throws Exception {
        DigestInputStream dis = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            dis = new DigestInputStream(new FileInputStream(file), messageDigest);
            byte[] buf = new byte[2048 * 10];
            int len;
            while ((len = dis.read(buf)) != -1) {
            }
            byte[] digest2 = messageDigest.digest();
            //当流读取完毕，即将文件读完了， 这时的摘要 才与 写入时的 一样
            return byteArray2Hex(digest2);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String toMd5Low(String data) {
        MessageDigest md5;
        byte[] m;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes());
            m = md5.digest();
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }

        return byteArray2HexLow(m);
    }

    static final String HEXES_LOW = "0123456789abcdef";

    public static String byteArray2HexLow(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES_LOW.charAt((b & 0xF0) >> 4)).append(HEXES_LOW.charAt((b & 0x0F)));
        }
        return hex.toString().toLowerCase();
    }

    public static byte[] toPrimitive(Byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new byte[0];
        } else {
            byte[] result = new byte[array.length];

            for (int i = 0; i < array.length; ++i) {
                result[i] = array[i];
            }

            return result;
        }
    }

    /**
     * 获取app_type
     *
     * @param context context
     * @return app_type
     */
    public static int getAppType(Context context) {
        int appType;
        try {
            appType = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).metaData.getInt("app_type");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appType = -1;
        }
        return appType;
    }

    /**
     * 获取secret_key
     *
     * @param context context
     * @return secret_key
     */
    public static int getSecretKey(Context context) {
        int secretKey;
        try {
            secretKey = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).metaData.getInt("secret_key");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            secretKey = -1;
        }
        return secretKey;
    }


    /**
     * BASE64 加密
     *
     * @param str         str
     * @param charsetName charsetName
     * @return String
     */
    public static String encryptBASE64(String str, String charsetName) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes(charsetName);
            // base64 解密
            return new String(Base64.encode(encode, Base64.DEFAULT), charsetName);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * BASE64 解密
     *
     * @param str         str
     * @param charsetName charsetName
     * @return String
     */
    public static String decryptBASE64(String str, String charsetName) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes(charsetName);
            // base64 解密
            return new String(Base64.decode(encode, Base64.DEFAULT), charsetName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
