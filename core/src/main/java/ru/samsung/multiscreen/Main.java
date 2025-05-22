package ru.samsung.multiscreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Main extends Game {
    public static final float SCR_WIDTH = 900;
    public static final float SCR_HEIGHT = 1600;

    public OrthographicCamera camera;
    public SpriteBatch batch;
    public Vector3 touch;
    public BitmapFont font;

    ScreenMenu screenMenu;
    ScreenGame screenGame;
    ScreenSettings screenSettings;
    ScreenAbout screenAbout;
    ScreenVillage screenVillage;
    ScreenBattle screenBattle;
    ScreenMap screenMap; // Added field for screenMap

    public int totalWood = 0;
    public int totalIron = 0;
    public int totalStone = 0;
    public int forgeLevel = 0;
    public int lazaretLevel = 0;
    public int barracksLevel = 0;

    private Preferences prefs;
    public boolean isMusicEnabled = true;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
        touch = new Vector3();
        font = new BitmapFont(Gdx.files.internal("Font2.fnt"));

        prefs = Gdx.app.getPreferences("MultiScreenGame");
        loadGameState();

        screenMenu = new ScreenMenu(this);
        screenGame = new ScreenGame(this);
        screenSettings = new ScreenSettings(this);
        screenAbout = new ScreenAbout(this);
        screenVillage = new ScreenVillage(this);
        screenMap = new ScreenMap(this);

        setScreen(screenMenu);
    }

    private void loadGameState() {
        totalWood = prefs.getInteger("totalWood", 0);
        totalIron = prefs.getInteger("totalIron", 0);
        totalStone = prefs.getInteger("totalStone", 0);
        forgeLevel = prefs.getInteger("forgeLevel", 0);
        lazaretLevel = prefs.getInteger("lazaretLevel", 0);
        barracksLevel = prefs.getInteger("barracksLevel", 0);
        isMusicEnabled = prefs.getBoolean("isMusicEnabled", true);

    }

    public void saveGameState() {
        prefs.putInteger("totalWood", totalWood);
        prefs.putInteger("totalIron", totalIron);
        prefs.putInteger("totalStone", totalStone);
        prefs.putInteger("forgeLevel", forgeLevel);
        prefs.putInteger("lazaretLevel", lazaretLevel);
        prefs.putInteger("barracksLevel", barracksLevel);
        prefs.putBoolean("isMusicEnabled", isMusicEnabled);
        prefs.flush();

    }

    @Override
    public void pause() {
        saveGameState();
        super.pause();
    }

    @Override
    public void dispose() {
        saveGameState();
        batch.dispose();
        font.dispose();
        if (screenBattle != null) {
            screenBattle.dispose();
        }
        super.dispose();
    }
}
