import static org.junit.Assert.assertTrue;

import calendar.command.EditEventsCommand;
import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.model.EventSeries;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests EditEventsCommand start-time change forward in a series.
 */
public class EditEventsCommandSplitTest {
  private CalendarManager manager;
  private CalendarModelInterface model;

  /**
   * Initializes a fresh model before each test.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.setCurrentCalendar("TestCalendar");
    model = manager.getCurrentCalendar().getModel();

  }

  /**
   * Verifies forward start-time change on a series instance.
   */
  @Test
  public void testForwardStartChange() throws IOException {
    EventInterface template = new Event("Standup",
        LocalDateTime.of(2025, 6, 2, 9, 0),
        LocalDateTime.of(2025, 6, 2, 9, 30),
        null, null, false, UUID.randomUUID(), null);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.TUESDAY);
    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 3, false);
    model.createEventSeries(series);

    
    EditEventsCommand cmd = new EditEventsCommand("start", "Standup",
        "2025-06-02T09:00", "2025-06-02T08:45");
    assertTrue(cmd.execute(manager, new calendar.view.ConsoleView(System.out)));

    List<calendar.model.EventInterface> events =
        model.getEventsOnDate(java.time.LocalDate.of(2025, 6, 2));
    assertTrue(events.stream().anyMatch(e -> e.getStartDateTime().toLocalTime().getHour() == 8
        && e.getStartDateTime().toLocalTime().getMinute() == 45));
  }
}
