package com.yunus1903.chatembeds.client;

import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.client.embed.AnimatedImageEmbed;
import com.yunus1903.chatembeds.client.embed.Embed;
import com.yunus1903.chatembeds.client.embed.ImageEmbed;
import com.yunus1903.chatembeds.client.screen.AnimatedImageEmbedScreen;
import com.yunus1903.chatembeds.client.screen.ImageEmbedScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.DefaultUncaughtExceptionHandler;

import org.jetbrains.annotations.NotNull;

/**
 * @author Yunus1903
 * @since 01/02/2021
 */
public class ChatGuiUtil
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

    public static boolean displayImageEmbedScreen(@NotNull Minecraft mc, int scrollPos, Embed embed)
    {
        if (mc.screen instanceof ChatScreen)
        {
            if (embed instanceof AnimatedImageEmbed)
            {
                mc.setScreen(new AnimatedImageEmbedScreen((ChatScreen) mc.screen, scrollPos, (AnimatedImageEmbed) embed));
                return true;
            }
            if (embed instanceof ImageEmbed)
            {
                mc.setScreen(new ImageEmbedScreen((ChatScreen) mc.screen, scrollPos, (ImageEmbed) embed));
                return true;
            }
        }
        return false;
    }
}
