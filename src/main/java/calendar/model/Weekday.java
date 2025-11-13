package calendar.model;

import java.time.DayOfWeek;

/**
 * Enum representing days of the week single-character abbreviations.
 * Uses standard abbreviations: M, T, W, R, F, S, U
 * (R for Thursday to avoid conflict with Tuesday).
 */
public enum Weekday {
  MONDAY('M', DayOfWeek.MONDAY), TUESDAY('T', DayOfWeek.TUESDAY),
  WEDNESDAY('W', DayOfWeek.WEDNESDAY), THURSDAY('R', DayOfWeek.THURSDAY),
  FRIDAY('F', DayOfWeek.FRIDAY), SATURDAY('S', DayOfWeek.SATURDAY), SUNDAY('U', DayOfWeek.SUNDAY);

  private final char abbreviation;
  private final DayOfWeek dayOfWeek;

  /**
   * Constructor for Weekday enum.
   *
   * @param abbreviation single character abbreviation
   * @param dayOfWeek    corresponding Java DayOfWeek
   */
  Weekday(char abbreviation, DayOfWeek dayOfWeek) {
    this.abbreviation = abbreviation;
    this.dayOfWeek = dayOfWeek;
  }

  /**
   * Parses a character to a Weekday enum.
   *
   * @param c the character to parse
   * @return the corresponding Weekday
   * @throws IllegalArgumentException if character is invalid
   */
  public static Weekday fromChar(char c) {
    char upper = Character.toUpperCase(c);
    for (Weekday day : values()) {
      if (day.abbreviation == upper) {
        return day;
      }
    }
    throw new IllegalArgumentException("Invalid weekday abbreviation: " + c);
  }

  /**
   * Parses a string of weekday abbreviations.
   * Example: "MWF" -> [MONDAY, WEDNESDAY, FRIDAY]
   *
   * @param abbreviations string of weekday characters
   * @return array of Weekday enums
   * @throws IllegalArgumentException if any character is invalid
   */
  public static Weekday[] parseString(String abbreviations) {
    if (abbreviations == null || abbreviations.trim().isEmpty()) {
      throw new IllegalArgumentException("Weekday string cannot be null or empty: ");
    }

    String trimmed = abbreviations.trim();
    Weekday[] result = new Weekday[trimmed.length()];

    for (int i = 0; i < result.length; i++) {
      result[i] = fromChar(trimmed.charAt(i));
    }
    return result;
  }

  /**
   * Converts a DayOfWeek to Weekday.
   *
   * @param dayOfWeek the Java DayOfWeek
   * @return the corresponding Weekday
   */
  public static Weekday fromDayOfWeek(DayOfWeek dayOfWeek) {
    for (Weekday day : values()) {
      if (day.dayOfWeek == dayOfWeek) {
        return day;
      }
    }
    throw new IllegalArgumentException("Invalid weekday day of week: " + dayOfWeek);
  }

  /**
   * Converts an array of Weekday to Set of DayOfWeek.
   *
   * @param weekdays array of weekdays
   * @return set of DayOfWeek values
   */
  public static java.util.Set<DayOfWeek> toDayOfWeekSet(Weekday[] weekdays) {
    java.util.Set<DayOfWeek> result = new java.util.HashSet<>();
    for (Weekday weekday : weekdays) {
      result.add(weekday.getDayOfWeek());
    }
    return result;
  }

  /**
   * Gets the single-character abbreviation.
   *
   * @return the abbreviation character
   */
  public char getAbbreviation() {
    return abbreviation;
  }

  /**
   * Gets the corresponding Java DayOfWeek.
   *
   * @return the DayOfWeek value
   */
  public DayOfWeek getDayOfWeek() {
    return dayOfWeek;
  }

  @Override
  public String toString() {
    return String.valueOf(abbreviation);
  }
}