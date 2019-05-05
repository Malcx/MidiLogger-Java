import java.util.*;
import java.io.*;

public class SimpleMidiRecorder
{


 public static void main(String[] args) {

	Properties props = new Properties();
	try{
  		InputStream propInput = SimpleMidiRecorder.class.getClassLoader().getResourceAsStream("config.props");
		props.load(propInput);
	}
	catch(Exception e){
		System.out.println("Bad or No config.props found.");
	}
			 	
	MidiHandler mh = new MidiHandler(props);
	MidiSaveShutdownHook sh = new MidiSaveShutdownHook(mh);
	Runtime.getRuntime().addShutdownHook(sh);
 	
 	
 	
 	
 	if(args.length>0 && args[0].equals("l"))
 	    mh.listDevices();
	else{
            mh.start();
        }

 	
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = "";

        try{
          while (line.equalsIgnoreCase("stop") == false) {
              line = in.readLine();
          }
        }
        catch(Exception e){}
        System.exit(0); // This will trigger our shutdownHook

 	
 }
  
  

}