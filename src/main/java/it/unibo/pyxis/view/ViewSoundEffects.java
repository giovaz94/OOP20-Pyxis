package it.unibo.pyxis.view;

public interface ViewSoundEffects {
    /**
     * Play the {@link it.unibo.pyxis.view.soundplayer.Sound} of a pushed
     * {@link javafx.scene.control.Button}.
     */
    void playGenericButtonPressSound();
    /**
     * Play the {@link it.unibo.pyxis.view.soundplayer.Sound} of the
     * {@link GameView}.
     */
    void playInGameMusic();
    /**
     * Play the {@link it.unibo.pyxis.view.soundplayer.Sound} of the
     * {@link MenuView}.
     */
    void playMainMenuMusic();
    /**
     * Play the {@link it.unibo.pyxis.view.soundplayer.Sound} of a pushed
     * start game {@link javafx.scene.control.Button}.
     */
    void playStartGameButtonPressSound();
}
