package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.ShowStatusCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "show status on" commands.
 */
public class ShowStatusCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN =
      Pattern.compile("^\\s*show\\s+status\\s+on\\s+(\\S+)\\s*$", Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      return new ShowStatusCommand(matcher.group(1));
    }
    return null;
  }
}
