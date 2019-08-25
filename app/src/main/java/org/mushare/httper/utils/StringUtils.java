package org.mushare.httper.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static List<CharSequence> SplitString(String text) {
        List<CharSequence> result = new ArrayList<>();
        if (text == null) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
                if (sb.length() > 1024) {
                    sb.setLength(sb.length() - 1);
                    result.add(sb.toString());
                    sb.setLength(0);
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
                result.add(sb.toString());
            }
        } catch (IOException ignored) {
        }
        return result;
    }
}