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
import com.exilant.exility.core.ExilityException;
import com.exilant.exility.core.XlxUtil;
import com.exilant.exility.core.ResourceManager;
import com.exilant.exility.core.DataDictionary;
import com.exilant.exility.core.Spit;
import com.exilant.exility.core.DbHandle;
import com.exilant.exility.core.DataCollection;
import com.exilant.exility.core.ServiceInterface;

public class GetLabelsFromDictionary implements ServiceInterface
{
    private static final String TRACE_TEXT = "traceText";
    
    @Override
    public void execute(final DataCollection dc, final DbHandle handle) throws ExilityException {
        final String existingText = Spit.startWriter();
        final String[][] labels = DataDictionary.getAllLabels();
        final String fileName = String.valueOf(ResourceManager.getResourceFolder()) + "i18n/translations.xls";
        final String resultText = "Translation file " + fileName;
        if (XlxUtil.getInstance().appendMissingOnes(fileName, labels)) {
            Spit.out(String.valueOf(resultText) + " saved.");
        }
        else {
            Spit.out(String.valueOf(resultText) + " NOT saved.");
        }
        final String traceText = Spit.stopWriter();
        if (existingText != null) {
            Spit.startWriter();
            Spit.out(existingText);
            Spit.out(traceText);
        }
        dc.addTextValue("traceText", traceText);
    }
    
    @Override
    public DataAccessType getDataAccessType(final DataCollection dc) {
        return DataAccessType.NONE;
    }
    
    @Override
    public String getName() {
        final String name = this.getClass().getSimpleName();
        return String.valueOf(name.substring(0, 1).toLowerCase()) + name.substring(1);
    }
}
