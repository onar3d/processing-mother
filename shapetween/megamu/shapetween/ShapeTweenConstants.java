/*
 * ShapeTweenConstants.java - Part of the Shapetween Processing Animation and Data Shaping Library
 * by Lee Byron and Golan Levin
 */

package megamu.shapetween;

/**
 * This interface defines constants for the shapetween library
 */
public interface ShapeTweenConstants {

	// timer duration modes

	public static final boolean FRAMES = true;
	public static final boolean SECONDS = false;

	// play repeat modes

	public static final int ONCE = 0; // from beginning to end and then stops
	public static final int REPEAT = 1; // from beginning to end, repeating
	public static final int REVERSE_ONCE = 2; // from beginning to end back to beginning
	public static final int REVERSE_REPEAT = 3; // from beginning to end back to beginning, repeating
	
	// the four different shapes
	
	public static final int IN = 0;
	public static final int OUT = 1;
	public static final int IN_OUT = 2;
	public static final int SIGMOID = 2;
	public static final int OUT_IN = 3;
	public static final int SEAT = 3;
	
	// When looking for methods, this is the argument signature to look for
	
	public static final Class[] ARGS = {float.class};
	
	// The built in shaper functions
	
	public static final Class LINEAR = LinearShaper.class;
	public static final Class COSINE = CosineShaper.class;
	public static final Class QUADRATIC = QuadraticShaper.class;
	public static final Class CIRCULAR = CircularShaper.class;
	public static final Class BEZIER = BezierShaper.class;
	public static final Class BACK = BackShaper.class;
	public static final Class BOUNCE = BounceShaper.class;
	
}
