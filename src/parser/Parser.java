package parser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class Parser {
	
	public abstract void parseTest();
	
	public String toUTF8(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "UTF-8");
	}

}
