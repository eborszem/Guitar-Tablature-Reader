function chordClicked(chordElement) {
    console.log("Chord clicked")
}

document.addEventListener("DOMContentLoaded", function() {
    
    var newMeasure = document.getElementById("add-new-measure")
    newMeasure.addEventListener("click", function() {
        console.log("test")
        let userInput = prompt("Enter a chord")
        // create new measure filled with empty chords/notes
        // blank measure appears on screen
        // user can select it
        // here, send info to controller -> controller, update composition -> update page, refresh
        const response = fetch("/createNewMeasure", {
            method : "POST",
        }).then((response) => response.json());
        // is there a better way to make the new measure appear?
        location.reload();
    });
    var addChord = document.getElementById("add-chord")
    addChord.addEventListener("click", function() {
        console.log("hello")
    });

    // Chord creation
    const modal = document.getElementById("chordModal");
    const span = document.getElementsByClassName("close")[0];
    const notesDisplay = document.getElementById('notesDisplay');

    function chordClicked(chordElement) {
        const notes = [];
        const noteElements = chordElement.querySelectorAll('.note span');
        noteElements.forEach(function(noteElement) {
            notes.push(noteElement.textContent.trim());
        });
        // Reverse notes array to match order of strings
        notes.reverse();
        // Display notes in the modal
        const notesDisplay = document.getElementById('notesDisplay');
        notesDisplay.textContent = 'Chord notes: ' + notes.reverse().join(', ');
        const modal = document.getElementById('chordModal');
        modal.style.display = "block";
    }

    const chordBoxes = document.querySelectorAll('.chord-box');
    chordBoxes.forEach(function(box) {
        box.addEventListener('click', function() {
            chordClicked(this);
            console.log(this.id)
        });
    });

    // Close when "x" is pressed on the popup
    span.onclick = function() {
        modal.style.display = "none";
    };

    // Close when clicking outside the popup
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    };

    // Clicking notes on fretboard to construct a chord
    document.querySelector('.string-rows').addEventListener('click', function(event) {
        if (event.target.classList.contains('note-btn')) {
            const string = event.target.getAttribute('data-string');
            const fret = event.target.getAttribute('data-fret');
            const row = event.target.closest('div');
            console.log("str=" + string + ", fret=" + fret);
            // only one note can be played per string
            row.querySelectorAll('.note-btn').forEach(button => {
                button.classList.remove('pressed');
            });
            event.target.classList.toggle('pressed');
        }
        
    });


    
});    


