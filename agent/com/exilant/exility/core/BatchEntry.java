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

class BatchEntry extends ServiceEntry
{
    String inputServiceName;
    String inputGridName;
    String inputSqlName;
    String batchServiceName;
    String logServiceName;
    
    BatchEntry() {
        this.inputServiceName = null;
        this.inputGridName = null;
        this.inputSqlName = null;
        this.batchServiceName = null;
        this.logServiceName = null;
    }
    
    @Override
    void serve(final DataCollection dc) {
        try {
            if (this.inputServiceName != null) {
                final Grid grid = this.executeInputService(dc);
                if (grid == null) {
                    dc.addError("Batch process " + this.name + " : input service did not generate grid " + this.inputGridName);
                }
                else {
                    this.iterateWithGrid(dc, grid);
                }
            }
            else if (this.inputSqlName != null) {
                this.iterateWithSql(dc);
            }
            if (this.logServiceName != null) {
                this.executeLogService(dc);
            }
        }
        catch (Exception e) {
            Spit.out(e);
            dc.addError(e.getMessage());
        }
    }
    
    private Grid executeInputService(final DataCollection dc) throws ExilityException {
        DbHandle dbHandle = null;
        try {
            dbHandle = DbHandle.borrowHandle(DataAccessType.READONLY);
            final ServiceInterface service = Services.getService(this.inputServiceName, dc);
            service.execute(dc, dbHandle);
        }
        catch (Exception e) {
            dc.addError(e.getMessage());
            return dc.getGrid(this.inputGridName);
        }
        finally {
            DbHandle.returnHandle(dbHandle);
        }
        DbHandle.returnHandle(dbHandle);
        return dc.getGrid(this.inputGridName);
    }
    
    private void executeLogService(final DataCollection dc) throws ExilityException {
        DbHandle dbHandle = null;
        try {
            dbHandle = DbHandle.borrowHandle(DataAccessType.READWRITE);
            final ServiceInterface service = Services.getService(this.logServiceName, dc);
            service.execute(dc, dbHandle);
        }
        catch (Exception e) {
            dc.addError(e.getMessage());
            return;
        }
        finally {
            DbHandle.returnHandle(dbHandle);
        }
        DbHandle.returnHandle(dbHandle);
    }
    
    private void iterateWithGrid(final DataCollection dc, final Grid grid) throws ExilityException {
        final ServiceInterface service = Services.getService(this.batchServiceName, dc);
        final DbHandle handle = DbHandle.borrowHandle(DataAccessType.READWRITE, dc.hasValue("suppress-sql-log"));
        for (int n = grid.getNumberOfRows(), i = 0; i < n; ++i) {
            grid.copyRowToDc(i, "", dc);
            handle.beginTransaction();
            try {
                service.execute(dc, handle);
            }
            catch (Exception e) {
                dc.addError(e.getMessage());
            }
            if (dc.hasError()) {
                handle.rollback();
            }
            else {
                handle.commit();
            }
            dc.zapMessages();
        }
        DbHandle.returnHandle(handle);
    }
    
    private void iterateWithSql(final DataCollection dc) throws ExilityException {
        final SqlInterface sql = Sqls.getTemplate(this.inputSqlName, dc);
        if (!(sql instanceof Sql)) {
            throw new ExilityException(String.valueOf(this.inputSqlName) + " is not a sql template. Batch service can not be defined using this sql.");
        }
        final ServiceInterface service = Services.getService(this.batchServiceName, dc);
        final DbHandle handle = DbHandle.borrowHandle(DataAccessType.READONLY);
        handle.callServiceForEachRow((Sql)sql, dc, service);
    }
}
