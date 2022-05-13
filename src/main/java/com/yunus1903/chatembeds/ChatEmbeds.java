package com.yunus1903.chatembeds;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

/**
 * @author Yunus1903
 * @since 10/08/2020
 */
@Mod(ChatEmbeds.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ChatEmbeds.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChatEmbeds
{
    public static final String MOD_ID = "chatembeds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ChatEmbeds()
    {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                Mixins.addConfiguration("chatembeds.mixins.json"));
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ChatEmbedsConfig.CLIENT_SPEC);
    }

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event)
    {
        ChatEmbeds.LOGGER.info("Chat Embeds is being initialized");
    }
}
