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

import java.util.Hashtable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.sql.ResultSetMetaData;
import java.sql.CallableStatement;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.Properties;
import javax.naming.InitialContext;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import javax.sql.DataSource;

public class DbHandle
{
    private static final String TABLE_SELECT_SQL = "select TABLE_NAME as \"tableName\", TABLE_TYPE as \"tableType\" from INFORMATION_SCHEMA.TABLES ORDER BY ";
    private static final String COLUMN_SELECT_SQL = "select COLUMN_NAME as \"columnName\", DATA_TYPE  as \"sqlDataType\", IS_NULLABLE  as \"isNullable\", COLUMN_DEFAULT  as \"defaultValue\", CHARACTER_MAXIMUM_LENGTH  as \"size\", NUMERIC_PRECISION  as \"precision\", NUMERIC_SCALE as \"scale\" from INFORMATION_SCHEMA.COLUMNS order by ORDINAL_POSITION where TABLE_NAME = '";
    private static final int FOR_INPUT = 0;
    private static final int FOR_OUTPUT = 1;
    private static final int FOR_INPUT_AND_OUTPUT = 2;
    private static DataSource dataSource;
    private static DataSource auditDataSource;
    private static boolean oracleIsUsed;
    private static boolean dbIsAudited;
    private static String connectionString;
    private static String auditConnectionString;
    private static String nlsDateFormat;
    private Connection con;
    private Connection auditcon;
    private boolean suppressSqlLog;
    private final boolean updatesAllowed;
    private final DataAccessType dataAccessType;
    private static final Map<Integer, DataValueType> JdbcToExilTypes;
    
    static {
        DbHandle.oracleIsUsed = (AP.dbDriver.contains("oracle") || AP.connectionString.contains("oracle"));
        DbHandle.dbIsAudited = (AP.auditConnectionString != null && AP.auditConnectionString.length() > 0);
        JdbcToExilTypes = new HashMap<Integer, DataValueType>();
        setTypeMaps();
        initiateJdbcDriver();
    }
    
    private static void setTypeMaps() {
        DbHandle.JdbcToExilTypes.put(new Integer(91), DataValueType.DATE);
        DbHandle.JdbcToExilTypes.put(new Integer(92), DataValueType.DATE);
        DbHandle.JdbcToExilTypes.put(new Integer(93), DataValueType.DATE);
        DbHandle.JdbcToExilTypes.put(new Integer(16), DataValueType.BOOLEAN);
        DbHandle.JdbcToExilTypes.put(new Integer(-7), DataValueType.BOOLEAN);
        DbHandle.JdbcToExilTypes.put(new Integer(3), DataValueType.DECIMAL);
        DbHandle.JdbcToExilTypes.put(new Integer(2), DataValueType.DECIMAL);
        DbHandle.JdbcToExilTypes.put(new Integer(8), DataValueType.DECIMAL);
        DbHandle.JdbcToExilTypes.put(new Integer(6), DataValueType.DECIMAL);
        DbHandle.JdbcToExilTypes.put(new Integer(7), DataValueType.DECIMAL);
        DbHandle.JdbcToExilTypes.put(new Integer(-6), DataValueType.INTEGRAL);
        DbHandle.JdbcToExilTypes.put(new Integer(5), DataValueType.INTEGRAL);
        DbHandle.JdbcToExilTypes.put(new Integer(4), DataValueType.INTEGRAL);
        DbHandle.JdbcToExilTypes.put(new Integer(-5), DataValueType.INTEGRAL);
        DbHandle.JdbcToExilTypes.put(new Integer(-8), DataValueType.INTEGRAL);
        DbHandle.JdbcToExilTypes.put(new Integer(1), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(12), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(-16), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(-9), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(-15), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(-15), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(2005), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(2004), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(-1), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(2011), DataValueType.TEXT);
        DbHandle.JdbcToExilTypes.put(new Integer(0), DataValueType.NULL);
    }
    
    static void initiateJdbcDriver() {
        DbHandle.nlsDateFormat = AP.nlsDateFormat;
        DbHandle.connectionString = AP.connectionString;
        DbHandle.auditConnectionString = AP.auditConnectionString;
        try {
            if (AP.dataSource != null) {
                final InitialContext exilInitContext = new InitialContext();
                DbHandle.dataSource = (DataSource)exilInitContext.lookup("java:/" + AP.dataSource);
                if (AP.auditDataSource != null) {
                    DbHandle.auditDataSource = (DataSource)exilInitContext.lookup("java:/" + AP.auditDataSource);
                    DbHandle.dbIsAudited = true;
                }
            }
            else if (AP.dbDriver == null || AP.connectionString == null) {
                Spit.out("ERROR: Project is not set-up properly. No database oerations are possible");
            }
            else {
                Class.forName(AP.dbDriver);
                if (DbHandle.auditConnectionString != null) {
                    DbHandle.dbIsAudited = true;
                }
            }
            DbHandle.oracleIsUsed = ((AP.dbDriver != null && AP.dbDriver.contains("oracle")) || (DbHandle.connectionString != null && DbHandle.connectionString.contains("oracle")));
        }
        catch (Exception e) {
            Spit.out("Unable to initialize jdbc driver.");
            Spit.out(e);
        }
    }
    
    @Deprecated
    public static DbHandle borrowHandle(final boolean forUpdate) throws ExilityException {
        return new DbHandle(forUpdate ? DataAccessType.READWRITE : DataAccessType.READONLY);
    }
    
    public static DbHandle borrowHandle(final DataAccessType accessType) throws ExilityException {
        return new DbHandle(accessType);
    }
    
    public static DbHandle borrowHandle(final DataAccessType accessType, final boolean suppressLogging) throws ExilityException {
        final DbHandle handle = new DbHandle(accessType);
        handle.suppressSqlLog = suppressLogging;
        return handle;
    }
    
    public static void returnHandle(final DbHandle handle) {
        if (handle == null) {
            return;
        }
        try {
            if (handle.con != null) {
                handle.con.close();
            }
            if (handle.auditcon != null) {
                handle.auditcon.close();
            }
        }
        catch (Exception ex) {}
    }
    
    public DbHandle(final DataAccessType accessType) {
        this.con = null;
        this.auditcon = null;
        this.suppressSqlLog = false;
        this.dataAccessType = accessType;
        this.updatesAllowed = (accessType == DataAccessType.READWRITE || accessType == DataAccessType.AUTOCOMMIT);
        if (accessType != DataAccessType.NONE) {
            this.openConnection();
        }
    }
    
    public boolean updateIsAllowed() {
        return this.updatesAllowed;
    }
    
    @Deprecated
    public Connection getConnection() {
        return this.con;
    }
    
    private void openConnection() {
        try {
            if (DbHandle.dataSource != null) {
                (this.con = DbHandle.dataSource.getConnection()).setReadOnly(!this.updatesAllowed);
                if (this.updatesAllowed && DbHandle.dbIsAudited) {
                    (this.auditcon = DbHandle.auditDataSource.getConnection()).setReadOnly(false);
                }
            }
            else {
                final Properties connProps = new Properties();
                if (!DbHandle.oracleIsUsed) {
                    ((Hashtable<String, String>)connProps).put("SetBigStringTryClob", "true");
                }
                (this.con = DriverManager.getConnection(DbHandle.connectionString, connProps)).setReadOnly(!this.updatesAllowed);
                if (DbHandle.dbIsAudited) {
                    (this.auditcon = DriverManager.getConnection(DbHandle.auditConnectionString, connProps)).setReadOnly(false);
                }
            }
            if (this.updatesAllowed) {
                this.con.setAutoCommit(this.dataAccessType == DataAccessType.AUTOCOMMIT);
                if (this.auditcon != null) {
                    this.auditcon.setAutoCommit(this.dataAccessType == DataAccessType.AUTOCOMMIT);
                }
                if (DbHandle.oracleIsUsed) {
                    final String nlsformat = "ALTER SESSION SET NLS_DATE_FORMAT = '" + DbHandle.nlsDateFormat + "'";
                    final Statement statement = this.con.createStatement();
                    statement.execute(nlsformat);
                    if (this.auditcon != null) {
                        final Statement auditstatement = this.auditcon.createStatement();
                        auditstatement.execute(nlsformat);
                    }
                    statement.close();
                }
                else {
                    this.con.setTransactionIsolation(1);
                    this.auditcon.setTransactionIsolation(1);
                }
            }
        }
        catch (Exception e) {
            Spit.out("Connection failed. Please check your database connection settings and network settings");
            Spit.out(e);
        }
    }
    
    boolean isDummy() {
        return this.con == null;
    }
    
    void beginTransaction() {
    }
    
    void commit() throws ExilityException {
        try {
            if (this.con != null) {
                this.con.commit();
            }
            if (this.auditcon != null) {
                this.auditcon.commit();
            }
        }
        catch (SQLException e) {
            throw new ExilityException();
        }
    }
    
    void startAutoCommit() throws ExilityException {
        try {
            if (this.con != null) {
                this.con.setAutoCommit(true);
            }
            if (this.auditcon != null) {
                this.auditcon.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            throw new ExilityException();
        }
    }
    
    void stopAutoCommit() throws ExilityException {
        try {
            if (this.con != null) {
                this.con.setAutoCommit(false);
            }
            if (this.auditcon != null) {
                this.auditcon.setAutoCommit(false);
            }
        }
        catch (SQLException e) {
            throw new ExilityException();
        }
    }
    
    void rollback() throws ExilityException {
        try {
            if (this.con != null) {
                this.con.rollback();
            }
            if (this.auditcon != null) {
                this.auditcon.rollback();
            }
        }
        catch (SQLException e) {
            throw new ExilityException();
        }
    }
    
    public Object extractSingleField(final String sqlText) throws ExilityException {
        if (!this.suppressSqlLog) {
            Spit.out(sqlText);
        }
        if (this.isDummy()) {
            return "";
        }
        Object result = null;
        try {
            final Statement statement = this.con.createStatement();
            final ResultSet rs = statement.executeQuery(sqlText);
            if (rs.next()) {
                result = rs.getObject(1);
            }
            rs.getStatement().close();
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
        return result;
    }
    
    public int extractSingleRow(final String sqlText, final Map<String, Value> values, final Sql sql, final String prefix) throws ExilityException {
        if (!this.suppressSqlLog) {
            Spit.out(sqlText);
        }
        if (this.isDummy()) {
            return 0;
        }
        try {
            final ResultSet rs = this.getRs(sqlText, false);
            if (rs == null) {
                return 0;
            }
            String[] columnNames = null;
            DataValueType[] dataValueTypes = null;
            if (sql != null) {
                columnNames = sql.getColumnNames();
                dataValueTypes = sql.getValueTypes();
            }
            this.rsToCollection(rs, columnNames, dataValueTypes, values, sql, prefix);
            rs.getStatement().close();
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
        return 1;
    }
    
    public int extractSingleRow(final String sqlText, final Map<String, Value> values, final String prefix) throws ExilityException {
        return this.extractSingleRow(sqlText, values, null, prefix);
    }
    
    public Grid extractToGrid(final String sqlText, final String gridName, final Grid gridToBeAppendedTo) throws ExilityException {
        return this.extractToGrid(sqlText, null, gridName, gridToBeAppendedTo);
    }
    
    public Grid extractToGrid(final String sqlText, final Sql sql, final String gridName, final Grid gridToBeAppendedTo) throws ExilityException {
        if (!this.suppressSqlLog) {
            Spit.out(sqlText);
        }
        if (this.isDummy()) {
            return new Grid();
        }
        try {
            final ResultSet rs = this.getRs(sqlText, true);
            final String[] columnNames = (String[])((sql == null) ? null : sql.getColumnNames());
            final DataValueType[] dataValueTypes = (DataValueType[])((sql == null) ? null : sql.getValueTypes());
            final Grid grid = this.rsToGrid(rs, columnNames, dataValueTypes, sql, gridName, gridToBeAppendedTo);
            Spit.out(String.valueOf(grid.getNumberOfRows()) + " rows extracted.");
            rs.getStatement().close();
            return grid;
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
    }
    
    public int execute(final String sqlText, final boolean asAudit) throws ExilityException {
        if (!this.suppressSqlLog) {
            Spit.out(sqlText);
        }
        if (this.isDummy()) {
            return 0;
        }
        int n;
        try {
            final Statement statement = asAudit ? this.auditcon.createStatement() : this.con.createStatement();
            n = statement.executeUpdate(sqlText);
            statement.close();
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
        Spit.out(String.valueOf(n) + " rows affected.");
        return n;
    }
    
    public int executeBatch(final String[] sqlTexts, final boolean asAudit) throws ExilityException {
        if (sqlTexts.length == 0 || this.isDummy()) {
            return 0;
        }
        try {
            final Statement statement = asAudit ? this.auditcon.createStatement() : this.con.createStatement();
            if (AP.commandTimeOutTime != 0) {
                statement.setQueryTimeout(AP.commandTimeOutTime);
            }
            for (final String Sqltext : sqlTexts) {
                if (!this.suppressSqlLog) {
                    Spit.out(Sqltext);
                }
                statement.addBatch(Sqltext);
            }
            final int[] nbrs = statement.executeBatch();
            int total = 0;
            int[] array;
            for (int length2 = (array = nbrs).length, j = 0; j < length2; ++j) {
                final int n = array[j];
                total += n;
            }
            statement.close();
            Spit.out(String.valueOf(total) + " rows affected.");
            return total;
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
    }
    
    public int executePreparedStatement(final String sqlText, final List<Value> values, final boolean asAudit) throws ExilityException {
        if (!this.suppressSqlLog) {
            Spit.out(sqlText);
        }
        if (this.isDummy()) {
            return 0;
        }
        try {
            final PreparedStatement statement = asAudit ? this.auditcon.prepareStatement(sqlText) : this.con.prepareStatement(sqlText);
            int fieldAt = 1;
            for (final Value value : values) {
                value.addToPrepearedStatement(statement, fieldAt);
                Spit.out(" value added = " + value.getTextValue());
                ++fieldAt;
            }
            final int n = statement.executeUpdate();
            statement.close();
            Spit.out(String.valueOf(n) + " rows affected.");
            return n;
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
    }
    
    public int extractFromPreparedStatement(final String sqlText, final List<Value> values, final String[] namesToExtract, final String prefix, final DataValueType[] valueTypes, final DataCollection dc, final String gridName, final Sql template) throws ExilityException {
        if (!this.suppressSqlLog) {
            Spit.out(sqlText);
        }
        if (this.isDummy()) {
            return 0;
        }
        try {
            final PreparedStatement statement = this.con.prepareStatement(sqlText);
            int fieldAt = 1;
            for (final Value value : values) {
                value.addToPrepearedStatement(statement, fieldAt);
                Spit.out(" value added = " + value.getTextValue());
                ++fieldAt;
            }
            int nbrRows = 0;
            if (!statement.execute()) {
                Spit.out("Sql has not returned results. Probably it is an update sql");
            }
            else {
                final ResultSet rs = statement.getResultSet();
                if (gridName != null) {
                    final Grid grid = this.rsToGrid(rs, namesToExtract, valueTypes, template, gridName, null);
                    dc.addGrid(gridName, grid);
                    nbrRows = grid.getNumberOfRows();
                }
                else if (rs.next()) {
                    this.rsToCollection(rs, namesToExtract, valueTypes, dc.values, template, prefix);
                    nbrRows = 1;
                }
            }
            statement.close();
            return nbrRows;
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
    }
    
    public int executePreparedStatementBatch(final String sqlText, final List<ValueList> values, final boolean asAudit, final boolean[] selector) throws ExilityException {
        if (!this.suppressSqlLog) {
            Spit.out(sqlText);
        }
        if (this.isDummy()) {
            return 0;
        }
        try {
            final PreparedStatement statement = asAudit ? this.auditcon.prepareStatement(sqlText) : this.con.prepareStatement(sqlText);
            if (AP.commandTimeOutTime != 0) {
                statement.setQueryTimeout(AP.commandTimeOutTime);
            }
            final int nbrRows = values.get(0).length();
            int nbrAdded = 0;
            int nbrRowsAfftected = 0;
            final StringBuilder forSpitting = new StringBuilder();
            for (int i = 0; i < nbrRows; ++i) {
                if (selector == null || selector[i]) {
                    int colId = 1;
                    if (!this.suppressSqlLog) {
                        forSpitting.append("row:" + (i + 1));
                    }
                    for (final ValueList list : values) {
                        final Value value = list.getValue(i);
                        if (!this.suppressSqlLog) {
                            forSpitting.append(String.valueOf(value.getTextValue()) + ',');
                        }
                        value.addToPrepearedStatement(statement, colId);
                        ++colId;
                    }
                    statement.addBatch();
                    if (!this.suppressSqlLog) {
                        forSpitting.setLength(forSpitting.length() - 1);
                        Spit.out(forSpitting.toString());
                        forSpitting.setLength(0);
                    }
                    ++nbrAdded;
                }
            }
            if (nbrAdded == 0) {
                Spit.out(" No rows selected for execution of above SQL");
            }
            else {
                final int[] rowsAffetced = statement.executeBatch();
                int[] array;
                for (int length = (array = rowsAffetced).length, k = 0; k < length; ++k) {
                    final int j = array[k];
                    nbrRowsAfftected += j;
                }
            }
            Spit.out(String.valueOf(nbrRowsAfftected) + " rows affetced.");
            statement.close();
            return nbrRowsAfftected;
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
    }
    
    public int executeSP(final String spName, final Parameter[] inParams, final Parameter[] inoutParams, final Parameter[] outParams, final DataCollection dc) throws ExilityException {
        if (this.isDummy()) {
            return 0;
        }
        final StringBuilder sbf = new StringBuilder("{call ");
        sbf.append(spName);
        int n = 0;
        if (inParams != null) {
            n += inParams.length;
        }
        if (inoutParams != null) {
            n += inoutParams.length;
        }
        if (outParams != null) {
            n += outParams.length;
        }
        if (n == 0) {
            sbf.append("()}");
        }
        else {
            sbf.append("(?");
            --n;
            while (n > 0) {
                sbf.append(", ?");
                --n;
            }
            sbf.append(")}");
        }
        final String sql = sbf.toString();
        Spit.out("Store procedure :\n" + sql);
        try {
            final CallableStatement statement = this.con.prepareCall(sql);
            if (AP.commandTimeOutTime != 0) {
                statement.setQueryTimeout(AP.commandTimeOutTime);
            }
            int curIndex = 1;
            if (inParams != null && inParams.length > 0) {
                curIndex = this.addParams(statement, dc, inParams, curIndex, 0);
            }
            if (inoutParams != null && inoutParams.length > 0) {
                curIndex = this.addParams(statement, dc, inoutParams, curIndex, 2);
            }
            if (outParams != null && outParams.length > 0) {
                curIndex = this.addParams(statement, dc, outParams, curIndex, 1);
            }
            final boolean retValue = statement.execute();
            curIndex = ((inParams == null) ? 1 : (inParams.length + 1));
            if (inoutParams != null && inoutParams.length > 0) {
                curIndex = this.extractOutput(statement, dc, inoutParams, curIndex);
            }
            if (outParams != null && outParams.length > 0) {
                this.extractOutput(statement, dc, outParams, curIndex);
            }
            statement.close();
            return retValue ? 1 : 0;
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
    }
    
    private int extractOutput(final CallableStatement statement, final DataCollection dc, final Parameter[] parameters, final int startExtractingAt) throws SQLException {
        int n = startExtractingAt;
        for (final Parameter p : parameters) {
            final String name = p.name;
            final AbstractDataType dt = DataDictionary.getDataType(p.dataElementName);
            final Value value = dt.extractFromStoredProcedure(statement, n);
            if (value != null && !value.isNull()) {
                dc.addValue(name, value);
            }
            ++n;
        }
        return n;
    }
    
    private int addParams(final CallableStatement statement, final DataCollection dc, final Parameter[] parameters, final int startAddingAt, final int inOut) throws ExilityException, SQLException {
        int n = startAddingAt;
        for (final Parameter p : parameters) {
            final String name = p.name;
            final AbstractDataType dt = DataDictionary.getDataType(p.dataElementName);
            Value value = null;
            if (inOut != 1) {
                value = dc.getValue(name);
                if (value == null || value.isNull()) {
                    if (p.defaultValue != null) {
                        value = Value.newValue(p.defaultValue, dt.getValueType());
                        if (value == null) {
                            dc.addMessage("exilityInvalidDataFormat", p.name, p.defaultValue, dt.getValueType().toString());
                            value = new NullValue(dt.getValueType());
                        }
                    }
                    else if (p.isOptional) {
                        value = new NullValue(dt.getValueType());
                    }
                    else {
                        dc.raiseException("exilNoParamValue", name);
                    }
                }
                dt.addInputToStoredProcedure(statement, n, value);
                if (!this.suppressSqlLog) {
                    Spit.out("Added in-param " + name + " with value = " + value + " at : " + n);
                }
            }
            if (inOut != 0) {
                dt.addOutputToStoredProcedure(statement, n);
                if (!this.suppressSqlLog) {
                    Spit.out("Added out-param " + name + " at : " + n);
                }
            }
            ++n;
        }
        return n;
    }
    
    private ColumnInfo getColumnInfo(final ResultSet rs) throws SQLException {
        final ResultSetMetaData md = rs.getMetaData();
        final int columnCount = md.getColumnCount();
        final String[] columnNames = new String[columnCount];
        final DataValueType[] valueTypes = new DataValueType[columnCount];
        for (int m = 0; m < columnCount; ++m) {
            final int mplus1 = m + 1;
            String name = md.getColumnLabel(mplus1);
            if (name == null || name.length() == 0) {
                name = md.getColumnName(mplus1);
            }
            columnNames[m] = name;
            final int jdbcType = md.getColumnType(mplus1);
            DataValueType exilityType = DataValueType.TEXT;
            if (jdbcType == 2 || jdbcType == 3) {
                if (md.getScale(mplus1) == 0) {
                    exilityType = DataValueType.INTEGRAL;
                }
                else {
                    exilityType = DataValueType.DECIMAL;
                }
            }
            else {
                exilityType = getExilType(name, jdbcType);
            }
            valueTypes[m] = exilityType;
        }
        final ColumnInfo info = new ColumnInfo();
        info.valueTypes = valueTypes;
        info.columnNames = columnNames;
        return info;
    }
    
    private void rsToCollection(final ResultSet rs, final String[] names, final DataValueType[] valueTypes, final Map<String, Value> values, final Sql template, final String prefix) throws SQLException {
        String[] localNames = names;
        DataValueType[] localTypes = valueTypes;
        if (names == null) {
            final ColumnInfo info = this.getColumnInfo(rs);
            localNames = info.columnNames;
            localTypes = info.valueTypes;
            if (template != null) {
                template.setColumnNames(names);
                template.setValueTypes(valueTypes);
            }
        }
        if (prefix == null) {
            for (int m = 0; m < localNames.length; ++m) {
                values.put(localNames[m], this.getValue(rs, m + 1, localTypes[m]));
            }
        }
        else {
            for (int m = 0; m < localNames.length; ++m) {
                values.put(String.valueOf(prefix) + localNames[m], this.getValue(rs, m + 1, localTypes[m]));
            }
        }
    }
    
    private ResultSet getRs(final String sql, final boolean doNotCheckForData) throws SQLException {
        final Statement statement = this.con.createStatement();
        if (AP.commandTimeOutTime != 0) {
            statement.setQueryTimeout(AP.commandTimeOutTime);
        }
        final ResultSet rs = statement.executeQuery(sql);
        if (doNotCheckForData || rs.next()) {
            return rs;
        }
        statement.close();
        return null;
    }
    
    private Grid rsToGrid(final ResultSet rs, final String[] names, final DataValueType[] valueTypes, final Sql template, final String gridName, final Grid gridToBeAppendedTo) throws SQLException {
        final List<Value[]> values = new ArrayList<Value[]>();
        String[] localNames = names;
        DataValueType[] localTypes = valueTypes;
        if (names == null) {
            final ColumnInfo info = this.getColumnInfo(rs);
            localNames = info.columnNames;
            localTypes = info.valueTypes;
            if (template != null) {
                template.setColumnNames(names);
                template.setValueTypes(valueTypes);
            }
        }
        while (rs.next()) {
            final Value[] aRow = new Value[localNames.length];
            for (int m = 0; m < aRow.length; ++m) {
                aRow[m] = this.getValue(rs, m + 1, localTypes[m]);
            }
            values.add(aRow);
        }
        if (gridToBeAppendedTo == null) {
            final Grid grid = new Grid(gridName);
            grid.setValues(localNames, localTypes, values, null);
            return grid;
        }
        gridToBeAppendedTo.appendValues(values);
        return gridToBeAppendedTo;
    }
    
    public static DataValueType getExilType(final String eleName, final int jdbcType) {
        final DataValueType typeInDb = DbHandle.JdbcToExilTypes.get(new Integer(jdbcType));
        if (typeInDb == null) {
            Spit.out("ERROR : Got a sql type " + jdbcType + " that is not mapped to exility type for [" + eleName + "]. Please report to Exility Support team.");
        }
        final DataValueType typeInDictionary = DataDictionary.getValueTypeOrNull(eleName);
        if (typeInDictionary == null) {
            if (typeInDb == null) {
                Spit.out("We could not determine data type for " + eleName + ". We will treat it as text.");
                return DataValueType.TEXT;
            }
            Spit.out("No entry found in data dictionary for " + eleName + ". its data type is inferred based on rdbms metadata");
            return typeInDb;
        }
        else {
            if (jdbcType == 93 && typeInDictionary == DataValueType.TIMESTAMP) {
                return typeInDictionary;
            }
            if (typeInDictionary != typeInDb && (jdbcType != 93 || typeInDictionary != DataValueType.TIMESTAMP) && (jdbcType != 1 || typeInDictionary != DataValueType.BOOLEAN)) {
                Spit.out("Warning: Element " + eleName + " is defined as " + typeInDictionary + " in data dictionary, but it is defined as a " + typeInDb + " in the database.[" + typeInDb + "]  type is assumed.");
            }
            return typeInDb;
        }
    }
    
    private Value getValue(final ResultSet rs, final int idx, final DataValueType type) throws SQLException {
        Value value = null;
        switch (type) {
            case DATE: {
                value = Value.newValue(rs.getDate(idx));
                break;
            }
            case TIMESTAMP: {
                value = Value.newTimeStampValue(rs.getTimestamp(idx));
                break;
            }
            case BOOLEAN: {
                value = Value.newValue(rs.getBoolean(idx));
                break;
            }
            case DECIMAL: {
                value = Value.newValue(rs.getDouble(idx));
                break;
            }
            case INTEGRAL: {
                value = Value.newValue(rs.getLong(idx));
                break;
            }
            default: {
                value = Value.newValue(rs.getString(idx));
                break;
            }
        }
        if (rs.wasNull()) {
            return new NullValue(value.getValueType());
        }
        return value;
    }
    
    public static Grid getAllTables() throws ExilityException {
        final DbHandle handle = borrowHandle(DataAccessType.READONLY);
        final Grid grid = handle.extractToGrid("select TABLE_NAME as \"tableName\", TABLE_TYPE as \"tableType\" from INFORMATION_SCHEMA.TABLES ORDER BY ", "tables", null);
        returnHandle(handle);
        return grid;
    }
    
    public static Grid getAllColumns(final String tableName) throws ExilityException {
        final DbHandle handle = borrowHandle(DataAccessType.READONLY);
        final Grid grid = handle.extractToGrid("select COLUMN_NAME as \"columnName\", DATA_TYPE  as \"sqlDataType\", IS_NULLABLE  as \"isNullable\", COLUMN_DEFAULT  as \"defaultValue\", CHARACTER_MAXIMUM_LENGTH  as \"size\", NUMERIC_PRECISION  as \"precision\", NUMERIC_SCALE as \"scale\" from INFORMATION_SCHEMA.COLUMNS order by ORDINAL_POSITION where TABLE_NAME = '" + tableName + "'", "columns", null);
        returnHandle(handle);
        return grid;
    }
    
    public int extractSingleRowFromSp(final String sqlText, final List<Value> inputValues, final Map<String, Value> values, final String prefix) throws ExilityException {
        if (!this.suppressSqlLog) {
            Spit.out(sqlText);
        }
        try {
            final ResultSet rs = this.getRsFromSp(sqlText, inputValues, false);
            if (rs == null) {
                return 0;
            }
            this.rsToCollection(rs, null, null, values, null, prefix);
            rs.getStatement().close();
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
        return 1;
    }
    
    public Grid extractToGridFromSp(final String sqlText, final List<Value> values, final String gridName, final Grid gridToBeAppendedTo) throws ExilityException {
        if (!this.suppressSqlLog) {
            Spit.out(sqlText);
        }
        try {
            final ResultSet rs = this.getRsFromSp(sqlText, values, true);
            final Grid grid = this.rsToGrid(rs, null, null, null, gridName, gridToBeAppendedTo);
            Spit.out(String.valueOf(grid.getNumberOfRows()) + " rows extracted.");
            rs.getStatement().close();
            return grid;
        }
        catch (SQLException e) {
            throw new ExilityException(e);
        }
    }
    
    private ResultSet getRsFromSp(final String sqlText, final List<Value> values, final boolean doNotCheckForData) throws SQLException {
        final CallableStatement statement = this.con.prepareCall(sqlText);
        if (AP.commandTimeOutTime != 0) {
            statement.setQueryTimeout(AP.commandTimeOutTime);
        }
        final int colId = 1;
        for (final Value value : values) {
            value.addToPrepearedStatement(statement, colId);
        }
        final ResultSet rs = statement.executeQuery();
        if (doNotCheckForData || rs.next()) {
            return rs;
        }
        statement.close();
        return null;
    }
    
    public boolean getOutputParamsFromDb(final Sql sql) throws ExilityException {
        final DataCollection dc = new DataCollection();
        SqlParameter[] inputParameters;
        for (int length = (inputParameters = sql.inputParameters).length, i = 0; i < length; ++i) {
            final SqlParameter p = inputParameters[i];
            p.putTestValues(dc);
        }
        final String sqlText = sql.getSql(dc);
        try {
            final ResultSet rs = this.getRs(sqlText, true);
            final ColumnInfo info = this.getColumnInfo(rs);
            rs.getStatement().close();
            sql.setColumnNames(info.columnNames);
            sql.setValueTypes(info.valueTypes);
            return true;
        }
        catch (SQLException e) {
            Spit.out("Unable to get mets data for sql " + sql.name + " Error : " + e.getMessage());
            return false;
        }
    }
    
    public void callServiceForEachRow(final Sql sql, final DataCollection dc, final ServiceInterface service) throws ExilityException {
        if (this.isDummy()) {
            return;
        }
        final String sqlText = sql.getSql(dc);
        DbHandle updater = borrowHandle(DataAccessType.READWRITE);
        try {
            final Statement statement = this.con.createStatement();
            final ResultSet rs = statement.executeQuery(sqlText);
            updater = borrowHandle(DataAccessType.READWRITE);
            while (rs.next()) {
                this.rsToCollection(rs, sql.getColumnNames(), sql.getValueTypes(), dc.values, sql, null);
                updater.beginTransaction();
                try {
                    service.execute(dc, updater);
                }
                catch (Exception e) {
                    dc.addError("Error while executing batch service " + service.getName() + ". " + e.getMessage());
                }
                if (dc.hasError()) {
                    updater.rollback();
                }
                else {
                    updater.commit();
                }
                dc.zapMessages();
            }
            statement.close();
        }
        catch (SQLException e2) {
            throw new ExilityException(e2);
        }
        finally {
            returnHandle(updater);
        }
        returnHandle(updater);
    }
}
