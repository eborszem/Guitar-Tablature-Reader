let synth;
let pianoPart;

function play() {
  // Initialize the synth
  synth = new Tone.PolySynth(Tone.Synth).toDestination();

  // Define note values
  const WHOLE_NOTE = 4;
  const HALF_NOTE = 2;
  const QUARTER_NOTE = 1;
  const EIGHTH_NOTE = .5;
  const SIXTEENTH_NOTE = .25;

  // Now there is a way to convert from fretboard[string][fret] to a specific chord and pitch
  // May pad the fretboard with an empty string to make it 1-indexed
  const FRETBOARD = [
    ["E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5"],
    ["B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4"],
    ["G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4"],
    ["D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4"],
    ["A2", "A#2", "B2", "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3"],
    ["E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3", "C#3", "D3", "D#3"],
  ]

  const C_chord = [FRETBOARD[5][8], FRETBOARD[4][10], FRETBOARD[3][10], FRETBOARD[2][9], FRETBOARD[1][8], FRETBOARD[0][8]]; // C major
  const Dm_chord = [FRETBOARD[4][5], FRETBOARD[3][7], FRETBOARD[2][7], FRETBOARD[1][6], FRETBOARD[0][5]]; // D minor
  const Em_chord = [FRETBOARD[4][7], FRETBOARD[3][9], FRETBOARD[2][9], FRETBOARD[1][8], FRETBOARD[0][7]]; // E minor
  const F_chord = [FRETBOARD[5][1], FRETBOARD[4][3], FRETBOARD[3][3], FRETBOARD[2][2], FRETBOARD[1][1], FRETBOARD[0][1]]; // F major
  const G_chord = [FRETBOARD[5][3], FRETBOARD[4][5], FRETBOARD[3][5], FRETBOARD[2][4], FRETBOARD[1][3], FRETBOARD[0][3]]; // G major
  const Am_chord = [FRETBOARD[5][5], FRETBOARD[4][7], FRETBOARD[3][7], FRETBOARD[2][5], FRETBOARD[1][5], FRETBOARD[0][5]]; // A minor
  const Bdim_chord = [FRETBOARD[4][2], FRETBOARD[3][3], FRETBOARD[2][4], FRETBOARD[1][3]]; // B diminished
  
  // Define the chord pattern and corresponding indices
  const pattern = [
      { chord: C_chord, duration: QUARTER_NOTE },
      { chord: F_chord, duration: EIGHTH_NOTE },
      { chord: F_chord, duration: EIGHTH_NOTE },
      { chord: G_chord, duration: QUARTER_NOTE },
      { chord: F_chord, duration: QUARTER_NOTE },

      { chord: Dm_chord, duration: EIGHTH_NOTE },
      { chord: Dm_chord, duration: EIGHTH_NOTE },
      { chord: C_chord, duration: QUARTER_NOTE },
      { chord: Dm_chord, duration: EIGHTH_NOTE },
      { chord: Dm_chord, duration: EIGHTH_NOTE },
      { chord: Dm_chord, duration: EIGHTH_NOTE },
      { chord: Dm_chord, duration: EIGHTH_NOTE },

      { chord: Bdim_chord, duration: HALF_NOTE },

      { chord: C_chord, duration: HALF_NOTE },

      { chord: Dm_chord, duration: HALF_NOTE },

      { chord: Em_chord, duration: HALF_NOTE },
  ];
  console.log(pattern)

  // An array which will be passed as a parameter
  const array = [];

  // Loop through pattern and populate array
  let t = 0;
  for (let i = 0; i < pattern.length; i++) {
    let dur = pattern[i].duration;
    let str = getDurationAsString(dur);
    array.push({
      time: `0:${t}`, 
      chord: pattern[i].chord,
      duration: str
    });
    t += (dur * 1.5)
  }

  function getDurationAsString(dur) {
    switch (dur) {
      case 0.125: return "32n";
      case 0.25: return "16n";
      case 0.5: return "8n";
      case 1: return "4n";
      case 2: return "2n";
      case 4: return "1n";
      default: return "4n"; // default to quarter note
    }
  }
  console.log("final array");
  console.log(array);
  
  pianoPart = new Tone.Part(function(time, val) {
    synth.triggerAttackRelease(val.chord, val.duration, time);  // 4n means play for quarter note duration. 2n -> half, 1n -> whole, etc
  }, array);

  /*pianoPart.loop = false;
  pianoPart.loopEnd = "3m";*/
}
document.addEventListener("DOMContentLoaded", () => {
  play();
  const playBtn = document.getElementById("play-btn");
  playBtn.addEventListener("click", async () => {
    await Tone.start();  // make sure audio is started
    Tone.Transport.start(); // start transport
    pianoPart.start(); // start part
  });
})