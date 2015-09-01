/*
 * Shaper.java - Part of the Shapetween Processing Animation and Data Shaping Library
 * by Lee Byron and Golan Levin
 */

package megamu.shapetween;

/**
 * This abstract class defines a Shaper
 */
public abstract class Shaper implements ShapeTweenConstants{
	
	// instance variables

	/**
	 * defines the type of shape this forms, which defaults to an "ease in out"
	 */
	public int shapeMode = IN_OUT;
	
	/**
	 * defines the transition point along the X (time)
	 */
	public float transitionX = 0.5f;
	
	/**
	 * defines the transition point along the f(x) (position)
	 * this is usually the same as transitionX unless looking for a skewed transition
	 */
	public float transitionY = 0.5f;
	
	/**
	 * If the output should be clamped between 0 and 1, defaults to not clamped
	 */
	public boolean clamp = false;
	
	
	
	// common functionality
	
	/**
	 * Shapes a normalized number. Takes a number between 0 and 1 and returns a number
	 * usually between 0 and 1. If the shaper is set to clamp, then this will return a number
	 * between 0 and 1
	 * @param in a normalized input between 0 and 1
	 * @return a normalized output usually between 0 and 1
	 */
	public float shape( float in ){
		return shape( in, clamp );
	}
	
	protected float shape( float in, boolean forceClamp ){
		float out;
		switch( shapeMode ){
		case IN:
		default:
			out = f(in);
			break;
		case OUT:
			out = invf(in);
			break;
		case IN_OUT:
			if( in < transitionX )
				out = f(in/transitionX)*transitionY;
			else
				out = invf( (in-transitionX)/(1-transitionX) )*(1-transitionY) + transitionY;
			break;
		case OUT_IN:
			if( in < transitionX )
				out = invf(in/transitionX)*transitionY;
			else
				out = f( (in-transitionX)/(1-transitionX) )*(1-transitionY) + transitionY;
			break;
		}
		
		if( forceClamp )
			out = Math.max(Math.min(out, 1), 0);
		
		return out;
	}
	
	/**
	 * Returns the slope of the function at a given point, useful for determining speed, or other various needs
	 * TODO: Currently totally not accurate if transitionx doesnt equal transitiony
	 */
	public float slope( float in ){
		
		// is it clamped and thus 0?
		if( clamp ){
			float shaped = shape(in,false);
			if( shaped < 0 || shaped > 1 )
				return 0;
		}
		
		// otherwise figure it out
		switch( shapeMode ){
		case IN:
		default:
			return df(in);
		case OUT:
			return invdf(in);
		case IN_OUT:
			if( in < transitionX )
				return df(in/transitionX);
			return invdf( (in-transitionX)/(1-transitionX) );
		case OUT_IN:
			if( in < transitionX )
				return invdf(in/transitionX);
			return df( (in-transitionX)/(1-transitionX) );
		}
	}

	/**
	 * Returns the slope of the function at a given point, useful for determining speed, or other various needs
	 * TODO: Currently totally not accurate if transitionx doesnt equal transitiony
	 */
	public float secondSlope( float in ){
		
		// is it clamped and thus 0?
		if( clamp ){
			float shaped = shape(in,false);
			if( shaped < 0 || shaped > 1 )
				return 0;
		}
		
		// otherwise figure it out
		switch( shapeMode ){
		case IN:
		default:
			return ddf(in);
		case OUT:
			return invddf(in);
		case IN_OUT:
			if( in < transitionX )
				return ddf(in/transitionX)/transitionX;
			return invddf( (in-transitionX)/(1-transitionX) )/(1-transitionX);
		case OUT_IN:
			if( in < transitionX )
				return invddf(in/transitionX)/transitionX;
			return ddf( (in-transitionX)/(1-transitionX) )/(1-transitionX);
		}
	}



	/**
	 * Calls the shape function on one of the Shaper library's built in functions
	 * @param shape one of the static functions: ie Shaper.LINEAR
	 * @param in the value in between 0 and 1
	 * @deprecated this is mad slow, and the use case is just lazy programming
	 */
	public static float shape( Class shape, float in ){
		if( shape == null )
			return in;
		try{
			Object[] args = {new Float(in)};
			return ((Float) shape.getMethod("shape", ARGS).invoke( shape.newInstance(), args )).floatValue();
		}catch( Exception e ){
			e.printStackTrace();
			return 0;
		}
	}
	
	
	
	// modifiers and setters

	/**
	 * Set's the shape mode
	 * @param mode IN, OUT, IN_OUT, SEAT
	 */
	public void setMode( int mode ){
		shapeMode = mode;
	}
	
	/**
	 * Sets the shape mode to ease in, identical to setShapeMode(IN)
	 * @deprecated This whole bit may be superfluous.
	 */
	public void easeIn(){
		setMode(IN);
	}
	
	/**
	 * Sets the shape mode to ease out, identical to setShapeMode(OUT)
	 * @deprecated This whole bit may be superfluous.
	 */
	public void easeOut(){
		setMode(OUT);
	}

	/**
	 * Sets the shape mode to ease in out, identical to setShapeMode(IN_OUT)
	 * @deprecated This whole bit may be superfluous.
	 */
	public void easeInOut(){
		setMode(IN_OUT);
	}

	/**
	 * Sets the transition point when in SIGMOID or SEAT modes
	 * @param x the time transition point
	 * @param y the shaped distortion point, optional
	 */
	public void setTransitionPoint(float x, float y){
		transitionX = Math.min( Math.max( x, 0), 1);
		transitionY = Math.min( Math.max( y, 0), 1);
	}

	public void setTransitionPoint(float x){
		setTransitionPoint(x,x);
	}
	
	/**
	 * Sets the shaper to clamp its output between 0 and 1.
	 * This is particularly necissary when using shapes for things that are not
	 * tweening through space
	 */
	public void clamp(){
		clamp = true;
	}
	
	/**
	 * Sets the shaper to not clamp its output, allowing a shape to
	 * return a number less than 0 or greater than 1
	 */
	public void noClamp(){
		clamp = false;
	}

	

	// number shaping functions

	/**
	 * All extensions of the Shaper class should implement this method f(x)
	 * which specifies that a number in between 0 and 1 returns a number usually between 0 and 1
	 */
	protected abstract float f( float x );
	
	/**
	 * The derivative of the function, used to calculate speed
	 */
	abstract protected float df( float x );
	
	/**
	 * The second derivative of the function, used to calculate acceleration
	 */
	abstract protected float ddf( float x );
	
	/**
	 * Defines a function which is a flipped inverse of f(x)
	 * ( f(x) reflected over the line y=1-x )
	 * used to create the second half of sigmoids and seats from simple shapes
	 * @param in the number in from 0 to 1
	 * @return a number out, usually between 0 and 1
	 */
	protected float invf( float x ){
		return 1-f(1-x);
	}
	
	/**
	 * flipped inverse of the derivative of the function
	 */
	protected float invdf( float x ){
		return df(1-x);
	}
	
	/**
	 * flipped inverse of the second derivative of the function
	 */
	protected float invddf( float x ){
		return -ddf(1-x);
	}
	
}
