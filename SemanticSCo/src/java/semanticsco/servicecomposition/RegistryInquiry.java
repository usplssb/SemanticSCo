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

import semanticsco.extra.Binding;
import semanticsco.extra.DataElement;
import semanticsco.extra.Interface;
import semanticsco.extra.Service;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.auth.JUDDIAuthenticator;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

public class RegistryInquiry {
    
    private static UDDISecurityPortType security = null;
    private static UDDIInquiryPortType inquiry = null;
    
    private AuthToken authToken;
    
    //Constructor
    public RegistryInquiry() throws ConfigurationException, TransportException{
        
        //Create a client & server and read the config in the archive
        UDDIClient uddiClient = new UDDIClient("META-INF/embedded-uddi.xml");
                
        //A UddiClient can be a client to multiple UDDI nodes, so supply the nodeName (defined in uddi.xml)
        //The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
        Transport transport = uddiClient.getTransport("default");
                
        //Create reference to the UDDI API
        security = transport.getUDDISecurityService();
        inquiry = transport.getUDDIInquiryService();
    }
    
    //Authenticate user in the UDDI registry (update table j3_auth_token)
    public String authenticate(String authorizedName) {
        
        try{
            
            //Try find registered authorized name into j3_publisher table. If does not exist, generate exception
            JUDDIAuthenticator authenticator = new JUDDIAuthenticator();
            authenticator.identify(authenticator.authenticate("uddi", ""),authorizedName);
            
            //Create new GetAuthToken object
            GetAuthToken getAuthToken = new GetAuthToken();
        
            //Set username and password
            getAuthToken.setUserID(authorizedName);
            getAuthToken.setCred("");
        
            //Authenticate user and set class variable (token to be subsequently used)
            authToken = security.getAuthToken(getAuthToken);
            
            return authToken.getAuthInfo();
            
        } catch (RemoteException ex) {
            
            return null;
            
        }
    }
    
    //Search for Business Services with given semantics in UDDI registry (No updates in UDDI registry)
    public LinkedList<Service> findBusinessServicesByFunction(LinkedList<String> semanticConcepts){
        
        //Create and initialize LinkedList to store found services
        LinkedList<Service> foundServices = new LinkedList<>();
        
        try{
            //Set qualifiers
            FindService fs = new FindService();
            FindQualifiers fq = new FindQualifiers();
            fq.getFindQualifier().add(UDDIConstants.OR_ALL_KEYS);
            fs.setFindQualifiers(fq);
            
            //Add semantic concepts to category bags
            fs.setCategoryBag(new CategoryBag());
            Iterator it = semanticConcepts.iterator();
            while(it.hasNext()){
                KeyedReference kr = new KeyedReference();
                kr.setTModelKey("uddi:extension:functionality");
                kr.setKeyName("uddi-extension:functionality");
                kr.setKeyValue((String) it.next());
                fs.getCategoryBag().getKeyedReference().add(kr);
            }
            
            //Search service registry
            ServiceList findService = inquiry.findService(fs);
            
            //If some service was retrieved (found)
            if(findService.getServiceInfos() != null){
                
                //For each retrieved service
                it = findService.getServiceInfos().getServiceInfo().iterator();
                while(it.hasNext()){
                    
                    ServiceInfo serviceInfo = (ServiceInfo) it.next();
                    
                    //Extract service information from UDDI registry and save to Service object
                    Service service = this.getServiceDetails(serviceInfo.getServiceKey());
                    
                    //Add service to LinkedList "foundServices"
                    foundServices.add(service);
                }
                
            }
        
        } catch (RemoteException ex) { }
        
        return foundServices;
    }    
    
    //Search for Business Services with given semantics in UDDI registry (No updates in UDDI registry)
    public LinkedList<Service> findBusinessServicesByData(String semType, LinkedList<String> semanticConcepts){
        
        //Create and initialize LinkedList to store found services
        LinkedList<Service> foundServices = new LinkedList<>();
        
        try{
            //Set qualifiers
            FindService fs = new FindService();
            FindQualifiers fq = new FindQualifiers();
            fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
            fs.setFindQualifiers(fq);
            
            //Add semantic concepts to category bags
            fs.setCategoryBag(new CategoryBag());
            Iterator it = semanticConcepts.iterator();
            while(it.hasNext()){
                KeyedReference kr = new KeyedReference();
                kr.setTModelKey("uddi:extension:"+semType);
                kr.setKeyName("uddi-extension:"+semType);
                kr.setKeyValue((String) it.next()+ "%");
                fs.getCategoryBag().getKeyedReference().add(kr);
            }
            
            //Search service registry
            ServiceList findService = inquiry.findService(fs);
            
            //If some service was retrieved (found)
            if(findService.getServiceInfos() != null){
                
                //For each retrieved service
                it = findService.getServiceInfos().getServiceInfo().iterator();
                while(it.hasNext()){
                    
                    ServiceInfo serviceInfo = (ServiceInfo) it.next();
                    
                    //Extract service information from UDDI registry and save to Service object
                    Service service = this.getServiceDetails(serviceInfo.getServiceKey());
                    
                    //Add service to LinkedList "foundServices"
                    foundServices.add(service);
                }
                
            }
        
        } catch (RemoteException ex) { }
        
        return foundServices;
    }    
    
    //Verifies type of tModel (No updates in UDDI registry)
    private String getTModelType(List<KeyedReference> keyedReferences) throws RemoteException{
        
        for (Iterator kIt = keyedReferences.iterator(); kIt.hasNext();){
            KeyedReference kr = (KeyedReference) kIt.next();
            switch (kr.getKeyValue()) {
                case "interface":
                    return "interface";
                case "binding":
                    return "binding";
                case "process":
                    return "process";
            }
        }     
        return "";
    }
 
    //Find the service details from the repository (No updates in UDDI registry)
    private Service getServiceDetails(String serviceKey) {
		
       Service service = new Service();
       
       //Create counters for input/output IDs
       int inputID = 1;
       int outputID = 1;
       
	try {
            
            //Get business service information in service registry
            GetServiceDetail gsd = new GetServiceDetail();
            gsd.getServiceKey().add(serviceKey);
            ServiceDetail serviceDetails = inquiry.getServiceDetail(gsd);
                
            BusinessService bs = serviceDetails.getBusinessService().get(0);
                
            //Set service identifier
            service.setIdentifier(serviceKey);
                    
            //Get service name
            if(!bs.getName().isEmpty())
                service.setName(bs.getName().get(0).getValue());
            
            //Set service description
            if(!bs.getDescription().isEmpty())
                service.setDescription(bs.getDescription().get(0).getValue());
                
            //Get category bag that stores service namespace and semantic concepts (goals, inputs and outputs)
            CategoryBag serviceCategoryBag = bs.getCategoryBag();
            if(serviceCategoryBag != null){
                List<KeyedReference> keyedReferences = serviceCategoryBag.getKeyedReference();
                
                for(Iterator kIt = keyedReferences.iterator(); kIt.hasNext();){
                    
                    KeyedReference kr = (KeyedReference) kIt.next();
                    
                    switch (kr.getKeyName()) {
                        case "uddi-org:xml:namespace":{
                            service.setNamespace(kr.getKeyValue());
                            break;
                        }
                        case "uddi-extension:functionality":{
                            service.addFunction(kr.getKeyValue());
                            break;
                        }
                        case "uddi-extension:input":{
                            //Extract semantic concept, parameter name and syntactical type
                            String value = kr.getKeyValue();
                            int index = value.indexOf("|");
                            int index2 = value.indexOf("|", index+1);
                            DataElement de = new DataElement(service.getIdentifier() + "-InputID-" + inputID++,value.substring(index+1, index2),value.substring(0, index),value.substring(index2+1, value.length()),serviceKey);
                            service.addInput(de);
                            break;
                        }
                        case "uddi-extension:output":{
                            //Extract semantic concept, parameter name and syntactical type
                            String value = kr.getKeyValue();
                            int index = value.indexOf("|");
                            int index2 = value.indexOf("|", index+1);
                            DataElement de = new DataElement(service.getIdentifier() + "-OutputID-" + outputID++,value.substring(index+1, index2),value.substring(0, index),value.substring(index2+1, value.length()),serviceKey);
                            service.addOutput(de);
                            break;
                        }
                    }
                }            
            }
            
            LinkedList<Binding> bindingTemps = new LinkedList<>();
            LinkedList<Interface> interfaces = new LinkedList<>();
            
            //Get binding templates
            if(bs.getBindingTemplates() != null){
                
                //For each binding template
                List<BindingTemplate> bindingTemplates = bs.getBindingTemplates().getBindingTemplate();
                for(Iterator btIt = bindingTemplates.iterator(); btIt.hasNext();){
                
                    //Create object Binding to store binding information
                    Binding binding = new Binding();
                            
                    BindingTemplate bt = (BindingTemplate) btIt.next();
                            
                    //Set address
                    binding.setAddress(bt.getAccessPoint().getValue());
                            
                    if(bt.getTModelInstanceDetails() != null){
                                
                        //For each tModelInstanceInfo
                        List<TModelInstanceInfo> tInstanceInfos = bt.getTModelInstanceDetails().getTModelInstanceInfo();
                        for(Iterator tiIt = tInstanceInfos.iterator(); tiIt.hasNext();){
                        
                            TModelInstanceInfo tInstanceInfo = (TModelInstanceInfo) tiIt.next();
                        
                            //Get tModel associated to tModel key
                            GetTModelDetail gtmd = new GetTModelDetail();
                            gtmd.getTModelKey().add(tInstanceInfo.getTModelKey());
                            TModelDetail tmd = inquiry.getTModelDetail(gtmd);
                            if(tmd != null){
                                TModel tModel = tmd.getTModel().get(0);
                                CategoryBag categoryBag = tModel.getCategoryBag();
                                if(categoryBag != null){
                                    List<KeyedReference> keyedReferences = categoryBag.getKeyedReference();
                                            
                                    String tModelType = this.getTModelType(keyedReferences);
                                    
                                    switch (tModelType) {
                                        //If is a process tModel
                                        case "process":{
                                            
                                            //Set service location
                                            if(!tModel.getOverviewDoc().isEmpty())
                                                service.setLocation(tModel.getOverviewDoc().get(0).getOverviewURL().getValue());
                                            
                                            break;
                                        }
                                        //If is an interface tModel
                                        case "interface":{
                                            
                                            Interface inter = new Interface();
                                            
                                            //Set interface id
                                            inter.setIdentifier(tModel.getTModelKey());
                                                                                        
                                            //Set interface name
                                            inter.setName(tModel.getName().getValue());
                                                                                        
                                            //Set interface location
                                            if(!tModel.getOverviewDoc().isEmpty())
                                                inter.setLocation(tModel.getOverviewDoc().get(0).getOverviewURL().getValue());
                                            
                                            //Set interface namespace
                                            for(Iterator kIt = keyedReferences.iterator(); kIt.hasNext();){
                                                KeyedReference kr = (KeyedReference) kIt.next();
                                                if(kr.getKeyName().equals("uddi-org:xml:namespace")){
                                                    inter.setNamespace(kr.getKeyValue());
                                                }
                                            }
                                            
                                            //Add interface to list of interfaces
                                            interfaces.add(inter);
                                            
                                            break;
                                        }
                                        //If is a binding tModel
                                        case "binding":{
                                            
                                            //Set binding name
                                            binding.setName(tModel.getName().getValue());
                                            
                                            //Set binding location
                                            if(!tModel.getOverviewDoc().isEmpty())
                                                binding.setLocation(tModel.getOverviewDoc().get(0).getOverviewURL().getValue());
                                            
                                            //Set binding namespace and protocol, and interface id
                                            for(Iterator kIt = keyedReferences.iterator(); kIt.hasNext();){
                                                KeyedReference kr = (KeyedReference) kIt.next();
                                                if(kr.getKeyName().equals("uddi-org:xml:namespace"))
                                                    binding.setNamespace(kr.getKeyValue());                                            
                                                else if(kr.getKeyName().contains("uddi-org:protocol"))
                                                    binding.setProtocol(kr.getKeyName().split("uddi-org:protocol:")[1]);
                                                else if(kr.getKeyName().contains("uddi-extension:wsdlInterfaceReference"))
                                                    binding.setInterfaceId(kr.getKeyValue());
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    //Add binding to list of bindings
                    bindingTemps.add(binding);
                    
                }
                
                //Add bindings to each interface
                for(Iterator it = interfaces.iterator(); it.hasNext();){
                    Interface inter = (Interface) it.next();
                    for(Iterator it2 = bindingTemps.iterator(); it2.hasNext();){
                        Binding binding = (Binding) it2.next();
                        if(binding.getInterfaceId().equals(inter.getIdentifier()))
                            inter.addBinding(binding);
                    }
                }
                
                //Add interfaces to service
                service.setInterfaces(interfaces);
            }
        } catch (RemoteException ex) { }
                
        return service;
    }
    
    //Discard auth token (No updates in UDDI registry)
    public void logout() {
        
        try {
            
            security.discardAuthToken(new DiscardAuthToken(authToken.getAuthInfo()));
                        
        } catch (RemoteException ex) {  }
        
    }    
}
