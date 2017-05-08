import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFile {
	private File audioFile = null;
	private AudioInputStream audioInputStream = null;
	private ArrayList<Complex[]> complexDataInput = null;
	private Complex[] complexData = null;
	private int frameLength = 0;
	private int frameSize = 0;
	private int frameRate = 0;
	private int numChannels = 0;
	private int audioDuration = 0;
	private int bandNumber = 512;
	private int index = 0;
	private int amountOfData = 0;
	private int buffer = 8;
	private String filePath;
	private ArrayList<double[]> averageDataArray;
	private AudioPlayer player;
	
	public AudioFile(String path){
		//reads in the audio file
		try{
			filePath = path;
			audioFile = new File(filePath);
			audioInputStream = AudioSystem.getAudioInputStream(audioFile);
			findAudioInfo();
		} catch (UnsupportedAudioFileException e){
		} catch (IOException e){
		} 
	}

	private void findAudioInfo(){
		//get and define basic audio properties
		frameLength = (int) audioInputStream.getFrameLength();
		frameSize = (int) audioInputStream.getFormat().getFrameSize();
		frameRate = (int) audioInputStream.getFormat().getFrameRate();
		audioDuration = (int) frameLength / frameRate;
		
		//read in the audio represented by bytes
		byte[] byteData = new byte[frameLength * frameSize];
		try{
			audioInputStream.read(byteData);
		} catch (Exception e){
			System.out.println("Exception : " + e.getMessage());
		}
		numChannels = audioInputStream.getFormat().getChannels();

		//get the amplitude from the audio byte data
		double[][] data = new double[numChannels][frameLength];
		int index = 0;
		for (int i = 0; i < this.frameLength; i ++){
			for (int j = 0; j < this.numChannels; j++){
				data[j][i] = (double) (byteData[index + 1] << 8 | byteData[index] & 0xFF);
				index += 2;
				data[j][i] = (data[j][i]>=32768) ? data[j][i]-65536 : data[j][i];
				data[j][i] /= 32768.0;
			}
		}

		//turn multichannel audio to monochannel audio
		double[] monoData = new double[frameLength];
		for (int i = 0; i < this.frameLength; i++){
			monoData[i] = data[0][i] + data[1][i];
		}

		//turn double to complex
		complexData = new Complex[frameLength];
		for (int i = 0; i < complexData.length; i++) {
            complexData[i] = new Complex(monoData[i], 0);
        }

		//separate data for input
		complexDataInput = new ArrayList<Complex[]>();
		for (int i = 0; i < complexData.length / bandNumber; i++){
			Complex[] tempArray = new Complex[bandNumber];
			for (int j = 0; j < bandNumber; j++){
				tempArray[j] = complexData[(i * bandNumber) + j];
			}
			complexDataInput.add(tempArray);
		}
		if (complexData.length != (complexDataInput.size() * bandNumber)){
			Complex[] tempArray = new Complex[bandNumber];
			int position =  bandNumber - complexData.length - (complexDataInput.size() * bandNumber);
			int count = 0;
			for (int i = 0; i < position; i++){
				count++;
				tempArray[i] = complexData[(complexDataInput.size() * bandNumber) + i];
			}
			if (count < bandNumber){
				for (int k = count; k < bandNumber; k++){
					tempArray[k] = new Complex(0, 0);
				}
			}
			complexDataInput.add(tempArray);
		}

		amountOfData = complexDataInput.size();
	}

	public int getBandNumber() { return bandNumber; }

	public int getAudioDuration() { return audioDuration; }

	public int getNumChannels() { return numChannels; }

	public int getFrameRate() { return frameRate; }

	public int getFrameSize() { return frameSize; }

	public int getFrameLength() { return frameLength; }

	public int getAmountOfData() { return amountOfData; }

	public int getBuffer() { return buffer; }

	//returns next set of frequency data
	public double[] getNext(int displayBands){
		ArrayList<Complex[]> fftComplexArrayList = new ArrayList<Complex[]>();
		for (int i = 0; i < buffer; i++){
			fftComplexArrayList.add(FFT.fft(complexDataInput.get(index++)));
		}

		double[][] realData = new double[buffer][bandNumber];
		for (int i = 0; i < buffer; i++){
			for (int j = 0; j < displayBands; j++){ 
				realData[i][j] = (Math.pow(fftComplexArrayList.get(i)[j].re(), 2)
						                          + Math.pow(fftComplexArrayList.get(i)[j].im(), 2));
			}
		}
		double[] averageData = new double[bandNumber];
		for (int i = 0; i < displayBands; i++){ 
			for (int j = 0; j < buffer; j++){
				averageData[i] += realData[j][i];
			}
			averageData[i] /= displayBands; 
			averageData[i] = Math.log10(averageData[i]);
			averageData[i] = (averageData[i]<0.0) ? 0 : 300*(averageData[i]-0);
		}
		return averageData;
	}

	//returns true if there is still data
	public boolean hasNext(){
		if (index < complexDataInput.size()){
			return true;
		}
		return false;
	}

	//resets current data index to the start
	public void reset(){
		index = 0;
	}

	public void playSong(){
		player = new AudioPlayer(filePath);
		player.start();
	}

	public void stopSong(){
		player.stop();
	}
}
