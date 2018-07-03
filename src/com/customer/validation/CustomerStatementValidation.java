package com.customer.validation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
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
    private static final String NEW_LINE_SEPARATOR = "\n";
    //Global variables
    private static final String FILENAME_CSV = "E:\\records.csv";
    private static final String FILENAME_XML = "E:\\records.xml";
    private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");
    //CSV file header
    private static final String FILE_HEADER = "Reference,Description";
    
    
	public static void main(String[] args) {
		System.out.println("Please select the file to validate");
		System.out.println("1. CSV file");
		System.out.println("2. XML file\n");
		
		//Create List for holding failure Customer Statement report
        List<TransactionDTO> failureTransList = new ArrayList<TransactionDTO>();
		Scanner input = null;
		
		try {
			input = new Scanner(System.in);
			int choice = input.nextInt();
			
			// Calling Customer Statement report file validation method
			failureTransList = doValidateCustomerStatement(choice);
			
			if(failureTransList.size() > 0) {
				if(choice == 1)
					doGenerateFailureCSVReport(failureTransList);
				else
					doGenerateFailureXMLReport(failureTransList);
			}
			
			
			System.out.println("Customer Statement report successfully validated.\n"+failureTransList);
		}catch(Exception ex) {
			input.close();
			System.out.println("Error occured while reading file"+ex.getMessage()+" "+ex.getCause());
		}
    }

	private static void doGenerateFailureXMLReport(List<TransactionDTO> failureTransList) {
		
		try {
			// Create XML writer objects 
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			// root element
			Element root = document.createElement("records");
			document.appendChild(root);
			
			// Generating XML records
			for(TransactionDTO transDTO:failureTransList) {
				// Transaction element
				Element transaction = document.createElement("record");
				root.appendChild(transaction);
	
				// set an attribute to transaction element
				Attr attr = document.createAttribute("reference");
				attr.setValue(String.valueOf(transDTO.getTxnsRefNo()));
				transaction.setAttributeNode(attr);
				
				Element description = document.createElement("description");
				description.appendChild(document.createTextNode(transDTO.getDescription()));
				transaction.appendChild(description);
			}
			
			
			//create the xml file, transform the DOM Object to an XML File
			 TransformerFactory transformerFactory = TransformerFactory.newInstance();
			 Transformer transformer = transformerFactory.newTransformer();
			 DOMSource domSource = new DOMSource(document);
			 StreamResult streamResult = new StreamResult(new File("E:\\failure.xml"));
			 transformer.transform(domSource, streamResult);
			 
			 System.out.println("Done creating XML File");
		}catch(Exception ex) {
			System.out.println("Error occured while writing the failure transactions report"+ex.getMessage()+"--"+ex.getCause());
		}
		
	}

	private static void doGenerateFailureCSVReport(List<TransactionDTO> failureTransList) {
		FileWriter fileWriter = null;
		//String fileName = System.getProperty("user.home")+"/student.csv";

		try{
			fileWriter = new FileWriter("E:\\failure.csv");
			//Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());
			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			//Write a failure report list to the CSV file
	        for (TransactionDTO transDTO : failureTransList) {
	        	fileWriter.append(String.valueOf(transDTO.getTxnsRefNo()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(transDTO.getDescription());
	            fileWriter.append(NEW_LINE_SEPARATOR);
	        }
		}catch(Exception ex){
			System.out.println("Error occured while writing the failure transactions report"+ex.getMessage()+"--"+ex.getCause());
		}finally {
			 try {
	              fileWriter.flush();
	              fileWriter.close();
	          }catch (IOException e) {
	              System.out.println("Error while flushing/closing fileWriter"+e.getMessage()+"--"+e.getCause());
	          }
	        }
	}

	// Validating the Customer Statement Report file
	public static List<TransactionDTO> doValidateCustomerStatement(int file) {
		
		//Create List for holding Customer Statement details objects
        List<TransactionDTO> transactionList = null;
        
        //Create List for holding failure Customer Statement report
        List<TransactionDTO> failureTransList = new ArrayList<TransactionDTO>();
        
        Set<Integer> uniqueReferNo = new HashSet<Integer>();
        double endBalance = 0; 

        try {
			if(file == 1)
				transactionList = doReadCSVCustomerStatement();
			else
				transactionList = doReadXMLCustomerStatement();
			
			// Validating transactions list
			for(TransactionDTO dto:transactionList) {
				//Calculating End Balance from Start Balance and Mutation
				endBalance  = dto.getStartBalance() + dto.getMutation();
				// Checking duplicate transaction references and Validating End Balance
				if(!uniqueReferNo.add(dto.getTxnsRefNo()) || endBalance != dto.getEndBalance())
					failureTransList.add(dto);
				
			}
		}catch(Exception ex) { 
			 System.out.println("Error occured while validating the Transactions List"+ex.getMessage()+"--"+ex.getCause());
		}
  		return failureTransList;
	}
	
	// Reading CSV Customer Statement report file
	public static List<TransactionDTO> doReadCSVCustomerStatement() {
		//Create List for holding Unique Customer Statement details objects
        List<TransactionDTO> transactionList = new ArrayList<TransactionDTO>();
		BufferedReader br = null;
		
		try {
            //Reading the csv file
            br = new BufferedReader(new FileReader(FILENAME_CSV));
            String line = "";
            //Read to skip the header
            br.readLine();
            
            //Reading from the second line
            while ((line = br.readLine()) != null) {
                String[] employeeDetails = line.split(COMMA_DELIMITER);
                
                if(employeeDetails.length > 0 ) {
                    //Save the Customer Statement details in TransactionDTO object
                	TransactionDTO txnDTO = new TransactionDTO(Integer.parseInt(employeeDetails[0]),
                            employeeDetails[1],employeeDetails[2],Double.parseDouble(employeeDetails[3]),
                            Double.parseDouble(employeeDetails[4]),Double.parseDouble(employeeDetails[5]));
                	// Adding transaction object into List
                	transactionList.add(txnDTO);
                }
            }
        }catch(Exception ex) {
        	System.out.println("Please input valid file");
        	System.out.println("Error occured while reading file"+ex.getMessage()+" "+ex.getCause());
        }finally {
        	try {
	            br.close();
	        }catch(IOException ie) {
	            System.out.println("Error occured while closing the BufferedReader"+ie.getMessage()+"--"+ie.getCause());
	        }
        }
		return transactionList;		
	}
	
	// Reading XML Customer Statement report file
	public static List<TransactionDTO> doReadXMLCustomerStatement() {
        //Create List for holding Customer Statement details objects
		 List<TransactionDTO> transactionList = new ArrayList<TransactionDTO>();;
		
		try{
			// Reading XML file from path
			File file = new File(FILENAME_XML);
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
					double mutation = Double.parseDouble(eElement.getElementsByTagName("mutation").item(0).getTextContent());
					
					 //Save the Customer Statement details in TransactionDTO object
                	TransactionDTO txnDTO = new TransactionDTO(txnRefNo,accountNum,description,
                			startBalance,mutation,endBalance);
                	//Adding transaction object into List
                	transactionList.add(txnDTO);
				}
			}
		}catch(Exception ex) {
			System.out.println("Error occured while reading file"+ex.getMessage()+"--"+ex.getCause());
        }
		return transactionList;
	}
} 
