package ru.karika.bolezni;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BolezniMod implements ModInitializer {
    private static final Map<UUID, Integer> ILLNESS_TICKS = new HashMap<>();
    private static int tickCounter = 0;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter < 12000) return; // Проверка примерно раз в 10 минут
            tickCounter = 0;

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID id = player.getUuid();
                if (ILLNESS_TICKS.getOrDefault(id, 0) > 0) continue;

                // Небольшой случайный шанс заболеть во время игры.
                if (player.getRandom().nextInt(100) < 18) {
                    ILLNESS_TICKS.put(id, 20 * 60 * 5); // 5 минут игрового времени
                    player.sendMessage(Text.literal("§cТы заболел! Отдохни и следи за состоянием."), false);
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 60 * 5, 0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 60 * 5, 0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 20, 0));
                }
            }

            ILLNESS_TICKS.replaceAll((uuid, ticks) -> Math.max(0, ticks - 12000));
            ILLNESS_TICKS.entrySet().removeIf(entry -> entry.getValue() <= 0);
        });
    }
}
