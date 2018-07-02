package com.customer.dto;

import java.io.Serializable;

public class TransactionDTO implements Serializable{

	private static final long serialVersionUID = -5353185194893150982L;
	private int TxnsRefNo;
	private String AccountNumber;
	private double StartBalance;
	private double EndBalance;
	private String Mutation;
	private String Description;
	 
	// Constructor 
	public TransactionDTO(int TxnsRefNo, String AccountNumber,String Description, 
			double StartBalance,String Mutation,double EndBalance) {
	   super();
	   this.TxnsRefNo = TxnsRefNo;
	   this.AccountNumber = AccountNumber;
	   this.StartBalance = StartBalance;
	   this.EndBalance = EndBalance;
	   this.Mutation = Mutation;
	   this.Description = Description;		   
	}

	 
	public int getTxnsRefNo() {
		return TxnsRefNo;
	}
	public void setTxnsRefNo(int txnsRefNo) {
		TxnsRefNo = txnsRefNo;
	}
	public String getAccountNumber() {
		return AccountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		AccountNumber = accountNumber;
	}
	public double getStartBalance() {
		return StartBalance;
	}
	public void setStartBalance(double startBalance) {
		StartBalance = startBalance;
	}
	public double getEndBalance() {
		return EndBalance;
	}
	public void setEndBalance(double endBalance) {
		EndBalance = endBalance;
	}
	public String getMutation() {
		return Mutation;
	}
	public void setMutation(String mutation) {
		Mutation = mutation;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	
	@Override
	public String toString() {
		return "[TxnsRefNo = " + TxnsRefNo + ", AccountNumber = "
				+ AccountNumber + ", StartBalance = " + StartBalance
				+ ", EndBalance = " + EndBalance + ", Mutation = " + Mutation
				+ ", Description = " + Description + "]";
	}		 
	 
	
}
