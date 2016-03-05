#include <LiquidCrystal.h>
LiquidCrystal lcd(2,3,4,5,6,7);

class Motor {
  private:
    int pin1, pin2;
    int en1, en2;
    int d=4, d1=3;
  public:  
    Motor(int Pin1, int Pin2, int En1, int En2) {
      pin1 = Pin1;
      pin2 = Pin2;
      en1 = En1;
      en2 = En2;
      pinMode(pin1, OUTPUT);
      pinMode(pin2, OUTPUT);
      pinMode(en1, OUTPUT);
      pinMode(en2, OUTPUT);
    }  

    void enable() {
      digitalWrite(en1,1);
      digitalWrite(en2,1);
    }

    void disable() {
      digitalWrite(en1,0);
      digitalWrite(en2,0);
    }
    
    void Step(bool dir, int val) {
      int i=0;
      while(i!=val) {
        if(dir==1) {
          digitalWrite(pin1,0);
          digitalWrite(pin2,0);
          delay(d);
          digitalWrite(pin1,0);
          digitalWrite(pin2,1);
          delay(d);
          digitalWrite(pin1,1);
          digitalWrite(pin2,1);
          delay(d);
          digitalWrite(pin1,1);
          digitalWrite(pin2,0);
          delay(d);
        }
        else {
          digitalWrite(pin1,1);
          digitalWrite(pin2,0);
          delay(d);
          digitalWrite(pin1,1);
          digitalWrite(pin2,1);
          delay(d);
          digitalWrite(pin1,0);
          digitalWrite(pin2,1);
          delay(d);
          digitalWrite(pin1,0);
          digitalWrite(pin2,0);
          delay(d);
        }
        i++;
      }
    }  
};

const int N = 64;
char data[N*N];
int posZ = 0, posX = 0, posY = 0;

Motor mY(30,31,32,33);
Motor mZ(34,35,36,37);
Motor mX(38,39,40,41);

void setup() {
  pinMode(A0, INPUT);
  pinMode(A1, INPUT);
  pinMode(A2, INPUT);
  
  Serial.begin(115200);
  
  lcd.begin(1, 2);
  lcd.setCursor(0, 0);
  lcd.print("PixelPrinter");
  
  reset();
}

void loop() {
  checkSerial();
}

void reset() {
  mX.enable();
  mY.enable();
  mZ.enable();
  while(digitalRead(A0)!=1) 
    mZ.Step(1,1);
  while(digitalRead(A1)!=1) 
    mY.Step(1,1);
  while(digitalRead(A2)!=1) 
    mX.Step(1,1);  
  posZ = 0;
  posX = 0;
  posY = 0;
}

int checkSerial() {

  if(Serial.available()>0) {
    if(Serial.peek()=='0' || Serial.peek()=='1') {
      Serial.readBytes(data, N*N);
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Data recieved.");
      lcd.setCursor(0, 1);
      lcd.print("Ready to print!");
    } 
    else if(Serial.peek()=='P') {
      Serial.read();
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Start printing");
      delay(500);
      Print();
    }
    else if(Serial.peek()=='z') {
      Serial.read();
      lcd.clear();
      lcd.setCursor(0, 0);
      if(posZ > 0) {
        mZ.Step(1,1);
        posZ--;
        lcd.print("Z: ");
        lcd.print(posZ);
      }
      else lcd.print("min Z");
    }
    else if(Serial.peek()=='Z') {
      Serial.read();
      lcd.clear();
      lcd.setCursor(0, 0);
      if(posZ < 28) {
        mZ.Step(0,1);
        posZ++;
        lcd.print("Z: ");
        lcd.print(posZ);
      }
      else lcd.print("max Z"); 
    }
    else if(Serial.peek()=='S') {
      Serial.read();
      lcd.clear();
      lcd.setCursor(1, 0);
      lcd.print("Stopped");
      reset();
      return 1;
    }
    else Serial.read();
  }
  return 0;
}

void Print() {
  int count = 0, row = 1;
  int percent = 0;
  
  while(digitalRead(A1) != 1) 
    mY.Step(1,1);
  while(digitalRead(A2) != 1) 
    mX.Step(1,1);  

  dispProgress(percent);
  
  for(int i=0; i<(N*N); i++) {
    count++;
    
    if(data[i]=='1') 
      Click();
    if(count!=N)
      mX.Step(0,1);
      
    if(count==N) {
      count = 0;
      mY.Step(0,1);
      mX.Step(1,N);
    }

    if(i == 41*(row)){     
      row++;
      dispProgress(++percent);  
    }
    if(checkSerial()==1) 
      break;
  }
  reset();
}

void Click() {
  mZ.Step(0,2);
  mZ.Step(1,2);
}

void dispProgress(int percent) {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Printing...");
  lcd.setCursor(0, 1);
  lcd.print(percent);
  lcd.print("%");
  if(percent==100) {
    lcd.setCursor(0, 0);
    lcd.clear();
    lcd.print("Done!");
    lcd.setCursor(0, 1);
    lcd.print(percent);
    lcd.print("%");
  }
} 
