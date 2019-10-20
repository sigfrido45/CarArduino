#include <SoftwareSerial.h>
#include <Servo.h>
SoftwareSerial BT(2, 3);
int izqA = 5;
int izqB = 6;
int derA = 9;
int derB = 10;
int tiempo = 0;
Servo servoMotor;

void setup()
{
    Serial.begin(9600);
    pinMode(derA, OUTPUT);
    pinMode(derB, OUTPUT);
    pinMode(izqA, OUTPUT);
    pinMode(izqB, OUTPUT);
    BT.begin(9600);
    servoMotor.attach(12);
    servoMotor.write(0);
}

void loop()
{
    if (BT.available())
    {
        Serial.println("hola bt read");
        char data = (char)BT.read();
        Serial.println(data);

        if (data == 'f') { // delante
            digitalWrite(derA, HIGH);
            digitalWrite(derB, LOW);
            digitalWrite(izqA, HIGH);
            digitalWrite(izqB, LOW);
            tiempo = 0;
        }
        if (data == 'r') { // retroceso
            digitalWrite(derA, LOW);
            digitalWrite(derB, HIGH);
            digitalWrite(izqA, LOW);
            digitalWrite(izqB, HIGH);
            tiempo = 0;
        }
        if (data == 'd') { // derecha
            digitalWrite(derA, HIGH);
            digitalWrite(derB, LOW);
            digitalWrite(izqA, LOW);
            digitalWrite(izqB, LOW);
            tiempo = 0;
        }
        if (data == 'i') { // izquierda
            digitalWrite(derA, LOW);
            digitalWrite(derB, LOW);
            digitalWrite(izqA, HIGH);
            digitalWrite(izqB, LOW);
            tiempo = 0;
        }

        if(data == 's') { //subir cuchillas
            moverServoMotor(90);
        }
        if(data == 'b') { //bajar cuchillas
            moverServoMotor(0);
        }
    }

    if (tiempo < 200) {
        tiempo++;
    }
    else {
        digitalWrite(derA, LOW);
        digitalWrite(izqA, LOW);
        digitalWrite(derB, LOW);
        digitalWrite(izqB, LOW);
    }
    delay(1);
}

void moverServoMotor(int grado){
  servoMotor.write(grado);
}