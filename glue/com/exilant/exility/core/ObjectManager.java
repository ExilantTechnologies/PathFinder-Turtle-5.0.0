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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.OutputStream;
import java.io.FileOutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.regex.Pattern;
import java.util.Date;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.HashMap;

public class ObjectManager
{
    private static final String MY_PACKAGE_NAME;
    private static final String[] candidateKeyNames;
    private static final HashMap<String, String> classNames;
    private static final String[] HEADER_FOR_ATTRIBUTE_ARRAY;
    static final DecimalFormat decimalFormat;
    private static final String VALUE = "value";
    public static final String OBJECT_TYPE = "_type";
    private static final String ALL_ATTRIBUTES_NAME = "ALL_ATTRIBUTES";
    
    static {
        MY_PACKAGE_NAME = String.valueOf(ObjectManager.class.getPackage().getName()) + '.';
        candidateKeyNames = new String[] { "name", "key", "id", "value" };
        classNames = new HashMap<String, String>();
        HEADER_FOR_ATTRIBUTE_ARRAY = new String[] { "key", "fieldName", "value" };
        (decimalFormat = new DecimalFormat()).setMaximumFractionDigits(4);
        ObjectManager.decimalFormat.setMinimumFractionDigits(1);
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        ObjectManager.classNames.put("setValue", "setValueStep");
        ObjectManager.classNames.put("SetValueStep", "SetValue");
        ObjectManager.classNames.put("list", "listStep");
        ObjectManager.classNames.put("ListStep", "List");
        ObjectManager.classNames.put("appendToList", "appendToListStep");
        ObjectManager.classNames.put("AppendToListStep", "AppendToList");
        ObjectManager.classNames.put("grid", "gridStep");
        ObjectManager.classNames.put("GridStep", "Grid");
        ObjectManager.classNames.put("addColumn", "addColumnStep");
        ObjectManager.classNames.put("AddColumnStep", "AddColumn");
        ObjectManager.classNames.put("lookup", "lookupStep");
        ObjectManager.classNames.put("LookupStep", "Lookup");
        ObjectManager.classNames.put("filter", "filterStep");
        ObjectManager.classNames.put("FilterStep", "Filter");
        ObjectManager.classNames.put("stop", "stopStep");
        ObjectManager.classNames.put("StopStep", "Stop");
        ObjectManager.classNames.put("save", "saveStep");
        ObjectManager.classNames.put("SaveStep", "Save");
        ObjectManager.classNames.put("saveGrid", "saveGridStep");
        ObjectManager.classNames.put("SaveGridStep", "SaveGrid");
        ObjectManager.classNames.put("delete", "deleteStep");
        ObjectManager.classNames.put("DeleteStep", "Delete");
        ObjectManager.classNames.put("massUpdate", "massUpdateStep");
        ObjectManager.classNames.put("MassUpdateStep", "MassUpdate");
        ObjectManager.classNames.put("massDelete", "massDeleteStep");
        ObjectManager.classNames.put("MassDeleteStep", "MassDelete");
        ObjectManager.classNames.put("aggregate", "aggregateStep");
        ObjectManager.classNames.put("AggregateStep", "Aggregate");
    }
    
    public static Object createNew(final Class type) {
        try {
            return type.newInstance();
        }
        catch (Exception e) {
            Spit.out("ObjectManager : Unable to create Object for type " + type.getName() + ". " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object createNew(final String fullName) {
        try {
            final Class type = Class.forName(fullName);
            return type.newInstance();
        }
        catch (Exception e) {
            Spit.out("ObjectManager : Unable to create Object for type  " + fullName + ". " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object createNew(final String classLoaderName, final String packageName, final String className) {
        return createNew(String.valueOf(packageName) + '.' + className);
    }
    
    public static Object createNew(final String packageName, final String className) {
        return createNew(String.valueOf(packageName) + '.' + className);
    }
    
    private static Class getType(final String className, final boolean errorIfNotFound) {
        try {
            return Class.forName(String.valueOf(ObjectManager.MY_PACKAGE_NAME) + className);
        }
        catch (Exception e) {
            if (errorIfNotFound) {
                Spit.out("ERROR: Unable to create a class for component " + className + ". Possible that the xml being processed has invalid tags in it.");
            }
            return null;
        }
    }
    
    private static Field getField(final Class type, final String fieldName) {
        Class typ = type;
        while (!typ.equals(Object.class)) {
            try {
                return typ.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException ex) {
                typ = typ.getSuperclass();
            }
        }
        return null;
    }
    
    public static Map<String, Field> getAllFields(final Class type, final boolean getPrivateFieldsAlso) {
        final Map<String, Field> fields = new HashMap<String, Field>();
        for (Class typ = type; !typ.equals(Object.class); typ = typ.getSuperclass()) {
            Field[] declaredFields;
            for (int length = (declaredFields = typ.getDeclaredFields()).length, i = 0; i < length; ++i) {
                final Field field = declaredFields[i];
                if (!Modifier.isStatic(field.getModifiers())) {
                    if (getPrivateFieldsAlso || !Modifier.isPrivate(field.getModifiers())) {
                        fields.put(field.getName(), field);
                    }
                }
            }
        }
        return fields;
    }
    
    private static boolean toBeIgnored(final Field field) {
        final int mod = field.getModifiers();
        return Modifier.isStatic(mod) || Modifier.isPrivate(mod) || Modifier.isProtected(mod);
    }
    
    public static Object getFieldValue(final Object objekt, final String fieldName) {
        final Field field = getField(objekt.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(objekt);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static String getFieldValueAsString(final Object objekt, final String fieldName) {
        final Object value = getFieldValue(objekt, fieldName);
        if (value == null) {
            return "";
        }
        if (isValueType(objekt.getClass())) {
            return valueTypeToString(objekt);
        }
        return value.toString();
    }
    
    public static Map<String, Object> getAllFieldValues(final Object objekt) {
        final Map<String, Object> values = new HashMap<String, Object>();
        final Map<String, Field> fields = getAllFields(objekt.getClass(), true);
        for (final Field field : fields.values()) {
            if (toBeIgnored(field)) {
                continue;
            }
            field.setAccessible(true);
            try {
                values.put(field.getName(), field.get(objekt));
            }
            catch (Exception ex) {}
        }
        return values;
    }
    
    public static void setFieldValue(final Object objekt, final String fieldName, final Object fieldValue) {
        final Field field = getField(objekt.getClass(), fieldName);
        if (field == null) {
            return;
        }
        setFieldValue(objekt, field, fieldValue);
    }
    
    public static void setFieldValue(final Object objekt, final Field field, final Object fieldValue) {
        final Class fieldType = field.getType();
        final Class valueType = fieldValue.getClass();
        Object rightTypedValue = null;
        if (fieldType.isAssignableFrom(valueType)) {
            rightTypedValue = fieldValue;
        }
        else if (fieldValue instanceof String) {
            final String stringValue = ((String)fieldValue).trim();
            if (fieldType.isArray()) {
                rightTypedValue = parseArray(fieldType, stringValue);
            }
            else {
                rightTypedValue = parsePrimitive(fieldType, stringValue);
            }
        }
        if (rightTypedValue != null) {
            try {
                field.setAccessible(true);
                field.set(objekt, rightTypedValue);
                return;
            }
            catch (Exception ex) {}
        }
        Spit.out("Field " + field.getName() + " could not be assigned from a value of type " + fieldType.getName() + " with value = " + fieldValue.toString());
    }
    
    private static Object parseArray(final Class type, final String commaSeparatedvalue) {
        final Class elementType = type.getComponentType();
        final String[] vals = commaSeparatedvalue.split(",");
        final int n = vals.length;
        final Object arr = Array.newInstance(elementType, n);
        for (int i = 0; i < vals.length; ++i) {
            try {
                final Object o = parsePrimitive(elementType, vals[i].trim());
                Array.set(arr, i, o);
            }
            catch (Exception e) {
                return null;
            }
        }
        return arr;
    }
    
    private static Object parsePrimitive(final Class type, final String value) {
        if (type.equals(String.class)) {
            return value;
        }
        if (type.isPrimitive()) {
            if (type.equals(Integer.TYPE)) {
                return new Integer(value);
            }
            if (type.equals(Long.TYPE)) {
                return new Long(value);
            }
            if (type.equals(Short.TYPE)) {
                return new Short(value);
            }
            if (type.equals(Byte.TYPE)) {
                return new Byte(value);
            }
            if (type.equals(Character.TYPE)) {
                if (value.length() == 0) {
                    return new Integer(32);
                }
                return new Integer(value.toCharArray()[0]);
            }
            else if (type.equals(Boolean.TYPE)) {
                if (value.equalsIgnoreCase("true") || value.equals("1")) {
                    return new Boolean(true);
                }
                return new Boolean(false);
            }
            else {
                if (type.equals(Float.TYPE)) {
                    return new Float(value);
                }
                if (type.equals(Double.TYPE)) {
                    return new Double(value);
                }
                return null;
            }
        }
        else {
            if (type.isEnum()) {
                try {
                    return Enum.valueOf((Class<Object>)type, value.toUpperCase());
                }
                catch (IllegalArgumentException iaEx) {
                    return Enum.valueOf((Class<Object>)type, value);
                }
            }
            if (type.equals(Expression.class)) {
                final Expression expr = new Expression();
                try {
                    expr.setExpression(value);
                }
                catch (ExilityException e) {
                    Spit.out("invalid expression :" + e.getMessage());
                    e.printStackTrace();
                }
                return expr;
            }
            if (type.equals(Date.class)) {
                return DateUtility.parseDate(value);
            }
            if (type.equals(Pattern.class)) {
                return Pattern.compile(value);
            }
            Spit.out("Unable to parse value " + value + " into type " + type.getName());
            return null;
        }
    }
    
    public static Object deepCopy(final Object component) {
        final Class type = component.getClass();
        if (isValueType(type)) {
            return component;
        }
        if (type.isArray()) {
            final Class elementType = type.getComponentType();
            final int len = Array.getLength(component);
            final Object newArray = Array.newInstance(elementType, len);
            for (int i = 0; i < len; ++i) {
                Array.set(newArray, i, deepCopy(Array.get(component, i)));
            }
            return newArray;
        }
        Object newComponent = null;
        try {
            newComponent = type.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (Map.class.isAssignableFrom(type)) {
            final Map map = (Map)component;
            final Map newMap = (Map)newComponent;
            newMap.putAll(map);
            return newMap;
        }
        final Map<String, Field> fields = getAllFields(type, true);
        for (final String fiedName : fields.keySet()) {
            final Field field = fields.get(fiedName);
            field.setAccessible(true);
            try {
                field.set(newComponent, deepCopy(field.get(component)));
            }
            catch (Exception ex) {}
        }
        return newComponent;
    }
    
    private static boolean isValueType(final Class type) {
        return type.isPrimitive() || type.isEnum() || type.equals(String.class) || type.equals(Expression.class) || type.equals(Date.class) || type.equals(Pattern.class);
    }
    
    private static boolean isExilityType(final Class type) {
        return type.getPackage().getName().equals(ObjectManager.class.getPackage().getName());
    }
    
    @Deprecated
    public static Object fromXml(final String xml) {
        return fromXml(xml, null);
    }
    
    @Deprecated
    public static Object fromXml(final String xml, final Class rootObjectType) {
        Document document = null;
        try {
            final StringReader reader = new StringReader(xml);
            final InputSource source = new InputSource(reader);
            final DocumentBuilderFactory docuBuilder = DocumentBuilderFactory.newInstance();
            docuBuilder.setIgnoringComments(true);
            docuBuilder.setValidating(false);
            docuBuilder.setCoalescing(false);
            docuBuilder.setXIncludeAware(false);
            docuBuilder.setNamespaceAware(false);
            final DocumentBuilder builder = docuBuilder.newDocumentBuilder();
            document = builder.parse(source);
        }
        catch (SAXParseException e) {
            Spit.out("Error while parsing xml text. " + e.getMessage() + "\n At line " + e.getLineNumber() + " and column " + e.getColumnNumber());
            return null;
        }
        catch (Exception e2) {
            Spit.out("Error while reading resource file. " + e2.getMessage());
            return null;
        }
        final Element rootElement = document.getDocumentElement();
        removeTextChildren(rootElement);
        Class typ = rootObjectType;
        final String xmlClassName = toClassName(rootElement.getNodeName(), "");
        if (typ == null || !rootObjectType.getSimpleName().equals(xmlClassName)) {
            typ = getType(xmlClassName, true);
            if (typ == null) {
                return null;
            }
        }
        return fromElement(rootElement, typ);
    }
    
    public static Object fromXml(final File file) {
        return fromXml(file, null);
    }
    
    public static Object fromXml(final File file, final Class rootObjectType) {
        final Document document = getDocument(file);
        if (document == null) {
            return null;
        }
        final Element rootElement = document.getDocumentElement();
        removeTextChildren(rootElement);
        Class typ = rootObjectType;
        final String xmlClassName = toClassName(rootElement.getNodeName(), "");
        if (typ == null || !rootObjectType.getSimpleName().equals(xmlClassName)) {
            typ = getType(xmlClassName, true);
            if (typ == null) {
                return null;
            }
        }
        return fromElement(rootElement, typ);
    }
    
    static Document getDocument(final File file) {
        Document doc = null;
        final DocumentBuilderFactory docuBuilder = DocumentBuilderFactory.newInstance();
        docuBuilder.setIgnoringComments(true);
        docuBuilder.setValidating(false);
        docuBuilder.setCoalescing(false);
        docuBuilder.setXIncludeAware(false);
        docuBuilder.setNamespaceAware(false);
        InputStream ins = null;
        try {
            final DocumentBuilder builder = docuBuilder.newDocumentBuilder();
            ins = new FileInputStream(file);
            doc = builder.parse(ins);
        }
        catch (SAXParseException e) {
            Spit.out("Error while parsing xml text. " + e.getMessage() + "\n At line " + e.getLineNumber() + " and column " + e.getColumnNumber());
        }
        catch (Exception e2) {
            Spit.out("Error while reading resource file. " + e2.getMessage());
        }
        finally {
            if (ins != null) {
                try {
                    ins.close();
                }
                catch (IOException ex) {}
            }
        }
        if (ins != null) {
            try {
                ins.close();
            }
            catch (IOException ex2) {}
        }
        return doc;
    }
    
    private static String toClassName(final String tagName, final String parentTagName) {
        String className;
        if (parentTagName.equalsIgnoreCase("steps") && ObjectManager.classNames.containsKey(tagName)) {
            className = ObjectManager.classNames.get(tagName);
        }
        else {
            className = tagName;
        }
        char firstChar = className.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            firstChar = (char)('A' + firstChar - 'a');
            return String.valueOf(firstChar) + className.substring(1);
        }
        return className;
    }
    
    private static String toTagName(final String className, final int noOfInterfaces) {
        String tagName;
        if (noOfInterfaces == 0 && ObjectManager.classNames.containsKey(className)) {
            tagName = ObjectManager.classNames.get(className);
        }
        else {
            tagName = className;
        }
        char firstChar = tagName.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            firstChar = (char)('a' + firstChar - 'A');
            return String.valueOf(firstChar) + tagName.substring(1);
        }
        return tagName;
    }
    
    static Object fromElement(final Element element, final Class objectType) {
        if (isValueType(objectType)) {
            return getValueFromElement(element, objectType);
        }
        if (objectType.isArray()) {
            return getArrayFromElement(element, objectType.getComponentType());
        }
        if (Map.class.isAssignableFrom(objectType)) {
            final Map map = new HashMap();
            fillMapFromElement(element, map);
            return map;
        }
        final NodeList children = element.getChildNodes();
        final int n = children.getLength();
        if (!element.hasAttributes() && n == 1) {
            final Element childElement = (Element)children.item(0);
            final Class possibleType = getType(toClassName(childElement.getNodeName(), element.getNodeName()), false);
            if (possibleType != null && objectType.isAssignableFrom(possibleType)) {
                return fromElement(childElement, possibleType);
            }
        }
        final Object newObject = createNew(objectType);
        final Map<String, Field> fields = getAllFields(objectType, true);
        if (element.hasAttributes()) {
            final NamedNodeMap atts = element.getAttributes();
            for (int i = atts.getLength() - 1; i >= 0; --i) {
                final Attr att = (Attr)atts.item(i);
                final String fieldName = att.getName();
                if (fields.containsKey(fieldName)) {
                    setFieldValue(newObject, fields.get(fieldName), att.getValue());
                }
                else if (fieldName.indexOf(":") == -1 && !fieldName.equals("_type")) {
                    if (!fieldName.equals("xmlns")) {
                        Spit.out("XML Loading: " + fieldName + " is not a field in type " + objectType.getName());
                    }
                }
            }
        }
        for (int j = 0; j < n; ++j) {
            final Node child = children.item(j);
            if (child.getNodeType() == 1) {
                final String fieldName2 = child.getNodeName();
                if (!fields.containsKey(fieldName2)) {
                    if (fieldName2.indexOf(":") == -1 && !fieldName2.equals("_type") && !fieldName2.equals("xmlns")) {
                        Spit.out("XML Loading: " + fieldName2 + " is not a field in type " + objectType.getName());
                    }
                }
                else {
                    final Element childElement2 = (Element)child;
                    final Field field = fields.get(fieldName2);
                    Class fieldType = getElementType(childElement2);
                    if (fieldType == null) {
                        fieldType = field.getType();
                    }
                    final Object fieldValue = fromElement((Element)child, fieldType);
                    field.setAccessible(true);
                    try {
                        field.set(newObject, fieldValue);
                    }
                    catch (Exception ex) {}
                }
            }
        }
        if (newObject instanceof ToBeInitializedInterface) {
            ((ToBeInitializedInterface)newObject).initialize();
        }
        return newObject;
    }
    
    private static Class getElementType(final Element element) {
        final String objectTypeName = element.getAttribute("_type");
        if (objectTypeName == null || objectTypeName.length() == 0) {
            return null;
        }
        if (objectTypeName.equals("HashMap")) {
            return Map.class;
        }
        try {
            return Class.forName(String.valueOf(ObjectManager.MY_PACKAGE_NAME) + objectTypeName);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private static Object[] getArrayFromElement(final Element element, final Class memberType) {
        final NodeList childNodes = element.getChildNodes();
        final int n = childNodes.getLength();
        int nbrActualNodes = 0;
        for (int i = 0; i < n; ++i) {
            final Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == 1) {
                ++nbrActualNodes;
            }
        }
        final Object[] array = (Object[])Array.newInstance(memberType, nbrActualNodes);
        Class childType = null;
        int elementIdx = 0;
        for (int j = 0; j < n; ++j) {
            final Node childNode2 = childNodes.item(j);
            if (childNode2.getNodeType() == 1) {
                final Element childElement = (Element)childNode2;
                childType = getElementType(childElement);
                if (childType == null) {
                    childType = getType(toClassName(childElement.getNodeName(), element.getNodeName()), true);
                }
                if (childType != null) {
                    final Object childObject = fromElement(childElement, childType);
                    array[elementIdx] = childObject;
                    ++elementIdx;
                }
            }
        }
        return array;
    }
    
    private static void fillMapFromElement(final Element element, final Map map) {
        final NodeList childNodes = element.getChildNodes();
        final int n = childNodes.getLength();
        Class childType = null;
        for (int i = 0; i < n; ++i) {
            final Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == 1) {
                final Element childElement = (Element)childNode;
                childType = getElementType(childElement);
                if (childType == null) {
                    childType = getType(toClassName(childElement.getNodeName(), element.getNodeName()), true);
                }
                if (childType != null) {
                    final Object childObject = fromElement(childElement, childType);
                    final String key = getCandidateKey(childElement);
                    if (map.containsKey(key)) {
                        Spit.out("ERROR : " + key + " is a duplicate entry in " + element.getNodeName());
                    }
                    map.put(key, childObject);
                }
            }
        }
    }
    
    private static Object getValueFromElement(final Element element, final Class type) {
        String val = element.getAttribute("value");
        if (val == null || val.length() == 0) {
            final Node aNode = element.getFirstChild();
            if (aNode != null) {
                final short nodeType = aNode.getNodeType();
                if (nodeType == 4 || nodeType == 3) {
                    val = aNode.getTextContent();
                }
            }
        }
        if (val == null) {
            val = "";
        }
        return parsePrimitive(type, val);
    }
    
    private static String getCandidateKey(final Element element) {
        String[] candidateKeyNames;
        for (int length = (candidateKeyNames = ObjectManager.candidateKeyNames).length, i = 0; i < length; ++i) {
            final String keyName = candidateKeyNames[i];
            final String key = element.getAttribute(keyName);
            if (key != null && key.length() != 0) {
                return key;
            }
        }
        return null;
    }
    
    public static String toXml(final Object rootObject) {
        String xml = "";
        try {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document document = builder.newDocument();
            final String elementName = toTagName(rootObject.getClass().getSimpleName(), rootObject.getClass().getInterfaces().length);
            final Element rootElement = document.createElement(elementName);
            toElement(rootObject, rootElement, document);
            document.appendChild(rootElement);
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            final DOMSource source = new DOMSource(document);
            final StringWriter writer = new StringWriter();
            final StreamResult stream = new StreamResult(writer);
            transformer.transform(source, stream);
            xml = writer.getBuffer().toString();
        }
        catch (Exception e) {
            Spit.out("Error outputting xml for " + rootObject.getClass().getName() + ". " + e.getMessage());
        }
        return xml;
    }
    
    public static void toXmlFile(final Object rootObject, final File file) {
        try {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document document = builder.newDocument();
            final String elementName = toTagName(rootObject.getClass().getSimpleName(), rootObject.getClass().getInterfaces().length);
            final Element rootElement = document.createElement(elementName);
            toElement(rootObject, rootElement, document);
            document.appendChild(rootElement);
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            final DOMSource source = new DOMSource(document);
            final OutputStream out = new FileOutputStream(file);
            final StreamResult stream = new StreamResult(out);
            transformer.transform(source, stream);
            out.close();
        }
        catch (Exception e) {
            Spit.out("Error outputting xml for " + rootObject.getClass().getName() + ". " + e.getMessage());
        }
    }
    
    private static void toElements(final Object[] children, final Element element, final Document document) {
        for (final Object child : children) {
            if (child != null) {
                final String tagName = toTagName(child.getClass().getSimpleName(), child.getClass().getInterfaces().length);
                final Element childElement = document.createElement(tagName);
                element.appendChild(childElement);
                toElement(child, childElement, document);
            }
        }
    }
    
    private static void toElements(final Map children, final Element element, final Document document) {
        for (final Object id : children.keySet()) {
            final Object value = children.get(id);
            final String tagName = toTagName(value.getClass().getSimpleName(), value.getClass().getInterfaces().length);
            final Element childElement = document.createElement(tagName);
            element.appendChild(childElement);
            toElement(value, childElement, document);
        }
    }
    
    private static void toElement(final Object objekt, final Element element, final Document document) {
        final Class objectType = objekt.getClass();
        element.setAttribute("_type", objectType.getSimpleName());
        if (isValueType(objectType)) {
            final String val = valueToString(objekt);
            if (val != null) {
                element.setAttribute("value", val);
            }
            return;
        }
        if (objectType.isArray()) {
            final Class elementType = objectType.getComponentType();
            if (isValueType(elementType)) {
                element.setAttribute("value", valueArrayToString(objekt));
                return;
            }
            toElements((Object[])objekt, element, document);
        }
        else {
            if (Map.class.isAssignableFrom(objectType)) {
                toElements((Map)objekt, element, document);
                return;
            }
            for (final Field field : getAllFields(objectType, true).values()) {
                if (toBeIgnored(field)) {
                    continue;
                }
                final String fieldName = field.getName();
                field.setAccessible(true);
                Object fieldValue = null;
                try {
                    fieldValue = field.get(objekt);
                }
                catch (Exception e) {
                    continue;
                }
                if (fieldValue == null) {
                    continue;
                }
                final Class type = field.getType();
                if (isValueType(type)) {
                    final String valu = valueToString(fieldValue);
                    if (valu == null) {
                        continue;
                    }
                    element.setAttribute(fieldName, valu);
                }
                else {
                    if (type.isArray()) {
                        final Class elementType2 = type.getComponentType();
                        if (isValueType(elementType2)) {
                            final String arv = valueArrayToString(fieldValue);
                            if (arv.length() > 0) {
                                element.setAttribute(fieldName, arv);
                                continue;
                            }
                            continue;
                        }
                    }
                    final Element newElement = document.createElement(fieldName);
                    element.appendChild(newElement);
                    toElement(fieldValue, newElement, document);
                }
            }
        }
    }
    
    private static String valueToString(final Object value) {
        if (value instanceof Date) {
            return DateUtility.formatDate((Date)value);
        }
        final String s = value.toString();
        if (s.length() == 0) {
            return null;
        }
        if (value instanceof String) {
            return s;
        }
        if (s.equals("0") || s.equals("false")) {
            return null;
        }
        return s;
    }
    
    private static String valueTypeToString(final Object value) {
        if (value instanceof Date) {
            return DateUtility.formatDate((Date)value);
        }
        return value.toString();
    }
    
    private static String valueArrayToString(final Object array) {
        if (array == null) {
            return "";
        }
        final int n = Array.getLength(array);
        if (n == 0) {
            return "";
        }
        String commaSeparatedvalue = valueTypeToString(Array.get(array, 0));
        for (int i = 1; i < n; ++i) {
            commaSeparatedvalue = String.valueOf(commaSeparatedvalue) + ',' + valueTypeToString(Array.get(array, i));
        }
        return commaSeparatedvalue;
    }
    
    public static void serializePrimitiveAttributes(final Object objekt, final String objectName, final StringBuilder sbf) {
        final Class objectType = objekt.getClass();
        final Map<String, Field> fields = getAllFields(objectType, true);
        for (final Field field : fields.values()) {
            if (toBeIgnored(field)) {
                continue;
            }
            final String fieldName = field.getName();
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(objekt);
            }
            catch (Exception e) {
                continue;
            }
            if (fieldValue == null) {
                continue;
            }
            final String val = valueToSerializedString(fieldValue, true);
            if (val == null) {
                continue;
            }
            sbf.append('\n').append(objectName).append('.').append(fieldName).append(" = ").append(val);
        }
    }
    
    public static String valueToSerializedString(final Object value, final boolean putQuotesIfRequired) {
        if (value == null) {
            return null;
        }
        final Class type = value.getClass();
        if (!isValueType(type)) {
            return null;
        }
        boolean quotesRequired = false;
        String val = value.toString();
        if (val.length() == 0) {
            return null;
        }
        if (value instanceof String) {
            quotesRequired = true;
        }
        else if (value instanceof Boolean) {
            if (val.equals("false")) {
                return null;
            }
            val = "true";
        }
        else if (value instanceof Integer) {
            final int ival = (int)value;
            if (ival == 0 || ival == Integer.MAX_VALUE || ival == Integer.MIN_VALUE) {
                return null;
            }
        }
        else if (value instanceof Double) {
            final double dbl = (double)value;
            if (dbl == 0.0 || dbl == Double.MAX_VALUE || dbl == Double.MIN_VALUE) {
                return null;
            }
            val = ObjectManager.decimalFormat.format(dbl);
        }
        else {
            if (!(value instanceof Date)) {
                return null;
            }
            val = DateUtility.formatDate((Date)value);
        }
        if (putQuotesIfRequired && quotesRequired) {
            val = String.valueOf('\"') + val + '\"';
        }
        return val;
    }
    
    private static void removeTextChildren(final Node element) {
        final NodeList children = element.getChildNodes();
        final int n = children.getLength();
        for (int i = n - 1; i >= 0; --i) {
            final Node child = children.item(i);
            final short nodeType = child.getNodeType();
            if (nodeType == 3 || nodeType == 8) {
                element.removeChild(child);
            }
            else if (nodeType == 1) {
                removeTextChildren(child);
            }
        }
    }
    
    public static void toDc(final Object object, final DataCollection dc) {
        final Class objectType = object.getClass();
        final Map<String, Field> fields = getAllFields(objectType, false);
        dc.addTextValue("_type", objectType.getName());
        for (final Field field : fields.values()) {
            try {
                field.setAccessible(true);
                final Object value = field.get(object);
                final Class fieldType = field.getType();
                final String fieldName = field.getName();
                if (value == null) {
                    if (fieldType.equals(String.class)) {
                        dc.addTextValue(fieldName, "");
                    }
                    else {
                        Spit.out(String.valueOf(fieldName) + " is null and hence no value is extracted.");
                    }
                }
                else if (fieldType.equals(Boolean.TYPE)) {
                    if (value) {
                        dc.addTextValue(fieldName, "1");
                    }
                    else {
                        dc.addTextValue(fieldName, "0");
                    }
                }
                else if (isValueType(fieldType)) {
                    dc.addTextValue(fieldName, valueTypeToString(value));
                }
                else if (fieldType.isArray()) {
                    final Class arrayType = fieldType.getComponentType();
                    final Object[] arr = (Object[])value;
                    if (arr.length == 0) {
                        Spit.out("Array " + fieldName + " has no entries. It is not added to dc");
                    }
                    else if (isValueType(arrayType)) {
                        dc.addTextValue(fieldName, valueArrayToString(arr));
                    }
                    else {
                        dc.addGrid(fieldName, getAttributes(arr));
                    }
                }
                else if (value instanceof Map) {
                    final Map map = (Map)value;
                    dc.addGrid(fieldName, getAttributes(map));
                }
                else if (value instanceof List) {
                    final List list = (List)value;
                    dc.addGrid(fieldName, getAttributes(list));
                }
                else {
                    final Object[] objects = { value };
                    dc.addGrid(fieldName, getAttributes(objects));
                }
            }
            catch (Exception e) {
                Spit.out("Error while adding attributes of object to dc\n " + e.getMessage());
            }
        }
    }
    
    private static String[] getAllFieldNames(final Class type) {
        final Field field = getField(type, "ALL_ATTRIBUTES");
        if (field != null) {
            final Class fieldType = field.getType();
            if (fieldType.isArray() && fieldType.getComponentType().equals(String.class)) {
                try {
                    return (String[])field.get(null);
                }
                catch (Exception e) {
                    Spit.out("Unable to get ALL_ATTRIBUTES as a static attribute of " + fieldType.getSimpleName());
                }
            }
        }
        final Map<String, Field> allFields = getAllFields(type, false);
        final String[] keys = allFields.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        return keys;
    }
    
    private static String valueOf(final Object object) {
        final Class type = object.getClass();
        if (isValueType(type)) {
            return valueTypeToString(object);
        }
        if (type.isArray()) {
            final Class arrayType = type.getComponentType();
            final Object[] arr = (Object[])object;
            if (isValueType(arrayType)) {
                return valueArrayToString(arr);
            }
        }
        return object.toString();
    }
    
    public static Object createFromDc(final DataCollection dc) {
        Object objekt = null;
        final String typeName = dc.getTextValue("_type", "");
        if (typeName.length() == 0) {
            dc.addMessage("exilityError", "Design error: client did not send _type");
            return null;
        }
        objekt = createNew(String.valueOf(ObjectManager.MY_PACKAGE_NAME) + typeName);
        if (objekt == null) {
            dc.addMessage("exilityError", "Design error: Could not instantiate class " + typeName);
            return null;
        }
        fromDc(objekt, dc);
        return objekt;
    }
    
    public static void fromDc(final Object objekt, final DataCollection dc) {
        final Class objectType = objekt.getClass();
        final Map<String, Field> fields = getAllFields(objectType, false);
        for (final Field field : fields.values()) {
            final Class fieldType = field.getType();
            final String fieldName = field.getName();
            String textValue = null;
            try {
                field.setAccessible(true);
                if (dc.hasValue(fieldName)) {
                    textValue = dc.getTextValue(fieldName, "");
                    if (textValue.length() == 0) {
                        continue;
                    }
                    if (isValueType(fieldType)) {
                        setFieldValue(objekt, field, textValue);
                        continue;
                    }
                    if (fieldType.isArray()) {
                        final Class arrayType = fieldType.getComponentType();
                        if (isValueType(arrayType)) {
                            setFieldValue(objekt, field, textValue);
                            continue;
                        }
                        Spit.out("Found a value of " + textValue + " for field " + field.getName() + ". But this field is an array, and hence value is expected in a grid instead.");
                    }
                    else {
                        if (!isExilityType(fieldType)) {
                            Spit.out("Attribute " + fieldName + " could not be assiged a value of " + textValue);
                            continue;
                        }
                        final Object newObject = createNew(textValue);
                        if (newObject == null) {
                            Spit.out("Attribute " + fieldName + " is of type " + textValue + ". Unable to create an instance of this  not be assiged a value of " + textValue);
                            continue;
                        }
                        fromDc(newObject, dc);
                        continue;
                    }
                }
                final Grid grid = dc.getGrid(fieldName);
                if (grid == null) {
                    continue;
                }
                if (grid.getNumberOfRows() == 0) {
                    continue;
                }
                Class type = fieldType;
                if (fieldType.isArray()) {
                    type = fieldType.getComponentType();
                }
                final Object value = fromGrid(grid.getRawData(), type);
                if (value == null) {
                    Spit.out(String.valueOf(fieldName) + " could not be assigend value from its grid with the same name.");
                }
                else if (fieldType.isArray()) {
                    field.set(objekt, value);
                }
                else if (!value.getClass().isArray()) {
                    Spit.out("Unabel to convert grid " + fieldName + " into an array of objects.");
                }
                else {
                    Object fieldValue = field.get(objekt);
                    if (fieldValue == null) {
                        final int modifiers = fieldType.getModifiers();
                        if (Modifier.isAbstract(modifiers) || Modifier.isAbstract(modifiers)) {
                            Spit.out(String.valueOf(fieldName) + " is not a concrete class, and there is no concrete class name is found to create an instance for this field.");
                            continue;
                        }
                        fieldValue = createNew(fieldType);
                        field.set(objekt, fieldValue);
                    }
                    final Object[] objects = (Object[])value;
                    if (fieldType.isAssignableFrom(Map.class)) {
                        final Map map = (Map)fieldValue;
                        Object[] array;
                        for (int length = (array = objects).length, i = 0; i < length; ++i) {
                            final Object obj = array[i];
                            map.put(getKeyValue(obj), obj);
                        }
                    }
                    else if (fieldType.isAssignableFrom(List.class)) {
                        final List list = (List)createNew(fieldType);
                        Object[] array2;
                        for (int length2 = (array2 = objects).length, j = 0; j < length2; ++j) {
                            final Object obj = array2[j];
                            list.add(obj);
                        }
                    }
                    else {
                        field.set(objekt, objects[0]);
                    }
                }
            }
            catch (Exception e) {
                Spit.out("ERROR : While creating Object instance from dc " + e.getMessage());
                Spit.out(e);
            }
        }
        if (objekt instanceof ToBeInitializedInterface) {
            ((ToBeInitializedInterface)objekt).initialize();
        }
    }
    
    private static Object fromGrid(final String[][] data, final Class type) {
        final int n = data.length;
        final Object arre = Array.newInstance(type, n - 1);
        final String[] columnNames = data[0];
        int typeIdx = 0;
        String[] array;
        for (int length = (array = columnNames).length, k = 0; k < length; ++k) {
            final String columnName = array[k];
            if ("_type".equals(columnName)) {
                break;
            }
            ++typeIdx;
        }
        Class commonType = null;
        if (typeIdx >= columnNames.length) {
            Spit.out("Grid does not have a column with name _type. Common type " + type.getSimpleName() + " will be used for all rows.");
            commonType = type;
        }
        for (int i = 1; i < n; ++i) {
            final String[] aRow = data[i];
            Object object;
            if (commonType == null) {
                object = createNew(getType(aRow[typeIdx], true));
                if (object == null) {
                    Spit.out("Unable to create an instance for row number " + i + " with " + aRow[typeIdx] + " as its type.");
                    continue;
                }
            }
            else {
                object = createNew(commonType);
                if (object == null) {
                    Spit.out("Unable to create an instance for row number " + i + " with " + type.getName() + " as its type.");
                    continue;
                }
            }
            Array.set(arre, i - 1, object);
            for (int j = 0; j < columnNames.length; ++j) {
                if (j != typeIdx) {
                    final String colValue = aRow[j];
                    if (colValue != null && colValue.length() > 0) {
                        setFieldValue(object, columnNames[j], colValue);
                    }
                }
            }
        }
        return arre;
    }
    
    private static String getKeyValue(final Object object) {
        String[] candidateKeyNames;
        for (int length = (candidateKeyNames = ObjectManager.candidateKeyNames).length, i = 0; i < length; ++i) {
            final String key = candidateKeyNames[i];
            final String keyValue = getFieldValueAsString(object, key);
            if (keyValue != null && keyValue.length() > 0) {
                return keyValue;
            }
        }
        return null;
    }
    
    public static void addAttributesToDc(final Object object, final DataCollection dc, final String[] attributes) {
        final Class type = object.getClass();
        final String typeName = type.getSimpleName();
        dc.addTextValue("_type", typeName);
        for (final String attName : attributes) {
            String valAsText = "";
            try {
                valAsText = valueOf(type.getField(attName).get(object));
            }
            catch (Exception e) {
                Spit.out(String.valueOf(attName) + " can not be found in an instance of " + typeName + ". Attribute not added to dc.");
            }
            dc.addTextValue(attName, valAsText);
        }
    }
    
    public static String[][] getAttributes(final Map<String, ?> map) {
        final int n = map.size();
        if (n == 0) {
            return new String[0][0];
        }
        final String[] sortedKeys = map.keySet().toArray(new String[0]);
        Arrays.sort(sortedKeys);
        final Object[] objects = new Object[n];
        int i = 0;
        String[] array;
        for (int length = (array = sortedKeys).length, j = 0; j < length; ++j) {
            final String key = array[j];
            objects[i] = map.get(key);
            ++i;
        }
        return getAttributes(objects);
    }
    
    public static String[][] getAttributes(final List list) {
        final Object[] objects = new Object[list.size()];
        int i = 0;
        for (final Object object : list) {
            objects[i] = object;
            ++i;
        }
        return getAttributes(objects);
    }
    
    public static String[][] getAttributes(final Object[] objects) {
        final int nbrObjects = objects.length;
        if (nbrObjects == 0) {
            return new String[0][0];
        }
        final String[] attributes = getAllFieldNames(objects[0].getClass());
        final int nbrAttrs = attributes.length;
        final String[][] grid = new String[nbrObjects + 1][];
        final String[] header = new String[nbrAttrs + 1];
        header[0] = "_type";
        int i = 1;
        String[] array;
        for (int length = (array = attributes).length, k = 0; k < length; ++k) {
            final String s = array[k];
            header[i] = s;
            ++i;
        }
        grid[0] = header;
        i = 1;
        for (final Object objekt : objects) {
            final String[] row = new String[nbrAttrs + 1];
            grid[i] = row;
            ++i;
            final Class type = objekt.getClass();
            row[0] = type.getSimpleName();
            int j = 1;
            String[] array2;
            for (int length3 = (array2 = attributes).length, n = 0; n < length3; ++n) {
                final String attName = array2[n];
                row[j] = getFieldValueAsString(objekt, attName);
                ++j;
            }
        }
        return grid;
    }
    
    public static void toDc(final Object[] objects, final DataCollection dc, final String keyPrefix, final String tableName) {
        final List<String[]> entries = new ArrayList<String[]>();
        entries.add(ObjectManager.HEADER_FOR_ATTRIBUTE_ARRAY);
        final int nbrObjects = objects.length;
        if (nbrObjects > 0) {
            int keyIdx = 1;
            for (final Object object : objects) {
                final String id = String.valueOf(keyPrefix) + keyIdx;
                ++keyIdx;
                addAttributesAsRows(entries, object, id, null);
            }
        }
        final String[][] rawData = entries.toArray(new String[0][0]);
        dc.addGridAfterCheckingInDictionary(tableName, rawData);
    }
    
    private static void addAttributesAsRows(final List<String[]> rows, final Object object, final String id, final Map<String, Field> parentFields) {
        final Map<String, Field> fields = getAllFields(object.getClass(), false);
        for (final String fieldName : fields.keySet()) {
            if (parentFields != null && parentFields.containsKey(fieldName)) {
                Spit.out("ERROR: Exility resource that is an attribute of a parent resource has a field name " + fieldName + ". This name clashes with that of teh parent resource. Current design of loading/unloadin fails in this case");
            }
            else {
                final Field field = fields.get(fieldName);
                final Class fieldType = field.getType();
                if (isValueType(fieldType)) {
                    final String[] row = { id, fieldName, getFieldValueAsString(object, fieldName) };
                    rows.add(row);
                }
                else {
                    final Object fieldValue = getFieldValue(object, fieldName);
                    if (fieldType.isArray()) {
                        if (isValueType(fieldType.getComponentType())) {
                            final String arv = valueArrayToString(fieldValue);
                            if (arv.length() <= 0) {
                                continue;
                            }
                            final String[] row2 = { id, fieldName, arv };
                            rows.add(row2);
                        }
                        else {
                            Spit.out("Ojbect Manager does not know how to extract value of field '" + fieldName + "' as it is an array of " + fieldType.getComponentType());
                        }
                    }
                    else if (isExilityType(fieldType)) {
                        if (parentFields != null) {
                            Spit.out("Object Manager is not designed to extract attributes if on Exility Resource which that is an attribute of another Exility resource, has an Exility resource as its attribute");
                        }
                        else {
                            Object childObject;
                            try {
                                childObject = field.get(object);
                            }
                            catch (Exception e) {
                                Spit.out(String.valueOf(fieldName) + " could not be extracted for id " + id + " error: " + e.getMessage());
                                continue;
                            }
                            if (childObject == null) {
                                Spit.out(String.valueOf(fieldName) + " is null for id " + id);
                            }
                            else {
                                final String[] row2 = { id, fieldName, childObject.getClass().getName() };
                                rows.add(row2);
                                addAttributesAsRows(rows, childObject, id, fields);
                            }
                        }
                    }
                    else {
                        Spit.out(String.valueOf(fieldType.getName()) + " is a field that can not be loaded/extracted by ObjectManager");
                    }
                }
            }
        }
    }
}
