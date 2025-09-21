
document.addEventListener("DOMContentLoaded", function() {
    /* ADDING NEW COMPOSITION/SONG */
    var newComposition = document.getElementById("new-composition");
    newComposition.addEventListener("click", function() {
        document.getElementById("popup").style.display = "block";
    });

    var submitNewComposition = document.getElementById("submit");
    var form = document.getElementById("popup");
    const token = localStorage.getItem("jwt");
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
                type: "POST",
                url: "/newComposition",
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
                    console.log("New composition created:", response);
                    alert("Composition added!");
                },
            });
        }
    });

    var cancelNewComposition = document.getElementById("cancel");
    cancelNewComposition.addEventListener("click", function() {
        document.getElementById("popup").style.display = "none";
    });

    // const dropdown = document.getElementById('composition-dropdown');
    // dropdown.addEventListener('change', function() {
    //     changeComposition();  // Call the function when an option is selected
    //     console.log("testing");
    // });
    // changing composition
    const select = document.getElementById('composition-dropdown');
    // const changeCompositionForm = document.getElementById('composition-form');
    select.addEventListener('change', (e) => {
        e.preventDefault();
        const selectedId = select.value;
        $.ajax({
            type: "POST",
            url: "/changeComposition",
            headers: {
                "Authorization": "Bearer " + token
            },
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify({
                selectedId: selectedId,
            }),
            success: function(response) {
                console.log(response);
                // console.log("New composition created:", response);
                // alert("Composition added!");
            },
        });
    });
        
        // fetch('/changeComposition', {
        //     method: 'POST',
        //     body: formData,
        //     headers: {
        //             "Authorization": "Bearer " + token,
        //             "Content-Type": "application/json"
        //     },
        // })
        // .then(response => response.text())
        // .then(data => {
        //     location.reload();
        //     console.log(data);
        // })
        // .catch(error => {
        //     console.error('Error:', error);
        // });
});