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

package management;

import interfacesemanticsco.SemanticSCoClient;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import javax.xml.bind.JAXBException;
import jaxbClasses.auxiliarClasses.ComposeServicesStructure;
import jaxbClasses.auxiliarClasses.ContextValueStructure;
import jaxbManipulation.JaxbCreation;
import jaxbManipulation.JaxbExtraction;
import jaxbClasses.auxiliarClasses.InputValidationStructure;
import jaxbClasses.auxiliarClasses.ServiceValidationStructure;
import jaxbClasses.basictypes.Input;
import jaxbClasses.basictypes.InputMatching;
import jaxbClasses.basictypes.InvalidInputs;
import jaxbClasses.basictypes.MatchingService;
import jaxbClasses.basictypes.Output;
import jaxbClasses.basictypes.SemanticConcept;
import jaxbClasses.basictypes.ServiceInfo;
import jaxbClasses.basictypes.SuggestionValue;

public class CompositionManager {

    private String activitiDbDriver, activitiDbURL, activitiDbUser, activitiDbPassword;
    
    private SemanticSCoClient client;
    private String sessionID;
    
    private JaxbCreation creator;
    private JaxbExtraction extractor;
    private String requestMessage,responseMessage;
    
    //Variables for Semantic Discovery of All Labels
    private List<SemanticConcept> listOfLabels;
    
    //Variable for Semantic Discovery of All Inputs
    private JTree tree; 
    
    //Variables for Semantic Discovery of Functions
    private SemanticConcept selectedInputConcept; //Store the last selected input concept
    private HashMap<String,SemanticConcept> listOfSelectedInputs; //Store all selected input concepts
    
    //Variables for Service Discovery
    private SemanticConcept selectedFunctionConcept;
    
    //Variables for Service Selection
    private ServiceInfo includedService;
    private LinkedList<ServiceInfo> listOfIncludedServices;
    private LinkedList<ServiceInfo> listOfExcludedServices;
    private HashMap<String,File> servicesDirectories;
    
    //Variables for Resolve Service
    private String selectedOutput;
    
    //Variables for Data source elements
    private LinkedList<DataSource> listOfDataSources;
    private LinkedList<String> listOfRemovedDataSources;
    
    private int countDataSources = 0;
    
    private int countNewAnalysis = 0;
    
    //Variables to manipulate files
    private File sessionDirectory = null;
    private HashMap<String,File> dataSourcesDirectories; //Store {dataSourceID,dataSourceDirectory}
    
    HashMap<String,String> mapServToAnalysisName;
    
    public CompositionManager(String activitiDbDriver, String activitiDbURL, String activitiDbUser, String activitiDbPassword){
        
        this.activitiDbDriver = activitiDbDriver;
        this.activitiDbURL = activitiDbURL;
        this.activitiDbUser = activitiDbUser;
        this.activitiDbPassword = activitiDbPassword;
        
        //Initialize client
        client = new SemanticSCoClient();
        
        //Initialize LinkedLists and HashMaps
        listOfIncludedServices = new LinkedList<>();
        listOfExcludedServices = new LinkedList<>();
        listOfLabels = new LinkedList<>();
        listOfSelectedInputs = new HashMap<>();
        listOfDataSources = new LinkedList<>();
        listOfRemovedDataSources = new LinkedList<>();
        dataSourcesDirectories = new HashMap<>();    
        servicesDirectories = new HashMap<>();
        mapServToAnalysisName = new HashMap<>();
        
        //Initialize objects
        creator = new JaxbCreation();
        extractor = new JaxbExtraction();
                
    }
    
    public void startSession(){
        
        //Start new session and save session ID
        sessionID = client.startSession();
        
        //Get system temporary files path
        String temporaryPath = System.getProperty("java.io.tmpdir").replaceAll("\\\\","/");
        
        //If temporary file path does not end with /, add it
        if(!temporaryPath.endsWith("/"))
            temporaryPath = temporaryPath + "/";
        
        //Create session directory
        sessionDirectory = new File(temporaryPath+sessionID);
        sessionDirectory.mkdir();
               
    }
    
    public void semDiscoveryAllLabels(String root) throws JAXBException{
        
        //Create XML Requisition
        requestMessage = creator.createGetLabelsRequisition("http://dcm.ffclrp.usp.br/lssb/geas/ontologies/GEXPASO.owl",root);
        
        //Call client for semantic discovery (all labels)
        responseMessage = client.coordinate(sessionID, "GET_LABELS", requestMessage);
        
        //Extract XML Response and add all elements to list listOfLabels
        listOfLabels.addAll(extractor.extractGetLabelsResponse(responseMessage));
        
    }
    
    public JTree semDiscoveryAllInputs(String root) throws JAXBException{
        
        //Create XML Requisition
        requestMessage = creator.createDiscoverInputSemanticsRequisition("http://dcm.ffclrp.usp.br/lssb/geas/ontologies/GEXPASO.owl",root);
        
        //Call client for semantic discovery (all inputs)
        responseMessage = client.coordinate(sessionID, "DISCOVER_INPUT_SEMANTICS", requestMessage);
        
        //Extract XML Response
        tree = extractor.extractDiscoverInputSemanticsResponse(responseMessage);
        
        return tree;
        
    }
    
    public List<SemanticConcept> semDiscoveryFunctions() throws JAXBException{
        
        LinkedList<SemanticConcept> listForSearch = new LinkedList<>();
        
        //For each element contained in the map listOfSelectedInputs
        for(Map.Entry<String,SemanticConcept> entry : listOfSelectedInputs.entrySet()){
            
            //Verifies if listForSearch contains the current map entry
            SemanticConcept sc = this.hasElement(listForSearch,entry.getValue());
            
            //If listForSearch already contains the current map entry
            if(sc != null){
                //If the (already inserted) concept is smaller than the concept being inserted, change it. Else, do nothing
                if(sc.compareTo(entry.getValue())<0)
                    sc.setSemanticSimilarity(entry.getValue().getSemanticSimilarity());
            }//Else, if listForSearch already contains the current map entry
            else
                listForSearch.add(entry.getValue());
            
        }
        
        //Create XML Requisition
        requestMessage = creator.createDiscoverFunctionSemanticsRequisition(listForSearch);
        
        //Call client for semantic discovery (functions)
        String response = client.coordinate(sessionID, "DISCOVER_FUNCTION_SEMANTICS", requestMessage);
        
        //Extract XML Response
        List<SemanticConcept> listOfDiscoveredFunctions = extractor.extractDiscoverFunctionSemanticsResponse(response);
        
        Collections.sort(listOfDiscoveredFunctions);
        
        return listOfDiscoveredFunctions;
        
    }
    
    private boolean hasElementForDisc(LinkedList<String> listOfInputs,String conceptURL){
        
        for(Iterator it=listOfInputs.iterator();it.hasNext();){
            String currentUrl = (String) it.next();
            if(currentUrl.equals(conceptURL))
                return true;
        }
        
        return false;
        
    } 
    
    public List<ServiceInfo> serviceDiscovery() throws JAXBException {
        
        LinkedList<String> listOfFunctions = new LinkedList<>();
        listOfFunctions.add(selectedFunctionConcept.getUrl());
        
        LinkedList<String> listOfInputs = new LinkedList<>();
        for(Map.Entry<String,SemanticConcept> entry : listOfSelectedInputs.entrySet()){
            if(!this.hasElementForDisc(listOfInputs,entry.getValue().getUrl()))
                listOfInputs.add(entry.getValue().getUrl());
        }
        
        //Create XML Requisition            
        requestMessage = creator.createDiscoverServicesRequisition(listOfFunctions,listOfInputs);
        
        //Call client for service discovery
        responseMessage = client.coordinate(sessionID, "DISCOVER_SERVICES", requestMessage);
        
        //Extract XML Response
        List<ServiceInfo> listOfDiscoveredServices = extractor.extractDiscoverServicesResponse(responseMessage);
        
        //Put list of discovered services in reverse order
        LinkedList<ServiceInfo> auxiliar = new LinkedList<>();
        for(int i= listOfDiscoveredServices.size()-1; i>=0;i--)
            auxiliar.add(listOfDiscoveredServices.get(i));
        listOfDiscoveredServices = auxiliar;
        
        //Put connectors at the end of the list
        auxiliar = new LinkedList<>();
        for(int i= 0; i<listOfDiscoveredServices.size();i++){
            if(listOfDiscoveredServices.get(i).getFunction().isEmpty())
                auxiliar.add(listOfDiscoveredServices.get(i));
        }
        for(int i= 0; i<listOfDiscoveredServices.size();i++){
            if(!listOfDiscoveredServices.get(i).getFunction().isEmpty())
                auxiliar.add(listOfDiscoveredServices.get(i));
        }
        
        return auxiliar;
        
    }
    
    public boolean serviceInclusion() throws JAXBException{
        
        HashMap<String,String> map = new HashMap<>();
        if(!includedService.getFunction().isEmpty())
            map.put(includedService.getID(),includedService.getFunction().get(0));
        else
            map.put(includedService.getID(),"");
        
        //Create XML Requisition
        requestMessage = creator.createIncludeServicesRequisition(map);
        
        //Call client for service inclusion
        responseMessage = client.coordinate(sessionID, "INCLUDE_SERVICES", requestMessage);
        
        //Extract XML Response
        List<ServiceInfo> list = extractor.extractIncludeServicesResponse(responseMessage);
        
        //If service was indeed included
        if(!list.isEmpty() && list.get(0).getID().equals(includedService.getID())){
            
            //For each previously included input, change semantic similarity
            for(Map.Entry<String,SemanticConcept> entry : listOfSelectedInputs.entrySet()){
                Double value = Double.parseDouble(entry.getValue().getSemanticSimilarity());
                if(value >= 0.1)
                    value = value - 0.1;
                entry.getValue().setSemanticSimilarity(value.toString());
            }
            
            //Add outputs of included service to list "listOfSelectedInputs"
            List<Output> outputs = list.get(0).getOutput();
            for(Iterator it=outputs.iterator();it.hasNext();){
                
                Output output = (Output) it.next();
                
                SemanticConcept sc = new SemanticConcept();
                sc.setUrl(output.getSemanticalType());
                sc.setName(this.getLabel(output.getSemanticalType()));
                sc.setSemanticSimilarity("5.0");
                
                listOfSelectedInputs.put(output.getID(),sc);   
                
            }
            
            //For each excluded service
            for(Iterator itExc = listOfExcludedServices.iterator();itExc.hasNext();){
                String id = ((ServiceInfo)itExc.next()).getID();
                
                //For each suggestionValue of each input of included service
                for(Iterator itInput = list.get(0).getInput().iterator();itInput.hasNext();){
                    for(Iterator itSug = ((Input) itInput.next()).getSuggestedValue().iterator();itSug.hasNext();){
                    
                        //If suggestionValue contains a reference to excluded service, exclude it
                        SuggestionValue suggestionValue = (SuggestionValue) itSug.next();
                        if(suggestionValue.getValueID().contains(id))
                            itSug.remove();
                        
                    }
                }
            }
            
            //For each excluded data source
            for(Iterator itExc = listOfRemovedDataSources.iterator();itExc.hasNext();){
                String value = (String)itExc.next();
                
                //For each suggestionValue of each input of included service
                for(Iterator itInput = list.get(0).getInput().iterator();itInput.hasNext();){
                    for(Iterator itSug = ((Input) itInput.next()).getSuggestedValue().iterator();itSug.hasNext();){
                    
                        //If suggestionValue contains a reference to excluded service, exclude it
                        SuggestionValue suggestionValue = (SuggestionValue) itSug.next();
                        if(suggestionValue.getValue() != null && suggestionValue.getValue().equals(value))
                            itSug.remove();
                        
                    }
                }
            }
                
            //Add included service to list "listOfIncludedServices"
            this.addIncludedService(list.get(0));
            
            //Create directory associated to service
            this.createServiceDirectory(list.get(0).getID());
            
            return true;
        }
        
        return false;
        
    }
    
    public boolean serviceValidation(String serviceID,String inputID, String value, boolean isSelected) throws JAXBException{
        
        this.clearServiceDirectory(serviceID);
        
        List<InvalidInputs> list;
        
        if(isSelected){
            
            LinkedList<ComposeServicesStructure> composeServicesStructure = new LinkedList<>();
            ComposeServicesStructure composeStructure = new ComposeServicesStructure(serviceID,inputID,value);
            composeServicesStructure.add(composeStructure);
            
            //Create XML Requisition
            requestMessage = creator.createComposeServicesRequisition(composeServicesStructure);
        
            //Call client for service input validation
            responseMessage = client.coordinate(sessionID, "COMPOSE_SERVICES", requestMessage);
        
            //Extract XML Response
            list = extractor.extractComposeServicesResponse(responseMessage);
        }
        else{
            
            LinkedList<InputValidationStructure> inputValidations  = new LinkedList<>();
            
            InputValidationStructure structure;
            
            if(!value.equals("null")){
                
                String dataSourceConcept = this.getDataSourceConcept(value);
                String dataSourceConceptUri = this.getOntologyUri(dataSourceConcept);
            
                structure = new InputValidationStructure(serviceID, inputID, value, "File" , dataSourceConceptUri);
            }
            else
                structure = new InputValidationStructure(serviceID, inputID, "null", "File" , "");
                        
            inputValidations.add(structure);
            
            //Create XML Requisition
            requestMessage = creator.createValidateInputsRequisition(inputValidations);
        
            //Call client for service input validation
            responseMessage = client.coordinate(sessionID, "VALIDATE_INPUTS", requestMessage);
        
            //Extract XML Response
            list = extractor.extractValidateInputsResponse(responseMessage);
            
        }
        
        //If list is empty (no invalid input), return true
        return list.isEmpty(); 
        
    }
    
    
    public void execServ() throws JAXBException{
        
        //Create XML Requisition
        requestMessage = creator.createGetExecutableServicesRequisition();
        
        //Call client for exec serv
        responseMessage = client.coordinate(sessionID, "GET_EXECUTABLE_SERVICES", requestMessage);
        
        //Extract XML Response
        List<ServiceInfo> servicesReadyToExecute = extractor.extractGetExecutableServicesResponse(responseMessage);
        
        //If no service is ready for execution
        if(servicesReadyToExecute.isEmpty()){
            
            //Update ReadyForExec parameter of all included services to false
            for(Iterator selIt = listOfIncludedServices.iterator();selIt.hasNext();){
                ServiceInfo oldServiceInfo = (ServiceInfo) selIt.next();
                oldServiceInfo.setReadyForExec(false);
                
                //Update execution values of all inputs to null
                List<Input> inputs = oldServiceInfo.getInput();
                for(Iterator itIn=inputs.iterator();itIn.hasNext();){
                    Input input = (Input) itIn.next();
                    input.setExecutionValue(null);
                }
            }
        }
        
        //Else, for each previously included service
        for(int i=0; i<listOfIncludedServices.size(); i++){
                
            //Try to find it into response message
            int index = servicesReadyToExecute.size()+1;
            for(int j=0;j<servicesReadyToExecute.size();j++){
                if(listOfIncludedServices.get(i).getID().equals(servicesReadyToExecute.get(j).getID()))
                    index = j;
            }    
            
            //If service is contained into response
            if(index != servicesReadyToExecute.size()+1){
                
                //Update ReadyForExec parameter according to the one contained in the response
                listOfIncludedServices.get(i).setReadyForExec(servicesReadyToExecute.get(index).isReadyForExec());
                
                //If service is ready for execution, i.e., it contains all needed executionValues
                if(listOfIncludedServices.get(i).isReadyForExec()){
                    //Update execution values of inputs according to the ones contained in the response
                    listOfIncludedServices.get(i).getInput().clear();
                    listOfIncludedServices.get(i).getInput().addAll(servicesReadyToExecute.get(index).getInput());
                }
                else{
                    for(Iterator it = listOfIncludedServices.get(i).getInput().iterator();it.hasNext();){
                        Input input = (Input) it.next();
                        input.setExecutionValue(null);
                    }
                }
                
                
            }
            else{ //Else, if service is not contained into response, i.e., is not ready for execution
                
                //Update ReadyForExec parameter to false
                listOfIncludedServices.get(i).setReadyForExec(false);     
                
                //Update execution values of all inputs to null
                List<Input> inputs = listOfIncludedServices.get(i).getInput();
                for(Iterator itIn=inputs.iterator();itIn.hasNext();){
                    Input input = (Input) itIn.next();
                    input.setExecutionValue(null);
                }
            }            
        }
    }
    
    public HashMap<String,ServiceInfo> resolveServiceInput(String serviceID,String inputID) throws JAXBException{
        
        LinkedList<ServiceValidationStructure> serviceValidations = new LinkedList<>();
        
        ServiceValidationStructure svs = new ServiceValidationStructure(serviceID);
        svs.addInputID(inputID);
        serviceValidations.add(svs);
        
        //Create XML Requisition
        requestMessage = creator.createResolveServicesRequisition(serviceValidations);
        
        //Call client for exec serv
        responseMessage = client.coordinate(sessionID, "RESOLVE_SERVICES", requestMessage);
        
        //Extract XML Response
        InputMatching serviceInputMatching = extractor.extractResolveServicesResponse(responseMessage).get(0).getInputMatching().get(0);
        
        //Initialize list to store discoveredServices
        HashMap<String,ServiceInfo> mapOfResolvedServices = new HashMap<>();
        
        //For each discovered value
        List<MatchingService> matchingServices = serviceInputMatching.getMatchingValue();
        for(Iterator it = matchingServices.iterator();it.hasNext();){
                
            MatchingService matchingService = (MatchingService) it.next();
                
            String foundOutputID = matchingService.getMatchingValueID();
            
            ServiceInfo foundService = matchingService.getServiceInfo();
            
            //If found service is already contained in composition
            if(this.hasIncludedService(foundService)){
                       
                //Find service with input being resolved and update its suggested values
                for(Iterator itSel=listOfIncludedServices.iterator();itSel.hasNext();){
                    
                    //Find service being resolved
                    ServiceInfo info = (ServiceInfo)itSel.next();
                    if(info.getID().equals(serviceID)){
                        
                        //Find service inputs
                        List<Input> inputs = info.getInput();
                        for(Iterator itInp=inputs.iterator();itInp.hasNext();){
                                
                            //Find service input being resolved
                            Input input = (Input) itInp.next();
                            if(input.getID().equals(inputID)){
                                    
                                //Verify if suggestion values of input being resolved already contain found OutputID
                                List<SuggestionValue> suggestionValues = input.getSuggestedValue();
                                boolean exists = false;
                                for(Iterator itSug=suggestionValues.iterator();itSug.hasNext();)
                                    if(((SuggestionValue)itSug.next()).getValueID().equals(foundOutputID))
                                        exists = true;
                                
                                //If suggestion value is not already contained into suggestion values, add it
                                if(!exists){
                                    SuggestionValue sv = new SuggestionValue();
                                    sv.setValueID(foundOutputID);
                                    suggestionValues.add(sv);
                                }
                            }
                        }
                    }
                }
            }//Else, if found service is not already contained into composition
            else{
                
                //Verifies if found service was excluded from composition
                boolean wasExcluded = false;
                for(Iterator itExc=listOfExcludedServices.iterator();itExc.hasNext();){
                    if(((ServiceInfo)itExc.next()).getID().equals(foundService.getID()))
                        wasExcluded = true;
                }
                
                //If service was not excluded, i.e., it is a new service, add it to "mapOfResolvedServices". Else, do nothing
                if(!wasExcluded)
                    mapOfResolvedServices.put(foundOutputID,foundService);
                
            }
        }  
        
        return mapOfResolvedServices;
    }
    
    public void addValuesToContext(LinkedList<ContextValueStructure> contextValues) throws JAXBException{
     
        //If there is some output to add to context
        if(!contextValues.isEmpty()){
            
            //Create XML Requisition
            requestMessage = creator.createAddToContextServiceOutputValuesRequisition(contextValues);
            
            //Call client for add to context
            responseMessage = client.coordinate(sessionID, "ADD_TO_CONTEXT", requestMessage);
            
        }  
        
    }
    
    public void addServiceIdToContext(String serviceId) throws JAXBException{
        
        LinkedList<String> serviceIds = new LinkedList<>();
        serviceIds.add(serviceId);
        
        //Create XML Requisition
        requestMessage = creator.createAddToContextServiceIdsRequisition(serviceIds);
            
        //Call client for add to context
        responseMessage = client.coordinate(sessionID, "ADD_TO_CONTEXT", requestMessage);
            
    }
    
    public String getSessionID(){
        return this.sessionID;
    }
    
    //Method for Semantic Discovery of All Inputs ------------------------------------------------------------------------
    public JTree getTreeOfInputs(){
        return this.tree;
    }
    
    public void setTreeOfInputs(JTree tree){
        this.tree = tree;
    }
    //---------------------------------------------------------------------------------------------------------------------
    
    //Methods for Semantic Discovery of Functions -------------------------------------------------------------------------
    public SemanticConcept getSelectedInputConcept(){
        
        return this.selectedInputConcept;
        
    }
    public void setSelectedInputConcept(SemanticConcept selectedInputConcept){
        
        //Set variable "selectedInputConcept"
        this.selectedInputConcept = selectedInputConcept;
        
    }
    public void addSelectedInputConcept(String dataSourceID){
        
        //For each previously selected input, change semantic similarity
        for(Map.Entry<String,SemanticConcept> entry : listOfSelectedInputs.entrySet()){
            Double value = Double.parseDouble(entry.getValue().getSemanticSimilarity());
            if(value >= 0.1)
                value = value - 0.1;
            entry.getValue().setSemanticSimilarity(value.toString());
        }
        
        //If selected concept is not already in the list, add it (with similarity 5.0)
        if(!listOfSelectedInputs.containsKey(dataSourceID))
            listOfSelectedInputs.put(dataSourceID,selectedInputConcept);
        else{ //Else, if selected concept is already in the list, change its URL, name and set similarity to 5.0
            listOfSelectedInputs.get(dataSourceID).setUrl(selectedInputConcept.getUrl());
            listOfSelectedInputs.get(dataSourceID).setName(selectedInputConcept.getName());
            listOfSelectedInputs.get(dataSourceID).setSemanticSimilarity("5.0");       
        }
        
    }
    
    //---------------------------------------------------------------------------------------------------------------------
    
    
    //Methods for Service Discovery ---------------------------------------------------------------------------------------
    public SemanticConcept getSelectedFunctionConcept(){
        
        return this.selectedFunctionConcept;
        
    }
    public void setSelectedFunctionConcept(SemanticConcept selectedFunctionConcept){
        
        //Set variable "selectedFunctionConcept"
        this.selectedFunctionConcept = selectedFunctionConcept;
        
    }
    //---------------------------------------------------------------------------------------------------------------------
    
    //Methods for Service Selection ---------------------------------------------------------------------------------------
    public ServiceInfo getSelectedService() {
        return includedService;
    }
    public void setSelectedService(ServiceInfo service){
        
        this.includedService = service;
        
    }
    private void addIncludedService(ServiceInfo serviceInfo){
        
        if(!hasIncludedService(serviceInfo))
            listOfIncludedServices.add(serviceInfo);
        
    }
    public boolean hasIncludedService(ServiceInfo service){
        
        for(Iterator it=listOfIncludedServices.iterator();it.hasNext();)
            if(((ServiceInfo)it.next()).getID().equals(service.getID()))
                    return true;
            
        return false; 
    }
    //---------------------------------------------------------------------------------------------------------------------
    
    //Methods for Resolve Service -----------------------------------------------------------------------------------------
    
    public String getSelectedOutputID(){
        return this.selectedOutput;
    }
    
    public void setSelectedOutputID(String selectedOutput){
        this.selectedOutput = selectedOutput;
    }
    
    public void addSuggestionValue(String serviceID,String inputID, String outputID){
        
        for(Iterator it=listOfIncludedServices.iterator();it.hasNext();){
            ServiceInfo serviceInfo = (ServiceInfo) it.next();
            if(serviceInfo.getID().equals(serviceID)){
                List<Input> inputs = serviceInfo.getInput();
                for(Iterator itInp=inputs.iterator();itInp.hasNext();){
                    Input input = (Input) itInp.next();
                    if(input.getID().equals(inputID)){
                        SuggestionValue sv = new SuggestionValue();
                        sv.setValueID(outputID);
                        input.getSuggestedValue().add(sv);
                    }
                }
            }                
        }
            
    }
    
    //---------------------------------------------------------------------------------------------------------------------
    
    //Auxiliar Methods ----------------------------------------------------------------------------------------------------
    public ServiceInfo getIncludedService(String serviceID){
        
        for(Iterator it=listOfIncludedServices.iterator();it.hasNext();){
            ServiceInfo info = (ServiceInfo)it.next();
            if(info.getID().equals(serviceID))
                return info;
        }
        
        return null;
    }
     
    //Return URI of ontology element label
    public String getOntologyUri(String label){
        
        for(Iterator it = listOfLabels.iterator();it.hasNext();){
            SemanticConcept sc = (SemanticConcept) it.next();
            if(sc.getName().equals(label))
                return sc.getUrl();
        }
        return " ";
    }
    
    //Return label of URI ontology element
    public String getLabel(String uri){
        
        for(Iterator it = listOfLabels.iterator();it.hasNext();){
            SemanticConcept sc = (SemanticConcept) it.next();
            if(sc.getUrl().equals(uri))
                return sc.getName();
        }
        return " ";
    }
    
    public String getOutputName(String outputID){
        
        String result = outputID;
        
        for(Iterator it=listOfIncludedServices.iterator();it.hasNext();){
            
            ServiceInfo info = (ServiceInfo) it.next();
            
            List<Output> outputs = info.getOutput();
            for(Iterator itOut=outputs.iterator();itOut.hasNext();){
                
                Output output = (Output) itOut.next();
                
                if(output.getID().equals(outputID))
                    result = "Output " + outputID.split("-OutputID-")[1] + " of " + mapServToAnalysisName.get(info.getID());            
                
            }
        }
        
        return result;
    }
    
    public String getServiceInfoForToolTip(String serviceID){
        
        String result = " ";
        
        for(Iterator it=listOfIncludedServices.iterator();it.hasNext();){
            
            ServiceInfo info = (ServiceInfo)it.next();
            
            if(info.getID().equals(serviceID)){
                
                result = "<html><p width=\"300px\"><b>Service: </b>" + info.getName() + "</p>";
                
                //If service is a connector, add description
                if(info.getFunction().isEmpty())
                    result = result + "<p width=\"300px\"><b>Description: </b> " + info.getDescription() + "</p>";
                
                List<Input> inputs = info.getInput();
                int count = 1;
                for(Iterator itInput = inputs.iterator();itInput.hasNext();){
                    Input input = (Input) itInput.next();
                    result = result + "<p width=\"300px\"><b>Input " + count + ":</b> " + " " + this.getLabel(input.getSemanticalType()) + "</p>";   
                    count++;
                }
                List<Output> outputs = info.getOutput();
                count = 1;
                for(Iterator itOutput = outputs.iterator();itOutput.hasNext();){
                    Output output = (Output) itOutput.next();
                    result = result + "<p width=\"300px\"><b>Output " + count + ":</b> " + " " + this.getLabel(output.getSemanticalType()) + "</p>";   
                    count++;
                }
            }
        }
        
        return result;
        
    }
    
    public ServiceInfo getServiceInfo(String serviceId){
        
        for(Iterator it=listOfIncludedServices.iterator();it.hasNext();){
            ServiceInfo info = (ServiceInfo) it.next();
            if(info.getID().equals(serviceId))
                return info;
        }
        
        return null;
    }
    
    public DataSource addDataSource(String userValue,File[] selectedFiles){
        
        DataSource dataSource = new DataSource("data"+(countDataSources+1),"Data Source " + (countDataSources+1),selectedInputConcept.getName(),userValue);
        
        //If data source is not already contained in the list listOfDataSources
        if(!this.hasDataSource(dataSource)){
            
            //Create directory associated to data source and add selected files to it
            String value = this.createDataSourceDirectory(dataSource.getDataSourceID(),selectedFiles);
            
            //Set value of data source
            dataSource.setDataSourceValue(value);
            
            //Add data source to listOfDataSources
            listOfDataSources.add(dataSource);
            
            //Remove value of dataSource from list "listOfRemovedDataSources"
            for(Iterator it=listOfRemovedDataSources.iterator();it.hasNext();){
                if(((String) it.next()).equals(value))
                    it.remove();
            }
            
            countDataSources++;
            
            return dataSource;
        }
        
        return null;
                
    }
    
    //Create directory associated to data source and add selected files to it
    private String createDataSourceDirectory(String dataSourceID,File[] selectedFiles){
         
        //Create directory to store files associated to dataSource
        File dir = new File(sessionDirectory.getPath()+"/"+dataSourceID);
        dir.mkdir();
        
        //Add created directory to map "dataSourcesDirectories"
        dataSourcesDirectories.put(dataSourceID,dir);
        
        String value = "";
        
        //For each file
        try {
            
            File newFile = null;
            
            for(File file : selectedFiles) {
                
                newFile = new File(dir.getPath()+"/" + file.getName());
            
                //Create new file
                newFile.createNewFile();
                    
                //Read original file into recently created file
                BufferedReader br = new BufferedReader(new FileReader(file));
                PrintWriter pw = new PrintWriter(new FileWriter(newFile));
                String line;
                while((line = br.readLine()) != null)
                    pw.write(line+"\n");
                pw.flush();
                pw.close();
                br.close();
            }
            
            //Construct a string representing all created files
            if(selectedFiles.length == 1)
                value = dir.getPath()+"/"+selectedFiles[0].getName();
            else{
                for(int i=0;i<selectedFiles.length-1;i++)
                    value = value + dir.getPath() + "/" + selectedFiles[i].getName() + ";";
                value = value + dir.getPath() + "/" + selectedFiles[selectedFiles.length-1].getName();
            }
            
            return value;
            
        }catch (IOException ex) { }
        
        return value;
        
    }
    
    public void removeDataSource(String dataSourceID){
        
        DataSource dataSourceToBeRemoved = null;
        
        //If data source to be removed is contained in the list "listOfDataSources", save it into "dataSourceToBeRemoved"
        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
            DataSource element = (DataSource) it.next();
            if(element.getDataSourceID().equals(dataSourceID))
                dataSourceToBeRemoved = element;
        }
        
        //If data source is found, remove it. Else, do nothing
        if(dataSourceToBeRemoved != null){
            
            //Remove directory associated do dataSourceID and all files on it
            this.removeDataSourceDirectory(dataSourceID);
            
            //Add removed dataSource to list "listOfRemovedDataSources"
            listOfRemovedDataSources.add(this.getDataSourceValue(dataSourceID));
            
            //Remove data source from list "listOfDataSources"
            listOfDataSources.remove(dataSourceToBeRemoved);
        }
                        
    }
    
    //Remove directory associated do data source and all files on it
    private void removeDataSourceDirectory(String dataSourceID){
        
        //If map "dataSourcesDirectories" contains the dataSourceID
        if(dataSourcesDirectories.containsKey(dataSourceID)){
            
            //Get directory associated to dataSourceID
            File dir = dataSourcesDirectories.get(dataSourceID);
            
            //List files contained in this directory
            File[] listOfFiles = dir.listFiles();
            
            //Delete each file contained in this directory
            for(File file : listOfFiles)
                file.delete();
            
            //Delete directory
            dir.delete();
            
            //Remove directory from map "dataSourcesDirectories"
            dataSourcesDirectories.remove(dataSourceID);
            
        }
        
    }
    
    private boolean hasDataSource(DataSource dataSource){
        
        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
            DataSource aux = (DataSource) it.next();
            if(aux.getDataSourceUserValue().equals(dataSource.getDataSourceUserValue()))
                return true;
        }
        
        return false;
    }
    
    //Return the number of data sources
    public int getNumberOfDataSources(){
        
        return listOfDataSources.size();
    }
    
    //Return the number of included services
    public int getNumberOfIncludedServices(){
        
        return listOfIncludedServices.size();
    }
    
    public String getDataSourceInfoForToolTip(String dataSourceID){
        
        String result = "";
        
        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
            DataSource dataSource = (DataSource)it.next();
            if(dataSource.getDataSourceID().equals(dataSourceID)){
                
                result = result + "<html><p><b>Input concept: </b>" + dataSource.getDataSourceConcept() + "</p><br><p><b>Files:</b></p>"; 
                        
                String[] parts = dataSource.getDataSourceUserValue().split(";");
                for(int i=0;i<parts.length;i++){
                    result = result + "<p>"+parts[i]+"</p>";
                }
                result = result + "</html>";
                
            }   
        }
        
        return result;
        
    }
    
    public LinkedList<DataSource> getListOfDataSources(){
        return listOfDataSources;
    }
    
    public void updateDataSource(String dataSourceUserValue){
        
        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
            DataSource dataSource = (DataSource)it.next();
            if(dataSource.getDataSourceUserValue().equals(dataSourceUserValue))
                dataSource.setDataSourceConcept(selectedInputConcept.getName());
        }
        
    }
    
    public String getDataSourceConcept(String dataSourceValue){
        
        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
            DataSource dataSource = (DataSource)it.next();
            if(dataSource.getDataSourceValue().equals(dataSourceValue))
                return dataSource.getDataSourceConcept();
        }
        
        return null;
    }
    
    public String getDataSourceName(String dataSourceValue){
        
        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
            DataSource dataSource = (DataSource)it.next();
            if(dataSource.getDataSourceValue().equals(dataSourceValue))
                return dataSource.getDataSourceName();
        }
        
        return null;
    }
    
    public String getDataSourceID(String dataSourceValue){
        
        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
            DataSource dataSource = (DataSource)it.next();
            if(dataSource.getDataSourceValue().equals(dataSourceValue))
                return dataSource.getDataSourceID();
        }
        
        return null;
    }
    
    public String getDataSourceIDByUserValue(String dataSourceUserValue){
        
        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
            DataSource dataSource = (DataSource)it.next();
            if(dataSource.getDataSourceUserValue().equals(dataSourceUserValue))
                return dataSource.getDataSourceID();
        }
        
        return null;
    }
    
    public String getDataSourceValue(String dataSourceID){
        
        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
            DataSource dataSource = (DataSource)it.next();
            if(dataSource.getDataSourceID().equals(dataSourceID))
                return dataSource.getDataSourceValue();
        }
        
        return null;
    }    
    
    
    public int getCountNewAnalysis(){
        return this.countNewAnalysis;
    }
    
    public void iterateCountNewAnalysis(){
        this.countNewAnalysis++;
    }
    
    private SemanticConcept hasElement(LinkedList<SemanticConcept> listForSearch,SemanticConcept concept){
        
        for(Iterator it=listForSearch.iterator();it.hasNext();){
            SemanticConcept currentConcept = (SemanticConcept) it.next();
            if(currentConcept.getUrl().equals(concept.getUrl()))
                return currentConcept;
        }
        
        return null;
        
    }
    
    //Update all suggestionValues of all services contained in the composition referencing the dataSource being removed
    public void updateAllSuggestionValuesForDataSourceRemoval(String dataSourceID){
        
        String value = this.getDataSourceValue(dataSourceID);
        
        //For each previously included service
        for(Iterator it=listOfIncludedServices.iterator();it.hasNext();){
            ServiceInfo info = (ServiceInfo) it.next();
            //For each suggestionValue of each input of serviceInfo
            for(Iterator itInput = info.getInput().iterator();itInput.hasNext();){
                for(Iterator itSug = ((Input) itInput.next()).getSuggestedValue().iterator();itSug.hasNext();){
                    SuggestionValue suggestionValue = (SuggestionValue) itSug.next();
                    //If value of suggestionValue is not null and is equal to dataSource value, remove it
                    if(suggestionValue.getValue() != null && suggestionValue.getValue().equals(value))
                        itSug.remove();       
                }
            }
        }
    }
    
    //Update list "listOfSelectedInputs" to remove reference to dataSource being removed
    public void updateListOfSelectedInputsForDataSourceRemoval(String dataSourceID){
     
        //If listOfSelectedInputs contains a reference to dataSource being removed, remove it from listOfSelectedInputs
        if(listOfSelectedInputs.containsKey(dataSourceID))
            listOfSelectedInputs.remove(dataSourceID);
                
    }
    
    //Update list "listOfSelectedInputs" to remove reference to outputs of service being removed
    public void updateListOfSelectedInputsForActivityRemoval(String outputID){
     
        //If listOfSelectedInputs contains a reference to output of service being removed, remove it from listOfSelectedInputs
        if(listOfSelectedInputs.containsKey(outputID))
            listOfSelectedInputs.remove(outputID);
                
    }
    
    //Put service into "listOfExcludedServices" and remove it from "listOfIncludedServices"
    public void updateListOfIncludedServices(String serviceID){
        
        for(Iterator it = listOfIncludedServices.iterator();it.hasNext();){
            ServiceInfo si = (ServiceInfo) it.next();
            if(si.getID().equals(serviceID)){
                
                //Remove directory associated do service and all files on it
                this.removeServiceDirectory(serviceID);
                
                listOfExcludedServices.add(si);
                
                it.remove();
            }
        }
    }
    
    public String getServiceDirectoryPath(String serviceID){
        
        return servicesDirectories.get(serviceID).getPath();
    }
    
    public File getServiceDirectory(String serviceID){
        
        return servicesDirectories.get(serviceID);
        
    }
    
    //Create directory associated to service
    private void createServiceDirectory(String serviceID){
        
        String changedServiceID = serviceID.replaceAll("\\.","&");
        changedServiceID = changedServiceID.replaceAll(":","&");
         
        //Create directory to store output files of service
        File dir = new File(sessionDirectory.getPath()+"/"+changedServiceID);
        dir.mkdir();
        
        //Add created directory to map "servicesDirectories"
        servicesDirectories.put(serviceID,dir);
        
    }
    
    //Remove directory associated do service and all files on it
    private void removeServiceDirectory(String serviceID){
        
        //If map "servicesDirectories" contains the serviceID
        if(servicesDirectories.containsKey(serviceID)){
            
            //Get directory associated to serviceID
            File dir = servicesDirectories.get(serviceID);
            
            //List files contained in this directory
            File[] listOfFiles = dir.listFiles();
            
            //Delete each file contained in this directory
            for(File file : listOfFiles)
                file.delete();
            
            //Delete directory
            dir.delete();
            
            //Remove directory from map "servicesDirectories"
            servicesDirectories.remove(serviceID);
            
        }
        
    }
    
    //Remove all files inside service directory
    public void clearServiceDirectory(String serviceID){
        
        //If map "servicesDirectories" contains the serviceID
        if(servicesDirectories.containsKey(serviceID)){
            
            //Get directory associated to serviceID
            File dir = servicesDirectories.get(serviceID);
            
            //List files contained in this directory
            File[] listOfFiles = dir.listFiles();
            
            //Delete each file contained in this directory
            for(File file : listOfFiles)
                file.delete();            
        }
        
    }
    
    public boolean serviceDirectoryIsEmpty(String serviceID){
        
        //If map "servicesDirectories" contains the serviceID
        if(servicesDirectories.containsKey(serviceID)){
            
            //Get directory associated to serviceID
            File dir = servicesDirectories.get(serviceID);
            
            //List files contained in this directory
            File[] listOfFiles = dir.listFiles();
            
            return listOfFiles.length == 0;
        }
        
        return true;
        
    }
    
    //Update all suggestionValues of all services contained in the composition referencing the service being removed
    public void updateAllSuggestionValuesForActivityRemoval(String serviceID){
        
        //For each previously included service
        for(Iterator it=listOfIncludedServices.iterator();it.hasNext();){
            ServiceInfo info = (ServiceInfo) it.next();
            //For each suggestionValue of each input of serviceInfo
            for(Iterator itInput = info.getInput().iterator();itInput.hasNext();){
                for(Iterator itSug = ((Input) itInput.next()).getSuggestedValue().iterator();itSug.hasNext();){
                    SuggestionValue suggestionValue = (SuggestionValue) itSug.next();
                    //If valueID of suggestionValue references some input/output of service being removed, remove it
                    if(suggestionValue.getValueID().contains(serviceID))
                        itSug.remove();
                }
            }
        }
        
    }
    
    public void addToMapServToAnalysisName(String serviceID, String analysisName){
        mapServToAnalysisName.put(serviceID,analysisName);
    }
    
    public String getAnalysisNameToServ(String serviceID){
        return mapServToAnalysisName.get(serviceID);
    }
    
    //---------------------------------------------------------------------------------------------------------------------
        
    public void cleanAll(){
        
        //Initialize client
        client = new SemanticSCoClient();
        
        //Initialize LinkedLists
        listOfIncludedServices = new LinkedList<>();
        listOfExcludedServices = new LinkedList<>();
        listOfSelectedInputs = new HashMap<>();
        listOfDataSources = new LinkedList<>();
        listOfRemovedDataSources = new LinkedList<>();
        mapServToAnalysisName = new HashMap<>();
        
        //Call "excludeAllFiles"
        this.excludeAllFiles();
        
        //Initialize sessionDirectory
        sessionDirectory = null;
        
        //Initialize LinkedLists "dataSourcesDirectories" and "servicesDirectories"
        dataSourcesDirectories = new HashMap<>();
        servicesDirectories = new HashMap<>();
        
        //Initialize count of data source elements
        countDataSources = 0;
        
        //Initialize objects
        creator = new JaxbCreation();
        extractor = new JaxbExtraction();
                
    }
    
    
    public void excludeAllFiles(){
        
        //For each directory created for each data source
        for(Map.Entry<String,File> entry : dataSourcesDirectories.entrySet()){
            
            //Get directory
            File dir = entry.getValue();
            
            //List files contained in this directory
            File[] listOfFiles = dir.listFiles();
            
            //Delete each file contained in this directory
            for(File file : listOfFiles)
                file.delete();
            
            //Delete directory
            dir.delete();
            
        }
        
        //For each directory created for each service
        for(Map.Entry<String,File> entry : servicesDirectories.entrySet()){
            
            //Get directory
            File dir = entry.getValue();
            
            //List files contained in this directory
            File[] listOfFiles = dir.listFiles();
            
            //Delete each file contained in this directory
            for(File file : listOfFiles)
                file.delete();
            
            //Delete directory
            dir.delete();
            
        }
        
        //Finally, delete sessionDirectory
        if(sessionDirectory != null)
            sessionDirectory.delete();
        
    }
    
    // -------------------------------------------------------------------------
    
    public String getActivitiDbDriver(){
        return this.activitiDbDriver;
    }
    
    public String getActivitiDbURL(){
        return this.activitiDbURL;
    }
    
    public String getActivitiDbUser(){
        return this.activitiDbUser;
    }
    
    public String getActivitiDbPassword(){
        return this.activitiDbPassword;
    }
    
}
