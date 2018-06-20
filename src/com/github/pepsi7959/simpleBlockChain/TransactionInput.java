package com.github.pepsi7959.simpleBlockChain;

public class TransactionInput {
	public String transactionOutputId;
	public TransactionOutput UTX0;
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
