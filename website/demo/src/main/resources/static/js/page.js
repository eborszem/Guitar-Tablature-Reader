const UNCHANGED = -1; // User has not chosen string, measure, chord, or duration to change

/* 
 * Rests (represented by "X") are initially set to -1
 * When the X changes from a rest to a note, then again changes back to a rest, it is -2
 * Essentially, a negative number represents a rest "note". A positive number represents a fret number, which corresponds to an audible note.
 */
const INIT_REST = -1; 
const REST = -2;
let newDur = UNCHANGED;

document.addEventListener("DOMContentLoaded", function() {
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
});