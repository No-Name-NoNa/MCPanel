package moe.mcg.mcpanel.api.minecraft;

public class SimpleVec3 {
    public final double y;
    public double x;
    public double z;

    public SimpleVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }
}
