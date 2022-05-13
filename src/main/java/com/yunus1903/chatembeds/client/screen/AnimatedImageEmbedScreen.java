package com.yunus1903.chatembeds.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.embed.AnimatedImageEmbed;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;

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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (minecraft == null) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        final int frameIndex = minecraft.frameTimer.getLogEnd();

        if (lastFrameIndex != frameIndex)
        {
            lastFrameIndex = frameIndex;
            time++;
        }

        if (time >= embed.getFrames().get(currentFrame).getDelay() / 2)
        {
            time = 0;
            if (currentFrame + 1 >= embed.getFrames().size()) currentFrame = 0;
            else currentFrame++;
        }

        RenderSystem.setShaderTexture(0, embed.getFrames().get(currentFrame).getResourceLocation());
        RenderSystem.enableBlend();
        GuiComponent.blit(matrixStack, (width - scaledImageWidth) / 2,
                (height - scaledImageHeight) / 2, 0, 0,
                scaledImageWidth, scaledImageHeight, scaledImageWidth, scaledImageHeight);
    }
}
