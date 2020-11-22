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

//const char* SSID     = "Slytherin";
//const char* PASSWORD = "9WF^F^ua";
const char* SSID     = "Gryffindor";
const char* PASSWORD = "XnU3Xz^`";

//Static IP address configuration
IPAddress staticIP(192, 168, 1, 100);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);
IPAddress dns(8, 8, 8, 8);

WiFiServer server(8000);
WiFiClient client;
char cmd[20];
byte cmdIndex = 0;

const int IN_A1 = 12;
const int IN_A2 = 14;
const int IN_B1 = 27;
const int IN_B2 = 26;

const int SIGNAL = 25;
const int FUNC_1 = 33;
const int FUNC_2 = 32;
const int FUNC_3 = 35;
const int FUNC_4 = 34;

void connet();
void host();
void direct();

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

  if (DEBUG) {
    Serial.begin(9600);
  }

  connet();

  server.begin();
//  xTaskCreate(
//    anotherTask, /* Task function. */
//    "another Task", /* name of task. */
//    10000, /* Stack size of task */
//    NULL, /* parameter of the task */
//    1, /* priority of the task */
//    NULL); /* Task handle to keep track of created task */
}

void loop() {
  if (!client.connected()) {
    client = server.available();
    return;
  }

  if (client) {
    Serial.println("Client connected.");
    HighLight();

    while (client.connected()) {
      if (client.available()) {
        char c = client.read();

        if (c == '\n') {
          cmdIndex = 0;
          direct();
          client.println("ok");

        } else {
          cmd[cmdIndex] = c;
          if (cmdIndex < 18) cmdIndex++;
        }
      }
    }

    client.stop();
    Serial.println("Client Disconnected.");
  }
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

/*
  | Character |      Handle      |    Description    |
  |-----------|:-----------------|-------------------|
  |     go    |       go()       |      Go ahead     |
*/
void direct() {
  if (!strcmp(cmd, "go")) {
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
}

/* HANDLE CAR DIRECTION */
void Stop() {
  digitalWrite(IN_A1, LOW);
  digitalWrite(IN_A2, LOW);
  digitalWrite(IN_B1, LOW);
  digitalWrite(IN_B2, LOW);
}

void Go() {
  digitalWrite(IN_A1, HIGH);
  digitalWrite(IN_A2, LOW);
  digitalWrite(IN_B1, LOW);
  digitalWrite(IN_B2, HIGH);
  delay(500);
}

void Back() {
  digitalWrite(IN_A1, LOW);
  digitalWrite(IN_A2, HIGH);
  digitalWrite(IN_B1, HIGH);
  digitalWrite(IN_B2, LOW);
  delay(500);
}

void TurnLeft() {
  digitalWrite(IN_A1, HIGH);
  digitalWrite(IN_A2, LOW);
  digitalWrite(IN_B1, HIGH);
  digitalWrite(IN_B2, LOW);
  delay(150);
}

void TurnRight() {
  digitalWrite(IN_A1, LOW);
  digitalWrite(IN_A2, HIGH);
  digitalWrite(IN_B1, LOW);
  digitalWrite(IN_B2, HIGH);
  delay(150);
}

/* HANDLE FUNCTIONS */
void HighLight() {
  digitalWrite(SIGNAL, LOW);
  delay(500);

  for (int i = 1; i < 2; i++) {
    digitalWrite(SIGNAL, HIGH);
    delay(1000);
    digitalWrite(SIGNAL, LOW);
    delay(1000);
  }

  digitalWrite(SIGNAL, HIGH);
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
