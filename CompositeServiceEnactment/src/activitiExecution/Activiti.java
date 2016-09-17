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

package activitiExecution;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.DataSpec;
import org.activiti.bpmn.model.IOSpecification;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

public class Activiti {
    
    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private ProcessInstance processInstance;
    
    private String processDefinitionKey;
    private String processDefinitionId;

    //Empty Constructor
    public Activiti(){
        
    }
    
    //Create process engine
    public boolean createProcessEngine(String driver, String databaseURL, String username, String password){
        
        try{
            //Create process engine
            processEngine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
                    .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE)
                    .setJdbcDriver(driver)
                    .setJdbcUrl(databaseURL)
                    .setJdbcUsername(username)
                    .setJdbcPassword(password)
                    .buildProcessEngine();
            
            //If engine is successfully created
            if(processEngine != null)
                return true;
            
            return false;
        
        }catch(Exception ex){          
            return false;
        }   
    }
    
    //Deploy process
    public boolean deployProcess(String processPath){
        
        try{
            
            //Verify if path of BPMN process is a file or URL, and extract resource name
            URL url;
            if(new File(processPath).isFile())
                url = new File(processPath).toURI().toURL();
            else
                url = new URL(processPath);
            String[] parts = processPath.split("/");
        
            //Get repository service
            repositoryService = processEngine.getRepositoryService();
        
            //Add BPMN process to repository and return deploymentId
            String deploymentId = repositoryService.createDeployment().addInputStream(parts[parts.length-1],url.openStream()).deploy().getId();
        
            //Get process definition key and id of the deployed process
            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).list();
            processDefinitionKey = list.get(0).getKey();
            processDefinitionId = list.get(0).getId();
            
            return true;
        
        }catch(Exception ex){
            return false;
        }
        
    }
    
    public boolean runProcess(String processPath, Map<String,String> executionValues){
        
        try{
            
            //Create map to store all inputs to be sent to BPMN process
            Map<String,Object> map = new HashMap<>();
            
            //Get IOSpecification of BPMN process
            BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
            org.activiti.bpmn.model.Process process = model.getProcesses().get(0);
            IOSpecification ioSpecification = process.getIoSpecification();
            if(ioSpecification != null){
                
                //Get all inputs of BPMN process
                List<DataSpec> listOfInputs = ioSpecification.getDataInputs();
                
                //For each input of BPMN process
                for(Iterator it=listOfInputs.iterator();it.hasNext();){
                    
                    //Get input and input name
                    DataSpec input = (DataSpec) it.next();
                    String inputName = input.getName().split("wsdl:")[1];
                    
                    //Find execution value for input and add it to map
                    for(Map.Entry<String,String> entry : executionValues.entrySet()){
                        if(entry.getKey().equals(inputName))
                            map.put(input.getName(),entry.getValue());
                    }
                }        
                
                //Get all outputs of BPMN process
                List<DataSpec> listOfOutputs = ioSpecification.getDataOutputs();
                
                //For each output of BPMN process
                for(Iterator it=listOfOutputs.iterator();it.hasNext();){
                    
                    //Get output and output name
                    DataSpec output = (DataSpec) it.next();
                    String outputName = output.getName().split("wsdl:")[1];
                    
                    //Find execution value for output and add it to map
                    for(Map.Entry<String,String> entry : executionValues.entrySet()){
                        if(entry.getKey().equals(outputName))
                            map.put(output.getName(),entry.getValue());
                    }
                }       
            }
            
            //Get runtime service
            RuntimeService runtimeService=processEngine.getRuntimeService();
            
            //Start process with variables and blocks until process is finished
            processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,map);
            
            return true;           
        
        }catch(Exception ex){
            return false;
        }
    }
    
    //Extract all output variables of process
    public HashMap<String,String> getOutputVariables(){
        
        HashMap<String,String> result = new HashMap<>();
        
        HistoryService historyService = processEngine.getHistoryService();
        
        //Get ioSpecification
        BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
        org.activiti.bpmn.model.Process process = model.getProcesses().get(0);
        IOSpecification ioSpecification = process.getIoSpecification();
        
        //If ioSpecification is not null
        if(ioSpecification != null){
                
            LinkedList<String> outputs = new LinkedList<>();
            
            //Get outputs of ioSpecification
            List<DataSpec> listOfOutputs = ioSpecification.getDataOutputs();
            
            //Add each output name of ioSpecification to LinkedList "outputs"
            for(Iterator it=listOfOutputs.iterator();it.hasNext();)
                outputs.add(((DataSpec) it.next()).getName());
            
            //Get all variables of process generated during execution (data objects and inputs/outputs)
            List<HistoricVariableInstance> outVariables = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).list();
            
            //For each variable of process
            for (HistoricVariableInstance historicVariableInstance : outVariables) {
                
                //Get variable name
                String variableName = historicVariableInstance.getVariableName();
                
                //If variable of process is an output of ioSpecification, add it to LinkedList "result"
                if(outputs.contains(variableName))
                    result.put(variableName.split("wsdl:")[1],(String) historicVariableInstance.getValue());
                    
            }
        }
        
        return result;
    }
    
    //Extract status of process
    public String getStatus(){
        
        HistoryService historyService = processEngine.getHistoryService();
        
        //Get all variables of process generated during execution (data objects and inputs/outputs)
        List<HistoricVariableInstance> outVariables = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).list();
            
        //For each variable of process
        for (HistoricVariableInstance historicVariableInstance : outVariables)
            if(historicVariableInstance.getVariableName().equals("status")){
                if(historicVariableInstance.getValue() != null)
                    return (String) historicVariableInstance.getValue();
            }
        
        return "failure";
    }
            
}
