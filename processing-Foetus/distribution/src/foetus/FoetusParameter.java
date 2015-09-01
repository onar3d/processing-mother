package foetus;

import megamu.shapetween.Shaper;
import megamu.shapetween.Tween;

import processing.core.PApplet;

public class FoetusParameter {
	float m_Value;
	float m_LastValue;
	float m_NewValue;
	//float m_Factor;
	
	String m_Address;
	
	public String getAddress() {return m_Address; }
	
	boolean m_Splerp = true;

	Foetus r_f; 
			
	Tween ani;

	long timeStarted;
	
	/**
	 * Allows spline interpolation for individual floating point synth parameters.
	 * @param f
	 * @param value
	 * @param address
	 * @param typetag
	 */
	public FoetusParameter(Foetus f, float value, String address, String typetag) {
		r_f 					= f;		
		m_Value 				= value;
		m_NewValue				= value;
		m_LastValue				= value;

		m_Address = address;
		
		if(r_f!=null)
			r_f.registerMethod(address, typetag);
		
		r_f.addParameter(this);

		timeStarted = System.currentTimeMillis();
		
		ani = new Tween(null/*r_f.parent*/, 0f, Tween.FRAMES, Shaper.COSINE);
	}

	
/*	public void setFactor(Float factor)
	{
		m_Factor = factor;
		
		if(m_Factor>=1.0f)
		{
			r_f.setUpdatingStatus(m_Address, false);
		}
		else
		{
			r_f.setUpdatingStatus(m_Address, true);	
		}
	}*/
	
	
	/**
	 * Returns the interpolated value held by the parameter at the current time.
	 * @return
	 */
	public float getValue() {
		if(m_Splerp) {
			m_Value = PApplet.lerp(m_LastValue, m_NewValue, ani.position());
		}
		
		if(ani.position()>=1.0f) {
			r_f.setUpdatingStatus(m_Address, false);
		}
		else {
			r_f.setUpdatingStatus(m_Address, true);	
		}

//		System.out.println(		"Last: " + m_LastValue + 
//								", New: " + m_NewValue + 
//								", Factor: " + m_Factor);
//		
//		System.out.println(	"Value: " + m_Value + 
//							", Lerp: " + PApplet.lerp(m_LastValue, m_NewValue, ani.position()));
		
		return m_Value;
	}
		
	/**
	 * Set a new value for the parameter. This may trigger an interpolation with the 
	 * new value as target.
	 * The criterium for whether interpolation is triggered or not, is how long it was since the last value was received.
	 * If it was more than 750ms, animation is triggered. If not, the value is just set directly.
	 * The duration of the animation is dependent on how long it was since the last value was received, up to a maximum of MaxAnimationDuration, a static parameter set in foetus
	 * @param val
	 */
	public void setValue(float val)	{
		long elapsed;
		long elapsedTime = System.currentTimeMillis() - timeStarted;
		
		//System.out.println("New value in: " + val);
		
	    m_LastValue = m_Value;
	    m_NewValue  = val;
		    
	    elapsed = (long)(elapsedTime/r_f.getSpeedFraction());
	  
	    //System.out.println(elapsed/1000f);
	    
	    ani.end();
	    
	    if(elapsed<(750.0/r_f.getSpeedFraction())) {
	    	m_Splerp 	= false;
	    	m_LastValue = val;
	    	m_Value 	= val;
	    	r_f.setUpdatingStatus(m_Address, false);
	    	timeStarted = System.currentTimeMillis(); // 2013
	    	//System.out.println("No splerp: " + elapsed);
	    }
	    else {
	    	m_Splerp = true;
	    	
	    	if(elapsed>(r_f.GetMaxAnimationDuration()/r_f.getSpeedFraction()))
	    		elapsed = (long)(r_f.GetMaxAnimationDuration()/r_f.getSpeedFraction());
	  	    	
	    	ani = new Tween(null, elapsed/1000f * r_f.parent.frameRate * r_f.getSpeedFraction(), Tween.FRAMES, Shaper.COSINE);
	    	//ani.setDuration(elapsed/1000f * r_f.parent.frameRate, Tween.FRAMES);
	    	//ani.setDuration(elapsed/1000f , Tween.SECONDS);
	    	ani.start();
	    	
	    	timeStarted = System.currentTimeMillis();
	    	
	    	//System.out.println("YES! Splerp: " + elapsed);
	    }
	}
	
	public void tick() {
		if(ani.isTweening()) {
			ani.tick();
			//System.out.println("tick " + ani.position());
		}
	}
}