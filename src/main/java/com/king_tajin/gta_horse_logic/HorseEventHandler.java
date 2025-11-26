package com.king_tajin.gta_horse_logic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HorseEventHandler {
    private static final Map<UUID, Integer> dyingHorses = new HashMap<>();
    private static final int DEATH_TIMER = 60; // 3 seconds (20 ticks per second)

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractHorse horse)) return;
        if (horse.level().isClientSide) return;
        if (!(horse.level() instanceof ServerLevel serverLevel)) return;

        boolean onlyTamed = serverLevel.getGameRules().getBoolean(ModGameRules.RULE_ONlY_TAMED_HORSES_EXPLODE);
        if (onlyTamed && !horse.isTamed()) {
            return;
        }

        float health = horse.getHealth();
        float maxHealth = horse.getMaxHealth();

        UUID horseId = horse.getUUID();
        if (dyingHorses.containsKey(horseId)) {
            int timer = dyingHorses.get(horseId);

            if (!horse.isOnFire()) {
                horse.setRemainingFireTicks(200);
            }

            spawnParticles(horse, ParticleTypes.LARGE_SMOKE, 3);

            timer--;
            if (timer <= 0) {
                explodeHorse(horse);
                dyingHorses.remove(horseId);
            } else {
                dyingHorses.put(horseId, timer);
            }
            return;
        }

        if (health <= maxHealth / 4) {
            spawnParticles(horse, ParticleTypes.CAMPFIRE_COSY_SMOKE, 1);
            spawnParticles(horse, ParticleTypes.LARGE_SMOKE, 1);
        } else if (health <= maxHealth / 2) {
            spawnParticles(horse, ParticleTypes.POOF, 1);
            spawnParticles(horse, ParticleTypes.SMOKE, 1);
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractHorse horse)) return;
        if (horse.level().isClientSide) return;
        if (!(horse.level() instanceof ServerLevel serverLevel)) return;

        boolean onlyTamed = serverLevel.getGameRules().getBoolean(ModGameRules.RULE_ONlY_TAMED_HORSES_EXPLODE);
        if (onlyTamed && !horse.isTamed()) {
            return;
        }

        float health = horse.getHealth();
        float damage = event.getNewDamage();
        UUID horseId = horse.getUUID();

        if (dyingHorses.containsKey(horseId)) {
            event.setNewDamage(0);
            return;
        }

        if (health - damage <= 0) {
            event.setNewDamage(health - 0.1f);
            dyingHorses.put(horseId, DEATH_TIMER);
        }
    }

    private void spawnParticles(AbstractHorse horse, net.minecraft.core.particles.ParticleOptions particleType, int count) {
        if (!(horse.level() instanceof ServerLevel serverLevel)) return;

        for (int i = 0; i < count; i++) {
            double x = horse.getX() + (horse.getRandom().nextDouble() - 0.5) * horse.getBbWidth();
            double y = horse.getY() + horse.getRandom().nextDouble() * horse.getBbHeight();
            double z = horse.getZ() + (horse.getRandom().nextDouble() - 0.5) * horse.getBbWidth();

            serverLevel.sendParticles(
                    particleType,
                    x, y, z,
                    1,
                    0, 0.1, 0,
                    0.02
            );
        }
    }

    private void explodeHorse(AbstractHorse horse) {
        if (!(horse.level() instanceof ServerLevel serverLevel)) return;

        dyingHorses.remove(horse.getUUID());

        if (horse.isTamed() && horse.getOwner() != null) {
            if (horse.getOwner() instanceof ServerPlayer owner) {
                String horseName = horse.hasCustomName() ? horse.getCustomName().getString() : "Your horse";
                owner.sendSystemMessage(Component.literal("§c" + horseName + " has exploded!"));
            }
        }

        boolean griefing = serverLevel.getGameRules().getBoolean(ModGameRules.RULE_HORSE_EXPLOSION_GRIEFING);
        Level.ExplosionInteraction interaction = griefing ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE;

        serverLevel.explode(
                horse,
                horse.getX(),
                horse.getY(),
                horse.getZ(),
                3.5f,
                griefing,
                interaction
        );

        horse.setHealth(0.0f);
        horse.die(serverLevel.damageSources().explosion(null));
    }
}