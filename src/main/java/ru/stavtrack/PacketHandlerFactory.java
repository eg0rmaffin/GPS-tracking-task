package ru.stavtrack;

import java.util.HashMap;
import java.util.Map;

public class PacketHandlerFactory {
    private static final Map<Integer, PacketHandler> handlers = new HashMap<>();

    static {
        handlers.put(0x004C, new UniversalPacketHandler());
        handlers.put(0x0B07, new UniversalPacketHandler());
        // другие типы пакетов допишу после трудоустройства ;)
    }

    public static PacketHandler getHandler(int packetType) {
        return handlers.get(packetType);
    }
}
