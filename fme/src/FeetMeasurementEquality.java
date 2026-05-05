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

// ---------- LENGTH UNIT ----------
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

// ---------- WEIGHT UNIT ----------
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

        // Round to 2 decimal places
        converted = Math.round(converted * 100.0) / 100.0;

        return new Quantity<>(converted, targetUnit);
    }

    // Add (same unit as first)
    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    // Add (target unit)
    public Quantity<U> add(Quantity<U> other, U targetUnit) {

        if (other == null)
            throw new IllegalArgumentException("Other cannot be null");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");

        // Type safety (extra runtime check)
        if (!this.unit.getClass().equals(other.unit.getClass())) {
            throw new IllegalArgumentException("Cannot add different measurement types");
        }

        double sum = this.toBase() + other.toBase();
        double result = targetUnit.convertFromBaseUnit(sum);

        result = Math.round(result * 100.0) / 100.0;

        return new Quantity<>(result, targetUnit);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (!(obj instanceof Quantity<?>)) return false;

        Quantity<?> other = (Quantity<?>) obj;

        // Prevent cross-category comparison
        if (!this.unit.getClass().equals(other.unit.getClass()))
            return false;

        double epsilon = 1e-6;
        return Math.abs(this.toBase() - other.toBase()) < epsilon;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(toBase());
    }

    @Override
    public String toString() {
        return value + " " + unit.getUnitName();
    }
}

// ---------- MAIN CLASS ----------
public class FeetMeasurementEquality {

    public static void main(String[] args) {

        // ===== LENGTH =====
        Quantity<LengthUnit> l1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12.0, LengthUnit.INCH);

        System.out.println("Length Equal: " + l1.equals(l2));
        System.out.println("Length Convert: " + l1.convertTo(LengthUnit.INCH));
        System.out.println("Length Add: " + l1.add(l2, LengthUnit.YARD));

        // ===== WEIGHT =====
        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000.0, WeightUnit.GRAM);

        System.out.println("Weight Equal: " + w1.equals(w2));
        System.out.println("Weight Convert: " + w2.convertTo(WeightUnit.KILOGRAM));
        System.out.println("Weight Add: " + w1.add(w2, WeightUnit.POUND));


        try {
            Quantity raw1 = new Quantity(1.0, LengthUnit.FEET);
            Quantity raw2 = new Quantity(1.0, WeightUnit.KILOGRAM);
            raw1.add(raw2);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}