package com.yunus1903.chatembeds;

import net.fabricmc.api.ModInitializer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ChatEmbeds implements ModInitializer {
    public static final String MOD_ID = "chatembeds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ChatEmbedsConfig.registerConfig();
        System.out.println("Chat Embeds active!");
    }
}
