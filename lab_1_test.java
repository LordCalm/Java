import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class lab_1_test {
    @Test
    void testAddition() {
        lab_1.MyBigInt a = new lab_1.MyBigInt(10);
        lab_1.MyBigInt b = new lab_1.MyBigInt(5);
        lab_1.MyBigInt result = lab_1.MyBigInt.add(a, b);

        assertEquals("15", result.toString());
    }

    @Test
    void testSubtraction() {
        lab_1.MyBigInt a = new lab_1.MyBigInt(20);
        lab_1.MyBigInt b = new lab_1.MyBigInt(7);
        lab_1.MyBigInt result = lab_1.MyBigInt.subtract(a, b);

        assertEquals("13", result.toString());
    }

    @Test
    void testSubtractionNegativeResult() {
        lab_1.MyBigInt a = new lab_1.MyBigInt(7);
        lab_1.MyBigInt b = new lab_1.MyBigInt(20);
        lab_1.MyBigInt result = lab_1.MyBigInt.subtract(a, b);

        assertEquals("-13", result.toString());
    }

    @Test
    void testConstructorFromString() {
        lab_1.MyBigInt a = new lab_1.MyBigInt("12345678901234567890");
        lab_1.MyBigInt b = new lab_1.MyBigInt("98765432109876543210");
        lab_1.MyBigInt sum = lab_1.MyBigInt.add(a, b);

        assertEquals("111111111011111111100", sum.toString());
    }

    @Test
    void testZero() {
        lab_1.MyBigInt zero = new lab_1.MyBigInt(0);
        lab_1.MyBigInt a = new lab_1.MyBigInt(123);
        lab_1.MyBigInt result = lab_1.MyBigInt.add(a, zero);

        assertEquals("123", result.toString());
    }

    @Test
    void testSubtractionLargeNumbers() {
        lab_1.MyBigInt a = new lab_1.MyBigInt("100000000000000000000");
        lab_1.MyBigInt b = new lab_1.MyBigInt("99999999999999999999");
        lab_1.MyBigInt result = lab_1.MyBigInt.subtract(a, b);

        assertEquals("1", result.toString());
    }
}
