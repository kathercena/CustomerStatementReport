package com.customer.testcase;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.customer.dto.TransactionDTO;


public class TestLogic {
	
	//Declaring Global variables
	 private BufferedReader in = null;
	 private List<TransactionDTO> result = new ArrayList<TransactionDTO>();
	 
	 //Reading file and store into array list
	 @Before
	 public void setup() throws IOException {
		 in = new BufferedReader(new FileReader(new File("E:\\records.csv")));
		 String st = "";
		//Read to skip the header
         in.readLine();
         
		 while ((st = in.readLine()) != null){
			 String[] employeeDetails = st.split(",");  
			 if(employeeDetails.length > 0 ) {
				//Save the Customer Statement details in TransactionDTO object
	          	TransactionDTO txnDTO = new TransactionDTO(Integer.parseInt(employeeDetails[0]),
	                      employeeDetails[1],employeeDetails[2],Double.parseDouble(employeeDetails[3]),
	                      Double.parseDouble(employeeDetails[4]),Double.parseDouble(employeeDetails[5]));
	          	result.add(txnDTO);
			 }
		  }
	 }
	
	 // Checking size of the file
	  @Test
	  public void SizeValidation() throws IOException  {
		  assertEquals("Size of the list should be 10", 10, result.size());	  
	  }

	// Checking size of the file
	  @Test
	  public void ContentValidation() throws IOException  {
		  TransactionDTO dto1 = new TransactionDTO(194261, "NL91RABO0315273637", "Clothes from Jan Bakker",21.6,-41.83, -20.23);
		  assertNotNull("List shouldn't be null", result);
		  assertEquals("Wrong 1st element", dto1, result.get(0));
	  }
	  // Checking account number
	  @Test
	  public void mySimpleEqualsTest() {	         
	      String accNumber = "NL91RABO0315273637";
	      TransactionDTO transactionDTO = result.get(0);
	      assertEquals(accNumber, transactionDTO.getAccountNumber());
	  }
	  // Checking objects  
	  @Test
	  public void myObjectEqualsTest() { 	         
	  	TransactionDTO txnObj = new TransactionDTO(183049, "NL69ABNA0433647324", "Clothes for Jan King",86.66,+44.5,131.16);
	    assertEquals(txnObj,  result.get(2));
	  }
	  
	  @After
	  public void teardown() throws IOException {
		  if (in != null) {
		        in.close();
		  }
		  in = null;
	  }
}

