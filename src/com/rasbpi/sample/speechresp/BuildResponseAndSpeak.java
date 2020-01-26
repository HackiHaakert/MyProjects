package com.rasbpi.sample.speechresp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;

public class BuildResponseAndSpeak extends Thread {

	List<String> theDialoginput = new ArrayList<String>();
	TextToSpeechClient textToSpeechClient;
	VoiceSelectionParams voice;
	AudioConfig audioConfig;
	String os="";
	String sPlayer = "";
	
	private boolean doStop = false;

    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }
	
	@Override
	public void run() {
		String answer;
		System.out.println("Speaker Thread started!!");
		String os = System.getProperty("os.name");
		System.out.println("OS: " + os);
		
		
		if(os.contains("os x") ) {
			sPlayer = "vlc --play-and-exit "; 			
		} else
		{
			sPlayer = "afplay ";
		}

//			"vlc --play-and-exit " + "./cache/" + fileName + ".mp3"  );
// 			"omxplayer SPoutput.mp3" ); Pi Player 1
//			"afplay " + "./cache/" + fileName + ".mp3" ); // Mac Player


		
		
		
		
		
		
		
		 try {
			 textToSpeechClient = TextToSpeechClient.create();
			 
				// Build the voice request, select the language code ("en-US") and the ssml
				// voice gender
				// ("neutral")
				voice = VoiceSelectionParams.newBuilder()
						.setLanguageCode("de-DE")
						.setName("de-DE-Wavenet-D")
						.setSsmlGender(SsmlVoiceGender.MALE )
						.build();

				// Select the type of audio file you want returned
				audioConfig = AudioConfig.newBuilder()
						.setAudioEncoding(AudioEncoding.MP3 )
						.setSpeakingRate(1.2) // Standard is 1 range 0,25 - 4
						.build();


			 while(keepRunning()) {
				 
				 if(!theDialoginput.isEmpty()) {
				     System.out.println("");
				     System.out.println("----------------------------------------");
				     System.out.println("Processing: " + theDialoginput.get(0));
				     System.out.println("----------------------------------------");
				     
				     
				     answer = buildAnswer(theDialoginput.get(0));
				     if (answer.length()>0) speakAnswer(answer);
				     
				     
				     
				     
				     theDialoginput.remove(0);
				 }
			     Thread.sleep(1000);

			 } // end of while
				 
			 
		
		} catch (Exception e) {

		     System.out.println("Interrupted");

		}

		     System.out.println( " exiting.");

		} // end of run()

	
	public void addInput(String input) {
		theDialoginput.add(input);
	}
	

	public String buildAnswer(String input) {
		
		input = input.toLowerCase();
		
		String answer = "Das habe ich leider nicht verstanden!";
		if ( input.contains("habe ich leider nicht verstanden") ) {
			answer = "";
		}

		if ( input.contains("xxxstartupbegruessung") ) {
			answer = "hallo ich bin bereit";
		}
		if ( input.contains("hallo ich bin bereit") ) {
			answer = "";
		}
		
		if ( input.contains("sag mal hallo uschi") ) {
			answer = "Hallo liebe Uschi!";
		}
		if ( input.contains("hallo liebe uschi") ) {
			answer = "";
		}
				
		
		if ( input.contains("spieglein spieglein an der wand wer ist die schönste im ganzen land") ) {
			answer = "das bin natürlich ich";
		}
		if ( input.contains("das bin natürlich ich") ) {
			answer = "";
		}
		
		if ( input.contains("was kannst du") ) {
			answer = "zuhören sprechen auskünfte geben";
		}
		if ( input.contains("zuhören sprechen auskünfte geben") ) {
			answer = "";
		}

		
		if ( input.contains("wer bist du") ) {
			answer = "ich bin spieglein der zauberspiegel";
		}
		if ( input.contains("ich bin spieglein der zauberspiegel") ) {
			answer = "";
		}

		
		if ( input.contains("was machst du den ganzen tag") ) {
			answer = "sprachen lernen damit ich demnächst übersetzen kann";
		}
		if ( input.contains("sprachen lernen damit ich demnächst übersetzen kann") ) {
			answer = "";
		}

		if ( input.contains("bist du eine alexa") ) {
			answer = "nein ich bin ein verwunschener zauberer";
		}
		if ( input.contains("nein ich bin ein verwunschener zauberer") ) {
			answer = "";
		}

		if ( input.contains("wo wohnst du") ) {
			answer = "seit ich verzaubert wurde wohne ich in diesem spiegel";
		}
		if ( input.contains("seit ich verzaubert wurde wohne ich in diesem spiegel") ) {
			answer = "";
		}
		
		if ( input.contains("wer hat dich gebaut") ) {
			answer = "hacki hat mich gebaut";
		}
		if ( input.contains("hacki hat mich gebaut") ) {
			answer = "";
		}
		
		if ( input.contains("worin bist du programmiert") ) {
			answer = "natürlich in java";
		}
		if ( input.contains("natürlich in java") ) {
			answer = "";
		}
		
		if ( input.contains("was trinkst du") ) {
			answer = "bier";
		}
		if ( input.contains("bier") ) {
			answer = "";
		}
		
		if ( input.contains("wie alt bist du") ) {
			answer = "ich wurde 2018 gebaut";
		}
		if ( input.contains("ich wurde 2018 gebaut") ) {
			answer = "";
		}
		
		if ( input.contains("wie funktionierst du") ) {
			answer = "ich nutze google spracherkennung und sprach synthetisierung in der cloud";
		}
		if ( input.contains("ich nutze google spracherkennung und sprach synthetisierung in der cloud") ) {
			answer = "";
		}
		
		
		if ( input.contains("spieglein bist du da") ) {
			answer = "ich warte auf deine fragen";
		}
		if ( input.contains("ich warte auf deine fragen") ) {
			answer = "";
		}
		
		if ( input.contains("darf ich nina ärgern") ) {
			answer = "ja gerne";
		}
		if ( input.contains("ja gerne") ) {
			answer = "";
		}
		
		return (answer);
	} // end of build answer
	
	

	public void speakAnswer (String answer) {
		// Instantiates a client
		try {
			String fileName = answer.replace(" ", "");
			
			
			File file = new File("/Users/al/.bash_history");
		    if (file.exists() && file.isFile())
		    {
		      System.out.println("file exists, and it is a file");
		    } else {
				
				// Set the text input to be synthesized
				SynthesisInput input = SynthesisInput.newBuilder().setText(answer).build();

				
				// Perform the text-to-speech request on the text input with the selected voice parameters and
			      // audio file type
			      SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
			          audioConfig);
			      
			      OutputStream out = new FileOutputStream("./cache/" + fileName + ".mp3");
			      response.writeTo(out);
		    	
		    }
				
				//Run a bat file
				Process pr2ocess = Runtime.getRuntime().exec(
						sPlayer + "./cache/" + fileName + ".mp3"  );

		} // end of try
 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
	} // end of speak answer
	

} // end of class 





/*********************************+
 * 
 * 
 * 
Planung der Konversation des Spiegels

Wer ist die schönste im ganzen Land?
Normalantwort: Das bin natürlich ich.
Spaßantwort:

Wer bist du?
N: Ich bin der Geist des Spiegels
S:

Was kannst du?
N: Zuhören, Sprechen, Auskünfte geben.
S:

Was machst du den ganzen Tag?
N: Ich lerne gerade sprachen, damit ich demnächst übersetzen kann.
S:

Wie ernst bist Du?
N: In meinem Programm ist eine Ernsthaftigkeit von 80% hinterlegt.
S: Das möchtest Du nicht ausprobieren.

Bist du eine Alexa?
N: Nein ich bin ein verwunschener Zauberer.
S: Nein ich schicke deine privaten Gespräche nicht an Amazon sondern an Google.

Wo wohnst Du?
N: In Hennef
S: Im Schlafzimmer der bösen Hexe.

Wer hat Dich geschaffen? 
N: Hacki hat mich gebaut.
S: Die böse Hexe hat mich verwunschen und in diesen Spiegel gesperrt.

Woraus besteht dein Gehirn? 
N: Aus einem Raspberry Pi 3B+

Was weist Du?
N: Alles was es im Internet so gibt.
S: Als Zauberer weis ich alles.

Wieviel Uhr haben wir?
N: Uhrzeit

Worin bist Du Programmiert?
N: Natürlich in Java.

Wie alt bist Du?
N: Ich bin 2018 gebaut worden.
S: Weiß ich nicht genau. Seit ca500 Jahren bin ich in diesem Spiegel gefangen.

Was ist xxxxxxxx?
Wer ist?
N: nachschlagen in Wikipedia
S: Mhmm es gibt also doch dumme Fragen.

Was trinkst Du?
N: Bier
S: Eigentlich alles und dazu Bier.




 * 
 * 
 */


