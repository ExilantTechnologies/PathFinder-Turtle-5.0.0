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

public class Exility
{
    public static String version() {
        return "3.1.6 19-Jul-2014";
    }
    
    public static void main(final String[] args) {
        if (args.length != 3) {
            System.err.println("Usage : Exility <rootPathName> <userId> <seviceName>");
        }
        final String rootFolderName = args[0];
        final String userId = args[1];
        final String serviceId = args[2];
        ResourceManager.loadAllResources(rootFolderName, null);
        final ServiceAgent agent = new ServiceAgent();
        final ServiceData inData = new ServiceData();
        inData.addValue("serviceId", serviceId);
        inData.addValue("userId", userId);
        final ServiceData outData = new ServiceData();
        agent.serve(serviceId, userId, inData, outData);
    }
}
