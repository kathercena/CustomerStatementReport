package com.customer.testcase;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.customer.dto.TransactionDTO;
import static com.customer.validation.CustomerStatementValidation.doReadXMLCustomerStatement;
import static com.customer.validation.CustomerStatementValidation.doReadCSVCustomerStatement;


public class TestLogic {
	
	//Declaring Global variables
	 private List<TransactionDTO> resultListCSV = new ArrayList<TransactionDTO>();
	 private List<TransactionDTO> resultListXML = new ArrayList<TransactionDTO>();
	 private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".##");
 
	 //Reading file and store into array list
	 @Before
	 public void setup() throws IOException {
		 //Getting CSV file records
         resultListCSV = doReadCSVCustomerStatement();
         //Getting XML file records
		 resultListXML = doReadXMLCustomerStatement();
	 }
	
	  /*Checking size of the file*/
	  @Test
	  public void SizeValidation() throws IOException  {
		  assertEquals("Size of the resultListCSV should be 10", 10, resultListCSV.size());
		  assertEquals("Size of the resultListXML should be 10", 10, resultListXML.size());
	  }

	  /*Checking null list validation */
	  @Test
	  public void NullValidation() throws IOException {		  
		  assertNotNull("List shouldn't be null", resultListCSV);
		  assertNotNull("List shouldn't be null", resultListXML);
	  }
	  
	 /*Checking description of the record*/
	  @Test
	  public void ContentValidation() throws IOException  {
		  TransactionDTO dtoCSV = new TransactionDTO(194261, "NL91RABO0315273637", "Clothes from Jan Bakker",21.6,-41.83, -20.23);
		  assertEquals("Wrong 1st element", dtoCSV.getDescription(), resultListCSV.get(0).getDescription());
		  
		  TransactionDTO dtoXML = new TransactionDTO(194261, "NL69ABNA0433647324", "Tickets for Peter Theuﬂ",26.9,-18.78, 8.12);
		  assertEquals("Wrong 1st element", dtoXML.getDescription(), resultListXML.get(0).getDescription());
		  
	  }
	
	  // Checking account number
	  @Test
	  public void AccNumberValidation() {	         
	      String accNumberCSV = "NL91RABO0315273637";
	      TransactionDTO transCSVDTO = resultListCSV.get(0);
	      assertEquals(accNumberCSV, transCSVDTO.getAccountNumber());
	      
	      String accNumberXML = "NL69ABNA0433647324";
	      TransactionDTO transXMLDTO = resultListXML.get(0);
	      assertEquals(accNumberXML, transXMLDTO.getAccountNumber());	      
	      
	  }
	  // Checking end balance  
	  @Test
	  public void EndBalanceEqualsTest() { 	         
	  	TransactionDTO txnObj = new TransactionDTO(183049, "NL69ABNA0433647324", "Clothes for Jan King",86.66,+44.5,131.16);
	    assertEquals(131.16, resultListCSV.get(2).getEndBalance(),0.002);
	  }
	  
  	  // Checking end balance calculation
	  @Test
	  public void EndBalanceCalculationTest() { 	         
	  	TransactionDTO txnObj = new TransactionDTO(183049, "NL69ABNA0433647324", "Clothes for Jan King",86.66,+44.5,131.16);
	  	String formatBalance  = DECIMAL_FORMAT.format(txnObj.getStartBalance() + txnObj.getMutation());
		double endBalance = Double.parseDouble(formatBalance);				
	    assertEquals(131.17, endBalance,0.01);
	  }
	  
}

