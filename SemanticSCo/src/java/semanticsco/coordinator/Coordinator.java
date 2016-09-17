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

package semanticsco.coordinator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import semanticsco.extra.Binding;
import semanticsco.extra.DataElement;
import semanticsco.extra.Interface;
import semanticsco.extra.Service;

import semanticsco.compositionandexecutioncontext.CompositionAndExecutionContext;
import semanticsco.servicecomposition.SemanticProvider;
import semanticsco.servicecomposition.ServiceDiscoverer;

import semanticsco.interactions.basictypes.*;
import semanticsco.interactions.addtocontext.*;
import semanticsco.interactions.discoverservices.*;
import semanticsco.interactions.getexecutableservices.GetExecutableServicesResponse;
import semanticsco.interactions.validateinputs.*;
import semanticsco.interactions.composeservices.*;
import semanticsco.interactions.discoverfunctionsemantics.*;
import semanticsco.interactions.discoverinputsemantics.*;
import semanticsco.interactions.getlabels.*;
import semanticsco.interactions.resolveservices.*;
import semanticsco.interactions.includeservices.*;


public class Coordinator {

    //Definition of Interaction Types Names
    static String GET_LABELS = "GET_LABELS";
    static String DISCOVER_INPUT_SEMANTICS = "DISCOVER_INPUT_SEMANTICS";
    static String DISCOVER_FUNCTION_SEMANTICS = "DISCOVER_FUNCTION_SEMANTICS";
    static String DISCOVER_SERVICES = "DISCOVER_SERVICES";
    static String INCLUDE_SERVICES = "INCLUDE_SERVICES";
    static String VALIDATE_INPUTS = "VALIDATE_INPUTS";
    static String COMPOSE_SERVICES = "COMPOSE_SERVICES";
    static String RESOLVE_SERVICES = "RESOLVE_SERVICES";
    static String GET_EXECUTABLE_SERVICES = "GET_EXECUTABLE_SERVICES";
    static String ADD_TO_CONTEXT = "ADD_TO_CONTEXT";    
    static String CLEAN_CONTEXT = "CLEAN_CONTEXT";
    

    //Empty Constructor
    public Coordinator() {
        
    }

    /**
     * @param interactionType primitive command
     * @param interactionArgs interaction arguments (in XML)
     * @param exeContext execution context
     * @return Interaction return value
     * @throws JAXBException
    */
    public String Coordinate(String interactionType, String interactionArgs,CompositionAndExecutionContext exeContext) throws JAXBException {
		
	//Interaction type return value (In XML form)
	String interactionReturn = null;
		
	//Forwards the interaction to the correspondent supporting coordination strategy
        if(interactionType.equals(GET_LABELS))
            interactionReturn = this.GetLabels(interactionArgs);
        else if(interactionType.equals(DISCOVER_INPUT_SEMANTICS))
            interactionReturn = this.DiscoverInputSemantics(interactionArgs);
        else if(interactionType.equals(DISCOVER_FUNCTION_SEMANTICS))
            interactionReturn = this.DiscoverFunctionSemantics(interactionArgs);
	else if(interactionType.equals(DISCOVER_SERVICES))
            interactionReturn = this.DiscoverServices(interactionArgs,exeContext);
        else if(interactionType.equals(INCLUDE_SERVICES))
            interactionReturn = this.IncludeServices(interactionArgs,exeContext);
        else if(interactionType.equals(VALIDATE_INPUTS))
            interactionReturn = this.ValidateInputs(interactionArgs,exeContext);
        else if(interactionType.equals(COMPOSE_SERVICES))
            interactionReturn = this.ComposeServices(interactionArgs,exeContext);
        else if (interactionType.equals(RESOLVE_SERVICES))
            interactionReturn = this.ResolveServices(interactionArgs,exeContext);
        else if (interactionType.equals(GET_EXECUTABLE_SERVICES))
            interactionReturn = this.GetExecutableServices(interactionArgs,exeContext);
        else if (interactionType.equals(ADD_TO_CONTEXT))
            interactionReturn = this.AddToContext(interactionArgs, exeContext);
        else if (interactionType.equals(CLEAN_CONTEXT))
            interactionReturn = this.CleaningContext(exeContext);
    
	return interactionReturn;
    }

    /**
     * GET_LABELS Interaction Type Supporting Strategy
 
     *   Call SemanticProvider SemanticSCo Component, returning a list of ontology
     *   concept labels
     * 
     * @param  Root concept
     * @return List of ontology concepts (in XML format)
    */
    private String GetLabels(String interactionArgs) throws JAXBException{
        
        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
        JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.getlabels");
                
        //Create an Unmarsheller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
        
        //Create the input stream to use as input for the unmarshaller
	InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
        
        //Unmarshal the interactionArgs
	GetLabelsRequest request = (GetLabelsRequest) unmarsh.unmarshal(interactionArgsStream);
        
        //Extract all arguments of the service call
        String root = request.getRootConcept();
        String ontologyURI = request.getOntologyUri();
        
        //Create the GetLabels Response
        semanticsco.interactions.getlabels.ObjectFactory objectfact = new semanticsco.interactions.getlabels.ObjectFactory();
        GetLabelsResponse response = objectfact.createGetLabelsResponse();
            
        //Create object SemanticProvider
        SemanticProvider sd = new SemanticProvider();
        
        //Return all labels of subclasses of variable root
        if(root != null && ontologyURI != null){
            
            LinkedList<SemanticConcept> labels = sd.findAllLabels(ontologyURI,root);
            
            //Add each function to GetLabels response
            for(Iterator it=labels.iterator();it.hasNext();){
                response.getSemanticConcept().add((SemanticConcept) it.next());
            }
            
            //Marshal the information into XML
            StringWriter sw = new StringWriter();
            Marshaller marsh = jc.createMarshaller();
            marsh.marshal(response, sw);

            String result = sw.toString();
            
            return result;
            
        }
        
        return "";
    }
    
    /**
     * DiscoverInputSemantics Interaction Type Supporting Strategy
 
     *  Call SemanticProvider SemanticSCo Component, returning a list of 
     *  ontology input concepts
     * 
     * @param Root concept
     * @return List of ontology concepts (in XML format)
    */
    private String DiscoverInputSemantics(String interactionArgs) throws JAXBException{
        
        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
        JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.discoverinputsemantics");
                
        //Create an Unmarsheller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
        
        //Create the input stream to use as input for the unmarshaller
	InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
        
        //Unmarshal the interactionArgs
	DiscoverInputSemanticsRequest request = (DiscoverInputSemanticsRequest) unmarsh.unmarshal(interactionArgsStream);
        
        //Extract all arguments of the service call
        String root = request.getRootConcept();
        String ontologyURI = request.getOntologyURI();
                
        //Create the DiscoverInputSemantics Response
        semanticsco.interactions.discoverinputsemantics.ObjectFactory objectfact = new semanticsco.interactions.discoverinputsemantics.ObjectFactory();
        DiscoverInputSemanticsResponse response = objectfact.createDiscoverInputSemanticsResponse();
            
        //Create object SemanticProvider
        SemanticProvider sd = new SemanticProvider();
        
        //Return a tree of ontology concepts representing all possible inputs
        if(root != null && ontologyURI != null){
            
            //Get tree of input concepts that are children of the root "data" concept
            Tree resultTree = sd.findAllInputs(ontologyURI,root);
                        
            //Set the DiscoverInputSemantics Response with the resulting tree of inputs
            response.setTreeOfInputConcepts(resultTree);
            
            //Marshal the information into XML
            StringWriter sw = new StringWriter();
            Marshaller marsh = jc.createMarshaller();
            marsh.marshal(response, sw);

            String result = sw.toString();
            
            return result;
            
        }
        
        return "";
    }
    
    /**
     * DiscoverFunctionSemantics Interaction Type Supporting Strategy
 
     *  Call SemanticProvider SemanticSCo Component, returning a list of ontology
     *  concepts that match the requested inputs
     * 
     * @param List of Inputs for discovery
     * @return List of ontology concepts (in XML format)
    */
    private String DiscoverFunctionSemantics(String interactionArgs) throws JAXBException{
        
        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
        JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.discoverfunctionsemantics");
                
        //Create an Unmarsheller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
        
        //Create the input stream to use as input for the unmarshaller
	InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
        
        //Unmarshal the interactionArgs
	DiscoverFunctionSemanticsRequest request = (DiscoverFunctionSemanticsRequest) unmarsh.unmarshal(interactionArgsStream);
        
        //Extract all arguments of the service call
        List<SemanticConcept> inputs = request.getInputConcept();
                
        //Create the SemDiscover Response
        semanticsco.interactions.discoverfunctionsemantics.ObjectFactory objectfact = new semanticsco.interactions.discoverfunctionsemantics.ObjectFactory();
        DiscoverFunctionSemanticsResponse response = objectfact.createDiscoverFunctionSemanticsResponse();
            
        //Create object SemanticProvider
        SemanticProvider sd = new SemanticProvider();
        
        //Return all function concepts associated with specified inputs
        if(!inputs.isEmpty()){
            
            //Get functions associated with each specified input concept
            LinkedList<SemanticConcept> functions = sd.findFunctionsWithInput(inputs);
            
            //Add each function to SemDiscover response
            for(Iterator it=functions.iterator();it.hasNext();)
                response.getFunctionConcept().add((SemanticConcept) it.next());
            
            //Marshal the information into XML
            StringWriter sw = new StringWriter();
            Marshaller marsh = jc.createMarshaller();
            marsh.marshal(response, sw);

            String result = sw.toString();
            
            return result;
            
        }
        
        return "";
    }
    
    /**
     * DiscoverServ Interaction Type Supporting Strategy
     * 
     * Call ServiceDiscoverer SemanticSCo Component, returning a list of services
     * that match the requested functions and inputs
     * 
     * @param List of Service Functions and Inputs for discovery - Several functions/inputs can be used for 
     * discovery (in XML format)
     * @return List of services (in XML format)
    */
    private String DiscoverServices(String interactionArgs,CompositionAndExecutionContext exeContext) throws JAXBException {

        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
        JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.discoverservices");
                
        //Create an Unmarsheller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
        
        //Create the input stream to use as input for the unmarshaller
	InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
        
        //Unmarshal the interactionArgs
	DiscoverServicesRequest request = (DiscoverServicesRequest) unmarsh.unmarshal(interactionArgsStream);
        
        //Extract all arguments of the service call
	InputFunctRequest arguments = request.getInputFunctRequest();
        
        //Get all request functions/goals and inputs
	List<String> listOfFunctions = arguments.getFunction();
        List<String> listOfInputs = arguments.getInput();
                
        //Create an instance of ServiceDiscoverer SemanticSCo component to perform service discovery
        ServiceDiscoverer sd = new ServiceDiscoverer(listOfFunctions,listOfInputs);
        
        //Perform service discovery based on requested functions and inputs
        List<Service> discoveredServices = sd.findServices();
        
        List<Service> changedServices = exeContext.changeDiscServices(discoveredServices);
        
        //Create the DiscoverServices Response
	semanticsco.interactions.discoverservices.ObjectFactory objectfact = new semanticsco.interactions.discoverservices.ObjectFactory();
	DiscoverServicesResponse response = objectfact.createDiscoverServicesResponse();

        //Store {Functions and Inputs, LIST(MatchingServices)} in discServMatching
        DiscoverServicesMatching discServsMatching = new DiscoverServicesMatching();
        discServsMatching.getFunction().addAll(listOfFunctions);
        discServsMatching.getInput().addAll(listOfInputs);
        
        //For each discovered service, store service information into DiscServMatching
        for(Iterator it = changedServices.iterator(); it.hasNext();){
            
            Service service = (Service) it.next();
            
            ServiceInfo servInfo = new ServiceInfo();
            servInfo.setID(service.getIdentifier()); //service ID
	    servInfo.setName(service.getName()); //service name
            servInfo.setDescription(service.getDescription()); //service description
            servInfo.setLocation(service.getLocation()); //service location
            servInfo.setNamespace(service.getNamespace()); //service namespace
            
            LinkedList<Interface> interfaces = service.getInterfaces(); //service interfaces
            for(Iterator iIt = interfaces.iterator(); iIt.hasNext();){
                
                Interface inter = (Interface) iIt.next();
                
                InterfaceInfo interInfo = new InterfaceInfo();
                interInfo.setID(inter.getIdentifier()); //interface id
                interInfo.setName(inter.getName()); //interface name
                interInfo.setLocation(inter.getLocation()); //interface location
                interInfo.setNamespace(inter.getNamespace()); //interface namespace
                
                LinkedList<Binding> bindings = inter.getBindings(); //service bindings
                for(Iterator bIt = bindings.iterator(); bIt.hasNext();){
                    
                    Binding binding = (Binding) bIt.next();
                    
                    BindingInfo bindingInfo = new BindingInfo();
                    
                    bindingInfo.setName(binding.getName()); //binding name
                    bindingInfo.setLocation(binding.getLocation()); //binding location
                    bindingInfo.setNamespace(binding.getNamespace()); //binding namespace
                    bindingInfo.setProtocol(binding.getProtocol()); //binding protocol
                    bindingInfo.setAddress(binding.getAddress()); //binding address
                    
                    //Add bindingInfo to interfaceInfo
                    interInfo.getBinding().add(bindingInfo);
                    
                }
                
                //Add interfaceInfo to serviceInfo
                servInfo.getInterface().add(interInfo);
            }
            
            LinkedList<String> functions = service.getFunctions(); //service functionality
            for(Iterator fIt = functions.iterator(); fIt.hasNext();)
                servInfo.getFunction().add((String) fIt.next());
            
            LinkedList<DataElement> inputs = service.getInputs(); //service inputs
            for(Iterator iIt = inputs.iterator(); iIt.hasNext();){
                
                DataElement de = (DataElement) iIt.next();
                
                Input input = new Input(); 
                input.setID(de.getIdentifier()); //input id
                input.setName(de.getName()); //input name
                input.setSemanticalType(de.getSemanticConcept()); //input semantic concept
                input.setSyntacticalType(de.getSyntacticalType()); //input syntactical type
                
                servInfo.getInput().add(input);
                
            }
            
            LinkedList<DataElement> outputs = service.getOutputs(); //service outputs
            for(Iterator oIt = outputs.iterator(); oIt.hasNext();){
                
                DataElement de = (DataElement) oIt.next();
                
                Output output = new Output();
                output.setID(de.getIdentifier()); //output id
                output.setName(de.getName()); //output name
                output.setSemanticalType(de.getSemanticConcept()); //input output concept
                output.setSyntacticalType(de.getSyntacticalType()); //input output type
                
                servInfo.getOutput().add(output);
                
            } 
            
            discServsMatching.getServiceInfo().add(servInfo);
            
        }
        
        //Add DiscServMatching to the response message
        response.getDiscoverServicesMatching().add(discServsMatching);
        
        //Add discovered services to the CompositionAndExecutionContext.DiscoveredServices
        exeContext.addToDiscServices(changedServices);
        
        //Marshals the information into XML
	StringWriter sw = new StringWriter();
	Marshaller marsh = jc.createMarshaller();
	marsh.marshal(response, sw);

	String result = sw.toString();
        
        return result;
    }
    
    /**
     * IncludeServices Interaction Type Supporting Strategy
     * 
     * This method stores included services in the GraphComposition, 
     * without having to compose them with other services. 
     * 
     * Only add services and make the inputs and nodes opened! 
     *
     * @param Service inclusion (service unique ID)
     * @return Service Inputs information
     * @throws JAXBException
    */
    private String IncludeServices(String interactionArgs, CompositionAndExecutionContext exeContext) throws JAXBException {
        
        //Variable to store included service IDs contained in the message
        List<String> includedServiceIDs = new LinkedList<>();
        
        //Variable to store details of all included service IDs contained in the message
	List<Service> includedServices = new LinkedList<>();
        
        //Structure to store included services
	HashMap<String,String> servTypeServSele = new HashMap<>();
        
        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
	JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.includeservices");
	
        //Create an unmarsheller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
	
        //Create the input stream to use as input for the unmarshaller
	InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
		
        //Unmarshal the interactionArgs
	IncludeServicesRequest request = (IncludeServicesRequest) unmarsh.unmarshal(interactionArgsStream);
		
        //Get all arguments of the service call
	List<IncludedServiceStructure> requestMessage = request.getIncludedService();	
        
        //For each IncludedService contained in the message, extract and save relevant info
	for(int i=0; i<requestMessage.size(); i++){
			
            //Extract and save included service ID
            includedServiceIDs.add(requestMessage.get(i).getIncludedServiceID());
            
            //Extract and save tuple <IncludedServiceID,ServiceFunction>
            if(requestMessage.get(i).getIncludedServiceFunction() != null)				
		servTypeServSele.put(requestMessage.get(i).getIncludedServiceID(),requestMessage.get(i).getIncludedServiceFunction());           
            
        }
        
        //For each included service ID contained in the message, get details from list DiscServices and
        // store them in the lists "includedServices" and "servicesToCheckForClosing"
	for (int i=0; i<includedServiceIDs.size(); i++)
            includedServices.add(exeContext.getDiscService(includedServiceIDs.get(i)));
        
        //Stores included services in the CLM (only if they were not previously included) 
        exeContext.addServCLM(includedServices);
        
        //Store each included service in the GraphComposition.
	for(int i=0; i<includedServices.size(); i++){
            
            //If included service was not already included in the IncludedServices + GraphComposition
            if(!exeContext.hasSelectedServiceKey(includedServices.get(i).getIdentifier())){
                
                //Add all service outputs to the CompositionAndExecutionContext.ServOutputs
                for(int j=0; j<includedServices.get(i).getOutputs().size(); j++)
                    exeContext.addServOutput(includedServices.get(i).getIdentifier(), includedServices.get(i).getOutputs().get(j).getIdentifier());
			
                //Add the service to the GraphComposition, but do not compose it with other services
                //Only add the service and make its input open and the node (service) itself open
                if(!servTypeServSele.isEmpty()){
		
                    //Add service to the ServCompGraph (not connected to any other service)
                    exeContext.addServToGraph(includedServices.get(i).getIdentifier());
				
                }
            }  
        }
        
        //For each included service, set suggestion values
        for (int i=0; i<includedServices.size(); i++) {
		
            //For each service input
            for (int j=0; j<includedServices.get(i).getInputs().size(); j++) {
                
                //Check User-provided Inputs -----------------------------------
                //Check if there are user-provided inputs that can be suggested as inputs for the included service inputs
                
                //If service input has some user-provided value
		if(exeContext.hasUserProvidedInput(includedServices.get(i).getInputs().get(j).getSemanticConcept())){
					
                    //Get input IDs with user-provided values
                    List<String> inputIDs = exeContext.getUserProvidedInputs(includedServices.get(i).getInputs().get(j).getSemanticConcept());
                    
                    //For each input ID, get data from "availableValues" and add it to "suggestionValues"
                    for(int k=0; k<inputIDs.size(); k++){
		
                        //Get data element from "availableValues"
			DataElement element = exeContext.getAvailableValue(inputIDs.get(k));
						
			//Store data element in the "suggestionValues" of the Input element
			if(!includedServices.get(i).getInputs().get(j).hasSuggestionValue(element))
                            includedServices.get(i).getInputs().get(j).addSuggestionValue(element);
                        
                    }
                    
                }
                
                //--------------------------------------------------------------
                
                //Check UserContextInfo ----------------------------------------
		//Check if there are user context info that can be suggested as inputs for the included service inputs
                
                //If service input has some user context info
		if(exeContext.hasUserContextInfo(includedServices.get(i).getInputs().get(j).getSemanticConcept())){
					
                    //Get input IDs with user context info
                    List<String> inputIDs = exeContext.getUserContextInfo(includedServices.get(i).getInputs().get(j).getSemanticConcept());
					
                    //For each input ID, get data from "availableValues" and add it to "suggestionValues"
                    for(int k=0; k<inputIDs.size(); k++){
						
                        //Get data element from "availableValues"
			DataElement element = exeContext.getAvailableValue(inputIDs.get(k));
						
			//Store data element in the "suggestionValues" of the Input element
			if(!includedServices.get(i).getInputs().get(j).hasSuggestionValue(element))
                            includedServices.get(i).getInputs().get(j).addSuggestionValue(element);
			
                    }
                    
                }
                
                //--------------------------------------------------------------
                
		//Check CLM ----------------------------------------------------
                /*Check CLM to see if the included service can be composed with some service output or user info
                 * context value. If so, these values are added to the suggestion values of the service input. 
                 * The user can then decide which suggestion better fits his needs.*/
		
                //Find services with outputs that semantically match the requested input concepts 
		List<String> matchingServiceIDs = exeContext.findMatchingValues(includedServices.get(i).getIdentifier(),includedServices.get(i).getInputs().get(j).getSemanticConcept());
		
                //For each retrieved service ("ServiceID;OutputID")
		for (int k=0; k<matchingServiceIDs.size(); k++) {
                    
                    //Extract service ID and ouput ID
                    int index = matchingServiceIDs.get(k).indexOf(";");
		    String serviceID = matchingServiceIDs.get(k).substring(0, index);
		    String outputID = matchingServiceIDs.get(k).substring(index + 1, matchingServiceIDs.get(k).length());
					
                    //If the matching output contains a value, put it as a suggestion value (ensures no duplicates)
                    if (exeContext.getAvailableValue(outputID) != null){
                        if(!includedServices.get(i).getInputs().get(j).hasSuggestionValue((exeContext.getAvailableValue(outputID))))
                            includedServices.get(i).getInputs().get(j).addSuggestionValue(exeContext.getAvailableValue(outputID));
                    } //Else, 
                    else{
		
                        //Get details of matching service
			Service matchingService = exeContext.getSelectedService(serviceID); 

                        /*Check if the service with the matching output can be composed with the included service,
                            i.e., if this service is not a children of the included service (deadlock situation)
                        */
			if(!exeContext.checkForDeadlock(includedServices.get(i),matchingService)){
                            
                            //For each output of the matching service 
                            for(int m=0; m<matchingService.getOutputs().size(); m++){
                                
                                //Add output element of the matching service to input suggestion value of included service
				if(outputID.equals(matchingService.getOutputs().get(m).getIdentifier())){
                                    if(!includedServices.get(i).getInputs().get(j).hasSuggestionValue(matchingService.getOutputs().get(m)))
                                        includedServices.get(i).getInputs().get(j).addSuggestionValue(matchingService.getOutputs().get(m));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        //Store included services in the CompositionAndExecutionContext (ensuring no duplicates)
	exeContext.addToSeleServices(includedServices);

        //Create the SeleServ Response
	semanticsco.interactions.includeservices.ObjectFactory objectfact = new semanticsco.interactions.includeservices.ObjectFactory();
	IncludeServicesResponse response = objectfact.createIncludeServicesResponse();
	
        //For each included service, store service information into SeleServ Response
        for(int i=0; i<includedServices.size(); i++){
	
            //Service Info
            ServiceInfo servInfo = new ServiceInfo();
			
            //Get serviceID
            String serviceID = includedServices.get(i).getIdentifier();
	
            //Get service from the CompositionAndExecutionContext.SelectedServices
            Service service = exeContext.getSelectedService(serviceID);
			
            servInfo.setID(service.getIdentifier()); //service ID  OK
            servInfo.setName(service.getName()); //service name OK
            servInfo.setDescription(service.getDescription()); //service description OK
            servInfo.setLocation(service.getLocation()); //service location
            servInfo.setNamespace(service.getNamespace()); //service namespace
            
            LinkedList<Interface> interfaces = service.getInterfaces(); //service interfaces
            for(Iterator iIt = interfaces.iterator(); iIt.hasNext();){
                
                Interface inter = (Interface) iIt.next();
                
                InterfaceInfo interInfo = new InterfaceInfo();
                interInfo.setID(inter.getIdentifier()); //interface id
                interInfo.setName(inter.getName()); //interface name
                interInfo.setLocation(inter.getLocation()); //interface location
                interInfo.setNamespace(inter.getNamespace()); //interface namespace
                                
                LinkedList<Binding> bindings = inter.getBindings(); //service bindings
                for(Iterator bIt = bindings.iterator(); bIt.hasNext();){
                    
                    Binding binding = (Binding) bIt.next();
                    
                    BindingInfo bindingInfo = new BindingInfo();
                    
                    bindingInfo.setName(binding.getName()); //binding name
                    bindingInfo.setLocation(binding.getLocation()); //binding location
                    bindingInfo.setNamespace(binding.getNamespace()); //binding namespace
                    bindingInfo.setProtocol(binding.getProtocol()); //binding protocol
                    bindingInfo.setAddress(binding.getAddress()); //binding address
                                        
                    //Add bindingInfo to interfaceInfo
                    interInfo.getBinding().add(bindingInfo);
                    
                }
                
                //Add interfaceInfo to serviceInfo
                servInfo.getInterface().add(interInfo);
                
            }
            
            LinkedList<String> functions = service.getFunctions(); //service functionality
            for(int j=0;j<functions.size();j++)
                servInfo.getFunction().add(functions.get(j));
            
            for(int j=0; j<service.getInputs().size(); j++) { //service inputs
                
                Input input = new Input();
		input.setID(service.getInputs().get(j).getIdentifier()); //input id
                input.setName(service.getInputs().get(j).getName()); //input name
                input.setSemanticalType(service.getInputs().get(j).getSemanticConcept()); //input semantic concept
                input.setSyntacticalType(service.getInputs().get(j).getSyntacticalType()); //input syntactical type
		
                //For each suggestion value of input
                for(int k=0; k<service.getInputs().get(j).getSuggestionValues().size(); k++){
                    
                    SuggestionValue sugestVal = new SuggestionValue();
		
                    sugestVal.setValueID(service.getInputs().get(j).getSuggestionValues().get(k).getIdentifier());
                    sugestVal.setValueDescription(service.getInputs().get(j).getSuggestionValues().get(k).getName());
                    sugestVal.setValue(service.getInputs().get(j).getSuggestionValues().get(k).getExecutionValue());
                    
                    input.getSuggestedValue().add(sugestVal);
                    
		}
		
                //If input has execution value
                if(service.getInputs().get(j).getExecutionValue() != null){
                    //If there is a data element for this execution value, set execution value of input
                    if(exeContext.getAvailableValue(service.getInputs().get(j).getExecutionValue()) != null)
			input.setExecutionValue(exeContext.getAvailableValue(service.getInputs().get(j).getExecutionValue()).getExecutionValue());
		}
		
                servInfo.getInput().add(input);
                
            }
            
            //Store outputs
            for(int j=0; j<service.getOutputs().size(); j++) {
                
                Output output = new Output();
		output.setID(service.getOutputs().get(j).getIdentifier()); //output id
                output.setName(service.getOutputs().get(j).getName()); //output name
                output.setSemanticalType(service.getOutputs().get(j).getSemanticConcept()); //output semantic concept
                output.setSyntacticalType(service.getOutputs().get(j).getSyntacticalType()); //output syntactical type		
                				
		servInfo.getOutput().add(output);
                
            }
            
            //Add ServiceInfo to response message
            response.getServiceInfo().add(servInfo);
            
        }
        
        //Marshals the information into XML
	StringWriter sw = new StringWriter();
	Marshaller marsh = jc.createMarshaller();
	marsh.marshal(response, sw);

	String result = sw.toString();
        
        return result;
    }
    
    /**
     * ValidateInputs Interaction Type Supporting Strategy
     * 
     * Validate inputs of a service. Validate inputs of a service with user-provided input data. 
     * If there are invalid inputs, i.e., values that don't semantically or syntactically match 
     * what the service requires as input, they are returned as invalid inputs.
     * 
     * @param interactionTypeArgs
     * @param exeContext
     * @return Invalid Inputs
     * @throws JAXBException
    */
    private String ValidateInputs(String interactionArgs, CompositionAndExecutionContext exeContext) throws JAXBException {
        
        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
	JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.validateinputs");
		
        //Create an unmarsheller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
	
        //Create the input stream to use as input for the unmarshaller
        InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
		
        //Unmarshal the interactionArgs
	ValidateInputsRequest request = (ValidateInputsRequest) unmarsh.unmarshal(interactionArgsStream);
		
        //Get all services inputs to validate - one InputValidation structure per service
	List<InputValidation> servicesToValidate = request.getInputValidation();
			
	//Create a ValidateInputs response message
	semanticsco.interactions.validateinputs.ObjectFactory objectfact = new semanticsco.interactions.validateinputs.ObjectFactory();
	ValidateInputsResponse response = objectfact.createValidateInputsResponse();

        //For each service containing inputs to be validated (structure InputValidation)
        for(int h=0; h<servicesToValidate.size(); h++){
            
            //Get service ID
            String validatingServiceID = servicesToValidate.get(h).getServiceID();       
            
            //Get all service inputs to be validated
            List<InputToValidate> inputsToValidate = servicesToValidate.get(h).getInputToValidate();

            //Structures to store information about service inputs to be validated
            List<String> invalidInputIDs = new LinkedList<>(); //invalid input IDs
            HashMap<String,String> validInputedInputs = new HashMap<>(); // user-provided inputs {inputID,inputedValue}
            HashMap<String,String> inputedInputsSemantics = new HashMap<>(); // user-provided inputs {inputID,dataSemantics}
            HashMap<String,String> inputedInputsSyntax = new HashMap<>(); // user-provided inputs {inputID,dataSyntax}
                        
            //Variable to hold invalid inputs for a given serviceID (to be included in the response message)
            InvalidInputs servInvalid = new InvalidInputs();
			
            //For each input to be validated
            for (int i=0; i<inputsToValidate.size(); i++){
		if(inputsToValidate.get(i).getInputedValue() != null){
                    
                    //Add (possibly valid) user-provided input as a tuple {inputID,inputedValue} to list "validInputedInputs"
                    validInputedInputs.put(inputsToValidate.get(i).getInputID(),inputsToValidate.get(i).getInputedValue());
                    
                    //Add (possibly valid) user-provided input as a tuple {inputID,dataSemantics} to list "inputedInputsSemantics"
                    inputedInputsSemantics.put(inputsToValidate.get(i).getInputID(),inputsToValidate.get(i).getDataSemantics());
                    
                    //Add (possibly valid) user-provided input as a tuple {inputID,dataSyntax} to list "inputedInputsSyntax"
                    inputedInputsSyntax.put(inputsToValidate.get(i).getInputID(),inputsToValidate.get(i).getDataSyntax());
                }
            }
            
            //For each user-provided input value
            Set<String> userInputID = validInputedInputs.keySet();
            Iterator it = userInputID.iterator();
            LinkedList<String> idsToRemove = new LinkedList<>();
            while (it.hasNext()) {
	
                //Get input ID being validated
		String inputID = (String) it.next();
                
                //Get semantics and syntax of user-provided value for input
		String inputedValueSemantics = inputedInputsSemantics.get(inputID);
                String inputedValueSyntax = inputedInputsSyntax.get(inputID);
                
                //Get syntactical and semantic type of input being validated
                String syntacticalInputType = exeContext.getInputSyntacticalType(validatingServiceID,inputID);
                String semanticInputType = exeContext.getInputSemanticType(validatingServiceID,inputID);
                
                //If there are defined syntactical/semantical types, check compatibility
                if(!syntacticalInputType.equals("") && !inputedValueSyntax.equals("") && !semanticInputType.equals("") && !inputedValueSemantics.equals("")){
                    
                    //If inputed value does not match syntactical type of input being validated, add inputID to list "idsToRemove"
                    if(!syntacticalInputType.equals(syntacticalInputType))
                        idsToRemove.add(inputID);
                    else{
                        
                        //If inputed value does not match semantic type of input being validated, add inputID to list "idsToRemove"
                        SemanticProvider sd = new SemanticProvider();
                        if(!sd.checkSemanticCompatibility(semanticInputType,inputedValueSemantics))
                            idsToRemove.add(inputID);
                        
                    }
                }
            }    
            
            //Remove each invalid input ID from lists "validInputedInputs", "inputedInputsSemantics" and "inputedInputsSyntax"
            if(!idsToRemove.isEmpty()){
                
                it = idsToRemove.iterator();
                while (it.hasNext()) {
	
                    //Get id to remove
                    String idToRemove = (String) it.next();
                
                    //Remove it from lists
                    validInputedInputs.remove(idToRemove);
                    inputedInputsSemantics.remove(idToRemove);
                    inputedInputsSyntax.remove(idToRemove);
                        
                    //Add input ID to list "invalidInputIDs"
                    invalidInputIDs.add(idToRemove);            
                
                }
            }
               
            //If there is some invalid input, set service ID into response message
            if(!invalidInputIDs.isEmpty())
                servInvalid.setServiceID(validatingServiceID);
	
            //For each invalid (empty) input, add input ID to response message
            for(int i=0; i<invalidInputIDs.size(); i++)
                servInvalid.getInvalidInputID().add(invalidInputIDs.get(i));
                            
            //For each user-provided input value
            userInputID = validInputedInputs.keySet();
            it = userInputID.iterator();
	    while (it.hasNext()) {	
                
                //Get input ID being validated
		String inputID = (String) it.next();
                
                //Get user-provided value for input
		String inputValue = validInputedInputs.get(inputID);
                
                //Add (possibly modifying) input ID to list "userInputs" and return the inputID
		String elementID = exeContext.addToUserInputs(validatingServiceID,inputID);
		
                //Create DataElement containing returned elementID and input value, and add it to list "availableValues"
		exeContext.addToAvailableValues(null,elementID,inputValue,null);
	
		//Add user-provided input value to the selected service input execution value
		//In fact, do not add the value itself, but a reference to the value (Data Element ID)
		exeContext.addInputValueToSeleServ(validatingServiceID,inputID,elementID);
            }
            
            //Tries to close the service (node) whose inputs are being validated
            exeContext.closeOpenNode(exeContext.getSelectedService(validatingServiceID));
			
            //If there are invalid service inputs, store them in the response message
            if(!servInvalid.getInvalidInputID().isEmpty()){
		response.getInvalidInputs().add(servInvalid);
            }
            
        }
        
        //Marshals the information into XML
	StringWriter sw = new StringWriter();
	Marshaller marsh = jc.createMarshaller();
	marsh.marshal(response, sw);

	String result = sw.toString();

	return result; 
        
    }
    
    /**
     * ComposeServices Interaction Type Supporting Strategy
     * 
     * Compose inputs of a service with outputs provided by previous service(s) in the composition chain.
     * 
     * Support forward and backward composition
     * 
     * @param interactionTypeArgs
     * @param exeContext
     * @return Invalid Inputs
     * @throws JAXBException
    */
    private String ComposeServices(String interactionArgs, CompositionAndExecutionContext exeContext) throws JAXBException {
        
        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
	JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.composeservices");
		
        //Create an unmarsheller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
	
        //Create the input stream to use as input for the unmarshaller
        InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
		
        //Unmarshal the interactionArgs
	ComposeServicesRequest request = (ComposeServicesRequest) unmarsh.unmarshal(interactionArgsStream);
		
        //Get all services inputs to validate - one CompositeStructure structure per service
	List<CompositeStructure> servicesToValidate = request.getCompositeStructure();
			
	//Create a ValidateInputs response message
	semanticsco.interactions.composeservices.ObjectFactory objectfact = new semanticsco.interactions.composeservices.ObjectFactory();
	ComposeServicesResponse response = objectfact.createComposeServicesResponse();

        //For each service containing inputs to be validated (structure CompositeStructure)
        for(int h=0; h<servicesToValidate.size(); h++){
            
            //Get service ID
            String validatingServiceID = servicesToValidate.get(h).getServiceID();       
            
            //Get all service inputs to be validated
            List<CompositePair> inputsToValidate = servicesToValidate.get(h).getCompositePair();

            //Structures to store information about service inputs to be validated
            List<String> invalidInputIDs = new LinkedList<>(); //input IDs when inputs are invalid
            HashMap<String,String> validSelectedInputs = new HashMap<>(); // {inputID,selectedOutput}         
            
            //Variable to hold invalid inputs for a given serviceID (to be included in the response message)
            InvalidInputs servInvalid = new InvalidInputs();
			
            //For each input to be validated, add tuple {inputID,selectedOutput} to list "validSelectedInputs"
            for (int i=0; i<inputsToValidate.size(); i++) 
                if(inputsToValidate.get(i).getSelectedOutput() != null)
                    validSelectedInputs.put(inputsToValidate.get(i).getInputID(),inputsToValidate.get(i).getSelectedOutput());
                
            //For each selected input value
            Set<String> userSelectInputID = validSelectedInputs.keySet();
            Iterator it = userSelectInputID.iterator();
            LinkedList<String> idsToRemove = new LinkedList<>();
	    while (it.hasNext()) {
                
                //Get inputID
		String inputID = (String) it.next();
                
                //Get syntactical type of input being validated
                String syntacticalInputType = exeContext.getInputSyntacticalType(validatingServiceID,inputID);
                
                //Get selected value for input (the output ID of another service already contained in the composition)
		String outputID = validSelectedInputs.get(inputID);
                
                //Get ID of the service containing this output
		String matchServID = exeContext.getServiceIdOfOutput(outputID);
                
                //Get syntactical type of selected output
                String syntacticalOutputType = exeContext.getOutputSyntacticalType(matchServID,outputID);
                
                //If there are defined syntactical types
                if(!syntacticalInputType.equals("") && !syntacticalOutputType.equals("")){
                    
                    //If selected output does not match syntactical type of input
                    if(!syntacticalInputType.equals(syntacticalOutputType))
                        idsToRemove.add(inputID);
                        
                }
                
            }
            
            //Remove each invalid input ID from lists "validSelectedInputs"
            if(!idsToRemove.isEmpty()){
                
                it = idsToRemove.iterator();
                while (it.hasNext()) {
	
                    //Get id to remove
                    String idToRemove = (String) it.next();
                
                    //Remove input ID from list "validSelectedInputs"
                    validSelectedInputs.remove(idToRemove);
                        
                    //Add input ID to list "invalidInputIDs"
                    invalidInputIDs.add(idToRemove);           
                
                }
            }
	
            //If there is some invalid input, set service ID into response message
            if(!invalidInputIDs.isEmpty())
                servInvalid.setServiceID(validatingServiceID);
	
            //For each invalid (empty) input, add input ID to response message
            for(int i=0; i<invalidInputIDs.size(); i++)
		servInvalid.getInvalidInputID().add(invalidInputIDs.get(i));
            
            //For each selected input value
            userSelectInputID = validSelectedInputs.keySet();
            it = userSelectInputID.iterator();
	    while (it.hasNext()) {
	
                //Get inputID
		String inputID = (String) it.next();
                
                //Get selected value for input (the output ID of another service already contained in the composition) 
		String dataElementID = validSelectedInputs.get(inputID);
                
		//Add selected input value to the selected service input execution value
                //In fact, do not add the value itself, but a reference to the value (Data Element ID)
		exeContext.addInputValueToSeleServ(validatingServiceID,inputID,dataElementID);
			
                //Get ID of the service containing this output (data element)
		String matchServID = exeContext.getServiceIdOfOutput(dataElementID);
		
                //If the selected value is in fact the output of another service (matchServID)
                if(matchServID!=null){
					
		    //The service with inputs to be validated is the target service in this case
                    List<String> selectedServiceID = new LinkedList<>();
                    selectedServiceID.add(validatingServiceID);
		
                    //Compose the selected value (a service output - matchServID) with the service input being validated
                    exeContext.addServToGraph(matchServID, selectedServiceID);
		}
            }
            
            //Tries to close the service (node) whose inputs are being validated
            exeContext.closeOpenNode(exeContext.getSelectedService(validatingServiceID));
			
            //If there are invalid service inputs, store them in the response message
            if(!servInvalid.getInvalidInputID().isEmpty())
		response.getInvalidInputs().add(servInvalid);
        }
        
        //Marshals the information into XML
	StringWriter sw = new StringWriter();
	Marshaller marsh = jc.createMarshaller();
	marsh.marshal(response, sw);

	String result = sw.toString();

	return result; 
    }
    
    
    /**
     * ResolveServices Interaction Type Supporting Strategy
     * 
     * Find services that can provide outputs to match (resolve) inputs of a service 
     * previously selected by the user.
     * 
     * @param interactionTypeArgs
     * @param exeContext
     * @return Matching services
     * @throws JAXBException
    */
    private String ResolveServices(String interactionArgs, CompositionAndExecutionContext exeContext) throws JAXBException {
        
        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
	JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.resolveservices");
		
        //Create an unmarsheller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
	
        //Create the input stream to use as input for the unmarshaller
	InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
		
        //Unmarshal the interactionArgs
	ResolveServicesRequest request = (ResolveServicesRequest) unmarsh.unmarshal(interactionArgsStream);
		
        //Get all ServInputToResolve structures contained in the request message
	List<ServInputToResolve> serviceValidations = request.getServInputToResolve();
		
	//Variable to store service IDs
	List<String> serviceIDs = new LinkedList<>();
        
        //Variable to store inputs to resolve per service HashMap<ServiceID,List[InvalidInputID(s)]>
	HashMap<String, List<String>> servInputsToResolve = new HashMap<>();	
		
	//For each ServiceValidation structure contained in the request message
	for(int i=0; i<serviceValidations.size(); i++){
			
            //Add service ID to list "serviceIDs"
            serviceIDs.add(serviceValidations.get(i).getServiceIdToResolve());
			
            //Create Hash for service whose inputs need to be resolved
            servInputsToResolve.put(serviceValidations.get(i).getServiceIdToResolve(), new LinkedList<String>());
			
            //For each input to resolve
            for(int j=0; j<serviceValidations.get(i).getInputIdToResolve().size(); j++){
                //Add the input ID to list "servInputToResolve"
		servInputsToResolve.get(serviceValidations.get(i).getServiceIdToResolve()).add(serviceValidations.get(i).getInputIdToResolve().get(j));
            }
        }
        
        //Create the ResolveServ response
	semanticsco.interactions.resolveservices.ObjectFactory objectfact = new semanticsco.interactions.resolveservices.ObjectFactory();
	ResolveServicesResponse response = objectfact.createResolveServicesResponse();
		
	//For each service containing inputs to be resolved
	for(int i=0; i<serviceIDs.size(); i++){
		
            //Create new ResolvingServiceMatching structure
            ResolvingServiceMatching resolvingServiceMatching = new ResolvingServiceMatching();
            
            //Add service ID to ResolvingServiceMatching structure
            resolvingServiceMatching.setServiceIDMatching(serviceIDs.get(i));	
            
            //For each service input to resolve
            for(int j=0; j<servInputsToResolve.get(serviceIDs.get(i)).size(); j++){
                
                //Create new ServiceInputMatching structure
                InputMatching serviceInputMatching = new InputMatching();
		
                //Add input ID to ServiceInputMatching structure
		serviceInputMatching.setInputID(servInputsToResolve.get(serviceIDs.get(i)).get(j));
                
                //Get semantic type of input
		String semTypeToSearch = null;
		for(int z=0; z<exeContext.getDiscService(serviceIDs.get(i)).getInputs().size(); z++){
                    if(exeContext.getDiscService(serviceIDs.get(i)).getInputs().get(z).getIdentifier().equals(servInputsToResolve.get(serviceIDs.get(i)).get(j))){
                        semTypeToSearch = exeContext.getDiscService(serviceIDs.get(i)).getInputs().get(z).getSemanticConcept();
			break;
                    }
                }
                
                //Create an instance of ServiceDiscoverer SemanticSCo component
                ServiceDiscoverer sd = new ServiceDiscoverer(semTypeToSearch);
                
                //Perform service discovery to find services with outputs matching the input to be resolved
                List<Service> discServices = sd.findResolvingServices();
                
                List<Service> changedServices = exeContext.changeDiscServicesForResolve(discServices);
        
                //For each discovered service
                for(int k=0; k<changedServices.size(); k++){
                    
                    if(!changedServices.get(k).getIdentifier().equals(serviceIDs.get(i))){
                    
                        //Create new ServiceInfo structure
                        ServiceInfo servInfo = new ServiceInfo();
                    
                        servInfo.setID(changedServices.get(k).getIdentifier()); //service ID
                        servInfo.setName(changedServices.get(k).getName()); //service name
                        servInfo.setDescription(changedServices.get(k).getDescription()); //service description
                        servInfo.setLocation(changedServices.get(k).getLocation()); //service location
                        servInfo.setNamespace(changedServices.get(k).getNamespace()); //service namespace
                    
                        LinkedList<Interface> interfaces = changedServices.get(k).getInterfaces(); //service interfaces
                        for(Iterator iIt = interfaces.iterator(); iIt.hasNext();){
                
                            Interface inter = (Interface) iIt.next();
                            
                            InterfaceInfo interInfo = new InterfaceInfo();
                            interInfo.setID(inter.getIdentifier()); //interface id
                            interInfo.setName(inter.getName()); //interface name
                            interInfo.setLocation(inter.getLocation()); //interface location
                            interInfo.setNamespace(inter.getNamespace()); //interface namespace
                
                            LinkedList<Binding> bindings = inter.getBindings(); //service bindings
                            for(Iterator bIt = bindings.iterator(); bIt.hasNext();){
                    
                                Binding binding = (Binding) bIt.next();
                    
                                BindingInfo bindingInfo = new BindingInfo();
                    
                                bindingInfo.setName(binding.getName()); //binding name
                                bindingInfo.setLocation(binding.getLocation()); //binding location
                                bindingInfo.setNamespace(binding.getNamespace()); //binding namespace
                                bindingInfo.setProtocol(binding.getProtocol()); //binding protocol
                                bindingInfo.setAddress(binding.getAddress()); //binding address
                    
                                //Add bindingInfo to interfaceInfo
                                interInfo.getBinding().add(bindingInfo);
                    
                            }
                
                            //Add interfaceInfo to serviceInfo
                            servInfo.getInterface().add(interInfo);
                        }
                    
                        LinkedList<String> functions = changedServices.get(k).getFunctions(); //service functionality
                        for(Iterator fIt = functions.iterator(); fIt.hasNext();)
                            servInfo.getFunction().add((String) fIt.next());
                    
                        LinkedList<DataElement> inputs = changedServices.get(k).getInputs(); //service inputs
                        for(Iterator iIt = inputs.iterator(); iIt.hasNext();){
                
                            DataElement de = (DataElement) iIt.next();
                
                            Input input = new Input(); 
                            input.setID(de.getIdentifier()); //input id
                            input.setName(de.getName()); //input name
                            input.setSemanticalType(de.getSemanticConcept()); //input semantic concept
                            input.setSyntacticalType(de.getSyntacticalType()); //input syntactical type
                
                            servInfo.getInput().add(input);
                
                        }
            
                        LinkedList<DataElement> outputs = changedServices.get(k).getOutputs(); //service outputs
                        for(Iterator oIt = outputs.iterator(); oIt.hasNext();){
                
                            DataElement de = (DataElement) oIt.next();
                
                            Output output = new Output();
                            output.setID(de.getIdentifier()); //output id
                            output.setName(de.getName()); //output name
                            output.setSemanticalType(de.getSemanticConcept()); //input output concept
                            output.setSyntacticalType(de.getSyntacticalType()); //input output type
                
                            servInfo.getOutput().add(output);
                
                        }
                    
                        //Get list "oSearchValues"
                        List<String> outputSearchValues = sd.getOSearchValues();
                    
                        //Find which outputs semantically match the service input being resolved and add them to MatchingService structure
                        for(int m=0; m<changedServices.get(k).getOutputs().size(); m++){
                     
                            if(outputSearchValues.contains(changedServices.get(k).getOutputs().get(m).getSemanticConcept())){
                            
                                //Create new MatchingService structure
                                MatchingService matchingservice = new MatchingService();
                            
                                matchingservice.setMatchingValueID(changedServices.get(k).getOutputs().get(m).getIdentifier());
                            
                                //Store the ServiceInfo structure of the matching service into the MatchingService structure
                                matchingservice.setServiceInfo(servInfo);
                            
                                //Store the MatchingService structure into the ServiceInputMatching structure
                                serviceInputMatching.getMatchingValue().add(matchingservice);
                            }
                        }
                    }
                }
                
                //Add ServiceInputMatching structure to the ResolvingServiceMatching structure
		resolvingServiceMatching.getInputMatching().add(serviceInputMatching);
		
                //Add discovered services to CompositionAndExecutionContext.DiscoveredServices
		exeContext.addToDiscServices(changedServices);
            
            }
            
            //Store all services able to resolve the service inputs in the response message
            response.getResolvingServiceMatching().add(resolvingServiceMatching);
        }
        
        //Marshals the information into XML
	StringWriter sw = new StringWriter();
	Marshaller marsh = jc.createMarshaller();
	marsh.marshal(response, sw);

	String result = sw.toString();

	return result;
    }
    
    /**
     * GetExecutableServices Interaction Type supporting strategy.
     *     
     * Return the services from the service composition that can be executed.
     * 
     * @param interactionArgs
     * @param exeContext
     * @return services to be executed next
     * @throws JAXBException 
    */
    private String GetExecutableServices(String interactionArgs, CompositionAndExecutionContext exeContext) throws JAXBException {

        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
	JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.getexecutableservices");
	
        //Create an unmarsheller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
	
        //Create the input stream to use as input for the unmarshaller
	InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
	unmarsh.unmarshal(interactionArgsStream);
        
        //Get IDs of all services (contained in the composition) that can be executed
	List<String> servsToExec = exeContext.nextServsToExec();
        
        //Create the GetExecutableServices Response
        semanticsco.interactions.getexecutableservices.ObjectFactory objectfact = new semanticsco.interactions.getexecutableservices.ObjectFactory();
	GetExecutableServicesResponse response = objectfact.createGetExecutableServicesResponse();
		
	//For each service to be executed
	for(int i=0; i<servsToExec.size(); i++){
            
            //Get service info from CompositionAndExecutionContext.DiscoveredServices
            Service servToAdd = exeContext.getDiscService(servsToExec.get(i));
			
            //Create new ServiceInfo structure
            ServiceInfo servInfo = new ServiceInfo();
                    
            servInfo.setID(servToAdd.getIdentifier()); //service ID
            servInfo.setName(servToAdd.getName()); //service name
            servInfo.setDescription(servToAdd.getDescription()); //service description
            servInfo.setLocation(servToAdd.getLocation()); //service location
            servInfo.setNamespace(servToAdd.getNamespace()); //service namespace
            
            LinkedList<Interface> interfaces = servToAdd.getInterfaces(); //service interfaces
            for(Iterator iIt = interfaces.iterator(); iIt.hasNext();){
                
                Interface inter = (Interface) iIt.next();
                
                InterfaceInfo interInfo = new InterfaceInfo();
                interInfo.setID(inter.getIdentifier()); //interface id
                interInfo.setName(inter.getName()); //interface name
                interInfo.setLocation(inter.getLocation()); //interface location
                interInfo.setNamespace(inter.getNamespace()); //interface namespace
                
                LinkedList<Binding> bindings = inter.getBindings(); //service bindings
                for(Iterator bIt = bindings.iterator(); bIt.hasNext();){
                    
                    Binding binding = (Binding) bIt.next();
                    
                    BindingInfo bindingInfo = new BindingInfo();
                    
                    bindingInfo.setName(binding.getName()); //binding name
                    bindingInfo.setLocation(binding.getLocation()); //binding location
                    bindingInfo.setNamespace(binding.getNamespace()); //binding namespace
                    bindingInfo.setProtocol(binding.getProtocol()); //binding protocol
                    bindingInfo.setAddress(binding.getAddress()); //binding address
                    
                    //Add bindingInfo to interfaceInfo
                    interInfo.getBinding().add(bindingInfo);
                    
                }
                
                //Add interfaceInfo to serviceInfo
                servInfo.getInterface().add(interInfo);
            }
            
            LinkedList<String> functions = servToAdd.getFunctions(); //service functionality
            for(Iterator fIt = functions.iterator(); fIt.hasNext();)
                servInfo.getFunction().add((String) fIt.next());
            
            LinkedList<DataElement> inputs = servToAdd.getInputs(); //service inputs
            for(Iterator iIt = inputs.iterator(); iIt.hasNext();){
                
                DataElement de = (DataElement) iIt.next();
                
                Input input = new Input(); 
                input.setID(de.getIdentifier()); //input id
                input.setName(de.getName()); //input name
                input.setSemanticalType(de.getSemanticConcept()); //input semantic concept
                input.setSyntacticalType(de.getSyntacticalType()); //input syntactical type
                
                //Set suggestion values for execution
                List<DataElement> suggestionValues = de.getSuggestionValues();
                for(Iterator sIt = suggestionValues.iterator(); sIt.hasNext();){
                   
                    DataElement deSugValue = (DataElement) sIt.next();
                   
                   SuggestionValue sugValue = new SuggestionValue();
                   sugValue.setValueID(deSugValue.getIdentifier());
                   sugValue.setValueDescription(deSugValue.getName());
                   sugValue.setValue(deSugValue.getExecutionValue());
                   
                   //Add suggestion value to input
                   input.getSuggestedValue().add(sugValue);
                   
                }
                
		//Set execution value according to ID stored in CompositionAndExecutionContext.AvailableValues (alterei aqui agora)
                if(exeContext.getAvailableValue(de.getExecutionValue()) != null){
                    if(!exeContext.getAvailableValue(de.getExecutionValue()).getExecutionValue().equals("null"))
                        input.setExecutionValue(exeContext.getAvailableValue(de.getExecutionValue()).getExecutionValue());
                }
                
                servInfo.getInput().add(input);
                
            }
            
            LinkedList<DataElement> outputs = servToAdd.getOutputs(); //service outputs
            for(Iterator oIt = outputs.iterator(); oIt.hasNext();){
                
                DataElement de = (DataElement) oIt.next();
                
                Output output = new Output();
                output.setID(de.getIdentifier()); //output id
                output.setName(de.getName()); //output name
                output.setSemanticalType(de.getSemanticConcept()); //input output concept
                output.setSyntacticalType(de.getSyntacticalType()); //input output type
                
                servInfo.getOutput().add(output);
                
            }
            
            //Make service ready for execution if all inputs have execution values
            boolean isReady = true;
            for(Iterator it=servInfo.getInput().iterator();it.hasNext();){
                if(((Input) it.next()).getExecutionValue() == null)
                    isReady = false;
            }
            servInfo.setReadyForExec(isReady);
			
            //Add service to be executed to the response message
            response.getServiceInfo().add(servInfo);
        }
        
        //In case there are no services to execute, but there are still open nodes in the graph, return them
	if(servsToExec.isEmpty()){
            
            //Get the open nodes (services)
            List<String> openNodes = exeContext.getOpenNodes();
            
            //Store the open nodes (services) in the response message
            for(int i=0; i<openNodes.size(); i++){
		
                Service servToAdd = exeContext.getDiscService(openNodes.get(i));
                
                //Create new ServiceInfo structure
                ServiceInfo servInfo = new ServiceInfo();
                    
                servInfo.setID(servToAdd.getIdentifier()); //service ID
                servInfo.setName(servToAdd.getName()); //service name
                servInfo.setDescription(servToAdd.getDescription()); //service description
                servInfo.setLocation(servToAdd.getLocation()); //service location
                servInfo.setNamespace(servToAdd.getNamespace()); //service namespace
				
		LinkedList<Interface> interfaces = servToAdd.getInterfaces(); //service interfaces
                for(Iterator iIt = interfaces.iterator(); iIt.hasNext();){
                
                    Interface inter = (Interface) iIt.next();
                
                    InterfaceInfo interInfo = new InterfaceInfo();
                    interInfo.setID(inter.getIdentifier()); //interface id
                    interInfo.setName(inter.getName()); //interface name
                    interInfo.setLocation(inter.getLocation()); //interface location
                    interInfo.setNamespace(inter.getNamespace()); //interface namespace
                
                    LinkedList<Binding> bindings = inter.getBindings(); //service bindings
                    for(Iterator bIt = bindings.iterator(); bIt.hasNext();){
                    
                        Binding binding = (Binding) bIt.next();
                    
                        BindingInfo bindingInfo = new BindingInfo();
                    
                        bindingInfo.setName(binding.getName()); //binding name
                        bindingInfo.setLocation(binding.getLocation()); //binding location
                        bindingInfo.setNamespace(binding.getNamespace()); //binding namespace
                        bindingInfo.setProtocol(binding.getProtocol()); //binding protocol
                        bindingInfo.setAddress(binding.getAddress()); //binding address
                    
                        //Add bindingInfo to interfaceInfo
                        interInfo.getBinding().add(bindingInfo);
                    
                    }
                
                    //Add interfaceInfo to serviceInfo
                    servInfo.getInterface().add(interInfo);
                }
                
                LinkedList<String> functions = servToAdd.getFunctions(); //service functionality
                for(Iterator fIt = functions.iterator(); fIt.hasNext();)
                    servInfo.getFunction().add((String) fIt.next());
                
                LinkedList<DataElement> inputs = servToAdd.getInputs(); //service inputs
                for(Iterator iIt = inputs.iterator(); iIt.hasNext();){
                
                    DataElement de = (DataElement) iIt.next();
                
                    Input input = new Input(); 
                    input.setID(de.getIdentifier()); //input id
                    input.setName(de.getName()); //input name
                    input.setSemanticalType(de.getSemanticConcept()); //input semantic concept
                    input.setSyntacticalType(de.getSyntacticalType()); //input syntactical type
                
                    //Set suggestion values for execution
                    List<DataElement> suggestionValues = de.getSuggestionValues();
                    for(Iterator sIt = suggestionValues.iterator(); sIt.hasNext();){
                   
                        DataElement deSugValue = (DataElement) sIt.next();
                        
                        SuggestionValue sugValue = new SuggestionValue();
                        sugValue.setValueID(deSugValue.getIdentifier());
                        sugValue.setValueDescription(deSugValue.getName());
                        sugValue.setValue(deSugValue.getExecutionValue());
                   
                        //Add suggestion value to input
                        input.getSuggestedValue().add(sugValue);
                
                    }
                
                    //Set execution value according to ID stored in CompositionAndExecutionContext.AvailableValues  (alterei aqui agora)
                    if(exeContext.getAvailableValue(de.getExecutionValue()) != null){
                        if(!exeContext.getAvailableValue(de.getExecutionValue()).getExecutionValue().equals("null"))
                            input.setExecutionValue(exeContext.getAvailableValue(de.getExecutionValue()).getExecutionValue());
                    }
                
                    servInfo.getInput().add(input);
                
                }
                
                LinkedList<DataElement> outputs = servToAdd.getOutputs(); //service outputs
                for(Iterator oIt = outputs.iterator(); oIt.hasNext();){
                    
                    DataElement de = (DataElement) oIt.next();
                
                    Output output = new Output();
                    output.setID(de.getIdentifier()); //output id
                    output.setName(de.getName()); //output name
                    output.setSemanticalType(de.getSemanticConcept()); //input output concept
                    output.setSyntacticalType(de.getSyntacticalType()); //input output type
                
                    servInfo.getOutput().add(output);
                
                }
		
                //Since the service is not ready for execution, set ReadyForExec to false
                servInfo.setReadyForExec(false);

		//Add service to be executed to the response message
		response.getServiceInfo().add(servInfo);
            }
        }
        
        //Marshals the information into XML
	StringWriter sw = new StringWriter();
	Marshaller marsh = jc.createMarshaller();
	marsh.marshal(response,sw);

	String result = sw.toString();

	return result;
    }
    
    /**
     * AddToContext Interaction Type supporting strategy.
     *     
     * Add information (service output/result, user context info) to the user execution context.
     * 
     * @param interactionArgs
     * @param exeContext
     * @return OK
     * @throws JAXBException 
    */
    private String AddToContext(String interactionArgs, CompositionAndExecutionContext exeContext) throws JAXBException {

        //Set the JAXBContext instance to handle the classes generated to manage the interaction type
	JAXBContext jc = JAXBContext.newInstance("semanticsco.interactions.addtocontext");
	
        //Create an unmarshaller instance
	Unmarshaller unmarsh = jc.createUnmarshaller();
	
        //Create the input stream to use as input for the unmarshaller
	InputStream interactionArgsStream = new ByteArrayInputStream(interactionArgs.getBytes());
		
        //Unmarshal the interactionArgs
	AddToContextRequest request = (AddToContextRequest) unmarsh.unmarshal(interactionArgsStream);
		
        //Get all service IDs of services without outputs that are reported as executed
        List<String> executedServicesIDs = request.getServiceIdToAdd();
        
        //Get all values to be added to the execution context
    	List<ContextValue> newExeContextValues = request.getValueToAdd();
        	
	//Variable to store the output values to be added to the context: HashMap<valueID,ServiceOutputValue>
	HashMap<String,ContextValue> serviceOutputValues = new HashMap<>();
		
        //For each ContextValue (ValueToAdd) structure
        for(int i=0; i<newExeContextValues.size();i++) {
            
            //Get valueID and serviceOutputValue and store in the list "serviceOutputValues"
            if(newExeContextValues.get(i).getServiceOutputValue() != null)
                serviceOutputValues.put(newExeContextValues.get(i).getValueID(), newExeContextValues.get(i));
            //Get ServiceID and store it in the list "executedServicesIDs" (ensuring no duplicates)
            if(newExeContextValues.get(i).getServiceID() != null)
                if(!executedServicesIDs.contains(newExeContextValues.get(i).getServiceID()))
                    executedServicesIDs.add(newExeContextValues.get(i).getServiceID());
        }
        
        //Move all executed services from CompositionAndExecutionContext.ActiveServices to CompositionAndExecutionContext.ExecutedServices
	for(Iterator it=executedServicesIDs.iterator();it.hasNext();)
            exeContext.moveFromActiveToExecuted((String) it.next());
        	
        //Store each provided service output value -----------------------------------------------
        Set<String> valueIDs = serviceOutputValues.keySet();
        for(Iterator it=valueIDs.iterator();it.hasNext();){
            
            //Get valueID (service output ID)
            String servOutput = (String)it.next();
            
            //Get ServiceOutputValue (service output value)
            String valueToAdd = serviceOutputValues.get(servOutput).getServiceOutputValue();
            
            //Get ServiceID
            String serviceID = serviceOutputValues.get(servOutput).getServiceID();
            
            //Add service ID, and associated output ID and value to list "availableValues"
            exeContext.addToAvailableValues(serviceID,servOutput,valueToAdd,null);
						
	}
	
        //Creates the AddToContext Response - reply OK or ERROR
	semanticsco.interactions.addtocontext.ObjectFactory objectfact = new semanticsco.interactions.addtocontext.ObjectFactory();
	AddToContextResponse response = objectfact.createAddToContextResponse();
	response.setAddToContextResult("OK");
		
	//Marshal the information into XML
	StringWriter sw = new StringWriter();
	Marshaller marsh = jc.createMarshaller();
	marsh.marshal(response, sw);

	String result = sw.toString();

	return result;
    }
    
    /**
     * Clean the user session context.
     * 
     * @param interactionTypeArgs
     * @param exeContext
     * @return
    */
    private String CleaningContext(CompositionAndExecutionContext exeContext) {
		
	//Clean all variables of the CompositionAndExecutionContext
	exeContext.cleanContext();
		
	return null;
    }

}