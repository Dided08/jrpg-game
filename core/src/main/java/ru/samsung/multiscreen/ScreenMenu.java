package ru.samsung.multiscreen;


import static ru.samsung.multiscreen.Main.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenMenu implements Screen {
    Main main;
    public OrthographicCamera camera;
    public SpriteBatch batch;
    public Vector3 touch;
    public BitmapFont font;

    Texture imgBG;
    PokeButton btnGame;
    PokeButton btnSettings;
    PokeButton btnAbout;
    PokeButton btnExit;


    public ScreenMenu(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font = main.font;

        imgBG = new Texture("bg5.jpg");

        btnGame = new PokeButton(font,"Play",250,1100);
        btnSettings = new PokeButton(font,"Settings",250,950);
        btnAbout = new PokeButton(font,"About",250,800);
        btnExit = new PokeButton(font,"Exit",260,650);



    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if(btnGame.hit(touch.x, touch.y)) {
                main.setScreen(main.screenGame);
            }
            if(btnSettings.hit(touch.x, touch.y)) {
                main.setScreen(main.screenSettings);
            }
            if(btnAbout.hit(touch.x, touch.y)) {
                main.setScreen(main.screenAbout);
            }
            if(btnExit.hit(touch.x, touch.y)) {
                Gdx.app.exit();
            }
        }


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBG,0,0, SCR_WIDTH, SCR_HEIGHT);
        font.setColor(Color.RED);
        font.draw(batch, "Defenders of Kingdom", 150,1450);
        font.setColor(Color.WHITE);
        font.setColor(Color.YELLOW);
        btnGame.font.draw(batch,btnGame.text,btnGame.x,btnGame.y);
        font.setColor(Color.WHITE);
        btnSettings.font.draw(batch,btnSettings.text,btnSettings.x,btnSettings.y);
        btnAbout.font.draw(batch,btnAbout.text,btnAbout.x,btnAbout.y);
        btnExit.font.draw(batch,btnExit.text,btnExit.x,btnExit.y);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        imgBG.dispose();

    }
}
