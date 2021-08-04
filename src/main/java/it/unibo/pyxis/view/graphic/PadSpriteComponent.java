package it.unibo.pyxis.view.graphic;

import it.unibo.pyxis.ecs.component.sprite.AbstractSpriteComponent;
import it.unibo.pyxis.model.element.pad.Pad;
import javafx.scene.image.Image;

public final class PadSpriteComponent extends AbstractSpriteComponent<Pad> {
    public PadSpriteComponent(final Pad entity) {
        super(entity);
    }

    @Override
    public Image obtainSprite() {
        return null;
    }
}
