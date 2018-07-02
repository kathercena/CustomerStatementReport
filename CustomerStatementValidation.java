package com.customer.validation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.customer.dto.TransactionDTO;


/**
 * @author Kather P - @created time June 30, 2018
 * 
 *  CustomerStatementValidation.java
 *  
 *  Validating Customer statement monthly reports
 */
public class CustomerStatementValidation {

	//Delimiters used in the CSV file
    private static final String COMMA_DELIMITER = ",";
    //Global variablesS
    private static String path = "";
    private static int count = 0;
    
    
	public static void main(String[] args) {
		System.out.println("Please select the file format to validate");
		System.out.println("1. CSV file format");
		System.out.println("2. XML file format\n");
		Scanner input = null;
		Scanner inputFile = null;
		try {
			input = new Scanner(System.in);
			int choice = input.nextInt();
			
			if(choice == 1) {
				System.out.println("Please input CSV file path to validate\n");
				inputFile = new Scanner(System.in);
				path = inputFile.next();
				// Calling CSV file validation method
				doValidateCSVCustomerStatement(path);
			}else if(choice == 2) {
				System.out.println("Please input XML file path to validate\n");
				inputFile = new Scanner(System.in);
				path = inputFile.next();
				// Calling XML file validation method
				doValidateXMLCustomerStatement(path);
			}else{
				System.out.println("Please select the valid file format\n");
			}
		}catch(Exception ex) {
			input.close();
			inputFile.close();
			System.out.println("Please select the valid file format\n");
			System.out.println("Error occured while reading file"+ex.getMessage()+" "+ex.getCause());
		}
    }

	// Validating CSV Customer Statement report file
	public static void doValidateCSVCustomerStatement(String path) {
		BufferedReader br = null;
       
		try {
            //Reading the csv file
            br = new BufferedReader(new FileReader(path));
            
            //Create List for holding Unique Customer Statement details objects
            List<TransactionDTO> uniqueTransList = new ArrayList<TransactionDTO>();
            
           //Create List for holding Duplicate Customer Statement details objects
            List<TransactionDTO> duplicateTransList = new ArrayList<TransactionDTO>();
            
            String line = "";
            HashSet<Integer> uniqueTxnRefNo = new HashSet<Integer>();
            
            //Read to skip the header
            br.readLine();
            
            //Reading from the second line
            while ((line = br.readLine()) != null) {
                String[] employeeDetails = line.split(COMMA_DELIMITER);
                
                if(employeeDetails.length > 0 ) {
                	             	
                    //Save the Customer Statement details in TransactionDTO object
                	TransactionDTO txnDTO = new TransactionDTO(Integer.parseInt(employeeDetails[0]),
                            employeeDetails[1],employeeDetails[2],Double.parseDouble(employeeDetails[3]),
                            employeeDetails[4],Double.parseDouble(employeeDetails[5]));
                	
                	// Validating unique transaction reference and negative end balance in CSV report
                	if(uniqueTxnRefNo.add(txnDTO.getTxnsRefNo()) && txnDTO.getEndBalance() > 0){   
                		uniqueTransList.add(txnDTO);
                	}else{
                		duplicateTransList.add(txnDTO);              		
                	}
                }
            }
            System.out.println("Customer Statement report successfully validated.\n");
            
            //Lets print the Failed Customer Statement details report
            if(duplicateTransList.size() > 0) {
	            System.out.println("Failed Customer Statement Reports are: ");
	            count = 1;
	            for(TransactionDTO dto : duplicateTransList) {      	
	                System.out.println(count++ +"."+"Transaction reference = "+dto.getTxnsRefNo()
	                		+", Description = "+dto.getDescription());
	            }
            }
        }
        catch(Exception ex) {
        	System.out.println("Please input valid file");
        	System.out.println("Error occured while reading file"+ex.getMessage()+" "+ex.getCause());
        }
        finally {
	        try {
	            br.close();
	        }
	        catch(IOException ie) {
	            System.out.println("Error occured while closing the BufferedReader"+ie.getMessage()+" "+ie.getCause());
	        }
        }		
	}
	
	// Validating XML Customer Statement report file
	public static void doValidateXMLCustomerStatement(String path) {
		
        //Create List for holding Unique Customer Statement details objects
        List<TransactionDTO> uniqueTransList = new ArrayList<TransactionDTO>();
        
       //Create List for holding Duplicate Customer Statement details objects
        List<TransactionDTO> duplicateTransList = new ArrayList<TransactionDTO>();
        
        HashSet<Integer> uniqueTxnRefNo = new HashSet<Integer>();
		
		try{
			// Reading XML file from given path
			File file = new File(path);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(file);
			document.getDocumentElement().normalize();

			//Get the parent tag element from xml file
			NodeList nList = document.getElementsByTagName("record");
			
			//Iterating the xml file to get the all records
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					
					// Store the attribute values in local variable
					int txnRefNo = Integer.parseInt(eElement.getAttribute("reference"));
					String accountNum = eElement.getElementsByTagName("accountNumber").item(0).getTextContent();
					String description = eElement.getElementsByTagName("description").item(0).getTextContent();
					double startBalance = Double.parseDouble(eElement.getElementsByTagName("startBalance").item(0).getTextContent());
					double endBalance = Double.parseDouble(eElement.getElementsByTagName("endBalance").item(0).getTextContent());
					String mutation = eElement.getElementsByTagName("mutation").item(0).getTextContent();
					
					 //Save the Customer Statement details in TransactionDTO object
                	TransactionDTO txnDTO = new TransactionDTO(txnRefNo,accountNum,description,
                			startBalance,mutation,endBalance);
                	
                	// Validating unique transaction reference and negative end balance in XML report
                	if(uniqueTxnRefNo.add(txnDTO.getTxnsRefNo()) && txnDTO.getEndBalance() > 0){   
                		uniqueTransList.add(txnDTO);
                	}else{
                		duplicateTransList.add(txnDTO);              		
                	}
				}
			}
			System.out.println("Customer Statement report successfully validated.\n");
			 
			//Lets print the Failed Customer Statement details report
			if(duplicateTransList.size() > 0) {
				System.out.println("Failed Customer Statement Reports are: ");
				count = 1;
				for(TransactionDTO dto : duplicateTransList) {      	
					System.out.println(count++ +"."+"Transaction reference = "+dto.getTxnsRefNo()
							+", Description = "+dto.getDescription());
				}
	         }
			
		}catch(Exception ex) {
			System.out.println("Please input valid file");
			System.out.println("Error occured while reading file"+ex.getMessage()+" "+ex.getCause());
        }
	}
} 
