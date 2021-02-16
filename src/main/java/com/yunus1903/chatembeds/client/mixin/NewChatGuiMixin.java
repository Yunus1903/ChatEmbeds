package com.yunus1903.chatembeds.client.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import com.yunus1903.chatembeds.client.EmbedChatLine;
import com.yunus1903.chatembeds.client.NewChatGuiUtil;
import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.minecraft.client.gui.AbstractGui.fill;
import static net.minecraft.client.gui.NewChatGui.getLineBrightness;

/**
 * @author Yunus1903
 * @since 01/02/2021
 */
@Mixin(NewChatGui.class)
public abstract class NewChatGuiMixin
{
    private static final String URL_REGEX = "((https?)://|(www)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?";

    private boolean doIndex = false;
    private int index = 0;

    @Shadow(remap = false)
    public abstract boolean func_238496_i_();

    @Shadow(remap = false)
    public abstract void func_238498_k_();

    @Shadow
    public abstract int getLineCount();

    @Shadow
    @Final
    public List<ChatLine<IReorderingProcessor>> drawnChatLines;

    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract double getScale();

    @Shadow
    public abstract int getChatWidth();

    @Shadow
    public int scrollPos;

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow(remap = false)
    @Final
    public Deque<ITextComponent> field_238489_i_;

    @Shadow
    public boolean isScrolled;

    @Shadow
    public abstract void deleteChatLine(int id);

    @Shadow
    public abstract void addScrollPos(double posInc);

    @Shadow
    @Final
    public List<ChatLine<ITextComponent>> chatLines;

    @SuppressWarnings("deprecation")
    @Inject(method = "func_238492_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;I)V", at = @At("HEAD"), cancellable = true, remap = false)
    public void func_238492_a_(MatrixStack matrixStack, int ticks, CallbackInfo ci)
    {
        if (!this.func_238496_i_())
        {
            this.func_238498_k_();
            int i = this.getLineCount();
            int j = this.drawnChatLines.size();
            if (j > 0)
            {
                boolean flag = false;
                if (this.getChatOpen()) flag = true;

                double d0 = this.getScale();
                int k = MathHelper.ceil((double) this.getChatWidth() / d0);
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0F, 8.0F, 0.0F);
                RenderSystem.scaled(d0, d0, 1.0D);
                double d1 = this.mc.gameSettings.chatOpacity * (double) 0.9F + (double) 0.1F;
                double d2 = this.mc.gameSettings.accessibilityTextBackgroundOpacity;
                double d3 = 9.0D * (this.mc.gameSettings.chatLineSpacing + 1.0D);
                double d4 = -8.0D * (this.mc.gameSettings.chatLineSpacing + 1.0D) + 4.0D * this.mc.gameSettings.chatLineSpacing;
                int l = 0;

                for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1)
                {
                    ChatLine<IReorderingProcessor> chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatline != null)
                    {
                        int j1 = ticks - chatline.getUpdatedCounter();
                        if (j1 < 200 || flag)
                        {
                            double d5 = flag ? 1.0D : getLineBrightness(j1);
                            int l1 = (int) (255.0D * d5 * d1);
                            int i2 = (int) (255.0D * d5 * d2);
                            ++l;
                            if (l1 > 3)
                            {
                                double d6 = (double) (-i1) * d3;
                                matrixStack.push();
                                matrixStack.translate(0.0D, 0.0D, 50.0D);
                                fill(matrixStack, -2, (int) (d6 - d3), k + 4, (int) d6, i2 << 24);
                                RenderSystem.enableBlend();
                                matrixStack.translate(0.0D, 0.0D, 50.0D);
                                if (chatline instanceof EmbedChatLine)
                                    ((EmbedChatLine<?>) chatline)
                                            .render(mc, matrixStack, 3, ((int) (d6 + d4)));
                                else
                                    this.mc.fontRenderer.func_238407_a_(matrixStack, chatline.getLineString(),
                                            0.0F, (float) ((int) (d6 + d4)), 16777215 + (l1 << 24));
                                RenderSystem.disableAlphaTest();
                                RenderSystem.disableBlend();
                                matrixStack.pop();
                            }
                        }
                    }
                }

                if (!this.field_238489_i_.isEmpty())
                {
                    int k2 = (int) (128.0D * d1);
                    int i3 = (int) (255.0D * d2);
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

        ci.cancel();
    }

    @Inject(method = "func_238493_a_(Lnet/minecraft/util/text/ITextComponent;IIZ)V", at = @At("HEAD"), cancellable = true, remap = false)
    public void func_238493_a_(ITextComponent chatComponent, int chatLineId, int ticks, boolean p_238493_4_, CallbackInfo ci)
    {
        if (chatLineId != 0) this.deleteChatLine(chatLineId);

        int i = MathHelper.floor((double) this.getChatWidth() / this.getScale());
        List<IReorderingProcessor> list = RenderComponentsUtil.func_238505_a_(chatComponent, i, this.mc.fontRenderer);
        boolean flag = this.getChatOpen();

        for (IReorderingProcessor iReorderingProcessor : list)
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
        final boolean embedFound = matcher.find();

        if (embedFound)
        {
            Thread embedLoader = NewChatGuiUtil.getThread("Embed loader", () ->
            {
                doIndex = true;

                Embed embed = new Embed.Builder(matcher.group(), ticks, chatLineId).build();
                if (embed != null)
                {
                    if (ChatEmbedsConfig.GeneralConfig.removeUrlMessage)
                        drawnChatLines.removeAll(drawnChatLines.stream()
                                .filter(iReorderingProcessorChatLine ->
                                        list.contains(iReorderingProcessorChatLine.getLineString()))
                                .collect(Collectors.toList()));
                    drawnChatLines.add(index, new ChatLine<>(ticks, LanguageMap.getInstance()
                            .func_241870_a(new StringTextComponent(chatComponent.getString().split(" ")[0])), chatLineId));
                    drawnChatLines.addAll(index, Lists.reverse(embed.getChatLines()));
                }

                index = 0;
                doIndex = false;
            });
            embedLoader.setDaemon(true);
            embedLoader.setUncaughtExceptionHandler(NewChatGuiUtil.getUncaughtExceptionHandler(() ->
            {
                index = 0;
                doIndex = false;
            }));
            embedLoader.start();
        }

        while (this.drawnChatLines.size() > 100)
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);

        if (!p_238493_4_)
        {
            if (!embedFound)
                this.chatLines.add(0, new ChatLine<>(ticks, chatComponent, chatLineId));

            while (this.chatLines.size() > 100)
                this.chatLines.remove(this.chatLines.size() - 1);
        }

        ci.cancel();
    }

    @Inject(method = "refreshChat()V", at = @At("HEAD"), remap = false)
    public void refreshChat(CallbackInfo ci)
    {
        index = 0;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "func_238491_a_(DD)Z", at = @At("HEAD"), cancellable = true, remap = false)
    public void func_238491_a_(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir)
    {
        if (NewChatGuiUtil.displayImageEmbedScreen(mc, scrollPos, getEmbed(mouseX, mouseY)))
            cir.setReturnValue(true);
    }

    /**
     * Gets {@link Embed embed} from mouse position
     *
     * @param mouseX mouseX Mouse X position
     * @param mouseY mouseY Mouse Y position
     * @return {@link Embed} instance
     */
    @Nullable
    private Embed getEmbed(double mouseX, double mouseY)
    {
        if (this.getChatOpen() && !this.mc.gameSettings.hideGUI && !this.func_238496_i_())
        {
            double d0 = mouseX - 2.0D;
            double d1 = (double) this.mc.getMainWindow().getScaledHeight() - mouseY - 40.0D;
            d0 = MathHelper.floor(d0 / this.getScale());
            d1 = MathHelper.floor(d1 / (this.getScale() * (this.mc.gameSettings.chatLineSpacing + 1.0D)));
            if (!(d0 < 0.0D) && !(d1 < 0.0D))
            {
                int i = Math.min(this.getLineCount(), this.drawnChatLines.size());
                if (d0 <= (double) MathHelper.floor((double) this.getChatWidth() / this.getScale()) && d1 < (double) (9 * i + i))
                {
                    int j = (int) (d1 / 9.0D + (double) this.scrollPos);
                    if (j >= 0 && j < this.drawnChatLines.size())
                    {
                        ChatLine<IReorderingProcessor> chatLine = this.drawnChatLines.get(j);
                        if (chatLine instanceof EmbedChatLine)
                        {
                            if (d0 - 3 <= ((EmbedChatLine<?>) chatLine).getWidth())
                                return ((EmbedChatLine<?>) chatLine).getEmbed();
                        }
                    }
                }
            }
        }
        return null;
    }
}
