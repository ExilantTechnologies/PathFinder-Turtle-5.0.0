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

package com.exilant.exility.core;

import java.util.Iterator;

class AutoDisplayPanel extends DisplayPanel
{
    private static final String TABLE = "table";
    String generationType;
    String generateFrom;
    boolean editable;
    
    AutoDisplayPanel() {
        this.generationType = "table";
        this.generateFrom = null;
        this.editable = false;
    }
    
    @Override
    public void initialize() {
        if (this.generationType.equals("table")) {
            this.generateFromTable();
        }
        else {
            this.generateFromGroup();
        }
        super.initialize();
    }
    
    private void generateFromGroup() {
        final DataGroup dataGroup = DataDictionary.getGroup(this.generateFrom);
        if (dataGroup == null) {
            Spit.out("Error: " + this.generateFrom + " is not a data group name.");
            return;
        }
        this.elements = new AbstractElement[dataGroup.elements.size()];
        final int i = 0;
        for (final DataElement de : dataGroup.elements.values()) {
            this.elements[i] = AbstractField.getDefaultField(de, this.editable);
        }
    }
    
    private void generateFromTable() {
        Table table = null;
        try {
            table = (Table)Tables.getTable(this.generateFrom, null);
        }
        catch (ExilityException e) {
            Spit.out("Error: " + this.generateFrom + " is not a table name.");
            return;
        }
        final Column[] columns = table.columns;
        this.elements = new AbstractElement[columns.length];
        for (int i = 0; i < columns.length; ++i) {
            this.elements[i] = AbstractField.getDefaultField(columns[i], this.editable);
        }
    }
}
