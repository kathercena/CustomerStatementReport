package com.customer.validation;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
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
    //Global variables for file path
    private static final String FILENAME_CSV = "\\target\\classes\\com\\customer\\files\\records.csv";
    private static final String FILENAME_XML = "\\target\\classes\\com\\customer\\files\\records.xml";
    private static final String FAILURE_FILE_CSV = "\\target\\classes\\com\\customer\\files\\failure.csv";
    private static final String FAILURE_FILE_XML = "\\target\\classes\\com\\customer\\files\\failure.xml";
    
    private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".##");
    //CSV file header
    private static final String FILE_HEADER = "Reference,Description";
    private static String filePath = "";
    
    /** 
	 * Main method for executing the class
	 */
	public static void main(String[] args) {
		System.out.println("1. CSV file");
		System.out.println("2. XML file\n");
		
		/*Create List for holding failure Customer Statement report*/
        List<TransactionDTO> failureTransList = new ArrayList<TransactionDTO>();
		Scanner input = null;
		boolean flag = false;
		try {
			input = new Scanner(System.in);
			int choice = input.nextInt();
			
			/*Calling Customer Statement report validation method*/
			failureTransList = doValidateCustomerStatement(choice);
			
			if(failureTransList.size() > 0) {
				if(choice == 1)
					flag =	doGenerateFailureCSVReport(failureTransList);
				else
					flag = doGenerateFailureXMLReport(failureTransList);
			}
			if(flag)
				System.out.println("Customer Statement report successfully validated.Failure Reports are generated at below path\n"+filePath);
			
		}catch(Exception ex) {
			input.close();
			System.out.println("Error occured while reading file"+ex.getMessage()+" "+ex.getCause());
		}
    }

	/** 
	 * Validating the Customer Statement Report file
	 */
	public static List<TransactionDTO> doValidateCustomerStatement(int file) {
		/*Create List for holding Customer Statement details objects*/
        List<TransactionDTO> transactionList = null;
        List<TransactionDTO> failureTransList = new ArrayList<TransactionDTO>();
        
        Set<Integer> uniqueReferNo = new HashSet<Integer>();
        String formatBalance = ""; 
        double endBalance = 0.0; 

        try {
			if(file == 1)
				transactionList = doReadCSVCustomerStatement();
			else
				transactionList = doReadXMLCustomerStatement();
			
			/*Validating transactions list*/
			for(TransactionDTO dto:transactionList) {
				/*Calculating End Balance from Start Balance and Mutation*/
				formatBalance  = DECIMAL_FORMAT.format(dto.getStartBalance() + dto.getMutation());
				endBalance = Double.parseDouble(formatBalance);				
				/* Checking duplicate transaction references and Validating End Balance*/
				if(!uniqueReferNo.add(dto.getTxnsRefNo()) || endBalance != dto.getEndBalance())
					failureTransList.add(dto);
			}
		}catch(Exception ex) { 
			 System.out.println("Error occured while validating the Transactions List"+ex.getMessage()+"--"+ex.getCause());
		}
  		return failureTransList;
	}
	
	/** 
	 *Reading CSV Customer Statement report
	 */
	public static List<TransactionDTO> doReadCSVCustomerStatement() {
		List<TransactionDTO> transactionList = new ArrayList<TransactionDTO>();
		//read file into stream, try-with-resources
		List<String> list = new ArrayList<>();
		
		/*Getting report file from resource bundle*/
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
               	transactionList.add(txnDTO);
              }

        } catch (IOException ie) {
        	System.out.println("Error occured while reading file"+ie.getMessage()+" "+ie.getCause());
        } catch (Exception e) {
        	System.out.println("Error occured while processingS file"+e.getMessage()+" "+e.getCause());
        }
		return transactionList;
	}
	
	
	/** 
	 *Reading XML Customer Statement report
	 */
	public static List<TransactionDTO> doReadXMLCustomerStatement() {
        /*Create List for holding Customer Statement details objects*/
		 List<TransactionDTO> transactionList = new ArrayList<TransactionDTO>();;
		
		try{
			/*Getting XML report from resource bundle*/
		    String fileName = System.getProperty("user.dir")+FILENAME_XML;
		    
			// Reading XML file from path
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(fileName);
			document.getDocumentElement().normalize();

			/*Get the parent tag element from xml file*/
			NodeList nList = document.getElementsByTagName("record");
			
			/*Iterating the xml file to get the all records*/
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					/* Store the attribute values in local variable*/
					int txnRefNo = Integer.parseInt(eElement.getAttribute("reference"));
					String accountNum = eElement.getElementsByTagName("accountNumber").item(0).getTextContent();
					String description = eElement.getElementsByTagName("description").item(0).getTextContent();
					double startBalance = Double.parseDouble(eElement.getElementsByTagName("startBalance").item(0).getTextContent());
					double endBalance = Double.parseDouble(eElement.getElementsByTagName("endBalance").item(0).getTextContent());
					double mutation = Double.parseDouble(eElement.getElementsByTagName("mutation").item(0).getTextContent());
					
					 /*Save the Customer Statement details in TransactionDTO object*/
                	TransactionDTO txnDTO = new TransactionDTO(txnRefNo,accountNum,description,
                			startBalance,mutation,endBalance);
                	/*Adding transaction object into List*/
                	transactionList.add(txnDTO);
				}
			}
		}catch(Exception ex) {
			System.out.println("Error occured while reading file"+ex.getMessage()+"--"+ex.getCause());
        }
		return transactionList;
	}
	
	/** 
	 * @param failureTransList - Generating failure reports in CSV file format
	 */
	private static boolean doGenerateFailureCSVReport(List<TransactionDTO> failureTransList) {
		FileWriter fileWriter = null;
		boolean flag = false;
		try{
			/* Getting destination path to generate report*/ 
			filePath = System.getProperty("user.dir")+FAILURE_FILE_CSV;
			
			/*Writing report file to destination path*/
			fileWriter = new FileWriter(filePath);
			//Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());
			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			//Write a failure report to the CSV file
	        for (TransactionDTO transDTO : failureTransList) {
	        	fileWriter.append(String.valueOf(transDTO.getTxnsRefNo()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(transDTO.getDescription());
	            fileWriter.append(NEW_LINE_SEPARATOR);
	        }
	        flag = true;
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
		 return flag;
	}
	
	/** 
	 * @param failureTransList - Generating failure reports in XML file format
	 */
	private static boolean doGenerateFailureXMLReport(List<TransactionDTO> failureTransList) {
		try {
			 /*Getting destination path to generate report*/
			 filePath = System.getProperty("user.dir")+FAILURE_FILE_XML;

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
	
				/*Set an attribute to transaction element*/
				Attr attr = document.createAttribute("reference");
				attr.setValue(String.valueOf(transDTO.getTxnsRefNo()));
				transaction.setAttributeNode(attr);
				
				Element description = document.createElement("description");
				description.appendChild(document.createTextNode(transDTO.getDescription()));
				transaction.appendChild(description);
			 }
			
			 /*Create the xml file, transform the DOM Object to an XML File*/
			 TransformerFactory transformerFactory = TransformerFactory.newInstance();
			 Transformer transformer = transformerFactory.newTransformer();
			 transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			 transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			 DOMSource domSource = new DOMSource(document);
			 StreamResult streamResult = new StreamResult(new File(filePath));
			 transformer.transform(domSource, streamResult);
			 return true;
		}catch(Exception ex) {
			System.out.println("Error occured while writing the failure transactions report"+ex.getMessage()+"--"+ex.getCause());
			 return false;
		}
	}
} 
