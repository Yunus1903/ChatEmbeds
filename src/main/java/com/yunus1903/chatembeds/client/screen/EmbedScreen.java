package com.yunus1903.chatembeds.client.screen;

import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

/**
 * Abstract class for {@link Embed} screen's
 * @author Yunus1903
 * @since 30/08/2020
 */
public abstract class EmbedScreen<T extends Embed> extends Screen
{
    private final ChatScreen parent;
    private final int scrollPos;
    protected final T embed;

    /**
     * Constructor
     * @param parent Parent {@link ChatScreen} (gets returned to when this screen is closed)
     * @param scrollPos Last chat scroll position
     * @param embed The {@link Embed embed} which this screen belongs to
     */
    public EmbedScreen(ChatScreen parent, int scrollPos, T embed)
    {
        super(new LiteralText("Embed image"));
        this.parent = parent;
        this.scrollPos = scrollPos;
        this.embed = embed;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrixStack);
    }

    @Override
    public void close()
    {
        if (client == null) return;
        client.setScreen(parent);
        client.inGameHud.getChatHud().scroll(scrollPos);
    }
}
