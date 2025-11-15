package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.CreateAllDayEventCommand;
import calendar.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "create event...on date" commands.
 */
public class CreateAllDayEventCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN =
      Pattern.compile("^\\s*create\\s+event\\s+(.+?)\\s+on\\s+(\\S+)\\s*$",
          Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      String subject = StringUtils.stripQuotes(matcher.group(1));
      String dateString = matcher.group(2);
      return new CreateAllDayEventCommand(subject, dateString);
    }
    return null;
  }
}
