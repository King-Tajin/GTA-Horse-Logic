package com.king_tajin.gta_horse_logic;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("gta_horse_logic")
public class GtaHorseLogicMod {
    public static final String MOD_ID = "gta_horse_logic";

    public GtaHorseLogicMod(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(new HorseEventHandler());
    }
}