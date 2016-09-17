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
 * 
 * Based on:
 * 
 * Copyright (C) 2006 FRANCE TELECOM R&D
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
*******************************************************************************/

package semanticsco.servicecomposition;

import semanticsco.extra.DataElement;
import semanticsco.extra.Service;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.francetelecom.fl.causalLinkMatrix.entry.FT_SetOfTriples;
import org.francetelecom.fl.causalLinkMatrix.entry.FT_Triple;
import org.francetelecom.fl.causalLinkMatrix.label.FT_RowAndColumnLabel;

/**
 * Support the construction of CLMManager+ matrix based on a list of services
 discovered from a user request and described using owl ontologies.
 *   - reasoning supported by OwlAPI
 * Implements a Causal Link Matrix with annotations on NF properties
*/
public class CLMManager extends org.francetelecom.fl.causalLinkMatrix.definition.FT_Matrix {

    //Number of rows of the CLMManager
    private int numRows;

    //Number of columns of the CLMManager
    private int numColumns;

    //List of input concepts of the CLMManager
    private List<String> inputConcepts = new LinkedList<>();

    //List of output concepts of the CLMManager
    private List<String> outputConcepts = new LinkedList<>();

    //HashTable instantiation to declare labels of rows and columns (inputs and outputs of CLMManager services)
    private FT_RowAndColumnLabel RCLabel;

    //Create a Hashtable with all possible output concepts
    private Hashtable ClmLabels = new Hashtable();

    //Labels of rows and columns of the CLMManager
    private FT_RowAndColumnLabel ft_rcLabel;

    /**
     * Constructor 
     * 
     * @param numRows
     * @param numColumns
     * @param rcLabel - Hash table with the labels of rows and columns
     */
    public CLMManager(int numRows, int numColumns, FT_RowAndColumnLabel rcLabel) {

        super(numRows,numColumns);
        this.ft_rcLabel = rcLabel;
        
    }

    //Return the hashtable of the CLMManager rows and columns labels
    public FT_RowAndColumnLabel ft_getRcLabel() {

        return ft_rcLabel;
    }

    //Return true if the CLMManager instantiation was ok, and false in case an element insertion failed.
    public boolean instantiateCLM() {

        //Instantiate a set of triples
        FT_SetOfTriples setOfTriples;

        //For each entry of the CLMManager, insert an empty set of triples
        for(int i=0;i<this.ft_getRow();i++) {
            for(int j=0;j<this.ft_getColumn();j++) {
                setOfTriples = new FT_SetOfTriples();
                boolean valid_insertion = this.ft_setEntry(i,j,setOfTriples);
                if (!valid_insertion) {
                    return valid_insertion;
                }
            }
        }

        return true;
    }

    /**
     * Insert a triple into the CLMManager matrix
     * 
     * @param row -- row number where triple should be inserted
     * @param column -- column number where element should be inserted
     * @param triple -- the triple to be inserted
     * @return "true" in case triple is successfully inserted (always true)
    */
    public boolean ft_addtripleInEntry(int row, int column, FT_Triple triple) {

        //If row and column indexes are correct, add the triple in the Set of triples
        if (row>=0 && row<this.ft_getRow() && column>=0 && column<this.ft_getColumn())
            ((FT_SetOfTriples) this.ft_getMatrix()[row][column]).add(triple);

        return true;
    }

    //Return triples of the matrix cell {row,column}
    public FT_SetOfTriples ft_getCellTriplets(int row, int column) {

        FT_SetOfTriples triples = new FT_SetOfTriples();

        triples = (FT_SetOfTriples) this.ft_getMatrix()[row][column];

        return triples;

    }

    /**
     * Check the semantic matching between two concepts.
     * Three matching values are possible: Exact (1.0); Plugin(0.66); SubSume (0.33); and NO matching (0.0)
     *
     * @param clmOutput CLMManager output concept
     * @param servOutput service output concept
     * @return semantic match value
     */
    public static double checkSemCon(String clmOutput, String servOutput) {

        //Create OWL reasoner
        SemanticReasoner reasoner = new SemanticReasoner();
        
        //Load CLMManager output concept
        if(reasoner.load(clmOutput)){
            
            //If service output has an exact semantic match with the CLMManager output
            if(reasoner.findEquivalentClasses().contains(servOutput))
                return 1.0;
            
            //If service output is a superclass of the CLMManager output (subsumption matching)
            LinkedList<String> superClasses = reasoner.findDirectSuperclasses();
            superClasses.addAll(reasoner.findIndirectSuperclasses());
            if(superClasses.contains(servOutput))
                return 0.33;
            
            //If service output is a subclass of the CLMManager output (plugin matching)
            LinkedList<String> subClasses = reasoner.findDirectSubclasses();
            subClasses.addAll(reasoner.findIndirectSubclasses());
            if(subClasses.contains(servOutput))
                return 0.66;            
        } 

        //In case the CLMManager output concept could not be loaded, return 0.0 (No semantic matching)
        return 0.0;
    }
    

    /**
     * Compute the Causal Link Matrix based on the list of selected services.
     *
     * @param selectedServices - selected services
     * @return CLMManager - causal link matrix
     */
    public CLMManager ComputeCLM(List<Service> selectedServices) {

        //Save the CLMManager input concepts (ensuring no duplicates) 
        for(Iterator it = selectedServices.iterator();it.hasNext();){
            Service service = (Service) it.next();
            
            for(Iterator itS = service.getInputs().iterator();itS.hasNext();){
                DataElement de = (DataElement) itS.next();
                if(!inputConcepts.contains(de.getSemanticConcept()))
                    inputConcepts.add(de.getSemanticConcept());
            }
            
        }
        
        //Set number of rows (inputs) of the CLMManager
        numRows = inputConcepts.size();

        //The columns of the CLMManager should contain the UNION of service input and output concepts 
        //So, add service input concepts to outputConcepts (this list will be the column of the CLMManager) (ensuring no duplicates) 
        for(Iterator it = inputConcepts.iterator();it.hasNext();){
            String inputConcept = (String) it.next();
            if(!outputConcepts.contains(inputConcept))
                outputConcepts.add(inputConcept);
        }
        
	//Save the CLMManager output concepts (ensuring no duplicates)
        for(Iterator it = selectedServices.iterator();it.hasNext();){
            Service service = (Service) it.next();
            
            for(Iterator itS = service.getOutputs().iterator();itS.hasNext();){
                DataElement de = (DataElement) itS.next();
                if(!outputConcepts.contains(de.getSemanticConcept()))
                    outputConcepts.add(de.getSemanticConcept());
            }
        }
        
        //Set number of columns (outputs) of the CLMManager
        numColumns = outputConcepts.size();
        
        //Manipulate labels of CLMManager
        RCLabel = new FT_RowAndColumnLabel(numRows, numColumns);
        for(int i=0; i<outputConcepts.size(); i++)
            ClmLabels.put(outputConcepts.get(i), i);
        RCLabel.ft_rcLabelInitialisationWithConceptKey(ClmLabels);
        
        //Create new CLMManager with all selected services
        CLMManager clm = new CLMManager(numRows, numColumns, RCLabel);

        //Instantiates the CLMManager
        clm.instantiateCLM();

	//For each row of the CLMManager (input concept)
        for (int i=0; i<numRows; i++) {

            //Get CLMManager input concept
            String input = inputConcepts.get(i);

            //For each selected service
            for (int j=0; j<selectedServices.size(); j++) {

                //If the service has the CLMManager input concept
                if(selectedServices.get(j).hasInput(input)){
                    
                    //For each column of the CML (output concept)
                    for (int k=0; k<outputConcepts.size(); k++) {

                        //For each service output
                        for (int m=0; m<selectedServices.get(j).getOutputs().size(); m++) {

                            String output = selectedServices.get(j).getOutputs().get(m).getSemanticConcept();

                            //Verify if the service output semantically matches the CLMManager output
                            double semMatVal = checkSemCon(output, outputConcepts.get(k));
                            
                            //If they match (exact, plugin or subsume matching)
                            if(semMatVal > 0.0){
                                
                                //Create service info to be added to CLMManager entry: serviceName(serviceID;serviceOutputID;serviceFunctionsSize;function1;function2; ... ; functionN;)
                                String servNameGoals = selectedServices.get(j).getName() + "(" + selectedServices.get(j).getIdentifier() + ";" + selectedServices.get(j).getOutputs().get(m).getIdentifier() + ";" + selectedServices.get(j).getFunctions().size() + ";";
                                for (int n=0; n<selectedServices.get(j).getFunctions().size(); n++)
                                    servNameGoals = servNameGoals.concat(selectedServices.get(j).getFunctions().get(n) + ";");
                                servNameGoals = servNameGoals.concat(")");

                                //Create CML entry
                                FT_Triple clmEntry = new FT_Triple(servNameGoals, semMatVal, 1);
                                
                                //Add entry to the CLMManager
                                clm.ft_addtripleInEntry(i,k,clmEntry);
                            }
                        }
                    }
                }
            }
        }

        return clm;
    }
}
