package de.swproj.teamchat.datamodell.chat;

import java.util.HashMap;
import java.util.Map;

public enum FirebaseActions{
    UPDATE(0),ADD(1),REMOVE(2);
    private int value;
    private static Map map = new HashMap<>();

    FirebaseActions(int value) {
        this.value = value;
    }

    static {
        for (FirebaseActions firebaseAction : FirebaseActions.values()) {
            map.put(firebaseAction.value, firebaseAction);
        }
    }

    public static FirebaseActions valueOf(int firebaseAction) {
        return (FirebaseActions) map.get(firebaseAction);
    }

    public int getValue() {
        return value;
    }
}
