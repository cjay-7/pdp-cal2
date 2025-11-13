package calendar.model;

import java.time.LocalDateTime;

/**
 * Specifies modifications to an event.
 *
 * <p>Only non-null fields indicate modifications to make.
 * Used by edit operations to represent what should change.
 */
public class EditSpec {
  private final String newSubject;
  private final LocalDateTime newStart;
  private final LocalDateTime newEnd;
  private final String newDescription;
  private final String newLocation;
  private final EventStatus newStatus;

  /**
   * Creates an edit specification.
   * All fields except the specified ones should be null.
   *
   * @param newSubject     the new subject, or null to keep current
   * @param newStart       the new start time, or null to keep current
   * @param newEnd         the new end time, or null to keep current
   * @param newDescription the new description, or null to keep current
   * @param newLocation    the new location, or null to keep current
   * @param newStatus      the new status, or null to keep current
   */
  public EditSpec(String newSubject, LocalDateTime newStart, LocalDateTime newEnd,
                  String newDescription, String newLocation, EventStatus newStatus) {
    this.newSubject = newSubject;
    this.newStart = newStart;
    this.newEnd = newEnd;
    this.newDescription = newDescription;
    this.newLocation = newLocation;
    this.newStatus = newStatus;
  }

  public String getNewSubject() {
    return newSubject;
  }

  public LocalDateTime getNewStart() {
    return newStart;
  }

  public LocalDateTime getNewEnd() {
    return newEnd;

  }

  public String getNewDescription() {
    return newDescription;
  }

  public String getNewLocation() {
    return newLocation;
  }

  public EventStatus getNewStatus() {
    return newStatus;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof EditSpec)) {
      return false;
    }
    EditSpec other = (EditSpec) obj;
    return java.util.Objects.equals(newSubject, other.newSubject)
        && java.util.Objects.equals(newStart, other.newStart)
        && java.util.Objects.equals(newEnd, other.newEnd)
        && java.util.Objects.equals(newDescription, other.newDescription)
        && java.util.Objects.equals(newLocation, other.newLocation)
        && java.util.Objects.equals(newStatus, other.newStatus);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(newSubject, newStart, newEnd, newDescription, newLocation,
        newStatus);
  }
}
