/*
 * PhysicsEngine.java
 * By Eric
 */
package forceengine.physics;

import java.util.ArrayList;
import java.util.List;

import forceengine.game.Triggerable;
import forceengine.math.CircleMath;
import forceengine.math.VectorMath;
import forceengine.objects.Accelerator;
import forceengine.objects.Boundaries;
import forceengine.objects.Circle;
import forceengine.objects.Force;
import forceengine.objects.ForceCircle;
import forceengine.objects.Point;
import forceengine.objects.PointVector;
import forceengine.objects.Rect;
import forceengine.objects.RectVector;
import forceengine.objects.StaticCircle;
import forceengine.objects.StaticLine;
import forceengine.objects.StaticRect;
import forceengine.objects.Vector;
import forceengine.physics.integrator.Integrator;
import forceengine.physics.integrator.RK4Integrator;

/** 
 * Forces Engine (basic 2d physics/game engine)
 * @author Eric Leong
 * @since Dec 28, 2008
 * @version 2
 */
public class PhysicsEngine implements Accelerator {
	
	//list of forces
	public ArrayList<ForceCircle> ForceCircles;
	
	//lines
	public ArrayList<StaticLine> Lines;
	//static circles... mainly for collision stuff that never moves
	public ArrayList<StaticCircle> StaticCircles;
	
//these are reference variables - checked but not changed
	
	private double time, deltaTime;
	
	private byte boundDetection;
	private byte boundResponse;
	private Rect bounds;
	
	private Integrator integrator;
	
//these variables are meant for the engine and not to be modified by externally except through generalforce
	
	public static final byte BOUNDS_DETECT_INSIDE = 1;
	public static final byte BOUNDS_DETECT_MIDDLE = 0;
	public static final byte BOUNDS_DETECT_OUTSIDE = -1;
	
	public static final byte BOUNDS_RESPOND_NONE = 0;
	public static final byte BOUNDS_RESPOND_BOUNCE = 1;
	
	/** started summer '07

	 * <feature list>
	 * - unlimited number of objects
	 * - moving all objec2ts in a certain direction
	 * - different types of boundaries
	 * - obj-obj collision
	 * - obj-obj gravity
	 * - //lines & line-obj collsion
	 * - static objects (circles) useful for maps
	 * - trigger objects (circles) useful to tell if an object is where the user is supposed to get it
	 * - explosions (outward and directed)
	 * - moving trigger objects
	 * - collision selectiveness (pass the object you are checking collision with)
	 * <in the future>
	 *  restructure program for more efficency
	 *  support for bullets (some of the functions are already there)
	 *  double matrix collision reducer [grid system] so fewer calculations are done for collisions
	 *  line-line collsion detection, i need to differentiate between static/moving lines
	 *  polygons and bounding circles for polygons
	 *  
	 */
	
	/**
	 * Creates a new {@link PhysicsEngine} with the specified width and height.
	 */
	public PhysicsEngine(int width, int height){
		this(new StaticRect(width / 2, height / 2, width, height), BOUNDS_DETECT_INSIDE, BOUNDS_RESPOND_BOUNCE);
	}
	
	public PhysicsEngine(Rect bounds, byte boundDetection, byte boundResponse){
		this(new ArrayList<ForceCircle>(), new ArrayList<StaticLine>(), new ArrayList<StaticCircle>(), bounds, boundDetection, boundResponse);
	}
	
	public PhysicsEngine(ArrayList<ForceCircle> ForceCircles, ArrayList<StaticLine> Lines, ArrayList<StaticCircle> StaticCircles, Rect bounds, byte boundDetection, byte boundResponse){
		this.ForceCircles = ForceCircles;
		this.Lines = Lines;
		this.StaticCircles = StaticCircles;
		
		this.bounds = bounds;
		this.boundDetection = boundDetection;
		this.boundResponse = boundResponse;
		
		this.integrator = new RK4Integrator();
		
		time = 1;
		deltaTime = .5;
	}
	
	public String toString(){
		return "Force Engine: by Eric Leong";
	}
	
	public static final double getVersion(){ //returns the version number
		return 2;
	}

	
	
	/**
	 * Main method of <code>PhysicsEngine</code>, takes each of the forcecircles and adjusts their values by one frame.
	 */
	public void components(){ //add all vector components
		
		Vector[] velocities = new Vector[ForceCircles.size()];
		
		// every object is integrated separately
		for(int i = 0; i < ForceCircles.size(); i++){
			Force f = ForceCircles.get(i);
			if(!f.isFrozen()){
				// TODO: don't actually move the object forward (this interferes with collision detection)
				PointVector pv = integrator.integrate(this, f, time, deltaTime);
				// XXX: I have no idea which this is supposed to be
//				f.setVector(VectorMath.getVector(pv, f));
				velocities[i] = pv.getVector();
//				f.setPointVector(pv);
				
				if(Double.isInfinite(f.getX()) || Double.isNaN(f.getX()))
					f.setX(0);
				if(Double.isInfinite(f.getY()) || Double.isNaN(f.getY()))
					f.setY(0);
				if(Double.isInfinite(f.getvx()) || Double.isNaN(f.getvx()))
					f.setvx(0);
				if(Double.isInfinite(f.getvy()) || Double.isNaN(f.getvy()))
					f.setvy(0);
				
				if(f.getvx() > -1E-2 && f.getvx() < 1E-2)
					f.setvx(0);
				if(f.getvy() > -1E-2 && f.getvy() < 1E-2)
					f.setvy(0);
			}
		}
		
		try{
			// TODO: clean this up : determine why circles still intersect
			// TODO: determine why circles can't rest on two lines
			Collideresult cr;
			cr = checkforcecirclescollide(ForceCircles, StaticCircles, Lines);
			if(cr.collidelist.size() > 0)
				cr = checkforcecirclescollide(ForceCircles, StaticCircles, Lines, cr.collidetime, cr.modified, false);
			
			for(int i = Math.min(ForceCircles.size(), cr.modified.length) - 1; i >= 0; i--){
				ForceCircle fc = ForceCircles.get(i);
				if(!fc.isFrozen()){
					if(!cr.modified[i]){ //unchanged, so move it forward
						fc.setPoint(fc.getX2(), fc.getY2());
						fc.setVector(velocities[i]);
						
						if(cr.collidetime[i] == 1) { //if it has not been moved forward (in the last collide() call) , move it forward
							//the collide function moves collided forcecircles forward if they have collided in that session
							fc.setPoint(fc.getX() + (1-cr.timepassed[i])*fc.getvx(), fc.getY() + (1-cr.timepassed[i])*fc.getvy());
							//there may still be more of the vector to move forward but you haven't done the collision checking what's left
						}
					} else {
						// XXX: this seems to solve a problem with circles sticking to lines
						if(cr.collidetime[i] == 0) {
							fc.setPoint(fc.getX2(), fc.getY2());
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Vector accelerate(Force f, PointVector pv, double t) {
		Vector next = f.getAcceleration(pv, t);
		for (ForceCircle b : ForceCircles){
			if (f.isResponsive(b)) {
				next.add(f.responsiveAcceleration(pv, t, b));
				if (b instanceof Accelerator)
					next.add(((Accelerator) b).accelerate(f, pv, t));
			}
		}
		for (StaticCircle sc : StaticCircles) {
			if (f.isResponsive(sc)) {
				next.add(f.responsiveAcceleration(pv, t, sc));
				if (sc instanceof Accelerator)
					next.add(((Accelerator) sc).accelerate(f, pv, t));
			}
		}
		for (StaticLine l : Lines){
			if (f.isResponsive(l)) {
				next.add(f.responsiveAcceleration(pv, t, l));
				if (l instanceof Accelerator)
					next.add(((Accelerator) l).accelerate(f, pv, t));
			}
		}
		return next;
	}
	
/*
 * END OF COMPONENTS
 */
	
	/**
	 * An object that has collided into another object.
	 * 
	 * @author Eric
	 * 
	 */
	private class Collider {
		/**
		 * The object that performed the collision.
		 */
		private Object collideobj;
		/**
		 * The location of the object in the array that contains it.
		 */
		private Integer collideindex;
		/**
		 * The amount of power transferred by the object.
		 */
		private Vector collideforce;
		
		@SuppressWarnings("unused")
		public Collider(Vector collideobj, Integer collidewith){
			this.collideobj = collideobj;
			this.collideindex = collidewith;
			this.collideforce = collideobj;
		}
		public Collider(Object collideobj, Integer collidewith, Vector collideforce){
			this.collideobj = collideobj;
			this.collideindex = collidewith;
			this.collideforce = collideforce;
		}
		public Object getCollideobj(){
			return collideobj;
		}
		public Integer getCollidewith(){
			return collideindex;
		}
		public Vector getCollideforce(){
			return collideforce;
		}
		/**
		 * Sets a new force transfered to the object.
		 * @param collideforce The new <code>Vector</code>.
		 */
		public void setCollideforce(Vector collideforce){
			this.collideforce = collideforce;
		}
		public String toString(){
			return collideobj.getClass().getSimpleName() + " @ " + collideindex + " hit with " + collideforce.toString();
		}
	}

	/**
	 * Stores a reference to the <code>Object</code> an <code>Object</code> has collided into and the index of the
	 * <code>Object</code>. Don't modify the lists directly.
	 * 
	 * @author Eric
	 */
	private class Collideclassindex {
		private ArrayList<Collider> colliders; // object you collided into
		private int numforcecircles;

		public Collideclassindex(){
			colliders = new ArrayList<Collider>(1);
			numforcecircles = 0;
		}
		/**
		 * Creates a new instance of <code>Collideclassindex</code> with the specified arguments.
		 * 
		 * @param collideclass The class of the <code>Object</code> the <code>Object</code> collided into.
		 * @param collidewith The index of the <code>Object</code> that was collided into.
		 */
		@SuppressWarnings("unused")
		public Collideclassindex(Object collideclass, int collidewith, Vector collideforce){
			colliders = new ArrayList<Collider>(1);
			colliders.add(new Collider(collideclass, collidewith, collideforce));
		}

		/**
		 * Returns whether or not there have been any collisions by testing if <code>size()</code> > 0.
		 * 
		 * @return <code>true</code> if there are collisions.
		 */
		public boolean collided(){
			return size() > 0;
		}
		/**
		 * Resets this object.
		 */
		public void reset(){
			this.colliders.trimToSize();
			this.colliders.clear();
			numforcecircles = 0;
		}
		public void setCollided(Object collideobj, int collidewith, Vector collideforce){
			if(size() > 0)
				reset();
			if(collideobj instanceof ForceCircle && !((ForceCircle)collideobj).isFrozen())
				numforcecircles++;
			colliders.add(new Collider(collideobj, collidewith, collideforce));
		}
		public void addCollided(Object collideobj, int collidewith, Vector collideforce){
			if(collideobj instanceof ForceCircle && !((ForceCircle)collideobj).isFrozen())
				numforcecircles++;
			colliders.add(new Collider(collideobj, collidewith, collideforce));
		}
		public ArrayList<Collider> getColliders(){
			return colliders;
		}
		public int getNumforcecircles(){
			return numforcecircles;
		}
		public boolean containsBoundaries(){
			for(Collider o : this.colliders){
				if(o.getCollideobj() instanceof Boundaries)
					return true;
			}
			return false;
		}
		public Collider contains(Object o){
			for(Collider c : colliders){
				if(c.getCollideobj().equals(o))
					return c;
			}
			return null;
		}
		/**
		 * 
		 * @return the size of <b>colliders</b>.
		 */
		public int size(){
			return this.colliders.size();
		}
		public String toString(){
			String s = "";
			for(Collider c : colliders){
				s += c.toString() + "\n";
			}
			return s;
		}
	}

	/**
	 * private class that contains the resultant vectors and the index of the <code>ForceCircle</code> that each
	 * <code>ForceCircle</code> collided with.
	 * 
	 * @author Eric
	 */
	private class Collideresult {
		/**
		 * The new <code>PointVector</code> for each of the <code>ForceCircles</code>.
		 */
		protected PointVector[] resultants;
		/**
		 * The list of <code>ForceCircles</code> that collided.
		 */
		protected ArrayList<Integer> collidelist;
		/**
		 * The Object, index, and power transferred of each object that collided into a certain object.
		 */
		protected Collideclassindex[] collideinto;
		/**
		 * The amount of time passed to arrive at the location given by <b>resultvectors</b> from the location given by
		 * circles.
		 */
		protected double[] timepassed;
		/**
		 * The amount of time passed to arrive at the location given by <b>resultvectors</b> from the initial location.
		 */
		protected double[] collidetime; // when each object collided
		/**
		 * Whether or not a vector has been modified since it's initial location (cumulative over a whole frame)
		 */
		protected boolean[] modified; // whether or not a forcecircle has been modified

		/**
		 * Creates a <code>Collideresult</code> with the specified parameters.
		 * 
		 * @param resultvectors the new <code>PointVector</code> for each of the <code>ForceCircles</code>.
		 * @param collideinto the Object, index, and power transferred of each object that collided into a certain
		 * object.
		 * @param collidelist the list of <code>ForceCircles</code> that collided.
		 * @param timepassed the amount of time passed to arrive at the location given by <b>resultvectors</b> from the
		 * location given by circles.
		 * @param collidetime the amount of time passed to arrive at the location given by <b>resultvectors</b> from the
		 * initial location.
		 * @param modified whether or not a vector has been modified since it's initial location (cumulative over a
		 * whole frame)
		 */
		public Collideresult(PointVector[] resultvectors, Collideclassindex[] collideinto,
				ArrayList<Integer> collidelist, double[] timepassed, double[] collidetime, boolean[] modified){
			this.resultants = resultvectors;
			this.collideinto = collideinto;
			this.collidelist = collidelist;
			this.timepassed = timepassed;
			this.collidetime = collidetime;
			this.modified = modified;
		}
		public String toString(){
			return collidelist.toString();
		}
	}
	
	/**
	 * Checks collision between <code>ForceCircles</code> and
	 * <code>StaticCircles</code>.
	 * 
	 * @param forceCircle
	 *            a list of <code>ForceCircles</code>.
	 * @param staticcircles
	 *            a list of <code>StaticCircles</code>.
	 * @return the new locations and vectors after any collisions.
	 */
	public final Collideresult checkforcecirclescollide(List<ForceCircle> forceCircle,
			ArrayList<StaticCircle> staticcircles, ArrayList<StaticLine> lines){
		double[] time = new double[forceCircle.size()];
		boolean[] collided = new boolean[forceCircle.size()];
		for(int i = forceCircle.size() - 1; i >= 0; i--)
			time[i] = 1;
		return checkforcecirclescollide(forceCircle, staticcircles, lines, time, collided, true);
	}

	/**
	 * Checks for collision between <code>ForceCircles</code> and other
	 * <code>Objects</code>. Can be run multiple times using the
	 * <code>timepassed</code> and <code>collided</code> fields of
	 * <code>Collideresult</code> to calculate successive collisions, allowing
	 * for one <code>Object</code> to collide with multiple <code>Objects</code>
	 * at different times along it's initial vector.
	 * 
	 * @param forceCircle2
	 *            The <code>ArrayList</code> of <code>ForceCircles</code> that
	 *            are to be tested for collisions between themselves and other
	 *            <code>Objects</code>.
	 * @param staticcircles
	 *            An <code>ArrayList</code> of <code>StaticCircles</code> that
	 *            are to be tested against <b>circles</b> for collision.
	 * @param timepassed
	 *            The amount of time (between 0 and 1) that has passed to get
	 *            from the Object's initial position to the object's current
	 *            position as returned by <b>circles</b>.
	 * @param modified
	 *            An array corresponding with <b>circles</b> that indicates
	 *            whether or not that specific <code>ForceCircle</code> has
	 *            collided with another <code>Object</code> due to it's initial
	 *            vector. <code>false</code> if the vector in <b>circles</b> is
	 *            the same as the original (before collision testing) vector.
	 * @param fullcheck
	 *            If <code>true</code>, checks every <code>ForceCircle</code>
	 *            against every other. If <code>false</code>, does not check
	 *            collision between two <code>ForceCircles</code> if
	 *            <b>collided</b> for both <code>ForceCircles</code> is
	 *            <code>false</code>.
	 * @return A <code>Collideresult</code> that contains: </br> The resultant
	 *         <code>PointVector</code> for each object, a list of
	 *         <code>ForceCircles</code> that collided, the time of each
	 *         <code>ForceCircle</code> collision, what each
	 *         <code>ForceCircle</code> collided into (if anything), an adjusted
	 *         <code>timepassed</code> that takes into account the new
	 *         collisions, and a corrected <b>collided</b> adjusted for the new
	 *         collisions.
	 */
	public Collideresult checkforcecirclescollide(List<ForceCircle> forceCircle2,
			ArrayList<StaticCircle> staticcircles, ArrayList<StaticLine> lines, double[] timepassed, boolean[] modified, boolean fullcheck){
		Collideresult cr = checkforcecirclescollidetime(forceCircle2, staticcircles, lines, timepassed, modified, fullcheck);
		ArrayList<Object> collidedinto = new ArrayList<Object>();
		for(int i = cr.resultants.length - 1; i >= 0; i--){
			// set the objects new position + velocity
			if(cr.collideinto[i].collided())
				forceCircle2.get(i).setPointVector(cr.resultants[i]);
			collidedinto.clear();
			
			// trigger objects if necessary
			for (int j = cr.collideinto[i].size() - 1; j >= 0; j--) {
				Object o = cr.collideinto[i].getColliders().get(j).getCollideobj();
				collidedinto.add(o);
				if (o instanceof Triggerable)
					((Triggerable) o).trigger(new Object[]{forceCircle2.get(i)});
			}
			if (forceCircle2.get(i) instanceof Triggerable)
				((Triggerable) forceCircle2.get(i)).trigger(collidedinto.toArray());
		}
		return cr;
	}

	/**
	 * Checks for collision between <code>ForceCircles</code> and other
	 * <code>Objects</code>. Does not move objects forward and does not trigger
	 * <code>Triggers</code>. Use <code>checkforcecirclescollide()</code> for
	 * multiple calls. Should only be used for collision testing between moving
	 * <code>ForceCircles</code>.
	 * 
	 * @param forceCircle2
	 *            The <code>ArrayList</code> of <code>ForceCircles</code> that
	 *            are to be tested for collisions between themselves and other
	 *            <code>Objects</code>.
	 * @param staticcircles
	 *            An <code>ArrayList</code> of <code>StaticCircles</code> that
	 *            are to be tested against <b>circles</b> for collision.
	 * @param timepassed
	 *            The amount of time (between 0 and 1) that has passed to get
	 *            from the Object's initial position to the object's current
	 *            position as returned by <b>circles</b>.
	 * @param modified
	 *            An array corresponding with <b>circles</b> that indicates
	 *            whether or not that specific <code>ForceCircle</code> has
	 *            collided with another <code>Object</code> due to it's initial
	 *            vector. <code>false</code> if the vector in <b>circles</b> is
	 *            the same as the original (before collision testing) vector.
	 * @param fullcheck
	 *            If <code>true</code>, checks every <code>ForceCircle</code>
	 *            against every other. If <code>false</code>, does not check
	 *            collision between two <code>ForceCircles</code> if
	 *            <b>collided</b> for both <code>ForceCircles</code> is
	 *            <code>false</code>.
	 * @return A <code>Collideresult</code> that contains: </br> The resultant
	 *         <code>PointVector</code> for each object, a list of
	 *         <code>ForceCircles</code> that collided, the time of each
	 *         <code>ForceCircle</code> collision, what each
	 *         <code>ForceCircle</code> collided into (if anything), an adjusted
	 *         <code>timepassed</code> that takes into account the new
	 *         collisions, and a corrected <b>collided</b> adjusted for the new
	 *         collisions.
	 */
	// add sliding (or rolling)
	// then add gridding (sectioning off of areas) :\
	public final Collideresult checkforcecirclescollidetime(List<ForceCircle> forceCircle2,
			ArrayList<StaticCircle> staticcircles, ArrayList<StaticLine> lines, double[] timepassed, boolean[] modified, boolean fullcheck){
		
		double upperxbound = bounds.getMaxX();
		double upperybound = bounds.getMaxY();
		double lowerxbound = bounds.getMinX();
		double lowerybound = bounds.getMinY();
		
		//refereces to the forcecircle you're currently working on
		ForceCircle a;
		ForceCircle b = null;
		
		PointVector[] results = new PointVector[forceCircle2.size()]; // the resultant vectors
		double[] closestdistsq = new double[forceCircle2.size()]; // the dist between circle and collision
		//stores what object each object collided into
		Collideclassindex[] collisionindex = new Collideclassindex[forceCircle2.size()];
		double[] collisiontimes = new double[forceCircle2.size()];

		// fill arrays
		for(int i = forceCircle2.size() - 1; i >= 0; i--){ 
			// done backwards to prevent overruns when arraylist is modified
			closestdistsq[i] = -1;
			collisionindex[i] = new Collideclassindex();
//			results[i] = new PointVector(circles.get(i));
			results[i] = forceCircle2.get(i);
			collisiontimes[i] = 1;
		}
		// test collision with walls
		int walls = 0;
		for(int i = Math.min(forceCircle2.size(), timepassed.length) - 1; i >= 0; i--){ //count down so you don't have out of bounds
			a = forceCircle2.get(i);
			if(!fullcheck && !modified[i])
				continue;
			if(a.isFrozen()) 
				continue;
			
			if (boundResponse == BOUNDS_RESPOND_NONE)
				continue;
			
			double cx1 = 0, cy1 = 0;
			walls = 0;
			
			double rad = a.getRadius() * boundDetection;
			if (boundResponse != BOUNDS_RESPOND_NONE) {
				// are you outside bounds?
				if (a.getX() + rad > upperxbound) // right
					a.setX(upperxbound - rad);
				if (a.getX() - rad < lowerxbound) // left
					a.setX(lowerxbound + rad);
				if (a.getY() + rad > upperybound) // bottom
					a.setY(upperybound - rad);
				if (a.getY() - rad < lowerybound) // top
					a.setY(lowerybound + rad);
			}
			// top
			if(a.getY2() - rad < lowerybound){
				if(!a.isCollide(new Boundaries(Boundaries.top, boundDetection))) continue;
				double A1 = ((lowerybound + rad) - (lowerybound + rad));
				double B1 = (lowerxbound - upperxbound);
				double C1 = ((lowerybound + rad) - (lowerybound + rad)) * lowerxbound
						+ (lowerxbound - upperxbound) * (lowerybound + rad);
				double A2 = (a.getvy());
				double B2 = (-a.getvx());
				double C2 = ((a.getvy()) * a.getX() + (-a.getvx()) * a.getY());
				double det = A1 * B2 - A2 * B1;
				double cx = 0;
				// line - vector intersect
				if(det != 0){
					//location of collision
					cx = (B2 * C1 - B1 * C2) / det;
					// closest point to obj1 on line
					double distsq = a.distanceSq(cx, lowerybound + rad);
					//if collision is after the end of the vector, it's not a collision
					if(!((timepassed[i] < 1 && timepassed[i] >= 0) && ((1 - timepassed[i]) * a.getLength())
							* ((1 - timepassed[i]) * a.getLength()) < distsq)){
						if(distsq < closestdistsq[i] || closestdistsq[i] < 0){
							walls = Boundaries.top;
							cx1 = (double)cx;
							cy1 = lowerybound + rad;
							closestdistsq[i] = distsq;
						}else if(distsq == closestdistsq[i]){ // on the line (same (x,y) values)
							walls += Boundaries.top;
							cx1 = (double)cx;
							cy1 = lowerybound + rad;
						}
					}
				}
			}
			// right
			if(a.getX2() + rad > upperxbound){
				if(!a.isCollide(new Boundaries(Boundaries.right, boundDetection))) continue;
				double A1 = (upperybound - lowerybound);
				double C1 = (upperybound - lowerybound) * (upperxbound - rad)
						+ ((upperxbound - rad) - (upperxbound - rad)) * lowerybound;
				double A2 = (a.getvy());
				double B2 = (-a.getvx());
				double C2 = ((a.getvy()) * a.getX() + (-a.getvx()) * a.getY());
				double det = A1 * B2;
				double cy = 0;
				// line - vector intersect
				if(det != 0){
					cy = (A1 * C2 - A2 * C1) / det;
					double distsq = Point.distanceSq(a.getX(), a.getY(), upperxbound - rad, cy);
					//if collision is after the end of the vector, it's not a collision
					if(!((timepassed[i] < 1 && timepassed[i] >= 0) && ((1 - timepassed[i]) * a.getLength())
							* ((1 - timepassed[i]) * a.getLength()) < distsq)){
						if(distsq < closestdistsq[i] || closestdistsq[i] < 0){
							walls = Boundaries.right;
							cx1 = upperxbound - rad;
							cy1 = (double)cy;
							closestdistsq[i] = distsq;
						}else if(distsq == closestdistsq[i]){ // on the line (same (x,y) values)
							walls += Boundaries.right;
							cx1 = upperxbound - rad;
							cy1 = (double)cy;
						}
					}
				}
			}
			// bottom
			if(a.getY2() + rad > upperybound){
				if(!a.isCollide(new Boundaries(Boundaries.bottom, boundDetection))) continue;
				double A1 = ((upperybound - rad) - (upperybound - rad));
				double B1 = (lowerxbound - upperxbound);
				double C1 = ((upperybound - rad) - (upperybound - rad)) * lowerxbound
						+ (lowerxbound - upperxbound) * (upperybound - rad);
				double A2 = (a.getvy());
				double B2 = (-a.getvx());
				double C2 = ((a.getvy()) * a.getX() + (-a.getvx()) * a.getY());
				double det = A1 * B2 - A2 * B1;
				double cx = 0;
				// line - vector intersect
				if(det != 0){
					cx = (B2 * C1 - B1 * C2) / det;
					double distsq = Point.distanceSq(a.getX(), a.getY(), cx, upperybound - rad);
					//if collision is after the end of the vector, it's not a collision
					if(!((timepassed[i] < 1 && timepassed[i] >= 0) && ((1 - timepassed[i]) * a.getLength())
							* ((1 - timepassed[i]) * a.getLength()) < distsq)){
						if(distsq < closestdistsq[i] || closestdistsq[i] < 0){
							walls = Boundaries.bottom;
							cx1 = (double)cx;
							cy1 = upperybound - rad;
							closestdistsq[i] = distsq;
						}else if(distsq == closestdistsq[i]){ // on the line (same (x,y) values)
							walls += Boundaries.bottom;
							cy1 = upperybound - rad;
							cx1 = cx;
						}
					}
				}
			}
			// left
			if(a.getX2() - rad < lowerxbound){
				if(!a.isCollide(new Boundaries(Boundaries.left, boundDetection))) continue;
				double A1 = (upperybound - lowerybound);
				double B1 = ((lowerxbound + rad) - (lowerxbound + rad));
				double C1 = (upperybound - lowerybound) * (lowerxbound + rad)
						+ ((lowerxbound + rad) - (lowerxbound + rad)) * lowerybound;
				double A2 = (a.getvy());
				double B2 = (-a.getvx());
				double C2 = ((a.getvy()) * a.getX() + (-a.getvx()) * a.getY());
				double det = A1 * B2 - A2 * B1;
				double cy = 0;
				// line - vector intersect
				if(det != 0){ //location of collision
					cy = (A1 * C2 - A2 * C1) / det;
					double distsq = Point.distanceSq(a.getX(), a.getY(), lowerxbound + rad, cy);
					//if collision is after the end of the vector, it's not a collision
					if(!((timepassed[i] < 1 && timepassed[i] >= 0) && ((1 - timepassed[i]) * a.getLength())
							* ((1 - timepassed[i]) * a.getLength()) < distsq)){
						if(distsq < closestdistsq[i] || closestdistsq[i] < 0){
							walls = Boundaries.left;
							cx1 = lowerxbound + rad;
							cy1 = (double)cy;
							closestdistsq[i] = distsq;
						}else if(distsq == closestdistsq[i]){ // on the line (same (x,y) values)
							walls += Boundaries.left;
							cx1 = lowerxbound + rad;
							cy1 = (double)cy;
						}
					}
				}
			}
			if(closestdistsq[i] >= 0){
				if(!collisionindex[i].collided())
					results[i] = new PointVector(cx1, cy1);
				if(closestdistsq[i] < 0.00001 && closestdistsq[i] > 0){
					// a.setVector(closesta);
					closestdistsq[i] = 0;
					collisiontimes[i] = 0;
				}
				if(closestdistsq[i] > 0){
					results[i] = new PointVector(cx1, cy1);
				}
				// the impact force is wrong because it's not calculated (will be calculated when it matters)
				collisionindex[i].setCollided(new Boundaries(walls, boundDetection), walls, a);
				// if(closestdistsq[i] == 0) array.get(i).setVector(closesta);
			}
		}
//test collision with lines
		
		StaticLine line;
		for(int i = Math.min(forceCircle2.size(), timepassed.length) - 1; i >= 0; i--){
			//important to not change staticcircle between calls to this method
			//otherwise fullcheck should be true
			//an object that has not collided with any other object within a particular frame
			//should not have collision checking done again against the list of static circles
			if(!fullcheck && !modified[i])
				continue;
			a = forceCircle2.get(i);
			if(a.isFrozen())
				continue;
			for(int j = lines.size() - 1; j >= 0; j--){
				line = lines.get(j);
				if(!a.isCollide(line))
					continue;
				Point intersp = VectorMath.closestpointonline(line.getX1(), line.getY1(), line.getX2(), line.getY2(), a.getX(), a.getY());
				double distsq = Point.distanceSq(intersp.getX(), intersp.getY(), a.getX(), a.getY());
				if(distsq < a.getRadiusSq()){
					double cx = 0, cy = 0;
					if(distsq == 0){ //circle is on the line
						Point pm = Point.midpoint(line.getP1(), line.getP2());
						double lineLength = line.getP1().distance(line.getP2());
						double distsq2 = a.distanceSq(pm);
						if(distsq2 < Math.pow(a.getRadius() + lineLength/2, 2) ){
							if(distsq2 != 0){
								double dist = a.distance(pm);
								double vx = (double)((a.getX() - pm.getX()) / dist);
								double vy = (double)((a.getY() - pm.getY()) / dist);
								cx = pm.getX() + (a.getRadius() + lineLength/2) * vx;
								cy = pm.getY() + (a.getRadius() + lineLength/2) * vy;
							}else{ //circle is at midpoint
								cx = a.getX();
								cy = a.getY();
							}
							if(closestdistsq[i] == -1)
								results[i] = new PointVector((double)cx, (double)cy);
							else
								results[i].setPoint((double)cx, (double)cy);
							if(closestdistsq[i] == 0){
								collisionindex[i].addCollided(line, j, a.getVector());
							}else{
								collisionindex[i].setCollided(line, j, a.getVector());
							}
							closestdistsq[i] = 0;
							continue;
						}else if(distsq2 == Math.pow(a.getRadius() + lineLength/2, 2) && a.getLength() == 0){
							continue;
						}
					}else if(Math.min(line.getX1(), line.getX2()) <= intersp.getX()
							&& intersp.getX() <= Math.max(line.getX1(), line.getX2())
							&& Math.min(line.getY1(), line.getY2()) <= intersp.getY()
							&& intersp.getY() <= Math.max(line.getY1(), line.getY2())){ //hits the line
						double dist = Math.sqrt(distsq);
						double vx = (double)((a.getX() - intersp.getX()) / dist);
						double vy = (double)((a.getY() - intersp.getY()) / dist);
						cx = intersp.getX() + a.getRadius() * vx;
						cy = intersp.getY() + a.getRadius() * vy;
						if(closestdistsq[i] == -1)
							results[i] = new PointVector((double)cx, (double)cy);
						else
							results[i].setPoint((double)cx, (double)cy);
						if(closestdistsq[i] == 0){
							collisionindex[i].addCollided(line, j, a.getVector());
						}else{
							collisionindex[i].setCollided(line, j, a.getVector());
						}
						closestdistsq[i] = 0;
						continue;
					}else if(Point.distanceSq(a.getX(), a.getY(), line.getX1(), line.getY1()) < a.getRadiusSq()){
						 //hits one end of the line
						double dist = Point.distance(a.getX(), a.getY(), line.getX1(), line.getY1());
						double vx = (double)((a.getX() - line.getX1()) / dist);
						double vy = (double)((a.getY() - line.getY1()) / dist);
						cx = line.getX1() + a.getRadius() * vx;
						cy = line.getY1() + a.getRadius() * vy;
						if(closestdistsq[i] == -1)
							results[i] = new PointVector((double)cx, (double)cy);
						else
							results[i].setPoint((double)cx, (double)cy);
						if(closestdistsq[i] == 0){
							collisionindex[i].addCollided(line, j, a.getVector());
						}else{
							collisionindex[i].setCollided(line, j, a.getVector());
						}
						closestdistsq[i] = 0;
						continue;
					}else if(Point.distanceSq(a.getX(), a.getY(), line.getX2(), line.getY2()) < a.getRadiusSq()){
						//hits the other end of the line
						double dist = Point.distance(a.getX(), a.getY(), line.getX2(), line.getY2());
						double vx = (double)((a.getX() - line.getX2()) / dist);
						double vy = (double)((a.getY() - line.getY2()) / dist);
						cx = line.getX2() + a.getRadius() * vx;
						cy = line.getY2() + a.getRadius() * vy;
						if(closestdistsq[i] == -1)
							results[i] = new PointVector((double)cx, (double)cy);
						else
							results[i].setPoint((double)cx, (double)cy);
						if(closestdistsq[i] == 0){
							collisionindex[i].addCollided(line, j, a.getVector());
						}else{
							collisionindex[i].setCollided(line, j, a.getVector());
						}
						closestdistsq[i] = 0;
						continue;
					}
		    	}
				double A1 = (line.getY2() - line.getY1());
		    	double B1 = (line.getX1() - line.getX2());
		    	double C1 = (line.getY2() - line.getY1())*line.getX1() + (line.getX1() - line.getX2())*line.getY1();
		    	double A2 = (a.getvy());
		    	double B2 = (-a.getvx());
		    	double C2 = ((a.getvy())*a.getX() + (-a.getvx())*a.getY());
		    	double det = A1*B2 - A2*B1;
		    	//double dist = (line.getX2()-line.getX1()*obj1.getY()-line.getY1() - line.getY2()-line.getY1()*obj1.getX()-line.getX1())/line.getlength(0);
		    	double cx = 0;
		    	double cy = 0;
		    	//line - vector intersect
		    	if(det != 0){
			    	cx = (B2*C1 - B1*C2)/det;
			    	cy = (A1*C2 - A2*C1)/det;
		    	}
		    	/*double avx = a.getRadius()*a.getnormvx();
		    	double avy = a.getRadius()*a.getnormvy();*/
		    	/*double lvx = a.getRadius()*(line.getX2()-line.getX1())/line.getlength(0);
		    	double lvy = a.getRadius()*(line.getY2()-line.getY1())/line.getlength(0);*/
		    	//on the line
		    	Point p = VectorMath.closestpointonline(line.getX1(), line.getY1(), line.getX2(), line.getY2(), a.getX2(), a.getY2());
		    	//on the vector
		    	Point p1 = VectorMath.closestpointonline(a.getX(), a.getY(), a.getX2(), a.getY2(), line.getX1(), line.getY1());
		    	Point p2 = VectorMath.closestpointonline(a.getX(), a.getY(), a.getX2(), a.getY2(), line.getX2(), line.getY2());
		    	
		    	if(((Point.distanceSq(p.getX(), p.getY(), a.getX2(), a.getY2()) < a.getRadiusSq() && 
		    			Math.min(line.getX1(), line.getX2()) <= p.getX() && p.getX() <= Math.max(line.getX1(), line.getX2()) && 
		    			Math.min(line.getY1(), line.getY2()) <= p.getY() && p.getY() <= Math.max(line.getY1(), line.getY2()) ) ||
		    			(Point.distanceSq(p1.getX(), p1.getY(), line.getX1(), line.getY1()) < a.getRadiusSq() &&
		    				Math.min(a.getX(), a.getX()+a.getvx()) <= p1.getX() && p1.getX() <= Math.max(a.getX(), a.getX()+a.getvx()) && 
		    	    		Math.min(a.getY(), a.getY()+a.getvy()) <= p1.getY() && p1.getY() <= Math.max(a.getY(), a.getY()+a.getvy()) ) ||
		    			(Point.distanceSq(p2.getX(), p2.getY(), line.getX2(), line.getY2()) < a.getRadiusSq() &&
		    					Math.min(a.getX(), a.getX()+a.getvx()) <= p2.getX() && p2.getX() <= Math.max(a.getX(), a.getX()+a.getvx()) && 
		    	    			Math.min(a.getY(), a.getY()+a.getvy()) <= p2.getY() && p2.getY() <= Math.max(a.getY(), a.getY()+a.getvy()) ) ||
		    			(Math.min(a.getX(), a.getX()+a.getvx()) <= cx && cx <= Math.max(a.getX(), a.getX()+a.getvx()) && 
		    			Math.min(a.getY(), a.getY()+a.getvy()) <= cy && cy <= Math.max(a.getY(), a.getY()+a.getvy()) && 
		    			Math.min(line.getX1(), line.getX2()) <= cx && cx <= Math.max(line.getX1(), line.getX2()) && 
		    			Math.min(line.getY1(), line.getY2()) <= cy && cy <= Math.max(line.getY1(), line.getY2())))
		    			|| (Point.distanceSq(line.getX1(), line.getY1(), a.getX2(), a.getY2()) <= a.getRadiusSq() || 
			    			Point.distanceSq(line.getX2(), line.getY2(), a.getX2(), a.getY2()) <= a.getRadiusSq())
		    			 ){
		    		double A3 = -B1;
			    	double B3 = A1;
			    	double C3 = A3*a.getX() + B3*a.getY();
			    	double det2 = (A1*B3 - A3*B1);
			    	double cx2 = 0;
			    	double cy2 = 0;
			    	if(det2 != 0){
			    		//closest point to obj1 on line
				    	cx2 = (B3*C1 - B1*C3)/det2;
				    	cy2 = (A1*C3 - A3*C1)/det2;
				    	/*g2.setColor(Color.gray);
				    	g2.fillRect((int)cx2 - 1, (int)cy2 - 1, 3, 3);*/
				    	
				    	//old method
				    	/*double dis = Math.sqrt(Math.abs((cx-a.getX())*(cx-a.getX()) + (cy-a.getY())*(cy-a.getY())));
				    	double dis2 = Math.sqrt(Math.abs((cx-cx2)*(cx-cx2) + (cy-cy2)*(cy-cy2)));
				    	double ang = Math.acos(VectorMath.dotproduct(cx, cy, cx2, cy2, a.getX(), a.getY())/(dis2*dis));
				    	double falldist = a.getRadius()/Math.sin(ang);*/
				    	//new method is 3x faster
				    	//how much to move back on the movement vector of a
				    	double falldist = (Point.distance(cx, cy, a.getX(), a.getY()) * a.getRadius()) / Point.distance(cx2, cy2, a.getX(), a.getY());
				    	//actual point of collision for the circle
				    	cx = cx + (-falldist) * a.getnormvx();
				    	cy = cy + (-falldist) * a.getnormvy();
				    	double C4 = A3*cx + B3*cy;
				    	double cx4 = (B3*C1 - B1*C4) / det2;
				    	double cy4 = (A1*C4 - A3*C1) / det2;
				    	/*g2.setColor(Color.black);
				    	g2.fillRect((int)cx4 - 1, (int)cy4 - 1, 3, 3);*/
				    	//does the circle collide parallel to the line?
				    	if(Math.min(line.getX1(), line.getX2()) <= cx4 && cx4 <= Math.max(line.getX1(), line.getX2()) && 
				    			Math.min(line.getY1(), line.getY2()) <= cy4 && cy4 <= Math.max(line.getY1(), line.getY2())){
					    	//real closest point on line
				    		double cx3 = cx2;
				    		double cy3 = cy2;
					    	cx2 = (cx2 + (cx - cx4));
					    	cy2 = (cy2 + (cy - cy4));
					    	
					    	double distsqa = Math.pow((cx - a.getX()), 2) + Math.pow((cy - a.getY()), 2);
					    	if((distsqa <= closestdistsq[i] || closestdistsq[i] < 0 || a.isFrozen())){
						    	RectVector v = null;
						    	if(!a.isFrozen()){
									if(collisionindex[i].collided() && closestdistsq[i] == distsqa){
									}else{
										// wipe out whoever you collided with before if they are farther away
										for(int l = 0; l < collisionindex[i].size(); l++){
											if(collisionindex[i].collided()
													&& collisionindex[i].getColliders().get(l).getCollideobj() instanceof ForceCircle
													&& (closestdistsq[i] > distsqa)){
												results[collisionindex[i].getColliders().get(l).getCollidewith()] = new PointVector(
														forceCircle2.get(collisionindex[i].getColliders().get(l).getCollidewith()));
												closestdistsq[collisionindex[i].getColliders().get(l).getCollidewith()] = -1;
											}
										}
									}
									if(Point.distanceSq(cx2, cy2, a.getX(), a.getY()) < 1e-8){ //circle is on the line
								    	Point pr = VectorMath.closestpointonline((double)(line.getX1() + (cx - cx3)), (double)(line.getY1() + (cy - cy3)),
								    			(double)(line.getX2() + (cx - cx3)), (double)(line.getY2() + (cy - cy3)), a.getX2(), a.getY2());
							    		v = new RectVector((double)((pr.getX() + (pr.getX()-a.getX2())) - a.getX()), (double)((pr.getY() + (pr.getY() - a.getY2())) - a.getY()));
										v = (RectVector)v.getUnitVector();
										v = new RectVector(v.getvx() * a.getLength(), v.getvy() * a.getLength());
							    	}else{ //circle is far from line
										v = new RectVector((double)((a.getX() - 2 * (cx2 - cx)) - cx), (double)((a.getY() - 2 * (cy2 - cy)) - cy));
										v = (RectVector)v.getUnitVector();
										v = new RectVector(v.getvx() * a.getLength(), v.getvy() * a.getLength());
							    	}
									v = (RectVector)v.getUnitVector();
									v = new RectVector(v.getvx() * a.getLength(), v.getvy() * a.getLength());
									if(closestdistsq[i] == -1)
										results[i] = new PointVector((double)cx, (double)cy);
									else
										results[i].setPoint((double)cx, (double)cy);
									if(closestdistsq[i] == distsqa){
										collisionindex[i].addCollided(line, j, v);
									}else{
										collisionindex[i].setCollided(line, j, v);
									}
									closestdistsq[i] = distsqa;
								}
					    	}
				    	}else{ //circle collides with a point
				    		double sumradsq = (a.getRadius()) * (a.getRadius());
							// if both objects aren't moving.. just make sure that they aren't close enough to collide
							Point pc1 = VectorMath.closestpointonline(a.getX(), a.getY(), a.getX2(), a.getY2(), line.getX1(), line.getY1());
							double distsq1 = Point.distanceSq(pc1.getX(), pc1.getY(), line.getX1(), line.getY1());
							double distsq_test1 = Point.distanceSq(pc1.getX(), pc1.getY(), a.getX(), a.getY());
							Point pc2 = VectorMath.closestpointonline(a.getX(), a.getY(), a.getX2(), a.getY2(), line.getX2(), line.getY2());
							double distsq2 = Point.distanceSq(pc2.getX(), pc2.getY(), line.getX2(), line.getY2());
							double distsq_test2 = Point.distanceSq(pc2.getX(), pc2.getY(), a.getX(), a.getY());
							// check if the resultant circle collides or if the point is inside the circle or if the point
							// is on the line segment
							double cdist = 0;
							if(distsq_test1 < distsq_test2 && distsq1 <= distsq2){
								// find point of collision
								// distance from closest point on line to point of collision
								cdist = Math.sqrt(Math.abs((sumradsq) - distsq1));
								cx = (pc1.getX() - cdist * a.getnormvx());
								cy = (pc1.getY() - cdist * a.getnormvy());
								cx2 = line.getX1();
								cy2 = line.getY1();
							}else if(distsq_test1 > distsq_test2 && distsq1 >= distsq2){
								cdist = Math.sqrt(Math.abs((sumradsq) - distsq2));
								cx = (pc2.getX() - cdist * a.getnormvx());
								cy = (pc2.getY() - cdist * a.getnormvy());
								cx2 = line.getX2();
								cy2 = line.getY2();
							}else{
								if(distsq1 < distsq2){
									if(distsq_test1 < distsq_test2 || Point.distanceSq(cx4, cy4, line.getX1(), line.getY1()) <= sumradsq){
										cdist = Math.sqrt(Math.abs((sumradsq) - distsq1));
										cx = (pc1.getX() - cdist * a.getnormvx());
										cy = (pc1.getY() - cdist * a.getnormvy());
										cx2 = line.getX1();
										cy2 = line.getY1();
									}else{
										cdist = Math.sqrt(Math.abs((sumradsq) - distsq2));
										cx = (pc2.getX() - cdist * a.getnormvx());
										cy = (pc2.getY() - cdist * a.getnormvy());
										cx2 = line.getX2();
										cy2 = line.getY2();
									}
								}else if(distsq1 > distsq2){
									//special case
									if(distsq_test2 < distsq_test1 || Point.distanceSq(cx4, cy4, line.getX2(), line.getY2()) <= sumradsq){
										cdist = Math.sqrt(Math.abs((sumradsq) - distsq2));
										cx = (pc2.getX() - cdist * a.getnormvx());
										cy = (pc2.getY() - cdist * a.getnormvy());
										cx2 = line.getX2();
										cy2 = line.getY2();
									}else{
										cdist = Math.sqrt(Math.abs((sumradsq) - distsq1));
										cx = (pc1.getX() - cdist * a.getnormvx());
										cy = (pc1.getY() - cdist * a.getnormvy());
										cx2 = line.getX1();
										cy2 = line.getY1();
									}
								}else if(//checks if second nearest point on line is in the line segment or in inside the final circle
										!((Math.min(a.getX(), a.getX2()) <= pc2.getX() && pc2.getX() <= Math.max(a.getX(), a.getX2()) && 
								    			Math.min(a.getY(), a.getY2()) <= pc2.getY() && pc2.getY() <= Math.max(a.getY(), a.getY2()))
								    			 || Point.distanceSq(pc2.getX(), pc2.getY(), a.getX2(), a.getY2()) <= a.getRadiusSq())){
									cdist = Math.sqrt(Math.abs((sumradsq) - distsq1));
									cx = (pc1.getX() - cdist * a.getnormvx());
									cy = (pc1.getY() - cdist * a.getnormvy());
									cx2 = line.getX1();
									cy2 = line.getY1();
								}else if(!((Math.min(a.getX(), a.getX2()) <= pc1.getX() && pc1.getX() <= Math.max(a.getX(), a.getX2()) && 
								    			Math.min(a.getY(), a.getY2()) <= pc1.getY() && pc1.getY() <= Math.max(a.getY(), a.getY2())) ||
								    			Point.distanceSq(pc2.getX(), pc2.getY(), a.getX2(), a.getY2()) <= a.getRadiusSq())){
									cdist = Math.sqrt(Math.abs((sumradsq) - distsq2));
									cx = (pc2.getX() - cdist * a.getnormvx());
									cy = (pc2.getY() - cdist * a.getnormvy());
									cx2 = line.getX2();
									cy2 = line.getY2();
				    			}else{
									if(distsq_test1 < distsq_test2){
										cdist = Math.sqrt(Math.abs((sumradsq) - distsq1));
										cx = (pc1.getX() - cdist * a.getnormvx());
										cy = (pc1.getY() - cdist * a.getnormvy());
										cx2 = line.getX1();
										cy2 = line.getY1();
									}else{
										cdist = Math.sqrt(Math.abs((sumradsq) - distsq2));
										cx = (pc2.getX() - cdist * a.getnormvx());
										cy = (pc2.getY() - cdist * a.getnormvy());
										cx2 = line.getX2();
										cy2 = line.getY2();
									}
								}
							}
							double distsqa = Math.pow((cx - a.getX()), 2) + Math.pow((cy - a.getY()), 2);
							if((distsqa <= closestdistsq[i] || closestdistsq[i] < 0 || a.isFrozen())){
								RectVector v = null;
						    	if(!a.isFrozen()){
									if(collisionindex[i].collided() && closestdistsq[i] == distsqa){
										
									}else{
										// wipe out whoever you collided with before if they are farther away
										for(int l = 0; l < collisionindex[i].size(); l++){
											if(collisionindex[i].collided()
													&& collisionindex[i].getColliders().get(l).getCollideobj() instanceof ForceCircle
													&& (closestdistsq[i] > distsqa)){
												results[collisionindex[i].getColliders().get(l).getCollidewith()] = new PointVector(
														forceCircle2.get(collisionindex[i].getColliders().get(l).getCollidewith()));
												closestdistsq[collisionindex[i].getColliders().get(l).getCollidewith()] = -1;
											}
										}
									}
									v = new RectVector((double)(cx - (cx2 - cx) - cx), (double)(cy - (cy2 - cy) - cy));
									v = (RectVector)v.getUnitVector();
									v = new RectVector(v.getvx() * a.getLength(), v.getvy() * a.getLength());
									if(closestdistsq[i] == -1)
										results[i] = new PointVector((double)cx, (double)cy);
									else
										results[i].setPoint((double)cx, (double)cy);
									if(closestdistsq[i] == distsqa){
										collisionindex[i].addCollided(line, j, v);
									}else{
										collisionindex[i].setCollided(line, j, v);
									}
									closestdistsq[i] = distsqa;
								}
							}
				    	}
					}
		    	}
			}
		}
		// test collision with staticcircles
		StaticCircle c;
		for(int i = Math.min(forceCircle2.size(), timepassed.length) - 1; i >= 0; i--){
			//important to not change staticcircle between calls to this method
			//otherwise fullcheck should be true
			//an object that has not collided with any other object within a particular frame
			//should not have collision checking done again against the list of static circles
			if(!fullcheck && !modified[i])
				continue;
			a = forceCircle2.get(i);
			for(int j = staticcircles.size() - 1; j >= 0; j--){
				c = staticcircles.get(j);
				if(!c.isCollide(a) || !a.isCollide(c))
					continue;
				if(a.getX() == c.getX() && a.getY() == c.getY())
					continue;
				double cx1 = a.getX() + a.getvx();
				double cy1 = a.getY() + a.getvy();
				double cx2 = c.getX();
				double cy2 = c.getY();
				double vx1 = 0;
				double vy1 = 0;
				double vx2 = 0;
				double vy2 = 0;
				if(CircleMath.checkcirclecollide(a, c)){ // they intersect
					double dist = 0;
					// find midpoint and push out radius
					cx1 = a.getX();
					cy1 = a.getY();
					cx2 = c.getX();
					cy2 = c.getY();
					dist = (double)Math.sqrt((c.getX() - a.getX()) * (c.getX() - a.getX()) + (c.getY() - a.getY())
							* (c.getY() - a.getY()));
					double sumrad = a.getRadius() + c.getRadius();
					cx1 = cx2 = c.getX();
					cy1 = cy2 = c.getY();
					vx1 = (sumrad + .25F) * ((a.getX() - c.getX()) / dist);
					vy1 = (sumrad + .25F) * ((a.getY() - c.getY()) / dist);
					cx1 += vx1;
					cy1 += vy1;
					cx2 += vx2;
					cy2 += vy2;
					if((a.getvx() != 0 || a.getvy() != 0)){
						// check for intersection
						double d = VectorMath.dotproduct(a.getvx(), a.getvy(), c.getX() - cx1, c.getY() - cy1);
						if(d <= 0){
							// distance from starting point to the (a) point of collision
							if(!a.isFrozen() && collisionindex[i].collided()){
								for(int l = 0; l < collisionindex[i].size(); l++){
									if(collisionindex[i].getColliders().get(l).getCollideobj() instanceof ForceCircle
											&& CircleMath.checkcirclecollide(forceCircle2.get(i), forceCircle2
													.get(collisionindex[i].getColliders().get(l).getCollidewith()))){
										cx1 = (results[i].getX() + cx1) / 2;
										cy1 = (results[i].getY() + cy1) / 2;
									}
								}
							}
							double distsqa = Point.distanceSq(a.getX(), a.getY(), cx1, cy1);
							if((distsqa <= closestdistsq[i] || closestdistsq[i] < 0)){
								if(distsqa == closestdistsq[i]){
									cx1 = (results[i].getX() + cx1) / 2;
									cy1 = (results[i].getY() + cy1) / 2;
								}
								if(closestdistsq[i] == -1)
									results[i] = new PointVector(cx1, cy1);
								else
									results[i].setPoint(cx1, cy1);
								closestdistsq[i] = distsqa;
								collisionindex[i].setCollided(c, j, a);
							}
							continue;
						}
					}
				}else{ // c is frozen
					double sumradsq = (double)(a.getRadius() + c.getRadius()) * (a.getRadius() + c.getRadius());
					// if both objects aren't moving.. just make sure that they aren't close enough to collide
					if((a.getvx() == 0 && a.getvy() == 0)
							&& Point.distanceSq(a.getX(), a.getY(), c.getX(), c.getY()) >= sumradsq)
						continue;
					Point p = VectorMath.closestpointonline(a.getX(), a.getY(), a.getX2(), a.getY2(), c.getX(), c
							.getY());
					double distsq = (double)Point.distanceSq(p.getX(), p.getY(), c.getX(), c.getY());
					// check if the resultant circle collides or if the point is inside the circle or if the point is on
					// the line segment
					if(distsq < sumradsq
							&& (CircleMath.checkcirclecollide(a.getX2(), a.getY2(), a.getRadius(), c.getX(), c.getY(),
									c.getRadius()) || (p.getX() >= (Math.min(a.getX(), a.getX2()))
									&& p.getX() <= (Math.max(a.getX(), a.getX2()))
									&& p.getY() >= (Math.min(a.getY(), a.getY2())) && p.getY() <= (Math.max(a.getY(), a
									.getY2()))))){
						// find point of collision
						double cdist = Math.sqrt((sumradsq + .789) - distsq); // distance from closest point on line to
																				// point of collision
						cx1 = (double)(p.getX() - cdist * a.getnormvx());
						cy1 = (double)(p.getY() - cdist * a.getnormvy());
						if((!CircleMath.checkcirclecollide(cx1, cy1, a.getRadius(), c.getX(), c.getY(), c.getRadius()) && !(Point
								.distanceSq(a.getX2(), a.getY2(), cx1, cy1) <= a.getRadiusSq()))
								&& !(p.getX() >= (Math.min(a.getX(), a.getX2()))
										&& p.getX() <= (Math.max(a.getX(), a.getX2()))
										&& p.getY() >= (Math.min(a.getY(), a.getY2())) && p.getY() <= (Math.max(a
										.getY(), a.getY2()))))
							cx2 = c.getX();
						cy2 = c.getY();
					}else
						continue;
				}
				double distsqa = 0;
				if(a.getvx() != 0 || a.getvy() != 0){
					distsqa = Point.distanceSq(a.getX(), a.getY(), cx1, cy1);
				}
				if((distsqa <= closestdistsq[i] || closestdistsq[i] < 0 || forceCircle2.get(i).isFrozen())){
					if(!a.isFrozen() && distsqa == closestdistsq[i]){
						cx1 = (results[i].getX() + cx1) / 2;
						cy1 = (results[i].getY() + cy1) / 2;
					}
					if(!a.isFrozen()){
						if(collisionindex[i].collided() && closestdistsq[i] == distsqa){
						}else{
							// wipe out whoever you collided with before if they are farther away
							for(int l = 0; l < collisionindex[i].size(); l++){
								if(collisionindex[i].collided()
										&& collisionindex[i].getColliders().get(l).getCollideobj() instanceof ForceCircle
										&& (closestdistsq[i] > distsqa)){
									results[collisionindex[i].getColliders().get(l).getCollidewith()] = new PointVector(
											forceCircle2.get(collisionindex[i].getColliders().get(l).getCollidewith()));
									closestdistsq[collisionindex[i].getColliders().get(l).getCollidewith()] = -1;
								}
							}
						}
						if(closestdistsq[i] == -1)
							results[i] = new PointVector(cx1, cy1);
						else
							results[i].setPoint(cx1, cy1);
					}
					if(CircleMath.checkcirclecollide(a, c) || closestdistsq[i] == distsqa){
						collisionindex[i].addCollided(c, j, null);
					}else{
						collisionindex[i].setCollided(c, j, null);
					}
					if(CircleMath.checkcirclecollide(a, c))
						closestdistsq[i] = 0;
					else
						closestdistsq[i] = distsqa;
				}
			}
		}

	// START OF FOR LOOP for forcecircles
		for(int i = Math.min(forceCircle2.size(), timepassed.length) - 1; i >= 0; i--){
			a = forceCircle2.get(i);
			double cx1 = a.getX() + a.getvx();
			double cy1 = a.getY() + a.getvy();
			for(int j = i + 1; j < Math.min(forceCircle2.size(), timepassed.length); j++){
				b = forceCircle2.get(j);
				if(!a.isCollide(b) || !b.isCollide(a)) //don't collide with each other
					continue;
				if(a.getX() == b.getX() && a.getY() == b.getY()) //same location
					continue;
				//do not check collision between two objects that have not collided
				//and have already had collision checking done between each other
				//useful when running this method more than once (fullcheck enables this)
				if(!fullcheck && !modified[i] && !modified[j])
					continue;
				/*if(modified[i]){
					Graphics2D g = buf.createGraphics();
					g.setColor(Color.gray);
					g.drawOval((int)(a.getX()-a.getRadius()), (int)(a.getY()-a.getRadius()), (int)a.getRadius()*2, (int)a.getRadius()*2);
				}
				if(modified[j]){
					Graphics2D g = buf.createGraphics();
					g.setColor(Color.gray);
					g.drawOval((int)(b.getX()-b.getRadius()), (int)(b.getY()-b.getRadius()), (int)b.getRadius()*2, (int)b.getRadius()*2);
				}*/
				// default c1 and c2 values assumes you have not collided
				double cx2 = b.getX() + b.getvx();
				double cy2 = b.getY() + b.getvy();
				double vx1 = 0;
				double vy1 = 0;
				double vx2 = 0;
				double vy2 = 0;
				double collidetime = -1;
				//one of them is frozen
				if(a.isFrozen() || b.isFrozen()
						|| ((a.getvx() == 0 && a.getvy() == 0) || (b.getvx() == 0 && b.getvy() == 0))){
					if(CircleMath.checkcirclecollide(a, b)){ // they intersect
						double dist = 0;
						// find midpoint and push out radius
						cx1 = a.getX();
						cy1 = a.getY();
						cx2 = b.getX();
						cy2 = b.getY();
						dist = Point.distance(a.getX(), a.getY(), b.getX(), b.getY());
						double sumrad = a.getRadius() + b.getRadius();
						if(a.getLength() == 0 && b.getLength() == 0 && !a.isFrozen() && !b.isFrozen()){
							boolean abound = a.getMinX() == lowerxbound || a.getMaxX() == upperxbound
									|| a.getMinY() == lowerybound || a.getMaxY() == upperybound;
							boolean bbound = b.getMinX() == lowerxbound || b.getMaxX() == upperxbound
									|| b.getMinY() == lowerybound || b.getMaxY() == upperybound;
							if(abound && !bbound){
								cx2 = a.getX();
								cy2 = a.getY();
								vx2 = (double)((sumrad + .25F) * ((b.getX() - a.getX()) / dist));
								vy2 = (double)((sumrad + .25F) * ((b.getY() - a.getY()) / dist));
							}else if(!abound && bbound){
								cx1 = b.getX();
								cy1 = b.getY();
								vx1 = (double)((sumrad + .25F) * ((a.getX() - b.getX()) / dist));
								vy1 = (double)((sumrad + .25F) * ((a.getY() - b.getY()) / dist));
							}else{
								cx1 = (double)(a.getX() + a.getRadius() * ((b.getX() - a.getX()) / dist));
								cy1 = (double)(a.getY() + a.getRadius() * ((b.getY() - a.getY()) / dist));
								cx2 = (double)(b.getX() + b.getRadius() * ((a.getX() - b.getX()) / dist));
								cy2 = (double)(b.getY() + b.getRadius() * ((a.getY() - b.getY()) / dist));
								cx2 = cx1 = (cx1 + cx2) / 2;
								cy2 = cy1 = (cy1 + cy2) / 2;
								vx1 = (double)((a.getRadius() + .25) * ((a.getX() - b.getX()) / dist));
								vy1 = (double)((a.getRadius() + .25) * ((a.getY() - b.getY()) / dist));
								vx2 = (double)((b.getRadius() + .25) * ((b.getX() - a.getX()) / dist));
								vy2 = (double)((b.getRadius() + .25) * ((b.getY() - a.getY()) / dist));
							}
						}else{
							boolean abound = !b.isFrozen()
									&& (a.getMinX() == lowerxbound || a.getMaxX() == upperxbound
											|| a.getMinY() == lowerybound || a.getMaxY() == upperybound);
							boolean bbound = !a.isFrozen()
									&& (b.getMinX() == lowerxbound || b.getMaxX() == upperxbound
											|| b.getMinY() == lowerybound || b.getMaxY() == upperybound);
							if(abound && !bbound){
								cx2 = a.getX();
								cy2 = a.getY();
								vx2 = (double)((sumrad + .25F) * ((b.getX() - a.getX()) / dist));
								vy2 = (double)((sumrad + .25F) * ((b.getY() - a.getY()) / dist));
							}else if(!abound && bbound){
								cx1 = b.getX();
								cy1 = b.getY();
								vx1 = (double)((sumrad + .25F) * ((a.getX() - b.getX()) / dist));
								vy1 = (double)((sumrad + .25F) * ((a.getY() - b.getY()) / dist));
							}else{
								if((a.isFrozen() || ((a.getvx() == 0 && a.getvy() == 0)) && !b.isFrozen())){
									cx2 = cx1 = a.getX();
									cy2 = cy1 = a.getY();
									vx2 = (double)((sumrad + .25F) * ((b.getX() - a.getX()) / dist));
									vy2 = (double)((sumrad + .25F) * ((b.getY() - a.getY()) / dist));
								}
								if((b.isFrozen() || ((b.getvx() == 0 && b.getvy() == 0)) && !a.isFrozen())){
									cx1 = cx2 = b.getX();
									cy1 = cy2 = b.getY();
									vx1 = (double)((sumrad + .25F) * ((a.getX() - b.getX()) / dist));
									vy1 = (double)((sumrad + .25F) * ((a.getY() - b.getY()) / dist));
								}
							}
						}
						cx1 += vx1;
						cy1 += vy1;
						cx2 += vx2;
						cy2 += vy2;
					}else if((a.isFrozen() || (a.getvx() == 0 && a.getvy() == 0))){ // a is frozen
						double sumradsq = (double)(b.getRadius() + a.getRadius()) * (b.getRadius() + a.getRadius());
						// if both objects aren't moving.. just make sure that they aren't close enough to collide
						Point p = VectorMath.closestpointonline(b.getX(), b.getY(), b.getX2(), b.getY2(), a.getX(), a
								.getY());
						double distsq = (double)Point.distanceSq(p.getX(), p.getY(), a.getX(), a.getY());
						// check if the resultant circle collides or if the point is inside the circle or if the point
						// is on the line segment
						if(distsq < sumradsq
								&& (CircleMath.checkcirclecollide(b.getX2(), b.getY2(), b.getRadius(), a.getX(), a
										.getY(), a.getRadius()) || (p.getX() >= (Math.min(b.getX(), b.getX2()))
										&& p.getX() <= (Math.max(b.getX(), b.getX2()))
										&& p.getY() >= (Math.min(b.getY(), b.getY2())) && p.getY() <= (Math.max(b
										.getY(), b.getY2()))))){
							// find point of collision
							// distance from closest point on line to point of collision
							double cdist = Math.sqrt((sumradsq + .789) - distsq);
							cx2 = (double)(p.getX() - cdist * b.getnormvx());
							cy2 = (double)(p.getY() - cdist * b.getnormvy());
							/*
							 * if((!CircleMath.checkcirclecollide(cx1, cy1, b.getRadius(), a.getX(), a.getY(),
							 * a.getRadius()) && !(Point.distanceSq(b.getnx(), b.getny(), cx1, cy1) <=
							 * b.getRadiussq())) && !(p.getX() >= (Math.min(b.getX(), b.getnx())) && p.getX() <=
							 * (Math.max(b.getX(), b.getnx())) && p.getY() >= (Math.min(b.getY(), b.getny())) &&
							 * p.getY() <= (Math.max(b.getY(), b.getny())))) continue;
							 */
							cx1 = a.getX();
							cy1 = a.getY();
						}else
							continue;
					}else if((b.isFrozen() || (b.getvx() == 0 && b.getvy() == 0))){ // b is frozen
						double sumradsq = (double)(a.getRadius() + b.getRadius()) * (a.getRadius() + b.getRadius());
						// if both objects aren't moving.. just make sure that they aren't close enough to collide
						Point p = VectorMath.closestpointonline(a.getX(), a.getY(), a.getX2(), a.getY2(), b.getX(), b
								.getY());
						/*double A1 = (a.getny() - a.getY());
						double B1 = (a.getX() - a.getnx());
						double C1 = (a.getny() - a.getY())*a.getX() + (a.getX() - a.getnx())*a.getY();
						double C2 = -B1*b.getX() + A1*b.getY();
						double det = (A1*A1 - (-B1*B1));
						double cx = 0;
				    	double cy = 0;
				    	if(det != 0){
					    	cx = (A1*C1 - B1*C2)/det;
					    	cy = (A1*C2 - -B1*C1)/det;
				    	}*/
						double distsq = (double)Point.distanceSq(p.getX(), p.getY(), b.getX(), b.getY());
						// check if the resultant circle collides or if the point is inside the circle or if the point
						// is on the line segment
						/*if(!fullcheck){
							Graphics2D g = buf.createGraphics();
							g.setColor(Color.gray);
							g.fillRect((int)p.getX()-1, (int)p.getY()-1, 3, 3);
							g.setColor(Color.cyan);
							g.fillRect((int)a.getX()-1, (int)a.getY()-1, 3, 3);
							g.fillRect((int)a.getnx()-1, (int)a.getny()-1, 3, 3);
							System.out.println(p.getY() + " : " + a.getY() + " : " + a.getny() + " : " +
										  (p.getX() + 0.00005 >= Math.min(a.getX(), a.getnx())
										&& p.getX() - 0.00005 <= Math.max(a.getX(), a.getnx())
										&& p.getY() + 0.00005 >= Math.min(a.getY(), a.getny())
										&& p.getY() - 0.00005 <= Math.max(a.getY(), a.getny())
										   )
										   );
						}*/
						if(distsq < sumradsq
								&& (CircleMath.checkcirclecollide(a.getX() + a.getvx() * (1 - timepassed[i]), a.getY()
										+ a.getvy() * (1 - timepassed[i]), a.getRadius(), b.getX(), b.getY(), b
										.getRadius()) || (p.getX() + 0.00005 >= Math.min(a.getX(), a.getX2())
										&& p.getX() - 0.00005 <= Math.max(a.getX(), a.getX2())
										&& p.getY() + 0.00005 >= Math.min(a.getY(), a.getY2()) && p.getY() - 0.00005 <= Math
										.max(a.getY(), a.getY2())))){
							// find point of collision
							// distance from closest point on line to point of collision
							double cdist = Math.sqrt((sumradsq + .789) - distsq);
							cx1 = (double)(p.getX() - cdist * a.getnormvx());
							cy1 = (double)(p.getY() - cdist * a.getnormvy());
							/*
							 * if((!CircleMath.checkcirclecollide(cx1, cy1, a.getRadius(), b.getX(), b.getY(),
							 * b.getRadius()) && !(Point.distanceSq(a.getnx(), a.getny(), cx1, cy1) <=
							 * a.getRadiussq())) && !(p.getX() >= (Math.min(a.getX(), a.getnx())) && p.getX() <=
							 * (Math.max(a.getX(), a.getnx())) && p.getY() >= (Math.min(a.getY(), a.getny())) &&
							 * p.getY() <= (Math.max(a.getY(), a.getny())))) continue;
							 */
							cx2 = b.getX();
							cy2 = b.getY();
						}else
							continue;
					}else
						continue;
				}else if(CircleMath.checkcirclecollide(a, b)){
					// (specialized collision checking code since you don't want to touch actual a/b values
					// find midpoint and push out radius
					double dist = (double)Point.distance(a.getX(), a.getY(), b.getX(), b.getY());
					Rect aBoundBox = a.getBounds();
					Rect bBoundBox = b.getBounds();
					boolean abound = aBoundBox.getMinX() == lowerxbound || aBoundBox.getMaxX() == upperxbound
							|| aBoundBox.getMinY() == lowerybound || aBoundBox.getMaxY() == upperybound;
					boolean bbound = bBoundBox.getMinX() == lowerxbound || bBoundBox.getMaxX() == upperxbound
							|| bBoundBox.getMinY() == lowerybound || bBoundBox.getMaxY() == upperybound;
					double sumrad = a.getRadius() + b.getRadius();
					if(abound && !bbound){
						cx1 = cx2 = a.getX();
						cy1 = cy2 = a.getY();
						vx1 = 0;
						vy1 = 0;
						vx2 = (sumrad + .25F) * ((b.getX() - a.getX()) / dist);
						vy2 = (sumrad + .25F) * ((b.getY() - a.getY()) / dist);
					}else if(!abound && bbound){
						cx2 = cx1 = b.getX();
						cy2 = cy1 = b.getY();
						vx2 = 0;
						vy2 = 0;
						vx1 = (sumrad + .25F) * ((a.getX() - b.getX()) / dist);
						vy1 = (sumrad + .25F) * ((a.getY() - b.getY()) / dist);
					}else{
						cx1 = a.getX() + a.getRadius() * ((b.getX() - a.getX()) / dist);
						cy1 = a.getY() + a.getRadius() * ((b.getY() - a.getY()) / dist);
						cx2 = b.getX() + b.getRadius() * ((a.getX() - b.getX()) / dist);
						cy2 = b.getY() + b.getRadius() * ((a.getY() - b.getY()) / dist);
						cx2 = cx1 = (cx1 + cx2) / 2;
						cy2 = cy1 = (cy1 + cy2) / 2;
						vx1 = (double)((a.getRadius() + .25) * ((a.getX() - b.getX()) / dist));
						vy1 = (double)((a.getRadius() + .25) * ((a.getY() - b.getY()) / dist));
						vx2 = (double)((b.getRadius() + .25) * ((b.getX() - a.getX()) / dist));
						vy2 = (double)((b.getRadius() + .25) * ((b.getY() - a.getY()) / dist));
					}
					cx1 += vx1;
					cy1 += vy1;
					cx2 += vx2;
					cy2 += vy2;
					// are they heading toward each other?
					double d = VectorMath.dotproduct(a.getvx() - b.getvx(), a.getvy() - b.getvy(),
							b.getX() - a.getX(), b.getY() - a.getY());
					// System.out.println(d);
					if(d > 0
							|| CircleMath.checkcirclecollide(a.getX2(), a.getY2(), a.getRadius(), b.getX2(), b.getY2(),
									b.getRadius()) || (abound && !bbound && collisionindex[i].containsBoundaries())
							|| (!abound && bbound && collisionindex[j].containsBoundaries())){
						collidetime = 0;
					}else
						continue;
				}else{ // 2 moving non-intersecting circles! wee!
					double sumradsq = (a.getRadius() + b.getRadius()) * (a.getRadius() + b.getRadius());
					// uses b's frame of reference to make b not moving and a moving
					vx1 = a.getvx() - b.getvx();
					vy1 = a.getvy() - b.getvy();
					// find the closest point to b on the v line
					double A1 = ((a.getY() + vy1) - a.getY());
					double B1 = (a.getX() - (a.getX() + vx1));
					double C1 = A1 * a.getX() + B1 * a.getY();
					double C2 = -B1 * b.getX() + A1 * b.getY();
					double det = (A1 * A1 - (-B1 * B1));
					double cx = 0;
					double cy = 0;
					if(det != 0){
						cx = (A1 * C1 - B1 * C2) / det;
						cy = (A1 * C2 - -B1 * C1) / det;
					}
					double distsq = Point.distanceSq(cx, cy, b.getX(), b.getY());
					// check that the point is on the line segment
					if(distsq < sumradsq
							&& (CircleMath.checkcirclecollide(a.getX2(), a.getY2(), a.getRadius(), b.getX2(), b
									.getY2(), b.getRadius())
									|| CircleMath.checkcirclecollide(a.getX() + vx1, a.getY() + vy1, a.getRadius(), b
											.getX(), b.getY(), b.getRadius()) || ((double)cx >= (Math.min(a.getX(), a.getX() + vx1))
									&& (double)cx <= (Math.max(a.getX(), a.getX() + vx1))
									&& (double)cy >= (Math.min(a.getY(), a.getY() + vy1)) && (double)cy <= (Math.max(a.getY(), a.getY() + vy1))))){
						// find point of collision
						double dist = VectorMath.length(vx1, vy1); // length of v
						double dot = VectorMath.dotproduct(vx1 / dist, vy1 / dist, b.getX() - a.getX(), b.getY()
								- a.getY());
						double falldistsq = (sumradsq + .789F) - distsq;
						if(falldistsq - .789F > dot * dot) // just make sure you don't end up moving backwards
							continue;
						// distance to collision divided by length of v
						// When the two objects collide (between 0 and 1)
						collidetime = (dot - Math.sqrt(falldistsq)) / dist;
						if(collidetime < -.789 / dist || collidetime > 1)
							continue;
						cx1 = (double)(a.getX() + collidetime * a.getvx());
						cy1 = (double)(a.getY() + collidetime * a.getvy());
						cx2 = (double)(b.getX() + collidetime * b.getvx());
						cy2 = (double)(b.getY() + collidetime * b.getvy());
					}else
						continue;
				}
				/*
				 * Graphics g2 = buf.getGraphics(); g2.setColor(new Color(127, 127, 127, 176));
				 * g2.drawOval((int)(cx1-a.getRadius()), (int)(cy1-a.getRadius()),
				 * (int)(2a.getRadius()),(int)(2a.getRadius())); g2.drawOval((int)(cx2-b.getRadius()),
				 * (int)(cy2-b.getRadius()), (int)(2b.getRadius()),(int)(2b.getRadius()));
				 */

				// distance from starting point to the point of collision
				double distsqa = 0;
				double distsqb = 0;
				if(!CircleMath.checkcirclecollide(a, b)){ // if they intersect, it should be zero
					if(a.getvx() != 0 || a.getvy() != 0){
						distsqa = Point.distanceSq(a.getX(), a.getY(), cx1, cy1);
					}
					if(b.getvy() != 0 || b.getvy() != 0){
						distsqb = Point.distanceSq(b.getX(), b.getY(), cx2, cy2);
					}
				}else{
					if(Math.abs(timepassed[i] - timepassed[j]) > .1)
						continue;
				}
				if(timepassed[i] < 1 && timepassed[i] >= 0){ //if timepassed indicates the "real" vector is shorter
					//check to make sure the point of collision is within that "real" vector
					if(((1 - timepassed[i]) * a.getLength()) * ((1 - timepassed[i]) * a.getLength()) < distsqa)
						continue;
				}
				if(timepassed[j] < 1 && timepassed[j] >= 0){
					if(((1 - timepassed[j]) * b.getLength()) * ((1 - timepassed[j]) * b.getLength()) < distsqb)
						continue;
				}
				if((distsqa <= closestdistsq[i] || closestdistsq[i] < 0 || forceCircle2.get(i).isFrozen() || (forceCircle2.get(i)
						.getvx() == 0 && forceCircle2.get(i).getvy() == 0))
						&& (distsqb <= closestdistsq[j] || closestdistsq[j] < 0 || forceCircle2.get(j).isFrozen() || (forceCircle2
								.get(j).getvx() == 0 && forceCircle2.get(j).getvy() == 0))){
					//TODO:change in circletotal
					//store whether or not they are responsive to each other
					//just in case methods take a while to execute
					boolean arespondb = a.isResponsive(b);
					boolean bresponda = b.isResponsive(a);
					if(collidetime >= 0){ //adjust collision times
						if(!a.isFrozen() && arespondb && (a.getvx() != 0 || a.getvy() != 0)){
							collisiontimes[i] = collidetime;
						}else
							collisiontimes[i] = 0;
						if(!b.isFrozen() && bresponda && (b.getvx() != 0 || b.getvy() != 0)){
							collisiontimes[j] = collidetime;
						}else
							collisiontimes[j] = 0;
					}
					
					//average locations if multiple intersections
					if(!a.isFrozen() && collisionindex[i].collided() && arespondb){
						for(int l = 0; l < collisionindex[i].size(); l++){
							if(collisionindex[i].getColliders().get(l).getCollideobj() instanceof Circle
									&& (distsqa == closestdistsq[i] || (closestdistsq[i] == 0 && CircleMath
											.checkcirclecollide(a, (Circle)collisionindex[i].getColliders().get(l)
													.getCollideobj())))){
								cx1 = (results[i].getX() + cx1) / 2;
								cy1 = (results[i].getY() + cy1) / 2;
							}
						}
					}
					if(!b.isFrozen() && collisionindex[j].collided() && bresponda){
						for(int l = 0; l < collisionindex[j].size(); l++){
							if(collisionindex[j].getColliders().get(l).getCollideobj() instanceof Circle
									&& (distsqb == closestdistsq[j] || (closestdistsq[j] == 0 && CircleMath
											.checkcirclecollide((Circle)collisionindex[j].getColliders().get(l)
													.getCollideobj(), b)))){
								cx2 = (results[j].getX() + cx2) / 2;
								cy2 = (results[j].getY() + cy2) / 2;
							}
						}
					}
					if(!a.isFrozen() && arespondb){
						// you collided with another forcecircle at the same exact time
						if(collisionindex[i].collided() && closestdistsq[i] == distsqa){
						}else{
							// wipe out whoever you collided with before if they are farther away
							if(collisionindex[i].collided() && (closestdistsq[i] > distsqa || distsqa == 0)){
								for(int l = 0; l < collisionindex[i].size(); l++){
									if(collisionindex[i].getColliders().get(l).getCollidewith() < Math.min(forceCircle2.size(), timepassed.length)
											&& collisionindex[i].getColliders().get(l).getCollideobj() instanceof ForceCircle){
										results[collisionindex[i].getColliders().get(l).getCollidewith()] = new PointVector(
												forceCircle2.get(collisionindex[i].getColliders().get(l).getCollidewith()));
										closestdistsq[collisionindex[i].getColliders().get(l).getCollidewith()] = -1;
										collisionindex[collisionindex[i].getColliders().get(l).getCollidewith()]
												.reset();
									}
								}
							}
						}
					}
					if(!b.isFrozen() && bresponda){
						if(collisionindex[j].collided() && closestdistsq[j] == distsqb){
						}else{
							// wipe out whoever you collided with before if they are farther away
							if(collisionindex[j].collided() && (closestdistsq[j] > distsqb || distsqb == 0)){
								for(int l = 0; l < collisionindex[j].size(); l++){
									if(collisionindex[j].getColliders().get(l).getCollidewith() < Math.min(forceCircle2.size(), timepassed.length)
											&& collisionindex[j].getColliders().get(l).getCollideobj() instanceof ForceCircle){
										results[collisionindex[j].getColliders().get(l).getCollidewith()] = new PointVector(
												forceCircle2.get(collisionindex[j].getColliders().get(l).getCollidewith()));
										closestdistsq[collisionindex[j].getColliders().get(l).getCollidewith()] = -1;
										collisionindex[collisionindex[j].getColliders().get(l).getCollidewith()]
												.reset();
									}
								}
							}
						}
					}
					//change position if not frozen + responsive
					if(!a.isFrozen() && arespondb){
						if(closestdistsq[i] == -1)
							results[i] = new PointVector(cx1, cy1);
						else
							results[i].setPoint(cx1, cy1);
					}
					if(!b.isFrozen() && bresponda){
						if(closestdistsq[j] == -1)
							results[j] = new PointVector(cx2, cy2);
						else
							results[j].setPoint(cx2, cy2);
					}
					//if intersect, change dist + add new collided
					//TODO: is this right? throw into circletotal
					if(CircleMath.checkcirclecollide(a, b)){
//						if(arespondb){
							closestdistsq[i] = 0;
							collisionindex[i].addCollided(b, j, b);
//						}
//						if(bresponda){
							closestdistsq[j] = 0;
							collisionindex[j].addCollided(a, i, a);
//						}
					}else{ //if they there is no intersection, collided + real dist
						//TODO: respond?
//						if(arespondb){
							if(closestdistsq[i] == 0 || closestdistsq[i] == distsqa)
								collisionindex[i].addCollided(b, j, b);
							else
								collisionindex[i].setCollided(b, j, b);
							closestdistsq[i] = distsqa;
//						}
//						if(bresponda){
							if(closestdistsq[j] == 0 || closestdistsq[j] == distsqb)
								collisionindex[j].addCollided(a, i, a);
							else
								collisionindex[j].setCollided(a, i, a);
							closestdistsq[j] = distsqb;
//						}
					}
				}
			}// end of testing for forcecircles
		} // end of all collision testing

		int[] numstaticcircles = new int[forceCircle2.size()];
		int[] numfrozencircles = new int[forceCircle2.size()];
	//reduce energy for multiple circle collision checks
		for(int i = Math.min(forceCircle2.size(), timepassed.length) - 1; i >= 0; i--){
			a = forceCircle2.get(i);
			for(int k = 0; k < collisionindex[i].size(); k++){
				if(collisionindex[i].getColliders().get(k).getCollideobj() instanceof ForceCircle){
					b = (ForceCircle)collisionindex[i].getColliders().get(k).getCollideobj();
					if(b.isFrozen()) numfrozencircles[i]++;
					Collider tmp = collisionindex[collisionindex[i].getColliders().get(k).getCollidewith()].contains(a);
					if(tmp != null){
						Vector v = tmp.getCollideforce();
						if(VectorMath.dotproduct(a.getvx(), a.getvy(),
								results[collisionindex[i].getColliders().get(k).getCollidewith()].getX() - results[i].getX(), 
								results[collisionindex[i].getColliders().get(k).getCollidewith()].getY() - results[i].getY()) > 0 
								){
							collisionindex[collisionindex[i].getColliders().get(k).getCollidewith()].contains(a).setCollideforce(new RectVector(v.getvx() / collisionindex[i].getNumforcecircles(), v.getvy()
									/ collisionindex[i].getNumforcecircles()));
						}
					}
				}else if(collisionindex[i].getColliders().get(k).getCollideobj() instanceof StaticCircle){
					c = (StaticCircle)collisionindex[i].getColliders().get(k).getCollideobj();
					if(!CircleMath.checkcirclecollide(a, c) && 
							VectorMath.dotproduct(a.getvx(), a.getvy(), c.getX() - results[i].getX(), c.getY() - results[i].getY()) < 0){
						numstaticcircles[i] += 1;
					}
				}
			}
		}

		/*
		 * What we're going to do is take all the objects that have collided and what they have collided with and do the
		 * collision calculations. This is so that redundant calculations (on objects farther away) are not done and so
		 * multiple simultaneous collisions are handled correctly.
		 */
		// all the objects that have collided with another object (indicates to the outside that
		// these objects should NOT be moved)
		ArrayList<Integer> collidedwith = new ArrayList<Integer>((int)Math.ceil(forceCircle2.size() / 10));

		// "buckets" this is so that a - b distance isn't calculated at a and at b
		/*
		 * double[] lengths = new double[circles.size()]; for(int i = 0; i<circles.size(); i++) lengths[i] = -1;
		 */
		for(int i = 0; i < results.length; i++){ // calculate resultant
			if(collisionindex[i].collided()){
				a = forceCircle2.get(i);
				if(a.isFrozen()){
					results[i].setRect(0, 0);
					continue;
				}
				double vx1 = 0, vy1 = 0;/* , vx2 = 0, vy2 = 0; */
				// check for intersect before you change vector direction
				double totalmass = a.getMass();
				boolean intersect = false;
				boolean addedself = false;
				boolean hitmovinginto = false;
				double cx1 = results[i].getX();
				double cy1 = results[i].getY();
				double avgx = 0; // average locations of the circles you collided into
				double avgy = 0;
				double avgrad = 0;
				double numforcecircles = collisionindex[i].getNumforcecircles();// number of forcecircles collided into
				double sumvx = 0; // sum of vectors of the circles collided into
				double sumvy = 0;
				double avgbounce = 0;
				//statics
				double avgstaticx = 0;
				double avgstaticy = 0;
				double totalstaticmass = 0;

				// make sure that modifying i doesn't affect any objects down the line
				for(int k = 0; k < collisionindex[i].size(); k++){
					Object o = collisionindex[i].getColliders().get(k).getCollideobj();
					if(!a.isResponsive(o)) //check to make sure a reacts to colliding into o
						continue;
					avgbounce += forceCircle2.get(i).getRestitution(collisionindex[i].getColliders().get(k).getCollideobj());
					if(o instanceof Boundaries){
						Boundaries bound = (Boundaries)o;
						int wall = bound.getSide();
						if(boundResponse == BOUNDS_RESPOND_NONE){
							vx1 += a.getvx();
							vy1 += a.getvy();
							results[i].setPoint(a.getX2(), a.getY2());
						}else if(boundResponse == BOUNDS_RESPOND_BOUNCE){ // normal bouncing
							// left
							if(wall == Boundaries.left){
								wall -= Boundaries.left;
								double restitution = a.getRestitution(new Boundaries(Boundaries.left, boundDetection));
								vx1 += -(double)(a.getvx() * restitution);
								vy1 += (double)(a.getvy() * restitution);
							}else // bottom
							if(wall == Boundaries.bottom){
								wall -= Boundaries.bottom;
								double restitution = a.getRestitution(new Boundaries(Boundaries.bottom, boundDetection));
								vx1 += (double)(a.getvx() * restitution);
								vy1 += -(double)(a.getvy() * restitution);
							}else
							// right
							if(wall == Boundaries.right){
								wall -= Boundaries.right;
								double restitution = a.getRestitution(new Boundaries(Boundaries.right, boundDetection));
								vx1 += -(double)(a.getvx() * restitution);
								vy1 += (double)(a.getvy() * restitution);
							}else
							// top
							if(wall == Boundaries.top){
								wall -= Boundaries.top;
								double restitution = a.getRestitution(new Boundaries(Boundaries.top, boundDetection));
								vx1 += (double)(a.getvx() * restitution);
								vy1 += -(double)(a.getvy() * restitution);
							}else{
								double restitution = 1;
								// left
								if(wall >= Boundaries.left){
									wall -= Boundaries.left;
									restitution = a.getRestitution(new Boundaries(Boundaries.left, boundDetection));
								} // bottom
								if(wall == Boundaries.bottom){
									wall -= Boundaries.bottom;
									 restitution = a.getRestitution(new Boundaries(Boundaries.bottom, boundDetection));
								}
								// right
								if(wall >= Boundaries.right){
									wall -= Boundaries.right;
									restitution = a.getRestitution(new Boundaries(Boundaries.right, boundDetection));
								}
								// top
								if(wall >= Boundaries.top){
									wall -= Boundaries.top;
									restitution = a.getRestitution(new Boundaries(Boundaries.top, boundDetection));
								}
								vx1 -= a.getvx() * restitution;
								vy1 -= a.getvy() * restitution;
							}
						}else{
							Vector v = boundsresponse(boundResponse, a, bound);
							if(v != null){
								vx1 += v.getvx();
								vy1 += v.getvy();
							}
						}
					}else if(o instanceof ForceCircle){
						b = (ForceCircle)o;
						if(CircleMath.checkcirclecollide(a, b))
							intersect = true;
						if(b.isFrozen()){
							hitmovinginto = true;
							if(numstaticcircles[i] + numfrozencircles[i] > 1){
								avgstaticx += b.getX();
								avgstaticy += b.getY();
								totalmass += b.getMass();
								continue;
							}
							double len = Point.distance(cx1, cy1, b.getX(), b.getY());
							double nx = (b.getX() - cx1) / len;
							double ny = (b.getY() - cy1) / len;
							double a1 = 0;
							if(a.isFrozen()){
								a1 = 0;
							}else
								a1 = (double)(a.getvx() * nx + a.getvy() * ny);
							// totalmass += c.getMass();
							/* totala = totala + a1 - a2; */
							double p = (double)((2 * (a1)) / (a.getMass() + b.getMass()));
							// first forcecircle
							vx1 += (double)(a.getvx() - (p * a.getMass() * nx) + (p * b.getMass() * -nx));
							vy1 += (double)(a.getvy() - (p * a.getMass() * ny) + (p * b.getMass() * -ny));
							continue;
						}
						int j = collisionindex[i].getColliders().get(k).getCollidewith();
						avgx += results[j].getX();
						avgy += results[j].getY();
						if(numforcecircles > 1 && (b.getvx() != 0 || b.getvy() != 0) && VectorMath.crossproduct(a, b) < 0 &&
								VectorMath.dotproduct(a.getvx() - b.getvx(), a.getvy() - b.getvy(), a.getX() - b.getX(),
										a.getY() - b.getY()) > 0){
							hitmovinginto = true;
							// it's not results[i] because we're not looking for (x, y) we're looking for <x, y>
							double A1 = (a.getX() - (a.getX2()));
							double B1 = -((a.getY2()) - a.getY());
							double C1 = A1 * a.getX() + B1 * a.getY();
							double C2 = -B1 * b.getX() + A1 * b.getY();
							double det = (A1 * A1 - (-B1 * B1));
							double cx = 0;
							double cy = 0;
							cx = (A1 * C1 - B1 * C2) / det;
							cy = (A1 * C2 - -B1 * C1) / det;
							cx = cx + (cx - b.getX());
							cy = cy + (cy - b.getY());

							C2 = -B1 * (b.getX() + collisionindex[i].getColliders().get(k).getCollideforce().getvx()) + 
								A1 * (b.getY() + collisionindex[i].getColliders().get(k).getCollideforce().getvy());
							double cx2 = 0;
							double cy2 = 0;
							cx2 = (A1 * C1 - B1 * C2) / det;
							cy2 = (A1 * C2 - -B1 * C1) / det;
							cx2 = cx2 + (cx2 -(b.getX() + collisionindex[i].getColliders().get(k).getCollideforce().getvx()));
							cy2 = cy2 + (cy2 - (b.getY() + collisionindex[i].getColliders().get(k).getCollideforce().getvy()));

							sumvx += cx2 - cx;
							sumvy += cy2 - cy;
							/*
							 * sumvx += b.getvx(); sumvy += b.getvy();
							 */
						}else{
							sumvx += collisionindex[i].getColliders().get(k).getCollideforce().getvx();
							sumvy += collisionindex[i].getColliders().get(k).getCollideforce().getvy();
							/*System.out.println(collisionindex[i].getColliders().get(k).getCollideforce().getvx() == b.getvx() &&
									collisionindex[i].getColliders().get(k).getCollideforce().getvy() == b.getvy());*/
							/*sumvx += b.getvx();
							sumvy += b.getvy();*/
						}
						avgrad += b.getRadius();
						totalmass += b.getMass();
					}else if(o instanceof StaticCircle){
						// figure out if a static/force interesection warrants a collision mark
						c = (StaticCircle)o;
						if(!addedself && CircleMath.checkcirclecollide(a, c)
								&& VectorMath.dotproduct(a.getvx(), a.getvy(), c.getX() - a.getX(), c.getY()
										- a.getY()) < 0){ //moving away from obj
							vx1 += a.getvx();
							vy1 += a.getvy();
							addedself = true;
						}else if(numstaticcircles[i] + numfrozencircles[i]  > 1){
							avgstaticx += c.getX();
							avgstaticy += c.getY();
							totalmass += a.getMass();
						}else{
							double len = Point.distance(cx1, cy1, c.getX(), c.getY());
							double nx = (c.getX() - cx1) / len;
							double ny = (c.getY() - cy1) / len;
							double a1 = 0;
							if(a.isFrozen())
								a1 = 0;
							else
								a1 = (double)(a.getvx() * nx + a.getvy() * ny);
							double p = (double)((2 * (a1)) / (a.getMass() + a.getMass()));
							vx1 += (double)(a.getvx() - (p * a.getMass() * nx) + (p * a.getMass() * -nx));
							vy1 += (double)(a.getvy() - (p * a.getMass() * ny) + (p * a.getMass() * -ny));
						}
					}else if(o instanceof StaticLine){
						//TODO: infinite accumulation of energy?
						if(collisionindex[i].getColliders().get(k).getCollideforce() != null){
							vx1 += collisionindex[i].getColliders().get(k).getCollideforce().getvx();
							vy1 += collisionindex[i].getColliders().get(k).getCollideforce().getvy();
						}
					}
				}
				if(numstaticcircles[i] + numfrozencircles[i] > 1){
					double len = Point.distance(cx1, cy1, avgstaticx, avgstaticy);
					double nx = (avgstaticx - cx1) / len;
					double ny = (avgstaticy - cy1) / len;
					double a1 = 0;
					if(a.isFrozen())
						a1 = 0;
					else
						a1 = (double)(a.getvx() * nx + a.getvy() * ny);
					double p = (double)((2 * (a1)) / (a.getMass() + totalstaticmass));
					double vx = (double)(a.getvx() - (p * a.getMass() * nx) + (p * totalstaticmass * -nx));
					double vy = (double)(a.getvy() - (p * a.getMass() * ny) + (p * totalstaticmass * -ny));
					if(VectorMath.dotproduct(a.getvx() - vx, a.getvy() - vy, avgx - a.getX(), avgy
							- a.getY()) > 0){
						vx = -vx;
						vy = -vy;
					}
					vx1 += vx;
					vy1 += vy;
				}
				if(numforcecircles > 0){
					avgx /= numforcecircles;
					avgy /= numforcecircles;
					avgrad /= numforcecircles;
					if(!intersect || VectorMath.dotproduct(a.getvx() - sumvx, a.getvy() - sumvy, avgx - a.getX(), avgy
									- a.getY()) > 0){
						// maybe try to speed this up with buckets but it seems to slow it down (searching)
						double len = Point.distance(cx1, cy1, avgx, avgy);
						double nx = (avgx - cx1) / len;
						double ny = (avgy - cy1) / len;
						double a1 = 0;
						if(a.isFrozen()){
							a1 = 0;
						}else
							a1 = (a.getvx() * nx + a.getvy() * ny); // dot product
						double a2 = 0;
						a2 = (sumvx * nx + sumvy * ny); // dot product of the sum of colliders
						double p = (2 * (a1 - a2)) / (totalmass);
						if(!a.isFrozen()){
							double vx = (double)(a.getvx() - (p * a.getMass() * nx));
							double vy = (double)(a.getvy() - (p * a.getMass() * ny));
							if(numforcecircles > 1
									&& VectorMath.dotproduct(a.getvx() - vx, a.getvy() - vy, avgx - a.getX(), avgy
											- a.getY()) > 0){
								if(hitmovinginto && ((VectorMath.dotproduct(a.getvx(), a.getvy(), avgx - results[i].getX(), avgy - results[i].getY()) <= 0 
										|| VectorMath.dotproduct(a.getvx(), a.getvy(), sumvx, sumvy) <= 0))){
									nx = -nx;
									ny = -ny;
									vx = -(double)(sumvx - (p * totalmass / numforcecircles * nx));
									vy = -(double)(sumvy - (p * totalmass / numforcecircles * ny));
								}else{
									if(!hitmovinginto){
//										System.out.println(vx);
										/*vx /= numforcecircles;
										vy /= numforcecircles;*/
									}
									vx = -vx;
									vy = -vy;
								}
							}
							vx1 += vx;
							vy1 += vy;
						}
					}else{ // intersection
						vx1 = a.getvx();
						vy1 = a.getvy();
					}
				}
				avgbounce /= collisionindex[i].getColliders().size();
				if(closestdistsq[i] == -1)
					results[i] = new PointVector(results[i].getX(), results[i].getY());
				results[i].setRect(vx1 * avgbounce, vy1 * avgbounce);
				collidedwith.add(i);
				if(collisiontimes[i] == 1 && forceCircle2.get(i).getLength() != 0 && !intersect){
					if(closestdistsq[i] == 0)
						collisiontimes[i] = 0;
					else if(closestdistsq[i] > 0)
						collisiontimes[i] = Math.sqrt(closestdistsq[i])/forceCircle2.get(i).getLength();
					else
						collisiontimes[i] = forceCircle2.get(i).distance(results[i])/forceCircle2.get(i).getLength();
				}
				timepassed[i] += collisiontimes[i]*(1-timepassed[i]);
//				time[i] = collisiontimes[i];
				if(!results[i].equals(forceCircle2.get(i)))
					modified[i] = true;
			}
		}
		return new Collideresult(results, collisionindex, collidedwith, timepassed, collisiontimes, modified);
	}

	/**
	 * Used to specify a new boundary type. Calculates the response from a
	 * collision with a boundary.
	 * 
	 * @param bounds
	 *            the type of boundary.
	 * @param circle
	 *            the <code>ForceCircle</code> that requires a response from the
	 *            collision.
	 * @param boundaries
	 *            the boundaries the circle collided into.
	 * @return A {@link Vector} with the new direction of the
	 *         <code>ForceCircle</code>. This is <b>not</b> a
	 *         <code>Vector</code> representing the surface normal of the
	 *         boundary, the returned vector is not added to the direction of
	 *         the <code>ForceCircle</code>, it is the new vector of the
	 *         <code>ForceCircle</code>.
	 */
	protected Vector boundsresponse(int bounds, ForceCircle circle,
			Boundaries boundaries) {
		return circle;
	}
	
	public void addForceCircle(ForceCircle force) {
		ForceCircles.add(force);
	}

	public int getBoundDetection() {
		return boundDetection;
	}

	public void setBoundDetection(byte boundDetection) {
		this.boundDetection = boundDetection;
	}

	public ArrayList<StaticLine> getLines() {
		return Lines;
	}

	public StaticLine getLine(int i) {
		if (i >= 0 && i < Lines.size())
			return Lines.get(i);
		return null;
	}

	public void setLines(ArrayList<StaticLine> line) {
		Lines = line;
	}

	public void addLine(StaticLine line) {
		Lines.add(line);
	}

	public void removeLine(int i) {
		if (i >= 0 && i <= Lines.size() - 1)
			Lines.remove(i);
	}

	public ArrayList<StaticCircle> getStaticCircles() {
		return StaticCircles;
	}

	public StaticCircle getStaticCircle(int i) {
		return StaticCircles.get(i);
	}

	public void setStaticCircles(ArrayList<StaticCircle> staticCircle) {
		StaticCircles = staticCircle;
	}

	public void addStaticCircle(StaticCircle sc) {
		StaticCircles.add(sc);
	}

	/**
	 * clears arraylists
	 */
	public void clear() {
		this.ForceCircles.clear();
		this.Lines.clear();
		this.StaticCircles.clear();
	}

	public void setBounds(int width, int height){
		bounds = new StaticRect(width / 2, height / 2, width, height);
	}
	
	public void setBounds(Rect bounds) {
		this.bounds = bounds;
	}
	
	public Rect getBounds() {
		return bounds;
	}

	public void setBounds(Rect bounds, byte boundTypes) {
		setBounds(bounds);
		setBoundDetection(boundTypes);
	}

	public List<ForceCircle> getForceCircles() {
		return ForceCircles;
	}

	public ForceCircle getForceCircle(int i) {
		return ForceCircles.get(i);
	}

	public void setForceCircle(ArrayList<ForceCircle> forceCircle) {
		ForceCircles = forceCircle;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getDeltaTime() {
		return deltaTime;
	}

	public void setDeltaTime(double deltaTime) {
		this.deltaTime = deltaTime;
	}
	public byte getBoundResponse() {
		return boundResponse;
	}
	public void setBoundResponse(byte boundResponse) {
		this.boundResponse = boundResponse;
	}
	/**
	 * @param integrator the integrator to set
	 */
	public void setIntegrator(Integrator integrator) {
		this.integrator = integrator;
	}
	/**
	 * @return the integrator
	 */
	public Integrator getIntegrator() {
		return integrator;
	}

}
