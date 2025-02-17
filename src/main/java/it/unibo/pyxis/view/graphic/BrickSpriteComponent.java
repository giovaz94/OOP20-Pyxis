package it.unibo.pyxis.view.graphic;

import it.unibo.pyxis.ecs.component.sprite.AbstractSpriteComponent;
import it.unibo.pyxis.model.element.brick.Brick;

public final class BrickSpriteComponent extends AbstractSpriteComponent<Brick> {

    private static final String BRICK_FOLDER = "brick/";

    public BrickSpriteComponent(final Brick entity) {
        super(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return this.getSpritesPath() + BRICK_FOLDER + this.getEntity().getBrickType().getTypeString() + "_BRICK.png";
    }
}
