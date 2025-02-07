package it.unibo.pyxis.model.arena.component;

import it.unibo.pyxis.model.arena.Arena;
import it.unibo.pyxis.ecs.component.physics.AbstractUpdateComponent;
import it.unibo.pyxis.model.element.ball.Ball;
import it.unibo.pyxis.model.event.Events;
import it.unibo.pyxis.model.hitbox.CollisionInformation;
import it.unibo.pyxis.model.hitbox.Hitbox;
import it.unibo.pyxis.model.util.Dimension;
import org.greenrobot.eventbus.EventBus;

import java.util.Optional;

public class ArenaUpdateComponent extends AbstractUpdateComponent<Arena> {

    public ArenaUpdateComponent(final Arena entity) {
        super(entity);
    }

    /**
     * Checks if {@link Ball} or {@link it.unibo.pyxis.model.element.powerup.Powerup} is colliding with a border.
     * Remove the {@link Ball} or {@link it.unibo.pyxis.model.element.powerup.Powerup} colliding with the bottom border,
     * Otherwise send a {@link it.unibo.pyxis.model.event.collision.CollisionEvent}
     * with the edge the {@link Ball} is colliding with.
     */
    private void checkBorderCollision() {
        final Arena arena = this.getEntity();
        for (final Ball ball: arena.getBalls()) {
            final Hitbox ballHitbox = ball.getHitbox();
            if (ballHitbox.isCollidingWithLowerBorder(arena.getDimension())) {
                arena.removeBall(ball);
                if (arena.getBalls().isEmpty()) {
                    EventBus.getDefault().post(Events.newDecreaseLifeEvent());
                    arena.clearPowerups();
                    arena.restorePadDimension();
                    arena.resetStartingPosition();
                    return;
                }
            } else {
                final Dimension arenaDimension = arena.getDimension();
                final Optional<CollisionInformation> collInformation = ballHitbox.collidingInformationWithBorder(arenaDimension);
                collInformation.ifPresent(cI -> EventBus.getDefault().post(Events.newBallCollisionWithBorderEvent(ball.getId(), cI)));
            }
        }
        arena.getPowerups().stream()
                .filter(p -> p.getHitbox().isCollidingWithLowerBorder(arena.getDimension()))
                .forEach(arena::removePowerup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void update(final double elapsed) {
        this.checkBorderCollision();
        this.getEntity().getBalls().forEach(b -> b.update(elapsed));
        this.getEntity().getPowerups().forEach(p -> p.update(elapsed));
    }
}
