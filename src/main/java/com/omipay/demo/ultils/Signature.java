package com.omipay.demo.ultils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

@Component
public class Signature {
    private static final Logger logger = LogManager.getLogger(Signature.class);

    @Value("${PRIV_MERCHANT_KEY}")
    private String privOmipayKey;

    @Value("${PUB_OMIPAY_PATH}")
    private String pubMerchantPath;

    public boolean verifySHA256withRSA(String message, String signature)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException,
            FileNotFoundException, CertificateException, URISyntaxException {

        ClassLoader classLoader = Signature.class.getClassLoader();
        URL resource = classLoader.getResource(pubMerchantPath);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + pubMerchantPath);
        }
        URI path = resource.toURI();
        java.security.Signature sign = java.security.Signature.getInstance("SHA256withRSA");
        sign.initVerify(getPublicKey(path));
        sign.update(message.getBytes(StandardCharsets.UTF_8));
        return sign.verify(Base64.decodeBase64(signature.getBytes(StandardCharsets.UTF_8)));
    }

    private PublicKey getPublicKey(URI path) throws FileNotFoundException, CertificateException, URISyntaxException {
        File file = new File(path);
        if (!file.canRead()) {
            throw new IllegalArgumentException("file not found! " + path);
        }

        FileInputStream fin = new FileInputStream(file);
        CertificateFactory f = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);
        PublicKey pubKey = certificate.getPublicKey();
        return pubKey;
    }


    public String signSHA256withRSA(String message) throws IOException, GeneralSecurityException, URISyntaxException {
        logger.info("Begin sign");
        java.security.Signature sign = java.security.Signature.getInstance("SHA256withRSA");
        // Get PrivateKey
        sign.initSign(getPrivateKey());
        sign.update(message.getBytes(StandardCharsets.UTF_8));
        logger.info("Begin sign");
        return new String(Base64.encodeBase64(sign.sign()), StandardCharsets.UTF_8);
    }

    private RSAPrivateKey getPrivateKey() throws IOException, URISyntaxException, GeneralSecurityException {
        logger.info("Begin getPrivateKey");

        ClassLoader classLoader = Signature.class.getClassLoader();
        URL resource = classLoader.getResource(privOmipayKey);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + privOmipayKey);
        }
        File file = new File(resource.toURI());
        if (!file.canRead()) {
            throw new IllegalArgumentException("file not found! " + privOmipayKey);
        }

        String privateKeyPEM = "";
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            privateKeyPEM += line + "\n";
        }
        br.close();
        logger.info("End getPrivateKeyHTP");
        return getPrivateKeyFromString(privateKeyPEM);
    }

    private RSAPrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
        logger.info("Begin getPrivateKeyFromString");
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
        logger.info("End getPrivateKeyFromString");
        return privKey;
    }

    public static <S> String getMD5(S ob) {
        return DigestUtils.md5Hex(ob.toString());
    }
}
