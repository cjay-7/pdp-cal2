package calendar.model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a calendar event with immutable properties.
 *
 * <p>All events must have a unique combination of subject, start time, and end
 * time.
 * Events may optionally have a description, location, and privacy status.
 * Events can be standalone or part of a recurring series.
 */
public interface EventInterface {

  /**
   * Gets the subject of the event.
   *
   * @return the subject, never null or empty
   */
  String getSubject();

  /**
   * Gets the start date and time.
   *
   * @return the start datetime in EST
   */
  LocalDateTime getStartDateTime();

  /**
   * Gets the end date and time.
   *
   * @return the end datetime in EST
   */
  LocalDateTime getEndDateTime();

  /**
   * Gets the optional description.
   *
   * @return the description, or empty if not set
   */
  Optional<String> getDescription();

  /**
   * Gets the optional location.
   *
   * @return the location, or empty if not set
   */
  Optional<String> getLocation();

  /**
   * Gets the privacy status.
   *
   * @return true if private, false if public
   */
  boolean isPrivate();

  /**
   * Gets the unique ID of this event.
   *
   * @return the event ID, never null
   */
  UUID getId();

  /**
   * Gets the series ID if this event is part of a series.
   *
   * @return series ID, or empty if standalone
   */
  Optional<UUID> getSeriesId();

  /**
   * Checks if this event is an all-day event (8am-5pm on same day).
   *
   * @return true if all-day event
   */
  boolean isAllDayEvent();

  /**
   * Two events are equal if they have same subject, start time, and end time.
   * This is the identity for uniqueness checking as per assignment requirements.
   *
   * @param obj the object to compare
   * @return true if equal by subject + start + end
   */
  @Override
  boolean equals(Object obj);

  /**
   * Returns hash code based on subject, start, and end.
   * Must be consistent with equals().
   *
   * @return the hash code
   */
  @Override
  int hashCode();

  /**
   * Creates a copy of this event with modified fields.
   *
   * @param newSubject     the new subject, or null to keep current
   * @param newStart       the new start time, or null to keep current
   * @param newEnd         the new end time, or null to keep current
   * @param newDescription the new description, or null to keep current
   * @param newLocation    the new location, or null to keep current
   * @param newStatus      the new privacy status, or null to keep current
   * @param newSeriesId    the new series ID, or null to keep current
   * @return a new event with modified fields
   */
  EventInterface withModifications(String newSubject, LocalDateTime newStart, LocalDateTime newEnd,
                                   String newDescription, String newLocation, Boolean newStatus,
                                   UUID newSeriesId);
}