/*********************************************************************
This is an example for our Monochrome OLEDs based on SSD1306 drivers

  Pick one up today in the adafruit shop!
  ------> http://www.adafruit.com/category/63_98

This example is for a 128x64 size display using SPI to communicate
4 or 5 pins are required to interface

Adafruit invests time and resources providing this open source code, 
please support Adafruit and open-source hardware by purchasing 
products from Adafruit!

Written by Limor Fried/Ladyada  for Adafruit Industries.  
BSD license, check license.txt for more information
All text above, and the splash screen must be included in any redistribution
*********************************************************************/

#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
// библиотека для работы с модулями IMU
#include <TroykaIMU.h>
#include <MahonyAHRS.h>

// If using software SPI (the default case):
#define OLED_MOSI 9
#define OLED_CLK 10
#define OLED_DC 11
#define OLED_CS 12
#define OLED_RESET 13
Adafruit_SSD1306 display(OLED_MOSI, OLED_CLK, OLED_DC, OLED_RESET, OLED_CS);


double fXg = 0;
double fYg = 0;
double fZg = 0;

/* Uncomment this block to use hardware SPI
#define OLED_DC     6
#define OLED_CS     7
#define OLED_RESET  8
Adafruit_SSD1306 display(OLED_DC, OLED_RESET, OLED_CS);
*/

// множитель фильтра
#define BETA 0.2f
 
// создаём объект для фильтра Madgwick
Madgwick filter;
 
// создаём объект для работы с акселерометром
Accelerometer accel;
// создаём объект для работы с гироскопом
Gyroscope gyro;
 
// переменные для данных с гироскопов, акселерометров
float gx, gy, gz, ax, ay, az;
 
// получаемые углы ориентации
float yaw = 0, pitch=0, roll=0;
 
// переменная для хранения частоты выборок фильтра
float fps = 50;

String a,b;


char* msg;

#define NUMFLAKES 10
#define XPOS 0
#define YPOS 1
#define DELTAY 2

#define LOGO16_GLCD_HEIGHT 16
#define LOGO16_GLCD_WIDTH 16
static const unsigned char PROGMEM logo16_glcd_bmp[] =
    {B00000000, B11000000,
     B00000001, B11000000,
     B00000001, B11000000,
     B00000011, B11100000,
     B11110011, B11100000,
     B11111110, B11111000,
     B01111110, B11111111,
     B00110011, B10011111,
     B00011111, B11111100,
     B00001101, B01110000,
     B00011011, B10100000,
     B00111111, B11100000,
     B00111111, B11110000,
     B01111100, B11110000,
     B01110000, B01110000,
     B00000000, B00110000};

#if (SSD1306_LCDHEIGHT != 64)
#error("Height incorrect, please fix Adafruit_SSD1306.h!");
#endif

void setup()
{
  Serial.begin(9600);

   accel.begin();
  // инициализация гироскопа
  gyro.begin();

  // by default, we'll generate the high voltage from the 3.3v line internally! (neat!)
  display.begin(SSD1306_SWITCHCAPVCC);
  // init done

  // Show image buffer on the display hardware.
  // Since the buffer is intialized with an Adafruit splashscreen
  // internally, this will display the splashscreen.
  display.display();
  delay(2000);

  // Clear the buffer.
  display.clearDisplay();

  // text display tests
  //print_text("qqqqqq");
}

void loop()
{
  // запоминаем текущее время
  unsigned long startMillis = millis();
  // считываем данные с акселерометра в единицах G
  accel.readGXYZ(&ax, &ay, &az);
  // считываем данные с акселерометра в радианах в секунду
  gyro.readRadPerSecXYZ(&gx, &gy, &gz);
  
  //filter.setKoeff(fps, BETA);
  // обновляем входные данные в фильтр
  //filter.update(gx, gy, gz, ax, ay, az);
 
  // получение углов yaw, pitch и roll из фильтра
  // yaw =  filter.getYawDeg();
  // pitch = filter.getPitchDeg();
  // roll = filter.getRollDeg();
 
  // выводим полученные углы в serial-порт
  // Serial.print("yaw: ");
  // Serial.print(yaw);
  // Serial.print("\t\t");
  // Serial.print("pitch: ");
  // Serial.print(pitch);
  // Serial.print("\t\t");
  // Serial.print("roll: ");
  // Serial.println(roll);


  //Serial.println(roll);


  update();
  //filter.updateIMU(gx,gy,gz,ax,ay,az);

  a = String((int)(abs(roll)));
  b = String((int)(abs(pitch)));

  Serial.print(a);
  Serial.print(" ");
  Serial.println(b);

  //Serial.println(pitch);
  print_text(a+"   "+b);
 
  // вычисляем затраченное время на обработку данных
  //unsigned long deltaMillis = millis() - startMillis;
  // вычисляем частоту обработки фильтра
  //fps = 1000 / deltaMillis;
  delay(fps);
}

void print_text(String s)
{
  display.setTextSize(1);
  display.setTextColor(WHITE);
  display.setCursor(0, 0);
  display.println("Pitch       Roll");
  display.setTextSize(2);
  display.setCursor(0, 16);
  display.println(s);
  display.display();
  //delay(2000);
  display.clearDisplay();
}

void update(){

  // Serial.print(acos(ax));
  // Serial.print(' ');
  // Serial.println(ax);
  // float angel_x = 90 - (57296 / 1000)*(acos(ay));
  // float angel_y = 90 - (57296 / 1000)*(acos(ax));
  // float angel_z = 90 - (57296 / 1000)*(acos(az));

  // //Serial.println(angel_x);

  // roll = roll + gx*fps/1000;
  // roll = roll * (1-BETA) + angel_x*BETA;

  // pitch = pitch + gy*fps/1000;
  // pitch = pitch * (1-BETA) + angel_y*BETA;

  // yaw = yaw + gz*fps/1000;
  // yaw = yaw * (1-BETA) + angel_z*BETA;

  fXg = ax * BETA + (fXg * (1.0 - BETA));
  fYg = ay * BETA + (fYg * (1.0 - BETA));
  fZg = az * BETA + (fZg * (1.0 - BETA));
  
  roll  = (atan2(-fYg, fZg)*180.0)/M_PI;
  pitch = (atan2(fXg, sqrt(fYg*fYg + fZg*fZg))*180.0)/M_PI;


  roll = roll + gx*fps/1000;
  //roll = roll * (1-BETA) + angel_x*BETA;

  pitch = pitch + gy*fps/1000;
  //pitch = pitch * (1-BETA) + angel_y*BETA;

  Serial.print(roll);
  Serial.print(" ");
  Serial.println(roll);
}

