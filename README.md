Please read the below instruction to run the program.

Tools used :
Maven 3.0.5
Eclipse 4.7
JDK 1.8

Step 1:

Kindly take the CustomerStatementReport project from given Github link and placed in your local system.


Step 2:

Kindly take the Cus_repository from Github link.

Change the location of Cus_repository folder in setting.xml.

Configure Cus_repository to Maven repository in Eclipse

->Eclipse ->Window -> Preference -> Maven -> User settings 
 
import the Cus_repository\setting.xml in User settings field

Step 3:

import the CustomerStatementReport maven projects into your eclispse.

--> File--> Import --> Maven --> Existing Maven projects --> browse the CustomerStatementReport project

Build the the CustomerStatementReport project by using maven

->Right click on CustomerStatementReport and Run As --> Maven build --> Goals --> clean install
Step 4:

Once successfully build the project, output jar will be generated at project target location.

Output jar -> CustomerStatementReport-0.0.1-SNAPSHOT-jar

Step 5:

go to the path: <<you placed projects in disk path>>\CustomerStatementReport-master\CustomerStatementReport\

Run the project jar file

-> java -cp target\CustomerStatementReport-0.0.1-SNAPSHOT-jar com.customer.validation.CustomerStatementValidation

Step 6:

follow that instruction and perform csv and xml file validation.

Step 7:

Post successful validation, invalid records will generated in failure reports.

Failure reports are generated at the below path

Path: <<you placed projects in disk path>> \CustomerStatementReport-master\CustomerStatementReport\target\classes\com\customer\files\

