package com.github.pepsi7959.simpleBlockChain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

import sun.security.util.SecurityProviderConstants;

public class BlockChain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	public static int difficulty = 5;
	public static Wallet walletA;
	public static Wallet walletB;
	public static float minimumTransaction = 0.1f;
	public static Transaction genesisTransaction;
	
	public static void main(String[] args) {
		//Setup Bouncey castle aa a security provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinBase = new Wallet();
		
		genesisTransaction = new Transaction(coinBase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.getnerateSignature(coinBase.privateKey);
		genesisTransaction.transactionId = "0";
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient,genesisTransaction.value, genesisTransaction.transactionId));
		UTXOs.put(genesisTransaction.outputs.get(0).id,genesisTransaction.outputs.get(0));
		
		System.out.println("Creating and Mining Genesis Block...");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		
		//testing
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is :" + walletA.getBalance());
		System.out.println("\nWalletB's balance is :" + walletB.getBalance());
		System.out.println("\nWallet1 is trying to send 40 to walletB..." + walletB.getBalance());
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is :" + walletA.getBalance());
		System.out.println("\nWalletB's balance is :" + walletB.getBalance());
		
		
		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA is trying to send 1000 to walletB");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is :" + walletA.getBalance());
		System.out.println("\nWalletB's balance is :" + walletB.getBalance());
		
		
		Block block3 = new Block(block1.hash);
		System.out.println("\nWalletB is trying to send 20 to walletA");
		block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
		addBlock(block3);
		System.out.println("\nWalletA's balance is :" + walletA.getBalance());
		System.out.println("\nWalletB's balance is :" + walletB.getBalance());

		//isChainValid();
		//String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		//System.out.println(blockchainJson);
	}

	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		for (int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);

			if (!currentBlock.hash.equals(currentBlock.calculatedHash())) {
				System.out.print("Current Hashs is not equal");
				return false;
			}

			if (!previousBlock.calculatedHash().equals(currentBlock.previousHash)) {
				System.out.print("previous Hashs is not equal");
				return false;
			}

			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.print("This block hasn't mined");
				return false;
			}
		}
		return true;
	}

	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
}
