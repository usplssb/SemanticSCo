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

import java.io.IOException;
import org.apache.woden.*;

public class ValidatorApplication {
    
    public static void main(String[] args) throws WSDLException, IOException{
        
        //Get command line arguments
        String inputFilename = args[0];
        
        // Obtain WSDLFactory objects
	WSDLFactory factoryReader;
        WSDLReader reader;
        
        //Obtain WSDLFactory
        factoryReader = WSDLFactory.newInstance();
            
        //Obtain a WSDLReader object (the WSDL parser)
        reader= factoryReader.newWSDLReader();
            
        //Enable WSDL validation on the reader
        reader.setFeature(WSDLReader.FEATURE_VALIDATION, true);
            
        //Read a WSDL document
        reader.readWSDL(inputFilename);
    }
}
