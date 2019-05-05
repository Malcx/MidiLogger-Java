# MidiLogger-Java

## Description

A small Java Application to get input from an external MIDI controller (In my case a Roland PC-200 Mk2).

The input is converted and played using the default MIDI output, and is also 

I developed this as an aid to learning the piano - all existing software was way more complicated than I needed. I just wanted to press a key on the keyboard and hear a note through my headphones.

As an added extra, this saves all input as .midi files so there is a complete record of my learning / practicing.

## Usage
This has only been tested on Windows 10 using a MIDI-USB input cable from a Roland midi controller keyboard. 
The code has no dependencies outside of Java, so should work as a good jumping off point for other developers.


1. To list your midi devices, from a command prompt run _java SimpleMidiRecorder l_
2. Edit config.props in a text editor
3. On Windows 10, simply run the _Midi.bat_ file.
4. To close the program without saving, close the java window via the x
5. Ctrl-c or type stop <Enter> to save your midi file and exit.

