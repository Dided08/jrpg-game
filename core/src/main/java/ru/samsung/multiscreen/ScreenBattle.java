package ru.samsung.multiscreen;

import static ru.samsung.multiscreen.Main.SCR_HEIGHT;
import static ru.samsung.multiscreen.Main.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScreenBattle implements Screen {
    Main main;
    public SpriteBatch batch;
    public BitmapFont font;
    public Vector3 touch;
    ShapeRenderer shapeRenderer;

    Texture imgBG;
    String enemyName;

    // Текстуры для героев и врагов
    Texture imgKnight, imgMage, imgPaladin;
    Texture imgEnemy;

    PokeButton[] abilityButtons = new PokeButton[4];

    BattleCharacter knight, paladin, mage, enemy;
    List<BattleCharacter> heroes = new ArrayList<>();
    BattleCharacter selectedHero = null;
    boolean heroesTurn = true;
    boolean battleEnded = false;
    boolean victory = false;
    float battleEndTime = 0;

    int woodCount = 0;
    int ironCount = 0;
    int stoneCount = 0;

    public ScreenBattle(Main main, String enemyName) {
        this.main = main;
        this.enemyName = enemyName;
        this.batch = main.batch;
        this.font = main.font;
        this.touch = main.touch;
        this.shapeRenderer = new ShapeRenderer();

        imgBG = new Texture("arena.jpg");
        System.out.println("Loading background: arena.jpg");

        // Загрузка текстур героев
        imgKnight = new Texture("knight.png");
        imgMage = new Texture("mage.png");
        imgPaladin = new Texture("paladin.png");

        // Загрузка текстуры врага
        if (enemyName.equals("Gnome")) {
            imgEnemy = new Texture("gnome.png");
        } else if (enemyName.equals("Golem")) {
            imgEnemy = new Texture("golem.png");
        } else {
            imgEnemy = new Texture("ent.png");
        }

        if (this.font == null) {
            System.out.println("ERROR: Font is null! Check Main.font initialization.");
            this.font = new BitmapFont();
        } else {
            System.out.println("Font initialized successfully.");
        }

        // Инициализация героев с учетом forgeLevel, lazaretLevel, barracksLevel
        float damageBoost = main.forgeLevel * 3;
        int hpBoost = main.lazaretLevel * 5;
        float defBoost = main.barracksLevel * 0.1f;
        knight = new BattleCharacter("Knight", 100 + hpBoost, 20 + damageBoost, 0, 0.2f + defBoost, 0.1f, 10);
        paladin = new BattleCharacter("Paladin", 110 + hpBoost, 15 + damageBoost, 5, 0.2f + defBoost, 0.2f, 8);
        mage = new BattleCharacter("Mage", 80 + hpBoost, 5 + damageBoost, 25 + damageBoost, 0.1f + defBoost, 0.3f, 12);
        heroes.add(knight);
        heroes.add(paladin);
        heroes.add(mage);
        System.out.println("Heroes initialized with damage boost: " + damageBoost + ", hp boost: " + hpBoost + ", def boost: " + defBoost);
        System.out.println("Images loaded: Knight=knight.png (" + (imgKnight != null) + "), Mage=mage.png (" + (imgMage != null) +
            "), Paladin=paladin.png (" + (imgPaladin != null) + "), Enemy=" + enemyName + ".png (" + (imgEnemy != null) + ")");

        // Инициализация врага
        if (enemyName.equals("Gnome")) {
            enemy = new BattleCharacter("Gnome", 10, 18, 0, 0.3f, 0.1f, 9);
        } else if (enemyName.equals("Golem")) {
            enemy = new BattleCharacter("Golem", 15, 20, 0, 0.4f, 0.1f, 7);
        } else {
            enemy = new BattleCharacter("Ent", 10, 18, 0, 0.3f, 0.1f, 9);
        }

        for (int i = 0; i < 4; i++) {
            abilityButtons[i] = new PokeButton(font, "", 0, 0);
            abilityButtons[i].font.getData().setScale(0.5f);
            System.out.println("Button " + i + " initialized with font scale: " + abilityButtons[i].font.getData().scaleX);
        }

        System.out.println("ScreenFightSelect initialized: " + (main.screenFightSelect != null));
    }

    @Override
    public void render(float delta) {
        if (battleEnded) {
            // Обновляем таймер окончания боя
            battleEndTime += delta;
            System.out.println("Post-battle screen, time elapsed: " + battleEndTime);

            // Переход на ScreenFightSelect через 3 секунды
            if (battleEndTime >= 3) {
                if (main.screenFightSelect != null) {
                    System.out.println("Transitioning to ScreenFightSelect after 3 seconds");
                    main.setScreen(main.screenFightSelect);
                } else {
                    System.out.println("ERROR: screenFightSelect is null! Falling back to ScreenGame");
                    main.setScreen(main.screenGame);
                }
                return;
            }

            // Отрисовка экрана окончания боя
            batch.setProjectionMatrix(main.camera.combined);
            batch.begin();
            batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

            font.getData().setScale(2.0f); // Увеличенный текст
            if (victory) {
                font.setColor(Color.GREEN);
                font.draw(batch, "Victory!", 240, 1000);
                font.setColor(Color.WHITE);
                String resourceText = "";
                if (enemyName.equals("Ent")) {
                    resourceText = "+" + woodCount + " Wood";
                } else if (enemyName.equals("Gnome")) {
                    resourceText = "+" + ironCount + " Iron";
                } else if (enemyName.equals("Golem")) {
                    resourceText = "+" + stoneCount + " Stone";
                }
                font.draw(batch, resourceText, 260, 850);
                System.out.println("Rendering victory screen: " + resourceText);
            } else {
                font.setColor(Color.RED);
                font.draw(batch, "Lose", 240, 1000);
                System.out.println("Rendering lose screen");
            }
            font.getData().setScale(1.0f); // Сброс масштаба
            batch.end();
            return;
        }

        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);
            System.out.println("Touch at: x=" + touch.x + ", y=" + touch.y);

            // Проверка выбора героя по области картинки
            if (touch.y > 400 && touch.y < 550) { // y=475 ± 75, aligns with image bounds
                if (touch.x > 180 && touch.x < 330 && knight.isAlive()) { // x=255 ± 75, Knight bounds
                    selectedHero = knight;
                    setHeroAbilities(knight);
                    positionAbilityButtons();
                    System.out.println("Selected Knight via image click (region: x=180-330, y=400-550)");
                } else if (touch.x > 360 && touch.x < 510 && paladin.isAlive()) { // x=435 ± 75, Paladin bounds
                    selectedHero = paladin;
                    setHeroAbilities(paladin);
                    positionAbilityButtons();
                    System.out.println("Selected Paladin via image click (region: x=360-510, y=400-550)");
                } else if (touch.x > 540 && touch.x < 690 && mage.isAlive()) { // x=615 ± 75, Mage bounds
                    selectedHero = mage;
                    setHeroAbilities(mage);
                    positionAbilityButtons();
                    System.out.println("Selected Mage via image click (region: x=540-690, y=400-550)");
                } else {
                    System.out.println("No hero selected at touch point (region: x=180-330, 360-510, 540-690, y=400-550)");
                }
            } else {
                System.out.println("Touch outside hero y-range (y=400-550)");
            }

            // Проверка использования способности
            if (selectedHero != null) {
                for (int i = 0; i < 4; i++) {
                    if (abilityButtons[i].hit(touch.x, touch.y)) {
                        useAbility(selectedHero, i);
                        selectedHero = null; // Сбрасываем выбор после способности
                        System.out.println("Used ability " + i);
                        break;
                    }
                }
            }
        }

        batch.setProjectionMatrix(main.camera.combined);
        batch.begin();

        batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

        // Отрисовка врага (картинка + HP)
        batch.draw(imgEnemy, 360, 950, 210, 210); // Центр: x=535, y=1055
        font.setColor(Color.RED);
        font.getData().setScale(1.0f);
        font.draw(batch, "HP: " + Math.max(enemy.hp, 0), 360, 900); // Ниже из-за увеличенного размера

        // Отрисовка героев (картинки)
        if (knight.isAlive()) {
            batch.draw(imgKnight, 180, 400, 150, 150);
            font.draw(batch, "HP: " + Math.max(knight.hp, 0), 120, 600);
        } else {
            batch.setColor(1, 1, 1, 0.5f);
            batch.draw(imgKnight, 180, 400, 150, 150);
            batch.setColor(1, 1, 1, 1);
        }
        if (paladin.isAlive()) {
            batch.draw(imgPaladin, 360, 400, 150, 150); // Центр: x=435, y=475
            font.draw(batch, "HP: " + Math.max(paladin.hp, 0), 360, 600);
        } else {
            batch.setColor(1, 1, 1, 0.5f);
            batch.draw(imgPaladin, 360, 400, 150, 150);
            batch.setColor(1, 1, 1, 1);
        }
        if (mage.isAlive()) {
            batch.draw(imgMage, 540, 400, 150, 150); // Центр: x=615, y=475
            font.draw(batch, "HP: " + Math.max(paladin.hp, 0), 600, 600);
        } else {
            batch.setColor(1, 1, 1, 0.5f);
            batch.draw(imgMage, 540, 400, 150, 150);
            batch.setColor(1, 1, 1, 1);
        }

        // Кнопки способностей
        if (selectedHero != null && selectedHero.isAlive()) {
            for (int i = 0; i < abilityButtons.length; i++) {
                abilityButtons[i].render(batch, Color.WHITE);
                System.out.println("Rendering button " + i + " at x=" + abilityButtons[i].x + ", y=" + abilityButtons[i].y + ", text: " + abilityButtons[i].text);
            }
        }

        batch.end();
    }


    void setHeroAbilities(BattleCharacter hero) {
        System.out.println("Setting abilities for " + hero.name);
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
        for (int i = 0; i < abilityButtons.length; i++) {
            System.out.println("Button " + i + " text: " + abilityButtons[i].text);
        }
    }

    void positionAbilityButtons() {
        float buttonWidth = 110; // Предполагаемая ширина кнопки после уменьшения
        float startX = 20; // Отступ слева
        float spacing = 130; // Промежуток между кнопками
        float y = 200; // Высота кнопок (под героями)

        abilityButtons[0].setPositionBottomLeft(startX, y);
        abilityButtons[1].setPositionBottomLeft(startX + buttonWidth + spacing, y);
        abilityButtons[2].setPositionBottomLeft(startX + 2 * (buttonWidth + spacing), y);
        abilityButtons[3].setPositionBottomLeft(startX + 3 * (buttonWidth + spacing), y);

        // Отладочный вывод для проверки позиций и размеров
        for (int i = 0; i < abilityButtons.length; i++) {
            System.out.println("Button " + i + ": x=" + abilityButtons[i].x + ", y=" + abilityButtons[i].y +
                ", width=" + abilityButtons[i].width + ", height=" + abilityButtons[i].height);
        }
    }

    void useAbility(BattleCharacter hero, int index) {
        if (battleEnded) {
            System.out.println("Battle already ended, ignoring ability use");
            return;
        }

        System.out.println("Using ability " + index + " by " + hero.name);
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
    }

    void heroAction(BattleCharacter hero, int index) {
        if (battleEnded) {
            System.out.println("Battle already ended, ignoring hero action");
            return;
        }

        if (!hero.isAlive()) {
            System.out.println(hero.name + " is not alive, skipping action");
            return;
        }

        System.out.println("Hero " + hero.name + " uses ability " + index + ", Enemy HP before: " + enemy.hp);
        switch (hero.name) {
            case "Knight":
                if (index == 0)
                    enemy.hp -= (int)(2 * hero.physDmg - hero.physDmg * enemy.def);
                else if (index == 1)
                    heroes.forEach(h -> h.def += 0.5f);
                else if (index == 2) {
                    enemy.bleeding = true;
                    enemy.hp -= (int)(0.5f * hero.physDmg * (1 - enemy.def));
                } else if (index == 3)
                    enemy.spd -= 5;
                break;
            case "Paladin":
                if (index == 0)
                    enemy.hp -= (int)(2 * hero.physDmg - hero.physDmg * enemy.def);
                else if (index == 1)
                    heroes.forEach(h -> h.magDef += 0.5f);
                else if (index == 2)
                    enemy.magDef -= 0.5f;
                else if (index == 3)
                    enemy.skipNext = true;
                break;
            case "Mage":
                if (index == 0)
                    enemy.hp -= (int)(2 * hero.magDmg - hero.magDmg * enemy.magDef);
                else if (index == 1)
                    heroes.forEach(h -> h.spd += 5);
                else if (index == 2)
                    enemy.physDmg -= 3;
                else if (index == 3)
                    enemy.blind = true;
                break;
        }
        System.out.println("Hero action: Enemy HP after: " + enemy.hp);

        checkBattleEnd();
    }

    void enemyAction() {
        if (battleEnded) {
            System.out.println("Battle already ended, ignoring enemy action");
            return;
        }

        System.out.println("EnemyAction called. Enemy HP: " + enemy.hp + ", Heroes alive: " + heroes.stream().anyMatch(BattleCharacter::isAlive));

        if (enemy.hp <= 0) {
            endBattle(true);
            return;
        }

        if (enemy.skipNext) {
            enemy.skipNext = false;
            System.out.println("Enemy skips turn due to skipNext");
            return;
        }

        if (enemy.bleeding) {
            enemy.hp -= 3;
            System.out.println("Enemy bleeding, HP reduced to: " + enemy.hp);
            if (enemy.hp <= 0) {
                endBattle(true);
                return;
            }
        }

        Random r = new Random();
        if (enemy.blind && r.nextBoolean()) {
            System.out.println("Enemy blinded, skipping turn");
            return;
        }

        int choice = r.nextInt(4);
        System.out.println("Enemy action choice: " + choice);
        switch (enemy.name) {
            case "Ent":
                switch (choice) {
                    case 0:
                        for (BattleCharacter h : heroes) {
                            if (h.isAlive()) {
                                h.hp -= (int)(2 * enemy.physDmg - enemy.physDmg * h.def);
                                System.out.println("Enemy attacks " + h.name + ", HP now: " + h.hp);
                            }
                        }
                        break;
                    case 1:
                        enemy.def += 0.5f;
                        System.out.println("Enemy increases defense to: " + enemy.def);
                        break;
                    case 2:
                        heroes.forEach(h -> h.spd -= 5);
                        System.out.println("Enemy reduces heroes' speed");
                        break;
                    case 3:
                        heroes.forEach(h -> h.skipNext = true);
                        System.out.println("Enemy sets heroes to skip next turn");
                        break;
                }
                break;
            case "Gnome":
                switch (choice) {
                    case 0: // Pickaxe Attack
                        for (BattleCharacter h : heroes) {
                            if (h.isAlive()) {
                                h.hp -= (int)(2 * enemy.physDmg - enemy.physDmg * h.def);
                                System.out.println("Gnome uses Pickaxe Attack on " + h.name + ", HP now: " + h.hp);
                            }
                        }
                        break;
                    case 1: // Dwarven Boots
                        enemy.spd += 5;
                        System.out.println("Gnome uses Dwarven Boots, speed now: " + enemy.spd);
                        break;
                    case 2: // Dust in Eyes
                        heroes.forEach(h -> h.blind = true);
                        System.out.println("Gnome uses Dust in Eyes, heroes blinded");
                        break;
                    case 3: // Dwarven Agility
                        heroes.forEach(h -> h.physDmg -= 5);
                        System.out.println("Gnome uses Dwarven Agility, heroes' physDmg reduced");
                        break;
                }
                break;
            case "Golem":
                switch (choice) {
                    case 0: // Boulder Strike
                        for (BattleCharacter h : heroes) {
                            if (h.isAlive()) {
                                h.hp -= (int)(2 * enemy.physDmg - enemy.physDmg * h.def);
                                System.out.println("Golem uses Boulder Strike on " + h.name + ", HP now: " + h.hp);
                            }
                        }
                        break;
                    case 1: // Natural Strength
                        enemy.physDmg += 5;
                        System.out.println("Golem uses Natural Strength, physDmg now: " + enemy.physDmg);
                        break;
                    case 2: // Rockfall
                        heroes.forEach(h -> h.skipNext = true);
                        System.out.println("Golem uses Rockfall, heroes skip next turn");
                        break;
                    case 3: // Unsteady Stones
                        heroes.forEach(h -> h.def -= 0.5f);
                        System.out.println("Golem uses Unsteady Stones, heroes' def reduced");
                        break;
                }
                break;
        }

        checkBattleEnd();
    }

    private void checkBattleEnd() {
        if (battleEnded) {
            System.out.println("Battle already ended, skipping check");
            return;
        }

        if (enemy.hp <= 0) {
            endBattle(true);
        } else if (heroes.stream().noneMatch(h -> h.hp > 0)) {
            endBattle(false);
        }
    }

    private void endBattle(boolean victory) {
        if (battleEnded) {
            System.out.println("Battle already ended, ignoring endBattle call");
            return;
        }

        this.victory = victory;
        battleEnded = true;
        battleEndTime = 0; // Сброс таймера

        if (victory) {
            if (enemy.name.equals("Ent")) {
                woodCount += 1;
                main.totalWood += woodCount;
                System.out.println("Victory! Wood gained: " + woodCount + ", Total Wood: " + main.totalWood);
            } else if (enemy.name.equals("Gnome")) {
                ironCount += 1;
                main.totalIron += ironCount;
                System.out.println("Victory! Iron gained: " + ironCount + ", Total Iron: " + main.totalIron);
            } else if (enemy.name.equals("Golem")) {
                stoneCount += 1;
                main.totalStone += stoneCount;
                System.out.println("Victory! Stone gained: " + stoneCount + ", Total Stone: " + main.totalStone);
            }
        } else {
            System.out.println("Defeat! No resources gained. Total Wood: " + main.totalWood + ", Iron: " + main.totalIron + ", Stone: " + main.totalStone);
        }
    }

    @Override
    public void show() {
        System.out.println("ScreenBattle shown");
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("ScreenBattle resized to width: " + width + ", height: " + height);
    }

    @Override
    public void pause() {
        System.out.println("ScreenBattle paused");
    }

    @Override
    public void resume() {
        System.out.println("ScreenBattle resumed");
    }

    @Override
    public void hide() {
        System.out.println("ScreenBattle hidden");
    }

    @Override
    public void dispose() {
        imgBG.dispose();
        imgKnight.dispose();
        imgMage.dispose();
        imgPaladin.dispose();
        imgEnemy.dispose();
        shapeRenderer.dispose();
        System.out.println("ScreenBattle disposed");
    }
}
