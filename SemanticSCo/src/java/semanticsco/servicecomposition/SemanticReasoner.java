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

import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.owlapiv3.OWL;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import semanticsco.interactions.basictypes.Node;
import semanticsco.interactions.basictypes.SemanticConcept;
import semanticsco.interactions.basictypes.Tree;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

//Class used to reason a OWL ontology
public class SemanticReasoner{
    
    private OWLClass owlClass;
    private IncrementalClassifier classifier;
    
    private OWLOntology semOntology;
    
    //Constructor
    public SemanticReasoner() { 
        
    }
    
    //Load ontology
    public boolean load(String entity){
        
        try {
            
            //Create class for entity
            this.owlClass = OWL.Class(entity);
                
            //Try to load ontology into OWLOntology object. If it fails, an exception is generated        
            OWLOntology ontology = OWL.manager.loadOntologyFromOntologyDocument(owlClass.getIRI());
        
            //Get an instance of the incremental classifier
            this.classifier = new IncrementalClassifier(ontology);
        
            //Remove ontology from manager
            OWL.manager.removeOntology(ontology);
        
            //Classify
            classifier.classify();
            
            return true;
            
        } catch (OWLOntologyCreationException ex) {
            
            return false;
            
        }
    }
    
    //Return a list containing all equivalent classes of loaded entity
    public LinkedList<String> findEquivalentClasses(){
        
        LinkedList<String> equivalentClasses = new LinkedList<>();
                
        //Get all equivalent classes and set LinkedList equivalentClasses
        Set<OWLClass> classes = classifier.getEquivalentClasses(owlClass).getEntities();
        
        //For each equivalent class
        Iterator it = classes.iterator();
        while(it.hasNext()){
            OWLClass oClass = (OWLClass)it.next();
            //Add to LinkedList equivalentClasses
            if(!oClass.isOWLThing())
                equivalentClasses.add(oClass.getIRI().toString());
        }
        
        return equivalentClasses;
    }
    
    //Return a list containing all direct superclasses of loaded entity
    public LinkedList<String> findDirectSuperclasses(){
        
        LinkedList<String> directSuperclasses = new LinkedList<>();
        
        //Get all direct superclasses and set LinkedList directSuperclasses
        Set<OWLClass> superClasses = classifier.getSuperClasses(owlClass,true).getFlattened();
        
        //For each direct superclass
        Iterator it = superClasses.iterator();
        while(it.hasNext()){
            OWLClass oClass = (OWLClass)it.next();
            //If is not the element owl:Thing, add to LinkedList directSuperclasses
            if(!oClass.isOWLThing())
                directSuperclasses.add(oClass.getIRI().toString());
        }
        
        return directSuperclasses;
    }
    
    //Return a list containing all indirect superclasses of loaded entity
    public LinkedList<String> findIndirectSuperclasses(){
        
        LinkedList<String> indirectSuperclasses = new LinkedList<>();
        
        //Get all superclasses and add them to LinkedList "indirectSuperclasses"
        Set<OWLClass> superClasses = classifier.getSuperClasses(owlClass,false).getFlattened();
        Iterator it = superClasses.iterator();
        while(it.hasNext()){
            OWLClass oClass = (OWLClass)it.next();
            //If is not the element owl:Thing, add to LinkedList indirectSuperclasses
            if(!oClass.isOWLThing())
                indirectSuperclasses.add(oClass.getIRI().toString());
        }
        
        //Remove direct superclasses from LinkedList "indirectSuperclasses"
        indirectSuperclasses.removeAll(findDirectSuperclasses());
        
        return indirectSuperclasses;
    }
    
    //Return a list containing all direct subclasses of loaded entity
    public LinkedList<String> findDirectSubclasses(){
        
        LinkedList<String> directSubclasses = new LinkedList<>();
        
        //Get all direct subclasses and set LinkedList directSubclasses
        Set<OWLClass> subClasses = classifier.getSubClasses(owlClass,true).getFlattened();
        
        //For each direct subclass
        Iterator it = subClasses.iterator();
        while(it.hasNext()){
            OWLClass oClass = (OWLClass)it.next();
            //If is not the element owl:Nothing, add to LinkedList directSubclasses
            if(!oClass.isOWLNothing())
                directSubclasses.add(oClass.getIRI().toString());
        }
        
        return directSubclasses;
    }
    
    //Return a list containing all indirect subclasses of loaded entity
    public LinkedList<String> findIndirectSubclasses(){
        
        LinkedList<String> indirectSubclasses = new LinkedList<>();
        
        //Get all subclasses and add them to LinkedList "indirectSubclasses"
        Set<OWLClass> subClasses = classifier.getSubClasses(owlClass,false).getFlattened();
        Iterator it = subClasses.iterator();
        while(it.hasNext()){
            OWLClass oClass = (OWLClass)it.next();
            //If is not the element owl:Nothing, add to LinkedList indirectSubclasses
            if(!oClass.isOWLNothing())
                indirectSubclasses.add(oClass.getIRI().toString());
        }
        
        //Remove direct subclasses from LinkedList "indirectSubclasses"
        indirectSubclasses.removeAll(findDirectSubclasses());
        
        return indirectSubclasses;
    }
    
    //Given a OWL class, this method returns its label if available
    public String getLabel(String entity){
        
        String label= " ";
        
        try{
            //Create OWL class for entity
            OWLClass entityClass = OWL.Class(entity);
        
            //Try to load ontology into OWLOntology object. If it fails, an exception is generated
            OWLOntology ontology = OWL.manager.loadOntologyFromOntologyDocument(entityClass.getIRI());
        
            //Extract annotations associated to the OWL class
            Set<OWLAnnotation> annotations = entityClass.getAnnotations(ontology);
        
            //Remove ontology from manager
            OWL.manager.removeOntology(ontology);
                
            //For each annotation, if it is a label, set variable "label"
            Iterator it = annotations.iterator();
            while(it.hasNext()){
                OWLAnnotation annotation = (OWLAnnotation) it.next();
                if(annotation.getProperty().isLabel())
                    label = annotation.getValue().toString().split("\"")[1];
            }
            
        }catch(OWLOntologyCreationException ex){
            return label;
        }
        
        return label;
    }
    
    //Return tree of all input types contained in the specified ontology (subclasses of rootClass)
    public Tree findAllInputs(String ontologyUri,String rootClass){

        OntModel model;
        
        try{
        
            //Create a reasoner
            model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

            //Create new URL of ontology
            URL url = new URL(ontologyUri);
        
            //Create model for the ontology
            model.read(url.openStream(),null);
        
            //Load model to the reasoner
            model.prepare();
        
            //Compute the classification tree
            ((PelletInfGraph) model.getGraph()).getKB().classify();
            
            //Set OWL root element (e.g., it could be thing: model.getOntClass(OWL.Thing.getURI()))
            OntClass owlRoot = model.getOntClass(rootClass);
        
            //Create new tree element
            Tree tree = new Tree();
        
            //Recursively create a tree starting from root
            Node rootNode = this.createTree(owlRoot);
        
            //Set root node of tree
            tree.setRoot(rootNode);
            
            return tree;
            
        }catch(IOException ex){
            return null;
        }
        
    }
    
    //Recursively create nodes of a tree
    private Node createTree(OntClass owlClass) {
        
        //If is a class Nothing, return null
        if(owlClass.getURI().equals(com.hp.hpl.jena.vocabulary.OWL.Nothing.getURI()))
            return null;

        //Create new node, and set name and url
        Node node = new Node();
        node.setName(owlClass.getLabel(null));
        node.setUrl(owlClass.getURI());
        
        //Create list of processed subclasses
        HashMap<String,String> processedSubclasses = new HashMap<>();
        
        //For each direct subclass
        for(Iterator it=owlClass.listSubClasses(true);it.hasNext();){
            
            OntClass subclass = (OntClass) it.next();
            
            //If subclass is not anonymous
            if(!subclass.isAnon()){
                
                //If subclass was not already processed
                if(!processedSubclasses.containsKey(subclass.getURI())){
                    
                    //Recursively call method "createTree" to process subclass
                    Node subNode = createTree(subclass);
                    
                    //If subclass is not owl:Nothing, create add subclass to parent node. Otherwise, the returned node is null
                    if(subNode != null) {
                        
                        //Add subclass node to parent node
                        node.getChildrenNode().add(subNode);
                        
                        //Add subclass to list of processed subclasses
                        processedSubclasses.put(subNode.getUrl(),subNode.getName());
            
                    }
                }
            }
        }
        
        return node;   
        
    }
    
    //Return functions that require the specified input in the ontology (has_participant relation)
    public LinkedList<SemanticConcept> findFunctionsWithInput(String input,String semanticSimilarity,String semanticSimilarityOfInput){
        
        LinkedList<SemanticConcept> listOfFunctions = new LinkedList<>();
        
        try{
            
            //Create local class
            OWLClass localClass = OWL.Class(input);
            
            //Create object property "has_participant"
            OWLObjectProperty owlObjectProperty = OWL.ObjectProperty("http://www.obofoundry.org/ro/ro.owl#has_participant");
            
            //Try to load ontology of Class into OWLOntology object. If it fails, an exception is generated        
            OWLOntology ontologyInput = OWL.manager.loadOntologyFromOntologyDocument(localClass.getIRI());
            
            //Remove ontology from manager
            OWL.manager.removeOntology(ontologyInput);
            
            //Get axioms of OWL Class 
            Set<OWLAxiom> props = localClass.getReferencingAxioms(ontologyInput);

            //For each axiom
            for(Iterator it = props.iterator(); it.hasNext();) {
                
                OWLAxiom axiom = (OWLAxiom) it.next();
                
                //Get Object Properties of Axiom
                Set<OWLObjectProperty> objectProperties = axiom.getObjectPropertiesInSignature();

                //For each object property of axiom
                for(Iterator it2 = objectProperties.iterator(); it2.hasNext();) {

                    //If the axiom contains the relation "has participant"
                    if(it2.next().toString().contains(owlObjectProperty.getIRI().toString())) {
                        
                        //Get classes contained in the axiom
                        Set<OWLClass> owlClasses = axiom.getClassesInSignature();

                        //For each class contained in the axiom
                        for(Iterator it3 = owlClasses.iterator(); it3.hasNext();) {
                            
                            OWLClass auxClass = (OWLClass) it3.next();
                            
                            //If class is not the class being searched
                            if(!auxClass.getIRI().toString().equals(localClass.getIRI().toString())){
                                
                                //If listOfFunctions do not already contains the class, add it
                                boolean contains = false;
                                for(Iterator it4 = listOfFunctions.iterator();it4.hasNext();){
                                    SemanticConcept sc = (SemanticConcept) it4.next();
                                    if(sc.getUrl().equals(auxClass.getIRI().toString()))
                                        contains = true;
                                }
                                if(!contains){
                                    SemanticConcept aux = new SemanticConcept();
                                    aux.setName(this.getLabel(auxClass.getIRI().toString()));
                                    aux.setUrl(auxClass.getIRI().toString());
                                    //Adjust semantic similarity
                                    aux.setSemanticSimilarity(String.valueOf((Double.parseDouble(semanticSimilarity) + Double.parseDouble(semanticSimilarityOfInput))/2));
                                    
                                    listOfFunctions.add(aux);
                                }
                            }
                        }
                    }
                }

            }
        
        }catch(OWLOntologyCreationException ex){
            return listOfFunctions;
        }
        
        return listOfFunctions;
    }
    
    public LinkedList<SemanticConcept> findAllLabels(String ontologyUri,String rootClass){
        
        LinkedList<SemanticConcept> list = new LinkedList<>();
        
        try{
        
            //Create a reasoner
            OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

            //Create new URL of ontology
            URL url = new URL(ontologyUri);
        
            //Create model for the ontology
            model.read(url.openStream(),null);
        
            //Load model to the reasoner
            model.prepare();
        
            //Compute the classification tree
            ((PelletInfGraph) model.getGraph()).getKB().classify();
            
            //Set OWL root element (e.g., it could be thing: model.getOntClass(OWL.Thing.getURI()))
            OntClass owlRoot = model.getOntClass(rootClass);
            
            SemanticConcept sc = new SemanticConcept();
            sc.setUrl(owlRoot.getURI());
            sc.setName("");
            sc.setSemanticSimilarity("");
            list.add(sc);
        
            //For each subclass (direct or indirect)
            for (Iterator it = owlRoot.listSubClasses(false); it.hasNext();) {

                OntClass subclass = (OntClass) it.next();

                //If subclass is not anonymous
                if (!subclass.isAnon()) {

                    //If subclass is not nothing
                    if(!subclass.getURI().equals(com.hp.hpl.jena.vocabulary.OWL.Nothing.getURI())){
                        
                        sc = new SemanticConcept();
                        sc.setUrl(subclass.getURI());
                        sc.setName("");
                        sc.setSemanticSimilarity("");
                    
                        list.add(sc);
                    }
                }
            }
            
        }catch(IOException ex){}
        
        return list;
        
    }
    
}