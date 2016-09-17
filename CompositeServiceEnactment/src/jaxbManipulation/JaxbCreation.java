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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import jaxbClasses.addtocontext.AddToContextRequest;
import jaxbClasses.auxiliarClasses.ComposeServicesStructure;
import jaxbClasses.auxiliarClasses.ContextValueStructure;
import jaxbClasses.auxiliarClasses.InputValidationStructure;
import jaxbClasses.auxiliarClasses.ServiceValidationStructure;
import jaxbClasses.basictypes.*;
import jaxbClasses.composeservices.ComposeServicesRequest;
import jaxbClasses.discoverfunctionsemantics.DiscoverFunctionSemanticsRequest;
import jaxbClasses.discoverinputsemantics.DiscoverInputSemanticsRequest;
import jaxbClasses.discoverservices.DiscoverServicesRequest;
import jaxbClasses.getexecutableservices.GetExecutableServicesRequest;
import jaxbClasses.getlabels.GetLabelsRequest;
import jaxbClasses.resolveservices.ResolveServicesRequest;
import jaxbClasses.validateinputs.ValidateInputsRequest;
import jaxbClasses.includeservices.IncludeServicesRequest;

public class JaxbCreation {
    
    private JAXBContext context;
    private StringWriter writer;
    private Marshaller marsh;
    
    //Constructor
    public JaxbCreation(){
        
    }
    
    //Create requisition for GetLabels
    public String createGetLabelsRequisition(String ontologyUri, String root) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.getlabels");
        
        //Create the GetLabels Request
        jaxbClasses.getlabels.ObjectFactory objectFactory = new jaxbClasses.getlabels.ObjectFactory();
        GetLabelsRequest request = objectFactory.createGetLabelsRequest();    
        
        //Set Ontology URI and Root
        request.setOntologyUri(ontologyUri);
        request.setRootConcept(root);
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }        
    
    //Create requisition for DiscoverInputSemantics
    public String createDiscoverInputSemanticsRequisition(String ontologyUri, String root) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.discoverinputsemantics");
        
        //Create the DiscoverInputSemantics Request
        jaxbClasses.discoverinputsemantics.ObjectFactory objectFactory = new jaxbClasses.discoverinputsemantics.ObjectFactory();
        DiscoverInputSemanticsRequest request = objectFactory.createDiscoverInputSemanticsRequest();       
        
        //Set Ontology URI and Root
        request.setOntologyURI(ontologyUri);
        request.setRootConcept(root);
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }
    
    //Create requisition for DiscoverFunctionSemantics
    public String createDiscoverFunctionSemanticsRequisition(LinkedList<SemanticConcept> listOfInputs) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.discoverfunctionsemantics");
        
        //Create the DiscoverFunctionSemantics Request
        jaxbClasses.discoverfunctionsemantics.ObjectFactory objectFactory = new jaxbClasses.discoverfunctionsemantics.ObjectFactory();
        DiscoverFunctionSemanticsRequest request = objectFactory.createDiscoverFunctionSemanticsRequest();     
        
        //Set Inputs of DiscoverFunctionSemantics Request
        for(Iterator it=listOfInputs.iterator();it.hasNext();){
            
            SemanticConcept sc = (SemanticConcept) it.next();
            
            jaxbClasses.basictypes.ObjectFactory objectFactorySC = new jaxbClasses.basictypes.ObjectFactory();
            jaxbClasses.basictypes.SemanticConcept concept = objectFactorySC.createSemanticConcept();
            
            concept.setName(sc.getName());
            concept.setUrl(sc.getUrl());
            concept.setSemanticSimilarity(sc.getSemanticSimilarity());
            
            request.getInputConcept().add(sc);
        }
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }        
    
    //Create requisition for DiscoverServices
    public String createDiscoverServicesRequisition(LinkedList<String> listOfFunctions, LinkedList<String> listOfInputs) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.discoverservices");
        
        //Create the DiscoverServices Request
        jaxbClasses.discoverservices.ObjectFactory objectFactory = new jaxbClasses.discoverservices.ObjectFactory();
        DiscoverServicesRequest request = objectFactory.createDiscoverServicesRequest();       
        
        //Create ServiceRequisition object
        jaxbClasses.basictypes.ObjectFactory objectFactory2 = new jaxbClasses.basictypes.ObjectFactory();
        InputFunctRequest servRequisition = objectFactory2.createInputFunctRequest();
        
        //Set ServiceRequisition functions
        for(Iterator it=listOfFunctions.iterator();it.hasNext();)
            servRequisition.getFunction().add((String) it.next());
        //Set ServiceRequisition inputs
        for(Iterator it=listOfInputs.iterator();it.hasNext();)
            servRequisition.getInput().add((String) it.next());
        
        //Set ServiceRequisition object of DiscoverServices Request
        request.setInputFunctRequest(servRequisition);
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }
    
    
    //Create requisition for IncludeServices
    public String createIncludeServicesRequisition(HashMap<String,String> includedServices) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.includeservices");
        
        //Create the IncludeServices Request
        jaxbClasses.includeservices.ObjectFactory objectFactory = new jaxbClasses.includeservices.ObjectFactory();
        IncludeServicesRequest request = objectFactory.createIncludeServicesRequest();       
        
        //For each included service
        for (Map.Entry<String, String> entry : includedServices.entrySet()) {
            
            //Create IncludedServiceStructure object
            jaxbClasses.basictypes.ObjectFactory objectFactory2 = new jaxbClasses.basictypes.ObjectFactory();
            IncludedServiceStructure servSelection = objectFactory2.createIncludedServiceStructure();
            
            //Set service ID and function
            servSelection.setIncludedServiceID(entry.getKey());
            servSelection.setIncludedServiceFunction(entry.getValue());
            
            //Add ServiceSelection object for SeleServ Request
            request.getIncludedService().add(servSelection);
        }
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }
    
    //Create requisition for ServValidation
    public String createValidateInputsRequisition(LinkedList<InputValidationStructure> inputValidations) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.validateinputs");
        
        //Create the ValidateInputs Request
        jaxbClasses.validateinputs.ObjectFactory objectFactory = new jaxbClasses.validateinputs.ObjectFactory();
        ValidateInputsRequest request = objectFactory.createValidateInputsRequest(); 
        
        //For each InputValidationStructure
        for(Iterator it=inputValidations.iterator();it.hasNext();){
            
            InputValidationStructure structure = (InputValidationStructure) it.next();
            
            //Create InputValidation object
            jaxbClasses.basictypes.ObjectFactory objectFactory2 = new jaxbClasses.basictypes.ObjectFactory();
            InputValidation inputValidation = objectFactory2.createInputValidation();
        
            inputValidation.setServiceID(structure.getServiceID());
                
            //Create InputToValidate object
            InputToValidate inputForValidation = objectFactory2.createInputToValidate();
            
            inputForValidation.setInputID(structure.getInputID());
            
            if(structure.getInputedValue() != null)
                inputForValidation.setInputedValue(structure.getInputedValue());
            
            if(structure.getInputedValueSemantics() != null)
                inputForValidation.setDataSemantics(structure.getInputedValueSemantics());
            
            if(structure.getInputedValueSyntax() != null)
                inputForValidation.setDataSyntax(structure.getInputedValueSyntax());
            
            //Add InputToValidate object to InputValidation Object
            inputValidation.getInputToValidate().add(inputForValidation);
            
            //Add InputValidation object to serviceValidation Request
            request.getInputValidation().add(inputValidation);
        }
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }
    
    //Create requisition for ComposeServices
    public String createComposeServicesRequisition(LinkedList<ComposeServicesStructure> composeServicesStructure) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.composeservices");
        
        //Create the ComposeServices Request
        jaxbClasses.composeservices.ObjectFactory objectFactory = new jaxbClasses.composeservices.ObjectFactory();
        ComposeServicesRequest request = objectFactory.createComposeServicesRequest(); 
        
        //For each ComposeServicesStructure
        for(Iterator it=composeServicesStructure.iterator();it.hasNext();){
            
            ComposeServicesStructure structure = (ComposeServicesStructure) it.next();
            
            //Create CompositeStructure object
            jaxbClasses.basictypes.ObjectFactory objectFactory2 = new jaxbClasses.basictypes.ObjectFactory();
            CompositeStructure compositeStructure = objectFactory2.createCompositeStructure();
        
            compositeStructure.setServiceID(structure.getServiceID());
                
            //Create CompositePair object
            CompositePair compositePair = objectFactory2.createCompositePair();
            
            compositePair.setInputID(structure.getInputID());
            
            if(structure.getSelectedOutput() != null)
                compositePair.setSelectedOutput(structure.getSelectedOutput());
            
            //Add CompositePair object to CompositeStructure Object
            compositeStructure.getCompositePair().add(compositePair);
            
            //Add CompositeStructure object to ComposeServices Request
            request.getCompositeStructure().add(compositeStructure);
        }
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }
    
    //Create requisition for ResolveServ
    public String createResolveServicesRequisition(LinkedList<ServiceValidationStructure> serviceValidations) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.resolveservices");
        
        //Create the ResolveServ Request
        jaxbClasses.resolveservices.ObjectFactory objectFactory = new jaxbClasses.resolveservices.ObjectFactory();
        ResolveServicesRequest request = objectFactory.createResolveServicesRequest();
        
        //For each serviceValidation
        for(Iterator it=serviceValidations.iterator();it.hasNext();){
            
            ServiceValidationStructure structure = (ServiceValidationStructure) it.next();
            
            //Create ServiceValidation object
            jaxbClasses.basictypes.ObjectFactory objectFactory2 = new jaxbClasses.basictypes.ObjectFactory();
            ServInputToResolve serviceValidation = objectFactory2.createServInputToResolve();
            
            //Set ServiceValidation values
            serviceValidation.setServiceIdToResolve(structure.getServiceID());
            for(Iterator itInput = structure.getInputIDs().iterator();itInput.hasNext();)
                serviceValidation.getInputIdToResolve().add((String)itInput.next());
            
            //Add ServiceValidation object to ResolveServ Request
            request.getServInputToResolve().add(serviceValidation);
                 
        }
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }
    
    //Create requisition for AddToContext (only service Ids)
    public String createAddToContextServiceIdsRequisition(LinkedList<String> serviceIds) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.addtocontext");
        
        //Create the AddToContext Request
        jaxbClasses.addtocontext.ObjectFactory objectFactory = new jaxbClasses.addtocontext.ObjectFactory();
        AddToContextRequest request = objectFactory.createAddToContextRequest();
        
        //Add service Ids of services that were executed but have no outputs
        for(Iterator it=serviceIds.iterator();it.hasNext();)
            request.getServiceIdToAdd().add((String) it.next());
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }
    
    //Create requisition for AddToContext (service output values)
    public String createAddToContextServiceOutputValuesRequisition(LinkedList<ContextValueStructure> contextValues) throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.addtocontext");
        
        //Create the AddToContext Request
        jaxbClasses.addtocontext.ObjectFactory objectFactory = new jaxbClasses.addtocontext.ObjectFactory();
        AddToContextRequest request = objectFactory.createAddToContextRequest();
        
        //For each contextValue
        for(Iterator it=contextValues.iterator();it.hasNext();){
            
            ContextValueStructure structure = (ContextValueStructure) it.next();
            
            //Create ContextValue object
            jaxbClasses.basictypes.ObjectFactory objectFactory2 = new jaxbClasses.basictypes.ObjectFactory();
            ContextValue contextValue = objectFactory2.createContextValue();
            
            //Set values of ContextValue object
            contextValue.setValueID(structure.getOutputID());
            contextValue.setServiceOutputValue(structure.getOutputValue());
            contextValue.setServiceID(structure.getServiceID());
            
            //Add ContextValue object to AddToContext Request
            request.getValueToAdd().add(contextValue);
            
        }
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }
    
    //Create requisition for ExecServ
    public String createGetExecutableServicesRequisition() throws JAXBException{
        
        //Set JAXBContext instance
        context = JAXBContext.newInstance("jaxbClasses.getexecutableservices");
        
        //Create the ExecServ Request
        jaxbClasses.getexecutableservices.ObjectFactory objectFactory = new jaxbClasses.getexecutableservices.ObjectFactory();
        GetExecutableServicesRequest request = objectFactory.createGetExecutableServicesRequest();
        
        request.setExecServParam("NEXT-SERVICES");
        
        //Marshal the information into XML
        writer = new StringWriter();
        marsh = context.createMarshaller();
        marsh.marshal(request, writer);
    
        //Return XML request message
        return writer.toString();
    }
    
}
