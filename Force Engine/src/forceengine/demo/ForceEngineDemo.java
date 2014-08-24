package forceengine.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import java.util.ListIterator;

import javax.swing.JComponent;
import javax.swing.JFrame;

import forceengine.demo.objects.ForceCircle_opt;
import forceengine.demo.objects.ForceCircle_opt_color;
import forceengine.demo.objects.Gel;
import forceengine.demo.objects.Mover;
import forceengine.demo.objects.Particle;
import forceengine.demo.objects.Pusher;
import forceengine.demo.objects.ForceCircle_opt.Constraint;
import forceengine.game.Freezable;
import forceengine.graphics.Colored;
import forceengine.graphics.Renderer;
import forceengine.input.AbstractAction;
import forceengine.input.Action;
import forceengine.input.HoldState;
import forceengine.input.InputBind;
import forceengine.input.SetState;
import forceengine.input.ToggleState;
import forceengine.math.VectorMath;
import forceengine.objects.Circle;
import forceengine.objects.Force;
import forceengine.objects.ForceCircle;
import forceengine.objects.Point;
import forceengine.objects.PointVector;
import forceengine.objects.RectVector;
import forceengine.objects.StaticCircle;
import forceengine.objects.StaticLine;
import forceengine.objects.Vector;
import forceengine.physics.PhysicsEngine;
import forceengine.physics.PhysicsMath;
import forceengine.physics.integrator.EulerIntegrator;
import forceengine.physics.integrator.RK4Integrator;

public class ForceEngineDemo implements Runnable {
	private JFrame Window;
	private Thread runner;

	private ToggleState pause = new ToggleState(), dev = new ToggleState(),
			gravity = new ToggleState(true), gravitation = new ToggleState(),
			collide = new ToggleState(true) {
				public void act(int action, InputEvent event) {
					if(action != Action.PRESSED)
						return;
					if(grabbed != null){
						if(grabbed instanceof ForceCircle_opt_color)
							((ForceCircle_opt_color)grabbed).setCollide(!grabbed.isCollide(grabbed));
					} else {
						super.act(action, event);
						for(int i = 0; i < engine.getForceCircles().size(); i++){
							if(engine.getForceCircle(i) instanceof ForceCircle_opt_color)
								((ForceCircle_opt_color)engine.getForceCircle(i)).setCollide(false);
						}
					}
				}
			}, fieldlines = new ToggleState(), help = new ToggleState() {
				public void act(int action, InputEvent event) {
					super.act(action, event);
					intro = 0;
				}
			};
			
	private HoldState createLine = new HoldState(), createConstraint = new HoldState(), createStatic = new HoldState(), createPusher= new HoldState();
	private HoldState windLeft = new HoldState(), windRight = new HoldState(),
			windUp = new HoldState(), windDown = new HoldState(),
			incRadius = new HoldState(), decRadius = new HoldState(),
			incBounce = new HoldState(), decBounce = new HoldState(),
			incDrag = new HoldState(), decDrag = new HoldState(),
			liquid = new HoldState(), removeFirst = new HoldState(), removeLast = new HoldState();
	
	private HoldState pointer = new HoldState(){
		public void act(int action, InputEvent event){
			if(action == Action.PRESSED){
				pointer.state = true;
				x = inputBind.x;
				y = inputBind.y;
				grabbed = selectObj(inputBind.x, inputBind.y);
				if(grabbed != null){
					if(grabbed instanceof Force){
						Force f = (Force) grabbed;
						if(f.isFrozen()){
							f.setPoint(inputBind.x, inputBind.y);
							dragged = false;
							forceAccel.setRect(0, 0);
						}
					} else {
						grabbed.setPoint(inputBind.x, inputBind.y);
					}
				} else {
					nextForceCircle = new ForceCircle_opt_color(inputBind.x, inputBind.y,
							2 * Math.PI * Math.random(), 0, size, Math.PI * size * size
									* density, restitution, collide.state, addColor(), true);
				}
				dragged = true;
			}else if(action == Action.RELEASED && pointer.state){
				if (grabbed != null){
					if(createConstraint.state){
						Point p = selectObj(inputBind.x, inputBind.y);
						// attach objects together
						if(grabbed != p && grabbed instanceof ForceCircle_opt && p instanceof ForceCircle_opt){
							ForceCircle_opt f1 = (ForceCircle_opt)grabbed;
							ForceCircle_opt f2 = (ForceCircle_opt)p;
							double d = f1.distance(f2);
							f1.addEnd(f2, d);
							f2.addEnd(f1, d);
						}
					}
					grabbed = null;
					forceAccel.setRect(0, 0);
				} else {
					if (grabbed == null) {
						if (createLine.state
								&& !createStatic.state) {
							addline = true;
						} else if (createStatic.state) {
							addstaticcircle = true;
						} else if (createPusher.state){
							addpusher = true;
						} else {
							addcircle = true;
						}
					}
				}
				pointer.state = false;
				dragged = false;
			}
		}
	};
	
	private HoldState suck = new HoldState(){
		public void act(int action, InputEvent event){
			if(action == Action.PRESSED){
				if(grabbed != null){
					if(grabbed instanceof Freezable){
						Freezable fz = (Freezable) grabbed;
						fz.setFrozen(!fz.isFrozen());
						if(fz.isFrozen()){
							grabbed.setPoint(inputBind.x, inputBind.y);
						}else{
							forceAccel.setRect(0, 0);
						}
					}
				}else if(!pointer.state){
					suck.state = true;
					sucker.setAlive(true);
					x = inputBind.x;
					y = inputBind.y;
					sucker.setRadius(size);
					if(createStatic.state)
						sucker.setDirection(-1);
					else
						sucker.setDirection(1);
					sucker.setPoint(x, y);
					addsucker = true;
				}
			}else if(action == Action.RELEASED){
				if(pointer.state){
					pointer.state = false;
					dragged = false;
				}
				sucker.setAlive(false);
				suck.state = false;
			}
		}
	};
	
	private SetState clear = new SetState();

	private long starttime;
	private long currenttime;
	private long fpslimiter = 16666666; // 8000000 = 125 FPS
	private long framelength;
	private long[] framelengths = new long[16];

	private WindowPane renderwindow;
	private BufferStrategy bs;
	private Color bckgrnd = Color.white;
	private int backgroundtype;

	private PhysicsEngine engine;
	private Renderer renderer;
	
	private ForceCircle nextForceCircle;
	private int size = 16; // size of next obj
	
	private Point grabbed = null; // obj that is being grabbed
	
	private Mover sucker;

	private int x, y;
	private boolean dragged;
	private InputBind inputBind;

	private static final double density = .01;
	private static final double restitution_default = .975F;
	private double restitution = restitution_default;
	private double drag = .01;

	private long delay = 500000000; // need a bit of a delay before resizing window
	private long intro;
	private boolean demo = true;
	private byte ball = 1;

	private Vector forceAccel = new RectVector(0, 0);
	private boolean addcircle, addline, addstaticcircle, addSpring, addsucker,
			addpusher;

	private static final double gravaccel = 5;
	private static final Vector gravAccel = new RectVector(0, gravaccel);
	private static final double gravC = 100;
	
	public static void main(String args[]){
//		System.setProperty("sun.java2d.opengl","True");
//		System.setProperty("sun.java2d.d3d","True");
		@SuppressWarnings("unused")
		ForceEngineDemo test;
		test = new ForceEngineDemo(640, 480);
	}

	@SuppressWarnings("serial")
	private class WindowPane extends JComponent {

		protected void paintComponent(Graphics gp){ // whatever you do in here kills FPS as it's dragged;
			if(delay < 0)
				engine.setBounds(getWidth(), getHeight());
		}
		public WindowPane(){
			super();
		}
	}

	public ForceEngineDemo(int width, int height){
		engine = new PhysicsEngine(width, height){
			public Vector accelerate(Force f, PointVector pv, double t){
				Vector v = super.accelerate(f, pv, t);
				
				if(f != null && grabbed != null && f == grabbed){
					v.add(forceAccel);
				}
				
				// circular motion around a single point
//					v.add(new RectVector(getBounds().getWidth() / 4 - .5 * pv.getX(), getBounds().getHeight() / 4 - .5 * pv.getY()));
//					v.add(PhysicsMath.inverseAttraction(1000, pv, getBounds().getPoint()));
				
				// circular motion (twirling)
//					v.add(new RectVector(-.1*pv.getvy(), .1*pv.getvx()));
				
				// drag
				v.add(new RectVector(-drag * pv.getvx(), -drag * pv.getvy()));
				
				// spin
//					Vector spin = VectorMath.getVector(pv, getBounds().getPoint());
//					spin.setRect(spin.getvy(), -spin.getvx());
//					spin.scale(.1);
//					v.add(spin);
				
//					v.add(PhysicsMath.inverseAttraction(f.getMass() * pv.getLength() * pv.getLength(), pv, getBounds().getPoint()));

				// rocket
//					v.add(new RectVector(8 * pv.getnormvx(), 8 * pv.getnormvy()));
				
				if(gravity.state)
					v.add(gravAccel);
				
				if(windLeft.state)
					v.add(new RectVector(-5, 0));
				if(windRight.state)
					v.add(new RectVector(5, 0));
				if(windUp.state)
					v.add(new RectVector(0, -5));
				if(windDown.state)
					v.add(new RectVector(0, 5));
				
				if(gravitation.state){
					for (ForceCircle b : ForceCircles){
						if (f.isResponsive(b)) {
							v.add(PhysicsMath.gravitationalAcceleration(gravC, f, b));
						}
					}
				}
				
				return v;
			}
		};
		
		renderer = new Renderer(engine.ForceCircles, engine.StaticCircles, engine.Lines);
		
		// input
		inputBind = new InputBind();
		inputBind.addKeyAction(KeyEvent.VK_SPACE, "pause", pause);
		inputBind.addKeyAction(KeyEvent.VK_BACK_QUOTE, "dev", dev);
		inputBind.addKeyAction(KeyEvent.VK_F1, "help", help);
		inputBind.addKeyAction(KeyEvent.VK_G, "gravity", gravity);
		inputBind.addKeyAction(KeyEvent.VK_B, "gravitation", gravitation);
		inputBind.addKeyAction(KeyEvent.VK_F4, "fieldlines", fieldlines);
		
		inputBind.addKeyAction(KeyEvent.VK_A, "windLeft", windLeft);
		inputBind.addKeyAction(KeyEvent.VK_D, "windRight", windRight);
		inputBind.addKeyAction(KeyEvent.VK_W, "windUp", windUp);
		inputBind.addKeyAction(KeyEvent.VK_S, "windDown", windDown);
		
		inputBind.addKeyAction(KeyEvent.VK_EQUALS, "incRadius", incRadius);
		inputBind.keyMap.put(KeyEvent.VK_PLUS, "incRadius");
		inputBind.addKeyAction(KeyEvent.VK_MINUS, "decRadius", decRadius);
		inputBind.keyMap.put(KeyEvent.VK_SUBTRACT, "decRadius");
		
		inputBind.addKeyAction(KeyEvent.VK_PERIOD, "incBounce", incBounce);
		inputBind.addKeyAction(KeyEvent.VK_COMMA, "decBounce", decBounce);
		
		inputBind.addKeyAction(KeyEvent.VK_QUOTE, "incDrag", incDrag);
		inputBind.addKeyAction(KeyEvent.VK_SEMICOLON, "decDrag", decDrag);
		
		inputBind.addKeyAction(KeyEvent.VK_V, "collide", collide);
		inputBind.addKeyAction(KeyEvent.VK_ENTER, "liquid", liquid);
		
		inputBind.addKeyAction(KeyEvent.VK_DELETE, "removeFirst", removeFirst);
		inputBind.addKeyAction(KeyEvent.VK_BACK_SPACE, "removeLast", removeLast);
		
		inputBind.addKeyAction(KeyEvent.VK_CONTROL, "createLine", createLine);
		inputBind.addKeyAction(KeyEvent.VK_SHIFT, "createStatic", createStatic);
		inputBind.addKeyAction(KeyEvent.VK_ALT, "createConstraint", createConstraint);
		inputBind.addKeyAction(KeyEvent.VK_Z, "createPusher", createPusher);
		
		inputBind.addKeyAction(KeyEvent.VK_F5, "integrator", new AbstractAction(){
			public void act(int action, InputEvent e){
				if(action != Action.PRESSED)
					return;
				if(engine.getIntegrator() instanceof EulerIntegrator)
					engine.setIntegrator(new RK4Integrator());
				else
					engine.setIntegrator(new EulerIntegrator());
			}
		});
		
		inputBind.addKeyAction(KeyEvent.VK_C, "clear", clear);
		
		inputBind.addKeyAction(KeyEvent.VK_1, "bouncebounds", new AbstractAction(){
			public void act(int action, InputEvent e){
				if(action != Action.PRESSED)
					return;
				engine.setBoundDetection(PhysicsEngine.BOUNDS_DETECT_INSIDE);
				engine.setBoundResponse(PhysicsEngine.BOUNDS_RESPOND_BOUNCE);
			}
		});
		
		inputBind.addKeyAction(KeyEvent.VK_2, "nobounds", new AbstractAction(){
			public void act(int action, InputEvent e){
				if(action != Action.PRESSED)
					return;
				engine.setBoundDetection(PhysicsEngine.BOUNDS_DETECT_OUTSIDE);
				engine.setBoundResponse(PhysicsEngine.BOUNDS_RESPOND_NONE);
			}
		});
		
		inputBind.addKeyAction(KeyEvent.VK_F2, "chgbackground", new AbstractAction(){
			public void act(int action, InputEvent e){
				if(action != Action.PRESSED)
					return;
				backgroundtype++;
				if(backgroundtype > 2)
					backgroundtype = 0;
				background();
			}
		});
		
		inputBind.addKeyAction(KeyEvent.VK_F3, "chgball", new AbstractAction(){
			public void act(int action, InputEvent event){
				if(action != Action.PRESSED)
					return;
				ball++;
				if(ball >= 2)
					ball = 0;
			}
		});
		
		inputBind.addMouseAction(MouseEvent.BUTTON1, "pointer", pointer);
		inputBind.addMouseAction(MouseEvent.BUTTON3, "suck", suck);
		
		inputBind.addMouseWheelAction(MouseEvent.NOBUTTON, "chgRadius", new AbstractAction(){
			public void act(int action, InputEvent event){
				MouseWheelEvent e = (MouseWheelEvent) event;
				if(grabbed != null){
					changeRadius(e.getWheelRotation());
				}else{
					size += e.getWheelRotation();
					if(size < 4)
						size = 4;
					if(size > 120)
						size = 120;
					if (nextForceCircle != null) {
						nextForceCircle.setRadius(size);
						nextForceCircle.setMass(Math.PI * size * size * density);
					}
				}
			}
		});
		
		Dimension scrnsize = Toolkit.getDefaultToolkit().getScreenSize();
		// buf = new BufferedImage(640, 480, 1);
		Window = new JFrame("Force Engine 2 - by Eric Leong");
		Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Window.setSize(width, height);
		Window.setLocation((int)scrnsize.getWidth() / 2 - Window.getWidth() / 2,
				(int)scrnsize.getHeight() / 2 - Window.getHeight() / 2);
		Window.setBackground(Color.white);
		renderwindow = new WindowPane();
		renderwindow.setFocusable(false);
		Window.setIconImage(Toolkit.getDefaultToolkit().getImage("forceengine2.png"));

		Window.getContentPane().add(renderwindow);

		Window.addKeyListener(inputBind);
		Window.getContentPane().addMouseListener(inputBind);
		Window.getContentPane().addMouseMotionListener(inputBind);
		Window.getContentPane().addMouseWheelListener(inputBind);

		Window.setVisible(true);

		engine.setBounds(renderwindow.getWidth(), renderwindow.getHeight());
		Window.createBufferStrategy(2);
		bs = Window.getBufferStrategy();
		
		sucker = new Mover(0, 0, size, 1);

		if(demo)
			intro = 5000000000L;
		/*ForceCircle a = new ForceCircle_opt_color(54, 176, -1.1 * Math.PI / 5D, 18.2101F, 4, 50, .989F, collide, new Color(
				0, 140, 255), true);
		engine.addForce(a);
		engine.addForce(new ForceCircle_opt_color(37, 194, 0D, 10.1F, 12F, 100F, .97F, collide, Color.green, true));
		engine.addForce(new ForceCircle_opt_color(63, 189, 1 * Math.PI / 5D, 17.6F, 7, 75, .99F, collide, Color.red, true));*/

		if(runner == null){
			runner = new Thread(this);
			runner.start();
		}
	}
	public void run(){
		while(runner != null){
			try{
				starttime = System.nanoTime(); // start work
				insert(framelength);
				keyCheck(); // like getKey but with the support of more than 1 key
				if(!pause.state)
					engine.components();
				do{
					Graphics g = null;
					try{
						g = bs.getDrawGraphics();
						Graphics g2 = g.create(Window.getComponent(0).getX(), Window.getComponent(0).getY(),
								renderwindow.getWidth(), renderwindow.getHeight());
						render((Graphics2D)g2);
					}catch(NullPointerException e){
						if(bs == null){
							Window.createBufferStrategy(2);
							bs = Window.getBufferStrategy();
							Window.requestFocusInWindow();
						}
					}catch(IllegalStateException e){
						Window.createBufferStrategy(2);
						bs = Window.getBufferStrategy();
						Thread.sleep(1);
					}finally{
						if(g != null)
							g.dispose();
					}
				}while(bs.contentsLost());
				bs.show();
				currenttime = System.nanoTime(); // end work

				// is there extra time left?
				long diftime;
				if((diftime = Math.abs(currenttime - starttime)) < fpslimiter){
					try{ // sleep for specifed amount of time
						Thread.sleep((long)((fpslimiter - diftime) / 1000000),
								(int)(((fpslimiter - diftime) % 1000000)));
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				framelength = System.nanoTime() - starttime;
			}catch(NullPointerException e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void keyCheck(){
		if (pointer.state && !createLine.state && !createConstraint.state) {
			if(grabbed instanceof Force){
				Force f = (Force) grabbed;
				if(!f.isFrozen())
					forceAccel.setRect((inputBind.x - grabbed.getX()) / 10, (inputBind.y - grabbed.getY()) / 10);
				else
					grabbed.setPoint(inputBind.x, inputBind.y);
			} else if (grabbed != null){
				grabbed.setPoint(inputBind.x, inputBind.y);
			}
		} else if (pointer.state && createLine.state){
			forceAccel.setRect(0, 0);
		}
		
		if(sucker != null && sucker.isAlive() && createStatic.state){
			sucker.setDirection(-1);
		}
		
		if(addline){
			engine.addLine(new StaticLine(x, y, inputBind.x, inputBind.y));
			addline = false;
		}
		
		if(addcircle){
			engine.addForceCircle(nextForceCircle);
			((ForceCircle_opt_color) engine.getForceCircles().get(
					engine.getForceCircles().size() - 1))
					.setCollide(collide.state);
			addColor();
			addcircle = false;
		}
		
		if(addstaticcircle){
			engine.addStaticCircle(new StaticCircle(inputBind.x, inputBind.y, size));
			addstaticcircle = false;
		}
		
		if(addpusher){
			engine.addStaticCircle(new Pusher(x, y, VectorMath.length(inputBind.x - x, inputBind.y - y), inputBind.x - x, inputBind.y - y));
			addpusher = false;
		}
		
		if(addsucker){
			engine.addStaticCircle(sucker);
			addsucker = false;
		}
		
		if(addSpring){
			ForceCircle_opt s1 = new ForceCircle_opt(x, y, size, size * size * Math.PI * density);
			ForceCircle_opt s2 = new ForceCircle_opt(inputBind.x, inputBind.y, size, size * size * Math.PI * density);
			double d = s1.distance(s2);
			s1.addEnd(s2, d);
			s2.addEnd(s1, d);
			engine.addForceCircle(s1);
			engine.addForceCircle(s2);
			
			double side = d;
			Point corner = new Point(x, y).translate(VectorMath.getVector(s1, s2).rotate(Math.PI / 3));
			ForceCircle_opt s3 = new ForceCircle_opt(corner.getX(), corner.getY(), size, size * size * Math.PI * density);
			s1.addEnd(s3, side);
			s3.addEnd(s1, side);
			s2.addEnd(s3, side);
			s3.addEnd(s2, side);
			engine.addForceCircle(s3);
			
			addSpring = false;
		}
		
		// dragged
		if (suck.state == true){
			x = inputBind.x;
			y = inputBind.y;
			sucker.setRadius(size);
			sucker.setPoint(x, y);
		}
		
		if(pointer.state == true){
			if(grabbed != null && !grabbed.isAlive()){
				grabbed = null;
				pointer.state = false;
				dragged = false;
				forceAccel.setRect(0, 0);
			}else if(grabbed != null){
				x = inputBind.x;
				y = inputBind.y;
				dragged = true;
				if(pause.state && !createConstraint.state)
					grabbed.setPoint(inputBind.x, inputBind.y);
			}else{
				double distance = Point.distance(x, y, inputBind.x, inputBind.y);
				if(nextForceCircle != null){
					nextForceCircle.setRect((x - inputBind.x) / 5, (y - inputBind.y) / 5);
					nextForceCircle.setRadius(size);
					nextForceCircle.setMass(Math.PI * size * size * density);
					nextForceCircle.setRestitution(restitution);
					if(distance > 2)
						dragged = true;
				}
			}
		}
		
		
		// change radius
		if(incRadius.state){
			changeRadius(1);
		}
		
		if(decRadius.state){
			changeRadius(-1);
		}
		
		if(decBounce.state){ // decrease bounce
			restitution -= .005F;
			if(restitution < 0)
				restitution = 0;
			for(Force f : engine.getForceCircles())
				f.setRestitution(restitution);
		}
		if(incBounce.state){
			restitution += .005F;
			if(restitution > 1)
				restitution = 1;
			for(Force f : engine.getForceCircles())
				f.setRestitution(restitution);
		}
		
		// drag
		if(decDrag.state){
			drag -= .01F;
			if(drag < 0)
				drag = 0;
		}
		if(incDrag.state){
			drag += .01F;
			if(drag > 4)
				drag = 4;
		}
		
		if (liquid.state && !clear.state) {
			double x = engine.getBounds().getX() + size * Math.random() - .5 * size;
			double y = 4 * size + size * Math.random() - .5 * size;
			engine.addForceCircle(new Gel(x, y, 0, 0, size, Math.PI * size * size
					* density, restitution));
		}
		
		if(removeLast.state){
			if(createLine.state){
				engine.removeLine(engine.getLines().size() - 1);
			}else if(createStatic.state){
				engine.getStaticCircles().remove(engine.getStaticCircles().size() - 1);
			}else{
				if(grabbed instanceof ForceCircle_opt && createConstraint.state){
					ForceCircle_opt f = (ForceCircle_opt) grabbed;
					if(!f.getEnds().isEmpty()){
						Point p = f.getEnds().removeLast().end;
						if(p instanceof ForceCircle_opt){
							for(ListIterator<Constraint> i = ((ForceCircle_opt) p).getEnds().listIterator(); i.hasNext();){
								if(i.next().end == f)
									i.remove();
							}
						}
					}
				}else if(grabbed != null){
					if(grabbed instanceof ForceCircle_opt)
						((ForceCircle_opt)grabbed).setAlive(false);
					else if(grabbed instanceof ForceCircle)
						engine.getForceCircles().remove(grabbed);
					else if(grabbed instanceof StaticCircle)
						engine.getStaticCircles().remove(grabbed);
					else if(grabbed instanceof StaticLine)
						engine.getLines().remove(grabbed);
					grabbed = null;
					pointer.state = false;
					dragged = false;
					forceAccel.setRect(0, 0);
				}else{
					if(engine.getForceCircles().size() > 0)
						engine.getForceCircles().remove(engine.getForceCircles().size() - 1);
				}
			}
			removeLast.state = false;
		}
		
		if(removeFirst.state){
			if(createLine.state){
				engine.removeLine(0);
			}else if(createStatic.state){
				engine.getStaticCircles().remove(0);
			}else{
				if(grabbed instanceof ForceCircle && createConstraint.state){
					ForceCircle_opt f = (ForceCircle_opt) grabbed;
					if(!f.getEnds().isEmpty()){
						Point p = f.getEnds().removeFirst().end;
						if(p instanceof ForceCircle_opt){
							for(ListIterator<Constraint> i = ((ForceCircle_opt) p).getEnds().listIterator(); i.hasNext();){
								if(i.next().end == f)
									i.remove();
							}
						}
					}
				}else if(grabbed != null){
					if(grabbed instanceof ForceCircle_opt)
						((ForceCircle_opt)grabbed).setAlive(false);
					else if(grabbed instanceof ForceCircle)
						engine.getForceCircles().remove(grabbed);
					else if(grabbed instanceof StaticCircle)
						engine.getStaticCircles().remove(grabbed);
					else if(grabbed instanceof StaticLine)
						engine.getLines().remove(grabbed);
					grabbed = null;
					pointer.state = false;
					dragged = false;
					forceAccel.setRect(0, 0);
				}else{
					if(engine.getForceCircles().size() > 0)
						engine.getForceCircles().remove(0);
				}
			}
			removeFirst.state = false;
		}
		
		if(clear.state && liquid.state){
			for(ListIterator<ForceCircle> i = engine.getForceCircles().listIterator(); i.hasNext(); ){
				ForceCircle fc = i.next();
				if(fc instanceof Gel)
					i.remove();
					
			}
			clear.state = false;
			liquid.state = false;
		}else if(clear.state){
			intro = 0;
			if(grabbed != null){
				pointer.state = false;
				dragged = false;
			}
			if(createLine.state){
				engine.getLines().clear();
			}else if(createStatic.state){
				engine.getStaticCircles().clear();
			}else if(createConstraint.state){
				if(grabbed instanceof ForceCircle){
					ForceCircle_opt f = (ForceCircle_opt) grabbed;
					for(ListIterator<Constraint> i = ((ForceCircle_opt) f).getEnds().listIterator(); i.hasNext();){
						Point p = i.next().end;
						if(p instanceof ForceCircle_opt){
							for(ListIterator<Constraint> j = ((ForceCircle_opt) p).getEnds().listIterator(); j.hasNext();){
								if(j.next().end == grabbed)
									j.remove();
							}
						}
						i.remove();
					}
				} else {
					for(ForceCircle fc : engine.getForceCircles()){
						if(fc instanceof ForceCircle_opt){
							((ForceCircle_opt) fc).getEnds().clear();
						}
					}
				}
			}else{
				engine.getLines().clear();
				engine.getStaticCircles().clear();
				engine.getForceCircles().clear();
			}
			grabbed = null;
			clear.state = false;
		}
	}

	public void render(Graphics2D g){
		if(ball >= 1){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		if(backgroundtype != 2){
			g.setColor(bckgrnd);
			g.fillRect(0, 0, (int)engine.getBounds().getWidth(), (int)engine.getBounds().getHeight());
		}
		
		if(grabbed == null){
			if(createStatic.state){
				g.setColor(new Color(128, 128, 128, 192));
				if(dragged)
					g.draw(new Ellipse2D.Double(inputBind.x - size, inputBind.y - size, size * 2, size * 2));
				else
					g.draw(new Ellipse2D.Double(x - size, y - size, size * 2, size * 2));
			}else if(dragged){
				if(createLine.state){
					g.setColor(Color.gray);
					g.drawLine(x, y, inputBind.x, inputBind.y);
				}else if(createPusher.state){
					g.setColor(Color.red);
					double d = Point.distance(x, y, inputBind.x, inputBind.y);
					g.draw(new Ellipse2D.Double(x - d, y - d, 2 * d, 2 * d));
					
					g.draw(new Line2D.Double(x, y, inputBind.x, inputBind.y));
					Vector right = VectorMath.rotate(new RectVector(inputBind.x - x, inputBind.y - y).getUnitVector(), Math.PI/10);
					g.draw(new Line2D.Double(inputBind.x, inputBind.y, x + .8*d*right.getvx(), y + .8*d*right.getvy()));
					Vector left = VectorMath.rotate(new RectVector(inputBind.x - x, inputBind.y - y).getUnitVector(), -Math.PI/10);
					g.draw(new Line2D.Double(inputBind.x, inputBind.y, x + .8*d*left.getvx(), y + .8*d*left.getvy()));
				}else{
					g.setColor(Color.darkGray);
					g.drawLine(x, y, inputBind.x, inputBind.y);
					
					g.setColor(new Color(128, 128, 128, 192));
					g.drawLine(x, y, x + (x - inputBind.x), y + (y - inputBind.y));
					
					g.setColor(Color.gray);
					g.fill(new Ellipse2D.Double(x, y, 2, 2));
					
					g.setColor(new Color(128, 128, 128, 192));
					g.draw(new Ellipse2D.Double(inputBind.x - size, inputBind.y - size, size * 2, size * 2));
				}
			}
		}else if(grabbed != null && dragged){
			g.setColor(Color.gray);
			g.draw(new Line2D.Double(grabbed.getX(), grabbed.getY(), inputBind.x, inputBind.y));
		}

		// remove dead circles
		
		for(java.util.ListIterator<ForceCircle> iter = engine.getForceCircles().listIterator(); iter.hasNext();){
			if(!iter.next().isAlive())
				iter.remove();
		}
		
		for(java.util.ListIterator<StaticCircle> iter = engine.getStaticCircles().listIterator(); iter.hasNext();){
			if(!iter.next().isAlive())
				iter.remove();
		}
		
		renderer.render(g);
		
		// draw constraints
		g.setColor(Color.gray);
		for(ForceCircle fc : engine.getForceCircles()){
			if (fc instanceof ForceCircle_opt)
				for(Constraint constraint : ((ForceCircle_opt)fc).getEnds()){
					g.draw(new Line2D.Double(fc.getX(), fc.getY(), constraint.end.getX(), constraint.end.getY()));
				}
		}
		
		g.setColor(Color.black);
		if(incBounce.state || decBounce.state){
			int leftx = 1;
			g.drawString(String.format("restitution: %.2f", restitution), leftx, 10 + 36);
		}
		if(incDrag.state || decDrag.state){
			int leftx = 1;
			g.drawString(String.format("drag: %.2f", drag), leftx, 10 + 36);
		}
		
		// processing
		
		if(delay > 0){
			delay -= framelength;
		}
		
		if (fieldlines.state){
			ForceCircle f = new ForceCircle(0, 0, 0, 0, 0, 1);
			final double ni = 160;
			final double nj = 90;
			double dx = engine.getBounds().getWidth() / ni;
			double dy = engine.getBounds().getHeight() / nj;
			
			for(int i = 0; i < ni - 1; i++){
				for(int j = 0; j < nj - 1; j++){
					f.setPoint(i * (dx + .5 * dx), j * (dy + .5 * dy));
					Vector a = engine.accelerate(f, f, 1);
					if(a.getLength() * 10 > 255 + 255)
						g.setColor(new Color(255, 0, (int) Math.max(255 - a.getLength() * 10 - 255, 0), 128));
					else if(a.getLength() * 10 > 255)
						g.setColor(new Color((int) Math.min(a.getLength() * 10 - 255, 255), 0, 255, 128));
					else
						g.setColor(new Color(0, 0, (int) (a.getLength() * 10), 128));
					a = a.getUnitVector().scale(6);
					g.draw(new Line2D.Double(f.getX(), f.getY(), f.getX() + a.getvx(), f.getY() + a.getvy()));
				}
			}
		}
		
		if(grabbed != null && dragged && createConstraint.state){
			g.setColor(Color.darkGray);
			g.fill(new Ellipse2D.Double(grabbed.getX() - 4, grabbed.getY() - 4, 8, 8));
		}
		
		// draw other stuff
		
		if(bckgrnd == Color.black)
			g.setColor(Color.white);
		else
			g.setColor(Color.black);
		
		if(intro > 0 || help.state){ // if the intro logo is still displayed
			if(intro > 0){
				if(intro <= 1000000000L)
				g.setColor(new Color(0, 0, 0, (int)(255 * ((double)intro / 1000000000D))));
				intro -= framelength;
			}
			int stringx = Window.getContentPane().getWidth() / 2 - 280;
			int top = Window.getContentPane().getHeight() / 2 - 75;
			if(help.state)
				top -= 200;
			
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
			g.drawString("Force Engine 2", stringx + 158, top + 34);
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
			g.drawString("by Eric Leong", stringx + 222, top + 60);
			if (!help.state) {
				g.setFont(new Font(null, Font.PLAIN, 14));
				g.drawString("Press F1 for Help", stringx + 220, top + 120);
			}
		}
		
		if(help.state){
			int stringx = Window.getContentPane().getWidth() / 2 - 300;
			int top = Window.getContentPane().getHeight() / 2 - 220;
			int rightcol = 350; //offset for right column
			Color color = g.getColor(); //save current color
			
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16)); //special font

			top = top + 24 + 64; //offset top
			final int mult = 20;
			int i = -1;
			// basic
			g.setColor(Color.gray);
			g.drawString("      Mouse", stringx, top + mult * i++);
			g.setColor(color);
			g.drawString("Left Button  --  add circle", stringx, top + mult * i++);
			g.drawString("Right Button  --  sucker", stringx, top + mult * i++);
			g.drawString("CTRL + Left Button (drag) -- add line", stringx, top + mult * i++);
			g.drawString("SHIFT + Left Button -- add static circle", stringx, top + mult * i++);
			g.drawString("ALT + Left Button -- constrain two objects", stringx, top + mult * i++);
			g.drawString("z + Left Button -- add pusher", stringx, top + mult * i++);
			g.drawString("Scrollwheel or (+/-)  --  change circle radius", stringx, top + mult * i++);
			i++;
			g.setColor(Color.gray);
			g.drawString("     Create / Move", stringx, top + mult * i++);
			g.setColor(color);
			g.drawString("Enter  --  spawn fluid", stringx, top + mult * i++);
			g.drawString("w, a, s, d  --  push objects around", stringx, top + mult * i++);
			i++;
			// deletion
			g.setColor(Color.gray);
			g.drawString("     Deletion", stringx, top + mult * i++);
			g.setColor(color);
			g.drawString("Backspace  --  delete newest object", stringx, top + mult * i++);
			g.drawString("Delete  --  delete oldest object", stringx, top + mult * i++);
			g.drawString("c  --  delete all", stringx, top + mult * i++);
			
			i = -1;
			// boundaries
			g.setColor(Color.darkGray);
			g.drawString("              Options", stringx + rightcol, top + mult * i++);
			i++;
			g.setColor(Color.gray);
			g.drawString("     Boundaries", stringx + rightcol, top + mult * i++);
			g.setColor(color);
			g.drawString("1  --  bounce", stringx + rightcol, top + mult * i++);
			g.drawString("2  --  no boundary", stringx + rightcol, top + mult * i++);
			i++;
			// circle options
			g.setColor(Color.gray);
			g.drawString("     Interactions", stringx + rightcol, top + mult * i++);
			g.setColor(color);
			g.drawString("v  --  toggle colliding on and off", stringx + rightcol, top + mult * i++);
			g.drawString("g  --  toggle gravity", stringx + rightcol, top + mult * i++);
			g.drawString("b  --  toggle gravitation", stringx + rightcol, top + mult * i++);
			g.drawString("; & '  --  increase / decrease friction", stringx + rightcol, top + mult * i++);
			g.drawString("< & >  --  decrease / increase resitution", stringx + rightcol, top + mult * i++);
			i++;
			// drawing styles
			g.setColor(Color.gray);
			g.drawString("     Graphics", stringx + rightcol, top + mult * i++);
			g.setColor(color);
			g.drawString("F2 --  toggle background", stringx + rightcol, top + mult * i++);
			g.drawString("F3  --  toggle circle types", stringx + rightcol, top + mult * i++);
			g.drawString("F4  --  display force field", stringx + rightcol, top + mult * i++);
			i++;
			// dev/frame control
			g.drawString("Space --  pause", stringx + rightcol, top + mult * i++);
			g.setFont(new Font(null, Font.PLAIN, 12));
		}
		
		if(dev.state){
			g.setFont(new Font(null, Font.PLAIN, 12));
			int leftx = 1;
			g.setColor(Color.black);
			g.setFont(new Font("sansserif", Font.PLAIN, 12));
			g.drawString(String.format("%.2f", 1000000000 / average()), leftx, 10);
			if(engine.getForceCircles().size() > 0)
				g.drawString(Integer.toString(engine.getForceCircles().size()), leftx, 10 + 12);
			if(engine.getLines().size() > 0)
				g.drawString(Integer.toString(engine.getLines().size()), leftx, 10 + 24);

			if(engine.getForceCircles().size() > 0){
				double totalEnergy = 0, totalMomentum = 0;
				for(ForceCircle fc : engine.getForceCircles()){
					totalEnergy += .5 * fc.getMass() * fc.getLength() * fc.getLength();
					
					totalMomentum += fc.getMass() * fc.getLength();
				}
				for(int i = 0; i < engine.getForceCircles().size(); i++){
					for(int j = i + 1; j < engine.getForceCircles().size(); j++){
						ForceCircle fc = engine.getForceCircle(i);
						ForceCircle fc2 = engine.getForceCircle(j);
						totalEnergy -= gravC * fc.getMass() * fc2.getMass() / fc.distance(fc2);
					}
				}
				g.setFont(new Font("sansserif", Font.PLAIN, 14));
				g.drawString(String.format("E tot: %.2f", totalEnergy), leftx, 10 + 48);
				g.drawString(String.format("p tot: %.2f", totalMomentum), leftx, 10 + 64);
			}
		}
	}
	private void insert(long num){ // insert number into frames array
		for(int i = framelengths.length - 1; i > 0; i--){
			framelengths[i] = framelengths[i - 1];
		}
		framelengths[0] = num;
	}
	private float average(){ // average length of each frame over the past 8 frames (better than using length of last
								// frame for calc)
		int sum = 0;
		for(int i = 0; i < framelengths.length; i++){
			sum += framelengths[i];
		}
		return sum / framelengths.length;
	}

	private Color addColor() {
		Color ncolor;
		boolean redo = false;
		do {
			redo = false;
			switch ((int) Math.round(Math.random() * 10) % 10) {
			case 0:
				ncolor = new Color(234, 253, 0);
				break;
			case 1:
				ncolor = new Color(76, 233, 0);
				break;
			case 2:
				ncolor = new Color(244, 0, 48);
				break;
			case 3:
				ncolor = new Color(152, 5, 200);
				break;
			case 4:
				ncolor = new Color(255, 113, 0);
				break;
			case 5:
				ncolor = new Color(255, 173, 0);
				break;
			case 6:
				ncolor = new Color(14, 64, 201);
				break;
			case 7:
				ncolor = new Color(20, 225, 160);
				break;
			case 8:
				ncolor = new Color(228, 0, 108);
				break;
			default:
				ncolor = new Color(0, 191, 255);
				break;
			}
			
			if (engine.getForceCircles().size() > 1){
				ForceCircle fc = engine.getForceCircle(engine.getForceCircles().size() - 1);
				if (fc instanceof Colored){
					Colored c = (Colored) fc;
					if (c.getColor().equals(ncolor)){
						redo = true;
					}
				}
			}
		} while (redo);
		
		return ncolor;
	}
	private void background(){
		switch(backgroundtype){
		case 1:
			bckgrnd = new Color(255, 255, 255, 64);
			break;
		case 2:
			bckgrnd = new Color(0, 0, 0, 0);
		default:
			bckgrnd = Color.white;
			break;
		}
	}

	public Point selectObj(int x, int y) {
		double mindistsq = -1;
		double distsq;
		Point p = null;
		for (ForceCircle f : engine.getForceCircles()) {
			if (f instanceof Particle || f instanceof Gel)
				continue;
			distsq = Point.distanceSq(x, y, f.getX(), f.getY());
			if (distsq < f.getRadiusSq()
					&& (mindistsq == -1 || distsq <= mindistsq)) {
				p = f;
				mindistsq = distsq;
			}
		}
		
		for (StaticCircle s : engine.getStaticCircles()) {
			distsq = Point.distanceSq(x, y, s.getX(), s.getY());
			if (distsq < s.getRadiusSq()
					&& (mindistsq == -1 || distsq <= mindistsq)) {
				p = s;
				mindistsq = distsq;
			}
		}
		
		for (StaticLine l : engine.getLines()) {
			distsq = Point.distanceSq(x, y, l.getX(), l.getY());
			if (distsq < 5
					&& (mindistsq == -1 || distsq <= mindistsq)) {
				p = l;
				mindistsq = distsq;
			}
		}
		return p;
	}
	
	public void changeRadius(int amount){
		if(amount > 0){ // increase size of next obj
			if(grabbed != null && grabbed instanceof Circle){
				Circle c = (Circle) grabbed;
				c.setRadius(c.getRadius() < 120 ? c.getRadius() + amount : 120);
			}else{
				size++;
				if(size > 120)
					size = 120;
			}
		}
		if(amount < 0){
			if(grabbed != null && grabbed instanceof Circle){
				Circle c = (Circle) grabbed;
				c.setRadius(c.getRadius() > 4 ? c.getRadius() - amount : 8);
			}else{
				size--;
				if(size < 4)
					size = 4;
			}
		}
	}

}
