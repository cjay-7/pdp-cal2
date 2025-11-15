package calendar.command;

import calendar.command.matchers.CopyEventCommandMatcher;
import calendar.command.matchers.CopyEventsOnDayCommandMatcher;
import calendar.command.matchers.CopyEventsRangeCommandMatcher;
import calendar.command.matchers.CreateAllDayEventCommandMatcher;
import calendar.command.matchers.CreateAllDayEventSeriesForCommandMatcher;
import calendar.command.matchers.CreateAllDayEventSeriesUntilCommandMatcher;
import calendar.command.matchers.CreateCalendarCommandMatcher;
import calendar.command.matchers.CreateEventCommandMatcher;
import calendar.command.matchers.CreateEventSeriesFromToForCommandMatcher;
import calendar.command.matchers.CreateEventSeriesFromToUntilCommandMatcher;
import calendar.command.matchers.EditCalendarCommandMatcher;
import calendar.command.matchers.EditEventCommandMatcher;
import calendar.command.matchers.EditEventsCommandMatcher;
import calendar.command.matchers.EditSeriesCommandMatcher;
import calendar.command.matchers.ExitCommandMatcher;
import calendar.command.matchers.ExportCommandMatcher;
import calendar.command.matchers.PrintAllEventsCommandMatcher;
import calendar.command.matchers.PrintEventsOnCommandMatcher;
import calendar.command.matchers.PrintEventsRangeCommandMatcher;
import calendar.command.matchers.ShowStatusCommandMatcher;
import calendar.command.matchers.UseCalendarCommandMatcher;
import java.util.Arrays;
import java.util.List;

/**
 * Parses command strings into CommandInterface objects using Chain of Responsibility pattern.
 *
 * <p>This parser uses a chain of CommandMatcher objects to try matching the input
 * against different command patterns. Each matcher is tried in sequence until one
 * successfully matches, or all matchers fail. This design eliminates the long
 * if-else chain and makes the parser more maintainable and extensible.
 */
public class CommandParser {
  private final List<CommandMatcher> matchers;

  /**
   * Creates a CommandParser with all registered matchers.
   * Matchers are ordered from most specific to least specific.
   */
  public CommandParser() {
    this.matchers = Arrays.asList(
        new ExitCommandMatcher(),
        new CreateCalendarCommandMatcher(),
        new EditCalendarCommandMatcher(),
        new UseCalendarCommandMatcher(),
        new CopyEventsRangeCommandMatcher(),
        new CopyEventsOnDayCommandMatcher(),
        new CopyEventCommandMatcher(),
        new PrintAllEventsCommandMatcher(),
        new PrintEventsOnCommandMatcher(),
        new PrintEventsRangeCommandMatcher(),
        new ShowStatusCommandMatcher(),
        new EditEventCommandMatcher(),
        new EditEventsCommandMatcher(),
        new EditSeriesCommandMatcher(),
        new ExportCommandMatcher(),
        new CreateEventSeriesFromToForCommandMatcher(),
        new CreateEventSeriesFromToUntilCommandMatcher(),
        new CreateAllDayEventSeriesForCommandMatcher(),
        new CreateAllDayEventSeriesUntilCommandMatcher(),
        new CreateAllDayEventCommandMatcher(),
        new CreateEventCommandMatcher()
    );
  }

  /**
   * Parses a command string into a CommandInterface object.
   *
   * @param input the command string to parse
   * @return the parsed command, or NoOpCommand if unrecognized
   */
  public CommandInterface parse(String input) {
    if (input == null || input.trim().isEmpty()) {
      return new NoOpCommand();
    }

    String trimmed = input.trim();

    for (CommandMatcher matcher : matchers) {
      CommandInterface command = matcher.tryMatch(trimmed);
      if (command != null) {
        return command;
      }
    }

    return new NoOpCommand(input);
  }
}