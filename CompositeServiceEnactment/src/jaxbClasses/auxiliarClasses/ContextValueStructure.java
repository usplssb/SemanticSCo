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

public class ContextValueStructure {

    private String outputID;
    private String serviceID;
    private String outputValue;
    
    public ContextValueStructure(String serviceID, String outputID, String outputValue){
        
        this.serviceID = serviceID;
        this.outputID = outputID;
        this.outputValue = outputValue;
    }

    public String getOutputID() {
        return outputID;
    }

    public void setOutputID(String outputID) {
        this.outputID = outputID;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(String outputValue) {
        this.outputValue = outputValue;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }
    
}
