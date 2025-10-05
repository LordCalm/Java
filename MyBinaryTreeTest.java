package lr2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class MyBinaryTreeTest {
    @Test
    void testAddAndContainsNode() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);

        assertTrue(tree.containsNode(10));
        assertTrue(tree.containsNode(5));
        assertTrue(tree.containsNode(15));
        assertFalse(tree.containsNode(20));
    }

    @Test
    void testDeleteLeafNode() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);

        tree.delete(5);
        assertFalse(tree.containsNode(5));
        assertTrue(tree.containsNode(10));
        assertTrue(tree.containsNode(15));
    }

    @Test
    void testDeleteNodeWithOneChild() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(2);

        tree.delete(5);
        assertFalse(tree.containsNode(5));
        assertTrue(tree.containsNode(2));
        assertTrue(tree.containsNode(10));
    }

    @Test
    void testDeleteNodeWithTwoChildren() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(12);
        tree.add(18);

        tree.delete(15);
        assertFalse(tree.containsNode(15));
        assertTrue(tree.containsNode(12));
        assertTrue(tree.containsNode(18));
    }

    @Test
    void testTraverseInOrderWithAction() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);

        StringBuilder sb = new StringBuilder();
        tree.traverseInOrder(value -> sb.append(value).append(" "));
        assertEquals("5 10 15 ", sb.toString());
    }

    @Test
    void testGetByIndex() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);

        assertEquals(5, tree.getByIndex(0));
        assertEquals(10, tree.getByIndex(1));
        assertEquals(15, tree.getByIndex(2));
    }

    @Test
    void testGetByIndexThrowsException() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);

        assertThrows(IndexOutOfBoundsException.class, () -> tree.getByIndex(1));
    }

    @Test
    void testDeleteByIndexRemovesCorrectElement() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);

        // In-order: 5, 10, 15
        tree.deleteByIndex(1); // Should delete 10
        assertFalse(tree.containsNode(10));
        assertTrue(tree.containsNode(5));
        assertTrue(tree.containsNode(15));
    }

    @Test
    void testDeleteByIndexFirstElement() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);

        // In-order: 5, 10, 15
        tree.deleteByIndex(0); // Should delete 5
        assertFalse(tree.containsNode(5));
        assertTrue(tree.containsNode(10));
        assertTrue(tree.containsNode(15));
    }

    @Test
    void testDeleteByIndexLastElement() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);

        // In-order: 5, 10, 15
        tree.deleteByIndex(2); // Should delete 15
        assertFalse(tree.containsNode(15));
        assertTrue(tree.containsNode(5));
        assertTrue(tree.containsNode(10));
    }

    @Test
    void testDeleteByIndexThrowsExceptionForInvalidIndex() {
        MyBinaryTree<Integer> tree = new MyBinaryTree<>();
        tree.add(10);
        tree.add(5);

        assertThrows(IndexOutOfBoundsException.class, () -> tree.deleteByIndex(2));
        assertThrows(IndexOutOfBoundsException.class, () -> tree.deleteByIndex(-1));
    }
}