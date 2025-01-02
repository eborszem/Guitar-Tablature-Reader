const UNCHANGED = -1; // User has not chosen string, measure, chord, or duration to change

/* Rests (represented by "X") are initially set to -1
 * When the X changes from a rest to a note, then again changes back to a rest, it is -2
 * Essentially, a negative number represents a rest "note". A positive number represents a fret number, which corresponds to an audible note.
 */
const INIT_REST = -1; 
const REST = -2;

function chordClicked(chordElement) {
    console.log("Chord clicked")
}

document.addEventListener("DOMContentLoaded", function() {
    /* ADDING NEW MEASURE TO COMPOSITION */
    var newMeasure = document.getElementById("add-new-measure");
    newMeasure.addEventListener("click", function() {
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
                // console.log("frets:", chord.fretNumbers, "dur:", chord.duration, "measureId:", chord.measureId);
                // TODO
            });
        })
        .catch(error => console.error("Error fetching new measure:", error));
    });

    /* DELETING MEASURE FROM COMPOSITION */
    var deleteMeasureBtns = document.querySelectorAll(".delete-measure");
    deleteMeasureBtns.forEach(function(btn) {
        btn.addEventListener("click", function() {
            let chordBox = btn.closest('.measure-box').querySelector('.chord-box');
            let deleteMeasureId = chordBox.getAttribute('data-measure-id');
            // console.log("Deleting measure with id:", deleteMeasureId);
            $.ajax({
                type: "POST",
                url: "/deleteMeasure",
                data: {
                    measureId: deleteMeasureId
                },
                timeout: 5000,
                success: function(response) {
                    // console.log("Measure deleted successfully:" + response);
                    location.reload();
                }
            });
        });
    });

    /* ADDING MEASURE TO COMPOSITION */
    var addMeasureBtns = document.querySelectorAll(".add-measure");
    addMeasureBtns.forEach(function(btn) {
        btn.addEventListener("click", function() {
            let chordBox = btn.closest('.measure-box').querySelector('.chord-box');
            let addMeasureId = chordBox.getAttribute('data-measure-id'); // the new measure will be added after this one
            // console.log("Adding measure with id:", addMeasureId);
            $.ajax({
                type: "POST",
                url: "/addMeasure",
                data: {
                    measureId: addMeasureId
                },
                timeout: 5000,
                success: function(response) {
                    // console.log("Measure added successfully:" + response);
                    location.reload();
                }
            });
        });
    });

    /* DUPLICATING MEASURE */
    var dupeMeasureBtns = document.querySelectorAll(".duplicate-measure");
    dupeMeasureBtns.forEach(function(btn) {
        btn.addEventListener("click", function() {
            let chordBox = btn.closest('.measure-box').querySelector('.chord-box');
            let dupeMeasureId = chordBox.getAttribute('data-measure-id'); // the new measure will be added after this one
            // console.log("Duping measure with id:", dupeMeasureId);
            $.ajax({
                type: "POST",
                url: "/duplicateMeasure",
                data: {
                    measureId: dupeMeasureId
                },
                timeout: 5000,
                success: function(response) {
                    // console.log("Measure duped successfully:" + response);
                    location.reload();
                }
            });
        });
    });


    /* ADDING NEW COMPOSITION/SONG */
    var newComposition = document.getElementById("new-composition");
    newComposition.addEventListener("click", function() {
        document.getElementById("popup").style.display = "block";
    });

    var submitNewComposition = document.getElementById("submit");
    var form = document.getElementById("popup");
    submitNewComposition.addEventListener("click", function() {
        const regex = /^.+$/; // makes sure that the title and composer are not empty
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

    /* CHORD MODIFICATION
     * User clicks on a chord box to modify the notes in the chord. A virtual fretboard will appear,
     * allowing the user to modify the notes (which are labeled). A chord on guitar has a maximum of
     * 6 notes (one note per string). The user can click on the virtual fretboard to change the notes.
     * Duration can also be changed.
     * The type (note or rest) is implied. Clicking a note already highlighted/selected on the virtual
     * fretboard will turn it into a rest, depicted by an "X". This is also the default value for a string
     * when a new measure or chord is created.
     */
    const modal = document.getElementById("chordModal"); // virtual fretboard popup
    const span = document.getElementsByClassName("close")[0]; // close popup button

    let notes = []; // notes in the selected chord

    // UNCHANGED values, they will become decided when the user clicks a chord
    let measureId = UNCHANGED; // measure id of the selected chord
    let chordLocation = UNCHANGED; // location of the selected chord relative to its position in the measure (e.g. 0=first chord in measure, 1=second chord in measure, etc.)
    
    function chordClicked(chordElement) {
        measureId = chordElement.getAttribute('data-measure-id'); // now decided
        chordLocation = chordElement.getAttribute('data-chord-num'); // now decided
        const noteElements = chordElement.querySelectorAll('.note');
        noteElements.forEach(function(noteElement) {
            notes.push(noteElement.getAttribute('data-fret-number').trim());
        });
        const duration = noteElements[0].getAttribute('data-duration');
        const notesDisplay = document.getElementById('notesDisplay');
        // Display notes in the virtual fretboard popup
        let notesText = 'Chord notes: ';
        for (let i = 0; i < notes.length; i++) {
            if (notes[i] == INIT_REST || notes[i] == REST) {
                notesText += "X"; // signifies no note
            } else {
                notesText += FRETBOARD[i][notes[i]]; // use: FRETBOARD[string number][fret number]
            }
            if (i < notes.length - 1) {
                notesText += ', ';
            }
        }
        notesDisplay.textContent = notesText;
        const modal = document.getElementById('chordModal');
        prepopulateBtns(notes); // prepopulate the frets that are already selected
        prepopulateDuration(duration); // prepoulate the duration (whole note, half note, etc. )that is already selected
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

    function prepopulateDuration(duration) {
        console.log("prepopulating duration: " + duration + "type=" + typeof(duration));
        document.querySelectorAll('.duration-wrapper button').forEach(button => {
            button.classList.remove('pressed');
        });
        switch (duration) {
            case "4": console.log("WHOLE NOTE DURATION"); document.getElementById('whole-duration').classList.add('pressed'); break;
            case "2": document.getElementById('half-duration').classList.add('pressed'); break;
            case "1": document.getElementById('quarter-duration').classList.add('pressed'); break;
            case "8": document.getElementById('eighth-duration').classList.add('pressed'); break;
            case "16": document.getElementById('sixteenth-duration').classList.add('pressed'); break;
        }
    }

    let newDur = UNCHANGED;
    document.querySelectorAll('.duration-wrapper button').forEach(button => {
        button.addEventListener('click', function(event) {
            document.querySelectorAll('.duration-wrapper button').forEach(btn => {
                btn.classList.remove('pressed');
            });
            button.classList.add('pressed');
            newDur = button.getAttribute('data-duration');
            const clickedButton = event.currentTarget;
            if (clickedButton.classList.contains('whole-duration')) {
                newDur = 4;
            } else if (clickedButton.classList.contains('half-duration')) {
                newDur = 2;
            } else if (clickedButton.classList.contains('quarter-duration')) {
                newDur = 1;
            } else if (clickedButton.classList.contains('eighth-duration')) {
                newDur = 8;
            } else if (clickedButton.classList.contains('sixteenth-duration')) {
                newDur = 16;
            }
            
        });
    });


    let curChord = "";
    const chordBoxes = document.querySelectorAll('.chord-box');
    chordBoxes.forEach(function(box) {
        box.addEventListener('click', function() {
            chordClicked(this);
            console.log(this.id);
            curChord = this.id;
        });
    });
    
    // These represent the changed notes which lay on the strings.
    // UNCHANGED (-1) represents a string that has not been changed.
    // When the user clicks on the virtual fretboard, its respective string will be updated.
    // Otherwise, it is not and the string does not change.
    // If the user does not confirm changes, the values will back to UNCHANGED.
    let updated_low_e_string = UNCHANGED;
    let updated_a_string = UNCHANGED;
    let updated_d_string = UNCHANGED;
    let updated_g_string = UNCHANGED;
    let updated_b_string = UNCHANGED;
    let updated_high_e_string = UNCHANGED;

    // Close when "x" is pressed on the popup
    span.onclick = function() {
        modal.style.display = "none";
        notes = [];
        measureId = UNCHANGED;
        chordLocation = UNCHANGED;
        newDur = UNCHANGED;
        btnArr = [];
    };

    // Close when clicking outside the popup
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
            notes = [];
            measureId = UNCHANGED;
            chordLocation = UNCHANGED;
            newDur = UNCHANGED;
            btnArr = [];
        }
        const newCompositionPopup = document.getElementById("popup");
        if (event.target === newCompositionPopup) {
            newCompositionPopup.style.display = "none";
        }
    };

    /* Changes notes upon user confirming on virtual fretboard. Sends data to controller */
    const confirmBtn = document.getElementById("confirm-btn");
    confirmBtn.addEventListener("click", async () => {
        modal.style.display = "none";
        res = [];
        console.log("string values="+updated_low_e_string + " " + updated_a_string + " " + updated_d_string + " " + updated_g_string + " " + updated_b_string + " " + updated_high_e_string);

        console.log("curChord (confirmBtn)="+curChord);
        // converting curChord (which contains the measure and chord values) into actual integer values to pass into ajax
        let measureAndChordStrs = curChord.match(/\d+/g);
        let measureAndChord = measureAndChordStrs.map(Number);
        //console.log(measureAndChord);
        console.log(newDur + "dur was selected.")
        console.log("notes array in confirm ="+notes);
        
        $.ajax({
            type: "POST",
            url: "/updateChord",
            data: {
                updated_low_e_string: updated_low_e_string,
                updated_a_string: updated_a_string,
                updated_d_string: updated_d_string,
                updated_g_string: updated_g_string,
                updated_b_string: updated_b_string,
                updated_high_e_string: updated_high_e_string,
                measureId: measureId,
                measure: measureAndChord[0],
                // chord: measureAndChord[1],
                chordLocation: chordLocation,
                newDuration: newDur,
                original_low_e_string: parseInt(notes[5], 10),
                original_a_string: parseInt(notes[4], 10),
                original_d_string: parseInt(notes[3], 10),
                original_g_string: parseInt(notes[2], 10),
                original_b_string: parseInt(notes[1], 10),
                original_high_e_string: parseInt(notes[0], 10)
            },
            timeout: 5000,
            success: function(response) {
                location.reload();
                // console.log("measure=" + measure + ", measureAndChord[0]=" + measureAndChord[0]);
                console.log("Chord updated successfully:", response);
            }
        });
        // Reset strings + other variables, as chord is changed
        updated_low_e_string = UNCHANGED;
        updated_a_string = UNCHANGED;
        updated_d_string = UNCHANGED;
        updated_g_string = UNCHANGED;
        updated_b_string = UNCHANGED;
        updated_high_e_string = UNCHANGED;
        notes = [];
        measureId = UNCHANGED;
        chordLocation = UNCHANGED;
        newDur = UNCHANGED;
    });

    // Clicking notes on fretboard to construct a chord
    document.querySelector('.string-rows').addEventListener('click', function(event) {
        if (event.target.classList.contains('note-btn')) {
            const string = event.target.getAttribute('data-string');
            const fret = parseInt(event.target.getAttribute('data-fret'));
            const row = event.target.closest('div');
            console.log("str=" + string + ", fret=" + fret);
            switch (parseInt(string, 10)) {
                case 6: updated_low_e_string = fret; console.log("----->6" + fret); break;
                case 5: updated_a_string = fret; console.log("----->5" + fret); break;
                case 4: updated_d_string = fret; console.log("----->4" + fret); break;
                case 3: updated_g_string = fret; console.log("----->3" + fret); break;
                case 2: updated_b_string = fret; console.log("----->2" + fret); break;
                case 1: updated_high_e_string = fret; console.log("----->1" + fret); break;
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
                    case 6: updated_low_e_string = REST; break;
                    case 5: updated_a_string = REST; break;
                    case 4: updated_d_string = REST; break;
                    case 3: updated_g_string = REST; break;
                    case 2: updated_b_string = REST; break;
                    case 1: updated_high_e_string = REST; break;
                }
                newBtnId = string + "-" + "-2"; // REST = -2
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