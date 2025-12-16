package lr2;

import lr3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class MyBinaryTreeIteratorTest {
    private MyBinaryTree<MyIntType> tree;

    @BeforeEach
    void setUp() {
        tree = new MyBinaryTree<>(new MyIntType(0));
        tree.add(new MyIntType(5));
        tree.add(new MyIntType(2));
        tree.add(new MyIntType(8));
        tree.add(new MyIntType(1));
        tree.add(new MyIntType(3));
    }

    // -------------------------------------------------
    // Навигация
    // -------------------------------------------------

    @Test
    void testInOrderTraversalUsingNext() {
        var it = tree.createIterator();

        assertEquals(new MyIntType(1), it.next());
        assertEquals(new MyIntType(2), it.next());
        assertEquals(new MyIntType(3), it.next());
        assertEquals(new MyIntType(5), it.next());
        assertEquals(new MyIntType(8), it.next());

        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testPreviousFromEnd() {
        var it = tree.createIterator();

        assertEquals(new MyIntType(8), it.previous());
        assertEquals(new MyIntType(5), it.previous());
        assertEquals(new MyIntType(3), it.previous());
        assertEquals(new MyIntType(2), it.previous());
        assertEquals(new MyIntType(1), it.previous());

        assertThrows(NoSuchElementException.class, it::previous);
    }

    @Test
    void testGoByIndex() {
        var it = tree.createIterator();

        it.goByIndex(2);
        assertEquals(new MyIntType(3), it.currentItem());
    }

    // -------------------------------------------------
    // Модификация через итератор
    // -------------------------------------------------

    @Test
    void testInsertViaIterator() {
        var it = tree.createIterator();

        it.insert(new MyIntType(4));

        assertTrue(tree.containsNode(new MyIntType(4)));
    }

    @Test
    void testRemoveViaIterator() {
        var it = tree.createIterator();

        it.first(); // 1
        it.remove();

        assertFalse(tree.containsNode(new MyIntType(1)));
    }

    @Test
    void testReplaceViaIterator() {
        var it = tree.createIterator();

        it.goByIndex(1); // 2
        it.replace(new MyIntType(6));

        assertFalse(tree.containsNode(new MyIntType(2)));
        assertTrue(tree.containsNode(new MyIntType(6)));
    }

    // -------------------------------------------------
    // Инвалидирование итераторов
    // -------------------------------------------------

    @Test
    void testOtherIteratorInvalidatedOnInsert() {
        var it1 = tree.createIterator();
        var it2 = tree.createIterator();

        it1.insert(new MyIntType(10));

        assertThrows(ConcurrentModificationException.class, it2::next);
    }

    @Test
    void testOtherIteratorInvalidatedOnRemove() {
        var it1 = tree.createIterator();
        var it2 = tree.createIterator();

        it1.first();
        it1.remove();

        assertThrows(ConcurrentModificationException.class, it2::hasNext);
    }

    @Test
    void testOtherIteratorInvalidatedOnReplace() {
        var it1 = tree.createIterator();
        var it2 = tree.createIterator();

        it1.first();
        it1.replace(new MyIntType(100));

        assertThrows(ConcurrentModificationException.class, it2::previous);
    }

    // -------------------------------------------------
    // Итератор, выполнивший модификацию, остаётся валидным
    // -------------------------------------------------

    @Test
    void testIteratorThatModifiedTreeRemainsValid() {
        var it = tree.createIterator();

        it.first();
        it.insert(new MyIntType(7));

        assertDoesNotThrow(it::next);
    }

    // -------------------------------------------------
    // Ошибочные ситуации
    // -------------------------------------------------

    @Test
    void testCurrentItemWithoutPosition() {
        var it = tree.createIterator();

        assertThrows(IllegalStateException.class, it::currentItem);
    }

    @Test
    void testRemoveWithoutCurrent() {
        var it = tree.createIterator();

        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void testReplaceWithoutCurrent() {
        var it = tree.createIterator();

        assertThrows(IllegalStateException.class,
                () -> it.replace(new MyIntType(42)));
    }
}
