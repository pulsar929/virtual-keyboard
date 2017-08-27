package org.github.pulsar929.virtualKeyboard;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.github.pulsar929.virtualKeyboard.VirtualKeyboard.PathResult;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MainApp {
	static ObjectMapper om = new ObjectMapper();

	static {
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.enable(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS);
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		RequestResponse[] rrs;
		if (args.length > 0) {
			rrs = readRequest(new FileInputStream(args[0]));
		} else {
			rrs = readRequest(System.in);
		}

		VirtualKeyboard vk = new VirtualKeyboard();
		for (RequestResponse rr : rrs) {
			PathResult path = vk.getPath(new String(rr.alphabet), rr.rowLength, rr.startingFocus, rr.word);
			rr.distance = path.distance;
			rr.path = path.path.toCharArray();
		}
		if (args.length > 1) {
			writeResponse(new FileOutputStream(args[1]), rrs);
		} else {
			writeResponse(System.out, rrs);
		}

	}

	protected static RequestResponse[] readRequest(InputStream in)
			throws JsonParseException, JsonMappingException, IOException {
		return om.readValue(in, RequestResponse[].class);
	}

	protected static void writeResponse(OutputStream out, RequestResponse[] rrs)
			throws JsonGenerationException, JsonMappingException, IOException {
		om.writeValue(out, rrs);
	}
}
