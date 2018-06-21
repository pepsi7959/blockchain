package com.github.pepsi7959.BlockChain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

	/* 
	 * Key is very importance.
	 */
	
	public PrivateKey privateKey;
	public PublicKey publicKey;

	// It will store only own wallet
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

	public Wallet() {
		generateKeyPair();
	}

	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a keypair

			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();

			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();

		} catch (Exception ex) {
			throw new RuntimeException();
		}
	}

	// Return balance and store stores the UTXO's owned by this wallet in this.UTXOs
	public float getBalance() {
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : BlockChain.UTXOs.entrySet()) {
			TransactionOutput UTX0 = item.getValue();
			if (UTX0.isMine(publicKey)) {
				this.UTXOs.put(UTX0.id, UTX0);
				total += UTX0.value;
			}
		}
		return total;
	}

	//
	public Transaction sendFunds(PublicKey _recipient, float value) {
		System.out.println("value: " + value);
		if (getBalance() < value) {
			System.out.println("#Not enough funds to send transaction. Transaction discarded");
			return null;
		}

		// Create Array List of inputs
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		float total = 0;

		for (Map.Entry<String, TransactionOutput> item : this.UTXOs.entrySet()) {
			TransactionOutput UTX0 = item.getValue();
			inputs.add(new TransactionInput(UTX0.id));
			total += UTX0.value;

			if (total > value) {
				break;
			}
		}

		Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
		newTransaction.getnerateSignature(privateKey);

		// Clear UTXOs up
		for (TransactionInput input : inputs) {
			this.UTXOs.remove(input.transactionOutputId);
		}

		return newTransaction;
	}
}
