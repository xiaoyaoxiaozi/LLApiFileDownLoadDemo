package com.lianlianpay.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.log4j.Logger;


/**
 * RSA签名公共类
 * @author shmily
 */
public class TraderRSAUtil{

    private static Logger  log = Logger.getLogger(TraderRSAUtil.class);
    private static TraderRSAUtil instance;

    private TraderRSAUtil()
    {

    }

    public static TraderRSAUtil getInstance()
    {
        if (null == instance)
            return new TraderRSAUtil();
        return instance;
    }

    /**
     * 
     * 公钥、私钥文件生成
     * @param keyPath：保存文件的路径
     * @param keyFlag：文件名前缀
     */
    private void generateKeyPair(String key_path, String name_prefix)
    {
        java.security.KeyPairGenerator keygen = null;
        try
        {
            keygen = java.security.KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e1)
        {
            log.error(e1.getMessage());
        }
        SecureRandom secrand = new SecureRandom();
        secrand.setSeed("3500".getBytes()); // 初始化随机产生器
        keygen.initialize(1024, secrand);
        KeyPair keys = keygen.genKeyPair();
        PublicKey pubkey = keys.getPublic();
        PrivateKey prikey = keys.getPrivate();

        String pubKeyStr = new String(org.apache.commons.codec.binary.Base64.encodeBase64(pubkey.getEncoded()));
        String priKeyStr = new String(org.apache.commons.codec.binary.Base64.encodeBase64(org.apache.commons.codec.binary.Base64.encodeBase64(prikey.getEncoded())));
        File file = new File(key_path);
        if (!file.exists())
        {
            file.mkdirs();
        }
        try
        {
            // 保存私钥
            FileOutputStream fos = new FileOutputStream(new File(key_path
                    + name_prefix + "_RSAKey_private.txt"));
            fos.write(priKeyStr.getBytes());
            fos.close();
            // 保存公钥
            fos = new FileOutputStream(new File(key_path + name_prefix
                    + "_RSAKey_public.txt"));
            fos.write(pubKeyStr.getBytes());
            fos.close();
        } catch (IOException e)
        {
            log.error(e.getMessage());
        }
    }

    /**
     * 读取密钥文件内容
     * @param key_file:文件路径
     * @return
     */
    private static String getKeyContent(String key_file)
    {
        File file = new File(key_file);
        BufferedReader br = null;
        InputStream ins = null;
        StringBuffer sReturnBuf = new StringBuffer();
        try
        {
            ins = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
            String readStr = null;
            readStr = br.readLine();
            while (readStr != null)
            {
                sReturnBuf.append(readStr);
                readStr = br.readLine();
            }
        } catch (IOException e)
        {
            return null;
        } finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                    br = null;
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (ins != null)
            {
                try
                {
                    ins.close();
                    ins = null;
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        return sReturnBuf.toString();
    }

    /**
     * 签名处理
     * @param prikeyvalue：私钥文件
     * @param sign_str：签名源内容
     * @return
     */
    public static String sign(String prikeyvalue, String sign_str)
    {
        try
        {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64
                    .getBytesBASE64(prikeyvalue));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey myprikey = keyf.generatePrivate(priPKCS8);
            // 用私钥对信息生成数字签名
            java.security.Signature signet = java.security.Signature
                    .getInstance("MD5withRSA");
            signet.initSign(myprikey);
            signet.update(sign_str.getBytes("UTF-8"));
            byte[] signed = signet.sign(); // 对信息的数字签名
//            return Base64.getBASE64(signed);
            return new String(org.apache.commons.codec.binary.Base64.encodeBase64(signed));
        } catch (java.lang.Exception e)
        {
            log.error("签名失败," + e.getMessage());
        }
        return null;
    }

    /**
     * 签名验证
     * @param pubkeyvalue：公钥
     * @param oid_str：源串
     * @param signed_str：签名结果串
     * @return
     */
    public static boolean checksign(String pubkeyvalue, String oid_str,
            String signed_str)
    {
        try
        {
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64
                    .getBytesBASE64(pubkeyvalue));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
            byte[] signed = Base64.getBytesBASE64(signed_str);// 这是SignatureData输出的数字签名
            Signature signetcheck = Signature
                    .getInstance("MD5withRSA");
            signetcheck.initVerify(pubKey);
            signetcheck.update(oid_str.getBytes("UTF-8"));
            return signetcheck.verify(signed);
        } catch (java.lang.Exception e)
        {
            log.error("签名验证异常," + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args)
    {
//        TraderRSAUtil.getInstance().generateKeyPair("D:\\CertFiles\\inpour\\",
//                "usersite");
        String sign = TraderRSAUtil.getInstance().sign("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJPb6UtHkRtCmunLtxgWUUkqKVMqdMrvLxU4UzTRaNddI2tHUszyTSntfz+l1S3BjRBvjx1/yvrFRvneW7lmM9w+e5LPUnIhqnNrl2aeioOJWHz+Ba6qrRXz8kCf6kfsAMG4H2A2xMcb26ZiMPZxFKHinuKcW7bT+bXTFxrQsR/JAgMBAAECgYEAh2vK6F/LzyPZrngeYblPCavL3ZftEFCw1saXrrB9TYLIheD1PTBO7C/RdAH2lcnH4V3LvkDlL3iv4Pp/F/c7Vvvgs/LbpXwnPvYVtdkZ1x3AZRfS/5uSrSoAkiN0zEJnmb3Ywp7YlCYfVlke4u6dhQN+WxvqPl69VMBzNpagXWECQQDlBVUvIqQp6e0Gsp4oOj3HyQtCT+BsaRZkLtMNTq5pcz/83s1H0cIoU8dTT7LCZvRw+yjYgQ5YBY9D0CZBmwdfAkEApUbzmt2klNpf2apadyI+fYcbYBky3kb2q6YZ/xQuCU8eSJC4F2bPDDfxpsIqADj5A8KB74EnB6h1UT9rQONx1wJBAMXuFfDmv3p58aAYPxgFPd+soU5uOkd3iyKKVVzq41G/iU3CQSgQ4Px5a4tVFeltkVUTu/lhkEQCig7Rlj6c/YECQHwqUIrQ5nsZj5bDv1Du/glp/ev1Il0Q7PHJSJB0RZ2ivbqAVnzmNLgWM0o3ZjxikNj9QIaA/aRoLzLJtTa7aGMCQFEkTk/9gIYYKolMwMllO/SN+dO54W1Pc/Dx65ZsEwzgq+UEBb0BjbxbVebVRcaXam6OKIuCW2KwdQuMlY6AqeQ=", "123");
        System.out.println(sign);
//        System.out.print(TraderRSAUtil.checksign("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT2+lLR5EbQprpy7cYFlFJKilTKnTK7y8VOFM00WjXXSNrR1LM8k0p7X8/pdUtwY0Qb48df8r6xUb53lu5ZjPcPnuSz1JyIapza5dmnoqDiVh8/gWuqq0V8/JAn+pH7ADBuB9gNsTHG9umYjD2cRSh4p7inFu20/m10xca0LEfyQIDAQAB", "123", sign));
    }
}
