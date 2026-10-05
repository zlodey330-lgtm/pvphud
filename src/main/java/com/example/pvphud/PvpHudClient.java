package com.example.pvphud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class PvpHudClient implements ClientModInitializer {

    private static final EquipmentSlot[] ARMOR = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final String[] NAMES = {"Шлем", "Нагрудник", "Поножи", "Ботинки"};

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> render(context));
    }

    private void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null || mc.options.hudHidden) return;

        TextRenderer tr = mc.textRenderer;
        int x = 6;
        int y = 6;

        int totems = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.isOf(Items.TOTEM_OF_UNDYING)) totems += s.getCount();
        }
        int totemColor = totems == 0 ? 0xFF5555 : (totems < 3 ? 0xFFAA00 : 0x55FF55);
        context.drawTextWithShadow(tr, "Тотемы: " + totems, x, y, totemColor);
        y += 11;

        for (int i = 0; i < ARMOR.length; i++) {
            ItemStack stack = player.getEquippedStack(ARMOR[i]);
            if (stack.isEmpty() || stack.getMaxDamage() <= 0) {
                context.drawTextWithShadow(tr, NAMES[i] + ": -", x, y, 0xAAAAAA);
            } else {
                int left = stack.getMaxDamage() - stack.getDamage();
                int percent = Math.round(100f * left / stack.getMaxDamage());
                int color = percent < 20 ? 0xFF5555 : (percent < 50 ? 0xFFAA00 : 0x55FF55);
                context.drawTextWithShadow(tr, NAMES[i] + ": " + left + " (" + percent + "%)", x, y, color);
            }
            y += 11;
        }
    }
}
