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

package semanticsco.servicecomposition;

import semanticsco.interactions.basictypes.SemanticConcept;
import semanticsco.interactions.basictypes.Tree;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SemanticProvider {
    
    private SemanticReasoner reasoner;
    
    //Initialize all Lists
    private List<String> equivalentClasses;
    private List<String> directSubclasses;
    private List<String> indirectSubclasses;
    private List<String> directSuperclasses;
    private List<String> indirectSuperclasses;
        
    
    //Empty Constructor
    public SemanticProvider(){
        
        //Initialize reasoner
        reasoner = new SemanticReasoner();
        
    }
    
    private void setSearchValues(String desiredInput){
        
        //Initialize all lists
        this.equivalentClasses = new LinkedList<>();
        this.directSubclasses = new LinkedList<>();
        this.indirectSubclasses = new LinkedList<>();
        this.directSuperclasses = new LinkedList<>();
        this.indirectSuperclasses = new LinkedList<>();
        
        //Find all equivalent classes of desired input (ensuring no duplicates)
        if(reasoner.load(desiredInput)){
            equivalentClasses.removeAll(reasoner.findEquivalentClasses());
            equivalentClasses.addAll(reasoner.findEquivalentClasses());
        }
        
        //Find all subclasses and superclasses of desired input (and equivalent classes) ensuring no duplicates
        for(Iterator it=equivalentClasses.iterator();it.hasNext();){
            
            String equivalentClass = (String) it.next();
            if(reasoner.load(equivalentClass)){
                directSubclasses.removeAll(reasoner.findDirectSubclasses());
                directSubclasses.addAll(reasoner.findDirectSubclasses());
                indirectSubclasses.removeAll(reasoner.findIndirectSubclasses());
                indirectSubclasses.addAll(reasoner.findIndirectSubclasses());
                directSuperclasses.removeAll(reasoner.findDirectSuperclasses());
                directSuperclasses.addAll(reasoner.findDirectSuperclasses());
                indirectSuperclasses.removeAll(reasoner.findIndirectSuperclasses());
                indirectSuperclasses.addAll(reasoner.findIndirectSuperclasses());
                         
            }            
        }
    }
    
    //Return functions that require the specified inputs in the ontology (has_participant relation)
    public LinkedList<SemanticConcept> findFunctionsWithInput(List<SemanticConcept> desiredInputs){
        
        LinkedList<SemanticConcept>listOfFunctions = new LinkedList();
        
        //For each desired input
        for(Iterator it=desiredInputs.iterator();it.hasNext();){
            
            SemanticConcept sc = (SemanticConcept) it.next();
            
            //Set search values
            this.setSearchValues(sc.getUrl());
            
            //For each equivalent class
            for(Iterator it2=equivalentClasses.iterator();it2.hasNext();){
                
                //Find functions for input
                LinkedList<SemanticConcept>aux = reasoner.findFunctionsWithInput((String) it2.next(),"5.0",sc.getSemanticSimilarity());
                
                //For each found function, verify if it is already contained into LinkedList "listOfFunctions" and add it if not
                for(Iterator it3=aux.iterator();it3.hasNext();){
                
                    SemanticConcept newSc = (SemanticConcept) it3.next();
                
                    if(!this.containsSemanticConcept(listOfFunctions,newSc))
                        listOfFunctions.add(newSc);
                
                }
            }      
            
            //For each direct superclass
            for(Iterator it2=directSuperclasses.iterator();it2.hasNext();){
                
                //Find functions for input
                LinkedList<SemanticConcept>aux = reasoner.findFunctionsWithInput((String) it2.next(),"4.0",sc.getSemanticSimilarity());
                
                //For each found function, verify if it is already contained into LinkedList "listOfFunctions" and add it if not
                for(Iterator it3=aux.iterator();it3.hasNext();){
                
                    SemanticConcept newSc = (SemanticConcept) it3.next();
                
                    if(!this.containsSemanticConcept(listOfFunctions,newSc))
                        listOfFunctions.add(newSc);
                
                }
            }
            
            //For each indirect superclass
            for(Iterator it2=indirectSuperclasses.iterator();it2.hasNext();){
                
                //Find functions for input
                LinkedList<SemanticConcept>aux = reasoner.findFunctionsWithInput((String) it2.next(),"3.0",sc.getSemanticSimilarity());
                
                //For each found function, verify if it is already contained into LinkedList "listOfFunctions" and add it if not
                for(Iterator it3=aux.iterator();it3.hasNext();){
                
                    SemanticConcept newSc = (SemanticConcept) it3.next();
                
                    if(!this.containsSemanticConcept(listOfFunctions,newSc))
                        listOfFunctions.add(newSc);
                
                }
            }
            
            //For each direct subclass
            for(Iterator it2=directSubclasses.iterator();it2.hasNext();){
                
                //Find functions for input
                LinkedList<SemanticConcept>aux = reasoner.findFunctionsWithInput((String) it2.next(),"2.0",sc.getSemanticSimilarity());
                
                //For each found function, verify if it is already contained into LinkedList "listOfFunctions" and add it if not
                for(Iterator it3=aux.iterator();it3.hasNext();){
                
                    SemanticConcept newSc = (SemanticConcept) it3.next();
                
                    if(!this.containsSemanticConcept(listOfFunctions,newSc))
                        listOfFunctions.add(newSc);
                
                }
            }
            
            //For each indirect subclass
            for(Iterator it2=indirectSubclasses.iterator();it2.hasNext();){
                
                //Find functions for input
                LinkedList<SemanticConcept>aux = reasoner.findFunctionsWithInput((String) it2.next(),"1.0",sc.getSemanticSimilarity());
                
                //For each found function, verify if it is already contained into LinkedList "listOfFunctions" and add it if not
                for(Iterator it3=aux.iterator();it3.hasNext();){
                
                    SemanticConcept newSc = (SemanticConcept) it3.next();
                
                    if(!this.containsSemanticConcept(listOfFunctions,newSc))
                        listOfFunctions.add(newSc);
                
                }
            }
            
        }
        
        return listOfFunctions;
    }
    
    //Return true if the element sc is already contained in the list. Otherwise, false.
    private boolean containsSemanticConcept(LinkedList<SemanticConcept> list, SemanticConcept sc){
        
        //For each element contained in the list
        for(Iterator it=list.iterator();it.hasNext();){
            
            SemanticConcept aux = (SemanticConcept) it.next();
            
            //If the provided semantic concept is already contained in the list, return true
            if(aux.getUrl().equals(sc.getUrl()))
                return true;
            
        }
        
        //Else, return false
        return false;
        
    }
    
    //Return tree of all input types contained in the specified ontology (subclasses of rootClass)
    public Tree findAllInputs(String ontologyUri,String rootClass){
        
        return reasoner.findAllInputs(ontologyUri,rootClass);
                
    }
    
    //Return labels of rootClass and all subclasses
    public LinkedList<SemanticConcept> findAllLabels(String ontologyUri, String rootClass){
        
        LinkedList<SemanticConcept> result = new LinkedList<>();
        
        //Find all subclasses
        LinkedList<SemanticConcept> list = reasoner.findAllLabels(ontologyUri, rootClass);
        
        //Create local reasoner
        SemanticReasoner or = new SemanticReasoner();
        
        //For each element of the list
        for(Iterator it=list.iterator();it.hasNext();){
            
            SemanticConcept entity = (SemanticConcept)it.next();
            entity.setName(or.getLabel(entity.getUrl()));
            
            result.add(entity);
        }
        
        return result;
        
    }
    
    public boolean checkSemanticCompatibility(String concept1,String concept2){
        
        //Initialize all lists
        LinkedList<String> relatedClasses = new LinkedList<>();
        
        //Find all equivalent classes of concept (ensuring no duplicates)
        if(reasoner.load(concept1)){
            relatedClasses.addAll(reasoner.findEquivalentClasses());
            relatedClasses.addAll(reasoner.findDirectSubclasses());
            relatedClasses.addAll(reasoner.findIndirectSubclasses());
            relatedClasses.addAll(reasoner.findDirectSuperclasses());
            relatedClasses.addAll(reasoner.findIndirectSuperclasses());
        }
        
        if(!relatedClasses.isEmpty() && relatedClasses.contains(concept2))
            return true;
        
        return false;
    }
    
}