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

import semanticsco.extra.Service;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;


/**
 * ServiceComposer allows to iteratively create a service composition based on the 
 services users select
 
 Services are stored in a Graph structure, and each service is represented as 
 a graph node. Each node (service) has children nodes (which compose with the services
 * outputs) and parent nodes, which compose with the service inputs.
 * 
 * Only one graph composition is available at each time, since only one service output can be 
 * selected for one service input by the user. Given this, there will not be alternative 
 * compositions for the same requirements.
 * 
 * A graph is considered ready for execution when there are no more open nodes, 
 * or open inputs, i.e., inputs that are not composed with other services outputs, or
 * that the user is not able to provide inputs to execute the service.
 * 
 * When a graph is ready for execution, it can be inspected to check the flow of
 * execution that has to be taken to execute all the services in the composition.
 *
 */
public class ServiceComposer {
	
    //Contains the graph composition, i.e., all the nodes/services
    private DirectedGraph<String,DefaultEdge> graphComposition = new DefaultDirectedGraph<>(DefaultEdge.class);    

    //Nodes that still have open inputs
    private List<String> openNodes = new LinkedList<>();
            
    //Empty Constructor
    public ServiceComposer(){
				
    }
	
    /**
     * Add a service to the Graph Composition
     * 
     * The added service may be composed with other services. This composition
     * may be done forwards or backwards
     * 
     * @param service - service to be added to the Graph Composition.
     * @param composingServices - services that the added service will be composed with.
     * @param isToCompose - defines if the service will be composed with other services.
    */
    public void addServiceToGraph(Service service, List<String> composingServices, boolean isToCompose){
		
        //If the graph composition does not contain the service, a node (vertex) is added to it
	if(!graphComposition.containsVertex(service.getIdentifier()))
            graphComposition.addVertex(service.getIdentifier());
		
	//If it is to compose services
	if(isToCompose && (composingServices != null)){
			
            //For each composing service
            for(int i=0; i<composingServices.size(); i++){
                
                //If a node (vertex) representing the composing service does not exist, add a node (vertex)
                if(!graphComposition.containsVertex(composingServices.get(i)))
                    graphComposition.addVertex(composingServices.get(i));
                
                //If an edge between the newly added service and the composing service does not exist
                if(graphComposition.getEdge(service.getIdentifier(),composingServices.get(i)) == null){
                
                    //Create edge to compose newly added service (source) with composing service (target)
                    graphComposition.addEdge(service.getIdentifier(),composingServices.get(i));
                }		
		
            }
	}

	//For each service input
	int openInputs = 0;
	for(int i=0; i<service.getInputs().size(); i++){
            //If service input is an open input (does not have execution value), increment "openInputs"
            if(service.getInputs().get(i).getExecutionValue() == null)
		openInputs++;
	}
        
        //If service has open inputs (without values for execution) and is not contained in the list "openNodes", add it
        if(openInputs>0 && !service.getInputs().isEmpty() && !openNodes.contains(service.getIdentifier()))
	    openNodes.add(service.getIdentifier());			
    }	
	
    /**
     * Checks if a given service can be removed from the set of GraphComposition
     * open nodes (OpenNodes). A node is removed if all its inputs have a given value.
     * 
     * @param service - service to be checked if it can be removed from the open nodes
     * @return true if the node was removed from the OpenNodes, or false if it didn't.
     */
    public boolean closeOpenNode(Service service){
		
        boolean removeOpenNode = true;
	
	if(service == null)
            return false;
		
	//Checks if the service still has some open input, i.e., node (service) can not be closed
	for(int i=0; i<service.getInputs().size(); i++){
			
            //If the input has no execution value (this is in fact an ID to access values in the ExecutionContext.AvailableValues)
            if(service.getInputs().get(i).getExecutionValue() == null){
                removeOpenNode = false;
		break;
            }				
	}
		
	//If it does not have open inputs, it can be removed from the open nodes!
	if(removeOpenNode)
            openNodes.remove(service.getIdentifier());        
		
	return removeOpenNode;
    }
	
	
    /**
     * Verify deadlock situation from serviceToID to serviceFromID, i.e., checks
     * if the serviceFromID is not a children node of the serviceToID! If a deadlock
     * is detected, the composition is not possible
     * 
     * @param serviceToID - ID of the service that has an input in the service for composition
     * @param serviceFromID - ID of the service that provides an output in the service composition
     * 
     * @return true if a deadlock situation is found and false otherwise
     * 
     */
    public boolean checkDeadlocks(String serviceToID, String serviceFromID){
        
        // - We do this, because the  was generating errors
        // when we were trying to find deadlock in not connected graphs, i.e., graphs
        // with parts that are connected!
        //If serviceToID has no children nodes, deadlock can not occur
        if(graphComposition.outgoingEdgesOf(serviceToID).isEmpty())
            return false;
        
        //Run DijkstraShortestPath algorithm to find paths between serviceToID and serviceFromID
        List path = DijkstraShortestPath.findPathBetween(graphComposition, serviceToID, serviceFromID);
      
        //If no paths were found, deadlock situation can not occur. Otherwise, deadlock is detected
        if(path == null)
            return false; 
	else
            return true;
    }
    
    //Return list of open nodes in the graph composition
    public List<String> getOpenNodes(){
		
	return this.openNodes;
    }	
    
    /**
     * Find the root nodes of the Service Composition
     * 
     * @return - IDs of the first nodes to be executed
    */
    public List<String> findRootNodes() {
		
	List<String> rootNodes = new LinkedList<>();
		
	//Get all nodes from composition
	Set<String> allNodes = graphComposition.vertexSet();
		
	//For each node
        Iterator it = allNodes.iterator();
	while(it.hasNext()){
            
            //If degree of node is zero (it has no incoming edges), add node to list "rootNodes"
            String nodeToCheck = (String)it.next();
            if(graphComposition.inDegreeOf(nodeToCheck) == 0)
                rootNodes.add(nodeToCheck);
            
	}
        
        return rootNodes;
    }
    
    //Return all nodes (services) from Service Composition
     public Set<String> getAllNodes(){
	
	return graphComposition.vertexSet();
    }
	
    
    //Find parent nodes (services) of the given node (service)
    public List<String> findParentNodes(String nodeID){
		
	List<String> parentNodes = new LinkedList<>();
	
        //Get incoming edges of given service
        Set<DefaultEdge> incomingEdges = graphComposition.incomingEdgesOf(nodeID);
		
	//Put the source nodes of the incomingEdges in the list of parentNodes
	Iterator it = incomingEdges.iterator();
	while (it.hasNext()) {
            String sourceNode = graphComposition.getEdgeSource((DefaultEdge) it.next());
            parentNodes.add(sourceNode);
	}
		
	return parentNodes;
    }

}
