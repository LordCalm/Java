import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class lab_1Test {

    // Import the class under test
    lab_1.MyBigInt bigInt(int num) {
        return new lab_1.MyBigInt(num);
    }

    lab_1.MyBigInt bigInt(long num) {
        return new lab_1.MyBigInt(num);
    }

    lab_1.MyBigInt bigInt(String num) {
        return new lab_1.MyBigInt(num);
    }

    @Test
    void testConstructorInt() {
        assertEquals("0", bigInt(0).toString());
        assertEquals("123", bigInt(123).toString());
        assertEquals("-456", new lab_1.MyBigInt(-456).toString());
    }

    @Test
    void testConstructorLong() {
        assertEquals("0", bigInt(0L).toString());
        assertEquals("9223372036854775807", bigInt(Long.MAX_VALUE).toString());
        assertEquals("-9223372036854775808", new lab_1.MyBigInt(Long.MIN_VALUE).toString());
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
        lab_1.MyBigInt a = bigInt("12345678901234567890");
        lab_1.MyBigInt b = bigInt("98765432109876543210");
        lab_1.MyBigInt sum = lab_1.MyBigInt.add(a, b);
        assertEquals("111111111011111111100", sum.toString());
    }

    @Test
    void testAddNegative() {
        lab_1.MyBigInt a = bigInt("-1000");
        lab_1.MyBigInt b = bigInt("-2000");
        lab_1.MyBigInt sum = lab_1.MyBigInt.add(a, b);
        assertEquals("-3000", sum.toString());
    }

    @Test
    void testAddMixedSigns() {
        lab_1.MyBigInt a = bigInt("5000");
        lab_1.MyBigInt b = bigInt("-3000");
        lab_1.MyBigInt sum = lab_1.MyBigInt.add(a, b);
        assertEquals("2000", sum.toString());
    }

    @Test
    void testSubtractPositive() {
        lab_1.MyBigInt a = bigInt("10000");
        lab_1.MyBigInt b = bigInt("9999");
        lab_1.MyBigInt diff = lab_1.MyBigInt.subtract(a, b);
        assertEquals("1", diff.toString());
    }

    @Test
    void testSubtractNegativeResult() {
        lab_1.MyBigInt a = bigInt("123");
        lab_1.MyBigInt b = bigInt("456");
        lab_1.MyBigInt diff = lab_1.MyBigInt.subtract(a, b);
        assertEquals("-333", diff.toString());
    }

    @Test
    void testMultiplyKaratsubaSmall() {
        lab_1.MyBigInt a = bigInt(7);
        lab_1.MyBigInt b = bigInt(20);
        lab_1.MyBigInt prod = lab_1.MyBigInt.multiplyKaratsuba(a, b);
        assertEquals("140", prod.toString());
    }

    @Test
    void testMultiplyKaratsubaLarge() {
        lab_1.MyBigInt a = bigInt("10000000000000000000000000");
        lab_1.MyBigInt b = bigInt("7");
        lab_1.MyBigInt prod = lab_1.MyBigInt.multiplyKaratsuba(a, b);
        assertEquals("70000000000000000000000000", prod.toString());
    }

    @Test
    void testMultiplyKaratsubaNegative() {
        lab_1.MyBigInt a = bigInt("-123456789");
        lab_1.MyBigInt b = bigInt("987654321");
        lab_1.MyBigInt prod = lab_1.MyBigInt.multiplyKaratsuba(a, b);
        assertEquals("-121932631112635269", prod.toString());
    }

    @Test
    void testShiftLeft() {
        lab_1.MyBigInt a = bigInt("1");
        lab_1.MyBigInt shifted = lab_1.MyBigInt.shiftLeft(a, 10);
        assertEquals("1024", shifted.toString());
    }

    @Test
    void testCompareAbs() {
        lab_1.MyBigInt a = bigInt("1000");
        lab_1.MyBigInt b = bigInt("999");
        assertTrue(lab_1.MyBigInt.compareAbs(a, b) > 0);
        assertTrue(lab_1.MyBigInt.compareAbs(b, a) < 0);
        assertEquals(0, lab_1.MyBigInt.compareAbs(a, bigInt("1000")));
    }

    @Test
    void testToStringZero() {
        lab_1.MyBigInt a = bigInt(0);
        assertEquals("0", a.toString());
    }

    @Test
    void testSaveBinaryAndLoadBinary_Positive() throws Exception {
        lab_1.MyBigInt original = bigInt("123456789012345678901234567890");
        String filename = "test_save_binary_positive.bin";
        original.saveBinary(filename);

        lab_1.MyBigInt loaded = lab_1.MyBigInt.loadBinary(filename);
        assertEquals(original.toString(), loaded.toString());

        new java.io.File(filename).delete();
    }

    @Test
    void testSaveBinaryAndLoadBinary_Negative() throws Exception {
        lab_1.MyBigInt original = bigInt("-98765432109876543210987654321");
        String filename = "test_save_binary_negative.bin";
        original.saveBinary(filename);

        lab_1.MyBigInt loaded = lab_1.MyBigInt.loadBinary(filename);
        assertEquals(original.toString(), loaded.toString());

        new java.io.File(filename).delete();
    }

    @Test
    void testSaveBinaryAndLoadBinary_Zero() throws Exception {
        lab_1.MyBigInt original = bigInt("0");
        String filename = "test_save_binary_zero.bin";
        original.saveBinary(filename);

        lab_1.MyBigInt loaded = lab_1.MyBigInt.loadBinary(filename);
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
        lab_1.MyBigInt loaded = lab_1.MyBigInt.loadBinary(filename);
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
        lab_1.MyBigInt loaded = lab_1.MyBigInt.loadBinary(filename);
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
        lab_1.MyBigInt loaded = lab_1.MyBigInt.loadBinary(filename);
        assertEquals("0", loaded.toString());
        new java.io.File(filename).delete();
    }

    @Test
    void testLoadBinary_InvalidFile() {
        String filename = "nonexistent_file.bin";
        assertThrows(IOException.class, () -> lab_1.MyBigInt.loadBinary(filename));
    }

    @Test
    void testSaveToFile_Positive() throws Exception {
        lab_1.MyBigInt original = bigInt("12345678901234567890");
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
        lab_1.MyBigInt original = bigInt("-98765432109876543210");
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
        lab_1.MyBigInt original = bigInt("0");
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
        lab_1.MyBigInt original = bigInt("123");
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
        lab_1.MyBigInt loaded = lab_1.MyBigInt.loadFromFile(filename);
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
        lab_1.MyBigInt loaded = lab_1.MyBigInt.loadFromFile(filename);
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
        lab_1.MyBigInt loaded = lab_1.MyBigInt.loadFromFile(filename);
        assertEquals("0", loaded.toString());
        new java.io.File(filename).delete();
    }

    @Test
    void testLoadFromFile_InvalidFile() {
        String filename = "nonexistent_file.txt";
        assertThrows(IOException.class, () -> lab_1.MyBigInt.loadFromFile(filename));
    }

    @Test
    void testLoadFromFile_InvalidContent() throws Exception {
        String filename = "test_load_from_file_invalid.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("not_a_number");
        }
        assertThrows(IllegalArgumentException.class, () -> lab_1.MyBigInt.loadFromFile(filename));
        new java.io.File(filename).delete();
    }
}