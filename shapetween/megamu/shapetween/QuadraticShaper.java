package megamu.shapetween;

public class QuadraticShaper extends Shaper{
	
	/**
	 * The default shape constructor, all Shaper extentions should have these optional parameters 
	 * @param shapeMode IN, OUT, IN_OUT or SEAT
	 * @param transitionX the transition point for IN_OUT or SEAT along the X (time)
	 * @param transitionY the transition point for IN_OUT or SEAT along the f(x) (position)
	 */
	public QuadraticShaper( int shapeMode, float transitionX, float transitionY ){
		this();
		setMode(shapeMode);
		setTransitionPoint(transitionX,transitionY);
	}

	public QuadraticShaper( int shapeMode, float transition ){
		this(shapeMode, transition, transition );
	}
	
	public QuadraticShaper( int shapeMode ){
		this();
		setMode(shapeMode);
	}
	
	public QuadraticShaper(){
		// common constructor
	}
	
	/**
	 * number shaping function
	 * @param x a normalized number between 0 and 1
	 * @returns a normalized number usually between 0 and 1
	 */
	public float f( float x ){
		return x*x;
	}
	
	public float df( float x ){
		return 2*x;
	}
	
	public float ddf( float x ){
		return 2;
	}
	
}