package com.king_tajin.gta_horse_logic;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModGameRules {

    public static final DeferredRegister<GameRule<?>> GAME_RULES =
            DeferredRegister.create(Registries.GAME_RULE, GtaHorseLogicMod.MOD_ID);

    public static final DeferredHolder<GameRule<?>, GameRule<Boolean>> RULE_ONLY_TAMED_HORSES_EXPLODE =
            GAME_RULES.register("only_tamed_horses_explode", registryName -> new GameRule<>(
                    GameRuleCategory.MOBS, GameRuleType.BOOL,
                    BoolArgumentType.bool(), GameRuleTypeVisitor::visitBoolean,
                    Codec.BOOL, bool -> bool ? 1 : 0, true, FeatureFlagSet.of()
            ));

    public static final DeferredHolder<GameRule<?>, GameRule<Boolean>> RULE_HORSE_EXPLOSION_GRIEFING =
            GAME_RULES.register("horse_explosion_griefing", registryName -> new GameRule<>(
                    GameRuleCategory.MOBS, GameRuleType.BOOL,
                    BoolArgumentType.bool(), GameRuleTypeVisitor::visitBoolean,
                    Codec.BOOL, bool -> bool ? 1 : 0, true, FeatureFlagSet.of()
            ));
}