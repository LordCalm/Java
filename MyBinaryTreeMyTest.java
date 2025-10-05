package lr2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MyBinaryTreeMyTest {
    @Test
    void testAddAndContains() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(5);
        tree.add(3);
        tree.add(7);

        assertTrue(tree.containsNode(5));
        assertTrue(tree.containsNode(3));
        assertTrue(tree.containsNode(7));
        assertFalse(tree.containsNode(10));
    }

    @Test
    void testDeleteLeafNode() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(5);
        tree.add(3);
        tree.add(7);

        tree.delete(3);
        assertFalse(tree.containsNode(3));
        assertTrue(tree.containsNode(5));
        assertTrue(tree.containsNode(7));
    }

    @Test
    void testDeleteNodeWithOneChild() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(5);
        tree.add(3);
        tree.add(4); // 3 has one child 4

        tree.delete(3);
        assertFalse(tree.containsNode(3));
        assertTrue(tree.containsNode(4));
        assertTrue(tree.containsNode(5));
    }

    @Test
    void testDeleteNodeWithTwoChildren() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.add(6);
        tree.add(8);

        tree.delete(7);
        assertFalse(tree.containsNode(7));
        assertTrue(tree.containsNode(6));
        assertTrue(tree.containsNode(8));
    }

    @Test
    void testTraverseInOrder() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.add(4);

        List<Integer> result = new ArrayList<>();
        tree.traverseInOrder(result::add);

        // Должен быть отсортированный порядок
        assertEquals(List.of(3, 4, 5, 7), result);
    }

    @Test
    void testGetByIndex() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.add(4);

        assertEquals(3, tree.getByIndex(0));
        assertEquals(4, tree.getByIndex(1));
        assertEquals(5, tree.getByIndex(2));
        assertEquals(7, tree.getByIndex(3));

        // Проверка выхода за пределы
        assertThrows(IndexOutOfBoundsException.class, () -> tree.getByIndex(4));
    }
}