package com.customer.dto;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionDTO implements Serializable{

	private static final long serialVersionUID = -5353185194893150982L;
	private int TxnsRefNo;
	private String AccountNumber;
	private double StartBalance;
	private double EndBalance;
	private double Mutation;
	private String Description;
	 
	// Constructor 
	public TransactionDTO(int TxnsRefNo, String AccountNumber,String Description, 
			double StartBalance,double Mutation,double EndBalance) {
	   super();
	   this.TxnsRefNo = TxnsRefNo;
	   this.AccountNumber = AccountNumber;
	   this.StartBalance = StartBalance;
	   this.EndBalance = EndBalance;
	   this.Mutation = Mutation;
	   this.Description = Description;		   
	}
	
	/*Generating list into the String */
	public String toCsvRow() {
	    return Stream.of(String.valueOf(TxnsRefNo),Description)
	            .map(value -> value.replaceAll("\"", "\"\""))
	            .map(value -> Stream.of("\"", ",").anyMatch(value::contains) ? "\"" + value + "\"" : value)
	            .collect(Collectors.joining(","));
	}
	

	/**
	 * @return the txnsRefNo
	 */
	public int getTxnsRefNo() {
		return TxnsRefNo;
	}




	/**
	 * @param txnsRefNo the txnsRefNo to set
	 */
	public void setTxnsRefNo(int txnsRefNo) {
		TxnsRefNo = txnsRefNo;
	}




	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return AccountNumber;
	}




	/**
	 * @param accountNumber the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		AccountNumber = accountNumber;
	}




	/**
	 * @return the startBalance
	 */
	public double getStartBalance() {
		return StartBalance;
	}




	/**
	 * @param startBalance the startBalance to set
	 */
	public void setStartBalance(double startBalance) {
		StartBalance = startBalance;
	}




	/**
	 * @return the endBalance
	 */
	public double getEndBalance() {
		return EndBalance;
	}




	/**
	 * @param endBalance the endBalance to set
	 */
	public void setEndBalance(double endBalance) {
		EndBalance = endBalance;
	}




	/**
	 * @return the mutation
	 */
	public double getMutation() {
		return Mutation;
	}




	/**
	 * @param mutation the mutation to set
	 */
	public void setMutation(double mutation) {
		Mutation = mutation;
	}




	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}




	/**
	 * @param description the description to set
	 */
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
