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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class LoadTestExecutor
{
    static ExecutorService executorService;
    private static final int HANDLERS = 5;
    
    public static void main(final String[] args) throws Throwable {
        test();
    }
    
    public static void test() {
        LoadTestExecutor.executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 1000; ++i) {
            final LoadHandler test = new LoadHandler();
            LoadTestExecutor.executorService.execute(test);
        }
        shutdownAndAwaitTermination(LoadTestExecutor.executorService);
    }
    
    static void shutdownAndAwaitTermination(final ExecutorService exService) {
        exService.shutdown();
        try {
            if (!exService.awaitTermination(60L, TimeUnit.SECONDS)) {
                exService.shutdownNow();
                if (!exService.awaitTermination(60L, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException ie) {
            exService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        while (!exService.isTerminated()) {}
        System.out.println("Threads completed");
    }
}
