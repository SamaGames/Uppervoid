package com.geekpower14.uppervoid.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.server.v1_8_R3.*;

import java.util.List;
import java.util.Map;

public class TNTExplosion
{
    private final boolean a;
    private final boolean b;
    private final World world;
    private final double posX;
    private final double posY;
    private final double posZ;
    private final Entity source;
    private final float size;
    private final Map<EntityHuman, Vec3D> k = Maps.newHashMap();

    public TNTExplosion(World world, Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1)
    {
        this.world = world;
        this.source = entity;
        this.size = (float) Math.max((double) f, 0.0D);
        this.posX = d0;
        this.posY = d1;
        this.posZ = d2;
        this.a = flag;
        this.b = flag1;
    }

    public void explode()
    {
        a();
        a(true);

        this.world.players.stream().filter(entityHuman -> entityHuman.e(this.posX, this.posY, this.posZ) < 4096.0D).forEach(entityHuman ->
                ((EntityPlayer) entityHuman).playerConnection.sendPacket(new PacketPlayOutExplosion(this.posX, this.posY, this.posZ, this.size, Lists.newArrayList(), k.get(entityHuman))));
    }

    public void a()
    {
        if(this.size >= 0.1F)
        {
            int i;
            int j;

            float var48 = this.size * 2.0F;

            i = MathHelper.floor(this.posX - (double) var48 - 1.0D);
            j = MathHelper.floor(this.posX + (double) var48 + 1.0D);

            int l = MathHelper.floor(this.posY - (double) var48 - 1.0D);
            int i1 = MathHelper.floor(this.posY + (double) var48 + 1.0D);
            int j1 = MathHelper.floor(this.posZ - (double) var48 - 1.0D);
            int k1 = MathHelper.floor(this.posZ + (double) var48 + 1.0D);

            List list = this.world.getEntities(this.source, new AxisAlignedBB((double) i, (double) l, (double) j1, (double) j, (double) i1, (double) k1));
            Vec3D vec3d = new Vec3D(this.posX, this.posY, this.posZ);

            for(int l1 = 0; l1 < list.size(); ++l1)
            {
                Entity entity = (Entity)list.get(l1);
                double d7 = entity.f(this.posX, this.posY, this.posZ) / (double) var48;

                if(d7 <= 1.0D)
                {
                    double d8 = entity.locX - this.posX;
                    double d9 = entity.locY + (double) entity.getHeadHeight() - this.posY;
                    double d10 = entity.locZ - this.posZ;
                    double d11 = (double) MathHelper.sqrt(d8 * d8 + d9 * d9 + d10 * d10);

                    if(d11 != 0.0D)
                    {
                        d8 /= d11;
                        d9 /= d11;
                        d10 /= d11;

                        double d12 = (double) this.world.a(vec3d, entity.getBoundingBox());
                        double d13 = (1.0D - d7) * d12;

                        entity.motX += d8;
                        entity.motY += d9;
                        entity.motZ += d10;

                        if(entity instanceof EntityHuman)
                            this.k.put((EntityHuman) entity, new Vec3D(d8 * d13, d9 * d13, d10 * d13));
                    }
                }
            }
        }
    }

    public void a(boolean flag)
    {
        this.world.makeSound(this.posX, this.posY, this.posZ, "random.explode", 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);

        if(this.size >= 2.0F && this.b)
            this.world.addParticle(EnumParticle.EXPLOSION_HUGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D, new int[0]);
        else
            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D, new int[0]);

        if(this.b)
        {
            for(int i = 0; i < 15 ; i++)
            {
                if(flag)
                {
                    double d0 = this.world.random.nextFloat();
                    double d1 = this.world.random.nextFloat();
                    double d2 = this.world.random.nextFloat();
                    double d3 = d0 - this.posX;
                    double d4 = d1 - this.posY;
                    double d5 = d2 - this.posZ;
                    double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;

                    double d7 = 0.5D / (d6 / (double) this.size + 0.1D);

                    d7 *= (double) (this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
                    d3 *= d7;
                    d4 *= d7;
                    d5 *= d7;

                    this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, (d0 + this.posX * 1.0D) / 2.0D, (d1 + this.posY * 1.0D) / 2.0D, (d2 + this.posZ * 1.0D) / 2.0D, d3, d4, d5);
                    this.world.addParticle(EnumParticle.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
                }
            }
        }
    }
}
