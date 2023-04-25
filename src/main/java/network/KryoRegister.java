package network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

public class KryoRegister {
    private static void registerWithKryo (Kryo kryo) {
        kryo.register(KryoRequest.class);
        kryo.register(KryoResponse.class);
        kryo.register(KryoProbe.class);
        kryo.register(KryoProbeResponse.class);
    }

    public static void register (Client client) {
        Kryo kryo = client.getKryo();
        registerWithKryo(kryo);
    }

    public static void register (Server server) {
        Kryo kryo = server.getKryo();
        registerWithKryo(kryo);
    }
}
