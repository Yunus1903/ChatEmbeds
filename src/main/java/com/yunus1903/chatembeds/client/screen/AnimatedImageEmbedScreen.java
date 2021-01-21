package com.yunus1903.chatembeds.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yunus1903.chatembeds.client.embed.AnimatedImageEmbed;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;

/**
 * @author Yunus1903
 * @since 30/08/2020
 */
public class AnimatedImageEmbedScreen extends AbstractImageEmbedScreen<AnimatedImageEmbed>
{
    private int time;
    private int lastFrameIndex;
    private int currentFrame;

    public AnimatedImageEmbedScreen(ChatScreen parent, int scrollPos, AnimatedImageEmbed embed)
    {
        super(parent, scrollPos, embed, embed.getFrames().get(0).getImage());
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (minecraft == null) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        final int frameIndex = minecraft.frameTimer.getIndex();

        if (lastFrameIndex != frameIndex)
        {
            lastFrameIndex = frameIndex;
            time++;
        }

        if (time >= embed.getFrames().get(currentFrame).getDelay())
        {
            time = 0;
            if (currentFrame + 1 >= embed.getFrames().size()) currentFrame = 0;
            else currentFrame++;
        }

        minecraft.getTextureManager().bindTexture(embed.getFrames().get(currentFrame).getResourceLocation());
        AbstractGui.blit(matrixStack, (width - scaledImageWidth) / 2,
                (height - scaledImageHeight) / 2, 0, 0,
                scaledImageWidth, scaledImageHeight, scaledImageWidth, scaledImageHeight);
    }
}