package com.yunus1903.chatembeds.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yunus1903.chatembeds.client.embed.AnimatedImageEmbed;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

/**
 * @author Yunus1903
 * @since 30/08/2020
 */
public class AnimatedImageEmbedScreen extends EmbedScreen<AnimatedImageEmbed>
{
    private int imageWidth, imageHeight;

    private int time;
    private int lastFrameIndex;
    private int currentFrame;

    private IFormattableTextComponent openImage;

    public AnimatedImageEmbedScreen(ChatScreen parent, int scrollPos, AnimatedImageEmbed embed)
    {
        super(parent, scrollPos, embed);
    }

    @Override
    protected void init()
    {
        imageWidth = embed.getFrames().get(0).getWidth();
        imageHeight = embed.getFrames().get(0).getHeight();

        if (imageWidth > width / 2 || imageHeight > height / 2)
        {
            if (imageWidth > width / 2) // Max width
            {
                imageWidth = width / 2;
                imageHeight = (int) (((float) embed.getFrames().get(0).getHeight() / (float) embed.getFrames().get(0).getWidth()) * (float) imageWidth);
            }

            if (imageHeight > height / 2) // Max height
            {
                imageHeight = height / 2;
                imageWidth = (int) (((float) embed.getFrames().get(0).getWidth() / (float) embed.getFrames().get(0).getHeight()) * (float) imageHeight);
            }
        }

        openImage = new StringTextComponent("Open image");
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        final int frameIndex = minecraft.frameTimer.getIndex();

        if (lastFrameIndex != frameIndex)
        {
            lastFrameIndex = frameIndex;
            time++;
        }

        if (time >= embed.getFrameDelays().get(currentFrame))
        {
            time = 0;
            if (currentFrame + 1 >= embed.getFrames().size()) currentFrame = 0;
            else currentFrame++;
        }

        minecraft.getTextureManager().bindTexture(embed.getFrameResourceLocations().get(currentFrame));
        AbstractGui.blit(matrixStack,  (width - imageWidth) / 2, (height - imageHeight) / 2, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        if (mouseOverImage(mouseX, mouseY)) openImage.mergeStyle(TextFormatting.UNDERLINE);
        else
        {
            openImage.setStyle(Style.EMPTY.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, embed.getUrl().toString())));
            openImage.mergeStyle(TextFormatting.DARK_GRAY);
        }

        minecraft.fontRenderer.func_238407_a_(matrixStack, LanguageMap.getInstance().func_241870_a(openImage), ((width - imageWidth) >> 1), ((height - imageHeight) >> 1) + imageHeight + 5, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_)
    {
        if (mouseX < (width - imageWidth) >> 1 || mouseX > ((width - imageWidth) >> 1) + imageWidth || mouseY < (height - imageHeight) >> 1 || mouseY > ((height - imageHeight) >> 1) + imageHeight)
        {
            if (mouseOverImage(mouseX, mouseY)) handleComponentClicked(openImage.getStyle());
            else closeScreen();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, p_231044_5_);
    }

    /**
     * Check if mouse if hovering over the image
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @return true if mouse is hovering over the shown image
     */
    private boolean mouseOverImage(double mouseX, double mouseY)
    {
        if (mouseX >= ((width - imageWidth) >> 1) && mouseX <= ((width - imageWidth) >> 1) + minecraft.fontRenderer.getStringWidth(openImage.getString()) && mouseY >= ((height - imageHeight) >> 1) + imageHeight + 5 && mouseY <= ((height - imageHeight) >> 1) + imageHeight + 5 + minecraft.fontRenderer.FONT_HEIGHT)
        {
            return true;
        }
        return false;
    }
}
