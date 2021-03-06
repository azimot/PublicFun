import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FourierFrame extends JPanel {
	public int VIEW_WIDTH = GraphPanel.VIEW_WIDTH / 2;
	private Color bGroundColor;
	private MainGraphFrame parentComponent;

	public FourierFrame(MainGraphFrame parent) {
		super();
		parentComponent = parent;
		setPreferredSize(new Dimension(1024, 250));
		bGroundColor = Color.BLACK;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!parentComponent.noFile) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(bGroundColor);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			int curMid = parentComponent.buttons.slide.getValue();

			if (curMid < 128)
				curMid = 128;
			else if (curMid > parentComponent.listOfSampleValues.size() - 128)
				curMid = parentComponent.listOfSampleValues.size() - 128;

			parentComponent.setFourierVals(parentComponent
					.getListOfFourierPointsFromSamplePoints(
							parentComponent.listOfSampleValues, curMid - 128));

			int max = findMaxOfFouriers(parentComponent.listOfFourierValues,
					curMid - 128, curMid + 128);

			g2.setColor(Color.WHITE);
			Point point1, point2;
			// point1 = new Point(0, parentComponent.listOfFourierValues.get(0));
			point2 = new Point(getWidth() / VIEW_WIDTH, getHeight()
					- resolvePointY(parentComponent.listOfFourierValues.get(0), max));
			// System.out.print(curMid + "\n");
			paintRule(g2, max);
			// for (int i = 0; i < 256 / 2; i++) {
			for (int i = 0; i < 250 / 2; i++) {
				point1 = point2;
				point2 = new Point(i * (getWidth() / VIEW_WIDTH), getHeight()
						- resolvePointY(parentComponent.listOfFourierValues.get(i), max));
				g2.setStroke(new BasicStroke(2));
				g2.setColor(new Color(100, 0, 0));

				g2.setColor(Color.YELLOW);
				g2.setStroke(new BasicStroke(2));
				g2.drawLine(point1.x, point1.y, point2.x, point2.y);

			}
		} else {

			g.drawString("No File Selected", 200, 300);
		}
	}

	public int resolvePointY(int rawInput, int max) {
		// if (true)return rawInput;
		double relative = rawInput / (double) max;
		return (int) (relative * getHeight());
	}

	private void paintRule(Graphics2D g2, int max) {
		Color originalColor = g2.getColor();
		Stroke originalStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(1));

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(Color.DARK_GRAY);

		for (int i = 1; i < 8; i++) {
			g2.drawLine(0, i * 32, this.getWidth(), i * 32);
		}

		g2.setColor(Color.WHITE);
		g2.drawLine(10, 0, 10, this.getHeight());
		g2.drawLine(10, 4, 20, 4);
		g2.drawString(max + "", 22, 8);

		g2.setColor(originalColor);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(originalStroke);
	}

	public void setBground(Color color) {
		bGroundColor = color;
	}

	private static int findMaxOfFouriers(ArrayList<Integer> in, int j, int k) {
		int ret = 0;
		for (int i = 0; i < in.size(); i++) {
			if (in.get(i) > ret)
				ret = in.get(i);
		}
		return ret;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
