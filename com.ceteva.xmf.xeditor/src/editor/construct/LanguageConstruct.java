package editor.construct;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

public class LanguageConstruct {

	private static Color[] colors    = ColorUtils.generateVisuallyDistinctColors(100, 0.0f, 0.5f);
	private static int     nextColor = 0;

	private static void ensureColor(String name) {
		if (!colourTable.containsKey(name)) {
			colourTable.put(name, colors[(nextColor++) % colors.length]);
		}
	}

	private static Hashtable<String, Color> colourTable = new Hashtable<String, Color>();

	private String          name;
	private int             charStart;
	private int             charEnd;
	private Vector<Keyword> keywords;
	private String canonical;

	public LanguageConstruct(String name, String canonical,int charStart, int charEnd, Vector<Keyword> keywords) {
		super();
		this.name      = name;
		this.charStart = charStart;
		this.charEnd   = charEnd;
		this.keywords  = keywords;
		this.canonical = canonical;
		ensureColor(name);
	}

	public boolean defines(String token, int offset) {
		if (token.equals("end")) {
			// Treat 'end' specially since it used everywhere as the end of a construct...
			if (offset >= charStart && offset + "end".length() == charEnd)
				return true;
			else
				return false;
		} else {
			if (offset >= charStart && offset <= charEnd) {
				if (token.equals(name)) {
					return true;
				} else {
					for (Keyword k : keywords) {
						if (k.getName().equals(token))
							return true;
					}
					return false;
				}
			} else
				return false;
		}
	}

	public int getCharEnd() {
		return charEnd;
	}

	public int getCharStart() {
		return charStart;
	}

	public Color getColor() {
		return colourTable.get(name);
	}

	public Vector<Keyword> getKeywords() {
		return keywords;
	}

	public String getName() {
		return name;
	}

	public static Color[] getColors() {
		return colors;
	}

	public static int getNextColor() {
		return nextColor;
	}

	public static Hashtable<String, Color> getColourTable() {
		return colourTable;
	}

	public String getCanonical() {
		return canonical;
	}

}
