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
import com.exilant.exility.core.Page;
import java.util.Date;
import com.exilant.exility.core.Pages;
import com.exilant.exility.core.ExilityException;
import com.exilant.exility.core.ResourceManager;
import com.exilant.exility.core.Spit;
import com.exilant.exility.core.DbHandle;
import com.exilant.exility.core.DataCollection;
import com.exilant.exility.core.ServiceInterface;

public class GeneratePage implements ServiceInterface
{
    private static final String FILE_NAME = "fileName";
    private static final String FOLDER_NAME = "folderName";
    private static final String LIVE_WITH_ERRORS = "liveWithErrors";
    private static final String LANGUAGE = "language";
    private static final String NBR_GENERATED = "nbrGenerated";
    private static final String TRACE_TEXT = "traceText";
    
    @Override
    public void execute(final DataCollection dc, final DbHandle dbHandle) throws ExilityException {
        String fileName = dc.getTextValue("fileName", "");
        final String language = dc.getTextValue("language", null);
        if (fileName.equals(".")) {
            fileName = "";
        }
        final String folderName = dc.getTextValue("folderName", null);
        final boolean liveWithErrors = dc.getBooleanValue("liveWithErrors", false);
        final String spittedSoFar = Spit.startWriter();
        int nbr = 0;
        if (fileName.equals("")) {
            Spit.out("Going to generate pages for all files in folder " + folderName);
            final String[] fileNames = ResourceManager.getResourceList("page/" + folderName.replace('.', '/'));
            String[] array;
            for (int length = (array = fileNames).length, i = 0; i < length; ++i) {
                final String fn = array[i];
                try {
                    nbr += this.generateOnePage(folderName, fn, dc, liveWithErrors, language);
                }
                catch (Exception e) {
                    dc.addMessage("exilError", String.valueOf(folderName) + '/' + fn + " could not be generated. error : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        else {
            Spit.out("Going to generate page for file " + fileName);
            nbr = this.generateOnePage(folderName, fileName, dc, liveWithErrors, language);
        }
        dc.addIntegralValue("nbrGenerated", nbr);
        dc.addTextValue("traceText", Spit.stopWriter());
        if (spittedSoFar != null) {
            Spit.startWriter();
            Spit.out(spittedSoFar);
        }
    }
    
    public int generateOnePage(final String folderName, final String fileName, final DataCollection dc, final boolean generateEvenOnError, final String language) {
        final Page page = Pages.getPage(folderName, fileName);
        if (page == null) {
            return 0;
        }
        final boolean ok = page.generateAndSavePage(false, language);
        final String msg = ok ? ("\n" + fileName + " generated at " + new Date()) : (String.valueOf(fileName) + " NOT GENERATED\n");
        Spit.out(msg);
        return ok ? 1 : 0;
    }
    
    @Override
    public DataAccessType getDataAccessType(final DataCollection dc) {
        return DataAccessType.NONE;
    }
    
    public static void main(final String[] args) {
        final DataCollection dc = new DataCollection();
        ResourceManager.loadAllResources(args[2], null);
        dc.addTextValue("folderName", args[0]);
        dc.addTextValue("fileName", args[1]);
        dc.addBooleanValue("liveWithErrors", true);
        final GeneratePage gen = new GeneratePage();
        try {
            gen.execute(dc, null);
        }
        catch (ExilityException e) {
            dc.addError(e.getMessage());
        }
    }
    
    @Override
    public String getName() {
        return "generatePage";
    }
}
