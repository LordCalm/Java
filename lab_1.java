import java.io.*;

public class lab_1 {
    public static class MyBigInt {
        private long[] value; // каждый блок хранит 63 бита
        private boolean sign = true; // true +, false -

        private static final long BLOCK_MASK = 0x7FFFFFFFFFFFFFFFL; // 63 бита
        private static final int BLOCK_SIZE = 63;

        // Конструкторы
        MyBigInt(int num) {
            if (num == 0) {
                this.value = new long[] { 0 };
            } else {
                this.value = new long[] { Math.abs((long) num) & BLOCK_MASK }; // нужно сделать беззнаковый тип
                this.sign = (num > 0);
            }
        }

        MyBigInt(long num) {
            if (num == 0) {
                this.value = new long[] { 0 };
            } else {
                this.value = new long[] { Math.abs(num) & BLOCK_MASK };
                this.sign = (num > 0);
            }
        }

        MyBigInt(long[] num, boolean sign) {
            int n = num.length;
            this.value = new long[n];
            for (int i = 0; i < n; i++) {
                this.value[i] = num[i] & BLOCK_MASK; // гарантируем 63 бита
            }
            this.sign = sign;

            // Дополнительно: убрать ведущие нули
            int lastNonZero = n;
            while (lastNonZero > 1 && this.value[lastNonZero - 1] == 0) {
                lastNonZero--;
            }
            if (lastNonZero < n) {
                long[] trimmed = new long[lastNonZero];
                System.arraycopy(this.value, 0, trimmed, 0, lastNonZero);
                this.value = trimmed;
            }
        }

        MyBigInt(String str) {
            if (str.startsWith("-")) {
                this.sign = false;
                str = str.substring(1);
            } else {
                this.sign = true;
            }
            if (!str.matches("\\d+")) {
                throw new IllegalArgumentException("Not a decimal number: " + str);
            }

            // Decimal to Binary Conversion Program
            // Given a non negative number n, the task is to convert
            // the given number into an equivalent binary representation.
            // https://www.geeksforgeeks.org/dsa/program-decimal-binary-conversion/?hl=ru-RU

            long currentStr = 0;
            int bitIndex = 0; // счётчик битов внутри числа
            int strCount = (int) ((str.length() * Math.log(10) / Math.log(2)) / BLOCK_SIZE) + 1;
            long[] temp = new long[strCount];
            int Pointer = 0; // счётчик блоков для длинного числа

            while (!str.equals("0")) {
                // Делим строку на 2
                StringBuilder sb = new StringBuilder();
                int prevMod = 0; // остаток от деления предыдущей цифры
                for (int i = 0; i < str.length(); i++) {
                    int digit = str.charAt(i) - '0'; // текущая цифра
                    int cur = prevMod * 10 + digit; // для деления столбиком надо учитывать прошлый остаток
                    sb.append(cur / 2); // постепенно набирается результат деления
                    prevMod = cur % 2; // получаем новый остаток
                }
                // Убираем нули в начале числа
                str = sb.toString().replaceFirst("^0+", "");
                if (str.isEmpty())
                    str = "0";

                // Кладём остаток в текущий блок
                currentStr |= ((long) prevMod << bitIndex); // остаток от деления в конец числа
                bitIndex++;

                if (bitIndex == BLOCK_SIZE) {
                    temp[Pointer++] = currentStr & BLOCK_MASK;
                    currentStr = 0;
                    bitIndex = 0;
                }
            }

            // Сохраняем последний неполный блок (полные блоки сохранены в цикле)
            if (bitIndex > 0) {
                temp[Pointer++] = currentStr & BLOCK_MASK;
            }

            // Копируем в массив нужного размера
            this.value = new long[Pointer];
            System.arraycopy(temp, 0, value, 0, Pointer);
        }

        // Сравнение по модулю
        // the value 0 if x == y;
        // a value less than 0 if x < y as unsigned values;
        // and a value greater than 0 if x > y as unsigned values
        public static int compareAbs(MyBigInt a, MyBigInt b) {
            int aLenth = a.value.length;
            int bLenth = b.value.length;
            if (aLenth < bLenth) {
                return -1;
            } else if (aLenth > bLenth) {
                return 1;
            } else {
                for (int i = aLenth - 1; i >= 0; i--) {
                    int j = bLenth - (aLenth - i);
                    long bi = (j >= 0) ? b.value[j] : 0;
                    int cmp = Long.compareUnsigned(a.value[i], bi);
                    if (cmp != 0)
                        return cmp;
                }
                return 0;
            }
        }

        // Сложение
        public static MyBigInt add(MyBigInt a, MyBigInt b) {
            if (a.sign == b.sign) {
                int maxLenth = Math.max(a.value.length, b.value.length);
                long[] result = new long[maxLenth + 1]; // +1 на случай переноса
                long carry = 0;

                for (int i = 0; i < maxLenth; i++) {
                    long x = (i < a.value.length) ? a.value[i] : 0;
                    long y = (i < b.value.length) ? b.value[i] : 0;

                    long sum = x + y + carry;
                    // перенос — старший (64-й) бит относительно базы 2^63
                    carry = sum >>> BLOCK_SIZE; // 0 или 1
                    result[i] = sum & BLOCK_MASK; // строго 63 бита
                }

                if (carry != 0) {
                    result[maxLenth] = 1; // переполнение уходит в новый блок
                    return new MyBigInt(result, a.sign);
                } else {
                    // убираем последний 0-блок
                    long[] trimmed = new long[maxLenth];
                    System.arraycopy(result, 0, trimmed, 0, maxLenth);
                    return new MyBigInt(trimmed, a.sign);
                }
            } else {
                // a + (-b) == a - b
                if (compareAbs(a, b) >= 0) {
                    MyBigInt r = subtractAbs(a, b);
                    r.sign = a.sign;
                    return r;
                } else {
                    MyBigInt r = subtractAbs(b, a);
                    r.sign = b.sign; // знак большего по модулю
                    return r;
                }
            }
        }

        // Вычитание по модулю a > b
        public static MyBigInt subtractAbs(MyBigInt a, MyBigInt b) {
            int n = a.value.length;
            long[] result = new long[n];
            long borrow = 0; // перенос в следующий блок

            for (int i = 0; i < n; i++) {
                long x = a.value[i];
                long y = (i < b.value.length) ? b.value[i] : 0;

                long sub = x - y - borrow;

                // Проверка заимствования (borrow)
                if (Long.compareUnsigned(x, y + borrow) < 0) {
                    // заняли 1 "единицу" из следующего блока (база 2^63)
                    sub += (1L << BLOCK_SIZE);
                    borrow = 1;
                } else {
                    borrow = 0;
                }
                result[i] = sub & BLOCK_MASK;
            }

            // Убираем старшие нулевые блоки
            int lastNonZero = result.length;
            while (lastNonZero > 1 && result[lastNonZero - 1] == 0) {
                lastNonZero--;
            }

            long[] trimmed = new long[lastNonZero];
            System.arraycopy(result, 0, trimmed, 0, lastNonZero);

            return new MyBigInt(trimmed, a.sign);
        }

        // Вычитание
        public static MyBigInt subtract(MyBigInt a, MyBigInt b) {
            if (a.sign == b.sign) {
                // Определяем, кто больше по модулю
                int cmp = compareAbs(a, b);
                if (cmp >= 0) {
                    MyBigInt result = subtractAbs(a, b); // |a| >= |b|
                    result.sign = a.sign;
                    return result;
                } else {
                    MyBigInt result = subtractAbs(b, a); // |b| > |a|
                    result.sign = false;
                    return result;
                }
            } else {
                MyBigInt result = MyBigInt.add(a, b);
                result.sign = a.sign;
                return result;
            }
        }

        // Вывод в строку в дестятичный формат
        @Override
        public String toString() {
            // https://www.geeksforgeeks.org/dsa/program-binary-decimal-conversion/
            // если число = 0
            if (value.length == 1 && value[0] == 0) {
                return "0";
            }

            // создаём копию, чтобы не портить исходный массив
            long[] temp = value.clone();

            StringBuilder sb = new StringBuilder();

            // делим большое число на 10 и собираем цифры
            while (!isZero(temp)) {
                long remainder = divBy10(temp);
                sb.append(remainder);
            }

            if (!sign) {
                sb.append("-");
            }

            return sb.reverse().toString();
        }

        // Проверка, что массив = 0
        private boolean isZero(long[] arr) {
            for (long v : arr) {
                if (v != 0)
                    return false;
            }
            return true;
        }

        // Деление на 10 для массива блоков по 63 бита
        private int divBy10(long[] arr) {
            final long BASE_DIV10 = 922337203685477580L; // floor(2^63 / 10)
            final long BASE_MOD10 = 8L;                  // 2^63 % 10

            long carry = 0; // остаток (0..9)
            for (int i = arr.length - 1; i >= 0; i--) {
                long x = arr[i] & BLOCK_MASK;

                // t = carry*8 + x  (может выглядеть отрицательным, но в unsigned это < 2^63 + 72)
                long t = x + carry * BASE_MOD10;

                long qLow = Long.divideUnsigned(t, 10);      // floor(t / 10) как unsigned
                long r = Long.remainderUnsigned(t, 10);      // t % 10 как unsigned

                long q = carry * BASE_DIV10 + qLow;          // итоговый частный для блока
                arr[i] = q & BLOCK_MASK;                     // строго 63 бита
                carry = r;                                   // остаток идёт на следующий (младший) блок
            }
            return (int) carry; // 0..9
        }
    }

    // Тест
    public static void main(String[] args) throws IOException {
        MyBigInt v1 = new MyBigInt(7);
        MyBigInt v2 = new MyBigInt("1000000000000000000000000");
        MyBigInt v3 = new MyBigInt(20);
        MyBigInt result = MyBigInt.add(v1, v2);
        System.out.println(v2.toString());
        System.out.println(Long.toBinaryString(v2.value[0]));
        System.out.println(Long.toBinaryString(v2.value[1]));
    }
}
