package diagrams.model;

import java.util.HashSet;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Package extends PackageElement {

	private Vector<PackageElement> contents    = new Vector<PackageElement>();
	private Vector<Edge>           edges       = new Vector<Edge>();
	private Vector<Note>           notes       = new Vector<Note>();
	private String                 path;
	private String                 packageNote = null;

	public Package(String path, String name) {
		super(name);
		this.path = path;
	}

	public String getPackageNote() {
		return packageNote;
	}

	public void setPackageNote(String packageNote) {
		this.packageNote = packageNote;
	}

	public Class addClass(String name) {
		Class c = new Class(name);
		contents.add(c);
		return c;
	}

	public Obj addObj(String id, String type) {
		Obj o = new Obj(id, type);
		contents.add(o);
		return o;
	}

	public Table addTable(String name) {
		Table t = new Table(name);
		contents.add(t);
		return t;
	}

	public Queue addQueue(String name) {
		Queue t = new Queue(name);
		contents.add(t);
		return t;
	}

	public void addEnum(String name, Vector<String> names) {
		contents.add(new Enum(name, names));
	}

	public void addImportedClass(String sourcePath, String name) {
		contents.add(new ImportedClass(sourcePath, getPath(), name));
	}

	public void addImportedEnum(String sourcePath, String name) {
		contents.add(new ImportedEnum(sourcePath, getPath(), name));
	}

	public void addNote(PackageElement target, String note) {
		notes.add(new Note(target, note));
	}

	public Package addPackage(String path, String name) {
		Package p = new Package(path, name);
		contents.add(p);
		return p;
	}

	public void addEdge(PackageElement source, PackageElement target, String label) {
		edges.add(new Edge(source, target, label));
	}

	public void addInherits(PackageElement source, PackageElement target) {
		edges.add(new Inherits(source, target, ""));
	}

	@Override
	public String getPlantUML() {
		if (isVisible()) {
			String s = getName().length() == 0 ? "" : "package \"<color:#Brown><size:14>" + getName() + "\" as " + getName() + "{\n";
			if (packageNote != null) {
				s += "note \"" + packageNote + "\" as NoteFor" + getName() + "\n";
			}
			for (PackageElement e : contents) {
				s += e.getPlantUML();
			}
			for (Edge e : edges) {
				s += e.getPlantUML();
			}
			for (Note n : notes) {
				s += n.getPlantUML();
			}
			s += getName().length() == 0 ? "\n" : "}\n";
			return s;
		} else
			return "";
	}

	@Override
	public void populate(VisibilityTree tree, DefaultMutableTreeNode parent, Vector<TreePath> visible, Vector<TreePath> invisible) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(this);
		tree.add(child, parent);
		for (String i : getImports()) {
			tree.add(new ImportNode(i), child);
		}
		for (PackageElement e : contents) {
			e.populate(tree, child, visible, invisible);
		}
		for (Edge e : edges) {
			e.populate(tree, child, visible, invisible);
		}
		for (Note n : notes) {
			n.populate(tree, child, visible, invisible);
		}
		if (isVisible())
			visible.add(new TreePath(child.getPath()));
		else
			invisible.add(new TreePath(child.getPath()));
	}

	public Vector<PackageElement> getContents() {
		return contents;
	}

	public Vector<Edge> getEdges() {
		return edges;
	}

	public PackageElement getElement(String name) {
		for (PackageElement e : contents) {
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public boolean hasClass(String path, String name) {
		if (getPath().equals(path))
			return hasLocalClass(name);
		else
			return hasImportedClass(path, name);
	}

	public ImportedClass getImportedClass(String path, String name) {
		return (ImportedClass) getElement(ImportedClass.constructName(path, name, getPath()));
	}

	public boolean hasImportedClass(String path, String name) {
		return getElement(ImportedClass.constructName(path, name, getPath())) != null;
	}

	private boolean hasLocalClass(String name) {
		return getElement(name) != null;
	}

	public Vector<Note> getNotes() {
		return notes;
	}

	public void showImports(String path, boolean show) {
		for (PackageElement e : contents) {
			if (e instanceof ImportedClass) {
				ImportedClass c = (ImportedClass) e;
				if (c.getSourcePath().equals(path))
					c.setVisible(show);
			}
		}
	}

	public HashSet<String> getImports() {
		HashSet<String> imports = new HashSet<String>();
		for (PackageElement e : contents) {
			if (e instanceof ImportedClass) {
				ImportedClass c = (ImportedClass) e;
				imports.add(c.getSourcePath());
			}
		}
		return imports;
	}

	public String getPath() {
		return path;
	}

}
