/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */
package cn.vlabs.commons.credential;

import java.io.FileInputStream;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class VerifySig {

    public VerifySig() {
    }

    /*
     * String src_string is the source string String sigedText is the signed
     * string String CertNameStr is the certificate's path String sigalg is the
     * signature algorithom
     */

    public boolean veriSig(String src_string, String sigedText,
            String CertNameStr, String sigalg) {

        try {

            java.security.cert.CertificateFactory certfactory = CertificateFactory
                    .getInstance("X509");
            FileInputStream fin = new FileInputStream(CertNameStr);
            X509Certificate certificate = (X509Certificate) certfactory
                    .generateCertificate(fin);
            Signature sign = Signature.getInstance(sigalg);
            sign.initVerify(certificate);
            sign.update(src_string.getBytes("UTF-8"));
            byte[] ver_byte = hex2byte(sigedText);
            return sign.verify(ver_byte);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static String byte2hex(byte[] b) { 
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs + ":";
            }
        }
        return hs;
    }

    public static String[] split_string(String src_string, String split_str) {
        int j = 0;
        int k = 0;
        String[] temp = new String[1000];
        /*
         * store the number of src_string's spliting string to k
         */
        for (int i = 0; i < src_string.length(); i++) {
            if (src_string.substring(i).indexOf(split_str) > 0) {
                k++;
                i += src_string.substring(i).indexOf(split_str);
            }
        }
        while (src_string != null) {
            if (src_string.indexOf(":") > 0) {

                temp[j] = src_string.substring(0, src_string.indexOf(":"));
                src_string = src_string.substring(temp[j].length() + 1);
                j++;
            } else {
                temp[j] = src_string.substring(0);
                src_string = null;
            }

        }
        String[] stemp = new String[k + 1];
        for (int i = 0; i < k + 1; i++) {
            stemp[i] = temp[i];
        }
        return stemp;
    }

    public static byte[] hex2byte(String in) {
        String[] stemp = split_string(in, ":");
        byte[] temp = new byte[stemp.length]; 
        byte temp1 = (byte) 0xff, temp2 = (byte) 0xff;
        char a;
        for (int i = 0; i < stemp.length; i++) {
            a = stemp[i].charAt(0);
            if ((a <= '9') && (a >= '0')) {
                temp1 = (byte) (a - '0');
            } else if ((a <= 'f') && (a >= 'a')) {
                temp1 = (byte) (a - 'a' + 10);
            }
            a = stemp[i].charAt(1);
            if ((a <= '9') && (a >= '0')) {
                temp2 = (byte) (a - '0');
            } else if ((a <= 'f') && (a >= 'a')) {
                temp2 = (byte) (a - 'a' + 10);
            }
            temp1 = (byte) (temp1 * 16);
            temp[i] = (byte) (temp1 | temp2);
        }
        return temp;
    }

}
