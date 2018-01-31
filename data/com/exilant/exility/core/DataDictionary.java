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

import java.io.OutputStream;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class DataDictionary
{
    private static DataDictionary instance;
    static String DEFAULT_ELEMENT_NAME;
    static Map<String, DataGroup> nonexistantGroups;
    static Map<String, DataElement> nonexistantElements;
    Map<String, DataGroup> groups;
    Map<String, DataElement> elements;
    
    static {
        DataDictionary.instance = new DataDictionary();
        DataDictionary.DEFAULT_ELEMENT_NAME = "text";
        DataDictionary.nonexistantGroups = new HashMap<String, DataGroup>();
        DataDictionary.nonexistantElements = new HashMap<String, DataElement>();
    }
    
    public DataDictionary() {
        this.groups = new HashMap<String, DataGroup>();
        this.elements = new HashMap<String, DataElement>();
    }
    
    static void reload(final boolean removeExistingElements) {
        if (DataDictionary.instance == null || removeExistingElements) {
            DataDictionary.instance = new DataDictionary();
        }
        load();
    }
    
    static synchronized void load() {
        try {
            final Map<String, Object> dictionaries = ResourceManager.loadFromFileOrFolder("dataDictionary", "dictionary");
            for (final String fileName : dictionaries.keySet()) {
                final Object obj = dictionaries.get(fileName);
                if (!(obj instanceof DataDictionary)) {
                    Spit.out("dataDictionary folder contains an xml that is not a dictionary. File ignored.");
                }
                else {
                    final DataDictionary d = (DataDictionary)obj;
                    DataDictionary.instance.copyFrom(d);
                }
            }
            Spit.out("**Final dictionary has " + DataDictionary.instance.groups.size() + " groups and " + DataDictionary.instance.elements.size() + " elements. ");
        }
        catch (Exception e) {
            Spit.out("Unable to load data dictionary with root folder at " + ResourceManager.getResourceFolder() + " : " + e.getMessage());
            Spit.out(e);
        }
    }
    
    public static DataElement getElement(final String name) {
        return DataDictionary.instance.elements.get(name);
    }
    
    public static DataElement getElement(final String groupName, final String elementName) {
        final DataGroup group = DataDictionary.instance.groups.get(groupName);
        if (group == null) {
            return null;
        }
        DataElement de = DataDictionary.instance.elements.get(elementName);
        if (de == null) {
            de = DataDictionary.instance.elements.get(String.valueOf(groupName) + AP.dataElementSeparator + elementName);
        }
        return de;
    }
    
    public static String getElementLabel(final String name) {
        final DataElement de = getElement(name);
        if (de == null) {
            return "";
        }
        return de.label;
    }
    
    public static AbstractDataType getDataType(final String name) {
        final AbstractDataType dt = getDataTypeOrNull(name);
        if (dt == null) {
            return new TextDataType();
        }
        return dt;
    }
    
    public static AbstractDataType getDataTypeOrNull(final String name) {
        AbstractDataType dt = null;
        final DataElement de = getElement(name);
        if (de != null) {
            dt = DataTypes.getDataType(de.dataType, null);
            if (dt != null) {
                return dt;
            }
        }
        return null;
    }
    
    public static DataValueType getValueType(final String name) {
        final AbstractDataType dt = getDataTypeOrNull(name);
        if (dt != null) {
            return dt.getValueType();
        }
        return DataValueType.TEXT;
    }
    
    public static DataValueType getValueTypeOrNull(final String name) {
        AbstractDataType dt = null;
        final DataElement de = getElement(name);
        if (de != null) {
            dt = DataTypes.getDataType(de.dataType, null);
            if (dt != null) {
                return dt.getValueType();
            }
        }
        return null;
    }
    
    static DataElement getDefaultElement(final String name) {
        final DataElement de = new DataElement();
        de.dataType = "text";
        de.label = name;
        de.name = name;
        return de;
    }
    
    public static DataGroup getGroup(final String groupName) {
        return getInstance().groups.get(groupName);
    }
    
    static DataDictionary getInstance() {
        return DataDictionary.instance;
    }
    
    public static void writeAll(final OutputStream writer) {
        try {
            final String tab = "\t";
            writer.write(new String("groupName\tname\tqualifiedName\tbusinessDescription\ttechnicalDescription\tisInternalElement\tdataType\tlistServiceName\t\n").getBytes());
            for (final DataGroup group : DataDictionary.instance.groups.values()) {
                for (final DataElement element : group.elements.values()) {
                    final String s = String.valueOf(group.name) + tab + element.name + tab + group.name + AP.dataElementSeparator + element.name + tab + element.businessDescription + tab + element.technicalDescription + tab + (element.isInternalElement ? "1" : "0") + tab + element.dataType + tab + element.listServiceName + '\n';
                    writer.write(s.getBytes());
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public static void createDefaultElement(final String elementName) {
        final DataElement element = getDefaultElement(elementName);
        addNonExistentElement(element);
    }
    
    public static void addNonExistentElement(final DataElement dElement) {
        final String currentName = dElement.name;
        final String[] currentNameParts = currentName.split("_");
        if (currentNameParts.length == 2) {
            DataGroup nonexistantGroup = null;
            if (!DataDictionary.nonexistantGroups.containsKey(currentNameParts[0])) {
                nonexistantGroup = new DataGroup();
                nonexistantGroup.name = currentNameParts[0];
                nonexistantGroup.label = currentNameParts[0];
                nonexistantGroup.technicalDescription = "Non existent group";
                dElement.name = currentNameParts[1];
                dElement.label = currentNameParts[1];
                nonexistantGroup.elements.put(currentNameParts[1], dElement);
            }
            else {
                nonexistantGroup = DataDictionary.nonexistantGroups.get(currentNameParts[0]);
                DataDictionary.nonexistantGroups.remove(currentNameParts[0]);
                if (!nonexistantGroup.elements.containsKey(currentNameParts[1])) {
                    dElement.name = currentNameParts[1];
                    dElement.label = currentNameParts[1];
                    nonexistantGroup.elements.put(currentNameParts[1], dElement);
                }
            }
            DataDictionary.nonexistantGroups.put(currentNameParts[0], nonexistantGroup);
        }
        else if (currentNameParts.length == 1 && !DataDictionary.nonexistantElements.containsKey(dElement.name)) {
            DataDictionary.nonexistantElements.put(dElement.name, dElement);
        }
    }
    
    public static void printNonExistentElements() {
        for (final DataGroup group : DataDictionary.nonexistantGroups.values()) {
            Spit.out("<dataGroup name=\"" + group.name + "\"    label=\"" + group.label + "\" technicalDescription=\"" + group.technicalDescription + "\" businessDescription=\"\">");
            Spit.out("\t<elements>");
            for (final DataElement element : group.elements.values()) {
                Spit.out("\t\t<dataElement  name=\"" + element.name + "\" dataType=\"" + element.dataType + "\" listServiceName=\"\" label=\"" + element.label + "\" businessDescription=\"\" technicalDescription=\"" + element.technicalDescription + "\"/>");
            }
            Spit.out("\t</elements>");
            Spit.out("</dataGroup>");
        }
        for (final DataElement element2 : DataDictionary.nonexistantElements.values()) {
            Spit.out("<dataElement  name=\"" + element2.name + "\" dataType=\"" + element2.dataType + "\" listServiceName=\"\" label=\"" + element2.label + "\" businessDescription=\"\" technicalDescription=\"" + element2.technicalDescription + "\"/>");
        }
    }
    
    public static void resetNonExistentElements() {
        DataDictionary.nonexistantGroups = new HashMap<String, DataGroup>();
        DataDictionary.nonexistantElements = new HashMap<String, DataElement>();
    }
    
    private void copyFrom(final DataDictionary d) {
        for (final DataGroup group : d.groups.values()) {
            final String groupName = group.name;
            this.groups.put(groupName, group);
            for (final DataElement element : group.elements.values()) {
                String elementName = element.name;
                final DataElement el = this.elements.get(elementName);
                if (el != null && el.dataType != null && !el.dataType.equals(element.dataType)) {
                    final AbstractDataType d2 = DataTypes.getDataType(element.dataType, null);
                    final AbstractDataType d3 = DataTypes.getDataType(el.dataType, null);
                    if (d2 != null && d3 != null && d2.getValueType() != d3.getValueType()) {
                        Spit.out("ERROR: " + elementName + " is defined in group " + groupName + " with dataType = " + element.dataType + " whose underlying value is " + d2.getValueType() + ". However, another group has defined it as " + el.dataType + " wich is " + d3.getValueType() + ". This is likley to result in some data error in your project.");
                    }
                }
                this.elements.put(elementName, element);
                if (!AP.uniqueNamesAcrossGroups) {
                    elementName = String.valueOf(groupName) + AP.dataElementSeparator + elementName;
                    this.elements.put(elementName, element);
                }
            }
        }
    }
    
    public static String[][] getAllLabels() {
        final Map<String, String> labels = new HashMap<String, String>();
        for (final DataElement de : DataDictionary.instance.elements.values()) {
            String thisDescription = "";
            if (de.businessDescription != null) {
                thisDescription = String.valueOf(thisDescription) + de.businessDescription;
            }
            if (de.technicalDescription != null) {
                thisDescription = String.valueOf(thisDescription) + de.technicalDescription;
            }
            final String desc = labels.get(de.label);
            if (desc == null) {
                thisDescription = String.valueOf(thisDescription) + desc;
            }
            labels.put(de.label, thisDescription);
        }
        final int nbrLabels = labels.size();
        final String[] arr = new String[nbrLabels];
        labels.keySet().toArray(arr);
        final String[][] allLabels = new String[nbrLabels + 1][2];
        allLabels[0][0] = "English";
        allLabels[0][1] = "Description";
        for (int i = 1; i < allLabels.length; ++i) {
            final String label = arr[i - 1];
            allLabels[i][0] = label;
            allLabels[i][1] = labels.get(label);
        }
        return allLabels;
    }
}
