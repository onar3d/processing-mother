package megamu.shapetween;

public class BackShaper extends Shaper {

	private float anticipation = 1.0f;
	private float s = 1.70158f;

	/**
	 * The default shape constructor, all Shaper extentions should have these optional parameters 
	 * @param shapeMode IN, OUT, IN_OUT or SEAT
	 * @param transitionX the transition point for IN_OUT or SEAT along the X (time)
	 * @param transitionY the transition point for IN_OUT or SEAT along the f(x) (position)
	 */
	public BackShaper( int shapeMode, float transitionX, float transitionY ){
		this();
		setMode(shapeMode);
		setTransitionPoint(transitionX,transitionY);
	}

	public BackShaper( int shapeMode, float transition ){
		this(shapeMode, transition, transition );
	}

	public BackShaper( int shapeMode ){
		this();
		setMode(shapeMode);
	}

	public BackShaper(){
		// common constructor
		setMode( IN );
	}

	public void setAnticipation( float howfar ){
		anticipation = (float) Math.max(0, howfar);
		s = sFromMinima( anticipation );
	}

	public float getAnticipation(){
		return anticipation;
	}

	/**
	 * number shaping function
	 * @param in a normalized number between 0 and 1
	 * @returns a normalized number usually between 0 and 1
	 */
	protected float f( float x ){
		return x*x*( (s+1)*x - s);
	}
	
	/**
	 * Derivative of f(), this would describe the speed of an animated element
	 */
	protected float df( float x ){
		return 3*x*x*(s+1) - 2*x*s;
	}
	
	protected float ddf( float x ){
		return 6*x*(s+1) - 2*s;
	}

	/**
	 * Calculates the minima point from a strength of the bend of the curve
	 * @param s the 'strength'
	 * @return the minima, which determines the aniticipation
	 */
	protected float minima_from_s( float s ){
		return ( -4*s*s*s ) / ( 27 * (s*s + 2*s + 1) );
	}

	/**
	 * Calculates the strength of the curve based on a desired minima point (anticipation)
	 * @param m the minima (anticipation)
	 * @return the 'strength'
	 */
	protected float sFromMinima( float m ){
		if( m == 0 )
			return 0;
		double third = 1.0/3.0;
		double a = -4.0/27.0;
		double ma = m/a;
		double tmmaa = third*ma*ma;
		double f = 2.0*ma - tmmaa;
		double g = (-a/2.0)*ma*ma*ma - 2.0*tmmaa + ma;
		double h = Math.sqrt( g*g/4.0 + f*f*f/27.0 );
		double t = Math.pow( -g/2.0 + h, third);
		double u = Math.pow( -g/2.0 - h, third);
		return (float) ( t+u - third*ma );
	}
}
