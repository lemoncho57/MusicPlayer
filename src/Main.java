import javax.print.DocFlavor;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

public class Main implements ActionListener, ListSelectionListener, LineListener, ChangeListener {

    private JFrame frame;
    private DefaultListModel<File> listModel;
    private JList<File> list;
    private JButton playB;
    private JButton pauseB;
    private JButton browseB;
    private JCheckBox isInLoop;
    private JLabel songTitleLabel;
    private static JSlider frameSlider;

    private JFileChooser fileChooser;

    java.io.FileFilter fileFilter;

    File folderPath;
    String path;
    File[] files;

    AudioInputStream audioInputStream;
    static Clip clip;

    Main() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        frame = new JFrame("Audio Player | Lemoncho");
        frame.setSize(800,820);

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
        clip.addLineListener(this);

        list = new JList<File>();
        list.setBounds(100,200,600,400);
        list.setModel(listModel);
        list.addListSelectionListener(this);

        playB = new JButton("Play");
        playB.setBounds(100,670, 130,30);
        playB.addActionListener(this);

        pauseB = new JButton("Pause");
        pauseB.setBounds(250,670, 130,30);
        pauseB.addActionListener(this);

        browseB = new JButton("Browse");
        browseB.setBounds(100, 150, 130,30);
        browseB.addActionListener(this);

        isInLoop = new JCheckBox("Loop");
        isInLoop.setBounds(400, 670, 130, 30);
        isInLoop.addActionListener(this);

        frameSlider = new JSlider(JSlider.HORIZONTAL, 0, clip.getFrameLength(), 0);
        frameSlider.setBounds(350, 630, 130,30);
        frameSlider.setMajorTickSpacing(10);
        frameSlider.setMinorTickSpacing(1);

        frameSlider.addChangeListener(this);

        songTitleLabel = new JLabel("Song selected: ");
        songTitleLabel.setBounds(200, 720, 600, 30);

        frame.add(playB);
        frame.add(pauseB);
        frame.add(browseB);
        frame.add(frameSlider);
        frame.add(isInLoop);
        frame.add(songTitleLabel);
        frame.add(list);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        new Main();

        while (true)
        {
            frameSlider.setValue(clip.getFramePosition());

            try {
                Thread.sleep(30); // Adjust as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseB) { //Handler for browse button
            list.clearSelection(); // Clears the selected item(It DOESNT delete it! It just unselects it) because it throws errors
            int v = fileChooser.showOpenDialog(null); // fileChooser option
            if (v == JFileChooser.APPROVE_OPTION) {
                folderPath = fileChooser.getSelectedFile();
                path = folderPath.getAbsolutePath(); // Dont ask me why I putted this if you want you can remove it. Wont break anything I promise
                files = folderPath.listFiles(fileFilter);
            }
            UpdateList();
        }

        if (e.getSource() == playB) { // Handler for play Button
            clip.start(); // Start
            if (clip.getFramePosition() == clip.getFrameLength()){
                clip.setFramePosition(0);
                clip.start();
            }
        }
        if (e.getSource() == pauseB) { // Handler for pause Button
            clip.stop(); // Pause
        }
        if (e.getSource() == isInLoop){ // Handler for loop check box
            if (isInLoop.isSelected()) clip.loop(Clip.LOOP_CONTINUOUSLY); // Checks if the check box is selected and if it is it sets the looping to true
            else clip.loop(0); // Checks again but if its not selected it disables it
        }
    }

    private void UpdateList(){
        list.clearSelection();
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
                if (!list.isSelectionEmpty()) {
                    File file = list.getSelectedValue().getAbsoluteFile();
                    audioInputStream = AudioSystem.getAudioInputStream(file);
                    clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    frameSlider.setMaximum(clip.getFrameLength());

                    songTitleLabel.setText("Song selected: " + file.getName());
                }
            }catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error");
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void update(LineEvent event) {
        //clip.setFramePosition(frameSlider.getValue());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (source.getValueIsAdjusting()){
            clip.setFramePosition(source.getValue());
        }
    }
}