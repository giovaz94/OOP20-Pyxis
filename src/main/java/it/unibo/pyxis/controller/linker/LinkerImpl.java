package it.unibo.pyxis.controller.linker;

import it.unibo.pyxis.controller.engine.GameLoop;
import it.unibo.pyxis.controller.engine.GameLoopImpl;
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

    public LinkerImpl(final Stage inputStage) {
        this.createGameState();
        this.createGameLoop();
        this.createSceneLoader(inputStage);
        this.switchScene(SceneType.MENU_SCENE);
    }
    
    @Override
    public final void pause() {
        this.gameState.setState(StateEnum.PAUSE);
        this.switchScene(SceneType.PAUSE_SCENE);
    }

    @Override
    public final void quit() {
        this.gameState.setState(StateEnum.STOP);
        this.gameState.getCurrentLevel().getArena().cleanup();
        this.sceneHandler.close();
    }

    @Override
    public final void endGame() {
        this.gameState.setState(StateEnum.PAUSE);
        this.switchScene(SceneType.PAUSE_SCENE);
    }

    @Override
    public final void run() {
        this.switchScene(SceneType.GAME_SCENE);
        this.render();
        this.gameLoop.start();
        this.gameState.setState(StateEnum.RUN);
    }

    @Override
    public final void switchScene(final SceneType inputSceneType) {
        this.sceneHandler.switchScene(inputSceneType);
        /*
        if (inputSceneType == SceneType.MENU_SCENE) {
            this.gameState.getCurrentLevel().getArena().cleanup();
            this.gameState.reset();
        }

        */
    }

    private void createGameState() {
        this.gameState = new GameStateImpl();
    }

    @Override
    public final GameState getGameState() {
        return this.gameState;
    }

    private void createGameLoop() {
        this.gameLoop = new GameLoopImpl(this.gameState);
    }

    private void createSceneLoader(final Stage inputStage) {
        this.sceneHandler = new SceneHandlerImpl(inputStage, this);
    }

    @Override
    public final void render() {
        if (this.sceneHandler.getCurrentController().getView() instanceof RenderableView) {
            ((RenderableView) this.sceneHandler.getCurrentController().getView()).render();
        }
    }

    @Override
    public void handleCommandControl() {

    }

    @Override
    public void handleApplicationCommand() {

    }
}
