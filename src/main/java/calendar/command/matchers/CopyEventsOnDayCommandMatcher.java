package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.CopyEventsOnDayCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches commands to copy all events on a specific day.
 * Format: copy events on &lt;dateString&gt; --target &lt;calendarName&gt; to &lt;dateString&gt;
 *
 * <p>Examples:
 * - copy events on 2025-06-01 --target Work to 2025-07-01
 * - copy events on 2025-06-15 --target Personal to 2025-08-01
 */
public class CopyEventsOnDayCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*copy\\s+events\\s+on\\s+(\\S+)\\s+--target\\s+(\\S+)\\s+to\\s+(\\S+)\\s*$",
      Pattern.CASE_INSENSITIVE
  );

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (!matcher.matches()) {
      return null;
    }

    String sourceDate = matcher.group(1);
    String targetCalendarName = matcher.group(2);
    String targetDate = matcher.group(3);

    return new CopyEventsOnDayCommand(sourceDate, targetCalendarName, targetDate);
  }
}
