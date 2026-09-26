function findMatches() {

    fetch("match")
        .then(response => response.text())
        .then(data => {

            document.getElementById("matchGrid").innerHTML = data;

        })
        .catch(error => {

            document.getElementById("matchGrid").innerHTML =
                "<p>Unable to load matches.</p>";

            console.error(error);

        });

}