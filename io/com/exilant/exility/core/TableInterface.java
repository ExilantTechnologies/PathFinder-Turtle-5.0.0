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

public interface TableInterface
{
    String getName();
    
    int readbasedOnKey(final DataCollection p0, final DbHandle p1) throws ExilityException;
    
    int read(final DataCollection p0, final DbHandle p1, final String p2, final String p3) throws ExilityException;
    
    int massRead(final DataCollection p0, final DbHandle p1, final String p2, final Grid p3) throws ExilityException;
    
    int filter(final DataCollection p0, final DbHandle p1, final String p2, final Condition[] p3, final String p4, final String p5, final String p6, final String p7, final boolean p8) throws ExilityException;
    
    int insert(final DataCollection p0, final DbHandle p1) throws ExilityException;
    
    int insertFromGrid(final DataCollection p0, final DbHandle p1, final Grid p2) throws ExilityException;
    
    int update(final DataCollection p0, final DbHandle p1) throws ExilityException;
    
    int updateFromGrid(final DataCollection p0, final DbHandle p1, final Grid p2) throws ExilityException;
    
    int massUpdate(final DataCollection p0, final DbHandle p1, final Condition[] p2, final String p3, final String p4) throws ExilityException;
    
    int save(final DataCollection p0, final DbHandle p1) throws ExilityException;
    
    int persist(final DataCollection p0, final DbHandle p1, final String p2) throws ExilityException;
    
    int saveFromGrid(final DataCollection p0, final DbHandle p1, final Grid p2) throws ExilityException;
    
    int delete(final DataCollection p0, final DbHandle p1) throws ExilityException;
    
    int deleteFromGrid(final DataCollection p0, final DbHandle p1, final Grid p2) throws ExilityException;
    
    int massDelete(final DataCollection p0, final DbHandle p1, final Condition[] p2) throws ExilityException;
    
    int bulkAction(final DataCollection p0, final DbHandle p1, final Grid p2, final String p3) throws ExilityException;
}
