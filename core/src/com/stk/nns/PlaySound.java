package com.stk.nns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class PlaySound {

    private Sound eatSound;
    private Sound burpSound;
    private Sound gameOverSound;

    public PlaySound() {
        eatSound = Gdx.audio.newSound(Gdx.files.internal("sound/apple-crunch.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound/fart2.wav"));
        burpSound = Gdx.audio.newSound(Gdx.files.internal("sound/burp1.wav"));
    }

    public void burp() {
        burpSound.play();
    }

    public void eat() {
        eatSound.play();
    }

    public void gameOver() {
        gameOverSound.play();
    }
}
