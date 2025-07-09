document.addEventListener("DOMContentLoaded", function() {
    /* 
     * There are currently two "add measure" buttons. One is at the top of the page, and the other is shown when the user hovers over a measure. 
     * These two buttons are immediately below.
     */
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
});