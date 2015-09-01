/*
 * Tween.java - Part of the Shapetween Processing Animation and Data Shaping Library
 * by Lee Byron and Golan Levin
 */

package megamu.shapetween;

import processing.core.PApplet;
import java.lang.reflect.Method;

/**
 * The Tween class is a general Tween object class that tweens a number from 0 to 1
 * over a duration of time. A tween can also have an easing shape function which maps
 * 'time' to 'position', creating more natural motions.
 */

public class Tween implements ShapeTweenConstants{

	// global variables
	
	/**
	 * This number alters the percieved speed of the tweens. Less than 1 is slow-mo, more than 1 is fast forward
	 */
	public static float timeScale = 1;

	
	
	// instance variables

	/**
	 * Stores the time of this Tween between 0 and 1, protected so that Processing isn't exposed to a double
	 */
	protected double time;

	/**
	 * The link back to the parent (should we choose to use it)
	 */
	protected PApplet parent;

	/**
	 * The position of this Tween (usually) between 0 and 1, based on whatever shape function is being applied
	 */
	protected float position;

	/**
	 * Flag the time mode of the Tween (FRAMES or SECONDS)
	 */
	protected boolean isFrameBased;

	/**
	 * The duration of 'time' each frame should consume, this is based on the input duration as well as the time mode
	 */
	protected double duration;

	/**
	 * Saves the last timestamp from the previous frame so that time-based animation can work properly
	 */
	protected double lastTime;

	/**
	 * Flag if this Tween is actively playing or not
	 */
	protected boolean isTweening;

	/**
	 * Flag if this Tween is playing in reverse
	 */
	protected boolean inReverse;

	/**
	 * Flag the play mode of the Tween (PLAY_ONCE, REPEAT or REVERSE_REPEAT)
	 */
	protected int mode;

	/**
	 * The Method that should be called for shaping
	 */
	protected Method shapeMethod;
	
	/**
	 * Should we be using the call to the PApplet's custom shape function?
	 */
	protected boolean useShapeMethod;
	
	/**
	 * The Shaper that maps time to position
	 */
	protected Shaper shaper;

	/**
	 * Count of how many times the Tween has repeated for the current 'start'
	 */
	protected int playCount;
	
	
	
	// object constructing

	/**
	 * Constructs a Timer
	 * @param parent the Processing Applet, usually 'this' ( if set to null, then manually call tick() )
	 * @param duration the number of seconds or frames this Tween will take
	 * @param durationType should be set to SECONDS or FRAMES accordingly
	 * @param easing the easing shape that this tween will use for position, see easing for details
	 * @param mode the play mode this tween uses: ONCE, REPEAT, REVERSE_ONCE or REVERSE_REPEAT
	 */
	public Tween( PApplet parent, float duration, boolean durationType, Object easing, int mode ){
		setDuration( duration, durationType );
		this.mode = mode;

		if( easing != null )
			setEasing( easing );
		
		if( parent != null ){
			this.parent = parent;
			parent.registerPre(this);
		}
		
		start();
	}
	
	public Tween( PApplet parent, float duration, boolean durationType, Object easing ){
		this( parent, duration, durationType, easing, ONCE );
	}
	
	public Tween( PApplet parent, float duration, boolean durationType ){
		this( parent, duration, durationType, null );
	}
	
	public Tween( PApplet parent, float duration ){
		this( parent, duration, SECONDS );
	}
	
	
	// getting the major variables
	
	/**
	 * Returns the time of this Tween, which is always linear between 0 and 1
	 */
	public float time(){
		return (float) time;
	}

	/**
	 * Returns the position of this Tween, which is usually between 0 and 1.
	 * This is based on what shape function is currently applied to the Tween
	 */
	public float position(){
		return position;
	}
	
	/**
	 * Returns if this Tween is currently moving
	 */
	public boolean isTweening(){
		return isTweening;
	}
	
	
	// play head control

	/**
	 * Begins the Tween, starting from the start position
	 */
	public void start(){
		playCount = 0;
		time = inReverse?1:0;
		resume();
	}

	/**
	 * Interrupts the Tween, jumping immediately to the end position
	 */
	public void end(){
		pause();
		time = inReverse?0:1;
	}
	
	/**
	 * Pauses the Tween updating
	 */
	public void pause(){
		isTweening = false;
	}

	/**
	 * Resumes the Tween from a paused state
	 */
	public void resume(){
		isTweening = true;
		if( !isFrameBased )
			lastTime = System.currentTimeMillis();
		updatePosition();
	}
	
	/**
	 * Plays the Tween in reverse, starting from the current position of the Tween
	 */
	public void reverse(){
		inReverse = !inReverse;
	}

	/**
	 * Advances the Tween to the specified position
	 */
	public void seek(float where){
		this.time = (double) where;
		updatePosition();
	}
	
	

	// modifiers and setters
	
	/**
	 * Changes the Tween's duration
	 * @param duration the number of seconds or frames this Tween will take
	 * @param durationType should be set to SECONDS or FRAMES accordingly
	 */
	public void setDuration( float duration, boolean durationType ){
		isFrameBased = durationType;
		setDuration( duration );
	}
	
	public void setDuration( float duration ){
		this.duration = 1/duration;
		if( !isFrameBased )
			this.duration /= 1000d;
	}
	
	/**
	 * setEasing takes a Shaper and applies it to this tween as the easing function
	 * This can work in a variety of ways:
	 * If the shape is a Shaper object, it uses it's "shape" method
	 * If the shape is a String, it looks for that method within the main PApplet sketch
	 * If the shape is a Class object, it creates a new instance of an assumed Shaper type
	 * @param shape The shaper to be used
	 * @param easeMode The easing mode to be used: IN, OUT, IN_OUT, SEAT
	 */
	public void setEasing( Object shape, int easeMode ){
		setEasing(shape);
		setEasingMode(easeMode);
	}
	
	public void setEasing( Object shape ){
		try{
			if( shape instanceof String ){
				useShapeMethod = true;
				shapeMethod = parent.getClass().getMethod( (String) shape, ARGS );
			}else{
				useShapeMethod = false;
				if( shape instanceof Shaper ){
					shaper = (Shaper) shape;
				}else if( shape instanceof Class ){
					shaper = (Shaper) ((Class) shape).newInstance();
				}else{
					shaper = null;
				}
			}

			if( isTweening )
				updatePosition();

		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the easing mode for this Tween
	 * @param easeMode The easing mode to be used: IN, OUT, IN_OUT, SEAT
	 */
	public void setEasingMode( int easeMode ){
		if( shaper != null )
			shaper.setMode(easeMode);
	}
	
	/**
	 * Removes easing, reverting to a linear relationship between time and position
	 */
	public void noEasing(){
		setEasing( null );
	}
	
	/**
	 * Changes the playmode to this new playmode:
	 * ONCE will animate from beginning to end and then stop
	 * REPEAT will animate from beginning to end and then repeat
	 * REVERSE_ONCE will animate from beginning to end and then from end back to beginning
	 * REVERSE_REPEAT will animate from beginning to end, then end to beginning and repeat
	 */
	public void setPlayMode( int mode ){
		this.mode = mode;
		updatePosition();
	}
	
	/**
	 * Sets the tween to repeat after it has finished playing
	 */
	public void repeat(){
		mode = (mode==REVERSE_ONCE||mode==REVERSE_REPEAT)?REVERSE_REPEAT:REPEAT;
	}
	
	/**
	 * Sets the tween to stop after it has finished playing
	 */
	public void noRepeat(){
		mode = (mode==REVERSE_ONCE||mode==REVERSE_REPEAT)?REVERSE_ONCE:ONCE;
	}

	
	
	//	 getting advanced variables
	
	/**
	 * Returns the Shaper object being used as the easing function
	 */
	public Shaper getEasing(){
		return shaper;
	}
	
	/**
	 * Returns how many times the Tween has played through
	 */
	public int playCount(){
		return playCount;
	}
	
	/**
	 * Returns the speed of the Tween in normalized unit per duration, i.e. returning 1 is standard linear motion
	 */
	public float speed(){
		if( shaper == null )
			return inReverse?-1:1;
		return (inReverse?-1:1) * shaper.slope( (float) time );
	}
	
	/**
	 * Returns the force of the Tween in normalized unit per duration, i.e. returning 0 is no acceleration, such as any linear motion.
	 */
	public float force(){
		if( shaper == null )
			return 0;
		return (inReverse?-1:1) * shaper.secondSlope( (float) time );
	}

	
	
	// Advanced functionality
	
	/**
	 * When you have created a Tween that doesn't use automatic updating you must call this
	 * method every time you want it to be updated, ideally at the beginning of a frame
	 */
	public void tick(){
		
		if( !isTweening )
			return;

		if( inReverse ){
			if( isFrameBased )
				time -= duration * timeScale;
			else
				time -= (System.currentTimeMillis() - lastTime) * duration * timeScale;
		}else{
			if( isFrameBased )
				time += duration * timeScale;
			else
				time += (System.currentTimeMillis() - lastTime) * duration * timeScale;
		}

		if( !isFrameBased )
			lastTime = System.currentTimeMillis();

		if( time >= 1 || time <= 0 ){
			playCount++;

			switch( mode ){
			case REVERSE_REPEAT:
				time = (time>=1?2:0) - time;
				reverse();
				resume();
				break;
			case REPEAT:
				time += time>1?-1:1;
				resume();
				break;
			case REVERSE_ONCE:
				if( playCount == 1){
					time = (time>=1?2:0) - time;
					resume();
				}else
					end();
				reverse();
				break;
			case ONCE:
			default:
				end();
			break;
			}
		}

		updatePosition();
	}
	
	/**
	 * PApplet will call this in order to automate the update process
	 * user will never need to call this method
	 */
	public void pre(){
		tick();
	}
	
	/**
	 * Updates the position of this tween, this keeps the shape function called as few times as possible
	 * by updating the position only when needed
	 */
	private void updatePosition(){
		
		if ( useShapeMethod ) try {
			Object[] args = {new Float(time)};
			position = ((Float) shapeMethod.invoke( parent, args )).floatValue();
			return;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if( shaper == null ){
			position = (float) time;
			return;
		}
			
		position = shaper.shape((float) time);
	}

}
