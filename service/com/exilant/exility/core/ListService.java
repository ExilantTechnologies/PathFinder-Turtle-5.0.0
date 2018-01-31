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

class ListService
{
    static ListService getService() {
        return new ListService();
    }
    
    void execute(final DataCollection dc) throws ExilityException {
        final Grid grid = dc.getGrid("listServiceIds");
        if (grid == null) {
            dc.addError("A grid named listServiceIds was not sent by client.");
            return;
        }
        dc.removeGrid("listServiceIds");
        final String[][] services = grid.getRawData();
        final DbHandle handle = DbHandle.borrowHandle(DataAccessType.READONLY, dc.getBooleanValue("suppress-sql-log", false));
        try {
            for (int i = 1; i < services.length; ++i) {
                final String[] serviceRow = services[i];
                final String serviceName = serviceRow[0];
                final String keyValue = (serviceRow.length > 1) ? serviceRow[1] : "";
                dc.addTextValue("serviceName", serviceName);
                dc.addTextValue("keyValue", keyValue);
                String gridName = serviceName;
                if (keyValue != null && keyValue.length() > 0) {
                    gridName = String.valueOf(gridName) + '_' + keyValue;
                }
                final SqlInterface sql = Sqls.getSqlTemplateOrNull(serviceName);
                if (sql != null) {
                    sql.execute(dc, handle, gridName, null);
                }
                else {
                    final ServiceInterface thisService = Services.getService(serviceName, dc);
                    if (thisService == null) {
                        final String errorText = "ERROR: list service " + serviceName + " is neither a sql nor a service.";
                        Spit.out(errorText);
                        dc.addError(errorText);
                    }
                    else {
                        thisService.execute(dc, handle);
                        if (!dc.hasGrid(gridName)) {
                            final String[] names = dc.getGridNames();
                            if (names.length == 0) {
                                Spit.out(String.valueOf(serviceName) + " is called as a list service, but it did not produce any grid");
                            }
                            else {
                                final Grid serviceDataGrid = dc.getGrid(names[0]);
                                dc.removeGrid(names[0]);
                                dc.addGrid(gridName, serviceDataGrid);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            dc.addError(e.getMessage());
        }
        DbHandle.returnHandle(handle);
    }
}
