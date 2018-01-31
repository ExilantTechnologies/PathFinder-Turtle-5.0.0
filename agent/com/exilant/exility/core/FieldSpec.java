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

class FieldSpec extends Parameter
{
    static final String VALIDATION_COLUMN_NAME = "KeyFound";
    boolean isFilterField;
    String basedOnField;
    String basedOnFieldValue;
    String fromField;
    String toField;
    String descServiceId;
    String descFields;
    boolean isUniqueField;
    String[] validValues;
    
    FieldSpec() {
        this.isFilterField = false;
        this.basedOnField = null;
        this.basedOnFieldValue = null;
        this.fromField = null;
        this.toField = null;
        this.descServiceId = null;
        this.descFields = null;
        this.isUniqueField = false;
        this.validValues = null;
    }
    
    boolean translateInput(final ServiceData inData, final DataCollection dc) {
        if (!inData.hasValue(this.name)) {
            return false;
        }
        final String val = inData.getValue(this.name);
        final AbstractDataType dt = this.getDataType();
        final Value value = dt.parseValue(this.name, val, this.validValues, dc);
        if (this.isFilterField) {
            String fieldName = String.valueOf(this.name) + "Operator";
            dc.addTextValue(fieldName, inData.getValue(fieldName));
            fieldName = String.valueOf(this.name) + "To";
            dc.addTextValue(fieldName, inData.getValue(fieldName));
        }
        if (value == null) {
            return false;
        }
        dc.addValue(this.name, value);
        return true;
    }
    
    boolean validateField(final DataCollection dc, final DbHandle handle) {
        final Value value = dc.getValue(this.name);
        if (value == null || !value.isSpecified()) {
            if (this.basedOnField == null) {
                if (!this.isOptional) {
                    dc.addMessage("exilFieldIsRequired", this.name);
                    return false;
                }
            }
            else {
                final Value v = dc.getValue(this.basedOnField);
                if (v != null && v.isSpecified() && (this.basedOnFieldValue == null || this.basedOnFieldValue.equals(v.toString()))) {
                    dc.addMessage("exilFieldIsRequired", this.name);
                    return false;
                }
            }
            return true;
        }
        boolean valueToReturn = true;
        if (this.fromField != null) {
            final Value v2 = dc.getValue(this.fromField);
            if (v2 != null && v2.greaterThan(value).getBooleanValue()) {
                dc.addMessage("exilInvalidFromTo", this.fromField, this.name, v2.toString(), value.toString());
                valueToReturn = false;
            }
        }
        if (this.toField != null) {
            final Value v2 = dc.getValue(this.toField);
            if (v2 != null && v2.lessThan(value).getBooleanValue()) {
                dc.addMessage("exilInvalidFromTo", this.name, this.toField, value.toString(), v2.toString());
                valueToReturn = false;
            }
        }
        if (this.descServiceId != null) {
            final SqlInterface sql = Sqls.getSqlTemplateOrNull(this.descServiceId);
            if (sql == null) {
                dc.addMessage("exilNoSuchSql", this.descServiceId);
                return false;
            }
            dc.addValue("keyValue", value);
            int n;
            try {
                n = sql.execute(dc, handle, null, null);
            }
            catch (ExilityException e) {
                dc.addError(e.getMessage());
                return false;
            }
            if (n == 0) {
                if (!this.isUniqueField) {
                    dc.addMessage("exilKeyNotFound", this.name, value.toString());
                    valueToReturn = false;
                }
            }
            else if (this.isUniqueField) {
                dc.addMessage("exilKeyAlreadyExists", this.name, value.toString());
                valueToReturn = false;
            }
        }
        return valueToReturn;
    }
    
    boolean validateColumn(final String gridName, final DataCollection dc, final DbHandle handle) {
        final Grid grid = dc.getGrid(gridName);
        if (!grid.hasColumn(this.name)) {
            return false;
        }
        final Value[] values = grid.getColumn(this.name).getValueArray();
        boolean valueToReturn = true;
        Value[] fromValues = null;
        Value[] toValues = null;
        Value[] basedOnValues = null;
        if (this.basedOnField != null) {
            if (!grid.hasColumn(this.basedOnField)) {
                dc.addMessage("exilNoColumn", gridName, this.basedOnField);
                return false;
            }
            basedOnValues = grid.getColumn(this.basedOnField).getValueArray();
        }
        if (this.fromField != null) {
            if (!grid.hasColumn(this.fromField)) {
                dc.addMessage("exilNoColumn", gridName, this.fromField);
                return false;
            }
            fromValues = grid.getColumn(this.fromField).getValueArray();
        }
        if (this.toField != null) {
            if (!grid.hasColumn(this.toField)) {
                dc.addMessage("exilNoColumn", gridName, this.toField);
                return false;
            }
            toValues = grid.getColumn(this.toField).getValueArray();
        }
        final int nbrRows = values.length;
        for (int i = 0; i < values.length; ++i) {
            final Value value = values[i];
            if (!value.isSpecified()) {
                if (this.basedOnField == null) {
                    if (!this.isOptional) {
                        dc.addMessage("exilFieldIsRequired", this.name);
                        valueToReturn = false;
                    }
                }
                else if (basedOnValues != null && basedOnValues[i].isSpecified() && (this.basedOnFieldValue == null || this.basedOnFieldValue.equals(basedOnValues[i].toString()))) {
                    dc.addMessage("exilFieldIsRequired", this.name);
                    valueToReturn = false;
                }
            }
            else {
                if (this.fromField != null && fromValues != null) {
                    final Value v = fromValues[i];
                    if (v.isSpecified() && v.greaterThan(value).getBooleanValue()) {
                        dc.addMessage("exilInvalidFromTo", this.fromField, this.name, v.toString(), value.toString());
                        valueToReturn = false;
                    }
                }
                if (this.toField != null && toValues != null) {
                    final Value v = toValues[i];
                    if (v.isSpecified() && v.lessThan(value).getBooleanValue()) {
                        dc.addMessage("exilInvalidFromTo", this.name, this.toField, value.toString(), v.toString());
                        valueToReturn = false;
                    }
                }
            }
        }
        if (this.descServiceId != null) {
            final SqlInterface sql = Sqls.getSqlTemplateOrNull(this.descServiceId);
            if (sql == null) {
                dc.addMessage("exilNoSuchSql", this.descServiceId);
                return false;
            }
            int n = 0;
            try {
                n = sql.executeBulkTask(dc, handle, gridName, true);
            }
            catch (ExilityException e) {
                dc.addError(e.getMessage());
                return false;
            }
            if ((n < nbrRows && !this.isUniqueField) || (n > 0 && this.isUniqueField)) {
                final ValueList result = grid.getColumn(String.valueOf(gridName) + "KeyFound");
                for (int j = 0; j < result.length(); ++j) {
                    if (result.getBooleanValue(j)) {
                        if (this.isUniqueField) {
                            dc.addMessage("exilKeyAlreadyExists", this.name, values[j].toString());
                            valueToReturn = false;
                        }
                    }
                    else if (!this.isUniqueField) {
                        dc.addMessage("exilInvalidFromTo", this.name, values[j].toString());
                        valueToReturn = false;
                    }
                }
            }
        }
        return valueToReturn;
    }
    
    void translateOutput(final DataCollection dc, final ServiceData outData) {
        final AbstractDataType dt = this.getDataType();
        final Value value = dc.getValue(this.name);
        if (value == null) {
            outData.addValue(this.name, "");
        }
        else {
            outData.addValue(this.name, dt.format(value));
        }
    }
    
    public void translate(final DataCollection fromDc, final DataCollection toDc) {
        toDc.addValue(this.name, fromDc.getValue(this.name));
    }
}
