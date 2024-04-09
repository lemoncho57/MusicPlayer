import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


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

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenuItem closeItem;
    private JMenuItem aboutItem;
    private JMenuItem supportedFormatsItem;

    private ImageIcon appIcon;

    private JFileChooser fileChooser;

    java.io.FileFilter fileFilter;

    File folderPath;
    String path;
    File[] files;

    AudioInputStream audioInputStream;
    static Clip clip;

    private static SongStatusE songStatus = SongStatusE.STOPPED;

    File fileConfig;
    FileReader fileConfigDir;
    private BufferedReader reader;
    private BufferedWriter writer;

    Main() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        frame = new JFrame("Audio Player | Lemoncho");
        frame.setSize(800,820);
        frame.setLocationRelativeTo(null);

        fileFilter = new java.io.FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.getName().endsWith(".wav")) return true;
                return false;
            }
        };

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        try {
            fileConfig = new File("configFileChooser.data");
            fileConfigDir = new FileReader(fileConfig);
            reader = new BufferedReader(fileConfigDir);

            File file = new File(String.valueOf(reader.readLine()));

            fileChooser.setCurrentDirectory(file);
            folderPath = new File(file.getAbsolutePath());
            files = folderPath.listFiles(fileFilter);
        }catch (Exception ex){
            System.out.println("Error when trying to load config");
            ex.printStackTrace();
        }


        listModel = new DefaultListModel<File>();
        try {
            for (File file : files) {
                listModel.addElement(file);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }


        //listModel.addElement(); // TESTING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! REMOVE PLS!!!!!!!!!!!!!!!

        try {
            audioInputStream = AudioSystem.getAudioInputStream(listModel.getElementAt(0));
        }catch (Exception e) {
            System.out.println("PROBLEM!!!!!");
            e.printStackTrace();
        }
        clip = AudioSystem.getClip();
        try {
            clip.open(audioInputStream);
        }catch (Exception ex) {
            ex.printStackTrace();}
        clip.addLineListener(this);


        list = new JList<File>();
        list.setBounds(100,170,600,400);
        list.setModel(listModel);
        list.addListSelectionListener(this);

        playB = new JButton("Play");
        playB.setBounds(100,640, 130,30);
        playB.addActionListener(this);

        pauseB = new JButton("Pause");
        pauseB.setBounds(250,640, 130,30);
        pauseB.addActionListener(this);

        stopB = new JButton("Stop");
        stopB.setBounds(400,640, 130,30);
        stopB.addActionListener(this);

        browseB = new JButton("Browse");
        browseB.setBounds(100, 120, 130,30);
        browseB.addActionListener(this);

        isInLoop = new JCheckBox("Loop");
        isInLoop.setBounds(550, 640, 130, 30);
        isInLoop.addActionListener(this);

        frameSlider = new JSlider(JSlider.HORIZONTAL, 0, clip.getFrameLength(), 0);
        frameSlider.setBounds(350, 600, 130,30);
        frameSlider.setMajorTickSpacing(10);
        frameSlider.setMinorTickSpacing(1);

        frameSlider.addChangeListener(this);

        songTitleLabel = new JLabel("Song selected: ");
        songTitleLabel.setBounds(200, 690, 600, 30);

        songStatusLabel = new JLabel("Status: " + songStatus);
        songStatusLabel.setBounds(300, 120, 130, 30);

        appIcon = new ImageIcon("Assets/pngegg.png");

        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");

        closeItem = new JMenuItem("Exit");
        closeItem.addActionListener(this);
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this);
        supportedFormatsItem = new JMenuItem("Supported formats");
        supportedFormatsItem.addActionListener(this);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        fileMenu.add(closeItem);
        helpMenu.add(aboutItem);
        helpMenu.add(supportedFormatsItem);

        frame.add(playB);
        frame.add(pauseB);
        frame.add(stopB);
        frame.add(browseB);
        frame.add(frameSlider);
        frame.add(isInLoop);
        frame.add(songTitleLabel);
        frame.add(songStatusLabel);
        frame.add(list);
        frame.setJMenuBar(menuBar);
        frame.setIconImage(appIcon.getImage());
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
            try {
                Browse();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
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

        if (e.getSource() == closeItem){
            System.exit(0);
        }
        if (e.getSource() == aboutItem){
            JOptionPane.showMessageDialog(null, "This is a music player made with ❤️ by Lemoncho");
        }
        if (e.getSource() == supportedFormatsItem){
            JOptionPane.showMessageDialog(null, "Currently the supported formats are: .wav");
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


    private void Browse() throws IOException {
        list.clearSelection(); // Clears the selected item(It DOESNT delete it! It just unselects it) because it throws errors
        int v = fileChooser.showOpenDialog(null); // fileChooser option
        if (v == JFileChooser.APPROVE_OPTION) {
            folderPath = fileChooser.getSelectedFile();
            path = folderPath.getAbsolutePath(); // Dont ask me why I putted this if you want you can remove it. Wont break anything I promise
            files = folderPath.listFiles(fileFilter);
            SaveDirectoryLocation();
        }
        UpdateList();
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

    private void SaveDirectoryLocation() throws IOException {
        writer = new BufferedWriter(new FileWriter(fileConfig));
        writer.write(fileChooser.getSelectedFile().getAbsolutePath());
        writer.flush();
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

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (source.getValueIsAdjusting()){
            clip.setFramePosition(source.getValue());
        }
    }
}