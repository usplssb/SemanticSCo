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

package semanticsco.extra;

import java.util.Iterator;
import java.util.LinkedList;

//Class representing a business service (BPMN process)
public class Service implements Comparable<Service>{

    //Service details
    private String identifier;
    private String name;
    private String description;
    private String location;
    private String namespace;
    private LinkedList<Interface> interfaces;
    
    //Semantic information
    private LinkedList<String> functions;
    private LinkedList<DataElement> inputs;
    private LinkedList<DataElement> outputs;
    
    //Semantic similarity
    private double functionValue;
    private double inputValue;
    private double outputValue;
    
    
    //Constructor
    public Service(){
        
        //Initialize Linked Lists and variables
        this.interfaces = new LinkedList<>();
        this.functions = new LinkedList<>();
        this.inputs = new LinkedList<>();
        this.outputs = new LinkedList<>();
        
        this.identifier = "";
        this.name = "";
        this.description = "";
        this.location = "";
        this.namespace = "";
        this.functionValue = 0.0;
        this.inputValue = 0.0;
        this.outputValue = 0.0;
        
    }

    //Return service identifier
    public String getIdentifier() {
        return identifier;
    }

    //Set service identifier
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    //Return service name
    public String getName() {
        return name;
    }

    //Set service name
    public void setName(String name) {
        this.name = name;
    }
    
    //Return service description
    public String getDescription() {
        return description;
    }

    //Set service description
    public void setDescription(String description) {
        this.description = description;
    }

    //Return service location
    public String getLocation() {
        return location;
    }

    //Set service location
    public void setLocation(String location) {
        this.location = location;
    }
    
    //Return service namespace
    public String getNamespace() {
        return namespace;
    }

    //Set service namespace
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    //Return list of interfaces associated to service
    public LinkedList<Interface> getInterfaces() {
        return interfaces;
    }

    //Set list of interfaces associated to service
    public void setInterfaces(LinkedList<Interface> interfaces) {
        this.interfaces = interfaces;
    }
    
    //Return list of semantic concepts representing service functions
    public LinkedList<String> getFunctions() {
        return functions;
    }
    
    //Set list of semantic concepts representing service functions
    public void setFunctions(LinkedList<String> functions) {
        this.functions = functions;
    }
    
    //Add semantic concept representing service function
    public void addFunction(String function){
        this.functions.add(function);
    }

    //Return list of semantic concepts representing service inputs
    public LinkedList<DataElement> getInputs() {
        return inputs;
    }
    
    //Set list of semantic concepts representing service inputs
    public void setInputs(LinkedList<DataElement> inputs) {
        this.inputs = inputs;
    }

    //Add semantic concept representing service input
    public void addInput(DataElement input){
        this.inputs.add(input);
    }

    //Return list of semantic concepts representing service outputs
    public LinkedList<DataElement> getOutputs() {
        return outputs;
    }
    
    //Set list of semantic concepts representing service outputs
    public void setOutputs(LinkedList<DataElement> outputs) {
        this.outputs = outputs;
    }
    
    //Add semantic concept representing service output
    public void addOutput(DataElement output){
        this.outputs.add(output);
    }

    //Return value representing semantic similarity of service function
    public double getFunctionValue() {
        return functionValue;
    }

    //Set value representing semantic similarity of service function
    public void setFunctionValue(double functionValue) {
        this.functionValue = functionValue;
    }

    //Return value representing semantic similarity of service input
    public double getInputValue() {
        return inputValue;
    }

    //Set value representing semantic similarity of service input
    public void setInputValue(double inputValue) {
        this.inputValue = inputValue;
    }
    
    //Return value representing semantic similarity of service output
    public double getOutputValue() {
        return outputValue;
    }

    //Set value representing semantic similarity of service output
    public void setOutputValue(double outputValue) {
        this.outputValue = outputValue;
    }
    

    //Return value representing global semantic similarity of service
    public double getSemanticSimilarity(double functValue, double inValue, double outValue) {
        return ((functValue*this.functionValue) + (inValue*this.inputValue) + (outValue*this.outputValue));
    }

    //Method used to sort services according to semantic similarity
    @Override
    public int compareTo(Service s) {
        double comparedSize = s.functionValue + s.inputValue + s.outputValue;
	if((this.functionValue + this.inputValue + this.outputValue) > comparedSize)
            return 1;
	else if ((this.functionValue + this.inputValue + this.outputValue) == comparedSize)
            return 0;
	else
            return -1;
    }
    
    //Check if the service has a given semantic input concept
    public boolean hasInput(String semInputToCheck){
        
        for(Iterator it = inputs.iterator();it.hasNext();){
            DataElement de = (DataElement) it.next();
            if(de.getSemanticConcept().equals(semInputToCheck))
                return true;
        }
        
        return false;
    }
    
    //Check if the service has a given semantic output concept
    public boolean hasOutput(String semOutputToCheck){
        
        for(Iterator it = outputs.iterator();it.hasNext();){
            DataElement de = (DataElement) it.next();
            if(de.getSemanticConcept().equals(semOutputToCheck))
                return true;
        }
        
        return false;
    }
    
    //Method used to print service information
    @Override
    public String toString(){
        return this.identifier;
    }
    
}
