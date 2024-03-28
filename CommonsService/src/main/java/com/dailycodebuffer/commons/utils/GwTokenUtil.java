package com.dailycodebuffer.commons.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GwTokenUtil {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-mm-dd_HH");
	private static final String GATEWAY_TOKEN = "gw_token";

	public static String generateGwToken() {
		OffsetDateTime off = OffsetDateTime.now(ZoneOffset.UTC);
		String secretKey = "prd" + "_" + off.format(FORMATTER);
		String gwtoken = encrypt(secretKey, Boolean.TRUE.toString());
		return gwtoken;
	}

	public static String encrypt(String key, String data) {
		try {
			Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, key);
			byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Cipher initCipher(int cipherMode, String key) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] hash = messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
		SecretKey secretKey = new SecretKeySpec(hash, "AES");
		Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
		cipher.init(cipherMode, secretKey);
		return cipher;
	}

	public static Boolean validateGwToken(HttpServletRequest req) {
		if (req.getHeader(GATEWAY_TOKEN) == null) {
			return false;
		}
		return validateGwToken(req.getHeader(GATEWAY_TOKEN));
	}

	public static Boolean validateGwToken(String token) {
		OffsetDateTime off = OffsetDateTime.now(ZoneOffset.UTC);
		String secretKey = "prd" + "_" + off.format(FORMATTER);
		String gwtoken = decrypt(secretKey, token);
		log.info("result gw_token : [{}]", Boolean.parseBoolean(gwtoken));
		return Boolean.parseBoolean(gwtoken);
	}

	private static String decrypt(String key, String data) {
		try {
			Cipher cipher = initCipher(Cipher.DECRYPT_MODE, key);
			byte[] decrypted = Base64.getDecoder().decode(data);
			byte[] bytes = cipher.doFinal(decrypted);
			return new String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
