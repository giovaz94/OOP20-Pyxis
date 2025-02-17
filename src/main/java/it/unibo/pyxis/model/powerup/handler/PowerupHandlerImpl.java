package it.unibo.pyxis.model.powerup.handler;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import it.unibo.pyxis.model.arena.Arena;
import it.unibo.pyxis.model.powerup.effect.PowerupEffect;
import it.unibo.pyxis.model.powerup.effect.PowerupEffectType;
import it.unibo.pyxis.model.powerup.handler.pool.PausablePoolImpl;
import it.unibo.pyxis.model.powerup.handler.pool.PowerupPool;

import static it.unibo.pyxis.model.powerup.effect.PowerupEffectType.BALL_POWERUP;
import static it.unibo.pyxis.model.powerup.effect.PowerupEffectType.PAD_POWERUP;
import static it.unibo.pyxis.model.powerup.effect.PowerupEffectType.ARENA_POWERUP;

public final class PowerupHandlerImpl implements PowerupHandler {

    private static final int MIN_POOL_SIZE = 6;
    private static final int MAX_POOL_SIZE = 10;
    private static final int KEEP_ALIVE_TIMEOUT = 10;

    private final InternalExecutor executor;
    private final Arena arena;

    public PowerupHandlerImpl(final Arena inputArena) {
        this.executor = new InternalExecutor(MIN_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIMEOUT,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        this.arena = inputArena;
    }
    /**
     * Returns the {@link Arena} where this {@link PowerupHandler} is currently
     * attached.
     *
     * @return The instance of {@link Arena}.
     */
    private Arena getArena() {
        return this.arena;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int activeCount() {
        return this.executor.getActiveCount();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void addPowerup(final PowerupEffect effect) {
        this.executor.submit(effect);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPaused() {
        return this.executor.isPaused();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void pause() {
        this.executor.pause();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void resume() {
        this.executor.resume();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        this.executor.shutdownNow();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        this.executor.stop();
    }

    private class InternalExecutor extends PausablePoolImpl implements PowerupPool {

        private final ConcurrentMap<PowerupEffectType, ConcurrentMap<Long, Thread>> threadMap;

        InternalExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                         final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
            this.threadMap = new ConcurrentHashMap<>();
            this.threadMap.put(PAD_POWERUP, new ConcurrentHashMap<>());
            this.threadMap.put(BALL_POWERUP, new ConcurrentHashMap<>());
            this.threadMap.put(ARENA_POWERUP, new ConcurrentHashMap<>());
        }

        /**
         * Starts tracking a new {@link it.unibo.pyxis.model.element.powerup.Powerup}
         * thread adding a new record in the internal thread map.
         *
         * @param type The {@link PowerupEffectType} of the
         *             {@link it.unibo.pyxis.model.element.powerup.Powerup}.
         * @param tid The thread identifier of the {@link Thread} instance.
         * @param thread The instance of the {@link Thread}.
         */
        private synchronized void trackThread(final PowerupEffectType type, final long tid, final Thread thread) {
            this.threadMap.get(type).put(tid, thread);
        }
        /**
         * Stop tracking a {@link it.unibo.pyxis.model.element.powerup.Powerup} thread.
         *
         * @param type The {@link PowerupEffectType} of the
         *             {@link it.unibo.pyxis.model.element.powerup.Powerup}
         *             that should be removed.
         * @param tid The thread identifier of the {@link Thread} instance.
         */
        private synchronized void untrackThread(final PowerupEffectType type, final long tid) {
            this.threadMap.get(type).remove(tid);
        }
        /**
         * This method is used for building a new runnable used for creating a
         * {@link it.unibo.pyxis.model.element.powerup.Powerup} thread.
         * The newly created {@link Runnable} will implement the logics for applying
         * and remove a {@link PowerupEffect}, pausing the thread and safely handling
         * any interruptions.
         *
         * @param effect The effect to apply.
         * @return A new {@link Runnable} to pass to the pool.
         */
        private Runnable buildRunnable(final PowerupEffect effect) {
            return new Runnable() {
                @Override
                public void run() {
                    final ReentrantLock lock = InternalExecutor.this.getLock();
                    final Condition cond = InternalExecutor.this.getWaitCondition();
                    InternalExecutor.this.trackThread(effect.getType(), Thread.currentThread().getId(), Thread.currentThread());
                    try {
                        effect.applyEffect(PowerupHandlerImpl.this.getArena());
                        for (int i = 0; i < effect.getApplyTime(); i++) {
                            lock.lock();
                            while (PowerupHandlerImpl.this.isPaused()) {
                                cond.await();
                            }
                            lock.unlock();
                            TimeUnit.SECONDS.sleep(1);
                        }
                        final Map<Long, Thread> typeMap = InternalExecutor.this.getTypeMap(effect.getType());
                        if (typeMap.size() == 1 && typeMap.containsKey(Thread.currentThread().getId())) {
                            effect.removeEffect(PowerupHandlerImpl.this.getArena());
                        }
                        InternalExecutor.this.untrackThread(effect.getType(), Thread.currentThread().getId());
                    } catch (InterruptedException e) {
                        effect.removeEffect(PowerupHandlerImpl.this.getArena());
                    }
                }
            };
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void submit(final PowerupEffect effect) {
            this.submit(this.buildRunnable(effect));
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized Map<Long, Thread> getTypeMap(final PowerupEffectType type) {
            return this.threadMap.get(type);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() {
            this.threadMap.values().stream().flatMap(m -> m.values().stream()).forEach(Thread::interrupt);
        }
    }
}
