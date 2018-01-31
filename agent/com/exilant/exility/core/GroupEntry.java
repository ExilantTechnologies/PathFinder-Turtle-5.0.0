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

class GroupEntry extends ServiceEntry
{
    ServiceEntry[] serviceEntries;
    
    GroupEntry() {
        this.serviceEntries = null;
    }
    
    @Override
    void serve(final DataCollection dc) {
        if (this.serviceEntries == null || this.serviceEntries.length == 0) {
            dc.addMessage("exilError", "No services defined for group entry " + this.name + " in serviceList.xml");
            return;
        }
        ServiceEntry[] serviceEntries;
        for (int length = (serviceEntries = this.serviceEntries).length, i = 0; i < length; ++i) {
            final ServiceEntry entry = serviceEntries[i];
            entry.serve(dc);
        }
    }
}
