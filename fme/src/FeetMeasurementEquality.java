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

        // 🔥 UC6: ADDITION (instance method)
        public QuantityLength add(QuantityLength other) {

            if (other == null)
                throw new IllegalArgumentException("Other length cannot be null");

            // Convert both to base (feet)
            double sumFeet = this.toFeet() + other.toFeet();

            // Convert back to THIS unit (first operand rule)
            double result = this.unit.fromFeet(sumFeet);

            return new QuantityLength(result, this.unit);
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

    // 🔥 STATIC ADD METHOD (API style)
    public static QuantityLength add(QuantityLength q1, QuantityLength q2) {

        if (q1 == null || q2 == null)
            throw new IllegalArgumentException("Lengths cannot be null");

        return q1.add(q2); // reuse instance logic
    }

    // Demo method
    public static void demonstrateAddition(QuantityLength q1, QuantityLength q2) {
        QuantityLength result = q1.add(q2);
        System.out.println(q1 + " + " + q2 + " = " + result);
    }

    public static void main(String[] args) {

        // Example 1: 1 ft + 12 in = 2 ft
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCH);

        demonstrateAddition(q1, q2);

        // Example 2: 1 yard + 1 ft = 1.333 yard
        QuantityLength q3 = new QuantityLength(1.0, LengthUnit.YARD);
        QuantityLength q4 = new QuantityLength(1.0, LengthUnit.FEET);

        demonstrateAddition(q3, q4);

        // Example 3: cm + inch
        QuantityLength q5 = new QuantityLength(2.54, LengthUnit.CENTIMETER);
        QuantityLength q6 = new QuantityLength(1.0, LengthUnit.INCH);

        demonstrateAddition(q5, q6);

        // Negative test
        QuantityLength q7 = new QuantityLength(-1.0, LengthUnit.FEET);
        demonstrateAddition(q7, q2);

        // Edge case: zero
        QuantityLength q8 = new QuantityLength(0.0, LengthUnit.FEET);
        demonstrateAddition(q8, q2);
    }
}
