package com.github.pepsi7959.simpleBlockChain;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.security.*;

public class StringUtil {
	public static String applySha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			// applies sha256 to input
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();
			
			for(int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static String getStringFromKey(Key publicKey) {
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}
	
	public static byte[] applyECDSAsig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig =  dsa.sign();
			output = realSig;
		}catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return output;
	}
	
	public static Boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA","BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);	
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//Tacks in array of transactions and returns a markle root.
	public static String getMarkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.transactionId);
		}
		ArrayList<String> treeLayer = previousTreeLayer;
		while(count  > 1 ) {
			treeLayer = new ArrayList<String>();
			for(int i = 1; i < previousTreeLayer.size();i++) {
				treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String markleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return markleRoot;
		
	}
}