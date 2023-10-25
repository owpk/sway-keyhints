### animated lolcat combined with figlet

## Usage
- Download [latest release](https://github.com/owpk/sway-keyhints/releases/tag/1.0)

```bash
# by default, the script parses the './config/sway/config' and '/etc/sway/config' paths
$ ./swaykeyhints /path/to/sway/config

# also you can specify output table properties
$ ./swaykeyhints -h 10 -w 20
# use -h key to set column height
# use -w key to set column width
```

<p align="center">
   <img src="https://github.com/owpk/sway-keyhints/blob/main/github/console.jpg"/>
</p>

 - Also you can use script with [nwg-wrapper](https://github.com/nwg-piotr/nwg-wrapper)

1. Create script with random name (for example keyhints.sh) in your 'nwg-wrapper config' directory (for me it '~/.config/nwg-wrapper/')  

* keyhints.sh
```bash
#!/bin/sh
 ~/.config/sway/scripts/swaykeyhints $HOME/.config/sway/config -h 48 -w 70
```

2. run nwg-wrapper binary 
```bash
$ nwg-wrapper -s keyhints.sh -r 1800000 -c style.css -p left -ml 200
```

<p align="center">
   <img src="https://github.com/owpk/sway-keyhints/blob/main/github/nwg.jpg"/>
</p>

## Manual build
### GraalVM native-image

> prerequisites:
- java graalvm 17 [install guide](https://www.graalvm.org/docs/getting-started/)  
  also you can use [sdk man](https://sdkman.io/install) if you are on linux machine
- native-image [install guide](https://www.graalvm.org/reference-manual/native-image/)

* Produce a native image

   * With gradle:

   ```bash
   $ ./gradlew nativeImage

   $ cd build/executable
   $ ./swaykeyhints
   ```
