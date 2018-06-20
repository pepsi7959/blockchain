package com.github.pepsi7959.BlockChain;

/* Transaction class
 * When sending some funds to another, The transaction will take place.
 */
import java.security.*;
import java.util.ArrayList;

public class Transaction {
	
	private static int sequence = 0;
	public String transactionId;
	public PublicKey sender;
	public PublicKey recipient;
	public float value;
	public byte[] signature;

	/* 
	 * inputs will be used for storing input transaction which are deposit or transfer in.
	 */
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}

	/* 
	 * Generate Signature
	 * It will be used to generate signature when create transaction
	 * 
	 */
	public void getnerateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value);
		signature = StringUtil.applyECDSAsig(privateKey, data);
	}

	public Boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	private String calulateHash() {
		sequence++;
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value) + sequence);
	}

	public float getInputValue() {
		float total = 0;
		for (TransactionInput i : inputs) {
			if (i.UTX0 == null)
				continue;
			total += i.UTX0.value;
		}
		return total;
	}

	public float getOutputValue() {
		float total = 0;
		for (TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}

	public boolean procsssTransaction() {
		// Verify signature
		if (verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		// Gather transaction inputs (Make sure they unspent)
		for (TransactionInput i : inputs) {
			i.UTX0 = BlockChain.UTXOs.get(i.transactionOutputId);
		}

		// Check balance, It should have more than "minimumTransaction".
		if (getInputValue() < BlockChain.minimumTransaction) {
			System.out.println("#Transaction inputs too small:" + getInputValue());
			return false;
		}

		// LelftOver is amount of money or funds
		float leftOver = getInputValue() - value;
		transactionId = calulateHash(); // sha256(sender + recipient + value + sequence)
		
		// This will have two transactions, 
		// First transaction will be used for sending value to another.
		outputs.add(new TransactionOutput(this.recipient, value, transactionId));
		// Second transaction will be used for sending the leftover to myself.
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

		// Add output transactions to unspent transaction for verification.
		for (TransactionOutput o : outputs) {
			BlockChain.UTXOs.put(o.id, o);
		}

		// Clear input transaction(the old output transaction) on unspent transaction.
		for (TransactionInput i : inputs) {
			if (i.UTX0 == null)
				continue;
			BlockChain.UTXOs.remove(i.UTX0.id);
		}

		return true;

	}

}
