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
    const notesDisplay = document.getElementById("notesDisplay");

    function chordClicked(chordElement) {
        const notes = [];
        const noteElements = chordElement.querySelectorAll('.note span');
        noteElements.forEach(function(noteElement) {
            notes.push(noteElement.textContent);
        });

        // Display notes in the modal
        notesDisplay.textContent = 'Chord notes: ' + notes.join(', ');
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


    
});    


