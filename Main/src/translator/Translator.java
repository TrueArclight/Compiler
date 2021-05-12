package translator;
import javax.swing.*;
import java.io.IOException;

public class Translator extends JFrame {
    private JTextField path;
    private JButton In;
    private JButton Out;
    private JTextArea inArea;
    private JTextArea outArea;
    private JPanel rootPanel;
    Translator(){
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setTitle("Translator");
        setVisible(true);
        In.addActionListener(e -> addNewFile());
        Out.addActionListener(e -> translate());

    }
    private void addNewFile() {
        JFileChooser chooser = new JFileChooser();
        int ret = chooser.showDialog(this, "Выбрать файл");
        if (ret == JFileChooser.APPROVE_OPTION)
            path.setText(chooser.getSelectedFile().getAbsolutePath());
    }

    private void translate() {
        try {
            String file = path.getText();
            if (file.isEmpty()) return;

            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(file);
            inArea.setText(lexicalAnalyzer.getSource());
            String lexic = lexicalAnalyzer.analyze();

            SyntaxAnalyzer sa = new SyntaxAnalyzer();
            String syntax = sa.analyze(lexic);

            CodeGenerator cg = new CodeGenerator();
            cg.generate(syntax);

            outArea.setText(cg.getOutput());
        } catch (IOException e) {
            inArea.setText("Ошибка I/O: " + e);
        } catch (IllegalStateException e) {
            outArea.setText("Ошибка трансляции.\n" + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Translator();
    }


}
