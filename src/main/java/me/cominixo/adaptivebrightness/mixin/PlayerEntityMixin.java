package me.cominixo.adaptivebrightness.mixin;

import me.cominixo.adaptivebrightness.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void adaptBrightness(CallbackInfo ci) {

        if (Config.disabled) {
            return;
        }

        // I wanted to "fix" the "issue" where the world gets brighter if you stand under something
        // at night time. you have to see it to get it
        // alas, I don't think there's an elegant way of fixing that. and it's not that big of
        // a deal in practice. if you DO find a way to fix it, then, well. it'd be cool to see

//        BlockPos blockPos = client.getCameraEntity().getBlockPos();
//        int totalSkyLight = client.world.getLightLevel(LightType.SKY, blockPos);
//        int totalBlockLight = client.world.getLightLevel(LightType.BLOCK, blockPos);
//        System.out.println(totalSkyLight);
//        System.out.println(totalBlockLight);

        if (((LivingEntity) (Object) this).getWorld().isClient) {
            MinecraftClient client = MinecraftClient.getInstance();
            int lightPosX = ((Entity)(Object)this).getBlockX();
            int lightPosY = (int) ((Entity)(Object)this).getEyeY() - 1;
            int lightPosZ = ((Entity)(Object)this).getBlockZ();
            BlockPos lightPos = new BlockPos(lightPosX, lightPosY, lightPosZ);
            int totalLight = client.world.getChunkManager().getLightingProvider().getLight(lightPos, 0);
            double gammaTarget = clamp(Math.abs(totalLight-15.0)/15.0, Config.min_gamma, Config.max_gamma);
            double gamma = client.options.getGamma().getValue();
            if (gamma < gammaTarget) {
                client.options.getGamma().setValue(Math.min(gamma + 0.05, gammaTarget));
            } else if (gamma > gammaTarget) {
                client.options.getGamma().setValue(Math.min(gamma - 0.05, gammaTarget));
            }

        }
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

}
