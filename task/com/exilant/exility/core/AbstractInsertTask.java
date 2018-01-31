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

public abstract class AbstractInsertTask extends ExilityTask
{
    String parentTableName;
    String parentKeyColumnNameInThisGrid;
    
    public AbstractInsertTask() {
        this.parentTableName = null;
        this.parentKeyColumnNameInThisGrid = null;
    }
    
    protected void copyGeneratedKeys(final Grid grid, final DataCollection dc) throws ExilityException {
        final ValueList vals = grid.getColumn(this.parentKeyColumnNameInThisGrid);
        if (vals == null) {
            dc.raiseException("exilNoColumn", grid.getName(), this.parentKeyColumnNameInThisGrid);
            return;
        }
        final long[] keys = vals.getIntegralList();
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i] <= 0L) {
                final long newKey = dc.getGeneratedKey(this.parentTableName, keys[i]);
                vals.setIntegralValue(newKey, i);
            }
        }
    }
}
