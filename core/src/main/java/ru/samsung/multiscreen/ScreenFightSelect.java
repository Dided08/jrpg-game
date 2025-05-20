package ru.samsung.multiscreen;

import static ru.samsung.multiscreen.Main.SCR_HEIGHT;
import static ru.samsung.multiscreen.Main.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenFightSelect implements Screen {
    Main main;
    public SpriteBatch batch;
    public BitmapFont font;
    public Vector3 touch;

    Texture imgBG;

    PokeButton btnEnt1;
    PokeButton btnGnome; // Добавлено для гнома
    PokeButton btnGolem; // Добавлено для голема
    PokeButton btnBack;

    public ScreenFightSelect(Main main) {
        this.main = main;
        this.batch = main.batch;
        this.font = main.font;
        this.touch = main.touch;

        imgBG = new Texture("battle.jpg");

        btnEnt1 = new PokeButton(font, "Ent (Wood)", 330, 1100);
        btnEnt1.font.getData().setScale(1.0f);
        btnGnome = new PokeButton(font, "Gnome (Iron)", 300, 950);
        btnGnome.font.getData().setScale(1.0f);
        btnGolem = new PokeButton(font, "Golem (Stone)", 300, 800);
        btnGolem.font.getData().setScale(1.0f);
        btnBack = new PokeButton(font, "Back", 360, 300);
        btnBack.font.getData().setScale(1.0f);

        System.out.println("ScreenFightSelect initialized. Font: " + (font != null));
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);
            System.out.println("Touch at: x=" + touch.x + ", y=" + touch.y);

            if (btnEnt1.hit(touch.x, touch.y)) {
                System.out.println("Starting battle with Ent 1");
                main.setScreen(new ScreenBattle(main, "Ent"));
            }

            if (btnGnome.hit(touch.x, touch.y)) {
                System.out.println("Starting battle with Gnome");
                main.setScreen(new ScreenBattle(main, "Gnome"));
            }

            if (btnGolem.hit(touch.x, touch.y)) {
                System.out.println("Starting battle with Golem");
                main.setScreen(new ScreenBattle(main, "Golem"));
            }

            if (btnBack.hit(touch.x, touch.y)) {
                System.out.println("Returning to ScreenGame");
                main.setScreen(main.screenGame);
            }
        }

        batch.setProjectionMatrix(main.camera.combined);
        batch.begin();

        batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

        // Заголовок
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        font.draw(batch, "Choose your enemy", 160, SCR_HEIGHT - 50);

        // Ресурсы
        font.setColor(Color.BROWN);
        font.draw(batch, "Total Wood: " + main.totalWood, SCR_WIDTH - 900, SCR_HEIGHT - 150);
        font.setColor(Color.DARK_GRAY);
        font.draw(batch, "Total Iron: " + main.totalIron, SCR_WIDTH - 900, SCR_HEIGHT - 200);
        font.setColor(Color.BLACK);
        font.draw(batch, "Total Stone: " + main.totalStone, SCR_WIDTH - 900, SCR_HEIGHT - 250);

        // Отрисовка кнопок
        btnEnt1.render(batch, Color.RED);
        btnGnome.render(batch, Color.RED);
        btnGolem.render(batch, Color.RED);
        btnBack.render(batch, Color.BLACK);

        // Отладка координат кнопок
        System.out.println("Ent button at x=" + btnEnt1.x + ", y=" + btnEnt1.y + ", text: " + btnEnt1.text);
        System.out.println("Gnome button at x=" + btnGnome.x + ", y=" + btnGnome.y + ", text: " + btnGnome.text);
        System.out.println("Golem button at x=" + btnGolem.x + ", y=" + btnGolem.y + ", text: " + btnGolem.text);
        System.out.println("Back button at x=" + btnBack.x + ", y=" + btnBack.y + ", text: " + btnBack.text);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        imgBG.dispose();
    }
}

