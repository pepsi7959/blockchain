package com.github.pepsi7959.simpleBlockChain;

public class simpleBockChain {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Block genesisBlock = new Block("Hi, I'm the first block", "0");
		System.out.println("Hash for block 1 : " + genesisBlock.hash );
		
		Block secondBlock = new Block("Hi, I'm the second block", genesisBlock.hash);
		System.out.println("Hash for block 1 : " + secondBlock.hash );
	
		Block thirdBlock = new Block("Hi, I'm the third block", "0");
		System.out.println("Hash for block 1 : " + thirdBlock.hash );
	}
}
