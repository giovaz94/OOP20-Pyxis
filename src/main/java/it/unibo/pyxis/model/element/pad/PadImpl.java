package it.unibo.pyxis.model.element.pad;

import it.unibo.pyxis.model.element.AbstractElement;
import it.unibo.pyxis.model.event.Events;
import it.unibo.pyxis.model.event.movement.BallMovementEvent;
import it.unibo.pyxis.model.event.movement.PowerupMovementEvent;
import it.unibo.pyxis.model.hitbox.HitEdge;
import it.unibo.pyxis.model.hitbox.RectHitbox;
import it.unibo.pyxis.model.util.Coord;
import it.unibo.pyxis.model.util.Dimension;
import it.unibo.pyxis.model.util.DimensionImpl;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Optional;

public final class PadImpl extends AbstractElement implements Pad {

    private static final Dimension DIMENSION = new DimensionImpl(100, 18);

    public PadImpl(final Dimension inputDimension, final Coord inputPosition) {
        super(inputDimension, inputPosition);
        this.setHitbox(new RectHitbox(this));
        EventBus.getDefault().register(this);
    }

    public PadImpl(final Coord inputPosition) {
        this(DIMENSION, inputPosition);
    }

    @Override
    public void update(final double dt) {
        throw new UnsupportedOperationException("You can't call an update on a brick");
    }

    @Override
    @Subscribe
    public void handleBallMovement(final BallMovementEvent movementEvent) {
        final Optional<HitEdge> hitEdge = movementEvent.getElement().getHitbox().collidingEdgeWithHB(this.getHitbox());
        hitEdge.ifPresent(edge -> {
            EventBus.getDefault().post(Events.newBallCollisionWithPadEvent(movementEvent.getElement().getId(), edge, this.getDimension().getWidth()));
        });
    }

    @Override
    @Subscribe
    public void handlePowerupMovement(final PowerupMovementEvent movementEvent) {
        final Optional<HitEdge> hitEdge = movementEvent.getElement().getHitbox().collidingEdgeWithHB(this.getHitbox());
        hitEdge.ifPresent(edge -> {
            EventBus.getDefault().post(Events.newPowerupActivationEvent(movementEvent.getElement()));
        });
    }
}
