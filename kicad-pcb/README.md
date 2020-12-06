# KiCad
> KiCad is a free software suite for electronic design automation. It facilitates the design of schematics for electronic circuits and their conversion to PCB designs.

## Installation

[Download instructions](https://kicad.org/download/)

Install KiCad on Ubuntu, enter these commands into terminal:

```
sudo add-apt-repository --yes ppa:kicad/kicad-5.1-releases
sudo apt update
sudo apt install --install-recommends kicad

# If you want demo projects
sudo apt install kicad-demos
```

## RC Car Schematic
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

<img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/rc_car_schematic.png" alt="schematic" />

## RC Car PCB
<table>
  <tr>
    <td><img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/rc_car_pcb_1.png" height="700px" alt="pcb1" /></td>
    <td><img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/rc_car_pcb_2.png" height="700px" alt="pcb2" /></td>
  </tr>
</table>

[PDF](https://github.com/tiendat77/remote-control-car/blob/master/assets/rc_car_pcb.pdf)
