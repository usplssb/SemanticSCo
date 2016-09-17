/*******************************************************************************
 * Copyright 2016 Gabriela D. A. Guardia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*******************************************************************************/

package semanticsco.compositionandexecutioncontext;

import semanticsco.servicecomposition.CLMManager;
import semanticsco.extra.DataElement;
import semanticsco.extra.Service;
import semanticsco.servicecomposition.ServiceComposer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.francetelecom.fl.causalLinkMatrix.entry.FT_SetOfTriples;
import org.francetelecom.fl.causalLinkMatrix.entry.FT_Triple;
import org.francetelecom.fl.causalLinkMatrix.label.FT_RowAndColumnLabel;

public class CompositionAndExecutionContext {

    //contextID = userID + sessionID (This allows to identify the user)
    public String contextID;
    
    //Store all details of all discovered services (it contains more info than the CLMManager)
    private HashMap<String,Service> discServices;
    
    //Store all details of services selected by user (it contains more info than the CLMManager)
    private HashMap<String,Service> selectedServices;

    //Causal Link Matrix (Keep track of discovered services, and their outputs)
    private CLMManager clm;
    
    //Store all service outputs IDs and service IDs to which the outputs belong to
    private HashMap<String,String> servOutputs;
    
    //Graph Composition (composition of services). Keep track of services that were executed and need to be executed
    private ServiceComposer graphComposition;
	
    //Store user-provided inputs for services - HashMap <SemanticType,ValidID_InAvailableValues> 
    private HashMap<String, List<String>> userInputs;
    
    //Store context information = user profile/context info + executed services outputs
    private HashMap<String,DataElement> availableValues;

    //Store user context/profile information - <SemanticType, LIST{ValidID_InAvailableValues}>
    private HashMap<String, List<String>> userContextInfo;
	
    //Store services from the GraphComposition that are ready to be executed or being executed
    private List<String> activeServices;
	
    //Store services from the GraphComposition that have already been executed
    private List<String> executedServices;
	

    //Empty Constructor
    public CompositionAndExecutionContext() {
		
        //Initialize lists
        this.discServices = new HashMap<>();
        this.selectedServices = new HashMap<>();
        this.servOutputs = new HashMap<>();
        this.graphComposition = new ServiceComposer();
        this.userInputs = new HashMap<>();
        this.availableValues = new HashMap<>();
        this.userContextInfo = new HashMap<>();
        this.activeServices = new LinkedList<>();
        this.executedServices = new LinkedList<>();
    }
    
    //Change discovered services to cope with multiple instances
    public List<Service> changeDiscServices(List<Service> services){
    
        //For each discovered service
        for(int i=0; i<services.size(); i++){
        
            //For each previously selected service
            int count = 0;
            for (Map.Entry<String,Service> entry : selectedServices.entrySet()){
                if(entry.getKey().contains(services.get(i).getIdentifier()))
                    count++;
            }
            
            //Set service ID
            services.get(i).setIdentifier("inst" + (count+1) + services.get(i).getIdentifier());
            
            //Set service input IDs
            LinkedList<DataElement> inputs = services.get(i).getInputs();
            for(Iterator it=inputs.iterator();it.hasNext();){
                DataElement element = (DataElement) it.next();
                element.setServiceID(services.get(i).getIdentifier());
                element.setIdentifier("inst" + (count+1)+element.getIdentifier());
            }
            
            //Set service output IDs
            LinkedList<DataElement> outputs = services.get(i).getOutputs();
            for(Iterator it=outputs.iterator();it.hasNext();){
                DataElement element = (DataElement) it.next();
                element.setServiceID(services.get(i).getIdentifier());
                element.setIdentifier("inst" + (count+1)+element.getIdentifier());
            }
            
        }
        
        return services;
		
    }
    
    //Change discovered services to cope with multiple instances during ResolveServ invocation
    public List<Service> changeDiscServicesForResolve(List<Service> services){
        
        List<Service> changedServices = new LinkedList<>();
        
        //For each discovered service
        for(int i=0; i<services.size(); i++){
            
            //For each previously selected service
            int count = 0;
            for(Map.Entry<String,Service> entry : selectedServices.entrySet()){
                
                if(entry.getKey().contains(services.get(i).getIdentifier())){
                    
                    Service previouslySelectedService = (Service)entry.getValue();
                    
                    Service newService = new Service();
                    newService.setIdentifier(previouslySelectedService.getIdentifier());
                    newService.setName(services.get(i).getName());
                    newService.setDescription(services.get(i).getDescription());
                    newService.setLocation(services.get(i).getLocation());
                    newService.setNamespace(services.get(i).getNamespace());
                    newService.setInterfaces(services.get(i).getInterfaces());
                    newService.setFunctionValue(services.get(i).getFunctionValue());
                    newService.setInputValue(services.get(i).getInputValue());
                    newService.setOutputValue(services.get(i).getOutputValue());
                    newService.setFunctions(services.get(i).getFunctions());
                    
                    String number = newService.getIdentifier().split("uddi:")[0];
                    
                    LinkedList<DataElement> originalInputs = services.get(i).getInputs();
                    LinkedList<DataElement> newInputs = new LinkedList<>();
                    for(Iterator it=originalInputs.iterator();it.hasNext();){
                        DataElement originalElement = (DataElement) it.next();
                        DataElement newElement = new DataElement(number+originalElement.getIdentifier(),originalElement.getName(),originalElement.getSemanticConcept(),originalElement.getSyntacticalType(),newService.getIdentifier());
                        newInputs.add(newElement);
                    }
                    newService.setInputs(newInputs);
                    
                    LinkedList<DataElement> originalOutputs = services.get(i).getOutputs();
                    LinkedList<DataElement> newOutputs = new LinkedList<>();
                    for(Iterator it=originalOutputs.iterator();it.hasNext();){
                        DataElement originalElement = (DataElement) it.next();
                        DataElement newElement = new DataElement(number+originalElement.getIdentifier(),originalElement.getName(),originalElement.getSemanticConcept(),originalElement.getSyntacticalType(),newService.getIdentifier());
                        newOutputs.add(newElement);
                    }
                    newService.setOutputs(newOutputs);
                                        
                    changedServices.add(newService);
                    
                    count++;
                }
            }
            
            //Do this part if count == 0 or other value -------------------------------------------
            
            //Set service ID
            services.get(i).setIdentifier("inst" + (count+1) + services.get(i).getIdentifier());
            
            //Set service input IDs
            LinkedList<DataElement> inputs = services.get(i).getInputs();
            for(Iterator it=inputs.iterator();it.hasNext();){
                DataElement element = (DataElement) it.next();
                element.setServiceID(services.get(i).getIdentifier());
                element.setIdentifier("inst" + (count+1)+element.getIdentifier());
            }
            
            //Set service output IDs
            LinkedList<DataElement> outputs = services.get(i).getOutputs();
            for(Iterator it=outputs.iterator();it.hasNext();){
                DataElement element = (DataElement) it.next();
                element.setServiceID(services.get(i).getIdentifier());
                element.setIdentifier("inst" + (count+1)+element.getIdentifier());
            }    
            changedServices.add(services.get(i));
            
            //-------------------------------------------------------------------------------------
        }
        
        return changedServices;
		
    }
    
    //Add discovered services to HashMap "DiscServices"
    public void addToDiscServices(List<Service> services){
        
        for(int i=0; i<services.size(); i++){
            if(!discServices.containsKey(services.get(i).getIdentifier()))
                discServices.put(services.get(i).getIdentifier(),services.get(i));
        }
		
    }
	
    //Return a service from the list of discovered services by its service ID
    public Service getDiscService(String serviceID){
		
	return discServices.get(serviceID);
		
    }
	
    //Add services to CLMManager
    public void addServCLM(List<Service> servicesToAdd){
	
        //Store services to be added to the CLMManager
        List<Service> listServices = new LinkedList<>();
        
	//Add all existing selected services to the list of services to be considered in the creation of the CLMManager
	Collection<Service> selecServs = this.selectedServices.values();
		
	//If some selected service was not previously selected
	if(!selecServs.containsAll(servicesToAdd)){
            
            //For each selected service
            for(Iterator it = servicesToAdd.iterator();it.hasNext();){
            
                Service s = (Service) it.next();
                
                //If selected service was not previously selected, add to list "listServices"
                if(!selecServs.contains(s))
                    listServices.add(s);
            }
            
            //Add all previously selected services to list "listServices"
            listServices.addAll(selecServs);
        
            //Create CLMManager with 1 row and 1 column, and no services
            CLMManager clmConstruction = new CLMManager(1, 1, null);
		
            //Compute the Causal Link Matrix with the list of selected services
            this.clm = clmConstruction.ComputeCLM(listServices);
            
        }
        
        //Else, if all selected services were previously selected, do nothing -> the CLMManager is not re-computed!
        
    }
    
    public boolean hasSelectedServiceKey(String serviceID){
        
        return this.selectedServices.containsKey(serviceID);
        
    }
	
    //Add service output ID and service ID to the servOutputs Hash, ensuring no duplicates
    public void addServOutput(String serviceId, String serviceOuputId) {
			
        if(!this.servOutputs.containsKey(serviceOuputId))
            this.servOutputs.put(serviceOuputId, serviceId);
		
    }
    
    /**
     * Add a service to the graph composition
     * 
     * @param serviceID - the service to be added to the ServCompGraph
     * 
    */
    public void addServToGraph(String serviceID) {
		
        //Add service to the ServCompGraph
	graphComposition.addServiceToGraph(discServices.get(serviceID),null,false);
		
    }
    
    /**
     * Add a service to the graph composition - Forward Composition!
     * 
     * @param serviceID - the service to be added to the ServCompGraph, which has outputs to resolve
     * services (inputs) that are already in the service composition
     * @param composingServicesIDs - services to be composed with the new service
     * 
    */
    public void addServToGraph(String serviceID, List<String> composingServicesIDs) {
        
        //Add service to the ServCompGraph
	if(serviceID != null)
            graphComposition.addServiceToGraph(discServices.get(serviceID),composingServicesIDs,true);
		
	//Close open nodes (services that had inputs composed after insertion of the newly added service)
	for(int i=0; i<composingServicesIDs.size(); i++)
            graphComposition.closeOpenNode(selectedServices.get(composingServicesIDs.get(i)));
		
    }
    
    /**
     * Close a service input (matched with a given service output) 
     * It also checks if the node (service) can be closed, i.e., if all service inputs were resolved!
     * 
     * @param service
     * @param inputID
     * @param outputID
    */
    public void closeInput(Service service, String inputID, String outputID) {
        
        //Add the outputID (a given service output) to the ExecutionValue of the Service Input
        //This allows to find the correct value in the CompositionAndExecutionContext.AvailableValues at execution time 
	for(int i=0; i<service.getInputs().size(); i++){
            if(service.getInputs().get(i).getIdentifier().equals(inputID))
		service.getInputs().get(i).setExecutionValue(outputID);
	}
		
	//Tries to close the open node
	graphComposition.closeOpenNode(service);
		
    }
    
    //Check if a given input (inputConcept) has some user-provided value
    public boolean hasUserProvidedInput(String inputConcept){
        return this.userInputs.containsKey(inputConcept);
    }
    
    //Return user-provided values for a given input (inputConcept)
    public List<String> getUserProvidedInputs(String inputConcept){
        return this.userInputs.get(inputConcept);
    }
    
    //Return data element of a given service input/ouput
    public DataElement getAvailableValue(String elementID){
	return this.availableValues.get(elementID);
    }
    
    //Check if a given input (inputConcept) has some user-context info
    public boolean hasUserContextInfo(String inputConcept){
        return this.userContextInfo.containsKey(inputConcept);
    }
    
    //Return user-context info for a given input (inputConcept)
    public List<String> getUserContextInfo(String inputConcept){
        return this.userContextInfo.get(inputConcept);
    }
    
    /**
     * Find services whose outputs semantically match the requested input concepts. 
     * It returns only the data element IDs, which allow to access the values using the getAvailableValue method 
     * 
     * @param serviceID
     * @param inputConcept input semantic concept
     * @return List of data element IDs that match the requested input concept
    */
    public List<String> findMatchingValues(String serviceID, String inputConcept){
		
	//Query CLMManager for all services whose outputs match the input semantic concept
	List<String> elementIDs = queriesCLMforOutput(serviceID,inputConcept);
		
	return elementIDs;
    }
    
    /**
     * Check if a given input concept is an output concept in the CLMManager. In case a given output 
 (or similar concept) exists in the CLMManager, retrieve the services IDs that provide the outputs and 
 the output IDs ("ServiceID;ElementID").
     * @param serviceID
     * @param inputConcept
     * @return 
    */
    public List<String> queriesCLMforOutput(String serviceID, String inputConcept) {

        List<String> result = new LinkedList<>();
		
        //Get CLMManager rows and columns labels
	FT_RowAndColumnLabel RCLabel = clm.ft_getRcLabel();
		
	//For each CLMManager column (output concept)
	for (int i=0; i<clm.ft_getColumn(); i++) {

            //If there is some CLMManager output concept that match the given input concept
            if(((String)RCLabel.ft_getConcept(i)).compareTo(inputConcept) == 0) {
				
                //For each CLMManager row -> saves{ServiceID of matching services}
		for (int j=0; j<clm.ft_getRow(); j++) {
                    
                    //Get set of triples of the CLMManager entry (j,i)
                    FT_SetOfTriples triples = clm.ft_getCellTriplets(j,i);

                    //If the set of triples is not empty
                    if (!(triples.isEmpty())) {
						
                        String tempServiceOutputID;
			String tempService;

                        //For each set of triples of the CLMManager entry (j,i)
                        FT_SetOfTriples setOftriple = (FT_SetOfTriples)clm.ft_getMatrix()[j][i];
			for(Iterator it= setOftriple.iterator();it.hasNext();) {
                            
                            //Get triple
                            FT_Triple triple = (FT_Triple) it.next();

                            //Extract service properties
                            tempService = triple.ft_getFirst().toString();
							
                            //Extract service ID and ouput ID of the service
                            int beginIndex = tempService.indexOf("(");
                            int endIndexOfServ = tempService.indexOf(";");
                            String clmServiceID = tempService.substring(beginIndex + 1, endIndexOfServ);
                            int endIndexOutputID = tempService.indexOf(";", endIndexOfServ + 2);
                            tempServiceOutputID = tempService.substring(beginIndex+1, endIndexOutputID);
                            				
                            //If found service ID is not the given service ID
                            if(!clmServiceID.equals(serviceID)){
				
                                //Add service output ID to list servicesToRetrieveCheck, ensuring no duplicates
                                boolean exists = false;
                                for (int k=0; k<result.size(); k++) {
                                    if (result.get(k).equals(tempServiceOutputID))
                                        exists = true;
                                }
                                if (!exists) 
                                    result.add(tempServiceOutputID);
                            }   
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    //Return a service from the list of selected services by its service ID.
    public Service getSelectedService(String serviceID){
		
        return this.selectedServices.get(serviceID);
    }
    
    /**
     * Checks if in the GraphComposition the targetService is a children node of the
     * sourceService, i.e. there is a deadlock 
     * 
     * @param sourceService - active service.
     * @param targetService - service that may be composed with the active service.
     * @return true if deadlock is found, false otherwise
     */     
    public boolean checkForDeadlock(Service sourceService, Service targetService) {
		
        //If source and target services are the same, deadlock is detected
        if(sourceService.getIdentifier().equals(targetService.getIdentifier()))
            return true;
		
        //Otherwise, check for deadlocks
        return graphComposition.checkDeadlocks(sourceService.getIdentifier(), targetService.getIdentifier());		
    }
    
    //Add services to the list of selected services
    public void addToSeleServices(List<Service> services){
        
        //For each service to be added
        for(int i=0; i<services.size(); i++){

            //If the service was not already selected, add it to selected services
            if(!selectedServices.containsKey(services.get(i).getIdentifier()))
                selectedServices.put(services.get(i).getIdentifier(),services.get(i));
	}
        
    }
    
    /**
     * Stores the input ID of an available value provided by the user
     * 
     * Each userInput element has a unique ID, which consists of the InputID + index. 
     * Then, in case a service has several values provided for the same input, we can grant 
 that all of the values can be used as suggestionValues, and can be correctly referred 
 into the CompositionAndExecutionContext.AvailableValues.
     * @param serviceID
     * @param inputID
     * @return 
    */
    public String addToUserInputs(String serviceID, String inputID) {
		
	String elementID = inputID;
		
	//Get input semantic concept
	String semanticConcept = "";
	for(int i=0; i<selectedServices.get(serviceID).getInputs().size(); i++){
            if (selectedServices.get(serviceID).getInputs().get(i).getIdentifier().equals(inputID))
                semanticConcept = selectedServices.get(serviceID).getInputs().get(i).getSemanticConcept();	
	}
	
        //If input ID already exist in list "availableValues" (context info), add a random value to input ID
        if(this.getAvailableValue(inputID) != null)
            elementID = inputID +"-" + (int) (Math.random() * 10000);
		
	//If input concept is not already in the list "userInputs", add it to the list
        if(!userInputs.containsKey(semanticConcept))
            userInputs.put(semanticConcept, new LinkedList<String>());
        
        //Add input ID to the list "userInputs"
        userInputs.get(semanticConcept).add(elementID);
		
	return elementID;
    }
    
    //Store the values provided for service parameters or user information/context.
    public void addToAvailableValues(String serviceID, String elementID, String elemValue, String elemDescription){
				
	//Create new data element and store info provided by parameters (not all information need to be stored)
        DataElement de = new DataElement();
        de.setIdentifier(elementID); //Set ID
        de.setExecutionValue(elemValue); //Set execution value
        de.setName(elemDescription); //Set name
        de.setServiceID(serviceID); //Set service ID associated to this data element
        
        //Add data element to list "availableValues" (context info)
        availableValues.put(elementID,de);

    }
	
    /**
     * Add an execution value (user-provided or selected) to a selected service input.
     * In fact, do not add the value itself, but a reference to the value (Data Element ID)
     * Thus, when we want to execute this service we know which input value has to be used for its execution. 
     * @param serviceID
     * @param inputID
     * @param inputValueID
    */
    public void addInputValueToSeleServ(String serviceID, String inputID, String inputValueID) {
		
        //Add/Overwrite execution value of service input
	for(int i=0; i<selectedServices.get(serviceID).getInputs().size(); i++){
            if(selectedServices.get(serviceID).getInputs().get(i).getIdentifier().equals(inputID))		
            	selectedServices.get(serviceID).getInputs().get(i).setExecutionValue(inputValueID);
	}
    }
    
    //Return the service ID of the service associated to a given output
    public String getServiceIdOfOutput(String outputID) {
			
	return servOutputs.get(outputID);

    }
    
    /**
     * Tries to close a node (service) in the GraphComposition.
     * 
     * @param service - the service to be closed
     * @return true if it closes the open node, or false if it cannot be closed (still require inputs)
     * 
    */
    public boolean closeOpenNode(Service service) {
		
        //Tries to close the open node
	return graphComposition.closeOpenNode(service);
	
    }
    
    //Return the syntactical type of a selected service input
    public String getInputSyntacticalType(String serviceID, String inputID){
        
	String syntacticalType = "";
	for(int i=0; i<selectedServices.get(serviceID).getInputs().size(); i++){
            if (selectedServices.get(serviceID).getInputs().get(i).getIdentifier().equals(inputID))
                syntacticalType = selectedServices.get(serviceID).getInputs().get(i).getSyntacticalType();	
	}
        
        return syntacticalType;
    }
    
    //Return the semantic type of a selected service input
    public String getInputSemanticType(String serviceID, String inputID){
        
	String semanticType = "";
	for(int i=0; i<selectedServices.get(serviceID).getInputs().size(); i++){
            if (selectedServices.get(serviceID).getInputs().get(i).getIdentifier().equals(inputID))
                semanticType = selectedServices.get(serviceID).getInputs().get(i).getSemanticConcept();	
	}
        
        return semanticType;
    }
    
    //Return the syntactical type of a selected service output
    public String getOutputSyntacticalType(String serviceID, String outputID){
        
        String syntacticalType = "";
	for(int i=0; i<selectedServices.get(serviceID).getOutputs().size(); i++){
            if (selectedServices.get(serviceID).getOutputs().get(i).getIdentifier().equals(outputID))
                syntacticalType = selectedServices.get(serviceID).getOutputs().get(i).getSyntacticalType();	
	}
        
        return syntacticalType;
    }
    
    //Return the open nodes (services)  from the graphComposition
    public List<String> getOpenNodes() {
		
	return graphComposition.getOpenNodes();
    
    }
    
    /**
     * Return the next services (IDs) from the service composition that are ready 
     * to be executed.
     * @return 
    */
    public List<String> nextServsToExec() {
					
    	//If the sets of active and executed services (nodes) are empty, i.e., the execution is going to start
	if(activeServices.isEmpty() && executedServices.isEmpty()){
            
            //Find the "root" nodes (with no incoming edges), which are the first nodes to be executed
            List<String> rootNodes = graphComposition.findRootNodes();
            
            //For each "root" node
            for(int i=0; i<rootNodes.size(); i++){
                
                //If service (node) is not opened, add it to list "activeServices"
		if(!getOpenNodes().contains(rootNodes.get(i)))
                    activeServices.add(rootNodes.get(i));
                
            }
            
	}//Else, if the execution already started, find the next services to be executed...
	else{

            //Gets all nodes (services) from graphComposition
            Set<String> graphNodes = graphComposition.getAllNodes();
			
            //For each node (service)
            Iterator it = graphNodes.iterator();
            while(it.hasNext()){
			
		//Get node ID of service to be analysed
		String nodeID = (String) it.next();
                
                //If the service was not already executed, is not opened and is not active
                //Removi esta parte do if p/ permitir que msm q 1 serviço tenha sido executado, ele pode ser de novo:
                // !executedServices.contains(nodeID) && 
                if(!graphComposition.getOpenNodes().contains(nodeID) && !activeServices.contains(nodeID)){
                    
                    //Get all parent nodes (previous services) of service to be analysed
                    List<String> parentNodes = graphComposition.findParentNodes(nodeID);
                    
                    //If its parent nodes were already executed or it has no parent nodes, add it to list "activeServices" (it can be executed)
                    if(executedServices.containsAll(parentNodes) || parentNodes.isEmpty())
                        activeServices.add(nodeID);					
                    
                }
            }
        }
        
        return activeServices;
    }
    
    //Move an executed service from the list "activeServices" to the list "executedServices"
    public void moveFromActiveToExecuted(String serviceID){
        
        //Remove service from list "activeServices"
        this.activeServices.remove(serviceID);
            
        //Add service to list "executedServices"
        this.executedServices.add(serviceID);
    }
	
    /**
     * Stores the ID of an available value from the user context information.
     * 
     * It stores based on the semantic type of the user context info value, i.e., a 
     * semantic type may have several context values (IDs) associate to them. The 
     * IDs must be unique, so that the correct value can be accessed in the list
     * "availableValues".
     * 
     * @param elementID
     * @param semtype
    */
    public void addToUserContextInfo(String elementID,String semtype) {
		
        //If semantic concept is not already contained in HashTable "userContextInfo", add it
        if(!userContextInfo.containsKey(semtype))
            userContextInfo.put(semtype, new LinkedList<String>());
        
        //Add elementID to list associated with the semtype
        userContextInfo.get(semtype).add(elementID);
            
    }
    
    //Clear user session context
    public void cleanContext() {
		
	this.contextID = null;
	this.clm = new CLMManager(1, 1, null);
	this.userContextInfo.clear();
	this.userInputs.clear();
	this.discServices.clear();
	this.selectedServices.clear();
	this.availableValues.clear();
	this.graphComposition = new ServiceComposer();
	this.activeServices = new LinkedList<>();
    
    }

}
