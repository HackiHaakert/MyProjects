package com.rasbpi.sample.speechresp;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;

public class GSpeechHearVoice {

	static BuildResponseAndSpeak speakerThread;
	boolean continueHearing = true;

	public GSpeechHearVoice() {

		// Set credentials?
		// GoogleCredentials credentials = GoogleCredentials.create(new
		// AccessToken("AIzaSyCtrBlhBiqNd7kI4BiOn2kWiCYlwp1azVM",Date.valueOf(LocalDate.now())));
		// System.out.print(credentials.getAccessToken());

		// Target data line
		TargetDataLine microphone;
		AudioInputStream audio = null;

		// Check if Microphone is Supported
		checkMicrophoneAvailability();

		// Print available mixers
		// printAvailableMixers();

		// Capture Microphone Audio Data
		try {

			// Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
			AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			// Check if Microphone is Supported
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Microphone is not available");
				System.exit(0);
			}

			// Get the target data line
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);
			microphone.start();

			// Audio Input Stream
			audio = new AudioInputStream(microphone);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "/home/pi/Mirror-91580a468f9c.json");

		// here we need to check the environment for the Auth settings

//		Map<String, String> env = System.getenv();
//		for (String envName : env.keySet()) {
//			System.out.format("%s=%s%n", envName, env.get(envName));
//		}

		// end of code for the environment check

		while (continueHearing) {

			// Send audio from Microphone to Google Servers and return Text
			try (SpeechClient client = SpeechClient.create()) {

				ResponseObserver<StreamingRecognizeResponse> responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {

					public void onStart(StreamController controller) {
						System.out.println("Started....");
						speakerThread.addInput("xxxstartupbegruessung");

					}
					


					public void onResponse(StreamingRecognizeResponse response) {
						System.out.println("Dies ist eine Meldung");
						System.out.println(response.getResults(0));

						String transcriptText = response.getResults(0).getAlternatives(0).getTranscript();
						System.out.println("------------------");
						System.out.println("Ausgabe des Transcripts:");
						System.out.println(transcriptText);
						System.out.println("Lowercase:");
						String lowerCaseText = transcriptText.toLowerCase();
						System.out.println(lowerCaseText);						
						System.out.println("------------------");

						speakerThread.addInput(lowerCaseText);
						if(transcriptText.contains("xxxxxxbeenden")){
							continueHearing = false;
						}
						


					}

					public void onComplete() {
						System.out.println("Complete");
					}

					public void onError(Throwable t) {
						System.err.println(t);
					}
				};

				ClientStream<StreamingRecognizeRequest> clientStream = client.streamingRecognizeCallable()
						.splitCall(responseObserver);

				// "en-US"
				// "de-DE"
				// "fr-FR"

				RecognitionConfig recConfig = RecognitionConfig.newBuilder()
						.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).setLanguageCode("de-DE")
						.setSampleRateHertz(16000).build();

				StreamingRecognitionConfig config = StreamingRecognitionConfig.newBuilder().setConfig(recConfig)
						.build();

				StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder().setStreamingConfig(config)
						.build(); // The first request in a streaming call has to be a config

				clientStream.send(request);

				// Infinity loop from microphone
				while (continueHearing) {
					byte[] data = new byte[10];
					try {
						audio.read(data);
					} catch (IOException e) {
						System.out.println(e);
					}
					request = StreamingRecognizeRequest.newBuilder().setAudioContent(ByteString.copyFrom(data)).build();
					clientStream.send(request);
				}
			} catch (Exception e) {
				System.out.println(e);
			}

		} // end of while
	}

	/**
	 * Checks if the Microphone is available
	 */
	public static void checkMicrophoneAvailability() {
		enumerateMicrophones().forEach((string, info) -> {
			System.out.println("Name :" + string);
		});
	}

	/**
	 * Generates a hashmap to simplify the microphone selection process. The keyset
	 * is the name of the audio device's Mixer The value is the first lineInfo from
	 * that Mixer.
	 * 
	 * @author Aaron Gokaslan (Skylion)
	 * @return The generated hashmap
	 */
	public static HashMap<String, Line.Info> enumerateMicrophones() {
		HashMap<String, Line.Info> out = new HashMap<String, Line.Info>();
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixerInfos) {
			Mixer m = AudioSystem.getMixer(info);
			Line.Info[] lineInfos = m.getTargetLineInfo();
			if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class))// Only adds to
																									// hashmap if it is
																									// audio input
																									// device
				out.put(info.getName(), lineInfos[0]);// Please enjoy my pun
		}
		return out;
	}

	/**
	 * Print available mixers
	 */
	public void printAvailableMixers() {

		// Get available Mixers
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

		// Print available Mixers
		Arrays.asList(mixerInfos).forEach(info -> {
			System.err.println("\n-----------Mixer--------------");

			Mixer mixer = AudioSystem.getMixer(info);

			System.err.println("\nSource Lines");

			// SourceLines
			Arrays.asList(mixer.getSourceLineInfo()).forEach(lineInfo -> {
				// Line Name
				System.out.println(info.getName() + "---" + lineInfo);
				Line line = null;
				try {
					line = mixer.getLine(lineInfo);
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("\t-----" + line);
			});

			System.err.println("\nTarget Lines");
			// TargetLines
			Arrays.asList(mixer.getTargetLineInfo()).forEach(lineInfo -> {

				// Line Name
				System.out.println(mixer + "---" + lineInfo);
				Line line = null;
				try {
					line = mixer.getLine(lineInfo);
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("\t-----" + line);

			});

		});
	}

	public static void main(String[] args) {
		
		/* Kommentar*/ 
		
		InetAddress IP;
		
		
		try {
			IP = null;
			IP = InetAddress.getLocalHost();
			System.out.println(IP.toString());
			
			String os = System.getProperty("os.name");
			System.out.println("OS: " + os);
			
		    
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		speakerThread = new BuildResponseAndSpeak();
		speakerThread.start();

		new GSpeechHearVoice();
	}

}
