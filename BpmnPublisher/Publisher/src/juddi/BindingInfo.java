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

public class BindingInfo {
    
    private String interfaceTModelKey;
    private String bindingTModelKey;
    private String wsdlServiceAddress;
    private String wsdlEndpoint;
    private boolean isSoap;

    
    public BindingInfo(){ }
    
    public BindingInfo(String interfaceTModelKey, String bindingTModelKey, String wsdlServiceAddress, String wsdlEndpoint, boolean isSoap){
        this.interfaceTModelKey = interfaceTModelKey;
        this.bindingTModelKey = bindingTModelKey;
        this.wsdlServiceAddress = wsdlServiceAddress;
        this.wsdlEndpoint = wsdlEndpoint;
        this.isSoap = isSoap;
                
    }
    
    public String getInterfaceTModelKey() {
        return interfaceTModelKey;
    }

    public void setInterfaceTModelKey(String interfaceTModelKey) {
        this.interfaceTModelKey = interfaceTModelKey;
    }

    public String getBindingTModelKey() {
        return bindingTModelKey;
    }

    public void setBindingTModelKey(String bindingTModelKey) {
        this.bindingTModelKey = bindingTModelKey;
    }

    public String getWsdlServiceAddress() {
        return wsdlServiceAddress;
    }

    public void setWsdlServiceAddress(String wsdlServiceAddress) {
        this.wsdlServiceAddress = wsdlServiceAddress;
    }

    public String getWsdlEndpoint() {
        return wsdlEndpoint;
    }

    public void setWsdlEndpoint(String wsdlEndpoint) {
        this.wsdlEndpoint = wsdlEndpoint;
    }
    
    public boolean isSoap() {
        return isSoap;
    }

    public void setIsSoap(boolean isSoap) {
        this.isSoap = isSoap;
    }
    
    @Override
    public boolean equals(Object object) {
    
        if (!(object instanceof BindingInfo))
            return false;

        BindingInfo bInfo = (BindingInfo) object;
    
        return this.bindingTModelKey.equals(bInfo.bindingTModelKey)
            && this.interfaceTModelKey.equals(bInfo.interfaceTModelKey)
            && this.isSoap == bInfo.isSoap
            && this.wsdlEndpoint.equals(bInfo.wsdlEndpoint)
            && this.wsdlServiceAddress.equals(bInfo.wsdlServiceAddress);
    }
}