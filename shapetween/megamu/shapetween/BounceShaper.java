package megamu.shapetween;

public class BounceShaper extends Shaper {
	
	protected int bounces;
	protected float decay;
	
	protected float totalWidth;
	protected float[] inflections;
	protected float[] heights;
	protected float[] maxima;

	public BounceShaper( int shapeMode, float transitionX, float transitionY ){
		this();
		setMode(shapeMode);
		setTransitionPoint(transitionX,transitionY);
	}

	public BounceShaper( int shapeMode, float transition ){
		this(shapeMode, transition, transition );
	}

	public BounceShaper( int shapeMode ){
		this();
		setMode(shapeMode);
	}

	public BounceShaper(){
		// common constructor
		bounces = 3;
		decay = 0.3333f;
		calcInflections();
		setMode( OUT );
	}
	
	public void setBounces( int howMany ){
		bounces = howMany;
		calcInflections();
	}
	
	public int getBounces(){
		return bounces;
	}
	
	public void setDecay( float howMuch ){
		decay = Math.max(0,Math.min(howMuch,1));
		calcInflections();
	}
	
	public float getDecay(){
		return decay;
	}
	
	protected void calcInflections(){
		inflections = new float[bounces+1];
		heights = new float[bounces+1];
		maxima = new float[bounces+1];
		
		// initial bounce
		inflections[0] = -1;
		heights[0] = 1;
		maxima[0] = 0;
		totalWidth = 1;
		float lastHeight = 1;
		
		for( int i=1; i<bounces+1; i++ ){
			lastHeight = lastHeight * decay;
			heights[i] = lastHeight;
			inflections[i] = totalWidth;
			float width = (float) Math.sqrt(heights[i]);
			totalWidth += width;
			maxima[i] = totalWidth;
			totalWidth += width;
		}
	}

	protected float f(float x) {
		// which bounce are we in
		int bounce = 0;
		x = (1-x)*totalWidth;
		while( bounce < bounces && x > inflections[bounce+1] )
			bounce++;
		
		// return value
		float xdiff = (x - maxima[bounce]);
		return (heights[bounce] - xdiff*xdiff);
	}

	protected float df(float x) {
		// which bounce are we in
		int bounce = 0;
		float sx = (1-x)*totalWidth;
		while( bounce < bounces && sx > inflections[bounce+1] )
			bounce++;
		
		return -2 * ( (x-1)*totalWidth + maxima[bounce] ) * totalWidth;
	}
	
	protected float ddf(float x) {
		// TODO Auto-generated method stub
		return 4*totalWidth*totalWidth;
	}
}
