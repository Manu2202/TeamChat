package de.swproj.teamchat.datamodell.chat;

import java.util.HashMap;
import java.util.Map;

public enum FirebaseTypes{
    Message(0),Chat(1),EVENTSTATE(2);
    private int value;
    private static Map map = new HashMap<>();

    private FirebaseTypes(int value) {
        this.value = value;
    }

    static {
        for (FirebaseTypes firebaseType : FirebaseTypes.values()) {
            map.put(firebaseType.value, firebaseType);
        }
    }

    public static FirebaseTypes valueOf(int firebaseType) {
        return (FirebaseTypes) map.get(firebaseType);
    }

    public int getValue() {
        return value;
    }
}
