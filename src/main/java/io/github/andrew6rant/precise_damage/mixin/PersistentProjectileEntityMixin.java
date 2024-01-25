package io.github.andrew6rant.precise_damage.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {
    @Shadow
    private double damage;

    @Unique
    private double damage_calc = 0.0F;

    // store damage value as double instead of int i
    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(D)I"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void storeDamageInFloat(EntityHitResult entityHitResult, CallbackInfo ci, Entity entity, float f) {
        damage_calc = MathHelper.clamp((double)f * this.damage, 0.0, 2.147483647E9);
    }

    // store crit damage value
    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(JJ)J"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void storeCritDamage(EntityHitResult entityHitResult, CallbackInfo ci, Entity entity, float f, int i, long l) {
        damage_calc = Math.min(l + damage_calc, 2147483647L);
    }

    // apply precise damage value
    @WrapOperation(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean applyArmorToDamage(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
        return original.call(instance, source, (float) damage_calc);
    }
}
