document.addEventListener("DOMContentLoaded", function() {
    const token = localStorage.getItem("jwt");
    var addMeasureBtns = document.querySelectorAll(".add-measure");
    addMeasureBtns.forEach(function(btn) {
        btn.addEventListener("click", function() {
            let chordBox = btn.closest('.measure-box').querySelector('.chord-box');
            let addMeasureId = chordBox.getAttribute('data-measure-id'); // the new measure will be added after this one
            console.log("Adding measure with id:", addMeasureId);
            console.log("Token ->", token);
            $.ajax({
                type: "POST",
                url: "/measure/add",
                headers: {
                    "Authorization": "Bearer " + token
                },
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify({
                    measureId: addMeasureId
                }),
                success: function(response) {
                    console.log("New measure created:", response);
                },
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
                url: "/measure/delete",
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