package it.unibo.pyxis.controller.linker;

import it.unibo.pyxis.controller.command.Command;
import it.unibo.pyxis.controller.engine.GameLoop;
import it.unibo.pyxis.controller.engine.GameLoopImpl;
import it.unibo.pyxis.controller.input.InputHandler;
import it.unibo.pyxis.controller.input.InputHandlerImpl;
import it.unibo.pyxis.controller.soundplayer.Sound;
import it.unibo.pyxis.controller.soundplayer.SoundPlayer;
import it.unibo.pyxis.model.level.status.LevelStatus;
import it.unibo.pyxis.model.state.GameState;
import it.unibo.pyxis.model.state.GameStateImpl;
import it.unibo.pyxis.model.state.StateEnum;
import it.unibo.pyxis.view.scene.SceneHandler;
import it.unibo.pyxis.view.scene.SceneHandlerImpl;
import it.unibo.pyxis.view.scene.SceneType;
import it.unibo.pyxis.view.views.RenderableView;
import javafx.stage.Stage;

public class LinkerImpl implements Linker {

    private GameState gameState;
    private GameLoop gameLoop;
    private SceneHandler sceneHandler;
    private InputHandler inputHandler;
    private int maximumLevelReached;

    public LinkerImpl(final Stage inputStage) {
        this.createGameState();
        this.createGameLoop();
        this.createInputHandler(inputStage);
        this.createSceneHandler(inputStage);
        SoundPlayer.playBackgroundMusic(Sound.MENU_MUSIC);
        this.switchScene(SceneType.MENU_SCENE);
        this.maximumLevelReached = 1;
    }

    /**
     * Establish if a command can be handled.
     * @return
     *          True if the {@link GameState}'s {@link StateEnum} is RUN
     *          or WAITING_FOR_STARTING_COMMAND.
     *          False if the {@link GameState}'s {@link StateEnum} is different.
     */
    private boolean conditionInsertCommand() {
        return this.getGameState().getState() == StateEnum.RUN
                || this.getGameState().getState()
                == StateEnum.WAITING_FOR_STARTING_COMMAND;
    }
    /**
     * Create and start a new {@link GameLoop} instance.
     */
    private void createGameLoop() {
        this.gameLoop = new GameLoopImpl(this);
        this.gameLoop.start();
    }
    /**
     * Create a new {@link GameState} instance.
     */
    private void createGameState() {
        this.gameState = new GameStateImpl();
    }
    /**
     * Create a new {@link InputHandler} instance and bind it with the
     * current {@link Stage}.
     * @param inputStage
     *          The {@link Stage} to bind.
     */
    private void createInputHandler(final Stage inputStage) {
        this.inputHandler = new InputHandlerImpl();
        this.inputHandler.bindCommands(this, inputStage);
    }
    /**
     * Create a new {@link SceneHandler} instance and bind it with the
     * current {@link Stage}.
     * @param inputStage
     *          The {@link Stage} to bind.
     */
    private void createSceneHandler(final Stage inputStage) {
        this.sceneHandler = new SceneHandlerImpl(inputStage, this);
    }
    /**
     * Switch the actual {@link SceneType} to the input {@link SceneType}.
     * @param inputSceneType
     *          The {@link SceneType} to load.
     */
    private void switchScene(final SceneType inputSceneType) {
        this.sceneHandler.switchScene(inputSceneType);
    }
    @Override
    public final void endLevel() {
        if (this.gameState.getState() != StateEnum.PAUSE) {
            this.gameState.setState(StateEnum.PAUSE);
        }
        this.gameState.updateTotalScore();
        this.switchScene(SceneType.END_LEVEL_SCENE);
    }
    @Override
    public final GameState getGameState() {
        return this.gameState;
    }
    @Override
    public final int getMaximumLevelReached() {
        return this.maximumLevelReached;
    }
    @Override
    public final void insertCommand(final Command<GameState> gameCommand) {
        if (this.conditionInsertCommand()) {
            gameCommand.execute(this.gameState);
        }
    }
    @Override
    public final void menu() {
        this.switchScene(SceneType.MENU_SCENE);
        if (this.gameState.getState() == StateEnum.PAUSE) {
            this.maximumLevelReached = Math.max(this.maximumLevelReached,
                    this.gameState.getCurrentLevel().getLevelNumber()
                            + (this.gameState.getCurrentLevel().getLevelStatus()
                            == LevelStatus.SUCCESSFULLY_COMPLETED ? 1 : 0));
            this.gameState.reset();
            this.gameState.setState(StateEnum.WAITING_FOR_NEW_GAME);
        }
    }
    @Override
    public final void pause() {
        if (this.gameState.getState() != StateEnum.PAUSE) {
            this.gameState.setState(StateEnum.PAUSE);
            this.gameState.getCurrentLevel().getArena().getPowerupHandler().pause();
        }
        this.switchScene(SceneType.PAUSE_SCENE);
    }
    @Override
    public final void quit() {
        this.gameState.setState(StateEnum.STOP);
        this.gameState.getCurrentLevel().getArena().cleanUp();
        SoundPlayer.shutdown();
        this.sceneHandler.close();
    }
    @Override
    public final void render() {
        if (this.sceneHandler.getCurrentController().getView() instanceof RenderableView) {
            ((RenderableView) this.sceneHandler.getCurrentController().getView()).render();
        }
    }
    @Override
    public final void resume() {
        this.switchScene(SceneType.GAME_SCENE);
        this.gameState.setState(StateEnum.RUN);
        this.gameState.getCurrentLevel().getArena().getPowerupHandler().resume();
    }
    @Override
    public final void run() {
        this.switchScene(SceneType.GAME_SCENE);
        this.render();
        this.gameState.setState(StateEnum.WAITING_FOR_STARTING_COMMAND);
    }
    @Override
    public final void selectLevel() {
        this.switchScene(SceneType.SELECT_LEVEL_SCENE);
    }
    @Override
    public final void settings() {
        this.switchScene(SceneType.SETTINGS_SCENE);
    }
    @Override
    public final void switchLevel() {
        this.gameState.switchLevel();
        this.run();
    }
}
