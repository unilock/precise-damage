package io.github.andrew6rant.precise_damage.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract double getAttributeValue(EntityAttribute attribute);

    @WrapOperation(method = "applyArmorToDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(FFF)F"))
    private float applyArmorToDamage(float damage, float armor, float armorToughness, Operation<Float> original) {
        return original.call(damage, (float) this.getAttributeValue(EntityAttributes.GENERIC_ARMOR), (float) this.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
    }
}
