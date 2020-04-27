package com.afweb.util;

import java.util.ArrayList;


public class StringTag {

    public static final int NullString = -1;
    private ArrayList strDataString = new ArrayList();
    private int TextCnt = 0;

    public StringTag() {
    }

    public StringTag(StringBuffer TagStringBuilder) {
        ParseTags(TagStringBuilder);
    }

    public boolean IsEmpty() {
        int Cnt = strDataString.size();
        if (Cnt == 0) {
            return true;
        }
        return false;
    }

    public String GetFirstText(String TagName, int offset) {
        TextCnt = -1;
        return GetNextText(TagName, offset);
    }

    public String GetNextText(String TagName, int offset) {
        int i;
        String RetString;
        RetString = GetNextText(TagName);
        if (RetString.length() == 0) {
            return RetString;
        }
        for (i = 0; i < offset; i++) {
            RetString = GetNextText();
        }
        return RetString;
    }

    public String GetNextText(String TagName) {
        int i, MaxSize;
        String RetStr = "";

        TextCnt++;
        MaxSize = strDataString.size();
        if (MaxSize == 0) {
            return RetStr;
        }

        if (TagName.length() == 0) {
            return RetStr;
        }

        for (i = TextCnt; i < MaxSize; i++) {
            RetStr = (String) strDataString.get(i);

            if (RetStr.charAt(0) == '<') {
                continue;
            }

            if (RetStr.indexOf(TagName.toString()) != NullString) {
                TextCnt = i;
                return RetStr;
            }
        }
        return "";
    }

    public String GetNextText() {
        int i, MaxSize;

        String RetStr = "";
        TextCnt++;
        MaxSize = strDataString.size();
        if (MaxSize == 0) {
            return RetStr;
        }

        for (i = TextCnt; i < MaxSize; i++) {
            RetStr = (String) strDataString.get(i);

            if (RetStr.charAt(0) == '<') {
                continue;
            }

            TextCnt = i;
            return RetStr;
        }
        return "";
    }

    public String GetPrevText() {
        int MaxSize;

        String RetStr = "";
        MaxSize = strDataString.size();
        if (MaxSize == 0) {
            return RetStr;
        }
        while (TextCnt >= 0) {
            TextCnt--;
            RetStr = (String) strDataString.get(TextCnt);
            if (RetStr.charAt(0) == '<') {
                continue;
            }
            return RetStr;
        }
        return "";
    }

    public String GetFirstTag(String TagName, int offset) {
        TextCnt = -1;
        return GetNextTag(TagName, offset);
    }

    public String GetNextTag(String TagName, int offset) {
        int i;
        String RetString;
        RetString = GetNextTag(TagName);
        if (RetString.length() == 0) {
            return RetString;
        }

        if (offset > 0) {
            for (i = 0; i < offset; i++) {
                RetString = GetNextTag();
            }
        } else if (offset < 0) {
            offset = -offset;
            for (i = 0; i < offset; i++) {
                RetString = GetPrevTag();
            }
        }
        return RetString;
    }

    public String GetNextTag(String TagName) {
        int i, MaxSize;
        String RetStr = "";

        TextCnt++;
        MaxSize = strDataString.size();
        if (MaxSize == 0) {
            return RetStr;
        }

        if (TagName.length() == 0) {
            return RetStr;
        }

        for (i = TextCnt; i < MaxSize; i++) {
            RetStr = (String) strDataString.get(i);

            if (RetStr.indexOf(TagName.toString()) != NullString) {
                TextCnt = i;
                return RetStr;
            }
        }
        return "";
    }

    public String GetNextTag() {
        int MaxSize;

        String RetStr = "";
        MaxSize = strDataString.size();
        if (MaxSize == 0) {
            return RetStr;
        }

        if (TextCnt < MaxSize) {
            TextCnt++;
            RetStr = (String) strDataString.get(TextCnt);
            return RetStr;
        }
        return "";
    }

    public String GetPrevTag() {
        int MaxSize;

        String RetStr = "";
        MaxSize = strDataString.size();
        if (MaxSize == 0) {
            return RetStr;
        }
        if (TextCnt >= 0) {
            TextCnt--;
            RetStr = (String) strDataString.get(TextCnt);
            return RetStr;
        }
        return "";
    }

    private boolean ParseTags(StringBuffer TagStringBuilder) {

        // using string for find index. One copy only
        String TagString = TagStringBuilder.toString();
        TagString = TagString.replaceAll("&nbsp;", " ");
        TagString = TagString.replaceAll("&amp;", "&");
        TagString = TagString.replaceAll("\t", " ");

        int pos1, pos2, PosCur;
        PosCur = 0;
        String TagStr;
        while (true) {
            if ((pos1 = TagString.indexOf("<", PosCur)) == NullString) {
                break;
            }
            if ((pos2 = TagString.indexOf(">", PosCur)) == NullString) {
                break;
            }
            if (pos1 == PosCur) {
                TagStr = TagString.substring(pos1, (pos2) + 1);
                PosCur = pos2 + 1;
            } else {
                TagStr = TagString.substring(PosCur, (pos1));
                PosCur = pos1;
            }
            strDataString.add(TagStr);
        }
        if (strDataString.size() == 0) {
            strDataString.add(TagString);
        }
        return true;
    }

    public static StringBuffer removeTags(StringBuffer TagStringBuilder) {

        int pos1, pos2;

        while (true) {
            String TagString = TagStringBuilder.toString();
            if ((pos1 = TagString.indexOf("<", 0)) == NullString) {
                break;
            }
            if ((pos2 = TagString.indexOf(">", 0)) == NullString) {
                break;
            }
            TagStringBuilder = TagStringBuilder.delete(pos1, (pos2) + 1);
        }
        return TagStringBuilder;
    }

    public static String replaceAll(String oldStr, String newStr, String inString) {
        while (true) {
            int start = inString.indexOf(oldStr);
            if (start == -1) {
                return inString;
            }
            inString = replace(oldStr, newStr, inString);
        }

    }

    public static String replace(String oldStr, String newStr, String inString) {
        int start = inString.indexOf(oldStr);
        if (start == -1) {
            return inString;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(inString.substring(0, start));
        sb.append(newStr);
        sb.append(inString.substring(start + oldStr.length()));
        return sb.toString();
    }

    public static String[] splitComma(String inputStr) {
        if (inputStr == null) {
            return null;
        }
        if (inputStr.charAt(inputStr.length() - 1) == ',') {
            inputStr += ",End";
        }
        return inputStr.split(",");
    }
//
//    public static void main(String[] args) {
//
//
//        String testStr = "<ht&nbsp;ml>&amp;<head&nbsp;>\t<title&nbsp;>";
//        StringTag strTagTest = new StringTag(new StringBuffer(testStr));
//        if (strTagTest.IsEmpty() == false) {
//            String strT;
//            strT = strTagTest.GetFirstText("Finance", 0);
//            strT = strTagTest.GetNextTag();
//            strT = strTagTest.GetPrevTag();
//        }
//    }
}
