public class FeetMeasurementEquality {

    // Enum for units (base = FEET)
    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARD(3.0),
        CENTIMETER(0.0328084);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }

        public double fromFeet(double feetValue) {
            return feetValue / toFeetFactor;
        }
    }

    // Value Object (Immutable)
    static class QuantityLength {
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

        // Convert to base unit (FEET)
        private double toFeet() {
            return unit.toFeet(value);
        }

        // Instance conversion → returns NEW object (immutability)
        public QuantityLength convertTo(LengthUnit targetUnit) {
            if (targetUnit == null)
                throw new IllegalArgumentException("Target unit cannot be null");

            double baseValue = this.toFeet();
            double converted = targetUnit.fromFeet(baseValue);

            return new QuantityLength(converted, targetUnit);
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) return true;

            if (obj == null || getClass() != obj.getClass()) return false;

            QuantityLength other = (QuantityLength) obj;

            double epsilon = 1e-6;
            return Math.abs(this.toFeet() - other.toFeet()) < epsilon;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // 🔥 STATIC API METHOD (UC5 Requirement)
    public static double convert(double value, LengthUnit source, LengthUnit target) {

        if (source == null || target == null)
            throw new IllegalArgumentException("Units cannot be null");

        if (!Double.isFinite(value))
            throw new IllegalArgumentException("Value must be finite");

        // Normalize to base (feet)
        double base = source.toFeet(value);

        // Convert to target
        return target.fromFeet(base);
    }

    // 🔥 METHOD OVERLOADING

    // Method 1: raw values
    public static void demonstrateLengthConversion(double value,
                                                   LengthUnit from,
                                                   LengthUnit to) {

        double result = convert(value, from, to);
        System.out.println(value + " " + from + " = " + result + " " + to);
    }

    // Method 2: using object
    public static void demonstrateLengthConversion(QuantityLength q,
                                                   LengthUnit to) {

        QuantityLength converted = q.convertTo(to);
        System.out.println(q + " = " + converted);
    }

    // Equality demo
    public static void demonstrateLengthEquality(QuantityLength q1,
                                                 QuantityLength q2) {

        System.out.println(q1 + " == " + q2 + " : " + q1.equals(q2));
    }

    public static void main(String[] args) {

        // --- Conversion Tests ---
        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCH);
        demonstrateLengthConversion(3.0, LengthUnit.YARD, LengthUnit.FEET);
        demonstrateLengthConversion(36.0, LengthUnit.INCH, LengthUnit.YARD);
        demonstrateLengthConversion(2.54, LengthUnit.CENTIMETER, LengthUnit.INCH);
        demonstrateLengthConversion(0.0, LengthUnit.FEET, LengthUnit.INCH);

        // --- Instance conversion ---
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        demonstrateLengthConversion(q1, LengthUnit.INCH);

        // --- Equality ---
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCH);
        demonstrateLengthEquality(q1, q2);

        // --- Round Trip Test ---
        double val = convert(convert(5.0, LengthUnit.FEET, LengthUnit.INCH),
                LengthUnit.INCH,
                LengthUnit.FEET);

        System.out.println("Round Trip (5 ft) = " + val);

        // --- Negative Test ---
        demonstrateLengthConversion(-1.0, LengthUnit.FEET, LengthUnit.INCH);

        // --- Error Handling ---
        try {
            convert(Double.NaN, LengthUnit.FEET, LengthUnit.INCH);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}