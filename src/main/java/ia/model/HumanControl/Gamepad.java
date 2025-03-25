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
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
    }

    @Override
    public Action getMovement(Engine engine) {
        double rot = 0;
        double speed = 0;

        if (!isInputSet) {
            engine.getDisplay().addListener(this);
        }
        debug();
        if (GLFW.glfwJoystickPresent(gamepadIndex) && GLFW.glfwJoystickIsGamepad(gamepadIndex)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                GLFWGamepadState state = GLFWGamepadState.malloc(stack);

                if (GLFW.glfwGetGamepadState(gamepadIndex, state)) {
                    rot = state.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X);
                    speed = -state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);
                }
            }
        }
        rot = rot > 0.1f || rot < -0.1f ? rot : 0;
        speed = speed > 0.1f || speed < 0.1f ? speed : 0;
        return new Action(rot, speed);
    }

    public void debug() {
        System.out.println("Gamepad: " + gamepadIndex);
        System.out.println("Input: " + isInputSet);
        System.out.println("GLFW.glfwJoystickPresent(gamepadIndex) : " + GLFW.glfwJoystickPresent(gamepadIndex));
        System.out.println("GLFW.glfwJoystickIsGamepad(gamepadIndex) : " + GLFW.glfwJoystickIsGamepad(gamepadIndex));
        //System.out.println(state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X));
        //System.out.println(state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y));
    }

    @Override
    public void onKeyPressed(KeyEvent e) {

    }

    @Override
    public void onKeyReleased(KeyEvent e) {

    }
}
