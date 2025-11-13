package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.CopyEventsRangeCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches commands to copy events in a date range.
 * Format: copy events between &lt;dateString&gt; and &lt;dateString&gt;
 *         --target &lt;calendarName&gt; to &lt;dateString&gt;
 *
 * <p>Examples:
 * - copy events between 2024-09-05 and 2024-12-18 --target Spring2025
 *   to 2025-01-08
 * - copy events between 2025-06-01 and 2025-06-30 --target Work
 *   to 2025-07-01
 */
public class CopyEventsRangeCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*copy\\s+events\\s+between\\s+(\\S+)\\s+and\\s+(\\S+)"
          + "\\s+--target\\s+(\\S+)\\s+to\\s+(\\S+)\\s*$",
      Pattern.CASE_INSENSITIVE
  );

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (!matcher.matches()) {
      return null;
    }

    String startDate = matcher.group(1);
    String endDate = matcher.group(2);
    String targetCalendarName = matcher.group(3);
    String targetStartDate = matcher.group(4);

    return new CopyEventsRangeCommand(startDate, endDate, targetCalendarName, targetStartDate);
  }
}
