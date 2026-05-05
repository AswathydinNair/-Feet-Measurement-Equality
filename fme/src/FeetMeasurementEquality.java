// ---------- LENGTH UNIT ----------
enum LengthUnit {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARD(3.0),
    CENTIMETER(0.0328084);

    private final double toFeetFactor;

    LengthUnit(double factor) {
        this.toFeetFactor = factor;
    }

    public double convertToBaseUnit(double value) {
        return value * toFeetFactor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toFeetFactor;
    }
}

// ---------- WEIGHT UNIT ----------
enum WeightUnit {
    KILOGRAM(1.0),
    GRAM(0.001),          // 1 g = 0.001 kg
    POUND(0.453592);      // 1 lb ≈ 0.453592 kg

    private final double toKgFactor;

    WeightUnit(double factor) {
        this.toKgFactor = factor;
    }

    public double convertToBaseUnit(double value) {
        return value * toKgFactor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toKgFactor;
    }
}

// ---------- LENGTH CLASS ----------
class QuantityLength {

    private final double value;
    private final LengthUnit unit;

    public QuantityLength(double value, LengthUnit unit) {
        if (unit == null || !Double.isFinite(value))
            throw new IllegalArgumentException("Invalid length");

        this.value = value;
        this.unit = unit;
    }

    private double toBase() {
        return unit.convertToBaseUnit(value);
    }

    public QuantityLength convertTo(LengthUnit target) {
        double base = this.toBase();
        return new QuantityLength(target.convertFromBaseUnit(base), target);
    }

    public QuantityLength add(QuantityLength other, LengthUnit target) {
        double sum = this.toBase() + other.toBase();
        return new QuantityLength(target.convertFromBaseUnit(sum), target);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QuantityLength)) return false;
        QuantityLength other = (QuantityLength) obj;
        return Math.abs(this.toBase() - other.toBase()) < 1e-6;
    }

    public String toString() {
        return value + " " + unit;
    }
}

// ---------- WEIGHT CLASS ----------
class QuantityWeight {

    private final double value;
    private final WeightUnit unit;

    public QuantityWeight(double value, WeightUnit unit) {
        if (unit == null || !Double.isFinite(value))
            throw new IllegalArgumentException("Invalid weight");

        this.value = value;
        this.unit = unit;
    }

    private double toBase() {
        return unit.convertToBaseUnit(value);
    }

    // Convert
    public QuantityWeight convertTo(WeightUnit target) {
        double base = this.toBase();
        return new QuantityWeight(target.convertFromBaseUnit(base), target);
    }

    // Add (UC7 style - target unit)
    public QuantityWeight add(QuantityWeight other, WeightUnit target) {
        double sum = this.toBase() + other.toBase();
        return new QuantityWeight(target.convertFromBaseUnit(sum), target);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QuantityWeight)) return false;
        QuantityWeight other = (QuantityWeight) obj;
        return Math.abs(this.toBase() - other.toBase()) < 1e-6;
    }

    public String toString() {
        return value + " " + unit;
    }
}

// ---------- MAIN CLASS ----------
public class FeetMeasurementEquality {

    public static void main(String[] args) {

        // ===== LENGTH =====
        QuantityLength l1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength l2 = new QuantityLength(12.0, LengthUnit.INCH);

        System.out.println("Length Equal: " + l1.equals(l2));
        System.out.println("Length Add: " + l1.add(l2, LengthUnit.YARD));
        System.out.println("Length Convert: " + l1.convertTo(LengthUnit.INCH));

        // ===== WEIGHT =====
        QuantityWeight w1 = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight w2 = new QuantityWeight(1000.0, WeightUnit.GRAM);

        System.out.println("Weight Equal: " + w1.equals(w2));

        QuantityWeight w3 = new QuantityWeight(2.0, WeightUnit.KILOGRAM);
        QuantityWeight w4 = new QuantityWeight(1.0, WeightUnit.POUND);

        System.out.println("Weight Add: " + w3.add(w4, WeightUnit.KILOGRAM));
        System.out.println("Weight Convert: " + w4.convertTo(WeightUnit.GRAM));
    }
}