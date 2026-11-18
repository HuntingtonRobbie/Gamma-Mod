package com.robbie.gammamod;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Full bright toggle. Pressing the key (default K) gives the local player a Night Vision effect that only exists on
 * the client (no particles, no HUD icon), and pressing it again removes it.
 */
public class GammaModClient implements ClientModInitializer {
	public static final String MOD_ID = "gamma-mod";

	// Same key name as the original Gamma-Mod, so an existing binding in options.txt carries over
	private static final KeyMapping TOGGLE_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.gamma.toggle",
			InputConstants.Type.KEYBOARD,
			InputConstants.KEY_K,
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"))));

	private static boolean fullBright;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(GammaModClient::tick);
	}

	private static void tick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null) return;

		while (TOGGLE_KEY.consumeClick()) {
			fullBright = !fullBright;
			if (fullBright) {
				applyNightVision(player);
				player.sendOverlayMessage(Component.translatable("message." + MOD_ID + ".enabled")
						.withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
			} else {
				player.removeEffect(MobEffects.NIGHT_VISION);
				player.sendOverlayMessage(Component.translatable("message." + MOD_ID + ".disabled")
						.withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
			}
		}

		// The client player is recreated on respawn and when changing dimension, which drops the effect: put it back
		if (fullBright && !player.hasEffect(MobEffects.NIGHT_VISION)) {
			applyNightVision(player);
		}
	}

	private static void applyNightVision(LocalPlayer player) {
		player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, MobEffectInstance.INFINITE_DURATION, 0, false, false));
	}
}
