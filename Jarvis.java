/*Licence*/
package jarvis;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.model.OutputFormat;
import java.io.*;
import javaFlacEncoder.*;
import java.nio.file.Files;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import javazoom.jl.decoder.*;
import java.io.File;
import java.io.IOException;
/**@author fsociety**/
public class Jarvis {
    public static String output = "";
    public static String ai = "jarvis";
    public static String[] words;
    public static String[][] variations;
    public static String task;
    public static String speak = "Good mornin Boss,this is a test text";
    public static void main(String[] args) throws IOException {
		GSpeechDuplex dup = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");//Instantiate the API
		dup.addResponseListener(new GSpeechResponseListener(){// Adds the listener
			public void onResponse(GoogleResponse gr){
				try{
					String resp = gr.getResponse();
					String[] sent = resp.split(",");
					Integer sent_count = sent.length;
					String[] conf_string = sent[1].split(":");
					Double conf_num = Double.parseDouble(removeLastChar(conf_string[1]));
					if(conf_num>0.84){
						words = sent[0].split(" ");
					}
					else{
						for(int i=1;i<sent_count;i++){
							String current_sent = removeLast2Char(sent[i]);
							current_sent = removeTransScript(current_sent);
							System.out.println(current_sent);
						}
						words = sent[0].split(" ");
					}
					if ( resp.toLowerCase().contains("jarvis".toLowerCase())) {
						speak = "Good mornin Boss,this is a test text";
						if ( resp.toLowerCase().contains("run".toLowerCase())) {
							Integer cmd_num = resp.indexOf("run");
							cmd_num++;
							task = removeLastChar(words[cmd_num]);
						}
					}
					TextToSpeechConvertor ttsc = new TextToSpeechConvertor(Region.getRegion(Regions.EU_WEST_1));
					InputStream speechStream = ttsc.synthesize(speak, OutputFormat.Mp3);
					//create an MP3 player
					AdvancedPlayer player = new AdvancedPlayer(speechStream,javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
					player.setPlayBackListener(new PlaybackListener() {
						@Override
						public void playbackStarted(PlaybackEvent evt) {
							System.out.println("Playback started");
							//System.out.println(resp);
						}	
						@Override
						public void playbackFinished(PlaybackEvent evt) {
							System.out.println("Playback finished");
						}
					});
					// play it!
					player.play();
				}
				catch(IOException | JavaLayerException e){
					System.err.println("Caught IOException: " + e.getMessage());
				}
			}
		});
		Microphone mic = new Microphone(FLACFileWriter.FLAC);//Instantiate microphone and have 
		// it record FLAC file.
		File file = new File("CRAudioTest.flac");//The File to record the buffer to. 
		//You can also create your own buffer using the getTargetDataLine() method.
		while(true){
			try{
				mic.captureAudioToFile(file);//Begins recording
				Thread.sleep(10000);//Records for 10 seconds
				mic.close();//Stops recording
				//Sends 10 second voice recording to Google
				byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());//Saves data into memory.
				dup.recognize(data, (int)mic.getAudioFormat().getSampleRate());
				mic.getAudioFile().delete();//Deletes Buffer file
				//REPEAT
			}
			catch(Exception ex){
				ex.printStackTrace();//Prints an error if something goes wrong.
			}
		}
	}
    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }
    private static String removeLast2Char(String str) {
        return str.substring(0, str.length() - 2);
    }
    private static String removeTransScript(String str){
        return str.substring(15);
    }
}

