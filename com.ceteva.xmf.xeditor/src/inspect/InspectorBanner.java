package inspect;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import console.Console;

public class InspectorBanner extends JPanel implements ActionListener {

	private static ImageIcon REFRESH = new ImageIcon("icons/refresh.png", "refresh");
	private static ImageIcon NEW = new ImageIcon("icons/new.png", "new");
	private static ImageIcon CONSTRAINT_SUCCESS = new ImageIcon("icons/constraint_succeeds.png", "success");
	private static ImageIcon CONSTRAINT_FAIL = new ImageIcon("icons/constraint_fails.png", "fail");
	private static ImageIcon ARROW = new ImageIcon("icons/right_arrow.png", "slot");

	private ObjectInspector inspector;
	private int objectHandle;
	private String objectLabel;
	private int classHandle;
	private String classLabel;
	private JLabel colon = new JLabel(" : ");
	private JLabel inspect = new JLabel("Inspect:   ");
	private JLabel relationship = new JLabel(ARROW);
	private JButton refresh = new JButton(REFRESH);
	private JButton _new = new JButton(NEW);
	private JLabel constraints = new JLabel(CONSTRAINT_SUCCESS);
	private Color colour = Color.white;
	private ObjLabel o;
	private TypeLabel c;

	public InspectorBanner(ObjectInspector inspector, int objectHandle, String objectLabel, int classHandle,
			String classLabel, boolean isClass, boolean satisfied, String report) {
		this.inspector = inspector;
		this.objectHandle = objectHandle;
		this.objectLabel = objectLabel;
		this.classHandle = classHandle;
		this.classLabel = classLabel;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		o = new ObjLabel(objectHandle, objectLabel);
		c = new TypeLabel(classHandle, classLabel);
		add(inspect);
		add(o);
		add(colon);
		add(c);
		add(Box.createHorizontalGlue());
		if (satisfied)
			constraints.setIcon(CONSTRAINT_SUCCESS);
		else
			constraints.setIcon(CONSTRAINT_FAIL);
		add(constraints);
		add(relationship);
		ToolTipManager.sharedInstance().registerComponent(refresh);
		ToolTipManager.sharedInstance().registerComponent(_new);
		ToolTipManager.sharedInstance().registerComponent(constraints);
		ToolTipManager.sharedInstance().registerComponent(relationship);
		refresh.setToolTipText("Refresh Object");
		_new.setToolTipText("New Instance");
		relationship.setToolTipText("Set Slot");
		constraints.setToolTipText(report);
		if (isClass) {
			add(_new);
		}
		add(refresh);
		inspect.setFont(InspectorTable.HEADER_FONT);
		o.setFont(InspectorTable.HEADER_FONT);
		colon.setFont(InspectorTable.HEADER_FONT);
		c.setFont(InspectorTable.HEADER_FONT);
		setBorder(BorderFactory.createEtchedBorder());
		inspect.setBackground(colour);
		colon.setBackground(colour);
		o.setBackground(colour);
		c.setBackground(colour);
		setBackground(colour);
		refresh.setBackground(colour);
		refresh.setBorderPainted(false);
		refresh.addActionListener(this);
		_new.setBackground(colour);
		_new.setBorderPainted(false);
		_new.addActionListener(this);
		relationship.setBackground(colour);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == refresh) {
			String snapshot = InspectorManager.MANAGER.currentSnapshotName();
			Console.CONSOLE.send(objectHandle, "edit", new Object[] { snapshot });
		}
		if (e.getSource() == _new) {
			String snapshot = InspectorManager.MANAGER.currentSnapshotName();
			Console.CONSOLE.send("invokeThenEdit",
					new Object[] { objectHandle, "new", new Vector<Object>(), snapshot });
		}
	}

	public void refresh(int objectHandle, String objectLabel, int classHandle, String classLabel, boolean satisfied,
			String report) {
		this.objectHandle = objectHandle;
		this.objectLabel = objectLabel;
		this.classHandle = classHandle;
		this.classLabel = classLabel;
		o.refresh(objectHandle, objectLabel);
		c.refresh(classHandle, classLabel);
		if (satisfied)
			constraints.setIcon(CONSTRAINT_SUCCESS);
		else
			constraints.setIcon(CONSTRAINT_FAIL);
		constraints.setToolTipText(report);
	}

}
