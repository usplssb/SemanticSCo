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

package semanticsco.service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import semanticsco.compositionandexecutioncontext.CompositionAndExecutionContext;
import semanticsco.coordinator.Coordinator;

@WebService(serviceName = "SemanticSCoService")
@Stateless()
public class SemanticSCoService {

    //Create list to manage user sessions, which is initialized only when the service is deployed in the server
    private LinkedList<Session> sessions = new LinkedList<>();
    
    //Start new session (id + Coordinator + ExecContext)
    @WebMethod(operationName = "startSession")
    public String startSession() {
        
        String identifier;        
        boolean alreadyExists;
        
        //Generate unique identifier for Session
        do{
            alreadyExists = false; 
            identifier = UUID.randomUUID().toString();
            
            for(Iterator it=sessions.iterator();it.hasNext();){
                Session session = (Session) it.next();
                if(session.getIdentifier().equals(identifier))
                    alreadyExists = true;
            }
        }while(alreadyExists);
        
        //Create new Coordinator object
        Coordinator coord = new Coordinator();
            
        //Create new CompositionAndExecutionContext object
        CompositionAndExecutionContext execContext = new CompositionAndExecutionContext();
        
        //Create new session
        Session session = new Session(identifier,coord,execContext);
        
        //Add session to list of sessions
        sessions.add(session);
        
        return session.getIdentifier();
        
    }

    @WebMethod(operationName = "coordinate")
    public String coordinate(@WebParam(name = "sessionID") String sessionID, @WebParam(name = "interactionType") String interactionType, @WebParam(name = "interactionMessage") String interactionMessage) {
        
        for(Iterator it=sessions.iterator();it.hasNext();){
            
            Session session = (Session) it.next();
            
            if(session.getIdentifier().equals(sessionID)){
                
                Coordinator coord = session.getCoordinator();
                CompositionAndExecutionContext execContext = session.getExecContext();
                
                try {
                    
                    String result = coord.Coordinate(interactionType, interactionMessage, execContext);
                    
                    //Update references in session list
                    session.setCoordinator(coord);
                    session.setExecContext(execContext);
                    
                    return result;
                    
                } catch (JAXBException ex) {
                    return null;
                }
                
            }
        }
        
        return null;
    }
}
