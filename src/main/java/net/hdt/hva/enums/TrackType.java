package net.hdt.hva.enums;

import net.minecraft.util.IStringSerializable;

public enum TrackType implements IStringSerializable {

    STRAIGHT(0, "straight"),
    CORNER_LEFT(1, "left_corner"),
    CORNER_RIGHT(2, "right_corner");

    private String name;
    private int ID;

    TrackType(int ID, String name) {
        this.name = name;
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

}
