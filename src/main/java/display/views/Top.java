package display.views;

import display.model.ModelMVC;

import java.io.IOException;

public class Top extends View {

    public Top(ModelMVC model) throws IOException {
        super(model);
        this.pane = loadFxml("Top", this.modelMVC);
    }

    @Override
    public void update() {
        super.update();
    }
}
