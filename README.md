![Force Engine logo](/Force Engine/forceengine2.png?raw=true "Image made in GIMP")

Force Engine 2
==============

[Force Engine 2](http://ericleong.me/games/force-engine) is an [old](https://sites.google.com/site/t3hprogrammer/software/forceengine/history) (from 2007) physics engine written in Java with some game engine components. It is primarily a learning and research project focused on testing ideas around collision detection and response, as well as fluid dynamics.

There are also tutorials, presentations, and research papers explaining the [circle-circle](http://ericleong.me/research/circle-circle) and [circle-line](http://ericleong.me/research/circle-line) collision detection and response algorithms.

`Force Engine` depends on Java 6. `Force Engine Android` requires the Android SDK.

demo
----

The easiest way to get the Java demo running is by opening the `Force Engine` project with [Eclipse](https://www.eclipse.org/) and running `ForceEngineDemo`.

To open the Android app, open `Force Engine Android` with [Android Studio](https://developer.android.com/sdk/installing/studio.html).

features
--------
- circle-circle collision
- circle-line collision
- circle-circle field interactions
- triggers
- explosions
- fluids that interact with rigid bodies

library
-------

The library can be exported from Eclipse using the `physics.jardesc` file in the `Force Engine` project. The `.jar` file produced is then used in `Force Engine Android/app/libs`.

usage
-----

Create a new instance of the physics engine with
```Java
PhysicsEngine engine = new PhysicsEngine(<width>, <height>);
```
Inside a thread, advance to the next tick with `engine.components()`.
