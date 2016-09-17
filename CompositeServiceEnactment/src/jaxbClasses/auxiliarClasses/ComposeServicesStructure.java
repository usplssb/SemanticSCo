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

public class ComposeServicesStructure {

    private String serviceID;
    private String inputID;
    
    //Sequence
    private String selectedOutput;
    
    //Constructor
    public ComposeServicesStructure(String serviceID, String inputID, String selectedOutput){
        
        this.serviceID = serviceID;
        this.inputID = inputID;
        this.selectedOutput = selectedOutput;

    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getInputID() {
        return inputID;
    }

    public void setInputID(String inputID) {
        this.inputID = inputID;
    }

    public String getSelectedOutput() {
        return selectedOutput;
    }

    public void setSelectedOutput(String selectedOutput) {
        this.selectedOutput = selectedOutput;
    }
    
    
}
