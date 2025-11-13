package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.PrintAllEventsCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "print all events" commands.
 */
public class PrintAllEventsCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN =
      Pattern.compile("^\\s*print\\s+all\\s+events\\s*$", Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      return new PrintAllEventsCommand();
    }
    return null;
  }
}
