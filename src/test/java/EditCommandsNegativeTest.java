import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.command.EditEventsCommand;
import calendar.command.EditSeriesCommand;
import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import calendar.model.Event;
import calendar.model.EventInterface;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Negative-path tests for edit commands.
 */
public class EditCommandsNegativeTest {
  private CalendarManager manager;
  private CalendarModelInterface model;

  /**
   * Initializes a fresh model for each test.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.setCurrentCalendar("TestCalendar");
    model = manager.getCurrentCalendar().getModel();

  }

  /**
   * EditEventsCommand should return false when no matching event exists.
   */
  @Test
  public void testEditEventsNotFound() throws IOException {
    EditEventsCommand cmd = new EditEventsCommand("location", "Missing",
        "2025-06-01T09:00", "Room");
    boolean ok = cmd.execute(manager, new calendar.view.ConsoleView(System.out));
    assertFalse(ok);
  }

  /**
   * EditSeriesCommand should fail when change would create duplicates.
   */
  @Test
  public void testEditSeriesDuplicatePrevention() throws IOException {
    
    EventInterface clash = new Event("Clash",
        LocalDateTime.of(2025, 6, 2, 9, 0),
        LocalDateTime.of(2025, 6, 2, 9, 30),
        null, null, false, UUID.randomUUID(), null);
    model.createEvent(clash);

    
    EventInterface template = new Event("Standup",
        LocalDateTime.of(2025, 6, 2, 9, 0),
        LocalDateTime.of(2025, 6, 2, 9, 30),
        null, null, false, UUID.randomUUID(), null);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    calendar.model.EventSeries series =
        new calendar.model.EventSeries(seriesId, template, weekdays, null, 1, false);
    model.createEventSeries(series);

    
    EditSeriesCommand cmd = new EditSeriesCommand("subject", "Standup",
        "2025-06-02T09:00", "Clash");
    boolean ok = cmd.execute(manager, new calendar.view.ConsoleView(System.out));
    assertFalse(ok);
  }
}
