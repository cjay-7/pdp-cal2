import static org.junit.Assert.assertTrue;

import java.security.Permission;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for CalendarRunner main argument validation.
 */
public class CalendarRunnerTest {

  private SecurityManager originalSm;
  private ExitInterceptSecurityManager sm;

  /**
   * Installs a SecurityManager that intercepts System.exit.
   */
  @Before
  public void setUp() {
    originalSm = System.getSecurityManager();
    sm = new ExitInterceptSecurityManager();
    System.setSecurityManager(sm);
  }

  /**
   * Restores original SecurityManager.
   */
  @After
  public void tearDown() {
    System.setSecurityManager(originalSm);
  }

  /**
   * Invoking without required args should try to exit with status 1.
   */
  @Test
  public void testMissingArgsExits() {
    try {
      CalendarRunner.main(new String[]{});
    } catch (SecurityException ignored) {
      // expected due to intercepted exit
    }
    assertTrue(sm.lastStatus == 1);
  }

  /**
   * Invoking headless without file should try to exit with status 1.
   */
  @Test
  public void testHeadlessWithoutFileExits() {
    try {
      CalendarRunner.main(new String[]{"--mode", "headless"});
    } catch (SecurityException ignored) {
      // expected due to intercepted exit
    }
    assertTrue(sm.lastStatus == 1);
  }

  /**
   * Interactive mode should run and return when input is 'exit'.
   */
  @Test
  public void testInteractiveModeRuns() {
    java.io.InputStream originalIn = System.in;
    try {
      System.setIn(new java.io.ByteArrayInputStream("exit\n".getBytes()));
      CalendarRunner.main(new String[]{"--mode", "interactive"});
      // If we reached here without SecurityException, it returned normally
      assertTrue(true);
    } finally {
      System.setIn(originalIn);
    }
  }

  /**
   * Headless mode should run with provided commands file.
   */
  @Test
  public void testHeadlessModeRuns() throws Exception {
    java.nio.file.Path tmp = java.nio.file.Files.createTempFile("cmds", ".txt");
    try {
      String cmds = "create event \"X\" from 2025-06-01T10:00 to 2025-06-01T11:00\nexit\n";
      java.nio.file.Files.writeString(tmp, cmds);
      CalendarRunner.main(new String[]{"--mode", "headless", tmp.toString()});
      assertTrue(true);
    } finally {
      java.nio.file.Files.deleteIfExists(tmp);
    }
  }

  /** SecurityManager that records exit status. */
  private static class ExitInterceptSecurityManager extends SecurityManager {
    int lastStatus = -1;

    @Override
    public void checkPermission(Permission perm) {
      // allow
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
      // allow
    }

    @Override
    public void checkExit(int status) {
      lastStatus = status;
      throw new SecurityException("Intercepted System.exit(" + status + ")");
    }
  }
}
