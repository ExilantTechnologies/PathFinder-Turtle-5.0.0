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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class CodeGeneratorTemplate
{
    private static final String BEGIN_TAG_LIST = "/*begin_tagList";
    private static final String END_TAG_LIST = "end_tagList*/";
    private static final String NO_TAG_LIST = "Template should have a comma separated list of tags within /*begin_tagList and end_tagList*/. \n e.g. /*begin_tagList tag1 tag2 tag3 end_tagList*/";
    private static final String BEGIN_COMMENT = "/*begin_templateComment";
    private static final String END_COMMENT = "end_templateComment*/";
    private static final String INVALID_COMMENT = "/*begin_templateComment as begin comment block and end_templateComment*/ are not nested properly in the templeate.";
    private String errorMessage;
    private String[] tagList;
    private String[] templateParts;
    private String[] tagsToInsert;
    
    public CodeGeneratorTemplate(final String template) {
        this.errorMessage = null;
        this.tagList = null;
        this.templateParts = null;
        this.tagsToInsert = null;
        this.parse(template);
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public String[] getTags() {
        return this.tagList;
    }
    
    public String generate(final Map<String, String> stubs) {
        if (this.errorMessage != null) {
            return this.errorMessage;
        }
        String err = "";
        final StringBuilder sbf = new StringBuilder();
        for (int i = 0; i < this.tagsToInsert.length; ++i) {
            final String tag = this.tagsToInsert[i];
            final String snippet = stubs.get(tag);
            if (snippet == null) {
                err = String.valueOf(err) + "Snippet for tag " + tag + " not supplied for generation.";
            }
            else {
                sbf.append(this.templateParts[i]);
                sbf.append(snippet);
            }
        }
        if (err.length() > 0) {
            return err;
        }
        sbf.append(this.templateParts[this.templateParts.length - 1]);
        return sbf.toString();
    }
    
    private void parse(final String template) {
        int beginList = template.indexOf("/*begin_tagList");
        int endList = template.indexOf("end_tagList*/");
        if (beginList < 0 || endList < 0 || beginList > endList) {
            this.errorMessage = "Template should have a comma separated list of tags within /*begin_tagList and end_tagList*/. \n e.g. /*begin_tagList tag1 tag2 tag3 end_tagList*/";
            return;
        }
        String str = template.substring(beginList + "/*begin_tagList".length(), endList);
        Spit.out("list of tags = " + str);
        this.tagList = str.split(",");
        for (int i = 0; i < this.tagList.length; ++i) {
            this.tagList[i] = this.tagList[i].trim();
        }
        if (beginList > 0) {
            str = template.substring(0, beginList);
        }
        else {
            str = "";
        }
        str = String.valueOf(str) + template.substring(endList + "end_tagList*/".length());
        beginList = str.indexOf("/*begin_templateComment");
        if (beginList >= 0) {
            endList = str.indexOf("end_templateComment*/");
            if (endList < beginList) {
                this.errorMessage = "/*begin_templateComment as begin comment block and end_templateComment*/ are not nested properly in the templeate.";
                return;
            }
            final String part2 = str.substring(endList + "end_templateComment*/".length());
            if (beginList == 0) {
                str = part2;
            }
            else {
                str = String.valueOf(str.substring(0, beginList)) + part2;
            }
        }
        final List<TagInfo> tags = new ArrayList<TagInfo>();
        String err = "";
        String[] tagList;
        for (int length = (tagList = this.tagList).length, n2 = 0; n2 < length; ++n2) {
            final String tag = tagList[n2];
            final String s1 = "/*begin_" + tag;
            final String s2 = "end_" + tag + "*/";
            int j = str.indexOf(s1);
            if (j == -1) {
                err = String.valueOf(err) + tag + " is defined as a tag, but it is not used in the template.";
            }
            else {
                do {
                    final TagInfo ti = new TagInfo();
                    ti.tag = tag;
                    ti.startAt = j;
                    ti.endAt = str.indexOf(s2, j);
                    if (ti.endAt == -1) {
                        err = String.valueOf(err) + "End tag not found for " + tag + ".\n";
                    }
                    tags.add(ti);
                    j = str.indexOf(s1, j + 1);
                } while (j >= 0);
            }
        }
        if (err.length() > 0) {
            this.errorMessage = err;
            return;
        }
        int n = tags.size();
        for (int k = 0; k < n; ++k) {
            int minAt = k;
            final TagInfo tag2 = tags.get(k);
            int min = tag2.startAt;
            for (int l = k + 1; l < n; ++l) {
                final int startAt = tags.get(l).startAt;
                if (startAt < min) {
                    min = startAt;
                    minAt = l;
                }
            }
            if (minAt != k) {
                tags.set(k, tags.get(minAt));
                tags.set(minAt, tag2);
            }
        }
        --n;
        for (int k = 0; k < n; ++k) {
            if (tags.get(k).endAt > tags.get(k + 1).startAt) {
                err = String.valueOf(err) + "Tags " + tags.get(k).tag + " and " + tags.get(k + 1).tag + " overlap.\n";
            }
        }
        if (err.length() > 0) {
            this.errorMessage = err;
            return;
        }
        ++n;
        this.tagsToInsert = new String[n];
        this.templateParts = new String[n + 1];
        int startAt2 = 0;
        for (int m = 0; m < n; ++m) {
            final TagInfo tag2 = tags.get(m);
            this.tagsToInsert[m] = tag2.tag;
            int endAt = tag2.startAt;
            if (endAt <= startAt2) {
                endAt = startAt2;
            }
            this.templateParts[m] = str.substring(startAt2, endAt);
            startAt2 = tag2.endAt + tag2.tag.length() + 6;
        }
        this.templateParts[n] = str.substring(startAt2);
    }
    
    public static void main(final String[] args) {
        final String template = "abcd/*begin_tagLista,bend_tagList*/123/*begin_a you should never see this end_a*/78912345/*begin_b it is an error if you see me  end_b*/34567";
        final CodeGeneratorTemplate gen = new CodeGeneratorTemplate(template);
        String str = gen.getErrorMessage();
        if (str != null) {
            System.out.print("Error : " + gen.errorMessage);
            return;
        }
        final Map<String, String> snippets = new HashMap<String, String>();
        snippets.put("a", "456");
        snippets.put("b", "6789012");
        str = gen.generate(snippets);
        System.out.print(str);
    }
    
    class TagInfo
    {
        String tag;
        int startAt;
        int endAt;
    }
}
