import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import calendar.model.EditSpec;
import calendar.model.Event;
import calendar.model.EventInterface;
import calendar.model.EventSeries;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that changing start time on series triggers split logic branches.
 */
public class CalendarModelSeriesSplitTest {
  private CalendarModelInterface model;

  /**
   * Initializes a fresh model before each test.
   */
  @Before
  public void setUp() {
    model = new CalendarModel();
  }

  /**
   * editSeriesFrom with start change should preserve each date and split from series.
   */
  @Test
  public void testEditSeriesFromStartChangeSplits() {
    EventInterface template = new Event("Standup",
        LocalDateTime.of(2025, 6, 2, 9, 0),
        LocalDateTime.of(2025, 6, 2, 9, 30),
        null, null, false, UUID.randomUUID(), null);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 3, false);
    model.createEventSeries(series);

    
    LocalDate fromDate = LocalDate.of(2025, 6, 4);
    LocalDateTime newStart = LocalDateTime.of(2025, 6, 4, 8, 45);
    EditSpec spec = new EditSpec(null, newStart, null, null, null, null);

    assertTrue(model.editSeriesFrom(seriesId, fromDate, spec));

    
    List<EventInterface> eventsOnFrom = model.getEventsOnDate(fromDate);
    assertTrue(eventsOnFrom.stream().anyMatch(e -> e.getSubject().equals("Standup")
        && e.getStartDateTime().toLocalTime().getHour() == 8
        && e.getStartDateTime().toLocalTime().getMinute() == 45));
  }

  /**
   * editEntireSeries with start change should adjust all dates and split.
   */
  @Test
  public void testEditEntireSeriesStartChangeSplits() {
    EventInterface template = new Event("Daily",
        LocalDateTime.of(2025, 6, 2, 10, 0),
        LocalDateTime.of(2025, 6, 2, 11, 0),
        null, null, false, UUID.randomUUID(), null);
    UUID seriesId = UUID.randomUUID();
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    EventSeries series = new EventSeries(seriesId, template, weekdays, null, 2, false);
    model.createEventSeries(series);

    LocalDateTime newStart = LocalDateTime.of(2025, 6, 2, 9, 30);
    EditSpec spec = new EditSpec(null, newStart, null, null, null, null);
    assertTrue(model.editEntireSeries(seriesId, spec));

    
    List<EventInterface> events = model.getAllEvents();
    long count = events.stream().filter(e -> e.getSubject().equals("Daily")
        && e.getStartDateTime().toLocalTime().getHour() == 9
        && e.getStartDateTime().toLocalTime().getMinute() == 30).count();
    assertTrue(count >= 2);
  }
}
