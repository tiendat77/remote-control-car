# ESP32 with Arduino

## ESP32
> ESP32 is a series of low-cost, low-power system on a chip microcontrollers with integrated Wi-Fi and dual-mode Bluetooth.

Board info:

```
 Name: NodeMCU-32s
 CPU: Xtensa® dual-core 32-bit LX6 microprocessor, up to 240 MHz
 ROM: 448 KB for booting and core functions
 SRAM: 520 KB for data and instructions
 Wi­Fi: 802.11b/g/n. Bit rate: 802.11n up to 150 Mbps
 Library: esp32 ver 1.0.3 by Espessif Systems
```

<p align="center">
  <img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/NodeMCU-32s.jpg" alt="nodeMCU-32s" />
</p>

## Arduino IDE
> The open-source Arduino Software (IDE) makes it easy to write code and upload it to the board.

Install instructions: [Arduino HomePage](https://www.arduino.cc/).

## Installing ESP32 Add-on in Arduino IDE

1. Open Arduino IDE, go to File > Preferences
2. Enter **https://dl.espressif.com/dl/package_esp32_index.json** into the “Additional Board Manager URLs” field. Then click **Ok** button.
3. Go to Tools > Board > Boards Manager…
4. Search for ESP32 and press install button for the “ESP32 by Espressif Systems“:

Arduino core for the ESP32: [github-arduino-esp32](https://github.com/espressif/arduino-esp32)

**Notes:**

Fix Permission denied: '/dev/ttyUSB0' by command

```
sudo chmod a+rw /dev/ttyUSB0
```

## Remote Control Car with ESP32

### Hardware components
| Module       | Description |
| ------------ | ----------- |
| ESP32        | Microcontroller |
| L298N        | Motor driver module  |
| SRF05        | Detecting and avoiding frontal obstacles |
| Chassis      | To put everything in place |
| 4 * DC Motor Mini 5V |     |
| 4 * Wheels   |             |
| 5 * LED      |             |
| 5 * Resistor (100 Ohm) |   |
| Buzzer       |             |
| Power switch button |      |
| Wires        |             |
| 2 * 18650 battery |        |
| 18650 battery holder (2 cell) | |

### Connect components
| PIN         | FUNCTION    | Description        |
| ----------- | ----------- | ------------------ |
| GPIO4       | L298N IN1   | L298N INPUT 1 (A1) |
| GPIO0       | L298N IN2   | L298N INPUT 2 (A2) |
| GPIO2       | L298N IN3   | L298N INPUT 3 (B1) |
| GPIO15      | L298N IN4   | L298N INPUT 4 (B2) |
| GPIO12      | SRF05 TRIG  | SRF05 TRIG (INPUT) |
| GPIO14      | SRF05 ECHO  | SRF05 ECHO (OUTPUT)|
| GPIO25      | SIGNAL      | WiFi connection status |
| GPIO33      | FUNCTION 1  | Headlight |
| GPIO32      | FUNCTION 2  | Left turn signal lights |
| GPIO35      | FUNCTION 3  | Right turn signal lights |
| GPIO34      | FUNCTION 4  | Horn |

<img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/rc_car_break_board.png" alt="fritzing break board" />

### Upload code to ESP32

Open Arduino IDE and upload [the sketch](https://github.com/tiendat77/remote-control-car/blob/master/rc-car/rc-car.ino) to ESP32 module.

**Note**: change WiFi configuration to yours.

### Make PCB

<table>
  <tr>
    <td><img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/rc_car_pcb_1.png" height="700px" alt="pcb1" /></td>
    <td><img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/rc_car_pcb_2.png" height="700px" alt="pcb2" /></td>
  </tr>
</table>

Print [PCB](https://github.com/tiendat77/remote-control-car/blob/master/assets/rc_car_pcb.pdf) and assembly.

### 3D printed RC car chassis

### Put everything in place

<img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/rc_car_car_frame.png" alt="car frame" />

<br />
<br />

Enjoy! :relaxed::relaxed::relaxed:
