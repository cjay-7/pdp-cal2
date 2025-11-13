package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.UseCalendarCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches commands to set the current active calendar.
 * Format: use calendar --name &lt;name-of-calendar&gt;
 *
 * <p>Examples:
 * - use calendar --name Work
 * - use calendar --name Personal
 */
public class UseCalendarCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*use\\s+calendar\\s+--name\\s+(\\S+)\\s*$",
      Pattern.CASE_INSENSITIVE
  );

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (!matcher.matches()) {
      return null;
    }

    String calendarName = matcher.group(1);
    return new UseCalendarCommand(calendarName);
  }
}
