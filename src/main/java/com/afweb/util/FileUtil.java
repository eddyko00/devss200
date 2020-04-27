package com.afweb.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtil {

    public FileUtil() {
    }

    public static void FileAppenndWrite(BufferedWriter output, StringBuffer msg) {
        //declared here only to make visible to finally clause; generic reference
        if (output == null) {
            return;
        }
        try {
            try {
                output.append(msg.toString());
            } catch (IOException e) {
            }
        } finally {
            //flush and close both "output" by the caller
        }
    }

    public static StringBuffer FileAppendRead(String openfile) {
        StringBuffer msg = new StringBuffer();

        //declared here only to make visible to finally clause
        BufferedReader input = null;
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            input = new BufferedReader(new FileReader(openfile));
            String line = null; //not declared within while loop
            /*
             * readLine is a bit quirky :
             * it returns the content of a line MINUS the newline.
             * it returns null only for the END of the stream.
             * it returns an empty String if two newlines appear in a row.
             */
            while ((line = input.readLine()) != null) {
                msg.append(line);
                msg.append(CKey.COMMA);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    //flush and close both "input" and its underlying FileReader
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return msg;
    }

    // http://javaalmanac.com/egs/java.io/WriteToFile.html
    public static boolean FileWriteText(String writefile, StringBuffer msg) {
        //declared here only to make visible to finally clause; generic reference
        BufferedWriter output = null;
        try {
            try {
                output = new BufferedWriter(new FileWriter(writefile));
                if (msg != null) {
                    output.write(msg.toString());
                }
            } catch (IOException e) {
                return false;
            }
        } finally {
            //flush and close both "output" and its underlying FileWriter
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    return false;
                }
            }
        }
        return true;
    }

    public static StringBuffer FileReadText(String openfile) {
        StringBuffer msg = new StringBuffer();

        //declared here only to make visible to finally clause
        BufferedReader input = null;
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            input = new BufferedReader(new FileReader(openfile));
            String line = null; //not declared within while loop
            /*
             * readLine is a bit quirky :
             * it returns the content of a line MINUS the newline.
             * it returns null only for the END of the stream.
             * it returns an empty String if two newlines appear in a row.
             */
            while ((line = input.readLine()) != null) {
                msg.append(line);
                msg.append(System.getProperty("line.separator"));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    //flush and close both "input" and its underlying FileReader
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return msg;
    }

    // http://javaalmanac.com/egs/java.io/WriteToFile.html
    public static boolean FileWriteTextArray(String writefile, ArrayList retArray) {
        //declared here only to make visible to finally clause; generic reference
        BufferedWriter output = null;
        try {
            try {
                output = new BufferedWriter(new FileWriter(writefile));
                for (int i = 0; i < retArray.size(); i++) {
                    String text = (String) retArray.get(i);
                    output.write(text + "\r\n");
                }
                return true;
            } catch (IOException e) {
            }
        } finally {
            //flush and close both "output" and its underlying FileWriter
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                }
            }
        }
        return false;
    }

    // ignore # - comment line
    public static boolean FileReadTextArray(String openfile, ArrayList retArray) {
        //declared here only to make visible to finally clause
        BufferedReader input = null;
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            input = new BufferedReader(new FileReader(openfile));
            String line = null; //not declared within while loop
            /*
             * readLine is a bit quirky :
             * it returns the content of a line MINUS the newline.
             * it returns null only for the END of the stream.
             * it returns an empty String if two newlines appear in a row.
             */
            while ((line = input.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    if (line.charAt(0) != '#') {
                        retArray.add(line);
                    }
                }
            }
            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    //flush and close both "input" and its underlying FileReader
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static void FileDelete(String FileName) {
        try {
            File f = new File(FileName);
            f.delete();
        } catch (Exception ex) {
        }
    }
    
    public static String wideCardName = "";
    // must us synchronized becasue it access global variables

    public static synchronized void FileDeleteWildCard(String FileDir, String endWithName) {
        wideCardName = endWithName;
        File directory = new File(FileDir);

        File[] toBeDeleted = directory.listFiles(new FileFilter() {

            public boolean accept(File theFile) {
                if (theFile.isFile()) {
                    return theFile.getName().endsWith(wideCardName);
                }
                return false;
            }
        });


//        System.out.println(Arrays.toString(toBeDeleted));
        for (File deletableFile : toBeDeleted) {
            deletableFile.delete();
        }
    }

    public static boolean FileMove(String fileName, String directory) {
        // File (or directory) to be moved
        File file = new File(fileName);

        // Destination directory
        File dir = new File(directory);

        // Move file to new directory
        return file.renameTo(new File(dir, file.getName()));
    }

    public static boolean FileTest0Size(String openfile) {


        //declared here only to make visible to finally clause
        BufferedReader input = null;
        String line = null; //not declared within while loop        
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            input = new BufferedReader(new FileReader(openfile));

            line = input.readLine();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                if (input != null) {
                    //flush and close both "input" and its underlying FileReader
                    input.close();
                }
            } catch (IOException ex) {
            }
        }
        if ((line == null) || (line.length() == 0)) {
            return true;
        }
        return false;
    }

    public static boolean FileTest(String FileName) {
        File f = new File(FileName);
        return f.exists();
    }

    public static String[] FileDirectory(String directory) {
        String[] files = null;
        File f = new File(directory);
        if (f.isDirectory()) {
            files = f.list();
        }
        return files;
    }

    public static ArrayList FileDirectorySortDate(String directory) {

        File f = new File(directory);

        File[] files = f.listFiles();

        Arrays.sort(files, new Comparator() {

            public int compare(Object o1, Object o2) {

                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });

        ArrayList fileNameList = new ArrayList();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            fileNameList.add(name);
        }
        return fileNameList;
    }


}
