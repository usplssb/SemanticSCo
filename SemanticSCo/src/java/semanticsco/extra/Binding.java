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

//Class representing a WSDL binding
public class Binding {
    
    //Binding details
    private String name;
    private String location;
    private String namespace; 
    private String protocol;
    private String address;
    private String interfaceId;
    
    //Constructor
    public Binding(){
        
        //Initialize variables
        this.name = "";
        this.location = "";
        this.namespace = "";
        this.protocol = "";
        this.address = "";
        this.interfaceId = "";
    }
   
    //Return binding name
    public String getName() {
        return name;
    }

    //Set binding name
    public void setName(String name) {
        this.name = name;
    }
    
    //Return binding location
    public String getLocation() {
        return location;
    }

    //Set binding location
    public void setLocation(String location) {
        this.location = location;
    }

    //Return binding namespace
    public String getNamespace() {
        return namespace;
    }

    //Set binding namespace
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    //Return binding protocol
    public String getProtocol() {
        return protocol;
    }

    //Set binding protocol
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    //Return binding address
    public String getAddress() {
        return address;
    }

    //Set binding address
    public void setAddress(String address) {
        this.address = address;
    }
    
    //Return identifier of interface associated to binding
    public String getInterfaceId() {
        return interfaceId;
    }

    //Set identifier of interface associated to binding
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }
    
}
