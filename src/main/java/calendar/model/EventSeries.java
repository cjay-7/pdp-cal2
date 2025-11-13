package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a recurring event series configuration.
 *
 * <p>A series defines a template for generating multiple events.
 * Events in a series repeat on specified weekdays.
 * All events in a series must start and end on the same day.
 *
 * <p>When an event series is modified (e.g., start time changes),
 * the events affected by the change may be split from the series.
 *
 * <p>A series can be defined to repeat for a fixed number of occurrences
 * or until a specific end date.
 */
public class EventSeries {
  private final UUID seriesId;
  private final EventInterface template;
  private final Set<DayOfWeek> weekdays;
  private final LocalDate endDate;
  private final Integer occurrences;
  private final boolean usesEndDate;

  /**
   * Creates a series that repeats until an end date.
   *
   * @param seriesId    the unique series ID
   * @param template    the first event with start/end time
   * @param weekdays    the days of the week to repeat on (e.g., MTW for Mon, Tue, Wed)
   * @param endDate     the last date to generate occurrences (inclusive)
   * @param occurrences the number of occurrences (null if using endDate)
   * @param usesEndDate true if using end date, false if using occurrences
   * @throws IllegalArgumentException if template ends on different day than start
   */
  public EventSeries(UUID seriesId, EventInterface template, Set<DayOfWeek> weekdays,
                     LocalDate endDate, Integer occurrences, boolean usesEndDate) {
    this.seriesId = seriesId;
    this.template = template;
    this.weekdays = weekdays;
    this.endDate = endDate;
    this.occurrences = occurrences;
    this.usesEndDate = usesEndDate;
  }

  /**
   * Gets the unique series ID.
   *
   * @return the series ID
   */
  public UUID getSeriesId() {
    return seriesId;
  }

  /**
   * Gets the template event (first occurrence).
   *
   * @return the template event
   */
  public EventInterface getTemplate() {
    return template;
  }

  /**
   * Gets the weekdays this series repeats on.
   *
   * @return set of weekdays
   */
  public Set<DayOfWeek> getWeekdays() {
    return weekdays;
  }

  /**
   * Gets the end date for occurrences (if using end date).
   *
   * @return the end date, or null if using occurrences
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * Gets the fixed number of occurrences (if using occurrences).
   *
   * @return the number of occurrences, or null if using end date
   */
  public Integer getOccurrences() {
    return occurrences;
  }

  /**
   * Checks if this series uses an end date vs occurrences.
   *
   * @return true if using end date, false if using occurrences
   */
  public boolean usesEndDate() {
    return usesEndDate;
  }
}
