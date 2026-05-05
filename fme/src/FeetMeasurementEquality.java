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

    LengthUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public String getUnitName() {
        return name();
    }
}

// ---------- WEIGHT ----------
enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double factor;

    WeightUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public String getUnitName() {
        return name();
    }
}

// ---------- VOLUME ----------
enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double factor;

    VolumeUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public String getUnitName() {
        return name();
    }
}

// ---------- GENERIC QUANTITY ----------
class Quantity<U extends IMeasurable> {

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null)
            throw new IllegalArgumentException("Unit cannot be null");

        if (!Double.isFinite(value))
            throw new IllegalArgumentException("Value must be finite");

        this.value = value;
        this.unit = unit;
    }

    private double toBase() {
        return unit.convertToBaseUnit(value);
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
        validate(other, targetUnit);

        double sum = this.toBase() + other.toBase();
        double result = targetUnit.convertFromBaseUnit(sum);

        result = Math.round(result * 100.0) / 100.0;
        return new Quantity<>(result, targetUnit);
    }

    // ---------- 🔥 SUBTRACT (UC12) ----------
    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validate(other, targetUnit);

        double diff = this.toBase() - other.toBase();
        double result = targetUnit.convertFromBaseUnit(diff);

        result = Math.round(result * 100.0) / 100.0;
        return new Quantity<>(result, targetUnit);
    }

    // ---------- 🔥 DIVIDE (UC12) ----------
    public double divide(Quantity<U> other) {
        if (other == null)
            throw new IllegalArgumentException("Other cannot be null");

        if (!this.unit.getClass().equals(other.unit.getClass()))
            throw new IllegalArgumentException("Different measurement types");

        double divisor = other.toBase();

        if (Math.abs(divisor) < 1e-9)
            throw new ArithmeticException("Division by zero");

        return this.toBase() / divisor;
    }

    // ---------- VALIDATION ----------
    private void validate(Quantity<U> other, U targetUnit) {
        if (other == null)
            throw new IllegalArgumentException("Other cannot be null");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");

        if (!this.unit.getClass().equals(other.unit.getClass()))
            throw new IllegalArgumentException("Different measurement types");
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

        // ===== LENGTH =====
        Quantity<LengthUnit> l1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6.0, LengthUnit.INCH);

        System.out.println("Subtract Length: " + l1.subtract(l2, LengthUnit.FEET));
        System.out.println("Divide Length: " + l1.divide(l2));

        // ===== WEIGHT =====
        Quantity<WeightUnit> w1 = new Quantity<>(10.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5.0, WeightUnit.KILOGRAM);

        System.out.println("Subtract Weight: " + w1.subtract(w2, WeightUnit.KILOGRAM));
        System.out.println("Divide Weight: " + w1.divide(w2));

        // ===== VOLUME =====
        Quantity<VolumeUnit> v1 = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(2.0, VolumeUnit.LITRE);

        System.out.println("Subtract Volume: " + v1.subtract(v2, VolumeUnit.LITRE));
        System.out.println("Divide Volume: " + v1.divide(v2));
    }
}