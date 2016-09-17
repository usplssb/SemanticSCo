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

package classes;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import org.apache.woden.*;
import org.apache.woden.schema.Schema;
import org.apache.woden.types.NCName;
import org.apache.woden.types.NamespaceDeclaration;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.extensions.ExtensionElement;
import org.apache.woden.wsdl20.xml.*;
import org.apache.woden.xml.XMLAttr;

public class EditorApplication {
    public static void main(String[] args) throws WSDLException, URISyntaxException {
        
        //Get command line arguments
        String inputFilename = args[0];
        String outputFilename = args[1];
        
        //Declare variables
	NamespaceDeclaration[] oldNamespaces;
	XMLAttr[] oldAttributes;
	DocumentationElement[] oldDocumentations;
	ImportElement[] oldImports;
	IncludeElement[] oldIncludes;
	ExtensionElement[] oldExtensions;
        URI[] uris;
        URI uri;
		
	// Obtain WSDLFactory objects
	WSDLFactory factoryReader = WSDLFactory.newInstance();
	WSDLFactory factoryNew = WSDLFactory.newInstance();

	//Obtain a WSDLReader object (the WSDL parser)
	WSDLReader reader = factoryReader.newWSDLReader();

	//Read a WSDL document and return it as a "DescriptionComponent"
	Description oldDescriptionComponent = reader.readWSDL(inputFilename);

	//Returns the "DescriptionComponent" as a "DescriptionElement"
	DescriptionElement oldDescriptionElement = oldDescriptionComponent.toElement();
		
	//Creates a new "DescriptionElement"		
	DescriptionElement newDescriptionElement = factoryNew.newDescription();		
		
	//Description Element --------------------------------------------------------------------------------------
		
	//Set DocumentBaseURI of "DescriptionElement" (required)
	newDescriptionElement.setDocumentBaseURI(oldDescriptionElement.getDocumentBaseURI());
	
	//Set DeclaredNamespaces of "DescriptionElement" (required)
	oldNamespaces = oldDescriptionElement.getDeclaredNamespaces();
	for (int i = 0; i<oldNamespaces.length; i++){
            //If namespace is nor wsoap neither soap
            if(!oldNamespaces[i].getPrefix().equals("wsoap") && !oldNamespaces[i].getPrefix().equals("soap"))
		newDescriptionElement.addNamespace(oldNamespaces[i].getPrefix(), oldNamespaces[i].getNamespaceURI());
	}
		
	//Set TargetNamespace of "DescriptionElement" (required)
	newDescriptionElement.setTargetNamespace(oldDescriptionElement.getTargetNamespace());
		
	//Set ExtensionAttributes of "DescriptionElement" (optional)
	oldAttributes = oldDescriptionElement.getExtensionAttributes();
	for(int i=0;i<oldAttributes.length;i++)
            newDescriptionElement.setExtensionAttribute(oldAttributes[i].getAttributeType(), oldAttributes[i]);
		
	//Set ExtensionElements of "DescriptionElement" (optional)
	oldExtensions = oldDescriptionElement.getExtensionElements();
	for(int i=0;i<oldExtensions.length;i++)
            newDescriptionElement.addExtensionElement(oldExtensions[i]);
		
	//Set DocumentationElements of "DescriptionElement" (optional)
	oldDocumentations = oldDescriptionElement.getDocumentationElements();
	for(int i=0;i<oldDocumentations.length;i++){
            
            //Create new "DocumentationElement"
            DocumentationElement newDocumentation = newDescriptionElement.addDocumentationElement();
			
            //Set Content of "DocumentationElement" (required)
            newDocumentation.setContent(oldDocumentations[i].getContent());
			
            //Set ExtensionElements of "DocumentationElement" (optional)
            oldExtensions = oldDocumentations[i].getExtensionElements();
            for(int j=0;j<oldExtensions.length;j++)
                newDocumentation.addExtensionElement(oldExtensions[j]);
	}
		
	//Set ImportElements of "DescriptionElement" (optional)
	oldImports = oldDescriptionElement.getImportElements();
	for(int i=0;i<oldImports.length;i++){
			
            //Create a new "ImportElement"
            ImportElement newImportElement = newDescriptionElement.addImportElement();
			
            //Set Namespace of "ImportElement" (required)
            newDescriptionElement.addImportElement().setNamespace(oldImports[i].getNamespace());
			
            //Set DeclaredNamespaces of "ImportElement" (optional)
            oldNamespaces = oldImports[i].getDeclaredNamespaces();
            for(int j=0;j<oldNamespaces.length;j++)
                newImportElement.addNamespace(oldNamespaces[j].getPrefix(), oldNamespaces[j].getNamespaceURI());
			
            //Set ExtensionAttributes of "ImportElement" (optional)
            oldAttributes = oldImports[i].getExtensionAttributes();
            for(int j=0;j<oldAttributes.length;j++)
                newImportElement.setExtensionAttribute(oldAttributes[j].getAttributeType(), oldAttributes[j]);
			
            //Set ExtensionElements of "ImportElement" (optional)
            oldExtensions = oldImports[i].getExtensionElements();
            for(int j=0;j<oldExtensions.length;j++)
                newImportElement.addExtensionElement(oldExtensions[j]);
			
            //Set Location of "ImportElement" (optional)
            newImportElement.setLocation(oldImports[i].getLocation());
			
            //Set DocumentationElements of "ImportElement" (optional)
            oldDocumentations = oldImports[i].getDocumentationElements();
            for(int j=0;j<oldDocumentations.length;j++){
				
                //Create new "DocumentationElement"
		DocumentationElement newDocumentation = newImportElement.addDocumentationElement();
				
		//Set Content of "DocumentationElement" (required)
		newDocumentation.setContent(oldDocumentations[j].getContent());
				
		//Set ExtensionElements of "DocumentationElement" (optional)
		oldExtensions = oldDocumentations[j].getExtensionElements();
		for(int k=0;k<oldExtensions.length;k++)
                    newDocumentation.addExtensionElement(oldExtensions[k]);
            }
        }
        
        //Set IncludeElements of "DescriptionElement" (optional)
	oldIncludes = oldDescriptionElement.getIncludeElements();
	for(int i=0;i<oldIncludes.length;i++){
			
            //Create a new "IncludeElement"
            IncludeElement newIncludeElement = newDescriptionElement.addIncludeElement();
			
            //Set Location of "IncludeElement" (required)
            newIncludeElement.setLocation(oldIncludes[i].getLocation());
			
            //Set DeclaredNamespaces of "IncludeElement" (optional)
            oldNamespaces = oldIncludes[i].getDeclaredNamespaces();
            for(int j=0;j<oldNamespaces.length;j++)
                newIncludeElement.addNamespace(oldNamespaces[j].getPrefix(), oldNamespaces[j].getNamespaceURI());
			
            //Set ExtensionAttributes of "IncludeElement" (optional)
            oldAttributes = oldIncludes[i].getExtensionAttributes();
            for(int j=0;j<oldAttributes.length;j++)
                newIncludeElement.setExtensionAttribute(oldAttributes[j].getAttributeType(), oldAttributes[j]);
				
            //Set ExtensionElements of "IncludeElement" (optional)
            oldExtensions = oldIncludes[i].getExtensionElements();
            for(int j=0;j<oldExtensions.length;j++)
                newIncludeElement.addExtensionElement(oldExtensions[j]);
			
            //Set DocumentationElements of "IncludeElement" (optional)
            oldDocumentations = oldIncludes[i].getDocumentationElements();
            for(int j=0;j<oldDocumentations.length;j++){
                
                //Create new "DocumentationElement"
		DocumentationElement newDocumentation = newIncludeElement.addDocumentationElement();
				
		//Set Content of "DocumentationElement" (required)
		newDocumentation.setContent(oldDocumentations[j].getContent());
				
		//Set ExtensionElements of "DocumentationElement" (optional)
		oldExtensions = oldDocumentations[j].getExtensionElements();
		for(int k=0;k<oldExtensions.length;k++)
                    newDocumentation.addExtensionElement(oldExtensions[k]);
            }
        }
        
        //Description Element --------------------------------------------------------------------------------------
		
	//Types Element --------------------------------------------------------------------------------------------
		
	//Obtain "TypesElement"	
	TypesElement oldType= oldDescriptionElement.getTypesElement();
		
	//Add "TypesElement" and set TypeSystem (required)
	newDescriptionElement.addTypesElement().setTypeSystem(oldType.getTypeSystem());
		
	//Set DeclaredNamespaces of "TypesElement" (optional)
	oldNamespaces = oldType.getDeclaredNamespaces();
	for(int i=0;i<oldNamespaces.length;i++)
            newDescriptionElement.getTypesElement().addNamespace(oldNamespaces[i].getPrefix(), oldNamespaces[i].getNamespaceURI());
		
	//Set ExtensionAttributes of "TypesElement" (optional)
	oldAttributes = oldType.getExtensionAttributes();
	for(int i=0;i<oldAttributes.length;i++)
            newDescriptionElement.getTypesElement().setExtensionAttribute(oldAttributes[i].getAttributeType(), oldAttributes[i]);
		
	//Set ExtensionElements of "TypesElement" (optional)
	oldExtensions = oldType.getExtensionElements();
	for(int i=0;i<oldExtensions.length;i++)
            newDescriptionElement.getTypesElement().addExtensionElement(oldExtensions[i]);
				
	//Set DocumentationElements of "TypesElement" (optional)
	oldDocumentations = oldType.getDocumentationElements();
	for(int i=0;i<oldDocumentations.length;i++){
		
            //Create new "DocumentationElement"
            DocumentationElement newDocumentation = newDescriptionElement.getTypesElement().addDocumentationElement();
			
            //Set Content of "DocumentationElement" (required)
            newDocumentation.setContent(oldDocumentations[i].getContent());
            
            //Set ExtensionElements of "DocumentationElement" (optional)
            oldExtensions = oldDocumentations[i].getExtensionElements();
            for(int j=0;j<oldExtensions.length;j++)
                newDocumentation.addExtensionElement(oldExtensions[j]);
	}
        
        //Set Schemas of "TypesElement" (optional)
	Schema[] schemas = oldType.getSchemas();
	for(int i=0;i<schemas.length;i++)
            newDescriptionElement.getTypesElement().addSchema(schemas[i]);	
		
	//Types Element --------------------------------------------------------------------------------------------
		
	//Interface Element ----------------------------------------------------------------------------------------
		
	//Obtain "InterfaceElements"
	InterfaceElement[] oldInterfaces = oldDescriptionElement.getInterfaceElements();
	for(int i=0;i<oldInterfaces.length;i++){
            
            //Create a new "InterfaceElement"
            InterfaceElement newInterface = newDescriptionElement.addInterfaceElement();
			
            //Set Name of "InterfaceElement" (required)
            newInterface.setName(new NCName(oldInterfaces[i].getName().getLocalPart()));
			
            //Set DeclaredNamespaces of "InterfaceElement" (optional)
            oldNamespaces = oldInterfaces[i].getDeclaredNamespaces();
            for (int j = 0; j<oldNamespaces.length; j++)
                newInterface.addNamespace(oldNamespaces[j].getPrefix(), oldNamespaces[j].getNamespaceURI());
			
            //Set ExtensionAttributes of "InterfaceElement" (optional)
            oldAttributes = oldInterfaces[i].getExtensionAttributes();
            for(int j=0;j<oldAttributes.length;j++)
                newInterface.setExtensionAttribute(oldAttributes[j].getAttributeType(), oldAttributes[j]);
			
            //Set ExtensionElements of "InterfaceElement" (optional)
            oldExtensions = oldInterfaces[i].getExtensionElements();
            for(int j=0;j<oldExtensions.length;j++)
                newInterface.addExtensionElement(oldExtensions[j]);
			
            //Set ExtendedInterfaceNames of "InterfaceElement" (optional)
            QName[] qnames = oldInterfaces[i].getExtendedInterfaceNames();
            for(int j=0;j<qnames.length;j++)
                newInterface.addExtendedInterfaceName(qnames[j]);
			
            //Set StyleDefaults of "InterfaceElement" (optional)
            uris = oldInterfaces[i].getStyleDefault();
            if(uris.length > 0){
                String concatenatedURI = "";
                //Construct concatenated URI
                for(int j=0;j<uris.length-1;j++)
                    concatenatedURI = concatenatedURI + uris[j] + "%20";
                concatenatedURI = concatenatedURI + uris[uris.length-1];
                
                //Set style
                uri = new URI(concatenatedURI);
                newInterface.addStyleDefaultURI(uri);
            }
            
            //Set DocumentationElements of "InterfaceElement" (optional)
            oldDocumentations = oldInterfaces[i].getDocumentationElements();
            for(int j=0;j<oldDocumentations.length;j++){
                
                //Create new "DocumentationElement"
		DocumentationElement newDocumentation = newInterface.addDocumentationElement();
				
		//Set Content of "DocumentationElement" (required)
		newDocumentation.setContent(oldDocumentations[j].getContent());
				
		//Set ExtensionElements of "DocumentationElement" (optional)
		oldExtensions = oldDocumentations[j].getExtensionElements();
		for(int k=0;k<oldExtensions.length;k++)
                    newDocumentation.addExtensionElement(oldExtensions[k]);
            }
            
            //Set InterfaceFaultElements of "InterfaceElement" (optional)
            InterfaceFaultElement[] oldFaults = oldInterfaces[i].getInterfaceFaultElements();
            for (int j = 0; j<oldFaults.length; j++) {
                
                //Create a new "InterfaceFaultElement"
		InterfaceFaultElement newFault = newInterface.addInterfaceFaultElement();
				
		//Set Name of "InterfaceFaultElement"
		newFault.setName(new NCName(oldFaults[j].getName().getLocalPart().toString()));
				
		//Set Element of "InterfaceFaultElement"
		newFault.setElement(oldFaults[j].getElement());
				
		//Set DeclaredNamespaces of "InterfaceFaultElement" (optional)
		oldNamespaces = oldFaults[j].getDeclaredNamespaces();
		for (int k = 0; k<oldNamespaces.length; k++)
                    newFault.addNamespace(oldNamespaces[k].getPrefix(), oldNamespaces[k].getNamespaceURI());
				
		//Set ExtensionAttributes of "InterfaceFaultElement" (optional)
		oldAttributes = oldFaults[j].getExtensionAttributes();
		for(int k=0;k<oldAttributes.length;k++)
                    newFault.setExtensionAttribute(oldAttributes[k].getAttributeType(), oldAttributes[k]);
			
		//Set ExtensionElements of "InterfaceFaultElement" (optional)
		oldExtensions = oldFaults[j].getExtensionElements();
		for(int k=0;k<oldExtensions.length;k++)
                    newFault.addExtensionElement(oldExtensions[k]);
				
		//Set DocumentationElements of "InterfaceFaultElement" (optional)
		oldDocumentations = oldFaults[j].getDocumentationElements();
		for(int k=0;k<oldDocumentations.length;k++){
                    
                    //Create new "DocumentationElement"
                    DocumentationElement newDocumentation = newFault.addDocumentationElement();
					
                    //Set Content of "DocumentationElement" (required)
                    newDocumentation.setContent(oldDocumentations[k].getContent());
					
                    //Set ExtensionElements of "DocumentationElement" (optional)
                    oldExtensions = oldDocumentations[k].getExtensionElements();
                    for(int w=0;w<oldExtensions.length;w++)
                        newDocumentation.addExtensionElement(oldExtensions[w]);
		}
            }
            
            //Set InterfaceOperationElements of "InterfaceElement" (optional)
            InterfaceOperationElement[] oldOperations = oldInterfaces[i].getInterfaceOperationElements();
            for (int j = 0; j<oldOperations.length; j++) {
				
                //Create a new "InterfaceOperationElement"
                InterfaceOperationElement newOperation = newInterface.addInterfaceOperationElement();
				
                //Set Name of "InterfaceOperationElement" (required)
                newOperation.setName(new NCName(oldOperations[j].getName().getLocalPart()));
				
                //Set Pattern of "InterfaceOperationElement" (optional)
                newOperation.setPattern(oldOperations[j].getPattern());
				
                //Set StyleURI of "InterfaceOperationElement" (optional)
                uris = oldOperations[j].getStyle();
                if(uris.length > 0){
                
                    String concatenatedURI = "";
                
                    //Construct concatenated URI
                    for(int k=0;k<uris.length-1;k++)
                        concatenatedURI = concatenatedURI + uris[k] + "%20";
                    concatenatedURI = concatenatedURI + uris[uris.length-1];
                                        
                    //Set style
                    uri = new URI(concatenatedURI);
                    newOperation.addStyleURI(uri);
                }
                
                //Set DeclaredNamespaces of "InterfaceOperationElement" (optional)
                oldNamespaces = oldOperations[j].getDeclaredNamespaces();
                for (int k = 0; k<oldNamespaces.length; k++)
                    newOperation.addNamespace(oldNamespaces[k].getPrefix(), oldNamespaces[k].getNamespaceURI());
				
                //Set ExtensionAttributes of "InterfaceOperationElement" (optional)
                oldAttributes = oldOperations[j].getExtensionAttributes();
                for(int k=0;k<oldAttributes.length;k++)
                    newOperation.setExtensionAttribute(oldAttributes[k].getAttributeType(), oldAttributes[k]);
				
                //Set ExtensionElements of "InterfaceOperationElement" (optional)
                oldExtensions = oldOperations[j].getExtensionElements();
                for(int k=0;k<oldExtensions.length;k++)
                    newOperation.addExtensionElement(oldExtensions[k]);
				
                //Set DocumentationElements of "InterfaceOperationElement" (optional)
                oldDocumentations = oldOperations[j].getDocumentationElements();
                for(int k=0;k<oldDocumentations.length;k++){
                
                    //Create new "DocumentationElement"
                    DocumentationElement newDocumentation = newOperation.addDocumentationElement();
					
                    //Set Content of "DocumentationElement" (required)
                    newDocumentation.setContent(oldDocumentations[k].getContent());
					
                    //Set ExtensionElements of "DocumentationElement" (optional)
                    oldExtensions = oldDocumentations[k].getExtensionElements();
                    for(int w=0;w<oldExtensions.length;w++)
                        newDocumentation.addExtensionElement(oldExtensions[w]);
                }
            
                //Set InterfaceMessageReferences of "InterfaceOperationElement"
                InterfaceMessageReferenceElement[] oldMessageReferences = oldOperations[j].getInterfaceMessageReferenceElements();
                for(int k=0;k<oldMessageReferences.length;k++){
                
                    //Create new "InterfaceMessageReferenceElement"
                    InterfaceMessageReferenceElement newMessageReference = newOperation.addInterfaceMessageReferenceElement();
					
                    //Set MessageLabel of "InterfaceMessageReferenceElement" (optional)
                    newMessageReference.setMessageLabel(oldMessageReferences[k].getMessageLabel());
					
                    //Set Direction of "InterfaceMessageReferenceElement" (required)
                    newMessageReference.setDirection(oldMessageReferences[k].getDirection());
					
                    //Set Element of "InterfaceMessageReferenceElement" (optional)
                    newMessageReference.setElement(oldMessageReferences[k].getElement());
					
                    //Set DeclaredNamespaces of "InterfaceMessageReferenceElement" (optional)
                    oldNamespaces = oldMessageReferences[k].getDeclaredNamespaces();
                    for (int w = 0; w<oldNamespaces.length; w++)
                        newMessageReference.addNamespace(oldNamespaces[w].getPrefix(), oldNamespaces[w].getNamespaceURI());
					
                    //Set ExtensionAttributes of "InterfaceMessageReferenceElement" (optional)
                    oldAttributes = oldMessageReferences[k].getExtensionAttributes();
                    for(int w=0;w<oldAttributes.length;w++)
                        newMessageReference.setExtensionAttribute(oldAttributes[w].getAttributeType(), oldAttributes[w]);
					
                    //Set ExtensionElements of "InterfaceMessageReferenceElement" (optional)
                    oldExtensions = oldMessageReferences[k].getExtensionElements();
                    for(int w=0;w<oldExtensions.length;w++)
                        newMessageReference.addExtensionElement(oldExtensions[w]);
					
                    //Set DocumentationElements of "InterfaceMessageReferenceElement" (optional)
                    oldDocumentations = oldMessageReferences[k].getDocumentationElements();
                    for(int w=0;w<oldDocumentations.length;w++){
                    
                        //Create new "DocumentationElement"
                        DocumentationElement newDocumentation = newMessageReference.addDocumentationElement();
						
                        //Set Content of "DocumentationElement" (required)
                        newDocumentation.setContent(oldDocumentations[w].getContent());
						
                        //Set ExtensionElements of "DocumentationElement" (optional)
                        oldExtensions = oldDocumentations[w].getExtensionElements();
                        for(int z=0;z<oldExtensions.length;z++)
                            newDocumentation.addExtensionElement(oldExtensions[z]);
                    }
                }
                
                //Set InterfaceFaultReferences of "InterfaceOperationElement"
                InterfaceFaultReferenceElement[] oldFaultReferences = oldOperations[j].getInterfaceFaultReferenceElements();
                for(int k=0;k<oldFaultReferences.length;k++){
					
                    //Create new "InterfaceFaultReferenceElement"
                    InterfaceFaultReferenceElement newFaultReference = newOperation.addInterfaceFaultReferenceElement();
					
                    //Set Ref of "InterfaceFaultReferenceElement" (required)
                    newFaultReference.setRef(oldFaultReferences[k].getRef());
					
                    //Set MessageLabel of "InterfaceFaultReferenceElement" (optional)
                    newFaultReference.setMessageLabel(oldFaultReferences[k].getMessageLabel());
					
                    //Set Direction of "InterfaceFaultReferenceElement" (required)
                    newFaultReference.setDirection(oldFaultReferences[k].getDirection());
					
                    //Set DeclaredNamespaces of "InterfaceFaultReferenceElement" (optional)
                    oldNamespaces = oldFaultReferences[k].getDeclaredNamespaces();
                    for (int w = 0; w<oldNamespaces.length; w++)
                        newFaultReference.addNamespace(oldNamespaces[w].getPrefix(), oldNamespaces[w].getNamespaceURI());
					
                    //Set ExtensionAttributes of "InterfaceFaultReferenceElement" (optional)
                    oldAttributes = oldFaultReferences[k].getExtensionAttributes();
                    for(int w=0;w<oldAttributes.length;w++)
                        newFaultReference.setExtensionAttribute(oldAttributes[w].getAttributeType(), oldAttributes[w]);
					
                    //Set ExtensionElements of "InterfaceFaultReferenceElement" (optional)
                    oldExtensions = oldFaultReferences[k].getExtensionElements();
                    for(int w=0;w<oldExtensions.length;w++)
                        newFaultReference.addExtensionElement(oldExtensions[w]);
					
                    //Set DocumentationElements of "InterfaceFaultReferenceElement" (optional)
                    oldDocumentations = oldFaultReferences[k].getDocumentationElements();
                    for(int w=0;w<oldDocumentations.length;w++){
                    
                        //Create new "DocumentationElement"
                        DocumentationElement newDocumentation = newFaultReference.addDocumentationElement();
						
                        //Set Content of "DocumentationElement" (required)
                        newDocumentation.setContent(oldDocumentations[w].getContent());
						
                        //Set ExtensionElements of "DocumentationElement" (optional)
                        oldExtensions = oldDocumentations[w].getExtensionElements();
                        for(int z=0;z<oldExtensions.length;z++)
                            newDocumentation.addExtensionElement(oldExtensions[z]);
                    }
                }
            }
        }
        
        //Interface Element ----------------------------------------------------------------------------------------
		
	//Binding Element ------------------------------------------------------------------------------------------
		
	//Obtain "BindingElements"
	BindingElement[] oldBindings = oldDescriptionElement.getBindingElements();
	for (int i = 0; i < oldBindings.length; i++) {
            
            //If is the HTTP binding
            if(oldBindings[i].getType().toString().equals("http://www.w3.org/ns/wsdl/http")){
                
                //Create new "BindingElement"
		BindingElement newBinding = newDescriptionElement.addBindingElement();
				
		//Set Name of "BindingElement" (required)
		newBinding.setName(new NCName(oldBindings[i].getName().getLocalPart().toString()));
			
		//Set InterfaceName of "BindingElement" (optional)
		newBinding.setInterfaceName(oldBindings[i].getInterfaceName());
				
		//Set Type of "BindingElement" (required)
		newBinding.setType(oldBindings[i].getType());
		
		//Set DeclaredNamespaces of "BindingElement" (optional)
		oldNamespaces = oldBindings[i].getDeclaredNamespaces();
		for (int j = 0; j<oldNamespaces.length; j++)
                    newBinding.addNamespace(oldNamespaces[j].getPrefix(), oldNamespaces[j].getNamespaceURI());
				
		//Set ExtensionAttributes of "BindingElement" (optional)
		oldAttributes = oldBindings[i].getExtensionAttributes();
		for(int j=0;j<oldAttributes.length;j++)
                    newBinding.setExtensionAttribute(oldAttributes[j].getAttributeType(),oldAttributes[j]);
				
		//Set ExtensionElements of "BindingElement" (optional)
		oldExtensions = oldBindings[i].getExtensionElements();
		for(int j=0;j<oldExtensions.length;j++)
                    newBinding.addExtensionElement(oldExtensions[j]);
				
		//Set DocumentationElements of "BindingElement" (optional)
		oldDocumentations = oldBindings[i].getDocumentationElements();
		for(int j=0;j<oldDocumentations.length;j++){
					
                    //Create new "DocumentationElement"
                    DocumentationElement newDocumentation = newBinding.addDocumentationElement();
					
                    //Set Content of "DocumentationElement" (required)
                    newDocumentation.setContent(oldDocumentations[j].getContent());
					
                    //Set ExtensionElements of "DocumentationElement" (optional)
                    oldExtensions = oldDocumentations[j].getExtensionElements();
                    for(int k=0;k<oldExtensions.length;k++)
                        newDocumentation.addExtensionElement(oldExtensions[k]);
                }
                
                //Set BindingFaultElement of "BindingElement" (optional)
		BindingFaultElement[] oldBindingFaults = oldBindings[i].getBindingFaultElements();
		for(int j=0;j<oldBindingFaults.length;j++){
				
                    //Create new "BindingFaultElement"
                    BindingFaultElement newBindingFault = newBinding.addBindingFaultElement();
					
                    //Set Ref of "BindingFaultElement" (required)
                    newBindingFault.setRef(oldBindingFaults[j].getRef());
					
                    //Set DeclaredNamespaces of "BindingFaultElement" (optional)
                    oldNamespaces = oldBindingFaults[j].getDeclaredNamespaces();
                    for (int k = 0; k<oldNamespaces.length; k++)
                        newBindingFault.addNamespace(oldNamespaces[k].getPrefix(), oldNamespaces[k].getNamespaceURI());
					
                    //Set ExtensionAttributes of "BindingFaultElement" (optional)
                    oldAttributes = oldBindingFaults[j].getExtensionAttributes();
                    for(int k=0;k<oldAttributes.length;k++)
                        newBindingFault.setExtensionAttribute(oldAttributes[k].getAttributeType(),oldAttributes[k]);
					
                    //Set ExtensionElements of "BindingFaultElement" (optional)
                    oldExtensions = oldBindingFaults[j].getExtensionElements();
                    for(int k=0;k<oldExtensions.length;k++)
                        newBindingFault.addExtensionElement(oldExtensions[k]);
					
                    //Set DocumentationElements of "BindingFaultElement" (optional)
                    oldDocumentations = oldBindingFaults[j].getDocumentationElements();
                    for(int k=0;k<oldDocumentations.length;k++){
                        
                        //Create new "DocumentationElement"
			DocumentationElement newDocumentation = newBindingFault.addDocumentationElement();
						
			//Set Content of "DocumentationElement" (required)
			newDocumentation.setContent(oldDocumentations[k].getContent());
						
			//Set ExtensionElements of "DocumentationElement" (optional)
			oldExtensions = oldDocumentations[k].getExtensionElements();
			for(int w=0;w<oldExtensions.length;w++)
                            newDocumentation.addExtensionElement(oldExtensions[w]);
                    }
                }
                
                //Set BindingOperationElement of "BindingElement" (optional)
		BindingOperationElement[] oldBindingOperations = oldBindings[i].getBindingOperationElements();
		for(int j=0;j<oldBindingOperations.length;j++){
				
                    //Create new "BindingOperationElement"
                    BindingOperationElement newBindingOperation = newBinding.addBindingOperationElement();
				
                    //Set Ref of "BindingOperationElement"
                    newBindingOperation.setRef(oldBindingOperations[j].getRef());
				
                    //Set DeclaredNamespaces of "BindingOperationElement" (optional)
                    oldNamespaces = oldBindingOperations[j].getDeclaredNamespaces();
                    for (int k = 0; k<oldNamespaces.length; k++)
                        newBindingOperation.addNamespace(oldNamespaces[k].getPrefix(), oldNamespaces[k].getNamespaceURI());
					
                    //Set ExtensionAttributes of "BindingOperationElement" (optional)
                    oldAttributes = oldBindingOperations[j].getExtensionAttributes();
                    for(int k=0;k<oldAttributes.length;k++)
                        newBindingOperation.setExtensionAttribute(oldAttributes[k].getAttributeType(), oldAttributes[k]);
				
                    //Set ExtensionElements of "BindingOperationElement" (optional)
                    oldExtensions = oldBindingOperations[j].getExtensionElements();
                    for(int k=0;k<oldExtensions.length;k++)
                        newBindingOperation.addExtensionElement(oldExtensions[k]);
				
                    //Set DocumentationElements of "BindingFaultElement" (optional)
                    oldDocumentations = oldBindingOperations[j].getDocumentationElements();
                    for(int k=0;k<oldDocumentations.length;k++){
                        
                        //Create new "DocumentationElement"
			DocumentationElement newDocumentation = newBindingOperation.addDocumentationElement();
						
			//Set Content of "DocumentationElement" (required)
			newDocumentation.setContent(oldDocumentations[k].getContent());
						
			//Set ExtensionElements of "DocumentationElement" (optional)
			oldExtensions = oldDocumentations[k].getExtensionElements();
			for(int w=0;w<oldExtensions.length;w++)
                            newDocumentation.addExtensionElement(oldExtensions[w]);
                    }
                    
                    //Set BindingMessageReferenceElements of "BindingOperationElement" (optional)
                    BindingMessageReferenceElement[] oldBindingMessageReferences = oldBindingOperations[j].getBindingMessageReferenceElements();
                    for(int k=0;k<oldBindingMessageReferences.length;k++){
                        
                        //Create new "BindingMessageReferenceElement"
			BindingMessageReferenceElement newBindingMessageReference = newBindingOperation.addBindingMessageReferenceElement();
					
			//Set MessageLabel of "BindingMessageReferenceElement" (optional)
			newBindingMessageReference.setMessageLabel(oldBindingMessageReferences[k].getMessageLabel());
						
			//Set Direction of "BindingMessageReferenceElement" (required)
			newBindingMessageReference.setDirection(oldBindingMessageReferences[k].getDirection());
						
			//Set DeclaredNamespaces of "BindingMessageReferenceElement" (optional)
			oldNamespaces = oldBindingMessageReferences[k].getDeclaredNamespaces();
			for (int w = 0; w<oldNamespaces.length; w++)
                            newBindingMessageReference.addNamespace(oldNamespaces[w].getPrefix(), oldNamespaces[w].getNamespaceURI());
						
			//Set ExtensionAttributes of of "BindingMessageReferenceElement" (optional)
			oldAttributes = oldBindingMessageReferences[k].getExtensionAttributes();
			for(int w=0;w<oldAttributes.length;w++)
                            newBindingMessageReference.setExtensionAttribute(oldAttributes[w].getAttributeType(), oldAttributes[w]);
						
			//Set ExtensionElements of of "BindingMessageReferenceElement" (optional)
			oldExtensions = oldBindingMessageReferences[k].getExtensionElements();
			for(int w=0;w<oldExtensions.length;w++)
                            newBindingMessageReference.addExtensionElement(oldExtensions[w]);
												
			//Set DocumentationElements of "BindingMessageReferenceElement" (optional)
			oldDocumentations = oldBindingMessageReferences[k].getDocumentationElements();
			for(int w=0;w<oldDocumentations.length;w++){
                            
                            //Create new "DocumentationElement"
                            DocumentationElement newDocumentation = newBindingMessageReference.addDocumentationElement();
							
                            //Set Content of "DocumentationElement" (required)
                            newDocumentation.setContent(oldDocumentations[w].getContent());
							
                            //Set ExtensionElements of "DocumentationElement" (optional)
                            oldExtensions = oldDocumentations[w].getExtensionElements();
                            for(int z=0;z<oldExtensions.length;z++)
                                newDocumentation.addExtensionElement(oldExtensions[z]);
                        }
                    }
                    
                    //Set BindingFaultReferenceElements of "BindingOperationElement" (optional)
                    BindingFaultReferenceElement[] oldBindingFaultReferences = oldBindingOperations[j].getBindingFaultReferenceElements();
                    for(int k=0;k<oldBindingFaultReferences.length;k++){
						
                        //Create new "BindingFaultReferenceElement"
			BindingFaultReferenceElement newBindingFaultReference = newBindingOperation.addBindingFaultReferenceElement();
						
			//Set Ref of "BindingFaultReferenceElement" (required)
			newBindingFaultReference.setRef(oldBindingFaultReferences[k].getRef());
						
			//Set MessageLabel of "BindingFaultReferenceElement" (optional)
			newBindingFaultReference.setMessageLabel(oldBindingFaultReferences[k].getMessageLabel());
						
			//Set DeclaredNamespaces of "BindingFaultReferenceElement" (optional)
			oldNamespaces = oldBindingFaultReferences[k].getDeclaredNamespaces();
			for (int w = 0; w<oldNamespaces.length; w++)
                            newBindingFaultReference.addNamespace(oldNamespaces[w].getPrefix(), oldNamespaces[w].getNamespaceURI());
						
			//Set ExtensionAttributes of "BindingFaultReferenceElement" (optional)
			oldAttributes = oldBindingFaultReferences[k].getExtensionAttributes();
			for(int w=0;w<oldAttributes.length;w++)
                            newBindingFaultReference.setExtensionAttribute(oldAttributes[w].getAttributeType(), oldAttributes[w]);
						
			//Set ExtensionElements of "BindingFaultReferenceElement" (optional)
			oldExtensions = oldBindingFaultReferences[k].getExtensionElements();
			for(int w=0;w<oldExtensions.length;w++)
                            newBindingFaultReference.addExtensionElement(oldExtensions[w]);
						
			//Set DocumentationElements of "BindingFaultReferenceElement" (optional)
			oldDocumentations = oldBindingFaultReferences[k].getDocumentationElements();
			for(int w=0;w<oldDocumentations.length;w++){
                            
                            //Create new "DocumentationElement"
                            DocumentationElement newDocumentation = newBindingFaultReference.addDocumentationElement();
							
                            //Set Content of "DocumentationElement" (required)
                            newDocumentation.setContent(oldDocumentations[w].getContent());
							
                            //Set ExtensionElements of "DocumentationElement" (optional)
                            oldExtensions = oldDocumentations[w].getExtensionElements();
                            for(int z=0;z<oldExtensions.length;z++)
                                newDocumentation.addExtensionElement(oldExtensions[z]);						
                        }
                    }
                }
            }
        }
        
        //Binding Element ------------------------------------------------------------------------------------------
		
	//Service Element ------------------------------------------------------------------------------------------
		
	//Obtain "ServiceElements"
	ServiceElement[] oldServices = oldDescriptionElement.getServiceElements();
	for(int i=0;i<oldServices.length;i++){
			
            //Create a new "ServiceElement"
            ServiceElement newService = newDescriptionElement.addServiceElement();
			
            //Set Name of "ServiceElement" (required)
            newService.setName(new NCName(oldServices[i].getName().getLocalPart().toString()));
			
            //Set InterfaceName of "ServiceElement" (required)
            newService.setInterfaceName(oldServices[i].getInterfaceName());
			
            //Set DeclaredNamespaces of "ServiceElement" (optional)
            oldNamespaces = oldServices[i].getDeclaredNamespaces();
            for (int j = 0; j<oldNamespaces.length; j++)
                newService.addNamespace(oldNamespaces[j].getPrefix(), oldNamespaces[j].getNamespaceURI());
			
            //Set ExtensionAttributes of "ServiceElement" (optional)
            oldAttributes = oldServices[i].getExtensionAttributes();
            for(int j=0;j<oldAttributes.length;j++)
                newService.setExtensionAttribute(oldAttributes[j].getAttributeType(), oldAttributes[j]);
			
            //Set ExtensionElements of "ServiceElement" (optional)
            oldExtensions = oldServices[i].getExtensionElements();
            for(int j=0;j<oldExtensions.length;j++)
                newService.addExtensionElement(oldExtensions[j]);
			
            //Set DocumentationElements of "ServiceElement" (optional)
            oldDocumentations = oldServices[i].getDocumentationElements();
            for(int j=0;j<oldDocumentations.length;j++){
                
                //Create new "DocumentationElement"
		DocumentationElement newDocumentation = newService.addDocumentationElement();
				
		//Set Content of "DocumentationElement" (required)
		newDocumentation.setContent(oldDocumentations[j].getContent());
				
		//Set ExtensionElements of "DocumentationElement" (optional)
		oldExtensions = oldDocumentations[j].getExtensionElements();
		for(int k=0;k<oldExtensions.length;k++)
                    newDocumentation.addExtensionElement(oldExtensions[k]);	
            }
            
            //Set EndpointElement of "ServiceElement" (required)
            EndpointElement[] oldEndpoints = oldServices[i].getEndpointElements();
            for(int j=0;j<oldEndpoints.length;j++){
                
                //If is an HTTP binding
		if(oldEndpoints[j].getBindingElement().getType().toString().equals("http://www.w3.org/ns/wsdl/http")){
					
                    //Create a new "EndpointElement"
                    EndpointElement newEndpoint = newService.addEndpointElement();
					
                    //Set Name of "EndpointElement" (required)
                    newEndpoint.setName(oldEndpoints[j].getName());
				
                    //Set BindingName of "EndpointElement" (required)
                    newEndpoint.setBindingName(oldEndpoints[j].getBindingName());
					
                    //Set Address of "EndpointElement" (optional)
                    newEndpoint.setAddress(oldEndpoints[j].getAddress());
				
                    //Set DeclaredNamespaces of "EndpointElement" (optional)
                    oldNamespaces = oldEndpoints[j].getDeclaredNamespaces();
                    for (int k = 0; k<oldNamespaces.length; k++)
                        newEndpoint.addNamespace(oldNamespaces[k].getPrefix(), oldNamespaces[k].getNamespaceURI());
					
                    //Set ExtensionAttributes of "EndpointElement" (optional)
                    oldAttributes = oldEndpoints[j].getExtensionAttributes();
                    for(int k=0;k<oldAttributes.length;k++)
                        newEndpoint.setExtensionAttribute(oldAttributes[k].getAttributeType(), oldAttributes[k]);
					
                    //Set ExtensionElements of "EndpointElement" (optional)
                    oldExtensions = oldEndpoints[j].getExtensionElements();
                    for(int k=0;k<oldExtensions.length;k++)
                        newEndpoint.addExtensionElement(oldExtensions[k]);
					
                    //Set DocumentationElements of "EndpointElement" (optional)
                    oldDocumentations = oldEndpoints[j].getDocumentationElements();
                    for(int k=0;k<oldDocumentations.length;k++){
                        
                        //Create new "DocumentationElement"
			DocumentationElement newDocumentation = newEndpoint.addDocumentationElement();
						
			//Set Content of "DocumentationElement" (required)
			newDocumentation.setContent(oldDocumentations[k].getContent());
					
			//Set ExtensionElements of "DocumentationElement" (optional)
			oldExtensions = oldDocumentations[k].getExtensionElements();
			for(int w=0;w<oldExtensions.length;w++)
                            newDocumentation.addExtensionElement(oldExtensions[w]);	
                    }
                }
            }
        }
        
        //Service Element ------------------------------------------------------------------------------------------
	
	WSDLFactory factory3 = WSDLFactory.newInstance();
	WSDLWriter writer = factory3.newWSDLWriter();
	    
	try {
            
            File temp = File.createTempFile("tempFile", ".tmp");              
            
            writer.writeWSDL(newDescriptionElement, new FileOutputStream(temp));     
            
            BufferedReader br = new BufferedReader(new FileReader(temp));
            PrintWriter pw = new PrintWriter(new FileWriter(new File(outputFilename)));
            
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("%20"," ");
                pw.write(line + "\n");
            }
            
            pw.flush();
            pw.close();
            br.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}