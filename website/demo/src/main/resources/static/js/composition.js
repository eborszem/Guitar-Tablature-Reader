
document.addEventListener("DOMContentLoaded", function() {
    /* ADDING NEW COMPOSITION/SONG */
    var newComposition = document.getElementById("new-composition");
    newComposition.addEventListener("click", function() {
        document.getElementById("popup").style.display = "block";
    });

    var submitNewComposition = document.getElementById("submit");
    const token = localStorage.getItem("jwt");9/
    submitNewComposition.addEventListener("click", function() {
        console.log("TOKEN="+token);
        const regex = /^.+$/; // makes sure that the title and composer are not empty
        var title = document.getElementById("title").value;
        var composer = document.getElementById("composer").value;
        if (!token) {
            alert("User not authenticated. Please log in.");
        } else if (!regex.test(title) && !regex.test(composer)) {
            alert("Invalid title and composer");
        } else if (!regex.test(title)) {
            alert("Invalid title");
        } else if (!regex.test(composer)) {
            alert("Invalid composer");
        } else {
            $.ajax({
                type: "PUT",
                url: "/composition",
                headers: {
                    "Authorization": "Bearer " + token
                },
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify({
                    title: title,
                    composer: composer
                }),
                success: function(response) {
                    const id = Object.values(response)[0];
                    console.log(response);
                    console.log(id);
                    document.getElementById("popup").style.display = "none";
                    window.location.href = `/song?compositionId=${id}`;
                },
            });
        }
    });

    var cancelNewComposition = document.getElementById("cancel");
    cancelNewComposition.addEventListener("click", function() {
        document.getElementById("popup").style.display = "none";
    });
});