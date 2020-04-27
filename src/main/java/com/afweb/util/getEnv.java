/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
https://community.talend.com/t5/Design-and-Development/resolved-How-to-get-an-environment-variable-from-the-system-into/td-p/108454

 */
package com.afweb.util;

import java.io.*;
import java.util.*;

public class getEnv {

    static String value = null;

    public static String getEnvValue(String key) {
        try {
            Properties p = getEnv.getEnvVars();
            value = p.getProperty(key);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean checkLocalPC() {
        String OS = System.getProperty("os.name").toLowerCase();
        if ((OS.indexOf("windows 2000") > -1) || (OS.indexOf("windows 10") > -1)
                || (OS.indexOf("windows xp") > -1) || (OS.indexOf("windows 7") > -1)) {
            return true;
        } else {
            // it is Unix os.
            return false;
        }
    }

    public static boolean checkWinOS() {
        if (CKey.LocalPCflag == false) {
            return false;
        }
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("windows 9") > -1) {
            return true;

        } else if ((OS.indexOf("windows 2000") > -1) || (OS.indexOf("windows 10") > -1)
                || (OS.indexOf("windows xp") > -1) || (OS.indexOf("windows 7") > -1)) {
            return true;
        } else {
            // it is Unix os.
            return false;
        }
    }

    public static Properties getEnvVars() throws Throwable {
        Process p = null;
        Properties envVars = new Properties();
        Runtime r = Runtime.getRuntime();
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("windows 9") > -1) {
            p = r.exec("command.com /c set");

        } else if ((OS.indexOf("windows 2000") > -1) || (OS.indexOf("windows 10") > -1)
                || (OS.indexOf("windows xp") > -1) || (OS.indexOf("windows 7") > -1)) {
            p = r.exec("cmd.exe /c set");
        } else {
            // it is Unix os.
            p = r.exec("env");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            int idx = line.indexOf('=');
            String key = line.substring(0, idx);
            String value = line.substring(idx + 1);
            envVars.setProperty(key, value);
        }
        return envVars;
    }
}
