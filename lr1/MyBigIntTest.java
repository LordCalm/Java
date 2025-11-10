package lr1;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyBigIntTest {

    // Import the class under test
    MyBigInt bigInt(int num) {
        return new MyBigInt(num);
    }

    MyBigInt bigInt(long num) {
        return new MyBigInt(num);
    }

    MyBigInt bigInt(String num) {
        return new MyBigInt(num);
    }

    @Test
    void testConstructorInt() {
        assertEquals("0", bigInt(0).toString());
        assertEquals("123", bigInt(123).toString());
        assertEquals("-456", new MyBigInt(-456).toString());
    }

    @Test
    void testConstructorLong() {
        assertEquals("0", bigInt(0L).toString());
        assertEquals("9223372036854775807", bigInt(Long.MAX_VALUE).toString());
        assertEquals("-9223372036854775808", new MyBigInt(Long.MIN_VALUE).toString());
    }

    @Test
    void testConstructorString() {
        assertEquals("0", bigInt("0").toString());
        assertEquals("12345678901234567890", bigInt("12345678901234567890").toString());
        assertEquals("-98765432109876543210", bigInt("-98765432109876543210").toString());
        assertThrows(IllegalArgumentException.class, () -> bigInt("abc"));
    }

    @Test
    void testAddPositive() {
        MyBigInt a = bigInt("12345678901234567890");
        MyBigInt b = bigInt("98765432109876543210");
        MyBigInt sum = MyBigInt.add(a, b);
        assertEquals("111111111011111111100", sum.toString());
    }

    @Test
    void testAddNegative() {
        MyBigInt a = bigInt("-1000");
        MyBigInt b = bigInt("-2000");
        MyBigInt sum = MyBigInt.add(a, b);
        assertEquals("-3000", sum.toString());
    }

    @Test
    void testAddMixedSigns() {
        MyBigInt a = bigInt("5000");
        MyBigInt b = bigInt("-3000");
        MyBigInt sum = MyBigInt.add(a, b);
        assertEquals("2000", sum.toString());
    }

    @Test
    void testSubtractPositive() {
        MyBigInt a = bigInt("10000");
        MyBigInt b = bigInt("9999");
        MyBigInt diff = MyBigInt.subtract(a, b);
        assertEquals("1", diff.toString());
    }

    @Test
    void testSubtractNegativeResult() {
        MyBigInt a = bigInt("123");
        MyBigInt b = bigInt("456");
        MyBigInt diff = MyBigInt.subtract(a, b);
        assertEquals("-333", diff.toString());
    }

    @Test
    void testMultiplyKaratsubaSmall() {
        MyBigInt a = bigInt(7);
        MyBigInt b = bigInt(20);
        MyBigInt prod = MyBigInt.multiplyKaratsuba(a, b);
        assertEquals("140", prod.toString());
    }

    @Test
    void testMultiplyKaratsubaLarge() {
        MyBigInt a = bigInt("10000000000000000000000000");
        MyBigInt b = bigInt("7");
        MyBigInt prod = MyBigInt.multiplyKaratsuba(a, b);
        assertEquals("70000000000000000000000000", prod.toString());
    }

    @Test
    void testMultiplyKaratsubaNegative() {
        MyBigInt a = bigInt("-123456789");
        MyBigInt b = bigInt("987654321");
        MyBigInt prod = MyBigInt.multiplyKaratsuba(a, b);
        assertEquals("-121932631112635269", prod.toString());
    }

    @Test
    void testShiftLeft() {
        MyBigInt a = bigInt("1");
        MyBigInt shifted = MyBigInt.shiftLeft(a, 10);
        assertEquals("1024", shifted.toString());
    }

    @Test
    void testCompareAbs() {
        MyBigInt a = bigInt("1000");
        MyBigInt b = bigInt("999");
        assertTrue(MyBigInt.compareAbs(a, b) > 0);
        assertTrue(MyBigInt.compareAbs(b, a) < 0);
        assertEquals(0, MyBigInt.compareAbs(a, bigInt("1000")));
    }

    @Test
    void testToStringZero() {
        MyBigInt a = bigInt(0);
        assertEquals("0", a.toString());
    }

    @Test
    void testSaveBinaryAndLoadBinary_Positive() throws Exception {
        MyBigInt original = bigInt("123456789012345678901234567890");
        String filename = "test_save_binary_positive.bin";
        original.saveBinary(filename);

        MyBigInt loaded = MyBigInt.loadBinary(filename);
        assertEquals(original.toString(), loaded.toString());

        new java.io.File(filename).delete();
    }

    @Test
    void testSaveBinaryAndLoadBinary_Negative() throws Exception {
        MyBigInt original = bigInt("-98765432109876543210987654321");
        String filename = "test_save_binary_negative.bin";
        original.saveBinary(filename);

        MyBigInt loaded = MyBigInt.loadBinary(filename);
        assertEquals(original.toString(), loaded.toString());

        new java.io.File(filename).delete();
    }

    @Test
    void testSaveBinaryAndLoadBinary_Zero() throws Exception {
        MyBigInt original = bigInt("0");
        String filename = "test_save_binary_zero.bin";
        original.saveBinary(filename);

        MyBigInt loaded = MyBigInt.loadBinary(filename);
        assertEquals(original.toString(), loaded.toString());

        new java.io.File(filename).delete();
    }

    @Test
    void testLoadBinary_ManualWrite_Positive() throws Exception {
        String filename = "test_manual_positive.bin";
        // Write a positive number: sign=true, length=2, value={123456789L, 987654321L}
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename))) {
            out.writeBoolean(true);
            out.writeInt(2);
            out.writeLong(123456789L);
            out.writeLong(987654321L);
        }
        MyBigInt loaded = MyBigInt.loadBinary(filename);
        BigInteger expected = BigInteger.valueOf(987654321)
                .shiftLeft(63)
                .add(BigInteger.valueOf(123456789));
        assertEquals(expected.toString(), loaded.toString()); // 987654321*2^63 + 123456789
        new java.io.File(filename).delete();
    }

    @Test
    void testLoadBinary_ManualWrite_Negative() throws Exception {
        String filename = "test_manual_negative.bin";
        // Write a negative number: sign=false, length=1, value={42L}
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename))) {
            out.writeBoolean(false);
            out.writeInt(1);
            out.writeLong(42L);
        }
        MyBigInt loaded = MyBigInt.loadBinary(filename);
        assertEquals("-42", loaded.toString());
        new java.io.File(filename).delete();
    }

    @Test
    void testLoadBinary_ManualWrite_Zero() throws Exception {
        String filename = "test_manual_zero.bin";
        // Write zero: sign=true, length=1, value={0L}
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename))) {
            out.writeBoolean(true);
            out.writeInt(1);
            out.writeLong(0L);
        }
        MyBigInt loaded = MyBigInt.loadBinary(filename);
        assertEquals("0", loaded.toString());
        new java.io.File(filename).delete();
    }

    @Test
    void testLoadBinary_InvalidFile() {
        String filename = "nonexistent_file.bin";
        assertThrows(IOException.class, () -> MyBigInt.loadBinary(filename));
    }

    @Test
    void testSaveToFile_Positive() throws Exception {
        MyBigInt original = bigInt("12345678901234567890");
        String filename = "test_save_to_file_positive.txt";
        original.saveToFile(filename);

        String content = new String(
                Files.readAllBytes(java.nio.file.Paths.get(filename)),
                java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(original.toString(), content.trim());

        new java.io.File(filename).delete();
    }

    @Test
    void testSaveToFile_Negative() throws Exception {
        MyBigInt original = bigInt("-98765432109876543210");
        String filename = "test_save_to_file_negative.txt";
        original.saveToFile(filename);

        String content = new String(
                Files.readAllBytes(java.nio.file.Paths.get(filename)),
                java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(original.toString(), content.trim());

        new java.io.File(filename).delete();
    }

    @Test
    void testSaveToFile_Zero() throws Exception {
        MyBigInt original = bigInt("0");
        String filename = "test_save_to_file_zero.txt";
        original.saveToFile(filename);

        String content = new String(
                Files.readAllBytes(java.nio.file.Paths.get(filename)),
                java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(original.toString(), content.trim());

        new java.io.File(filename).delete();
    }

    @Test
    void testSaveToFile_InvalidPath() {
        MyBigInt original = bigInt("123");
        String filename = "/invalid_path/test_save_to_file.txt";
        assertThrows(IOException.class, () -> original.saveToFile(filename));
    }

    @Test
    void testLoadFromFile_Positive() throws Exception {
        String filename = "test_load_from_file_positive.txt";
        String value = "12345678901234567890";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(value);
        }
        MyBigInt loaded = MyBigInt.loadFromFile(filename);
        assertEquals(value, loaded.toString());
        new java.io.File(filename).delete();
    }

    @Test
    void testLoadFromFile_Negative() throws Exception {
        String filename = "test_load_from_file_negative.txt";
        String value = "-98765432109876543210";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(value);
        }
        MyBigInt loaded = MyBigInt.loadFromFile(filename);
        assertEquals(value, loaded.toString());
        new java.io.File(filename).delete();
    }

    @Test
    void testLoadFromFile_Zero() throws Exception {
        String filename = "test_load_from_file_zero.txt";
        String value = "0";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(value);
        }
        MyBigInt loaded = MyBigInt.loadFromFile(filename);
        assertEquals("0", loaded.toString());
        new java.io.File(filename).delete();
    }

    @Test
    void testLoadFromFile_InvalidFile() {
        String filename = "nonexistent_file.txt";
        assertThrows(IOException.class, () -> MyBigInt.loadFromFile(filename));
    }

    @Test
    void testLoadFromFile_InvalidContent() throws Exception {
        String filename = "test_load_from_file_invalid.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("not_a_number");
        }
        assertThrows(IllegalArgumentException.class, () -> MyBigInt.loadFromFile(filename));
        new java.io.File(filename).delete();
    }

    @Test
    void testCompareToEqualPositive() {
        MyBigInt a = bigInt("123456789");
        MyBigInt b = bigInt("123456789");
        assertEquals(0, a.compareTo(b));
    }

    @Test
    void testCompareToEqualNegative() {
        MyBigInt a = bigInt("-987654321");
        MyBigInt b = bigInt("-987654321");
        assertEquals(0, a.compareTo(b));
    }

    @Test
    void testCompareToPositiveGreater() {
        MyBigInt a = bigInt("1000");
        MyBigInt b = bigInt("999");
        assertTrue(a.compareTo(b) > 0);
    }

    @Test
    void testCompareToPositiveLess() {
        MyBigInt a = bigInt("123");
        MyBigInt b = bigInt("456");
        assertTrue(a.compareTo(b) < 0);
    }

    @Test
    void testCompareToNegativeGreater() {
        MyBigInt a = bigInt("-123");
        MyBigInt b = bigInt("-456");
        assertTrue(a.compareTo(b) > 0);
    }

    @Test
    void testCompareToNegativeLess() {
        MyBigInt a = bigInt("-789");
        MyBigInt b = bigInt("-123");
        assertTrue(a.compareTo(b) < 0);
    }

    @Test
    void testCompareToPositiveVsNegative() {
        MyBigInt a = bigInt("1");
        MyBigInt b = bigInt("-1");
        assertTrue(a.compareTo(b) > 0);
        assertTrue(b.compareTo(a) < 0);
    }

    @Test
    void testCompareToZero() {
        MyBigInt zero = bigInt("0");
        MyBigInt pos = bigInt("123");
        MyBigInt neg = bigInt("-123");
        assertTrue(zero.compareTo(pos) < 0);
        assertTrue(zero.compareTo(neg) > 0);
        assertEquals(0, zero.compareTo(bigInt("0")));
    }
}