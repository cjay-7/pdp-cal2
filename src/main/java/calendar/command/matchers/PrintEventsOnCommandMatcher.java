package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.PrintEventsOnCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "print events on" commands.
 */
public class PrintEventsOnCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN =
      Pattern.compile("^\\s*print\\s+events\\s+on\\s+(\\S+)\\s*$", Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      return new PrintEventsOnCommand(matcher.group(1));
    }
    return null;
  }
}
