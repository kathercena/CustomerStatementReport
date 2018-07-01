package com.customer.testcase;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestLogic {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	  @Test
	  public void file_assertions_examples() throws Exception {
		  File file = folder.newFile("E:\\records.csv");
		  BufferedReader br = new BufferedReader(new FileReader(file));
		  List<String> result = new ArrayList<String>();
		  String st = "";
		  while ((st = br.readLine()) != null){
			  result.add(st);
		  }
		  br.close();
		  assertEquals("Size of the list should be 2", 2, result.size());
		  
	  }

	 
	
	    
	    

	    

	   
}
