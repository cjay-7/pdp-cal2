package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.EditEventsCommand;
import calendar.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "edit events" commands.
 */
public class EditEventsCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN = Pattern.compile(
      "^\\s*edit\\s+events\\s+(\\S+)\\s+(.+?)\\s+from\\s+(\\S+)\\s+with\\s+(.+?)\\s*$",
      Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      String property = matcher.group(1);
      String subject = StringUtils.stripQuotes(matcher.group(2));
      String from = matcher.group(3);
      String value = StringUtils.stripQuotes(matcher.group(4));
      return new EditEventsCommand(property, subject, from, value);
    }
    return null;
  }
}
