package calendar.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Immutable implementation of a calendar event.
 *
 * <p>An event must have a subject and valid start/end times where end > start.
 * Events may optionally have a description, location, and privacy status.
 *
 * <p>Two events are considered equal if they have the same subject,
 * start time, and end time. This is enforced by the model to prevent
 * duplicate events.
 */
public class Event implements EventInterface {

  /**
   * Default start hour for all-day events (8:00 AM).
   */
  public static final int ALL_DAY_EVENT_START_HOUR = 8;

  /**
   * Default end hour for all-day events (5:00 PM).
   */
  public static final int ALL_DAY_EVENT_END_HOUR = 17;

  private final String subject;
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final Optional<String> description;
  private final Optional<String> location;
  private final boolean isPrivate;
  private final UUID eventId;
  private final Optional<UUID> seriesId;

  /**
   * Creates a new event with all required and optional fields.
   *
   * @param subject       the subject (cannot be null or empty)
   * @param startDateTime the start time (cannot be null)
   * @param endDateTime   the end time (cannot be null, must be after start)
   * @param description   optional description
   * @param location      optional location
   * @param isPrivate     true if private, false if public
   * @param eventId       unique ID for this event
   * @param seriesId      series ID if part of a series, or null
   * @throws IllegalArgumentException if subject is empty or end <= start
   */
  public Event(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
               String description, String location, boolean isPrivate, UUID eventId,
               UUID seriesId) {

    Objects.requireNonNull(subject, "Subject cannot be null");
    Objects.requireNonNull(startDateTime, "Start time cannot be null");
    Objects.requireNonNull(endDateTime, "End time cannot be null");

    if (subject.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject cannot be empty");
    }

    if (!endDateTime.isAfter(startDateTime)) {
      throw new IllegalArgumentException("End time must be after start time");
    }

    this.subject = subject.trim();
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = Optional.ofNullable(description);
    this.location = Optional.ofNullable(location);
    this.isPrivate = isPrivate;
    this.eventId = Objects.requireNonNull(eventId, "Event ID cannot be null");
    this.seriesId = Optional.ofNullable(seriesId);
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  @Override
  public Optional<String> getDescription() {
    return description;
  }

  @Override
  public Optional<String> getLocation() {
    return location;
  }

  @Override
  public boolean isPrivate() {
    return isPrivate;
  }

  @Override
  public UUID getId() {
    return eventId;
  }

  @Override
  public Optional<UUID> getSeriesId() {
    return seriesId;
  }

  @Override
  public boolean isAllDayEvent() {
    LocalTime morning = LocalTime.of(ALL_DAY_EVENT_START_HOUR, 0);
    LocalTime evening = LocalTime.of(ALL_DAY_EVENT_END_HOUR, 0);

    return startDateTime.toLocalDate().equals(endDateTime.toLocalDate())
        && startDateTime.toLocalTime().equals(morning)
        && endDateTime.toLocalTime().equals(evening);
  }

  @Override
  public EventInterface withModifications(String newSubject, LocalDateTime newStart,
                                          LocalDateTime newEnd, String newDescription,
                                          String newLocation, Boolean newStatus, UUID newSeriesId) {

    String updatedSubject = (newSubject != null) ? newSubject : this.subject;
    LocalDateTime updatedStart = (newStart != null) ? newStart : this.startDateTime;
    LocalDateTime updatedEnd = (newEnd != null) ? newEnd : this.endDateTime;
    Optional<String> updatedDescription = (newDescription != null)
        ? Optional.of(newDescription)
        : this.description;
    Optional<String> updatedLocation = (newLocation != null)
        ? Optional.of(newLocation)
        : this.location;
    boolean updatedStatus = (newStatus != null) ? newStatus : this.isPrivate;
    Optional<UUID> updatedSeriesId = (newSeriesId != null)
        ? Optional.of(newSeriesId)
        : this.seriesId;

    return new Event(updatedSubject, updatedStart, updatedEnd, updatedDescription.orElse(null),
        updatedLocation.orElse(null), updatedStatus, this.eventId,
        updatedSeriesId.orElse(null));
  }

  /**
   * Two events are equal if they have the same subject, start time, and end time.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof EventInterface)) {
      return false;
    }

    EventInterface other = (EventInterface) obj;
    return Objects.equals(this.subject, other.getSubject())
        && Objects.equals(this.startDateTime, other.getStartDateTime())
        && Objects.equals(this.endDateTime, other.getEndDateTime());
  }

  /**
   * Hash code based on subject, start, and end.
   *
   * <p>DESIGN CHECK: Model Leaking Implementation Details
   * The equals() and hashCode() methods reveal that events are uniquely identified
   * by subject + startDateTime + endDateTime. This "leaks" the business key
   * implementation to the public API. This is necessary because the Set of
   * EventInterface instances in CalendarModel relies on equals() for duplicate
   * detection, and clients may need to use equals() to check event identity.
   * However, this exposes the fact that description, location, and other fields
   * don't contribute to uniqueness.
   */
  @Override
  public int hashCode() {
    return Objects.hash(subject, startDateTime, endDateTime);
  }

  @Override
  public String toString() {
    return String.format("Event[id=%s, subject=%s, start=%s, end=%s]", eventId, subject,
        startDateTime, endDateTime);
  }

}