package lr1;

import lr2.*;
import lr3.*;
import lr4.*;

public class Main {
    public static void main(String[] args) {
        // Создаем дерево с типом MyIntType
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());

        // --- ИСПРАВЛЕНИЕ ЗДЕСЬ ---
        // parseValue возвращает новый Object, его нужно привести к типу и сохранить
        MyIntType val1 = (MyIntType) new MyIntType().parseValue("10");
        MyIntType val2 = (MyIntType) new MyIntType().parseValue("5");
        MyIntType val3 = (MyIntType) new MyIntType().parseValue("15");
        MyIntType val4 = (MyIntType) new MyIntType().parseValue("2");
        MyIntType val5 = (MyIntType) new MyIntType().parseValue("7");

        tree.add(val1);
        tree.add(val2);
        tree.add(val3);
        tree.add(val4);
        tree.add(val5);

        System.out.println("--- Дерево создано ---");
        
        // 1. Создаем первый итератор (встает в вектор activeIterators)
        MyBinaryTree<MyIntType>.MyTreeIterator it1 = tree.createIterator();
        
        // 2. Создаем второй итератор
        MyBinaryTree<MyIntType>.MyTreeIterator it2 = tree.createIterator();

        System.out.println("IT1 (First):");
        it1.first();
        // Теперь здесь должно быть 2, а не 0
        System.out.println(it1.currentItem()); 

        System.out.println("IT1 (Next):");
        // Теперь следующий элемент существует (5), ошибки не будет
        System.out.println(it1.next());      

        System.out.println("IT2 (Go by Index 3):");
        // Индексы в отсортированном порядке: 2(0), 5(1), 7(2), 10(3), 15(4)
        it2.goByIndex(3);
        System.out.println(it2.currentItem()); // 10

        System.out.println("--- Модификация через IT1 ---");
        // Вставляем новый элемент через IT1
        MyIntType newVal = (MyIntType) new MyIntType().parseValue("20");
        it1.insert(newVal); 
        System.out.println("IT1 вставил значение 20. IT1 работает дальше.");

        // IT1 обновил свой expectedModCount, поэтому он валиден
        it1.last();
        System.out.println("IT1 Last: " + it1.currentItem()); // 20

        System.out.println("--- Проверка конфликта в IT2 ---");
        try {
            // IT2 не знает, что дерево изменилось (insert через it1).
            // При попытке доступа должна возникнуть ошибка.
            it2.next(); 
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("Успех! Перехвачено исключение: " + e.getMessage());
            System.out.println("Это подтверждает, что изменение через один итератор инвалидировало другие.");
        } catch (java.util.NoSuchElementException e) {
            // На всякий случай ловим, если вдруг итератор улетел в конец, 
            // но ожидаем именно ConcurrentModificationException
            System.out.println("Ошибка навигации: " + e.getMessage());
        }
    }
}