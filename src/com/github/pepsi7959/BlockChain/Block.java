package com.github.pepsi7959.BlockChain;

import java.util.ArrayList;
import java.util.Date;

public class Block {
	public String hash;
	public String previousHash;
	public String markleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private long timestamp;
	private int nonce; /* running number to calculate hash */   
	
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timestamp = new Date().getTime();
		this.hash = calculatedHash();
	}
	
	/* Calculate hash by using SHA256 
	 * hash will be derived by hashing the combination of "previousHash", "timestamp", "nonce" and "markleRoot" */
	
	public String calculatedHash() {
		String calculatedhash = StringUtil.applySha256(this.previousHash + Long.toString(timestamp) +Integer.toString(nonce)+ markleRoot);
		return calculatedhash;
	}
	
	/* Mining Block by using simple algorithm */
	/* This algorithm will match hash with "target", which is filled by "0" 
	 * and number of "0" will depended on "difficulty" */
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0','0');
		while(!this.hash.substring(0, difficulty).equals(target)) {
			nonce ++;
			hash = calculatedHash();
		}
		System.out.println("Block Mine!!! :" + hash);
	}

	/* Add a new transaction to block */
	public boolean addTransaction(Transaction transaction) {
		if(transaction == null) return false;
		if(previousHash != "0") {
			if(transaction.procsssTransaction() != true) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction successfully added to block");
		return true;
	}
}
