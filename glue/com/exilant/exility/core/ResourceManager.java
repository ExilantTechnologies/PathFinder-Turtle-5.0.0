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
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.Vector;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.FileInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager
{
    private static final String PROPERTY_NAME = "exility.resourceFolder";
    private static final String ENV_NAME = "EXILITY_RESOURCE_FOLDER";
    private static Map<String, String> resourceTypes;
    private static String resourceFolder;
    private static String rootFolder;
    private static String appFileName;
    
    static {
        (ResourceManager.resourceTypes = new HashMap<String, String>()).put("workflow", ".xls");
        ResourceManager.resourceFolder = null;
        ResourceManager.rootFolder = null;
        ResourceManager.appFileName = null;
    }
    
    @Deprecated
    public static void setResourceFolder(final String folderName) {
        loadAllResources(null, folderName);
    }
    
    public static void setRootFolder(final String folderName) {
        ResourceManager.rootFolder = folderName;
    }
    
    public static String getRootFolderName() {
        return ResourceManager.rootFolder;
    }
    
    public static String getResourceFolder() {
        return ResourceManager.resourceFolder;
    }
    
    private static boolean trySettingResourceFolder() {
        Spit.out("Resource folder was not set by a startup servlet. Locating resource folder : looking for property with name exility.resourceFolder");
        ResourceManager.resourceFolder = System.getProperty("exility.resourceFolder");
        if (ResourceManager.resourceFolder == null || ResourceManager.resourceFolder.length() == 0) {
            Spit.out("Property not found, trying environment variable EXILITY_RESOURCE_FOLDER");
            ResourceManager.resourceFolder = System.getenv("EXILITY_RESOURCE_FOLDER");
        }
        if (ResourceManager.resourceFolder == null || ResourceManager.resourceFolder.length() == 0) {
            Spit.out("ERROR: Unable to locate resource folder. You should set either property exility.resourceFolder or environment variable EXILITY_RESOURCE_FOLDER");
            Spit.out("Alternately, edit web.xml in WEB-INF folder and add a context-param with name resource-folder and value that points to the absolute pth of your resource folder.");
            Spit.out("Exility Engine will be cranky ;-)");
            return false;
        }
        if (!ResourceManager.resourceFolder.endsWith("/") && !ResourceManager.resourceFolder.endsWith("\\")) {
            ResourceManager.resourceFolder = String.valueOf(ResourceManager.resourceFolder) + '/';
        }
        FileUtility.setBasePath(new File(ResourceManager.resourceFolder).getParent());
        return true;
    }
    
    public static String readResourceNotUsedAnyMore(final String resourceName) {
        if (ResourceManager.resourceFolder == null && !trySettingResourceFolder()) {
            return null;
        }
        final String resourceFullName = String.valueOf(ResourceManager.resourceFolder) + resourceName.replace('.', '/') + ".xml";
        return readFile(resourceFullName);
    }
    
    public static String readFile(final String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Spit.out(String.valueOf(fileName) + " does not exist. trying in resource folder");
            file = new File(String.valueOf(getResourceFolder()) + fileName);
            if (!file.exists()) {
                Spit.out(String.valueOf(getResourceFolder()) + fileName + " does not exist in resource folder.");
                return null;
            }
        }
        return readFile(file);
    }
    
    public static String readFile(final File file) {
        try {
            final int n = (int)file.length();
            final char[] buf = new char[n];
            final Reader reader = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
            reader.read(buf);
            reader.close();
            return new String(buf);
        }
        catch (Exception e) {
            Spit.out("Error reading resource " + file.getName() + ". " + e.getMessage());
            return null;
        }
    }
    
    public static File getFile(final String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return file;
        }
        Spit.out(String.valueOf(fileName) + " does not exist. trying in resource folder ");
        String tryName = String.valueOf(getResourceFolder()) + fileName;
        file = new File(tryName);
        if (file.exists()) {
            return file;
        }
        Spit.out(String.valueOf(tryName) + " does not exist. trying in root folder ");
        tryName = String.valueOf(getRootFolderName()) + fileName;
        file = new File(tryName);
        if (file.exists()) {
            return file;
        }
        Spit.out("Unable to locate " + fileName + ". Resource will not be loaded.");
        return null;
    }
    
    public static Object loadResource(final String resourceName, final Class resourceObjectType) {
        if (ResourceManager.resourceFolder == null) {
            Spit.out("Resource folder is not set for this project. ResourceManager.loadAllResources() should be called");
            return null;
        }
        String resourceFullName = String.valueOf(ResourceManager.resourceFolder) + resourceName.replace('.', '/');
        File file = null;
        final int firstDelimiter = resourceName.indexOf(46);
        if (firstDelimiter > 0) {
            final String resType = resourceName.substring(0, firstDelimiter);
            Spit.out("About to load a resource of type " + resType);
            final String extn = ResourceManager.resourceTypes.get(resType);
            if (extn != null) {
                resourceFullName = String.valueOf(resourceFullName) + extn;
                final DataCollection dc = new DataCollection();
                final Object object = ObjectManager.createNew(resourceObjectType);
                XlxUtil.getInstance().extract(resourceFullName, dc, true);
                ObjectManager.fromDc(object, dc);
                return object;
            }
        }
        resourceFullName = String.valueOf(resourceFullName) + ".xml";
        file = getFile(resourceFullName);
        if (file == null) {
            return null;
        }
        return ObjectManager.fromXml(file);
    }
    
    public static String translateResourceFileName(final String resourceName) {
        final String resourceFullName = String.valueOf(ResourceManager.resourceFolder) + resourceName.replace('.', '/') + ".xml";
        return resourceFullName;
    }
    
    public static Object loadResourceFromFile(final String fileName, final Class resourceObjectType) {
        final File file = getFile(fileName);
        if (file == null) {
            return null;
        }
        return ObjectManager.fromXml(file, resourceObjectType);
    }
    
    public static String getResourceFullName(final String name) {
        String fullName = null;
        Field classField = null;
        final ClassLoader loader = ClassLoader.getSystemClassLoader();
        try {
            classField = ClassLoader.class.getDeclaredField("classes");
            if (classField.getType() != Vector.class) {
                throw new RuntimeException("not of type java.util.Vector: " + classField.getType().getName());
            }
            classField.setAccessible(true);
            final Vector classes = (Vector)classField.get(loader);
            for (final Object curObject : classes) {
                final Class curClass = (Class)curObject;
                if (curClass.getName().endsWith(name)) {
                    fullName = curClass.getCanonicalName();
                }
            }
        }
        catch (Exception e) {
            Spit.out("Load error: " + name + " could not be obtained. " + e.getMessage());
            Spit.out(e);
            return null;
        }
        return fullName;
    }
    
    public static void saveResource(final String resourceName, final Object resourceObject) {
        final String fileName = String.valueOf(ResourceManager.resourceFolder) + resourceName.replace('.', '/') + ".xml";
        Spit.out("Going to Save " + fileName);
        final String stamp = getTimeStamp();
        File file;
        try {
            file = new File(fileName);
            if (file.exists()) {
                final File oldFile = new File(String.valueOf(fileName) + stamp + ".bak");
                final boolean ok = file.renameTo(oldFile);
                if (!ok) {
                    file.delete();
                }
                file = new File(fileName);
            }
        }
        catch (Exception e) {
            Spit.out("Error saving resource  to file " + fileName + ". " + e.getMessage());
            return;
        }
        ObjectManager.toXmlFile(resourceObject, file);
    }
    
    public static String getTimeStamp() {
        final Calendar dt = Calendar.getInstance();
        final String stamp = String.valueOf(dt.get(1)) + "_" + dt.get(2) + "_" + dt.get(5) + "_" + dt.get(11) + "_" + dt.get(12) + "_" + dt.get(13);
        return stamp;
    }
    
    public static boolean renameAsBackup(final File file) {
        final Calendar dt = Calendar.getInstance();
        final String stamp = String.valueOf(dt.get(1)) + "_" + dt.get(1) + "_" + dt.get(2) + "_" + dt.get(5) + "_" + dt.get(11) + "_" + dt.get(12) + "_" + dt.get(13);
        try {
            final File oldFile = new File(String.valueOf(file.getAbsolutePath()) + stamp + ".bak");
            return file.renameTo(oldFile);
        }
        catch (Exception e) {
            Spit.out("Unable to rename " + file.getAbsolutePath() + " to " + file.getAbsolutePath() + stamp + ".bak. " + e.getMessage());
            return false;
        }
    }
    
    public static void saveText(final String fileName, final String content) {
        Spit.out("Going to save " + fileName);
        try {
            final int idx = fileName.lastIndexOf(47);
            if (idx > 0) {
                createFolder(fileName.substring(0, idx));
            }
            final File file = new File(fileName);
            final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("utf-8"));
            writer.write(content);
            writer.close();
        }
        catch (Exception e) {
            Spit.out("Error saving resource  to file " + fileName + ". " + e.getMessage());
            Spit.out("stacktrace=" + e.getStackTrace());
            return;
        }
        Spit.out(String.valueOf(fileName) + " saved.");
    }
    
    public static String[][] getFiles(final String startingFolder) {
        final List<String> names = new ArrayList<String>();
        final List<String> fileOrFolder = new ArrayList<String>();
        String folderName = ResourceManager.resourceFolder;
        if (startingFolder != null && startingFolder.length() > 0) {
            final String thisFolderName = startingFolder.replaceAll(".", "/");
            folderName = String.valueOf(folderName) + thisFolderName;
            names.add(thisFolderName);
            fileOrFolder.add("folder");
        }
        final File file = new File(folderName);
        addFiles(file, "", names, fileOrFolder);
        final int n = names.size();
        final String[][] data = new String[n + 1][2];
        data[0][0] = "name";
        data[0][1] = "isFolder";
        for (int i = 0; i < n; ++i) {
            final String[] row = data[i + 1];
            row[0] = names.get(i);
            row[1] = fileOrFolder.get(i);
        }
        return data;
    }
    
    private static void addFiles(final File folder, final String prefix, final List<String> names, final List<String> fileOrFodler) {
        final File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        final int n = files.length;
        String[] fileNames = new String[n];
        int nbrFiles = 0;
        final Map<String, File> folders = new HashMap<String, File>();
        int nbrFolders = 0;
        File[] array;
        for (int length = (array = files).length, i = 0; i < length; ++i) {
            final File f = array[i];
            if (!f.isHidden()) {
                final String name = f.getName();
                if (!name.equalsIgnoreCase("SVN") && !name.equalsIgnoreCase("CVS") && !name.equalsIgnoreCase("VSS")) {
                    if (!name.startsWith(".")) {
                        if (f.isDirectory()) {
                            folders.put(name, f);
                            ++nbrFolders;
                        }
                        else {
                            fileNames[nbrFiles] = name;
                            ++nbrFiles;
                        }
                    }
                }
            }
        }
        if (nbrFolders > 0) {
            final String[] folderNames = folders.keySet().toArray(new String[0]);
            Arrays.sort(folderNames);
            String[] array2;
            for (int length2 = (array2 = folderNames).length, j = 0; j < length2; ++j) {
                final String folderName = array2[j];
                final String newName = String.valueOf(prefix) + folderName;
                names.add(newName);
                fileOrFodler.add("folder");
                addFiles(folders.get(folderName), String.valueOf(newName) + '/', names, fileOrFodler);
            }
        }
        if (nbrFiles > 0) {
            fileNames = Arrays.copyOf(fileNames, nbrFiles);
            Arrays.sort(fileNames);
            String[] array3;
            for (int length3 = (array3 = fileNames).length, k = 0; k < length3; ++k) {
                final String fileName = array3[k];
                names.add(String.valueOf(prefix) + fileName);
                fileOrFodler.add("file");
            }
        }
    }
    
    public static String[] getResourceList(final String startingFolder) {
        final Set<String> names = new HashSet<String>();
        String folderName = ResourceManager.resourceFolder;
        if (startingFolder != null && startingFolder.length() > 0) {
            folderName = String.valueOf(folderName) + startingFolder;
        }
        final File file = new File(folderName);
        addResourceList(file, "", names);
        final String[] arr = names.toArray(new String[0]);
        Arrays.sort(arr);
        return arr;
    }
    
    public static Map<String, Object> loadFromFileOrFolder(final String fileName, final String folderName) {
        final Map<String, Object> list = new HashMap<String, Object>();
        final File file = new File(String.valueOf(ResourceManager.resourceFolder) + fileName.replace('.', '/') + ".xml");
        if (file.exists() && file.isFile()) {
            list.put("", ObjectManager.fromXml(file));
        }
        final File folder = new File(String.valueOf(ResourceManager.resourceFolder) + folderName);
        if (!folder.exists()) {
            Spit.out(String.valueOf(folderName) + " is not found as a resource folder");
            return list;
        }
        final Set<String> names = new HashSet<String>();
        final String filePrefix = String.valueOf(ResourceManager.resourceFolder) + folderName + '/';
        addResourceList(folder, "", names);
        for (final String fn : names) {
            final String fullName = String.valueOf(filePrefix) + fn.replace('.', '/') + ".xml";
            Spit.out("Going to read " + fullName);
            try {
                final File f = getFile(fullName);
                list.put(fn, ObjectManager.fromXml(f));
            }
            catch (Exception e) {
                Spit.out("Error while parsing. " + e.getMessage() + "\n dictionary skipped.");
            }
        }
        return list;
    }
    
    private static void addResourceList(final File folder, final String prefix, final Set<String> names) {
        final File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        File[] array;
        for (int length = (array = files).length, i = 0; i < length; ++i) {
            final File f = array[i];
            if (!f.isHidden()) {
                final String name = f.getName();
                if (f.isDirectory()) {
                    if (!name.equalsIgnoreCase("SVN") && !name.equalsIgnoreCase("CVS")) {
                        if (!name.equalsIgnoreCase("VSS")) {
                            addResourceList(f, String.valueOf(prefix) + f.getName() + '.', names);
                        }
                    }
                }
                else {
                    final int n = name.lastIndexOf(".xml");
                    if (n > 0) {
                        names.add(String.valueOf(prefix) + name.substring(0, n));
                    }
                }
            }
        }
    }
    
    public static String[] getResourceFolders(final String resourceType) {
        final Set<String> folderNames = new HashSet<String>();
        final String folderName = String.valueOf(ResourceManager.resourceFolder) + resourceType;
        final File folder = new File(folderName);
        if (!folder.exists() || !folder.isDirectory()) {
            return new String[0];
        }
        addResourceFolder(folder, "", folderNames, true);
        String[] arr = new String[folderNames.size()];
        arr = folderNames.toArray(arr);
        Arrays.sort(arr);
        return arr;
    }
    
    private static boolean addResourceFolder(final File folder, final String prefix, final Set<String> folderNames, final boolean isRoot) {
        final String folderName = folder.getName();
        final String lowerFolder = folderName.toLowerCase();
        if (lowerFolder.equals("cvs") || folderName.equals(".svn") || folderName.equals("vss")) {
            return false;
        }
        boolean toBeAdded = false;
        final String newPrefix = isRoot ? "" : (String.valueOf(prefix) + folderName + '.');
        File[] listFiles;
        for (int length = (listFiles = folder.listFiles()).length, i = 0; i < length; ++i) {
            final File f = listFiles[i];
            if (f.isDirectory() && addResourceFolder(f, newPrefix, folderNames, false)) {
                toBeAdded = true;
            }
            final String name = f.getName();
            final int n = name.lastIndexOf(".xml");
            if (n > 0) {
                toBeAdded = true;
            }
        }
        if (!toBeAdded || isRoot) {
            return false;
        }
        folderNames.add(String.valueOf(prefix) + folderName);
        return true;
    }
    
    public static void createFolder(final String path) {
        final File folder = new File(path);
        if (!folder.exists()) {
            Spit.out("folder " + path + " created");
            folder.mkdirs();
        }
    }
    
    public static void resetApplication() {
        AP.load();
        Messages.reload(true);
        DataTypes.reload(true);
        DataDictionary.reload(true);
        ServiceList.reload(true);
        Services.flush();
        Sqls.flush();
        Tables.flush();
    }
    
    public static void reloadApplication() {
        AP.load();
        Messages.reload(false);
        DataTypes.reload(false);
        DataDictionary.reload(false);
        ServiceList.reload(false);
        Services.flush();
        Sqls.flush();
        Tables.flush();
    }
    
    static int chooseProject(final String absoluteFolderName, final DataCollection dc) {
        String folderName = absoluteFolderName;
        if (!folderName.endsWith("/") && !folderName.endsWith("\\")) {
            folderName = String.valueOf(folderName) + '/';
        }
        final File folder = new File(folderName);
        String msg = null;
        if (!folder.exists()) {
            msg = String.valueOf(folderName) + " is not a valid folder name. Can not be set as resource fodler";
        }
        else if (!folder.isDirectory()) {
            msg = String.valueOf(folderName) + " is not a folder. Can not be set as resource fodler";
        }
        else {
            final File file = new File(String.valueOf(folderName) + "applicationParameters.xml");
            if (file.exists()) {
                ResourceManager.resourceFolder = folderName;
                reloadApplication();
                return 1;
            }
            msg = String.valueOf(folderName) + " does not contain applicationParameters.xml. Please check.";
        }
        if (dc == null) {
            Spit.out(msg);
        }
        else {
            dc.addMessage("exilityError", msg);
        }
        return 0;
    }
    
    public static boolean loadAllResources(final String resFolder, final String internalFolder) {
        Spit.out("loading all resources using resource folder = " + resFolder + " and internal folder = " + internalFolder);
        try {
            boolean normalresourcesToBeFlushed = true;
            if (internalFolder != null) {
                ResourceManager.resourceFolder = sanitizedFolder(internalFolder);
                loadResources(true);
                normalresourcesToBeFlushed = false;
            }
            if (resFolder != null) {
                ResourceManager.resourceFolder = sanitizedFolder(resFolder);
                loadResources(normalresourcesToBeFlushed);
            }
            flushCachedResources();
            return true;
        }
        catch (Exception e) {
            Spit.out(e);
            return false;
        }
    }
    
    private static String sanitizedFolder(final String folderName) {
        if (folderName.endsWith("/") || folderName.endsWith("\\")) {
            return folderName;
        }
        return String.valueOf(folderName) + '/';
    }
    
    private static void flushCachedResources() {
        Services.flush();
        Tables.flush();
        Sqls.flush();
    }
    
    private static void loadResources(final boolean flushBeforeLoading) {
        AP.load();
        DataTypes.reload(flushBeforeLoading);
        Messages.reload(flushBeforeLoading);
        DataDictionary.reload(flushBeforeLoading);
        ServiceList.reload(flushBeforeLoading);
    }
    
    @Deprecated
    public static void setApplicationParametersFileName(final String filePathRelativeToRoot) {
        ResourceManager.appFileName = filePathRelativeToRoot;
        if (ResourceManager.appFileName.indexOf(".") == -1) {
            ResourceManager.appFileName = String.valueOf(ResourceManager.appFileName) + ".xml";
        }
        loadAppParameters();
    }
    
    @Deprecated
    private static void loadAppParameters() {
        String fileName = null;
        if (ResourceManager.appFileName == null) {
            fileName = String.valueOf(ResourceManager.resourceFolder) + AP.parametersFileName;
        }
        else {
            fileName = String.valueOf(ResourceManager.rootFolder) + ResourceManager.appFileName;
        }
        try {
            final File file = getFile(fileName);
            final ApplicationParameters ap = (ApplicationParameters)ObjectManager.fromXml(file, ApplicationParameters.class);
            AP.setInstance(ap);
        }
        catch (Exception e) {
            final String msg = "Unable to read applicaiton parameters file. Applicaiton will not work. Error : " + e.getMessage();
            Spit.out(msg);
        }
    }
    
    public static void main(final String[] args) {
        final String root = "d:/b/ez/webapp/";
        final String res = null;
        final String internal = "d:/b/ez/webapp/WEB-INF/exilityResource";
        setRootFolder("d:/b/ez/webapp/");
        loadAllResources(res, "d:/b/ez/webapp/WEB-INF/exilityResource");
        System.out.println(AP.projectName);
    }
}
