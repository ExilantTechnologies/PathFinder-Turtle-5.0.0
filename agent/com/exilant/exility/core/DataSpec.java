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

class DataSpec implements ToBeInitializedInterface
{
    static final String SERVICE_ID_NAME = "serviceId";
    ListSpec[] lists;
    FieldSpec[] fields;
    GridSpec[] grids;
    private boolean hasDescService;
    
    DataSpec() {
        this.lists = new ListSpec[0];
        this.fields = new FieldSpec[0];
        this.grids = new GridSpec[0];
        this.hasDescService = false;
    }
    
    void translateInput(final ServiceData inData, final DataCollection dc) {
        DbHandle handle = null;
        try {
            if (this.hasDescService) {
                handle = DbHandle.borrowHandle(DataAccessType.READONLY);
            }
            FieldSpec[] fields;
            for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
                final FieldSpec field = fields[i];
                field.translateInput(inData, dc);
            }
            FieldSpec[] fields2;
            for (int length2 = (fields2 = this.fields).length, j = 0; j < length2; ++j) {
                final FieldSpec field = fields2[j];
                field.validateField(dc, handle);
            }
            ListSpec[] lists;
            for (int length3 = (lists = this.lists).length, k = 0; k < length3; ++k) {
                final ListSpec list = lists[k];
                list.translateInput(inData, dc);
            }
            GridSpec[] grids;
            for (int length4 = (grids = this.grids).length, l = 0; l < length4; ++l) {
                final GridSpec grid = grids[l];
                grid.translateInput(inData, dc);
                grid.validate(dc, handle);
            }
            if (handle != null) {
                DbHandle.returnHandle(handle);
            }
        }
        catch (ExilityException e) {
            Spit.out(e);
            dc.addMessage("exilityError", e.getMessage());
        }
    }
    
    void translateOutput(final DataCollection dc, final ServiceData outData) {
        FieldSpec[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final FieldSpec field = fields[i];
            field.translateOutput(dc, outData);
        }
        ListSpec[] lists;
        for (int length2 = (lists = this.lists).length, j = 0; j < length2; ++j) {
            final ListSpec list = lists[j];
            list.translateOutput(dc, outData);
        }
        GridSpec[] grids;
        for (int length3 = (grids = this.grids).length, k = 0; k < length3; ++k) {
            final GridSpec grid = grids[k];
            grid.translateOutput(dc, outData);
        }
    }
    
    void translate(final DataCollection fromDc, final DataCollection toDc) {
        FieldSpec[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final FieldSpec field = fields[i];
            field.translate(fromDc, toDc);
        }
        ListSpec[] lists;
        for (int length2 = (lists = this.lists).length, j = 0; j < length2; ++j) {
            final ListSpec list = lists[j];
            list.translate(fromDc, toDc);
        }
        GridSpec[] grids;
        for (int length3 = (grids = this.grids).length, k = 0; k < length3; ++k) {
            final GridSpec grid = grids[k];
            grid.translate(fromDc, toDc);
        }
    }
    
    @Override
    public void initialize() {
        if (this.fields != null) {
            FieldSpec[] fields;
            for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
                final FieldSpec f = fields[i];
                if (f.descServiceId != null) {
                    this.hasDescService = true;
                    return;
                }
            }
        }
        if (this.grids != null) {
            GridSpec[] grids;
            for (int length2 = (grids = this.grids).length, j = 0; j < length2; ++j) {
                final GridSpec gs = grids[j];
                if (gs.columns != null) {
                    FieldSpec[] columns;
                    for (int length3 = (columns = gs.columns).length, k = 0; k < length3; ++k) {
                        final FieldSpec f2 = columns[k];
                        if (f2.descServiceId != null) {
                            this.hasDescService = true;
                            return;
                        }
                    }
                }
            }
        }
    }
}
