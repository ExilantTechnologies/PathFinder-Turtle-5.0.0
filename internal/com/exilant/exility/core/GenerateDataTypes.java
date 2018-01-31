/* *******************************************************************************************************
Copyright (c) 2015 EXILANT Technologies Private Limited
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ******************************************************************************************************** */

package com.exilant.exility.ide;

import com.exilant.exility.core.DataAccessType;
import com.exilant.exility.core.DataTypesGenerator;
import com.exilant.exility.core.ResourceManager;
import com.exilant.exility.core.Spit;
import com.exilant.exility.core.DbHandle;
import com.exilant.exility.core.DataCollection;
import com.exilant.exility.core.ServiceInterface;

public class GenerateDataTypes implements ServiceInterface
{
    static String JS_FILE_NAME;
    static String XSD_FILE_NAME;
    
    static {
        GenerateDataTypes.JS_FILE_NAME = "dataTypes.js";
        GenerateDataTypes.XSD_FILE_NAME = "dataTypes.xsd";
    }
    
    @Override
    public void execute(final DataCollection dc, final DbHandle dbHandle) {
        try {
            final String spittedSoFar = Spit.startWriter();
            String fileName = String.valueOf(ResourceManager.getResourceFolder()) + GenerateDataTypes.JS_FILE_NAME;
            String txt = DataTypesGenerator.toJavaScript();
            ResourceManager.saveText(fileName, txt);
            Spit.out(String.valueOf(fileName) + " saved. You may have to copy to a different place depending on your include tags in your home page.");
            fileName = String.valueOf(ResourceManager.getResourceFolder()) + GenerateDataTypes.XSD_FILE_NAME;
            txt = DataTypesGenerator.toXsd();
            ResourceManager.saveText(fileName, txt);
            Spit.out(String.valueOf(fileName) + " saved as schema include for data types.");
            dc.addTextValue("traceText", Spit.stopWriter());
            Spit.out(spittedSoFar);
        }
        catch (Exception e) {
            final String txt2 = "Unable to generate and save data types. " + e.getMessage();
            dc.addError(txt2);
            Spit.out(txt2);
        }
    }
    
    @Override
    public DataAccessType getDataAccessType(final DataCollection dc) {
        return DataAccessType.NONE;
    }
    
    @Override
    public String getName() {
        return "generateDataTypes";
    }
}
