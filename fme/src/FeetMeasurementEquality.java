// ---------- COMMON INTERFACE ----------
interface IMeasurable {

    double getConversionFactor();

    default double convertToBaseUnit(double value) {
        return value * getConversionFactor();
    }

    default double convertFromBaseUnit(double baseValue) {
        return baseValue / getConversionFactor();
    }

    String getUnitName();
}

// ---------- LENGTH ----------
enum LengthUnit implements IMeasurable {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARD(3.0),
    CENTIMETER(0.0328084);

    private final double factor;

    LengthUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }

    public String getUnitName() { return name(); }
}

// ---------- WEIGHT ----------
enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double factor;

    WeightUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }

    public String getUnitName() { return name(); }
}

// ---------- VOLUME ----------
enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double factor;

    VolumeUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }

    public String getUnitName() { return name(); }
}

// ---------- GENERIC QUANTITY ----------
class Quantity<U extends IMeasurable> {

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid value");
        this.value = value;
        this.unit = unit;
    }

    private double toBase() {
        return unit.convertToBaseUnit(value);
    }

    // 🔥 OPERATION ENUM
    private enum Operation {
        ADD, SUBTRACT, DIVIDE
    }

    // 🔥 CENTRALIZED HELPER
    private double computeBase(Quantity<U> other, Operation op) {

        if (other == null)
            throw new IllegalArgumentException("Other cannot be null");

        if (!this.unit.getClass().equals(other.unit.getClass()))
            throw new IllegalArgumentException("Different measurement types");

        double base1 = this.toBase();
        double base2 = other.toBase();

        switch (op) {
            case ADD:
                return base1 + base2;

            case SUBTRACT:
                return base1 - base2;

            case DIVIDE:
                if (Math.abs(base2) < 1e-9)
                    throw new ArithmeticException("Division by zero");
                return base1 / base2;

            default:
                throw new IllegalArgumentException("Unsupported operation");
        }
    }

    // ---------- CONVERT ----------
    public Quantity<U> convertTo(U targetUnit) {
        double base = this.toBase();
        double result = targetUnit.convertFromBaseUnit(base);
        result = Math.round(result * 100.0) / 100.0;
        return new Quantity<>(result, targetUnit);
    }

    // ---------- ADD ----------
    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        double baseResult = computeBase(other, Operation.ADD);
        double result = targetUnit.convertFromBaseUnit(baseResult);
        result = Math.round(result * 100.0) / 100.0;
        return new Quantity<>(result, targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    // ---------- SUBTRACT ----------
    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        double baseResult = computeBase(other, Operation.SUBTRACT);
        double result = targetUnit.convertFromBaseUnit(baseResult);
        result = Math.round(result * 100.0) / 100.0;
        return new Quantity<>(result, targetUnit);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    // ---------- DIVIDE ----------
    public double divide(Quantity<U> other) {
        return computeBase(other, Operation.DIVIDE);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Quantity<?>)) return false;

        Quantity<?> other = (Quantity<?>) obj;

        if (!this.unit.getClass().equals(other.unit.getClass()))
            return false;

        return Math.abs(this.toBase() - other.toBase()) < 1e-6;
    }

    @Override
    public String toString() {
        return value + " " + unit.getUnitName();
    }
}

// ---------- MAIN ----------
public class FeetMeasurementEquality {

    public static void main(String[] args) {

        Quantity<LengthUnit> l1 = new Quantity<>(10, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6, LengthUnit.INCH);

        System.out.println("Add: " + l1.add(l2));
        System.out.println("Subtract: " + l1.subtract(l2));
        System.out.println("Divide: " + l1.divide(l2));

        Quantity<WeightUnit> w1 = new Quantity<>(10, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5, WeightUnit.KILOGRAM);

        System.out.println("Weight Divide: " + w1.divide(w2));
    }
}