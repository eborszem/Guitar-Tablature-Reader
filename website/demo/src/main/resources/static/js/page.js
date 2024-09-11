function chordClicked(chordElement) {
    console.log("Chord clicked")
}

document.addEventListener("DOMContentLoaded", function() {
    const UNINITIALIZED = "";
    var newMeasure = document.getElementById("add-new-measure");
    newMeasure.addEventListener("click", function() {
        console.log("test");

        fetch("/createNewMeasure", {
            method: "POST",
        })
        .then(response => response.json())
        .then(data => {
            console.log("Received JSON:", data);  // Log the JSON to console
            // Update the page with the new measure without reloading
            // You can iterate over the response and dynamically add the new measure to the DOM
            data.forEach(chord => {
                console.log("frets:", chord.fretNumbers, "dur:", chord.duration, "measureId:", chord.measureId);
                // TODO
            });
        })
        .catch(error => console.error("Error fetching new measure:", error));
    });
    
    var addChord = document.getElementById("add-chord")
    addChord.addEventListener("click", function() {
        console.log("hello")
    });

    // Chord creation
    const modal = document.getElementById("chordModal");
    const span = document.getElementsByClassName("close")[0];
    const notesDisplay = document.getElementById('notesDisplay');

    let notes = [];
    let chord_duration = 0;
    let measureId = -1;
    let chordNum = -1;
    function chordClicked(chordElement) {
        measureId = chordElement.getAttribute('data-measure-id');
        console.log("DATABASE MEASURE ID ==="+measureId);
        chordNum = chordElement.getAttribute('data-chord-num');
        const noteElements = chordElement.querySelectorAll('.note');
        noteElements.forEach(function(noteElement) {
            notes.push(noteElement.textContent.trim());
        });
        // Reverse notes array to match order of strings
        notes.reverse();
        const duration = noteElements[0].getAttribute('data-duration');
        console.log("DURATION TEST==="+duration);
        // Display notes in the modal
        const notesDisplay = document.getElementById('notesDisplay');
        notesDisplay.textContent = 'Chord notes: ' + notes.reverse().join(', ');
        
        const modal = document.getElementById('chordModal');
        prepopulateBtns(notes);
        modal.style.display = "block";
    }

    function prepopulateBtns(notes) {
        document.querySelectorAll('.note-btn').forEach(button => {
            button.classList.remove('pressed');
        });
        const notesFormatted = notes.map((note, index) => (index + 1) + '-' + note);
        notesFormatted.forEach(note => {
            document.querySelectorAll('.note-btn').forEach(button => {
                console.log("->"+note + " " + button.textContent.trim());
                if (button.textContent.trim() === note) {
                    button.classList.add('pressed');
                }
            });
        });
    }
    let curChord = "";
    const chordBoxes = document.querySelectorAll('.chord-box');
    chordBoxes.forEach(function(box) {
        box.addEventListener('click', function() {
            chordClicked(this);
            console.log(this.id);
            curChord = this.id;
        });
    });

    /* Dropdowns which determine duration of chord/note/rest (quarter note, half note, etc. )and type (rest/not rest) */ 
    const durationDropdown = document.getElementById("duration-dropdown");
    let newDur = UNINITIALIZED;
    durationDropdown.addEventListener('change', function() {
        newDur = durationDropdown.value;
        console.log(newDur + " was selected.")
    });
    
    const typeDropdown = document.getElementById("type-dropdown");
    let newType = UNINITIALIZED;
    typeDropdown.addEventListener('change', function() {
        newType = typeDropdown.value;
        console.log(newType + " was selected.")
    });

    // 6 arrays which store the fret numbers being changed on the chord (strings implicitly stored)
    // no values in an array = user did not change string
    // 3 values in an array = user clicked three notes on string
    let low_e_string = -1;
    let a_string = -1;
    let d_string = -1;
    let g_string = -1;
    let b_string = -1;
    let high_e_string = -1;

    // Close when "x" is pressed on the popup
    span.onclick = function() {
        modal.style.display = "none";
        notes = [];
        chord_duration = 0;
        measureId = -1;
        chordNum = -1;
        newDur = UNINITIALIZED;
        newType = UNINITIALIZED;
    };

    // Close when clicking outside the popup
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
            notes = [];
            chord_duration = 0;
            measureId = -1;
            chordNum = -1;
            newDur = UNINITIALIZED;
            newType = UNINITIALIZED;
        }
    };

    const confirmBtn = document.getElementById("confirm-btn");
    confirmBtn.addEventListener("click", async () => { 
        modal.style.display = "none";
        res = [];
        console.log("string values="+low_e_string + " " + a_string + " " + d_string + " " + g_string + " " + b_string + " " + high_e_string);
        console.log("curChord (confirmBtn)="+curChord);
        // converting curChord (which contains the measure and chord values) into actual integer values to pass into ajax
        let measureAndChordStrs = curChord.match(/\d+/g);
        let measureAndChord = measureAndChordStrs.map(Number);
        //console.log(measureAndChord);
        console.log(newDur + "dur was selected.")
        console.log(newType + "type was selected.")
        console.log("notes array in confirm ="+notes);
        let durData = 0;
        if (newDur === "sixteenth") {
            durData = 16;
        } else if (newDur === "eighth") {
            durData = 8;
        } else if (newDur === "quarter") {
            durData = 1;
        } else if (newDur === "half") {
            durData = 2;
        } else if (newDur === "whole") {
            durData = 4;
        }
        
        let typeData = 0;
        if (newType === "note-option") {
            typeData = 0;
        } else if (newType === "rest-option") {
            typeData = 1;
        }

        $.ajax({
            type: "POST",
            url: "/updateChord",
            data: {
                low_e_string: low_e_string,
                a_string: a_string,
                d_string: d_string,
                g_string: g_string,
                b_string: b_string,
                high_e_string: high_e_string,
                measureId: measureId,
                measure: measureAndChord[0],
                chord: measureAndChord[1],
                chordNum: chordNum,
                duration: chord_duration,
                newDuration: durData,
                newType: typeData,
                original_l_e: parseInt(notes[5], 10),
                original_a: parseInt(notes[4], 10),
                original_d: parseInt(notes[3], 10),
                original_g: parseInt(notes[2], 10),
                original_b: parseInt(notes[1], 10),
                original_h_e: parseInt(notes[0], 10)
            },
            timeout: 5000,
            success: function(response) {
                // Handle a successful response here
                console.log("Chord updated successfully:", response);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                // Handle errors here
                console.error("Error updating chord:", textStatus, errorThrown);
            }
        });
        location.reload();
        // reset strings + other variables
        low_e_string = -1;
        a_string = -1;
        d_string = -1;
        g_string = -1;
        b_string = -1;
        high_e_string = -1;
        notes = [];
        chord_duration = 0;
        measureId = -1;
        chordNum = -1;
        newDur = UNINITIALIZED;
        newType = UNINITIALIZED;
    });

    // Clicking notes on fretboard to construct a chord
    document.querySelector('.string-rows').addEventListener('click', function(event) {
        if (event.target.classList.contains('note-btn')) {
            const string = event.target.getAttribute('data-string');
            const fret = parseInt(event.target.getAttribute('data-fret'));
            const row = event.target.closest('div');
            console.log("str=" + string + ", fret=" + fret);
            switch (parseInt(string, 10)) {
                case 6: low_e_string = fret; console.log("----->6" + fret); break;
                case 5: a_string = fret; console.log("----->5" + fret); break;
                case 4: d_string = fret; console.log("----->4" + fret); break;
                case 3: g_string = fret; console.log("----->3" + fret); break;
                case 2: b_string = fret; console.log("----->2" + fret); break;
                case 1: high_e_string = fret; console.log("----->1" + fret); break;
            }
            // only one note can be played per string
            row.querySelectorAll('.note-btn').forEach(button => {
                button.classList.remove('pressed');
            });
            event.target.classList.add('pressed');

        } 
    });
});