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
            location.reload();
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

    // make a new composition/song
    var newComposition = document.getElementById("new-composition");
    newComposition.addEventListener("click", function() {
        document.getElementById("popup").style.display = "block";
    });

    var submitNewComposition = document.getElementById("submit");
    var form = document.getElementById("popup");
    submitNewComposition.addEventListener("click", function() {
        const regex = /^.+$/; // make sure that the title and composer are not empty
        var title = document.getElementById("title").value;
        var composer = document.getElementById("composer").value;
        if (!regex.test(title) && !regex.test(composer)) {
            alert("Invalid title and composer");
        } else if (!regex.test(title)) {
            alert("Invalid title");
        } else if (!regex.test(composer)) {
            alert("Invalid composer");
        } else {
            $.ajax({
                type: "POST",
                url: "/newComposition",
                data: {
                    title: title,
                    composer: composer
                },
                timeout: 5000,
                success: function(response) {
                    location.reload();
                },
            });
        }

    });
    var cancelNewComposition = document.getElementById("cancel");
    cancelNewComposition.addEventListener("click", function() {
        document.getElementById("popup").style.display = "none";
    });

    // Chord creation
    const modal = document.getElementById("chordModal");
    const span = document.getElementsByClassName("close")[0];
    const notesDisplay = document.getElementById('notesDisplay');

    let notes = [];
    let chord_duration = 0;
    let measureId = -1;
    let chordNum = -1;
    // TODO: if note is selected and already pressed, turn it into a rest
    function chordClicked(chordElement) {
        measureId = chordElement.getAttribute('data-measure-id');
        console.log("DATABASE MEASURE ID ==="+measureId);
        chordNum = chordElement.getAttribute('data-chord-num');
        const noteElements = chordElement.querySelectorAll('.note');
        noteElements.forEach(function(noteElement) {
            notes.push(noteElement.getAttribute('data-fret-number').trim());
        });
        // Reverse notes array to match order of strings
        notes.reverse();
        const duration = noteElements[0].getAttribute('data-duration');
        console.log("DURATION TEST==="+duration);
        // Display notes in the modal
        const notesDisplay = document.getElementById('notesDisplay');
        let notesText = 'Chord notes: ';
        //notesDisplay.textContent = 'Chord notes: ' + notes.reverse().join(', ');
        notes.reverse();
        for (let i = 0; i < notes.length; i++) {
            if (notes[i] === "-1" || notes[i] === "-2") {
                notesText += "X"; // signifies no note
            } else {
                notesText += notes[i];
            }
            if (i < notes.length - 1) {
                notesText += ', ';
            }
            console.log("notesText...="+notesText);
        }
        notesDisplay.textContent = notesText;
        const modal = document.getElementById('chordModal');
        prepopulateBtns(notes);
        modal.style.display = "block";
    }

    let btnArr = [];
    function prepopulateBtns(notes) {
        document.querySelectorAll('.note-btn').forEach(button => {
            button.classList.remove('pressed');
        });
        const notesFormatted = notes.map((note, index) => (index + 1) + '-' + note);
        console.log("notesFormatted="+notesFormatted);
        notesFormatted.forEach(noteId => {
            btnArr.push(noteId);
            const button = document.getElementById(noteId);
            if (button) {
                button.classList.add('pressed');
                console.log("NOTE ID ="+noteId);
            }
        });
        console.log("btnArr===="+btnArr);
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
        btnArr = [];
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
            btnArr = [];
        }
        const newCompositionPopup = document.getElementById("popup");
        if (event.target === newCompositionPopup) {
            newCompositionPopup.style.display = "none";
        }
    };

    /* Changes notes upon user confirming on virtual fretboard */
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
                location.reload();
                console.log("Chord updated successfully:", response);
            }
        });
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
            let newBtnId = string + "-" + fret;
            console.log("new button id="+newBtnId);
            // replace button in button array
            //if (btnArr.includes(newBtnId)) {
            //console.log("CONTAINS NOTE ALREADY!!!!!!!!");
            /*const button = document.getElementById(newBtnId);
            if (btnArr.includes(button)) {
                console.log("BUTTON WAS ALREADY CLICKED");
            }*/

            let i = 0;
            while (btnArr[i] != newBtnId && i < btnArr.length) {
                i++;
            }
            if (i < btnArr.length) { // button was already clicked, so unpress it (aka make a rest)
                console.log("Replacing " + btnArr[i] + " with " + newBtnId + "!!!");
                event.target.classList.remove('pressed');
                // update string to have a rest
                // -2 represents rest here to avoid bug involving -1 in updateChord() in DemoController
                switch (parseInt(string, 10)) {
                    case 6: low_e_string = -2; break;
                    case 5: a_string = -2; break;
                    case 4: d_string = -2; break;
                    case 3: g_string = -2; break;
                    case 2: b_string = -2; break;
                    case 1: high_e_string = -2; break;
                }

                newBtnId = string + "-" + "-2";
                btnArr[string - 1] = newBtnId;
            } else {
                event.target.classList.add('pressed');
                //event.target.textContent = fret;
                btnArr[string - 1] = newBtnId;
            }
            console.log("updated button array="+btnArr);

        } 
    });


    const dropdown = document.getElementById('composition-dropdown');

    dropdown.addEventListener('change', function() {
        changeComposition();  // Call the function when an option is selected
        console.log("testing");
    });
    // changing composition
    function changeComposition() {
        const form = document.getElementById('composition-form');
        const formData = new FormData(form);
        fetch('/changeComposition', {
            method: 'POST',
            body: formData
        })
        .then(response => response.text())
        .then(data => {
            location.reload();
            console.log(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
    }

});