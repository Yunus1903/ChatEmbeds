package com.yunus1903.chatembeds.client;

import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Language;

/**
 * {@link ChatHudLine} class for chat embeds
 * @author Yunus1903
 * @since 30/08/2020
 */
public abstract class EmbedChatLine<T extends Embed> extends ChatHudLine<OrderedText>
{
    protected final T embed;

    /**
     * Constructor
     * @param ticks Minecraft ticks
     * @param ChatHudLineId ID of the chat line
     * @param embed Instance of the {@link Embed}
     */
    public EmbedChatLine(int ticks, int ChatHudLineId, T embed)
    {
        this(ticks, new LiteralText(""), ChatHudLineId, embed);
    }

    /**
     * Constructor
     * @param ticks Minecraft ticks
     * @param text Text to show in chat
     * @param ChatHudLineId ID of the chat line
     * @param embed Instance of the {@link Embed}
     */
    public EmbedChatLine(int ticks, LiteralText text, int ChatHudLineId, T embed)
    {
        super(ticks, Language.getInstance().reorder(text), ChatHudLineId);
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
     * @param mc Instance of {@link MinecraftClient}
     * @param matrixStack Instance of {@link MatrixStack}
     * @param x X position
     * @param y Y position
     */
    public abstract void render(MinecraftClient mc, MatrixStack matrixStack, int x, int y);

    /**
     * @return Width of the embed
     */
    public abstract int getWidth();
}
