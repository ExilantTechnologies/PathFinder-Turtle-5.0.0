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

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;

public class Page implements ToBeInitializedInterface
{
    static final String JS_VAR_NAME = "ele";
    static final String NOT_A_TABLE = " ";
    private static final String[] FIELD_NAMES;
    String name;
    String title;
    String breadCrumpTitle;
    String type;
    String[] onLoadActionNames;
    String[] onModifyModeActionNames;
    String[] fieldsToDisableOnModifyMode;
    int minParametersToFireOnLoad;
    int minParameters;
    int width;
    int height;
    int popupWidth;
    int popupHeight;
    int popupLeft;
    int popupTop;
    String[] scriptsToInclude;
    String module;
    String description;
    String[] buttonsToHideForPicker;
    PageParameter[] pageParameters;
    AbstractElement[] panels;
    AbstractAction[] actions;
    String onLoadActionName;
    String reloadActionName;
    boolean validateOnlyOnUserChange;
    boolean trackFieldChanges;
    String formValidationFunction;
    boolean hasChartFields;
    String hotkeyFunction;
    String script;
    String style;
    String customLabelName;
    boolean doNotResizeTables;
    String firstFieldName;
    static String customLabelKey;
    private HashMap<String, String> referredActions;
    private HashMap<String, String> allFieldNames;
    private HashMap<String, Set<String>> duplicateFields;
    private Set<String> allNonFieldNames;
    private Set<String> allTableNames;
    private HashMap<String, AbstractPanel> allPanels;
    String pageMode;
    private ArrayList<String> buttonsToEnableOnFormChange;
    private ArrayList<String> buttonsToDisableOnFormChange;
    String[] buttonsToEnable;
    String[] buttonsToDisable;
    int pageWidth;
    int pageHeight;
    String[] fieldsToHideOnLoad;
    String enableRichTextOnAreas;
    boolean hasPageSpecificCSS;
    String onFormChangeActionName;
    String onFormResetActionName;
    boolean generateOnlyMetaData;
    String htmlFileName;
    private String translatedLanguage;
    String pageLayoutType;
    private static final String[] ALL_LABELS;
    private static final String[] ALL_LABEL_LISTS;
    private static final String[] ALL_LABEL_VALUE_LISTS;
    
    static {
        FIELD_NAMES = new String[] { "minParametersToFireOnLoad", "minParameters", "defaultButtonName", "onLoadActionNames", "onModifyModeActionNames", "pageMode", "buttonsToEnable", "buttonsToDisable", "pageWidth", "pageHeight", "popupWidth", "popupHeight", "popupTop", "popupLeft", "fieldsToDisableOnModifyMode", "buttonsToHideForPicker", "trackFieldChanges", "reloadActionName", "validateOnlyOnUserChange", "breadCrumpTitle", "formValidationFunction", "hasChartFields", "hotkeyFunction", "fieldsToHideOnLoad", "doNotResizeTables", "onFormChangeActionName", "onFormResetActionName", "enableRichTextOnAreas", "firstFieldName" };
        Page.customLabelKey = null;
        ALL_LABELS = new String[] { "hoverText", "footerLabe", "labelForBulkDeleteCheckBox", "labelForAddRowButton", "hoverForDeleteCheckBox", "paginationLabel", "rowHelpText", "blankOption", "trueValue", "falseValue", "xaxislabel", "yaxislabel" };
        ALL_LABEL_LISTS = new String[] { "xaxislabels", "yaxislabels" };
        ALL_LABEL_VALUE_LISTS = new String[] { "valueList" };
    }
    
    public Page() {
        this.name = null;
        this.title = null;
        this.breadCrumpTitle = null;
        this.type = null;
        this.onLoadActionNames = null;
        this.onModifyModeActionNames = null;
        this.fieldsToDisableOnModifyMode = null;
        this.minParametersToFireOnLoad = 0;
        this.minParameters = 0;
        this.width = 0;
        this.height = 0;
        this.popupWidth = 0;
        this.popupHeight = 0;
        this.popupLeft = 0;
        this.popupTop = 0;
        this.scriptsToInclude = null;
        this.module = null;
        this.description = null;
        this.buttonsToHideForPicker = null;
        this.pageParameters = null;
        this.panels = new AbstractElement[0];
        this.actions = new AbstractAction[0];
        this.onLoadActionName = null;
        this.reloadActionName = null;
        this.validateOnlyOnUserChange = false;
        this.trackFieldChanges = false;
        this.formValidationFunction = null;
        this.hasChartFields = false;
        this.hotkeyFunction = null;
        this.script = null;
        this.style = null;
        this.customLabelName = null;
        this.doNotResizeTables = false;
        this.firstFieldName = null;
        this.referredActions = null;
        this.allFieldNames = null;
        this.duplicateFields = null;
        this.allNonFieldNames = null;
        this.allTableNames = null;
        this.allPanels = null;
        this.pageMode = null;
        this.buttonsToEnableOnFormChange = null;
        this.buttonsToDisableOnFormChange = null;
        this.buttonsToEnable = null;
        this.buttonsToDisable = null;
        this.pageWidth = 0;
        this.pageHeight = 0;
        this.fieldsToHideOnLoad = null;
        this.enableRichTextOnAreas = null;
        this.hasPageSpecificCSS = false;
        this.onFormChangeActionName = null;
        this.onFormResetActionName = null;
        this.generateOnlyMetaData = false;
        this.htmlFileName = null;
        this.translatedLanguage = null;
    }
    
    public void toHtml(final StringBuilder html, final PageGeneratorContext pageContext) {
        if (this.onLoadActionName != null) {
            final String err = "ERROR: You are to use onLoadActionNames and not the singular onLoadActionName. Please change your page xml";
            Spit.out(err);
            pageContext.reportError(err);
            if (this.onLoadActionNames == null) {
                (this.onLoadActionNames = new String[1])[0] = this.onLoadActionName;
            }
        }
        if (this.actions.length > 1) {
            this.checkForDuplicateActions(pageContext);
        }
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        html.append("\n<html xmlns=\"http://www.w3.org/1999/xhtml\" ><head><title>").append(this.title).append("</title>");
        html.append("\n<meta content='text/html; charset=UTF-8' http-equiv='Content-Type'/>");
        if (AP.httpNoCacheTagRequires) {
            html.append("\n<meta http-equiv=\"Cache-Control\" content=\"no-cache\"/>");
        }
        html.append("\n<link rel=\"stylesheet\" href=\"").append(AP.commonFolderPrefix).append("default.css\" type=\"text/css\" />");
        if (this.hasPageSpecificCSS) {
            html.append("\n<link rel=\"stylesheet\" href=\"").append(this.name).append(".css\" type=\"text/css\" />");
        }
        html.append("\n<script type=\"text/javascript\" src=\"").append(AP.exilityFolderPrefix).append("exilityLoader.js\"></script>");
        if (this.hasChartFields) {
            html.append("\n<script language=\"javascript\" type=\"text/javascript\" src=\"").append(AP.exilityFolderPrefix).append("flotr/lib/excanvas.js\"></script>");
            html.append("\n<script language=\"javascript\" type=\"text/javascript\" src=\"").append(AP.exilityFolderPrefix).append("flotr/lib/prototype-1.6.0.2.js\"></script>");
            html.append("\n<script language=\"javascript\" type=\"text/javascript\" src=\"").append(AP.exilityFolderPrefix).append("flotr/flotr-0.1.0alpha.js\"></script>");
            html.append("\n<script language=\"javascript\" type=\"text/javascript\" src=\"").append(AP.exilityFolderPrefix).append("chartutil.js\"></script>");
        }
        if (this.enableRichTextOnAreas != null) {
            html.append("\n<script language=\"javascript\" type=\"text/javascript\" src=\"").append(AP.exilityFolderPrefix).append("../tiny_mce/tiny_mce.js\"></script>");
            html.append("\n<script language=\"javascript\" type=\"text/javascript\" src=\"").append(AP.exilityFolderPrefix).append("../tiny_mce/config.js\"></script>");
        }
        this.addScriptFile(html, String.valueOf(this.name) + ".metadata.js");
        if (this.scriptsToInclude != null) {
            String[] scriptsToInclude;
            for (int length = (scriptsToInclude = this.scriptsToInclude).length, i = 0; i < length; ++i) {
                final String includeFile = scriptsToInclude[i];
                if (includeFile.endsWith(".css")) {
                    this.addStyleFile(html, includeFile);
                }
                else {
                    this.addScriptFile(html, includeFile);
                }
            }
        }
        if (this.style != null) {
            html.append("\n<style type=\"text/css\">");
            html.append(this.style);
            html.append("\n</style>");
        }
        if (this.script != null) {
            html.append("\n<script language=\"javascript\" type=\"text/javascript\">");
            html.append(this.script);
            html.append("\n</script>");
        }
        html.append("\n</head>");
        final String layoutType = pageContext.getLayoutType();
        html.append("\n<body onload=\"exilPageLoad();");
        if (layoutType.equals("3") || layoutType.equals("5")) {
            html.append(" centerAlign();\" class=\"wmsbody\" ");
        }
        else {
            html.append("\" ");
        }
        html.append("  style=\"display:none;\" onunload=\"exilPageUnload();\" onscroll=\"adjustFrameHeightToBodyHeight();\" >");
        html.append("\n<form id=\"form1\" autocomplete=\"off\" onsubmit=\"return false;\" action=\"\" accept-charset=\"UTF-8\" >");
        if (this.title != null) {
            html.append("<fieldset>");
            html.append("<legend><span id=\"pageheader\" class=\"pageheader\">").append(this.title).append("</span></legend>");
        }
        html.append("<table id=\"pageTable\"");
        if (AP.alignPanels) {
            html.append(" width=\"100%\"");
        }
        if (layoutType.equals("3")) {
            html.append(" class=\"wmspage\" ><tr><td style=\"width:10px;\"></td><td>");
        }
        else if (layoutType.equals("5")) {
            html.append(" class=\"wmspage\" ><tr><td>");
        }
        else {
            html.append(" ><tr><td style=\"width:10px;\"></td><td>");
        }
        if (this.panels == null) {
            html.append("There are no panels defined for this page <br/>");
        }
        else {
            AbstractElement[] panels;
            for (int length2 = (panels = this.panels).length, j = 0; j < length2; ++j) {
                final AbstractElement ele = panels[j];
                ele.toHtml(html, pageContext);
                if (ele.inError) {
                    pageContext.reportError("");
                }
            }
        }
        if (AP.pageLayoutType.equals("5")) {
            html.append("</td></tr></table>");
        }
        else {
            html.append("</td><td style=\"width:10px;\"></td></tr></table>");
        }
        if (this.title != null) {
            html.append("</fieldset>");
        }
        html.append("</form></body></html>");
        if (pageContext.fieldsToHideOnPageLoad.size() > 0) {
            this.fieldsToHideOnLoad = pageContext.fieldsToHideOnPageLoad.toArray(new String[0]);
        }
    }
    
    public void toJavaScript(final StringBuilder js, final PageGeneratorContext pageContext) {
        this.referredActions = new HashMap<String, String>();
        this.allFieldNames = new HashMap<String, String>();
        this.duplicateFields = new HashMap<String, Set<String>>();
        this.allNonFieldNames = new HashSet<String>();
        this.allTableNames = new HashSet<String>();
        this.allPanels = new HashMap<String, AbstractPanel>();
        this.buttonsToEnableOnFormChange = new ArrayList<String>();
        this.buttonsToDisableOnFormChange = new ArrayList<String>();
        pageContext.allFieldNames = this.allFieldNames;
        pageContext.duplicateFields = this.duplicateFields;
        pageContext.allNonFieldNames = this.allNonFieldNames;
        pageContext.allTableNames = this.allTableNames;
        this.collectAllNames(pageContext);
        if (this.isDualMode()) {
            this.pageMode = "modify";
        }
        js.append("\n var ele;");
        js.append("\nvar P2 = new PM.ExilityPage(window, '").append(this.name).append("');");
        if (this.breadCrumpTitle == null || this.breadCrumpTitle.equals("")) {
            this.breadCrumpTitle = this.name;
        }
        if (this.buttonsToEnableOnFormChange.size() > 0) {
            this.buttonsToEnable = this.buttonsToEnableOnFormChange.toArray(new String[0]);
        }
        if (this.buttonsToDisableOnFormChange.size() > 0) {
            this.buttonsToDisable = this.buttonsToDisableOnFormChange.toArray(new String[0]);
        }
        pageContext.setAttributes(this, "P2", js, Page.FIELD_NAMES);
        if (this.pageParameters != null) {
            js.append("\n/*Page parameters */");
            PageParameter[] pageParameters;
            for (int length = (pageParameters = this.pageParameters).length, i = 0; i < length; ++i) {
                final PageParameter pp = pageParameters[i];
                if (pp.inError) {
                    pageContext.reportError("");
                }
                pp.toJavaScript(js, pageContext);
                js.append("\nP2.addParameter(").append("ele").append(");");
                js.append("\n");
            }
        }
        AbstractElement[] panels;
        for (int length2 = (panels = this.panels).length, j = 0; j < length2; ++j) {
            final AbstractElement panel = panels[j];
            if (panel instanceof AbstractPanel) {
                ((AbstractPanel)panel).toJs(js, pageContext);
                if (panel.inError) {
                    pageContext.reportError("");
                }
            }
            else {
                Spit.out("Page " + this.name + " has a " + panel.getClass().getSimpleName() + " as its panel. Note that elements must be wrapped in a panel.");
            }
        }
        final ArrayList<String> unusedAction = new ArrayList<String>();
        AbstractAction[] actions;
        for (int length3 = (actions = this.actions).length, k = 0; k < length3; ++k) {
            final AbstractAction action = actions[k];
            if (!this.referredActions.containsKey(action.name)) {
                unusedAction.add(action.name);
            }
            else {
                pageContext.setTableName(this.referredActions.get(action.name), false);
                this.referredActions.remove(action.name);
            }
            if (action.hidePanels != null) {
                this.checkPanelNames(action.hidePanels, pageContext);
            }
            if (action.showPanels != null) {
                this.checkPanelNames(action.showPanels, pageContext);
            }
            action.toJavaScript(js, pageContext);
            if (action instanceof ServerAction) {
                final ServerAction sa = (ServerAction)action;
                if (sa.submitFields != null) {
                    this.outputFieldsToSubmit(js, sa.name, sa.submitFields);
                }
            }
            pageContext.resetTableName();
        }
        if (unusedAction.size() > 0) {
            Spit.out("Warning: Following action" + ((unusedAction.size() == 1) ? " is" : "s are") + " defined but not used. Ignore this if actions are referred in your java script");
            for (final String aname : unusedAction) {
                Spit.out("\t" + aname);
            }
        }
        if (this.referredActions.size() > 0) {
            String str = "ERROR : Following ";
            if (this.referredActions.size() > 1) {
                str = String.valueOf(str) + " actions are ";
            }
            else {
                str = String.valueOf(str) + " action is ";
            }
            str = String.valueOf(str) + "used in the page but not declared.";
            for (final String rname : this.referredActions.keySet()) {
                str = String.valueOf(str) + "\n\t" + rname;
            }
            Spit.out(str);
            pageContext.reportError(str);
        }
        this.allFieldNames = null;
        this.allNonFieldNames = null;
        this.allTableNames = null;
        this.duplicateFields = null;
        this.referredActions = null;
    }
    
    private void outputFieldsToSubmit(final StringBuilder js, final String actionName, final String fields) {
        final String fieldPrefix = "\n" + actionName + "fieldsToSubmit['";
        final String suffix = "'] = true;";
        final String tablePrefix = "\n" + actionName + "tablesToSubmit['";
        final String[] entities = fields.split(",");
        String[] array;
        for (int length = (array = entities).length, i = 0; i < length; ++i) {
            String ntt = array[i];
            ntt = ntt.trim();
            if (this.allPanels.containsKey(ntt)) {
                final AbstractPanel panel = this.allPanels.get(ntt);
                String prefix = "";
                if (panel.tableName != null) {
                    js.append(tablePrefix).append(panel.tableName).append(suffix);
                    prefix = String.valueOf(panel.tableName) + "_";
                }
                AbstractElement[] elements;
                for (int length2 = (elements = panel.elements).length, j = 0; j < length2; ++j) {
                    final AbstractElement ele = elements[j];
                    if (!ele.markedAsComment) {
                        if (ele instanceof AbstractField) {
                            final AbstractField field = (AbstractField)ele;
                            js.append(fieldPrefix).append(prefix).append(field.name).append(suffix);
                        }
                    }
                }
            }
            else if (this.allTableNames.contains(ntt)) {
                js.append(tablePrefix).append(ntt).append(suffix);
            }
            else {
                js.append(fieldPrefix).append(ntt).append(suffix);
            }
        }
    }
    
    private boolean isDualMode() {
        if (this.onModifyModeActionNames != null || this.fieldsToDisableOnModifyMode != null) {
            return true;
        }
        if (this.pageParameters == null) {
            return false;
        }
        PageParameter[] pageParameters;
        for (int length = (pageParameters = this.pageParameters).length, i = 0; i < length; ++i) {
            final PageParameter p = pageParameters[i];
            if (p.isPrimaryKey) {
                return true;
            }
        }
        return false;
    }
    
    private void addScriptFile(final StringBuilder sbf, final String fileName) {
        sbf.append("\n<script type=\"text/javascript\" src=\"").append(fileName).append("\" ></script> ");
    }
    
    private void addStyleFile(final StringBuilder sbf, final String fileName) {
        sbf.append("\n<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(fileName).append("\" />");
    }
    
    private void checkPanelNames(final String[] names, final PageGeneratorContext pc) {
        for (final String panelName : names) {
            if (!this.allNonFieldNames.contains(panelName)) {
                if (!this.allFieldNames.containsKey(panelName)) {
                    final String err = "ERROR : Panel " + panelName + " is referred but it is not defined anywhere in the page.";
                    Spit.out(err);
                    pc.reportError(err);
                }
            }
        }
    }
    
    private void collectAllNames(final PageGeneratorContext pc) {
        final String tableName = " ";
        AbstractElement[] panels;
        for (int length = (panels = this.panels).length, i = 0; i < length; ++i) {
            final AbstractElement panel = panels[i];
            this.collectNames(panel, tableName, pc);
        }
        this.verifyElementReference(this.buttonsToEnableOnFormChange.toArray(new String[this.buttonsToEnableOnFormChange.size()]), pc);
        this.verifyElementReference(this.buttonsToDisableOnFormChange.toArray(new String[this.buttonsToDisableOnFormChange.size()]), pc);
        this.verifyElementReference(this.buttonsToHideForPicker, pc);
        this.verifyElementReference(this.fieldsToDisableOnModifyMode, pc);
        if (this.onFormChangeActionName != null) {
            this.checkAndAddAction(this.onFormChangeActionName, tableName, pc);
        }
        if (this.onFormResetActionName != null) {
            this.checkAndAddAction(this.onFormResetActionName, tableName, pc);
        }
        if (this.onLoadActionNames != null) {
            String[] onLoadActionNames;
            for (int length2 = (onLoadActionNames = this.onLoadActionNames).length, j = 0; j < length2; ++j) {
                final String actionName = onLoadActionNames[j];
                this.checkAndAddAction(actionName, tableName, pc);
            }
        }
        if (this.onModifyModeActionNames != null) {
            String[] onModifyModeActionNames;
            for (int length3 = (onModifyModeActionNames = this.onModifyModeActionNames).length, k = 0; k < length3; ++k) {
                final String actionName = onModifyModeActionNames[k];
                this.checkAndAddAction(actionName, tableName, pc);
            }
        }
        if (this.reloadActionName != null) {
            this.checkAndAddAction(this.reloadActionName, tableName, pc);
        }
        AbstractAction[] actions;
        for (int length4 = (actions = this.actions).length, l = 0; l < length4; ++l) {
            final AbstractAction action = actions[l];
            if (action instanceof ServerAction) {
                final ServerAction se = (ServerAction)action;
                if (se.callBackActionName != null) {
                    this.checkAndAddAction(se.callBackActionName, tableName, pc);
                }
            }
        }
    }
    
    private void verifyElementReference(final String[] names, final PageGeneratorContext pc) {
        if (names == null) {
            return;
        }
        for (final String ename : names) {
            if (!this.allNonFieldNames.contains(ename)) {
                if (!this.allFieldNames.containsKey(ename)) {
                    final String msg = String.valueOf(ename) + " is referred but it is not defined.";
                    Spit.out(msg);
                    pc.reportError(msg);
                }
            }
        }
    }
    
    private void collectNames(final AbstractElement ele, final String tableName, final PageGeneratorContext pc) {
        if (ele instanceof AbstractPanel) {
            final AbstractPanel panel = (AbstractPanel)ele;
            if (ele.name != null) {
                this.checkAndAddElementNames(ele.name, pc);
                this.allPanels.put(ele.name, panel);
            }
            String tName = tableName;
            if (panel.tableName != null) {
                if (!tableName.equals(" ")) {
                    final String err = "ERROR: panel " + panel.name + " has a table with name " + panel.tableName + " but this panel is already inside a table " + tableName;
                    Spit.out(err);
                    pc.reportError(err);
                }
                else {
                    tName = panel.tableName;
                    if (this.allTableNames.contains(tName)) {
                        final String err = "ERROR: panel " + panel.name + " uses table " + tName + ". This is already used by another panel. Two panels can not use the same table name";
                        Spit.out(err);
                        pc.reportError(err);
                    }
                    this.allTableNames.add(tName);
                }
            }
            if (ele.onClickActionName != null) {
                this.checkAndAddAction(ele.onClickActionName, tName, pc);
            }
            if (ele instanceof ListPanel) {
                final String an = ((ListPanel)ele).onDblClickActionName;
                if (an != null) {
                    this.checkAndAddAction(an, tName, pc);
                }
            }
            if (panel.elements != null) {
                AbstractElement[] elements;
                for (int length = (elements = panel.elements).length, i = 0; i < length; ++i) {
                    final AbstractElement childEle = elements[i];
                    if (!childEle.markedAsComment) {
                        this.collectNames(childEle, tName, pc);
                    }
                }
            }
            return;
        }
        if (ele.onClickActionName != null) {
            this.checkAndAddAction(ele.onClickActionName, tableName, pc);
        }
        if (ele instanceof AbstractField) {
            this.checkAndAddFieldName(ele.name, tableName, pc);
            if (ele instanceof AbstractInputField) {
                final AbstractInputField field = (AbstractInputField)ele;
                if (field.onChangeActionName != null) {
                    this.checkAndAddAction(field.onChangeActionName, tableName, pc);
                }
                if (field.onUserChangeActionName != null) {
                    this.checkAndAddAction(field.onUserChangeActionName, tableName, pc);
                }
                if (ele instanceof FilterField) {
                    this.checkAndAddFieldName(String.valueOf(field.name) + "Operator", tableName, pc);
                    final DataValueType vt = field.getValueType();
                    if (vt == DataValueType.DATE || vt == DataValueType.INTEGRAL || vt == DataValueType.DECIMAL) {
                        this.checkAndAddFieldName(String.valueOf(field.name) + "To", tableName, pc);
                    }
                }
            }
        }
        else if (ele.name != null) {
            this.checkAndAddElementNames(ele.name, pc);
            if (ele instanceof ButtonElement) {
                final ButtonElement be = (ButtonElement)ele;
                if (be.whatToDoOnFormChange != WhatToDoOnFormChange.LEAVEMEALONE) {
                    if (be.name == null) {
                        final String err2 = "ERROR: Button element that needs to be enabled/disabled has to be assignd a name";
                        Spit.out(err2);
                        pc.reportError(err2);
                        be.name = "buttonAssignedName" + (this.buttonsToDisableOnFormChange.size() + this.buttonsToEnableOnFormChange.size());
                    }
                    if (be.whatToDoOnFormChange == WhatToDoOnFormChange.DISABLE) {
                        this.buttonsToDisableOnFormChange.add(be.name);
                    }
                    else {
                        this.buttonsToEnableOnFormChange.add(be.name);
                    }
                }
            }
        }
    }
    
    private void checkForDuplicateActions(final PageGeneratorContext pc) {
        if (this.actions == null) {
            return;
        }
        final int n = this.actions.length;
        for (int m = n - 1, i = 0; i < m; ++i) {
            final String actionName = this.actions[i].name;
            for (int j = i + 1; j < n; ++j) {
                if (actionName.equals(this.actions[j].name)) {
                    final String err = "ERROR: Action name " + actionName + " is defined more than once.";
                    Spit.out(err);
                    pc.reportError(err);
                }
            }
        }
    }
    
    private void checkAndAddElementNames(final String ename, final PageGeneratorContext pc) {
        if (this.allNonFieldNames.contains(ename) || this.allFieldNames.containsKey(ename)) {
            final String err = "ERROR :" + ename + " is used as name for more than one element.";
            Spit.out(err);
            pc.reportError(err);
            return;
        }
        this.allNonFieldNames.add(ename);
    }
    
    void checkAndAddAction(final String actionName, final String tableName, final PageGeneratorContext pc) {
        if (!this.referredActions.containsKey(actionName)) {
            this.referredActions.put(actionName, tableName);
            return;
        }
        final String tn = this.referredActions.get(actionName);
        if (tn.equals(tableName)) {
            return;
        }
        AbstractAction action = null;
        AbstractAction[] actions;
        for (int length = (actions = this.actions).length, i = 0; i < length; ++i) {
            final AbstractAction a = actions[i];
            if (a.name.equals(actionName)) {
                action = a;
                break;
            }
        }
        if (action == null) {
            return;
        }
        if ((action.enableFields == null || action.enableFields.length <= 0) && (action.disableFields == null || action.disableFields.length <= 0)) {
            if (action instanceof ServerAction) {
                final ServerAction sa = (ServerAction)action;
                if ((sa.queryFieldNames == null || sa.queryFieldNames.length == 0) && (sa.submitFields == null || sa.submitFields.length() == 0)) {
                    return;
                }
            }
            else {
                if (!(action instanceof NavigationAction)) {
                    return;
                }
                final NavigationAction na = (NavigationAction)action;
                if (na.queryFieldNames == null || na.queryFieldNames.length == 0) {
                    return;
                }
            }
        }
        String err = "ERROR : Action " + actionName + " is used in two different table contexts :";
        if (tn.equals(" ")) {
            err = String.valueOf(err) + " inside table " + tn;
        }
        else {
            err = String.valueOf(err) + " outside of any table ";
        }
        if (tableName == null || tableName.equals(" ")) {
            err = String.valueOf(err) + " and outside of any table";
        }
        else {
            err = String.valueOf(err) + " and inside table  " + tableName;
        }
        err = String.valueOf(err) + ".\nAny of the fields you have referred to in this action may lead to scope issue. You can resolve this issue by creating a copy of this action to be used inside the table.";
        Spit.out(err);
        pc.reportError(err);
    }
    
    void checkAndAddFieldName(final String fieldName, final String tableName, final PageGeneratorContext pc) {
        if (this.allNonFieldNames.contains(fieldName)) {
            final String err = "Field name " + fieldName + " is used as a name for a field as well as an element";
            Spit.out(err);
            pc.reportError(err);
            return;
        }
        if (!this.allFieldNames.containsKey(fieldName)) {
            this.allFieldNames.put(fieldName, tableName);
        }
        else {
            Set<String> tables = null;
            if (!this.duplicateFields.containsKey(fieldName)) {
                tables = new HashSet<String>();
                tables.add(this.allFieldNames.get(fieldName));
                this.duplicateFields.put(fieldName, tables);
            }
            tables = this.duplicateFields.get(fieldName);
            if (tables.contains(tableName)) {
                String err2 = "ERROR : Field name " + fieldName + " is dublicate";
                if (!tableName.equals(" ")) {
                    err2 = String.valueOf(err2) + " inside table " + tableName;
                }
                Spit.out(err2);
                pc.reportError(err2);
            }
            tables.add(tableName);
        }
        if (!tableName.equals(" ")) {
            this.allFieldNames.put(String.valueOf(tableName) + "_" + fieldName, " ");
        }
    }
    
    private void replaceIncludePanel(final AbstractElement[] eles) {
        for (int i = 0; i < eles.length; ++i) {
            AbstractElement ele = eles[i];
            if (ele == null) {
                ele = new TextElement();
                ele.label = "Panel/Field was not parsed properly. Please review your page.xml";
                eles[i] = ele;
            }
            else if (!ele.markedAsComment) {
                if (ele instanceof AbstractPanel) {
                    if (ele instanceof IncludePanel) {
                        final IncludePanel ip = (IncludePanel)ele;
                        Spit.out("found an include panel = " + ip.name + ". Name of Panel.xml to be included is " + ip.panelNameToBeIncluded);
                        final AbstractPanel newPanel = Pages.getPanel(ip.panelNameToBeIncluded);
                        if (newPanel == null) {
                            final AbstractElement te = new TextElement();
                            te.label = "ERROR: Include panel " + ip.panelNameToBeIncluded + " could not be loaded. Please look at the trace and take corrective action.";
                            eles[i] = te;
                        }
                        else {
                            newPanel.name = ele.name;
                            eles[i] = newPanel;
                        }
                    }
                    else {
                        final AbstractPanel panel = (AbstractPanel)ele;
                        if (panel.elements != null && panel.elements.length > 0) {
                            this.replaceIncludePanel(panel.elements);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void initialize() {
        if (this.panels != null) {
            this.replaceIncludePanel(this.panels);
        }
        this.pageHeight = ((this.height > 0) ? this.height : AP.defaultPageHeight);
        this.pageWidth = ((this.width > 0) ? this.width : AP.defaultPageWidth);
        Page.customLabelKey = this.customLabelName;
    }
    
    public boolean generateAndSavePage(final boolean generateEvenOnerror, final String language) {
        if (language != null && !language.equals("English")) {
            if (this.translatedLanguage == null) {
                Spit.out("Going to translate page to " + language + " before generating.");
                this.translate(language);
            }
            else {
                if (!this.translatedLanguage.equals(language)) {
                    Spit.out("Page is already translated to " + this.translatedLanguage + ". Can not generate page in " + language);
                    return false;
                }
                Spit.out("Page is already translated to " + language + ". proceeding to page generation.");
            }
        }
        String layoutType = this.pageLayoutType;
        if (layoutType == null) {
            layoutType = AP.pageLayoutType;
        }
        final PageGeneratorContext pageContext = new PageGeneratorContext(this.name, layoutType);
        String html = null;
        if (this.htmlFileName != null) {
            html = this.templateToHtml(pageContext);
        }
        else if (layoutType.equals("css")) {
            html = this.toHtml5(pageContext);
        }
        else {
            final StringBuilder sbf = new StringBuilder();
            this.toHtml(sbf, pageContext);
            html = sbf.toString();
        }
        int nbrErrors = pageContext.getNbrErrors();
        final StringBuilder js = new StringBuilder();
        this.toJavaScript(js, pageContext);
        nbrErrors += pageContext.getNbrErrors();
        String path = AP.htmlRootRelativeToResourcePath;
        if (nbrErrors != 0 && !generateEvenOnerror) {
            return false;
        }
        if (path == null || path.length() == 0) {
            Spit.out("htmlRootRelativeToResourcePath is not set in your project. Page generation will nto work.");
            return false;
        }
        path = String.valueOf(ResourceManager.getResourceFolder()) + path;
        if (!path.endsWith("/")) {
            path = String.valueOf(path) + "/";
        }
        if (language != null && language.length() > 0 && !language.equals("English")) {
            path = String.valueOf(path.substring(0, path.length() - 1)) + '-' + language + '/';
        }
        if (this.module != null) {
            path = String.valueOf(path) + this.module.replace('.', '/');
        }
        path = String.valueOf(path) + '/' + this.name;
        if (this.generateOnlyMetaData) {
            Spit.out("Html is not generated as generateOnlyMetaData is set to true in your page.xml");
        }
        else {
            ResourceManager.saveText(String.valueOf(path) + ".htm", html);
        }
        ResourceManager.saveText(String.valueOf(path) + ".metadata.js", js.toString());
        return true;
    }
    
    public String toHtml5(final PageGeneratorContext pageContext) {
        final String initialHtml = ResourceManager.readFile(String.valueOf(ResourceManager.getResourceFolder()) + "htmlTemplate.txt");
        if (initialHtml == null) {
            final String err = "unable to read htmlTemplate.txt from resource folder.";
            Spit.out(err);
            pageContext.reportError(err);
            return "";
        }
        final int scriptAt = initialHtml.indexOf("@script@");
        final int titleAt = initialHtml.indexOf("@title@");
        final int panelsAt = initialHtml.indexOf("@panels@");
        if (scriptAt == -1 || panelsAt == -1) {
            final String err = "htmlTemplate.txt is not valid. Ensure that you have @script@ and @panels@ at right place in the template.";
            Spit.out(err);
            pageContext.reportError(err);
            return "";
        }
        if (this.onLoadActionName != null) {
            final String err = "ERROR: You are to use onLoadActionNames and not the singular onLoadActionName. Please change your page xml";
            Spit.out(err);
            pageContext.reportError(err);
            if (this.onLoadActionNames == null) {
                (this.onLoadActionNames = new String[1])[0] = this.onLoadActionName;
            }
        }
        if (this.actions.length > 1) {
            this.checkForDuplicateActions(pageContext);
        }
        final StringBuilder html = new StringBuilder(initialHtml.substring(0, scriptAt));
        if (this.hasChartFields) {
            this.addScriptFile(html, String.valueOf(AP.exilityFolderPrefix) + "flotr/lib/prototype-1.6.0.2.js");
            this.addScriptFile(html, String.valueOf(AP.exilityFolderPrefix) + "flotr/flotr-0.1.0alpha.js");
        }
        this.addScriptFile(html, String.valueOf(this.name) + ".metadata.js");
        if (this.scriptsToInclude != null) {
            String[] scriptsToInclude;
            for (int length = (scriptsToInclude = this.scriptsToInclude).length, i = 0; i < length; ++i) {
                final String includeFile = scriptsToInclude[i];
                if (includeFile.endsWith(".css")) {
                    this.addStyleFile(html, includeFile);
                }
                else {
                    this.addScriptFile(html, includeFile);
                }
            }
        }
        if (titleAt > 0) {
            html.append(initialHtml.substring(scriptAt + 8, titleAt));
            html.append((this.title == null) ? "" : this.title);
            html.append(initialHtml.subSequence(titleAt + 7, panelsAt));
        }
        else {
            html.append(initialHtml.subSequence(scriptAt + 8, panelsAt));
        }
        pageContext.useHtml5 = true;
        if (this.panels == null) {
            html.append("There are no panels defined for this page <br/>");
        }
        else {
            AbstractElement[] panels;
            for (int length2 = (panels = this.panels).length, j = 0; j < length2; ++j) {
                final AbstractElement ele = panels[j];
                ele.toHtml5(html, pageContext);
                if (ele.inError) {
                    pageContext.reportError("");
                }
            }
        }
        html.append(initialHtml.substring(panelsAt + 8));
        return html.toString();
    }
    
    public String templateToHtml(final PageGeneratorContext pageContext) {
        final String f = String.valueOf(ResourceManager.getResourceFolder()) + "page/" + this.htmlFileName;
        final String template = ResourceManager.readFile(f);
        if (template == null) {
            final String err = "You have specified htmlFileName=\"" + this.htmlFileName + "\" for page " + this.name + ". As per convention, we tried reading " + f + " but we were got into trouble. You would have got actual file I/O related error in another part of the trace text.";
            Spit.out(err);
            pageContext.reportError(err);
            return "";
        }
        if (this.panels == null || this.panels.length == 0) {
            final String err = "No panels fround.";
            Spit.out(err);
            pageContext.reportError(err);
            return "";
        }
        boolean isHtml5 = false;
        if (pageContext.getLayoutType().equals("css")) {
            isHtml5 = true;
            pageContext.useHtml5 = true;
        }
        final StringBuilder html = new StringBuilder(template);
        AbstractElement[] panels;
        for (int length = (panels = this.panels).length, i = 0; i < length; ++i) {
            final AbstractElement ele = panels[i];
            if (ele.name == null) {
                final String err = " panels MUST be named for you to use html template.";
                Spit.out(err);
                pageContext.reportError(err);
            }
            else {
                final String tag = "<" + ele.name + "/>";
                final int idx = html.indexOf(tag);
                if (idx == -1) {
                    final String err = "Tag " + tag + " not found for panel " + ele.name + " inside html template. ";
                    Spit.out(err);
                    pageContext.reportError(err);
                }
                else {
                    final StringBuilder fragment = new StringBuilder();
                    if (isHtml5) {
                        ele.toHtml5(fragment, pageContext);
                    }
                    else {
                        ele.toHtml(fragment, pageContext);
                    }
                    html.replace(idx, idx + tag.length(), fragment.toString());
                }
            }
        }
        return html.toString();
    }
    
    public String[] getAllLabels() {
        final Set<String> labels = new HashSet<String>();
        if (this.title != null) {
            labels.add(this.title);
        }
        if (this.panels != null && this.panels.length > 0) {
            AbstractElement[] panels;
            for (int length = (panels = this.panels).length, i = 0; i < length; ++i) {
                final AbstractElement ele = panels[i];
                this.addLabelsFromElement(ele, labels);
            }
        }
        final String[] arr = new String[labels.size()];
        labels.toArray(arr);
        Arrays.sort(arr);
        return arr;
    }
    
    private void addLabelsFromElement(final AbstractElement ele, final Set<String> labels) {
        String lbl = ele.label;
        if (lbl == null && ele instanceof AbstractField) {
            final DataElement dataElement = ((AbstractField)ele).dataElement;
            if (dataElement != null) {
                lbl = dataElement.label;
            }
        }
        if (lbl != null) {
            labels.add(lbl);
        }
        final Class<?> klass = ele.getClass();
        String[] all_LABELS;
        for (int length = (all_LABELS = Page.ALL_LABELS).length, i = 0; i < length; ++i) {
            final String attribute = all_LABELS[i];
            try {
                final Field field = klass.getDeclaredField(attribute);
                field.setAccessible(true);
                final Object val = field.get(ele);
                if (val != null) {
                    labels.add(val.toString());
                }
            }
            catch (Exception ex) {}
        }
        String[] all_LABEL_LISTS;
        for (int length2 = (all_LABEL_LISTS = Page.ALL_LABEL_LISTS).length, j = 0; j < length2; ++j) {
            final String attribute = all_LABEL_LISTS[j];
            try {
                final Field field = klass.getDeclaredField(attribute);
                field.setAccessible(true);
                final Object val = field.get(ele);
                if (val != null) {
                    final String[] theseLabels = val.toString().split(",");
                    String[] array;
                    for (int length3 = (array = theseLabels).length, k = 0; k < length3; ++k) {
                        final String aLabel = array[k];
                        labels.add(aLabel.trim());
                    }
                }
            }
            catch (Exception ex2) {}
        }
        String[] all_LABEL_VALUE_LISTS;
        for (int length4 = (all_LABEL_VALUE_LISTS = Page.ALL_LABEL_VALUE_LISTS).length, l = 0; l < length4; ++l) {
            final String attribute = all_LABEL_VALUE_LISTS[l];
            try {
                final Field field = klass.getDeclaredField(attribute);
                field.setAccessible(true);
                final Object val = field.get(ele);
                if (val != null) {
                    final String[] pairs = val.toString().split(";");
                    String[] array2;
                    for (int length5 = (array2 = pairs).length, n = 0; n < length5; ++n) {
                        final String pairString = array2[n];
                        final String[] aPair = pairString.trim().split(",");
                        if (aPair.length == 2) {
                            labels.add(aPair[1].trim());
                        }
                        else if (aPair.length == 2) {
                            labels.add(aPair[1].trim());
                        }
                        else {
                            Spit.out("Page " + this.name + " has an element called " + ele.name + ". This element has invalid value for its attribute " + attribute);
                        }
                    }
                }
            }
            catch (Exception ex3) {}
        }
        if (!(ele instanceof AbstractPanel)) {
            return;
        }
        final AbstractPanel panel = (AbstractPanel)ele;
        if (panel.elements == null || panel.elements.length == 0) {
            return;
        }
        AbstractElement[] elements;
        for (int length6 = (elements = ((AbstractPanel)ele).elements).length, n2 = 0; n2 < length6; ++n2) {
            final AbstractElement child = elements[n2];
            this.addLabelsFromElement(child, labels);
        }
    }
    
    private void translate(final String language) {
        final Dubhashi dubhashi = Dubhashi.getDubhashi(language);
        if (this.title != null) {
            this.title = dubhashi.translateOrRetain(this.title);
        }
        if (this.panels != null && this.panels.length > 0) {
            AbstractElement[] panels;
            for (int length = (panels = this.panels).length, i = 0; i < length; ++i) {
                final AbstractElement ele = panels[i];
                this.translateLabels(ele, dubhashi);
            }
        }
    }
    
    private void translateLabels(final AbstractElement ele, final Dubhashi dubhashi) {
        String lbl = ele.label;
        if (lbl == null && ele instanceof AbstractField) {
            final DataElement dataElement = ((AbstractField)ele).dataElement;
            if (dataElement != null) {
                lbl = dataElement.label;
            }
        }
        if (lbl != null) {
            ele.label = dubhashi.translateOrRetain(lbl);
        }
        final Class<?> klass = ele.getClass();
        String[] all_LABELS;
        for (int length = (all_LABELS = Page.ALL_LABELS).length, i = 0; i < length; ++i) {
            final String attribute = all_LABELS[i];
            try {
                final Field field = klass.getDeclaredField(attribute);
                field.setAccessible(true);
                Object val = field.get(ele);
                if (val != null) {
                    val = dubhashi.translateOrRetain(val.toString());
                    field.set(ele, val);
                }
            }
            catch (Exception ex) {}
        }
        String[] all_LABEL_LISTS;
        for (int length2 = (all_LABEL_LISTS = Page.ALL_LABEL_LISTS).length, j = 0; j < length2; ++j) {
            final String attribute = all_LABEL_LISTS[j];
            try {
                final Field field = klass.getDeclaredField(attribute);
                field.setAccessible(true);
                final Object val = field.get(ele);
                if (val != null) {
                    final String[] theseLabels = val.toString().split(",");
                    final StringBuilder sbf = new StringBuilder();
                    String[] array;
                    for (int length3 = (array = theseLabels).length, k = 0; k < length3; ++k) {
                        final String aLabel = array[k];
                        sbf.append(dubhashi.translateOrRetain(aLabel)).append(',');
                    }
                    sbf.deleteCharAt(sbf.length() - 1);
                    field.set(ele, sbf.toString());
                }
            }
            catch (Exception ex2) {}
        }
        String[] all_LABEL_VALUE_LISTS;
        for (int length4 = (all_LABEL_VALUE_LISTS = Page.ALL_LABEL_VALUE_LISTS).length, l = 0; l < length4; ++l) {
            final String attribute = all_LABEL_VALUE_LISTS[l];
            try {
                final Field field = klass.getDeclaredField(attribute);
                field.setAccessible(true);
                final Object val = field.get(ele);
                if (val != null) {
                    final StringBuilder sbf2 = new StringBuilder();
                    final String[] pairs = val.toString().split(";");
                    String[] array2;
                    for (int length5 = (array2 = pairs).length, n = 0; n < length5; ++n) {
                        final String pairString = array2[n];
                        final String[] aPair = pairString.trim().split(",");
                        if (aPair.length == 2) {
                            sbf2.append(aPair[0]).append(',').append(dubhashi.translateOrRetain(aPair[1])).append(';');
                        }
                        else if (aPair.length == 1) {
                            sbf2.append(aPair[0]).append(',').append(dubhashi.translateOrRetain(aPair[0])).append(';');
                        }
                        else {
                            Spit.out("Page " + this.name + " has an element called " + ele.name + ". This element as invalid value for its attribute " + attribute);
                        }
                    }
                    sbf2.deleteCharAt(sbf2.length() - 1);
                    field.set(ele, sbf2.toString());
                }
            }
            catch (Exception ex3) {}
        }
        if (!(ele instanceof AbstractPanel)) {
            return;
        }
        final AbstractPanel panel = (AbstractPanel)ele;
        if (panel.elements == null || panel.elements.length == 0) {
            return;
        }
        AbstractElement[] elements;
        for (int length6 = (elements = ((AbstractPanel)ele).elements).length, n2 = 0; n2 < length6; ++n2) {
            final AbstractElement child = elements[n2];
            this.translateLabels(child, dubhashi);
        }
    }
}
