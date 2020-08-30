package com.yunus1903.chatembeds.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

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
        super(new StringTextComponent("Embed image"));
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
    public void closeScreen()
    {
        minecraft.displayGuiScreen(parent);
        minecraft.ingameGUI.getChatGUI().scrollPos = scrollPos;
    }
}
