package lr4;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import lr2.MyBinaryTree;
import lr3.UserType;

public class MainWindow extends JFrame {
    // Компоненты GUI
    private JComboBox<String> typeSelector;
    private JTextField inputField;
    private JTextArea outputArea;
    private JButton addButton;
    private JButton deleteButton;
    private JButton traverseButton;
    private JButton balanceButton;
    private JComboBox<String> treeElementsSelector;

    private final UserFactory factory = new UserFactory();
    private UserType selectedUserType = null;
    private MyBinaryTree<UserType> tree = null;

    // Конструктор
    public MainWindow() {
        super("Л.р.4 — Оконное приложение");

        setLayout(new BorderLayout());

        // Инициализация компонентов
        initUserTypeSelectionPanel();
        initControlPanel();
        initOutputPanel();

        // Инициализация данных
        updateTypeSelector();

        // Обновление состояния элементов управления
        updateControlsState();

        // === Окно ===
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Окно по центру
        setVisible(true);
    }

    // --- Инициализация панелей GUI ---

    private void initUserTypeSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Выбор типа данных"));

        typeSelector = new JComboBox<>();
        typeSelector.addActionListener(e -> onTypeSelected());

        panel.add(new JLabel("Тип:"));
        panel.add(typeSelector);

        add(panel, BorderLayout.NORTH);
    }

    private void initControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Операции с деревом"));

        // --- Инициализация элементов ---
        addButton = new JButton("Добавить элемент");
        deleteButton = new JButton("Удалить по значению");
        traverseButton = new JButton("Обход In-Order");
        balanceButton = new JButton("Сбалансировать");
        JButton deleteByIndexButton = new JButton("Удалить по индексу");

        // 1. Находим максимальную ширину среди всех кнопок
        int maxButtonWidth = Math.max(addButton.getPreferredSize().width, deleteButton.getPreferredSize().width);
        maxButtonWidth = Math.max(maxButtonWidth, traverseButton.getPreferredSize().width);
        maxButtonWidth = Math.max(maxButtonWidth, balanceButton.getPreferredSize().width);
        maxButtonWidth = Math.max(maxButtonWidth, deleteByIndexButton.getPreferredSize().width);

        // 2. Устанавливаем единый размер для всех кнопок
        Dimension buttonSize = new Dimension(maxButtonWidth, addButton.getPreferredSize().height);

        JButton[] allButtons = { addButton, deleteButton, traverseButton, balanceButton, deleteByIndexButton };
        for (JButton btn : allButtons) {
            btn.setPreferredSize(buttonSize);
            btn.setMinimumSize(buttonSize);
            btn.setMaximumSize(buttonSize);
        }

        // === Общая ширина для группирующих панелей ===
        // Ширина = 2 * (Ширина кнопки) + 5 (зазор GridLayout)
        int maxGroupWidth = 2 * maxButtonWidth + 5;

        // --- Инициализация остальных элементов (с учетом новой ширины) ---
        inputField = new JTextField(15);
        inputField.setMaximumSize(new Dimension(maxGroupWidth, inputField.getPreferredSize().height));

        treeElementsSelector = new JComboBox<>();
        treeElementsSelector
                .setMaximumSize(new Dimension(maxGroupWidth, treeElementsSelector.getPreferredSize().height));

        // Добавление слушателей
        addButton.addActionListener(e -> onAddElement());
        deleteButton.addActionListener(e -> onDeleteByValue());
        traverseButton.addActionListener(e -> onTraverse());
        balanceButton.addActionListener(e -> onBalance());
        deleteByIndexButton.addActionListener(e -> onDeleteByIndex());
        treeElementsSelector.addActionListener(e -> onElementSelectedForDelete());

        // -------------------------------------------------------
        // === БЛОК 1 — Добавление / удаление по значению ===
        // -------------------------------------------------------
        JPanel valueOpPanel = new JPanel();
        valueOpPanel.setLayout(new BoxLayout(valueOpPanel, BoxLayout.Y_AXIS));
        valueOpPanel.setBorder(BorderFactory.createTitledBorder("Операции по значению"));
        valueOpPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel("Значение:");
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueOpPanel.add(valueLabel);
        valueOpPanel.add(Box.createVerticalStrut(4));

        // Обёртка для поля ввода (фиксируем левое выравнивание)
        JPanel inputWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        inputWrapper.setMaximumSize(new Dimension(maxGroupWidth, inputField.getPreferredSize().height + 5));
        inputWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputWrapper.add(inputField);
        valueOpPanel.add(inputWrapper);

        valueOpPanel.add(Box.createVerticalStrut(5));

        // Кнопки (два столбца)
        JPanel valueButtonsPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        // Принудительно устанавливаем размер панели по рассчитанной ширине
        valueButtonsPanel.setPreferredSize(new Dimension(maxGroupWidth, buttonSize.height));
        valueButtonsPanel.setMaximumSize(new Dimension(maxGroupWidth, buttonSize.height));
        valueButtonsPanel.add(addButton);
        valueButtonsPanel.add(deleteButton);
        valueButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueOpPanel.add(valueButtonsPanel);
        panel.add(valueOpPanel);
        panel.add(Box.createVerticalStrut(10));

        // -------------------------------------------------------
        // === БЛОК 2 — Операции с деревом ===
        // -------------------------------------------------------
        JPanel treeOpPanel = new JPanel();
        treeOpPanel.setLayout(new BoxLayout(treeOpPanel, BoxLayout.Y_AXIS));
        treeOpPanel.setBorder(BorderFactory.createTitledBorder("Действия с деревом"));
        treeOpPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Внутренняя панель с GridLayout для кнопок
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 5, 5));

        // фиксируем ширину в точности как в блоке 1
        buttonRow.setPreferredSize(new Dimension(maxGroupWidth, buttonSize.height));
        buttonRow.setMaximumSize(new Dimension(maxGroupWidth, buttonSize.height));
        buttonRow.setMinimumSize(new Dimension(maxGroupWidth, buttonSize.height));

        buttonRow.add(traverseButton);
        buttonRow.add(balanceButton);

        // добавляем отступы сверху/снизу, чтобы заголовок не давил
        treeOpPanel.add(Box.createVerticalStrut(5));
        treeOpPanel.add(buttonRow);
        treeOpPanel.add(Box.createVerticalStrut(5));

        panel.add(treeOpPanel);
        panel.add(Box.createVerticalStrut(10));

        // -------------------------------------------------------
        // === БЛОК 3 — Удаление по индексу ===
        // -------------------------------------------------------
        JPanel indexOpPanel = new JPanel();
        indexOpPanel.setLayout(new BoxLayout(indexOpPanel, BoxLayout.Y_AXIS));
        indexOpPanel.setBorder(BorderFactory.createTitledBorder("Удаление по индексу"));
        indexOpPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Лейбл
        JLabel indexLabel = new JLabel("Элементы в дереве:");
        indexLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        indexOpPanel.add(indexLabel);

        indexOpPanel.add(Box.createVerticalStrut(4));

        // Обертка для JComboBox — устраняет смещение
        JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboWrapper.setMaximumSize(new Dimension(maxGroupWidth, treeElementsSelector.getPreferredSize().height + 5));
        comboWrapper.add(treeElementsSelector);
        indexOpPanel.add(comboWrapper);

        indexOpPanel.add(Box.createVerticalStrut(6));

        // Кнопка
        deleteByIndexButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        indexOpPanel.add(deleteByIndexButton);

        panel.add(indexOpPanel);

        // Добавляем на форму
        add(panel, BorderLayout.WEST);
    }

    private void initOutputPanel() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Результат операций"));

        add(scrollPane, BorderLayout.CENTER);
    }

    // --- Логика работы с фабрикой и деревом ---

    /** Обновляет выпадающий список типов данных на основе UserFactory. */
    private void updateTypeSelector() {
        ArrayList<String> typeNames = factory.getTypeNameList();
        typeSelector.setModel(new DefaultComboBoxModel<>(typeNames.toArray(new String[0])));

        // Автоматический выбор первого элемента, если список не пуст
        if (!typeNames.isEmpty()) {
            typeSelector.setSelectedIndex(0);
        } else {
            selectedUserType = null;
            tree = null;
            updateControlsState();
        }
    }

    /** Обрабатывает выбор нового типа данных. */
    private void onTypeSelected() {
        String selectedName = (String) typeSelector.getSelectedItem();
        if (selectedName != null) {
            try {
                // Фабрика возвращает объект-прототип
                selectedUserType = factory.getBuilderByName(selectedName);

                // Создание нового дерева с компаратором от прототипа
                // selectedUserType используется как пример типа (UserType exampleType) для
                // MyBinaryTree
                tree = new MyBinaryTree<>(selectedUserType);

                outputArea.setText("Выбран тип: " + selectedName + ". Создано новое бинарное дерево.\n");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                selectedUserType = null;
                tree = null;
                outputArea.setText("Ошибка при выборе типа: " + ex.getMessage() + "\n");
            }
        } else {
            selectedUserType = null;
            tree = null;
        }
        updateControlsState();
    }

    /**
     * Обновляет состояние кнопок и комбобокса, зависит от наличия выбранного типа и
     * данных в дереве.
     */
    private void updateControlsState() {
        boolean typeSelected = selectedUserType != null;

        inputField.setEnabled(typeSelected);
        addButton.setEnabled(typeSelected);
        traverseButton.setEnabled(typeSelected);
        balanceButton.setEnabled(typeSelected);
        deleteButton.setEnabled(typeSelected);

        // Обновление списка элементов для ComboBox
        updateTreeElementsSelector();
    }

    /**
     * Обновляет список элементов в ComboBox, который используется для удаления по
     * индексу.
     */
    private void updateTreeElementsSelector() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if (tree != null) {
            List<UserType> elements = tree.toList(); // Класс СД создает вектор (ArrayList) ссылок на хранимые объекты
            for (int i = 0; i < elements.size(); i++) {
                // Отображаем индекс и значение
                model.addElement(String.format("[%d]: %s", i, elements.get(i).toString()));
            }
        }
        treeElementsSelector.setModel(model);
        treeElementsSelector.setEnabled(model.getSize() > 0);
    }

    /** Обрабатывает нажатие кнопки "Добавить элемент". */
    private void onAddElement() {
        if (selectedUserType == null || tree == null)
            return;

        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите значение для добавления.", "Предупреждение",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Оконный класс получает от объекта-прототипа клон с данными, пропарсеными из
            // строки
            UserType clonedValue = (UserType) selectedUserType.parseValue(input);

            // Добавление в дерево
            tree.add(clonedValue);

            outputArea.append("Добавлен элемент: " + clonedValue.toString() + "\n");
            inputField.setText(""); // Очистка поля ввода
            updateTreeElementsSelector(); // Обновление списка элементов
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка парсинга или добавления: " + ex.getMessage(), "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Обрабатывает нажатие кнопки "Удалить элемент по значению". */
    private void onDeleteByValue() {
        if (selectedUserType == null || tree == null)
            return;

        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите значение для удаления.", "Предупреждение",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Создание объекта для поиска/удаления
            UserType valueToDelete = (UserType) selectedUserType.parseValue(input);

            if (tree.containsNode(valueToDelete)) {
                tree.delete(valueToDelete);
                outputArea.append("Удален элемент: " + valueToDelete.toString() + "\n");
                updateTreeElementsSelector(); // Обновление списка элементов
            } else {
                outputArea.append("Элемент не найден: " + valueToDelete.toString() + "\n");
            }

            inputField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка парсинга или удаления: " + ex.getMessage(), "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Обрабатывает нажатие кнопки "Удалить элемент по индексу". */
    private void onDeleteByIndex() {
        if (tree == null)
            return;

        int selectedIndex = treeElementsSelector.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Нет элементов для удаления.", "Предупреждение",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String elementInfo = (String) treeElementsSelector.getSelectedItem();
            tree.deleteByIndex(selectedIndex);
            outputArea.append("Удален элемент по индексу: " + elementInfo + "\n");
            updateTreeElementsSelector(); // Обновление списка элементов
        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Обрабатывает нажатие кнопки "Обход In-Order (Вывод)". */
    private void onTraverse() {
        if (tree == null)
            return;

        // Использование NodeAction для записи в JTextArea
        StringBuilder sb = new StringBuilder();
        MyBinaryTree.NodeAction<UserType> action = value -> sb.append(value.toString()).append(" ");

        // Выполняем обход
        sb.append("Обход In-Order: ");
        tree.traverseInOrder(action);
        sb.append("\n");

        outputArea.append(sb.toString());
        updateTreeElementsSelector(); // Обновление списка элементов на случай, если вывод нужен для отладки
    }

    /** Обрабатывает нажатие кнопки "Сбалансировать дерево". */
    private void onBalance() {
        if (tree == null)
            return;

        tree.balance();
        outputArea.append("Дерево сбалансировано.\n");
        updateTreeElementsSelector(); // Обновление списка элементов
    }

    /**
     * Обрабатывает выбор элемента в ComboBox, не обязательная
     * логика.
     */
    private void onElementSelectedForDelete() {
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Если Nimbus недоступен, используем стандартный
        }

        SwingUtilities.invokeLater(MainWindow::new);
    }
}