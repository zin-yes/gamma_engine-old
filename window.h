#include <glad/glad.h>
#include <GLFW/glfw3.h>

#include <iostream>

enum displayState
{
    WINDOWED, BORDERLESS, FULLSCREEN
};

class Window
{
    private:
        GLFWwindow* identifier;
        int width, height;
        const char* title;
        displayState state;
    public:
        Window(int width, int height, const char* title, displayState state);
        ~Window();
        
        bool create();
        void update();

        inline bool getKeyDown(int keyCode) { return glfwGetKey(identifier, keyCode); };

        /*TODO*/
        inline bool getKeyPressed(int keyCode) { return glfwGetKey(identifier, keyCode); };
        inline bool getKeyReleased(int keyCode) { return glfwGetKey(identifier, keyCode); };

        inline int getWidth() { return width; }
        inline int getHeight() { return height; }
        inline const char* getTitle() { return title; }
        inline GLFWwindow* getIdentifier() { return identifier; }
        inline bool shouldntClose() { return !glfwWindowShouldClose(identifier); }
};