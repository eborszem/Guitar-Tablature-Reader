// an array storing real time (tone js: 0:0, 0:1, etc.), notes arr, and duration (4n, 2n, 1n, etc.) of a chord
const array = [];
let synth = new Tone.PolySynth(Tone.Synth, {
    volume: -44 + (50 * 60) / 100, // initially volume control is 50/100
    type: "sine",
}).toDestination();


synth.maxPolyphony = 1000; // prevents chords being skipped when composition is played fast
Tone.Transport.bpm.value = 120;
// Define note values
const WHOLE_NOTE = 4;
const HALF_NOTE = 2;
const QUARTER_NOTE = 1;
const EIGHTH_NOTE = .5;
const SIXTEENTH_NOTE = .25;

document.addEventListener("DOMContentLoaded", () => {
    /* VOLUME CONTROL */
    // volume adjusting via volume bar
    const volumeSlider = document.getElementById('volume');
    const volumeValue = document.getElementById('volumeValue');
    // either load volume or set to default (50) 
    const savedVolume = localStorage.getItem('volume') || 50;
    volumeSlider.value = savedVolume;
    volumeValue.textContent = savedVolume;
    synth.volume.value = -44 + (savedVolume * 60) / 100;
    
    // update volume value when volume changes
    volumeSlider.addEventListener('input', (event) => {
        let curVolume = event.target.value;
        volumeValue.textContent = curVolume;
        // set volume to the slider value
        // synth.volume.value = Tone.gainToDb(curVolume / 100);
        if (curVolume == 0) {
            synth.volume.value = -100;
        } else {
            synth.volume.value = -44 + (curVolume * 60) / 100;
        }
        localStorage.setItem('volume', curVolume); // save volume
        // console.log("current volume is "+curVolume);
    });

    /* BPM (beats per minute) CONTROL */
    const bpmSlider = document.getElementById('bpm');
    const bpmValue = document.getElementById('bpmValue');
    // either load bpm or set to default (120) 
    const savedBpm = localStorage.getItem('bpm') || 120;
    bpmSlider.value = savedBpm;
    bpmValue.textContent = savedBpm;
    Tone.Transport.bpm.value = savedBpm;
    
    // update bpm value when bpm changes
    bpmSlider.addEventListener('input', (event) => {
        let curBpm = event.target.value;
        bpmValue.textContent = curBpm;
        Tone.Transport.bpm.value = curBpm;
        localStorage.setItem('bpm', curBpm); // save bpm
    });
    

    /* PLAY AND PAUSE BUTTONS */
    const playBtn = document.getElementById("play-btn");
    let currentTime = 0;
    let isPaused = false;
    playBtn.addEventListener("click", async () => {
        await Tone.start(); // make sure audio is started
        Tone.Transport.stop();
        pianoPart.stop(); // stop audio if already started
        isPaused = false;
        currentTime = 0;
        pianoPart.start(0); // start pianoPart at time 0
        Tone.Transport.start(); 
        pauseBtn.textContent = "Pause";
    });

    const pauseBtn = document.getElementById("pause-btn");
    pauseBtn.addEventListener("click", async () => {
        if (isPaused) { // means we have clicked the button to resume
            //cycleChordBoxes();
            isPaused = false;
            Tone.Transport.start();
            pianoPart.start("+0.1", currentTime); // start playing again from the paused time
            pauseBtn.textContent = "Pause";
        } else { // means we have clicked the button to pause
            isPaused = true;
            currentTime = Tone.Transport.seconds;
            Tone.Transport.pause();
            pianoPart.stop();
            pauseBtn.textContent = "Resume";
        }
    });

    /* This code executes upon page being loaded. Sets up an array containing chords    */   
    /* that will be played as well as durations.                                        */    
    $(document).ready(function() {
        $.ajax({
            url: '/play',
            method: 'GET',
            success: function(chords) {
                // This function takes in an arraylist of arraylists of integer arrays. The outer arraylist represents the overall composition. The inner array list represents the measures (of which the composition is comprised of), which may contain multiple chords. A chord is represented as a six integer array. Each integer represents one of the six notes playable at once on a guitar (as it has six strings). 
                play(chords);
            },
            error: function(error) {
                console.log('Error:', error);
            }
        });

        function play(chords) {
            console.log("chords="+chords);
            // synth = new Tone.PolySynth(Tone.Synth, {
            //     volume: -30 + (50 * 60) / 100, // initially volume control is 50/100
            // }).toDestination();
            // const filter = new Tone.Filter(1000, "lowpass").toDestination();
            // synth.connect(filter);
            // synth.maxPolyphony = 1000; // prevents chords being skipped when composition is played fast
    
            // Now there is a way to convert from fretboard[string][fret] to a specific chord and pitch
            // May pad the fretboard with an empty string to make it 1-indexed
            const FRETBOARD = [
                ["E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5"],
                ["B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5"],
                ["G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5"],
                ["D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4"],
                ["A2", "A#2", "B2", "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4"],
                ["E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3", "C#3", "D3", "D#3",  "E3", "F3", "F#3", "G3", "G#3", "A3"],
            ];

            // An array which will be passed as a parameter
            //const array = [];

            let t = 0;
            for (let i = 0; i < chords.length; i++) {
                let chordData = chords[i];
                console.log("TEST--chords[0]=");
                console.log(chords[0]);
                let dur = chordData[0][2];  // first note dur = every note dur
                if (dur == 8) {
                    dur = EIGHTH_NOTE;
                }
                if (dur == 16) {
                    dur = SIXTEENTH_NOTE;
                }
                let str = getDurationString(dur);
                /*------------------------------------*/

                //let chordArray = getChord(chordData);
                
                let chordArray = [];
                console.log("len="+chords[i].length);
                console.log("len="+chordData.length);
                for (let i = 0; i < chordData.length; i++) {
                    let string = chordData[i][1];
                    let fret = chordData[i][0];
                    console.log("str=" + string + ", fret=" + fret);

                    chordArray.push(FRETBOARD[string][fret]);  // STRINGS ARE ZERO BASED
                }
                console.log("chord array=" + chordArray)
                
                array.push({
                    time: `0:${t}`, 
                    chord: chordArray,
                    duration: str
                });
                t += (dur);                                     // TODO: MULTIPLIER HERE DETERMINES TEMPO
                // time in tone js:
                // default tempo = 120 beats per minute
                // so, t / 2 = the current second in the song
            }

            /*function getChord(notes) {
                console.log("notes..."+notes)
                let chordArray = [];
                for (let i = 0; i < notes.length; i++) {
                    let string = notes[i][1];
                    let fret = notes[i][0];
                    chordArray.push(FRETBOARD[string][fret]);
                }
                return chordArray;
            }*/

            function getDurationString(dur) {
                switch (dur) {
                    case SIXTEENTH_NOTE: return "16n";
                    case EIGHTH_NOTE: return "8n";
                    case QUARTER_NOTE: return "4n";
                    case HALF_NOTE: return "2n";
                    case WHOLE_NOTE: return "1n";
                    default: return "4n"; // default to quarter note
                }
            }
            
            pianoPart = new Tone.Part(function(time, val) {
                synth.triggerAttackRelease(val.chord, val.duration, time);  // 4n means play for quarter note duration. 2n -> half, 1n -> whole, etc
            }, array);

            let j = 1;
            for (let i = 0; i < array.length; i++) {
                if (i % 4 == 0) {
                    console.log("new measure " + j);
                    j++;
                }
                console.log(array[i]);
            }
        }
    });
    /*------------------------------------------------------------------------------------
    // moving beam
    let curIndex = 0;
    const chordBoxes = document.querySelectorAll('.chord-box');
    function highlightChordBox(index) {
        chordBoxes.forEach((box, i) => {
            box.classList.toggle("active", i == index);
            // debugging info
            if (i == index) {
                console.log(i + ", " + array[i]);
            }
        });
    }

    function durToInt(d) {
        switch (d) {
            case "16n": return .25;
            case "8n": return .5;
            case "4n": return 1;
            case "2n": return 2;
            case "1n": return 4;
            default: return 1; // default to quarter note
        }
    }
    let isPaused = false; // pause button (as opposed to resume button) is disabled
    
    function cycleChordBoxes() {
        if (curIndex >= array.length)
            return;
        if (isPaused)
            return;
        const durInteger = durToInt(array[curIndex].duration);
        highlightChordBox(curIndex);
        console.log("dur=" + durInteger + ", " + array[curIndex].time);
        setTimeout(() => {
            cycleChordBoxes();
        }, durInteger * 1000 / 2); // convert to milliseconds
        // currently must divide by 2 to accommodate for 120 beats per minute == 1 beat every 2 seconds
        // without halving it, a quarter note for example would be one second (two beats) instead of 0.5 seconds (one beat)
        curIndex++;
    }*/
    // end moving beam


});
