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
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api_v3.Publisher;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.*;
import org.uddi.v3_service.*;

public class JuddiRegistryAdmin {
    
    static JUDDIApiPortType juddi = null;
    private static UDDISecurityPortType security = null;
    private static UDDIPublicationPortType publish = null;
    private static UDDIInquiryPortType inquiry = null;
        
    private AuthToken authToken;
    
    //Constructor
    public JuddiRegistryAdmin() throws ConfigurationException, TransportException {

        //Create a client & server and read the config in the archive
        UDDIClient uddiClient = new UDDIClient("META-INF/embedded-uddi.xml");
                
        //A UddiClient can be a client to multiple UDDI nodes, so supply the nodeName (defined in uddi.xml)
        //The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
        Transport transport = uddiClient.getTransport("default");
                
        //Create reference to the UDDI API
        juddi = transport.getJUDDIApiService();
        security = transport.getUDDISecurityService();
        publish = transport.getUDDIPublishService();
        inquiry = transport.getUDDIInquiryService();
        
    }
    
    //Authenticate admin user in the UDDI registry (update table j3_auth_token)
    public String authenticate() throws RemoteException {

        //Create new GetAuthToken object
        GetAuthToken getAuthToken = new GetAuthToken();
        
        //Set username and password
        getAuthToken.setUserID("root");
        getAuthToken.setCred("");
        
        //Authenticate user
        authToken = security.getAuthToken(getAuthToken);
        
        return authToken.getAuthInfo();

    }
    
    //Create/Find publisher into UDDI registry (update table j3_publisher) and login as publisher
    public void createPublisher(String authorizedName, String emailAddress, String publisherName) throws RemoteException{
        
        Publisher pb = new Publisher();
        pb.setAuthorizedName(authorizedName);
        pb.setEmailAddress(emailAddress);
        pb.setPublisherName(publisherName);
        pb.setIsAdmin(Boolean.FALSE);
        pb.setIsEnabled(Boolean.TRUE);
        
        SavePublisher sp = new SavePublisher();
        sp.getPublisher().add(pb);
        sp.setAuthInfo(authToken.getAuthInfo());
        PublisherDetail bd = juddi.savePublisher(sp);
            
        //Create new GetAuthToken object
        GetAuthToken getAuthToken = new GetAuthToken();
        
        //Set username and password
        getAuthToken.setUserID(authorizedName);
        getAuthToken.setCred("");
        
        //Authenticate user
        authToken = security.getAuthToken(getAuthToken);
       
    }
    
    private void createPartition() throws RemoteException{
        
        //If partition does not exist, creates partition
        if(findTModel("UDDI extension") == null){
            
            //Create SaveTModel structure and set AuthInfo
            SaveTModel st = new SaveTModel();
            st.setAuthInfo(authToken.getAuthInfo());
        
            //Create new TModel
            TModel tm = new TModel();

            //Set name of TModel
            tm.setName(new Name());
            tm.getName().setValue("UDDI extension");
            
            //Set language of TModel
            tm.getName().setLang("en");
            
            //Set CategoryBag (and associated KeyedReferences) of TModel
            tm.setCategoryBag(new CategoryBag());
            
            KeyedReference kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:categorization:types");
            kr.setKeyName("uddi-org:keyGenerator");
            kr.setKeyValue("keyGenerator");
            tm.getCategoryBag().getKeyedReference().add(kr);
        
            tm.setTModelKey("uddi:extension:keygenerator");
            
            //Save TModel into SaveTModel structure
            st.getTModel().add(tm);
            
            //Publish SaveTModel structure into UDDI Registry
            publish.saveTModel(st);
            
        }
    }
    
    //Find TModel with a given name (no updates)
    private String findTModel(String searchName){
        try{
            
            FindTModel ftm = new FindTModel();
            ftm.setAuthInfo(authToken.getAuthInfo());
            FindQualifiers fq = new FindQualifiers();
            fq.getFindQualifier().add(UDDIConstants.CASE_SENSITIVE_MATCH);
            ftm.setFindQualifiers(fq);
            Name searchname = new Name();
            searchname.setValue(searchName);
            ftm.setName(searchname);
            TModelList findTModel = inquiry.findTModel(ftm);
            
            //If TModel with the given name was found, return TModel key
            if(findTModel.getTModelInfos() != null)
                return findTModel.getTModelInfos().getTModelInfo().get(0).getTModelKey();
            
        } catch (RemoteException ex) { }
        
        return null;
    }
    
    //Find/Create category system (TModel)
    //(Update tables j3_tmodel; j3_tmodel_descr; j3_overview_doc; j3_tmodel_category_bag; j3_category_bag; j3_keyed_reference; j3_uddi_entity)
    private void createCategorySystemTModel(String tModelKey,String name, String descriptionName, String url) throws RemoteException{

        //If TModel does not exist, creates TModel
        if(findTModel(name) ==null){
            
            //Create SaveTModel structure and set AuthInfo
            SaveTModel st = new SaveTModel();
            st.setAuthInfo(authToken.getAuthInfo());
            
            //Create new TModel
            TModel tm = new TModel();

            //Set name of TModel
            tm.setName(new Name());
            tm.getName().setValue(name);
            
            //Set language of TModel
            tm.getName().setLang("en");
            
            //Set description of TModel
            tm.getDescription().add(new Description(descriptionName + " Type Category System","en"));
            
            //Set OverviewDoc of TModel
            OverviewDoc od = new OverviewDoc();
            OverviewURL overviewUrl = new OverviewURL();
            overviewUrl.setValue(url);
            od.setOverviewURL(overviewUrl);
            tm.getOverviewDoc().add(od);
            
            //Set CategoryBag (and associated KeyedReferences) of TModel
            tm.setCategoryBag(new CategoryBag());
            
            KeyedReference kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:categorization:types");
            kr.setKeyName("uddi-org:types:categorization");
            kr.setKeyValue("categorization");
            tm.getCategoryBag().getKeyedReference().add(kr);
               
            kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:categorization:types");
            kr.setKeyName("uddi-org:types:unchecked");
            kr.setKeyValue("unchecked");
            tm.getCategoryBag().getKeyedReference().add(kr);
            
            if(tModelKey.equals("uddi:extension:wsdlInterfaceReference")){
                kr = new KeyedReference();
                kr.setTModelKey("uddi:uddi.org:categorization:entitykeyvalues");
                kr.setKeyName("uddi-org:entityKeyValues");
                kr.setKeyValue("tModelKey");
                tm.getCategoryBag().getKeyedReference().add(kr);
            }
            
            tm.setTModelKey(tModelKey);
            
            //Save TModel into SaveTModel structure
            st.getTModel().add(tm);
            
            //Publish SaveTModel structure into UDDI Registry
            publish.saveTModel(st);
        }
    }
    
    //Create pre-defined category systems (TModels)
    //(Update tables j3_tmodel; j3_tmodel_descr; j3_overview_doc; j3_tmodel_category_bag; j3_category_bag; j3_keyed_reference; j3_uddi_entity)
    public void createCategorySystemTModels() throws RemoteException{
        
        createPartition();
        
        //Create BPMN Process TModel (Arrumar depois e direcionar a URL  p/ minha documentação (fazer))
        createCategorySystemTModel("uddi:extension:bpmn","uddi-extension:bpmn","BPMN Process","http://www.omg.org/spec/BPMN/2.0/PDF/");
        
        //Create WSDL Interface Reference TModel (Arrumar depois e direcionar a URL  p/ minha documentação (fazer))
        createCategorySystemTModel("uddi:extension:wsdlInterfaceReference","uddi-extension:wsdlInterfaceReference","WSDL Interface Reference","http://www.w3.org/TR/wsdl20/#Binding_interface_attribute");
        
        //Create BPMN Process Functionality TModel (Arrumar depois e direcionar a URL  p/ minha documentação (fazer))
        createCategorySystemTModel("uddi:extension:functionality","uddi-extension:functionality","BPMN Process Functionality","http://www.myFunctionalityDefinition.com");
            
        //Create BPMN Process Input TModel (Arrumar depois e direcionar a URL  p/ minha documentação (fazer))
        createCategorySystemTModel("uddi:extension:input","uddi-extension:input","BPMN Process Input","http://www.myInputDefinition.com");
            
        //Create BPMN Process Output TModel (Arrumar depois e direcionar a URL  p/ minha documentação (fazer))
        createCategorySystemTModel("uddi:extension:output","uddi-extension:output","BPMN Process Output","http://www.myOutputDefinition.com");
            
    }
    
    //Discard auth token (No updates in UDDI registry)
    public void logout() throws RemoteException {
        
        security.discardAuthToken(new DiscardAuthToken(authToken.getAuthInfo()));
        
    }    
}