import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import calendar.model.EditSpec;
import calendar.model.EventStatus;
import java.time.LocalDateTime;
import org.junit.Test;

/**
 * Tests for EditSpec class.
 */
public class EditSpecTest {

  @Test
  public void testEditSpecCreation() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 1, 10, 0);

    EditSpec spec = new EditSpec("New Subject", start, end, "New Desc", "New Loc",
        EventStatus.PRIVATE);

    assertEquals("New Subject", spec.getNewSubject());
    assertEquals(start, spec.getNewStart());
    assertEquals(end, spec.getNewEnd());
    assertEquals("New Desc", spec.getNewDescription());
    assertEquals("New Loc", spec.getNewLocation());
    assertEquals(EventStatus.PRIVATE, spec.getNewStatus());
  }

  @Test
  public void testEditSpecAllNull() {
    EditSpec spec = new EditSpec(null, null, null, null, null, null);

    assertNull(spec.getNewSubject());
    assertNull(spec.getNewStart());
    assertNull(spec.getNewEnd());
    assertNull(spec.getNewDescription());
    assertNull(spec.getNewLocation());
    assertNull(spec.getNewStatus());
  }

  @Test
  public void testEditSpecEquals() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 1, 10, 0);

    EditSpec spec1 = new EditSpec("Subject", start, end, "Desc", "Loc", EventStatus.PUBLIC);
    EditSpec spec2 = new EditSpec("Subject", start, end, "Desc", "Loc", EventStatus.PUBLIC);

    assertEquals(spec1, spec2);
    assertEquals(spec1.hashCode(), spec2.hashCode());
  }

  @Test
  public void testEditSpecEqualsReflexive() {
    EditSpec spec = new EditSpec("Subject", null, null, null, null, null);
    assertEquals(spec, spec);
  }

  @Test
  public void testEditSpecNotEqualsDifferentSubject() {
    EditSpec spec1 = new EditSpec("Subject1", null, null, null, null, null);
    EditSpec spec2 = new EditSpec("Subject2", null, null, null, null, null);
    assertNotEquals(spec1, spec2);
  }

  @Test
  public void testEditSpecNotEqualsWithNull() {
    EditSpec spec = new EditSpec("Subject", null, null, null, null, null);
    assertNotEquals(spec, null);
  }

  @Test
  public void testEditSpecNotEqualsDifferentType() {
    EditSpec spec = new EditSpec("Subject", null, null, null, null, null);
    assertNotEquals(spec, "Not an EditSpec");
  }

  @Test
  public void testEditSpecHashCode() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 1, 9, 0);
    EditSpec spec1 = new EditSpec("Subject", start, null, null, null, null);
    EditSpec spec2 = new EditSpec("Subject", start, null, null, null, null);
    assertEquals(spec1.hashCode(), spec2.hashCode());
  }
}

