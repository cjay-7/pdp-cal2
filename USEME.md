# Calendar Application - User Guide

## Overview

This is a multi-calendar application that supports creating and managing multiple calendars with different timezones, events, recurring event series, and exporting to CSV and iCal formats.

---

## Running the Application

### Prerequisites
- Java 8 or higher installed
- JAR file located at: `build/libs/calendar-1.0.jar`

### Basic Usage

The application supports two modes: **interactive** and **headless**.

#### Interactive Mode

Run the application in interactive mode to type commands manually:

```bash
java -jar build/libs/calendar-1.0.jar --mode interactive
```

In interactive mode:
- You can type commands one at a time
- Press Enter after each command
- The application displays results immediately
- Type `exit` to quit

**Example Interactive Session:**
```bash
java -jar build/libs/calendar-1.0.jar --mode interactive
> create calendar --name Work --timezone America/New_York
Calendar 'Work' created successfully with timezone America/New_York.
> use calendar --name Work
Now using calendar 'Work'.
> create event "Team Meeting" from 2025-01-15T09:00 to 2025-01-15T10:00
Event 'Team Meeting' created successfully.
> print all events
Team Meeting: 2025-01-15 09:00 to 10:00
> exit
```

#### Headless Mode

Run the application with a file containing commands:

```bash
java -jar build/libs/calendar-1.0.jar --mode headless <path-to-commands-file>
```

**Examples:**

Run with valid commands file:
```bash
java -jar build/libs/calendar-1.0.jar --mode headless res/commands.txt
```

Run with invalid commands file (to test error handling):
```bash
java -jar build/libs/calendar-1.0.jar --mode headless res/invalid.txt
```

---

## Command Reference

### Calendar Management Commands

#### Create a Calendar
```bash
create calendar --name <calendar-name> --timezone <IANA-timezone>
```

**Examples:**
```bash
create calendar --name Work --timezone America/New_York
create calendar --name Personal --timezone Europe/Paris
create calendar --name School --timezone Asia/Kolkata
```

**Supported Timezones:** Use IANA timezone format (e.g., America/New_York, Europe/London, Asia/Tokyo). See [IANA Time Zone Database](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones).

#### Use a Calendar
Set the current calendar context for event operations:
```bash
use calendar --name <calendar-name>
```

**Example:**
```bash
use calendar --name Work
```

#### Edit Calendar Properties
```bash
edit calendar --name <calendar-name> --property <property-name> <new-value>
```

**Supported Properties:**
- `name` - Change calendar name (must be unique)
- `timezone` - Change calendar timezone (IANA format)

**Examples:**
```bash
edit calendar --name Work --property timezone America/Chicago
edit calendar --name Work --property name WorkUpdated
```

---

### Event Creation Commands

**Note:** You must use a calendar before creating events (`use calendar --name <name>`).

#### Create a Single Event
```bash
create event "<event-name>" from <start-datetime> to <end-datetime>
```

**Format:** DateTime must be in `YYYY-MM-DDTHH:MM` format

**Example:**
```bash
create event "Team Meeting" from 2025-01-15T09:00 to 2025-01-15T10:00
```

#### Create an All-Day Event
```bash
create event "<event-name>" on <date>
```

**Format:** Date must be in `YYYY-MM-DD` format

**Example:**
```bash
create event "Company Holiday" on 2025-12-25
```

#### Create a Recurring Event Series (by count)
```bash
create event "<event-name>" from <start-datetime> to <end-datetime> repeats <weekdays> for <count> times
```

**Weekdays:**
- `M` = Monday
- `T` = Tuesday
- `W` = Wednesday
- `R` = Thursday (R for thuRsday)
- `F` = Friday
- `S` = Saturday
- `U` = Sunday

**Examples:**
```bash
create event "Daily Standup" from 2025-01-16T09:00 to 2025-01-16T09:15 repeats MWF for 10 times
create event "Weekend Workout" from 2025-01-18T08:00 to 2025-01-18T09:00 repeats SU for 5 times
```

#### Create a Recurring Event Series (by end date)
```bash
create event "<event-name>" from <start-datetime> to <end-datetime> repeats <weekdays> until <end-date>
```

**Example:**
```bash
create event "Weekly Sync" from 2025-01-17T15:00 to 2025-01-17T16:00 repeats T until 2025-03-31
```

#### Create All-Day Recurring Events
```bash
create event "<event-name>" on <date> repeats <weekdays> for <count> times
create event "<event-name>" on <date> repeats <weekdays> until <end-date>
```

**Examples:**
```bash
create event "Training Day" on 2025-01-20 repeats R for 4 times
create event "Gym Day" on 2025-01-20 repeats MWF until 2025-02-28
```

---

### Query Commands

#### Print All Events
```bash
print all events
```

#### Print Events on a Specific Date
```bash
print events on <date>
```

**Example:**
```bash
print events on 2025-01-16
```

#### Print Events in a Time Range
```bash
print events from <start-datetime> to <end-datetime>
```

**Example:**
```bash
print events from 2025-01-15T00:00 to 2025-01-20T23:59
```

#### Check Availability Status
```bash
show status on <datetime>
```

**Example:**
```bash
show status on 2025-01-16T10:00
```

---

### Event Editing Commands

#### Edit a Single Event
```bash
edit event <property> "<event-name>" from <start-datetime> to <end-datetime> with "<new-value>"
```

**Supported Properties:** `subject`, `description`, `location`, `private`

**Examples:**
```bash
edit event subject "Team Meeting" from 2025-01-15T09:00 to 2025-01-15T10:00 with "Executive Meeting"
edit event location "Team Meeting" from 2025-01-15T09:00 to 2025-01-15T10:00 with "Conference Room A"
edit event description "Team Meeting" from 2025-01-15T09:00 to 2025-01-15T10:00 with "Quarterly review meeting"
edit event private "Team Meeting" from 2025-01-15T09:00 to 2025-01-15T10:00 with "true"
```

#### Edit Events in a Series (from a date forward)
```bash
edit events <property> "<event-name>" from <start-datetime> with "<new-value>"
```

**Example:**
```bash
edit events location "Daily Standup" from 2025-01-20T09:00 with "Conference Room B"
```

#### Edit Entire Series
```bash
edit series <property> "<event-name>" from <start-datetime> with "<new-value>"
```

**Example:**
```bash
edit series description "Weekly Sync" from 2025-01-17T15:00 with "Important weekly meeting"
```

---

### Copy Events Commands

#### Copy a Single Event
```bash
copy event <event-name> on <source-datetime> --target <target-calendar> to <target-datetime>
```

**Example:**
```bash
copy event Executive Meeting on 2025-01-15T09:00 --target Personal to 2025-01-25T10:00
```

**Note:** The event is copied with the same duration but at the new start time.

#### Copy All Events on a Specific Day
```bash
copy events on <source-date> --target <target-calendar> to <target-date>
```

**Example:**
```bash
copy events on 2025-01-16 --target Personal to 2025-01-26
```

**Note:** Times are converted between timezones. An event at 2pm EST becomes 11am PST (same moment in time).

#### Copy Events in a Date Range
```bash
copy events between <start-date> and <end-date> --target <target-calendar> to <target-start-date>
```

**Example:**
```bash
copy events between 2025-01-15 and 2025-01-17 --target School to 2025-02-01
```

**Note:**
- Endpoint dates are inclusive
- Series relationships are preserved in the destination calendar
- If a series partially overlaps the range, only overlapping events are copied

---

### Export Commands

#### Export to CSV
```bash
export cal <filename>.csv
```

**Example:**
```bash
export cal my_calendar.csv
```

The exported CSV file is compatible with Google Calendar and can be imported.

#### Export to iCal Format
```bash
export cal <filename>.ical
```

**Example:**
```bash
export cal my_calendar.ical
```

The exported iCal file follows RFC 5545 specification and can be imported into:
- Google Calendar
- Apple Calendar
- Microsoft Outlook
- Any calendar application supporting iCal format

**Note:** The application automatically detects the export format based on the file extension (.csv or .ical/.ics).

---

### Exit Command

```bash
exit
```

Terminates the application.

---

## Complete Example Workflow

Here's a complete example showing the full workflow:

```bash
# Create calendars with different timezones
create calendar --name Work --timezone America/New_York
create calendar --name Personal --timezone America/Los_Angeles

# Use Work calendar
use calendar --name Work

# Create various events
create event "Team Meeting" from 2025-01-15T09:00 to 2025-01-15T10:00
create event "Lunch" on 2025-01-15
create event "Daily Standup" from 2025-01-16T09:00 to 2025-01-16T09:15 repeats MWF for 5 times

# Query events
print all events
print events on 2025-01-16

# Edit an event
edit event subject "Team Meeting" from 2025-01-15T09:00 to 2025-01-15T10:00 with "Executive Meeting"

# Copy event to another calendar
copy event Executive Meeting on 2025-01-15T09:00 --target Personal to 2025-01-25T10:00

# Switch to Personal calendar
use calendar --name Personal
print all events

# Export calendars
export cal work_events.csv
export cal work_events.ical

# Change calendar timezone
edit calendar --name Work --property timezone America/Chicago

# Exit
exit
```

---

## Error Handling

The application provides clear error messages for common issues:

### Invalid Calendar Name
```bash
> use calendar --name NonExistent
Error: Calendar 'NonExistent' not found.
```

### Invalid Timezone
```bash
> create calendar --name Test --timezone Invalid/Zone
Error: Invalid timezone 'Invalid/Zone'. Use IANA format (e.g., America/New_York).
```

### Duplicate Calendar Name
```bash
> create calendar --name Work --timezone America/New_York
Error: Calendar 'Work' already exists.
```

### Invalid Command
```bash
> this is not a valid command
Error: Unrecognized command: "this is not a valid command"
```

### No Calendar in Use
```bash
> create event "Test" from 2025-01-15T10:00 to 2025-01-15T11:00
Error: No calendar is currently in use. Use 'use calendar --name <name>' first.
```

---

## File Locations

When you export calendars or run the application, files are created in the current working directory:

- **Exported CSV files**: `<filename>.csv` (e.g., `work_calendar.csv`)
- **Exported iCal files**: `<filename>.ical` (e.g., `work_calendar.ical`)

The application displays the absolute path of exported files so you know exactly where to find them.

---

## Date and Time Formats

### DateTime Format
- **Pattern:** `YYYY-MM-DDTHH:MM`
- **Example:** `2025-01-15T09:00`
- The `T` separates date and time

### Date Format
- **Pattern:** `YYYY-MM-DD`
- **Example:** `2025-01-15`

### Weekday Abbreviations
- `M` = Monday
- `T` = Tuesday
- `W` = Wednesday
- `R` = Thursday
- `F` = Friday
- `S` = Saturday
- `U` = Sunday

---

## Tips and Best Practices

1. **Always create and use a calendar first** before creating events
2. **Use quotes** for event names with spaces: `"Team Meeting"`
3. **Commands are case-insensitive**: `EXIT` and `exit` both work
4. **Export to both formats** for compatibility: CSV for Google Calendar, iCal for most other apps
5. **Verify timezone names** at [IANA Time Zone Database](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)
6. **Use descriptive calendar names** that reflect their purpose (Work, Personal, School, etc.)
7. **Test invalid commands** in headless mode to ensure error handling works correctly

---

## Troubleshooting

### Application Won't Start
- Ensure Java is installed: `java -version`
- Check JAR file path: `build/libs/calendar-1.0.jar`
- Verify you're using correct syntax: `java -jar build/libs/calendar-1.0.jar --mode <mode> [file]`

### Commands File Not Found
- Use absolute path: `java -jar build/libs/calendar-1.0.jar --mode headless /full/path/to/commands.txt`
- Or run from project root directory

### Export File Not Found
- Check the absolute path printed by the application
- Ensure you have write permissions in the current directory

---

## Additional Resources

- **README.md**: Assignment requirements and project overview
- **DESIGN.md**: Comprehensive design documentation and architecture explanation
- **CHANGES.md**: Summary of changes made from HW4 to HW5
- **res/commands.txt**: Example file with all valid commands
- **res/invalid.txt**: Example file with invalid commands for error handling testing

---

**Version:** 1.0
**Last Updated:** 2025-01-12
**Application:** Calendar Application HW5
