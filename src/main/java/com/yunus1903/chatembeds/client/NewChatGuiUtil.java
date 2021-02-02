package com.yunus1903.chatembeds.client;

import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.client.embed.AnimatedImageEmbed;
import com.yunus1903.chatembeds.client.embed.Embed;
import com.yunus1903.chatembeds.client.embed.ImageEmbed;
import com.yunus1903.chatembeds.client.screen.AnimatedImageEmbedScreen;
import com.yunus1903.chatembeds.client.screen.ImageEmbedScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.DefaultUncaughtExceptionHandler;

import javax.annotation.Nonnull;

/**
 * @author Yunus1903
 * @since 01/02/2021
 */
public class NewChatGuiUtil
{
    public static Thread getThread(String name, Runnable runnable)
    {
        return new Thread(name)
        {
            @Override
            public void run()
            {
                runnable.run();
            }
        };
    }

    public static DefaultUncaughtExceptionHandler getUncaughtExceptionHandler(Runnable runnable)
    {
        return new DefaultUncaughtExceptionHandler(ChatEmbeds.LOGGER)
        {
            @Override
            public void uncaughtException(Thread p_uncaughtException_1_, Throwable p_uncaughtException_2_)
            {
                super.uncaughtException(p_uncaughtException_1_, p_uncaughtException_2_);
                runnable.run();
            }
        };
    }

    public static boolean displayImageEmbedScreen(@Nonnull Minecraft mc, int scrollPos, Embed embed)
    {
        if (mc.currentScreen instanceof ChatScreen)
        {
            if (embed instanceof AnimatedImageEmbed)
            {
                mc.displayGuiScreen(new AnimatedImageEmbedScreen((ChatScreen) mc.currentScreen, scrollPos, (AnimatedImageEmbed) embed));
                return true;
            }
            if (embed instanceof ImageEmbed)
            {
                mc.displayGuiScreen(new ImageEmbedScreen((ChatScreen) mc.currentScreen, scrollPos, (ImageEmbed) embed));
                return true;
            }
        }
        return false;
    }
}
