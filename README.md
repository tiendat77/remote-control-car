# Remote Control Car Project
> Remote control car with android and esp32

This project aims to build a Remote Control Car (RC Car) with Espressif ESP32 and Android Remote Control Application.

<p align="center">
  <img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/app-icon.png" with="120px" height="120px" alt="logo" />
</p>

## Requirements :alien:
1. RC Car. Check out [building instructions](https://github.com/tiendat77/remote-control-car/tree/master/rc-car)
2. Android phone running 5.0 (Lollipop) or higher
3. WiFi network

## Usage :book:
1. Upload [the sketch](https://github.com/tiendat77/remote-control-car/blob/master/rc-car/rc-car.ino) to the RC Car via [Arduino IDE](https://www.arduino.cc/en/software)
2. Install [Car Controller](https://github.com/tiendat77/remote-control-car/blob/master/CarController.apk) application to your smartphone
3. Connect RC Car and your smartphone to the same WiFi network
4. Open Car Controller application, then open *App Settings* and make sure *RC Car IP Address* is correct.
5. Click `Connect Button` on the top left corner of the screen. *Light bulb on is connected*.
6. Enjoy!

## Features :muscle:
- Stable connection.
- Obstacles detection.
- Changeable themes.
- Supports 3 type of control: buttons, joystick, motion sensor (tilt to turn).

## Demos

<img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/demo_1.jpg" alt="demo_1" />

<img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/demo_2.jpg" alt="demo_2" />

<img src="https://github.com/tiendat77/remote-control-car/blob/master/assets/demo_3.jpg" alt="demo_3" />

## Contributing :sparkles:

If you have any problem from installation, please [open an issue](https://github.com/tiendat77/remote-control-car/issues/new).

Contributions of any kind are definitely welcome!

## Open source libraries
- [virtual-joystick-android](https://github.com/controlwear/virtual-joystick-android)

## License

MIT © 2020 [TienDat](mailto:huynhztienzdat@gmail.com), see [the license](https://github.com/tiendat77/remote-control-car/blob/master/LICENSE).
