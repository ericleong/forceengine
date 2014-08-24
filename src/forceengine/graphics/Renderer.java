package forceengine.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.List;

import forceengine.objects.Circle;
import forceengine.objects.ForceCircle;
import forceengine.objects.Line;
import forceengine.objects.StaticCircle;
import forceengine.objects.StaticLine;

public class Renderer {
	public List<ForceCircle> forceCircles;
	public List<StaticCircle> staticCircles;
	public List<StaticLine> staticLines;

	public Color defaultColor = Color.gray;

	public Renderer(List<ForceCircle> forceCircles,
			List<StaticCircle> staticCircles, List<StaticLine> staticLines) {
		this.forceCircles = forceCircles;
		this.staticCircles = staticCircles;
		this.staticLines = staticLines;
	}

	public void render(Graphics2D g) {
		for (StaticCircle sc : staticCircles) {
			render(g, sc);
		}
		for (StaticLine l : staticLines) {
			render(g, l);
		}
		for (ForceCircle fc : forceCircles) {
			render(g, (Circle)fc);
		}
	}
	
	public void render(Graphics2D g, Circle c){
		if (c instanceof Painter)
			((Painter) c).paint(g);
		else {
			if (c instanceof Colored)
				g.setColor(((Colored) c).getColor());
			else
				g.setColor(defaultColor);
			g.fill(new Ellipse2D.Double(c.getMinX(), c.getMinY(), c
					.getWidth(), c.getHeight()));
		}
	}
	
	public void render(Graphics2D g, Line l){
		if (l instanceof Painter)
			((Painter) l).paint(g);
		else {
			if (l instanceof Colored)
				g.setColor(((Colored) l).getColor());
			else
				g.setColor(defaultColor);
			g.draw(new Line2D.Double(l.getX1(), l.getY1(), l.getX2(), l
					.getY2()));
		}
	}
}
