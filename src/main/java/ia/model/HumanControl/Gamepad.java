package ia.model.HumanControl;

import display.InputListener;
import engine.Engine;
import engine.agent.Action;
import javafx.scene.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;

public class Gamepad extends HumanControl implements InputListener {

    private int gamepadIndex;
    private boolean isInputSet = false;

    public Gamepad(int gamepadIndex) {
        this.gamepadIndex = gamepadIndex;
    }

    @Override
    public Action getMovement(Engine engine) {
        double rot = 0;
        double speed = 0;

        if (!isInputSet) {
            engine.getDisplay().addListener(this);
        }

        if (GLFW.glfwJoystickPresent(gamepadIndex) && GLFW.glfwJoystickIsGamepad(gamepadIndex)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                GLFWGamepadState state = GLFWGamepadState.malloc(stack);
                debug(state);
                if (GLFW.glfwGetGamepadState(gamepadIndex, state)) {
                    rot = state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);
                    speed = -state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);
                }
            }
        }
        return new Action(rot > 0.1f ? rot : 0, speed > 0.1f ? speed : 0);
    }

    public void debug(GLFWGamepadState state) {
        System.out.println("Gamepad: " + gamepadIndex);
        System.out.println("Input: " + isInputSet);
        System.out.println("GLFW.glfwJoystickPresent(gamepadIndex) : " + GLFW.glfwJoystickPresent(gamepadIndex));
        System.out.println("GLFW.glfwJoystickIsGamepad(gamepadIndex) : " + GLFW.glfwJoystickIsGamepad(gamepadIndex));
        System.out.println(state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X));
        System.out.println(state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y));    }

    @Override
    public void onKeyPressed(KeyEvent e) {

    }

    @Override
    public void onKeyReleased(KeyEvent e) {

    }
}
