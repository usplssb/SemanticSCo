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

package classes;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Wsdl2Parameters")  
@XmlAccessorType(XmlAccessType.NONE)
public class Wsdl2Parameters {
    @XmlElement  
    private String classPath;   
    
    @XmlElement  
    private String serviceAddress;   
    
    @XmlElement  
    private String targetNamespace;   
    
    @XmlElement  
    private String targetNamespacePrefix = " ";   
    
    @XmlElement  
    private String schemaTargetNamespace = "http://services";  
    
    @XmlElement  
    private String schemaTargetNamespacePrefix = " ";  
    
    @XmlElement  
    private String attributeFormDefault = "qualified";  
    
    @XmlElement  
    private String elementFormDefault = "qualified";  
    
   
    public String getClassPath() {  
        return classPath;  
    }  
    
    public void setClassPath(String classPath) {  
        this.classPath = classPath;  
    }   
    
    public String getServiceAddress() {  
        return serviceAddress;  
    }  
    
    public void setServiceAddress(String serviceAddress) {  
        this.serviceAddress = serviceAddress;  
    }   
    
    public String getTargetNamespace() {  
        return targetNamespace;  
    }  
    
    public void setTargetNamespace(String targetNamespace) {  
        this.targetNamespace = targetNamespace;  
    }   
    
    public String getTargetNamespacePrefix() {  
        return targetNamespacePrefix;  
    }  
    
    public void setTargetNamespacePrefix(String targetNamespacePrefix) {  
        this.targetNamespacePrefix = targetNamespacePrefix;  
    }   
    
     public String getSchemaTargetNamespace() {  
        return schemaTargetNamespace;  
    }  
    
    public void setSchemaTargetNamespace(String schemaTargetNamespace) {  
        this.schemaTargetNamespace = schemaTargetNamespace;  
    }   
    
    public String getSchemaTargetNamespacePrefix() {  
        return schemaTargetNamespacePrefix;  
    }  
    
    public void setSchemaTargetNamespacePrefix(String schemaTargetNamespacePrefix) {  
        this.schemaTargetNamespacePrefix = schemaTargetNamespacePrefix;  
    }   
    
    public String getAttributeFormDefault() {  
        return attributeFormDefault;  
    }  
    
    public void setAttributeFormDefault(String attributeFormDefault) {  
        this.attributeFormDefault = attributeFormDefault;  
    } 
    
    public String getElementFormDefault() {  
        return elementFormDefault;  
    }  
    
    public void setElementFormDefault(String elementFormDefault) {  
        this.elementFormDefault = elementFormDefault;  
    } 
}
