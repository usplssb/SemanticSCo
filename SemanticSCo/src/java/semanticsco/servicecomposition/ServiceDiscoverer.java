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

package semanticsco.servicecomposition;

import semanticsco.extra.DataElement;
import semanticsco.extra.Service;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.transport.TransportException;

//Class for service discovery
public class ServiceDiscoverer {
    
    /***********************************************************
    * Place your juddi user here
    ***********************************************************/
    private String juddiUser = "user@email.com";
    /***********************************************************/
    
    private SemanticReasoner reasoner;
    
    //Lists to store requisition
    private List<String> desiredFunctions;
    private List<String> desiredInputs;
    private String desiredOutput;
    
    //Lists to store classes associated to function values
    private List<String> fSearchValues;
    private List<String> fEquivalentClasses;
    private List<String> fDirectSubclasses;
    private List<String> fIndirectSubclasses;
    private List<String> fDirectSuperclasses;
    private List<String> fIndirectSuperclasses;
    
    //Lists to store classes associated to input values
    private List<String> iSearchValues;
    private List<String> iEquivalentClasses;
    private List<String> iDirectSubclasses;
    private List<String> iIndirectSubclasses;
    private List<String> iDirectSuperclasses;
    private List<String> iIndirectSuperclasses;
    
    //Lists to store classes associated to output values
    private List<String> oSearchValues;
    private List<String> oEquivalentClasses;
    private List<String> oDirectSubclasses;
    private List<String> oIndirectSubclasses;
    private List<String> oDirectSuperclasses;
    private List<String> oIndirectSuperclasses;
    
    //List to store found services
    private List<Service> foundServices;
    
    //Constructor to be used to service discovery
    public ServiceDiscoverer(List<String> desiredFunctions,List<String> desiredInputs){
        
        //Set semantic concepts representing desired functions and inputs
        this.desiredFunctions = desiredFunctions;
        this.desiredInputs = desiredInputs;
        
        //Initialize reasoner and find equivalent classes, subclasses and superclasses of all request concepts
        reasoner = new SemanticReasoner();
        this.setFunctionSearchValues();
        this.setInputSearchValues();
        
    }
    
    //Constructor to be used to resolve service
    public ServiceDiscoverer(String desiredOutput){
        
        //Set semantic concepts representing desired outputs
        this.desiredOutput = desiredOutput;
        
        //Initialize reasoner and find equivalent classes of output concept
        reasoner = new SemanticReasoner();
        this.setOutputSearchValues();
        
    }
    
    //Find services according to semantic concepts representing the request
    public List<Service> findServices() {
        
        //Initialize List "foundServices"
        this.foundServices =  new LinkedList<>();
        
        try{
            
            RegistryInquiry jr = new RegistryInquiry();
        
            //Try to authenticate user in service registry
            String authentication = jr.authenticate(juddiUser);
                
            //If user is authenticated
            if(authentication != null){
                
                //Find services semantically annotated with corresponding functionality and add them to List "foundServices"
                foundServices.addAll(jr.findBusinessServicesByFunction((LinkedList<String>) this.fSearchValues));
                
                //Find services semantically annotated with corresponding input and add them to List "foundServices" (without duplicates)
                for(Iterator it = this.iSearchValues.iterator();it.hasNext();){
                    
                    String input = (String) it.next();
                    LinkedList<String> inputs = new LinkedList<>();
                    inputs.add(input);
                    
                    List<Service> auxList;
                    auxList = jr.findBusinessServicesByData("input", (LinkedList<String>) inputs);
                    for(Iterator i = auxList.iterator(); i.hasNext();){
                    
                        Service serv1 = (Service) i.next();
                    
                        boolean alreadyContains = false;
                        for(Iterator j = foundServices.iterator(); j.hasNext();)
                            if(((Service) j.next()).getIdentifier().equals(serv1.getIdentifier()))
                                alreadyContains = true;
                                 
                        if(!alreadyContains)
                            foundServices.add(serv1);
                    }
                }
                
                //Calculate semantic similarity
                this.setSemanticSimilarity("functionInput");
                
                //Sort List "foundServices" in descending order
                Collections.sort(foundServices, Collections.reverseOrder());
                
                //Logout from service registry
                jr.logout();
                
            }
            
        }catch(ConfigurationException|TransportException ex){ }
        
        return foundServices;
    }
    
    //Find services able to provide an output to resolve some service input
    public List<Service> findResolvingServices() {
        
        //Initialize List "foundServices"
        this.foundServices =  new LinkedList<>();
        
        try{
            
            RegistryInquiry jr = new RegistryInquiry();
        
            //Try to authenticate user in service registry
            String authentication = jr.authenticate(juddiUser);
                
            //If user is authenticated
            if(authentication != null){
                
                //Find services semantically annotated with corresponding output and add them to List "foundServices"
                for(Iterator it = this.oSearchValues.iterator(); it.hasNext();){
                    
                    String outputConcept = (String) it.next();
                    LinkedList<String> outputConcepts = new LinkedList<>();
                    outputConcepts.add(outputConcept);
                    foundServices.addAll(jr.findBusinessServicesByData("output", (LinkedList<String>) outputConcepts));
                    
                }
                
                //Calculate semantic similarity
                this.setSemanticSimilarity("output");
                
                //Sort List "foundServices" in descending order
                Collections.sort(foundServices, Collections.reverseOrder());
                
                //Logout from service registry
                jr.logout();
                
            }
            
        }catch(ConfigurationException|TransportException ex){ }
        
        return foundServices;
    }
    
    //Find equivalent classes, subclasses and superclasses of all request concepts representing functions
    private void setFunctionSearchValues(){
        
        //Initialize all Lists
        fSearchValues = new LinkedList<>();
        fEquivalentClasses = new LinkedList<>();
        fDirectSubclasses = new LinkedList<>();
        fIndirectSubclasses = new LinkedList<>();
        fDirectSuperclasses = new LinkedList<>();
        fIndirectSuperclasses = new LinkedList<>();
        
        //For each desired functionality, find all equivalent classes ensuring no duplicates
        Iterator it = desiredFunctions.iterator();
        while(it.hasNext()){
            String desiredFunction = (String) it.next();
            if(reasoner.load(desiredFunction)){
                fEquivalentClasses.removeAll(reasoner.findEquivalentClasses());
                fEquivalentClasses.addAll(reasoner.findEquivalentClasses());
            }
        }
        fSearchValues.removeAll(fEquivalentClasses);
        fSearchValues.addAll(fEquivalentClasses);
        
        //For each desired functionality (including equivalent classes), find all subclasses and superclasses ensuring no duplicates
        it = fEquivalentClasses.iterator();
        while(it.hasNext()){
            String desiredFunction = (String) it.next();
            if(reasoner.load(desiredFunction)){
                fDirectSubclasses.removeAll(reasoner.findDirectSubclasses());
                fDirectSubclasses.addAll(reasoner.findDirectSubclasses());
                fIndirectSubclasses.removeAll(reasoner.findIndirectSubclasses());
                fIndirectSubclasses.addAll(reasoner.findIndirectSubclasses());
                fDirectSuperclasses.removeAll(reasoner.findDirectSuperclasses());
                fDirectSuperclasses.addAll(reasoner.findDirectSuperclasses());
                fIndirectSuperclasses.removeAll(reasoner.findIndirectSuperclasses());
                fIndirectSuperclasses.addAll(reasoner.findIndirectSuperclasses());
                         
            }            
        }
        fSearchValues.removeAll(fDirectSubclasses);
        fSearchValues.addAll(fDirectSubclasses);
        fSearchValues.removeAll(fIndirectSubclasses);
        fSearchValues.addAll(fIndirectSubclasses);
        fSearchValues.removeAll(fDirectSuperclasses);
        fSearchValues.addAll(fDirectSuperclasses);
        fSearchValues.removeAll(fIndirectSuperclasses);
        fSearchValues.addAll(fIndirectSuperclasses);
                
    }
    
    //Find equivalent classes, subclasses and superclasses of all request concepts representing inputs
    private void setInputSearchValues(){
        
        //Initialize all Linked Lists
        iSearchValues = new LinkedList<>();
        iEquivalentClasses = new LinkedList<>();
        iDirectSubclasses = new LinkedList<>();
        iIndirectSubclasses = new LinkedList<>();
        iDirectSuperclasses = new LinkedList<>();
        iIndirectSuperclasses = new LinkedList<>();
        
        //For each desired input, find all equivalent classes ensuring no duplicates
        Iterator it = desiredInputs.iterator();
        while(it.hasNext()){
            String desiredInput = (String) it.next();
            if(reasoner.load(desiredInput)){
                iEquivalentClasses.removeAll(reasoner.findEquivalentClasses());
                iEquivalentClasses.addAll(reasoner.findEquivalentClasses());
            }
        }
        iSearchValues.removeAll(iEquivalentClasses);
        iSearchValues.addAll(iEquivalentClasses);
        
        //For each desired input (including equivalent classes), find all subclasses and superclasses ensuring no duplicates
        it = iEquivalentClasses.iterator();
        while(it.hasNext()){
            String desiredInput = (String) it.next();
            if(reasoner.load(desiredInput)){
                iDirectSubclasses.removeAll(reasoner.findDirectSubclasses());
                iDirectSubclasses.addAll(reasoner.findDirectSubclasses());
                iIndirectSubclasses.removeAll(reasoner.findIndirectSubclasses());
                iIndirectSubclasses.addAll(reasoner.findIndirectSubclasses());
                iDirectSuperclasses.removeAll(reasoner.findDirectSuperclasses());
                iDirectSuperclasses.addAll(reasoner.findDirectSuperclasses());
                iIndirectSuperclasses.removeAll(reasoner.findIndirectSuperclasses());
                iIndirectSuperclasses.addAll(reasoner.findIndirectSuperclasses());
            }            
        }
        iSearchValues.removeAll(iDirectSubclasses);
        iSearchValues.addAll(iDirectSubclasses);
        iSearchValues.removeAll(iIndirectSubclasses);
        iSearchValues.addAll(iIndirectSubclasses);
        iSearchValues.removeAll(iDirectSuperclasses);
        iSearchValues.addAll(iDirectSuperclasses);
        iSearchValues.removeAll(iIndirectSuperclasses);
        iSearchValues.addAll(iIndirectSuperclasses);
                
    }
    
    //Find equivalent classes of concept representing output
    private void setOutputSearchValues(){
        
        //Initialize all Linked Lists
        oSearchValues = new LinkedList<>();
        oEquivalentClasses = new LinkedList<>();
        oDirectSubclasses = new LinkedList<>();
        oIndirectSubclasses = new LinkedList<>();
        oDirectSuperclasses = new LinkedList<>();
        oIndirectSuperclasses = new LinkedList<>();
        
        //Find all equivalent classes of desired output ensuring no duplicates
        if(reasoner.load(desiredOutput)){
            oEquivalentClasses.removeAll(reasoner.findEquivalentClasses());
            oEquivalentClasses.addAll(reasoner.findEquivalentClasses());
        }
        oSearchValues.removeAll(oEquivalentClasses);
        oSearchValues.addAll(oEquivalentClasses);
        
        //For each equivalent class of desired output, find all subclasses and superclasses ensuring no duplicates
        Iterator it = oEquivalentClasses.iterator();
        while(it.hasNext()){
            String output = (String) it.next();
            if(reasoner.load(output)){
                oDirectSubclasses.removeAll(reasoner.findDirectSubclasses());
                oDirectSubclasses.addAll(reasoner.findDirectSubclasses());
                oIndirectSubclasses.removeAll(reasoner.findIndirectSubclasses());
                oIndirectSubclasses.addAll(reasoner.findIndirectSubclasses());
                oDirectSuperclasses.removeAll(reasoner.findDirectSuperclasses());
                oDirectSuperclasses.addAll(reasoner.findDirectSuperclasses());
                oIndirectSuperclasses.removeAll(reasoner.findIndirectSuperclasses());
                oIndirectSuperclasses.addAll(reasoner.findIndirectSuperclasses());
            }            
        }
        oSearchValues.removeAll(oDirectSubclasses);
        oSearchValues.addAll(oDirectSubclasses);
        oSearchValues.removeAll(oIndirectSubclasses);
        oSearchValues.addAll(oIndirectSubclasses);
        oSearchValues.removeAll(oDirectSuperclasses);
        oSearchValues.addAll(oDirectSuperclasses);
        oSearchValues.removeAll(oIndirectSuperclasses);
        oSearchValues.addAll(oIndirectSuperclasses);
                
    }
    
    private void setSemanticSimilarity(String discoveryType){
        
        
        if(discoveryType.equals("functionInput")){
            
            //For each found service
            for(Iterator i = foundServices.iterator(); i.hasNext();){
         
                Service service = (Service) i.next();
            
                //For each semantic functionality, assign semantic value according to semantic similarity 
                List<String> semFunctions = service.getFunctions();
                double value = 0.0;
                for(Iterator j = semFunctions.iterator(); j.hasNext();){
                
                    String semFunction = (String) j.next();
                
                    if(fEquivalentClasses.contains(semFunction))
                        value = value + 5.0; 
                    else if(fDirectSubclasses.contains(semFunction))
                        value = value + 4.0; 
                    else if(fIndirectSubclasses.contains(semFunction))
                        value = value + 3.0; 
                    else if(fDirectSuperclasses.contains(semFunction))
                        value = value + 2.0; 
                    else if(fIndirectSuperclasses.contains(semFunction))
                        value = value + 1.0; 
                }
                if(value > 0.0)
                    value = value/semFunctions.size();
                service.setFunctionValue(value);
            
                //For each semantic input, assign semantic value according to semantic similarity 
                List<DataElement> inputs = service.getInputs();
                value = 0.0;
                for(Iterator j = inputs.iterator(); j.hasNext();){
                
                    DataElement input = (DataElement) j.next();
                
                    if(iEquivalentClasses.contains(input.getSemanticConcept()))
                        value = value + 5.0; 
                    else if(iDirectSubclasses.contains(input.getSemanticConcept()))
                        value = value + 4.0; 
                    else if(iIndirectSubclasses.contains(input.getSemanticConcept()))
                        value = value + 3.0; 
                    else if(iDirectSuperclasses.contains(input.getSemanticConcept()))
                        value = value + 2.0; 
                    else if(iIndirectSuperclasses.contains(input.getSemanticConcept()))
                        value = value + 1.0; 
                }
                if(value > 0.0)
                    value = value/inputs.size();
                service.setInputValue(value);
            }        
        }
        else if(discoveryType.equals("output")){
            
            //For each found service
            for(Iterator i = foundServices.iterator(); i.hasNext();){
         
                Service service = (Service) i.next();
            
                //For each semantic output, assign semantic value according to semantic similarity 
                List<DataElement> outputs = service.getOutputs();
                double value = 0.0;
                for(Iterator j = outputs.iterator(); j.hasNext();){
                
                    DataElement output = (DataElement) j.next();
                
                    if(oEquivalentClasses.contains(output.getSemanticConcept()))
                        value = value + 5.0; 
                    else if(oDirectSubclasses.contains(output.getSemanticConcept()))
                        value = value + 4.0; 
                    else if(oIndirectSubclasses.contains(output.getSemanticConcept()))
                        value = value + 3.0; 
                    else if(oDirectSuperclasses.contains(output.getSemanticConcept()))
                        value = value + 2.0; 
                    else if(oIndirectSuperclasses.contains(output.getSemanticConcept()))
                        value = value + 1.0; 
                }
                if(value > 0.0)
                    value = value/outputs.size();
                service.setOutputValue(value);
            }
        }
    }
    
    public List<String> getOSearchValues(){
        return this.oSearchValues;
    }
}