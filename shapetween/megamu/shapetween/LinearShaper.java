package megamu.shapetween;

public class LinearShaper extends Shaper {
	
	/**
	 * The default shape constructor, all Shaper extentions should have these optional parameters 
	 * @param shapeMode IN, OUT, IN_OUT or SEAT
	 * @param transitionX the transition point for IN_OUT or SEAT along the X (time)
	 * @param transitionY the transition point for IN_OUT or SEAT along the f(x) (position)
	 */
	public LinearShaper( int shapeMode, float transitionX, float transitionY ){
		this();
		setMode(shapeMode);
		setTransitionPoint(transitionX,transitionY);
	}

	public LinearShaper( int shapeMode, float transition ){
		this(shapeMode, transition, transition );
	}
	
	public LinearShaper( int shapeMode ){
		this();
		setMode(shapeMode);
	}
	
	public LinearShaper(){
		// common constructor
		setMode( IN );
	}
	
	/**
	 * number shaping function
	 * @param x a normalized number between 0 and 1
	 * @returns a normalized number usually between 0 and 1
	 */
	protected float f( float x ){
		return x;
	}
	
	protected float df( float x ){
		return 1;
	}
	
	protected float ddf( float x ){
		return 0;
	}
}
