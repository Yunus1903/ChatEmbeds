package com.yunus1903.chatembeds.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yunus1903
 * @since 10/08/2020
 */
public class EmbeddedChatGui extends NewChatGui
{
    private static final String URL_REGEX = "((https?)://|(www)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?";

    private final Minecraft mc;

    private boolean doIndex = false;
    private int index = 0;

    public EmbeddedChatGui(Minecraft mcIn)
    {
        super(mcIn);
        this.mc = mcIn;
    }

    @Override
    public void func_238492_a_(MatrixStack matrixStack, int ticks)
    {
        if (!this.func_238496_i_())
        {
            this.func_238498_k_();
            int i = this.getLineCount();
            int j = this.drawnChatLines.size();
            if (j > 0)
            {
                boolean flag = false;
                if (this.getChatOpen())
                {
                    flag = true;
                }

                double d0 = this.getScale();
                int k = MathHelper.ceil((double)this.getChatWidth() / d0);
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0F, 8.0F, 0.0F);
                RenderSystem.scaled(d0, d0, 1.0D);
                double d1 = this.mc.gameSettings.chatOpacity * (double)0.9F + (double)0.1F;
                double d2 = this.mc.gameSettings.accessibilityTextBackgroundOpacity;
                double d3 = 9.0D * (this.mc.gameSettings.field_238331_l_ + 1.0D);
                double d4 = -8.0D * (this.mc.gameSettings.field_238331_l_ + 1.0D) + 4.0D * this.mc.gameSettings.field_238331_l_;
                int l = 0;

                for(int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1)
                {
                    ChatLine<IReorderingProcessor> chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatline != null)
                    {
                        int j1 = ticks - chatline.getUpdatedCounter();
                        if (j1 < 200 || flag)
                        {
                            double d5 = flag ? 1.0D : getLineBrightness(j1);
                            int l1 = (int)(255.0D * d5 * d1);
                            int i2 = (int)(255.0D * d5 * d2);
                            ++l;
                            if (l1 > 3)
                            {
                                int j2 = 0;
                                double d6 = (double)(-i1) * d3;
                                matrixStack.push();
                                matrixStack.translate(0.0D, 0.0D, 50.0D);
                                fill(matrixStack, -2, (int)(d6 - d3), 0 + k + 4, (int)d6, i2 << 24);
                                RenderSystem.enableBlend();
                                matrixStack.translate(0.0D, 0.0D, 50.0D);
                                if (chatline instanceof Embed.ImageChatLine)
                                {
                                    ((Embed.ImageChatLine) chatline).render(mc, matrixStack, 3, ((int)(d6 + d4)));
                                }
                                else
                                {
                                    this.mc.fontRenderer.func_238407_a_(matrixStack, chatline.func_238169_a_(), 0.0F, (float)((int)(d6 + d4)), 16777215 + (l1 << 24));
                                }
                                RenderSystem.disableAlphaTest();
                                RenderSystem.disableBlend();
                                matrixStack.pop();
                            }
                        }
                    }
                }

                if (!this.field_238489_i_.isEmpty())
                {
                    int k2 = (int)(128.0D * d1);
                    int i3 = (int)(255.0D * d2);
                    matrixStack.push();
                    matrixStack.translate(0.0D, 0.0D, 50.0D);
                    fill(matrixStack, -2, 0, k + 4, 9, i3 << 24);
                    RenderSystem.enableBlend();
                    matrixStack.translate(0.0D, 0.0D, 50.0D);
                    this.mc.fontRenderer.func_243246_a(matrixStack, new TranslationTextComponent("chat.queue", this.field_238489_i_.size()), 0.0F, 1.0F, 16777215 + (k2 << 24));
                    matrixStack.pop();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.disableBlend();
                }

                if (flag)
                {
                    int l2 = 9;
                    RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
                    int j3 = j * l2 + j;
                    int k3 = l * l2 + l;
                    int l3 = this.scrollPos * k3 / j;
                    int k1 = k3 * k3 / j3;
                    if (j3 != k3)
                    {
                        int i4 = l3 > 0 ? 170 : 96;
                        int j4 = this.isScrolled ? 13382451 : 3355562;
                        fill(matrixStack, 0, -l3, 2, -l3 - k1, j4 + (i4 << 24));
                        fill(matrixStack, 2, -l3, 1, -l3 - k1, 13421772 + (i4 << 24));
                    }
                }

                RenderSystem.popMatrix();
            }
        }
    }

    @Override
    public void func_238493_a_(ITextComponent chatComponent, int chatLineId, int ticks, boolean p_238493_4_)
    {
        if (chatLineId != 0)
        {
            this.deleteChatLine(chatLineId);
        }

        int i = MathHelper.floor((double)this.getChatWidth() / this.getScale());
        List<IReorderingProcessor> list = RenderComponentsUtil.func_238505_a_(chatComponent, i, this.mc.fontRenderer);
        boolean flag = this.getChatOpen();

        for(IReorderingProcessor iReorderingProcessor : list)
        {
            if (flag && this.scrollPos > 0)
            {
                this.isScrolled = true;
                this.addScrollPos(1.0D);
            }

            this.drawnChatLines.add(0, new ChatLine<>(ticks, iReorderingProcessor, chatLineId));
            if (doIndex) index++;
        }

        Matcher matcher = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE).matcher(chatComponent.getString());

        if (matcher.find())
        {
            new Thread("embed_loader")
            {
                @Override
                public void run()
                {
                    doIndex = true;
                    Embed embed = new Embed(matcher.group());
                    drawnChatLines.addAll(index, Lists.reverse(embed.getLines(ticks, chatLineId)));
                    index = 0;
                    doIndex = false;
                }
            }.start();
        }

        while(this.drawnChatLines.size() > 100)
        {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!p_238493_4_)
        {
            this.chatLines.add(0, new ChatLine<>(ticks, chatComponent, chatLineId));

            while(this.chatLines.size() > 100)
            {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    @Override
    public boolean func_238491_a_(double mouseX, double mouseY)
    {
        Embed.ImageChatLine chatLine = getImageChatLine(mouseX, mouseY);
        if (chatLine != null)
        {
            Minecraft.getInstance().displayGuiScreen(new EmbedImageScreen(mc.currentScreen, this.scrollPos, chatLine.getOriginalImage(), chatLine.getUrl()));
            return true;
        }
        return super.func_238491_a_(mouseX, mouseY);
    }

    /**
     * Gets {@link Embed.ImageChatLine ImageChatLine} from mouse position
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @return {@link Embed.ImageChatLine} instance
     */
    @Nullable
    private Embed.ImageChatLine getImageChatLine(double mouseX, double mouseY)
    {
        if (this.getChatOpen() && !this.mc.gameSettings.hideGUI && !this.func_238496_i_())
        {
            double d0 = mouseX - 2.0D;
            double d1 = (double)this.mc.getMainWindow().getScaledHeight() - mouseY - 40.0D;
            d0 = (double)MathHelper.floor(d0 / this.getScale());
            d1 = (double)MathHelper.floor(d1 / (this.getScale() * (this.mc.gameSettings.field_238331_l_ + 1.0D)));
            if (!(d0 < 0.0D) && !(d1 < 0.0D))
            {
                int i = Math.min(this.getLineCount(), this.drawnChatLines.size());
                if (d0 <= (double)MathHelper.floor((double)this.getChatWidth() / this.getScale()) && d1 < (double)(9 * i + i))
                {
                    int j = (int)(d1 / 9.0D + (double)this.scrollPos);
                    if (j >= 0 && j < this.drawnChatLines.size())
                    {
                        ChatLine<?> chatLine = this.drawnChatLines.get(j);
                        if (chatLine instanceof Embed.ImageChatLine)
                        {
                            if (d0 - 3 <= ((Embed.ImageChatLine) chatLine).getImage().getWidth())
                            {
                                return (Embed.ImageChatLine) chatLine;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}