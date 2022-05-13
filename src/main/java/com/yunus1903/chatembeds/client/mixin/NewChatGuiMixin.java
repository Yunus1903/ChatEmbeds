package com.yunus1903.chatembeds.client.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import com.yunus1903.chatembeds.client.ChatGuiUtil;
import com.yunus1903.chatembeds.client.EmbedChatLine;
import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
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

import static com.yunus1903.chatembeds.client.ChatScrollPos.setScrollPos;
import static net.minecraft.client.gui.GuiComponent.fill;

/**
 * @author Yunus1903
 * @since 01/02/2021
 */
@Mixin(ChatComponent.class)
public abstract class NewChatGuiMixin
{
    private static final String URL_REGEX = "((https?)://|(www)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?";

    private boolean doIndex = false;
    private int index = 0;

    @Shadow
    protected abstract boolean isChatHidden();

    @Shadow
    protected abstract void processPendingMessages();

    @Shadow
    public abstract int getLinesPerPage();

    @Shadow
    @Final
    private List<GuiMessage<FormattedCharSequence>> trimmedMessages;

    @Shadow
    protected abstract boolean isChatFocused();

    @Shadow
    public abstract double getScale();

    @Shadow
    public abstract int getWidth();

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private Deque<Component> chatQueue;

    @Shadow
    private boolean newMessageSinceScroll;

    @Shadow
    protected abstract void removeById(int id);

    @Shadow
    public abstract void scrollChat(int posInc);

    @Shadow
    @Final
    private List<GuiMessage<Component>> allMessages;

    @Shadow
    private static double getTimeFactor(int p_93776_) {
        return 0.0;
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack poseStack, int ticks, CallbackInfo ci)
    {
        if (!this.isChatHidden()) {
            this.processPendingMessages();
            int i = this.getLinesPerPage();
            int j = this.trimmedMessages.size();
            if (j > 0) {
                boolean flag = false;
                if (this.isChatFocused()) {
                    flag = true;
                }

                float f = (float)this.getScale();
                int k = Mth.ceil((float)this.getWidth() / f);
                poseStack.pushPose();
                poseStack.translate(4.0D, 8.0D, 0.0D);
                poseStack.scale(f, f, 1.0F);
                double d0 = this.minecraft.options.chatOpacity * (double)0.9F + (double)0.1F;
                double d1 = this.minecraft.options.textBackgroundOpacity;
                double d2 = 9.0D * (this.minecraft.options.chatLineSpacing + 1.0D);
                double d3 = -8.0D * (this.minecraft.options.chatLineSpacing + 1.0D) + 4.0D * this.minecraft.options.chatLineSpacing;
                int l = 0;

                for(int i1 = 0; i1 + this.chatScrollbarPos < this.trimmedMessages.size() && i1 < i; ++i1) {
                    GuiMessage<FormattedCharSequence> guimessage = this.trimmedMessages.get(i1 + this.chatScrollbarPos);
                    if (guimessage != null) {
                        int j1 = ticks - guimessage.getAddedTime();
                        if (j1 < 200 || flag) {
                            double d4 = flag ? 1.0D : getTimeFactor(j1);
                            int l1 = (int)(255.0D * d4 * d0);
                            int i2 = (int)(255.0D * d4 * d1);
                            ++l;
                            if (l1 > 3) {
                                int j2 = 0;
                                double d5 = (double)(-i1) * d2;
                                poseStack.pushPose();
                                poseStack.translate(0.0D, 0.0D, 50.0D);
                                fill(poseStack, -4, (int)(d5 - d2), 0 + k + 4, (int)d5, i2 << 24);
                                RenderSystem.enableBlend();
                                poseStack.translate(0.0D, 0.0D, 50.0D);
                                if (guimessage instanceof EmbedChatLine)
                                    ((EmbedChatLine<?>) guimessage)
                                            .render(this.minecraft, poseStack, 3, ((int) (d5 + d3)));
                                else
                                    this.minecraft.font.drawShadow(poseStack, guimessage.getMessage(),
                                            0.0F, (float)((int)(d5 + d3)), 16777215 + (l1 << 24));
                                RenderSystem.disableBlend();
                                poseStack.popPose();
                            }
                        }
                    }
                }

                if (!this.chatQueue.isEmpty()) {
                    int k2 = (int)(128.0D * d0);
                    int i3 = (int)(255.0D * d1);
                    poseStack.pushPose();
                    poseStack.translate(0.0D, 0.0D, 50.0D);
                    fill(poseStack, -2, 0, k + 4, 9, i3 << 24);
                    RenderSystem.enableBlend();
                    poseStack.translate(0.0D, 0.0D, 50.0D);
                    this.minecraft.font.drawShadow(poseStack, new TranslatableComponent("chat.queue", this.chatQueue.size()), 0.0F, 1.0F, 16777215 + (k2 << 24));
                    poseStack.popPose();
                    RenderSystem.disableBlend();
                }

                if (flag) {
                    int l2 = 9;
                    int j3 = j * l2;
                    int k3 = l * l2;
                    int l3 = this.chatScrollbarPos * k3 / j;
                    int k1 = k3 * k3 / j3;
                    if (j3 != k3) {
                        int i4 = l3 > 0 ? 170 : 96;
                        int j4 = this.newMessageSinceScroll ? 13382451 : 3355562;
                        poseStack.translate(-4.0D, 0.0D, 0.0D);
                        fill(poseStack, 0, -l3, 2, -l3 - k1, j4 + (i4 << 24));
                        fill(poseStack, 2, -l3, 1, -l3 - k1, 13421772 + (i4 << 24));
                    }
                }

                poseStack.popPose();
            }
        }

        ci.cancel();
    }

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V", at = @At("HEAD"), cancellable = true)
    public void addMessage(Component chatComponent, int chatLineId, int ticks, boolean p_238493_4_, CallbackInfo ci)
    {
        if (chatLineId != 0) this.removeById(chatLineId);

        int i = Mth.floor((double) this.getWidth() / this.getScale());
        List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(chatComponent, i, this.minecraft.font);
        boolean flag = this.isChatFocused();

        for (FormattedCharSequence formattedcharsequence : list)
        {
            if (flag && this.chatScrollbarPos > 0)
            {
                this.newMessageSinceScroll = true;
                this.scrollChat(1);
            }

            this.trimmedMessages.add(0, new GuiMessage<>(ticks, formattedcharsequence, chatLineId));
            if (doIndex) index++;
        }

        Matcher matcher = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE).matcher(chatComponent.getString());
        final boolean embedFound = matcher.find();

        if (embedFound)
        {
            Thread embedLoader = ChatGuiUtil.getThread("Embed loader", () ->
            {
                doIndex = true;

                Embed embed = new Embed.Builder(matcher.group(), ticks, chatLineId).build();
                if (embed != null)
                {
                    if (ChatEmbedsConfig.GeneralConfig.removeUrlMessage)
                        trimmedMessages.removeAll(trimmedMessages.stream()
                                .filter(iReorderingProcessorChatLine ->
                                        list.contains(iReorderingProcessorChatLine.getMessage()))
                                .collect(Collectors.toList()));
                    trimmedMessages.add(index, new GuiMessage<>(ticks, Language.getInstance()
                            .getVisualOrder(new TextComponent(chatComponent.getString().split(" ")[0])), chatLineId));
                    trimmedMessages.addAll(index, Lists.reverse(embed.getChatLines()));
                }

                index = 0;
                doIndex = false;
            });
            embedLoader.setDaemon(true);
            embedLoader.setUncaughtExceptionHandler(ChatGuiUtil.getUncaughtExceptionHandler(() ->
            {
                index = 0;
                doIndex = false;
            }));
            embedLoader.start();
        }

        while (this.trimmedMessages.size() > 100)
            this.trimmedMessages.remove(this.trimmedMessages.size() - 1);

        if (!p_238493_4_)
        {
            if (!embedFound)
                this.allMessages.add(0, new GuiMessage<>(ticks, chatComponent, chatLineId));

            while (this.allMessages.size() > 100)
                this.allMessages.remove(this.allMessages.size() - 1);
        }

        ci.cancel();
    }

    @Inject(method = "rescaleChat", at = @At("HEAD"))
    public void rescaleChat(CallbackInfo ci)
    {
        index = 0;
    }

    @Inject(method = "resetChatScroll", at = @At("RETURN"))
    public void resetChatScroll(CallbackInfo ci) {
        setScrollPos(chatScrollbarPos);
    }

    @Inject(method = "scrollChat", at = @At("RETURN"))
    public void scrollChat(int i, CallbackInfo ci) {
        setScrollPos(chatScrollbarPos);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "handleChatQueueClicked", at = @At("HEAD"), cancellable = true)
    public void handleChatQueueClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir)
    {
        if (ChatGuiUtil.displayImageEmbedScreen(minecraft, chatScrollbarPos, getEmbed(mouseX, mouseY)))
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
        if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden())
        {
            double d0 = mouseX - 2.0D;
            double d1 = (double) this.minecraft.getWindow().getGuiScaledHeight() - mouseY - 40.0D;
            d0 = Mth.floor(d0 / this.getScale());
            d1 = Mth.floor(d1 / (this.getScale() * (this.minecraft.options.chatLineSpacing + 1.0D)));
            if (!(d0 < 0.0D) && !(d1 < 0.0D))
            {
                int i = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
                if (d0 <= (double) Mth.floor((double) this.getWidth() / this.getScale()) && d1 < (double) (9 * i + i))
                {
                    int j = (int) (d1 / 9.0D + (double) this.chatScrollbarPos);
                    if (j >= 0 && j < this.trimmedMessages.size())
                    {
                        GuiMessage<FormattedCharSequence> chatLine = this.trimmedMessages.get(j);
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
