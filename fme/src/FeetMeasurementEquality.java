public class FeetMeasurementEquality {

    // Enum (base = FEET)
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

    // Immutable Value Object
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

        // Convert to base unit (feet)
        private double toFeet() {
            return unit.toFeet(value);
        }

        // Convert to another unit
        public QuantityLength convertTo(LengthUnit targetUnit) {
            if (targetUnit == null)
                throw new IllegalArgumentException("Target unit cannot be null");

            double base = this.toFeet();
            double converted = targetUnit.fromFeet(base);

            return new QuantityLength(converted, targetUnit);
        }

        // UC6 Addition (result in first operand unit)
        public QuantityLength add(QuantityLength other) {
            if (other == null)
                throw new IllegalArgumentException("Other length cannot be null");

            double sumFeet = this.toFeet() + other.toFeet();
            double result = this.unit.fromFeet(sumFeet);

            return new QuantityLength(result, this.unit);
        }

        // 🔥 UC7 Addition (result in TARGET UNIT)
        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {

            if (other == null)
                throw new IllegalArgumentException("Other length cannot be null");

            if (targetUnit == null)
                throw new IllegalArgumentException("Target unit cannot be null");

            // Step 1: normalize to base (feet)
            double sumFeet = this.toFeet() + other.toFeet();

            // Step 2: convert to target unit
            double result = targetUnit.fromFeet(sumFeet);

            return new QuantityLength(result, targetUnit);
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

    // 🔥 STATIC API (UC7)
    public static QuantityLength add(QuantityLength q1,
                                     QuantityLength q2,
                                     LengthUnit targetUnit) {

        if (q1 == null || q2 == null)
            throw new IllegalArgumentException("Lengths cannot be null");

        return q1.add(q2, targetUnit);
    }

    // Demo method
    public static void demonstrateAddition(QuantityLength q1,
                                           QuantityLength q2,
                                           LengthUnit target) {

        QuantityLength result = q1.add(q2, target);
        System.out.println(q1 + " + " + q2 + " = " + result);
    }

    public static void main(String[] args) {

        // Example 1: (given in question)
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCH);

        demonstrateAddition(q1, q2, LengthUnit.YARD);
        // Expected ≈ 0.667 yard

        // Example 2
        demonstrateAddition(q1, q2, LengthUnit.INCH);
        // Expected = 24 inch

        // Example 3
        QuantityLength q3 = new QuantityLength(2.54, LengthUnit.CENTIMETER);
        QuantityLength q4 = new QuantityLength(1.0, LengthUnit.INCH);

        demonstrateAddition(q3, q4, LengthUnit.CENTIMETER);

        // Example 4 (same unit target)
        demonstrateAddition(q1, q2, LengthUnit.FEET);

        // Edge case: zero
        QuantityLength q5 = new QuantityLength(0.0, LengthUnit.FEET);
        demonstrateAddition(q5, q2, LengthUnit.INCH);
    }
}