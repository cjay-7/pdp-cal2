import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import calendar.command.Tokenizer;
import java.util.List;
import org.junit.Test;

/**
 * Tests for Tokenizer utility.
 */
public class TokenizerTest {

  /**
   * Ensures null and empty strings return empty list.
   */
  @Test
  public void testNullAndEmpty() {
    Tokenizer t = new Tokenizer();
    assertTrue(t.tokenize(null).isEmpty());
    assertTrue(t.tokenize("").isEmpty());
    assertTrue(t.tokenize("   ").isEmpty());
  }

  /**
   * Ensures whitespace is normalized to single-token splits.
   */
  @Test
  public void testWhitespaceSplitting() {
    Tokenizer t = new Tokenizer();
    List<String> tokens = t.tokenize("  a   b\t c\n d  ");
    assertEquals(4, tokens.size());
    assertEquals("a", tokens.get(0));
    assertEquals("b", tokens.get(1));
    assertEquals("c", tokens.get(2));
    assertEquals("d", tokens.get(3));
  }
}
