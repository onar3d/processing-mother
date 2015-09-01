package megamu.shapetween;

public class BezierShaper extends Shaper {
	
	public static final double EPSILON = 0.0001;

	public float inHandleX, inHandleY, outHandleX, outHandleY;
	
	public double A,B,C,D,E,F,G,H;

	/**
	 * The default shape constructor, all Shaper extentions should have these optional parameters 
	 * @param shapeMode IN, OUT, IN_OUT or SEAT
	 * @param transitionX the transition point for IN_OUT or SEAT along the X (time)
	 * @param transitionY the transition point for IN_OUT or SEAT along the f(x) (position)
	 */
	public BezierShaper( int shapeMode, float transitionX, float transitionY ){
		this();
		setMode(shapeMode);
		setTransitionPoint(transitionX,transitionY);
	}

	public BezierShaper( int shapeMode, float transition ){
		this(shapeMode, transition, transition );
	}

	public BezierShaper( int shapeMode ){
		this();
		setMode(shapeMode);
	}

	public BezierShaper(){
		// common constructor
		inHandleX = 0.4f;
		inHandleY = 0.05f;
		outHandleX = 0.6f;
		outHandleY = 0.95f;
		calcConstants();
		setMode( IN );
	}

	/**
	 * Sets the handle for the beginning of the bezier
	 */
	public void setInHandle( float x, float y ){
		inHandleX = Math.min( Math.max(0,x), 1); // stay inside the domain
		inHandleY = y;
		
		calcConstants();
	}

	/**
	 * Sets the handle for the end of the bezier
	 */
	public void setOutHandle( float x, float y ){
		outHandleX = Math.min( Math.max(0,x), 1); // stay inside the domain
		outHandleY = y;
		
		calcConstants();
	}
	
	protected void calcConstants(){
		A = 1.0 - 3.0*outHandleX + 3.0*inHandleX - 0.0;
		B = 3.0*outHandleX - 6.0*inHandleX + 3.0*0;
		C = 3.0*inHandleX - 3.0*0;
		D = 0.0;
		E = 1.0 - 3.0*outHandleY + 3.0*inHandleY - 0.0;
		F = 3.0*outHandleY - 6.0*inHandleY + 3.0*0;
		G = 3.0*inHandleY - 3.0*0;
		H = 0.0;
	}

	protected float f(float x){
		double t = tFromX(x);
		return (float) yFromT(t, E,F,G,H);
	}
	
	protected float df(float x){
		double t = tFromX(x);
		return (float) (dydt(t,E,F,G)/dxdt(t,A,B,C));
	}
	
	protected float ddf(float x){
		double t = tFromX(x);
		double dxdt = dxdt(t,A,B,C);
		return (float) ((dydtdt(t,E,F)*dxdt - dydt(t,E,F,G)*dxdtdt(t,A,B)) / dxdt);
	}
	
	protected double tFromX(float x){
		// Solve for t given x (using Newton-Raphelson), then solve for y given t.
		// Assume for the first guess that t = x.
		double currentt = x;
		int nRefinementIterations = 5;
		for (int i=0; i<nRefinementIterations; i++){
			double currentx = xFromT (currentt, A,B,C,D); 
			double currentslope = slopeFromT (currentt, A,B,C);
			double dt = (currentx - x)*(currentslope);
			currentt -= dt;
			if( Math.abs(dt) < EPSILON )
				break;
		}
		return currentt;
	}

	protected double slopeFromT (double t, double A, double B, double C){
		return 1.0/(3.0*A*t*t + 2.0*B*t + C);
	}

	protected double xFromT (double t, double A, double B, double C, double D){
		return A*(t*t*t) + B*(t*t) + C*t + D;
	}
	
	protected double yFromT (double t, double E, double F, double G, double H){
		return E*(t*t*t) + F*(t*t) + G*t + H;
	}
	
	protected double dxdt (double t, double A, double B, double C){
		return 3.0*A*(t*t) + 2.0*B*(t) + C;
	}
	
	protected double dydt (double t, double E, double F, double G){
		return 3.0*E*(t*t) + 2.0*F*(t) + G;
	}
	
	protected double dxdtdt (double t, double A, double B){
		return 6.0*A*(t) + 2.0*B;
	}
	
	protected double dydtdt (double t, double E, double F){
		return 6.0*E*(t) + 2.0*F;
	}
}
