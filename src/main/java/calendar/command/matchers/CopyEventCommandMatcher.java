package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.CopyEventCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches commands to copy a single event.
 * Format: copy event &lt;eventName&gt; on &lt;dateStringTtimeString&gt;
 *         --target &lt;calendarName&gt; to &lt;dateStringTtimeString&gt;
 *
 * <p>Examples:
 * - copy event Meeting on 2025-06-01T09:00 --target Work
 *   to 2025-07-01T09:00
 * - copy event "Team Standup" on 2025-06-01T10:00 --target Personal
 *   to 2025-06-05T10:00
 */
public class CopyEventCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*copy\\s+event\\s+(.+?)\\s+on\\s+(\\S+)\\s+--target\\s+(\\S+)\\s+to\\s+(\\S+)\\s*$",
      Pattern.CASE_INSENSITIVE
  );

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (!matcher.matches()) {
      return null;
    }

    String eventName = matcher.group(1).trim();
    String sourceDateTime = matcher.group(2);
    String targetCalendarName = matcher.group(3);
    String targetDateTime = matcher.group(4);

    return new CopyEventCommand(eventName, sourceDateTime, targetCalendarName, targetDateTime);
  }
}
