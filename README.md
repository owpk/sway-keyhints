### animated lolcat combined with figlet

<p align="center">
   <img src="https://github.com/owpk/sway-keyhints/blob/main/github/nwg.jpg"/>
</p>

<p align="center">
   <img src="https://github.com/owpk/sway-keyhints/blob/main/github/console.jpg"/>
</p>

## Usage
- Download latest 

```bash
# by default, the script parses the './config/sway/config' and '/etc/sway/config' paths
$ ./swaykeyhints /path/to/sway/config

# also you can specify output table properties
$ ./swaykeyhints -h 10 -w 20
# use -h key to set column height
# use -w key to set column width
```

---
## Build
# <a name="gvm"></a><h1>GraalVM native-image</h1>

> prerequisites:
- java graalvm 17 [install guide](https://www.graalvm.org/docs/getting-started/)  
  also you can use [sdk man](https://sdkman.io/install) if you are on linux machine
- native-image [install guide](https://www.graalvm.org/reference-manual/native-image/)

* Produce a native image

   * With gradle:

   ```bash
   $ ./gradlew nativeImage

   $ cd build/executable
   $ ./swaykeyhints"Hello world!" -a
   ```
