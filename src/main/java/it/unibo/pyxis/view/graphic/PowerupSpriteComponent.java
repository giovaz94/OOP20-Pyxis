package it.unibo.pyxis.view.graphic;

import it.unibo.pyxis.ecs.component.sprite.AbstractSpriteComponent;
import it.unibo.pyxis.model.element.powerup.Powerup;

public final class PowerupSpriteComponent extends AbstractSpriteComponent<Powerup> {

    private static final String POWERUP_FOLDER = "powerup/";

    public PowerupSpriteComponent(final Powerup entity) {
        super(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return this.getSpritesPath() + POWERUP_FOLDER + this.getEntity().getType().toString() + "_POWERUP.png";
    }
}
