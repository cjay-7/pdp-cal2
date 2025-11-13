package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.ExitCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches exit commands.
 */
public class ExitCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN =
      Pattern.compile("^\\s*exit\\s*$", Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      return new ExitCommand();
    }
    return null;
  }
}
