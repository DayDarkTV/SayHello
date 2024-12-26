package com.daysportal.sayhello;

import com.daysportal.sayhello.particles.StarlightParticle;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SayHello implements ClientModInitializer {
	public static final String MOD_ID = "sayhello";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static DefaultParticleType STARLIGHT;

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		final ParticleHandler particleHandler = new ParticleHandler();

		// Register the custom particle type
		STARLIGHT = Registry.register(Registries.PARTICLE_TYPE, id("starlight"), FabricParticleTypes.simple(true));
		ParticleFactoryRegistry.getInstance().register(STARLIGHT, StarlightParticle.DefaultFactory::new);

		// Initialize the TwitchClient
		TwitchClient twitchClient = TwitchClientBuilder.builder()
				.withEnableHelix(true)
				.withEnableChat(true)
				.build();

		// Join a channel
		twitchClient.getChat().joinChannel("daydarktv");

		// Add a listener to handle incoming chat messages
		twitchClient.getChat().getEventManager().onEvent(ChannelMessageEvent.class, event -> {
			onChatMessage(event.getChannel().getName(), event.getUser().getName(), event.getMessage());
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	// Handle messages from Twitch chat
	public void onChatMessage(String channel, String user, String message) {
		LOGGER.info("Message from {}: {}", user, message);
		// Determine if player is in a world
		if (MinecraftClient.getInstance().world == null) return;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;
		// Spawn a particle at the player's location

		player.getWorld().addParticle(STARLIGHT, true, player.getX(), player.getY(), player.getZ(), 0.0D, 0.0D, 0.0D);
	}
}