package com.gimnasio.usuarios;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Base64;
public class PasswordHasher {
    private static final int iteraciones=120000;
    private static final int tamanoClave=256;
    private static final int tamanoSal=16;
    public String hash(String password){
        byte[] sal = new byte[tamanoSal];
        new SecureRandom().nextBytes(sal);
        byte[] hash= derivar(password,sal);
        return Base64.getEncoder().encodeToString(sal) + ":" + Base64.getEncoder().encodeToString(hash);
    }
    public boolean verificar(String password, String almacenado) {
        String[] partes = almacenado.split(":");
        if (partes.length != 2) return false;
        byte[] sal = Base64.getDecoder().decode(partes[0]);
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        byte[] calculado = derivar(password, sal);
        return MessageDigest.isEqual(esperado, calculado); 
    }
    private byte[] derivar(String password, byte[] sal) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sal, iteraciones, tamanoClave);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo calcular el hash", e);
        }
    }
}
