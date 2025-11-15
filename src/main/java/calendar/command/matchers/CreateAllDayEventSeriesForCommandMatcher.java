package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.CreateAllDayEventSeriesCommand;
import calendar.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "create event...on...repeats...for N times" commands.
 */
public class CreateAllDayEventSeriesForCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*create\\s+event\\s+(.+?)\\s+on\\s+(\\S+)\\s+repeats\\s+(\\S+)\\s+for\\s+(\\d+)\\s+"
      + "times\\s*$", Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      String subject = StringUtils.stripQuotes(matcher.group(1));
      String dateString = matcher.group(2);
      String weekdays = matcher.group(3);
      int occurrences = Integer.parseInt(matcher.group(4));
      return new CreateAllDayEventSeriesCommand(subject, dateString, weekdays, occurrences, null,
          false);
    }
    return null;
  }
}
