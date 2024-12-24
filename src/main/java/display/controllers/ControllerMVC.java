package display.controllers;

import display.model.ModelMVC;

public abstract class ControllerMVC {

    protected ModelMVC model;

    public void setModel(ModelMVC m) {
        this.model = m;
    }

}
