package com.tafsir.fleetmanagement.logic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {

	private static final String ALGORITHM ="PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 120000;
	private static final int SALT_BYTES = 16;
	private static final int HASH_BITS = 256;
	
	private PasswordHasher() {
	}
	
		// Returns text like pbkdf2$120000$<salt>$<hash>  ready to store in users.password_hash.
		public static String hash(String password) {
			byte[] salt = new byte[SALT_BYTES];
			new SecureRandom().nextBytes(salt);
			byte[] hash = pbkdf2(password.toCharArray(),salt, ITERATIONS);
			return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
		}
		public static boolean verify(String password, String stored) {
			if (password == null || stored == null) {
				return false;
			}
			String[] parts = stored.split("\\$");
			if (parts.length !=4 || !"pbkdf2".equals(parts[0])) {
				return false;
				
			}
			try {
				int iterations = Integer.parseInt(parts[1]);
				byte[] salt = Base64.getDecoder().decode(parts[2]);
				byte[] expected = Base64.getDecoder().decode(parts[3]);
				byte[] actual = pbkdf2(password.toCharArray(), salt, iterations);
				return MessageDigest.isEqual(expected, actual);		
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		
		private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
	        try {
	            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_BITS);
	            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
	        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
	            throw new IllegalStateException("Password hashing is not available", e);  
	        }
		}
}
