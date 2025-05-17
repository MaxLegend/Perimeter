package ru.tesmio.perimeter.util;

import net.minecraft.core.Direction;

public enum SegmentDirection {
    CENTER, UP, DOWN, LEFT, RIGHT;

    public SegmentDirection getOpposite() {
        return switch (this) {
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case UP -> DOWN;
            case DOWN -> UP;
            case CENTER -> CENTER;
        };
    }

    public Direction getWorldDirection(Direction attachedFace) {
        return switch (this) {

            case LEFT -> switch (attachedFace) {
                case UP, DOWN -> Direction.WEST;
                case NORTH -> Direction.EAST;
                case SOUTH -> Direction.WEST;
                case EAST -> Direction.SOUTH;
                case WEST -> Direction.NORTH;
            };
            case RIGHT -> switch (attachedFace) {
                case UP, DOWN -> Direction.EAST;
                case NORTH -> Direction.WEST;
                case SOUTH -> Direction.EAST;
                case EAST -> Direction.NORTH;
                case WEST -> Direction.SOUTH;
            };
            case UP -> switch (attachedFace) {
                case UP, DOWN -> Direction.NORTH;
                case NORTH -> Direction.UP;
                case SOUTH -> Direction.DOWN;
                case EAST -> Direction.UP;
                case WEST -> Direction.UP;
            };
            case DOWN -> switch (attachedFace) {
                case UP, DOWN -> Direction.SOUTH;
                case NORTH -> Direction.DOWN;
                case SOUTH -> Direction.UP;
                case EAST -> Direction.DOWN;
                case WEST -> Direction.DOWN;
            };
            case CENTER -> attachedFace; // направление самой плоскости
        };

    }
}

