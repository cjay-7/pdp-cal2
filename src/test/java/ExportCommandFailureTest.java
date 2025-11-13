import static org.junit.Assert.assertFalse;

import calendar.command.ExportCommand;
import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.model.CalendarModelInterface;
import calendar.view.ConsoleView;
import calendar.view.ViewInterface;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests failure path of ExportCommand.
 */
public class ExportCommandFailureTest {
  private CalendarManager manager;
  private CalendarModelInterface model;
  private ViewInterface view;
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;

  /**
   * Sets up model and view capturing output.
   */
  @Before
  public void setUp() {
    manager = new CalendarManager();
    manager.createCalendar("TestCalendar", ZoneId.of("America/New_York"));
    manager.setCurrentCalendar("TestCalendar");
    model = manager.getCurrentCalendar().getModel();

    originalOut = System.out;
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    view = new ConsoleView(System.out);
  }

  /**
   * Restores System.out.
   */
  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  /**
   * Using a directory path should fail and return false.
   */
  @Test
  public void testExportToDirectoryPathFails() throws IOException {
    Path dir = Files.createTempDirectory("csv-dir");
    try {
      ExportCommand cmd = new ExportCommand(dir.toString());
      boolean ok = cmd.execute(manager, view);
      assertFalse(ok);
    } finally {
      Files.deleteIfExists(dir);
    }
  }
}
