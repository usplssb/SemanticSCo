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

import semanticsco.compositionandexecutioncontext.CompositionAndExecutionContext;
import semanticsco.coordinator.Coordinator;

public class Session {

    private String identifier;
    private Coordinator coordinator;
    private CompositionAndExecutionContext execContext;
    
    //Constructor
    public Session(String identifier,Coordinator coordinator,CompositionAndExecutionContext execContext){ 
    
        this.identifier = identifier;
        this.coordinator = coordinator;
        this.execContext = execContext; 
    }
    

    public String getIdentifier() {
        return this.identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public CompositionAndExecutionContext getExecContext() {
        return execContext;
    }

    public void setExecContext(CompositionAndExecutionContext execContext) {
        this.execContext = execContext;
    }
    
    
}