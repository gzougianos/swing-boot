[![Build Status](https://travis-ci.com/gzougianos/swing-boot.svg?token=RPL2mFZzxw8dqRziSkpP&branch=main)](https://travis-ci.com/gzougianos/swing-boot)
[![BCH compliance](https://bettercodehub.com/edge/badge/gzougianos/swing-boot?branch=main&token=4b59f470c9cd6b639cb3b27d83522d676b0d6375)](https://bettercodehub.com/)
# Swing Boot is a small framework for [Swing](https://en.wikipedia.org/wiki/Swing_(Java)). Essentialy, a set of [Guice](https://github.com/google/guice) modules to be used on top of Swing.

_(Also, the project of my diploma thesis.)_

## How to use Swing Boot

**NOTE: The framework is not ready for any kind of production!** For now, it is just a snapshot.

Temporarily, Swing Boot is deployed only for maven. So, add the following dependencies to your `pom.xml`:

```
<dependency>
  <groupId>io.github.swingboot</groupId>
  <artifactId>control</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency> 

<dependency>
  <groupId>io.github.swingboot</groupId>
  <artifactId>concurrency</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency> 
```
For how to make the annotation processor work, read [here](https://github.com/gzougianos/swing-boot/tree/main/processor).

## See the modules in detail:
- [Control module](https://github.com/gzougianos/swing-boot/tree/main/control)
- [Concurrency module](https://github.com/gzougianos/swing-boot/tree/main/concurrency)




