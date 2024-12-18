import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
public class SecretSanta {
    private final JFrame frame;
    private final DefaultListModel<String> nameListModel;
    private final JFileChooser folderChooser;
    private final JLabel folderLabel;
    private File saveDirectory;

    public SecretSanta() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        frame = new JFrame("Secret Santa Organizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(mainPanel);

        JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));
        JPanel folderPanel = new JPanel(new BorderLayout(5, 5));
        JButton selectFolderButton = new JButton("Choose Folder");
        nameListModel = new DefaultListModel<>();
        JList<String> nameList = new JList<>(nameListModel);
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JButton addButton = new JButton("Add");

        JTextField nameField = new JTextField();
        JButton saveButton = new JButton("Save");
        addButton.addActionListener(e -> saveNameToList(nameField));
        nameField.addActionListener(e -> saveNameToList(nameField));

        inputPanel.add(nameField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        inputPanel.setBackground(Color.RED);

        nameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScrollPane = new JScrollPane(nameList);

        folderLabel = new JLabel("No folder selected");
        folderLabel.setForeground(Color.WHITE);

        folderPanel.add(selectFolderButton, BorderLayout.WEST);
        folderPanel.add(folderLabel, BorderLayout.CENTER);
        folderPanel.setBackground(new Color(48, 110, 0));

        buttonPanel.add(folderPanel, BorderLayout.CENTER);
        buttonPanel.add(saveButton, BorderLayout.EAST);
        buttonPanel.setBackground(new Color(48, 110, 0));

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.RED);

        folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        nameList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = nameList.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        nameListModel.remove(index);
                    }
                }
            }
        });

        selectFolderButton.addActionListener(e -> {
            int returnValue = folderChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                saveDirectory = folderChooser.getSelectedFile();
                folderLabel.setText("Selected folder: " + saveDirectory.getAbsolutePath());
            } else {
                saveDirectory = folderChooser.getCurrentDirectory();
                folderLabel.setText("Selected folder: " + saveDirectory.getAbsolutePath());
            }
        });

        saveButton.addActionListener(e -> {
            if (saveDirectory == null) {
                JOptionPane.showMessageDialog(frame, "Please choose a folder first.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nameListModel.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "The name list is empty.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ArrayList<String> names = new ArrayList<>();
            for (int i = 0; i < nameListModel.size(); i++) {
                names.add(nameListModel.getElementAt(i));
            }

            Collections.shuffle(names);

            try {
                for (int i = 0; i < names.size(); i++) {
                    String giver = names.get(i);
                    String recipient = names.get((i + 1) % names.size());
                    File file = new File(saveDirectory, giver + ".txt");

                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write("Your Secret Santa recipient is: " + recipient);
                    }
                }

                JOptionPane.showMessageDialog(frame, "Files saved successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error saving files: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void saveNameToList(JTextField nameField){
        String name = nameField.getText().trim();
        if (!name.isEmpty() && !nameListModel.contains(name)) {
            nameListModel.addElement(name);
            nameField.setText("");
        }
    }
}
