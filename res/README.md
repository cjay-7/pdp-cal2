# Resource Files Directory

This directory contains example command files and output files for the calendar application.

## Files

### commands.txt
Valid commands file demonstrating all features of the calendar application.

**Contents:**
- Create single events
- Create all-day events
- Create recurring event series (by count and by date)
- Print commands (all events, events on date, events in range)
- Show status command
- Edit commands (single event, multiple events, entire series)
- Export to CSV

**Usage:**
```bash
java -jar build/libs/calendar-1.0.jar --mode headless res/commands.txt
```

**Expected Behavior:**
- All commands execute successfully
- Creates multiple events and series
- Displays event listings
- Exports calendar to CSV file
- Exits cleanly

---

### invalid.txt
Commands file containing at least one invalid command to demonstrate error handling.

**Contents:**
- Valid commands
- One invalid/unrecognized command
- Exit command

**Usage:**
```bash
java -jar build/libs/calendar-1.0.jar --mode headless res/invalid.txt
```

**Expected Behavior:**
- Valid commands execute successfully
- Invalid command displays error message
- Application continues processing remaining commands
- Exits cleanly

---

## Generated Files

When running the commands, the following files may be generated:

### calendar_export.csv
CSV export of calendar events in Google Calendar format.

**Format:**
```csv
Subject,Start Date,Start Time,End Date,End Time,All Day Event,Description,Location,Private
Team Meeting,2025/06/01,10:00 AM,2025/06/01,11:00 AM,False,,,False
```

**Can be imported to:**
- Google Calendar
- Microsoft Outlook
- Apple Calendar
- Any application supporting CSV calendar import

---

## Testing the Application

### Interactive Mode
```bash
java -jar build/libs/calendar-1.0.jar --mode interactive
```
Type commands manually and see results immediately.

### Headless Mode with Valid Commands
```bash
java -jar build/libs/calendar-1.0.jar --mode headless res/commands.txt
```
Run all commands from file automatically.

### Headless Mode with Invalid Commands
```bash
java -jar build/libs/calendar-1.0.jar --mode headless res/invalid.txt
```
Verify error handling works correctly.

---

## Command Format Examples

### Create Events
```
create event "Meeting" from 2025-06-01T10:00 to 2025-06-01T11:00
create event "Holiday" on 2025-06-01
```

### Create Series
```
create event "Standup" from 2025-06-02T09:00 to 2025-06-02T09:15 repeats MWF for 5 times
create event "Weekly" from 2025-06-03T15:00 to 2025-06-03T16:00 repeats T until 2025-06-30
```

### Query Events
```
print all events
print events on 2025-06-02
print events from 2025-06-01T00:00 to 2025-06-07T23:59
show status on 2025-06-02T10:30
```

### Edit Events
```
edit event subject "Old Name" from 2025-06-01T10:00 to 2025-06-01T11:00 with "New Name"
edit events location "Meeting" from 2025-06-04T09:00 with "Room A"
edit series description "Weekly" from 2025-06-03T15:00 with "Important meeting"
```

### Export
```
export cal filename.csv
```

---

## Notes

- All date-times use format: `YYYY-MM-DDThh:mm`
- All dates use format: `YYYY-MM-DD`
- Weekdays: M (Monday), T (Tuesday), W (Wednesday), R (Thursday), F (Friday), S (Saturday), U (Sunday)
- Event names with spaces must be quoted: `"Team Meeting"`
- Commands are case-insensitive
- All commands files must end with `exit` command

---

**Last Updated**: 2025-11-11
**Version**: 1.0
