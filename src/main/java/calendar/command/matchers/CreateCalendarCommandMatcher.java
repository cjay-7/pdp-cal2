package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.CreateCalendarCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches commands to create a new calendar.
 * Format: create calendar --name &lt;calName&gt; --timezone area/location
 *
 * <p>Examples:
 * - create calendar --name Work --timezone America/New_York
 * - create calendar --name Personal --timezone Europe/Paris
 */
public class CreateCalendarCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*create\\s+calendar\\s+--name\\s+(\\S+)\\s+--timezone\\s+(\\S+)\\s*$",
      Pattern.CASE_INSENSITIVE
  );

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (!matcher.matches()) {
      return null;
    }

    String name = matcher.group(1);
    String timezone = matcher.group(2);

    return new CreateCalendarCommand(name, timezone);
  }
}
