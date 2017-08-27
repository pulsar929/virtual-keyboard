package org.github.pulsar929.virtualKeyboard;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Unit test for simple App.
 */
public class MainAppTest {
	/**
	 * Rigourous Test :-)
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	public void testApp() throws JsonParseException, JsonMappingException, IOException {
		MainApp.main(new String[] { "src/test/resources/sample.json" });
	}
}
