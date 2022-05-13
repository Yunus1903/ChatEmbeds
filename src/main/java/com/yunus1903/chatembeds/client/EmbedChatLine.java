package com.yunus1903.chatembeds.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import java.awt.*;

/**
 * {@link GuiMessage} class for chat embeds
 * @author Yunus1903
 * @since 30/08/2020
 */
public abstract class EmbedChatLine<T extends Embed> extends GuiMessage<FormattedCharSequence>
{
    protected final T embed;

    /**
     * Constructor
     * @param ticks Minecraft ticks
     * @param chatLineId ID of the chat line
     * @param embed Instance of the {@link Embed}
     */
    public EmbedChatLine(int ticks, int chatLineId, T embed)
    {
        this(ticks, new TextComponent(""), chatLineId, embed);
    }

    /**
     * Constructor
     * @param ticks Minecraft ticks
     * @param text Text to show in chat
     * @param chatLineId ID of the chat line
     * @param embed Instance of the {@link Embed}
     */
    public EmbedChatLine(int ticks, FormattedText text, int chatLineId, T embed)
    {
        super(ticks, Language.getInstance().getVisualOrder(text), chatLineId);
        this.embed = embed;
    }

    /**
     * @return Instance of {@link Embed}
     */
    public T getEmbed()
    {
        return embed;
    }

    /**
     * Renders the embed
     * @param mc Instance of {@link Minecraft}
     * @param matrixStack Instance of {@link PoseStack}
     * @param x X position
     * @param y Y position
     */
    public abstract void render(Minecraft mc, PoseStack matrixStack, int x, int y);

    /**
     * @return Width of the embed
     */
    public abstract int getWidth();
}
