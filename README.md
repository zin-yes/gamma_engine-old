# Gamma Engine
This game engine is written in Java and it uses OpenGL for graphics. If you are wondering, I at first thought of making this engine in C++ but I decided that Java would suffice, since the libraries do not use Java to render. For libraries, this project uses Maven to import LWJGL. 

This file will be updated with further information once the project is more complete. But until then I want to thank you for taking a look around!

Here are some images of how the current example game on the repository will look like once you clone the project. All of these textures are from https://freepbr.com/.
![Bricks](https://imgur.com/0v1vkQy.png)
![Rusted Iron](https://imgur.com/tZvtTqv.png)

Currently this repository is experimentational and has a lot of loose ends that need thightening up. E.g. there is only one type of light component, that being PointLight. All these issues and more will be addressed once I have more time on my hand after getting everything fleshed out.

## Current Features:
    - Object and component system like the alpha version of Gamma Engine.
    - Skybox.
    - PBR rendering basics (will be expanded in the future).
    
## Planned Features:
    - JSON or YAML (or both) reading for saving and configing.
    - Blueprints.
    - Displacement parallax mapping.
    - Deferred rendering.
    - Post processing/framebufferobject system.
    - Shadow maps.
    - Wet floor effect.
