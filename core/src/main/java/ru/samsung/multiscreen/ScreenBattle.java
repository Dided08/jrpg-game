package ru.samsung.multiscreen;

import static ru.samsung.multiscreen.Main.SCR_HEIGHT;
import static ru.samsung.multiscreen.Main.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Класс экрана битвы, реализующий интерфейс Screen для управления игровым процессом боя
public class ScreenBattle implements Screen {
    Main main;
    SpriteBatch batch;
    BitmapFont font;
    Vector3 touch;
    ShapeRenderer shapeRenderer;
    Music battleMusic;
    Music victoryMusic;
    Music loseMusic;
    Texture imgBG;
    String enemyName;
    Texture imgKnight;
    Texture imgMage;
    Texture imgPaladin;
    Texture imgEnemy;
    PokeButton[] abilityButtons = new PokeButton[4];
    BattleCharacter knight;
    BattleCharacter paladin;
    BattleCharacter mage;
    BattleCharacter enemy;
    List<BattleCharacter> heroes = new ArrayList<>();
    BattleCharacter selectedHero = null;
    boolean battleEnded = false;
    boolean victory = false;
    float battleEndTime = 0;
    int woodCount = 0;
    int ironCount = 0;
    int stoneCount = 0;
    private final Map<String, String> abilityDescriptions = new HashMap<>();
    private String enemyActionText = "";
    private float enemyActionTimer = 0f;
    private final int initialEnemyHp;

    // Конструктор класса, инициализирующий экран битвы
    public ScreenBattle(Main main, String enemyName) {
        this.main = main;
        this.enemyName = enemyName;
        this.batch = main.batch;
        this.font = main.font;
        this.touch = main.touch;
        this.shapeRenderer = new ShapeRenderer();

        // Инициализация шрифта, если он отсутствует
        if (this.font == null) {
            this.font = new BitmapFont();
        }

        // Инициализация персонажей с учётом бонусов от уровней зданий
        float damageBoost = main.forgeLevel * 3;
        int hpBoost = main.lazaretLevel * 5;
        int spdBoost = main.barracksLevel; // Simplified: removed * 1
        knight = new BattleCharacter("Knight", 100 + hpBoost, 20 + damageBoost, 0, 0.2f, 0.1f, 10 + spdBoost);
        paladin = new BattleCharacter("Paladin", 110 + hpBoost, 15 + damageBoost, 5, 0.2f, 0.2f, 8 + spdBoost);
        mage = new BattleCharacter("Mage", 80 + hpBoost, 5 + damageBoost, 25 + damageBoost, 0.1f, 0.3f, 12 + spdBoost);
        heroes.add(knight);
        heroes.add(paladin);
        heroes.add(mage);

        // Инициализация врага в зависимости от его имени
        if (enemyName.equals("Dwarf")) {
            enemy = new BattleCharacter("Dwarf", 10, 18, 0, 0.3f, 0.1f, 9);
            initialEnemyHp = 10;
        } else if (enemyName.equals("Golem")) {
            enemy = new BattleCharacter("Golem", 15, 20, 0, 0.4f, 0.1f, 7);
            initialEnemyHp = 15;
        } else {
            enemy = new BattleCharacter("Ent", 10, 18, 0, 0.3f, 0.1f, 9);
            initialEnemyHp = 10;
        }

        // Инициализация кнопок способностей
        for (int i = 0; i < 4; i++) {
            abilityButtons[i] = new PokeButton(font, "", 0, 0);
            abilityButtons[i].font.getData().setScale(0.5f);
        }

        // Заполнение описаний способностей
        abilityDescriptions.put("Knight_Sword", "Deals physical damage.");
        abilityDescriptions.put("Knight_Shield", "Increasing all heroes’ defense for 2 turns.");
        abilityDescriptions.put("Knight_Bleed", "Dealing damage and bleeding for 3 turns.");
        abilityDescriptions.put("Knight_Trip", "Reduce enemy speed for 2 turns.");
        abilityDescriptions.put("Paladin_Sword", "Deals physical damage.");
        abilityDescriptions.put("Paladin_M.Shield", "Boost all heroes’ magic defense for 2 turns.");
        abilityDescriptions.put("Paladin_Curse", "Lowers enemy magic defense for 2 turns.");
        abilityDescriptions.put("Paladin_Sleep", "Cause enemy to skip a turn for 2 turns.");
        abilityDescriptions.put("Mage_Bolt", "Deals magical damage.");
        abilityDescriptions.put("Mage_Speed", "Increase speed for 2 turns.");
        abilityDescriptions.put("Mage_Weak", "Reduce enemy physical damage for 2 turns.");
        abilityDescriptions.put("Mage_Blind", "Cause enemy to miss attacks for 2 turns.");
    }

    // Выполнение действий при показе экрана битвы
    @Override
    public void show() {
        if (main.isMusicEnabled) {
            try {
                battleMusic = Gdx.audio.newMusic(Gdx.files.internal("battle.mp3"));
                battleMusic.setLooping(true);
                battleMusic.play();
                victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("victory.mp3"));
                loseMusic = Gdx.audio.newMusic(Gdx.files.internal("lose.mp3"));
            } catch (Exception e) {
                Gdx.app.log("ScreenBattle", "Failed to load music: " + e.getMessage());
            }
        }

        try {
            imgBG = new Texture("arena.jpg");
            imgKnight = new Texture("knight.png");
            imgMage = new Texture("mage.png");
            imgPaladin = new Texture("paladin.png");

            switch (enemyName) {
                case "Dwarf":
                    imgEnemy = new Texture("dwarf.png");
                    break;
                case "Golem":
                    imgEnemy = new Texture("golem.png");
                    break;
                default:
                    imgEnemy = new Texture("ent.png");
                    break;
            }
        } catch (Exception e) {
            Gdx.app.log("ScreenBattle", "Failed to load texture: " + e.getMessage());
        }
    }

    // Рендеринг экрана битвы
    @Override
    public void render(float delta) {
        // Обработка завершения боя
        if (battleEnded) {
            battleEndTime += delta;
            if (battleEndTime >= 3) {
                if (main.screenMap != null) {
                    main.screenMap.onBattleEnd(enemyName, victory);
                } else {
                    main.setScreen(main.screenGame);
                }
                return;
            }

            batch.setProjectionMatrix(main.camera.combined);
            batch.begin();
            if (imgBG != null) batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

            // Отображение результата боя
            font.getData().setScale(2.0f);
            if (victory) {
                font.setColor(Color.GREEN);
                font.draw(batch, "Victory!", 240, 1000);
                font.setColor(Color.WHITE);
                String resourceText = "";
                switch (enemyName) {
                    case "Ent":
                        resourceText = "+" + woodCount + " Wood";
                        break;
                    case "Dwarf":
                        resourceText = "+" + ironCount + " Iron";
                        break;
                    case "Golem":
                        resourceText = "+" + stoneCount + " Stone";
                        break;
                }
                font.draw(batch, resourceText, 260, 850);
            } else {
                font.setColor(Color.RED);
                font.draw(batch, "Lose", 330, 1000);
            }
            font.getData().setScale(1.0f);
            batch.end();
            return;
        }

        // Обновление таймера действия врага
        if (enemyActionTimer > 0) {
            enemyActionTimer -= delta;
            if (enemyActionTimer <= 0) {
                enemyActionText = "";
            }
        }

        // Обработка касания для удержания кнопки
        int heldButtonIndex = -1;
        if (Gdx.input.isTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);
            if (selectedHero != null) {
                for (int i = 0; i < 4; i++) {
                    if (abilityButtons[i].hit(touch.x, touch.y)) {
                        heldButtonIndex = i;
                        break;
                    }
                }
            }
        }

        // Обработка выбора героя и использования способности
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);

            if (touch.y > 400 && touch.y < 550) {
                if (touch.x > 180 && touch.x < 330 && knight.isAlive()) {
                    selectedHero = knight;
                    setHeroAbilities(knight);
                    positionAbilityButtons();
                } else if (touch.x > 360 && touch.x < 510 && paladin.isAlive()) {
                    selectedHero = paladin;
                    setHeroAbilities(paladin);
                    positionAbilityButtons();
                } else if (touch.x > 540 && touch.x < 690 && mage.isAlive()) {
                    selectedHero = mage;
                    setHeroAbilities(mage);
                    positionAbilityButtons();
                }
            }

            if (selectedHero != null) {
                for (int i = 0; i < 4; i++) {
                    if (abilityButtons[i].hit(touch.x, touch.y)) {
                        useAbility(selectedHero, i);
                        selectedHero = null;
                        break;
                    }
                }
            }
        }

        batch.setProjectionMatrix(main.camera.combined);
        batch.begin();

        if (imgBG != null) batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        if (imgEnemy != null) batch.draw(imgEnemy, 360, 950, 210, 210);
        if (imgKnight != null && knight.isAlive()) batch.draw(imgKnight, 180, 400, 150, 150);
        else if (imgKnight != null && !knight.isAlive()) {
            batch.setColor(1, 1, 1, 0.5f);
            batch.draw(imgKnight, 180, 400, 150, 150);
            batch.setColor(1, 1, 1, 1);
        }
        if (imgPaladin != null && paladin.isAlive()) batch.draw(imgPaladin, 360, 400, 150, 150);
        else if (imgPaladin != null && !paladin.isAlive()) {
            batch.setColor(1, 1, 1, 0.5f);
            batch.draw(imgPaladin, 360, 400, 150, 150);
            batch.setColor(1, 1, 1, 1);
        }
        if (imgMage != null && mage.isAlive()) batch.draw(imgMage, 540, 400, 150, 150);
        else if (imgMage != null && !mage.isAlive()) {
            batch.setColor(1, 1, 1, 0.5f);
            batch.draw(imgMage, 540, 400, 150, 150);
            batch.setColor(1, 1, 1, 1);
        }

        font.setColor(Color.RED);
        font.getData().setScale(1.0f);
        font.draw(batch, "HP: " + Math.max(enemy.hp, 0), 360, 900);

        if (enemyActionTimer > 0 && !enemyActionText.isEmpty()) {
            font.setColor(Color.YELLOW);
            font.getData().setScale(0.8f);
            font.draw(batch, enemyActionText, 360, 850);
            StringBuilder effectsText = new StringBuilder();
            if (enemy.blindTurns > 0) effectsText.append("Blind: ").append(enemy.blindTurns).append(" turns ");
            if (enemy.bleedingTurns > 0) effectsText.append("Bleeding: ").append(enemy.bleedingTurns).append(" turns ");
            if (enemy.sleepTurns > 0) effectsText.append("Sleep: ").append(enemy.sleepTurns).append(" turns ");
            if (effectsText.length() > 0) {
                font.draw(batch, effectsText.toString(), 360, 800);
            }
            font.getData().setScale(1.0f);
        }

        if (knight.isAlive()) font.draw(batch, "HP: " + Math.max(knight.hp, 0), 120, 600);
        if (paladin.isAlive()) font.draw(batch, "HP: " + Math.max(paladin.hp, 0), 360, 600);
        if (mage.isAlive()) font.draw(batch, "HP: " + Math.max(mage.hp, 0), 600, 600);

        if (selectedHero != null && selectedHero.isAlive()) {
            for (PokeButton button : abilityButtons) {
                button.render(batch, Color.WHITE);
            }
        }

        if (selectedHero != null && heldButtonIndex >= 0) {
            String abilityKey = selectedHero.name + "_" + abilityButtons[heldButtonIndex].text;
            String description = abilityDescriptions.getOrDefault(abilityKey, "No description available.");
            font.setColor(Color.WHITE);
            font.getData().setScale(0.8f);
            font.draw(batch, description, SCR_WIDTH / 2f - 450, 350);
            font.getData().setScale(1.0f);
        }

        batch.end();
    }

    // Установка способностей для выбранного героя
    void setHeroAbilities(BattleCharacter hero) {
        if (hero == knight) {
            abilityButtons[0].setText("Sword");
            abilityButtons[1].setText("Shield");
            abilityButtons[2].setText("Bleed");
            abilityButtons[3].setText("Trip");
        } else if (hero == paladin) {
            abilityButtons[0].setText("Sword");
            abilityButtons[1].setText("M.Shield");
            abilityButtons[2].setText("Curse");
            abilityButtons[3].setText("Sleep");
        } else if (hero == mage) {
            abilityButtons[0].setText("Bolt");
            abilityButtons[1].setText("Speed");
            abilityButtons[2].setText("Weak");
            abilityButtons[3].setText("Blind");
        }
    }

    // Расположение кнопок способностей на экране
    void positionAbilityButtons() {
        float buttonWidth = 110;
        float startX = 20;
        float spacing = 130;
        float y = 200;

        abilityButtons[0].setPositionBottomLeft(startX, y);
        abilityButtons[1].setPositionBottomLeft(startX + buttonWidth + spacing, y);
        abilityButtons[2].setPositionBottomLeft(startX + 2 * (buttonWidth + spacing), y);
        abilityButtons[3].setPositionBottomLeft(startX + 3 * (buttonWidth + spacing), y);
    }

    // Использование способности выбранным героем
    void useAbility(BattleCharacter hero, int index) {
        if (battleEnded) {
            return;
        }

        boolean heroFirst = hero.spd >= enemy.spd;

        if (heroFirst) {
            heroAction(hero, index);
            if (!battleEnded && enemy.isAlive()) {
                enemyAction();
            }
        } else {
            enemyAction();
            if (!battleEnded && heroes.stream().anyMatch(BattleCharacter::isAlive)) {
                heroAction(hero, index);
            }
        }

        updateTurnEffects();
    }

    // Обновление эффектов для всех персонажей
    void updateTurnEffects() {
        for (BattleCharacter h : heroes) {
            h.updateBuffs();
        }
        enemy.updateBuffs();
        if (enemy.blindTurns > 0) enemy.blindTurns--;
        if (enemy.bleedingTurns > 0) enemy.bleedingTurns--;
        if (enemy.sleepTurns > 0) enemy.sleepTurns--;
    }

    // Действие героя в бою
    void heroAction(BattleCharacter hero, int index) {
        if (battleEnded) {
            return;
        }

        if (!hero.isAlive()) {
            return;
        }

        switch (hero.name) {
            case "Knight":
                if (index == 0)
                    enemy.hp -= (int)(2 * hero.physDmg - hero.physDmg * enemy.def);
                else if (index == 1)
                    heroes.forEach(h -> h.applyBuff("def", 0.5f, 2));
                else if (index == 2) {
                    enemy.bleeding = true;
                    enemy.bleedingTurns = 3;
                    enemy.hp -= (int)(0.5f * hero.physDmg * (1 - enemy.def));
                } else if (index == 3)
                    enemy.applyBuff("spd", -5, 2);
                break;
            case "Paladin":
                if (index == 0)
                    enemy.hp -= (int)(2 * hero.physDmg - hero.physDmg * enemy.def);
                else if (index == 1)
                    heroes.forEach(h -> h.applyBuff("magDef", 0.5f, 2));
                else if (index == 2)
                    enemy.applyBuff("magDef", -0.5f, 2);
                else if (index == 3)
                    enemy.sleepTurns = 2;
                break;
            case "Mage":
                if (index == 0)
                    enemy.hp -= (int)(2 * hero.magDmg - hero.magDmg * enemy.magDef);
                else if (index == 1)
                    heroes.forEach(h -> h.applyBuff("spd", 5, 2));
                else if (index == 2)
                    enemy.applyBuff("physDmg", -3, 2);
                else if (index == 3) {
                    enemy.blind = true;
                    enemy.blindTurns = 2;
                }
                break;
        }

        checkBattleEnd();
    }

    // Действие врага в бою
    void enemyAction() {
        if (battleEnded) {
            return;
        }

        if (enemy.hp <= 0) {
            endBattle(true);
            return;
        }

        if (enemy.sleepTurns > 0) {
            enemyActionText = "Enemy is asleep and skips turn!";
            enemyActionTimer = 2.0f;
            return;
        }

        if (enemy.bleeding && enemy.bleedingTurns > 0) {
            enemy.hp -= 3;
            enemyActionText = "Enemy takes bleeding damage!";
            enemyActionTimer = 2.0f;
            if (enemy.hp <= 0) {
                endBattle(true);
                return;
            }
        }

        Random r = new Random();
        if (enemy.blind && enemy.blindTurns > 0 && r.nextBoolean()) {
            enemyActionText = "Enemy is blinded and misses!";
            enemyActionTimer = 2.0f;
            return;
        }

        int choice;
        if (enemy.hp > 0.6 * initialEnemyHp) {
            choice = 0;
        } else {
            choice = r.nextInt(4);
        }

        switch (enemy.name) {
            case "Ent":
                switch (choice) {
                    case 0:
                        enemyActionText = "Ent attacks all heroes!";
                        for (BattleCharacter h : heroes) {
                            if (h.isAlive()) {
                                h.hp -= (int)(2 * enemy.physDmg - enemy.physDmg * h.def);
                            }
                        }
                        break;
                    case 1:
                        enemyActionText = "Ent increases defense!";
                        enemy.applyBuff("def", 0.5f, 2);
                        break;
                    case 2:
                        enemyActionText = "Ent slows heroes!";
                        heroes.forEach(h -> h.applyBuff("spd", -5, 2));
                        break;
                    case 3:
                        enemyActionText = "Ent stuns heroes!";
                        heroes.forEach(h -> h.sleepTurns = 2);
                        break;
                }
                break;
            case "Dwarf":
                switch (choice) {
                    case 0:
                        enemyActionText = "Dwarf uses Pickaxe Attack!";
                        for (BattleCharacter h : heroes) {
                            if (h.isAlive()) {
                                h.hp -= (int)(2 * enemy.physDmg - enemy.physDmg * h.def);
                            }
                        }
                        break;
                    case 1:
                        enemyActionText = "Dwarf uses Dwarven Boots!";
                        enemy.applyBuff("spd", 5, 2);
                        break;
                    case 2:
                        enemyActionText = "Dwarf uses Dust in Eyes!";
                        heroes.forEach(h -> {
                            h.blind = true;
                            h.blindTurns = 2;
                        });
                        break;
                    case 3:
                        enemyActionText = "Dwarf uses Dwarven Agility!";
                        heroes.forEach(h -> h.applyBuff("physDmg", -5, 2));
                        break;
                }
                break;
            case "Golem":
                switch (choice) {
                    case 0:
                        enemyActionText = "Golem uses Boulder Strike!";
                        for (BattleCharacter h : heroes) {
                            if (h.isAlive()) {
                                h.hp -= (int)(2 * enemy.physDmg - enemy.physDmg * h.def);
                            }
                        }
                        break;
                    case 1:
                        enemyActionText = "Golem uses Natural Strength!";
                        enemy.applyBuff("physDmg", 5, 2);
                        break;
                    case 2:
                        enemyActionText = "Golem uses Rockfall!";
                        heroes.forEach(h -> h.sleepTurns = 2);
                        break;
                    case 3:
                        enemyActionText = "Golem uses Unsteady Stones!";
                        heroes.forEach(h -> h.applyBuff("def", -0.5f, 2));
                        break;
                }
                break;
        }
        if (!enemyActionText.isEmpty()) {
            enemyActionTimer = 2.0f;
        }

        checkBattleEnd();
    }

    // Проверка завершения боя
    private void checkBattleEnd() {
        if (battleEnded) {
            return;
        }

        if (enemy.hp <= 0) {
            endBattle(true);
        } else if (heroes.stream().noneMatch(h -> h.hp > 0)) {
            endBattle(false);
        }
    }

    // Завершение боя с указанием результата
    private void endBattle(boolean victory) {
        if (battleEnded) {
            return;
        }

        this.victory = victory;
        battleEnded = true;
        battleEndTime = 0;

        if (battleMusic != null && battleMusic.isPlaying()) {
            battleMusic.stop();
        }

        if (main.isMusicEnabled) {
            if (victory) {
                if (victoryMusic != null) {
                    victoryMusic.play();
                }
            } else {
                if (loseMusic != null) {
                    loseMusic.play();
                }
            }
        }

        if (victory) {
            switch (enemy.name) {
                case "Ent":
                    woodCount += 1;
                    main.totalWood += woodCount;
                    main.saveGameState();
                    break;
                case "Dwarf":
                    ironCount += 1;
                    main.totalIron += ironCount;
                    main.saveGameState();
                    break;
                case "Golem":
                    stoneCount += 1;
                    main.totalStone += stoneCount;
                    main.saveGameState();
                    break;
            }
        }
    }

    // Выполнение действий при скрытии экрана битвы
    @Override
    public void hide() {
        if (battleMusic != null && battleMusic.isPlaying()) {
            battleMusic.stop();
        }
        if (victoryMusic != null && victoryMusic.isPlaying()) {
            victoryMusic.stop();
        }
        if (loseMusic != null && loseMusic.isPlaying()) {
            loseMusic.stop();
        }
        if (imgBG != null) imgBG.dispose();
        if (imgKnight != null) imgKnight.dispose();
        if (imgMage != null) imgMage.dispose();
        if (imgPaladin != null) imgPaladin.dispose();
        if (imgEnemy != null) imgEnemy.dispose();
        if (battleMusic != null) battleMusic.dispose();
        if (victoryMusic != null) victoryMusic.dispose();
        if (loseMusic != null) loseMusic.dispose();
    }

    // Освобождение ресурсов при уничтожении экрана
    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
