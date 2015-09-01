package megamu.shapetween;

public class CosineShaper extends Shaper{
	
	/**
	 * The default shape constructor, all Shaper extentions should have these optional parameters 
	 * @param shapeMode IN, OUT, IN_OUT or SEAT
	 * @param transitionX the transition point for IN_OUT or SEAT along the X (time)
	 * @param transitionY the transition point for IN_OUT or SEAT along the f(x) (position)
	 */
	public CosineShaper( int shapeMode, float transitionX, float transitionY ){
		this();
		setMode(shapeMode);
		setTransitionPoint(transitionX,transitionY);
	}

	public CosineShaper( int shapeMode, float transition ){
		this(shapeMode, transition, transition );
	}
	
	public CosineShaper( int shapeMode ){
		this();
		setMode(shapeMode);
	}
	
	public CosineShaper(){
		// common constructor
	}
	
	/**
	 * number shaping function
	 * @param in a normalized number between 0 and 1
	 * @returns a normalized number usually between 0 and 1
	 */
	protected float f( float x ){
		return (float) ( 1 - Math.cos( x*0.5*Math.PI ) );
	}
	
	protected float df( float x ){
		return (float) ( Math.sin( x*0.5*Math.PI )*Math.PI*0.5 );
	}
	
	protected float ddf( float x ){
		return (float) ( Math.cos( x*0.5*Math.PI )*Math.PI*Math.PI*0.25 );
	}
}
