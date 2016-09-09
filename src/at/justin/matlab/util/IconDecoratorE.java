package at.justin.matlab.util;

/**
 * Created by Andreas Justin on 2016-08-25.
 */
public enum IconDecoratorE {
    NORTH_EAST_INSIDE(45),
    SOUTH_EAST_INSIDE(135),
    SOUTH_WEST_INSIDE(225),
    NORTH_WEST_INSIDE(315),
    NORTH_OUTSIDE(360),
    EAST_OUTSIDE(360 + 90),
    SOUTH_OUTSIDE(360 + 180),
    WEST_OUTSIDE(360 + 270);

    private final int location;

    IconDecoratorE(int location) {
        this.location = location;
    }

    public int getLocation() {
        return location;
    }
}
