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
  
import javax.xml.bind.annotation.XmlRegistry;  
  
@XmlRegistry  
public class ObjectFactory  
{  
    public CorrectionMethod createCorrectionMethod() {  
        return new CorrectionMethod();  
    }  
    
    public ListOfCorrectionMethods createListOfCorrectionMethods() {  
        return new ListOfCorrectionMethods();  
    }
    
    public Species createSpecies() {  
        return new Species();  
    }  
    
    public ListOfSpecies createListOfSpecies() {  
        return new ListOfSpecies();  
    } 
    
    public FileRepresentation createFileRepresentation(){
        return new FileRepresentation();
    }
    
    public ListOfFiles createListOfFiles(){
        return new ListOfFiles();
    }
    
    public DistanceMeasure createDistanceMeasure(){
        return new DistanceMeasure();
    }
    
    public ListOfDistanceMeasures createListOfDistanceMeasures(){
        return new ListOfDistanceMeasures();
    }
    
    public HierarchicalClusteringMethod createHierarchicalClusteringMethod(){
        return new HierarchicalClusteringMethod();
    }
    
    public ListOfHierarchicalClusteringMethods createListOfHierarchicalClusteringMethods(){
        return new ListOfHierarchicalClusteringMethods();
    }
    
    public Pathway createPathway(){
        return new Pathway();
    }
    
    public ListOfPathways createListOfPathways(){
        return new ListOfPathways();
    }
    
    public GeneIdentifierType createGeneIdentifierType(){
        return new GeneIdentifierType();
    }
    
    public ListOfGeneIdentifierTypes createListOfGeneIdentifierTypes(){
        return new ListOfGeneIdentifierTypes();
    }
    
    public ExperimentalCondition createExperimentalCondition(){
        return new ExperimentalCondition();
    }
    
    public ListOfExperimentalConditions createListOfExperimentalConditions(){
        return new ListOfExperimentalConditions();
    }
    
    public EnrichmentCategory createEnrichmentCategory(){
        return new EnrichmentCategory();
    }
    
    public ListOfEnrichmentCategories createListOfEnrichmentCategories(){
        return new ListOfEnrichmentCategories();
    }
      
}  