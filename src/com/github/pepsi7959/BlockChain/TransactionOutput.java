package com.github.pepsi7959.BlockChain;

/*
 * Transaction Output class
 * Store output transaction including recipient, value and parentTransactionId.
 */
import java.security.PublicKey;

public class TransactionOutput {
	public String id;
	public PublicKey recipient;
	public float value;
	public String parentTransactionId;

	public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient));
	}

	public boolean isMine(PublicKey publicKey) {
		return (publicKey == recipient);
	}
}