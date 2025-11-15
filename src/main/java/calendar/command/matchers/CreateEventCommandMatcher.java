package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.CreateEventCommand;
import calendar.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "create event...from...to" commands.
 */
public class CreateEventCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN =
      Pattern.compile("^\\s*create\\s+event\\s+(.+?)\\s+from\\s+(\\S+)\\s+to\\s+(\\S+)\\s*$",
          Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      String subject = StringUtils.stripQuotes(matcher.group(1));
      String from = matcher.group(2);
      String to = matcher.group(3);
      return new CreateEventCommand(subject, from, to);
    }
    return null;
  }
}
