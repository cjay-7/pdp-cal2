package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.PrintEventsRangeCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "print events from...to" commands.
 */
public class PrintEventsRangeCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN =
      Pattern.compile("^\\s*print\\s+events\\s+from\\s+(\\S+)\\s+to\\s+(\\S+)\\s*$",
          Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      return new PrintEventsRangeCommand(matcher.group(1), matcher.group(2));
    }
    return null;
  }
}
