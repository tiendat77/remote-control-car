# Remote Control Car Project
> Remote control car with android and esp32

This project aims to build a WiFi Remote Control Car (RC Car) by Android Application

<img src="" alt="demo" />

## Requirements :alien:
1. RC Car. Check out [building instructions](https://github.com/tiendat/remote-control-car/tree/master/esp32)
2. Android phone running 5.0 (Lollipop) or higher
3. WiFi network

## Usage :book:
1. Upload [the sketch](https://github.com/tiendat/remote-control-car/blob/master/esp32/esp32.ino) to the RC Car via [Arduino IDE](https://www.arduino.cc/en/software)
2. Install [Car Controller](https://github.com/tiendat/remote-control-car/blob/master/CarController.apk) application to your smartphone
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

<img src="" alt="demo" />

<img src="" alt="demo" />

<img src="" alt="demo" />

## Contributing :sparkles:

If you have any problem from installation, please [open an issue](https://github.com/tiendat/remote-control-car/issues/new).

Contributions of any kind are definitely welcome!

## Open source libraries
- [virtual-joystick-android](https://github.com/controlwear/virtual-joystick-android)

## License

MIT © 2020 [TienDat](mailto:huynhztienzdat@gmail.com), see [the license](https://github.com/tiendat/remote-control-car/blob/master/LICENSE).
