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

import java.util.LinkedList;
import java.util.List;

public class DataElement {

    private String identifier;
    private String name;
    private String semanticConcept;
    private String syntacticalType;
    private String executionValue = null;
    private String serviceID  = null;

    //Store suggestions values or possible values to be composed with this data element
    public List<DataElement> suggestionValues = new LinkedList<>();
    
    //Empty Constructor
    public DataElement(){

    }
    
    //Constructor
    public DataElement(String identifier, String name, String semanticConcept, String syntacticalType,String serviceID){
        
        this.identifier = identifier;
        this.name = name;
        this.semanticConcept = semanticConcept;
        this.syntacticalType = syntacticalType;
        this.serviceID = serviceID;
        this.executionValue = null;
        this.suggestionValues = new LinkedList<>();
        
    }
    
    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemanticConcept() {
        return semanticConcept;
    }

    public void setSemanticConcept(String semanticConcept) {
        this.semanticConcept = semanticConcept;
    }

    public String getSyntacticalType() {
        return syntacticalType;
    }

    public void setSyntacticalType(String syntacticalType) {
        this.syntacticalType = syntacticalType;
    }
    
    public String getExecutionValue() {
        return executionValue;
    }

    public void setExecutionValue(String executionValue) {
        this.executionValue = executionValue;
    }

    public List<DataElement> getSuggestionValues() {
        return suggestionValues;
    }

    public void setSuggestionValues(List<DataElement> suggestionValues) {
        this.suggestionValues = suggestionValues;
    }
    
    public void addSuggestionValue(DataElement dataElement) {
        this.suggestionValues.add(dataElement);
    }
    
    public boolean hasSuggestionValue(DataElement dataElement){
        return this.suggestionValues.contains(dataElement);
    }
    
    public String getServiceID() {
        return this.serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }
    
}
