package com.yunus1903.chatembeds;

import com.yunus1903.chatembeds.client.EmbeddedChatGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Yunus1903
 * @since 10/08/2020
 */
@Mod(ChatEmbeds.MOD_ID)
@Mod.EventBusSubscriber(modid = ChatEmbeds.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChatEmbeds
{
    public static final String MOD_ID = "chatembeds";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public ChatEmbeds()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ChatEmbedsConfig.CLIENT_SPEC);
    }

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event)
    {
        LOGGER.info("TestMod is being initialized");
        Minecraft mc = Minecraft.getInstance();
        mc.ingameGUI.persistantChatGUI = new EmbeddedChatGui(mc);
    }
}
