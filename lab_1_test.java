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
    void testAdditionWithNegative() {
        lab_1.MyBigInt a = new lab_1.MyBigInt(-50);
        lab_1.MyBigInt b = new lab_1.MyBigInt(20);
        lab_1.MyBigInt result = lab_1.MyBigInt.add(a, b);
        assertEquals("-30", result.toString());
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
    void testSubtractionToZero() {
        lab_1.MyBigInt a = new lab_1.MyBigInt("123456789123456789");
        lab_1.MyBigInt b = new lab_1.MyBigInt("123456789123456789");
        lab_1.MyBigInt result = lab_1.MyBigInt.subtract(a, b);
        assertEquals("0", result.toString());
    }

    @Test
    void testConstructorFromString() {
        lab_1.MyBigInt a = new lab_1.MyBigInt("12345678901234567890");
        lab_1.MyBigInt b = new lab_1.MyBigInt("98765432109876543210");
        lab_1.MyBigInt sum = lab_1.MyBigInt.add(a, b);

        assertEquals("111111111011111111100", sum.toString());
    }

    @Test
    void testNegativeFromString() {
        lab_1.MyBigInt a = new lab_1.MyBigInt("-12345678901234567890");
        assertEquals("-12345678901234567890", a.toString());
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

    @Test
    void testMultiplySmall() {
        lab_1.MyBigInt a = new lab_1.MyBigInt(7);
        lab_1.MyBigInt b = new lab_1.MyBigInt(6);
        lab_1.MyBigInt result = lab_1.MyBigInt.multiplyKaratsuba(a, b);
        assertEquals("42", result.toString());
    }

    @Test
    void testMultiplyByZero() {
        lab_1.MyBigInt a = new lab_1.MyBigInt("12345678901234567890");
        lab_1.MyBigInt zero = new lab_1.MyBigInt(0);
        lab_1.MyBigInt result = lab_1.MyBigInt.multiplyKaratsuba(a, zero);
        assertEquals("0", result.toString());
    }

    @Test
    void testMultiplyByOne() {
        lab_1.MyBigInt a = new lab_1.MyBigInt("98765432109876543210");
        lab_1.MyBigInt one = new lab_1.MyBigInt(1);
        lab_1.MyBigInt result = lab_1.MyBigInt.multiplyKaratsuba(a, one);
        assertEquals(a.toString(), result.toString());
    }

    @Test
    void testMultiplyLargeNumbers() {
        lab_1.MyBigInt a = new lab_1.MyBigInt("12345678901234567890");
        lab_1.MyBigInt b = new lab_1.MyBigInt("98765432109876543210");
        lab_1.MyBigInt result = lab_1.MyBigInt.multiplyKaratsuba(a, b);

        // проверим через BigInteger для надёжности
        java.math.BigInteger expected = new java.math.BigInteger("12345678901234567890")
                .multiply(new java.math.BigInteger("98765432109876543210"));
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void testMultiplyNegative() {
        lab_1.MyBigInt a = new lab_1.MyBigInt(-12345);
        lab_1.MyBigInt b = new lab_1.MyBigInt(6789);
        lab_1.MyBigInt result = lab_1.MyBigInt.multiplyKaratsuba(a, b);

        java.math.BigInteger expected = java.math.BigInteger.valueOf(-12345)
                .multiply(java.math.BigInteger.valueOf(6789));
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void testMultiplyBothNegative() {
        lab_1.MyBigInt a = new lab_1.MyBigInt("-123456789");
        lab_1.MyBigInt b = new lab_1.MyBigInt("-987654321");
        lab_1.MyBigInt result = lab_1.MyBigInt.multiplyKaratsuba(a, b);

        java.math.BigInteger expected = new java.math.BigInteger("-123456789")
                .multiply(new java.math.BigInteger("-987654321"));
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void testSetAndGetBlock_Normal() {
        lab_1.MyBigInt num = new lab_1.MyBigInt(0);
        num.setBlock(0, 12345L);
        assertEquals(12345L, num.getBlock(0));
    }

    
    @Test
    void testSetBlock_TruncateToMask() {
        lab_1.MyBigInt num = new lab_1.MyBigInt(0);
        long bigValue = (1L << 63) | 999L; // превышает 63 бита
        num.setBlock(0, bigValue);
        assertEquals(999L, num.getBlock(0), "Должен отбросить лишний 64-й бит");
    }

    @Test
    void testSetBlock_FirstAndLastIndex() {
        lab_1.MyBigInt num = new lab_1.MyBigInt(new long[]{1, 1}, true);
        num.setBlock(0, 111L);
        num.setBlock(1, 222L);

        assertEquals(111L, num.getBlock(0));
        assertEquals(222L, num.getBlock(1));
    }

    @Test
    void testGetBlock_IndexOutOfBounds_Negative() {
        lab_1.MyBigInt num = new lab_1.MyBigInt(0);
        assertThrows(IndexOutOfBoundsException.class, () -> num.getBlock(-1));
    }

    @Test
    void testGetBlock_IndexOutOfBounds_TooBig() {
        lab_1.MyBigInt num = new lab_1.MyBigInt(0);
        assertThrows(IndexOutOfBoundsException.class, () -> num.getBlock(1));
    }

    @Test
    void testSetBlock_IndexOutOfBounds_Negative() {
        lab_1.MyBigInt num = new lab_1.MyBigInt(0);
        assertThrows(IndexOutOfBoundsException.class, () -> num.setBlock(-1, 123));
    }

    @Test
    void testSetBlock_IndexOutOfBounds_TooBig() {
        lab_1.MyBigInt num = new lab_1.MyBigInt(0);
        assertThrows(IndexOutOfBoundsException.class, () -> num.setBlock(5, 123));
    }

    @Test
    void testMultipleSetAndGet() {
        lab_1.MyBigInt num = new lab_1.MyBigInt(new long[]{1, 1, 1}, true);
        num.setBlock(0, 10L);
        num.setBlock(1, 20L);
        num.setBlock(2, 30L);

        assertEquals(10L, num.getBlock(0));
        assertEquals(20L, num.getBlock(1));
        assertEquals(30L, num.getBlock(2));
    }

    @Test
    void testGetBlock_DefaultValue() {
        lab_1.MyBigInt num = new lab_1.MyBigInt(0);
        assertEquals(0L, num.getBlock(0), "Новый объект должен содержать 0 в блоке");
    }
}