/* fretboard modal logic */
const UNCHANGED = -1; // User has not chosen string, measure, chord, or duration to change
/* 
 * Rests (represented by "X") are initially set to -1
 * When the X changes from a rest to a note, then again changes back to a rest, it is -2
 * Essentially, a negative number represents a rest "note". A positive number represents a fret number, which corresponds to an audible note.
 */
const INIT_REST = -1; 
const REST = -2;

const LOW_E = 5;
const A = 4;
const D = 3;
const G = 2;
const B = 1;
const HIGH_E = 0;
document.addEventListener("DOMContentLoaded", function() {
    const token = localStorage.getItem("jwt");
    /* 
     * CHORD MODIFICATION
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
    let compositionId = UNCHANGED;
    let measureId = UNCHANGED; // measure id of the selected chord
    let chordId = UNCHANGED; // location of the selected chord relative to its position in the measure (e.g. 0=first chord in measure, 1=second chord in measure, etc.)

    // These represent the changed notes which lay on the strings.
    // UNCHANGED (-1) represents a string that has not been changed.
    // When the user clicks on the virtual fretboard, its respective string will be updated.
    // Otherwise, it is not and the string does not change.
    // If the user does not confirm changes, the values will back to UNCHANGED.
    let updatedStrings = [];
    window.chordClicked = function(chordElement, compId, mId, cId) {
        
        console.log("chordClicked called with compId=" + compId + ", mId=" + mId + ", cId=" + cId);
        const duration = chordElement.getAttribute('data-duration'); 
        newDur = duration;
        console.log("duration="+duration);
        compositionId = compId;
        measureId = mId;
        chordId = cId;
        const noteElements = chordElement.querySelectorAll('.note');
        // console.log("chordElement="+chordElement);
        console.log("noteElements="+noteElements);
        noteElements.forEach(function(noteElement) {
            notes.push(noteElement.getAttribute('data-fret-number').trim());
            updatedStrings.push(noteElement.getAttribute('data-fret-number').trim());
        });
        const notesDisplay = document.getElementById('notesDisplay');
        // Display notes in the virtual fretboard popup
        // let notesText = 'Chord notes: ';
        let notesText = '';
        for (let i = 0; i < notes.length; i++) {
            if (notes[i] == INIT_REST || notes[i] == REST) {
                notesText += "X"; // signifies no note
            } else {
                console.log("FRETBOARD[" + i + "][" + notes[i] + "]=" + FRETBOARD[i][notes[i]]);
                notesText += FRETBOARD[i][notes[i]]; // use: FRETBOARD[string number][fret number]
            }
            if (i < notes.length - 1) {
                notesText += ' | ';
            }
        }
        notesDisplay.textContent = notesText;
        const modal = document.getElementById('chordModal');
        prepopulateBtns(notes); // prepopulate the frets that are already selected
        prepopulateDuration(duration); // prepoulate the duration (whole note, half note, etc. )that is already selected
        modal.style.display = "block";
    }

    let newDur;
    document.querySelectorAll('.duration-wrapper button').forEach(button => {
        button.addEventListener('click', function(event) {
            document.querySelectorAll('.duration-wrapper button').forEach(btn => {
                btn.classList.remove('pressed');
            });
            button.classList.add('pressed');
            newDur = button.getAttribute('data-duration');
            const clickedButton = event.currentTarget;
            if (clickedButton.classList.contains('whole-duration')) {
                newDur = "WHOLE";
            } else if (clickedButton.classList.contains('half-duration')) {
                newDur = "HALF";
            } else if (clickedButton.classList.contains('quarter-duration')) {
                newDur = "QUARTER";
            } else if (clickedButton.classList.contains('eighth-duration')) {
                newDur = "EIGHTH";
            } else if (clickedButton.classList.contains('sixteenth-duration')) {
                newDur = "SIXTEENTH";
            }
            
        });
    });

    let btnArr = [];
    function prepopulateBtns(notes) {
        document.querySelectorAll('.note-btn').forEach(button => {
            button.classList.remove('pressed');
        });
        btnArr = [];

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
            case "WHOLE": console.log("WHOLE NOTE DURATION"); document.getElementById('whole-duration').classList.add('pressed'); break;
            case "HALF": document.getElementById('half-duration').classList.add('pressed'); break;
            case "QUARTER": document.getElementById('quarter-duration').classList.add('pressed'); break;
            case "EIGHTH": document.getElementById('eighth-duration').classList.add('pressed'); break;
            case "SIXTEENTH": document.getElementById('sixteenth-duration').classList.add('pressed'); break;
        }
    }

        

    // Close when "x" is pressed on the popup
    span.onclick = function() {
        modal.style.display = "none";
        reset();
    };

    // Close when clicking outside the popup
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
            reset();
        }
        const newCompositionPopup = document.getElementById("popup");
        if (event.target === newCompositionPopup) {
            newCompositionPopup.style.display = "none";
        }
    };

    /* Changes notes upon user confirming on virtual fretboard. Sends data to controller */
    const confirmBtn = document.getElementById("confirm-btn");
    confirmBtn.addEventListener("click", async () => {
        console.log("**Confirm button clicked");
        modal.style.display = "none";
        console.log("string values="+updatedStrings);
        // console.log("curChord (confirmBtn)="+curChord);
        //console.log(measureAndChord);
        console.log(newDur + " dur was selected.")
        console.log("notes array in confirm ="+notes);
        $.ajax({
            type: "POST",
            url: "/chord/update",
            headers: {
                "Authorization": "Bearer " + token
            },
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify({
                compositionId: compositionId,
                measureId: measureId,
                chordId: chordId,
                updatedNotes: toIntArray(updatedStrings),
                newDuration: newDur
            }),
            success: function(response) {
                location.reload();
                console.log("Chord updated successfully:", response);
            }
        });
        reset();
    });

    function toIntArray(updatedStrings) {
        return updatedStrings.map(str => parseInt(str, 10));
    }

    function reset() {
        updatedStrings = [];
        notes = [];
        measureId = null;
        chordLocation = null;
        newDur = "QUARTER";
        btnArr = null;
    }

    // Clicking notes on fretboard to construct a chord
    document.querySelector('.string-rows').addEventListener('click', function(event) {
        if (event.target.classList.contains('note-btn')) {
            const string = event.target.getAttribute('data-string');
            const fret = parseInt(event.target.getAttribute('data-fret'));
            const row = event.target.closest('div');
            console.log("str=" + string + ", fret=" + fret);
            switch (parseInt(string, 10)) {
                case 6: updatedStrings[LOW_E] = fret; console.log("----->6" + fret); break;
                case 5: updatedStrings[A] = fret; console.log("----->5" + fret); break;
                case 4: updatedStrings[D] = fret; console.log("----->4" + fret); break;
                case 3: updatedStrings[G] = fret; console.log("----->3" + fret); break;
                case 2: updatedStrings[B] = fret; console.log("----->2" + fret); break;
                case 1: updatedStrings[HIGH_E] = fret; console.log("----->1" + fret); break;
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
                    case 6: updatedStrings[LOW_E] = REST; break;
                    case 5 : updatedStrings[A] = REST; break;
                    case 4 : updatedStrings[D] = REST; break;
                    case 3 : updatedStrings[G] = REST; break;
                    case 2 : updatedStrings[B] = REST; break;
                    case 1 : updatedStrings[HIGH_E] = REST; break;
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

});