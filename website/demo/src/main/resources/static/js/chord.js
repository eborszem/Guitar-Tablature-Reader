document.addEventListener("DOMContentLoaded", function() {
    let token = localStorage.getItem("jwt");
    var addChordBtns = document.querySelectorAll(".add-chord");
    addChordBtns.forEach(function(btn) {
        btn.addEventListener("click", function(event) {
            event.stopPropagation(); // prevent fretboard popup from appearing
            let chordBox = btn.closest('.chord-box');
            let measureId = chordBox.getAttribute('data-measure-id');
            let chordId = chordBox.getAttribute('data-chord-id'); // the new chord will be added after this one
            console.log("adding chord after chord with id:", chordId);
            console.log("Token ->", token);
            $.ajax({
                type: "POST",
                url: "/chord/add",
                headers: {
                    "Authorization": "Bearer " + token
                },
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify({
                    measureId: measureId,
                    chordId: chordId,
                }),
                success: function(response) {
                    location.reload();
                    console.log("New chord created:", response);
                },
            });
        });
    });

    var deleteChordBtns = document.querySelectorAll(".delete-chord");
    deleteChordBtns.forEach(function(btn) {
        btn.addEventListener("click", function(event) {
            event.stopPropagation(); // prevent fretboard popup from appearing
            let chordBox = btn.closest('.chord-box');
            let measureId = chordBox.getAttribute('data-measure-id');
            let chordId = chordBox.getAttribute('data-chord-id');
            let chordIndex = chordBox.getAttribute('data-chord-index'); // placement of chord relative to other chords in measure
            console.log("deleting chord after chord with id:", chordId);
            console.log("Token ->", token);
            $.ajax({
                type: "POST",
                url: "/chord/delete",
                headers: {
                    "Authorization": "Bearer " + token
                },
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify({
                    measureId: measureId,
                    chordId: chordId,
                    chordIndex: chordIndex
                }),
                success: function(response) {
                    location.reload();
                    console.log("Chord deleted:", response);
                },
                error: function() {
                    alert("Error: Not allowed to delete last chord in measure. Try deleting the measure instead.");
                }
            });
        });
    });

    var duplicateChordBtns = document.querySelectorAll(".duplicate-chord");
    duplicateChordBtns.forEach(function(btn) {
        btn.addEventListener("click", function(event) {
            event.stopPropagation(); // prevent fretboard popup from appearing
            let chordBox = btn.closest('.chord-box');
            let measureId = chordBox.getAttribute('data-measure-id');
            let chordId = chordBox.getAttribute('data-chord-id'); // the new chord will be added after this one
            console.log("duplicating chord after chord with id:", chordId);
            console.log("Token ->", token);
            $.ajax({
                type: "POST",
                url: "/chord/duplicate",
                headers: {
                    "Authorization": "Bearer " + token
                },
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify({
                    measureId: measureId,
                    chordId: chordId,
                }),
                success: function(response) {
                    location.reload();
                    console.log("New chord created:", response);
                },
            });
        });
    });

    var swapChordBtns = document.querySelectorAll(".swap-chord");
    swapChordBtns.forEach(function(btn) {
        btn.addEventListener("click", function(event) {
            event.stopPropagation(); // prevent fretboard popup from appearing
            let chordBox = btn.closest('.chord-box');
            let measureId = chordBox.getAttribute('data-measure-id');
            let chordId = chordBox.getAttribute('data-chord-id');
            let direction = btn.getAttribute('data-direction');
            $.ajax({
                type: "POST",
                url: "/chord/swap",
                headers: {
                    "Authorization": "Bearer " + token
                },
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify({
                    measureId: measureId,
                    chordId: chordId,
                    direction: direction
                }),
                success: function(response) {
                    location.reload();

                    // console.log("New chord created:", response);
                },
                error: function() {
                    alert("Error: Chords are not allowed to change measures.");
                }
            });
        });
    });

});