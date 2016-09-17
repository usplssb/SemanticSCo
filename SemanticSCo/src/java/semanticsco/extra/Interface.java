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

//Class representing a WSDL interface
public class Interface {
    
    //Interface details
    private String identifier;
    private String name;
    private String location;
    private String namespace;
    private LinkedList<Binding> bindings;
    
    //Constructor
    public Interface(){
        
        //Initialize Linked List and variables
        bindings = new LinkedList<>();
        this.identifier = "";
        this.name = "";
        this.location = "";
        this.namespace = "";
    
    }

    //Return interface identifier
    public String getIdentifier() {
        return identifier;
    }

    //Set interface identifier
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    //Return interface name
    public String getName() {
        return name;
    }

    //Set interface name
    public void setName(String name) {
        this.name = name;
    }
    
    //Return interface location
    public String getLocation() {
        return location;
    }

    //Set interface location
    public void setLocation(String location) {
        this.location = location;
    }

    //Return interface namespace
    public String getNamespace() {
        return namespace;
    }

    //Set interface namespace
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    //Return list of bindings associated to interface
    public LinkedList<Binding> getBindings() {
        return bindings;
    }
    
    //Add binding to list of bindings associated to interface
    public void addBinding(Binding binding){
        this.bindings.add(binding);
    }
    
}
