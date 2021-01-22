package com.yunus1903.chatembeds.client;

import com.yunus1903.chatembeds.ChatEmbeds;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * @author Yunus1903
 * @since 12/08/2020
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ChatEmbeds.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration
{
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event)
    {
        ChatEmbeds.LOGGER.info("Chat Embeds is being initialized");
        Minecraft mc = event.getMinecraftSupplier().get();
        mc.ingameGUI.persistantChatGUI = new EmbeddedChatGui(mc);
    }
}