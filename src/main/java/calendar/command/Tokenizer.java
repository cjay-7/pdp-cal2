package calendar.command;

import java.util.Arrays;
import java.util.List;

/**
 * Tokenizer helper for parsing input strings.
 */
public class Tokenizer {
  /**
   * Tokenizes a string into a list of words.
   *
   * @param input the input string
   * @return list of tokens
   */
  public List<String> tokenize(String input) {
    if (input == null || input.trim().isEmpty()) {
      return java.util.Collections.emptyList();
    }
    return Arrays.asList(input.trim().split("\\s+"));
  }
}
