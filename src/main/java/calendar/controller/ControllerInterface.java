package calendar.controller;

import java.io.IOException;

/**
 * Controller abstraction for running the app.
 */
public interface ControllerInterface {

  /**
   * Starts processing commands.
   *
   * @throws IOException if I/O fails
   */
  void run() throws IOException;
}

