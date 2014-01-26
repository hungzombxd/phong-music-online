package com.nct.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	public static String md5(String paramString) {
		String str1;
		if (paramString != null)
			try {
				MessageDigest localMessageDigest = MessageDigest
						.getInstance("MD5");
				localMessageDigest.update(paramString.getBytes());
				byte[] arrayOfByte = localMessageDigest.digest();
				StringBuffer localStringBuffer = new StringBuffer();
				for (int i = 0;; i++) {
					if (i >= arrayOfByte.length) {
						str1 = localStringBuffer.toString();
						break;
					}
					String str2 = Integer.toHexString(0xFF & arrayOfByte[i]);
					if (str2.length() == 1)
						localStringBuffer.append('0');
					localStringBuffer.append(str2);
				}
			} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
				localNoSuchAlgorithmException.printStackTrace();
				str1 = "";
			}
		else
			str1 = "";
		return str1;
	}

	// public static String md5(String message){
	// try {
	// MessageDigest digest = MessageDigest.getInstance("md5");
	// return new String(digest.digest(message.getBytes()));
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
}
