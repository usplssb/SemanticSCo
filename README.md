#SemanticSCo Platform

##Gabriela D. A. Guardia

This file provides an overview of the SemanticSCo platform and some instructions to deploy it to your own server

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
