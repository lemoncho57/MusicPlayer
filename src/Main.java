import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


public class Main implements ActionListener, ListSelectionListener, LineListener, ChangeListener {

    private JFrame frame;
    private DefaultListModel<File> listModel;
    private JList<File> list;
    private JButton playB;
    private JButton pauseB;
    private JButton stopB;
    private JButton browseB;
    private JCheckBox isInLoop;
    private JLabel songTitleLabel;
    private static JLabel songStatusLabel;
    private static JSlider frameSlider;

    private JFileChooser fileChooser;

    java.io.FileFilter fileFilter;

    File folderPath;
    String path;
    File[] files;

    AudioInputStream audioInputStream;
    static Clip clip;

    private static SongStatusE songStatus = SongStatusE.STOPPED;

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

        stopB = new JButton("Stop");
        stopB.setBounds(400,670, 130,30);
        stopB.addActionListener(this);

        browseB = new JButton("Browse");
        browseB.setBounds(100, 150, 130,30);
        browseB.addActionListener(this);

        isInLoop = new JCheckBox("Loop");
        isInLoop.setBounds(550, 670, 130, 30);
        isInLoop.addActionListener(this);

        frameSlider = new JSlider(JSlider.HORIZONTAL, 0, clip.getFrameLength(), 0);
        frameSlider.setBounds(350, 630, 130,30);
        frameSlider.setMajorTickSpacing(10);
        frameSlider.setMinorTickSpacing(1);

        frameSlider.addChangeListener(this);

        songTitleLabel = new JLabel("Song selected: ");
        songTitleLabel.setBounds(200, 720, 600, 30);

        songStatusLabel = new JLabel("Status: " + songStatus);
        songStatusLabel.setBounds(300, 150, 130, 30);

        frame.add(playB);
        frame.add(pauseB);
        frame.add(stopB);
        frame.add(browseB);
        frame.add(frameSlider);
        frame.add(isInLoop);
        frame.add(songTitleLabel);
        frame.add(songStatusLabel);
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
                Thread.sleep(40); // Adjust as needed
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
            Play();
        }
        if (e.getSource() == pauseB) { // Handler for pause Button
            Pause();
        }
        if (e.getSource() == stopB) {
            Stop();
        }

        if (e.getSource() == isInLoop){ // Handler for loop check box
            if (isInLoop.isSelected()) clip.loop(Clip.LOOP_CONTINUOUSLY); // Checks if the check box is selected and if it is it sets the looping to true
            else clip.loop(0); // Checks again but if its not selected it disables it
            UpdateSongStatusLabel();
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

    private void UpdateSongStatusLabel(){
        songStatusLabel.setText("Status: " + songStatus);
    }

    private void Play(){
        clip.start(); // Start
        songStatus = SongStatusE.PLAYING;
        if (clip.getFramePosition() == clip.getFrameLength()){
            clip.setFramePosition(0);
            clip.start();
        }
        UpdateSongStatusLabel();
    }
    private void Pause(){
        clip.stop(); // Pause
        songStatus = !(clip.getFramePosition() == 0) ? SongStatusE.PAUSED : SongStatusE.STOPPED;
        UpdateSongStatusLabel();
    }
    private void Stop(){
        songStatus = SongStatusE.STOPPED;
        clip.stop();
        clip.setFramePosition(0);
        UpdateSongStatusLabel();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == list){
            try {
                if (!list.isSelectionEmpty()) {
                    File file = list.getSelectedValue().getAbsoluteFile();
                    clip.stop();
                    audioInputStream = AudioSystem.getAudioInputStream(file);
                    clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    //clip.addLineListener(this);
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
        LineEvent.Type type = event.getType();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (source.getValueIsAdjusting()){
            clip.setFramePosition(source.getValue());
        }
    }
}