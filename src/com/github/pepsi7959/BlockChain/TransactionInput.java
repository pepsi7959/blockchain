package com.github.pepsi7959.BlockChain;

/* 
 * Transaction Input class
 * Store incoming transaction which is deposit or transfer in.
 */
public class TransactionInput {
	
	public String transactionOutputId;
	public TransactionOutput UTX0;
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
