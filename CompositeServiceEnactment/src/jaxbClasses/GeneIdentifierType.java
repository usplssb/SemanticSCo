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

package jaxbclasses;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "GeneIdentifierType")  
@XmlAccessorType(XmlAccessType.NONE)
public class GeneIdentifierType {  
      
    @XmlElement  
    private String GeneIdentifierTypeName;   
    
    @XmlElement  
    private String GeneIdentifierTypeDescription; 
      
    public String getName() {  
        return GeneIdentifierTypeName;  
    }  
    
    public String getDescription() {  
        return GeneIdentifierTypeDescription;  
    }  
      
    public void setName(String GeneIdentifierTypeName) {  
        this.GeneIdentifierTypeName = GeneIdentifierTypeName;  
    }   
    
    public void setDescription(String GeneIdentifierTypeDescription) {  
        this.GeneIdentifierTypeDescription = GeneIdentifierTypeDescription;  
    }   
}