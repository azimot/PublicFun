package plotter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Plotter extends JPanel {

	private static final Color B_GROUND_COLOR = Color.WHITE;
	private static final Color LINE_COLOR = Color.BLUE;
	private static final Color POINT_COLOR = Color.BLACK;
	private static final Color RULE_COLOR = Color.LIGHT_GRAY;

	public static int VIEWPORT_MAX = 10;
	public static int VIEWPORT_MIN = -10;
	public static int Y_MAX = 10;
	public static int Y_MIN = -10;
	public static int X_MAX = VIEWPORT_MAX;
	public static int X_MIN = VIEWPORT_MIN;
	private static int WIDTH = X_MAX - X_MIN;
	private static int HEIGHT = Y_MAX - Y_MIN;

	private static final int POINT_DIAMETER = 4;
	private static final double SLOPE_TOLLERANCE = Double.MAX_VALUE;

	private Point mouseLoc;

	private List<Point2D.Double> points;
	private boolean connectedMode;
	private boolean onTop = true;

	private JFrame plotHolder;

	public Plotter() {
		this(true);
	}

	public Plotter(boolean connectedModeP) {
		points = new ArrayList<Point2D.Double>();
		setPreferredSize(new Dimension(400, 400));
		setBackground(B_GROUND_COLOR);
		addMouseMotionListener(mouseMove);
		connectedMode = connectedModeP;
	}

	public void plotPoint(Point2D.Double p) {
		points.add(p);
		repaint();
	}

	public void addPoint(final double x, final double y) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				plotPoint(new Point2D.Double(x, y));
			}
		};
		SwingUtilities.invokeLater(r);
	}

	public void clear() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				points.clear();
				repaint();
			}
		};
		SwingUtilities.invokeLater(r);

	}

	public void startPlotter() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				createAndRun();
			}
		};
		SwingUtilities.invokeLater(r);
	}

	private void createAndRun() {
		plotHolder = new JFrame();
		plotHolder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		plotHolder.add(this);
		plotHolder.pack();
		plotHolder.setAlwaysOnTop(onTop);
		plotHolder.setVisible(true);
		addMouseMotionListener(mouseMove);
	};

	public void stopPlotter() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				points.clear();
				plotHolder.dispose();
			}
		};
		SwingUtilities.invokeLater(r);
	}

	public void zoomOut(int factor) {
		changeViewport(X_MAX * factor, X_MIN * factor, Y_MAX * factor, Y_MIN * factor);
	}

	public void zoomIn(int factor) {
		changeViewport(X_MAX / factor, X_MIN / factor, Y_MAX / factor, Y_MIN / factor);
	}

	public void widenView(int factor) {
		changeViewport(X_MAX * factor, X_MIN * factor, Y_MAX, Y_MIN);
	}

	public void thinView(int factor) {
		changeViewport(X_MAX / factor, X_MIN / factor, Y_MAX, Y_MIN);
	}

	public void growViewHeight(int factor) {
		changeViewport(X_MAX, X_MIN, Y_MAX * factor, Y_MIN * factor);
	}

	public void shrinkViewHeight(int factor) {
		changeViewport(X_MAX, X_MIN, Y_MAX / factor, Y_MIN / factor);
	}

	public void changeViewport(int xMax, int xMin, int yMax, int yMin) {
		if (xMax < 1)
			return;
		if (yMax < 1)
			return;
		X_MAX = xMax;
		X_MIN = xMin;
		Y_MAX = yMax;
		Y_MIN = yMin;
		VIEWPORT_MAX = X_MAX;
		VIEWPORT_MIN = X_MIN;
		WIDTH = X_MAX - X_MIN;
		HEIGHT = Y_MAX - Y_MIN;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		paintGrid(g2);
		paintPoints(g2);
		if (mouseLoc != null)
			paintMouseLocation(g2);
	}

	private void paintPoints(Graphics2D g) {
		boolean paintHugeSlope = true;
		Color originalColor = g.getColor();
		Stroke originalStroke = g.getStroke();
		g.setColor(POINT_COLOR);
		g.setStroke(new BasicStroke(2));
		Point oldJavaPoint = null;
		Point2D.Double oldRegPoint = null;
		for (Point2D.Double point : points) {
			if (point.x > X_MAX || point.x < X_MIN) {
				oldJavaPoint = null;
				oldRegPoint = null;
				continue;
			}
			if (point.y > Y_MAX || point.y < Y_MIN) {
				oldJavaPoint = null;
				oldRegPoint = null;
				continue;
			}
			Point javaPoint = shiftPointToJavaCoord(point, getHeight(), getWidth());
			if (connectedMode && oldRegPoint != null) {
				double slope;
				if (oldRegPoint.x - point.x != 0) {
					slope = (oldRegPoint.y - point.y) / (double) (oldRegPoint.x - point.x);
				} else {
					slope = 0;
				}
				// For tan graphs we want to exclude the lines between points that shouldn't be
				// there
				if (paintHugeSlope || Math.abs(slope) < SLOPE_TOLLERANCE) {
					g.setColor(LINE_COLOR);
					g.drawLine(oldJavaPoint.x, oldJavaPoint.y, javaPoint.x, javaPoint.y);
					g.setColor(POINT_COLOR);
				}
			} else {
				g.fillOval(javaPoint.x - POINT_DIAMETER / 2, javaPoint.y - POINT_DIAMETER / 2,
						POINT_DIAMETER, POINT_DIAMETER);
			}
			oldJavaPoint = javaPoint;
			oldRegPoint = point;
		}

		g.setStroke(originalStroke);
		g.setColor(originalColor);
	}

	private static Point shiftPointToJavaCoord(Point2D.Double cartesianCoord,
			int componentHeight, int componentWidth) {
		// this assumes the view is always centered on the origin
		double relativeX = cartesianCoord.x + X_MAX;
		double relativeY = cartesianCoord.y + Y_MAX;

		int javaCoordX = (int) ((relativeX / WIDTH) * componentWidth);
		int javaCoordY = componentHeight - (int) ((relativeY / HEIGHT) * componentHeight);
		return new Point(javaCoordX, javaCoordY);
	}

	private static Point2D.Double shiftJavaPointToCartesianCoord(Point javaPoint,
			int componentHeight, int componentWidth) {
return null;
	}

	private void paintMouseLocation(Graphics2D g) {
		Color originalColor = g.getColor();
		Stroke originalStroke = g.getStroke();
		final int RECT_HEIGHT = 15;
		final int RECT_WIDTH = 68;
		g.setColor(B_GROUND_COLOR);
		g.fillRect(0, 0, RECT_WIDTH, RECT_HEIGHT);
		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(0, 0, RECT_WIDTH, RECT_HEIGHT);
		g.setColor(Color.BLACK);
		String toDraw = String.format("X:%d, Y:%d", mouseLoc.x, mouseLoc.y);
		g.drawString(toDraw, 1, 12);
	}

	private void paintGrid(Graphics2D g) {
		// save some things before changing them
		Color originalColor = g.getColor();
		Stroke originalStroke = g.getStroke();

		g.setColor(RULE_COLOR);

		final int MINI_LINE_LENGTH = 10;

		// Main lines
		g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
		g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
		// Mini lines
		for (int i = 1; i < WIDTH; i++) {
			// the ones on the x axis
			g.drawLine(i * getWidth() / WIDTH, getHeight() / 2 - MINI_LINE_LENGTH / 2, i
					* getWidth() / WIDTH, getHeight() / 2 + MINI_LINE_LENGTH / 2);
			// the ones on the y axis
			g.drawLine(getWidth() / 2 - MINI_LINE_LENGTH / 2, i * getHeight() / HEIGHT,
					getWidth() / 2 + MINI_LINE_LENGTH / 2, i * getHeight() / HEIGHT);
		}

		g.setStroke(originalStroke);
		g.setColor(originalColor);
	}

	private MouseMotionListener mouseMove = new MouseMotionListener() {
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		private JFrame getFrameContainer(Component c) {
			Component cur = c;
			while (c.getClass() != JFrame.class) {
				cur = cur.getParent();
			}
			return (JFrame) cur;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			final int TOP_BAR_OFFSET = 0;
			mouseLoc = e.getPoint();
			mouseLoc.y -= TOP_BAR_OFFSET;
			Plotter.this.repaint(true);
		}
	};

	private void repaint(boolean paintOnlyMouseCoord) {
		Graphics2D g = (Graphics2D) getGraphics();
		paintMouseLocation(g);
	}
}
