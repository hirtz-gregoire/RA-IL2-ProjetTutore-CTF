package display;

import javafx.scene.input.KeyEvent;

public interface InputListener {
    void onKeyPressed(KeyEvent e);
    void onKeyReleased(KeyEvent e);
}
