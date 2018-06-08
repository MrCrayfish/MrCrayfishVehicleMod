package net.hdt.hva.enums;

import net.minecraft.util.IStringSerializable;

public enum RoadTypes implements IStringSerializable {

    WHITE_MIDDLE_LINE_STRAIGHT(0, "white_middle_line_straight"),
    WHITE_MIDDLE_LINE_CORNER_LEFT(1, "white_middle_line_left_corner"),
    WHITE_MIDDLE_LINE_CORNER_RIGHT(2, "white_middle_line_right_corner"),
    YELLOW_MIDDLE_LINE_STRAIGHT(3, "yellow_middle_line_straight"),
    YELLOW_MIDDLE_LINE_CORNER_LEFT(4, "yellow_middle_line_left_corner"),
    YELLOW_MIDDLE_LINE_CORNER_RIGHT(5, "yellow_middle_line_right_corner"),
    WHITE_OUTER_LINE_STRAIGHT(6, "white_outer_line_straight"),
    WHITE_OUTER_LINE_CORNER_LEFT(7, "white_outer_line_left_corner"),
    WHITE_OUTER_LINE_CORNER_RIGHT(8, "white_outer_line_right_corner"),
    YELLOW_OUTER_LINE_STRAIGHT(9, "yellow_outer_line_straight"),
    YELLOW_OUTER_LINE_CORNER_LEFT(10, "yellow_outer_line_left_corner"),
    YELLOW_OUTER_LINE_CORNER_RIGHT(11, "yellow_outer_line_right_corner");

    private String name;
    private int ID;

    RoadTypes(int ID, String name) {
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
