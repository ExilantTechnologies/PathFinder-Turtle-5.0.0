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

@Deprecated
public class UserCodes
{
    private static UserCodes singleton;
    Map<String, UserCodeEntry> codes;
    
    static {
        UserCodes.singleton = null;
        load();
    }
    
    public UserCodes() {
        this.codes = new HashMap<String, UserCodeEntry>();
    }
    
    static UserCodeInterface getUserCode(final String userCodeName, final DataCollection dc) throws ExilityException {
        UserCodeInterface userCode = null;
        UserCodeEntry entry = null;
        entry = UserCodes.singleton.codes.get(userCodeName);
        if (entry != null) {
            userCode = (UserCodeInterface)ObjectManager.createNew(entry.nameSpace, userCodeName);
        }
        if (userCode == null) {
            dc.raiseException("exilNoSuchUserCode", userCodeName);
        }
        return userCode;
    }
    
    static void load() {
        UserCodes.singleton = (UserCodes)ResourceManager.loadResource("userCodes", UserCodes.class);
    }
    
    static UserCodes getInstance() {
        return UserCodes.singleton;
    }
}
