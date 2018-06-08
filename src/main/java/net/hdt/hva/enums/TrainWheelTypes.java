package net.hdt.hva.enums;

import net.minecraft.util.IStringSerializable;

public enum TrainWheelTypes implements IStringSerializable {

    SMALL("small", 0),
    NORMAL("normal", 1),
    BIG("big", 2);

    private String name;
    private int id;

    TrainWheelTypes(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}
