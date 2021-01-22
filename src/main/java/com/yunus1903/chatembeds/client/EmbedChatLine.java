package com.yunus1903.chatembeds.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

/**
 * {@link ChatLine} class for chat embeds
 * @author Yunus1903
 * @since 30/08/2020
 */
public abstract class EmbedChatLine<T extends Embed> extends ChatLine<IReorderingProcessor>
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
        this(ticks, new StringTextComponent(""), chatLineId, embed);
    }

    /**
     * Constructor
     * @param ticks Minecraft ticks
     * @param text Text to show in chat
     * @param chatLineId ID of the chat line
     * @param embed Instance of the {@link Embed}
     */
    public EmbedChatLine(int ticks, TextComponent text, int chatLineId, T embed)
    {
        super(ticks, LanguageMap.getInstance().func_241870_a(text), chatLineId);
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
     * @param matrixStack Instance of {@link MatrixStack}
     * @param x X position
     * @param y Y position
     */
    public abstract void render(Minecraft mc, MatrixStack matrixStack, int x, int y);

    /**
     * @return Width of the embed
     */
    public abstract int getWidth();
}