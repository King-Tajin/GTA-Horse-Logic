package com.king_tajin.gta_horse_logic;

import net.minecraft.world.level.GameRules;

public class ModGameRules {

    public static GameRules.Key<GameRules.BooleanValue> RULE_ONlY_TAMED_HORSES_EXPLODE;
    public static GameRules.Key<GameRules.BooleanValue> RULE_HORSE_EXPLOSION_GRIEFING;

    public static void register() {
        RULE_ONlY_TAMED_HORSES_EXPLODE = GameRules.register(
                "OnlyTamedHorsesExplode",
                GameRules.Category.MOBS,
                GameRules.BooleanValue.create(true)
        );
        RULE_HORSE_EXPLOSION_GRIEFING = GameRules.register(
                "HorseExplosionGriefing",
                GameRules.Category.MOBS,
                GameRules.BooleanValue.create(true)
        );
    }
}