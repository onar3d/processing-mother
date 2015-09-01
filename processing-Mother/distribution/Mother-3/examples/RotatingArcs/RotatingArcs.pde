import oscP5.*;
import foetus.*;
import processing.core.*;
import java.util.*;
import processing.opengl.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

/**
 * Geometry by Marius Watz, modified by Ilias Bergstrom.
 * 
 * Visual Synth example, for use with the Mother 1.0 library, both by Ilias Bergstrom.
 * To see what this synth looks like when mixed with another synth, please run the MotherDelivery example,
 * and the MotherControllerExample, and then use the contols in the second to add synths and manipulate their parameters.
 * For more info, read the included "Mother Documentation.pdf" file.
 */

public Foetus f;   // Declare Foetus

FoetusParameter m_Scale, m_Alpha;

float sinLUT[];   // Trig lookup tables borrowed from Toxi. Cryptic but effective
float cosLUT[];

float SINCOS_PRECISION = 3.0f;

int SINCOS_LENGTH = (int) ((360.0 / SINCOS_PRECISION));

boolean dosave = false;   // System data

int num;

float pt[];

int style[];

void setup()
{
  // When run as a synth, setup() is never called!
  // 
  // Put the necessary initialization code in a method named initializeFoetus().
  // The necessary Processing initialization calls size() and frameRate()
  // are called by Mother, and so should be left out from initializeFoetus().
  // Finally, for the synth to work as a processing sketch within the PDE,
  // call initializeFoetus() from within setup().

  size(640, 480, OPENGL);
  frameRate(24);

  initializeFoetus();
}

void initializeFoetus()
{
  noStroke();

  f = new Foetus(this); // Instantiate foetus object here

  m_Scale = new FoetusParameter(f, 1.0f, "/Scale", "f");
  m_Alpha = new FoetusParameter(f, 255, "/Alpha", "i");

  // Fill the tables
  sinLUT=new float[SINCOS_LENGTH];
  cosLUT=new float[SINCOS_LENGTH];
  for (int i = 0; i < SINCOS_LENGTH; i++) {
    sinLUT[i]= (float)Math.sin(i*DEG_TO_RAD*SINCOS_PRECISION);
    cosLUT[i]= (float)Math.cos(i*DEG_TO_RAD*SINCOS_PRECISION);
  }

  num = 150;
  pt = new float[6*num]; // rotx, roty, deg, rad, w, speed
  style = new int[2*num]; // color, render style

  // Set up arc shapes
  int index=0;
  float prob;
  for (int i=0; i<num; i++) {
    pt[index++] = random(PI*2); // Random X axis rotation
    pt[index++] = random(PI*2); // Random Y axis rotation

    pt[index++] = random(60, 80); // Short to quarter-circle arcs
    if (random(100)>90) pt[index]=(int)random(8, 27)*10;

    pt[index++] = int(random(2, 50)*5); // Radius. Space them out nicely

    pt[index++] = random(4, 32); // Width of band
    if (random(100)>90) pt[index]=random(40, 60); // Width of band

    pt[index++] = radians(random(5, 30))/5; // Speed of rotation

    // get colors
    prob = random(100);
    if (prob<30) style[i*2]=colorBlended(random(1), 255, 0, 100, 255, 0, 0, 210);
    else if (prob<70) style[i*2]=colorBlended(random(1), 0, 153, 255, 170, 225, 255, 210);
    else if (prob<90) style[i*2]=colorBlended(random(1), 200, 255, 0, 150, 255, 0, 210);
    else style[i*2]=color(255, 255, 255, 220);

    if (prob<50) style[i*2]=colorBlended(random(1), 200, 255, 0, 50, 120, 0, 210);
    else if (prob<90) style[i*2]=colorBlended(random(1), 255, 100, 0, 255, 255, 0, 210);
    else style[i*2]=color(255, 255, 255, 220);

    style[i*2+1]=(int)(random(100))%3;
  }
}

void draw()
{
  f.startDrawing();
  clear();

  int index = 0;

  if (m_Alpha.getValue() != 0)
  {
    translate(width / 2, height / 2, 0);
    rotateX(PI / 6);
    rotateY(PI / 6);

    scale(m_Scale.getValue());

    for (int i = 0; i < num; i++)
    {
      pushMatrix();

      rotateX(pt[index++]);
      rotateY(pt[index++]);

      if (style[i * 2 + 1] == 0)
      {
        stroke(style[i * 2], m_Alpha.getValue());
        noFill();
        strokeWeight(1);
        arcLine(0, 0, pt[index++], pt[index++], pt[index++]);
      } 
      else if (style[i * 2 + 1] == 1)
      {
        fill(style[i * 2], m_Alpha.getValue());
        noStroke();
        arcLineBars(0, 0, pt[index++], pt[index++], pt[index++]);
      } 
      else
      {
        fill(style[i * 2], m_Alpha.getValue());
        noStroke();
        arc(0, 0, pt[index++], pt[index++], pt[index++]);
      }

      // increase rotation
      pt[index - 5] += pt[index] / 10;
      pt[index - 4] += pt[index++] / 20;

      popMatrix();
    }
  }

  f.endDrawing();
}


// Get blend of two colors
int colorBlended(float fract, float r, float g, float b, float r2, float g2, float b2, float a)
{

  r2 = (r2 - r);
  g2 = (g2 - g);
  b2 = (b2 - b);
  return color(r + r2 * fract, g + g2 * fract, b + b2 * fract, a);
}

// Draw arc line
void arcLine(float x, float y, float deg, float rad, float w)
{
  int a = (int) (min(deg / SINCOS_PRECISION, SINCOS_LENGTH - 1));
  int numlines = (int) (w / 2);

  for (int j = 0; j < numlines; j++)
  {
    beginShape();
    for (int i = 0; i < a; i++)
    {
      vertex(cosLUT[i] * rad + x, sinLUT[i] * rad + y);
    }
    endShape();
    rad += 2;
  }
}

// Draw arc line with bars
void arcLineBars(float x, float y, float deg, float rad, float w)
{
  int a = (int) ((min(deg / SINCOS_PRECISION, SINCOS_LENGTH - 1)));
  a /= 4;

  beginShape(QUADS);

  for (int i = 0; i < a; i += 4)
  {
    vertex(cosLUT[i] * (rad) + x, sinLUT[i] * (rad) + y);
    vertex(cosLUT[i] * (rad + w) + x, sinLUT[i] * (rad + w) + y);
    vertex(cosLUT[i + 2] * (rad + w) + x, sinLUT[i + 2] * (rad + w) + y);
    vertex(cosLUT[i + 2] * (rad) + x, sinLUT[i + 2] * (rad) + y);
  }

  endShape();
}

// Draw solid arc
void arc(float x, float y, float deg, float rad, float w)
{
  int a = (int) (min(deg / SINCOS_PRECISION, SINCOS_LENGTH - 1));
  beginShape(QUAD_STRIP);
  for (int i = 0; i < a; i++)
  {
    vertex(cosLUT[i] * (rad) + x, sinLUT[i] * (rad) + y);
    vertex(cosLUT[i] * (rad + w) + x, sinLUT[i] * (rad + w) + y);
  }
  endShape();
}

/**
 * This method is called when an OSC message is received by the synth.
 */
void oscEvent(OscMessage theOscMessage)
{
}
