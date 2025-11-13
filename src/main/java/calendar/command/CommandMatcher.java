package calendar.command;

/**
 * Interface for command matchers in the Chain of Responsibility pattern.
 *
 * <p>Each matcher attempts to parse a command string and return a CommandInterface
 * if it matches, or null if it doesn't match. Matchers are chained together
 * and tried in sequence until one succeeds.
 */
public interface CommandMatcher {

  /**
   * Attempts to match and parse the command string.
   *
   * @param input the command string to parse
   * @return the parsed command if matched, null if not matched
   */
  CommandInterface tryMatch(String input);
}
