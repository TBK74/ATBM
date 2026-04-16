package vn.edu.hcmuaf.fit.util;

import java.util.regex.Pattern;

public class HtmlSanitizer {

    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<script.*?>.*?<\\/script>", Pattern.DOTALL);

    public static String sanitize(String html) {
        if (html == null) return null;
        return SCRIPT_PATTERN.matcher(html).replaceAll("");
    }
}