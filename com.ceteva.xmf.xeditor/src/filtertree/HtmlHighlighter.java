package filtertree;

import java.util.regex.Pattern;

public class HtmlHighlighter {
	private static final String HighLightTemplate = "<span style='background:rgb(50,50,0);'>$1</span>";

	public static String highlightText(String text, String textToHighlight) {
		if (textToHighlight.length() == 0) {
			return text;
		}

		try {
			text = text.replaceAll("(?i)(" + Pattern.quote(textToHighlight) + ")", HighLightTemplate);
		} catch (Exception e) {
			return text;
		}
		return text;
	}
}