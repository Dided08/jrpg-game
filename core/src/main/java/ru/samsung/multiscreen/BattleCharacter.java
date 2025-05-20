package ru.samsung.multiscreen;

public class BattleCharacter {
    String name;
    int hp;
    float physDmg; // Изменено на float
    float magDmg;  // Изменено на float
    float def;
    float magDef;
    int spd;
    boolean bleeding = false;
    boolean blind = false;
    boolean skipNext = false;

    public BattleCharacter(String name, int hp, float physDmg, float magDmg, float def, float magDef, int spd) {
        this.name = name;
        this.hp = hp;
        this.physDmg = physDmg;
        this.magDmg = magDmg;
        this.def = def;
        this.magDef = magDef;
        this.spd = spd;
    }

    public boolean isAlive() {
        return hp > 0;
    }
}
