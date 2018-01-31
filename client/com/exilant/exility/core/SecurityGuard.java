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

import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class SecurityGuard
{
    public static final int CLEARED = 0;
    public static final int SESSIONEXPIRED = 1;
    public static final int NOTLOGGEDIN = 2;
    public static final int HACKERATTEMPT = 5;
    public static final String AUTHENTICATION_FIELD_NAME = "authenticationStatus";
    private static final SecurityGuard singletonInstance;
    
    static {
        singletonInstance = new SecurityGuard();
    }
    
    public static SecurityGuard getGuard() {
        return SecurityGuard.singletonInstance;
    }
    
    public boolean cleared(final HttpServletRequest request, final ServiceData outData) {
        final int authenticationStatus = this.authenticate(request, outData);
        outData.addValue("authenticationStatus", Integer.toString(authenticationStatus));
        return authenticationStatus == 0;
    }
    
    int authenticate(final HttpServletRequest request, final ServiceData outData) {
        if (!AP.securityEnabled) {
            final Object o = request.getSession().getAttribute(AP.loggedInUserFieldName);
            if (o == null) {
                return 1;
            }
            return 0;
        }
        else {
            final Object o = request.getSession().getAttribute(AP.loggedInUserFieldName);
            if (o == null) {
                outData.addMessage("exilSessionExpired", new String[0]);
                return 1;
            }
            Cookie c = null;
            final Cookie[] reqCookie = request.getCookies();
            if (reqCookie != null) {
                Cookie[] array;
                for (int length = (array = reqCookie).length, i = 0; i < length; ++i) {
                    final Cookie cookie = array[i];
                    if (cookie.getName().equals(AP.loggedInUserFieldName)) {
                        c = cookie;
                        break;
                    }
                }
            }
            if (c == null) {
                outData.addMessage("exilNotLoggedIn", new String[0]);
                return 2;
            }
            final String cookieUserID = c.getValue();
            final String sessionUserID = o.toString();
            if (!cookieUserID.equals(sessionUserID)) {
                outData.addMessage("exilNotLoggedIn", new String[0]);
                return 5;
            }
            return 0;
        }
    }
    
    public boolean cleared(final HttpServletRequest request, final ServiceData inData, final ServiceData outData) {
        final HttpSession session = request.getSession(true);
        boolean allOk = false;
        String token = request.getHeader("X-CSRF-Token");
        if (token == null) {
            token = inData.getValue("X-CSRF-Token");
        }
        Spit.out("CSRF = " + token);
        if (token != null) {
            allOk = (session.getAttribute(token) != null);
            Spit.out("Security cleared with " + allOk);
        }
        else {
            Cookie c = null;
            final Cookie[] reqCookie = request.getCookies();
            if (reqCookie != null) {
                Cookie[] array;
                for (int length = (array = reqCookie).length, i = 0; i < length; ++i) {
                    final Cookie cookie = array[i];
                    if (cookie.getName().equals(AP.loggedInUserFieldName)) {
                        c = cookie;
                        break;
                    }
                }
            }
            if (c == null) {
                allOk = false;
            }
            else {
                final String sessionUserId = c.getValue();
                final Object o = session.getAttribute(AP.loggedInUserFieldName);
                allOk = (o != null && sessionUserId.equals(o.toString()));
            }
        }
        if (!allOk) {
            outData.addMessage("exilNotLoggedIn", new String[0]);
        }
        return allOk;
    }
}
