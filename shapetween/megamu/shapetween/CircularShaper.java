package megamu.shapetween;

public class CircularShaper extends Shaper {

	public float pointX, pointY;
	public float m_Centerx, m_Centery, m_dRadius;

	/**
	 * The default shape constructor, all Shaper extentions should have these optional parameters 
	 * @param shapeMode IN, OUT, IN_OUT or SEAT
	 * @param transitionX the transition point for IN_OUT or SEAT along the X (time)
	 * @param transitionY the transition point for IN_OUT or SEAT along the f(x) (position)
	 */
	public CircularShaper( int shapeMode, float transitionX, float transitionY ){
		this();
		setMode(shapeMode);
		setTransitionPoint(transitionX,transitionY);
	}

	public CircularShaper( int shapeMode, float transition ){
		this(shapeMode, transition, transition );
	}

	public CircularShaper( int shapeMode ){
		this();
		setMode(shapeMode);
	}

	public CircularShaper(){
		// point defaults
		pointX = 1;
		pointY = 0;
		
		// ease in by default
		setMode( IN );
	}

	/**
	 * Sets a point that the circular arc will go through
	 * @param x a normalized number between 0 and 1
	 * @param y a normalized number between 0 and 1
	 */
	public void throughPoint( float x, float y ){
		pointX = x;
		pointY = y;
	}

	/**
	 * number shaping function
	 * @param in a normalized number between 0 and 1
	 * @returns a normalized number usually between 0 and 1
	 */
	protected float f(float x) {
		
		// if x is in an extreme, don't bother calculating
		if( x == 1 || x == 0 )
			return x;
		
		// this is an ease in
		if( pointX == 1 || pointY == 0 )
			return 1 - sqrt(1 - x*x);
		
		// this is an ease out
		if( pointX == 0 || pointY == 1 )
			return sqrt(1 - sq(1 - x));
		
		// otherwise lets figure out that curve!
		float pt1x = 0;
		float pt1y = 0;
		float pt2x = pointX;
		float pt2y = pointY;
		float pt3x = 1;
		float pt3y = 1;

		if      (!isPerpendicular(pt1x,pt1y, pt2x,pt2y, pt3x,pt3y) )	calcCircleFrom3Points (pt1x,pt1y, pt2x,pt2y, pt3x,pt3y);	
		else if (!isPerpendicular(pt1x,pt1y, pt3x,pt3y, pt2x,pt2y) )	calcCircleFrom3Points (pt1x,pt1y, pt3x,pt3y, pt2x,pt2y);	
		else if (!isPerpendicular(pt2x,pt2y, pt1x,pt1y, pt3x,pt3y) )	calcCircleFrom3Points (pt2x,pt2y, pt1x,pt1y, pt3x,pt3y);	
		else if (!isPerpendicular(pt2x,pt2y, pt3x,pt3y, pt1x,pt1y) )	calcCircleFrom3Points (pt2x,pt2y, pt3x,pt3y, pt1x,pt1y);	
		else if (!isPerpendicular(pt3x,pt3y, pt2x,pt2y, pt1x,pt1y) )	calcCircleFrom3Points (pt3x,pt3y, pt2x,pt2y, pt1x,pt1y);	
		else if (!isPerpendicular(pt3x,pt3y, pt1x,pt1y, pt2x,pt2y) )	calcCircleFrom3Points (pt3x,pt3y, pt1x,pt1y, pt2x,pt2y);	
		else return 0;
		
		//------------------
		// constrain
		if ((m_Centerx > 0) && (m_Centerx < 1)){
			if ( pointX < m_Centerx){
				m_Centerx = 1;
				m_Centery = 0;
				m_dRadius = 1;
			} else {
				m_Centerx = 0;
				m_Centery = 1;
				m_dRadius = 1;
			}
		}

		//------------------
		float y = 0;
		if (x >= m_Centerx){
			y = m_Centery - sqrt(sq(m_dRadius) - sq(x-m_Centerx));
		} 
		else{
			y = m_Centery + sqrt(sq(m_dRadius) - sq(x-m_Centerx)); 
		}
		return y;
	}
	
	protected float df(float x){
		float cx = m_Centerx;
		float cy = m_Centery;
		float r = m_dRadius;
		
		// this is an ease in
		if( pointX == 1 || pointY == 0 ){
			cx = 0;
			cy = 1;
			r = 1;
		}
		
		// this is an ease out
		if( pointX == 0 || pointY == 1 ){
			cx = 1;
			cy = 0;
			r = 1;
		}

		// through a point
		if( x == cx)
			return 0;
		
		float y = f(x);
		
		if( Math.abs(x-cx) >= r )
			return (cy>y?Float.NEGATIVE_INFINITY:Float.POSITIVE_INFINITY);
		
		return (cx - x)/(y - cy);
	}
	
	protected float ddf(float x){
		//return 0; // dunno yet, hard math
		//eq for circle y = sqrt(r^2 - (x-cx)^2) + cy
		
		float cx = m_Centerx;
		float cy = m_Centery;
		
		// this is an ease in
		if( pointX == 1 || pointY == 0 ){
			cx = 0;
			cy = 1;
		}
		
		// this is an ease out
		if( pointX == 0 || pointY == 1 ){
			cx = 1;
			cy = 0;
		}
		
		float y = f(x);
		double o = y-cy;
		double h = Math.sqrt(o*o + (x-cx)*(x-cx));
		double sin = o/h;
		return (float) (-1/(sin*sin*sin));
	}

	/**
	 * Returns true if a line through point 1 and 2 is perpendicular to a line through point 2 and 3
	 * @param pt1x
	 * @param pt1y
	 * @param pt2x
	 * @param pt2y
	 * @param pt3x
	 * @param pt3y
	 * @return
	 */
	protected boolean isPerpendicular(
			float pt1x, float pt1y,
			float pt2x, float pt2y,
			float pt3x, float pt3y)
	{
		// Check the given point are perpendicular to x or y axis 
		float yDelta_a = pt2y - pt1y;
		float xDelta_a = pt2x - pt1x;
		float yDelta_b = pt3y - pt2y;
		float xDelta_b = pt3x - pt2x;
		float epsilon = 0.000001f;

		// checking whether the line of the two pts are vertical
		if (abs(xDelta_a) <= epsilon && abs(yDelta_b) <= epsilon){
			return false;
		}
		if (abs(yDelta_a) <= epsilon){
			return true;
		}
		else if (abs(yDelta_b) <= epsilon){
			return true;
		}
		else if (abs(xDelta_a)<= epsilon){
			return true;
		}
		else if (abs(xDelta_b)<= epsilon){
			return true;
		}
		else return false;
	}

	/**
	 * Calculates a center position and radius that passes through these 3 points
	 * @param pt1x
	 * @param pt1y
	 * @param pt2x
	 * @param pt2y
	 * @param pt3x
	 * @param pt3y
	 */
	protected void calcCircleFrom3Points (
			float pt1x, float pt1y,
			float pt2x, float pt2y,
			float pt3x, float pt3y)
	{
		float yDelta_a = pt2y - pt1y;
		float xDelta_a = pt2x - pt1x;
		float yDelta_b = pt3y - pt2y;
		float xDelta_b = pt3x - pt2x;
		float epsilon = 0.000001f;

		if (abs(xDelta_a) <= epsilon && abs(yDelta_b) <= epsilon){
			m_Centerx = 0.5f*(pt2x + pt3x);
			m_Centery = 0.5f*(pt1y + pt2y);
			m_dRadius = sqrt(sq(m_Centerx-pt1x) + sq(m_Centery-pt1y));
			return;
		}

		// isPerpendicular() assure that xDelta(s) are not zero
		float aSlope = yDelta_a / xDelta_a; 
		float bSlope = yDelta_b / xDelta_b;
		if (abs(aSlope-bSlope) <= epsilon){	// checking whether the given points are colinear. 	
			return;
		}

		// calc center
		m_Centerx = (aSlope*bSlope*(pt1y - pt3y) + bSlope*(pt1x + pt2x)- aSlope*(pt2x+pt3x) )/(2* (bSlope-aSlope) );
		m_Centery = -1*(m_Centerx - (pt1x+pt2x)/2)/aSlope +  (pt1y+pt2y)/2;
		m_dRadius = sqrt(sq(m_Centerx-pt1x) + sq(m_Centery-pt1y));
	}
	
	protected float sq(float x){
		return x*x;
	}
	
	protected float sqrt(float x){
		return (float) Math.sqrt(x);
	}
	
	protected float abs(float x){
		return (float) Math.abs(x);
	}

}
