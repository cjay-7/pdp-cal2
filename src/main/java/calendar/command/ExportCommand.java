package calendar.command;

import calendar.model.Calendar;
import calendar.model.CalendarManager;
import calendar.model.CalendarModelInterface;
import calendar.utils.IcalExporter;
import calendar.view.ViewInterface;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Command to export calendar to CSV or iCal file.
 * Format: export cal fileName.csv or export cal fileName.ical
 *
 * <p>The export format is automatically detected by the file extension:
 * - .csv: Exports to CSV format
 * - .ical or .ics: Exports to iCalendar format (RFC 5545)
 *
 * <p>DESIGN RATIONALE:
 * - Auto-detection by extension provides better user experience
 * - Supports multiple export formats without changing command syntax
 * - Displays absolute path so user knows where file is saved
 * - Platform-independent path handling
 */
public class ExportCommand implements CommandInterface {
  private final String fileName;

  /**
   * Creates an ExportCommand.
   *
   * @param fileName the name of the file to create (.csv or .ical)
   */
  public ExportCommand(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public boolean execute(CalendarManager manager, ViewInterface view) throws IOException {
    try {
      Calendar currentCal = manager.getCurrentCalendar();
      if (currentCal == null) {
        view.displayMessage("Error: No calendar is currently in use. "
            + "Use 'use calendar --name <name>' first.");
        return false;
      }

      CalendarModelInterface model = currentCal.getModel();

      Path filePath = Paths.get(fileName).toAbsolutePath().normalize();

      String lowerFileName = fileName.toLowerCase();
      boolean success = false;

      if (lowerFileName.endsWith(".csv")) {
        model.exportToCsv(filePath);
        success = true;
      } else if (lowerFileName.endsWith(".ical") || lowerFileName.endsWith(".ics")) {
        String icalContent = IcalExporter.toIcal(
            model.getAllEvents(),
            currentCal.getName(),
            currentCal.getTimezone()
        );
        Files.writeString(filePath, icalContent);
        success = true;
      } else {
        view.displayMessage("Error: Unsupported file format. Use .csv or .ical extension.");
        return false;
      }

      if (success) {
        view.displayMessage("Calendar exported to: " + filePath);
        return true;
      } else {
        view.displayMessage("Error: Failed to export calendar.");
        return false;
      }
    } catch (IOException e) {
      view.displayMessage("Error: Failed to export calendar: " + e.getMessage());
      return false;
    } catch (Exception e) {
      view.displayMessage("Error: Failed to export calendar: " + e.getMessage());
      return false;
    }
  }
}

