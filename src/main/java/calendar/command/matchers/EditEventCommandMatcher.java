package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.EditEventCommand;
import calendar.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "edit event" commands.
 */
public class EditEventCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*edit\\s+event\\s+(\\S+)\\s+(.+?)\\s+from\\s+(\\S+)\\s+to\\s+(\\S+)\\s+with\\s+"
      + "(.+?)\\s*$", Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      String property = matcher.group(1);
      String subject = StringUtils.stripQuotes(matcher.group(2));
      String from = matcher.group(3);
      String to = matcher.group(4);
      String value = StringUtils.stripQuotes(matcher.group(5));
      return new EditEventCommand(property, subject, from, to, value);
    }
    return null;
  }
}
