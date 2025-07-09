
document.addEventListener("DOMContentLoaded", function() {
    /* ADDING NEW COMPOSITION/SONG */
    var newComposition = document.getElementById("new-composition");
    newComposition.addEventListener("click", function() {
        document.getElementById("popup").style.display = "block";
    });

    var submitNewComposition = document.getElementById("submit");
    var form = document.getElementById("popup");
    submitNewComposition.addEventListener("click", function() {
        const regex = /^.+$/; // makes sure that the title and composer are not empty
        var title = document.getElementById("title").value;
        var composer = document.getElementById("composer").value;
        if (!regex.test(title) && !regex.test(composer)) {
            alert("Invalid title and composer");
        } else if (!regex.test(title)) {
            alert("Invalid title");
        } else if (!regex.test(composer)) {
            alert("Invalid composer");
        } else {
            $.ajax({
                type: "POST",
                url: "/newComposition",
                data: {
                    title: title,
                    composer: composer
                },
                timeout: 5000,
                success: function(response) {
                    location.reload();
                },
            });
        }
    });

    var cancelNewComposition = document.getElementById("cancel");
    cancelNewComposition.addEventListener("click", function() {
        document.getElementById("popup").style.display = "none";
    });

    const dropdown = document.getElementById('composition-dropdown');
    dropdown.addEventListener('change', function() {
        changeComposition();  // Call the function when an option is selected
        console.log("testing");
    });
    // changing composition
    function changeComposition() {
        const form = document.getElementById('composition-form');
        const formData = new FormData(form);
        fetch('/changeComposition', {
            method: 'POST',
            body: formData
        })
        .then(response => response.text())
        .then(data => {
            location.reload();
            console.log(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
    }
});