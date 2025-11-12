package moe.mcg.mcpanel.api.minecraft;

/**
 * 玩家坐标
 * @param x
 * @param y
 * @param z
 */
public record SimpleVec3(double x, double y, double z) {

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }
}
