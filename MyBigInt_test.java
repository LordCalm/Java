package lr1;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyBigInt_test {
    @Test
    void testAddition() {
        MyBigInt a = new MyBigInt(10);
        MyBigInt b = new MyBigInt(5);
        MyBigInt result = MyBigInt.add(a, b);

        assertEquals("15", result.toString());
    }

    @Test
    void testAdditionWithNegative() {
        MyBigInt a = new MyBigInt(-50);
        MyBigInt b = new MyBigInt(20);
        MyBigInt result = MyBigInt.add(a, b);
        assertEquals("-30", result.toString());
    }

    @Test
    void testSubtraction() {
        MyBigInt a = new MyBigInt(20);
        MyBigInt b = new MyBigInt(7);
        MyBigInt result = MyBigInt.subtract(a, b);

        assertEquals("13", result.toString());
    }

    @Test
    void testSubtractionNegativeResult() {
        MyBigInt a = new MyBigInt(7);
        MyBigInt b = new MyBigInt(20);
        MyBigInt result = MyBigInt.subtract(a, b);

        assertEquals("-13", result.toString());
    }

    @Test
    void testSubtractionToZero() {
        MyBigInt a = new MyBigInt("123456789123456789");
        MyBigInt b = new MyBigInt("123456789123456789");
        MyBigInt result = MyBigInt.subtract(a, b);
        assertEquals("0", result.toString());
    }

    @Test
    void testConstructorFromString() {
        MyBigInt a = new MyBigInt("12345678901234567890");
        MyBigInt b = new MyBigInt("98765432109876543210");
        MyBigInt sum = MyBigInt.add(a, b);

        assertEquals("111111111011111111100", sum.toString());
    }

    @Test
    void testNegativeFromString() {
        MyBigInt a = new MyBigInt("-12345678901234567890");
        assertEquals("-12345678901234567890", a.toString());
    }

    @Test
    void testZero() {
        MyBigInt zero = new MyBigInt(0);
        MyBigInt a = new MyBigInt(123);
        MyBigInt result = MyBigInt.add(a, zero);

        assertEquals("123", result.toString());
    }

    @Test
    void testSubtractionLargeNumbers() {
        MyBigInt a = new MyBigInt("100000000000000000000");
        MyBigInt b = new MyBigInt("99999999999999999999");
        MyBigInt result = MyBigInt.subtract(a, b);

        assertEquals("1", result.toString());
    }

    @Test
    void testMultiplySmall() {
        MyBigInt a = new MyBigInt(7);
        MyBigInt b = new MyBigInt(6);
        MyBigInt result = MyBigInt.multiplyKaratsuba(a, b);
        assertEquals("42", result.toString());
    }

    @Test
    void testMultiplyByZero() {
        MyBigInt a = new MyBigInt("12345678901234567890");
        MyBigInt zero = new MyBigInt(0);
        MyBigInt result = MyBigInt.multiplyKaratsuba(a, zero);
        assertEquals("0", result.toString());
    }

    @Test
    void testMultiplyByOne() {
        MyBigInt a = new MyBigInt("98765432109876543210");
        MyBigInt one = new MyBigInt(1);
        MyBigInt result = MyBigInt.multiplyKaratsuba(a, one);
        assertEquals(a.toString(), result.toString());
    }

    @Test
    void testMultiplyLargeNumbers() {
        MyBigInt a = new MyBigInt("12345678901234567890");
        MyBigInt b = new MyBigInt("98765432109876543210");
        MyBigInt result = MyBigInt.multiplyKaratsuba(a, b);

        // проверим через BigInteger для надёжности
        java.math.BigInteger expected = new java.math.BigInteger("12345678901234567890")
                .multiply(new java.math.BigInteger("98765432109876543210"));
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void testMultiplyNegative() {
        MyBigInt a = new MyBigInt(-12345);
        MyBigInt b = new MyBigInt(6789);
        MyBigInt result = MyBigInt.multiplyKaratsuba(a, b);

        java.math.BigInteger expected = java.math.BigInteger.valueOf(-12345)
                .multiply(java.math.BigInteger.valueOf(6789));
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void testMultiplyBothNegative() {
        MyBigInt a = new MyBigInt("-123456789");
        MyBigInt b = new MyBigInt("-987654321");
        MyBigInt result = MyBigInt.multiplyKaratsuba(a, b);

        java.math.BigInteger expected = new java.math.BigInteger("-123456789")
                .multiply(new java.math.BigInteger("-987654321"));
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void testSetAndGetBlock_Normal() {
        MyBigInt num = new MyBigInt(0);
        num.setBlock(0, 12345L);
        assertEquals(12345L, num.getBlock(0));
    }

    
    @Test
    void testSetBlock_TruncateToMask() {
        MyBigInt num = new MyBigInt(0);
        long bigValue = (1L << 63) | 999L; // превышает 63 бита
        num.setBlock(0, bigValue);
        assertEquals(999L, num.getBlock(0), "Должен отбросить лишний 64-й бит");
    }

    @Test
    void testSetBlock_FirstAndLastIndex() {
        MyBigInt num = new MyBigInt(new long[]{1, 1}, true);
        num.setBlock(0, 111L);
        num.setBlock(1, 222L);

        assertEquals(111L, num.getBlock(0));
        assertEquals(222L, num.getBlock(1));
    }

    @Test
    void testGetBlock_IndexOutOfBounds_Negative() {
        MyBigInt num = new MyBigInt(0);
        assertThrows(IndexOutOfBoundsException.class, () -> num.getBlock(-1));
    }

    @Test
    void testGetBlock_IndexOutOfBounds_TooBig() {
        MyBigInt num = new MyBigInt(0);
        assertThrows(IndexOutOfBoundsException.class, () -> num.getBlock(1));
    }

    @Test
    void testSetBlock_IndexOutOfBounds_Negative() {
        MyBigInt num = new MyBigInt(0);
        assertThrows(IndexOutOfBoundsException.class, () -> num.setBlock(-1, 123));
    }

    @Test
    void testSetBlock_IndexOutOfBounds_TooBig() {
        MyBigInt num = new MyBigInt(0);
        assertThrows(IndexOutOfBoundsException.class, () -> num.setBlock(5, 123));
    }

    @Test
    void testMultipleSetAndGet() {
        MyBigInt num = new MyBigInt(new long[]{1, 1, 1}, true);
        num.setBlock(0, 10L);
        num.setBlock(1, 20L);
        num.setBlock(2, 30L);

        assertEquals(10L, num.getBlock(0));
        assertEquals(20L, num.getBlock(1));
        assertEquals(30L, num.getBlock(2));
    }

    @Test
    void testGetBlock_DefaultValue() {
        MyBigInt num = new MyBigInt(0);
        assertEquals(0L, num.getBlock(0), "Новый объект должен содержать 0 в блоке");
    }
}