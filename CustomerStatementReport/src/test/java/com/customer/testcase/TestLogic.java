package com.customer.testcase;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import com.customer.dto.TransactionDTO;


public class TestLogic {
	
	//Declaring Global variables
	 private List<TransactionDTO> result = new ArrayList<TransactionDTO>();
	 private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".##");
    //Global variables for file path
    private static final String FILENAME_CSV = "\\target\\classes\\com\\customer\\files\\records.csv";
	 
	 //Reading file and store into array list
	 @Before
	 public void setup() throws IOException {
		 List<String> list = new ArrayList<>();
		 String fileName = System.getProperty("user.dir")+FILENAME_CSV;
		 
		 try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
             list = stream.skip(1).filter(Objects::nonNull).filter(s -> s.trim().length() > 0).collect(Collectors.toList());
             for(String obj:list){
           	 String[] trans = obj.split(",");
           	/*Save the Customer Statement details in TransactionDTO object*/
              	TransactionDTO txnDTO = new TransactionDTO(Integer.parseInt(trans[0]),
              			trans[1],trans[2],Double.parseDouble(trans[3]),
                       Double.parseDouble(trans[4]),Double.parseDouble(trans[5]));
             
              	/*Adding transaction object into List*/
              	result.add(txnDTO);
             }
		 }
	 }
	
	 // Checking size of the file
	  @Test
	  public void SizeValidation() throws IOException  {
		  assertEquals("Size of the list should be 10", 10, result.size());	  
	  }

	// Checking description of the record
	  @Test
	  public void ContentValidation() throws IOException  {
		  TransactionDTO dto1 = new TransactionDTO(194261, "NL91RABO0315273637", "Clothes from Jan Bakker",21.6,-41.83, -20.23);
		  assertNotNull("List shouldn't be null", result);
		  assertEquals("Wrong 1st element", dto1.getDescription(), result.get(0).getDescription());
	  }
	  // Checking account number
	  @Test
	  public void AccNumberValidation() {	         
	      String accNumber = "NL91RABO0315273637";
	      TransactionDTO transactionDTO = result.get(0);
	      assertEquals(accNumber, transactionDTO.getAccountNumber());
	  }
	  // Checking end balance  
	  @Test
	  public void EndBalanceEqualsTest() { 	         
	  	TransactionDTO txnObj = new TransactionDTO(183049, "NL69ABNA0433647324", "Clothes for Jan King",86.66,+44.5,131.16);
	    assertEquals(131.16, result.get(2).getEndBalance(),0.002);
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

