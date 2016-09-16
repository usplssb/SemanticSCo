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

package juddi;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.auth.JUDDIAuthenticator;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

public class JuddiRegistry {

    private static UDDISecurityPortType security = null;
    private static UDDIPublicationPortType publish = null;
    private static UDDIInquiryPortType inquiry = null;
    
    private AuthToken authToken;
    
    //Constructor
    public JuddiRegistry() throws ConfigurationException, TransportException, RemoteException {

        //Create a client & server and read the config in the archive
        UDDIClient uddiClient = new UDDIClient("META-INF/embedded-uddi.xml");
                
        //A UddiClient can be a client to multiple UDDI nodes, so supply the nodeName (defined in uddi.xml)
        //The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
        Transport transport = uddiClient.getTransport("default");
                
        //Create reference to the UDDI API
        security = transport.getUDDISecurityService();
        publish = transport.getUDDIPublishService();
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
    
    //Find all BusinessEntities for given authToken (No updates in UDDI registry)
    public List<BusinessInfo> findAllBusinessEntities() throws RemoteException{
        
        FindBusiness fb = new FindBusiness();
        fb.setAuthInfo(authToken.getAuthInfo());
        org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
        fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
        
        fb.setFindQualifiers(fq);
        Name searchname = new Name();
        searchname.setValue("%");
        fb.getName().add(searchname);
        BusinessList findBusiness = inquiry.findBusiness(fb);
        
        //If Business Entity with given name exists, return its key
        if(findBusiness.getBusinessInfos() != null){
            
            List<BusinessInfo> list = findBusiness.getBusinessInfos().getBusinessInfo();
            
            return list;
        }
        else
            return null;
        
    }
    
    //Verify if BusinessEntity with given name exists (No updates in UDDI registry)
    private String findBusinessEntity(String businessEntityName) throws RemoteException{
        
        FindBusiness fb = new FindBusiness();
        fb.setAuthInfo(authToken.getAuthInfo());
        org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
        fq.getFindQualifier().add(UDDIConstants.CASE_SENSITIVE_MATCH);

        fb.setFindQualifiers(fq);
        Name searchname = new Name();
        searchname.setValue(businessEntityName);
        fb.getName().add(searchname);
        BusinessList findBusiness = inquiry.findBusiness(fb);
            
        //If Business Entity with given name exists, return its key
        if(findBusiness.getBusinessInfos() != null)
            return findBusiness.getBusinessInfos().getBusinessInfo().get(0).getBusinessKey();
        else
            return null;
        
    }
    
    //Find/Create BusinessEntity (update tables j3_business_entity; j3_business_name; j3_uddi_entity)
    public void createBusinessEntity(String businessEntityName) throws RemoteException{
        
        //Verify if Business Entity with given name already exists
        String businessKey = findBusinessEntity(businessEntityName);
        
        //If Business Entity does not exist, create new Business Entity
        if(businessKey == null){
            
            //Create business entity that will contain the service
            BusinessEntity myBusEntity = new BusinessEntity();
            Name myBusName = new Name();
            myBusName.setLang("en");
            myBusName.setValue(businessEntityName);
            myBusEntity.getName().add(myBusName);
            
            //Add the business entity to the "save" structure, using the publisher's authentication info and saving away
            SaveBusiness sb = new SaveBusiness();
            sb.getBusinessEntity().add(myBusEntity);
            sb.setAuthInfo(authToken.getAuthInfo());
            BusinessDetail bd = publish.saveBusiness(sb);
            String myBusKey = bd.getBusinessEntity().get(0).getBusinessKey();
                
        }
    }
    
    //Verify if interface TModel with given name and namespace exists (No updates in UDDI registry)
    private String findWsdlInterfaceTModel(String interfaceName,String wsdlNamespace) throws RemoteException{
        
        FindTModel ftm = new FindTModel();
        ftm.setAuthInfo(authToken.getAuthInfo());
        FindQualifiers fq = new FindQualifiers();
        fq.getFindQualifier().add(UDDIConstants.CASE_SENSITIVE_MATCH);
        ftm.setFindQualifiers(fq);
        Name searchname = new Name();
        searchname.setValue(interfaceName);
        ftm.setName(searchname);
        ftm.setCategoryBag(new CategoryBag());
        KeyedReference kr = new KeyedReference();
        kr.setTModelKey("uddi:uddi.org:xml:namespace");
        kr.setKeyName("uddi-org:xml:namespace");
        kr.setKeyValue(wsdlNamespace);
        ftm.getCategoryBag().getKeyedReference().add(kr);
        kr = new KeyedReference();
        kr.setTModelKey("uddi:uddi.org:wsdl:types");
        kr.setKeyName("uddi-org:wsdl:types");
        kr.setKeyValue("interface");
        ftm.getCategoryBag().getKeyedReference().add(kr);
        
        TModelList findTModel = inquiry.findTModel(ftm);
            
        if(findTModel.getTModelInfos() != null)
            return findTModel.getTModelInfos().getTModelInfo().get(0).getTModelKey();
        
        return null;
            
    }
    
    //Find/Create WSDL interface TModel into UDDI registry
    //(Update tables j3_tmodel; j3_tmodel_descr; j3_overview_doc; j3_tmodel_category_bag; j3_category_bag; j3_keyed_reference; j3_uddi_entity)
    public String publishWsdlInterfaceTModel(String interfaceName, String wsdlLocation, String interfaceNamespace) throws RemoteException{

        //Verify if WSDL interface TModel was already defined
        String wsdlInterfaceKey = findWsdlInterfaceTModel(interfaceName,interfaceNamespace);
            
        //If interface was already defined, return its key
        if(wsdlInterfaceKey != null){
            
            return wsdlInterfaceKey;
                
        }//Else, create WSDL interface TModel and return its created key
        else{
                
            //Create SaveTModel structure and set AuthInfo
            SaveTModel st = new SaveTModel();
            st.setAuthInfo(authToken.getAuthInfo());
            
            //Create new TModel
            TModel tm = new TModel();

            //Set name of TModel
            tm.setName(new Name());
            tm.getName().setValue(interfaceName);
            
            //Set language of TModel
            tm.getName().setLang("en");
            
            //Set description of TModel
            tm.getDescription().add(new Description("WSDL interface: " + interfaceName,"en"));
            
            //Set OverviewDoc of TModel
            OverviewDoc od = new OverviewDoc();
            OverviewURL url = new OverviewURL();
            url.setValue(wsdlLocation);
            od.setOverviewURL(url);
            tm.getOverviewDoc().add(od);
            
            //Set CategoryBag (and associated KeyedReferences) of TModel
            tm.setCategoryBag(new CategoryBag());
            
            KeyedReference kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:xml:namespace");
            kr.setKeyName("uddi-org:xml:namespace");
            kr.setKeyValue(interfaceNamespace);
            tm.getCategoryBag().getKeyedReference().add(kr);
            
            kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:wsdl:types");
            kr.setKeyName("uddi-org:wsdl:types");
            kr.setKeyValue("interface");
            tm.getCategoryBag().getKeyedReference().add(kr);
            
            //Save TModel into SaveTModel structure
            st.getTModel().add(tm);
            
            //Publish SaveTModel structure into UDDI Registry
            TModelDetail saveTModel = publish.saveTModel(st);
                
            return saveTModel.getTModel().get(0).getTModelKey();
        }
    }
    
    //Verify if BPMN process TModel with given name and namespace exists (No updates in UDDI registry)
    private String findBpmnProcessTModel(String processName,String processTargetNamespace,LinkedList<String> wsdlInterfaceKeys) throws RemoteException{
        
        FindTModel ftm = new FindTModel();
        ftm.setAuthInfo(authToken.getAuthInfo());
        FindQualifiers fq = new FindQualifiers();
        fq.getFindQualifier().add(UDDIConstants.CASE_SENSITIVE_MATCH);
        ftm.setFindQualifiers(fq);
        Name searchname = new Name();
        searchname.setValue(processName);
        ftm.setName(searchname);
        ftm.setCategoryBag(new CategoryBag());
        KeyedReference kr = new KeyedReference();
        kr.setTModelKey("uddi:uddi.org:xml:namespace");
        kr.setKeyName("uddi-org:xml:namespace");
        kr.setKeyValue(processTargetNamespace);
        ftm.getCategoryBag().getKeyedReference().add(kr);
        kr = new KeyedReference();
        kr.setTModelKey("uddi:extension:bpmn");
        kr.setKeyName("uddi-extension:bpmn");
        kr.setKeyValue("process");
        ftm.getCategoryBag().getKeyedReference().add(kr);
        
        Iterator it = wsdlInterfaceKeys.iterator();
        while(it.hasNext()){
            String wsdlInterfaceKey = (String) it.next();
            kr = new KeyedReference();
            kr.setTModelKey("uddi:extension:wsdlInterfaceReference");
            kr.setKeyName("uddi-extension:wsdlInterfaceReference");
            kr.setKeyValue(wsdlInterfaceKey);
            ftm.getCategoryBag().getKeyedReference().add(kr);
        }
        
        TModelList findTModel = inquiry.findTModel(ftm);
            
        if(findTModel.getTModelInfos() != null)
            return findTModel.getTModelInfos().getTModelInfo().get(0).getTModelKey();
        
        return null;
            
    }
    
    //Find/Create BPMN process TModel into UDDI registry
    //(Update tables j3_tmodel; j3_tmodel_descr; j3_overview_doc; j3_tmodel_category_bag; j3_category_bag; j3_keyed_reference; j3_uddi_entity)
    public String publishBpmnProcessTModel(LinkedList<String> wsdlInterfaceKeys, String processName, String processLocation, String processTargetNamespace) throws RemoteException {
        
        //Verify if BPMN process TModel was already defined
        String bpmnProcessKey = findBpmnProcessTModel(processName,processTargetNamespace,wsdlInterfaceKeys);
            
        //If BPMN process TModel was already defined, return its key
        if(bpmnProcessKey != null){
                
            return bpmnProcessKey;
                
        }//Else, create BPMN process TModel and return its created key
        else{
        
            //Create SaveTModel structure and set AuthInfo
            SaveTModel st = new SaveTModel();
            st.setAuthInfo(authToken.getAuthInfo());
            
            //Create new TModel
            TModel tm = new TModel();

            //Set name of TModel
            tm.setName(new Name());
            tm.getName().setValue(processName);
            
            //Set language of TModel
            tm.getName().setLang("en");
            
            //Set description of TModel
            tm.getDescription().add(new Description(processName + " BPMN process","en"));
            
            //Set OverviewDoc of TModel
            OverviewDoc od = new OverviewDoc();
            OverviewURL url = new OverviewURL();
            url.setValue(processLocation);
            od.setOverviewURL(url);
            tm.getOverviewDoc().add(od);
            
            //Set CategoryBag (and associated KeyedReferences) of TModel
            tm.setCategoryBag(new CategoryBag());
            
            KeyedReference kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:xml:namespace");
            kr.setKeyName("uddi-org:xml:namespace");
            kr.setKeyValue(processTargetNamespace);
            tm.getCategoryBag().getKeyedReference().add(kr);
            
            kr = new KeyedReference();
            kr.setTModelKey("uddi:extension:bpmn");
            kr.setKeyName("uddi-extension:bpmn");
            kr.setKeyValue("process");
            tm.getCategoryBag().getKeyedReference().add(kr);
            
            Iterator it = wsdlInterfaceKeys.iterator();
            while(it.hasNext()){
                String wsdlInterfaceKey = (String) it.next();
                kr = new KeyedReference();
                kr.setTModelKey("uddi:extension:wsdlInterfaceReference");
                kr.setKeyName("uddi-extension:wsdlInterfaceReference");
                kr.setKeyValue(wsdlInterfaceKey);
                tm.getCategoryBag().getKeyedReference().add(kr);
            }
            
            //Save TModel into SaveTModel structure
            st.getTModel().add(tm);
            
            //Publish SaveTModel structure into UDDI Registry
            TModelDetail saveTModel = publish.saveTModel(st);
                
            return saveTModel.getTModel().get(0).getTModelKey();
        }
    }
    
    //Verify if binding TModel with given name, namespace and interface reference exists (No updates in UDDI registry)
    private String findWsdlBindingTModel(String bindingName,String wsdlNamespace) throws RemoteException{
        
        FindTModel ftm = new FindTModel();
        
        ftm.setAuthInfo(authToken.getAuthInfo());
        
        FindQualifiers fq = new FindQualifiers();
        fq.getFindQualifier().add(UDDIConstants.CASE_SENSITIVE_MATCH);
        ftm.setFindQualifiers(fq);
        
        Name searchname = new Name();
        searchname.setValue(bindingName);
        ftm.setName(searchname);
        
        ftm.setCategoryBag(new CategoryBag());
        
        KeyedReference kr = new KeyedReference();
        kr.setTModelKey("uddi:uddi.org:xml:namespace");
        kr.setKeyName("uddi-org:xml:namespace");
        kr.setKeyValue(wsdlNamespace);
        ftm.getCategoryBag().getKeyedReference().add(kr);
        
        kr = new KeyedReference();
        kr.setTModelKey("uddi:uddi.org:wsdl:types");
        kr.setKeyName("uddi-org:wsdl:types");
        kr.setKeyValue("binding");
        ftm.getCategoryBag().getKeyedReference().add(kr);
        
        TModelList findTModel = inquiry.findTModel(ftm);
            
        if(findTModel.getTModelInfos() != null)
            return findTModel.getTModelInfos().getTModelInfo().get(0).getTModelKey();
        
        return null;
            
    }
    
    //Find/Create WSDL Binding TModel into UDDI registry
    //(Update tables j3_tmodel; j3_tmodel_descr; j3_overview_doc; j3_tmodel_category_bag; j3_category_bag; j3_keyed_reference; j3_uddi_entity)
    public String publishWsdlBindingTModel(String wsdlInterfaceKey,String bindingName,String wsdlLocation,String wsdlNamespace,String protocol) throws RemoteException{
        
        //Verify if WSDL Binding TModel was already defined
        String bindingKey = findWsdlBindingTModel(bindingName,wsdlNamespace);
            
        //If binding was already defined, return its key
        if(bindingKey != null){
                
            return bindingKey;
                
        }//Else, create WSDL Binding TModel and return its created key
        else{
                
            //Create SaveTModel structure and set AuthInfo
            SaveTModel st = new SaveTModel();
            st.setAuthInfo(authToken.getAuthInfo());
            
            //Create new TModel
            TModel tm = new TModel();

            //Set name of TModel
            tm.setName(new Name());
            tm.getName().setValue(bindingName);
            
            //Set language of TModel
            tm.getName().setLang("en");
            
            //Set description of TModel
            tm.getDescription().add(new Description("WSDL binding: " + bindingName,"en"));
            
            //Set OverviewDoc of TModel
            OverviewDoc od = new OverviewDoc();
            OverviewURL url = new OverviewURL();
            url.setValue(wsdlLocation);
            od.setOverviewURL(url);
            tm.getOverviewDoc().add(od);
            
            //Set CategoryBag (and associated KeyedReferences) of TModel
            tm.setCategoryBag(new CategoryBag());
            
            KeyedReference kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:xml:namespace");
            kr.setKeyName("uddi-org:xml:namespace");
            kr.setKeyValue(wsdlNamespace);
            tm.getCategoryBag().getKeyedReference().add(kr);
            
            kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:wsdl:types");
            kr.setKeyName("uddi-org:wsdl:types");
            kr.setKeyValue("binding");
            tm.getCategoryBag().getKeyedReference().add(kr);
            
            kr = new KeyedReference();
            kr.setTModelKey("uddi:extension:wsdlInterfaceReference");
            kr.setKeyName("uddi-extension:wsdlInterfaceReference");
            kr.setKeyValue(wsdlInterfaceKey);
            tm.getCategoryBag().getKeyedReference().add(kr);
            
            if(protocol.equals("soap11")){
                kr = new KeyedReference();
                kr.setTModelKey("uddi:uddi.org:wsdl:categorization:protocol");
                kr.setKeyName("uddi-org:protocol:soap");
                kr.setKeyValue("uddi:uddi.org:protocol:soap");
                tm.getCategoryBag().getKeyedReference().add(kr);   
            }
            else if(protocol.equals("soap12")){
                kr = new KeyedReference();
                kr.setTModelKey("uddi:uddi.org:wsdl:categorization:protocol");
                kr.setKeyName("uddi-org:protocol:soap12");
                kr.setKeyValue("uddi:uddi.org:protocol:soap12");
                tm.getCategoryBag().getKeyedReference().add(kr);  
            }
            else if(protocol.equals("rest")){
                kr = new KeyedReference();
                kr.setTModelKey("uddi:uddi.org:wsdl:categorization:protocol");
                kr.setKeyName("uddi-org:protocol:rest");
                kr.setKeyValue("uddi:uddi.org:protocol:rest");
                tm.getCategoryBag().getKeyedReference().add(kr); 
            }
            
            //Save TModel into SaveTModel structure
            st.getTModel().add(tm);
            
            //Publish SaveTModel structure into UDDI Registry
            TModelDetail saveTModel = publish.saveTModel(st);
                          
            return saveTModel.getTModel().get(0).getTModelKey();
        }
        
    }
    
    //Find/Create Binding Template
    //(Update tables j3_binding_template; j3_tmodel_instance_info; j3_uddi_entity) VERRRR
    private BindingTemplate publishBindingTemplate(String bindingTModelKey,String interfaceTModelKey,String bpmnProcessTModelKey,String wsdlServiceAddress,String wsdlEndpointName, boolean isSoap) {
        
        //Create binding template
        BindingTemplate myBindingTemplate = new BindingTemplate();
            
        //Set access point of binding template
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setUseType(AccessPointType.END_POINT.toString());
        accessPoint.setValue(wsdlServiceAddress);
        myBindingTemplate.setAccessPoint(accessPoint);
            
        //Create TModel instance details and related TModel instance infos
        TModelInstanceDetails myTModelInstanceDetails = new TModelInstanceDetails();
            
        //Define TModelInstanceInfo specifying the binding that this endpoint implements and add it to TModel Instance Details
        TModelInstanceInfo myTModelInstanceInfo = new TModelInstanceInfo();
        myTModelInstanceInfo.setTModelKey(bindingTModelKey);
        InstanceDetails myInstanceDetails = new InstanceDetails();
        myInstanceDetails.setInstanceParms(wsdlEndpointName);
        myTModelInstanceInfo.setInstanceDetails(myInstanceDetails);
        myTModelInstanceDetails.getTModelInstanceInfo().add(myTModelInstanceInfo);
        
        //TModelInstanceInfo specifying the interface that this endpoint implements and add it to TModel Instance Details
        myTModelInstanceInfo = new TModelInstanceInfo();
        myTModelInstanceInfo.setTModelKey(interfaceTModelKey);
        myTModelInstanceDetails.getTModelInstanceInfo().add(myTModelInstanceInfo);
            
        //TModelInstanceInfo specifying the BPMN process that this endpoint support and add it to TModel Instance Details
        myTModelInstanceInfo = new TModelInstanceInfo();
        myTModelInstanceInfo.setTModelKey(bpmnProcessTModelKey);
        myTModelInstanceDetails.getTModelInstanceInfo().add(myTModelInstanceInfo);
            
        //Add TModel Instance Details to Binding Template
        myBindingTemplate.setTModelInstanceDetails(myTModelInstanceDetails);
            
        //Optional but recommended step, this annotates binding with all the standard tModel instance infos
        if(isSoap)
            myBindingTemplate = UDDIClient.addSOAPtModels(myBindingTemplate);
        else
            myBindingTemplate = UDDIClient.addRESTtModels(myBindingTemplate);
            
        return myBindingTemplate;           
    }
    
    //Verify if Business Service with given name and associated businessKey exists (No updates in UDDI registry)
    private String findBusinessService(String businessKey, String processName, String processTargetNamespace){
        
        try{
            
            FindService fs = new FindService();
            FindQualifiers fq = new FindQualifiers();
            fq.getFindQualifier().add(UDDIConstants.CASE_SENSITIVE_MATCH);
            fs.setFindQualifiers(fq);
            fs.setAuthInfo(authToken.getAuthInfo());
            Name searchname = new Name();
            searchname.setValue(processName);
            fs.getName().add(searchname);
            fs.setBusinessKey(businessKey);
            
            fs.setCategoryBag(new CategoryBag());
            KeyedReference kr = new KeyedReference();
            kr.setTModelKey("uddi:extension:bpmn");
            kr.setKeyName("uddi-extension:bpmn");
            kr.setKeyValue("process");
            fs.getCategoryBag().getKeyedReference().add(kr);
           
            kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:xml:namespace");
            kr.setKeyName("uddi-org:xml:namespace");
            kr.setKeyValue(processTargetNamespace);
            fs.getCategoryBag().getKeyedReference().add(kr);
                
            kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:xml:localname");
            kr.setKeyName("uddi-org:xml:localName");
            kr.setKeyValue(processName);
            fs.getCategoryBag().getKeyedReference().add(kr);
            
            ServiceList findService = inquiry.findService(fs);
            
            if(findService.getServiceInfos() != null)
                return findService.getServiceInfos().getServiceInfo().get(0).getServiceKey();
        
        } catch (RemoteException e) { }
        
        return null;
    }    
    
    //Find/Create Business Service representing a BPMN process into UDDI registry
    //(Update tables j3_business_service; j3_service_name; j3_service_descr; j3_service_category_bag; j3_category_bag; j3_keyed_reference; j3_uddi_entity)
    public String publishBusinessService(LinkedList<String> functionalities,LinkedList<String> inputs,LinkedList<String> outputs,String businessKey,String processName,String processDescription,String processTargetNamespace,String bpmnProcessTModelKey,LinkedList<BindingInfo> bindingInfos) throws DispositionReportFaultMessage, RemoteException {
        
        //Verify if Business Service was already defined
        String serviceKey = findBusinessService(businessKey, processName, processTargetNamespace);
            
        //If Business Service was already defined, return its key
        if(serviceKey != null){
           
            return serviceKey;
                
        }//Else, create Business Service and return its created key
        else{
             
            //Create business service and set its name and related businessKey
            BusinessService myService = new BusinessService();
            myService.setBusinessKey(businessKey);
            Name myServName = new Name();
            myServName.setValue(processName);
            myServName.setLang("en");
            myService.getName().add(myServName);
            Description desc = new Description(processDescription,"en");
            myService.getDescription().add(desc);
            
            //Create CategoryBag to store Functionality, Inputs and Outputs of BPMN Process, among other infos
            myService.setCategoryBag(new CategoryBag());
                
            KeyedReference kr = new KeyedReference();
            kr.setTModelKey("uddi:extension:bpmn");
            kr.setKeyName("uddi-extension:bpmn");
            kr.setKeyValue("process");
            myService.getCategoryBag().getKeyedReference().add(kr);
           
            kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:xml:namespace");
            kr.setKeyName("uddi-org:xml:namespace");
            kr.setKeyValue(processTargetNamespace);
            myService.getCategoryBag().getKeyedReference().add(kr);
                
            kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:xml:localname");
            kr.setKeyName("uddi-org:xml:localName");
            kr.setKeyValue(processName);
            myService.getCategoryBag().getKeyedReference().add(kr);
                
            //Functionalities
            Iterator itFunctionalities = functionalities.iterator();
            while(itFunctionalities.hasNext()){
                String functionality= (String) itFunctionalities.next();
                kr = new KeyedReference();
                kr.setTModelKey("uddi:extension:functionality");
                kr.setKeyName("uddi-extension:functionality");
                kr.setKeyValue(functionality);
                myService.getCategoryBag().getKeyedReference().add(kr);
            }
            
            //Inputs
            Iterator itInputs = inputs.iterator();
            while(itInputs.hasNext()){
                String input= (String) itInputs.next();
                kr = new KeyedReference();
                kr.setTModelKey("uddi:extension:input");
                kr.setKeyName("uddi-extension:input");
                kr.setKeyValue(input);
                myService.getCategoryBag().getKeyedReference().add(kr);
            }
            
            //Outputs
            Iterator itOutputs = outputs.iterator();
            while(itOutputs.hasNext()){
                String output= (String) itOutputs.next();
                kr = new KeyedReference();
                kr.setTModelKey("uddi:extension:output");
                kr.setKeyName("uddi-extension:output");
                kr.setKeyValue(output);
                myService.getCategoryBag().getKeyedReference().add(kr);
            }
            
            //Create binding templates for business service
            BindingTemplates myBindingTemplates = new BindingTemplates();
            
            BindingTemplate myBindingTemplate;
            
            //For each WSDL binding create one binding template
            Iterator it = bindingInfos.iterator();
            while(it.hasNext()){
              
                BindingInfo bindingInfo= (BindingInfo) it.next();
                myBindingTemplate = publishBindingTemplate(bindingInfo.getBindingTModelKey(),bindingInfo.getInterfaceTModelKey(),bpmnProcessTModelKey,bindingInfo.getWsdlServiceAddress(),bindingInfo.getWsdlEndpoint(),bindingInfo.isSoap());
                                
                //Add binding template to Binding Templates structure
                myBindingTemplates.getBindingTemplate().add(myBindingTemplate);
            }
            
            //Add binding templates to business service
            myService.setBindingTemplates(myBindingTemplates);
            
            //Add the business service to the "save" structure, using the publisher's authentication info and saving away
            SaveService ss = new SaveService();
            ss.getBusinessService().add(myService);
            ss.setAuthInfo(authToken.getAuthInfo());
            ServiceDetail sd = publish.saveService(ss);
                
            return sd.getBusinessService().get(0).getServiceKey();
        }
           
    }
    
    //Discard auth token (No updates in UDDI registry)
    public void logout() {
        
        try {
            security.discardAuthToken(new DiscardAuthToken(authToken.getAuthInfo()));
        } catch (RemoteException ex) { }
        
    }    
}