import javax.sound.midi.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.*;


public class MidiHandler
{


	public MidiDevice device;
	public String MidiID = "";
	private Synthesizer midiSynth = null;

	private MidiInputReceiver receiver = null;


	private Sequence seq = null; 
	private Track track = null;
	private Properties props = null;
	public boolean alreadyExiting = false;

	public MidiHandler(Properties propsIn)
	{
		this.props = propsIn;
	}
	
	
	
	public void listDevices(){
		System.out.println("In and out MIDI devices on this machine");
		System.out.println("They may have one or more spaces at the end of their name");
		
		
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			try {
				device = MidiSystem.getMidiDevice(infos[i]);
				System.out.println("[" + infos[i] + "]");
			}
			catch (MidiUnavailableException e) {
			}
		} 
	}
	
	
	public void stopListening(){
		this.alreadyExiting = true;
		try{
			receiver.addWhiteSpace();
			SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd_HHmmss");  
			Date date = new Date(System.currentTimeMillis());  
			File f = new File(this.props.getProperty("outputPath") + "/" + formatter.format(date) + ".midi");
			MidiSystem.write(this.seq,1,f);
		}
		catch(Exception e)
		{
			System.out.println("Exception caught " + e.toString());
		}
	

	}
	
	
	
	//https://stackoverflow.com/questions/6937760/java-getting-input-from-midi-keyboard
	public void start(){
	
		System.out.println("---");
		System.out.println("Listening to device: " + this.props.getProperty("inputDevice"));
		System.out.println("Listening on Midi Channel: " + this.props.getProperty("channelNumber"));
		System.out.println("Instrument ID: " + this.props.getProperty("instrumentNumber"));
		System.out.println("---");
		System.out.println("To list available devices run \"java SimpleMidiRecorder l\" ");
		System.out.println("To exit and save, type \"stop\" or press Ctrl-C ");
		System.out.println("...");


		MidiChannel[] mChannels = null;
		int instrumentNumber = Integer.parseInt(this.props.getProperty("instrumentNumber"));
		int channelNumber = Integer.parseInt(this.props.getProperty("channelNumber"));
		try{
			//timeCode = System.currentTimeMillis();
			
			this.midiSynth = MidiSystem.getSynthesizer(); 
			this.midiSynth.open();
			
			//get and load default instrument and channel lists
		        Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
        		mChannels = midiSynth.getChannels();
        		mChannels[ channelNumber ].programChange( instrumentNumber ); 


			
			//****  Create a new MIDI sequence with 24 ticks per beat  ****
			seq = new Sequence(javax.sound.midi.Sequence.PPQ,24);
			
			//****  Obtain a MIDI track from the sequence  ****
			track = seq.createTrack();
			
			//****  General MIDI sysex -- turn on General MIDI sound set  ****
			byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
			SysexMessage sm = new SysexMessage();
			sm.setMessage(b, 6);
			this.track.add(new MidiEvent(sm,(long)0));
			
			//****  set tempo (meta event)  ****
			MetaMessage mt = new MetaMessage();
		        byte[] bt = {0x02, (byte)0x00, 0x00};
			mt.setMessage(0x51 ,bt, 3);
			this.track.add(new MidiEvent(mt,(long)0));
			
			//****  set track name (meta event)  ****
			mt = new MetaMessage();
			String TrackName = new String("midifile track");
			mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
			this.track.add(new MidiEvent(mt,(long)0));
			
			//****  set omni on  ****
			ShortMessage mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7D,0x00);
			this.track.add(new MidiEvent(mm,(long)0));
			
			//****  set poly on  ****
			mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7F,0x00);
			this.track.add(new MidiEvent(mm,(long)0));
		
			//****  set instrument  ****
			mm = new ShortMessage();
			mm.setMessage(0xC0, instrumentNumber, 0x00);
			this.track.add(new MidiEvent(mm,(long)0));
			
			        
		}
		catch(Exception e){
		}
		
		
		String midiInterfaceID = this.props.getProperty("inputDevice");
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			try {
				this.device = MidiSystem.getMidiDevice(infos[i]);
				if(!infos[i].toString().equals(midiInterfaceID)) // only get the one we want based on name
					continue;
			
				Transmitter trans = this.device.getTransmitter();
				receiver = new MidiInputReceiver(this.device.getDeviceInfo().toString(), mChannels, this.seq, this.track, this.props);
				trans.setReceiver(receiver);

			
				//if code gets this far without throwing an exception we are good - let's start listening
				this.device.open();
				return;


			} catch (MidiUnavailableException e) {
				System.out.println(e);
			}
		}
	}





public class MidiInputReceiver implements Receiver {
	public String name;
	public MidiChannel[] mChannels = null;
	public Sequence seq = null;
	public Track track = null;
	private long timeCode = -1;
	
	private Properties props = null;

	public MidiInputReceiver(String name, MidiChannel[] chan, Sequence sq, Track tr, Properties pr) {
		this.props = pr;
		this.name = name;
		this.mChannels = chan;
		this.seq = sq;
		this.track = tr;
		this.timeCode = -1;
	}
	
	
	public void addWhiteSpace(){
		try{
			int when = (int)((System.currentTimeMillis() + 1000 - this.timeCode) / 24 * 4);
			MidiEvent me = new MidiEvent(new ShortMessage(0x90,0,1),(long)when);
			track.add(me);

 			when = (int)((System.currentTimeMillis() + 1100 - this.timeCode) / 24 * 4);
 			me = new MidiEvent(new ShortMessage(0x80,0,1),(long)when);
			track.add(me);
		}
		catch(Exception e)
		{}	
	}
	
	public void send(MidiMessage msg, long timeStamp) {
		
	
		if(((ShortMessage)msg).getChannel() == Integer.parseInt(this.props.getProperty("channelNumber"))){
			if(this.timeCode<0)
				this.timeCode = System.currentTimeMillis();
				
			int when = (int)((System.currentTimeMillis() - this.timeCode) / 24 * 4);
	
				System.out.println(((ShortMessage)msg).getData1());

			if(((ShortMessage)msg).getData2() == 0){
				mChannels[0].noteOff( ((ShortMessage)msg).getData1());
				MidiEvent me = new MidiEvent(((ShortMessage)msg),(long)when);
				track.add(me);
			}
			//else if( (int) ((ShortMessage)msg).getData1() == 123 || (int) ((ShortMessage)msg).getData1() == 86)
			//{
			// do nothing
			// Sometimes on a Roland PC-200 MK2 it send the "stop all notes" message every half second or so.
			//}
			else{
			
				int vel = (int)(((ShortMessage)msg).getData2() * Double.parseDouble(this.props.getProperty("velocityGain")));
				mChannels[0].noteOn( ((ShortMessage)msg).getData1(), vel);
				
				
				try{
					((ShortMessage)msg).setMessage( ((ShortMessage)msg).getStatus(), ((ShortMessage)msg).getData1(), vel );
					MidiEvent me = new MidiEvent(((ShortMessage)msg),(long)when);
					track.add(me);
				}
				catch(Exception e){}
			}

		}		
	}
	public void close() {}
	}
}