package net.mrwooly357.medievalstuff.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.mrwooly357.medievalstuff.MedievalStuff;
import net.mrwooly357.medievalstuff.item.ModItems;
import net.mrwooly357.wool_lib.items.weapons.AdvancedSweepMeleeWeaponItem;


import java.util.List;

public class WeightlessDaggerTier2Item extends AdvancedSweepMeleeWeaponItem {
    private final RegistryEntry<StatusEffect> effect;

    public WeightlessDaggerTier2Item(ToolMaterial toolMaterial, Settings settings, RegistryEntry<StatusEffect> effect) {
        super(toolMaterial, settings);
        this.effect = effect;
    }

    int counter = 0;
    int x = 0;
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(x == 0) {
            if(counter == 4) {
                int chanceToAddLevitation3 = MathHelper.nextInt(Random.createLocal(), 1, 1);
                if (chanceToAddLevitation3 == 1) {
                    target.addStatusEffect(new StatusEffectInstance(effect, 100, 1), attacker);
                    x = 1;
                }
            }
        }
        if(x == 0) {
            if(counter == 3) {
                int chanceToAddLevitation4 = MathHelper.nextInt(Random.createLocal(), 1, 2);
                if (chanceToAddLevitation4 == 1) {
                    target.addStatusEffect(new StatusEffectInstance(effect, 80, 1), attacker);
                    x = 1;
                } else {
                    counter = 4;
                }
            }
        }
         if(x == 0) {
             if(counter == 2) {
                 int chanceToAddLevitation3 = MathHelper.nextInt(Random.createLocal(), 1, 3);
                 if (chanceToAddLevitation3 == 1) {
                     target.addStatusEffect(new StatusEffectInstance(effect, 60, 1), attacker);
                     x = 1;
                 } else {
                     counter = 3;
                 }
             }
         }
        if(x == 0) {
            if(counter == 1) {
                int chanceToAddLevitation2 = MathHelper.nextInt(Random.createLocal(), 1, 4);
                if (chanceToAddLevitation2 == 1) {
                    target.addStatusEffect(new StatusEffectInstance(effect, 40, 1), attacker);
                    x = 1;
                } else {
                    counter = 2;
                }
            }
        }
        if(x == 0) {
            if (counter == 0) {
                int chanceToAddLevitation1 = MathHelper.nextInt(Random.createLocal(), 1, 5);
                if (chanceToAddLevitation1 == 1) {
                    target.addStatusEffect(new StatusEffectInstance(effect, 20, 1), attacker);
                } else {
                    counter = 1;
                }
            }
        }
        if(x == 1) {
            counter = 0;
            x = 0;
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 150, 0, true , true));
            user.getStackInHand(Hand.MAIN_HAND).damage(1, ((ServerWorld) world), ((ServerPlayerEntity) user),
                    item -> user.sendEquipmentBreakStatus(ModItems.WEIGHTLESS_DAGGER, EquipmentSlot.MAINHAND));
            user.damage(user.getDamageSources().create(RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    Identifier.of(MedievalStuff.MOD_ID, "player_weightless_dagger_tier_2_ability_damage_source"))), 2.5F);

        }
        user.getItemCooldownManager().set(this, 300);
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.medievalstuff.weightless_dagger_tier_2_1.tooltip"));
            if(Screen.hasControlDown()) {
                tooltip.add(Text.translatable("tooltip.medievalstuff.weightless_dagger_tier_2_2.tooltip"));
            } else {
                tooltip.add(Text.translatable("tooltip.medievalstuff.weightless_dagger_tier_2_5.tooltip"));
            }
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.medievalstuff.weightless_dagger_tier_2_3.tooltip"));
            } else {
                tooltip.add(Text.translatable("tooltip.medievalstuff.weightless_dagger_tier_2_6.tooltip"));
            }
            if(Screen.hasAltDown()) {
                tooltip.add(Text.translatable("tooltip.medievalstuff.weightless_dagger_tier_2_4.tooltip"));
            } else {
                tooltip.add(Text.translatable("tooltip.medievalstuff.weightless_dagger_tier_2_7.tooltip"));
            }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
