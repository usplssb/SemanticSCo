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
import java.util.List;  
import javax.swing.event.TreeModelListener;  
import javax.swing.tree.TreeModel;  
import javax.swing.tree.TreePath;  
  
public class WsdlTreeModel implements TreeModel {  
       
    private String root = "Interfaces";
    private List<TreeModelListener> listeners;  
    private List<TreeInterface> interfaces;  
  
    //Constructor
    public WsdlTreeModel(List<TreeInterface> interfaces) {  
        
        //Initialize list of listeners
        this.listeners = new ArrayList<>();
        
        //Set list of interfaces
        this.interfaces = interfaces;  
    }
    
    @Override
    public Object getRoot() {  
        return this.root;  
    }  
  
    @Override
    public Object getChild(Object parent, int index) {  
        
        //If parent is the root node, return an interface
        if (parent == root)
            return interfaces.get(index);  
  
        //If parent is an interface, returns an operation
        if (parent instanceof TreeInterface)
            return ((TreeInterface) parent).getOperations().get(index);
        
        //If parent is an operation, returns a top level element
        if (parent instanceof TreeOperation)
            return ((TreeOperation) parent).getTopLevelElements().get(index);
        
        //If parent is an top level element, returns an element
        if (parent instanceof TreeTopLevelElement)
            return ((TreeTopLevelElement) parent).getElements().get(index);
  
        //If parent is non of those  
        throw new IllegalArgumentException("Invalid parent class" + parent.getClass().getSimpleName());  
    }  
  
    //Return number of childs
    @Override
    public int getChildCount(Object parent) {  
        
        //If parent is the root node, returns the number of interfaces
        if (parent == root)  
            return interfaces.size();  
  
        //If parent is an interface, returns the number of operations
        if (parent instanceof TreeInterface)
            return ((TreeInterface) parent).getOperations().size();  
        
        //If parent is an operation, returns the number of top level elements
        if (parent instanceof TreeOperation)
            return ((TreeOperation) parent).getTopLevelElements().size();
        
        //If parent is a top level element, returns the number of elements
        if (parent instanceof TreeTopLevelElement)
            return ((TreeTopLevelElement) parent).getElements().size();
  
        //If parent is non of those  
        throw new IllegalArgumentException("Invalid parent class"  + parent.getClass().getSimpleName());  
    }  
  
    //Returns the index of a child  
    @Override
    public int getIndexOfChild(Object parent, Object child) { 
        //If parent is the root node
        if (parent == root)  
            return interfaces.indexOf(child); 
        //If parent is an interface
        if (parent instanceof TreeInterface)  
            return ((TreeInterface) parent).getOperations().indexOf(child);
        //If parent is an operation
        if (parent instanceof TreeOperation)  
            return ((TreeOperation) parent).getTopLevelElements().indexOf(child);
        //If parent is a top level element
        if (parent instanceof TreeTopLevelElement)  
            return ((TreeTopLevelElement) parent).getElements().indexOf(child);
  
        return 0;  
    }  
  
    @Override
    public boolean isLeaf(Object node) {  
        return node instanceof TreeElement;
    }  
  
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {  }  
  
    @Override
    public void removeTreeModelListener(TreeModelListener l) {  
        listeners.remove(l);  
    }  
  
    @Override
    public void addTreeModelListener(TreeModelListener l) {  
        listeners.add(l);  
    }  
}  