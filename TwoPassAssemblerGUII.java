import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TwoPassAssemblerGUII {

    private static Map<String, Integer> symbolTable = new HashMap<>();
    private static Map<String, String> opcodeTable = new HashMap<>();
    private static int startingAddress = 0;
    private static int programLength = 0;

    private static JTextArea intermediateArea;
    private static JTextArea symtabArea;
    private static JTextArea lengthArea;
    private static JTextArea outputArea;
    private static JTextArea objectCodeArea;
    private static File inputFile;
    private static File optabFile;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Two-Pass Assembler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.decode("#222831"));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.decode("#393E46"));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.decode("#393E46"));
        JButton loadInputButton = createModernButton("Load Input File");
        JButton loadOptabButton = createModernButton("Load Optab File");
        JButton runAssemblerButton = createModernButton("Run Assembler");
        buttonPanel.add(loadInputButton);
        buttonPanel.add(loadOptabButton);
        buttonPanel.add(runAssemblerButton);

        JPanel pass1Panel = createPassPanel("Pass 1", "Intermediate Code:", intermediateArea = createTextArea(),
                "Symbol Table:", symtabArea = createTextArea(),
                "Program Length:", lengthArea = createTextArea());

        JPanel pass2Panel = createPassPanel("Pass 2", "Output Code:", outputArea = createTextArea(),
                "Object Code:", objectCodeArea = createTextArea());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pass1Panel, pass2Panel);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(10);
        splitPane.setBackground(Color.decode("#393E46"));

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);

        loadInputButton.addActionListener(e -> loadFile(true, frame));
        loadOptabButton.addActionListener(e -> loadFile(false, frame));
        runAssemblerButton.addActionListener(e -> runAssembler(frame));
    }

    private static JPanel createPassPanel(String title, String label1, JTextArea area1, String label2, JTextArea area2, String label3, JTextArea area3) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.decode("#393E46"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.decode("#00ADB5"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        JLabel lbl1 = new JLabel(label1);
        lbl1.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lbl1, gbc);

        JScrollPane scroll1 = new JScrollPane(area1);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(scroll1, gbc);

        JLabel lbl2 = new JLabel(label2);
        lbl2.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lbl2, gbc);

        JScrollPane scroll2 = new JScrollPane(area2);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(scroll2, gbc);

        if (label3 != null && area3 != null) {
            JLabel lbl3 = new JLabel(label3);
            lbl3.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(lbl3, gbc);

            JScrollPane scroll3 = new JScrollPane(area3);
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(scroll3, gbc);
        }

        return panel;
    }

    private static JPanel createPassPanel(String title, String label1, JTextArea area1, String label2, JTextArea area2) {
        return createPassPanel(title, label1, area1, label2, area2, null, null);
    }

    private static JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(1, 1);
        textArea.setLineWrap(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(Color.decode("#222831"));
        textArea.setForeground(Color.WHITE);
        textArea.setBorder(BorderFactory.createLineBorder(Color.decode("#00ADB5"), 2));
        return textArea;
    }

    private static JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.decode("#00ADB5"));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode("#393E46"));
                button.setForeground(Color.decode("#00ADB5"));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode("#00ADB5"));
                button.setForeground(Color.WHITE);
            }
        });
        return button;
    }

    private static void loadFile(boolean isInputFile, JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                if (isInputFile) {
                    inputFile = selectedFile;
                    JOptionPane.showMessageDialog(frame, "Input file loaded: " + inputFile.getName());
                } else {
                    optabFile = selectedFile;
                    JOptionPane.showMessageDialog(frame, "Optab file loaded: " + optabFile.getName());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error loading file: " + ex.getMessage());
            }
        }
    }

    private static void runAssembler(JFrame frame) {
        if (inputFile == null || optabFile == null) {
            JOptionPane.showMessageDialog(frame, "Please load both input and Optab files.");
            return;
        }
        try {
            loadOptabFromFile(optabFile);
            pass1(inputFile.getAbsolutePath());
            loadSymtab();
            pass2();
            JOptionPane.showMessageDialog(frame, "Assembling Completed!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error during assembling: " + ex.getMessage());
        }
    }

    private static void loadOptabFromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        opcodeTable.clear();
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length == 2) {
                opcodeTable.put(parts[0], parts[1]);
            }
        }

        reader.close();
    }

    private static void loadSymtab() {
        StringBuilder symtabContent = new StringBuilder();
        for (Map.Entry<String, Integer> entry : symbolTable.entrySet()) {
            symtabContent.append(entry.getKey())
                    .append("\t")
                    .append(String.format("%04X", entry.getValue()).toUpperCase())
                    .append("\n");
        }
        symtabArea.setText(symtabContent.toString());
    }

    private static void pass1(String inputFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
        StringBuilder intermediateContent = new StringBuilder();
        StringBuilder lengthContent = new StringBuilder();

        String line;
        int locationCounter = 0;
        boolean isStartFound = false;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");

            String label = "-", opcode = "-", operand = "-";

            if (parts.length > 0) {
                label = parts[0].equals("-") ? "-" : parts[0];
                opcode = (parts.length > 1 && !parts[1].equals("-")) ? parts[1] : "-";
                operand = (parts.length > 2 && !parts[2].equals("-")) ? parts[2] : "-";

                if (opcode.equals("START") && !isStartFound) {
                    startingAddress = Integer.parseInt(operand);
                    locationCounter = startingAddress;
                    isStartFound = true;
                } else if (isStartFound) {
                    if (!label.equals("-")) {
                        symbolTable.put(label, locationCounter);
                    }
                    if (!opcode.equals("-")) {
                        intermediateContent.append(String.format("%04X\t", locationCounter));
                        intermediateContent.append(label).append("\t").append(opcode).append("\t").append(operand).append("\n");
                        locationCounter += 3; 
                    }
                }
            }
        }

        programLength = locationCounter - startingAddress;
        lengthContent.append("Program Length: ").append(String.format("%04X", programLength).toUpperCase());
        lengthArea.setText(lengthContent.toString());
        intermediateArea.setText(intermediateContent.toString());
        reader.close();
    }

    private static void pass2() {
        StringBuilder outputContent = new StringBuilder();
        StringBuilder objectCodeContent = new StringBuilder();
        String[] intermediateLines = intermediateArea.getText().split("\n");

        for (String line : intermediateLines) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 2) {
                String label = parts[1];
                String opcode = parts[2];
                String operand = parts.length > 3 ? parts[3] : "-";
                String objCode = "";

                if (opcodeTable.containsKey(opcode)) {
                    objCode = opcodeTable.get(opcode);
                }

                outputContent.append(line).append("\n");
                objectCodeContent.append(objCode).append(" ").append(operand).append("\n");
            }
        }

        outputArea.setText(outputContent.toString());
        objectCodeArea.setText(objectCodeContent.toString());
    }
}
