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

package jaxbManipulation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JTree;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jaxbClasses.addtocontext.AddToContextResponse;
import jaxbClasses.auxiliarClasses.TreeExtraction;
import jaxbClasses.basictypes.DiscoverServicesMatching;
import jaxbClasses.basictypes.InvalidInputs;
import jaxbClasses.basictypes.Node;
import jaxbClasses.basictypes.ResolvingServiceMatching;
import jaxbClasses.basictypes.SemanticConcept;
import jaxbClasses.basictypes.ServiceInfo;
import jaxbClasses.basictypes.Tree;
import jaxbClasses.composeservices.ComposeServicesResponse;
import jaxbClasses.discoverfunctionsemantics.DiscoverFunctionSemanticsResponse;
import jaxbClasses.discoverinputsemantics.DiscoverInputSemanticsResponse;
import jaxbClasses.discoverservices.DiscoverServicesResponse;
import jaxbClasses.getexecutableservices.GetExecutableServicesResponse;
import jaxbClasses.getlabels.GetLabelsResponse;
import jaxbClasses.resolveservices.ResolveServicesResponse;
import jaxbClasses.includeservices.IncludeServicesResponse;
import jaxbClasses.validateinputs.ValidateInputsResponse;


public class JaxbExtraction {
    
    private JAXBContext context;    
    private Unmarshaller unmarsh;
            
    //Constructor
    public JaxbExtraction(){
        
        
    }
    
    //Extract response of GetLabels
    public List<SemanticConcept> extractGetLabelsResponse(String xmlMessage) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.getlabels");
                
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	GetLabelsResponse response = (GetLabelsResponse) unmarsh.unmarshal(inputStream);
        
        return response.getSemanticConcept();        
    }

    //Extract response of DiscoverInputSemantics
    public JTree extractDiscoverInputSemanticsResponse(String xmlMessage) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.discoverinputsemantics");
                
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	DiscoverInputSemanticsResponse response = (DiscoverInputSemanticsResponse) unmarsh.unmarshal(inputStream);
        
        //Get returned tree
        Tree tree = response.getTreeOfInputConcepts();
        
        Node root = tree.getRoot();
        
        TreeExtraction ext = new TreeExtraction(root);
            
        JTree jTree = ext.getJTree();
        
        return jTree;        
    }
    
    //Extract response of DiscoverFunctionSemantics
    public List<SemanticConcept> extractDiscoverFunctionSemanticsResponse(String xmlMessage) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.discoverfunctionsemantics");
                
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	DiscoverFunctionSemanticsResponse response = (DiscoverFunctionSemanticsResponse) unmarsh.unmarshal(inputStream);
        
        return response.getFunctionConcept();
    }
    
    //Extract response of DiscoverServices
    public List<ServiceInfo> extractDiscoverServicesResponse(String xmlMessage) throws JAXBException{
        
        List<ServiceInfo> serviceInfos = new LinkedList<>();
        List<ServiceInfo> filteredServiceInfos = new LinkedList<>();
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.discoverservices");
        
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	DiscoverServicesResponse response = (DiscoverServicesResponse) unmarsh.unmarshal(inputStream);
        
        //Extract DiscServMatching objects
        List<DiscoverServicesMatching> discoveredServices = response.getDiscoverServicesMatching();
        
        //If some service was discovered
        if(!discoveredServices.isEmpty()){
         
            //Get discovered services
            DiscoverServicesMatching dsm = discoveredServices.get(0);
            
            //Extract ServiceInfo objects
            serviceInfos = dsm.getServiceInfo();
            
            for(Iterator it=serviceInfos.iterator();it.hasNext();){
                ServiceInfo si = (ServiceInfo) it.next();
                if(!si.getName().contains("Software Adapter"))
                    filteredServiceInfos.add(si);
            }
        }
        
        return filteredServiceInfos;
        
    }
    
    //Extract response of IncludeServices
    public List<ServiceInfo> extractIncludeServicesResponse(String xmlMessage) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.includeservices");
        
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	IncludeServicesResponse response = (IncludeServicesResponse) unmarsh.unmarshal(inputStream);
        
        return response.getServiceInfo();
        
    }
    
    //Extract response of ServiceValidation
    public List<InvalidInputs> extractValidateInputsResponse(String xmlMessage) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.validateinputs");
        
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	ValidateInputsResponse response = (ValidateInputsResponse) unmarsh.unmarshal(inputStream);
        
       
        return response.getInvalidInputs();
        
    }
    
    //Extract response of ComposeServices
    public List<InvalidInputs> extractComposeServicesResponse(String xmlMessage) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.composeservices");
        
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	ComposeServicesResponse response = (ComposeServicesResponse) unmarsh.unmarshal(inputStream);
       
        return response.getInvalidInputs();
        
    }
    
    //Extract response of ExecServ
    public List<ServiceInfo> extractGetExecutableServicesResponse(String xmlMessage) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.getexecutableservices");
        
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	GetExecutableServicesResponse response = (GetExecutableServicesResponse) unmarsh.unmarshal(inputStream);
        
        return response.getServiceInfo();
    }
    
    //Extract response of AddToContext
    public String extractAddToContextResponse(String xmlMessage) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.addtocontext");
        
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	AddToContextResponse response = (AddToContextResponse) unmarsh.unmarshal(inputStream);
        
        return response.getAddToContextResult();
    }
    
    //Extract response of ResolveServ
    public List<ResolvingServiceMatching> extractResolveServicesResponse(String xmlMessage) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.resolveservices");
        
        //Create an Unmarsheller instance
	unmarsh = context.createUnmarshaller();
        
        //Create input stream to use as input for the unmarshaller
	InputStream inputStream = new ByteArrayInputStream(xmlMessage.getBytes());
        
        //Unmarshal input stream
	ResolveServicesResponse response = (ResolveServicesResponse) unmarsh.unmarshal(inputStream);
        
        return response.getResolvingServiceMatching();
    }
    
    
}