package com.speechmodule.SpeechRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.sound.sampled.AudioSystem;

import javax.sound.sampled.Port;

import org.apache.log4j.Logger;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.recognizer.Recognizer;

/**
 * Hello world!
 * 
 */
public class App {
	private final static Logger LOG = Logger.getLogger(App.class);
	private static LiveSpeechRecognizer recognizer;
	private static String result;
	static Thread speechThread;
	static Thread resourceThread;

	public static void main(String[] args) throws IOException {
		LOG.info("Loading JARVIS \n");
		Configuration configuration = new Configuration();

		configuration
				.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration
				.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		/*configuration
				.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");*/
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar");
		configuration.setUseGrammar(true);
		try {
			recognizer = new LiveSpeechRecognizer(configuration);

		} catch (IOException ex) {
			LOG.error("ERROR : Could not intialize the Speechrecognizer");
		}
		recognizer.startRecognition(true);
		startSpeechThread();
		startResourceThread();
	}

	protected static void startSpeechThread() {
		speechThread = new Thread(
				() -> {
					LOG.info("You can start to speak");
					try {
						while (true) {
							SpeechResult speechresult = recognizer.getResult();
							if (speechresult != null) {
								result = speechresult.getHypothesis();
								System.out.println("You said : " + " " + result
										+ "\n");
							} else {
								LOG.info("I cant understand what you said");
							}
						}
					} catch (Exception ex) {
						LOG.warn(ex);
					}

				});
		speechThread.start();
	}

	private static void startResourceThread() {
		if (resourceThread != null && resourceThread.isAlive())
			return;
		resourceThread = new Thread(() -> {
			try {
				while (true) {
					if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
						//LOG.info("Microphone is enabled");
					} else {
						LOG.info("Microphone is not enabled");
					}
					Thread.sleep(350);
				}

			} catch (InterruptedException ex) {
				LOG.warn(ex);
				resourceThread.interrupt();
			}

		});
     resourceThread.start();
	}
}
