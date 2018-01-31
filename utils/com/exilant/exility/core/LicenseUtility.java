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

import java.util.Calendar;
import sun.misc.BASE64Decoder;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import sun.misc.BASE64Encoder;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import java.util.Collections;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LicenseUtility
{
    String licensePath;
    boolean isServerLicense;
    boolean isPageGeneratorLicense;
    int useCountLeft;
    String expiresOn;
    
    public LicenseUtility() {
        this.licensePath = "exilant.lic";
        this.isServerLicense = false;
        this.isPageGeneratorLicense = false;
        this.useCountLeft = 0;
        this.expiresOn = "";
    }
    
    public String getHexString(final byte[] b) throws Exception {
        String result = "";
        for (final byte element : b) {
            result = String.valueOf(result) + Integer.toString((element & 0xFF) + 256, 16).substring(1);
        }
        return result;
    }
    
    public void createLicense(final Date startDate, final Date endDate, final int useCount, final String hostnameText, final String domainText, final String macaddressText) throws Exception {
        final String dateFormat = "M/d/yyyy H:m:s a";
        final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String computerName = "";
        String macAddress = "";
        if (hostnameText == "" || domainText == "") {
            computerName = InetAddress.getLocalHost().getCanonicalHostName().replaceFirst("\\.", "");
        }
        else {
            computerName = String.valueOf(hostnameText) + domainText;
        }
        if (macaddressText == "") {
            final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (final NetworkInterface netint : Collections.list(nets)) {
                if (netint.getName().startsWith("eth") && netint.getDisplayName().contains("Ethernet") && netint.getHardwareAddress() != null) {
                    macAddress = this.getHexString(netint.getHardwareAddress()).toUpperCase();
                }
            }
        }
        else {
            macAddress = macaddressText;
        }
        final String password = String.valueOf(computerName) + macAddress;
        final String initVector = String.valueOf(macAddress) + computerName;
        final char fieldSeparator = '\r';
        final String dataToWrite = sdf.format(startDate) + fieldSeparator + sdf.format(endDate) + fieldSeparator + useCount;
        final byte[] plainText = dataToWrite.getBytes();
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(password.getBytes());
        final SecretKeySpec keySpec = new SecretKeySpec(algorithm.digest(), "AES");
        algorithm.reset();
        algorithm.update(initVector.getBytes());
        final IvParameterSpec ivSpec = new IvParameterSpec(algorithm.digest());
        cipher.init(1, keySpec, ivSpec);
        final byte[] results = cipher.doFinal(plainText);
        final BASE64Encoder encoder = new BASE64Encoder();
        final String licenseData = encoder.encode(results);
        final BufferedWriter licenseWriter = new BufferedWriter(new FileWriter("exilant.lic"));
        licenseWriter.write(licenseData);
        licenseWriter.close();
    }
    
    public boolean isValidLicense(final boolean isServer) throws Exception {
        if (isServer) {
            this.isServerLicense = true;
        }
        else {
            this.isPageGeneratorLicense = true;
        }
        final String dateFormat = "M/d/yyyy H:m:s a";
        final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        final String computerName = InetAddress.getLocalHost().getCanonicalHostName().replaceFirst("\\.", "");
        String macAddress = "";
        final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (final NetworkInterface netint : Collections.list(nets)) {
            if (netint.getName().startsWith("eth") && netint.getDisplayName().contains("Ethernet") && netint.getHardwareAddress() != null) {
                macAddress = this.getHexString(netint.getHardwareAddress()).toUpperCase();
            }
        }
        final String password = String.valueOf(computerName) + macAddress;
        final String initVector = String.valueOf(macAddress) + computerName;
        final char fieldSeparator = '\r';
        final StringBuffer fileData = new StringBuffer(1000);
        final BufferedReader reader = new BufferedReader(new FileReader("exilant.lic"));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            final String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        final String readlicenseData = fileData.toString();
        final BASE64Decoder decoder = new BASE64Decoder();
        final byte[] forDec = decoder.decodeBuffer(readlicenseData);
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(password.getBytes());
        final SecretKeySpec keySpec = new SecretKeySpec(algorithm.digest(), "AES");
        algorithm.reset();
        algorithm.update(initVector.getBytes());
        final IvParameterSpec ivSpec = new IvParameterSpec(algorithm.digest());
        cipher.init(2, keySpec, ivSpec);
        final byte[] results = cipher.doFinal(forDec);
        final String decryptedData = new String(results);
        boolean isValidLicense = false;
        final String[] licenseDetails = decryptedData.split(new StringBuilder(String.valueOf(fieldSeparator)).toString());
        final Calendar currentCal = Calendar.getInstance();
        final Calendar startCal = Calendar.getInstance();
        startCal.setTime(sdf.parse(licenseDetails[0]));
        final Calendar endCal = Calendar.getInstance();
        endCal.setTime(sdf.parse(licenseDetails[1]));
        int newUseCount = Integer.parseInt(licenseDetails[2]);
        if (startCal.before(currentCal) && currentCal.before(endCal) && newUseCount > 0) {
            isValidLicense = true;
            this.expiresOn = licenseDetails[1];
            if (this.isPageGeneratorLicense && !this.isServerLicense) {
                --newUseCount;
                final String updatedLicenseData = String.valueOf(licenseDetails[0]) + fieldSeparator + licenseDetails[1] + fieldSeparator + newUseCount;
                final byte[] plainText = updatedLicenseData.getBytes();
                final Cipher newCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                final MessageDigest newAlgorithm = MessageDigest.getInstance("MD5");
                newAlgorithm.reset();
                newAlgorithm.update(password.getBytes());
                final SecretKeySpec newKeySpec = new SecretKeySpec(newAlgorithm.digest(), "AES");
                newAlgorithm.reset();
                newAlgorithm.update(initVector.getBytes());
                final IvParameterSpec newIvSpec = new IvParameterSpec(newAlgorithm.digest());
                newCipher.init(1, newKeySpec, newIvSpec);
                final byte[] newResults = newCipher.doFinal(plainText);
                final BASE64Encoder newEncoder = new BASE64Encoder();
                final String newLicenseData = newEncoder.encode(newResults);
                final BufferedWriter licenseWriter = new BufferedWriter(new FileWriter("exilant.lic"));
                licenseWriter.write(newLicenseData);
                licenseWriter.close();
            }
        }
        return isValidLicense;
    }
    
    public int getUseCountLeft() {
        return this.useCountLeft;
    }
    
    public long getDaysLeft() throws Exception {
        final String dateFormat = "M/d/yyyy H:m:s a";
        final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        final Date expireDate = sdf.parse(this.expiresOn);
        final Date currentDate = new Date();
        return (expireDate.getTime() - currentDate.getTime()) / 1000L * 60L * 60L * 24L;
    }
}
