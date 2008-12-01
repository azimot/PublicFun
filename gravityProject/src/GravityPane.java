import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GravityPane extends JPanel {
	private long INTERVAL = 100;
	private final Color BGROUND_COLOR = Color.WHITE;

	private List<GravityObject> objects;

	private PrintStream log;

	public GravityPane() throws FileNotFoundException {
		super();
		log = new PrintStream(new File("PhysLog"));
		setBackground(BGROUND_COLOR);
		setPreferredSize(new Dimension(500, 500));
		objects = new ArrayList<GravityObject>();
		addMouseListener(clickListener);
		addMouseMotionListener(mouseMoveListener);
	}

	public void advanceFrame() {
		for (GravityObject obj : objects) {
			obj.fall(INTERVAL);
			/*
			 * log.println(String.format("Position: %8f, %8f   Velocity: %8f, %8f" ,
			 * obj.getPosition_X(), obj.getPosition_Y(), obj .getVelocity_X(),
			 * obj.getVelocity_Y()));
			 */
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (GravityObject obj : objects) {
			obj.draw((Graphics2D) g, getHeight());
		}
	}

	public void addObject(GravityObject obj) {
		if (obj == null)
			return;
		objects.add(obj);
	}

	private MouseListener clickListener = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			Point xFormedPoint = new Point(e.getX(), e.getComponent().getHeight() - e.getY());
			for (GravityObject obj : objects) {
				if (obj instanceof Dragable && ((Dragable)obj).containsPoint(xFormedPoint)) {
					System.out.println("Grabbed something");
					Dragable objD = (Dragable) obj;
					objD.grabedOnto(e);
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			for (GravityObject obj : objects) {
				if (obj instanceof Dragable) {
					((Dragable) obj).letGo();
				}
			}
		}
	};

	private MouseMotionListener mouseMoveListener = new MouseMotionListener() {

		@Override
		public void mouseDragged(MouseEvent e) {
			Point xFormedPoint = new Point(e.getX(), e.getComponent().getHeight() - e.getY());
			for (GravityObject obj : objects) {
				if (obj instanceof Dragable && ((Dragable) obj).containsPoint(xFormedPoint)  && ((Dragable) obj).isHeld()) {
					System.out.println("Grabbed something");
					Dragable objD = (Dragable) obj;
					objD.grabedOnto(e);
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	};
}