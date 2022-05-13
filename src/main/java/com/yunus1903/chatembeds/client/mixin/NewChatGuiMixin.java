package com.yunus1903.chatembeds.client.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import com.yunus1903.chatembeds.client.ChatGuiUtil;
import com.yunus1903.chatembeds.client.EmbedChatLine;
import com.yunus1903.chatembeds.client.embed.Embed;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.yunus1903.chatembeds.client.ChatScrollPos.setScrollPos;
import static net.minecraft.client.gui.DrawableHelper.fill;

/**
 * @author Yunus1903
 * @since 01/02/2021
 */
@Mixin(ChatHud.class)
public abstract class NewChatGuiMixin
{
    private static final String URL_REGEX = "((https?)://|(www)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?";

    private boolean doIndex = false;
    private int index = 0;

    @Shadow
    protected abstract boolean isChatHidden();

    @Shadow
    protected abstract void processMessageQueue();

    @Shadow
    public abstract int getVisibleLineCount();

    @Shadow
    @Final
    private List<ChatHudLine<OrderedText>> visibleMessages;

    @Shadow
    protected abstract boolean isChatFocused();

    @Shadow
    public abstract double getChatScale();

    @Shadow
    public abstract int getWidth();

    @Shadow
    private int scrolledLines;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private Deque<Text> messageQueue;

    @Shadow
    private boolean hasUnreadNewMessages;

    @Shadow
    protected abstract void removeMessage(int id);

    @Shadow
    public abstract void scroll(int posInc);

    @Shadow
    @Final
    private List<ChatHudLine<Text>> messages;

    @Shadow
    private static double getMessageOpacityMultiplier(int p_93776_) {
        return 0.0;
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrixStack, int ticks, CallbackInfo ci)
    {
        if (!this.isChatHidden()) {
            this.processMessageQueue();
            int i = this.getVisibleLineCount();
            int j = this.visibleMessages.size();
            if (j > 0) {
                boolean flag = false;
                if (this.isChatFocused()) {
                    flag = true;
                }

                float f = (float)this.getChatScale();
                int k = MathHelper.ceil((float)this.getWidth() / f);
                matrixStack.push();
                matrixStack.translate(4.0D, 8.0D, 0.0D);
                matrixStack.scale(f, f, 1.0F);
                double d0 = this.client.options.chatOpacity * (double)0.9F + (double)0.1F;
                double d1 = this.client.options.textBackgroundOpacity;
                double d2 = 9.0D * (this.client.options.chatLineSpacing + 1.0D);
                double d3 = -8.0D * (this.client.options.chatLineSpacing + 1.0D) + 4.0D * this.client.options.chatLineSpacing;
                int l = 0;

                for(int i1 = 0; i1 + this.scrolledLines < this.visibleMessages.size() && i1 < i; ++i1) {
                    ChatHudLine<OrderedText> chatHudLine = this.visibleMessages.get(i1 + this.scrolledLines);
                    if (chatHudLine != null) {
                        int j1 = ticks - chatHudLine.getCreationTick();
                        if (j1 < 200 || flag) {
                            double d4 = flag ? 1.0D : getMessageOpacityMultiplier(j1);
                            int l1 = (int)(255.0D * d4 * d0);
                            int i2 = (int)(255.0D * d4 * d1);
                            ++l;
                            if (l1 > 3) {
                                int j2 = 0;
                                double d5 = (double)(-i1) * d2;
                                matrixStack.push();
                                matrixStack.translate(0.0D, 0.0D, 50.0D);
                                fill(matrixStack, -4, (int)(d5 - d2), 0 + k + 4, (int)d5, i2 << 24);
                                RenderSystem.enableBlend();
                                matrixStack.translate(0.0D, 0.0D, 50.0D);
                                if (chatHudLine instanceof EmbedChatLine)
                                    ((EmbedChatLine<?>) chatHudLine)
                                            .render(this.client, matrixStack, 3, ((int) (d5 + d3)));
                                else
                                    this.client.textRenderer.drawWithShadow(matrixStack, chatHudLine.getText(),
                                            0.0F, (float)((int)(d5 + d3)), 16777215 + (l1 << 24));
                                RenderSystem.disableBlend();
                                matrixStack.pop();
                            }
                        }
                    }
                }

                if (!this.messageQueue.isEmpty()) {
                    int k2 = (int)(128.0D * d0);
                    int i3 = (int)(255.0D * d1);
                    matrixStack.push();
                    matrixStack.translate(0.0D, 0.0D, 50.0D);
                    fill(matrixStack, -2, 0, k + 4, 9, i3 << 24);
                    RenderSystem.enableBlend();
                    matrixStack.translate(0.0D, 0.0D, 50.0D);
                    this.client.textRenderer.drawWithShadow(matrixStack, new TranslatableText("chat.queue", this.messageQueue.size()), 0.0F, 1.0F, 16777215 + (k2 << 24));
                    matrixStack.pop();
                    RenderSystem.disableBlend();
                }

                if (flag) {
                    int l2 = 9;
                    int j3 = j * l2;
                    int k3 = l * l2;
                    int l3 = this.scrolledLines * k3 / j;
                    int k1 = k3 * k3 / j3;
                    if (j3 != k3) {
                        int i4 = l3 > 0 ? 170 : 96;
                        int j4 = this.hasUnreadNewMessages ? 13382451 : 3355562;
                        matrixStack.translate(-4.0D, 0.0D, 0.0D);
                        fill(matrixStack, 0, -l3, 2, -l3 - k1, j4 + (i4 << 24));
                        fill(matrixStack, 2, -l3, 1, -l3 - k1, 13421772 + (i4 << 24));
                    }
                }

                matrixStack.pop();
            }
        }

        ci.cancel();
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"), cancellable = true)
    public void addMessage(Text chatText, int chatLineId, int ticks, boolean p_238493_4_, CallbackInfo ci)
    {
        if (chatLineId != 0) this.removeMessage(chatLineId);

        int i = MathHelper.floor((double) this.getWidth() / this.getChatScale());
        List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(chatText, i, this.client.textRenderer);
        boolean flag = this.isChatFocused();

        for (OrderedText orderedText : list)
        {
            if (flag && this.scrolledLines > 0)
            {
                this.hasUnreadNewMessages = true;
                this.scroll(1);
            }

            this.visibleMessages.add(0, new ChatHudLine<>(ticks, orderedText, chatLineId));
            if (doIndex) index++;
        }

        Matcher matcher = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE).matcher(chatText.getString());
        final boolean embedFound = matcher.find();

        if (embedFound)
        {
            Thread embedLoader = ChatGuiUtil.getThread("Embed loader", () ->
            {
                doIndex = true;

                Embed embed = new Embed.Builder(matcher.group(), ticks, chatLineId).build();
                if (embed != null)
                {
                    if (ChatEmbedsConfig.getConfig().removeUrlMessage)
                        visibleMessages.removeAll(visibleMessages.stream()
                                .filter(iReorderingProcessorChatLine ->
                                        list.contains(iReorderingProcessorChatLine.getText()))
                                .collect(Collectors.toList()));
                    visibleMessages.add(index, new ChatHudLine<>(ticks, Language.getInstance()
                            .reorder(new LiteralText(chatText.getString().split(" ")[0])), chatLineId));
                    visibleMessages.addAll(index, Lists.reverse(embed.getChatHudLines()));
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

        while (this.visibleMessages.size() > 100)
            this.visibleMessages.remove(this.visibleMessages.size() - 1);

        if (!p_238493_4_)
        {
            if (!embedFound)
                this.messages.add(0, new ChatHudLine<>(ticks, chatText, chatLineId));

            while (this.messages.size() > 100)
                this.messages.remove(this.messages.size() - 1);
        }

        ci.cancel();
    }

    @Inject(method = "reset", at = @At("HEAD"))
    public void reset(CallbackInfo ci)
    {
        index = 0;
    }

    @Inject(method = "resetScroll", at = @At("RETURN"))
    public void resetScroll(CallbackInfo ci) {
        setScrollPos(scrolledLines);
    }

    @Inject(method = "scroll", at = @At("RETURN"))
    public void scroll(int i, CallbackInfo ci) {
        setScrollPos(scrolledLines);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir)
    {
        if (ChatGuiUtil.displayImageEmbedScreen(client, scrolledLines, getEmbed(mouseX, mouseY)))
            cir.setReturnValue(true);
    }

    /**
     * Gets {@link Embed embed} from mouse position
     *
     * @param mouseX mouseX Mouse X position
     * @param mouseY mouseY Mouse Y position
     * @return {@link Embed} instance
     */
    private Embed getEmbed(double mouseX, double mouseY)
    {
        if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden())
        {
            double d0 = mouseX - 2.0D;
            double d1 = (double) this.client.getWindow().getScaledHeight() - mouseY - 40.0D;
            d0 = MathHelper.floor(d0 / this.getChatScale());
            d1 = MathHelper.floor(d1 / (this.getChatScale() * (this.client.options.chatLineSpacing + 1.0D)));
            if (!(d0 < 0.0D) && !(d1 < 0.0D))
            {
                int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
                if (d0 <= (double) MathHelper.floor((double) this.getWidth() / this.getChatScale()) && d1 < (double) (9 * i + i))
                {
                    int j = (int) (d1 / 9.0D + (double) this.scrolledLines);
                    if (j >= 0 && j < this.visibleMessages.size())
                    {
                        ChatHudLine<OrderedText> chatLine = this.visibleMessages.get(j);
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
