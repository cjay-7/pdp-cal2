package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.CreateEventSeriesFromToCommand;
import calendar.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "create event...from...to...repeats...for N times" commands.
 */
public class CreateEventSeriesFromToForCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*create\\s+event\\s+(.+?)\\s+from\\s+(\\S+)\\s+to\\s+(\\S+)\\s+repeats\\s+(\\S+)\\s+"
      + "for\\s+(\\d+)\\s+times\\s*$", Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      String subject = StringUtils.stripQuotes(matcher.group(1));
      String from = matcher.group(2);
      String to = matcher.group(3);
      String weekdays = matcher.group(4);
      int occurrences = Integer.parseInt(matcher.group(5));
      return new CreateEventSeriesFromToCommand(subject, from, to, weekdays, occurrences, null,
          false);
    }
    return null;
  }
}
