package lr2;
import lr3.*;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;

public class MyBinaryTree<T extends UserType> implements Iterable<T> {
    // https://www.baeldung.com/java-binary-tree

    public class MyNode {
        private T value;
        MyNode left;
        MyNode right;
        public MyNode parent;

        MyNode(T num, MyNode parent) {
            this.value = num;
            this.parent = parent;
            left = right = null;
        }
    }

    private MyNode root;
    private Comparator cmp;

    // Вектор созданных итераторов
    private List<MyTreeIterator> activeIterators = new ArrayList<>();

    // Счетчик изменений структуры для контроля валидности итераторов
    private int modCount = 0;



    public interface NodeAction<T> {
        void toDo(T value);
    }

    // Конструктор
    public MyBinaryTree(UserType exampleType) {
        this.cmp = exampleType.getTypeComparator();
    }

    // Вставка
    private void addRecursive(MyNode current, T num) {
        if (cmp.compare(num, current.value) < 0) {
            if (current.left == null) {
                current.left = new MyNode(num, current);
            } else {
                addRecursive(current.left, num);
            }
        } else if (cmp.compare(num, current.value) > 0) {
            if (current.right == null) {
                current.right = new MyNode(num, current);
            } else {
                addRecursive(current.right, num);
            }
        }
    }

    public void add(T num) {
        if (root == null) {
            root = new MyNode(num, null);
        } else {
            addRecursive(root, num);
        }
        modCount++;
    }

    // Поиск элемента
    private boolean containsNodeRecursive(MyNode current, T num) {
        if (current == null) {
            return false;
        }
        if (cmp.compare(num, current.value) == 0) {
            return true;
        }
        return (cmp.compare(num, current.value) < 0)
                ? containsNodeRecursive(current.left, num)
                : containsNodeRecursive(current.right, num);
    }

    public boolean containsNode(T num) {
        return containsNodeRecursive(root, num);
    }

    // Удаление элемента
    private MyNode deleteRecursive(MyNode current, T num) {
        if (current == null) {
            return null;
        }
        if (cmp.compare(num, current.value) == 0) {
            // 1. Нет детей
            if (current.left == null && current.right == null) {
                return null;
            }
            // 2. Один ребенок
            if (current.right == null) {
                current.left.parent = current.parent; // Обновляем родителя
                return current.left;
            }
            if (current.left == null) {
                current.right.parent = current.parent; // Обновляем родителя
                return current.right;
            }
            // 3. Два ребенка
            T smallestValue = findSmallestValue(current.right);
            current.value = smallestValue;
            current.right = deleteRecursive(current.right, smallestValue);
            return current;
        }
        if (cmp.compare(num, current.value) < 0) {
            current.left = deleteRecursive(current.left, num);
        } else {
            current.right = deleteRecursive(current.right, num);
        }
        return current;
    }

    private T findSmallestValue(MyNode root) {
        return root.left == null ? root.value : findSmallestValue(root.left);
    }

    public void delete(T num) {
        if (num == null) return;
        root = deleteRecursive(root, num);
        modCount++;
    }

    // Удаление по индексу
    public void deleteByIndex(int index) {
        T bigNum = getByIndex(root, new int[] { index });
        if (bigNum == null) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        delete(bigNum);
    }

    // Поиск в глубину
    public void traverseInOrder() {
        traverseInOrder(root);
    }

    public void traverseInOrder(NodeAction<T> action) {
        traverseInOrder(root, action);
    }

    public void traverseInOrder(MyNode node) {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.print(" " + node.value);
            traverseInOrder(node.right);
        }
    }

    public void traverseInOrder(MyNode node, NodeAction<T> action) {
        if (node != null) {
            traverseInOrder(node.left, action);
            action.toDo(node.value);
            traverseInOrder(node.right, action);
        }
    }

    // Поиск в ширину
    public void traverseLevelOrder() {
        if (root == null) {
            return;
        }
        Queue<MyNode> nodes = new LinkedList<>();
        nodes.add(root);
        while (!nodes.isEmpty()) {
            MyNode node = nodes.remove();
            System.out.print(" " + node.value);
            if (node.left != null) {
                nodes.add(node.left);
            }
            if (node.right != null) {
                nodes.add(node.right);
            }
        }
    }

    // Доступ по индексу
    public T getByIndex(int index) {
        T bigNum = getByIndex(root, new int[] { index });
        if (bigNum == null) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        return bigNum;
    }

    // Массив в Java — это объект, и ссылка на него передаётся по значению
    private T getByIndex(MyNode node, int[] index) {
        if (node == null) {
            return null;
        }
        T left = getByIndex(node.left, index);
        if (left != null) {
            return left;
        }
        if (index[0] == 0) {
            return node.value;
        }
        index[0]--;
        return getByIndex(node.right, index);
    }

    // --- Реализация расширенного Итератора ---
    @Override
    public java.util.Iterator<T> iterator() {
        return createIterator();
    }

    // Метод создания итератора, который регистрирует его в списке
    public MyTreeIterator createIterator() {
        MyTreeIterator it = new MyTreeIterator();
        activeIterators.add(it); // Регистрация в векторе
        return it;
    }

    public class MyTreeIterator implements java.util.Iterator<T> {
        private MyNode current;
        private boolean valid = true;

        public MyTreeIterator() {
            this.current = null; // Изначально не установлен
        }

        private void invalidate() {
            valid = false;
        }

        // Проверка на валидность (ConcurrentModification)
        private void checkValidity() {
            if (!valid) {
                throw new ConcurrentModificationException("Итератор невалиден: коллекция была изменена другим итератором.");
            }
        }

        private void invalidateOtherIterators(MyTreeIterator current) {
            for (MyTreeIterator it : activeIterators) {
                if (it != current) {
                    it.invalidate();
                }
            }
        }

        // --- Навигация ---

        // Установить на первый элемент (самый левый)
        public void first() {
            checkValidity();
            if (root == null) {
                current = null;
                return;
            }
            MyNode node = root;
            while (node.left != null) {
                node = node.left;
            }
            current = node;
        }

        // Установить на последний элемент (самый правый)
        public void last() {
            checkValidity();
            if (root == null) {
                current = null;
                return;
            }
            MyNode node = root;
            while (node.right != null) {
                node = node.right;
            }
            current = node;
        }

        // Перейти к следующему (in-order successor)
        @Override
        public boolean hasNext() {
            checkValidity();
            // Упрощенная проверка, есть ли следующий элемент в дереве относительно current
            if (root == null) return false;
            if (current == null) return true; // Если итератор в начале, next доступен
            
            // Логика поиска следующего без изменения состояния
            MyNode temp = current;
            if (temp.right != null) {
                return true;
            }
            while (temp.parent != null && temp == temp.parent.right) {
                temp = temp.parent;
            }
            return temp.parent != null;
        }

        @Override
        public T next() {
            checkValidity();
            if (current == null) {
                first(); // Если не инициализирован, идем в начало
            } else {
                // Алгоритм поиска следующего узла (Successor)
                if (current.right != null) {
                    current = current.right;
                    while (current.left != null) {
                        current = current.left;
                    }
                } else {
                    while (current.parent != null && current == current.parent.right) {
                        current = current.parent;
                    }
                    current = current.parent;
                }
            }
            
            if (current == null) throw new NoSuchElementException();
            return current.value;
        }

        // Перейти к предыдущему (in-order predecessor)
        public T previous() {
            checkValidity();
            if (current == null) {
                last(); // Если не инициализирован, идем в конец (или начало, зависит от логики, обычно last для reverse traversal)
            } else {
                // Алгоритм поиска предыдущего узла
                if (current.left != null) {
                    current = current.left;
                    while (current.right != null) {
                        current = current.right;
                    }
                } else {
                    while (current.parent != null && current == current.parent.left) {
                        current = current.parent;
                    }
                    current = current.parent;
                }
            }
            
            if (current == null) throw new NoSuchElementException();
            return current.value;
        }

        // Перейти по логическому индексу (0 - первый элемент при обходе)
        public void goByIndex(int index) {
            checkValidity();
            first();
            for (int i = 0; i < index; i++) {
                if (current == null) throw new IndexOutOfBoundsException();
                next();
            }
        }

        // Получить текущее значение
        public T currentItem() {
            checkValidity();
            if (current == null) throw new IllegalStateException("Итератор не установлен в позицию");
            return current.value;
        }

        // --- Модификация ---

        // Замещение значения
        // В бинарном дереве нельзя просто заменить значение, так как нарушится сортировка.
        // Поэтому мы удаляем старое и добавляем новое. Итератор ставим на новое.
        public void replace(T newValue) {
            checkValidity();
            if (current == null) throw new IllegalStateException();
            
            T oldValue = current.value;
            delete(oldValue); // Удаляем текущее
            add(newValue); // Добавляем новое
            
            // Инвалидируем остальные итераторы
            invalidateOtherIterators(this);
            
            // Перепозиционируемся на новый элемент
            MyNode node = root;
            while (node != null) {
                int cmpResult = cmp.compare(newValue, node.value);
                if (cmpResult == 0) {
                    current = node;
                    break;
                } else if (cmpResult < 0) {
                    node = node.left;
                } else {
                    node = node.right;
                }
            }

            this.valid = true;
        }

        // Вставка (вставка в BST определяется значением, а не позицией итератора)
        // Мы добавляем значение в дерево и обновляем итератор.
        public void insert(T newValue) {
            checkValidity();
            add(newValue);

            // Инвалидируем остальные итераторы
            invalidateOtherIterators(this);

            this.valid = true;
        }

        // Удаление текущего элемента через итератор
        @Override
        public void remove() {
            checkValidity();
            if (current == null) throw new IllegalStateException();
            
            T valueToRemove = current.value;
            // Удаляем элемент
            MyBinaryTree.this.delete(valueToRemove);

            // Инвалидируем ВСЕ остальные итераторы
            invalidateOtherIterators(this);

            // Этот итератор остаётся валидным
            this.valid = true;

            current = null;
        }
    }

    // Балансировка
    // https://www.baeldung.com/cs/balanced-bst-from-sorted-list
    public void balance() {
        List<T> sorted = new ArrayList<>();
        traverseInOrder(sorted::add); // используем forEach для добавления в список
        root = buildBalancedTree(sorted, 0, sorted.size() - 1);
    }

    private MyNode buildBalancedTree(List<T> sorted, int start, int end) {
        if (start > end) return null;
        int mid = (start + end) / 2;
        MyNode node = new MyNode(sorted.get(mid), null);
        node.left = buildBalancedTree(sorted, start, mid - 1);
        node.right = buildBalancedTree(sorted, mid + 1, end);
        return node;
    }

    // В список
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        traverseInOrder(result::add);
        return result;
    }
}