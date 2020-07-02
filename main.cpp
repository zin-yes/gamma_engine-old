#include "window.h"

int main()
{
    Window window(1280, 720, "bruhg", WINDOWED);

    if(!window.create())
        return -1;

    while(window.shouldntClose() && !glfwGetKey(window.getIdentifier(), GLFW_KEY_ESCAPE))
    {
        glClear(GL_COLOR_BUFFER_BIT);
        window.update();
    }

    return 0;
}