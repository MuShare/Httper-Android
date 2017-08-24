package org.mushare.httper.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dklap on 8/23/2017.
 */

public class StringUtils {
    static public List<String> splitLines(String string, int num) {
        List<String> texts = new ArrayList<>();
//        StringBuilder stringBuilder = new StringBuilder();
//        int count = num;
//        for (int i = 0; i < string.length(); i++) {
//            char c = string.charAt(i);
//            if (c != '\n') {
//                stringBuilder.append(c);
//                continue;
//            }
//            count--;
//            if (count == 0) {
//                count = num;
//                texts.add(stringBuilder.toString());
//                stringBuilder.setLength(0);
//            } else {
//                stringBuilder.append('\n');
//            }
//        }
//        if (stringBuilder.length() > 0) texts.add(stringBuilder.toString());
        int begin = 0, end = -1;
        while (end != string.length()) {
            for (int i = 0; i < num; i++) {
                end = string.indexOf('\n', end + 1);
                if (end == -1) {
                    end = string.length();
                    break;
                }
            }
            texts.add(string.substring(begin, end));
            begin = end + 1;
        }
        return texts;
    }
}
