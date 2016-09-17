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

package jaxbClasses.auxiliarClasses;

import java.util.LinkedList;

public class ServiceValidationStructure {

    private String serviceID;
    private LinkedList<String> inputIDs;
    
    public ServiceValidationStructure(String serviceID){
        
        //Initialize list of input IDs
        this.inputIDs = new LinkedList<>();
        
        this.serviceID = serviceID;
    }
    
    public void addInputID(String inputID){
        this.inputIDs.add(inputID);
    }

    public LinkedList<String> getInputIDs() {
        return this.inputIDs;
    }

    public void setInputIDs(LinkedList<String> inputIDs) {
        this.inputIDs = inputIDs;
    }

    public String getServiceID() {
        return this.serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }
    
}
