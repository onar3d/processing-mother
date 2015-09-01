/*
Copyright 2008 - 2014 Ilias Bergstrom.
  
This file is part of "Mother".

Mother is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Foobar is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Mother.  If not, see <http://www.gnu.org/licenses/>.
 
onar3d@hotmail.com, www.onar3d.com
 
*/

package foetus;

import processing.core.*;
//import processing.core.PApplet.RegisteredMethods;

import java.util.*;

import megamu.shapetween.Tween;

public class Foetus {
	/**
	 * For use by Mother, do not alter!
	 */
	public boolean standalone = true;

	public ArrayList<PGraphics> olderSiblings;
	
	public PGraphics incoming;
	public PGraphics outgoing;
	
	public final String VERSION = "1.5.0";
	
	PApplet parent;
	
	int[] m_BGColor;
	
	Hashtable<String, String> 	m_Messages;
	Hashtable<String, Boolean> 	m_Updating;
	ArrayList<FoetusParameter> 	m_Parameters;
	
	float m_SpeedFraction = 1;
	
	PGraphics old_g = null;
	
	static float m_MaxAnimationDuration = 3000.0f;

	FoetusParameter		m_Alpha;
	int					m_BlendMode = 1;
	
	float m_Xmag, m_Ymag = 0;
	boolean m_CubeEnabled = false;
	public PGraphics m_CubePG = null;
	
	public ArrayList<FoetusParameter> getParameters() { return m_Parameters; }
	
	public float GetMaxAnimationDuration() { return m_MaxAnimationDuration; }
	public void  SetMaxAnimationDuration(float maxAnimationDuration) { m_MaxAnimationDuration = maxAnimationDuration; }

	public float 	GetAlpha() 				{ return m_Alpha.getValue(); }
	public void 	SetAlpha(float a) 		{ m_Alpha.setValue(a); }
		
	public int 		GetBlendMode()			{ return m_BlendMode; }
	public void		SetBlendMode(int mode)	{ m_BlendMode = mode; }
	
	public boolean	GetCubeEnabledWhenStandalone() { return m_CubeEnabled; }
	
	/**
	 * Constructor
	 * @param parent
	 */
	public Foetus(PApplet parent, boolean cubeEnabled) {
		m_CubeEnabled = cubeEnabled;
		
		Construct(parent);	
	}
		
	public Foetus(PApplet parent) {
		m_CubeEnabled = false;
		Construct(parent);			
	}
	
	private void Construct(PApplet parent) {
		this.parent = parent;
		
		m_Parameters = new ArrayList<FoetusParameter>();
		
		olderSiblings = new ArrayList<PGraphics>();
		
		m_Messages = new Hashtable<String,String>();
		
		m_Updating = new Hashtable<String,Boolean>();
				
		parent.registerMethod("dispose", this);
		parent.registerMethod("pre", this);
		parent.registerMethod("post", this);
		parent.registerMethod("draw", this);
				
		m_BGColor = new int[] {128, 128, 128};
		
		m_Alpha = new FoetusParameter(this, 1.0f, "/SetAlpha", "f" );
		
		if(m_CubeEnabled) {
			m_CubePG = parent.createGraphics(	parent.width, 
					parent.height, 
					parent.OPENGL);
		}
	}
	
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public String version()	{
		return VERSION;
	}

	/**
	 * For use by Mother, do not alter!
	 * @param inSF
	 */
	public void setSpeedFraction(float inSF) {
		m_SpeedFraction = inSF; 
		Tween.timeScale = 1f/m_SpeedFraction;
	}
	
	/**
	 * For use by Mother!
	 * @param inSF
	 */
	public float getSpeedFraction() {
		return m_SpeedFraction; 
	}
	
	/**
	 * millis() is the same as the Processing millis() function, 
	 * with the additional feature that it takes the specified speed fraction into account. 
	 * This is useful when running in non real-time mode, 
	 * as the f.millis() call returns the time value at a given frame number 
	 * that it would have if it were running in real-time. 
	 * @return int
	 */
	public int millis()	{
		double pm = parent.millis();
	    return (int)(pm/m_SpeedFraction);
	}
	
	/**
	 * Use this to set the background color of the sketch when it is running in standalone mode,
	 * i.e. when it is not hosted by Mother.
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setBGColor(int r, int g, int b)	{
		m_BGColor[0] = r;
		m_BGColor[1] = g;
		m_BGColor[2] = b;
	}
	
	public void dispose() {
//		System.out.println("Dispose: " + parent.toString());
	}

	public void draw() {
//		System.out.println("draw: " + parent.toString());
	}
	
	/**
	 * Use this method to register your sketches methods with Mother. This means they will 
	 * then be listed when the sketch is asked for a list of its capabilities.
	 * @param OSC address
	 * @param OSC typetag
	 */
	public void registerMethod(String address, String typetag) {
		m_Messages.put(address, typetag);
		m_Updating.put(address, false);
	}

	/**
	 * For use by Mother!
	 * @return
	 */
	public void unregisterMethod(String address) {
		m_Messages.remove(address);
		m_Updating.remove(address);
	}
	
	/**
	 * For use by Mother!
	 * @return
	 */
	public Hashtable<String,String> getSupportedMessages() {
		return m_Messages;
	}
	
	/**
	 * For use by Mother, do not alter!
	 * @return
	 */
	public void setUpdatingStatus(String address, boolean status) {
		synchronized(m_Updating) {
			m_Updating.put(address, status);
		}
	}
	
	/**
	 * For use by Mother!
	 * @return
	 */
	public boolean getUpdatingStatus()	{
		synchronized(m_Updating) {
			if(m_Updating.containsValue(true)) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * For use by Mother!
	 * @return
	 */
	public void pre() {
		if (standalone) {
			parent.background(m_BGColor[0],m_BGColor[1],m_BGColor[2]);
			
			if(m_CubeEnabled) {
				drawCube();
				
				incoming = m_CubePG;
			}
			
			outgoing = parent.g;
		}
		
//		System.out.println("Pre: " + parent.toString());
	}
	
	/**
	 * For use by Mother!
	 * @return
	 */
	public void post() {
//		System.out.println("Post: " + parent.toString());
	}
	
	public void addParameter(FoetusParameter f)	{
		m_Parameters.add(f);	
	}
	
	public void startDrawing() {		
		if(!standalone) {
			old_g = parent.g;
			outgoing.beginDraw();
			parent.g = outgoing;
		}
//		parent.g.clear();
	}
	
	public void endDrawing() {
		if(!standalone) {
			outgoing.endDraw();
			parent.g = old_g;
			old_g = null;
		}
	}
	
	public void drawCube()  { 
		m_CubePG.beginDraw();
		
		m_CubePG.noStroke();
		m_CubePG.clear();
		  
		m_CubePG.pushMatrix();
		 
		m_CubePG.translate(m_CubePG.width/2, m_CubePG.height/2, -30); 
				  
		m_Ymag+= 0.005 * m_CubePG.TWO_PI;
		m_Xmag+= 0.005 * m_CubePG.TWO_PI;
		  
		m_CubePG.rotateX(-m_Ymag); 
		m_CubePG.rotateY(-m_Xmag); 
		  
		m_CubePG.scale(90);
		m_CubePG.beginShape(m_CubePG.QUADS);

		m_CubePG.fill(255, 0, 0); m_CubePG.vertex(-1,  1,  1);
		m_CubePG.fill(255, 0, 0); m_CubePG.vertex( 1,  1,  1);
		m_CubePG.fill(255, 0, 0); m_CubePG.vertex( 1, -1,  1);
		m_CubePG.fill(255, 0, 0); m_CubePG.vertex(-1, -1,  1);

		m_CubePG.fill(0, 0, 255); m_CubePG.vertex( 1,  1,  1);
		m_CubePG.fill(0, 0, 255); m_CubePG.vertex( 1,  1, -1);
		m_CubePG.fill(0, 0, 255); m_CubePG.vertex( 1, -1, -1);
		m_CubePG.fill(0, 0, 255); m_CubePG.vertex( 1, -1,  1);

		m_CubePG.fill(0, 255, 0); m_CubePG.vertex( 1,  1, -1);
		m_CubePG.fill(0, 255, 0); m_CubePG.vertex(-1,  1, -1);
		m_CubePG.fill(0, 255, 0); m_CubePG.vertex(-1, -1, -1);
		m_CubePG.fill(0, 255, 0); m_CubePG.vertex( 1, -1, -1);

		m_CubePG.fill(0, 255, 255); m_CubePG.vertex(-1,  1, -1);
		m_CubePG.fill(0, 255, 255); m_CubePG.vertex(-1,  1,  1);
		m_CubePG.fill(0, 255, 255); m_CubePG.vertex(-1, -1,  1);
		m_CubePG.fill(0, 255, 255); m_CubePG.vertex(-1, -1, -1);

		m_CubePG.fill(255, 255, 0); m_CubePG.vertex(-1,  1, -1);
		m_CubePG.fill(255, 255, 0); m_CubePG.vertex( 1,  1, -1);
		m_CubePG.fill(255, 255, 0); m_CubePG.vertex( 1,  1,  1);
		m_CubePG.fill(255, 255, 0); m_CubePG.vertex(-1,  1,  1);

		m_CubePG.fill(255, 255, 255); m_CubePG.vertex(-1, -1, -1);
		m_CubePG.fill(255, 255, 255); m_CubePG.vertex( 1, -1, -1);
		m_CubePG.fill(255, 255, 255); m_CubePG.vertex( 1, -1,  1);
		m_CubePG.fill(255, 255, 255); m_CubePG.vertex(-1, -1,  1);

		m_CubePG.endShape();
		  
		m_CubePG.popMatrix();
		
		m_CubePG.endDraw();
	}
}