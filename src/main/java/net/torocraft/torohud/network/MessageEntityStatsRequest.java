package net.torocraft.torohud.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.torohud.ToroHUD;

public class MessageEntityStatsRequest implements IMessage {

  public int id;

  public static void init(int packetId) {
    ToroHUD.NETWORK.registerMessage(Handler.class, MessageEntityStatsRequest.class, packetId, Side.SERVER);
  }

  public MessageEntityStatsRequest() {

  }

  public MessageEntityStatsRequest(int id) {
    this.id = id;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    id = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(id);
  }

  public static class Handler implements IMessageHandler<MessageEntityStatsRequest, IMessage> {

    @Override
    public IMessage onMessage(final MessageEntityStatsRequest message, MessageContext ctx) {
      final EntityPlayerMP player = ctx.getServerHandler().player;
      player.getServerWorld().addScheduledTask(() -> work(player, message));
      return null;
    }

    private void work(EntityPlayerMP player, MessageEntityStatsRequest message) {
      Entity entity = player.world.getEntityByID(message.id);
      if (!(entity instanceof EntityLivingBase)) {
        return;
      }
      EntityLivingBase entityLiving = (EntityLivingBase)entity;

      MessageEntityStatsResponse reply = new MessageEntityStatsResponse(message.id, entityLiving.getActivePotionEffects());
      ToroHUD.NETWORK.sendTo(reply, player);
    }
  }

}