import Soundfont from 'https://esm.sh/soundfont-player';

    window.FRETBOARD = [
        ["E4","F4","F#4","G4","G#4","A4","A#4","B4","C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5"],
        ["B3","C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4","C5","C#5","D5","D#5","E5"],
        ["G3","G#3","A3","A#3","B3","C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4","C5"],
        ["D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3","C4","C#4","D4","D#4","E4","F4","F#4","G4"],
        ["A2","A#2","B2","C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3","C4","C#4","D4"],
        ["E2","F2","F#2","G2","G#2","A2","A#2","B2","C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3"]
    ];

    let array = [];
    let playingNotes = [];
    let playing = false;
    let audioStartTime = 0;
    let elapsed = 0;
    let restartBtnToggled = false;
    let compositionId = -1;

    document.addEventListener("DOMContentLoaded", async () => {

    window.ac = new AudioContext();
    const gainNode = ac.createGain();
    gainNode.connect(ac.destination);
    window.gainNode = gainNode;


    window.player = await Soundfont.instrument(ac, 'acoustic_guitar_nylon', { destination: gainNode });

    let curBpm = parseInt(localStorage.getItem('bpm') || 120, 10);
    const bpmSlider = document.getElementById('bpm');
    bpmSlider.value = curBpm;

    bpmSlider.addEventListener('change', () => {
        let bpm = parseInt(bpmSlider.value, 10);
        bpm = Math.min(Math.max(bpm, 50), 300);
        bpmSlider.value = bpm;
        curBpm = bpm;
        localStorage.setItem('bpm', bpm);
        restart();
    });

    let curVolume = Number(localStorage.getItem('volume') || 50);
    gainNode.gain.setValueAtTime(curVolume / 100, ac.currentTime);
    const volumeSlider = document.getElementById('volume');
    const volumeValue = document.getElementById('volumeValue');
    volumeSlider.value = curVolume;
    volumeValue.textContent = curVolume;

    // add slider listener
    volumeSlider.addEventListener('input', (event) => {
        const vol = Number(event.target.value);
        gainNode.gain.setValueAtTime(vol / 100, ac.currentTime);
        volumeValue.textContent = vol;
        localStorage.setItem('volume', vol);
    });

    let scrollTimers = [];

    async function loadChordsToPlay(compositionId) {
        return new Promise((resolve, reject) => {
            array.length = 0;
            $.ajax({
                url: '/play',
                data: { compositionId },
                method: 'GET',
                success: (composition) => {
                let t = 0;
                composition.measures.forEach(measure => {
                    measure.chords.forEach(chord => {
                        const durMap = { WHOLE: 4, HALF: 2, QUARTER: 1, EIGHTH: 0.5, SIXTEENTH: 0.25 };
                        const durInteger = (durMap[chord.duration] || 1) * (60 / curBpm);
                        const chordArray = chord.notes.map(n => FRETBOARD[n.stringNumber][n.fretNumber]);
                        array.push({
                            time: t,
                            chord: chordArray,
                            duration: durInteger,
                            element: document.getElementById(chord.id)
                        });
                        t += durInteger;
                        console.log("arr="+chordArray + " " + t);
                    });
                });
                resolve(array);
                },
                error: (err) => reject(err)
            });
        });
    }

    const playBtn = document.getElementById("play-btn");
    playBtn.addEventListener("click", async () => {
        playBtn.disabled = true;
        playBtn.style.opacity = '1';
        playBtn.style.color = 'black'

        compositionId = playBtn.getAttribute('data-composition-id');
        array = [];
        await loadChordsToPlay(compositionId);
        if (!playing) {
            await ac.resume(); // only resume on user gesture
            audioStartTime = ac.currentTime - elapsed;

            array.forEach(val => {
                if (elapsed > val.time) return; // skip already played
                const relativeTime = val.time - elapsed;
                val.chord.forEach(note => {
                    const playingNote = player.play(note, ac.currentTime + relativeTime, val.duration);
                    if (playingNote)
                        playingNotes.push(playingNote);
                });
                // const timerId = setTimeout(() => {
                //     if (val.element) {
                //         val.element.scrollIntoView({ behavior: "smooth", block: "center" });
                //         val.element.classList.add("highlight");
                //         setTimeout(() => val.element.classList.remove("highlight"), 500);
                //     }
                // }, relativeTime * 1000);
                // scrollTimers.push(timerId);
            });

        if (!restartBtnToggled) toggleRestartBtn();
            playBtn.innerHTML = '<i class="fa-solid fa-pause"></i>';
            playing = true;
        } else {
            elapsed = ac.currentTime - audioStartTime;
            await ac.suspend();
            playingNotes.forEach(note => { if (note) note.stop(); });
            playingNotes = [];
            // clearScrollTimers();
            playBtn.innerHTML = '<i class="fa-solid fa-play"></i>';
            playing = false;
        }
        setTimeout(() => {
            playBtn.disabled = false;
        }, 250);
    });

    // function clearScrollTimers() {
    //     scrollTimers.forEach(id => clearTimeout(id));
    //     scrollTimers = [];
    // }

    function toggleRestartBtn() {
        const restartBtn = document.getElementById('restart-btn');
        restartBtn.style.display = 'block';
        restartBtnToggled = true;
    }

    const restartBtn = document.getElementById('restart-btn');
    
    async function killAudioContext() {
        if (window.ac)
            await window.ac.close();
        window.ac = new AudioContext();
        const gainNode = ac.createGain();
        gainNode.connect(ac.destination);
        window.gainNode = gainNode;
        window.player = await Soundfont.instrument(ac, 'acoustic_guitar_nylon', { destination: gainNode });
    }

    
    async function restart() {
        playingNotes.forEach(note => { if (note) note.stop(); });
        playingNotes = [];
        elapsed = 0;
        playing = false;
        // clearScrollTimers();
        
        await killAudioContext();
        await loadChordsToPlay(compositionId);
        
        playBtn.innerHTML = '<i class="fa-solid fa-play"></i>';
        restartBtn.style.display = 'none';
        restartBtnToggled = false;
    }
    
    restartBtn.addEventListener("click", async () => {
        playBtn.style.display = 'none';
        restartBtn.style.display = 'none';
        await restart();
        playBtn.style.display = 'block';
    });
    
    // for playing individual chord
    var playChordBtn = document.querySelectorAll(".play-chord");
    playChordBtn.forEach(function(btn) {
        btn.addEventListener("click", async function(event) {
            let chordBox = btn.closest('.chord-box');
            let duration = chordBox.getAttribute('data-duration');
            let notes = Array.from(chordBox.querySelectorAll('.note')).map(note => {
                let fret = parseInt(note.getAttribute('data-fret-number'), 10);
                let string = parseInt(note.getAttribute('data-string-number'), 10);
                // no rests
                if (fret < 0)
                    return null;
                return FRETBOARD[string][fret];
            }).filter(n => n !== null);

            const durMap = { WHOLE: 4, HALF: 2, QUARTER: 1, EIGHTH: 0.5, SIXTEENTH: 0.25 };
            let durSeconds = (durMap[duration] || 1) * (60 / curBpm);

            await ac.resume();

            notes.forEach(note => {
                player.play(note, ac.currentTime, durSeconds);
            });
        });
    });
});
