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

import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class DataTypes
{
    String version;
    Map<String, AbstractDataType> dataTypes;
    private static DataTypes instance;
    
    static {
        DataTypes.instance = new DataTypes();
    }
    
    public DataTypes() {
        this.version = "1.0";
        this.dataTypes = new HashMap<String, AbstractDataType>();
    }
    
    public static AbstractDataType getDataType(final String dataTypeName, final DataCollection dc) {
        if (dataTypeName == null) {
            Spit.out("A request was made for a dataType with null as name. Default data type is returned.");
        }
        else {
            if (DataTypes.instance.dataTypes.containsKey(dataTypeName)) {
                return DataTypes.instance.dataTypes.get(dataTypeName);
            }
            Spit.out(String.valueOf(dataTypeName) + " does not exist. A default data type is assumed.");
        }
        return new TextDataType();
    }
    
    static void reload(final boolean removeExistingTypes) {
        if (DataTypes.instance == null || removeExistingTypes) {
            DataTypes.instance = new DataTypes();
        }
        load();
    }
    
    static synchronized void load() {
        try {
            final Map<String, Object> types = ResourceManager.loadFromFileOrFolder("dataTypes", "dataType");
            for (final String fileName : types.keySet()) {
                final Object obj = types.get(fileName);
                if (!(obj instanceof DataTypes)) {
                    Spit.out("dataTypes folder contains an xml that is not dataTypes. File ignored");
                }
                else {
                    final DataTypes dt = (DataTypes)obj;
                    DataTypes.instance.copyFrom(dt);
                }
            }
            Spit.out(String.valueOf(DataTypes.instance.dataTypes.size()) + " dataTypes loaded.");
        }
        catch (Exception e) {
            e.printStackTrace();
            Spit.out("Unable to load data types : " + e.getMessage());
        }
    }
    
    private void copyFrom(final DataTypes types) {
        for (final AbstractDataType dt : types.dataTypes.values()) {
            if (this.dataTypes.containsKey(dt.name)) {
                Spit.out("Error : " + dt.name + " is defined more than once as a dataType");
            }
            else {
                this.dataTypes.put(dt.name, dt);
            }
        }
    }
    
    public static Set<String> getDataTypeNames() {
        return DataTypes.instance.dataTypes.keySet();
    }
    
    static DataTypes getInstance() {
        return DataTypes.instance;
    }
    
    static void removeDataType(final AbstractDataType dataType) {
        if (DataTypes.instance == null) {
            return;
        }
        DataTypes.instance.dataTypes.remove(dataType.name);
    }
    
    static void addDataType(final AbstractDataType dataType) {
        if (DataTypes.instance == null) {
            DataTypes.instance = new DataTypes();
        }
        DataTypes.instance.dataTypes.put(dataType.name, dataType);
    }
    
    public void put(final AbstractDataType dataType) {
        this.dataTypes.put(dataType.name, dataType);
    }
}
