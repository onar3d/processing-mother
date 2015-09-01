import controlP5.*;
import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress myRemoteLocation;

int fontSize = 18;
ControlP5 cp5;
int myColor = color(0, 0, 0);

ColorPicker cpTop, cpBottom;
Textlabel myTextlabelCP;
Button b;

void setup() {
  size(700, 400);
  noStroke();

  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this, 5432);

  /* myRemoteLocation is a NetAddress. a NetAddress takes 2 parameters,
   * an ip address and a port number. myRemoteLocation is used as parameter in
   * oscP5.send() when sending osc packets to another computer, device, 
   * application. usage see below. for testing purposes the listening port
   * and the port of the remote location address are the same, hence you will
   * send messages back to this sketch.
   */
  myRemoteLocation = new NetAddress("127.0.0.1", 7000);

  cp5 = new ControlP5(this);

  PFont pfont = createFont("Arial", 20, true); // use true/false for smooth/no-smooth
  ControlFont font = new ControlFont(pfont, 241);

  // Add Gradient Button
  b = cp5.addButton("AddGradient")
    .setValue(0)
      .setPosition(10, 10)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Add Gradient")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;

  // Add Rotating Arcs Button
  b = cp5.addButton("AddRotatingArcs")
    .setValue(0)
      .setPosition(10, 41)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Add Rotating Arcs")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;

  // Gradient Top Color controls
  myTextlabelCP = cp5.addTextlabel("topColorLabel")
    .setText("Gradient top color")
      .setPosition(10, 72)
        .setFont(pfont)
          ;

  cpTop = cp5.addColorPicker("topPicker")
    .setPosition(10, 100)
      .setColorValue(color(0, 0, 255, 255))
        ;

  // Gradient Bottom Color controls
  myTextlabelCP = cp5.addTextlabel("botColorLabel")
    .setText("Gradient bottom color")
      .setPosition(10, 163)
        .setFont(pfont)
          ;

  cpBottom = cp5.addColorPicker("botPicker")
    .setPosition(10, 194)
      .setColorValue(color(0, 0, 0, 255))
        ;

  // A slider for the Alpha value of the Arcs
  controlP5.Slider sl = cp5.addSlider("ArcsAlpha")
    .setCaptionLabel("Arcs Alpha")
      .setPosition(10, 276)
        .setSize(255, 20)
          .setRange(0, 255)
            .setValue(255)
              ;

  sl.getCaptionLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  sl.getValueLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  // reposition the Label for controller 'slider'
  cp5.getController("ArcsAlpha").
    getValueLabel().
    align(ControlP5.RIGHT, ControlP5.CENTER).
    setPaddingX(0);

  cp5.getController("ArcsAlpha").
    getCaptionLabel().
    align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).
    setPaddingX(0);

  // A slider for the Scale value of the Arcs
  sl = cp5.addSlider("ArcsScale")
    .setCaptionLabel("Arcs Scale")
      .setPosition(10, 320)
        .setSize(255, 20)
          .setRange(0, 255)
            .setValue(64)
              ;

  sl.getCaptionLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  sl.getValueLabel()
    .setFont(font)
      .toUpperCase(false)
        .setSize(fontSize)
          ;

  // reposition the Label for controller 'slider'
  cp5.getController("ArcsScale").
    getValueLabel().
    align(ControlP5.RIGHT, ControlP5.CENTER).
    setPaddingX(0);

  cp5.getController("ArcsScale").
    getCaptionLabel().
    align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).
    setPaddingX(0);

  // Remove Gradient Button
  b = cp5.addButton("RemoveGradient")
    .setValue(0)
      .setPosition(276, 10)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Remove Gradient")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;

  // Remove Rotating Arcs Button
  b = cp5.addButton("RemoveRotatingArcs")
    .setValue(0)
      .setPosition(276, 41)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Remove Rotating Arcs")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
            
  // Move Rotating Arcs To Top
  b = cp5.addButton("MoveRotatingArcsToTop")
    .setValue(0)
      .setPosition(276, 72)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Move Arcs to Top")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
            
  // Move Rotating Arcs To Bottom
  b = cp5.addButton("MoveRotatingArcsToBottom")
    .setValue(0)
      .setPosition(276, 103)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Move Arcs to Bottom")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
            
  // Query Synth Names
  b = cp5.addButton("QuerySynthNames")
    .setValue(0)
      .setPosition(276, 134)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Query Synth Names")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
  
  // Query Grad Synth supported Messages
  b = cp5.addButton("QuerySupportedMessages")
    .setValue(0)
      .setPosition(276, 165)
        .setSize(255, 30)
          ;

  b.setCaptionLabel("Query Grad Synth Messages")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize)
            ;
}

void draw() {
  background(0);

  fill(myColor);
  //rect(0,280,width,70);
}

public void controlEvent(ControlEvent theEvent) {
  // when a value change from a ColorPicker is received, extract the ARGB values
  // from the controller's array value
  if (theEvent.isFrom(cpTop)) {
    float r = int(theEvent.getArrayValue(0));
    float g = int(theEvent.getArrayValue(1));
    float b = int(theEvent.getArrayValue(2));

    OscMessage myMessage;

    myMessage = new OscMessage("/Mother/Grad_01/TopRed"); 
    myMessage.add(r/255f);
    oscP5.send(myMessage, myRemoteLocation);

    myMessage = new OscMessage("/Mother/Grad_01/TopGreen");
    myMessage.add(g/255f);
    oscP5.send(myMessage, myRemoteLocation);

    myMessage = new OscMessage("/Mother/Grad_01/TopBlue");
    myMessage.add(b/255f);
    oscP5.send(myMessage, myRemoteLocation);
  }
  else if (theEvent.isFrom(cpBottom)) {
    float r = int(theEvent.getArrayValue(0));
    float g = int(theEvent.getArrayValue(1));
    float b = int(theEvent.getArrayValue(2));

    OscMessage myMessage;

    myMessage = new OscMessage("/Mother/Grad_01/BotRed"); 
    myMessage.add(r/255f);
    oscP5.send(myMessage, myRemoteLocation);

    myMessage = new OscMessage("/Mother/Grad_01/BotGreen");
    myMessage.add(g/255f);
    oscP5.send(myMessage, myRemoteLocation);

    myMessage = new OscMessage("/Mother/Grad_01/BotBlue");
    myMessage.add(b/255f);
    oscP5.send(myMessage, myRemoteLocation);
  }
}

void ArcsAlpha(float alpha) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Arcs_01/Alpha"); 
  myMessage.add((int)alpha);
  oscP5.send(myMessage, myRemoteLocation);
}

void ArcsScale(float scale) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Arcs_01/Scale"); 
  myMessage.add((float)scale/64f);
  oscP5.send(myMessage, myRemoteLocation);
}

void MoveRotatingArcsToTop(int theValue) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Move_synth"); 
  myMessage.add("Arcs_01");
  myMessage.add(1);
  oscP5.send(myMessage, myRemoteLocation);
}

void MoveRotatingArcsToBottom(int theValue) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Move_synth"); 
  myMessage.add("Arcs_01");
  myMessage.add(0);
  oscP5.send(myMessage, myRemoteLocation);
}

void QuerySynthNames(int theValue) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Get_synth_names"); 
  oscP5.send(myMessage, myRemoteLocation);
}

void QuerySupportedMessages(int theValue) {
  OscMessage myMessage;
  myMessage = new OscMessage("/Mother/Grad_01/Get_Supported_Messages"); 
  oscP5.send(myMessage, myRemoteLocation);
}

// function AddGradient will receive changes from 
// controller with name AddGradient
public void AddGradient(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Add_synth");
  myMessage.add("Gradient");
  myMessage.add("Grad_01");
  oscP5.send(myMessage, myRemoteLocation);
}

// function AddRotatingArcs will receive changes from 
// controller with name AddRotatingArcs
public void AddRotatingArcs(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Add_synth");
  myMessage.add("RotatingArcs");
  myMessage.add("Arcs_01");
  oscP5.send(myMessage, myRemoteLocation);
}

// function RemoveGradient will receive changes from 
// controller with name RemoveGradient
public void RemoveGradient(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Remove_synth");
  myMessage.add("Grad_01");
  oscP5.send(myMessage, myRemoteLocation);
}

// function RemoveRotatingArcs will receive changes from 
// controller with name RemoveRotatingArcs
public void RemoveRotatingArcs(int theValue) {
  OscMessage myMessage = new OscMessage("/Mother/Remove_synth");
  myMessage.add("Arcs_01");
  oscP5.send(myMessage, myRemoteLocation);
}

/* incoming osc message are forwarded to the oscEvent method. */
void oscEvent(OscMessage theOscMessage) {
  /* print the address pattern and the typetag of the received OscMessage */
  print("### received an osc message.");
  print(" addrpattern: "+theOscMessage.addrPattern());
  println(" typetag: "+theOscMessage.typetag());
}

