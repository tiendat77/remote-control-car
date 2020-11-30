/*
  Author:
   _____ ___ _____ _   _   ____    _  _____   _   _ _   ___   ___   _ _   _
  |_   _|_ _| ____| \ | | |  _ \  / \|_   _| | | | | | | \ \ / / \ | | | | |
    | |  | ||  _| |  \| | | | | |/ _ \ | |   | |_| | | | |\ V /|  \| | |_| |
    | |  | || |___| |\  | | |_| / ___ \| |   |  _  | |_| | | | | |\  |  _  |
    |_| |___|_____|_| \_| |____/_/   \_\_|   |_| |_|\___/  |_| |_| \_|_| |_|

  Date created 19-Nov-2020 13:45
  TCP server power by ESP32
*/

#include <WiFi.h>

/* DECLARATION */
const int DEBUG = 1;

const char* SSID     = "Slytherin";
const char* PASSWORD = "9WF^F^ua";

//const char* SSID     = "Gryffindor";
//const char* PASSWORD = "XnU3Xz^`";

//Static IP address configuration
IPAddress staticIP(192, 168, 1, 177);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);
IPAddress dns(8, 8, 8, 8);

WiFiServer server(8000);
WiFiClient client;

char cmd[20];
byte cmdIndex = 0;
unsigned long cmdTime = 0;
unsigned long duration;

const int IN_A1 = 4;
const int IN_A2 = 0;
const int IN_B1 = 2;
const int IN_B2 = 15;

const int SIGNAL = 25;
const int FUNC_1 = 33;
const int FUNC_2 = 32;
const int FUNC_3 = 35;
const int FUNC_4 = 34;

const int SRF_TRIG = 12;
const int SRF_ECHO = 14;

void connet();
void host();
void direct();
void HighLight();
void handler(void * parameter);
void Blink(void * parameter);

/* MAIN CODE HERE */
void setup() {
  pinMode(IN_A1, OUTPUT);
  pinMode(IN_A2, OUTPUT);
  pinMode(IN_B1, OUTPUT);
  pinMode(IN_B2, OUTPUT);
  pinMode(SIGNAL, OUTPUT);
  pinMode(FUNC_1, OUTPUT);
  pinMode(FUNC_2, OUTPUT);
  pinMode(FUNC_3, OUTPUT);
  pinMode(FUNC_4, OUTPUT);
  pinMode(SRF_TRIG, OUTPUT);
  pinMode(SRF_ECHO, INPUT);

  if (DEBUG) {
    Serial.begin(115200);
  }

  // Connect to WiFi
  connet();

  // Start TCP Server
  server.begin();

  // Set up handle client task to run independently.
  xTaskCreate(
    handler,
    "Handler",   // Stack name for debug
    2048,  // Stack size
    NULL,
    2,  // Priority, with 3 being the highest, and 0 being the lowest.
    NULL);
}

void loop() {
  // Empty. Things are done in Tasks.
}

/* CONFIG ESP AS WIFI CLIENT OR WIFI ACCESS POINT */
void connet() {
  WiFi.disconnect();

  WiFi.config(staticIP, subnet, gateway, dns);

  WiFi.begin(SSID, PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("Connected to: ");
  Serial.println(SSID);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println("");

  HighLight();
}

void host() {
  WiFi.mode(WIFI_AP);
  WiFi.softAP(SSID, PASSWORD, 1);
  IPAddress ip = WiFi.softAPIP();

  Serial.print("WiFi turned on: ");
  Serial.println(ip);
}

/* HANDLERS */
void handler(void * parameter) {
  (void) parameter;

  while (true) {
    if (!client.connected()) {
      client = server.available();
    }

    if (client) {
      Serial.println("Client connected.");

      while (client.connected()) {
        if (client.available()) {
          char c = client.read();

          if (c == '\n') {
            cmd[cmdIndex] = '\0';
            cmdIndex = 0;

            client.println("ok");

            direct();

          } else {
            cmd[cmdIndex] = c;
            if (cmdIndex < 18) {
              cmdIndex++;
            }
          }
        }
      }

      Reset();
      client.stop();
      Serial.println("Client Disconnected.");
    }

    Stop();
  }
}

void direct() {

  // Flood prevention
  if (millis() - cmdTime < 100) {
    return;
  }

  cmdTime = millis();
  Serial.println(cmd);

  if (!strcmp(cmd, "go")) {
    if (detectObstacles()) {
      Serial.println("Obstacles detected");
      return;
    }

    Go();
    return;
  }

  if (!strcmp(cmd, "ba")) {
    Back();
    return;
  }

  if (!strcmp(cmd, "le")) {
    TurnLeft();
    return;
  }

  if (!strcmp(cmd, "ri")) {
    TurnRight();
    return;
  }

  if (!strcmp(cmd, "f1")) {
    Function1();
    return;
  }

  if (!strcmp(cmd, "f2")) {
    Function2();
    return;
  }

  if (!strcmp(cmd, "f3")) {
    Function3();
    return;
  }

  if (!strcmp(cmd, "f4")) {
    HighLight();
    return;
  }
}

boolean detectObstacles() {
  int distance;

  /* Send from TRIG */
  digitalWrite(SRF_TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(SRF_TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(SRF_TRIG, LOW);

  /* Receive on ECHO */
  duration = pulseIn(SRF_ECHO, HIGH);
  distance = int(duration/58.824);

  Serial.print("duration ");
  Serial.println(duration);
  Serial.print("distance ");
  Serial.println(distance);

  if (distance < 7) {
    return true;
  }

  return false;
}

/* HANDLE CAR DIRECTION */
void Stop() {
  digitalWrite(IN_A1, LOW);
  digitalWrite(IN_A2, LOW);
  digitalWrite(IN_B1, LOW);
  digitalWrite(IN_B2, LOW);
}

void Go() {
  digitalWrite(IN_A1, LOW);
  digitalWrite(IN_A2, HIGH);
  digitalWrite(IN_B1, LOW);
  digitalWrite(IN_B2, HIGH);
  delay(300);
  Stop();
}

void Back() {
  digitalWrite(IN_A1, HIGH);
  digitalWrite(IN_A2, LOW);
  digitalWrite(IN_B1, HIGH);
  digitalWrite(IN_B2, LOW);
  delay(300);
  Stop();
}

void TurnLeft() {
  digitalWrite(IN_A1, HIGH);
  digitalWrite(IN_A2, LOW);
  digitalWrite(IN_B1, LOW);
  digitalWrite(IN_B2, HIGH);
  delay(200);
  Stop();
}

void TurnRight() {
  digitalWrite(IN_A1, LOW);
  digitalWrite(IN_A2, HIGH);
  digitalWrite(IN_B1, HIGH);
  digitalWrite(IN_B2, LOW);
  delay(200);
  Stop();
}

/* HANDLE FUNCTIONS */
void Blink(void * parameter) {
  (void) parameter;

  digitalWrite(SIGNAL, LOW);
  vTaskDelay(250);

  for (int i = 1; i < 3; i++) {
    digitalWrite(SIGNAL, HIGH);
    vTaskDelay(250);

    digitalWrite(SIGNAL, LOW);
    vTaskDelay(250);
  }

  vTaskDelete(NULL);
}

void HighLight() {
  xTaskCreate(Blink, "Blink", 1024, NULL, 1, NULL);
}

void Function1() {
  int state = digitalRead(FUNC_1);

  if (state) {
    digitalWrite(FUNC_1, LOW);
    return;
  }

  digitalWrite(FUNC_1, HIGH);
}

void Function2() {
  int state = digitalRead(FUNC_2);

  if (state) {
    digitalWrite(FUNC_2, LOW);
    return;
  }

  digitalWrite(FUNC_2, HIGH);
}

void Function3() {
  digitalWrite(FUNC_3, HIGH);
  delay(500);
  digitalWrite(FUNC_3, LOW);
}

void Reset() {
  digitalWrite(IN_A1, LOW);
  digitalWrite(IN_A2, LOW);
  digitalWrite(IN_B1, LOW);
  digitalWrite(IN_B2, LOW);

  digitalWrite(SIGNAL, LOW);
  digitalWrite(FUNC_1, LOW);
  digitalWrite(FUNC_2, LOW);
  digitalWrite(FUNC_3, LOW);
  digitalWrite(FUNC_4, LOW);

  cmd[0] = '\0';
  cmdTime = 0;
}
