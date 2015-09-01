package megamu.shapetween;

/**
 * This is a special kind of Shaper which takes two Shapers
 * and interpolates between the two based on a blend variable
 * @author lee
 */
public class BlendShaper extends Shaper {
	
	Shaper a,b;
	public float blend;
	
	public BlendShaper( Shaper a ){
		this( a, null, 0.5f );
	}
	
	public BlendShaper( Shaper a, Shaper b ){
		this( a, b, 0.5f );
	}
	
	public BlendShaper( Shaper a, float blend ){
		this(a, null, blend);
	}
	
	public BlendShaper( Shaper a, Shaper b, float blend ){
		this.a = a;
		this.b = b;
		this.blend = blend;
		
		// set default mode
		setMode( IN );
	}
	
	public float getBlend(){
		return blend;
	}
	
	public void setBlend( float blend ){
		this.blend = blend;
	}
	
	public Shaper getShaperOne(){
		return a;
	}
	
	public Shaper getShaperTwo(){
		return b;
	}
	
	public void setShaperOne(Shaper s){
		a = s;
	}
	
	public void setShaperTwo(Shaper s){
		b = s;
	}

	protected float f(float x) {
		if( blend == 0 )
			return a.shape(x);
		if( blend == 1)
			return b==null?x:b.shape(x);
		return a.shape(x)*(1-blend) + (b==null?x:b.shape(x))*blend;
	}
	
	protected float df(float x){
		if( blend == 0 )
			return a.slope(x);
		if( blend == 1)
			return b==null?x:b.slope(x);
		return a.slope(x)*(1-blend) + (b==null?x:b.slope(x))*blend;
	}
	
	protected float ddf(float x){
		if( blend == 0 )
			return a.secondSlope(x);
		if( blend == 1)
			return b==null?x:b.secondSlope(x);
		return a.secondSlope(x)*(1-blend) + (b==null?x:b.secondSlope(x))*blend;
	}

}
