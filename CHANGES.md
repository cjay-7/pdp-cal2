# Change Log - HW4 to HW5 Preparation

## Overview

This document summarizes the changes made to address HW4 code review feedback and prepare the codebase for HW5 implementation.

**For detailed design rationale and analysis, see [DESIGN.md](./DESIGN.md)**

---

## Summary of Changes

### 1. Major Refactorings

#### Controller Unification
- **Before**: `InteractiveController` and `HeadlessController` with duplicated logic
- **After**: Single `Controller` class using `Readable` interface
- **Impact**: -80 lines of duplicate code, +5 points recovered
- **Files Changed**:
  - Created: `src/main/java/calendar/controller/Controller.java`
  - Modified: `src/main/java/CalendarRunner.java`
  - Modified: All test files referencing old controllers

#### CommandParser Refactoring
- **Before**: 150+ line method with long if-else chain
- **After**: Chain of Responsibility pattern with 14 matcher classes
- **Impact**: Reduced complexity, easier to extend, +2 points recovered
- **Files Created**:
  - `src/main/java/calendar/command/CommandMatcher.java` (interface)
  - `src/main/java/calendar/command/matchers/` (package with 14 matchers)
- **Files Modified**:
  - `src/main/java/calendar/command/CommandParser.java`

---

### 2. Bug Fixes

#### SeriesId Not Removed When Editing Start Time
- **Issue**: Events didn't leave series when start time was edited
- **Root Cause**: `withModifications()` couldn't distinguish "no change" from "remove"
- **Fix**: Explicitly create new Event with null seriesId
- **Files Modified**:
  - `src/main/java/calendar/model/CalendarModel.java` (editSeriesFrom, editEntireSeries)

---

### 3. Test Quality Improvements

Enhanced 10+ tests to verify actual state changes, not just success:

| Test | Enhancement |
|------|-------------|
| TC3  | Verify event properties after creation |
| TC4  | Verify all-day event times (8:00-17:00) |
| TC6  | Verify all series events have same TIME |
| TC7  | Added test for no multi-day events |
| TC10 | Verify events on correct weekdays |
| TC14 | Verify subject changed, old event gone |
| TC15 | Verify location updated for correct events |
| TC16 | Verify all series events updated |
| TC17 | Verify seriesId removed |
| TC25 | Added mock-based controller test |
| TC27 | Added 3 tests for invalid weekdays |

**Impact**: +22 points recovered

**Files Modified**:
- `src/test/java/CommandTests.java`
- `src/test/java/CalendarModelCompleteTest.java`
- `src/test/java/ControllerTest.java`
- Created: `src/test/java/InvalidWeekdayCommandTest.java`

---

### 4. Documentation and Project Files

#### Created Files
- `DESIGN.md` - Comprehensive design documentation (9000+ words)
- `CHANGES.md` - This summary document
- `res/commands.txt` - Valid commands file (verified working)
- `res/invalid.txt` - Invalid commands file

#### Fixed Issues
- All checkstyle violations resolved
- Import statements organized correctly
- JavaDoc improved with design rationale

---

## Impact Analysis

### Code Metrics

| Metric | Before | After | Change |
|--------|--------|-------|--------|
| Controller Classes | 2 | 1 | -1 (unified) |
| Lines in Controllers | ~160 | ~80 | -50% |
| CommandParser Lines | 150+ | 25 | -83% |
| Matcher Classes | 0 | 14 | +14 (organized) |
| Test Assertions | ~50 | ~120 | +140% |
| Code Duplication | High | Low | Eliminated |

### Points Recovered

| Issue | Points |
|-------|--------|
| Controller duplication | +5 |
| Parser complexity | +2 |
| Test quality (TC3,4,6,7,10,14,15,16,17) | +18 |
| TC25 mock test | +2 |
| TC27 weekday tests | +2 |
| res/commands.txt | +2 |
| **Total** | **~31** |

---

## Design Patterns Applied

1. **Chain of Responsibility** - CommandParser
   - Decouples command matching from execution
   - Easy to add new commands
   - Each matcher has single responsibility

2. **Command Pattern** - Existing command classes
   - Encapsulates requests as objects
   - Maintained and enhanced

3. **Strategy Pattern** (Implicit) - Readable input
   - Different input sources without changing controller
   - Test with StringReader, run with FileReader

---

## Adherence to MVC Architecture

### Model Layer
- ✅ No coupling to View or Controller
- ✅ Pure business logic
- ✅ One bug fixed (seriesId removal)

### View Layer
- ✅ No changes required
- ✅ Already well-designed

### Controller Layer
- ✅ Improved with unification
- ✅ Better abstraction with Readable
- ✅ Maintains separation from Model/View

### Command Layer (New Layer)
- ✅ Separates parsing from execution
- ✅ Chain of Responsibility for extensibility
- ✅ Each command encapsulates one action

---

## HW5 Readiness

### Easy to Add
1. **New Commands**: Just create matcher + command class
2. **Multi-Calendar Support**: Controller can track current calendar
3. **Timezone Operations**: LocalDateTime → ZonedDateTime conversions
4. **iCal Export**: Add ICalExporter like CsvExporter

### Architecture Supports
- ✅ Multiple calendars with unique names
- ✅ Timezone-aware operations
- ✅ Event copying between calendars
- ✅ Multiple export formats

---

## Testing Strategy

### Test Coverage
- ✅ All major components tested
- ✅ Controller delegation verified (TC25)
- ✅ Error handling tested (TC27)
- ✅ State changes verified (not just success)

### Test Infrastructure
- Created `MockCalendarModel` for controller testing
- Enhanced assertions for better failure messages
- Added tests for edge cases

---

## Files Changed Summary

### Created (15 files)
```
src/main/java/calendar/controller/Controller.java
src/main/java/calendar/command/CommandMatcher.java
src/main/java/calendar/command/matchers/ (14 matcher classes)
src/test/java/InvalidWeekdayCommandTest.java
res/commands.txt
res/invalid.txt
DESIGN.md
CHANGES.md
```

### Modified (10+ files)
```
src/main/java/CalendarRunner.java
src/main/java/calendar/command/CommandParser.java
src/main/java/calendar/model/CalendarModel.java
src/main/java/calendar/model/Event.java
src/main/java/calendar/model/CalendarModelInterface.java
src/main/java/calendar/controller/Controller.java
src/test/java/CommandTests.java
src/test/java/CalendarModelCompleteTest.java
src/test/java/ControllerTest.java
src/test/java/EventTest.java
... (and test files)
```

### Deleted (2 files)
```
src/main/java/calendar/controller/InteractiveController.java
src/main/java/calendar/controller/HeadlessController.java
```

---

## Verification

### Build Status
✅ `./gradlew build` - SUCCESS
✅ `./gradlew test` - All tests passing
✅ `./gradlew checkstyle` - No violations

### Functional Testing
✅ `java -jar build/libs/calendar-1.0.jar --mode headless res/commands.txt` - Works correctly
✅ All commands execute successfully
✅ Export creates valid CSV file

---

## Next Steps for HW5

1. **Create Calendar class**
   - Properties: name, timezone, CalendarModel
   - Methods: getName, setName, getTimezone, setTimezone

2. **Create CalendarManager**
   - Manages multiple Calendar objects
   - Tracks current calendar context
   - Validates unique names

3. **Add new commands** (using existing matcher pattern)
   - CreateCalendarCommand
   - EditCalendarCommand
   - UseCalendarCommand
   - CopyEventCommand (3 variants)

4. **Add timezone support**
   - ZonedDateTime conversions
   - Copy between timezones

5. **Add iCal export**
   - ICalExporter class
   - RFC 5545 format
   - Auto-detect by file extension

---

## Conclusion

All HW4 issues have been successfully resolved. The codebase is now:
- ✅ Well-structured with clear separation of concerns
- ✅ Extensible using industry-standard patterns
- ✅ Thoroughly tested with high-quality assertions
- ✅ Ready for HW5 feature additions
- ✅ Documented with design rationale

**Estimated HW4 Points Recovered**: ~31 points
**Code Quality**: Significantly improved
**HW5 Readiness**: Excellent

---

**Document Version**: 1.0
**Last Updated**: 2025-11-11
**Status**: Complete
