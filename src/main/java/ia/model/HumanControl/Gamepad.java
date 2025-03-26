package ia.model.HumanControl;

import display.InputListener;
import engine.Engine;
import engine.agent.Action;
import javafx.scene.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

public class Gamepad extends HumanControl implements InputListener {

    private final int gamepadIndex;
    private boolean isInputSet = false;

    public Gamepad(String gamepadIndex) {
        this.gamepadIndex = Integer.parseInt(gamepadIndex);
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
    }

    public static List<String> getConnectedGamepads() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        List<String> gamepadIndices = new ArrayList<>();
        for (int i = 0; i <= GLFW.GLFW_JOYSTICK_LAST; i++) {
            if (GLFW.glfwJoystickPresent(i) && GLFW.glfwJoystickIsGamepad(i)) {
                gamepadIndices.add("Manette " + i);
            }
        }
        return gamepadIndices;
    }

    @Override
    public Action getMovement(Engine engine) {
        double rot = 0;
        double speed = 0;

        if (!isInputSet) {
            engine.getDisplay().addListener(this);
            isInputSet = true;
        }
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

    @Override
    public void onKeyPressed(KeyEvent e) {

    }

    @Override
    public void onKeyReleased(KeyEvent e) {

    }
}
