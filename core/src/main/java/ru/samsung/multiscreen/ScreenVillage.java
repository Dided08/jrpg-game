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

public class ScreenVillage implements Screen {
    Main main;
    SpriteBatch batch;
    BitmapFont font;
    Vector3 touch;
    Texture imgBG;
    Texture imgForge, imgBarracks, imgLazaret;
    PokeButton btnBack;

    // Размеры и позиции иконок
    private final float ICON_WIDTH = 200;
    private final float ICON_HEIGHT = 200;
    private final float FORGE_X = SCR_WIDTH / 2f - ICON_WIDTH / 2;
    private final float FORGE_Y = 1200;
    private final float BARRACKS_X = SCR_WIDTH / 2f - ICON_WIDTH / 2;
    private final float BARRACKS_Y = 1000;
    private final float LAZARET_X = SCR_WIDTH / 2f - ICON_WIDTH / 2;
    private final float LAZARET_Y = 800;

    public ScreenVillage(Main main) {
        this.main = main;
        this.batch = main.batch;
        this.font = main.font;
        this.touch = main.touch;

        imgBG = new Texture("village.jpg");
        System.out.println("Loading background: village.jpg");

        // Загрузка иконок
        imgForge = new Texture("forge.png");
        imgBarracks = new Texture("barracks.png");
        imgLazaret = new Texture("lazaret.png");
        System.out.println("Images loaded: Forge=forge.png (" + (imgForge != null) + "), Barracks=barracks.png (" +
            (imgBarracks != null) + "), Lazaret=lazaret.png (" + (imgLazaret != null) + ")");

        if (this.font == null) {
            System.out.println("ERROR: Font is null! Check Main.font initialization.");
            this.font = new BitmapFont();
        } else {
            System.out.println("Font initialized successfully.");
        }

        btnBack = new PokeButton(font, "Back", SCR_WIDTH / 2f, 200);
        btnBack.font.getData().setScale(1.0f);
        System.out.println("Back button initialized at x=" + btnBack.x + ", y=" + btnBack.y);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);
            System.out.println("Touch at: x=" + touch.x + ", y=" + touch.y);

            // Проверка клика по иконкам
            if (touch.x > FORGE_X && touch.x < FORGE_X + ICON_WIDTH && touch.y > FORGE_Y && touch.y < FORGE_Y + ICON_HEIGHT) {
                if (main.totalWood >= 1 && main.totalIron >= 1) {
                    main.totalWood -= 1;
                    main.totalIron -= 1;
                    main.forgeLevel++;
                    // main.saveToFile(); // Commented out until defined in Main
                    System.out.println("Forge upgraded! Level: " + main.forgeLevel + ", Wood: " + main.totalWood + ", Iron: " + main.totalIron);
                } else {
                    System.out.println("Not enough resources for Forge upgrade (Need: 1 Wood, 1 Iron)");
                }
            }
            if (touch.x > BARRACKS_X && touch.x < BARRACKS_X + ICON_WIDTH && touch.y > BARRACKS_Y && touch.y < BARRACKS_Y + ICON_HEIGHT) {
                if (main.totalWood >= 1 && main.totalStone >= 1) {
                    main.totalWood -= 1;
                    main.totalStone -= 1;
                    main.barracksLevel++;
                    // main.saveToFile(); // Commented out until defined in Main
                    System.out.println("Barracks upgraded! Level: " + main.barracksLevel + ", Wood: " + main.totalWood + ", Stone: " + main.totalStone);
                } else {
                    System.out.println("Not enough resources for Barracks upgrade (Need: 1 Wood, 1 Stone)");
                }
            }
            if (touch.x > LAZARET_X && touch.x < LAZARET_X + ICON_WIDTH && touch.y > LAZARET_Y && touch.y < LAZARET_Y + ICON_HEIGHT) {
                if (main.totalIron >= 1 && main.totalStone >= 1) {
                    main.totalIron -= 1;
                    main.totalStone -= 1;
                    main.lazaretLevel++;
                    // main.saveToFile(); // Commented out until defined in Main
                    System.out.println("Lazaret upgraded! Level: " + main.lazaretLevel + ", Iron: " + main.totalIron + ", Stone: " + main.totalStone);
                } else {
                    System.out.println("Not enough resources for Lazaret upgrade (Need: 1 Iron, 1 Stone)");
                }
            }
            if (btnBack.hit(touch.x, touch.y)) {
                if (main.screenFightSelect != null) {
                    System.out.println("Back button clicked, transitioning to ScreenFightSelect");
                    main.setScreen(main.screenGame);
                } else {
                    System.out.println("ERROR: screenFightSelect is null!");
                }
            }
        }

        batch.setProjectionMatrix(main.camera.combined);
        batch.begin();

        batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

        // Отрисовка иконок
        batch.draw(imgForge, FORGE_X, FORGE_Y, ICON_WIDTH, ICON_HEIGHT);
        batch.draw(imgBarracks, BARRACKS_X, BARRACKS_Y, ICON_WIDTH, ICON_HEIGHT);
        batch.draw(imgLazaret, LAZARET_X, LAZARET_Y, ICON_WIDTH, ICON_HEIGHT);

        // Отрисовка кнопки Back
        btnBack.render(batch, Color.BLACK);

        // Отрисовка ресурсов и уровней
        font.setColor(Color.BROWN);
        font.getData().setScale(1.0f);
        font.draw(batch, "Wood: " + main.totalWood, 40, SCR_HEIGHT - 50);
        font.setColor(Color.DARK_GRAY);
        font.draw(batch, "Iron: " + main.totalIron, 40, SCR_HEIGHT - 100);
        font.setColor(Color.BLACK);
        font.draw(batch, "Stone: " + main.totalStone, 40, SCR_HEIGHT - 150);

        font.setColor(Color.YELLOW);
        font.draw(batch, "Forge Level: " + main.forgeLevel, SCR_WIDTH / 2f - 100, FORGE_Y + ICON_HEIGHT + 30);
        font.draw(batch, "+Dmg", SCR_WIDTH / 2f + 80, FORGE_Y + ICON_HEIGHT - 80);
        font.draw(batch, "Barracks Level: " + main.barracksLevel, SCR_WIDTH / 2f - 100, BARRACKS_Y + ICON_HEIGHT + 30);
        font.draw(batch, "+Def", SCR_WIDTH / 2f + 80, BARRACKS_Y + ICON_HEIGHT - 80);
        font.draw(batch, "Lazaret Level: " + main.lazaretLevel, SCR_WIDTH / 2f - 100, LAZARET_Y + ICON_HEIGHT + 30);
        font.draw(batch, "+HP", SCR_WIDTH / 2f + 80, LAZARET_Y + ICON_HEIGHT - 80);


        batch.end();
    }

    @Override
    public void show() {
        System.out.println("ScreenVillage shown");
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("ScreenVillage resized to width: " + width + ", height: " + height);
    }

    @Override
    public void pause() {
        System.out.println("ScreenVillage paused");
    }

    @Override
    public void resume() {
        System.out.println("ScreenVillage resumed");
    }

    @Override
    public void hide() {
        System.out.println("ScreenVillage hidden");
    }

    @Override
    public void dispose() {
        imgBG.dispose();
        imgForge.dispose();
        imgBarracks.dispose();
        imgLazaret.dispose();
        System.out.println("ScreenVillage disposed");
    }
}
