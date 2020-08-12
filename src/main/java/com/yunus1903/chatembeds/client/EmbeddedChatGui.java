package com.yunus1903.chatembeds.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextProperties;
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

    public EmbeddedChatGui(Minecraft mcIn)
    {
        super(mcIn);
        this.mc = mcIn;
    }

    @Override
    public void func_238492_a_(MatrixStack p_238492_1_, int p_238492_2_)
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
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatline != null)
                    {
                        int j1 = p_238492_2_ - chatline.getUpdatedCounter();
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
                                p_238492_1_.push();
                                p_238492_1_.translate(0.0D, 0.0D, 50.0D);
                                fill(p_238492_1_, -2, (int)(d6 - d3), 0 + k + 4, (int)d6, i2 << 24);
                                RenderSystem.enableBlend();
                                p_238492_1_.translate(0.0D, 0.0D, 50.0D);
                                if (chatline instanceof Embed.ImageChatLine)
                                {
                                    //TestMod.LOGGER.debug(scrollPos + "");
                                    ((Embed.ImageChatLine) chatline).render(mc, p_238492_1_, 3, ((int)(d6 + d4)));
                                }
                                else
                                {
                                    this.mc.fontRenderer.func_238407_a_(p_238492_1_, chatline.func_238169_a_(), 0.0F, (float)((int)(d6 + d4)), 16777215 + (l1 << 24));
                                }
                                RenderSystem.disableAlphaTest();
                                RenderSystem.disableBlend();
                                p_238492_1_.pop();
                            }
                        }
                    }
                }

                if (!this.field_238489_i_.isEmpty())
                {
                    int k2 = (int)(128.0D * d1);
                    int i3 = (int)(255.0D * d2);
                    p_238492_1_.push();
                    p_238492_1_.translate(0.0D, 0.0D, 50.0D);
                    fill(p_238492_1_, -2, 0, k + 4, 9, i3 << 24);
                    RenderSystem.enableBlend();
                    p_238492_1_.translate(0.0D, 0.0D, 50.0D);
                    this.mc.fontRenderer.func_238407_a_(p_238492_1_, new TranslationTextComponent("chat.queue", this.field_238489_i_.size()), 0.0F, 1.0F, 16777215 + (k2 << 24));
                    p_238492_1_.pop();
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
                        fill(p_238492_1_, 0, -l3, 2, -l3 - k1, j4 + (i4 << 24));
                        fill(p_238492_1_, 2, -l3, 1, -l3 - k1, 13421772 + (i4 << 24));
                    }
                }

                RenderSystem.popMatrix();
            }
        }
    }

    @Override
    public void func_238493_a_(ITextProperties p_238493_1_, int p_238493_2_, int p_238493_3_, boolean p_238493_4_)
    {
        if (p_238493_2_ != 0)
        {
            this.deleteChatLine(p_238493_2_);
        }

        int i = MathHelper.floor((double)this.getChatWidth() / this.getScale());
        List<ITextProperties> list = RenderComponentsUtil.func_238505_a_(p_238493_1_, i, this.mc.fontRenderer);
        boolean flag = this.getChatOpen();

        for(ITextProperties itextproperties : list)
        {
            if (flag && this.scrollPos > 0)
            {
                this.isScrolled = true;
                this.addScrollPos(1.0D);
            }

            this.drawnChatLines.add(0, new ChatLine(p_238493_3_, itextproperties, p_238493_2_));
        }

        Matcher matcher = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE).matcher(p_238493_1_.getString());

        if (matcher.find())
        {
            new Thread("embed_loader")
            {
                @Override
                public void run()
                {
                    Embed embed = new Embed(matcher.group());
                    drawnChatLines.addAll(0, Lists.reverse(embed.getLines(p_238493_3_, p_238493_2_)));
                }
            }.start();
        }

        while(this.drawnChatLines.size() > 100)
        {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!p_238493_4_)
        {
            this.chatLines.add(0, new ChatLine(p_238493_3_, p_238493_1_, p_238493_2_));

            while(this.chatLines.size() > 100)
            {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    @Override
    public boolean func_238491_a_(double p_238491_1_, double p_238491_3_)
    {
        Embed.ImageChatLine chatLine = getImageChatLine(p_238491_1_, p_238491_3_);
        if (chatLine != null)
        {
            Minecraft.getInstance().displayGuiScreen(new EmbedImageScreen(mc.currentScreen, this.scrollPos, chatLine.getOriginalImage(), chatLine.getUrl()));
            return true;
        }
        return super.func_238491_a_(p_238491_1_, p_238491_3_);
    }

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
                        ChatLine chatLine = this.drawnChatLines.get(j);
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
