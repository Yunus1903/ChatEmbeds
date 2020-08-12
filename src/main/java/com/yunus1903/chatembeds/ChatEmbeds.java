package com.yunus1903.chatembeds;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Yunus1903
 * @since 10/08/2020
 */
@Mod(ChatEmbeds.MOD_ID)
public class ChatEmbeds
{
    public static final String MOD_ID = "chatembeds";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public ChatEmbeds()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ChatEmbedsConfig.CLIENT_SPEC);
    }
}
