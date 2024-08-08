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
    
});