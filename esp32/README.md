# ESP32 with Arduino

## Installing ESP32 Add-on in Arduino IDE

1. Open Arduino IDE, go to File > Preferences
2. Enter **https://dl.espressif.com/dl/package_esp32_index.json** into the “Additional Board Manager URLs” field. Then click **Ok** button.
3. Go to Tools > Board > Boards Manager…
4. Search for ESP32 and press install button for the “ESP32 by Espressif Systems“:

## Board info

```
 Name: NodeMCU-32s
 Library: esp32 ver 1.0.3 by Espessif Systems
```

Arduino core for the ESP32:  
[Github](https://github.com/espressif/arduino-esp32)

Fix Permission denied: '/dev/ttyUSB0' by command
```
sudo chmod a+rw /dev/ttyUSB0
```

## Remote Control Car with ESP32

