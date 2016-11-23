#SemanticSCo Platform

##Gabriela D. A. Guardia

This file provides an overview of the SemanticSCo platform, instructions to deploy it to your own server and some usage information.

##Code Organization

	- BpmnPublisher: provides support to the publication of services into the platform service registry.
	- CompositeServiceEnactment: provides a GUI to enable the creation and execution of service compositions.
	- SemanticSCo: provides three components, namely, "Service Composition", "Coordinator" and "Composition and Execution Context".
	- Wsdl2BpmnMapper: provides support to the creation BPMN processes to specify the local behaviour of WSDL services.
	- Wsdl2Creator: provides support to the creation, edition and validation of WSDL service descriptions.
	- XMLSchemas: provides the primitive commands supported by the platform.
	

## Dependencies
We have used the following software/libraries/frameworks (we also present the version 
numbers we have used, you may use other versions, however some may generate incompatibilities):

	- JDK 7
	- Apache Tomcat 8.0.15
	- juddi-0.9rc4
	- MySQL 5

	
## Deployment

We have implemented the SemanticSCo service registry as an extended version of the jUDDI implementation provided 
by the UDDI specification. In order to publish services into the registry, create a DB to hold the jUDDI 
information. We have created the juddiv3 DB in MySQL using the following code:

	/**********************************************************
	CREATE DATABASE juddiv3 CHARACTER SET utf8 COLLATE utf8_bin; 
	CREATE USER 'localhost' @ '%' IDENTIFIED BY PASSWORD '*HASH';
	GRANT ALL ON juddiv3.* TO 'localhost' @ '%' ;
	GRANT ALL ON juddiv3 TO 'localhost' @ '%';
	GRANT CREATE ON juddiv3 TO 'root' @ '%'; 
	FLUSH PRIVILEGES;
	**********************************************************/

	Observations: 
		*juddiv3 DB tables will be automatically created when running BpmnPublisher
		** Change MySQL connection properties accordingly on "persistence.xml"
		*** Change the juddiv3 DB location on "juddiv3.xml"

Once you published services on the SemanticSCo registry, service execution is delegated to the external Activiti 
BPMN process engine for Java. In order to execute services, create a DB to hold Activiti information. We have 
created the activiti DB in MySQL using the following code:

	/**********************************************************
	CREATE DATABASE activiti CHARACTER SET utf8 COLLATE utf8_bin; 
	CREATE USER 'localhost' @ '%' IDENTIFIED BY PASSWORD '*HASH';
	GRANT ALL ON activiti.* TO 'localhost' @ '%' ;
	GRANT ALL ON activiti TO 'localhost' @ '%';
	GRANT CREATE ON activiti TO 'root' @ '%'; 
	FLUSH PRIVILEGES;
	**********************************************************/

	Create (MySQL) Activiti DB tables by running the following files (BpmnPublisher folder):	activiti.mysql.create.identity.sql
																								activiti.mysql55.create.engine.sql
																								activiti.mysql55.create.history.sql

## Usage

Once you deployed the SemanticSCo platform, you should run the BpmnPublisher component in order to publish your own services into the platform service registry. Once your services are available in the service registry, you can then start running the composition features of SemanticSCo. 

The SemanticSCo component encapsulates three subcomponents that are responsible for the management of the composition process. One of these components, namely Coordinator, is available as a SOAP web service which provides indirect access to the other two components. The Coordinator service provides a set of primitive commands that can be accessed through SOAP request/response messages by the CompositeServiceEnactment component (user interface). The primitive commands provided by the Coordinator are defined as follows:

	/***************************************************************************************************************
	1. DISCOVER INPUT SEMANTICS: requests the set of available ontological concepts that can be used to specify the 
	semantics of a user-provided dataset;
	
	2. DISCOVER FUNCTION SEMANTICS: requests the set of ontological concepts representing analysis activities that 
	can be performed on a given dataset;
	
	3. DISCOVER SERVICES: requests the set of services that provide a given functionality and which are capable of 
	consuming a given dataset;
	
	4. INCLUDE SERVICES: includes a given set of services in the composition chain;
	
	5. RESOLVE SERVICES: requests the set of services that are capable of producing data required as input for a 
	given set of services;
	
	6. VALIDATE INPUTS: validates a given set of service inputs, i.e., the association of user-provided datasets to 
	service inputs;
	
	7. COMPOSE SERVICES: requests the forward or backward composition of two services previously included in the 
	composition chain using the INCLUDE SERVICES command;
	
	8. GET EXECUTABLE SERVICES: requests the set of services that are ready for execution, i.e., services whose 
	required inputs are available; 
	
	9. ADD TO CONTEXT: stores a given dataset produced by the execution of a service.	
	***************************************************************************************************************/
	
	More information about the usage of all primitive commands can be found in package XML Schemas.
