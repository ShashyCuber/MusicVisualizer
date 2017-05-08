import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SwingVisualizer {
    private AudioFile audioFile;
    private int rectangleNum;
    private JFrame frame;
    private int frameHeight;
    private int frameWidth;
    private JPanel panel;
    private JPanel optionPanel;
    private JLabel[] rectangles;
    private int maxHeight;
    private int minHeight;
    private Timer t;
    private double smoothingRatio = 0.5;
    private boolean[] stretch;
    private double[] pastHeights;
    private double[] currentHeights;
    private ArrayList<String> backgroundFileName;
    private ArrayList<String> musicFileName;
    private int backgroundIndex = 0;
    private int musicIndex = 2;
    private int colorIndex = 0;
    private Color[] colors = {Color.WHITE, new Color(219, 20, 20), new Color(219, 70, 20), new Color(219, 120, 20),
            new Color(219, 170, 20), new Color(219, 219, 20), new Color(170, 219, 20),
            new Color(120, 219, 20), new Color(70, 219, 20), new Color(20, 219, 20),
            new Color(20, 219, 70), new Color(20, 219, 120), new Color(20, 219, 170),
            new Color(20, 219, 219), new Color(20, 170, 219), new Color(20, 120, 219),
            new Color(20, 70, 219), new Color(20, 20, 219), new Color(70, 20, 219),
            new Color(120, 20, 219), new Color(170, 20, 219), new Color(219, 20, 219),
            new Color(219, 20, 170), new Color(219, 20, 120), new Color(219, 20, 70),
            new Color(220, 20, 60), new Color(219, 20, 20)};

    long nano;
    long currTime;
    int rectangleWidth;
    public SwingVisualizer(){
        rectangleNum = 64;
        stretch = new boolean[rectangleNum];
        getFiles();
        createFrame();
        createOptionFrame();
    }

    private void getFiles(){
        //get all the possible backgrounds
        backgroundFileName = new ArrayList<String>();
        int fileIndex = 1;
        loop:
        while (true){
            try{
                String backgrouundFilePath = "files/wallpapers/monstercat" + fileIndex++ + ".jpg";
                File f = new File(backgrouundFilePath);
                if (!f.exists()){
                    throw new FileNotFoundException();
                }
                backgroundFileName.add(backgrouundFilePath);
                //System.out.println(backgrouundFilePath);
            } catch (FileNotFoundException e){
                break loop;
            }
        }

        File folder = new File("files/music/");
        File[] listFiles = folder.listFiles();
        musicFileName = new ArrayList<String>();
        for (File f : listFiles){
            musicFileName.add(f.getName());
        }
    }

    public void createFrame(){
        frame = new JFrame();
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        ImageIcon logo = new ImageIcon("files/icon/monstercat_icon_transparent.png");
        frame.setIconImage(logo.getImage());
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frameHeight = (int) screen.getHeight();
        frameWidth = (int) screen.getWidth();
    }

    private void createOptionFrame(){
        //background
        ImageIcon i = new ImageIcon("files/wallpapers/black.jpg");
        Image image = i.getImage();
        Image newImg = image.getScaledInstance(frameWidth, frameHeight,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.setIcon(i);
        backgroundLabel.setSize(frameWidth, frameHeight);
        backgroundLabel.setLocation(0, 0);

        //title
        int titleLength = (int) (frameWidth * 0.8);
        int titleWidth = (int) (frameWidth * 0.1) / 2;
        i = new ImageIcon("files/icon/monstercat_visualizer.png");
        image = i.getImage();
        newImg = image.getScaledInstance(titleLength, titleWidth,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JLabel titleLabel = new JLabel();
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setIcon(i);
        titleLabel.setSize(titleLength, titleWidth);
        titleLabel.setLocation((frameWidth / 2) - (titleLength / 2), (int) (titleWidth * 0.5));

        //background option
        int backgroundThumbWidth = (int) (frameWidth * 0.25);
        int backgroundThumbHeight = (int) (frameHeight * 0.25);
        JLabel backgroundThumb = new JLabel();
        i = new ImageIcon(backgroundFileName.get(backgroundIndex));
        image = i.getImage();
        newImg = image.getScaledInstance(backgroundThumbWidth, backgroundThumbHeight,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        backgroundThumb.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundThumb.setIcon(i);
        backgroundThumb.setSize(backgroundThumbWidth, backgroundThumbHeight);
        backgroundThumb.setLocation((int) ((frameWidth - backgroundThumbWidth) * 0.5), (int) (frameHeight * 0.2));
        backgroundThumb.setOpaque(true);
        backgroundThumbWidth = (int) (frameWidth * 0.255);
        backgroundThumbHeight = (int) (frameHeight * 0.26);
        JLabel backgroundBorder = new JLabel();
        i = new ImageIcon("files/wallpapers/white.jpg");
        image = i.getImage();
        newImg = image.getScaledInstance(backgroundThumbWidth, backgroundThumbHeight,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        backgroundBorder.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundBorder.setIcon(i);
        backgroundBorder.setSize(backgroundThumbWidth, backgroundThumbHeight);
        backgroundBorder.setLocation((int) ((frameWidth - backgroundThumbWidth) * 0.5),
            (int) (frameHeight * 0.2) - (((int) (frameHeight * 0.26) - (int) (frameHeight * 0.25)) / 2));
        backgroundBorder.setOpaque(true);

        //left button for background
        int arrowWidth = (int) (frameWidth * 0.05);
        int arrowHeight = (int) (frameHeight * 0.05);
        i = new ImageIcon("files/icon/left.png");
        image = i.getImage();
        newImg = image.getScaledInstance(arrowHeight, arrowWidth,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JButton leftButton = new JButton();
        leftButton.setContentAreaFilled(false);
        leftButton.setBorderPainted(false);
        leftButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftButton.setIcon(i);
        leftButton.setSize(arrowHeight, arrowWidth);
        leftButton.setLocation((int) (frameWidth * 0.3), (int) (frameHeight * 0.27));
        i = new ImageIcon("files/icon/left.png");
        image = i.getImage();
        newImg = image.getScaledInstance((int) (arrowHeight * 1.05), (int) (arrowWidth * 1.05),  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        leftButton.setRolloverEnabled(true);
        leftButton.setRolloverIcon(i);

        //right button for background
        i = new ImageIcon("files/icon/right.png");
        image = i.getImage();
        newImg = image.getScaledInstance(arrowHeight, arrowWidth,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JButton rightButton = new JButton();
        rightButton.setContentAreaFilled(false);
        rightButton.setBorderPainted(false);
        rightButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightButton.setIcon(i);
        rightButton.setSize(arrowHeight, arrowWidth);
        rightButton.setLocation((int) (frameWidth * 0.7) - arrowHeight, (int) (frameHeight * 0.27));
        i = new ImageIcon("files/icon/right.png");
        image = i.getImage();
        newImg = image.getScaledInstance((int) (arrowHeight * 1.05), (int) (arrowWidth * 1.05),  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        rightButton.setRolloverEnabled(true);
        rightButton.setRolloverIcon(i);

        //start button
        i = new ImageIcon("files/icon/start.png");
        image = i.getImage();
        newImg = image.getScaledInstance((int) (frameWidth * 0.1), (int) (frameHeight * 0.05),  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JButton startButton = new JButton();
        startButton.setIcon(i);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial-Black", Font.BOLD, frameWidth / 10));
        startButton.setSize((int) (frameWidth * 0.1), (int) (frameHeight * 0.05));
        startButton.setLocation((int) (frameWidth * 0.5) - (int) (frameWidth * 0.1 * 0.5), (int) (frameHeight * 0.92));
        startButton.setRolloverEnabled(true);
        i = new ImageIcon("files/icon/start.png");
        image = i.getImage();
        newImg = image.getScaledInstance((int) (startButton.getWidth() * 1.05), (int) (startButton.getHeight() * 1.05),  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        startButton.setRolloverEnabled(true);
        startButton.setRolloverIcon(i);

        //left button for song
        arrowWidth = (int) (frameWidth * 0.05);
        arrowHeight = (int) (frameHeight * 0.05);
        i = new ImageIcon("files/icon/left.png");
        image = i.getImage();
        newImg = image.getScaledInstance(arrowHeight, arrowWidth,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JButton leftSongButton = new JButton();
        leftSongButton.setContentAreaFilled(false);
        leftSongButton.setBorderPainted(false);
        leftSongButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftSongButton.setIcon(i);
        leftSongButton.setSize(arrowHeight, arrowWidth);
        leftSongButton.setLocation((int) (frameWidth * 0.3), (int) (frameHeight * 0.57));
        i = new ImageIcon("files/icon/left.png");
        image = i.getImage();
        newImg = image.getScaledInstance((int) (arrowHeight * 1.05), (int) (arrowWidth * 1.05),  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        leftSongButton.setRolloverEnabled(true);
        leftSongButton.setRolloverIcon(i);

        //right button for background
        i = new ImageIcon("files/icon/right.png");
        image = i.getImage();
        newImg = image.getScaledInstance(arrowHeight, arrowWidth,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JButton rightSongButton = new JButton();
        rightSongButton.setContentAreaFilled(false);
        rightSongButton.setBorderPainted(false);
        rightSongButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightSongButton.setIcon(i);
        rightSongButton.setSize(arrowHeight, arrowWidth);
        rightSongButton.setLocation((int) (frameWidth * 0.7) - arrowHeight, (int) (frameHeight * 0.57));
        i = new ImageIcon("files/icon/right.png");
        image = i.getImage();
        newImg = image.getScaledInstance((int) (arrowHeight * 1.05), (int) (arrowWidth * 1.05),  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        rightSongButton.setRolloverEnabled(true);
        rightSongButton.setRolloverIcon(i);

        //song
        JLabel songLabel = new JLabel(musicFileName.get(musicIndex));
        songLabel.setForeground(Color.WHITE);
        songLabel.setOpaque(false);
        songLabel.setSize((int) (frameWidth * 0.3), (int) (frameHeight * 0.1));
        songLabel.setFont(new Font("Arial-Black", Font.BOLD, frameWidth / 50));
        songLabel.setLocation((int) (frameWidth * 0.35), (int) (frameHeight * 0.57));

        //left button for color
        arrowWidth = (int) (frameWidth * 0.05);
        arrowHeight = (int) (frameHeight * 0.05);
        i = new ImageIcon("files/icon/left.png");
        image = i.getImage();
        newImg = image.getScaledInstance(arrowHeight, arrowWidth,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JButton leftColorButton = new JButton();
        leftColorButton.setContentAreaFilled(false);
        leftColorButton.setBorderPainted(false);
        leftColorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftColorButton.setIcon(i);
        leftColorButton.setSize(arrowHeight, arrowWidth);
        leftColorButton.setLocation((int) (frameWidth * 0.3), (int) (frameHeight * 0.67));
        i = new ImageIcon("files/icon/left.png");
        image = i.getImage();
        newImg = image.getScaledInstance((int) (arrowHeight * 1.05), (int) (arrowWidth * 1.05),  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        leftColorButton.setRolloverEnabled(true);
        leftColorButton.setRolloverIcon(i);

        //right button for color
        i = new ImageIcon("files/icon/right.png");
        image = i.getImage();
        newImg = image.getScaledInstance(arrowHeight, arrowWidth,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JButton rightColorButton = new JButton();
        rightColorButton.setContentAreaFilled(false);
        rightColorButton.setBorderPainted(false);
        rightColorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightColorButton.setIcon(i);
        rightColorButton.setSize(arrowHeight, arrowWidth);
        rightColorButton.setLocation((int) (frameWidth * 0.7) - arrowHeight, (int) (frameHeight * 0.67));
        i = new ImageIcon("files/icon/right.png");
        image = i.getImage();
        newImg = image.getScaledInstance((int) (arrowHeight * 1.05), (int) (arrowWidth * 1.05),  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        rightColorButton.setRolloverEnabled(true);
        rightColorButton.setRolloverIcon(i);

        //color label
        JLabel colorLabel = new JLabel();
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorLabel.setForeground(Color.WHITE);
        colorLabel.setOpaque(true);
        colorLabel.setBackground(colors[colorIndex]);
        colorLabel.setSize((int) (frameWidth * 0.05), (int) (frameHeight * 0.05));
        colorLabel.setLocation((int) (frameWidth * 0.5) - (int) (frameWidth * 0.05 * 0.5), (int) (frameHeight * 0.69));

        optionPanel = new JPanel(null);
        frame.add(optionPanel);
        optionPanel.add(backgroundThumb);
        optionPanel.add(backgroundBorder);
        optionPanel.add(songLabel);
        optionPanel.add(leftButton);
        optionPanel.add(rightButton);
        optionPanel.add(startButton);
        optionPanel.add(leftSongButton);
        optionPanel.add(rightSongButton);
        optionPanel.add(leftColorButton);
        optionPanel.add(rightColorButton);
        optionPanel.add(colorLabel);
        optionPanel.add(backgroundLabel);
        frame.setVisible(true);

        class leftBackGroundButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                if (--backgroundIndex < 0){
                    backgroundIndex = backgroundFileName.size() - 1;
                }
                ImageIcon i = new ImageIcon(backgroundFileName.get(backgroundIndex));
                Image image = i.getImage();
                Image newImg = image.getScaledInstance(backgroundThumb.getWidth(), backgroundThumb.getHeight(),  java.awt.Image.SCALE_SMOOTH);
                i = new ImageIcon(newImg);
                backgroundThumb.setAlignmentX(Component.CENTER_ALIGNMENT);
                backgroundThumb.setIcon(i);
            }
        }
        leftButton.addActionListener(new leftBackGroundButtonListener());

        class rightBackGroundButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                if (++backgroundIndex >= backgroundFileName.size()){
                    backgroundIndex = 0;
                }
                ImageIcon i = new ImageIcon(backgroundFileName.get(backgroundIndex));
                Image image = i.getImage();
                Image newImg = image.getScaledInstance(backgroundThumb.getWidth(), backgroundThumb.getHeight(),  java.awt.Image.SCALE_SMOOTH);
                i = new ImageIcon(newImg);
                backgroundThumb.setAlignmentX(Component.CENTER_ALIGNMENT);
                backgroundThumb.setIcon(i);
            }
        }
        rightButton.addActionListener(new rightBackGroundButtonListener());

        class leftColorButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                if (--colorIndex < 0){
                    colorIndex = colors.length - 1;
                }
                colorLabel.setBackground(colors[colorIndex]);
            }
        }
        leftColorButton.addActionListener(new leftColorButtonListener());

        class rightColorButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                if (++colorIndex >= colors.length){
                    colorIndex = 0;
                }
                colorLabel.setBackground(colors[colorIndex]);
            }
        }
        rightColorButton.addActionListener(new rightColorButtonListener());

        class leftSongButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                if (--musicIndex < 0){
                    musicIndex = musicFileName.size() - 1;
                }
                songLabel.setText(musicFileName.get(musicIndex));
            }
        }
        leftSongButton.addActionListener(new leftSongButtonListener());

        class rightSongButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                if (++musicIndex >= musicFileName.size()){
                    musicIndex = 0;
                }
                songLabel.setText(musicFileName.get(musicIndex));
            }
        }
        rightSongButton.addActionListener(new rightSongButtonListener());

        class startButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                frame.remove(optionPanel);
                createBackground();
                audioFile = new AudioFile("files/music/" + musicFileName.get(musicIndex));
                startVisualizer();
            }
        }
        startButton.addActionListener(new startButtonListener());
    }

    private void createBackground(){
        ImageIcon i = new ImageIcon(backgroundFileName.get(backgroundIndex));
        Image image = i.getImage();
        Image newImg = image.getScaledInstance(frameWidth, frameHeight,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);

        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.setIcon(i);
        backgroundLabel.setSize(frameWidth, frameHeight);
        backgroundLabel.setLocation(0, 0);

        panel = new JPanel(null);
        frame.add(panel);
        createRectangles();
        panel.updateUI();
        panel.add(backgroundLabel);
    }

    private void createTitle(){
        int y = (int) (frameHeight * 0.67);
        int x = (int) (frameWidth * 0.1);
        int iconSize = (int) (frameWidth * 0.1);

        ImageIcon i = new ImageIcon("files/icon/monstercat_icon_transparent.png");
        Image image = i.getImage();
        Image newImg = image.getScaledInstance(iconSize, iconSize,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);

        JLabel iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setIcon(i);
        iconLabel.setSize(iconSize, iconSize);
        iconLabel.setLocation(x, y);
        iconLabel.setOpaque(true);

        x = (int) (frameWidth * 0.21);
        int titleLength = (int) (frameWidth * 0.8) - x;
        int titleWidth = (int) (frameWidth * 0.1) / 2;

        i = new ImageIcon("files/icon/monstercat_visualizer.png");
        image = i.getImage();
        newImg = image.getScaledInstance(titleLength, titleWidth,  java.awt.Image.SCALE_SMOOTH);
        i = new ImageIcon(newImg);
        JLabel titleLabel = new JLabel();
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setIcon(i);
        titleLabel.setSize(titleLength, titleWidth);
        titleLabel.setLocation(x, y);
        panel.add(iconLabel);
        panel.add(titleLabel);
    }

    private void createRectangles(){
        rectangles = new JLabel[rectangleNum];
        maxHeight = (int) (frameHeight * 0.5);
        minHeight = frameHeight / 200;
        int totalRectangleWidth = (int) (frameWidth * 0.8);
        int y = (int) (frameHeight * 0.7);
        int rectangleHeight = minHeight;
        rectangleWidth =  totalRectangleWidth / (int) (rectangleNum + (0.2 * rectangleNum) + 0.2);
        int spaceWidth = (int) (rectangleWidth * 0.2);
        int x = (int) (frameWidth * 0.1);
        pastHeights = new double[rectangleNum];

        for (int i = 0; i < rectangleNum; i++){
            pastHeights[i] = rectangleHeight;
            rectangles[i] = new JLabel();
            rectangles[i].setSize(rectangleWidth, rectangleHeight);
            rectangles[i].setLocation(x, y - rectangleHeight);
            x += rectangleWidth + spaceWidth;
            rectangles[i].setOpaque(true);
            rectangles[i].setBackground(colors[colorIndex]);
            panel.add(rectangles[i]);
            stretch[i] = false;
        }
    }

    private void startVisualizer(){
        class keyboardListener implements KeyListener {
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    t.stop();
                    audioFile.stopSong();
                    createOptionFrame();
                    frame.remove(panel);
                    frame.setVisible(true);
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        }
        frame.addKeyListener(new keyboardListener());

        class TimerListener implements Runnable{
            private int count = 0;
            public void run()
            {
                while(true){
                    if(Math.abs((System.nanoTime()-currTime)%nano) <= 1000000)
                    {
                        try {
                            updateRectangles();
                        } catch(Exception e){}
                        if (count++ == 1){
                            audioFile.playSong();
                        }
                    }
                }
            }
        }
        nano = ((((audioFile.getAudioDuration() * 1000) * audioFile.getBuffer()*1) / audioFile.getAmountOfData()*1)*1000000);
        currTime = System.nanoTime();
        new Thread(new TimerListener()).start();
    }

    private void updateRectangles(){
        if (audioFile.hasNext()){
            double[] heights = audioFile.getNext(rectangleNum);
            for (int i = 0; i < rectangleNum; i++){
                int y = (int) (frameHeight * 0.6);
                int x = rectangles[i].getX();
                int height = (int) heights[i];
                if (heights[i] < (pastHeights[i] - 35)){
                    height = (int) pastHeights[i] - (40);
                }

                if (height < minHeight){
                    height = minHeight;
                }

                boolean goingup = (height > pastHeights[i]);
                try{
                    if((height-heights[i-1])>(height/2))
                    {
                        int nh = (int)heights[i-1]+(height/2);
                        if(goingup){
                            rectangles[i-1].setLocation(rectangles[i-1].getX(), y - (nh));
                            rectangles[i-1].setSize(rectangles[i-1].getWidth(),nh);
                        }
                        else 
                        {
                            rectangles[i-1].setSize(rectangles[i-1].getWidth(),nh);
                            rectangles[i-1].setLocation(rectangles[i-1].getX(), y - (nh));
                        }
                    }
                    if((height-heights[i+1])>(height/2) && heights[i+1] > 0)
                    {
                        int nh = (int)heights[i+1]+(height/2);
                        if(goingup)
                        {
                            rectangles[i+1].setLocation(rectangles[i+1].getX(), y - (nh));
                            rectangles[i+1].setSize(rectangles[i+1].getWidth(),nh);
                        }
                        else
                        {
                            rectangles[i+1].setSize(rectangles[i+1].getWidth(),nh);
                            rectangles[i+1].setLocation(rectangles[i+1].getX(), y - (nh));
                        }
                    }
                }catch(Exception ex){}

                if (height > maxHeight){
                    if(goingup)
                    {
                        if(stretch[i])
                        {
                            rectangles[i].setLocation(x-1, y - height);
                            stretch[i] = true;
                        }
                        else 
                        {
                            rectangles[i].setLocation(x, y - height);
                        }

                        try{
                            int val = 180 - ((height-maxHeight)/8);
                            rectangles[i].setBackground(new Color(255,val,val));
                        }catch(Exception ex){ rectangles[i].setBackground(Color.RED);}

                        rectangles[i].setSize(rectangleWidth+2,height);
                    }
                    else
                    {
                        try{
                            int val = 180 - ((height-maxHeight)/8);
                            rectangles[i].setBackground(new Color(255,val,val));
                        }catch(Exception ex){ rectangles[i].setBackground(Color.RED);}

                        rectangles[i].setSize(rectangleWidth+2,height);

                        if(stretch[i])
                        {
                            rectangles[i].setLocation(x-1, y - height);
                            stretch[i] = true;
                        }
                        else 
                        {
                            rectangles[i].setLocation(x, y - height);
                        }
                    }
                }
                else
                {
                    if(goingup)
                    {
                        if(stretch[i])
                        {
                            stretch[i] = false;
                            rectangles[i].setLocation(x+1, y - height);
                        }
                        else
                            rectangles[i].setLocation(x, y - height);
                        rectangles[i].setBackground(Color.WHITE);
                        rectangles[i].setSize(rectangleWidth, height);
                    }
                    else 
                    {
                        rectangles[i].setBackground(Color.WHITE);
                        rectangles[i].setSize(rectangleWidth, height);
                        if(stretch[i])
                        {
                            stretch[i] = false;
                            rectangles[i].setLocation(x+1, y - height);
                        }
                        else
                            rectangles[i].setLocation(x, y - height);
                    }
                }
                pastHeights[i] = height;

            }
            frame.setVisible(true);
        } else{
            t.stop();
        }
    }
}
