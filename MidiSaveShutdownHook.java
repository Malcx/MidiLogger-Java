
public class MidiSaveShutdownHook extends Thread {
	MidiHandler mh = null; // pointer to the parent class so we can save everything

	public MidiSaveShutdownHook(MidiHandler MidiH){
		this.mh = MidiH;
	}
	
    public void run() {
    	if(!this.mh.alreadyExiting)
	    	this.mh.stopListening();
    }
}

