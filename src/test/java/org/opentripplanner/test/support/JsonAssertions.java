package org.opentripplanner.test.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.opentripplanner.standalone.config.framework.json.JsonSupport;

public class JsonAssertions {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
  }

  /**
   * Take two JSON documents and reformat them before comparing {@code actual} with {@code expected}.
   */
  public static void assertEqualJson(String expected, String actual) {
    try {
      assertEqualJson(expected, MAPPER.readTree(actual));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @see JsonAssertions#assertEqualJson(String, String)
   */
  public static void assertEqualJson(String expected, JsonNode actual) {
    try {
      var exp = MAPPER.readTree(expected);
      assertEquals(JsonSupport.prettyPrint(exp), JsonSupport.prettyPrint(actual));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
