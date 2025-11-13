package calendar.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of a calendar model.
 *
 * <p>This model stores events and series, validates uniqueness,
 * and provides query operations. It does not perform any I/O
 * or command parsing - that is handled by other components.
 */
public class CalendarModel implements CalendarModelInterface {

  // Storage: Using Set for fast uniqueness checking
  private final Set<EventInterface> events;

  // Map seriesId -> EventSeries configuration
  private final Map<UUID, EventSeries> seriesConfigs;

  /**
   * Creates a new empty calendar model.
   */
  public CalendarModel() {
    this.events = new HashSet<>();
    this.seriesConfigs = new HashMap<>();
  }

  @Override
  public boolean createEvent(EventInterface event) {
    Objects.requireNonNull(event, "Event cannot be null");

    // Check for duplicate (uniqueness: subject + start + end)
    if (events.contains(event)) {
      return false;
    }

    events.add(event);
    return true;
  }

  @Override
  public boolean createEventSeries(EventSeries series) {
    Objects.requireNonNull(series, "Series cannot be null");

    // Generate all occurrences
    List<EventInterface> occurrences = generateOccurrences(series);

    // Check if any duplicate exists
    for (EventInterface occurrence : occurrences) {
      if (events.contains(occurrence)) {
        return false;
      }
    }

    // All are unique, add them all
    for (EventInterface occurrence : occurrences) {
      events.add(occurrence);
    }

    // Store series configuration
    seriesConfigs.put(series.getSeriesId(), series);

    return true;
  }

  @Override
  public boolean editEvent(UUID eventId, EditSpec spec) {
    Objects.requireNonNull(eventId, "Event ID cannot be null");
    Objects.requireNonNull(spec, "Edit specification cannot be null");

    EventInterface event = findEventById(eventId);
    if (event == null) {
      return false;
    }

    // Apply modifications
    EventInterface modified = applyEditSpec(event, spec);

    // Check if modified event would be duplicate (and different from original)
    if (!modified.equals(event) && events.contains(modified)) {
      return false;
    }

    // Remove old, add new
    events.remove(event);
    events.add(modified);

    return true;
  }

  @Override
  public boolean editSeriesFrom(UUID seriesId, LocalDate fromDate, EditSpec spec) {
    Objects.requireNonNull(seriesId, "Series ID cannot be null");
    Objects.requireNonNull(fromDate, "From date cannot be null");
    Objects.requireNonNull(spec, "Edit specification cannot be null");

    EventSeries series = seriesConfigs.get(seriesId);
    if (series == null) {
      return false;
    }

    // Find all events in series from the given date forward
    List<EventInterface> toEdit = events.stream()
        .filter(e -> e.getSeriesId().isPresent())
        .filter(e -> e.getSeriesId().get().equals(seriesId))
        .filter(e -> !e.getStartDateTime().toLocalDate().isBefore(fromDate))
        .collect(Collectors.toList());

    if (toEdit.isEmpty()) {
      return false;
    }

    // If editing start time, events must leave the series
    boolean mustSplit = spec.getNewStart() != null;

    // Apply edits to each event
    List<EventInterface> modifiedEvents = new ArrayList<>();
    for (EventInterface event : toEdit) {
      // If editing start time, preserve each event's date but use new time
      EditSpec eventSpec = spec;
      if (spec.getNewStart() != null && spec.getNewEnd() == null) {
        // Extract time component from new start and apply to this event's date
        java.time.LocalTime newTime = spec.getNewStart().toLocalTime();
        java.time.LocalDate eventDate = event.getStartDateTime().toLocalDate();
        LocalDateTime adjustedStart = LocalDateTime.of(eventDate, newTime);
        eventSpec = new EditSpec(
            spec.getNewSubject(),
            adjustedStart,
            null, // Will be calculated in applyEditSpec to preserve duration
            spec.getNewDescription(),
            spec.getNewLocation(),
            spec.getNewStatus());
      }
      EventInterface modified = applyEditSpec(event, eventSpec);

      // If splitting, remove series ID by creating a new Event without seriesId
      if (mustSplit) {
        modified = new Event(
            modified.getSubject(),
            modified.getStartDateTime(),
            modified.getEndDateTime(),
            modified.getDescription().orElse(null),
            modified.getLocation().orElse(null),
            modified.isPrivate(),
            modified.getId(),
            null  // Remove series ID
        );
      }

      // Check for duplicates
      if (!modified.equals(event) && events.contains(modified)) {
        return false;
      }

      modifiedEvents.add(modified);
    }

    // All valid, apply changes
    for (int i = 0; i < toEdit.size(); i++) {
      events.remove(toEdit.get(i));
      events.add(modifiedEvents.get(i));
    }

    // If split, update series configuration
    if (mustSplit && !toEdit.isEmpty()) {
      // Events that were split no longer belong to series
      // Series config remains for remaining events
    }

    return true;
  }

  @Override
  public boolean editEntireSeries(UUID seriesId, EditSpec spec) {
    Objects.requireNonNull(seriesId, "Series ID cannot be null");
    Objects.requireNonNull(spec, "Edit specification cannot be null");

    EventSeries series = seriesConfigs.get(seriesId);
    if (series == null) {
      return false;
    }

    // Find all events in series
    List<EventInterface> toEdit = events.stream()
        .filter(e -> e.getSeriesId().isPresent())
        .filter(e -> e.getSeriesId().get().equals(seriesId))
        .collect(Collectors.toList());

    if (toEdit.isEmpty()) {
      return false;
    }

    // If editing start time, events must leave the series
    boolean mustSplit = spec.getNewStart() != null;

    // Apply edits to each event
    List<EventInterface> modifiedEvents = new ArrayList<>();
    for (EventInterface event : toEdit) {
      // If editing start time, preserve each event's date but use new time
      EditSpec eventSpec = spec;
      if (spec.getNewStart() != null && spec.getNewEnd() == null) {
        // Extract time component from new start and apply to this event's date
        java.time.LocalTime newTime = spec.getNewStart().toLocalTime();
        java.time.LocalDate eventDate = event.getStartDateTime().toLocalDate();
        LocalDateTime adjustedStart = LocalDateTime.of(eventDate, newTime);
        eventSpec = new EditSpec(
            spec.getNewSubject(),
            adjustedStart,
            null, // Will be calculated in applyEditSpec to preserve duration
            spec.getNewDescription(),
            spec.getNewLocation(),
            spec.getNewStatus());
      }
      EventInterface modified = applyEditSpec(event, eventSpec);

      // If splitting, remove series ID by creating a new Event without seriesId
      if (mustSplit) {
        modified = new Event(
            modified.getSubject(),
            modified.getStartDateTime(),
            modified.getEndDateTime(),
            modified.getDescription().orElse(null),
            modified.getLocation().orElse(null),
            modified.isPrivate(),
            modified.getId(),
            null  // Remove series ID
        );
      }

      // Check for duplicates
      if (!modified.equals(event) && events.contains(modified)) {
        return false;
      }

      modifiedEvents.add(modified);
    }

    // All valid, apply changes
    for (int i = 0; i < toEdit.size(); i++) {
      events.remove(toEdit.get(i));
      events.add(modifiedEvents.get(i));
    }

    // If split, remove series config
    if (mustSplit) {
      seriesConfigs.remove(seriesId);
    }

    return true;
  }

  @Override
  public List<EventInterface> getEventsOnDate(LocalDate date) {
    Objects.requireNonNull(date, "Date cannot be null");

    return events.stream()
        .filter(e -> {
          LocalDate eventStart = e.getStartDateTime().toLocalDate();
          LocalDate eventEnd = e.getEndDateTime().toLocalDate();
          return !date.isBefore(eventStart) && !date.isAfter(eventEnd);
        })
        .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
        .collect(Collectors.toList());
  }

  @Override
  public List<EventInterface> getAllEvents() {
    return events.stream()
        .sorted(Comparator.comparing(EventInterface::getStartDateTime)
            .thenComparing(EventInterface::getEndDateTime))
        .collect(Collectors.toList());
  }

  @Override
  public List<EventInterface> getEventsInRange(LocalDateTime startDateTime,
                                               LocalDateTime endDateTime) {
    Objects.requireNonNull(startDateTime, "Start date-time cannot be null");
    Objects.requireNonNull(endDateTime, "End date-time cannot be null");

    return events.stream()
        .filter(e -> {
          // Event overlaps if: eventStart < rangeEnd AND eventEnd > rangeStart
          return e.getStartDateTime().isBefore(endDateTime)
              && e.getEndDateTime().isAfter(startDateTime);
        })
        .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    Objects.requireNonNull(dateTime, "Date-time cannot be null");

    return events.stream()
        .anyMatch(e -> {
          // Event is busy at this time if it starts before or at time and ends after time
          return !e.getStartDateTime().isAfter(dateTime)
              && e.getEndDateTime().isAfter(dateTime);
        });
  }

  @Override
  public void exportToCsv(Path filePath) throws IOException {
    Objects.requireNonNull(filePath, "File path cannot be null");

    // Sort events and delegate CSV formatting to utility to avoid duplication
    List<EventInterface> sortedEvents = events.stream()
        .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
        .collect(java.util.stream.Collectors.toList());

    String csv = calendar.util.CsvExporter.toCsv(sortedEvents);
    Files.writeString(filePath, csv);
  }

  @Override
  public EventInterface findEventById(UUID eventId) {
    Objects.requireNonNull(eventId, "Event ID cannot be null");

    return events.stream()
        .filter(e -> e.getId().equals(eventId))
        .findFirst()
        .orElse(null);
  }

  @Override
  public EventInterface findEventByProperties(String subject, LocalDateTime startDateTime,
                                              LocalDateTime endDateTime) {
    Objects.requireNonNull(subject, "Subject cannot be null");
    Objects.requireNonNull(startDateTime, "Start date-time cannot be null");
    Objects.requireNonNull(endDateTime, "End date-time cannot be null");

    // Create a temporary event to use equals() for matching
    // Note: This is a bit of a hack, but works with our equals() implementation
    return events.stream()
        .filter(e -> e.getSubject().equals(subject.trim()))
        .filter(e -> e.getStartDateTime().equals(startDateTime))
        .filter(e -> e.getEndDateTime().equals(endDateTime))
        .findFirst()
        .orElse(null);
  }

  // ========== Private Helper Methods ==========

  /**
   * Generates all occurrences for an event series.
   *
   * @param series the series configuration
   * @return list of all event occurrences
   */
  private List<EventInterface> generateOccurrences(EventSeries series) {
    List<EventInterface> occurrences = new ArrayList<>();

    LocalDateTime startTime = series.getTemplate().getStartDateTime();
    LocalDateTime endTime = series.getTemplate().getEndDateTime();

    // Get time components (same for all occurrences)
    LocalDate templateDate = startTime.toLocalDate();
    int startHour = startTime.getHour();
    int startMinute = startTime.getMinute();
    // end hour/minute not needed; duration captures the offset
    int durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();

    LocalDate currentDate = templateDate;
    Set<DayOfWeek> weekdays = series.getWeekdays();
    int occurrenceCount = 0;

    // Continue generating until we hit the limit
    while (true) {
      // Check if we should generate on this date
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime eventStart = LocalDateTime.of(currentDate,
            java.time.LocalTime.of(startHour, startMinute));
        LocalDateTime eventEnd = eventStart.plusMinutes(durationMinutes);

        // Create event for this occurrence
        EventInterface occurrence = new Event(
            series.getTemplate().getSubject(),
            eventStart,
            eventEnd,
            series.getTemplate().getDescription().orElse(null),
            series.getTemplate().getLocation().orElse(null),
            series.getTemplate().isPrivate(),
            UUID.randomUUID(), // Each occurrence gets its own ID
            series.getSeriesId());

        occurrences.add(occurrence);
        occurrenceCount++;

        // Check if we've hit the occurrence limit
        if (series.getOccurrences() != null && occurrenceCount >= series.getOccurrences()) {
          break;
        }
      }

      // Check if we've passed the end date
      if (series.usesEndDate() && currentDate.isAfter(series.getEndDate())) {
        break;
      }

      // Move to next day
      currentDate = currentDate.plusDays(1);

      // Safety check: prevent infinite loop
      if (currentDate.isAfter(templateDate.plusYears(10))) {
        break;
      }
    }

    return occurrences;
  }

  /**
   * Applies an edit specification to an event.
   *
   * @param event the event to modify
   * @param spec  the edit specification
   * @return a new event with modifications applied
   */
  private EventInterface applyEditSpec(EventInterface event, EditSpec spec) {
    String newSubject = (spec.getNewSubject() != null)
        ? spec.getNewSubject()
        : event.getSubject();

    LocalDateTime newStart = (spec.getNewStart() != null)
        ? spec.getNewStart()
        : event.getStartDateTime();

    LocalDateTime newEnd;
    if (spec.getNewEnd() != null) {
      // Explicit end time change
      newEnd = spec.getNewEnd();
    } else if (spec.getNewStart() != null && spec.getNewEnd() == null) {
      // Only start time changed - preserve duration
      java.time.Duration duration = java.time.Duration.between(
          event.getStartDateTime(), event.getEndDateTime());
      newEnd = newStart.plus(duration);
    } else {
      // No changes to start/end
      newEnd = event.getEndDateTime();
    }

    String newDescription = (spec.getNewDescription() != null)
        ? spec.getNewDescription()
        : event.getDescription().orElse(null);

    String newLocation = (spec.getNewLocation() != null)
        ? spec.getNewLocation()
        : event.getLocation().orElse(null);

    Boolean newIsPrivate = (spec.getNewStatus() != null)
        ? spec.getNewStatus().isPrivate()
        : event.isPrivate() ? Boolean.TRUE : Boolean.FALSE;

    // Keep same ID and series ID
    return event.withModifications(
        newSubject, newStart, newEnd, newDescription, newLocation, newIsPrivate,
        event.getSeriesId().orElse(null));
  }
}