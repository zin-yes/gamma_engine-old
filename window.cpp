#include "window.h"

Window::Window(int width, int height, const char* title, displayState state)
{
    this->width = width;
    this->height = height;
    this->title = title;
    this->state = state;
}
        
Window::~Window()
{
    glfwTerminate();
}

bool Window::create()
{
    if(!glfwInit())
    {
        std::cout << "Failed to initialize GLFW.\n";
        return false;
    }

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    switch (state)
    {
        case WINDOWED:
            identifier = glfwCreateWindow(width, height, title, NULL, NULL);
            break;

        case BORDERLESS:
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
            identifier = glfwCreateWindow(width, height, title, NULL, NULL);
            break;

        case FULLSCREEN:
            identifier = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), NULL);
            break;
        
        default:
            break;
    }

    if(identifier == NULL)
    {
        std::cout << "Failed to create window instance. Terminating.\n";
        return false;
    }
    glfwMakeContextCurrent(identifier);

    if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress))
    {
        std::cout << "Failed to initialize GLAD. Terminating.\n";
        glfwTerminate();
        return false;
    } 

    return true;
}

void Window::update()
{
    glfwPollEvents();
    glfwSwapBuffers(identifier);
}