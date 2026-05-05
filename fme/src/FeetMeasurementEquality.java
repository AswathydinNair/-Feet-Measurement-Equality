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

// ---------- 🔥 NEW: VOLUME ----------
enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),      // 1 mL = 0.001 L
    GALLON(3.78541);        // 1 gal ≈ 3.78541 L

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

    // Convert
    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");

        double base = this.toBase();
        double converted = targetUnit.convertFromBaseUnit(base);

        converted = Math.round(converted * 100.0) / 100.0;
        return new Quantity<>(converted, targetUnit);
    }

    // Add (same unit)
    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    // Add (target unit)
    public Quantity<U> add(Quantity<U> other, U targetUnit) {

        if (other == null)
            throw new IllegalArgumentException("Other cannot be null");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");

        if (!this.unit.getClass().equals(other.unit.getClass()))
            throw new IllegalArgumentException("Different measurement types");

        double sum = this.toBase() + other.toBase();
        double result = targetUnit.convertFromBaseUnit(sum);

        result = Math.round(result * 100.0) / 100.0;

        return new Quantity<>(result, targetUnit);
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
        Quantity<LengthUnit> l1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12.0, LengthUnit.INCH);

        System.out.println("Length Equal: " + l1.equals(l2));
        System.out.println("Length Add: " + l1.add(l2, LengthUnit.YARD));

        // ===== WEIGHT =====
        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000.0, WeightUnit.GRAM);

        System.out.println("Weight Equal: " + w1.equals(w2));
        System.out.println("Weight Convert: " + w2.convertTo(WeightUnit.POUND));


        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);

        System.out.println("Volume Equal: " + v1.equals(v2));

        Quantity<VolumeUnit> v3 = new Quantity<>(1.0, VolumeUnit.GALLON);
        System.out.println("Volume Convert: " + v3.convertTo(VolumeUnit.LITRE));

        System.out.println("Volume Add: " + v1.add(v3, VolumeUnit.LITRE));


        System.out.println("Length vs Volume: " + l1.equals(v1)); // false
    }
}