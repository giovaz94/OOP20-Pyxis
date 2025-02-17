package it.unibo.pyxis.model.powerup.effect;

public enum PowerupEffectType {
    /**
     * Type of {@link it.unibo.pyxis.model.element.powerup.Powerup}
     * that affects the {@link it.unibo.pyxis.model.arena.Arena} environment.
     */
    ARENA_POWERUP,
    /**
     * Type of {@link it.unibo.pyxis.model.element.powerup.Powerup} that affects
     * the {@link it.unibo.pyxis.model.element.ball.Ball}s of the
     * {@link it.unibo.pyxis.model.arena.Arena}.
     */
    BALL_POWERUP,
    /**
     * Type of {@link it.unibo.pyxis.model.element.powerup.Powerup}
     * that affects the {@link it.unibo.pyxis.model.element.pad.Pad}.
     */
    PAD_POWERUP;
}
