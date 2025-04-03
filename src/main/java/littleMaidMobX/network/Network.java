package littleMaidMobX.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import littleMaidMobX.LittleMaidMobX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class Network {
    //このMOD用のSimpleNetworkWrapperを生成。チャンネルの文字列は固有であれば何でも良い。MODIDの利用を推奨。
    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LittleMaidMobX.MOD_ID);

    public static void init() {
        /*IMesssageHandlerクラスとMessageクラスの登録。
         *第三引数：MessageクラスのMOD内での登録ID。256個登録できる
         *第四引数：送り先指定。クライアントかサーバーか、Side.CLIENT Side.SERVER*/
        // どうせまた変わるだろうから最低限のみ登録。メッセージの振り分けはMOD側で行う。
        INSTANCE.registerMessage(MessageHandler.class, Message.class, 0, Side.SERVER);
        INSTANCE.registerMessage(MessageHandler.class, Message.class, 0, Side.CLIENT);
    }

    public static void sendPacketToServer(int ch, byte[] data) {
        INSTANCE.sendToServer(new Message(ch, data));
    }

    public static void sendPacketToPlayer(int ch, EntityPlayer player, byte[] data) {
        if (player instanceof EntityPlayerMP) {
            INSTANCE.sendTo(new Message(ch, data), (EntityPlayerMP) player);
        }
    }

    public static void sendPacketToAllPlayer(int ch, byte[] data) {
        INSTANCE.sendToAll(new Message(ch, data));
    }
}