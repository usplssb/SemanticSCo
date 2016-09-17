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

import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import jaxbClasses.basictypes.Node;

public class TreeExtraction {
    
    private Node root;
    
    public TreeExtraction(Node root){
        
        this.root = root;
        
    }
 
    public JTree getJTree() {
        
        //Create a tree starting with owl:Thing node as the root
        DefaultMutableTreeNode rootNode = createTree(root);
        
        //Create tree
        JTree classTree = new JTree(new DefaultTreeModel(rootNode));

        // expand everything
        for(int r=0; r<classTree.getRowCount(); r++ )
            classTree.expandRow( r );

        return classTree;
        
    }
    
    DefaultMutableTreeNode createTree(Node cls) {
        
        HashMap<String,String> concept = new HashMap<>();
        
        concept.put(cls.getUrl(), cls.getName());
        
        DefaultMutableTreeNode rootTree = new DefaultMutableTreeNode(concept);
        
        HashMap<String,String> processedSubs = new HashMap<>();
        
        //Get only direct subclasses
        for(Iterator subs = cls.getChildrenNode().iterator();subs.hasNext();){
            
            Node sub = (Node) subs.next();
            
            if(processedSubs.containsKey(sub.getUrl()))
                continue;
            
            DefaultMutableTreeNode node = createTree(sub);
            
            //If set contains owl:Nothing tree will be null
            if(node != null) {
                
                rootTree.add(node);
            
                HashMap<String,String> aux = (HashMap<String,String>) node.getUserObject();
                
                for (String key : aux.keySet())
                    processedSubs.put(key,aux.get(key));
                
            }
        }

        return rootTree;
    }
}
