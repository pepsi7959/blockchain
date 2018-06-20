package com.github.pepsi7959.simpleBlockChain;

import java.util.ArrayList;
import java.util.Date;

public class Block {
	public String hash;
	public String previousHash;
	public String markleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private long timestamp;
	private int nonce;
	
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timestamp = new Date().getTime();
		this.hash = calculatedHash();
	}
	
	public String calculatedHash() {
		String calculatedhash = StringUtil.applySha256(this.previousHash + Long.toString(timestamp) +Integer.toString(nonce)+ markleRoot);
		return calculatedhash;
	}
	
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0','0');
		while(!this.hash.substring(0, difficulty).equals(target)) {
			nonce ++;
			hash = calculatedHash();
			//System.out.println("calculating hash("+nonce+"): " + hash);
		}
		System.out.println("Block Mine!!! :" + hash);
	}

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
