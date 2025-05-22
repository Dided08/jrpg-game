package ru.samsung.multiscreen;

import java.util.ArrayList;
import java.util.List;

public class BattleCharacter {
    String name;
    int hp;
    float physDmg, magDmg, def, magDef, spd;
    boolean bleeding, blind;
    int blindTurns, bleedingTurns, sleepTurns; // Duration tracking
    List<Buff> buffs;

    public BattleCharacter(String name, int hp, float physDmg, float magDmg, float def, float magDef, float spd) {
        this.name = name;
        this.hp = hp;
        this.physDmg = physDmg;
        this.magDmg = magDmg;
        this.def = def;
        this.magDef = magDef;
        this.spd = spd;
        this.bleeding = false;
        this.blind = false;
        this.blindTurns = 0;
        this.bleedingTurns = 0;
        this.sleepTurns = 0;
        this.buffs = new ArrayList<>();
        clampDefenses();
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void clampDefenses() {
        def = Math.max(0, Math.min(2, def));
        magDef = Math.max(0, Math.min(2, magDef));
    }

    public void applyBuff(String stat, float value, int turns) {
        buffs.add(new Buff(stat, value, turns));
        updateStat(stat, value);
    }

    private void updateStat(String stat, float value) {
        switch (stat) {
            case "def": def += value; break;
            case "magDef": magDef += value; break;
            case "spd": spd += value; break;
            case "physDmg": physDmg += value; break;
        }
        clampDefenses();
    }

    public void updateBuffs() {
        List<Buff> expired = new ArrayList<>();
        for (Buff buff : buffs) {
            buff.turnsLeft--;
            if (buff.turnsLeft <= 0) {
                updateStat(buff.stat, -buff.value);
                expired.add(buff);
            }
        }
        buffs.removeAll(expired);
    }
}

class Buff {
    String stat;
    float value;
    int turnsLeft;

    Buff(String stat, float value, int turns) {
        this.stat = stat;
        this.value = value;
        this.turnsLeft = turns;
    }
}
