package calendar.util;

/**
 * Utility class for common string operations.
 */
public class StringUtils {

  /**
   * Strips surrounding quotes (single or double) from a string if present.
   * Trims whitespace before checking for quotes.
   *
   * @param s the string to process
   * @return the string without surrounding quotes
   */
  public static String stripQuotes(String s) {
    String t = s.trim();
    if ((t.startsWith("\"") && t.endsWith("\"")) || (t.startsWith("'") && t.endsWith("'"))) {
      return t.substring(1, t.length() - 1);
    }
    return t;
  }
}
