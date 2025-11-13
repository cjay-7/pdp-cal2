package calendar.model;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents the core logic of the calendar application.
 *
 * <p>This model manages events, event series, queries, and exports.
 * All operations are validated to ensure no duplicate events
 * (events with same subject, start time, end time).
 *
 * <p>The model is completely decoupled from I/O - it does not know
 * about user input, command parsing, or displaying output.
 *
 * <p>DESIGN CHECK: Model Interface
 * This interface represents the contract for the calendar's data model.
 * It defines all operations that can be performed on events and series
 * without exposing implementation details. It follows the Interface
 * Segregation Principle by providing a focused API for calendar operations.
 */
public interface CalendarModelInterface {

  /**
   * Creates a single event in the calendar.
   *
   * @param event the event to create
   * @return true if created successfully, false if duplicate exists
   * @throws IllegalArgumentException if event is invalid
   * 
   *                                  DESIGN CHECK: Single Event Creation
   *                                  This method demonstrates the core single
   *                                  event creating logic.
   *                                  It validates uniqueness based on subject,
   *                                  start time, and end time.
   *                                  The event is only added if it doesn't
   *                                  already exist in the model.
   */
  boolean createEvent(EventInterface event);

  /**
   * Creates an event series.
   *
   * @param series the series configuration
   * @return true if created successfully, false if any duplicate exists
   * @throws IllegalArgumentException if series is invalid
   * 
   *                                  DESIGN CHECK: Series Creation
   *                                  This method demonstrates the core series
   *                                  creating logic.
   *                                  It generates all occurrences based on the
   *                                  template, weekdays, and
   *                                  either occurrences count or end date. All
   *                                  events are validated
   *                                  for uniqueness before being added to ensure
   *                                  atomicity.
   */
  boolean createEventSeries(EventSeries series);

  /**
   * Edits a single event instance.
   *
   * @param eventId the ID of the event to edit
   * @param spec    the edit specification
   * @return true if successful, false if edit would create duplicate
   * 
   *         DESIGN CHECK: Single Event Editing
   *         This method demonstrates the core logic to modify the property of a
   *         single event.
   *         It uses the EditSpec pattern to allow partial updates, only applying
   *         specified changes while preserving other attributes.
   */
  boolean editEvent(UUID eventId, EditSpec spec);

  /**
   * Edits events in a series starting from a specific date forward.
   *
   * @param seriesId the series ID
   * @param fromDate the start date (inclusive)
   * @param spec     the edit specification
   * @return true if successful, false if edit would create duplicates
   * 
   *         DESIGN CHECK: Events Editing
   *         This method demonstrates the core logic to modify the property of
   *         several events
   *         in a series starting from a specific time forward. It applies changes
   *         to
   *         multiple occurrences atomically, preserving date-specific times while
   *         allowing series-wide modifications.
   */
  boolean editSeriesFrom(UUID seriesId, LocalDate fromDate, EditSpec spec);

  /**
   * Edits all events in a series.
   *
   * @param seriesId the series ID
   * @param spec     the edit specification
   * @return true if successful, false if edit would create duplicates
   */
  boolean editEntireSeries(UUID seriesId, EditSpec spec);

  /**
   * Gets all events on a specific date.
   *
   * @param date the date to query
   * @return list of events on that date, in chronological order
   */
  List<EventInterface> getEventsOnDate(LocalDate date);

  /**
   * Gets all events that overlap with a time range.
   *
   * @param startDateTime start of range (inclusive)
   * @param endDateTime   end of range (inclusive)
   * @return list of events in range, in chronological order
   * 
   *         DESIGN CHECK: Querying Events
   *         This method demonstrates the core logic to query events based on
   *         specific parameters.
   *         It finds events that overlap with the given range using interval
   *         intersection
   *         logic: event overlaps if eventStart < rangeEnd AND eventEnd >
   *         rangeStart.
   */
  List<EventInterface> getEventsInRange(LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Gets all events in the calendar.
   *
   * @return list of all events, in chronological order
   */
  List<EventInterface> getAllEvents();

  /**
   * * Checks if user is busy at a specific time.
   * *
   * * @param dateTime the time to check
   * * @return true if any event overlaps with this time
   *
   */
  boolean isBusy(LocalDateTime dateTime);

  /**
   * Exports the calendar to a CSV file compatible with Google Calendar.
   *
   * @param filePath the path to write the CSV file
   * @throws IOException if file writing fails
   * 
   *                     DESIGN CHECK: Exporting events & Model File IO & Platform
   *                     Independence
   *                     This method demonstrates how a CSV file is created and
   *                     saved in the filesystem.
   *                     It uses java.nio.file.Path which provides
   *                     platform-independent path handling.
   *                     The model accepts a Path parameter instead of a String
   *                     file path to demonstrate
   *                     platform independence while still requiring file I/O for
   *                     export functionality.
   */
  void exportToCsv(Path filePath) throws IOException;

  /**
   * Finds an event by ID.
   *
   * @param eventId the event ID
   * @return the event, or null if not found
   */
  EventInterface findEventById(UUID eventId);

  /**
   * Finds an event by its unique properties (subject + start + end).
   * Used when searching for events to edit.
   *
   * @param subject       the subject
   * @param startDateTime the start time
   * @param endDateTime   the end time
   * @return the event, or null if not found
   * 
   *         DESIGN CHECK: Event Identification
   *         This method demonstrates the core logic to uniquely identify an
   *         event.
   *         Events are identified by their business key: subject + start + end,
   *         which allows finding events without requiring their UUID.
   */
  EventInterface findEventByProperties(String subject, LocalDateTime startDateTime,
      LocalDateTime endDateTime);
}
