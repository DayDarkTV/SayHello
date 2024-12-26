package com.daysportal.sayhello.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class StarlightParticle extends SpriteBillboardParticle {

    private final PlayerEntity owner;

    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected int targetChangeCooldown = 0;

    // this class will be used to create a custom particle
    // the particle will spawn around the player whenever a message is sent in chat
    // the particle will glow faintly after each message, and hover nearby for a few seconds

    protected StarlightParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);
        this.owner = clientWorld.getClosestPlayer(TargetPredicate.createNonAttackable().setBaseMaxDistance(1D), this.x, this.y, this.z);
        this.setSprite(spriteProvider);
        this.maxAge = ThreadLocalRandom.current().nextInt(200, 1200);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        if ((this.owner == null || !this.owner.isAlive()) && this.age++ <= this.maxAge) {
            this.age = this.maxAge;
        }

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        this.move(this.velocityX, this.velocityY, this.velocityZ);

        this.velocityX *= 0.99;
        this.velocityY *= 0.99;
        this.velocityZ *= 0.99;

        if (this.owner != null && this.targetChangeCooldown-- <= 0) {
            this.setTargetBlock();
            this.velocityX = this.velocityX + (this.targetX - (this.x + this.velocityX)) * 0.5;
            this.velocityY = this.velocityY + (this.targetY - (this.y + this.velocityY)) * 0.5;
            this.velocityZ = this.velocityZ + (this.targetZ - (this.z + this.velocityZ)) * 0.5;
        }

        this.velocityX = Math.max(this.velocityX, 0.5);
        this.velocityY = Math.max(this.velocityY, 0.5);
        this.velocityZ = Math.max(this.velocityZ, 0.5);

        if (this.age++ >= this.maxAge) {
            this.alpha = this.alpha - 0.1F;
            if (this.alpha <= 0.0F) {
                this.markDead();
            }
        }
    }

    private void setTargetBlock() {
        double groundLevel = 0;
        for (int i = 0; i < 20; i++) {
            BlockState blockState = this.world.getBlockState(BlockPos.ofFloored(this.x, this.y - i, this.z));
            if (!blockState.getBlock().canMobSpawnInside(blockState)) {
                groundLevel = this.y - i;
            }
            if (groundLevel != 0) break;
        }

        this.targetX = this.owner.getX() + random.nextGaussian();
        this.targetY = Math.min(Math.max(this.owner.getY() + random.nextGaussian() * 2, groundLevel), groundLevel + 2);
        this.targetZ = this.owner.getZ() + random.nextGaussian();

        BlockPos blockPos = BlockPos.ofFloored(this.targetX, this.targetY, this.targetZ);
        if (this.world.getBlockState(blockPos).isFullCube(this.world, blockPos) &&
                this.world.getBlockState(blockPos).isSolidBlock(this.world, blockPos)) {
            this.targetY += 1;
        }
        targetChangeCooldown = random.nextInt() % 100;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public DefaultFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType DefaultParticleType, ClientWorld world, double x, double y, double z, double vX, double vY, double vZ) {
            return new StarlightParticle(world, x, y, z, this.spriteProvider);
        }
    }

}
