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

@Deprecated
class Crafts
{
    private static Crafts instance;
    HashMap<String, CraftEntry> crafts;
    
    static {
        load();
    }
    
    Crafts() {
        this.crafts = new HashMap<String, CraftEntry>();
    }
    
    static Crafts getInstance() {
        return Crafts.instance;
    }
    
    static CraftInterface getCraft(final String craftName, final DataCollection dc) throws ExilityException {
        if (craftName == null || craftName.length() == 0) {
            return null;
        }
        CraftInterface craft = null;
        final CraftEntry entry = Crafts.instance.crafts.get(craftName);
        if (entry != null) {
            craft = (CraftInterface)ObjectManager.createNew(entry.nameSpace, craftName);
        }
        if (craft == null) {
            dc.raiseException("exilNoSuchHandCodedLogic", craftName);
        }
        return craft;
    }
    
    static void load() {
        Crafts.instance = (Crafts)ResourceManager.loadResource("crafts", Crafts.class);
    }
}
