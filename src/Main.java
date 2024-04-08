import javax.print.DocFlavor;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

public class Main implements ActionListener, ListSelectionListener {

    private JFrame frame;
    private DefaultListModel<File> listModel;
    private JList<File> list;
    private JButton playB;
    private JButton pauseB;
    private JButton stopB;
    private JButton nextB;
    private JButton prevB;
    private JButton browseB;
    private JLabel songTitleLabel;

    private JFileChooser fileChooser;

    java.io.FileFilter fileFilter;

    File folderPath;
    String path;
    File[] files;

    AudioInputStream audioInputStream;
    Clip clip;

    Main() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        frame = new JFrame("Audio Player | Lemoncho");
        frame.setSize(800,800);

        fileFilter = new java.io.FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.getName().endsWith(".wav")) return true;
                return false;
            }
        };

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        folderPath = new File("C:\\Users\\Public\\Music");
        files = folderPath.listFiles(fileFilter);

        File randFile = new File("E:\\sound\\discord_ringtone_but_its_a_loop.wav");

        listModel = new DefaultListModel<File>();
        for (File file : files){
            listModel.addElement(file);
        }
        listModel.addElement(randFile); // TESTING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! REMOVE PLS!!!!!!!!!!!!!!!

        try {
            audioInputStream = AudioSystem.getAudioInputStream(listModel.getElementAt(0));
            System.out.println(randFile.getAbsolutePath());
        }catch (Exception e) {
            System.out.println("IMA PROBLEM!!!!!");
            System.out.println(randFile.getAbsolutePath());
        }
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);


        list = new JList<File>();
        list.setBounds(100,200,600,400);
        list.setModel(listModel);
        list.addListSelectionListener(this);

        playB = new JButton("Play");
        playB.setBounds(100,650, 130,30);
        playB.addActionListener(this);

        pauseB = new JButton("Pause");
        pauseB.setBounds(250,650, 130,30);
        pauseB.addActionListener(this);

        browseB = new JButton("Browse");
        browseB.setBounds(100, 150, 130,30);
        browseB.addActionListener(this);

        songTitleLabel = new JLabel("Song selected: ");
        songTitleLabel.setBounds(200, 700, 600, 30);

        frame.add(playB);
        frame.add(pauseB);
        frame.add(browseB);
        frame.add(songTitleLabel);
        frame.add(list);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        new Main();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseB) {
            list.clearSelection();
            int v = fileChooser.showOpenDialog(null);
            if (v == JFileChooser.APPROVE_OPTION) {
                folderPath = fileChooser.getSelectedFile();
                path = folderPath.getAbsolutePath();
                files = folderPath.listFiles(fileFilter);
            }
            UpdateList();
        }

        if (e.getSource() == playB) {
            clip.start();
        }
        if (e.getSource() == pauseB) {
            clip.stop();
        }

    }

    private void UpdateList(){
        folderPath = fileChooser.getSelectedFile();
        path = folderPath.getAbsolutePath();
        files = folderPath.listFiles(fileFilter);
        listModel.clear();
        for (File file : files){
            listModel.addElement(file);
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == list){
            try {
                File file = list.getSelectedValue().getAbsoluteFile();
                audioInputStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                songTitleLabel.setText("Song selected: " + file.getName());

            }catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error");
            }
        }
    }
}