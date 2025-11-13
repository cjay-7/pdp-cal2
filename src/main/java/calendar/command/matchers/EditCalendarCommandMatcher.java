package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.EditCalendarCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches commands to edit a calendar property.
 * Format: edit calendar --name &lt;name-of-calendar&gt;
 *         --property &lt;property-name&gt; &lt;new-value&gt;
 *
 * <p>Examples:
 * - edit calendar --name Work --property timezone America/Los_Angeles
 * - edit calendar --name Personal --property name PersonalLife
 */
public class EditCalendarCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*edit\\s+calendar\\s+--name\\s+(\\S+)\\s+--property\\s+(\\S+)\\s+(\\S+)\\s*$",
      Pattern.CASE_INSENSITIVE
  );

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (!matcher.matches()) {
      return null;
    }

    String calendarName = matcher.group(1);
    String property = matcher.group(2);
    String newValue = matcher.group(3);

    return new EditCalendarCommand(calendarName, property, newValue);
  }
}
