package com.ericsson.ecut.collector.cpp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExUtil {   
	/**
     * Searches for regular expression pattern in string text and returns
     * an array of all matches.
     * 
     * @param pattern A regular expression.
     * @param text    String to search.
     * @return
     */
    public static String[] findAll(String pattern, String text)
    {
        return findAll(pattern, text, 0);
    }

    /**
     * Searches for regular expression pattern in string text and returns
     * an array of all subgroup matches.
     * 
     * @param pattern    A regular expression.
     * @param text       String to search.
     * @param groupIndex Subgroup index. 1 is the match of the first subgroup.
     * @return
     */
	public static String[] findAll(String pattern, String text, int groupIndex)
    {
        Pattern regexPattern = Pattern.compile(pattern, Pattern.MULTILINE);

        // Find all matches.
        List<String> matches = new ArrayList<String>();
        Matcher matcher = regexPattern.matcher(text);
        while (matcher.find())
        {
            matches.add(matcher.group(groupIndex));
        }

        // Convert to array.
        String[] result = new String[matches.size()];
        matches.toArray(result);

        return result;
    }
}
