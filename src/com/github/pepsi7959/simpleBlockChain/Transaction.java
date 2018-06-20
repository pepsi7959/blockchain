package com.github.pepsi7959.simpleBlockChain;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
	public String transactionId;
	public PublicKey sender;
	public PublicKey recipient;
	public float value;
	public byte[] signature;

	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0;

	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}

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

		if (verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		// Gather transaction inputs (Make sure they usspent)
		for (TransactionInput i : inputs) {
			System.out.println("input transaction :" + i.transactionOutputId);
			i.UTX0 = BlockChain.UTXOs.get(i.transactionOutputId);
		}

		// Check balance, and minimum per transaction
		if (getInputValue() < BlockChain.minimumTransaction) {
			System.out.println("#Transaction inputs too small:" + getInputValue());
			return false;
		}
		
		// LelftOver of balance 
		float leftOver = getInputValue() - value;
		transactionId = calulateHash(); // sha256(sender + recipient + value + sequence)
		outputs.add(new TransactionOutput(this.recipient, value, transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

		for (TransactionOutput o : outputs) {
			BlockChain.UTXOs.put(o.id, o);
		}

		for (TransactionInput i : inputs) {
			if (i.UTX0 == null)
				continue;
			BlockChain.UTXOs.remove(i.UTX0.id);
		}

		return true;

	}

}
