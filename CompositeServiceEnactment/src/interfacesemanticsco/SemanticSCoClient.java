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

package interfacesemanticsco;

public class SemanticSCoClient {
    
    public SemanticSCoClient(){
        
    }

    public String startSession() {
        semanticsco.service.SemanticSCoService_Service service = new semanticsco.service.SemanticSCoService_Service();
        semanticsco.service.SemanticSCoService port = service.getSemanticSCoServicePort();
        return port.startSession();
    }

    public String coordinate(java.lang.String sessionID, java.lang.String interactionType, java.lang.String interactionMessage) {
        semanticsco.service.SemanticSCoService_Service service = new semanticsco.service.SemanticSCoService_Service();
        semanticsco.service.SemanticSCoService port = service.getSemanticSCoServicePort();
        return port.coordinate(sessionID, interactionType, interactionMessage);
    }
    
    
}