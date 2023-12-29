package diagrams;

import java.awt.Font;
import java.awt.Graphics;
import java.util.Vector;

public class Descriptor {

	private String         name;
	private String         svg      = "";
	private Vector<String> allNames;
	private Vector<String> shown;
	private Vector<String> selected = new Vector<String>();

	public Descriptor(String name, String svg, Vector<String> allNames) {
		super();
		this.name = name;
		this.svg = svg;
		this.allNames = allNames;
		this.shown = allNames;
	}

	public void select(String name) {
		String s = getSelection(name);
		if (s != null)
			selected.remove(s);
		else
			selected.add(name);
	}

	private String getSelection(String name) {
		for (String s : selected) {
			if (s.equals(name)) {
				return s;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public String getSvg() {
		return svg;
	}

	public Vector<String> getAllNames() {
		return allNames;
	}

	public Vector<String> getShown() {
		return shown;
	}

	public Vector<String> getSelected() {
		return selected;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public void setAllNames(Vector<String> allNames) {
		this.allNames = allNames;
	}

	public void setShown(Vector<String> shown) {
		this.shown = shown;
	}

	public void hide() {
		for (String s : selected) {
			shown.remove(s);
		}
		selected.clear();
	}

	public void show() {
		shown.clear();
		for (String s : selected) {
			shown.add(s);
		}
		selected.clear();
	}

	public void reset() {
		shown.clear();
		for (String name : allNames) {
			shown.add(name);
		}
		selected.clear();
	}

}
