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

package wsdlComponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeOperation {  
    
    private String name;
    
    private List<TreeTopLevelElement> elements;
    
  
    public TreeOperation(String name) {  
        this.name = name;  
        elements = new ArrayList<>();
    }  
  
    public String getName() {  
        return name;  
    }  
    
    @Override  
    public String toString() {
        return getName();  
    }
    
    public void addTopLevelElement(TreeTopLevelElement element) {
        elements.add(element);  
    }  
  
    public List<TreeTopLevelElement> getTopLevelElements() {  
        return Collections.unmodifiableList(elements);  
    }  
}  
