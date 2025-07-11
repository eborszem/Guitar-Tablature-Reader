import Soundfont from 'https://esm.sh/soundfont-player';
// Define note values
const WHOLE_NOTE = 4;
const HALF_NOTE = 2;
const QUARTER_NOTE = 1;
const EIGHTH_NOTE = 8;
const SIXTEENTH_NOTE = 16;

document.addEventListener("DOMContentLoaded", async () => {
    window.ac = new AudioContext();
    let gainNode = ac.createGain();
    gainNode.connect(ac.destination);
    let player = await Soundfont.instrument(ac, 'acoustic_guitar_nylon', { destination: gainNode });

    /* BPM (beats per minute) CONTROL */

    let curBpm = localStorage.getItem('bpm') || 120;
    const bpmSlider = document.getElementById('bpm');
    // update bpm value when bpm changes
    // also stop the song playing 
    // bpmSlider.addEventListener('blur', async (event) => {
    //     let val = parseInt(bpmSlider.value, 10);
    //     if (val < 50) {
    //         bpmSlider.value = 50
    //     } else if (val > 300) {
    //         bpmSlider.value = 300;
    //     }
    //     curBpm = bpmSlider.value;
    //     localStorage.setItem('bpm', curBpm); // save bpm
    //     restart();
    // });

    bpmSlider.addEventListener('change', async (event) => {
        let bpmSliderInt = parseInt(bpmSlider.value, 10);
        if (isNaN(bpmSliderInt)) {
            bpmSlider.value = 120;
            bpmSliderInt = 120; // default for invalid input
        }
        if (bpmSliderInt < 50) {
            bpmSlider.value = 50;
            bpmSliderInt = 50;
        } else if (bpmSliderInt > 300) {
            bpmSlider.value = 300;
            bpmSliderInt = 300;
        }
        curBpm = bpmSliderInt;
        localStorage.setItem('bpm', curBpm); // save bpm
        restart();
    });

    /* VOLUME CONTROL */

    let curVolume = localStorage.getItem('volume') || 50;
    gainNode.gain.value = curVolume / 100;
    const volumeSlider = document.getElementById('volume');
    const volumeValue = document.getElementById('volumeValue');
    const savedVolume = localStorage.getItem('volume') || 50;
    volumeSlider.value = savedVolume;
    volumeValue.textContent = savedVolume;
    // update volume value when volume changes
    volumeSlider.addEventListener('input', async (event) => {
        let curVolume = event.target.value;
        volumeValue.textContent = curVolume;
        gainNode.gain.value = curVolume / 100;
        localStorage.setItem('volume', curVolume);
        console.log("volume="+curVolume);
        console.log("gainNode.gain.value="+gainNode.gain.value)
        array = await loadChordsToPlay(); // update array with new volume
    });

    let array = await loadChordsToPlay(); // init the chord array which contains chords/notes and their durations and times to play
    loadChordsToPlay();
    function loadChordsToPlay() {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: '/play',
                method: 'GET',
                success: function(chords) {
                    // This function takes in an arraylist of arraylists of Note objects. 
                    // The outer arraylist represents the overall composition with every chord. 
                    // The inner array list represents the chords themselves (of which the composition is comprised of).
                    // A chord is represented as a six integer arraylist. 
                    // Each integer represents one of the six notes playable at once on a guitar (as it has six strings).
                    // Each of these 6 notes has a duration, fret location, and string location that alligns with
                    // the guitar fretboard.
                    let array = []
                    window.FRETBOARD = [
                        ["E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5"],
                        ["B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5"],
                        ["G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5"],
                        ["D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4"],
                        ["A2", "A#2", "B2", "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4"],
                        ["E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3", "C#3", "D3", "D#3",  "E3", "F3", "F#3", "G3", "G#3", "A3"],
                    ];
                    let t = 0;
                    for (let i = 0; i < chords.length; i++) {
                        let chordData = chords[i]; // this is a chord of of >=6 notes
                        let dur = chordData[0].duration; // every note's duration is the same as its chord
                        
                        // accounting for 8th and 16th notes really being doubles relative to a quarter note (which has a duration of 1)
                        if (dur == EIGHTH_NOTE) {
                            dur = 0.5;
                        } else if (dur == SIXTEENTH_NOTE) {
                            dur = 0.25;
                        }
                        // when bpm is 120, quarter note duration is 0.5 sec. when bpm is 60, quarter note duration is 1 sec
                        dur *= (60 / curBpm);
                        let chordArray = [];
                        // console.log("len="+chords[i].length);
                        // console.log("len="+chordData.length);
                        for (let j = 0; j < chordData.length; j++) {
                            let string = chordData[j].stringNumber;
                            let fret = chordData[j].fretNumber;
                            // console.log("str=" + string + ", fret=" + fret);
                            chordArray.push(FRETBOARD[string][fret]);
                        }
                        // console.log("chord array=" + chordArray)
                        
                        array.push({
                            // time: `0:${t}`, 
                            time: t,
                            chord: chordArray,
                            duration: dur
                        });
                        t += dur;
                    }
                    console.log("array of notes=");
                    console.log(array);
                    resolve(array);
                },
                error: function(error) {
                    console.log('Error:', error);
                    reject(error);
                }
            });
        });
    }

    /* Play, pause, and resume buttons */
    let audioStartTime = 0;
    let elapsed = 0;
    // let playbackStartTime = null;
    let restartBtnToggled = false;
    let playing = false;
    let playingNotes = [];

    
    const playBtn = document.getElementById("play-btn");
    playBtn.addEventListener("click", async () => {
        if (!playing) {
            await ac.resume();
            audioStartTime = ac.currentTime - elapsed;
            const resumeTime = ac.currentTime;
            console.log("resumed at", resumeTime, "seconds");
            array.forEach((val) => {
                console.log(val);
                if (elapsed > val.time) {
                    // already played, skip note
                    return;
                }
                const relativeTime = val.time - elapsed;
                val.chord.forEach(async (note) => {
                    console.log(note);
                    if (relativeTime >= 0) {
                        let playingNote = await player.play(note, resumeTime + relativeTime, val.duration, { destination: gainNode });
                        playingNotes.push(playingNote);
                    }
                });
            });
            if (!restartBtnToggled) {
                toggleRestartBtn();
            }
            playBtn.innerHTML = '<i class="fa-solid fa-pause"></i>';
        } else {
            elapsed = ac.currentTime - audioStartTime;
            await ac.suspend();
            playingNotes.forEach((note) => {
                try {
                    note.stop();
                } catch (e) {}
            });
            playingNotes = [];
            console.log("paused at", elapsed, "seconds");
            playBtn.innerHTML = '<i class="fa-solid fa-play"></i>';
        }
        playing = !playing;
    });
    

    function toggleRestartBtn() {
        const restartBtn = document.getElementById('restart-btn');
        restartBtn.style.display = "block";
        restartBtnToggled = true;
    };

    const restartBtn = document.getElementById("restart-btn");
    restartBtn.addEventListener("click", async () => {
        restart();
    });
    async function restart() {
        playingNotes.forEach((note) => {
            try {
                note.stop();
            } catch (e) {}
        });
        elapsed = 0;
        playing = false;
        playingNotes = [];
        await ac.suspend();
        array = await loadChordsToPlay(); // reset in case bpm changed
        audioStartTime = ac.currentTime;
        playBtn.innerHTML = '<i class="fa-solid fa-play"></i>';
        restartBtn.style.display = 'none';
        restartBtnToggled = false;
    }
});