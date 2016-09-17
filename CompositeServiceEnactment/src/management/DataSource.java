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

package management;

public class DataSource {

    private String dataSourceID;
    private String dataSourceName;
    private String dataSourceConcept;
    private String dataSourceValue;
    private String dataSourceUserValue;
    
    public DataSource(String dataSourceID,String dataSourceName,String dataSourceConcept,String dataSourceUserValue){
        this.dataSourceID = dataSourceID;
        this.dataSourceName = dataSourceName;
        this.dataSourceConcept = dataSourceConcept;
        this.dataSourceUserValue = dataSourceUserValue;
    }

    public String getDataSourceID() {
        return dataSourceID;
    }

    public void setDataSourceID(String dataSourceID) {
        this.dataSourceID = dataSourceID;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceConcept() {
        return dataSourceConcept;
    }

    public void setDataSourceConcept(String dataSourceConcept) {
        this.dataSourceConcept = dataSourceConcept;
    }

    public String getDataSourceValue() {
        return dataSourceValue;
    }

    public void setDataSourceValue(String dataSourceValue) {
        this.dataSourceValue = dataSourceValue;
    }   

    public String getDataSourceUserValue() {
        return dataSourceUserValue;
    }

    public void setDataSourceUserValue(String dataSourceUserValue) {
        this.dataSourceUserValue = dataSourceUserValue;
    }
    
}
