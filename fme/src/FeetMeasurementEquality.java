enum LengthUnit {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARD(3.0),
    CENTIMETER(0.0328084);

    private final double toFeetFactor;

    LengthUnit(double toFeetFactor) {
        this.toFeetFactor = toFeetFactor;
    }

    // Convert to base unit (feet)
    public double convertToBaseUnit(double value) {
        return value * toFeetFactor;
    }

    // Convert from base unit (feet)
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toFeetFactor;
    }
}

// Quantity class (Value Object)
class QuantityLength {

    private final double value;
    private final LengthUnit unit;

    public QuantityLength(double value, LengthUnit unit) {
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

    // Convert to another unit
    public QuantityLength convertTo(LengthUnit targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");

        double base = this.toBase();
        double converted = targetUnit.convertFromBaseUnit(base);

        return new QuantityLength(converted, targetUnit);
    }

    // UC7 Add with target unit
    public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
        if (other == null)
            throw new IllegalArgumentException("Other cannot be null");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");

        double sum = this.toBase() + other.toBase();
        double result = targetUnit.convertFromBaseUnit(sum);

        return new QuantityLength(result, targetUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof QuantityLength)) return false;

        QuantityLength other = (QuantityLength) obj;

        return Math.abs(this.toBase() - other.toBase()) < 1e-6;
    }

    @Override
    public String toString() {
        return value + " " + unit;
    }
}

// Main class (must match file name)
public class FeetMeasurementEquality {

    // Static convert API (UC5)
    public static double convert(double value,
                                 LengthUnit source,
                                 LengthUnit target) {

        if (source == null || target == null)
            throw new IllegalArgumentException("Units cannot be null");

        if (!Double.isFinite(value))
            throw new IllegalArgumentException("Value must be finite");

        double base = source.convertToBaseUnit(value);
        return target.convertFromBaseUnit(base);
    }

    public static void main(String[] args) {

        // --- Conversion ---
        System.out.println("1 ft to inch: " +
                convert(1.0, LengthUnit.FEET, LengthUnit.INCH));

        // --- Equality ---
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCH);

        System.out.println("Equal: " + q1.equals(q2));

        // --- Addition (UC7) ---
        QuantityLength result = q1.add(q2, LengthUnit.YARD);
        System.out.println(q1 + " + " + q2 + " = " + result);

        // --- Conversion (object) ---
        System.out.println(q1 + " = " + q1.convertTo(LengthUnit.INCH));
    }
}