import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PixelPaint extends PApplet {



int dotColor = color(249,249,204);
int pressedDotColor = color(34,102,116);
int bColorStroke = color(110,110,110);
int bColorFill = color(50,50,50);
int bTextColor = color(255,255,255);
int pressBColorStroke = color(164,164,21);
int pressBColorFill = color(36,36,36);
int pressBTextColor = color(164,164,21);

class Button 
{
    int xpos;
    int ypos;
    int Width;
    int Height;
    String name = "";
    
    Button(int X, int Y, int _Width, int _Height, String Name) {
      xpos = X;
      ypos = Y;
      name = Name;
      Width = _Width;
      Height = _Height;
      Draw1();
    }

    public void Draw1() {
      fill(bColorFill);
      stroke(bColorStroke);
      rect(xpos, ypos, Width, Height);
      textSize(16);
      textAlign(CENTER,CENTER);
      fill(bTextColor);
      text(name, Width/2+xpos, Height/2+ypos);
    }
    
    public void Draw2() {
      fill(pressBColorFill);
      stroke(pressBColorStroke);
      rect(xpos, ypos, Width, Height);
      textSize(16);
      textAlign(CENTER,CENTER);
      fill(pressBTextColor);
      text(name, Width/2+xpos, Height/2+ypos);
    }
    
    public boolean check() {
      if(mouseButton==LEFT && mouseX>xpos && 
         mouseX<xpos+Width && mouseY>ypos && mouseY<ypos+Height)
      {
        Draw2();
        return true;
      }
      else {
        Draw1(); 
        return false;
      }
    }
    
    public boolean checkS() {
      if(checkSerial && mouseButton==LEFT && mouseX>xpos && 
         mouseX<xpos+Width && mouseY>ypos && mouseY<ypos+Height)
      {
        Draw2();
        return true;
      }
      else {
        Draw1(); 
        return false;
      }
    }
};

class Dot
{
    int xpos;
    int ypos;
    int pressed;

    Dot(int X, int Y) {
      xpos = X;
      ypos = Y;
      Draw1();
      pressed = 0; 
    }

    public void Draw1() {
      stroke(dotColor);
      fill(dotColor);
      rect(xpos, ypos, 8, 8);
      pressed = 0;
    }
    
    public void Draw2() {
      stroke(pressedDotColor);
      fill(pressedDotColor);
      rect(xpos, ypos, 8, 8);
      pressed = 1;
    }
  
};             /////////////////////////////////////////////////////////////////////////////////////////
final int N = 62; 
int div=10;
char[] data = new char[3845];
int colr1 = color(76, 126, 144);
int colr2 = color(34, 102, 102);
int colr3 = color(10, 47, 52);
int posX=2, posY=2;
boolean DrawMat=false, checkSerial=false;
String imgPath, imgPathTemp = null;
String saveDir, saveDirTemp = null;
float brightness = 0.5f, brightnessTemp = 0.5f;

Dot[][] dot;
Button clear,
       fill,
       invert,
       open,
       save,
       print,
       random,
       connect,
       send, 
       zplus, 
       zminus, 
       stop,
       brPlus,
       brMinus;

PImage inpImg = createImage(248, 248, RGB),
       chdImg = createImage(62, 62, RGB),
       bgrImg = createImage(248, 248, RGB);
Serial port;

public void setup() {                                                     //SETUP  
  
  
  bgrImg.loadPixels();
  for(int i=0; i<248; i++) 
    for(int j=0; j<248; j++) 
      bgrImg.pixels[j+i*248] = color(255,255,255);
  bgrImg.updatePixels();
  
  surface.setTitle("PixelPaint");
  dot = new Dot[N][N];
  for(int i=0; i<N; i++) {
    for(int j=0; j<N; j++) {
      dot[i][j] = new Dot(i*10+150, j*10+20);
    }
  }
  Background();
  Coordinates();
  Buttons();
  drawMat();
}

public void drawMat() {
  for(int i=0; i<N; i++) {
    for(int j=0; j<N; j++) {
      if(dot[j][i].pressed==0) {
        dot[j][i].Draw1();
      } else dot[j][i].Draw2();
    }
  } 
}

public void mousePressed() {
  buttonsCheck();
}

public void buttonsCheck() {
  clear.check();
  fill.check();
  random.check();
  invert.check();
  connect.check();
  open.check();
  save.check();
  send.check();
  print.check();
  zplus.check();
  zminus.check();
  stop.check();
  brPlus.check();
  brMinus.check();
}

public void mouseReleased() {
  Clear();
  Fill();
  Random();
  Invert();
  Connect();
  Open();
  Save();
  Print();
  Send();
  zplus();
  zminus();
  Stop();
  brPlus();
  brMinus();
}

public void Buttons() {
  clear = new Button(14,15,110,30,"clear");
  fill = new Button(14,50,110,30,"fill");
  random = new Button(14,85,110,30,"random");
  invert = new Button(14,120,110,30,"invert");
  open = new Button(14,180,110,30,"open");
  save = new Button(14,215,110,30,"save");
  connect = new Button(14,275,110,30,"connect");
  send = new Button(14,310,110,30,"send");
  print = new Button(14,345,110,30,"print");
  stop = new Button(14,380,110,30,"stop");
  zplus = new Button(24,415,40,30,"+Z");
  zminus = new Button(74,415,40,30,"-Z");
  brPlus = new Button(14,500,110,30,"brness+");
  brMinus = new Button(14,535,110,30,"brness-");
}

public void Clear() {
  if(clear.check()) {
    for(int i=0; i<N; i++) {
      for(int j=0; j<N; j++) {
        dot[i][j].Draw1();
      }
    }
    clear.Draw1();
  }
}

public void Fill() {
  if(fill.check()) {
    for(int i=0; i<N; i++) {
      for(int j=0; j<N; j++) {
        dot[i][j].Draw2();
      }
    }
    fill.Draw1();
  }
}

public void Random() {
  if(random.check()) {
    for(int i=0; i<N; i++) {
      for(int j=0; j<N; j++) {
        int Rand = PApplet.parseInt(random(0,2));
        if(Rand==1) dot[i][j].Draw2();
        else dot[i][j].Draw1();
      }
    }
    random.Draw1();
  }
}

public void Invert() {
  if(invert.check()) {
    for(int i=0; i<N; i++) {
      for(int j=0; j<N; j++) {
        if(dot[i][j].pressed == 1) {
          dot[i][j].pressed = 0;
          dot[i][j].Draw1();
        } else { 
          dot[i][j].pressed = 1;
          dot[i][j].Draw2();
        }  
      }
    }
    invert.Draw1();
  }
}

public void Connect() {                                    ///////////////CONNECT
  if(connect.check()) {
    try {
      //println(Serial.list());
      port = new Serial(this, Serial.list()[1], 9600);
      checkSerial = true;
    }
    catch(Exception e) {}
    connect.Draw1();
  }
}

public void makeDataArr() {
  int count = 0;
  for(int j=0; j<N; j++) {
    for(int i=0; i<N; i++) {
       if(dot[i][j].pressed == 1) {
         data[count] = '1'; 
         count++;
       }
       if(dot[i][j].pressed == 0) {
         data[count] = '0'; 
         count++;
       }  
     }
   }
   data[3844] = 'E';
}

public void Send() {                                                       ////SEND
  makeDataArr();
  if(send.checkS()) {
    for(int i=0; i<3845; i++) {
      port.write(data[i]);
    } 
  }
  send.Draw1();
}

public void Print() {
  if(print.checkS()) {
    port.write('P');
    print.Draw1();
  }
}

public void zminus() {
  if(zminus.checkS()) {
    port.write('z');
    zminus.Draw1();
  }
}

public void zplus() {
  if(zplus.checkS()) {
    port.write('Z');
    zplus.Draw1();
  }
}

public void Stop() {
  if(stop.checkS()) {
    port.write('S');
    stop.Draw1();
  }
}

public void brPlus() {     /////////////BRIGHTNESS
  if(brPlus.check()) 
    if(brightness<1)
      brightness += 0.05f;
  brPlus.Draw1();
}

public void brMinus() {
  if(brMinus.check())
    if(brightness>0)
      brightness -= 0.05f;
  brMinus.Draw1();
}

public void Save() {
  if(save.check()) {
    selectOutput("Select a folder:", "folderSelected");
    save.Draw1();
  }
}

public void folderSelected(File selection) {
  if (selection == null) {
  } else {
    saveDir = selection.getAbsolutePath() + ".jpg";
  }
}

public void Saving() {
    chdImg.loadPixels();
    for(int i=0; i<N; i++) {
      for(int j=0; j<N; j++) {
        if(dot[j][i].pressed==0) {          
          chdImg.pixels[j+i*N] = color(255,255,255);
        } else chdImg.pixels[j+i*N] = color(0,0,0);
      }
    }
    chdImg.updatePixels();
    chdImg.save(saveDir);
}

public void Open() {
  if(open.check()) {
    selectInput("Select a file:", "ImgSelected");
    open.Draw1();
  }
}

public void ImgSelected(File selection) {
  if (selection == null) {
  } else {
    imgPath = selection.getAbsolutePath();
  }
}

public void drawImg() {
  
  image(bgrImg, 800, 19);
  
  if(imgPath!=null) 
    inpImg = loadImage(imgPath);
    
  if(inpImg.width > inpImg.height) 
    inpImg.resize(0,248);
  else inpImg.resize(248,0);
  
  image(inpImg, 800, 19);
  chdImg = get(800,19,248,248);
  Background();
  Coordinates();
  image(chdImg, 800, 19);  /////////Y390
  chdImg.resize(62,62);
  chdImg.filter(THRESHOLD, brightness);
  chdImg.loadPixels();
  
  for(int i=0; i<N; i++) {
    for(int j=0; j<N; j++) {
      if(chdImg.pixels[j+i*N] == -1) {
        dot[j][i].pressed=0;
      } else dot[j][i].pressed=1;
    }
  }
  drawMat();
  buttonsCheck();
}

public void draw() {
  
  if(imgPath!=imgPathTemp) {
    imgPathTemp = imgPath;
    brightness = 0.5f;
    drawImg();
  }
  
  if(saveDir!=saveDirTemp) {
    saveDirTemp = saveDir;
    Saving();
  }
  
  if(brightness!=brightnessTemp) {
    brightnessTemp = brightness;
    drawImg();
  }
  
  if(mouseX>150 && mouseX<770 && mouseY>20 && mouseY<640)
    Coordinates();
    
  posX=(mouseX)/div-15;
  posY=(mouseY)/div-2;

  if(mousePressed && (mouseButton==LEFT) && mouseX>150 
    && mouseX<770 && mouseY>20 && mouseY<640) {
    dot[posX][posY].Draw2();
    dot[posX][posY].pressed=1;
  }
  if(mousePressed && (mouseButton==RIGHT) && mouseX>150 
    && mouseX<770 && mouseY>20 && mouseY<640) {
    dot[posX][posY].Draw1();
    dot[posX][posY].pressed=0;  
  }
  
}

public void Background() {
  int rectY=10, rectX=10;
  background(colr3); 
  noStroke();
  for(int i=0; i<64; i++) {
    for(int j=0; j<94; j++) {
      fill(10,random(55,70),52);
      rect(rectX+random(10), rectY+random(10), 7, 7);
      rectX=rectX+10+PApplet.parseInt(random(15));
    }
    rectY=rectY+10+PApplet.parseInt(random(15));
    rectX=10;
  } 
  stroke(179,237,237);
  fill(89,89,89); 
  rect(9, 10, 120, 640);
  
  fill(0, 84, 84);
  stroke(179, 237, 237);
  rect(139, 9, 640, 640);
  
  fill(100);
  textSize(12);
  text("PixelPaint v2.2 2016", 360, 675);          //////////////////////version
  
  fill(0, 84, 84);
  stroke(179, 237, 237);
  rect(790, 9, 268, 268);
}

public void Coordinates() {
  String coordX, coordX_buff="", coordY, coordY_buff="";
  if(posX>0 && posY>0) {
    coordX = "x:" + (posX);
    coordY = "y:" + (posY);
  } else {
    coordX = "x: 0";
    coordY = "y: 0";
  }

  if(coordX_buff!=coordX || coordY_buff!=coordY) {
    fill(249,249,204);
    stroke(colr1);
    //stroke(179, 237, 237);
    rect(12 ,624, 114, 23);
    coordX_buff=coordX;
    coordY_buff=coordY;
  }
  fill(colr2);
  textAlign(LEFT,CENTER);
  textSize(15);
  text(coordX,20,634);
  text(coordY,75,634);
}
  public void settings() {  size(1070, 685); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PixelPaint" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
