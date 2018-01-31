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

import java.util.HashMap;
import java.util.Map;

class ReportService implements ToBeInitializedInterface
{
    private static final String SERVICE_TABLE_NAME = "reportServiceIds";
    private static ReportService instance;
    CachedLists cachedLists;
    private Map<String, String> sqlsToBeCached;
    
    static {
        ReportService.instance = new ReportService();
        ReportService.instance = new ReportService();
    }
    
    ReportService() {
        this.cachedLists = new CachedLists();
        this.sqlsToBeCached = null;
    }
    
    static ReportService getService() {
        return ReportService.instance;
    }
    
    void execute(final DataCollection dc) throws ExilityException {
        final DbHandle handle = DbHandle.borrowHandle(DataAccessType.READONLY, dc.getBooleanValue("suppress-sql-log", false));
        final String[][] services = dc.getGrid("reportServiceIds").getRawData();
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
            final ServiceInterface service = Services.getService(serviceName, dc);
            if (service != null) {
                service.execute(dc, handle);
            }
            else {
                final SqlInterface sql = Sqls.getTemplate(serviceName, dc);
                if (sql != null) {
                    sql.execute(dc, handle, gridName, null);
                }
                else {
                    dc.addMessage("exilNoSuchReportService", serviceName);
                }
            }
        }
        DbHandle.returnHandle(handle);
    }
    
    static void addGrid(final String gridName, final String[][] grid) {
        if (ReportService.instance == null) {
            ReportService.instance = new ReportService();
        }
        ReportService.instance.cachedLists.grids.put(gridName, grid);
    }
    
    void loadGrids() {
    }
    
    static void reload() {
        ReportService.instance.loadGrids();
    }
    
    @Override
    public void initialize() {
        if (this.cachedLists.sqls != null) {
            this.sqlsToBeCached = new HashMap<String, String>();
            String[] sqls;
            for (int length = (sqls = this.cachedLists.sqls).length, i = 0; i < length; ++i) {
                final String sql = sqls[i];
                this.sqlsToBeCached.put(sql, sql);
            }
        }
    }
    
    class CachedLists
    {
        Map<String, String[][]> grids;
        String[] sqls;
        
        CachedLists() {
            this.grids = new HashMap<String, String[][]>();
        }
    }
}
