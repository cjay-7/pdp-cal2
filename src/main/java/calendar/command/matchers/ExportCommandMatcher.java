package calendar.command.matchers;

import calendar.command.CommandInterface;
import calendar.command.CommandMatcher;
import calendar.command.ExportCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches "export cal" commands.
 */
public class ExportCommandMatcher implements CommandMatcher {
  private static final Pattern PATTERN =
      Pattern.compile("^\\s*export\\s+cal\\s+(.+?)\\s*$", Pattern.CASE_INSENSITIVE);

  @Override
  public CommandInterface tryMatch(String input) {
    Matcher matcher = PATTERN.matcher(input);
    if (matcher.matches()) {
      String fileName = matcher.group(1).trim();
      return new ExportCommand(fileName);
    }
    return null;
  }
}
